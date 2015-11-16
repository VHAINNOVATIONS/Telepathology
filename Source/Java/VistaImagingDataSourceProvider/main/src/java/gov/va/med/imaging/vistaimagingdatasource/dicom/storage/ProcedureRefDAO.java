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
import gov.va.med.imaging.exchange.business.dicom.ProcedureRef;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.CacheableEntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class ProcedureRefDAO extends CacheableEntityDAO<ProcedureRef>
{
	private String RPC_FIND_PROC_REF = "MAGV FIND PROC REF";
	private String RPC_ATTACH_PAT_PROC_REF = "MAGV ATTACH PAT PROC REF";

	private String DB_PARENT_IEN = "PATIENT REFERENCE"; // was "PARENT IEN"; // the immediate parent of a child entity
	private String PRR_PROCEDURE_ID = "ACCESSION NUMBER"; // was PROCEDURE ID (and should be) // was "CREATOR ID";
	private String PRR_ASSIGNING_AUTHORITY = "ASSIGNING AUTHORITY";
	private String PRR_CREATING_ENTITY = "CREATING ENTITY";
	private String PRR_PROCEDURE_TYPE = "PROCEDURE TYPE"; // was "CREATOR ID TYPE"; before 05/28/10
	private String PRR_EVENT_DATIME = "PROCEDURE DATE/TIME"; // was "EVENT DATE/TIME"; before 05/28/10
	private String PRR_PACKAGE_IX = "PACKAGE INDEX";
//	private String PRR_CLASS_IX = "CLASS INDEX";
//	private String PRR_PROC_EVENT_IX = "PROC/EVENT INDEX";
//	private String PRR_SPEC_SUBSPEC_IX = "SPEC/SUBSPEC INDEX";
	
	private TimedCache<ProcedureRefCacheItem> procedureRefCache = TimedCacheFactory.<ProcedureRefCacheItem>getTimedCache("ProcedureRef");
	
	// Constructor
	public ProcedureRefDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(ProcedureRef procedureRef) {
		VistaQuery vm = new VistaQuery(RPC_ATTACH_PAT_PROC_REF);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", DB_PARENT_IEN + dbSeparator + procedureRef.getPatientRefIEN());
		hm.put("2", PRR_PROCEDURE_ID + dbSeparator + procedureRef.getProcedureID());	// like Accession number
		hm.put("3", PRR_ASSIGNING_AUTHORITY + dbSeparator + procedureRef.getAssigningAuthority());	// (V)A, (D)oD, (I)HS
		hm.put("4", PRR_CREATING_ENTITY + dbSeparator + procedureRef.getCreatingEntity());	// department
		hm.put("5", PRR_PROCEDURE_TYPE + dbSeparator + procedureRef.getProcedureIDType());	// 'D" for DFN
		hm.put("6", PRR_EVENT_DATIME + dbSeparator + procedureRef.getProcedureExamDateTime()); // YYYYYMMDD.HHMISS
		hm.put("7", PRR_PACKAGE_IX + dbSeparator + procedureRef.getPackageIX());	// "RAD","LAB","MED","NOTE","CP","SUR","PHOTOID","NONE","CONS"
//		hm.put("8", PRR_CLASS_IX + dbSeparator + procedureRef.getClassIX());	// "CLIN","CLIN/ADMIN","ADMIN" or "ADMIN/CLIN"
//		hm.put("9", PRR_PROC_EVENT_IX + dbSeparator + procedureRef.getProcedureEventIX());
//		hm.put("10", PRR_SPEC_SUBSPEC_IX + dbSeparator + procedureRef.getSpecSubSpecIX());	// -ology/dept.
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public ProcedureRef translateCreate(ProcedureRef procedureRef, String returnValue) throws CreationException
	{
		procedureRef.setIEN(translateNewEntityIEN(returnValue, true));
		return procedureRef;
	}

	//
	// Retrieve by Example overrides
	//
	@Override
	public VistaQuery generateGetEntityByExampleQuery(ProcedureRef procedureRef) 
	{
		VistaQuery vm = new VistaQuery(RPC_FIND_PROC_REF);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", DB_PARENT_IEN + dbSeparator + procedureRef.getPatientRefIEN());
		hm.put("2", PRR_PROCEDURE_ID + dbSeparator + procedureRef.getProcedureID());	// like Accession number
		hm.put("3", PRR_ASSIGNING_AUTHORITY + dbSeparator + procedureRef.getAssigningAuthority());	// (V)A, (D)oD, (I)HS
		hm.put("4", PRR_CREATING_ENTITY + dbSeparator + procedureRef.getCreatingEntity());
		hm.put("5", PRR_PROCEDURE_TYPE + dbSeparator + procedureRef.getProcedureIDType());	// 'D" for DFN
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public ProcedureRef translateGetEntityByExample(ProcedureRef procedureRef, String returnValue) throws CreationException
	{
		if (returnValue.startsWith("0"))
		{
			procedureRef.setIEN(translateNewEntityIEN(returnValue, false));
			return procedureRef;
		}
		else
		{
			return null;
		}
	}

	@Override
	protected void cacheEntity(ProcedureRef procedureRef)
	{
		ProcedureRefCacheItem cacheItem = new ProcedureRefCacheItem(procedureRef);
		procedureRefCache.updateItem(cacheItem);
	}

	@Override
	protected void cacheEntityByExample(ProcedureRef procedureRef)
	{
		ProcedureRefCacheItem cacheItem = new ProcedureRefCacheItem(procedureRef);
		procedureRefCache.updateItem(cacheItem);
	}

	@Override
	protected ProcedureRef getEntityFromCacheByExample(ProcedureRef procedureRef)
	{
		ProcedureRef cachedValue = null;
		Object key = ProcedureRefCacheItem.getCacheKey(procedureRef);
		ProcedureRefCacheItem cacheItem = procedureRefCache.getItem(key);
		if (cacheItem != null)
		{
			// Item was found in the cache. Return it...
			cachedValue = cacheItem.getProcedureRef();
			logger.debug(this.getClass().getName()+": Procedure Found in local Cache.");
		}
		return cachedValue;
	}
}
