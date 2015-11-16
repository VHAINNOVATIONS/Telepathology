/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 20, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.health;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.log4j.Logger;

import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.health.configuration.VixHealthConfiguration;
import gov.va.med.imaging.health.configuration.VixHealthConfigurationLoader;

/**
 * Helper class to retrieve the VIX Site health from a remote VIX. This object contains the cache and
 * makes the HTTP GET call to get the site health, this class does NOT handle the parsing of the response.
 * 
 * @author vhaiswwerfej
 *
 */
public class VixServerHealthHelper 
{
	private final static Logger logger = Logger.getLogger(VixServerHealthHelper.class);
	
	private final HashMap<String, VixSiteServerHealth> vixSiteServerHealthCache = 
		new HashMap<String, VixSiteServerHealth>();
	
	private static VixServerHealthHelper helper = null;
	
	public static synchronized VixServerHealthHelper getVixServerHealthHelper()
	{
		if(helper == null)
		{
			helper = new VixServerHealthHelper();
		}
		return helper;
	}
	
	/**
	 * Prevent creation from external classes
	 */
	private VixServerHealthHelper()
	{
		super();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Site> getEnabledLocalVixSites()
	{
		VixHealthConfiguration configuration = 
			VixHealthConfigurationLoader.getVixHealthConfigurationLoader().getVixHealthConfiguration();
		
		List<Site> sites = new ArrayList<Site>();
		for(VixSite site : configuration.getVixLocalSites())
		{
			if(site.isEnabled())
			{
				sites.add(site.toSite());
			}
		}
		
		return sites;		
	}
	
	/**
	 * Returns the list of sites to not include in the health report. This should be used for entries in the
	 * site service which are not actually VIX servers (BIA, BHIE, HAIMS, NCAT, etc)
	 * @return
	 */
	public List<String> getExcludedSiteNumbers()
	{
		VixHealthConfiguration configuration = 
			VixHealthConfigurationLoader.getVixHealthConfigurationLoader().getVixHealthConfiguration();
		return configuration.getExcludedSiteNumbers();
	}
	
	private CountDownLatch countdownLatch;
	private static int threadCount = 0;
	
	private synchronized static int getNextThreadId()
	{
		if(threadCount >= Integer.MAX_VALUE)
		{
			threadCount = 0;
		}
		else
		{
			threadCount += 1;
		}
		return threadCount;
	}
	
	public List<VixSiteServerHealth> getSitesServerHealth(List<Site> sites, boolean forceRefresh, 
			VixServerHealthSource [] vixServerHealthSources)
	{
		countdownLatch = new CountDownLatch(sites.size());
		final List<VixSiteServerHealth> result = new ArrayList<VixSiteServerHealth>();
		for(Site site : sites)
		{
			final Site s = site;
			final boolean refresh = forceRefresh;
			final VixServerHealthSource [] sources = vixServerHealthSources;
			Thread t = new Thread("SiteHealthLoader-" + getNextThreadId())
			{

				@Override
				public void run()
				{
					result.add(getSiteServerHealth(s, refresh, sources));
					if(countdownLatch != null)
						countdownLatch.countDown();
				}				
			};
			t.start();
		}
		
		try
		{
			countdownLatch.await(120, TimeUnit.SECONDS);		
		}
		catch(InterruptedException iX)
		{
			logger.error("InterruptedException waiting for site health, " + iX.getMessage(), iX);
		}
		long remaining = countdownLatch.getCount();
		countdownLatch = null;
		logger.info("Completed getting health for '" + sites.size() + "' sites, got '" + result.size() + "' results. '" + remaining + "' did not complete in time");
		return result;
	}

	/**
	 * Get the health information about a specific site.  Vix health information is cached and will be loaded from
	 * cache if the health information has not expired and forceRefresh=false.
	 * 
	 * @param site
	 * @param forceRefresh If true then the site information is reloaded from the source, not the cache.
	 * @return
	 */
	public VixSiteServerHealth getSiteServerHealth(Site site, boolean forceRefresh, VixServerHealthSource [] vixServerHealthSources)
	{
		logger.info("Getting VIX Site health from site '" + site.getSiteNumber() + "', forceRefresh=" + forceRefresh);
		VixSiteServerHealth health = null;
		if(!forceRefresh)
		{
			synchronized(vixSiteServerHealthCache)
			{
				health = vixSiteServerHealthCache.get(site.getSiteNumber());
				if(health != null)
				{
					logger.debug("Found health for site '" + site.getSiteNumber() + "' in cache.");
					if(health.expired())
					{
						logger.debug("Health for site '" + site.getSiteNumber() + "' from cache expired, will request new version.");
						vixSiteServerHealthCache.remove(site.getSiteNumber());
						health = null;
					}
				}
			}		
			if(health != null)
				return health;
		}
		// health was not found in the cache (or it expired), get an updated version of the health
		health = getSiteServerHealthFromSite(site, vixServerHealthSources);
		if(health != null)
		{		
			synchronized(vixSiteServerHealthCache)
			{
				vixSiteServerHealthCache.put(site.getSiteNumber(), health);
			}
		}		
		return health;
	}
	
	/**
	 * Get the current health of the specified site
	 * @param site
	 * @return
	 */
	private VixSiteServerHealth getSiteServerHealthFromSite(Site site, 
			VixServerHealthSource [] vixServerHealthSources)
	{		
		HttpClient client = new HttpClient();
		String url = createVixHealthUrl(site, vixServerHealthSources);
		logger.info("Requesting VIX Server health from site '" + site.getSiteNumber() + "' using URL '" + url + "'.");
		GetMethod getMethod = new GetMethod(url);
		int responseCode = -1;
		String errorMessage = null;
		try
		{
			// JMW set connection timeout so VIX doesn't wait forever if remote VIX is unavailable
			HttpConnectionManager connectionManager = client.getHttpConnectionManager();
			HttpConnectionParams params = connectionManager.getParams();
			params.setConnectionTimeout(1000 * 10); // 10 seconds
			responseCode = client.executeMethod(getMethod);			
		}
		catch(HttpException httpX)
		{
			logger.error("HttpException requesting Vix Server Health from site '" + site.getSiteNumber() + "'", httpX);
			errorMessage = "HttpException requesting Vix Server Health from site '" + site.getSiteNumber() + "', " + httpX.getMessage();
		}
		catch(IOException ioX)
		{
			logger.error("IOException requesting Vix Server Health from site '" + site.getSiteNumber() + "'", ioX);
			errorMessage = "IOException requesting Vix Server Health from site '" + site.getSiteNumber() + "', " + ioX.getMessage();
		}
		
		if(responseCode == HttpStatus.SC_OK)
		{
			logger.info("Got HTTP resonse code '" + responseCode + "', parsing result.");
			try
			{
				InputStream input = getMethod.getResponseBodyAsStream();
				VixServerHealth health = VixServerHealth.getVixServerHealthFromXml(input);
				VixSiteServerHealth siteHealth = new VixSiteServerHealth(site, health, Calendar.getInstance());
				return siteHealth;
			}
			catch(IOException ioX)
			{
				logger.error("Error parsing VIX Server Health response", ioX);
				errorMessage = "IOException parsing VIX Server Health response, " + ioX.getMessage();
			}				
		}
		else
		{
			logger.error("Did not recieve 200 response from server, received [" + responseCode + "]");
			if(errorMessage == null)
				errorMessage = "Did not recieve 200 response from server, received [" + responseCode + "]";
		}
		
		return new VixSiteServerHealth(site, errorMessage, Calendar.getInstance());
	}
	
	private String createVixHealthUrl(Site site, VixServerHealthSource [] vixServerHealthSources)
	{
		StringBuilder url = new StringBuilder();
		url.append("http://");
		url.append(site.getAcceleratorServer());
		url.append(":");
		url.append(site.getAcceleratorPort());
		url.append("/VixServerHealthWebApp/VixServerHealthServlet");
		
		StringBuilder parameters = new StringBuilder();
		String prefix = "?";
		for(VixServerHealthSource source : vixServerHealthSources)
		{
			parameters.append(prefix);
			parameters.append(source.name());
			parameters.append("=true");
			prefix = "&";
		}
		url.append(parameters.toString());
		
		return url.toString();
	}
}
