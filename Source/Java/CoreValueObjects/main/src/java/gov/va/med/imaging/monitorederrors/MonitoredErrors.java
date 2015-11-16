/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 28, 2013
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
package gov.va.med.imaging.monitorederrors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
public class MonitoredErrors
{
	private List<MonitoredError> monitoredErrors =
			new ArrayList<MonitoredError>();
	private boolean enabled;
	
	private MonitoredErrors()
	{
		super();
	}

	private static MonitoredErrors singleton = null;
	
	private synchronized static MonitoredErrors getSingleton()
	{
		if(singleton == null)
		{
			singleton = new MonitoredErrors();
			singleton.loadFromConfigurationFile();
		}
		return singleton;
	}
	
	public synchronized static List<MonitoredError> getMonitoredErrors()
	{
		return getSingleton().monitoredErrors;
	}
	
	/**
	 * This method updates the monitored errors based on the configuration file. This does not remove old monitored errors that are no longer monitored but they will not be collected any more
	 */
	private void loadFromConfigurationFile()
	{
		MonitoredErrorConfiguration config = MonitoredErrorConfiguration.getMonitoredConfiguration();
		this.enabled = config.isEnabled();
		for(String monitoredError : config.getMonitoredErrors())
		{
			addMonitoredErrorOrSetActive(monitoredError);
		}
	}
	
	/**
	 * This method updates the monitored errors based on the configuration file. This does not remove old monitored errors that are no longer monitored but they will not be collected any more
	 */
	public synchronized static void reloadFromConfiguration()
	{
		MonitoredErrors monitoredErrors = getSingleton();
		monitoredErrors.setAllMonitoredErrorsInactive();
		monitoredErrors.loadFromConfigurationFile();
	}
	
	private void setAllMonitoredErrorsInactive()
	{
		for(MonitoredError me : monitoredErrors)
		{
			me.setActive(false);
		}
	}
	
	private void addMonitoredErrorOrSetActive(String errorMsg)
	{
		for(MonitoredError monitoredError : monitoredErrors)
		{
			if(monitoredError.isErrorMsgMonitored(errorMsg))
			{
				monitoredError.setActive(true);
				return;
			}
		}
		// this error message is not already monitored, add it
		this.monitoredErrors.add(new MonitoredError(errorMsg, true));
	}
	
	/**
	 * Increment the count for an error message if enabled and this error message is monitored
	 * @param errorMsg
	 */
	public synchronized static void addIfMonitored(String errorMsg)
	{
		MonitoredErrors monitoredErrors = getSingleton();
		
		if(monitoredErrors.enabled && errorMsg != null && errorMsg.length() > 0)
		{
			for(MonitoredError monitoredError : monitoredErrors.monitoredErrors)
			{
				if(monitoredError.isActive() && monitoredError.isErrorMsgMonitored(errorMsg))
				{
					monitoredError.incrementErrorCount();
					break;
				}
			}
		}
	}
	
}
