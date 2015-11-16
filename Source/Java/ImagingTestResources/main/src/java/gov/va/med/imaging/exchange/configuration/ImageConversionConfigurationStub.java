/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 17, 2008
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
package gov.va.med.imaging.exchange.configuration;

import java.util.List;

import gov.va.med.imaging.core.interfaces.IImageConversionConfiguration;
import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;
import gov.va.med.imaging.exchange.business.ImageFormatAllowableConversionList;
import gov.va.med.imaging.exchange.enums.ImageFormat;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ImageConversionConfigurationStub 
implements IImageConversionConfiguration 
{
	
	protected boolean downSamplingEnabled = false;
	protected boolean noLosslessCompression = false;
	protected boolean decompressionEnabled = false;
	
	public void init() throws ApplicationConfigurationException
	{
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#isDecompressionEnabled()
	 */
	@Override
	public boolean isDecompressionEnabled() 
	{
		return decompressionEnabled;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#isDownSamplingEnabled()
	 */
	@Override
	public boolean isDownSamplingEnabled() 
	{
		return downSamplingEnabled;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#isNoLosslessCompression()
	 */
	@Override
	public boolean isNoLosslessCompression() 
	{
		return noLosslessCompression;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#loadImageConversionConfigurationFromFile()
	 */
	@Override
	public boolean loadImageConversionConfigurationFromFile() 
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#saveImageConversionConfigurationToFile()
	 */
	@Override
	public boolean saveImageConversionConfigurationToFile() 
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#setDecompressionEnabled(boolean)
	 */
	@Override
	public void setDecompressionEnabled(boolean enabled) 
	{
		this.decompressionEnabled = enabled;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#setDownSamplingEnabled(boolean)
	 */
	@Override
	public void setDownSamplingEnabled(boolean enabled) 
	{
		this.downSamplingEnabled = enabled;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.IImageConversionConfiguration#setNoLosslessCompression(boolean)
	 */
	@Override
	public void setNoLosslessCompression(boolean enabled) 
	{
		this.noLosslessCompression = enabled;
	}

	@Override
	public ImageFormatAllowableConversionList getFormatConfiguration(
			ImageFormat format) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ImageFormatAllowableConversionList> getFormatConfigurations() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
