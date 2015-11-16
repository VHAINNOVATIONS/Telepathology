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
package gov.va.med.imaging.exchange.configuration;

import gov.va.med.imaging.core.interfaces.IAppConfiguration;
import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBUCKD
 * Do not add member variables here. Instead put them in AppConfiguration.
 */
public class AppConfigurationStub 
implements IAppConfiguration 
{
	private final static Logger logger = Logger.getLogger(AppConfigurationStub.class);
	
	private static final String APP_CONFIG_FILENAME = "ViXConfig.xml";
	
	private boolean cachingEnabled;
	private String biaPassword;
	private String biaUsername;
	private String localSiteNumber;
	private URI siteServiceUri;
	private URI smtpServerUri;
	private String vistaImagingVersionRequired;
	private String vixSoftwareVersion;
	private boolean decompressionEnabled;
	private boolean downSamplingEnabled;
	private boolean noLosslessCompression;
	private boolean sensitivePatientBlocked;
	private boolean vixConfigured;
	private boolean vixEnabled;
	private String appConfigurationFilespec = null;
	private String vixConfigurationDirectory = null;
	
	
	public AppConfigurationStub() {
		super();
	}
	
	/**
	 * Initialization called by Spring to initialize singleton.
	 * Note that this method is not a part of the IAppConfiguration interface
	 * @throws ApplicationConfigurationException 
	 */
	public void init() 
	throws ApplicationConfigurationException 
	{
		// a concession for the development environment - create the directory that the app configuration
		// can reside in
		File configFile = new File(this.getAppConfigurationFilespec());
		File configDir = configFile.getParentFile();
		if (configDir != null && !configDir.exists())
		{
			configDir.mkdirs();
		}
		this.cachingEnabled = true;
		this.downSamplingEnabled = false;
		this.noLosslessCompression = false;
		this.decompressionEnabled=true;
		try 
		{
			// establish remaining development environment defaults
			this.siteServiceUri = new URI("http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx");
			this.smtpServerUri = new URI("smtp.va.gov");
		}
		catch (URISyntaxException ex)
		{
			throw new ApplicationConfigurationException(ex);
		}
		this.localSiteNumber = "660";
		this.vixSoftwareVersion = "0.00";
		this.biaUsername = "";
		this.biaPassword = "";
		this.vixEnabled = true;

		logger.info("using class AppConfigurationStub");		
	}

	@Override
	public String getLocalSiteNumber() {
		return localSiteNumber;
	}

	@Override
	public URI getSmtpServerUri() {
		return smtpServerUri;
	}

	@Override
	public String getVistAImagingVersionRequired()
			throws ApplicationConfigurationException {
		return vistaImagingVersionRequired;
	}

	@Override
	public String getVixConfigurationDirectory()
			throws ApplicationConfigurationException {
		String configDir = null;
		configDir = this.vixConfigurationDirectory;
		
		
		if (configDir == null)
		{
			configDir = System.getenv("vixconfig");
			if (configDir == null)
			{
				throw new ApplicationConfigurationException("The vixconfig has not been set.");
//				configDir = this.getBootstrapAppConfigurationDirectory();
			}

			this.setVixConfigurationDirectory(configDir);
		}
		return configDir;
	}

	@Override
	public String getVixSoftwareVersion() {
		return vixSoftwareVersion;
	}

	@Override
	public boolean isCachingEnabled() {
		return cachingEnabled;
	}

	@Override
	public boolean isDecompressionEnabled() {
		return decompressionEnabled;
	}

	@Override
	public boolean isDownSamplingEnabled() {
		return downSamplingEnabled;
	}

	@Override
	public boolean isNoLosslessCompression() {
		return noLosslessCompression;
	}

	@Override
	public boolean isSensitivePatientBlocked() {
		return sensitivePatientBlocked;
	}

	@Override
	public boolean loadAppConfigurationFromFile()
			throws ApplicationConfigurationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean saveAppConfigurationToFile()
			throws ApplicationConfigurationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCachingEnabled(boolean enabled) {
		this.cachingEnabled = enabled;
	}

	@Override
	public void setDecompressionEnabled(boolean enabled) {
		this.decompressionEnabled = enabled;
	}

	@Override
	public void setDownSamplingEnabled(boolean enabled) {
		this.downSamplingEnabled = enabled;
	}

	@Override
	public void setLocalSiteNumber(String siteNumber) {
		this.localSiteNumber = siteNumber;
	}

	@Override
	public void setNoLosslessCompression(boolean enabled) {
		this.noLosslessCompression = enabled;
	}

	@Override
	public void setSensitivePatientBlocked(boolean enabled) {
		this.sensitivePatientBlocked = enabled;
	}

	@Override
	public void setSmtpServerUri(URI uri) {
		this.smtpServerUri = uri;
	}

	@Override
	public void setVistAImagingVersionRequired(String version) {
		this.vistaImagingVersionRequired = version;
	}

	@Override
	public void setVixSoftwareVersion(String vixSoftwareVersion) {
		this.vixSoftwareVersion = vixSoftwareVersion;
	}
	
	public String getAppConfigurationFilespec() throws ApplicationConfigurationException
	{
		String fileSpec = null;
		fileSpec = this.appConfigurationFilespec;

		if (fileSpec == null)
		{
			fileSpec = this.getVixConfigurationDirectory();
			// add the trailing file separator character if necessary
			if (!fileSpec.endsWith("\\") || !fileSpec.endsWith("/"))
			{
				fileSpec += "/";
			}
			fileSpec += APP_CONFIG_FILENAME;

			this.setAppConfigurationFilespec(fileSpec);
		}
		
		return fileSpec;
	}
	
	public void setAppConfigurationFilespec(String fileSpec) {
		this.appConfigurationFilespec = fileSpec;	
	}

	public void setVixConfigurationDirectory(String vixConfigurationDirectory) {
		this.vixConfigurationDirectory = vixConfigurationDirectory;	
	}
}
