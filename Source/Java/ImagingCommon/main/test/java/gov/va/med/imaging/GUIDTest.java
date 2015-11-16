package gov.va.med.imaging;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author beckey created: Jan 10, 2005 at 1:28:57 PM
 * 
 * This class does ...
 */
public class GUIDTest
		extends AbstractTestCase
{

	/**
	 * @param arg0
	 */
	public GUIDTest(String arg0)
	{
		super(arg0);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(GUIDTest.class);
	}

	public void testFormat()
	{
		new GUID();		// forces some other system out's to occur
		System.out.println("Long format [" + (new GUID()).toLongString() + "] ?" );
		System.out.println("Short format [" + (new GUID()).toShortString() + "] ?" );
		System.out.println("Is the following GUID representation the expected format [" + (new GUID()).toString() + "] ?" );
	}
	
	/**
	 * Generate 1000 GUIDS and assure that they can be recreated from their
	 * String form.
	 */
	public void testStringConstructors()
	{
		for (int i = 0; i < 10000; ++i)
		{
			if(i % 1000 == 0)
			{
				try
				{
					// sleep some random time to let the clock
					// return something new
					Thread.sleep((int)(Math.random() * 1000.0));
				}
				catch (InterruptedException iX)
				{
					iX.printStackTrace();
					// ignore it an keep going
				}
			}

			GUID guid = new GUID();
			GUID guid2 = new GUID(guid.toString());

			//System.out.println("testConstructors(" + i + " [" + guid.toString() + "]");
			
			assertTrue("GUID [" + guid.toString() + "]", guid.equals(guid2));
		}
	}

	public void testByteArrayConstructors()
	{
		for (int i = 0; i < 10000; ++i)
		{
			if(i % 1000 == 0)
			{
				try
				{
					// sleep some random time to let the clock
					// return something new
					Thread.sleep((int)(Math.random() * 1000.0));
				}
				catch (InterruptedException iX)
				{
					iX.printStackTrace();
					// ignore it an keep going
				}
			}

			GUID guid = new GUID();
			GUID guid2 = new GUID(guid.byteArray());

			//System.out.println("testConstructors(" + i + " [" + guid.toString() + "]");
			
			assertTrue("GUID [" + guid.toString() + "]", guid.equals(guid2));
		}
	}
	
	/**
	 * 
	 *
	 */
	public void testXor()
	{
		for (int i = 0; i < 10; ++i)
		{
			GUID guid = new GUID();
			System.out.println("XOR = " + Byte.toString( guid.getChecksum() ));
		}		
	}

	private static int performanceTestCount = 100;

	private static int performanceTestMaxTime = 1000;

	/**
	 * Assert that one million GUIDs can be generated in their String-ified form
	 * in under 10 seconds.
	 *  
	 */
	public void testPerformance()
	{
		long start = System.currentTimeMillis();
		GUID guid = null;

		for (int i = 0; i < performanceTestCount; ++i)
		{
			guid = new GUID();
			guid.toString();
		}
		long end = System.currentTimeMillis();

		assertTrue((end - start) < performanceTestMaxTime);
	}

	/*
	 * Part of a test to see if duplicate GUIDs can be generated on multiple
	 * threads. This test is designed to simulate many free threads, as would be
	 * running on an app server. Before making modifications to this test,
	 * please evaluate whether those changes would create synchronization
	 * between threads.
	 */
	protected Map totalMap = null;

	protected synchronized void dupThreadResult(Map results)
	{
		if (totalMap == null)
			totalMap = new Hashtable();

		for (Iterator iter = results.keySet().iterator(); iter.hasNext();)
		{
			String key = (String) iter.next();

			if (totalMap.get(key) != null)
				Assert.fail("Duplicate GUID in collective map[" + key + "]");

			totalMap.put(key, results.get(key));
		}
	}

	protected static int duplicateTestCount = 10000; // per thread
	protected static int duplicateThreads = 10;

	/**
	 * Create many GUIDs on many threads and see if there are any duplicates.
	 * Please see the note above on synchronization befor making any changes in
	 * this code (i.e. it may look more complex than necessary but tht is to
	 * avoid synchronization between threads during GUID creation).
	 *  
	 */
	public void testForDuplicates()
	{
		Thread[] threads = new Thread[duplicateThreads];
		ThreadGroup workerThreadGroup = new ThreadGroup("GUIDDup");
		workerThreadGroup.setDaemon(true);

		for (int n = 0; n < duplicateThreads; ++n)
		{
			threads[n] = new Thread(workerThreadGroup, "ThreadDup" + n)
			{
				public void run()
				{
					Hashtable threadMap = new Hashtable(duplicateTestCount);
					System.out.println("Thread [" + this.getName() + "] started");
					long start = System.currentTimeMillis();
					
					for (int i = 0; i < duplicateTestCount; ++i)
					{
						GUID guid = new GUID();
						String guidAsString = guid.toString();
						if (threadMap.get(guidAsString) != null)
							Assert
									.fail("Duplicate GUID [" + guidAsString
											+ "]");
						threadMap.put(guidAsString, guid);
					}
					dupThreadResult(threadMap);
					
					long elapsed = System.currentTimeMillis() - start;
					
					System.out.println("Thread [" + this.getName() + 
							"], created GUID " + duplicateTestCount + 
							" in " + elapsed + " milliseconds, " +
							(double)duplicateTestCount / ((double)elapsed / 1000.0) + " per second.");
					
					synchronized (this)
					{
						this.notifyAll();
					}
				}
			};
		}

		for (int n = 0; n < duplicateThreads; ++n)
			threads[n].start();

		for (int n = 0; n < duplicateThreads; ++n)
			synchronized (threads[n])
			{
				try
				{
					if(threads[n].isAlive())
						threads[n].wait();
				} 
				catch (InterruptedException x)
				{
					Assert.fail(x.getMessage());
				}
			}
		
		System.out.println("Map size = " + totalMap.size());
		Assert.assertTrue(totalMap.size() == (duplicateTestCount * duplicateThreads));
	}
}