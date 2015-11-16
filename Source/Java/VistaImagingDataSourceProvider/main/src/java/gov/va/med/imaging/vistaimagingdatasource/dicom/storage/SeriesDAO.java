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

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ParentREFDeletedMethodException;
import gov.va.med.imaging.exchange.TimedCache;
import gov.va.med.imaging.exchange.TimedCacheFactory;
import gov.va.med.imaging.exchange.business.dicom.Series;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.CacheableEntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class SeriesDAO extends CacheableEntityDAO<Series>
{
	private String RPC_ATTACH_SERIES = "MAGV ATTACH SERIES";
	private String RPC_UPDATE_SERIES = "MAGV UPDATE SERIES";
	private String RPC_FIND_SERIES_BY_UID= "MAGV FIND SERIES BY UID";
	
	private String DB_PARENT_IEN = "STUDY REFERENCE"; // was "PARENT IEN"; // the immediate parent of the child entity
	private String SER_IEN = "SERIEN"; // **
	private String DB_PARENT_CHECK_OVERRIDE_IN_LIST = "OVERRIDE FLAG"; // the immediate parent of a child entity

	private String SER_INSTANCE_UID = "SERIES INSTANCE UID";
	private String SER_ORIG_INST_UID = "ORIGINAL SERIES UID";
	private String SER_NUMBER =  "SERIES NUMBER";
	private String SER_DESCRIPTION =  "DESCRIPTION";
	private String SER_MODALITY =  "MODALITY";
	private String SER_BODY_PART =  "BODY PART";
	private String SER_ACQ_SITE =  "ACQUISITION LOCATION";
	private String SER_DATIME =  "SERIES DATE/TIME";
	private String SER_CREATOR =  "DEVICE MANUFACTURER";
	private String SER_CREATOR_DEVICE_MODEL =  "DEVICE MODEL";
	private String SER_FRAME_OF_REF_UID =  "FRAME OF REFERENCE UID";
	private String SER_LATERALITY =  "LATERALITY";
	private String SER_SPATIAL_POS =  "SPATIAL POSITION";
	private String SER_SRC_AE_TITLE =  "CALLING AE TITLE"; // was "SOURCE AE TITLE"
	private String SER_RETR_AE_TITLE =  "RETRIEVE AE TITLE";
	private String SER_ACQ_ENTRY_POINT =  "VI ACQ ENTRY POINT";
	private String SER_IOD_VIOLATION_DETECTED =  "IOD VIOLATION DETECTED";
	private String SER_TIU_NOTE_REFERENCE =  "TIU NOTE REFERENCE";
	private String SER_CLASS_IX = "CLASS INDEX";
	private String SER_PROC_EVENT_IX = "PROC/EVENT INDEX";
	private String SER_SPEC_SUBSPEC_IX = "SPEC/SUBSPEC INDEX";

	private TimedCache<SeriesCacheItem> seriesCache = TimedCacheFactory.<SeriesCacheItem>getTimedCache("Series");

	// Constructor
	public SeriesDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	// 
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(Series series) {
		VistaQuery vm = new VistaQuery(RPC_ATTACH_SERIES);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", DB_PARENT_IEN + dbSeparator + series.getStudyIEN());
		hm.put("2", SER_INSTANCE_UID + dbSeparator + series.getSeriesIUID());
		hm.put("3", SER_ORIG_INST_UID + dbSeparator + series.getOriginalSeriesIUID());
		hm.put("4", SER_NUMBER + dbSeparator + series.getSeriesNumber());
		hm.put("5", SER_DESCRIPTION + dbSeparator + series.getDescription());
		hm.put("6", SER_MODALITY + dbSeparator + series.getModality());
		hm.put("7", SER_BODY_PART + dbSeparator + series.getBodyPart());
		hm.put("8", SER_ACQ_SITE + dbSeparator + series.getAcqSite());
		hm.put("9", SER_DATIME + dbSeparator + series.getSeriesDateTime());
		hm.put("10", SER_CREATOR + dbSeparator + series.getSeriesCreator());
		hm.put("11", SER_CREATOR_DEVICE_MODEL + dbSeparator + series.getSeriesCreatorDeviceModel());
		hm.put("12", SER_FRAME_OF_REF_UID + dbSeparator + series.getFrameOfReferenceUID());
		hm.put("13", SER_LATERALITY + dbSeparator + series.getLaterality());
		hm.put("14", SER_SPATIAL_POS + dbSeparator + series.getSpatialPosition());
		hm.put("15", SER_SRC_AE_TITLE + dbSeparator + series.getSourceAETitle());
		hm.put("16", SER_RETR_AE_TITLE + dbSeparator + series.getRetrieveAETitle());
		hm.put("17", SER_ACQ_ENTRY_POINT + dbSeparator + series.getVIAcqEntryPoint());
		hm.put("18", SER_IOD_VIOLATION_DETECTED + dbSeparator + series.getIODViolationDetected());
		hm.put("19", SER_CLASS_IX + dbSeparator + series.getClassIX());
		hm.put("20", SER_PROC_EVENT_IX + dbSeparator + series.getProcedureEventIX());
		hm.put("21", SER_SPEC_SUBSPEC_IX + dbSeparator + series.getSpecSubSpecIX());
		if ((series.getTiuNoteReference()!=null) && !series.getTiuNoteReference().equals("0"))
			hm.put("22", SER_TIU_NOTE_REFERENCE + dbSeparator + series.getTiuNoteReference());
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public Series translateCreate(Series series, String returnValue) 
						throws CreationException,ParentREFDeletedMethodException
	{
		//Check if the Study REF was deleted in the new data structure. If deleted, throw exception.
		if(returnValue == null){
			throw new CreationException("RPC returned value for "+RPC_ATTACH_SERIES+" is null.");
		}
		String[] results = StringUtils.Split(returnValue, DB_OUTPUT_SEPARATOR1);
		if (results[0].equals("-100")){ // Parent IEN Deleted
			throw new ParentREFDeletedMethodException("Parent Study REF IEN was deleted: " + results[1]);
		}
		series.setIEN(translateNewEntityIEN(returnValue, true));
		return series;
	}

	//
	// Update overrides
	//
	@Override
	public VistaQuery generateUpdateQuery(Series series) throws MethodException
	{
		VistaQuery vm = new VistaQuery(RPC_UPDATE_SERIES);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", SER_IEN + dbSeparator + series.getIEN());
		hm.put("2", DB_PARENT_IEN + dbSeparator + series.getStudyIEN());
		hm.put("3", DB_PARENT_CHECK_OVERRIDE_IN_LIST + dbSeparator + "0"); // force parent check!
		hm.put("4", SER_INSTANCE_UID + dbSeparator + series.getSeriesIUID()); // this needs to be checked that stays unique
		hm.put("5", SER_ORIG_INST_UID + dbSeparator + series.getOriginalSeriesIUID());
		hm.put("6", SER_NUMBER + dbSeparator + series.getSeriesNumber());
		hm.put("7", SER_DESCRIPTION + dbSeparator + series.getDescription());
		hm.put("8", SER_MODALITY + dbSeparator + series.getModality());
		hm.put("9", SER_BODY_PART + dbSeparator + series.getBodyPart());
		hm.put("10", SER_ACQ_SITE + dbSeparator + series.getAcqSite());
		hm.put("11", SER_DATIME + dbSeparator + series.getSeriesDateTime());
		hm.put("12", SER_CREATOR + dbSeparator + series.getSeriesCreator());
		hm.put("13", SER_CREATOR_DEVICE_MODEL + dbSeparator + series.getSeriesCreatorDeviceModel());
		hm.put("14", SER_FRAME_OF_REF_UID + dbSeparator + series.getFrameOfReferenceUID());
		hm.put("15", SER_LATERALITY + dbSeparator + series.getLaterality());
		hm.put("16", SER_SPATIAL_POS + dbSeparator + series.getSpatialPosition());
//		hm.put("17", SER_LAB_SLICE_ID + dbSeparator + series.getLabSliceID());
//		hm.put("18", SER_HISTOL_STAIN + dbSeparator + series.getHistologicalStain());
		hm.put("17", SER_SRC_AE_TITLE + dbSeparator + series.getSourceAETitle());
		hm.put("18", SER_RETR_AE_TITLE + dbSeparator + series.getRetrieveAETitle());
		hm.put("19", SER_ACQ_ENTRY_POINT + dbSeparator + series.getVIAcqEntryPoint());
		hm.put("20", SER_IOD_VIOLATION_DETECTED + dbSeparator + series.getIODViolationDetected());
		hm.put("21", SER_CLASS_IX + dbSeparator + series.getClassIX());
		hm.put("22", SER_PROC_EVENT_IX + dbSeparator + series.getProcedureEventIX());
		hm.put("23", SER_SPEC_SUBSPEC_IX + dbSeparator + series.getSpecSubSpecIX());
		if ((series.getTiuNoteReference()!=null) && !series.getTiuNoteReference().equals("0"))
			hm.put("24", SER_TIU_NOTE_REFERENCE + dbSeparator + series.getTiuNoteReference());
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public Series translateUpdate(Series series, String returnValue) throws MethodException, UpdateException, ParentREFDeletedMethodException
	{
		//Check if the Study REF was deleted in the new data structure. If deleted, throw exception.
		if(returnValue == null){
			throw new CreationException("RPC returned value for "+RPC_UPDATE_SERIES+" is null.");
		}
		String[] results = StringUtils.Split(returnValue, DB_OUTPUT_SEPARATOR1);
		if (results[0].equals("-100")){ // Parent IEN Deleted
			throw new ParentREFDeletedMethodException("Parent Study REF IEN was deleted: " + results[1]);
		}
		series.setIEN(translateNewEntityIEN(returnValue, true));
		return series;
	}

	//
	// Retrieval overrides
	//
	@Override
	public VistaQuery generateGetEntityByExampleQuery(Series series) 
	{
		VistaQuery vm = new VistaQuery(RPC_FIND_SERIES_BY_UID);
		vm.addParameter(VistaQuery.LITERAL, series.getSeriesIUID());
		return vm;
	}

	@Override
	public Series translateGetEntityByExample(Series series, String returnValue) throws CreationException
	{
		if (returnValue.startsWith("0"))
		{
			series.setIEN(translateNewEntityIEN(returnValue, false));
			return series;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Purge Series Cache.  This also creates a new Series Cache.
	 */
	public void purgeSeriesCache(){
		this.seriesCache = TimedCacheFactory.<SeriesCacheItem>purgeTimedCache("Series");
		logger.debug(this.getClass().getName()+": Purged Series Cache.");
	}
	
	@Override
	protected void cacheEntity(Series series)
	{
		SeriesCacheItem cacheItem = new SeriesCacheItem(series);
		seriesCache.updateItem(cacheItem);
	}
	
	@Override
	protected void cacheEntityByExample(Series series)
	{
		SeriesCacheItem cacheItem = new SeriesCacheItem(series);
		seriesCache.updateItem(cacheItem);
	}

	@Override
	protected Series getEntityFromCacheByExample(Series series)
	{
		Series cachedSeries = null;
		Object key = SeriesCacheItem.getCacheKey(series);
		SeriesCacheItem cacheItem = seriesCache.getItem(key);
		if (cacheItem != null)
		{
			// Item was found in the cache. Return it...
			cachedSeries = cacheItem.getSeries();
		}
		return cachedSeries;
	}
}
