/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 17, 2010
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
package gov.va.med.imaging.url.vista.image;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class ImagingSiteCredentials
{
	private final String siteNumber;
	private final SiteParameterCredentials siteParameterCredentials;
	private final List<NetworkLocation> networkLocations = new ArrayList<NetworkLocation>();
	
	private final static Logger logger = Logger.getLogger(ImagingSiteCredentials.class);
	
	public ImagingSiteCredentials(String siteNumber, SiteParameterCredentials siteParameterCredentials)
	{
		this.siteNumber = siteNumber;
		this.siteParameterCredentials = siteParameterCredentials;
	}

	public String getSiteNumber()
	{
		return siteNumber;
	}

	public SiteParameterCredentials getSiteParameterCredentials()
	{
		return siteParameterCredentials;
	}

	public List<NetworkLocation> getNetworkLocations()
	{
		return networkLocations;
	}

	public ImagingStorageCredentials getStorageCredentials(String imagePath)
	{
		logger.info("Searching imaging site credentials from site '" + getSiteNumber() + "' for network location with path '" + imagePath + "'.");
		if(imagePath != null)
		{
			for(NetworkLocation networkLocation : networkLocations)
			{
				if(imagePath.equalsIgnoreCase(networkLocation.getPath()))
				{
					logger.info("Found network location for path '" + imagePath + "' from site '" + getSiteNumber() + "'.");
					return networkLocation;
				}
			}
		}
		logger.info("Did not find network location for path '" + imagePath + "' from site '" + getSiteNumber() + "', using default site credentials to access image share.");
		return new ImagingStorageCredentials(getSiteNumber(), 
				getSiteParameterCredentials().getUsername(), getSiteParameterCredentials().getPassword());
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("ImagingSiteCredentials for site '" + getSiteNumber() + "' contains the following network locations:\n");
		for(NetworkLocation networkLocation : networkLocations)
		{
			sb.append("\t" + networkLocation.getPath() + "\n");
		}	
		
		return sb.toString();
	}
}
