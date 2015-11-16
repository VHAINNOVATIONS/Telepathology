/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 10, 2006
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

import java.beans.BeanInfo;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.MissingResourceException;
//import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBUCKD
 * This class holds the configuration for the ViX web app. It uses the XmlEncoder/XmlDecoder classes to manage persistance.
 */
public class AppConfiguration implements IAppConfiguration
{
    public static boolean loadAppConfigurationfromFile = true; // for use by unit tests - not production code
    private static final String APP_CONFIG_FILENAME = "ViXConfig.xml";
    
	protected final Logger logger = Logger.getLogger(this.getClass());
	//	protected static org.apache.commons.logging.Log log = null;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    protected final Lock readLock = rwl.readLock();
    protected final Lock writeLock = rwl.writeLock();

    // transient members - never persisted - initialized on demand in the respective getters
    // do not access directly
	private String appConfigurationFilespec = null;
	private String vixConfigurationDirectory = null;
	
	protected String vixSoftwareVersion = null;
	
	// members exposed as public properties that may be persisted
	protected URI smtpServerUri = null;
	protected boolean cachingEnabled = false;
	protected boolean downSamplingEnabled = false;
	protected boolean noLosslessCompression = false;
	protected boolean decompressionEnabled = false;
	protected String localSiteNumber = null;
    protected boolean sensitivePatientBlocked = false;
    
    protected String vistaImagingVersionRequired = "3.0P83";// null;
	
	/**
	 * Default constructor
	 */
	public AppConfiguration() {
		logger.info("AppConfiguration() - " + this.hashCode());
	}
	
	/**
	 * Initialization called by Spring to load the configuration information from the database.
	 * Note that this method is not a part of the IAppConfiguration interface
	 */
	public void init()  throws ApplicationConfigurationException{
		logger.info("AppConfiguration:Init() - " + this.hashCode());
		// Transient properties - always access through getter to ensure proper initialization
		// Note: this is where the ApplicationConfigurationException could be thrown (in theory)
		// In practice, this error will be caught in development because Spring initialization
		// will fail.
		this.markPropertyAsTransient("vixConfigurationDirectory"); //determined at run time
		this.markPropertyAsTransient("appConfigurationFilespec"); //determined at run time 
		
		//initialize appConfigurationFilespec and appConfigurationDirectory properties.
		
		if (AppConfiguration.loadAppConfigurationfromFile == true)
		{
			// attempt to load the app configuration from file
			this.loadAppConfigurationFromFile();
		}
	}

