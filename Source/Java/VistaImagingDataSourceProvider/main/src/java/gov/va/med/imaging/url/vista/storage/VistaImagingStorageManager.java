/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 22, 2008
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
package gov.va.med.imaging.url.vista.storage;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.url.vista.image.ImagingSiteCredentials;
import gov.va.med.imaging.url.vista.image.ImagingStorageCredentials;
import gov.va.med.imaging.url.vista.image.NetworkLocation;
import gov.va.med.imaging.url.vista.image.NetworkLocationCacheManager;
import gov.va.med.imaging.url.vista.image.SiteParameterCredentials;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingQueryFactory;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

/**
 * Storage manager utility for holding onto the network location cache items and for managing 
 * the network locations (getting them from VistA)
 * 
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingStorageManager 
{
	private final static Logger logger = Logger.getLogger(VistaImagingStorageManager.class);
	
	private final static NetworkLocationCacheManager networkLocationCache = 
		new NetworkLocationCacheManager();
	
	private VistaImagingStorageManager()
	{
		super();
	}
	
	/**
	 * Passing in the Site parameter even though there is a site number in the image object.  This in the event the site
	 * number from the image is not the VistA site number, using the Vista site number in all other cases for caching, this
	 * makes it more consistent
	 * @param image
	 * @param site
	 * @return
	 */
	public synchronized static ImagingStorageCredentials getImagingStorageCredentialsFromCache(Image image, Site site)
	{
		if(networkLocationCache == null)
		{
			logger.warn("network location cache is null, shouldn't happen");
			return null;
		}
		return networkLocationCache.getNetworkLocations(image, site.getSiteNumber());
	}
	
	public synchronized static ImagingStorageCredentials getImagingStorageCredentialsFromCache(String filename, String siteNumber)
	{
		if(networkLocationCache == null)
		{
			logger.warn("network location cache is null, shouldn't happen");
			return null;
		}
		return networkLocationCache.getNetworkLocation(filename, siteNumber);
	}
	
	/**
	 * Returns the network location from VistA that is associated with the image share parameter.
	 * This function also caches the network locations it receives from VistA for future use
	 * 
	 * @param imageShare The image share to find in the database 
	 * @return The network location (with credentials) associated with the image share
	 * @throws IOException
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public static ImagingStorageCredentials getImagingStorageCredentialsFromVista(VistaSession vistaSession, 
			String imageShare, Site site)
	throws IOException, MethodException, ConnectionException
	{
		if(imageShare == null)
			return null;
		logger.info("Getting Site parameter credentials and network locations from site '" + site.getSiteNumber() + "'.");
		SiteParameterCredentials siteParameterCredentials = getSiteParameterCredentials(vistaSession);
		List<NetworkLocation> networkLocations = getNetworkLocations(vistaSession, site, siteParameterCredentials);
		
		ImagingSiteCredentials imagingSiteCredentials = 
			new ImagingSiteCredentials(site.getSiteNumber(), siteParameterCredentials);
		imagingSiteCredentials.getNetworkLocations().addAll(networkLocations);
		logger.info("Putting imaging site credentials for site '" + imagingSiteCredentials.getSiteNumber() + "' into cache.");
		logger.info("Imaging Site Credentials details: " + imagingSiteCredentials.toString());
		synchronized(networkLocationCache)
		{
			networkLocationCache.updateImagingSiteCredentials(imagingSiteCredentials);
		}
		// put into cache
		
		return imagingSiteCredentials.getStorageCredentials(imageShare);	
	}
	
	/**
	 * Retrieves the network locations from VistA (all network locations, not just MAG)
	 * @return A list of network locations from VistA
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws IOException
	 */
	private static List<NetworkLocation> getNetworkLocations(VistaSession vistaSession, 
			Site site, SiteParameterCredentials siteParameterCredentials)
	throws MethodException, ConnectionException, IOException
	{
		VistaQuery netLocQuery = VistaImagingQueryFactory.createGetNetworkLocationsVistaQuery();
		try 
		{
			String rtn = vistaSession.call(netLocQuery);
			if(rtn.length() <= 0)
				throw new MethodException("No network locations found for connection '" + vistaSession.getURL().toExternalForm() + "'");
			return VistaImagingTranslator.VistaNetworkLocationsToNetworkLocationsList(rtn, site, siteParameterCredentials);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Exception retrieving network locations", vmX);
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Exception retrieving network locations", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
	}
	
	/**
	 * Retrieves the credentials information from the Imaging Site Parameters file from VistA
	 * @return The general credentials for image share access from the Site Parameters file 
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws IOException
	 */
	private static SiteParameterCredentials getSiteParameterCredentials(VistaSession vistaSession)
	throws MethodException, ConnectionException, IOException
	{
		VistaQuery siteParameterQuery = VistaImagingQueryFactory.createGetImagingSiteParametersQuery(VistaCommonUtilities.getWorkstationId());
		try
		{
			String rtn = vistaSession.call(siteParameterQuery);
			return VistaImagingTranslator.VistaImagingSiteParametersStringToSiteCredentials(rtn);
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error retrieving Imaging Site Parameter credentials", vmX);
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error retrieving Imaging Site Parameter credentials", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
	}

}
