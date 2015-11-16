/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 6, 2009
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
package gov.va.med.imaging.url.vista.storage;

import org.apache.log4j.Logger;

import gov.va.med.imaging.url.vista.image.VistaRadSiteCredentials;
import gov.va.med.imaging.url.vista.image.VistaRadSiteCredentialsCacheManager;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImagingRadStorageManager 
{
	private final static VistaRadSiteCredentialsCacheManager credentialsCache = 
		new VistaRadSiteCredentialsCacheManager();
	
	private final static Logger logger = Logger.getLogger(VistaImagingRadStorageManager.class);
	
	public VistaImagingRadStorageManager()
	{
		super();
	}
	
	public static VistaRadSiteCredentials getSiteCredentialsFromCache(String siteNumber)
	{
		synchronized(credentialsCache)
		{
			if(credentialsCache != null)
			{
				logger.info("getting VistARad credentials for site '" + siteNumber + "' into cache");
				return credentialsCache.getSiteCredentials(siteNumber);
			}
		}
		logger.warn("VistARad Site Credentials cache is null, this should not happen!");
		return null;
	}
	
	public static void updateSiteCredentials(VistaRadSiteCredentials siteCredentials)
	{		
		if(credentialsCache == null)
		{
			logger.warn("VistARad Site Credentials cache is null, this should not happen!");
		}
		else
		{
			if(siteCredentials != null)
			{
				synchronized (credentialsCache) 
				{
					logger.info("putting VistARad credentials for site '" + siteCredentials.getSiteNumber() + "' into cache");
					credentialsCache.updateSiteCredentials(siteCredentials);
				}	
			}
		}
	}
}
