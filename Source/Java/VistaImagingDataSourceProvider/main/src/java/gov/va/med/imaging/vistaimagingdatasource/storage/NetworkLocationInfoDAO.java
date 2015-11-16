/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;
import gov.va.med.imaging.exchange.business.storage.Place;
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.EncryptionUtils;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class NetworkLocationInfoDAO extends StorageDAO<NetworkLocationInfo>
{
	//
	// RPC Names
	//
	private final static String RPC_GET_CURRENT_WRITE_LOCATION = "MAGVA GET CWL"; // 
	private final static String RPC_GET_NET_LOC_DETAILS = "MAGVA GET NET LOC DETAILS";
	private final static String RPC_GET_JUKEBOX_WRITE_LOCATION = "MAGVA GET JUKEBOX WL";
	
	//
	// Field Names
	//
	private final static String PL_STATION_NUMBER = "STATION NUMBER";
	private final static String NL_IEN = "NETWORK LOCATION IEN";

	//
	// Constructor
	//
	public NetworkLocationInfoDAO(){}
	public NetworkLocationInfoDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	// Get current write location
	public NetworkLocationInfo getCurrentWriteLocation(Provider provider)
	throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetCurrentWriteLocationQuery(provider);
		return translateGetWriteLocation(executeRPC(vm));
	}

	public VistaQuery generateGetCurrentWriteLocationQuery(Provider provider) 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_CURRENT_WRITE_LOCATION);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(PL_STATION_NUMBER, provider.getPlace().getSiteNumber());
		vm.addParameter(VistaQuery.ARRAY, hm);

		return vm;
	}

	// Get current write location
	public NetworkLocationInfo getCurrentJukeboxWriteLocation(Provider provider)
	throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetCurrentJukeboxWriteLocationQuery(provider);
		return translateGetWriteLocation(executeRPC(vm));
	}

	public VistaQuery generateGetCurrentJukeboxWriteLocationQuery(Provider provider) 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_JUKEBOX_WRITE_LOCATION);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(PL_STATION_NUMBER, provider.getPlace().getSiteNumber());
		vm.addParameter(VistaQuery.ARRAY, hm);

		return vm;
	}


	public NetworkLocationInfo translateGetWriteLocation(String returnValue)
	throws RetrievalException
	{
		String[] lines = StringUtils.Split(returnValue, STORAGE_LINE_SEPARATOR);
		if (lines[0].startsWith("0"))
		{
			String[] locationFields = StringUtils.Split(lines[2], STORAGE_FIELD_SEPARATOR);
			
			// Resolve the place for this network location
			int placeId = Integer.parseInt(locationFields[4]);
			StorageServerDatabaseConfiguration config = StorageServerDatabaseConfiguration.getConfiguration();
			Place place = config.getPlace(placeId);

			// Read the username and password from the location record
			String username = locationFields[2];
			String password = locationFields[3];
			
			if (username == null || username.equals(""))
			{
				// This network location instance did not provide a username and password.
				// Use the default values for the site
				username = place.getUsername();
				password = place.getPassword();

			}
			else
			{
				// The username was specified in the location record. If the password is
				// not blank, we need to decrypt it.
				if (password != null && !password.equals("")){
					password = EncryptionUtils.decrypt(password);
				}
			}
			return new NetworkLocationInfo(locationFields[0],
					place,
					locationFields[1],
					username,
					password);
		}
		else
		{
			String[] resultFields = StringUtils.Split(lines[0], STORAGE_FIELD_SEPARATOR);
			throw new RetrievalException(resultFields[1]);
		}
	}
	
	// Get network location details
	public NetworkLocationInfo getNetworkLocationDetails(String networkLocationIEN)
	throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetNetworkLocationDetailsQuery(networkLocationIEN);
		return translateGetNetworkLocationDetails(networkLocationIEN, executeRPC(vm));
	}

	public VistaQuery generateGetNetworkLocationDetailsQuery(String networkLocationIEN) 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_NET_LOC_DETAILS);
		vm.addParameter(VistaQuery.LITERAL, networkLocationIEN);
		return vm;
	}

	public NetworkLocationInfo translateGetNetworkLocationDetails(String networkLocationIEN, String returnValue)
	throws RetrievalException
	{
		String[] lines = StringUtils.Split(returnValue, STORAGE_LINE_SEPARATOR);
		if (lines[0].startsWith("0"))
		{
			String[] locationFields = StringUtils.Split(lines[2], STORAGE_FIELD_SEPARATOR);
			
			
			// Resolve the place for this network location
			int placeId = Integer.parseInt(locationFields[4]);
			StorageServerDatabaseConfiguration config = StorageServerDatabaseConfiguration.getConfiguration();
			Place place = config.getPlace(placeId);

			// Read the username and password from the location record
			String username = locationFields[1];
			String password = locationFields[2];
			
			if (username == null || username.equals(""))
			{
				// This network location instance did not provide a username and password.
				// Use the default values for the site
				username = place.getUsername();
				password = place.getPassword();

			}
			else
			{
				// The username was specified in the location record. If the password is
				// not blank, we need to decrypt it.
				if (password != null && !password.equals("")){
					password = EncryptionUtils.decrypt(password);
				}
			}

			
			NetworkLocationInfo netLoc = new NetworkLocationInfo(networkLocationIEN,
					place,
					locationFields[0],
					username,
					password);
			
			return netLoc;
		}
		else
		{
			String[] resultFields = StringUtils.Split(lines[0], STORAGE_FIELD_SEPARATOR);
			throw new RetrievalException(resultFields[1]);
		}
	}
}
