/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandController;
import gov.va.med.interactive.CommandLineParseException;
import gov.va.med.interactive.CommandProcessor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.management.MBeanException;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestInteractiveCacheContainer 
extends TestCase
{
	InteractiveCacheContainer icc;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		
		CommandController.initializeLogging();
		
		CacheManagerImpl cacheManager;
		try
		{
			cacheManager = CacheManagerImpl.getSingleton();
		}
		catch (MBeanException x1)
		{
			x1.printStackTrace();
			return;
		}
		catch (CacheException x1)
		{
			x1.printStackTrace();
			return;
		}
		try
		{
			InteractiveCacheCommandFactoryImpl commandFactory = InteractiveCacheCommandFactoryImpl.getSingleton();
			icc = new InteractiveCacheContainer(cacheManager, commandFactory);
			icc.getCommandSource().pushCommands(new String[]{"exit"});
			icc.run();
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return;
		}
	}

	public void testExistence() 
	throws CommandLineParseException
	{
		assertNotNull(icc);
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

}
