/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 5, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.siteservice.rest.translator;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.RegionImpl;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteConnection;
import gov.va.med.imaging.exchange.business.SiteImpl;
import gov.va.med.imaging.exchange.siteservice.rest.types.SiteServiceSiteConnectionType;
import gov.va.med.imaging.exchange.siteservice.rest.types.SiteServiceSiteConnectionsType;
import gov.va.med.imaging.exchange.siteservice.rest.types.SiteServiceSiteType;
import gov.va.med.imaging.exchange.siteservice.rest.types.SiteServiceSitesType;
import gov.va.med.imaging.exchange.siteservice.rest.types.SiteServiceVisnType;
import gov.va.med.imaging.exchange.siteservice.rest.types.SiteServiceVisnsType;

/**
 * @author VHAISWWERFEJ
 *
 */
public class SiteServiceRestTranslator
{
	
	public static SiteServiceSiteType translate(Site site)
	{
		if(site == null)
			return null;
		
		SiteServiceSiteType result = new SiteServiceSiteType();
		
		result.setVisnNumber(site.getRegionId());
		result.setSiteAbbr(site.getSiteAbbr());
		result.setSiteName(site.getSiteName());
		result.setSiteNumber(site.getSiteNumber());
		result.setSitePatientLookupable(site.isSitePatientLookupable());
		result.setSiteUserAuthenticatable(site.isSiteUserAuthenticatable());
		
		result.setSiteConnections(translate(site.getSiteConnections()));		
		
		return result;
	}
	
	private static SiteServiceSiteConnectionsType translate(Map<String, SiteConnection> siteConnections)
	{
		if(siteConnections == null)
			return null;
		SiteServiceSiteConnectionType [] result = 
			new SiteServiceSiteConnectionType[siteConnections.size()];
		int i = 0;
		for(SiteConnection siteConnection : siteConnections.values())
		{
			result[i] = new SiteServiceSiteConnectionType(siteConnection.getProtocol(), siteConnection.getServer(), siteConnection.getPort());
			i++;
		}		
		
		return new SiteServiceSiteConnectionsType(result);
	}
	
	public static SiteServiceVisnsType translate(List<Region> regions)
	{
		SiteServiceVisnType [] visns = translateRegions(regions);
		return new SiteServiceVisnsType(visns);
	}
	
	
	private static SiteServiceVisnType [] translateRegions(List<Region> regions)
	{
		if(regions == null)
			return null;
		SiteServiceVisnType [] result = new SiteServiceVisnType[regions.size()];
		for(int i = 0; i < regions.size(); i++)
		{
			result[i] = translate(regions.get(i));
		}
		return result;
	}
	
	public static SiteServiceVisnType translate(Region region)
	{
		if(region == null)
			return null;
		
		SiteServiceVisnType result = new SiteServiceVisnType();
		
		result.setVisnName(region.getRegionName());
		result.setVisnNumber(region.getRegionNumber());
		result.setSites(translateSites(region.getSites()));		
		
		return result;
	}
	
	public static SiteServiceSitesType translateSites(List<Site> sites)
	{
		return new SiteServiceSitesType(translateSitesToArray(sites));
	}
	
	private static SiteServiceSiteType[] translateSitesToArray(List<Site> sites)
	{
		if(sites == null)
			return null;
		SiteServiceSiteType []result = new SiteServiceSiteType[sites.size()];
		for(int i = 0; i < sites.size(); i++)
		{
			result[i] = translate(sites.get(i));
		}
		return result;
	}
	
	public static List<Site> translateToSites(SiteServiceVisnsType visns)
	throws MethodException
	{
		if(visns == null)
			return null;
		List<Site> result = new ArrayList<Site>();		
		
		for(SiteServiceVisnType visn : visns.getVisns())
		{
			for(SiteServiceSiteType site : visn.getSites().getSites())
			{
				result.add(translate(site));
			}
		}		
		
		return result;
	}
	
	public static List<Region> translateToRegions(SiteServiceVisnsType visns)
	throws MethodException
	{
		if(visns == null)
			return null;
		List<Region> result = new ArrayList<Region>();		
		
		for(SiteServiceVisnType visn : visns.getVisns())
		{
			Region region = new RegionImpl(visn.getVisnName(), visn.getVisnNumber());
			result.add(region);
			List<Site> sites = new ArrayList<Site>();
			for(SiteServiceSiteType site : visn.getSites().getSites())
			{
				
				sites.add(translate(site));
			}
			region.setSites(sites);
		}		
		
		return result;
	}
	
	/*
	public static List<Site> translate(SiteServiceSitesType sites)
	throws MethodException
	{
		if(sites == null)
			return null;
		List<Site> result = new ArrayList<Site>();
		for(SiteServiceSiteType site : sites.getSites())
		{
			result.add(translate(site));
		}
		
		return result;
	}*/
	
	private static Site translate(SiteServiceSiteType site)
	throws MethodException
	{
		Map<String, SiteConnection> siteConnections = translate(site.getSiteConnections());
		SiteConnection vistaSiteConnection = siteConnections.get(SiteConnection.siteConnectionVista);
		SiteConnection vixSiteConnection = siteConnections.get(SiteConnection.siteConnectionVix);
		
		String vistaServer = (vistaSiteConnection == null ? "" : vistaSiteConnection.getServer());
		int vistaPort = (vistaSiteConnection == null ? 0 : vistaSiteConnection.getPort());
		String vixServer = (vixSiteConnection == null ? "" : vixSiteConnection.getServer());
		int vixPort = (vixSiteConnection == null ? 0 : vixSiteConnection.getPort());
		
		try
		{
			return new SiteImpl(site.getSiteNumber(), site.getSiteName(), site.getSiteAbbr(),
					vistaServer, vistaPort, vixServer, vixPort, site.getVisnNumber(), siteConnections);
		}
		catch(MalformedURLException murlX)
		{
			throw new MethodException(murlX);
		}
	}
	
	private static Map<String, SiteConnection> translate(SiteServiceSiteConnectionsType siteConnections)
	{
		Map<String, SiteConnection> result = new HashMap<String, SiteConnection>();
		
		for(SiteServiceSiteConnectionType siteConnection : siteConnections.getConnections())
		{
			result.put(siteConnection.getProtocol(), 
					new SiteConnection(siteConnection.getProtocol(), siteConnection.getServer(), 
							siteConnection.getPort()));
		}
		return result;
		
		
	}

}
