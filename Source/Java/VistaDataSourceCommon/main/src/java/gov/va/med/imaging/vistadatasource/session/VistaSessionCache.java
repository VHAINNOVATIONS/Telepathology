package gov.va.med.imaging.vistadatasource.session;

import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaConnection;
import gov.va.med.imaging.vistadatasource.session.configuration.VistaSessionConfiguration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * A cache of VistaConnection instances.
 * This class will maintain the open VistaConnection instances that may be re-used
 * if the same user invokes multiple calls in a short time.
 * There are some security implications in re-using connections and this class closes
 * loopholes that could be created.  In particular it is important that the content
 * of the cache not be exposed outside of this class.
 * 
 * @author vhaiswbeckec
 *
 */
class VistaSessionCache
{
	private static final long serialVersionUID = 9013218348943840922L;
	private static VistaSessionCache singleton;
	
	/**
	 * 
	 * @return
	 */
	static synchronized VistaSessionCache getSingleton()
	{
		if(singleton == null)
			singleton = new VistaSessionCache();
		return singleton;
	}
	
	private final Collection<CacheEntry> cache;
	private final Logger logger;
	private long maximumIdleTime;
	
	/**
	 * 
	 */
	private VistaSessionCache()
	{
		cache = new ArrayList<CacheEntry>();
		logger = Logger.getLogger(this.getClass());
		maximumIdleTime = getVistaSessionConfiguration().getSessionMaxIdleTime();
		
		// Connection timer that watches for connections that don't get closed
		// Connections that don't get closed (are in the cache) can get reclaimed
		// by the same user for another request
		Timer timer = new Timer("VistaSessionCache");
		timer.schedule(new TimerTask() 
		{
			@Override
			public void run()
			{
				long now = System.currentTimeMillis();
				
				try
                {
	                // Close the connections that have been open too long.
					// NOTE: this is the normal way that things get closed, they get added
					// to the cache when a call() is complete and then just time out.
	                long minLastUsedTime = now - getMaximumIdleTime();
	                Collection<CacheEntry> killList = new ArrayList<CacheEntry>();
	                synchronized (cache)
                    {
		                for( Iterator<CacheEntry> cacheIterator = cache.iterator(); cacheIterator.hasNext(); )
		                {
		                	CacheEntry cacheEntry = cacheIterator.next();
		                	
		                	VistaSession vistaSession = cacheEntry.getValue();
		                	int brokerTimeoutTime = vistaSession.getSiteBrokerConnectionTimeout();
		                	long minLastBrokerTimeoutTime = now - (brokerTimeoutTime * 1000);
		                	if( vistaSession.getLastUsedTime() < minLastUsedTime)
		                	{
		                		Logger.getLogger(VistaConnection.class.getName()).info(
		                				"VistaSession instance '" + 
		                				vistaSession.getSessionIndex() +  
		                				"' has been connected and unused for " + 
		                				((now - vistaSession.getLastUsedTime()) / 1000) + 
		                				" seconds, max connection time is " + (getMaximumIdleTime() / 1000) + 
		                				" seconds.  The session, and its associated connection, are being closed\n" +
		                		        "  This information WILL NOT BE REPEATED.");
		                		
		                		// even if the disconnect fails, drop the session from the list
		                		killList.add(cacheEntry);
			                	cacheIterator.remove();
		                	}
		                	// if brokerTimeoutTime = 0 then didn't get real timeout value from VistA, cannot use it
		                	else if((brokerTimeoutTime > 0) 
		                		&& (vistaSession.getLastInternalTime() < minLastBrokerTimeoutTime))
		                	{
		                		if(getVistaSessionConfiguration().isBrokerKeepAliveEnabled())
		                		{
		                			logger.info("VistaSession (" + vistaSession.getSessionIndex() + ")"
		                				+ " instance has been idle for " + ((now - vistaSession.getLastInternalTime()) / 1000) 
		                				+ " seconds, beyond broker keep alive time of " + brokerTimeoutTime + " seconds, refreshing connection");
			                		// the broker can still be open but a keep alive must be done to keep the connection active
			                		if(!vistaSession.keepAlive())
			                		{
			                			logger.warn("There was an error keeping the broker connection alive, disconnecting NOW.");
			                			// there was an error during the keep alive, the broker is now in an unstable state
			                			// it should be added to the kill list to disconnect it
			                			killList.add(cacheEntry);
			                			cacheIterator.remove();		                			
			                		}
		                		}
		                	}
		                }
                    }
	                
	                for(Iterator<CacheEntry> killListIterator = killList.iterator(); killListIterator.hasNext();)
	                {
	                	CacheEntry cacheEntry = killListIterator.next();
	                	
	                	VistaSession vistaSession = cacheEntry.getValue();
	                	try{vistaSession.disconnect(false);}
	                	catch(Exception x){logger.warn(x.getMessage());}
	                	
	                }
                } 
				catch (RuntimeException e)
                {
	                e.printStackTrace();
	                logger.error(e.getMessage());
                }
			}
		}, 30000, 10000);		// starting 30 seconds from now, run every 10 seconds
		
	}
	
