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

import gov.va.med.imaging.exchange.business.storage.RetentionPolicyProviderMapping;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RetentionPolicyProviderMapDAO extends StorageDAO<RetentionPolicyProviderMapping>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_RETPOL_PROV_MAP = "MAGVA CREATE RETPOL PROV MAP";
	private final static String RPC_GET_ALL_RETPOL_PROV_MAPS = "MAGVA GET ALL RETPOL PROV MAPS";
	private final static String RPC_UPDATE_RETPOL_PROV_MAP = "MAGVA UPDATE RETPOL PROV MAP";
	private final static String RPC_DELETE_RETPOL_PROV_MAP = "MAGVA DELETE RETPOL PROV MAP";

	
	// Retention Policy Provider Map table (2006.923) fields
	private final static String RPPM_PK = "PK";
	private final static String RPPM_RETPOL_FK = "RETENTION POLICY"; // FK removed // foreign key (IEN/pointer) to RetRol file's record
	private final static String RPPM_PROVIDER_FK = "PROVIDER"; // FK removed // foreign key (IEN/pointer) to Artifact Provider's record
	private final static String RPPM_SOURCE_PLACE_FK = "SOURCE PLACE FK"; // foreign key (IEN/pointer) to Place file's record
	private final static String RPPM_IS_SYNCHRONOUS = "SYNCHRONOUS"; // IS removed
	private final static String RPPM_IS_OFFSITE = "OFFSITE"; // IS removed

	//
	// Constructor
	//
	public RetentionPolicyProviderMapDAO(){}
	
	public RetentionPolicyProviderMapDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Create Mapping
	//
	public VistaQuery generateCreateQuery(RetentionPolicyProviderMapping mapping) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_RETPOL_PROV_MAP);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(RPPM_RETPOL_FK, Integer.toString(mapping.getRetentionPolicyId()));
		hm.put(RPPM_PROVIDER_FK, Integer.toString(mapping.getProviderId()));
		hm.put(RPPM_SOURCE_PLACE_FK, Integer.toString(mapping.getPlaceId()));
		hm.put(RPPM_IS_SYNCHRONOUS, mapping.isSynchronous() ? "1":"0");
		hm.put(RPPM_IS_OFFSITE, mapping.isOffsite() ? "1":"0");
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

	//
	// Retrieval methods
	//
	@Override
	public VistaQuery generateFindAllQuery() 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_ALL_RETPOL_PROV_MAPS);
		return vm;
	}

	@Override
	public List<RetentionPolicyProviderMapping> translateFindAll(String returnValue) throws RetrievalException
	{
		List<RetentionPolicyProviderMapping> list = new ArrayList<RetentionPolicyProviderMapping>();

		String[] resultLines = DicomTranslatorUtility.createResultsArray(returnValue);
		checkRetrievalStatus(resultLines);
		
		if (resultLines.length > 2)
		{
			// We have at least one result row. Start at the third line, 
			// skipping status and header row...
			for (int i=2; i<resultLines.length; i++)
			{
				String[] fields = StringUtils.Split(resultLines[i], STORAGE_FIELD_SEPARATOR);
				RetentionPolicyProviderMapping mapping = new RetentionPolicyProviderMapping(
						Integer.parseInt(fields[0]),
						Integer.parseInt(fields[1]),
						Integer.parseInt(fields[2]),
						Integer.parseInt(fields[3]),
						getBooleanValue(fields[4]),
						getBooleanValue(fields[5]));
				
				list.add(mapping);
				
			}
		}
		
		return list;
	}

	//
	// Update 
	//
	@Override
	public VistaQuery generateUpdateQuery(RetentionPolicyProviderMapping mapping) 
	{
		VistaQuery vm = new VistaQuery(RPC_UPDATE_RETPOL_PROV_MAP);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(RPPM_PK, Integer.toString(mapping.getId()));
		hm.put(RPPM_IS_SYNCHRONOUS, mapping.isSynchronous() ? "1":"0");
		hm.put(RPPM_IS_OFFSITE, mapping.isOffsite() ? "1":"0");
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

	//
	// Delete
	//
	@Override
	public VistaQuery generateDeleteQuery(int id) 
	{
		VistaQuery vm = new VistaQuery(RPC_DELETE_RETPOL_PROV_MAP);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(RPPM_PK, Integer.toString(id));
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}


	
}