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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.TimedCache;
import gov.va.med.imaging.exchange.TimedCacheFactory;
import gov.va.med.imaging.exchange.business.dicom.PatientRef;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.CacheableEntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class PatientRefDAO extends CacheableEntityDAO<PatientRef>
{
	private String RPC_FIND_PAT_REF = "MAGV FIND PAT REF"; // was "MAGV FIND PAT";
	private String RPC_CREATE_PAT_REF = "MAGV CREATE PAT REF";

	private String PAR_ID_VALUE = "ENTERPRISE PATIENT ID";
	private String PAR_ID_TYPE = "ID TYPE";
	private String PAR_CREATING_ENTITY = "CREATING ENTITY";
	private String PAR_ASSIGNING_AUTHORITY = "ASSIGNING AUTHORITY";

	private TimedCache<PatientRefCacheItem> patientRefCache = TimedCacheFactory.<PatientRefCacheItem>getTimedCache("PatientRef");
	
	// Constructor
	public PatientRefDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	//
	// Creation template method overrides
	//
	@Override
	public VistaQuery generateCreateQuery(PatientRef patientRef) {
		VistaQuery vm = new VistaQuery(RPC_CREATE_PAT_REF);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", PAR_ID_VALUE + dbSeparator + patientRef.getEnterprisePatientId());
		hm.put("2", PAR_ID_TYPE + dbSeparator + patientRef.getIdType());					// (M)RN or (I)CN or (D)FN
		hm.put("3", PAR_CREATING_ENTITY + dbSeparator + patientRef.getCreatingEntity());	// site/station/division/location
		hm.put("4", PAR_ASSIGNING_AUTHORITY + dbSeparator + patientRef.getAssigningAuthority());	// (V)A, (D)oD, (I)HS
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}
	
	@Override
	public PatientRef translateCreate(PatientRef patientRef, String returnValue) throws CreationException
	{
		patientRef.setIEN(translateNewEntityIEN(returnValue, true));
		return patientRef;
	}


	//
	// Retrieval template method overrides
	//
	@Override
	public VistaQuery generateGetEntityByExampleQuery(PatientRef patientRef) {
		VistaQuery vm = new VistaQuery(RPC_FIND_PAT_REF);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", PAR_ID_VALUE + dbSeparator + patientRef.getEnterprisePatientId());		// VA Patient DFN by default
		hm.put("2", PAR_ID_TYPE + dbSeparator + patientRef.getIdType());					// (M)RN or (I)CN or (D)FN
		hm.put("3", PAR_CREATING_ENTITY + dbSeparator + patientRef.getCreatingEntity());	// site/station/division/location
		hm.put("4", PAR_ASSIGNING_AUTHORITY + dbSeparator + patientRef.getAssigningAuthority());	// (V)A, (D)oD, (I)HS
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public PatientRef translateGetEntityByExample(PatientRef patientRef,String returnValue) throws CreationException
	{
		if (returnValue.startsWith("0"))
		{
			patientRef.setIEN(translateNewEntityIEN(returnValue, false));
			return patientRef;
		}
		else
		{
			return null;
		}
	}

	//
	// Caching support
	//
	@Override
	protected void cacheEntity(PatientRef patientRef)
	{
		PatientRefCacheItem cacheItem = new PatientRefCacheItem(patientRef);
		patientRefCache.updateItem(cacheItem);
	}
	
	@Override
	protected void cacheEntityByExample(PatientRef patientRef)
	{
		PatientRefCacheItem cacheItem = new PatientRefCacheItem(patientRef);
		patientRefCache.updateItem(cacheItem);
	}
	
	@Override
	protected PatientRef getEntityFromCacheByExample(PatientRef patientRef)
	{
		PatientRef cachedPatientRef = null;
		Object key = PatientRefCacheItem.getCacheKey(patientRef);
		PatientRefCacheItem cacheItem = patientRefCache.getItem(key);
		if (cacheItem != null)
		{
			// Item was found in the cache. Return it...
			cachedPatientRef = cacheItem.getPatientRef();
		}
		return cachedPatientRef;
	}


}