	private VistaSessionConfiguration getVistaSessionConfiguration()
	{
		return VistaSessionConfiguration.getConfiguration();
	}
	
	// ==============================================================================
	// public methods are synchronized to prevent simultaneous
	// getting and removing an instance of a VistaConnection
	// ==============================================================================
	
	public long getMaximumIdleTime()
    {
    	return maximumIdleTime;
    }

	public void setMaximumIdleTime(long maximumIdleTime)
    {
    	this.maximumIdleTime = maximumIdleTime;
    }

	/**
	 * Put a VistaSession into the cache.
	 * 
	 * @param url
	 * @param vistaConnection
	 */
	synchronized void put(VistaSession vistaSession)
	{
		if(vistaSession == null)
			return;
		if((getVistaSessionConfiguration().isBrokerKeepAliveEnabled()) 
			&& (vistaSession.getSiteBrokerConnectionTimeout() > 0))
		{
			// execute the keep alive first so we have a known time when the broker was last used
			if(!vistaSession.keepAlive())
			{
				// if there was a problem doing the keep alive, disconnect rather than put into cache
				logger.warn("Error executing keep alive, disconnecting connection rather than putting into cache");
				try{vistaSession.disconnect(false);}
	        	catch(Exception x){logger.warn(x.getMessage());}
	        	return;
			}
		}
		
		VistaSessionContext vistaConnectionContext = createConnectionContext(vistaSession.getURL(), 
				vistaSession.getSecurityContext(), vistaSession.getVersion());
		
		CacheEntry cacheEntry = new CacheEntry(vistaConnectionContext, vistaSession);
		synchronized(cache)
		{
			cache.add( cacheEntry );
		}
		
		logger.info("Added VistaSession (" + vistaSession.getSessionIndex() + ") to VistaSessionCache.");
	}

	/**
	 * Get a VistaSession connected to the given URL and operating in the SecurityContext
	 * of the current caller.  Remove the VistaSession from the cache.
	 * 
	 * @param url
	 * @return
	 */
	synchronized VistaSession get(URL url, String securityContext, String version)
	{
		if(url == null)
			return null;
		VistaSession vistaSession = null;
		VistaSessionContext vistaConnectionContext = createConnectionContext(url, securityContext, version);
		
		// search for a usable VistaSession and
		// take the opportunity to clean up old connections 
		synchronized(cache)
		{
			for(Iterator<CacheEntry> cacheEntryIter = cache.iterator(); cacheEntryIter.hasNext(); )
			{
				CacheEntry indexCacheEntry = cacheEntryIter.next();
				if(indexCacheEntry.getKey().equals(vistaConnectionContext))
				{
					logger.info("Removing VistaSession (" + indexCacheEntry.getValue().getSessionIndex() + ") from VistaSessionCache for use.");
					cacheEntryIter.remove();
					vistaSession = indexCacheEntry.getValue();
					boolean vistaSessionOk = true;
					
					if((getVistaSessionConfiguration().isBrokerKeepAliveEnabled()) 
						&& (vistaSession.getSiteBrokerConnectionTimeout() > 0))
					{
						// check to be sure the session is still good before returning it from the cache
						if(!vistaSession.isUserFullySignedOn())
						{
							// if there was a problem doing the keep alive, disconnect rather than put into cache
							logger.warn("Error checking if user fully signed on, disconnecting connection rather than remvoing session from cache");
							try{vistaSession.disconnect(false);}
				        	catch(Exception x){logger.warn(x.getMessage());}
				        	vistaSessionOk = false;
				        	vistaSession = null;
						}
					}
					if(vistaSessionOk && (vistaSession != null))
						break;
				}
			}
		}
		
		return vistaSession;
	}
	
