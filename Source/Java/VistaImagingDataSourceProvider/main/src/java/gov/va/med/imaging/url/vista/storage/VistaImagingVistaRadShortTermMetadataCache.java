/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 19, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

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
package gov.va.med.imaging.url.vista.storage;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImagingVistaRadShortTermMetadataCache
{
	private final static long VISTA_EXAM_IMAGE_LIST_CACHE_TIMER_REFRESH = 1000 * 60 * 4; // check for expired items in cache every 4 minutes
	private final static long VISTA_EXAM_IMAGE_LIST_CACHE_RETENTION_PERIOD = 1000 * 60 * 5; // items last in cache for 5 minutes
	private final static Logger logger = Logger.getLogger(VistaImagingVistaRadShortTermMetadataCache.class);
	private static BaseTimedCache<StudyURN, ExamImagesBaseTimedCacheValueItem> imageListCache = null;
	
	static
	{
		imageListCache = 
			new BaseTimedCache<StudyURN, ExamImagesBaseTimedCacheValueItem>(VistaImagingVistaRadShortTermMetadataCache.class.toString());
		imageListCache.setRetentionPeriod(VISTA_EXAM_IMAGE_LIST_CACHE_RETENTION_PERIOD);
		TaskScheduler.getTaskScheduler().schedule(imageListCache, 
				VISTA_EXAM_IMAGE_LIST_CACHE_TIMER_REFRESH, VISTA_EXAM_IMAGE_LIST_CACHE_TIMER_REFRESH);
	}
	
	public VistaImagingVistaRadShortTermMetadataCache()
	{
		super();
	}
	
	public void cacheImages(StudyURN studyUrn, ExamImages examImages)
	{
		logger.debug("Caching exam images associated with exam '" + studyUrn.toString() + "' in short term metadata cache.");
		ExamImagesBaseTimedCacheValueItem item = 
			new ExamImagesBaseTimedCacheValueItem(studyUrn, examImages);
		synchronized (imageListCache)
		{
			imageListCache.updateItem(item);
		}
	}
	
	public void cacheImages(ImageURN imageUrn, ExamImages examImages)
	{
		try
		{			
			StudyURN studyUrn = imageUrn.getParentStudyURN();
			cacheImages(studyUrn, examImages);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("Error creating studyURN from imageURN '" + imageUrn.toString() + "' to cache exam images, " + urnfX.getMessage());
		}		
	}
	
	public ExamImages getCacheImages(StudyURN studyUrn)
	{
		ExamImages images = null;
		logger.debug("Searching for exam images associated with exam '" + studyUrn.toString() + "'.");
		synchronized (imageListCache)
		{
			ExamImagesBaseTimedCacheValueItem cacheItem = 
				(ExamImagesBaseTimedCacheValueItem)imageListCache.getItem(studyUrn);
			if(cacheItem != null)
			{				
				images = cacheItem.getExamImages();
				logger.debug("Found '" + images.size() + "' exam images associated with exam '" + studyUrn.toString() + "'.");
			}
		}
		return images;
	}
	
	public ExamImages getCacheImages(ImageURN imageUrn)
	{
		try
		{
			StudyURN studyUrn = imageUrn.getParentStudyURN();
			return getCacheImages(studyUrn);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("Error creating studyURN from imageURN '" + imageUrn.toString() + "' to get cached exam images, " + urnfX.getMessage());
			return null;
		}
	}
	
	class ExamImagesBaseTimedCacheValueItem
	extends BaseTimedCacheValueItem
	{
		final ExamImages examImages;
		final StudyURN studyUrn;
		
		ExamImagesBaseTimedCacheValueItem(StudyURN studyUrn, ExamImages examImages)
		{
			this.examImages = examImages;
			this.studyUrn = studyUrn;
		}

		@Override
		public Object getKey()
		{
			return this.studyUrn;
		}

		@Override
		public String toString()
		{
			return this.studyUrn.toString();
		}

		public ExamImages getExamImages()
		{
			return examImages;
		}
	}
}
