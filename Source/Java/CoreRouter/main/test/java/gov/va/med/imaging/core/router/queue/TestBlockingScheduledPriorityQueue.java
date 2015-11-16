/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 30, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.imaging.core.router.queue;

import gov.va.med.imaging.core.MockCommandContext;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.router.AbstractCommandImpl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 * 
 */
public class TestBlockingScheduledPriorityQueue extends TestCase
{
	private CommandContext commandContext = new MockCommandContext();

	/**
	 * Test method for
	 * {@link gov.va.med.imaging.core.router.queue.AsynchronousCommandProcessorPriorityBlockingQueue#take()}.
	 */
	public final void testTakeWithScheduledElement()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		AsynchronousCommandProcessorPriorityBlockingQueue queue = new AsynchronousCommandProcessorPriorityBlockingQueue();

		AbstractCommandImpl<String> cmd1 = new MockCommandImpl("One",
				ScheduledPriorityQueueElement.Priority.NORMAL);
		AbstractCommandImpl<String> cmd2 = new MockCommandImpl("Two",
				ScheduledPriorityQueueElement.Priority.HIGH, new Date(System
						.currentTimeMillis() + 2000L));

		queue.add(cmd1);
		queue.add(cmd2);

		try
		{
			MockCommandImpl comparable = ((MockCommandImpl) (queue.poll(3000L,
					TimeUnit.MILLISECONDS)));
			assertNotNull("Timed out waiting for queue element", comparable);
			assertEquals("One", comparable.getName());

			comparable = ((MockCommandImpl) (queue.poll(3000L,
					TimeUnit.MILLISECONDS)));
			assertNotNull("Timed out waiting for queue element", comparable);
			assertEquals("Two", comparable.getName());
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	/**
	 * Test method for
	 * {@link gov.va.med.imaging.core.router.queue.AsynchronousCommandProcessorPriorityBlockingQueue#peek()}.
	 */
	public final void testDelayedTakeWithScheduledElement()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		AsynchronousCommandProcessorPriorityBlockingQueue queue = new AsynchronousCommandProcessorPriorityBlockingQueue();

		long now = System.currentTimeMillis();

		AbstractCommandImpl<String> cmd1 = new MockCommandImpl("One",
				ScheduledPriorityQueueElement.Priority.NORMAL, new Date(now), // immediately
				// accessible
				new Date(now), // immediate processing
				2000L // estimated two second processing
		);
		AbstractCommandImpl<String> cmd2 = new MockCommandImpl("Two",
				ScheduledPriorityQueueElement.Priority.HIGH, new Date(
						now + 1000L), // accessible in 1 second
				new Date(now), // immediate processing
				2000L // estimated two second processing
		);

		queue.add(cmd1);
		queue.add(cmd2);

		// wait for the accessible time of the last element added 'Two' to pass
		try
		{
			Thread.sleep(3000L);
		} catch (InterruptedException iX)
		{
		}

		try
		{
			MockCommandImpl comparable = ((MockCommandImpl) (queue.poll(3000L,
					TimeUnit.MILLISECONDS)));
			assertNotNull("Timed out waiting for queue element", comparable);
			assertEquals("Two", comparable.getName());

			comparable = ((MockCommandImpl) (queue.poll(3000L,
					TimeUnit.MILLISECONDS)));
			assertNotNull("Timed out waiting for queue element", comparable);
			assertEquals("One", comparable.getName());
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	/**
	 * If the duration is -1 then the sort should revert to strictly priority
	 * based
	 */
	public final void testNoDurationSpecified()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		AsynchronousCommandProcessorPriorityBlockingQueue queue = new AsynchronousCommandProcessorPriorityBlockingQueue();

		long now = System.currentTimeMillis();

		AbstractCommandImpl<String> cmd0 = new MockCommandImpl("Zero",
				ScheduledPriorityQueueElement.Priority.LOW, new Date(now), // immediately
				// accessible
				new Date(now), // immediate processing
				-1L // explicit, no estimate provided
		);
		AbstractCommandImpl<String> cmd1 = new MockCommandImpl("One",
				ScheduledPriorityQueueElement.Priority.NORMAL, new Date(now), // immediately
				// accessible
				new Date(now), // immediate processing
				-1L // explicit, no estimate provided
		);
		AbstractCommandImpl<String> cmd2 = new MockCommandImpl("Two",
				ScheduledPriorityQueueElement.Priority.HIGH, new Date(now), // accessible
				// in 1
				// second
				new Date(now), // immediate processing
				-1L // explicit, no estimate provided

		);

		queue.add(cmd0);
		queue.add(cmd1);
		queue.add(cmd2);

		try
		{
			MockCommandImpl comparable = ((MockCommandImpl) (queue.poll(3000L,
					TimeUnit.MILLISECONDS)));
			assertNotNull("Timed out waiting for queue element", comparable);
			assertEquals("Two", comparable.getName());

			comparable = ((MockCommandImpl) (queue.poll(3000L,
					TimeUnit.MILLISECONDS)));
			assertNotNull("Timed out waiting for queue element", comparable);
			assertEquals("One", comparable.getName());

			comparable = ((MockCommandImpl) (queue.poll(3000L,
					TimeUnit.MILLISECONDS)));
			assertNotNull("Timed out waiting for queue element", comparable);
			assertEquals("Zero", comparable.getName());
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	private static final int CAPACITY_TEST = 20000;

	public void testCapacity() throws InterruptedException
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		AsynchronousCommandProcessorPriorityBlockingQueue queue = new AsynchronousCommandProcessorPriorityBlockingQueue();

		for (int n = 0; n < CAPACITY_TEST; ++n)
			queue.add(gov.va.med.imaging.core.MockCoreRouterUtility
					.createMockCommand(commandContext));

		for (int n = 0; n < CAPACITY_TEST; ++n)
			assertNotNull(queue.poll(3000L, TimeUnit.MILLISECONDS));
		
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	private AsynchronousCommandProcessorPriorityBlockingQueue threadCapacityTestQueue = new AsynchronousCommandProcessorPriorityBlockingQueue();
	private static final int CAPACITY_THREADS = 10;

	public void testThreading() throws InterruptedException
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");

		Thread[] capacityThreads = new Thread[CAPACITY_THREADS];
		for (int n = 0; n < capacityThreads.length; ++n)
		{
			capacityThreads[n] = new Thread()
			{
				@Override
				public void run()
				{
					for (int i = 0; i < CAPACITY_TEST / CAPACITY_THREADS; ++i)
						threadCapacityTestQueue
								.add(gov.va.med.imaging.core.MockCoreRouterUtility
										.createMockCommand(commandContext));
				}

			};
			capacityThreads[n].start();
		}

		for (int n = 0; n < CAPACITY_TEST; ++n)
			assertNotNull(threadCapacityTestQueue.poll(3000L,
					TimeUnit.MILLISECONDS));

		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}
}
