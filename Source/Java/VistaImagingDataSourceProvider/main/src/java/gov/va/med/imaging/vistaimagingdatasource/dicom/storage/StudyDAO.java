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
import gov.va.med.imaging.exchange.business.dicom.Study;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.CacheableEntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class StudyDAO extends CacheableEntityDAO<Study>
{
	private String RPC_ATTACH_STUDY = "MAGV ATTACH STUDY";	
	private String RPC_FIND_STUDY_BY_UID = "MAGV FIND STUDY BY UID";

	private String PAR_IEN = "PATIENT REFERENCE"; // was "PATIENT REF IEN"; before 05/28/10
	private String DB_PARENT_IEN = "PROCEDURE REFERENCE"; // "PARENT IEN"; // the immediate parent of the child entity

	private String STD_INSTANCE_UID = "STUDY INSTANCE UID";
	private String STD_ORIG_INST_UID = "ORIGINAL STUDY INSTANCE UID";
	private String STD_ID = "STUDY ID";
	private String STD_DESCRIPTION = "DESCRIPTION";
	private String STD_MODALITIES = "MODALITIES IN STUDY";
	private String STD_DATIME = "STUDY DATE/TIME";
	private String STD_REASON = "REASON FOR STUDY";
	private final static String STD_ACC_NUM = "ACCESSION NUMBER";
	private String STD_ACQ_COMPLETED = "ACQUISITION STATUS"; // was "IS ACQUISITION COMPLETE";
	private String STD_ORIGIN_IX = "ORIGIN INDEX";
	private String STD_PRIORITY = "PRIORITY";
	
	private TimedCache<StudyCacheItem> studyCache = TimedCacheFactory.<StudyCacheItem>getTimedCache("Study");

	// 
	// Constructor
	//
	public StudyDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	// 
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(Study study) 
	{
		VistaQuery vm = new VistaQuery(RPC_ATTACH_STUDY);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", PAR_IEN + dbSeparator + study.getPatientRefIEN());
		hm.put("2", DB_PARENT_IEN + dbSeparator + study.getProcedureRefIEN());
		hm.put("3", STD_INSTANCE_UID + dbSeparator + study.getStudyIUID());
		hm.put("4", STD_ORIG_INST_UID + dbSeparator + study.getOriginalStudyIUID());
		hm.put("5", STD_ID + dbSeparator + study.getStudyID());
		hm.put("6", STD_DESCRIPTION + dbSeparator + study.getDescription());
		hm.put("7", STD_MODALITIES + dbSeparator + study.getModalitiesInStudy());
		hm.put("8", STD_DATIME + dbSeparator + study.getStudyDateTime());
		hm.put("9", STD_REASON + dbSeparator + study.getReasonForStudy());
		hm.put("10", STD_ACQ_COMPLETED + dbSeparator + study.getAcqComplete());
		hm.put("11", STD_ORIGIN_IX + dbSeparator + study.getOriginIX());
		hm.put("12", STD_PRIORITY + dbSeparator + study.getPriority());
		hm.put("13", STD_ACC_NUM + dbSeparator + study.getAccessionNumber());
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public Study translateCreate(Study study, String returnValue) throws CreationException
	{
		study.setIEN(translateNewEntityIEN(returnValue, true));
		return study;
	}

	// 
	// Retrieval overrides
	//
	@Override
	public VistaQuery generateGetEntityByExampleQuery(Study study) 
	{
		VistaQuery vm = new VistaQuery(RPC_FIND_STUDY_BY_UID);
//		HashMap <String, String> hm = new HashMap <String, String>();
//		hm.put("1", PAR_IEN + separator + study.getPatientRefIEN());
//		hm.put("2", PRR_IEN + separator + study.getProcedureRefIEN());
//		hm.put("3", STD_INSTANCE_UID + separator + study.getStudyIUID());
//		vm.addParameter(VistaQuery.LIST, hm);
		vm.addParameter(VistaQuery.LITERAL, study.getStudyIUID());
		return vm;
	}

	@Override
	public Study translateGetEntityByExample(Study study, String returnValue) throws CreationException
	{
		if (returnValue.startsWith("0"))
		{
			study.setIEN(translateNewEntityIEN(returnValue, false));
			return study;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Purge Study Cache.  This also creates a new Study Cache.
	 */
	public void purgeStudyCache(){
		this.studyCache = TimedCacheFactory.<StudyCacheItem>purgeTimedCache("Study");
		logger.debug(this.getClass().getName()+": Purged Study Cache.");
	}

	// 
	// Caching support
	//
	@Override
	protected void cacheEntity(Study study)
	{
		StudyCacheItem cacheItem = new StudyCacheItem(study);
		studyCache.updateItem(cacheItem);
	}
	
	@Override
	protected void cacheEntityByExample(Study study)
	{
		StudyCacheItem cacheItem = new StudyCacheItem(study);
		studyCache.updateItem(cacheItem);
	}
	
	@Override
	protected Study getEntityFromCacheByExample(Study study)
	{
		Study cachedStudy = null;
		Object key = StudyCacheItem.getCacheKey(study);
		StudyCacheItem cacheItem = studyCache.getItem(key);
		if (cacheItem != null)
		{
			// Item was found in the cache. Return it...
			cachedStudy = cacheItem.getStudy();
		}
		return cachedStudy;
	}

}
