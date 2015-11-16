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

import java.util.Calendar;

import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.health.configuration.VixHealthConfiguration;
import gov.va.med.imaging.health.configuration.VixHealthConfigurationLoader;

/**
 * Represents a health snapshot from a particular site at a particular time
 * 
 * @author vhaiswwerfej
 *
 */
public class VixSiteServerHealth 
{
	private final Site site;
	private final Calendar lastRefreshed;
	private final VixServerHealth vixServerHealth;
	private final String errorMessage;
	
	private final static long defaultMaxHealthAgeInMs = 1000 * 60 * 5; // 5 minutes
	
	public VixSiteServerHealth(Site site, VixServerHealth vixServerHealth, 
			Calendar lastRefreshed) 
	{
		super();
		this.site = site;
		this.lastRefreshed = lastRefreshed;
		this.vixServerHealth = vixServerHealth;
		this.errorMessage = null;
	}
	
	public VixSiteServerHealth(Site site, String errorMessage, Calendar lastRefreshed)
	{
		super();
		this.site = site;
		this.lastRefreshed = lastRefreshed;
		this.vixServerHealth = null;
		this.errorMessage = errorMessage;
	}
	
	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @return the lastRefreshed
	 */
	public Calendar getLastRefreshed() {
		return lastRefreshed;
	}

	/**
	 * @return the vixServerHealth
	 */
	public VixServerHealth getVixServerHealth() {
		return vixServerHealth;
	}
	
	/**
	 * Determines if this VixSiteServerHealth object has been around too long and has expired
	 * @return True if expired, false if still active
	 */
	public boolean expired()
	{
		long currentTime = System.currentTimeMillis();
		long lastRefreshedMs = lastRefreshed.getTimeInMillis();
		if((lastRefreshedMs + getMaxHealthAgeInMs()) < currentTime)
			return true;
		return false;
	}
	
	public boolean isError()
	{
		if(vixServerHealth == null)
			return true;
		if(errorMessage != null)
			return true;
		return false;
	}
	
	/**
	 * get the configuration property from the configuration object and return (if not empty) - return default if no value
	 * @return
	 */
	private long getMaxHealthAgeInMs()
	{
		long timeout = defaultMaxHealthAgeInMs;
		VixHealthConfigurationLoader loader = VixHealthConfigurationLoader.getVixHealthConfigurationLoader();
		if(loader != null)
		{
			VixHealthConfiguration configuration = loader.getVixHealthConfiguration();
			if(configuration != null)
			{
				Long ms = configuration.getHealthTimeoutMs();
				if(ms != null)
					return ms;
			}
		}
		return timeout;		
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}
}
