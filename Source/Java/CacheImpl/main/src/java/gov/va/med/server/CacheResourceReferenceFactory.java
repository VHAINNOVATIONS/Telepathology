package gov.va.med.server;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.CacheManager;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;

import java.util.Hashtable;

import javax.management.MBeanException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;

/**
 * This class implements the JNDI SPI ObjectFactory interface, providing a way
 * to create Cache instances and make them available as
 * Resources. The app server may create many copies of the class, however only
 * one instance of the cache (and cache manager) should be created. 
 * 
 * @author VHAISWBECKEC
 * 
 */
public class CacheResourceReferenceFactory 
implements ObjectFactory 
{
	public final static String CACHE_MANAGER_NAME = "CacheManager";
	private static final Logger logger = Logger.getLogger(CacheResourceReferenceFactory.class);

	public CacheResourceReferenceFactory() 
	{
		try 
		{
			registerServerLifecycleListener();
		} 
		catch (MBeanException e) 
		{
			e.printStackTrace();
			throw new ExceptionInInitializerError();
		} 
		catch (CacheException e) 
		{
			e.printStackTrace();
			throw new ExceptionInInitializerError();
		}
	}

	private static boolean lifecycleListenerRegistered = false;
	private static synchronized void registerServerLifecycleListener() 
	throws MBeanException, CacheException 
	{
		if( ! lifecycleListenerRegistered)
		{
			ServerAdapterImpl.getSingleton().addServerLifecycleListener( CacheManagerImpl.getSingleton() );
			lifecycleListenerRegistered = true;
		}
	}

	/**
	 * @param obj
	 *            The possibly null object containing location or reference
	 *            information that can be used in creating an object
	 * @param name
	 *            The name of this object relative to <code>nameCtx</code>
	 * @param nameCtx
	 *            The context relative to which the <code>name</code> parameter
	 *            is specified, or <code>null</code> if <code>name</code> is
	 *            relative to the default initial context
	 * @param environment
	 *            The possibly null environment that is used in creating this
	 *            object
	 * 
	 *            For the following element in server.xml: <Resource
	 *            auth="Container"
	 *            description="Core application (Spring) context"
	 *            name="CoreRouterContext"
	 *            type="org.springframework.context.ApplicationContext"
	 *            instance-name="CoreRouterContext"
	 *            factory="gov.va.med.server.tomcat.CoreRouterContextFactory" />
	 * 
	 *            nameCts = 'org.apache.naming.NamingContext@787d6a'
	 *            name.toString = 'ImagingExchangeCache'
	 * 
	 *            Reference factory
	 *            classname[org.apache.naming.factory.ResourceFactory], factory
	 *            class location [null] RefAddr type [description] = [Caching
	 *            mechanism for ViXS] RefAddr type [scope] = [Shareable] RefAddr
	 *            type [auth] = [Container] RefAddr type [factory] =
	 *            [gov.va.med.imaging.storage.cache.impl.tomcat.CacheFactory]
	 * 
	 *            javax.naming.Name [.ImagingExchangeCache]
	 */
	@Override
	public Object getObjectInstance(
		Object obj, 
		Name name, 
		Context nameCtx,
		Hashtable<?, ?> environment) 
	throws Exception 
	{
		// We only know how to deal with <code>javax.naming.Reference</code>s
		// that specify a class name of "gov.va.med.imaging.storage.cache.Cache"
		if ((obj == null) || !(obj instanceof Reference))
			return null;

		Reference reference = (Reference) obj;

		// The properties of the Reference instance come from the server.xml
		// file, Resource element:
		// <Resource
		// auth="Container"
		// description="VIX Cache"
		// name="ImagingExchangeCache"
		// scope="Shareable"
		// type="gov.va.med.imaging.storage.cache.Cache"
		// instance-name="CoreRouterContext"
		// factory="gov.va.med.imaging.storage.cache.impl.CacheManagerImpl"/>

		String referenceClassName = reference.getClassName();
		Class<?> referenceClass = null;
		
		try
		{
			referenceClass = referenceClassName == null ? null : Class.forName(referenceClassName);
		}
		catch (Exception e)
		{
			logger.warn("Unable to validate returned value is expected type '"
				+ referenceClassName 
				+ "', continuing but ClassCastExceptions may be thrown later."); 
		}
		
		CacheManager cacheManager = CacheManagerImpl.getSingleton();
		
		String cacheName = name.toString();
		if(CACHE_MANAGER_NAME.equalsIgnoreCase(cacheName))
		{
			logger.info("Returning reference to cache manager " 
				+ getClass().getSimpleName()
				+ " getObjectInstance(Reference, '" + cacheName + "', '"
				+ nameCtx.toString() + ", ...) ");
			
			if(referenceClass != null && !referenceClass.isInstance(cacheManager) )
			{
				logger.error("Returned value is NOT expected type '"
					+ referenceClassName 
					+ "', returning null.");
				return null;
			}
			
			return cacheManager;
		}
		else
		{
			logger.info("Returning reference to cache instance " 
				+ getClass().getSimpleName()
				+ " getObjectInstance(Reference, '" + cacheName + "', '"
				+ nameCtx.toString() + ", ...) ");
	
			Cache cache = cacheManager.getCache(cacheName);
			
			if(referenceClass != null && !referenceClass.isInstance(cache) )
			{
				logger.error("Returned value is NOT expected type '"
					+ referenceClassName 
					+ "', returning null.");
				return null;
			}
			
			return cache;
		}
	}
}
