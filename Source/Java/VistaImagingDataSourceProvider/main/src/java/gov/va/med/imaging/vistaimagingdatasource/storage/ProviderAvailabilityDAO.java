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

import gov.va.med.imaging.exchange.business.storage.ProviderAvailability;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProviderAvailabilityDAO extends StorageDAO<ProviderAvailability>
{
	//
	// RPC Names
	//
	private final static String RPC_CREATE_PPOVAVAILTY = "MAGVA CREATE PROVAVAILTY";
	private final static String RPC_GET_ALL_PROVAVAILS = "MAGVA GET ALL PROVAVAILS";
	private final static String RPC_UPDATE_PA = "MAGVA UPDATE PROVAVAILTY";
	private final static String RPC_DELETE_PA = "MAGVA DELETE PROVAVAILTY";

	//
	// Provider Availability table (2006.924) fields
	//
	private final static String PA_PK = "PK";
	private final static String PA_PROVIDER_FK = "STORAGE PROVIDER"; // FK removed // foreign key (IEN/pointer) to Provider file's record
	private final static String PA_SOURCE_PLACE_FK = "SOURCE PLACE"; // FK removed // foreign key (IEN/pointer) to RetentionPolicy file's record
	private final static String PA_START_TIME = "START TIME"; // YYYYMMDD.HHMISS
	private final static String PA_END_TIME = "END TIME"; // YYYYMMDD.HHMISS
//	private final static String PA_IS_ACTIVE = "IS ACTIVE"; // ** not in SDD

	//
	// Constructor
	//
	public ProviderAvailabilityDAO(){}
	
	public ProviderAvailabilityDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
		
	//
	// Retrieve All methods
	//
	@Override
	public VistaQuery generateFindAllQuery() 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_ALL_PROVAVAILS);
		return vm;
	}

	@Override
	public List<ProviderAvailability> translateFindAll(String returnValue) throws RetrievalException
	{
	
		List<ProviderAvailability> list = new ArrayList<ProviderAvailability>();

		String[] resultLines = DicomTranslatorUtility.createResultsArray(returnValue);
		checkRetrievalStatus(resultLines);
		
		if (resultLines.length > 2)
		{
			// We have at least one result row. Start at the third line, 
			// skipping status and header row...
			for (int i=2; i<resultLines.length; i++)
			{
				String[] fields = StringUtils.Split(resultLines[i], STORAGE_FIELD_SEPARATOR);
				ProviderAvailability providerAvailability = new ProviderAvailability(
						Integer.parseInt(fields[0]),
						Integer.parseInt(fields[1]),
						Integer.parseInt(fields[2]),
						getTimeComponent(fields[3]),
						getTimeComponent(fields[4]));
				list.add(providerAvailability);
			}
		}
		return list;

	}
	
	public String getTimeComponent(String dateTimeString){
		String[] dateTimeParts = StringUtils.Split(dateTimeString, ".");
		String result = "000000";
		if (dateTimeString.length() >= 2)
		{
			result = padRight(dateTimeParts[1], 6, '0');
			if (result.startsWith("24"))
				result = "000000";
		}
		return result;
	}
	
	public String padRight(String str, int size, char padChar)
	{
	  StringBuffer padded = new StringBuffer(str);
	  while (padded.length() < size)
	  {
	    padded.append(padChar);
	  }
	  return padded.toString();
	}

	//
	// Create methods
	//
	@Override
	public VistaQuery generateCreateQuery(ProviderAvailability providerAvailability) 
	{
		VistaQuery vm = new VistaQuery(RPC_CREATE_PPOVAVAILTY);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(PA_PROVIDER_FK, Integer.toString(providerAvailability.getProviderId()));
		hm.put(PA_SOURCE_PLACE_FK, Integer.toString(providerAvailability.getPlaceId()));
		hm.put(PA_START_TIME, providerAvailability.getStartTime());
		hm.put(PA_END_TIME, providerAvailability.getEndTime());
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}

	//
	// Update overrides
	//
	@Override
	public VistaQuery generateUpdateQuery(ProviderAvailability providerAvailability) 
	{
		VistaQuery vm = new VistaQuery(RPC_UPDATE_PA);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(PA_PK, Integer.toString(providerAvailability.getId()));
		hm.put(PA_START_TIME, providerAvailability.getStartTime());
		hm.put(PA_END_TIME, providerAvailability.getEndTime());
//		hm.put(PA_IS_ACTIVE, isActive?"1":"0");
		vm.addParameter(VistaQuery.ARRAY, hm);
		
		return vm;
	}
	
	//
	// Delete methods
	//
	@Override
	public VistaQuery generateDeleteQuery(int providerAvailabilityPK) 
	{
		VistaQuery vm = new VistaQuery(RPC_DELETE_PA);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put(PA_PK, Integer.toString(providerAvailabilityPK));
		vm.addParameter(VistaQuery.ARRAY, hm);		
		
		return vm;
	}	
}