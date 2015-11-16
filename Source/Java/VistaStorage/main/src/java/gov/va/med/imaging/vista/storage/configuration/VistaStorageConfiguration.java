/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 12, 2010
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
package gov.va.med.imaging.vista.storage.configuration;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * Configuration properties for VistaStorage
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaStorageConfiguration 
extends AbstractBaseFacadeConfiguration
{
	private boolean readFileIntoBuffer = true;
	
	public VistaStorageConfiguration()
	{
		super();
	}
		
	public synchronized static VistaStorageConfiguration getVistaStorageConfiguration()
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
					VistaStorageConfiguration.class);
		}
		catch(CannotLoadConfigurationException clcX)
		{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		this.readFileIntoBuffer = true;
		return this;
	}

	/**
	 * Determines if the file should be read into a buffer or not
	 * 
	 * @return the readFileIntoBuffer
	 */
	public boolean isReadFileIntoBuffer()
	{
		return readFileIntoBuffer;
	}

	/**
	 * @param readFileIntoBuffer the readFileIntoBuffer to set
	 */
	public void setReadFileIntoBuffer(boolean readFileIntoBuffer)
	{
		this.readFileIntoBuffer = readFileIntoBuffer;
	}
}
