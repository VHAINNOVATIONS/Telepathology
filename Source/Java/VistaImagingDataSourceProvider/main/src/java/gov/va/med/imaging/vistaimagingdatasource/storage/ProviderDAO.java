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
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ProviderDAO extends StorageDAO<Provider>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_PROV = "MAGVA CREATE PROVIDER";		// 7
	private final static String RPC_GET_ALL_PROVS = "MAGVA GET ALL PROVIDERS";	// 7
	private final static String RPC_UPDATE_PROV = "MAGVA UPDATE PROVIDER";		// 7
	
	//
	// Provider table (2006.917) fields
	//
	private final static String P_PK = "PK";
	private final static String P_PLACE_FK = "STORAGE PLACE"; // was "PROVIDER PLACE FK"; // foreign key (IEN/pointer) to Place file's record
	private final static String P_TYPE = "STORAGE PROVIDER TYPE"; // was "PROVIDER TYPE"
	private final static String P_IS_ACTIVE = "ACTIVE"; // IS removed
	private final static String P_IS_ARCHIVE = "ARCHIVE";  // IS removed
	private final static String P_IS_PRIMARY_STORAGE = "PRIMARY STORAGE";  // IS removed
	private final static String P_IS_WRITABLE = "WRITABLE"; // IS removed

	//
	// Constructor
	//
	public ProviderDAO(){}
	
	public ProviderDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Retrieve All methods
	//
	@Override
	public VistaQuery generateFindAllQuery() 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_ALL_PROVS);
		return vm;
	}

	@Override
	public List<Provider> translateFindAll(String returnValue) throws RetrievalException
	{
	
		List<Provider> list = new ArrayList<Provider>();

		String[] resultLines = DicomTranslatorUtility.createResultsArray(returnValue);
		checkRetrievalStatus(resultLines);
		
		if (resultLines.length > 2)
		{
			// We have at least one result row. Start at the third line, 
			// skipping status and header row...
			for (int i=2; i<resultLines.length; i++)
			{
				String[] fields = StringUtils.Split(resultLines[i], STORAGE_FIELD_SEPARATOR);
				Provider provider = new Provider(
						Integer.parseInt(fields[0]),
						Integer.parseInt(fields[1]),
						fields[2],
						getBooleanValue(fields[3]),
						getBooleanValue(fields[4]),
						getBooleanValue(fields[5]),
						getBooleanValue(fields[6]));
				
				list.add(provider);
			}
		}
		return list;
	}

	//
	// Create methods
	//
	@Override
	public VistaQuery generateCreateQuery(Provider provider) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_PROV);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(P_TYPE, provider.getProviderType());
		hm.put(P_PLACE_FK, Integer.toString(provider.getPlaceId()));
		hm.put(P_IS_ARCHIVE, provider.isArchive() ? "1":"0");
		hm.put(P_IS_PRIMARY_STORAGE, provider.isPrimaryStorage() ? "1":"0");
		vm.addParameter(VistaQuery.ARRAY, hm);

		return vm;
	}

	//
	// Update overrides
	//
	@Override
	public VistaQuery generateUpdateQuery(Provider provider) 
	{
		VistaQuery vm = new VistaQuery(RPC_UPDATE_PROV);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(P_PK, Integer.toString(provider.getId()));
		hm.put(P_PLACE_FK, Integer.toString(provider.getPlaceId()));
		hm.put(P_IS_ARCHIVE, provider.isArchive() ? "1":"0");
		hm.put(P_IS_PRIMARY_STORAGE, provider.isPrimaryStorage() ? "1":"0");
		hm.put(P_IS_ACTIVE, provider.isActive() ? "1":"0");
		hm.put(P_IS_WRITABLE, provider.isWritable() ? "1":"0");
		vm.addParameter(VistaQuery.ARRAY, hm);

		return vm;
	}

	// Get current write location
	public String getCurrentWriteLocation(Provider provider) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetCurrentWriteLocationQuery(provider);
		return translateGetCurrentWriteLocation(provider, executeRPC(vm));
	}

	public VistaQuery generateGetCurrentWriteLocationQuery(Provider provider) 
	{
		throw new NotImplementedException();
	}

	public String translateGetCurrentWriteLocation(Provider provider, String returnValue) 
	{
		throw new NotImplementedException();
	}
	
}