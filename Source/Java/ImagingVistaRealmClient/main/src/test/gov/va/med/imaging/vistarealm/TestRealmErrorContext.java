/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 24, 2011
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
package gov.va.med.imaging.vistarealm;

import gov.va.med.imaging.tomcat.vistarealm.RealmErrorContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class TestRealmErrorContext
{

	private final static Logger logger = 
		Logger.getLogger(TestRealmErrorContext.class);
	
	private final static int ITERATIONS = 100;
	private final static int THREADS = 20;
	
	private CountDownLatch waiter = new CountDownLatch(THREADS);
	
	@Test
	public void threadedTester()
	{		
		for(int i = 0; i < THREADS; i++)
		{
			RealmErrorContextThread t = new RealmErrorContextThread("THREAD [" + i + "]");
			t.setDaemon(false);
			t.start();
		}
		
		try
		{
			waiter.await(30, TimeUnit.SECONDS);			
		}
		catch(InterruptedException iX)
		{
			iX.printStackTrace();
			fail(iX.getMessage());			
		}
		assertEquals(0, waiter.getCount());		
	}
	
	class RealmErrorContextThread
	extends Thread
	{
		public RealmErrorContextThread(String name)
		{
			super(name);
		}

		@Override
		public void run()
		{
			logger.info("Starting test on thread [" + getName() + "]");
			
			for(int i = 0; i < ITERATIONS; i++)
			{
				RealmErrorContext.clear();
				String expectedExceptionMessage = "Thread '" + this.getName() + "', iteration '" + i + "' exception message";
				String expectedExceptionClassName = "Thread[" + this.getName() + "], " + i;
				RealmErrorContext.setProperty(RealmErrorContext.realmErrorContextExceptionMessage, expectedExceptionMessage);
				RealmErrorContext.setProperty(RealmErrorContext.realmErrorContextExceptionName, expectedExceptionClassName);
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e)
				{
					fail(e.getMessage());
				}
				assertEquals(expectedExceptionMessage, RealmErrorContext.getProperty(RealmErrorContext.realmErrorContextExceptionMessage));
				assertEquals(expectedExceptionClassName, RealmErrorContext.getProperty(RealmErrorContext.realmErrorContextExceptionName));				
				
				RealmErrorContext.unsetRealmErrorContext();
				assertNull(RealmErrorContext.getProperty(RealmErrorContext.realmErrorContextExceptionMessage));
				assertNull(RealmErrorContext.getProperty(RealmErrorContext.realmErrorContextExceptionName));
				
			}
			
			waiter.countDown();
			logger.info("Test thread [" + getName() + "] complete");
			
		}
		
	}
}
