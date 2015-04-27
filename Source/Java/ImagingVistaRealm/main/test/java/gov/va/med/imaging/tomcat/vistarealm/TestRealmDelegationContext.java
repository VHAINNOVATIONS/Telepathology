/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestRealmDelegationContext
extends TestCase
{
	private final static int THREADS = 100;
	private final static int ITERATIONS = 100;
	
	private CountDownLatch count = new CountDownLatch(THREADS);
	
	public void testThreadedUsage()
	{
		for(int threadIndex=0; threadIndex < THREADS; ++threadIndex)
		{
			WorkerThread wt = new WorkerThread("TEST" + threadIndex);
			wt.setDaemon(false);
			wt.start();
		}
		try{count.await();}catch (InterruptedException x){x.printStackTrace(); fail(x.getMessage());}
	}

	class WorkerThread 
	extends Thread
	{
		/**
		 * @param name
		 */
		public WorkerThread(String name)
		{
			super(name);
		}

		@Override
		public void run()
		{
			System.out.println("Thread '" + this.getName() + "' starting.");
			for(int index=0; index < ITERATIONS; ++index)
			{
				assertNotNull(RealmDelegationContext.getRealmDelegationProperties());
				String value = this.getName() + Integer.toHexString(index);
				RealmDelegationContext.getRealmDelegationProperties().put( this.getName(), value );
				try{Thread.sleep(10);}catch(InterruptedException iX){iX.printStackTrace();fail(iX.getMessage());}
				assertEquals(value, RealmDelegationContext.getRealmDelegationProperties().get(this.getName()) );
				RealmDelegationContext.unsetRealmDelegationContext();
				assertNull(RealmDelegationContext.getRealmDelegationProperties().get(this.getName()));
			}
			count.countDown();
			System.out.println("Thread '" + this.getName() + "' complete.");
		}
	}
}
