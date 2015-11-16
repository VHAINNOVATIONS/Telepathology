/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 2, 2008
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
package gov.va.med.imaging.federationdatasource.configuration;

import gov.va.med.imaging.exchange.enums.ImageFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Federation datasource configuration details. This includes all of the information used to 
 * communicate with a Federation Web application using Certificate authentication
 * 
 * 
 * @author vhaiswwerfej
 *
 */
public class FederationConfiguration 
implements Serializable 
{
	private final static long serialVersionUID = 1L;	
	
	public final static String defaultKeystoreUrl = "file:///c:/VixConfig/federation.keystore";
	public final static String defaultTruststoreUrl = "file:///c:/vixconfig/federation.truststore";
	public final static String defaultFederationSslProtocol = "https";
	
	public final static String federationVersion3Number = "3";
	
	private String truststoreUrl;
	private String keystoreUrl;
	private String keystorePassword;
	private String truststorePassword;
	private String federationSslProtocol;
	private boolean addCompressionForImageRequests = true;
	private Integer metadataTimeoutMs = null;
	private Map<String, List<ImageFormat>> allowedImageFormats = 
		new HashMap<String, List<ImageFormat>>();
	
	public FederationConfiguration()
	{
		super();
		truststorePassword = "";
		truststoreUrl = "";
		keystorePassword = "";
		keystoreUrl = "";
		federationSslProtocol = "";
		metadataTimeoutMs = 600000; // 5 minutes
	}
	
	/**
	 * 
	 * @param truststoreUrl
	 * @param keystoreUrl
	 * @param keystorePassword
	 * @param truststorePassword
	 * @param federationSslProtocol
	 */
	public FederationConfiguration(String truststoreUrl, String keystoreUrl,
			String keystorePassword, String truststorePassword, String federationSslProtocol) {
		this();
		this.truststoreUrl = truststoreUrl;
		this.keystoreUrl = keystoreUrl;
		this.keystorePassword = keystorePassword;
		this.truststorePassword = truststorePassword;
		this.federationSslProtocol = federationSslProtocol;
	}
	
	public static FederationConfiguration createConfiguration(String keystoreUrl, String keystorePassword, 
			String truststoreUrl, String truststorePassword, String federationSslProtocol)
	{
		if(keystoreUrl == null)
			keystoreUrl = defaultKeystoreUrl;
		if(truststoreUrl == null)
			truststoreUrl = defaultTruststoreUrl;
		if(federationSslProtocol == null)
			federationSslProtocol = defaultFederationSslProtocol;
		FederationConfiguration config = new FederationConfiguration(truststoreUrl, keystoreUrl, 
				keystorePassword, truststorePassword, federationSslProtocol);
		loadDefaultAllowedImageFormats(config);
		return config;
	}

	public static FederationConfiguration createDefaultConfiguration(String keystorePassword, String truststorePassword)
	{
		FederationConfiguration config = new FederationConfiguration(defaultTruststoreUrl, 
				defaultKeystoreUrl, keystorePassword, truststorePassword, defaultFederationSslProtocol);
		loadDefaultAllowedImageFormats(config);
		return config;
	}
	
	private static void loadDefaultAllowedImageFormats(FederationConfiguration config)
	{
		// These are the allowed formats for patch 83 (federation version 3)
		List<ImageFormat> fedV3ImageFormats = new ArrayList<ImageFormat>();
		fedV3ImageFormats.add(ImageFormat.ANYTHING);
		fedV3ImageFormats.add(ImageFormat.TGA);
		fedV3ImageFormats.add(ImageFormat.DOWNSAMPLEDTGA);
		fedV3ImageFormats.add(ImageFormat.TIFF);
		fedV3ImageFormats.add(ImageFormat.BMP);
		fedV3ImageFormats.add(ImageFormat.JPEG);
		fedV3ImageFormats.add(ImageFormat.J2K);
		fedV3ImageFormats.add(ImageFormat.DICOM);
		fedV3ImageFormats.add(ImageFormat.DICOMJPEG);
		fedV3ImageFormats.add(ImageFormat.DICOMJPEG2000);
		fedV3ImageFormats.add(ImageFormat.DICOMPDF);
		fedV3ImageFormats.add(ImageFormat.PDF);
		fedV3ImageFormats.add(ImageFormat.TEXT_DICOM);
		fedV3ImageFormats.add(ImageFormat.TEXT_PLAIN);
		fedV3ImageFormats.add(ImageFormat.IMAGE);
		fedV3ImageFormats.add(ImageFormat.APPLICATION);
		fedV3ImageFormats.add(ImageFormat.DOC);
		fedV3ImageFormats.add(ImageFormat.AVI);
		fedV3ImageFormats.add(ImageFormat.RTF);
		fedV3ImageFormats.add(ImageFormat.WAV);
		fedV3ImageFormats.add(ImageFormat.HTML);
		fedV3ImageFormats.add(ImageFormat.MP3);
		fedV3ImageFormats.add(ImageFormat.MPG);
		fedV3ImageFormats.add(ImageFormat.ORIGINAL);
		fedV3ImageFormats.add(ImageFormat.GIF);
		fedV3ImageFormats.add(ImageFormat.PNG);		
		config.allowedImageFormats.put(federationVersion3Number, fedV3ImageFormats);
	}

	/**
	 * @return the truststoreUrl
	 */
	public String getTruststoreUrl() {
		return truststoreUrl;
	}

	/**
	 * @param truststoreUrl the truststoreUrl to set
	 */
	public void setTruststoreUrl(String truststoreUrl) {
		this.truststoreUrl = truststoreUrl;
	}

	/**
	 * @return the keystoreUrl
	 */
	public String getKeystoreUrl() {
		return keystoreUrl;
	}

	/**
	 * @param keystoreUrl the keystoreUrl to set
	 */
	public void setKeystoreUrl(String keystoreUrl) {
		this.keystoreUrl = keystoreUrl;
	}

	/**
	 * @return the keystorePassword
	 */
	public String getKeystorePassword() {
		return keystorePassword;
	}

	/**
	 * @param keystorePassword the keystorePassword to set
	 */
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	/**
	 * @return the truststorePassword
	 */
	public String getTruststorePassword() {
		return truststorePassword;
	}

	/**
	 * @param truststorePassword the truststorePassword to set
	 */
	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	/**
	 * @return the federationSslProtocol
	 */
	public String getFederationSslProtocol() {
		return federationSslProtocol;
	}

	/**
	 * @param federationSslProtocol the federationSslProtocol to set
	 */
	public void setFederationSslProtocol(String federationSslProtocol) {
		this.federationSslProtocol = federationSslProtocol;
	}
	
	/**
	 * @return the addCompressionForImageRequests
	 */
	public boolean isAddCompressionForImageRequests()
	{
		return addCompressionForImageRequests;
	}

	/**
	 * @param addCompressionForImageRequests the addCompressionForImageRequests to set
	 */
	public void setAddCompressionForImageRequests(
		boolean addCompressionForImageRequests)
	{
		this.addCompressionForImageRequests = addCompressionForImageRequests;
	}

	/**
	 * @return the metadataTimeoutMs
	 */
	public Integer getMetadataTimeoutMs()
	{
		return metadataTimeoutMs;
	}

	/**
	 * @param metadataTimeoutMs the metadataTimeoutMs to set
	 */
	public void setMetadataTimeoutMs(Integer metadataTimeoutMs)
	{
		this.metadataTimeoutMs = metadataTimeoutMs;
	}

	public Map<String, List<ImageFormat>> getAllowedImageFormats()
	{
		return allowedImageFormats;
	}

	public void setAllowedImageFormats(
			Map<String, List<ImageFormat>> allowedImageFormats)
	{
		this.allowedImageFormats = allowedImageFormats;
	}
}
