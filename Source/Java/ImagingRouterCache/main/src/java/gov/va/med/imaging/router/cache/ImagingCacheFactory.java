/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 31, 2011
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
package gov.va.med.imaging.router.cache;

import gov.va.med.imaging.exchange.storage.cache.DODSourcedCache;
import gov.va.med.imaging.exchange.storage.cache.VASourcedCache;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ImagingCacheFactory 
implements ObjectFactory
{
	private final static Logger logger = Logger.getLogger(ImagingCacheFactory.class);
	private static ApplicationContext cacheApplicationContext = null;
	public static final String[] CONTEXT_FILENAMES = new String[]{"imagingRouterCacheContext.xml"};
	public static final String DOD_SOURCED_CACHE_BEAN_NAME = "dodSourcedCache";
	public static final String VA_SOURCED_CACHE_BEAN_NAME = "vaSourcedCache";

	/* (non-Javadoc)
	 * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
	 */
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable<?, ?> environment) 
	throws Exception 
	{
		// We only know how to deal with <code>javax.naming.Reference</code>s
        // that specify a class name of "gov.va.med.imaging.storage.cache.Cache"
        if( (obj == null) || !(obj instanceof Reference) )
            return null;

        // The JNDI defines the Reference class to represent reference. 
        // A reference contains information on how to construct a copy of the object. 
        // The JNDI will attempt to turn references looked up from the directory into the Java objects that they 
        // represent so that JNDI clients have the illusion that what is stored in the directory are Java objects.
        Reference ref = (Reference) obj;
        
        // Best guess; the properties of the Reference instance come from the server.xml file, Resource element:
        // <Resource
        // auth="Container"
        // description="Core application (Spring) context"
        // name="CoreRouterContext"
        // type="org.springframework.context.ApplicationContext"
        // instance-name="CoreRouterContext"
        // factory="gov.va.med.server.tomcat.CoreRouterContextFactory"/>
        
        logger.info(getClass().getSimpleName() + " getObjectInstance(Reference, '" +
        		name.toString() + "', '" + nameCtx.toString() + ", ...) " );
        logger.info("begin Reference contents ====================== ");
        //logger.info(dumpReferenceContents(ref));
        logger.info("end   Reference contents ====================== ");
        
        //System.out.println(dumpName(name));
		String resourceName = name.toString();
		
		// this reference will contain the reference that is being requested
    	Object implementation = null;
    	
    	String msg = getClass().getSimpleName() + " getting reference to '" + resourceName + "', expected type is [" + ref.getClassName() + "].";
    	logger.info(msg);
    	
    	// The router implementation is determined by the Spring context
    	logger.info("Ref classname [" + ref.getClassName() + "]");
    	logger.info("Cache Class Name [" + gov.va.med.imaging.router.cache.ImagingCache.class.getName() + "]");
        if( gov.va.med.imaging.router.cache.ImagingCache.class.getName().equals(ref.getClassName()) )
        {
    		implementation = getCache();
        }
    	
    	if(implementation == null)
    	{
    		logger.error( getClass().getSimpleName() + " reference to '" + resourceName + "' is null." );
    	}
    	else
    	{
        	msg = 
        		getClass().getSimpleName() + " reference to '" + resourceName + "' is " +
    			(implementation == null ? "null" : "[" + implementation.getClass().getName() + "]") + ".";
        	
    		logger.info(getClass().getSimpleName() + " reference to '" + resourceName + "' is of type [" + implementation.getClass().getName() + "]." );

    	}
    	
    	return implementation;
	}

	private synchronized static gov.va.med.imaging.router.cache.ImagingCache getCache()
	{
		DODSourcedCache dodCache = getDODSourcedCache();
		VASourcedCache vaCache = getVASourcedCache();
		return new ImagingCacheImpl(dodCache, vaCache);
	}
	
	private static synchronized VASourcedCache getVASourcedCache() 
	{
		ApplicationContext appContext = getApplicationContext();
		
		String msg = "ImagingCacheFactory " + 
			(appContext == null ? "failed to obtain" : "obtained") + 
			" reference to application context when getting VASourcedCache";
		logger.info(msg);
		
		VASourcedCache vaCache = 
			(VASourcedCache)appContext.getBean(VA_SOURCED_CACHE_BEAN_NAME);
		
		msg = "ImagingCacheFactory " + 
			(vaCache == null ? "failed to obtain" : "obtained") + 
			" reference to router from application context." + 
			"VA Sourced Cache implementation is of type '" + (vaCache == null ? "<null>" : vaCache.getClass().getName()) + "'.";
		logger.info(msg);
		
		return vaCache;
	}
	
	private static synchronized DODSourcedCache getDODSourcedCache() 
	{
		ApplicationContext appContext = getApplicationContext();
		
		String msg = "ImagingCacheFactory " + 
			(appContext == null ? "failed to obtain" : "obtained") + 
			" reference to application context when getting DODSourcedCache";
		logger.info(msg);
		
		DODSourcedCache dodCache = 
			(DODSourcedCache)appContext.getBean(DOD_SOURCED_CACHE_BEAN_NAME);
		
		msg = "ImagingCacheFactory " + 
			(dodCache == null ? "failed to obtain" : "obtained") + 
			" reference to router from application context." + 
			"DOD Sourced Cache implementation is of type '" + (dodCache == null ? "<null>" : dodCache.getClass().getName()) + "'.";
		logger.info(msg);
		
		return dodCache;
	}
	
	private synchronized static ApplicationContext getApplicationContext() 
	{
		if(cacheApplicationContext == null)
		{
			logger.info("Creating VIX Cache context.");
			cacheApplicationContext = new ClassPathXmlApplicationContext( CONTEXT_FILENAMES );
			logger.info("VIX Cache context " + cacheApplicationContext.hashCode() + " created.");
			
		}
		
		return cacheApplicationContext;
	}
}