	//-----------------------------------
	// IAppConfiguration properties
	//-----------------------------------

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#isCachingEnabled()
	 */
	public boolean isCachingEnabled() {
		this.readLock.lock();
		try {return this.cachingEnabled;}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#setCachingEnabled(boolean)
	 */
	public void setCachingEnabled(boolean enabled) {
		this.writeLock.lock();
		try {this.cachingEnabled = enabled;}
		finally {this.writeLock.unlock();}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#isSensitivePatientBlocked()
	 */
	public boolean isSensitivePatientBlocked() {
		this.readLock.lock();
		try {return this.sensitivePatientBlocked;}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#setSensitivePatientBlocked(boolean)
	 */
	public void setSensitivePatientBlocked(boolean enabled) {
		this.writeLock.lock();
		try {this.sensitivePatientBlocked = enabled;}
		finally {this.writeLock.unlock();}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#isDownSamplingEnabled()
	 */
	public boolean isDownSamplingEnabled() {
		this.readLock.lock();
		try {return this.downSamplingEnabled;}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#setDownSamplingEnabled(boolean)
	 */
	public void setDownSamplingEnabled(boolean enabled) {
		this.writeLock.lock();
		try {this.downSamplingEnabled = enabled;}
		finally {this.writeLock.unlock();}
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#isNoLosslessCompression()
	 */
	public boolean isNoLosslessCompression() {
		this.readLock.lock();
		try {return this.noLosslessCompression;}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#setNoLosslessCompression(boolean)
	 */
	public void setNoLosslessCompression(boolean enabled) {
		this.writeLock.lock();
		try {this.noLosslessCompression = enabled;}
		finally {this.writeLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#isNoLosslessCompression()
	 */
	public boolean isDecompressionEnabled() {
		this.readLock.lock();
		try {return this.decompressionEnabled;}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#setNoLosslessCompression(boolean)
	 */
	public void setDecompressionEnabled(boolean enabled) {
		this.writeLock.lock();
		try {this.decompressionEnabled = enabled;}
		finally {this.writeLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#getSmtpServerUri()
	 */
	public URI getSmtpServerUri() {
		this.readLock.lock();
		try {return this.smtpServerUri;}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#setSmtpServerUri(java.net.URI)
	 */
	public void setSmtpServerUri(URI uri) {
		this.writeLock.lock();
		try {this.smtpServerUri = uri;}
		finally {this.writeLock.unlock();}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#getVixSoftwareVersion()
	 */
	public String getVixSoftwareVersion() {
		this.readLock.lock();
		try {return this.vixSoftwareVersion;}
		finally {this.readLock.unlock();}
	}

	public void setVixSoftwareVersion(String vixSoftwareVersion) {
		this.writeLock.lock();
		try {this.vixSoftwareVersion = vixSoftwareVersion;}
		finally {this.writeLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#getLocalSiteNumber()
	 */
	public String getLocalSiteNumber() {
		this.readLock.lock();
		try {return this.localSiteNumber;}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#setLocalSiteNumber(java.lang.String)
	 */
	public void setLocalSiteNumber(String siteNumber) {
		this.writeLock.lock();
		try {this.localSiteNumber = siteNumber;}
		finally {this.writeLock.unlock();}
	}

	//-----------------------------------
	// IAppConfiguration methods
	//-----------------------------------
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#loadAppConfigurationFromFile()
	 */
	public boolean loadAppConfigurationFromFile() {
		boolean success = false;
		try
		{
			success = this.loadAppConfigurationFromFile(this.getAppConfigurationFilespec()); // uses read lock
		}
		catch (ApplicationConfigurationException ex) {}
		return success;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IAppConfiguration#saveAppConfigurationToFile()
	 */
	public boolean saveAppConfigurationToFile() {
		boolean success = false;
		XMLEncoder xmlEncoder = null;

		this.readLock.lock();
		try
		{
			xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(this.getAppConfigurationFilespec())));
			// add a PersistenceDelegate for the URI class
			xmlEncoder.setPersistenceDelegate(URI.class,
					new PersistenceDelegate() {
						@Override
						protected Expression instantiate(Object oldInstance, Encoder out) {
							return new Expression(oldInstance,oldInstance.getClass(),"new", new Object[]{ oldInstance.toString() });
						}
					});
			xmlEncoder.writeObject(this);
			logger.info("AppConfiguration: configuration saved to: " + this.getAppConfigurationFilespec());
			success = true;
		}
		catch (ApplicationConfigurationException ex)
		{
			logger.error(ex.getMessage());
		}
		catch (FileNotFoundException ex)
		{
			logger.error("AppConfiguration.saveAppConfigurationToFile: " + ex.getMessage());
		}
		finally
		{
			this.readLock.unlock();
			if (xmlEncoder != null)
			{
				xmlEncoder.close();
			}
		}

		return success;
	}

	/**
	 * @return the vixConfigurationDirectory
	 */
	public String getVixConfigurationDirectory() throws ApplicationConfigurationException
	{
		String configDir = null;
		this.readLock.lock();
		try {configDir = this.vixConfigurationDirectory;}
		finally {this.readLock.unlock();}
		
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
	
	//--------------------------------------------------------------------
	// public methods that are not part of the IAppConfiguration interface
	//--------------------------------------------------------------------

	/**
	 * @param fileSpec - the file that contains the application configuration
	 * @return true if the configuration was sucessfully loaded from the file specified by appConfigurationFilespec
	 */
	public boolean loadAppConfigurationFromFile(String fileSpec) {
		boolean success = false;
		XMLDecoder xmlDecoder = null;
		AppConfiguration appConfiguration = null;

		if (fileSpec != null)
		{
			File configFile = new File(fileSpec);
			if (configFile.exists())
			{
				try
				{
					xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileSpec))); // throws FileNotFoundException
					appConfiguration = (AppConfiguration) xmlDecoder.readObject();
					if (appConfiguration != null)
					{
						this.assignState(appConfiguration); // this method obtains a write lock
						logger.info("AppConfiguration: loaded Vix configuration from: " + fileSpec);
						success = true;
					}
				}
				catch (FileNotFoundException ex)
				{
					logger.error(ex.getMessage());
				}
				catch (ArrayIndexOutOfBoundsException ex)
				{
					logger.error("AppConfiguration.loadAppConfigurationFromFile: ArrayIndexOutOfBoundsException : " + ex.getMessage());
				}
				finally
				{
					if (xmlDecoder != null)
					{
						xmlDecoder.close();
					}
				}
			}
		}
		// perform the bootStrap initialization of any property that has not already been initialized.
//		this.loadBootStrapProperties();
		return success;
	}


	/**
	 * @param vixConfigurationDirectory the vixConfigurationDirectory to set
	 */
	public void setVixConfigurationDirectory(String vixConfigurationDirectory) {
		this.writeLock.lock();
		try {this.vixConfigurationDirectory = vixConfigurationDirectory;}
		finally {this.writeLock.unlock();}
	}
	/**
	 * @param set the fully qualified file that contains the persisted state of this object - for use by unit tests
	 */
	public void setAppConfigurationFilespec(String fileSpec) {
		this.writeLock.lock();
		try {this.appConfigurationFilespec = fileSpec;}
		finally {this.writeLock.unlock();}
	}

	/**
	 * @return the fully qualified file that contains the persisted state of this object
	 */
	public String getAppConfigurationFilespec() throws ApplicationConfigurationException
	{
		String fileSpec = null;
		this.readLock.lock();
		try {fileSpec = this.appConfigurationFilespec;}
		finally {this.readLock.unlock();}

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

	/**
	 * @param vixSoftwareVersion - the version of the ViX software - for use by unit tests
	 */
//	public void setVixSoftwareVersion(String vixSoftwareVersion)
//	{
//		this.writeLock.lock();
//		try {this.vixSoftwareVersion = vixSoftwareVersion;}
//		finally {this.writeLock.unlock();}
//	}

	/**
	 * @param appConfig - the AppConfiguration object that contains the state to use
	 * This public method takes the state of the passed appConfiguration param and overlays it onto the current instance
	 */
	public void assignState(AppConfiguration appConfiguration)
	{
		if (appConfiguration != null)
		{
			this.writeLock.lock();
			appConfiguration.readLock.lock();
			try
			{
				// TODO: consider doing via inspection
// Transient properties do not participate				
//				this.appConfigurationFilespec = appConfiguration.appConfigurationFilespec;
//				this.vixConfigurationDirectory = appConfiguration.vixConfigurationDirectory;
//				this.vixEnabled = appConfiguration.vixEnabled;
//				this.vixConfigured = appConfiguration.vixConfigured;
				this.cachingEnabled = appConfiguration.cachingEnabled;
				this.downSamplingEnabled = appConfiguration.downSamplingEnabled;
				this.noLosslessCompression = appConfiguration.noLosslessCompression;
				this.decompressionEnabled = appConfiguration.decompressionEnabled;
				this.localSiteNumber = appConfiguration.localSiteNumber;
				this.smtpServerUri = appConfiguration.smtpServerUri;
				this.vixSoftwareVersion = appConfiguration.vixSoftwareVersion;
				this.sensitivePatientBlocked = appConfiguration.sensitivePatientBlocked;
			}
			finally
			{
				this.writeLock.unlock();
				appConfiguration.readLock.unlock();
			}
		}
	}
	
	//-----------------------------------
	// private methods
	//-----------------------------------
	
	/**
	 * Mark a bean property as transient so that the XmlEncoder will not include it as part of the
	 * persisted state.
	 * @param propertyName - the property to mark as transient
	 * @throws ApplicationConfigurationException
	 * @see gov.va.med.imaging.exchange.configuration.AppConfiguration#saveAppConfigurationToFile()
	 * @see gov.va.med.imaging.exchange.configuration.AppConfiguration#loadAppConfigurationFromFile()
	 */
	private void markPropertyAsTransient(String propertyName) throws ApplicationConfigurationException
	{
		BeanInfo info = null;
		boolean propertySet = false;
		
		try {
			info = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; ++i)
			{
				PropertyDescriptor pd = propertyDescriptors[i];
				if (pd.getName().equals(propertyName))
				{
					pd.setValue("transient", Boolean.TRUE);
					propertySet = true;
					break;
				}
			}
			if (!propertySet)
			{
				throw new ApplicationConfigurationException("markPropertyAsTransient: property not found : " + propertyName);
			}
		}
		catch (IntrospectionException ex) {
			throw new ApplicationConfigurationException(ex.getMessage());
		}
	}

	/*
	 * The following method was generated by Eclipse. If you regenerate this method, note that:
	 * transient property appConfigurationFilespec does not participate.
	 * transient property vixConfigurationDirectory does not participate
	 * all final members do not participate 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		this.readLock.lock();
		try
		{
			result = PRIME * result + (cachingEnabled ? 1231 : 1237);
			result = PRIME * result + (downSamplingEnabled ? 1231 : 1237);
			result = PRIME * result + ((localSiteNumber == null) ? 0 : localSiteNumber.hashCode());
			result = PRIME * result + (noLosslessCompression ? 1231 : 1237);
			result = PRIME * result + (decompressionEnabled ? 1231 : 1237);
			result = PRIME * result + (sensitivePatientBlocked ? 1231 : 1237);
			result = PRIME * result + ((smtpServerUri == null) ? 0 : smtpServerUri.hashCode());
			result = PRIME * result + ((vixSoftwareVersion == null) ? 0 : vixSoftwareVersion.hashCode());
		}
		finally {this.readLock.unlock();}
		return result;
	}

	/* 
	 * The following method was generated by Eclipse. If you regenerate this method, note that:
	 * transient property appConfigurationFilespec does not participate.
	 * transient property vixConfigurationDirectory does not participate
	 * all final members do not participate 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		this.readLock.lock();
		try
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final AppConfiguration other = (AppConfiguration) obj;
			if (cachingEnabled != other.cachingEnabled)
				return false;
			if (downSamplingEnabled != other.downSamplingEnabled)
				return false;
			if (localSiteNumber == null) {
				if (other.localSiteNumber != null)
					return false;
			} else if (!localSiteNumber.equals(other.localSiteNumber))
				return false;
			if (noLosslessCompression != other.noLosslessCompression)
				return false;
			if (decompressionEnabled != other.decompressionEnabled)
				return false;
			if (sensitivePatientBlocked != other.sensitivePatientBlocked)
				return false;
			if (smtpServerUri == null) {
				if (other.smtpServerUri != null)
					return false;
			} else if (!smtpServerUri.equals(other.smtpServerUri))
				return false;
			if (vixSoftwareVersion == null) {
				if (other.vixSoftwareVersion != null)
					return false;
			} else if (!vixSoftwareVersion.equals(other.vixSoftwareVersion))
				return false;
			return true;
		}
		finally {this.readLock.unlock();}
	}

	@Override
	public String getVistAImagingVersionRequired()
			throws ApplicationConfigurationException {
		this.readLock.lock();
		try {return this.vistaImagingVersionRequired;}
		finally {this.readLock.unlock();}
	}

	@Override
	public void setVistAImagingVersionRequired(String version) {
		this.writeLock.lock();
		try {this.vistaImagingVersionRequired = version;}
		finally {this.writeLock.unlock();}
	}

}

