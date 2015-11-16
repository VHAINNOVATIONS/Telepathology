/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 25, 2012
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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.exchange.BaseTimedCache;
import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.TaskScheduler;
import gov.va.med.imaging.exchange.business.WelcomeMessage;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class WelcomeMessageCache
{
	
	private final static long WELCOME_MESSAGE_CACHE_TIMER_REFRESH = 1000 * 60 * 10; // check for expired items in cache every 10 minutes
	private final static long WELCOME_MESSAGE_CACHE_RETENTION_PERIOD = 1000 * 60 * 20; // items last in cache for 20 minutes
	//private static BaseTimedCache<String, DocumentSetResultCacheValueItem> documentSetResultCache = null;
	private final BaseTimedCache<String, WelcomeMessageCacheValueItem> welcomeMessageCache;
	private final static Logger logger = Logger.getLogger(WelcomeMessageCache.class);
	
	private WelcomeMessageCache()
	{
		super();
		welcomeMessageCache = 
			new BaseTimedCache<String, WelcomeMessageCacheValueItem>(WelcomeMessageCache.class.toString());
		//imageListCache.setRetentionPeriod(VISTA_IMAGE_LIST_CACHE_TIMER_REFRESH);
		welcomeMessageCache.setRetentionPeriod(WELCOME_MESSAGE_CACHE_RETENTION_PERIOD);
		TaskScheduler.getTaskScheduler().schedule(welcomeMessageCache, 
				WELCOME_MESSAGE_CACHE_TIMER_REFRESH, WELCOME_MESSAGE_CACHE_TIMER_REFRESH);
	}
	
	private final static WelcomeMessageCache singleton = new WelcomeMessageCache();
	private static WelcomeMessageCache getSingleton()
	{
		return singleton;
	}
	
	public static void cacheWelcomeMessage(RoutingToken routingToken, WelcomeMessage welcomeMessage)
	{
		try
		{
			if(welcomeMessage != null)
			{
				WelcomeMessageCache cache = getSingleton();
				WelcomeMessageCacheValueItem cacheItem = 
					new WelcomeMessageCacheValueItem(routingToken, welcomeMessage);
				synchronized(cache.welcomeMessageCache)
				{
					cache.welcomeMessageCache.updateItem(cacheItem);
				}
			}
		}
		catch(Exception ex)
		{
			logger.error("Error caching welcome message for routing token '" + routingToken.toRoutingTokenString() + "', " + ex.getMessage());
		}
	}
	
	public static WelcomeMessage getCachedWelcomeMessage(RoutingToken routingToken)
	{
		
		String key = WelcomeMessageCacheValueItem.createKey(routingToken);
		WelcomeMessageCache cache = getSingleton();
		synchronized(cache.welcomeMessageCache)
		{
			WelcomeMessageCacheValueItem cacheItem = 
				(WelcomeMessageCacheValueItem) cache.welcomeMessageCache.getItem(key);
			if(cacheItem != null)
			{
				return cacheItem.getWelcomeMessage();
			}
		}
		return null;
	}

	static class WelcomeMessageCacheValueItem
	extends BaseTimedCacheValueItem
	{
		private final RoutingToken routingToken;
		private final WelcomeMessage welcomeMessage;
		/**
		 * @param routingToken
		 * @param patientIcn
		 */
		public WelcomeMessageCacheValueItem(RoutingToken routingToken, WelcomeMessage welcomeMessage)
		{
			super();
			this.routingToken = routingToken;
			this.welcomeMessage = welcomeMessage;			
		}
		/**
		 * @return the routingToken
		 */
		public RoutingToken getRoutingToken()
		{
			return routingToken;
		}
		/**
		 * @return the welcomeMessage
		 */
		public WelcomeMessage getWelcomeMessage()
		{
			return welcomeMessage;
		}
		/* (non-Javadoc)
		 * @see gov.va.med.imaging.exchange.BaseTimedCacheValueItem#getKey()
		 */
		@Override
		public Object getKey()
		{
			return createKey(getRoutingToken());
		}
		
		static String createKey(RoutingToken routingToken)
		{
			return routingToken.toRoutingTokenString();
		}
	}
}
