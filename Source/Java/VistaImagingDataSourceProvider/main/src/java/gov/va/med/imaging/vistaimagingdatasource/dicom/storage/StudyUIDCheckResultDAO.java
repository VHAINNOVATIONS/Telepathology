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

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TimedCache;
import gov.va.med.imaging.exchange.business.dicom.UIDCheckInfo;
import gov.va.med.imaging.exchange.business.dicom.UIDCheckResult;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.CacheableEntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

public class StudyUIDCheckResultDAO extends CacheableEntityDAO<UIDCheckResult>
{
	private String RPC_MAGV_STUDY_UID_CHECK = "MAGV STUDY UID CHECK";
	private TimedCache<StudyUIDCheckResultCacheItem> cache = new TimedCache<StudyUIDCheckResultCacheItem>("StudyUIDCheckResult");
	
	public StudyUIDCheckResultDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	@Override
	protected UIDCheckResult getEntityFromCacheByCriteria(Object criteria)
	{
		UIDCheckInfo uidCheckInfo = (UIDCheckInfo)criteria;
		UIDCheckResult cachedValue = null;
		Object key = getCacheKey(uidCheckInfo);
		StudyUIDCheckResultCacheItem cacheItem = cache.getItem(key);
		if (cacheItem != null)
		{
			// Item was found in the cache. Return it...
			cachedValue = cacheItem.getUIDCheckResult();
		}
		return cachedValue;
	}


	@Override
	public VistaQuery generateGetEntityByCriteriaQuery(Object criteria)
	{
		UIDCheckInfo uidCheckInfo = (UIDCheckInfo)criteria;
		VistaQuery vm = new VistaQuery(RPC_MAGV_STUDY_UID_CHECK);
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getPatientDFN());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getStudyAccessionNumber());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getSiteID());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getInstrumentID());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getStudyInstanceUID());
		
		return vm;
	}
	
	@Override
	public UIDCheckResult translateGetEntityByCriteria(Object criteria, String returnValue) {
		return VistaImagingDicomStorageUtility.translateUIDCheckResults(returnValue, ((UIDCheckInfo)criteria).getStudyInstanceUID(), FIELD_SEPARATOR1);
	}

	@Override
	protected void cacheEntityByCriteria(Object criteria, UIDCheckResult uidCheckResult)
	{
		UIDCheckInfo uidCheckInfo = (UIDCheckInfo)criteria;
		// Only cache the result if we had a successful retrieve
		if (!uidCheckResult.isFatalError())
		{
			StudyUIDCheckResultCacheItem cacheItem = new StudyUIDCheckResultCacheItem(uidCheckInfo, uidCheckResult);
			cache.updateItem(cacheItem);
		}
	}

	private Object getCacheKey(UIDCheckInfo uidCheckInfo)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(uidCheckInfo.getStudyInstanceUID() + "_");
		buffer.append(uidCheckInfo.getPatientDFN() + "_");
		buffer.append(uidCheckInfo.getStudyAccessionNumber() + "_");
		buffer.append(uidCheckInfo.getSiteID() + "_");
		buffer.append(uidCheckInfo.getInstrumentID());
		return buffer.toString();
	}

	class StudyUIDCheckResultCacheItem extends BaseTimedCacheValueItem
	{

		UIDCheckInfo uidCheckInfo;
		UIDCheckResult result;
		public StudyUIDCheckResultCacheItem(UIDCheckInfo uidCheckInfo, UIDCheckResult result)
		{
			this.uidCheckInfo = uidCheckInfo;
			this.result = result;
		}

		@Override
		public Object getKey()
		{
			return getCacheKey(uidCheckInfo);
		}

		public UIDCheckResult getUIDCheckResult()
		{
			return result;
		}
	}


}
