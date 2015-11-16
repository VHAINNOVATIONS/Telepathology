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

import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;
import gov.va.med.imaging.exchange.business.Image;

/**
 * This is a very short term in memory cache of VistA Imaging objects
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingShortTermMetadataCache
{
	private final static long VISTA_IMAGE_LIST_CACHE_TIMER_REFRESH = 1000 * 60 * 4; // check for expired items in cache every 4 minutes
	private final static long VISTA_IMAGE_LIST_CACHE_RETENTION_PERIOD = 1000 * 60 * 5; // items last in cache for 5 minutes
	private final static Logger logger = Logger.getLogger(VistaImagingShortTermMetadataCache.class);
	private static BaseTimedCache<StudyURN, ImageListBaseTimedCacheValueItem> imageListCache = null;
	
	static
	{
		imageListCache = 
			new BaseTimedCache<StudyURN, ImageListBaseTimedCacheValueItem>(VistaImagingShortTermMetadataCache.class.toString());
		//imageListCache.setRetentionPeriod(VISTA_IMAGE_LIST_CACHE_TIMER_REFRESH);
		imageListCache.setRetentionPeriod(VISTA_IMAGE_LIST_CACHE_RETENTION_PERIOD);
		TaskScheduler.getTaskScheduler().schedule(imageListCache, 
				VISTA_IMAGE_LIST_CACHE_TIMER_REFRESH, VISTA_IMAGE_LIST_CACHE_TIMER_REFRESH);
	}
		
	public VistaImagingShortTermMetadataCache()
	{
		super();
	}
	
	public void cacheImages(StudyURN studyUrn, List<Image> images)
	{
		logger.debug("Caching images associated with study '" + studyUrn.toString() + "' in short term metadata cache.");
		ImageListBaseTimedCacheValueItem item = 
			new ImageListBaseTimedCacheValueItem(studyUrn, images);
		synchronized (imageListCache)
		{
			imageListCache.updateItem(item);
		}
	}
	
	public void cacheImages(ImageURN imageUrn, List<Image> images)
	{
		try
		{			
			StudyURN studyUrn = imageUrn.getParentStudyURN();
			cacheImages(studyUrn, images);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("Error creating studyURN from imageURN '" + imageUrn.toString() + "' to cache images, " + urnfX.getMessage());
		}		
	}
	
	public List<Image> getCacheImages(StudyURN studyUrn)
	{
		List<Image> images = null;
		logger.debug("Searching for images associated with study '" + studyUrn.toString() + "'.");
		synchronized (imageListCache)
		{
			ImageListBaseTimedCacheValueItem cacheItem = 
				(ImageListBaseTimedCacheValueItem)imageListCache.getItem(studyUrn);
			if(cacheItem != null)
			{				
				images = cacheItem.getImages();
				logger.debug("Found '" + images.size() + "' images associated with study '" + studyUrn.toString() + "'.");
			}
		}
		return images;
	}
	
	public List<Image> getCacheImages(ImageURN imageUrn)
	{
		try
		{
			StudyURN studyUrn = imageUrn.getParentStudyURN();
			return getCacheImages(studyUrn);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("Error creating studyURN from imageURN '" + imageUrn.toString() + "' to get cached images, " + urnfX.getMessage());
			return null;
		}
	}
	
	class ImageListBaseTimedCacheValueItem
	extends BaseTimedCacheValueItem
	{
		final List<Image> images;
		final StudyURN studyUrn;
		
		ImageListBaseTimedCacheValueItem(StudyURN studyUrn, List<Image> images)
		{
			this.images = images;
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

		public List<Image> getImages()
		{
			return images;
		}
		
	}
	
}
