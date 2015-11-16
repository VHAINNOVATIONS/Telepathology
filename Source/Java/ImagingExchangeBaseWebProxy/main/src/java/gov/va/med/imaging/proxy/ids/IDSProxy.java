/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 29, 2008
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
package gov.va.med.imaging.proxy.ids;

import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.proxy.ids.configuration.IDSProxyConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.log4j.Logger;

/**
 * Proxy for retrieving IDS Service information. Makes use of the IDSServiceCache to reduce the number of times
 * the version is requested from the originating source. If no value is found in the cache, then the source
 * is requested the available versions.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class IDSProxy 
{
	// This value is used when storing data about the local site VIX
	public final static String defaultLocalSiteNumber = "localSiteNumber";
	
	private final static Logger logger = Logger.getLogger(IDSProxy.class);
	
	private final static IDSServiceCache serviceCache = new IDSServiceCache();
	
	public IDSProxy()
	{
		super();
	}
	
	public SortedSet<IDSService> getImagingServices(String siteNumber, String acceleratorServer, 
			int acceleratorPort, String applicationName, String version)
	{	
		if(serviceCache.isSiteOffline(siteNumber))
		{
			logger.info("Site '" + siteNumber + "' has been previously tested and determined to be offline, returning null services.");
			return null;
		}
		
		SortedSet<IDSService> services = new TreeSet<IDSService>();
		// can only look in the cache if a specified service type and version was specified
		if((applicationName != null) && (applicationName.length() > 0) && 
			(version != null) && (version.length() > 0))
		{
			logger.info("Search IDS Service cache for service from site [" + siteNumber + "] [" + applicationName + ", " + version + "]");
			IDSService service = serviceCache.getCachedService(siteNumber, applicationName, version);
			if(service != null)
			{
				logger.info("Found service in cache, returning service information from cache");
				services.add(service);
				return services;
			}
			// check to see if this site/version/application was previously attempted and not found, don't bother again
			if(serviceCache.isSiteVersionUnavailable(siteNumber, version, applicationName))
			{
				logger.info("Site '" +siteNumber + "', version '" + version + "', application '" + applicationName + "', was previously tested and no services found, returning null services.");
				return null;
			}
		}		
		
		HttpClient client = null;
		GetMethod getMethod = null;
		int response = 0;
		String type = ""; //$NON-NLS-1$
		String versionQuery = ""; //$NON-NLS-1$
		if((applicationName != null) && (applicationName.length() > 0))
		{
			type = "?type=" + applicationName; //$NON-NLS-1$
		}
		if((version != null) && (version.length() > 0))
		{
			if(type.length() > 0)
			{
				versionQuery = "&"; //$NON-NLS-1$
			}
			else
			{
				versionQuery = "?"; //$NON-NLS-1$
			}
			versionQuery += "version=" + version;			 //$NON-NLS-1$
		}
		String idsServiceUrlString = getIdsProxyConfiguration().getIdsProtocolWithDefault() + "://" +  //$NON-NLS-1$
			acceleratorServer + ":" + acceleratorPort + "/" +  //$NON-NLS-1$ //$NON-NLS-2$
			getIdsProxyConfiguration().getIdsApplicationPathWithDefault() + "/" +
			getIdsProxyConfiguration().getIdsServicePathWithDefault() + type + versionQuery ; //$NON-NLS-1$
		try
		{
			logger.info("Querying IDS Service at site [" + siteNumber + "] with URL [" + idsServiceUrlString + "]");
			client = new HttpClient();
			
			// JMW set connection timeout so VIX doesn't wait forever if remote VIX is unavailable
			HttpConnectionManager connectionManager = client.getHttpConnectionManager();
			HttpConnectionParams params = connectionManager.getParams();
			// amount of time (in ms) for a connection to occur
			params.setConnectionTimeout(getIdsProxyConfiguration().getIdsConnectionTimeoutMsWithDefault());
			// amount of time (in ms) to wait for a response
			params.setSoTimeout(getIdsProxyConfiguration().getIdsResponseTimeoutMsWithDefault());
			
			getMethod = new GetMethod(idsServiceUrlString);
			response = client.executeMethod(getMethod);
			
			// JMW 2/2/2009 - don't track the amount of data called to IDS or the amount of time - not relevant or consistent (only should do the first time) - not specific to a transaction			
		}
		catch(IOException ioX)
		{
			logger.error("Error getting service list from [" + idsServiceUrlString + "]", ioX); //$NON-NLS-1$ //$NON-NLS-2$
			serviceCache.setSiteOffline(siteNumber); // log that site is offline
			return null;
		}
		
		if(response == HttpStatus.SC_OK)
		{
			try
			{
				InputStream input = getMethod.getResponseBodyAsStream();
				IDSServiceParser parser = new IDSServiceParser();
				services = parser.parse(input);
				if((services == null) || (services.size() <= 0))
				{
					logger.info("Found " + (services == null ? "null" : "no") + " services for site '" + siteNumber + "', version '" + version + "', " + applicationName + "', caching this status.");
					serviceCache.setSiteVersionUnavailable(siteNumber, version, applicationName);
				}
				else
				{
					logger.info("Caching services in IDS Service cache");
					serviceCache.cacheServices(siteNumber, services);	
				}
			}
			catch(IOException ioX)
			{
				logger.error("Error reading response from server [" + idsServiceUrlString + "]", ioX); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			}
		}
		else
		{
			logger.error("Did not recieve 200 response from server, received [" + response + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		
		return services;
	}
	
	public SortedSet<IDSService> getImagingServices(Site site, String applicationName, String version)
	{
		if(site == null)
			return null;
		if(!site.hasAcceleratorServer())
		{
			logger.info("Site [" + site.getSiteNumber() + "] does not have VIX defined in site service, cannot get imaging services");
			return null;
		}		
		return getImagingServices(site.getSiteNumber(), site.getAcceleratorServer(), 
				site.getAcceleratorPort(), applicationName, version);		
	}

	private IDSProxyConfiguration getIdsProxyConfiguration()
	{
		return IDSProxyConfiguration.getIdsProxyConfiguration();
	}
}
