/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 6, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.imaging.vistarealm;

import java.lang.reflect.InvocationTargetException;
import gov.va.med.imaging.tomcat.vistarealm.ThreadLocalWeaselUtility;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class ThreadLocalWeaselUtilityTest
	extends TestCase
{
	// 
	ThreadLocal<String> someThreadLocal;
	WorkerThread workerThread;
	Object synchObject = new Object();
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		someThreadLocal = new ThreadLocal<String>();
		someThreadLocal.set("MainThread");
		workerThread = new WorkerThread();
		workerThread.start();
		
		// wait 1 second so that the worker thread can get started and 
		// reach the synch point
		try{Thread.sleep(1000L);}
		catch(InterruptedException iX){return;}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() 
	throws Exception
	{
		someThreadLocal.remove();
		super.tearDown();
	}

	/**
	 * Test method for {@link gov.va.med.imaging.tomcat.vistarealm.ThreadLocalWeaselUtility#get(java.lang.Thread, java.lang.ThreadLocal)}.
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public void testGet() 
	throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		System.out.println( "Main thread says '" + workerThread.getThreadLocalValue() + "'." );
		assertEquals("MainThread", someThreadLocal.get());
		releaseWorkerThread();
		
		ThreadLocalWeaselUtility.set(workerThread, someThreadLocal, "WorkerThreadSet");
		assertEquals("WorkerThreadSet", ThreadLocalWeaselUtility.get(workerThread, someThreadLocal) );
		releaseWorkerThread();
		
		ThreadLocalWeaselUtility.remove(workerThread, someThreadLocal);
		assertEquals(null, ThreadLocalWeaselUtility.get(workerThread, someThreadLocal) );
		
		killWorkerThread();
	}

	/**
	 * 
	 */
	private void killWorkerThread()
	{
		workerThread.hariKari();
		releaseWorkerThread();
	}

	private void releaseWorkerThread()
	{
		synchronized (synchObject)
		{
			synchObject.notifyAll();
		}
	}

	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	class WorkerThread 
	extends Thread
	{
		private boolean sepuku = false;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			while(!sepuku)
			{
				synchronized (synchObject)
				{
					try
					{
						synchObject.wait();
					}
					catch (InterruptedException x)
					{
						x.printStackTrace();
					}
				}
				
				System.out.println( "WorkerThread says '" + someThreadLocal.get() + "'.");
			}
		}

		public void hariKari()
		{
			sepuku = true;
		}
		
		public String getThreadLocalValue()
		{
			return someThreadLocal.get();
		}
	}
}