	/**
	 * Clear the open, unused VistaConnection instances in he cache.
	 */
	synchronized void clear()
	{
		cache.clear();
	}
	
	/**
	 * Create a VistaConnectionContext instance using the given URL and the 
	 * current TransactionContext provided security context.
	 * 
     * @param url
     * @return
     */
    private VistaSessionContext createConnectionContext(URL url, 
    		String securityContext, String version)
    {
	    TransactionContext transactionContext = TransactionContextFactory.get();
		String securityHashCode = transactionContext.getSecurityHashCode();
		VistaSessionContext connectionContext = new VistaSessionContext(url, 
				securityHashCode, securityContext, version);
		
	    return connectionContext;
    }
	
	/**
     * This class is the key value in a cache map to determine
     * whether a connection can be re-used for a subsequent call.
     * To be re-used a connection must have the same URL and the same
     * security context (the same user).
     * 
     * @author vhaiswbeckec
     *
     */
    private class VistaSessionContext
    {
    	private URL url;
    	private String securityHashCode;
    	private final String securityContext;
    	private final String version;
    	
    	VistaSessionContext(URL url, String securityHashCode, 
    			String securityContext, String version)
    	{
    		this.url = url;
    		this.securityHashCode = securityHashCode;
    		this.securityContext = securityContext;
    		this.version = version;
    	}

    	@Override
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
    		result = PRIME * result + ((this.securityHashCode == null) ? 0 : this.securityHashCode.hashCode());
            result = PRIME * result + ((url == null) ? 0 : url.hashCode());
            result = PRIME * result + ((this.securityContext == null) ? 0 : this.securityContext.hashCode());
            result = PRIME * result + ((this.version == null) ? 0 : this.version.hashCode());
            return result;
        }

    	@Override
        public boolean equals(Object obj)
        {
            if (this == obj)
    	        return true;
            if (obj == null)
    	        return false;
            final VistaSessionContext other = (VistaSessionContext) obj;
            if( ! securityHashCode.equals(other.securityHashCode) )
    	        return false;
            if (url == null)
            {
    	        if (other.url != null)
    		        return false;
            } else if (!url.equals(other.url))
    	        return false;
            if(securityContext == null)
            {
            	if(other.securityContext != null)
            		return false;
            }
            else if(!securityContext.equals(other.securityContext))
            	return false;
            if(version == null)
            {
            	if(other.version != null)
            		return false;
            }
            else if(!version.equals(other.version))
            	return false;
            return true;
        }
    }
    
    /**
     * This class is akin to the Map.Entry class in java.util, but specialized
     * for use as a cache entry.  In particular, the Map semantics precludes
     * duplicate entries whereas we allow them.
     * 
     * @author vhaiswbeckec
     *
     */
    private class CacheEntry
    {
    	private final VistaSessionContext key;
    	private final VistaSession value;
    	
		public CacheEntry(VistaSessionContext key, VistaSession value)
        {
	        this.key = key;
	        this.value = value;
        }

		public VistaSessionContext getKey()
        {
        	return key;
        }

		public VistaSession getValue()
        {
        	return value;
        }

		@Override
        public int hashCode()
        {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + ((key == null) ? 0 : key.hashCode());
	        result = prime * result + ((value == null) ? 0 : value.hashCode());
	        return result;
        }

		@Override
        public boolean equals(Object obj)
        {
	        if (this == obj)
		        return true;
	        if (obj == null)
		        return false;
	        if (getClass() != obj.getClass())
		        return false;
	        final CacheEntry other = (CacheEntry) obj;
	        if (key == null)
	        {
		        if (other.key != null)
			        return false;
	        } else if (!key.equals(other.key))
		        return false;
	        if (value == null)
	        {
		        if (other.value != null)
			        return false;
	        } else if (!value.equals(other.value))
		        return false;
	        return true;
        }
    }
}
