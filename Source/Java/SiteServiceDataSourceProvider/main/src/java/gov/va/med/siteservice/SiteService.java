/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 6, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWBECKEC
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
package gov.va.med.siteservice;

import gov.va.med.imaging.DateUtil;
import gov.va.med.imaging.ReadWriteLockCollections;
import gov.va.med.imaging.ReadWriteLockMap;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.RegionImpl;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.siteservice.rest.SiteServiceRestProxy;
import gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO;
import gov.va.med.vistaweb.WebServices.SiteService.SiteServiceLocator;
import gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap;
import gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ArrayOfImagingExchangeSiteTO;
import gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceLocator;
import gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.namespace.NamespaceContext;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.thoughtworks.xstream.XStream;

/**
 * This class is a caching proxy for SiteService.
 * It maintains a map from site ID to Site instance.  The map
 * is first obtained from SiteService and cached in memory and
 * in local persistent storage.  The map is periodically updated
 * by a timer task.  If SiteService is unavailable then the map
 * is loaded from the local persistent copy.
 * 
 * @author VHAISWBECKEC
 */
public class SiteService
implements Iterable<Site>
{
	private static Log logger = null;
	private final SiteServiceConfiguration configuration;
	private Timer refreshTimer;
	private RefreshTimerTask refreshTask;
	private Date lastCacheUpdate;
	
	// This maps the site ID to the Site for faster lookup by site ID.
	// Use a Map that is synchronized seperately on read and write operations.
	// Reads may happen simultaneously, writes must be serialized.
	private final ReadWriteLockMap<String, Site> siteMap;
	private final ReadWriteLockMap<String, Region> regionMap;
	
	// The first time that the cache is loaded it will load from the local
	// persistent copy if SiteService is unavailable.  On subsequent refreshes,
	// it will not use the local persistent copy.
	private boolean initialLoadComplete;
	private SiteServiceSource siteServiceSource = null;
	private String siteServiceDataSourceVersion = "";
	
	static 
	{
		logger = LogFactory.getFactory().getInstance(SiteService.class);
	}
	
	private static Log getLogger()
	{
		return logger;
	}
	
	/**
	 * @param appConfiguration
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * Constructor for Spring singleton.
	 */
	@SuppressWarnings( "unchecked" )
	public SiteService(SiteServiceConfiguration configuration) 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		assert configuration != null;
		this.configuration = configuration;
		siteMap = (ReadWriteLockMap<String, Site>)ReadWriteLockCollections.readWriteLockMap(new HashMap<String, Site>());
		regionMap = (ReadWriteLockMap<String, Region>)ReadWriteLockCollections.readWriteLockMap(new HashMap<String, Region>());
		initialLoadComplete = false;
		
		getSiteServiceUri();		// here to force an error in the initializer if the URI is not formatted properly
		initialize();				// loads the cache for the first time
	}
	
	public SiteServiceConfiguration getConfiguration()
	{
		return this.configuration;
	}

	// If initialLoadComplete is true then the map has been successfully
	// loaded at least once (either from local file or remote siteService).
	// Whether initialLoadComplete is true affects how refreshes behave.
	public boolean isInitialLoadComplete()
    {
    	return initialLoadComplete;
    }

	// initialLoadComplete is a latching property, once set to true it
	// cannot be reset to false
	public void setInitialLoadComplete(boolean initialLoadComplete)
    {
    	this.initialLoadComplete = this.initialLoadComplete || initialLoadComplete;
    }

	/**
	 * return the Date of the last successful update from SiteService
	 * @return
	 */
	public Date getLastCacheUpdate()
    {
    	return lastCacheUpdate;
    }

	/**
	 * 
	 */
	private void initialize()
    {
		// refresh from SiteService if we can else from persistent storage it it exists
		refreshCache();
		
		// start the refresh timer task
		scheduleRefresh();
    }

	/**
	 * 
	 * @return
	 */
	public URL getUrl()
	{
		try
		{
			return getConfiguration().getSiteServiceUri().toURL();
		}
		catch (MalformedURLException x)
		{
			String message = "The site service URL '" + getConfiguration().getSiteServiceUri() + "' is not a valid URL."; 
			logger.error(message);
			return null;
		}
	}
	
	/**
	 * A convenience method for getting the SiteService URI.
	 * Wraps some error handling and instance creation.
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	public URI getSiteServiceUri()
	throws IllegalArgumentException
	{
		return getConfiguration().getSiteServiceUri();
	}
	
	public String getSiteServiceSource()
	{
		if(siteServiceSource == SiteServiceSource.siteService)
			return getSiteServiceUri().toString();
		else if(siteServiceSource == SiteServiceSource.cacheFile)
			return getVhaSitesFile().getAbsolutePath();
		return "";
	}
	
	public String getSiteServiceV2Url()
	{
		URI siteServiceUri = getConfiguration().getSiteServiceUri();
		return siteServiceUri.toString().replace("ImagingExchangeSiteService.asmx", "restservices/siteservice/sites");
	}
	
	/**
	 * Converts the exchange site service URL into the older site service URL. 
	 * This is a bit of a kludge, would be better to store the actual old site service URL instead 
	 * of converting it...
	 * @return The URL for the older site service implementation
	 * @throws IllegalArgumentException
	 */
	private URI getBaseSiteServiceUri()
	throws IllegalArgumentException
	{
		String siteServiceUrl = "";
		try
		{
			String exchangeUrl = getUrl().toExternalForm();	
			// JMW 2/21/2011 - need to make siteservice.asmx all lowercase to support CVIX hosting the site service.
			// It is all lowercase because that is what the Delphi clients need and since Tomcat is case sensitive
			// this needs to call the service with the proper case.  When calling a IIS site service it doesn't matter
			// what the case is so lowercase should work fine
			siteServiceUrl = exchangeUrl.replace("ImagingExchangeSiteService.asmx", "siteservice.asmx"); 
			return new URI(siteServiceUrl);
		}
		catch(URISyntaxException urisX)
		{
			String message = "Error converting Exchange URL into Site service URL, the site service URL '" + siteServiceUrl + "' is not a valid URI."; 
			logger.error(message);
			// give up and kill everything, the URI must be valid
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Schedule the refresh of the cache.  By default, the site service cache is refreshed
	 * once a day at 11PM local time.
	 * If the refresh schedule is updated (i.e. refreshHour or refreshPeriod) are modified
	 * then this method must be called to reschedule the refresh.
	 * This method is synchronized to protect the refreshTimer and refreshTask locals
	 * from uncoordinated modification.
	 */
	public synchronized void scheduleRefresh()
	{
		// if the refresh task already exists, then we are re-scheduling ourselves
		if(refreshTask != null)
		{
			refreshTask.cancel();
			refreshTask = null;
		}
		
		// if the refreshTimer exists it can be re-used
		if(refreshTimer == null)
			refreshTimer = new Timer("SiteServiceCacheRefresh", true);
		
		refreshTask = new RefreshTimerTask();
		
		// schedule ourselves to, by default, refresh every 24 hours,
		// starting at 23:00, the first occurrence of which must be at least 1 hour from now
		refreshTimer.schedule(
			new RefreshTimerTask(), 
			DateUtil.nextOccurenceOfHour(
				getConfiguration().getRefreshHour(), 
				getConfiguration().getRefreshMinimumDelay()), 
				getConfiguration().getRefreshPeriod()
		);
		getLogger().info(
				"SiteService scheduled to refresh at [" + 
				getConfiguration().getRefreshHour() + 
				":00:00] and every [" + 
				(float)getConfiguration().getRefreshPeriod()/(float)DateUtil.MILLISECONDS_IN_DAY + 
				"] days thereafter");
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.ISiteService#getSiteByNumber(java.lang.String)
	 */
	public Site getSiteByNumber(String siteNumber) 
	{
		return this.siteMap.get(siteNumber);
	}
	
	public List<Region> getAllRegions()
	{
		logger.debug("getAllRegions ()");
		List<Region> regions = new ArrayList<Region>();
		for(String key : regionMap.keySet())
		{
			Region region = regionMap.get(key);
			regions.add(getRegionByNumber(region.getRegionNumber()));
		}
		logger.debug("Found [" + regions.size() + "] regions");
		return regions;
	}
	
	public Region getRegionByNumber(String regionNumber)
	{
		logger.debug("getRegionByNumber (" + regionNumber + ")");
		Region region = this.regionMap.get(regionNumber);
		if(region == null)
			return null;		
		// want to create a new instance of region since we are putting in the site objects 
		// (but don't want those in the regions in the cache)
		Region result = new RegionImpl(region.getRegionName(), region.getRegionNumber());
		
		// find all the sites for this region
		for(String key : this.siteMap.keySet())
		{
			Site site = this.siteMap.get(key);
			if(regionNumber.equals(site.getRegionId()))
			{
				result.getSites().add(site);
			}
		}
		logger.debug("Found [" + result.getSites().size() + "] sites for region [" + regionNumber + "]");
		return result;			
	}

	/**
	 * Return an Iterator over all of the Site
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
    public Iterator<Site> iterator()
    {
		return new Iterator<Site>()
		{
			private Iterator<String> keysetIterator = siteMap.keySet().iterator();
			
			@Override
            public boolean hasNext()
            {
	            return keysetIterator.hasNext();
            }

			@Override
            public Site next()
            {
	            return siteMap.get(keysetIterator.next());
            }

			@Override
            public void remove()
            {
				throw new UnsupportedOperationException("remove() is not supported by the SiteService iterator.");
            }
		};
    }
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.ISiteService#forceCacheRefresh()
	 * Force the local cache site to refresh from SiteService.
	 */
	public void refreshCache()
	{
		getLogger().info( isInitialLoadComplete() ? 
			"Refreshing Site cache." : 
			"Loading Site cache for the first time." 
		);
		
		if (this.getVhaSitesFile().exists()){
			try
            {
				getLogger().info( "Loading site map from local cache file '" + getVhaSitesFile().getAbsolutePath() + "'." );
				loadCacheFromVhaSitesXml();
	    		setInitialLoadComplete(true);	// set this true only if we have successfully loaded a non-zero length Site list
				getLogger().info( "Successfully loaded site map from local cache." );
				siteServiceSource = SiteServiceSource.cacheFile;
            } 
			catch (SiteMapLoadException e)
            {
				logger.error(e);
            }
			catch (RegionMapLoadException e)
            {
				logger.error(e);
            }
		}
		else{
			try
			{
				getLogger().info( "Loading cache from site service." );
				loadCacheFromSiteService();
				setInitialLoadComplete(true);	// set this true only if we have successfully loaded a non-zero length Site list
				getLogger().info( "Successfully loaded cache from site service." );
				siteServiceSource = SiteServiceSource.siteService;
			}
			catch (SiteMapLoadException ex)
			{
				// If there was an exception while loading the site service from the web service, 
				// attempt to load it from the file on disk
				getLogger().error( "SiteService cache refresh failed with exception [" + 
						(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()) + 
						"]." );				
				try
	            {
					getLogger().info( "Loading site map from local cache." );
		            loadSiteMapFromLocalCache();
		    		setInitialLoadComplete(true);	// set this true only if we have successfully loaded a non-zero length Site list
					getLogger().info( "Successfully loaded site map from local cache." );
	            } 
				catch (SiteMapLoadException e)
	            {
					logger.error(e);
	            }
				catch (RegionMapLoadException e)
	            {
					logger.error(e);
	            }
			}
			catch(RegionMapLoadException rmlX)
			{
				// If there was an exception while loading the site service from the web service, 
				// attempt to load it from the file on disk
				getLogger().error( "SiteService region cache refresh failed with exception [" + 
						(rmlX.getCause() != null ? rmlX.getCause().getMessage() : rmlX.getMessage()) + 
						"]." );
				try
	            {
		            loadSiteMapFromLocalCache();
		    		setInitialLoadComplete(true);	// set this true only if we have successfully loaded a non-zero length Site list
	            } 
				catch (SiteMapLoadException e)
	            {
					logger.error(e);
	            }
				catch(RegionMapLoadException e)
				{
					logger.error(e);
				}
			}
		}
	}

	private void loadCacheFromVhaSitesXml() throws SiteMapLoadException, RegionMapLoadException {
		NamespaceContext ctx = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                String uri;
                if (prefix.equals("vha"))
                    uri = "http://med.va.gov/vistaweb/sitesTable";
                else
                    uri = null;
                return uri;
            }
            public Iterator getPrefixes(String val) {
                return null;
            }
            public String getPrefix(String uri) {
                return null;
            }
        };
		InputSource source = new InputSource(this.getConfiguration().getVhaSitesXmlFileName());
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(ctx);
		SiteServiceTranslator translator = new SiteServiceTranslator();
		try{
			NodeList siteNodes =  (NodeList)xPath.evaluate("//vha:VhaSite",
					source,
					XPathConstants.NODESET);
			List<Site> newSites = translator.translateSiteNodes(siteNodes);
			updateCacheFromSiteList(newSites);
		} catch(XPathExpressionException xpee){
			throw new SiteMapLoadException(xpee, true);
		}
		
		try{
			NodeList regionNodes =  (NodeList)xPath.evaluate("//vha:VhaVisn",
					source,
					XPathConstants.NODESET);
			List<Region> newRegions = translator.translateRegionNodes(regionNodes);
			updateCacheFromRegionList(newRegions);
		} catch(XPathExpressionException xpee){
			throw new RegionMapLoadException(xpee, true);
		}
		lastCacheUpdate = new Date();
	}
	
	/**
	 * Load the SiteService cache from the SiteService webservice.
	 * The "new" site list is first obtained in its entirety and then the
	 * site map is updated in one synchronized method.  The site map should
	 * always be modified in this manner, not by adding/updating single entries.
	 * This method either succeeds in loading a non-zero length list or 
	 * throws an exception.
	 * 
	 * @throws SiteMapLoadException - wraps the underlying exception
	 */
	private void loadCacheFromSiteService() 
	throws SiteMapLoadException, RegionMapLoadException
    {
		
		
		List<Region> newRegions = null;
	    List<Site> newSites = null;
	    
	    boolean isLoaded = false;	    
	    if(getConfiguration().isUseV2Service())
		{
	    	try
	    	{
		    	newSites = loadSitesFromSiteServiceV2();
		    	newRegions = loadRegionsFromSiteServiceV2();
		    	isLoaded = true;
		    	siteServiceDataSourceVersion = "2";
	    	}
	    	catch(SiteMapLoadException smlX)
	    	{
	    		getLogger().warn("Error loading sites from v2 site service, " + smlX.getMessage());
	    	}
	    	catch(RegionMapLoadException rmlX)
	    	{
	    		getLogger().warn("Error loading regions from v2 site service, " + rmlX.getMessage());
	    	}
		}
	    if(!isLoaded)
	    {
	    	newSites = loadSitesFromSiteService();
	    	newRegions = loadRegionsFromSiteService();
	    	siteServiceDataSourceVersion = "1";
	    }
	    
	    // update the cache map
    	updateCacheFromSiteList(newSites);
    	
    	// save the latest list of Site so that we can reboot without
    	// contacting SiteService
    	//storeSiteListToCacheFile(newSites);    
	    
	    // update the cache map
    	updateCacheFromRegionList(newRegions);
    	
    	// save the latest list of Site so that we can reboot without
    	// contacting SiteService
    	storeSiteListToCacheFile(newSites);
    	storeRegionListToCacheFile(newRegions);
    	
    	lastCacheUpdate = new Date();
    	getLogger().info( "SiteService cache refresh completed successfully. " ); 
    }
	
	private List<Site> loadSitesFromSiteService()
	throws SiteMapLoadException
	{
		ImagingExchangeSiteServiceSoap webService;
	    ImagingExchangeSiteServiceLocator locator = new ImagingExchangeSiteServiceLocator();
	    SiteServiceTranslator translator = new SiteServiceTranslator();
		try
        {
	        webService = locator.getImagingExchangeSiteServiceSoap(new URL(getSiteServiceUri().toString()) );
	        // getVAImageMetadata(new
	        // URL("http://localhost:8080/ImagingExchangeWebApp/services/VAImageMetadata"));
	        ArrayOfImagingExchangeSiteTO sitesArray = webService.getImagingExchangeSites();
	        
	        List<Site> newSites = translator.translate(sitesArray);

	        if( newSites == null || newSites.size() == 0 )
	        	throw new SiteMapLoadException("Null or zero length site list returned from translator, has format changed?", true);
	        
	        return newSites;
        } 
	    catch (MalformedURLException e)
        {
	    	logger.error(e);
	    	throw new SiteMapLoadException(e, true);
        } 
	    catch (IllegalArgumentException e)
        {
	    	logger.error(e);
	    	throw new SiteMapLoadException(e, true);
        } 
	    catch (RemoteException e)
        {
	    	logger.error(e);
	    	throw new SiteMapLoadException(e, true);
        } 
	    catch (ServiceException e)
        {
	    	logger.error(e);
	    	throw new SiteMapLoadException(e, true);
        }
	}
	
	private List<Region> loadRegionsFromSiteService()
	throws RegionMapLoadException
	{
		SiteServiceSoap siteServiceWebService = null;
		SiteServiceLocator siteServiceLocator = new SiteServiceLocator();
		try
		{
			SiteServiceTranslator translator = new SiteServiceTranslator();
			siteServiceWebService = siteServiceLocator.getSiteServiceSoap(new URL(getBaseSiteServiceUri().toString()));
			ArrayOfRegionTO regionArray = siteServiceWebService.getVHA();
			List<Region> newRegions = translator.translate(regionArray);
			if( newRegions == null || newRegions.size() == 0 )
	        	throw new RegionMapLoadException("Null or zero length region list returned from translator, has format changed?", true);
			return newRegions;
		}
		catch (MalformedURLException e)
        {
	    	logger.error(e);
	    	throw new RegionMapLoadException(e, true);
        } 
	    catch (IllegalArgumentException e)
        {
	    	logger.error(e);
	    	throw new RegionMapLoadException(e, true);
        } 
	    catch (RemoteException e)
        {
	    	logger.error(e);
	    	throw new RegionMapLoadException(e, true);
        } 
	    catch (ServiceException e)
        {
	    	logger.error(e);
	    	throw new RegionMapLoadException(e, true);
        }
	}
	
	private List<Site> loadSitesFromSiteServiceV2()
	throws SiteMapLoadException
	{
		getLogger().info("Loading SiteService sites cache from V2 interface");		
		String siteServiceV2Url = getSiteServiceV2Url();		
		return SiteServiceRestProxy.getSites(siteServiceV2Url);
	}
	
	private List<Region> loadRegionsFromSiteServiceV2()
	throws RegionMapLoadException
	{
		getLogger().info("Loading SiteService regions cache from V2 interface");		
		String siteServiceV2Url = getSiteServiceV2Url();		
		return SiteServiceRestProxy.getRegions(siteServiceV2Url);
	}

	/**
	 * Load the SiteService cache from the locally stored copy.
	 * 
	 * The site list is first obtained in its entirety and then the
	 * site map is updated in one synchronized method.  The site map should
	 * always be modified in this manner, not by adding/updating single entries.
	 * 
	 * This method either succeeds in loading a non-zero length list or 
	 * throws an exception.
	 */
	private void loadSiteMapFromLocalCache()
	throws SiteMapLoadException, RegionMapLoadException
    { 
	    List<Site> newSites = loadSiteListFromCacheFile();
	    List<Region> newRegions = loadRegionListFromCacheFile();
	    
	    if(newSites == null || newSites.size() == 0)
	    	throw new SiteMapLoadException("SiteService failed to load from cache. ", false);
	    if(newRegions == null || newRegions.size() == 0)
	    	throw new RegionMapLoadException("SiteService failed to load regions from cache. ", false);
	    
    	// update the cache map
    	updateCacheFromSiteList(newSites);
    	updateCacheFromRegionList(newRegions);
    	getLogger().info( "SiteService loaded from cache successfully. " ); 
    }

	/**
	 * A convenience method to load the Site Map from a Site List.
	 * 
	 * Given a List of Site instances, update the local Site Map.
	 * The List may come from the SiteService or from the local cached copy.
	 * 
	 * @param newSites
	 */
	private void updateCacheFromSiteList(List<Site> newSites)
    {
	    Map<String, Site> newSiteMap = new HashMap<String, Site>();
	    
	    for(Site site : newSites)
	    	newSiteMap.put(site.getSiteNumber(), site);
	    
	    //ImagingExchangeSiteTO[] sites = webService.getImagingExchangeSites();
	    
	    // Note that we build a temporary Map instance and then load the
	    // real Map in one fell swoop. This is done because the clearAndPutAll()
	    // method does the entire add in one isolated transaction. No read
	    // operations will occur while the Map is being cleared and updated.
	    // This is how updates to this type of map should be done, at least
	    // in the context of this application.
	    siteMap.clearAndPutAll(newSiteMap);
    }
	
	/**
	 * A convenience method to load the Region Map from a Region List.
	 * 
	 * Given a List of Region instances, update the local Region Map.
	 * The List may come from the SiteService or from the local cached copy.
	 * 
	 * @param newRegions
	 */
	private void updateCacheFromRegionList(List<Region> newRegions)
	{
		Map<String, Region> newRegionMap = new HashMap<String, Region>();
		for(Region region : newRegions)
		{
			newRegionMap.put(region.getRegionNumber(), region);
		}
		regionMap.clearAndPutAll(newRegionMap);
	}
	
	// ===============================================================================================
	// Local Persistent Caching
	// ===============================================================================================
	/**
	 * Retrieve the filename to use to store the cache on disk.
	 * 
	 * @return The filename to use for the cache on disk
	 */
	private File getSiteListCacheFile() 
	{
		return new File( getConfiguration().getSiteServiceCacheFileName() );
	}
	
	private File getRegionListCacheFile()
	{
		return new File( getConfiguration().getRegionListCacheFileName() );
	}
	
	private File getVhaSitesFile() 
	{
		return new File( getConfiguration().getVhaSitesXmlFileName() );
	}

	/**
	 * Load the data from the site service from a file stored on the disk.
	 * 
	 * @return a list of sites from the caches file, null if the file does not exist
	 * or if there were no sites
	 */
	@SuppressWarnings("unchecked")
	private List<Site> loadSiteListFromCacheFile() 
	{
		List<Site> sites = null;
		
		logger.info("SiteService.loadsiteServiceFromCache: Loading site service data from cache");
		try 
		{			
			String filename = getSiteListCacheFile().getAbsolutePath();
			
			logger.info("Loading Site Service site cache using XStream from file '" + filename + "'.");
			XStream xstream = new XStream();
			sites = (List<Site>)xstream.fromXML(new FileInputStream(filename));
			logger.info("Site Service site cache loaded using XStream and is " + (sites == null ? "NULL" : "NOT NULL") );
			
		}
		catch(FileNotFoundException fnfX)
		{
			// This should never happen because the first time the ViX is brought up, the site service should
			// be available. After that the file should exist in the ViX configuration directory
			logger.error("SiteService.loadSiteServiceFromCache: " + fnfX.getMessage());
		}
		catch(Exception ex)
		{
			logger.error("SiteService.loadSiteServiceFromCache: " + ex.getMessage());
		}
		
		// Do NOT return a zero length site list
		// If there are no sites in the file then return null
		return sites != null && sites.size() > 0 ? sites : null;
	}	
	
	/**
	 * Load the data from the site service from a file stored on the disk.
	 * 
	 * @return a list of regions from the caches file, null if the file does not exist
	 * or if there were no regions
	 */
	@SuppressWarnings("unchecked")
	private List<Region> loadRegionListFromCacheFile() 
	{
		List<Region> regions = null;
		
		logger.info("SiteService.loadRegionListFromCacheFile: Loading region list data from cache");
		try 
		{
			String filename = getRegionListCacheFile().getAbsolutePath();
			
			logger.info("Loading Site Service region cache using XStream from file '" + filename + "'.");
			XStream xstream = new XStream();
			regions = (List<Region>)xstream.fromXML(new FileInputStream(filename));
			logger.info("Site Service region cache loaded using XStream and is " + (regions == null ? "NULL" : "NOT NULL") );			
		}
		catch(FileNotFoundException fnfX)
		{
			// This should never happen because the first time the ViX is brought up, the site service should
			// be available. After that the file should exist in the ViX configuration directory
			logger.error("SiteService.loadRegionListFromCacheFile: " + fnfX.getMessage());
		}
		catch(Exception ex)
		{
			logger.error("SiteService.loadRegionListFromCacheFile: " + ex.getMessage());
		}
		
		// Do NOT return a zero length site list
		// If there are no sites in the file then return null
		return regions != null && regions.size() > 0 ? regions : null;
	}	
	
	/**
	 * Stores the list of sites to the cache file on disk
	 * @param sites
	 * @return
	 */
	private boolean storeSiteListToCacheFile(List<Site> sites)
	{
		boolean success = false;
		try
		{
			String filename = getSiteListCacheFile().getAbsolutePath();
			logger.info("Saving local site cache to '" + filename + "' using XStream.");
			XStream xstream = new XStream();
			xstream.toXML(sites, new FileOutputStream(filename));
			logger.info("SiteService site cache saved to: " + filename );
			success = true;
		}
		catch (FileNotFoundException ex)
		{
			// This exception should not occur, it would only occur if the ViX configuration location does
			// not exist, this shouldn't happen or we have other bigger problems
			logger.error("SiteService.storeSiteListToCacheFile: " + ex.getMessage());
		}
		catch(Exception eX) 
		{
			logger.error("SiteService.storeSiteListToCacheFile: " + eX.getMessage());
		}
		return success;
	}
	
	/**
	 * Stores the list of regions to the cache file on disk
	 * @param regions
	 * @return
	 */
	private boolean storeRegionListToCacheFile(List<Region> regions)
	{		
		boolean success = false;
		try
		{
			String filename = getRegionListCacheFile().getAbsolutePath();
			logger.info("Saving SiteService region cache '" + filename + "' using XStream.");
			XStream xstream = new XStream();
			xstream.toXML(regions, new FileOutputStream(filename));
			logger.info("SiteService region cache saved to: " + filename );		
			success = true;
		}
		catch (FileNotFoundException ex)
		{
			// This exception should not occur, it would only occur if the ViX configuration location does
			// not exist, this shouldn't happen or we have other bigger problems
			logger.error("SiteService.storeRegionListToCacheFile: " + ex.getMessage());
		}
		catch(Exception eX) 
		{
			logger.error("SiteService.storeRegionListToCacheFile: " + eX.getMessage());
		}
		return success;
	}

	@Override
	public String toString()
	{
		return 
			this.getClass().getName() + " [" +
			"Site Service URI: " + this.getSiteServiceUri() +
			"]";
	}
	
	public String getSiteServiceDataSourceVersion()
	{
		return siteServiceDataSourceVersion;
	}

	/**
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class RefreshTimerTask
	extends TimerTask
	{
		@Override
        public void run()
        {
			try
            {
				refreshCache();
            } 
			catch (Exception e)
            {
				logger.error(e);
            } 
        }
		
	}
	
	enum SiteServiceSource
	{
		siteService, cacheFile;
	}
}

