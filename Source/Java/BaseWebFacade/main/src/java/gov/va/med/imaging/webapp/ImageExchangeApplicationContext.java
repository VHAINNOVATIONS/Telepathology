/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: August 6, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWBUCKD
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
package gov.va.med.imaging.webapp;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author VHAISWBUCKD
 * 
 * This static class is responsible for holding the single instance of the spring factory that will be used by
 * the exchange applications.
 * 
 * This class implements a Singleton pattern, but allows for an initialization parameter.
 * The one-arg form of the static getSingleton() method should be called only once, though it
 * may be safely called multiple times if the single-arg is identical to the original call.  If
 * the single-arg getSingleton() called and the single-arg is not identical to that on the original
 * call then getSingleton() will throw a runtime exception (i.e. unchecked, stop the VM kind of error).
 * 
 * Once initialized, the no-arg version of getSingleton() should be called.
 * 
 * If the no-arg version of getSingleton() is called before the singleton is initialized then this class
 * will be initialized using the default context file - "applicationContext.xml"
 */
public class ImageExchangeApplicationContext 
extends ClassPathXmlApplicationContext
{
	private static String[] effectiveApplicationContext = null;
	private static ImageExchangeApplicationContext singleton = null;
	private static Logger log = Logger.getLogger(ImageExchangeApplicationContext.class);
	
	public static synchronized ImageExchangeApplicationContext getSingleton(String[] contexts)
	{
		if(singleton == null && contexts == null)
			throw new ImageExchangeApplicationContextInitializationError("ImageExchangeApplicationContext may not be explicitly initialized with a null context.");
		
		if(singleton != null)
		{
			if(contexts.length != effectiveApplicationContext.length)
				throw new ImageExchangeApplicationContextInitializationError("ImageExchangeApplicationContext may not be re-initialized with a different context.");
			
			for(int index=0; index < contexts.length; ++index)
				if(! contexts[index].equalsIgnoreCase(effectiveApplicationContext[index]))
					throw new ImageExchangeApplicationContextInitializationError("ImageExchangeApplicationContext may not be re-initialized with a different context.");
			log.warn("ImageExchangeApplicationContext.getSingleton was called twice with the same context.");
		}
		
		if( singleton == null )
		{
			effectiveApplicationContext = contexts;
			singleton = new ImageExchangeApplicationContext();
		}
		
		return singleton;
	}
	
	public static ImageExchangeApplicationContext getSingleton() // used to be synchronized - DKB 
	{
		if (singleton == null)
		{
			log.error("ImageExchangeApplicationContext was not initialized when getSingleton() was called.");
			throw new ImageExchangeApplicationContextInitializationError("ImageExchangeApplicationContext was not initialized when getSingleton() was called.");
		}
		
		return singleton;
	}

	/* 
	 * ===========================================================================================================================
	 * Instance Fields/Methods/Constructors
	 * ===========================================================================================================================
	 */
	
	/**
	 * 
	 *
	 */
	private ImageExchangeApplicationContext() 
	{
		super( effectiveApplicationContext );
	}

//	public IManager getVAManager() {
//		return (IManager) getBean("vaManager");
//	}
//
//	public IManager getDODManager() {
//		return (IManager) getBean("dodManager");
//	}

}
