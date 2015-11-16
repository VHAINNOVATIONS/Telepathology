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
package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.exchange.business.ImageFormatAllowableConversionList;
import gov.va.med.imaging.exchange.enums.ImageFormat;

import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
public interface IImageConversionConfiguration 
{
	/**
	 * @return true if image downsampling is enabled
	 */
	public abstract boolean isDownSamplingEnabled();

	/**
	 * @param enabled
	 */
	public abstract void setDownSamplingEnabled(boolean enabled);
	
	/**
	 * @return true if image lossless comression is disabled (default is false)
	 */
	public abstract boolean isNoLosslessCompression();

	/**
	 * @param enabled
	 */
	public abstract void setNoLosslessCompression(boolean enabled);

	/**
	 * @return true if image decomression is enabled (default is false)
	 */
	public abstract boolean isDecompressionEnabled();

	/**
	 * @param enabled
	 */
	public abstract void setDecompressionEnabled(boolean enabled);
	
	public abstract boolean loadImageConversionConfigurationFromFile();
	
	public abstract boolean saveImageConversionConfigurationToFile();
	
	public abstract List<ImageFormatAllowableConversionList> getFormatConfigurations();
	
	public abstract ImageFormatAllowableConversionList getFormatConfiguration(ImageFormat format);
}
