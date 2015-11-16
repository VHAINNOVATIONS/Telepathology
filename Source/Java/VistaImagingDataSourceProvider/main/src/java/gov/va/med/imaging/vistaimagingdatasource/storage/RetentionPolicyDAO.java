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

import gov.va.med.imaging.exchange.business.storage.RetentionPolicy;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RetentionPolicyDAO extends StorageDAO<RetentionPolicy>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_RETPOL = "MAGVA CREATE RETPOL";			// 1
	private final static String RPC_GET_ALL_RETPOLS = "MAGVA GET ALL RETPOLS";		// 1
	
	//
	// 	Retention Policy (1) table (2006.914) fields
	//
	private final static String RP_ACTIVE_DURA_YEARS = "ARCHIVE DURATION YEARS";
	private final static String RP_ARCHIVE_DURA_TRIGGER = "ARCHIVE DURATION TRIGGER";
	private final static String RP_MIN_ARCHIVE_COPIES = "MINIMUM ARCHIVE COPIES";
	private final static String RP_MIN_OFFSITE_COPIES = "MINIMUM OFFSITE COPIES";
	private final static String RP_DISPLAY_NAME = "DISPLAY NAME";
	private final static String RP_BUSINESS_KEY = "BUSINESS KEY";
	private final static String RP_IS_ACTIVE = "ACTIVE"; // was "IS ACTIVE";

	//
	// Constructor
	//
	public RetentionPolicyDAO(){}
	
	public RetentionPolicyDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Create methods
	//
	@Override
	public VistaQuery generateCreateQuery(RetentionPolicy retentionPolicy) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_RETPOL);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(RP_ACTIVE_DURA_YEARS, retentionPolicy.getArchiveDurationYears());
		hm.put(RP_ARCHIVE_DURA_TRIGGER, retentionPolicy.getArchiveDurationTrigger());	// LAD (last access), CD (creation), DD (death)
		hm.put(RP_MIN_ARCHIVE_COPIES, Integer.toString(retentionPolicy.getMinimumArchiveCopies()));
		hm.put(RP_MIN_OFFSITE_COPIES, Integer.toString(retentionPolicy.getMinimumOffsiteCopies()));
		hm.put(RP_DISPLAY_NAME, retentionPolicy.getDisplayName());
		hm.put(RP_BUSINESS_KEY, retentionPolicy.getBusinessKey());
		hm.put(RP_IS_ACTIVE, retentionPolicy.isActive()?"1":"0");
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

	//
	// Retrieval methods
	//
	@Override
	public VistaQuery generateFindAllQuery() 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_ALL_RETPOLS);
		return vm;
	}

	@Override
	public List<RetentionPolicy> translateFindAll(String returnValue) throws RetrievalException
	{
		List<RetentionPolicy> list = new ArrayList<RetentionPolicy>();

		String[] resultLines = DicomTranslatorUtility.createResultsArray(returnValue);
		checkRetrievalStatus(resultLines);
		
		
		if (resultLines.length > 2)
		{
			// We have at least one result row. Start at the third line, 
			// skipping status and header row...
			for (int i=2; i<resultLines.length; i++)
			{
				String[] fields = StringUtils.Split(resultLines[i], STORAGE_FIELD_SEPARATOR);
				RetentionPolicy policy = new RetentionPolicy(
						Integer.parseInt(fields[0]),
						fields[1],
						fields[2],
						fields[3],
						Integer.parseInt(fields[4]),
						Integer.parseInt(fields[5]),
						fields[6],
						getBooleanValue(fields[7]));
				
				list.add(policy);
				
			}
		}
		
		return list;
	}

	//
	// Update overrides
	//
	@Override
	public VistaQuery generateUpdateQuery(RetentionPolicy retentionPolicy) 
	{
		throw new NotImplementedException();
	}
	
}