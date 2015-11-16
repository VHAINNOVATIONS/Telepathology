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

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * @author VHAISWWERFEJ
 *
 */
public class MonitoredErrorConfiguration
extends AbstractBaseFacadeConfiguration
{
	private List<String> monitoredErrors;
	private boolean enabled;
	
	public MonitoredErrorConfiguration()
	{
		super();
		monitoredErrors = new ArrayList<String>();
	}

	/**
	 * @return the monitoredErrors
	 */
	public List<String> getMonitoredErrors()
	{
		return monitoredErrors;
	}
	
	/**
	 * Only adds the new monitored error if it doesn't already exist in the list. This is a case sensitive comparison
	 * @param newMonitoredError
	 * @return True if it is added, false if it already exists in the list.
	 */
	public boolean addUniqueMonitoredError(String newMonitoredError)
	{
		for(String monitoredError : monitoredErrors)
		{
			if(monitoredError.equals(newMonitoredError))
				return false;
		}
		this.monitoredErrors.add(newMonitoredError);
		return true;
	}

	/**
	 * @param monitoredErrors the monitoredErrors to set
	 */
	public void setMonitoredErrors(List<String> monitoredErrors)
	{
		this.monitoredErrors = monitoredErrors;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		this.enabled = false;
		return this;
	}
	
	public synchronized static MonitoredErrorConfiguration getMonitoredConfiguration()
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(MonitoredErrorConfiguration.class);
		}
		catch(CannotLoadConfigurationException clcX)
		{
			// no need to log, already logged
			return null;
		}
	}

	/*
	public boolean isErrorMonitored(String errorMessage)
	{
		if(enabled && errorMessage != null && errorMessage.length() > 0)
		{
			if(this.monitoredErrors != null)
			{
				for(String monitoredError : monitoredErrors)
				{
					if(errorMessage.contains(monitoredError))
						return true;
				}
			}
		}
		return false;
	}*/

	public static void main(String [] args)
	{
		MonitoredErrorConfiguration mec = getMonitoredConfiguration();
		boolean enabled = false;
		if(args.length > 0)
		{
			enabled = Boolean.parseBoolean(args[0]);
		}
		else
		{
			System.out.println("enabled true|false <monitored error 1> <monitored error 2> ...");
		}
		mec.setEnabled(enabled);
		for(int i = 1; i < args.length; i++)
		{
			mec.monitoredErrors.add(args[i].trim());			
		}
		mec.storeConfiguration();
	}
}
