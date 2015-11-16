package gov.va.med.siteservice;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import gov.va.med.imaging.exchange.business.*;
import gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO;
import gov.va.med.vistaweb.WebServices.SiteService.RegionTO;
import gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ArrayOfImagingExchangeSiteTO;
import gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class SiteServiceTranslator
{
    private static Logger logger = Logger.getLogger(SiteServiceTranslator.class);
    private static final int defaultVistaPort = 9200;
    // private static final int defaultVixPort = 8080; // not used

	/**
	 * Translate the response from the SiteService into
	 * a List of Site instances.
	 *  
	 * @param sitesArray
	 * @return
	 */
	public List<Site> translate(ArrayOfImagingExchangeSiteTO sitesArray)
	{
		ImagingExchangeSiteTO[] sites = sitesArray.getImagingExchangeSiteTO();
		List<Site> vaSites = new ArrayList<Site>(sites.length);

		Map<String, Site> tempMap = new HashMap<String, Site>();
		for( ImagingExchangeSiteTO siteTO : sites )
		{
			Site site = null;
			// site 200 is the gateway to the DoD for radiology images
			// it is treated almost as a VA site but it does not support the
			// same protocols as a site VIX
			try
			{
				Map<String, SiteConnection> siteConnections = new HashMap<String, SiteConnection>();
				siteConnections.put(SiteConnection.siteConnectionVista, 
						new SiteConnection(SiteConnection.siteConnectionVista, siteTO.getVistaServer(),  siteTO.getVistaPort()));
				
				if(siteTO.getAcceleratorServer() != null && siteTO.getAcceleratorServer().length() > 0)
					siteConnections.put(SiteConnection.siteConnectionVix, 
							new SiteConnection(SiteConnection.siteConnectionVista, siteTO.getAcceleratorServer(),  siteTO.getAcceleratorPort()));
				site = new SiteImpl(
					siteTO.getSiteNumber(), 
					siteTO.getSiteName(),
					siteTO.getSiteAbbr(),
					siteTO.getVistaServer(), siteTO.getVistaPort(),
					siteTO.getAcceleratorServer(), siteTO.getAcceleratorPort(),
					siteTO.getRegionID(),
					true,
					true,
					siteConnections
				);
				
//				if(	"200".equals(siteTO.getSiteNumber()) )
//					site = new SiteImpl(
//						siteTO.getRegionID(),
//						siteTO.getSiteNumber(), 
//						siteTO.getSiteName(),
//						siteTO.getSiteAbbr(),
//						new URL("xca", siteTO.getAcceleratorServer(), siteTO.getAcceleratorPort(), ""),
//						new URL("vistaimaging", siteTO.getVistaServer(), siteTO.getVistaPort(), ""),
//						new URL("vista", siteTO.getVistaServer(), siteTO.getVistaPort(), ""),
//						new URL("exchange", siteTO.getAcceleratorServer(), siteTO.getAcceleratorPort(), "")
//					);
//				else
//				{
//					site = new SiteImpl(
//						siteTO.getRegionID(), 
//						siteTO.getSiteNumber(), 
//						siteTO.getSiteName(), 
//						siteTO.getSiteAbbr(), 
//						new URL(SiteImpl.VISTA_PROTOCOL, siteTO.getVistaServer(), siteTO.getVistaPort(), ""),
//						new URL(SiteImpl.VISTAIMAGING_PROTOCOL, siteTO.getVistaServer(), siteTO.getVistaPort(), ""),
//						new URL(SiteImpl.VFTP_PROTOCOL, siteTO.getAcceleratorServer(), siteTO.getAcceleratorPort(), "")
//					);
//				}
				tempMap.put(site.getSiteNumber(), site);
				vaSites.add(site); // also put the site into a list so we can store them to disk				
				logger.info("SiteService added site (" + site.toString() + ")");
			}
			catch (MalformedURLException x)
			{
				System.out.println("SiteService failed to add site (" + siteTO.getSiteNumber() + ") due to " + x.getMessage());
			}
		}
		
		return vaSites;
	}
	
	/**
	 * Translates an array of region site service objects into a list of 
	 * Region instances
	 * @param regionsArray
	 * @return
	 */
	public List<Region> translate(ArrayOfRegionTO regionsArray)
	{
		RegionTO [] regions = regionsArray.getRegionTO();
		List<Region> vaRegions = new ArrayList<Region>();
		for(RegionTO region : regions)
		{
			Region vaRegion = new RegionImpl(region.getName(),
					region.getID());
			vaRegions.add(vaRegion);
			logger.info("SiteService added region (" + vaRegion.toString() + ")");
		}
		return vaRegions;
	}
	
	public List<Site> translateSiteNodes(NodeList siteNodes)
	{
		List<Site> vaSites = new ArrayList<Site>(siteNodes.getLength());
		NodeList dataSourceNodes;
		Element siteElement, dataSourceElement;
		Site site;
		String siteNumber = "", siteName, siteAbbr, vistaServer, acceleratorServer, regionId, protocol;
		int vistaPort, acceleratorPort;		
		for (int siteIndex = 0; siteIndex < siteNodes.getLength(); siteIndex++){
			try{
				siteElement = (Element)siteNodes.item(siteIndex);
				siteNumber = siteElement.getAttribute("ID");
				siteName = siteElement.getAttribute("name");
				siteAbbr = siteElement.getAttribute("moniker");
				regionId = ((Element)siteElement.getParentNode()).getAttribute("ID");
				boolean siteUserAuthenticatable = true;
				boolean sitePatientLookupable = true;
				if(siteElement.hasAttribute("siteUserAuthenticatable"))
				{
					siteUserAuthenticatable = Boolean.parseBoolean(siteElement.getAttribute("siteUserAuthenticatable"));
				}
				if(siteElement.hasAttribute("sitePatientLookupable"))
				{
					sitePatientLookupable = Boolean.parseBoolean(siteElement.getAttribute("sitePatientLookupable"));
				}
				Map<String, SiteConnection> siteConnections = new HashMap<String, SiteConnection>();
				
				vistaPort = defaultVistaPort;
				vistaServer = "";
				acceleratorServer = "";
				acceleratorPort = 0; // default the VIX port to 0, if not in site service, not using VIX
				dataSourceNodes = siteElement.getElementsByTagName("DataSource");
				for (int dataSourceIndex=0;dataSourceIndex<dataSourceNodes.getLength();dataSourceIndex++){
					dataSourceElement = (Element)dataSourceNodes.item(dataSourceIndex);
					protocol = dataSourceElement.getAttribute("protocol");
					if (protocol.equals("VIX")){
						acceleratorServer = dataSourceElement.getAttribute("source");
						if (dataSourceElement.hasAttribute("port")){
							acceleratorPort = Integer.parseInt(dataSourceElement.getAttribute("port"));
						}
						siteConnections.put(SiteConnection.siteConnectionVix, 
								new SiteConnection(SiteConnection.siteConnectionVix, acceleratorServer, acceleratorPort));
					}
					else if (protocol.equals("VISTA")) {
						vistaServer = dataSourceElement.getAttribute("source");
						if (dataSourceElement.hasAttribute("port")){
							vistaPort = Integer.parseInt(dataSourceElement.getAttribute("port"));
						}
					} 
					else if (protocol.equals("FHIE") && vistaServer.equals("")) 
					{
						vistaServer = dataSourceElement.getAttribute("source");
						if (dataSourceElement.hasAttribute("port"))
						{
							vistaPort = Integer.parseInt(dataSourceElement.getAttribute("port"));
						}
					}
					else
					{
						String server = dataSourceElement.getAttribute("source");
						int port = 0;
						if (dataSourceElement.hasAttribute("port"))
						{
							port = Integer.parseInt(dataSourceElement.getAttribute("port"));
						}
						siteConnections.put(protocol, 
								new SiteConnection(protocol, server, port));
					}
					
					// always add an entry for VistA
					siteConnections.put(SiteConnection.siteConnectionVista, 
							new SiteConnection(SiteConnection.siteConnectionVista, vistaServer, vistaPort));
				}
				site = new SiteImpl(siteNumber,
						siteName,
						siteAbbr,
						vistaServer,
						vistaPort,
						acceleratorServer,
						acceleratorPort,
						regionId,
						sitePatientLookupable,
						siteUserAuthenticatable,
						siteConnections);
				vaSites.add(site);
				logger.info("SiteService added site (" + site.toString() + ")");
			}
			catch (MalformedURLException mux)
			{
				System.out.println("SiteService failed to add site (" + siteNumber + ") due to " + mux.getMessage());
			}
		}
		return vaSites;
	}
	
	public List<Region> translateRegionNodes(NodeList regionNodes){
		List<Region> vaRegions = new ArrayList<Region>(regionNodes.getLength());
		Element regionElement;
		Region region;
		for (int regionIndex = 0; regionIndex < regionNodes.getLength(); regionIndex++){
		//while (regionNodes.getCurrentNode() != null){
			regionElement = (Element)regionNodes.item(regionIndex);
			region = new RegionImpl(regionElement.getAttribute("name"), regionElement.getAttribute("ID"));
			vaRegions.add(region);
			logger.info("SiteService added region (" + region.toString() + ")");
		}
		return vaRegions;		
	}
}
