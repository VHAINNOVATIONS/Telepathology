/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 17, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswbuckd
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
 *
 */

package gov.va.med.imaging.javalogs;

import org.apache.log4j.Logger;

import gov.va.med.imaging.DateUtil;
import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

public class JavaLogConfiguration extends AbstractBaseFacadeConfiguration
{
	private static final Logger logger = Logger.getLogger(JavaLogConfiguration.class);
    
	private int retentionPeriodDays;
	private boolean purgeAtStartup;
	private int refreshHour;
	private int refreshMinimumDelay;
	private long refreshPeriod;  
	
	/**
	 * @return the length of time to retain java logs in days
	 */
	public int getRetentionPeriodDays()
	{
		return retentionPeriodDays;
	}

	/**
	 * @param retentionPeriodDays  - the length of time to retain java logs in days
	 */
	public void setRetentionPeriodDays(int retentionPeriodDays)
	{
		this.retentionPeriodDays = retentionPeriodDays;
	}

	/**
	 * @return whether to purge java logs at startup
	 */
	public boolean isPurgeAtStartup()
	{
		return purgeAtStartup;
	}

	/**
	 * @param purgeAtStartup - whether to purge java logs at startup
	 */
	public void setPurgeAtStartup(boolean purgeAtStartup)
	{
		this.purgeAtStartup = purgeAtStartup;
	}

	/**
	 * @return the hour that the purge java log task should run
	 */
	public int getRefreshHour()
	{
		return refreshHour;
	}

	/**
	 * @param refreshHour - the hour that the purge java log task should run
	 */
	public void setRefreshHour(int refreshHour)
	{
		this.refreshHour = refreshHour;
	}

	/**
	 * @return the minimum delay until the purge java logs task is run in days
	 */
	public int getRefreshMinimumDelay()
	{
		return refreshMinimumDelay;
	}

	/**
	 * @param refreshMinimumDelay - the minimum delay until the purge java logs task is run in days
	 */
	public void setRefreshMinimumDelay(int refreshMinimumDelay)
	{
		this.refreshMinimumDelay = refreshMinimumDelay;
	}

	/**
	 * @return the - how often to run the purge java logs task 
	 */
	public long getRefreshPeriod()
	{
		return refreshPeriod;
	}

	/**
	 * @param refreshPeriod - how often to run the purge java logs task
	 */
	public void setRefreshPeriod(long refreshPeriod)
	{
		this.refreshPeriod = refreshPeriod;
	}

	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		retentionPeriodDays = 30; // 30 days
		purgeAtStartup = false; // useful for debugging
		refreshHour = 1; // 1:00 AM
		refreshMinimumDelay = 1;
		refreshPeriod = DateUtil.MILLISECONDS_IN_DAY; // 1 day expressed as milliseconds.
		return this;
	}

    public synchronized static JavaLogConfiguration getConfiguration() 
    {
        try
        {
        	return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
        			JavaLogConfiguration.class);
        }
        catch(CannotLoadConfigurationException clcX)
        {
        	return null;
        }
    }

    public static void main(String[] args)
    {
        if (args.length != 0)
        {
        	logger.error("JavaLogConfiguration: main called with parameters");
            return;
        }
    	JavaLogConfiguration defaultConfig = getConfiguration();
        defaultConfig.storeConfiguration();
    }

}
