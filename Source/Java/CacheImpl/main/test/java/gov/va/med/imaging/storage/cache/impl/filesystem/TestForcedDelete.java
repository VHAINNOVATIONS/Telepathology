/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.junit.Test;

import gov.va.med.DataGenerationConfiguration;
import gov.va.med.imaging.storage.cache.AbstractAccessDirectCacheTest;
import gov.va.med.imaging.storage.cache.AbstractCacheTest;
import gov.va.med.imaging.storage.cache.CacheItemPath;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * Test that we can delete an instance while other threads are reading/writing to
 * the Instance.
 * 
 * The reading/writing threads must expect IOException to be thrown.
 * 
 * @author VHAISWBECKEC
 *
 */
public class TestForcedDelete 
extends AbstractAccessDirectCacheTest 
{
	private final CacheItemPath rootPath = new CacheItemPath(getCacheName(), "test-metadata");
	
	protected URI getCacheUri() 
	throws URISyntaxException
	{
		return new URI("file:///vix/cache/" + this.getName());
	}
	protected String getPrototypeName()
	{
		return "TestWithNoEvictionPrototype";
	}

	@Test
	public void testDeleteWhileReading() 
	throws CacheException, IOException
	{
		// create an item in the cache
		CacheItemPath path = AbstractCacheTest.createRandomCacheItemPath(rootPath, 3);
		byte[] data = AbstractCacheTest.createSampleData(2048);
		createAndWriteInstance(path, data);
		
		// read the item from the cache, slowly enough that we can delete it while it is being read
		Thread slowReaderThread = new Thread(new InstanceReader(path, IOException.class));
		slowReaderThread.start();
		
		try{Thread.sleep(1000L);}catch(InterruptedException iX){}
		deleteAndConfirmDeletionInstance(path.getRegionName(), path.getGroupsName(), path.getInstanceName(), true);
	}


	@Test
	public void testDeleteWhileWriting() 
	throws CacheException, IOException
	{
		// create an item in the cache
		CacheItemPath path = AbstractCacheTest.createRandomCacheItemPath(rootPath, 3);
		getCache().getOrCreateInstance(path.getRegionName(), path.getGroupsName(), path.getInstanceName());
		
		// read the item from the cache, slowly enough that we can delete it while it is being read
		Thread slowWriterThread = new Thread(new InstanceWriter(path, IOException.class));
		slowWriterThread.start();
		
		try{Thread.sleep(1000L);}catch(InterruptedException iX){}
		deleteAndConfirmDeletionInstance(path.getRegionName(), path.getGroupsName(), path.getInstanceName(), true);
	}
	
	/**
	 * Reads an Instance very slowly
	 */
	class InstanceReader
	implements Runnable
	{
		private final CacheItemPath path;
		private final Class<? extends Exception>[] expectedExceptionTypes;
		
		InstanceReader(CacheItemPath path, Class<? extends Exception>... expectedExceptionTypes)
		{
			this.expectedExceptionTypes = expectedExceptionTypes;
			this.path = path;
		}
		
		@Override
		public void run() 
		{
			try 
			{
				slowlyReadInstance(this.path);
				TestCase.fail("No exception was thrown in the reading thread, test was inconclusive.");
			} 
			catch (Exception x) 
			{
				boolean expectedExceptionFound = false;
				if(expectedExceptionTypes != null && expectedExceptionTypes.length > 0)
					for(Class<? extends Exception> expectedException : expectedExceptionTypes)
						if(expectedException.isInstance(x))
							expectedExceptionFound = true;
				
				if(! expectedExceptionFound )
					TestCase.fail("Unexpected exception occured when doing slow read.");
			}
		}
	}
	
	/**
	 * Write an Instance
	 */
	class InstanceWriter
	implements Runnable
	{
		private final CacheItemPath path;
		private final Class<? extends Exception>[] expectedExceptionTypes;
		
		InstanceWriter(CacheItemPath path, Class<? extends Exception>... expectedExceptionTypes)
		{
			this.expectedExceptionTypes = expectedExceptionTypes;
			this.path = path;
		}
		
		@Override
		public void run() 
		{
			try 
			{
				slowlyWriteInstance(this.path);
				TestCase.fail("No exception was thrown in the reading thread, test was inconclusive.");
			} 
			catch (Exception x) 
			{
				boolean expectedExceptionFound = false;
				if(expectedExceptionTypes != null && expectedExceptionTypes.length > 0)
					for(Class<? extends Exception> expectedException : expectedExceptionTypes)
						if(expectedException.isInstance(x))
							expectedExceptionFound = true;
				
				if(! expectedExceptionFound )
					TestCase.fail("Unexpected exception occured when doing slow read.");
			}
		}
	}
}

