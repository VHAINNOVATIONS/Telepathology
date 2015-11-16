/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 6, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWBUCKD
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



import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;

import java.net.URI;

public interface IAppConfiguration {
	//-----------------------------------
	// properties
	//-----------------------------------

	/**
	 * @return true if image and storage caching is enabled
	 */
	public abstract boolean isCachingEnabled();

	/**
	 * @param enabled
	 */
	public abstract void setCachingEnabled(boolean enabled);
	
	
	/**
	 * @return true if VA sensitive patients should be blocked to the DOD
	 */
	public abstract boolean isSensitivePatientBlocked();
	
	/**
	 * @param enabled
	 */
	public abstract void setSensitivePatientBlocked(boolean enabled);

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

	/**
	 * @return the URI that specifies the location of the SMTP server used to send email notifications from the ViX. This is
	 * usually loaded from the appConfigBootstrapProperties.properties file.
	 */
	public abstract URI getSmtpServerUri();
	
	/**
	 * @param uri
	 */
	public abstract void setSmtpServerUri(URI uri);
	
	/**
	 * @return the version number of the ViX software (see the appConfigBootstrapProperties.properties file)
	 */
	public abstract String getVixSoftwareVersion();

	/**
	 * @param vixSoftwareVersion
	 */
	public abstract void setVixSoftwareVersion(String vixSoftwareVersion);
	
	/**
	 * @return the localSiteNumber where the ViX server is installed
	 */
	public abstract String getLocalSiteNumber();
	
	/**
	 * @param siteNumber
	 */
	public abstract void setLocalSiteNumber(String siteNumber);
	
	//-----------------------------------
	// persistence methods
	//-----------------------------------

	/**
	 * @throws ApplicationConfigurationException
	 */
	public abstract boolean loadAppConfigurationFromFile() throws ApplicationConfigurationException;
	
	/**
	 * @throws ApplicationConfigurationException
	 */
	public abstract boolean saveAppConfigurationToFile() throws ApplicationConfigurationException;

	//-----------------------------------
	// other methods
	//-----------------------------------
	
	/**
	 * @throws ApplicationConfigurationException
	 */
	public abstract String getVixConfigurationDirectory() throws ApplicationConfigurationException;
	
	public abstract String getVistAImagingVersionRequired() throws ApplicationConfigurationException;
	
	public abstract void setVistAImagingVersionRequired(String version);
	
}