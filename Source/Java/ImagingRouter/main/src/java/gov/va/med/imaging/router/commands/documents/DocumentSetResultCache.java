/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 20, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.router.commands.documents;

import org.apache.log4j.Logger;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;

/**
 * This is a short term in memory cache of document set results
 * 
 * @author VHAISWWERFEJ
 *
 */
public class DocumentSetResultCache
{
	private final static long DOCUMENT_SET_RESULT_CACHE_TIMER_REFRESH = 1000 * 60 * 10; // check for expired items in cache every 10 minutes
	private final static long DOCUMENT_SET_RESULT_CACHE_RETENTION_PERIOD = 1000 * 60 * 10; // items last in cache for 10 minutes
	//private static BaseTimedCache<String, DocumentSetResultCacheValueItem> documentSetResultCache = null;
	private final BaseTimedCache<String, DocumentSetResultCacheValueItem> documentSetResultCache;
	private final static Logger logger = Logger.getLogger(DocumentSetResultCache.class);
	
	/*
	static
	{
		documentSetResultCache = 
			new BaseTimedCache<String, DocumentSetResultCacheValueItem>(DocumentSetResultCache.class.toString());
		//imageListCache.setRetentionPeriod(VISTA_IMAGE_LIST_CACHE_TIMER_REFRESH);
		documentSetResultCache.setRetentionPeriod(DOCUMENT_SET_RESULT_CACHE_RETENTION_PERIOD);
		TaskScheduler.getTaskScheduler().schedule(documentSetResultCache, 
				DOCUMENT_SET_RESULT_CACHE_TIMER_REFRESH, DOCUMENT_SET_RESULT_CACHE_TIMER_REFRESH);
	}*/
	
	private DocumentSetResultCache()
	{
		super();
		documentSetResultCache = 
			new BaseTimedCache<String, DocumentSetResultCacheValueItem>(DocumentSetResultCache.class.toString());
		//imageListCache.setRetentionPeriod(VISTA_IMAGE_LIST_CACHE_TIMER_REFRESH);
		documentSetResultCache.setRetentionPeriod(DOCUMENT_SET_RESULT_CACHE_RETENTION_PERIOD);
		TaskScheduler.getTaskScheduler().schedule(documentSetResultCache, 
				DOCUMENT_SET_RESULT_CACHE_TIMER_REFRESH, DOCUMENT_SET_RESULT_CACHE_TIMER_REFRESH);
	}
	
	private final static DocumentSetResultCache singleton = new DocumentSetResultCache();
	private static DocumentSetResultCache getSingleton()
	{
		return singleton;
	}
	
	public static void cacheDocumentSetResult(RoutingToken routingToken, PatientIdentifier patientIdentifier,
			DocumentSetResult documentSetResult)
	{
		try
		{
			if(documentSetResult != null)
			{
				DocumentSetResultCache cache = getSingleton();
				DocumentSetResultCacheValueItem cacheItem = 
					new DocumentSetResultCacheValueItem(routingToken, patientIdentifier, documentSetResult);
				synchronized(cache.documentSetResultCache)
				{
					cache.documentSetResultCache.updateItem(cacheItem);
				}
			}
		}
		catch(Exception ex)
		{
			logger.error("Error caching document set for routing token '" + routingToken.toRoutingTokenString() + "', patient '" + patientIdentifier + "', " + ex.getMessage());
		}
	}
	
	public static DocumentSetResult getCachedDocumentSetResult(RoutingToken routingToken, PatientIdentifier patientIdentifier)
	{
		
		String key = DocumentSetResultCacheValueItem.createKey(routingToken, patientIdentifier);
		DocumentSetResultCache cache = getSingleton();
		synchronized(cache.documentSetResultCache)
		{
			DocumentSetResultCacheValueItem cacheItem = 
				(DocumentSetResultCacheValueItem) cache.documentSetResultCache.getItem(key);
			if(cacheItem != null)
			{
				return cacheItem.getDocumentSetResult();
			}
		}
		return null;
	}
	
	static class DocumentSetResultCacheValueItem
	extends BaseTimedCacheValueItem
	{
		private final RoutingToken routingToken;
		private final PatientIdentifier patientIdentifier;
		private final DocumentSetResult documentSetResult;

		/**
		 * @param routingToken
		 * @param patientIcn
		 * @param documentSetResult
		 */
		public DocumentSetResultCacheValueItem(RoutingToken routingToken,
				PatientIdentifier patientIdentifier, DocumentSetResult documentSetResult)
		{
			super();
			this.routingToken = routingToken;
			this.patientIdentifier = patientIdentifier;
			this.documentSetResult = documentSetResult;
		}

		/**
		 * @return the routingToken
		 */
		public RoutingToken getRoutingToken()
		{
			return routingToken;
		}

		public PatientIdentifier getPatientIdentifier()
		{
			return patientIdentifier;
		}

		/**
		 * @return the documentSetResult
		 */
		public DocumentSetResult getDocumentSetResult()
		{
			return documentSetResult;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.exchange.BaseTimedCacheValueItem#getKey()
		 */
		@Override
		public Object getKey()
		{
			return createKey(getRoutingToken(), getPatientIdentifier());
			//return getRoutingToken().toRoutingTokenString() + "_" + getPatientIcn();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return getKey().toString();
		}
		
		static String createKey(RoutingToken routingToken, PatientIdentifier patientIdentifier)
		{
			return routingToken.toRoutingTokenString() + "_" + patientIdentifier.toString();
		}
	}
	
}
