/**
 * 
 */
package gov.va.med.imaging.core.router.queue;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author vhaiswbeckec
 * 
 * A Terracotta compatible clusterable blocking queue.  Barring bugs, this is a drop-in for 
 * java.util.concurrent.PriorityBlockingQueue with the exception that it does not derive from
 * the same parents as that class.
 * 
 * From the javadoc for java.util.concurrent.PriorityBlockingQueue
 * An unbounded blocking queue that uses the same ordering rules as class PriorityQueue and 
 * supplies blocking retrieval operations. While this queue is logically unbounded, attempted 
 * additions may fail due to resource exhaustion (causing OutOfMemoryError). This class does 
 * not permit null elements. A priority queue relying on natural ordering also does not permit 
 * insertion of non-comparable objects (doing so results in ClassCastException).
 *
 * @See java.util.concurrent.PriorityBlockingQueue
 *
 */
public class ClusterablePriorityBlockingQueue<E> 
extends PriorityQueue<E>
implements BlockingQueue<E>, Iterable<E>, Collection<E>, Queue<E>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public ClusterablePriorityBlockingQueue()
	{
		super();
	}
	
	public ClusterablePriorityBlockingQueue(Collection<? extends E> c)
	{
		super(c);
	}
	
	public ClusterablePriorityBlockingQueue(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	public ClusterablePriorityBlockingQueue(int initialCapacity, Comparator<? super E> comparator)
	{
		super(initialCapacity, comparator);
	}

	/* ============================================================================================
	 * Implementation of BlockingQueue methods
	 * A Queue that additionally supports operations that wait for the queue to become non-empty when 
	 * retrieving an element, and wait for space to become available in the queue when storing an element.
	 * 
	 * BlockingQueue methods come in four forms, with different ways of handling operations that cannot 
	 * be satisfied immediately, but may be satisfied at some point in the future: 
	 * one throws an exception, 
	 * the second returns a special value (either null or false, depending on the operation), 
	 * the third blocks the current thread indefinitely until the operation can succeed, 
	 * and the fourth blocks for only a given maximum time limit before giving up. 
	 * These methods are summarized in the following table:
	 * 
	 * 				Throws exception	Special value		Blocks			Times out
	 * Insert		add(e)				offer(e)			put(e) 			offer(e, time, unit)
	 * Remove		remove()			poll()				take()			poll(time, unit)
	 * Examine		element()			peek()				not applicable	not applicable
	 * 
	 * ============================================================================================ */
	
	/**
	 * Removes at most the given number of available elements from this queue and adds them to the given collection.
	 * 
	 * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection, int)
	 */
	@Override
	public synchronized int drainTo(Collection<? super E> c, int maxElements)
	{
		int count=0;
		for(; count < maxElements; ++count)
			try{ c.add(this.remove()); }
			catch(NoSuchElementException nseX){break;}
			
		return count;
	}

	/**
	 * Removes all available elements from this queue and adds them to the given collection.
	 * 
	 * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection)
	 */
	@Override
	public int drainTo(Collection<? super E> c)
	{
		return this.drainTo(c, Integer.MAX_VALUE);
	}

	/**
	 * Inserts the specified element into this queue if it is possible to do so immediately without 
	 * violating capacity restrictions, returning true upon success and false if no space is currently available.
	 * This queue is of unlimited capacity so this method will always return true
	 * Equivalent to add(e)
	 *  
	 * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object)
	 */
	@Override
	public synchronized boolean offer(E e)
	{
		boolean result = super.offer(e);
		
		notifyAll();		// notify threads that may be waiting for a new item in the queue
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
	throws InterruptedException
	{
		return offer(e);
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#put(java.lang.Object)
	 */
	@Override
	public void put(E e) 
	throws InterruptedException
	{
		offer(e);
	}

	/**
	 * Retrieves and removes the head of this queue, or returns null if this queue is empty
	 * 
	 * @see java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public synchronized E poll() 
	{
		return super.poll();
	}
	
	/**
	 * Retrieves and removes the head of this queue, waiting up to the specified wait time if necessary 
	 * for an element to become available.
	 * 
	 * @see java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public E poll(long timeout, TimeUnit unit) 
	throws InterruptedException
	{
		timeout = Math.max(timeout, 0);		// must be greater than or equal to 0
		
		long milliseconds = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long expiration = System.currentTimeMillis() + milliseconds;
		
		E e = null;
		
		synchronized (this)
		{
			e = this.poll();
			while(e == null && System.currentTimeMillis() < expiration)
			{
				long wait = expiration - System.currentTimeMillis();
				wait = Math.max(0, wait);
				wait( wait );
				e = this.poll();
			}			
		}
		
		return e;
	}

	/**
	 * Returns the number of additional elements that this queue can ideally (in the absence of memory or 
	 * resource constraints) accept without blocking, or Integer.MAX_VALUE if there is no intrinsic limit.
	 *  
	 * This implementation always returns Integer.MAX_VALUE
	 * 
	 * @see java.util.concurrent.BlockingQueue#remainingCapacity()
	 */
	@Override
	public int remainingCapacity()
	{
		return Integer.MAX_VALUE;
	}

	/**
	 * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
	 * 
	 * @see java.util.concurrent.BlockingQueue#take()
	 */
	@Override
	public E take() 
	throws InterruptedException
	{
		E e = null;
		
		synchronized (this)
		{
			for( e = this.poll(); e == null; e = this.poll())
				wait();
		}
		
		return e;
	}

	/**
	 * @see java.util.PriorityQueue#clear()
	 */
	@Override
	public synchronized void clear()
	{
		super.clear();
	}

	/**
	 * Removes a single instance of the specified element from this queue, if it is present. 
	 * More formally, removes an element e such that o.equals(e), if this queue contains 
	 * one or more such elements. Returns true if and only if this queue contained the specified 
	 * element (or equivalently, if this queue changed as a result of the call).
	 *  
	 * @see java.util.PriorityQueue#remove(java.lang.Object)
	 */
	@Override
	public synchronized boolean remove(Object o)
	{
		return super.remove(o);
	}

	/**
	 * 
	 * @see java.util.PriorityQueue#toArray()
	 */
	@Override
	public synchronized Object[] toArray()
	{
		return super.toArray();
	}

	/**
	 * @see java.util.PriorityQueue#toArray(T[])
	 */
	@Override
	public synchronized <T> T[] toArray(T[] a)
	{
		return super.toArray(a);
	}

	
}
