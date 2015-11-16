/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 20, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.imaging;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is a list of the protocols currently known and used by VISA applications.
 * This enumeration is a convenience only and is not a definitive list of protocols.
 * Repeat, this is NOT a complete enumeration and should NEVER be depended on to be a complete
 * enumeration.  New protocols may be added without being added to this enumeration.
 * In addition, the existence of a protocol within this list DOES NOT guarantee that the
 * corresponding protocol handler is installed.
 * 
 * @author vhaiswbeckec
 *
 */
public enum KnownProtocols
{
	EXCHANGE("exchange", 80, "Image exchange protocol used between the VIX and BIA", "VaImagingExchange", "/ImageMetadataService.asmx", "/RetrieveImage.ashx"),
	VFTP("vftp", 443, "VIX federation transport protocol, used between VIXEN", "VixFederation", "/webservices/ImageMetadataService", "/wai/images"),
	CDTP("cdtp", 80, "Clinical Display Transport Protocol, used between Clinical Display and a VIX", "ClinicalDisplayWebApp", "/webservices/ImageMetadataService", "/wai/images"),
	AWIV("awiv", 80, "AWIV Protocol, used between the AWIV viewer and a VIX", "AWIVWebApp", "/webservices/ImageMetadataService", "/wai/images"),
	VISTA("vista", 9300, "Used between the VIX and a VISTA installation", "", "", ""),
	VISTAIMAGING("vistaimaging", 9300, "Used between the VIX and a VISTA Imaging installation", "", "", ""),
	XCA("xca", 443, "Cross-Community Access, defined by IHE", "", "", "");

	private final String protocol;
	private final int defaultPort;
	private final String description;
	private final String defaultApplication;
	private final String defaultMetadataPath;
	private final String defaultArtifactPath;
	
	/**
	 * @param protocol
	 * @param defaultApplication
	 * @param defaultMetadataPath
	 * @param defaultArtifactPath
	 */
	private KnownProtocols(
		String protocol,
		int defaultPort, 
		String description,
		String defaultApplication, 
		String defaultMetadataPath,
		String defaultArtifactPath)
	{
		this.protocol = protocol;
		this.defaultPort = defaultPort;
		this.description = description;
		this.defaultApplication = defaultApplication;
		this.defaultMetadataPath = defaultMetadataPath;
		this.defaultArtifactPath = defaultArtifactPath;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol()
	{
		return this.protocol;
	}

	/**
	 * @return
	 */
	private int getDefaultPort()
	{
		return defaultPort;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @return the defaultApplication
	 */
	public String getDefaultApplication()
	{
		return this.defaultApplication;
	}

	/**
	 * @return the defaultMetadataPath
	 */
	public String getDefaultMetadataPath()
	{
		return this.defaultMetadataPath;
	}

	/**
	 * @return the defaultArtifactPath
	 */
	public String getDefaultArtifactPath()
	{
		return this.defaultArtifactPath;
	}
	
	public String getDefaultLocalMetadataURL()
	{
		return getProtocol() + "://localhost:" + getDefaultPort() + "/" + getDefaultApplication() + getDefaultMetadataPath();
	}
	
	public String getDefaultLocalArtifactURL()
	{
		return getProtocol() + "://localhost:" + getDefaultPort() + "/" + getDefaultApplication() + getDefaultArtifactPath();
	}
	
	/**
	 * Makes a "best guess" as to whether the protocol handlers are installed
	 * by building a URL from the default metadata path.  If the URL can be constructed 
	 * then the protocol handlers are installed, else false.
	 * This algorithm may fail if the URL is not in the "normal" format, but at this time all
	 * of the known protocols work.
	 * 
	 * @return
	 */
	public boolean isInstalled()
	{
		try
		{
			URL url = new URL( getProtocol(), "127.0.0.1", getDefaultApplication() + getDefaultMetadataPath() );
			return url != null;
		}
		catch (MalformedURLException x)
		{
			return false;
		}
	}
}
