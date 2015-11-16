/**
 * 
 */
package gov.va.med.imaging.core.router.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestClusterablePriorityBlockingQueue 
extends TestCase
{
	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#ClusterablePriorityBlockingQueue()}.
	 */
	public void testClusterablePriorityBlockingQueue()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		assertNotNull(queue);
		assertEquals(0, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#ClusterablePriorityBlockingQueue(java.util.Collection)}.
	 */
	public void testClusterablePriorityBlockingQueueCollectionOfQextendsE()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		Collection<MockElement> c = new ArrayList<MockElement>();
		
		c.add(new MockElement(0));
		c.add(new MockElement(1));
		c.add(new MockElement(2));
		
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>(c);
		
		assertNotNull(queue);
		assertEquals(3, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#ClusterablePriorityBlockingQueue(int)}.
	 */
	public void testClusterablePriorityBlockingQueueInt()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>(1000);
		
		assertNotNull(queue);
		assertEquals(0, queue.size());
		assertTrue(queue.remainingCapacity() >= 1000);
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#ClusterablePriorityBlockingQueue(int, java.util.Comparator)}.
	 */
	public void testClusterablePriorityBlockingQueueIntComparatorOfQsuperE()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		ClusterablePriorityBlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>(1000, new MockElementInverseComparator());
		
		assertNotNull(queue);
		assertEquals(0, queue.size());
		assertTrue(queue.remainingCapacity() >= 1000);
		assertNotNull(queue.comparator());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}
	
	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#clear()}.
	 */
	public void testClear()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		queue.add(new MockElement(0));
		queue.add(new MockElement(1));
		queue.add(new MockElement(2));
		queue.add(new MockElement(3));
		queue.add(new MockElement(4));
		
		queue.clear();
		assertEquals(0, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}
	
	public void testAddE()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		for(int n=0; n<100; ++n)
			queue.add(new MockElement(n));
		
		assertEquals(100, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	public void testAddEThreaded()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();
		
		Addster[] addsters = new Addster[100];
		for(int threads = 0; threads < 100; ++threads)
			addsters[threads] = new Addster<MockElement>(queue, 0L, 1000L, factory);
		for(Addster addster : addsters)
			addster.start();

		boolean anyAlive = true;
		
		do
		{
			try{Thread.sleep(1000);} 
			catch (InterruptedException x){x.printStackTrace();}
	
			anyAlive = false;
			for(Addster addster : addsters)
				anyAlive |= addster.isAlive();
		} while(anyAlive);		
		
		assertEquals(100000, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}
	
	public void testClearThreaded()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		for(int n=0; n<100; ++n)
			queue.add(new MockElement(n));

		Pollster<MockElement> p1 = new Pollster<MockElement>(queue, 100, 100);
		Pollster<MockElement> p2 = new Pollster<MockElement>(queue, 100, 100);
		
		p1.start();
		p2.start();
		
		queue.clear();
		assertEquals(0, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	public void testOfferE()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		for(int n=0; n<100; ++n)
			queue.offer(new MockElement(n));
		
		assertEquals(100, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	public void testOfferEThreaded()
	{
		System.out.println("Test " + this.getName() + " starting =========================================================");
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();
		
		Offerster[] offersters = new Offerster[100];
		for(int threads = 0; threads < 100; ++threads)
			offersters[threads] = new Offerster<MockElement>(queue, 0L, 1000L, factory);
		for(Offerster offerster : offersters)
			offerster.start();

		boolean anyAlive = true;
		
		do
		{
			try{Thread.sleep(1000);} 
			catch (InterruptedException x){x.printStackTrace();}
	
			anyAlive = false;
			for(Offerster offerster : offersters)
				anyAlive |= offerster.isAlive();
		} while(anyAlive);		
		
		assertEquals(100000, queue.size());
		System.out.println("Test " + this.getName() + " finishing =========================================================");
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#offer(java.lang.Object, long, java.util.concurrent.TimeUnit)}.
	 */
	public void testOfferELongTimeUnit()
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();
		
		Offerster[] addsters = new Offerster[100];
		for(int threads = 0; threads < 100; ++threads)
			addsters[threads] = new Offerster<MockElement>(queue, 0L, 1000L, factory, 10, TimeUnit.MILLISECONDS);
		for(Offerster addster : addsters)
			addster.start();

		boolean anyAlive = true;
		
		do
		{
			try{Thread.sleep(1000);} 
			catch (InterruptedException x){x.printStackTrace();}
	
			anyAlive = false;
			for(Offerster addster : addsters)
				anyAlive |= addster.isAlive();
		} while(anyAlive);		
		
		assertEquals(100000, queue.size());
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#poll()}.
	 */
	public void testPoll()
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();

		assertNull( queue.poll() );
		queue.add(factory.create());
		assertNotNull( queue.poll() );
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#poll(long, java.util.concurrent.TimeUnit)}.
	 * @throws InterruptedException 
	 */
	public void testPollLongTimeUnit() 
	throws InterruptedException
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();

		long begin = System.currentTimeMillis();
		assertNull( queue.poll(1, TimeUnit.SECONDS) );
		assertTrue(System.currentTimeMillis() - begin > 900);
		queue.add(factory.create());
		assertNotNull( queue.poll() );
	}

	public void testPollMultithread() throws InterruptedException
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();

		assertNull( queue.poll() );
		Pollster<MockElement>[] pollsters = new Pollster[100];
		Addster<MockElement> addster = new Addster<MockElement>(queue, 0L, 1000L, factory);
		addster.start();
		
		for(int n=0; n<pollsters.length; ++n)
			pollsters[n] = new Pollster<MockElement>(queue, 100, 10);
		
		for(Pollster<MockElement> pollster : pollsters)
			pollster.start();
		
		Thread.sleep(30000L);
		
		assertEquals(0, queue.size());
	}
	
	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#put(java.lang.Object)}.
	 * @throws InterruptedException 
	 */
	public void testPut() 
	throws InterruptedException
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();

		for(int n=0; n<100; ++n)
			queue.put(factory.create());
		
		for(int n=0; n<100; ++n)
			assertNotNull(queue.poll());
	}

	public void testPutMultithread() 
	throws InterruptedException
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();

		assertNull( queue.poll() );
		Putster[] pollsters = new Putster[100];
		
		for(int n=0; n<pollsters.length; ++n)
			pollsters[n] = new Putster(queue, 100, 10, factory);
		
		for(Putster pollster : pollsters)
			pollster.start();
		
		Thread.sleep(3000L);
		
		assertEquals(1000, queue.size());
		for(int n=0; n<1000; ++n)
			assertNotNull(queue.poll());
	}
	
	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#remainingCapacity()}.
	 * @throws InterruptedException 
	 */
	public void testRemainingCapacity() 
	throws InterruptedException
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		assertEquals(Integer.MAX_VALUE, queue.remainingCapacity());
		
		MockElementFactory factory = new MockElementFactory();

		assertNull( queue.poll() );
		Putster[] pollsters = new Putster[100];
		
		for(int n=0; n<pollsters.length; ++n)
			pollsters[n] = new Putster(queue, 100, 10, factory);
		
		for(Putster pollster : pollsters)
			pollster.start();
		
		Thread.sleep(3000L);
		assertEquals(Integer.MAX_VALUE, queue.remainingCapacity());
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.queue.ClusterablePriorityBlockingQueue#take()}.
	 * @throws InterruptedException 
	 */
	public void testTake() 
	throws InterruptedException
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		MockElementFactory factory = new MockElementFactory();

		assertNull( queue.poll() );
		Putster[] pollsters = new Putster[100];
		
		for(int n=0; n<pollsters.length; ++n)
			pollsters[n] = new Putster(queue, 100, 10, factory);
		
		for(Putster pollster : pollsters)
			pollster.start();

	
		Takester[] takesters = new Takester[100];
		
		for(int n=0; n<takesters.length; ++n)
			takesters[n] = new Takester(queue, 100, 10);
		
		for(Takester takester : takesters)
			takester.start();
		
		Thread.sleep(3000L);
		
		assertEquals(0, queue.size());
	}

	/**
	 * Test method for {@link java.util.PriorityQueue#peek()}.
	 * @throws InterruptedException 
	 */
	public void testPeek() 
	throws InterruptedException
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();

		assertNull( queue.peek() );
		
		queue.put(factory.create());
		assertNotNull(queue.peek());

		queue.take();
		assertNull( queue.peek() );
	}

	/**
	 * Test method for {@link java.util.AbstractQueue#addAll(java.util.Collection)}.
	 */
	public void testAddAll()
	{
		BlockingQueue<MockElement> queue = new ClusterablePriorityBlockingQueue<MockElement>();
		Collection<MockElement> c = new ArrayList<MockElement>();
		
		MockElementFactory factory = new MockElementFactory();
		for(int n=0; n<100; ++n)
			c.add(factory.create());
		
		queue.addAll(c);
		
		assertEquals(100, queue.size());
	}

	/**
	 * An abstract Runnable class that just does some queue operation repeatedly
	 */
	abstract class AbstractQueueOperation<E> 
	extends Thread
	{
		private final BlockingQueue<E> queue;
		private final long delay;
		private long iterations;
		private boolean living = true;
		
		AbstractQueueOperation(BlockingQueue<E> queue, long delay, long iterations)
		{
			this.queue = queue;
			this.delay = delay;
			this.iterations = iterations;
		}
	
		void kill()
		{
			living = false;
		}
		
		protected BlockingQueue<E> getQueue()
		{
			return this.queue;
		}

		@Override
		public void run()
		{
			for(; living && iterations > 0; iterations--)
			{
				try
				{
					Thread.sleep(delay);
					doOperation();
				} 
				catch (InterruptedException x)
				{
					x.printStackTrace();
				}
			}
		}

		/**
		 * 
		 */
		protected abstract void doOperation();
	}

	// ------------------------------------------------------------------------------------------------------------------
	// 
	// ------------------------------------------------------------------------------------------------------------------
	
	class Pollster<E> 
	extends AbstractQueueOperation<E>
	{
		public Pollster(BlockingQueue<E> queue, long delay, long iterations)
		{
			super(queue, delay, iterations);
		}

		@Override
		protected void doOperation()
		{
			getQueue().poll();
		}
	}
	
	class Addster<E> 
	extends AbstractQueueOperation<E>
	{
		private final ElementFactory<E> elementFactory;
		
		public Addster(BlockingQueue<E> queue, long delay, long iterations, ElementFactory<E> elementFactory)
		{
			super(queue, delay, iterations);
			this.elementFactory = elementFactory;
		}

		@Override
		protected void doOperation()
		{
			getQueue().add( elementFactory.create() );
		}
	}
	
	class Putster<E> 
	extends AbstractQueueOperation<E>
	{
		private final ElementFactory<E> elementFactory;
		
		public Putster(BlockingQueue<E> queue, long delay, long iterations, ElementFactory<E> elementFactory)
		{
			super(queue, delay, iterations);
			this.elementFactory = elementFactory;
		}

		@Override
		protected void doOperation()
		{
			try
			{
				getQueue().put( elementFactory.create() );
			} 
			catch (InterruptedException x)
			{
				x.printStackTrace();
				fail();
			}
		}
	}
	
	class Takester<E> 
	extends AbstractQueueOperation<E>
	{
		public Takester(BlockingQueue<E> queue, long delay, long iterations)
		{
			super(queue, delay, iterations);
		}

		@Override
		protected void doOperation()
		{
			try
			{
				getQueue().take( );
			} 
			catch (InterruptedException x)
			{
				x.printStackTrace();
				fail();
			}
		}
	}
	
	class Offerster<E> 
	extends AbstractQueueOperation<E>
	{
		private final ElementFactory<E> elementFactory;
		private final long wait;
		private final TimeUnit timeUnit;
		
		public Offerster(BlockingQueue<E> queue, long delay, long iterations, ElementFactory<E> elementFactory)
		{
			super(queue, delay, iterations);
			this.elementFactory = elementFactory;
			this.wait = 0;
			this.timeUnit = TimeUnit.MILLISECONDS;
		}

		public Offerster(BlockingQueue<E> queue, long delay, long iterations, ElementFactory<E> elementFactory, long wait, TimeUnit timeUnit)
		{
			super(queue, delay, iterations);
			this.elementFactory = elementFactory;
			this.wait = wait;
			this.timeUnit = timeUnit;
		}

		@Override
		protected void doOperation()
		{
			try
			{
				getQueue().offer( elementFactory.create(), wait, timeUnit );
			} 
			catch (InterruptedException x)
			{
				x.printStackTrace();
				fail();
			}
		}
	}
	
	interface ElementFactory<E>
	{
		E create();
	}
	
	class MockElement
	implements Comparable<MockElement>
	{
		private final int value;
		
		
		MockElement(int value)
		{
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public int getValue()
		{
			return this.value;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + this.value;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final MockElement other = (MockElement) obj;
			if (this.value != other.value)
				return false;
			return true;
		}

		/**
		 * Compares this object with the specified object for order. 
		 * Returns a negative integer, zero, or a positive integer as 
		 * this object is less than, equal to, or greater than the specified 
		 * object.
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(MockElement that)
		{
			return this.value - that.value;
		}
	}
	
	class MockElementInverseComparator
	implements Comparator<MockElement>
	{
		@Override
		public int compare(MockElement o1, MockElement o2)
		{
			return -1 * (o1.value - o2.value);
		}
		
	}
	
	class MockElementFactory
	implements ElementFactory<MockElement>
	{
		int serialNumber = 0;
		
		@Override
		public synchronized MockElement create()
		{
			return new MockElement(serialNumber++);
		}
		
	}
	
}
