/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 17, 2013
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
package gov.va.med.imaging.exchange.business.configuration;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * @author vhaiswwerfej
 *
 */
public class ImagingFacadeConfiguration
extends AbstractBaseFacadeConfiguration 
{

	private boolean localEnabled;
	private boolean enterpriseEnabled;
	
	public ImagingFacadeConfiguration()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		this.localEnabled = true;
		this.enterpriseEnabled = false;
		return this;
	}
	
	public synchronized static ImagingFacadeConfiguration getConfiguration()	
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
					ImagingFacadeConfiguration.class);
		}
		catch(CannotLoadConfigurationException clcX)
		{
			// no need to log, already logged
			return null;
		}
	}
	
	public static void main(String [] args)
	{
		ImagingFacadeConfiguration config = getConfiguration();
		
		if(args.length == 2)
		{
			config.setLocalEnabled(Boolean.parseBoolean(args[0]));
			config.setEnterpriseEnabled(Boolean.parseBoolean(args[1]));
			config.storeConfiguration();
		}
		else
		{
			System.out.println("Requires 2 parameters <localEnabled> <enterpriseEnabled>");
		}
	}

	public boolean isLocalEnabled()
	{
		return localEnabled;
	}

	public void setLocalEnabled(boolean localEnabled)
	{
		this.localEnabled = localEnabled;
	}

	public boolean isEnterpriseEnabled()
	{
		return enterpriseEnabled;
	}

	public void setEnterpriseEnabled(boolean enterpriseEnabled)
	{
		this.enterpriseEnabled = enterpriseEnabled;
	}

}
