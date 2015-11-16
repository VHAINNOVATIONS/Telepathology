package gov.va.med.imaging.core.router.queue;

import gov.va.med.imaging.core.router.AbstractCommandImpl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * This Queue implementation combines a Priority Queue with scheduled availability.
 * Each queue element is marked with optional priority and scheduled availability.
 * If an items scheduled availability has not occurred yet then the item is not available
 * and take() and  
 * 
 * An unbounded blocking queue that uses the same ordering rules as class PriorityQueue once
 * a queue element's accessible time has passed. This queue supplies blocking retrieval 
 * operations. While this queue is logically unbounded, attempted 
 * additions may fail due to resource exhaustion (causing OutOfMemoryError). This class does 
 * not permit null elements. A priority queue relying on natural ordering also does not permit 
 * insertion of non-comparable objects (doing so results in ClassCastException).
 * 
 * This class and its iterator implement all of the optional methods of the Collection and Iterator 
 * interfaces. The Iterator provided in method iterator() is not guaranteed to traverse the elements 
 * of the PriorityBlockingQueue in any particular order. If you need ordered traversal, consider using 
 * Arrays.sort(pq.toArray()). Also, method drainTo can be used to remove some or all elements in priority 
 * order and place them in another collection.
 * 
 * Operations on this class make no guarantees about the ordering of elements with equal priority. If you 
 * need to enforce an ordering, you can define custom classes or comparators that use a secondary key to 
 * break ties in primary priority values. For example, here is a class that applies first-in-first-out 
 * tie-breaking to comparable elements. To use it, you would insert a new FIFOEntry(anEntry) instead of a 
 * plain entry object. 
 * 
 * Whatever the ordering used, the head of the queue is that element which would be removed by a call to 
 * remove() or poll(). In a FIFO queue, all new elements are inserted at the  tail of the queue. Other kinds 
 * of queues may use different placement rules. Every Queue implementation must specify its ordering properties.
 * 
 * This queue is sorted by a combination of element accessibility date, commencement date and priority.
 * The head of this queue is the least element with respect to the specified ordering. 
 *  
 * @author VHAISWBECKEC
 *
 * @param <E>
 */
public class AsynchronousCommandProcessorPriorityBlockingQueue
implements BlockingQueue<AbstractCommandImpl<?>>
{
	//private final ClusterablePriorityBlockingQueue<AbstractCommandImpl<?>> priorityQueue;
	private final PriorityBlockingQueue<AbstractCommandImpl<?>> priorityQueue;
	private final InaccessibleQueueElements scheduledQueue;
	private static final long serialVersionUID = -6399293227643514590L;
	
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Create an instance using the natural ordering of the elements.
	 */
	public AsynchronousCommandProcessorPriorityBlockingQueue()
    {
		//priorityQueue = new ClusterablePriorityBlockingQueue<AbstractCommandImpl<?>>(
		//	10, new ScheduledPriorityQueueElementPriorityComparator() 
		//);
		priorityQueue = new PriorityBlockingQueue<AbstractCommandImpl<?>>(
			10, new ScheduledPriorityQueueElementPriorityComparator() 
		);
		
		scheduledQueue = new InaccessibleQueueElements(priorityQueue);
    }

	/**
	 * Create an instance using a Comparator to establish the priority of the elements.
	 * 
	 * @param initialCapacity
	 * @param comparator
	 */
	public AsynchronousCommandProcessorPriorityBlockingQueue(int initialCapacity)
    {
		//priorityQueue = new ClusterablePriorityBlockingQueue<AbstractCommandImpl<?>>(
		//	initialCapacity, new ScheduledPriorityQueueElementPriorityComparator() 
		//);
		priorityQueue = new PriorityBlockingQueue<AbstractCommandImpl<?>>(
			10, new ScheduledPriorityQueueElementPriorityComparator() 
		);
		scheduledQueue = new InaccessibleQueueElements(priorityQueue);
    }
	
	// ==============================================================================
	// Queue Addition Methods
	//
	// The queue is unbounded so the behavior of add(), put() and offer() is 
	// identical.  add() and its variants are implemented, put() and offer()
	// are pass-through calls to add().
	// ==============================================================================
	
	/**
	 * Add an element to the priority queue, making it immediately accessible.
	 * 
	 * @see java.util.Queue#add(java.lang.Object)
	 */
	@Override
    public boolean add(AbstractCommandImpl<?> e)
    {
    	// if the element's accessible date has passed then immediately add the element to the priority
    	// queue
    	if( e.getAccessibilityDate().after(new Date()) )
    		return scheduledQueue.add(e);
    	else
			return priorityQueue.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends AbstractCommandImpl<?>> c)
    {
		boolean result = true;
		for(Iterator<? extends AbstractCommandImpl<?>> i = c.iterator(); i.hasNext(); )
			result &= add(i.next());
		
		return result;
    }

	
    /**
     * Inserts the specified element into this priority queue. 
     * As the queue is unbounded this method will never block.
     *  
     * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object)
     */
	@Override
    public boolean offer(AbstractCommandImpl<?> e)
    {
		return add(e);
    }
	
	/**
	 * As the queue is unbounded this method always returns immediately.
	 * The timeout and time unit parameters are ignored.
	 * 
	 * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
    public boolean offer(AbstractCommandImpl<?> e, long timeout, TimeUnit unit) 
	throws InterruptedException
    {
		return add(e);
    }

	/**
	 * As the queue is unbounded this method always returns immediately.
	 * 
	 * @see java.util.concurrent.BlockingQueue#put(java.lang.Object)
	 */
	@Override
    public void put(AbstractCommandImpl<?> e) 
	throws InterruptedException
    {
		add(e);
    }

    // ==========================================================================================
    // Element retrieval methods.
    // ==========================================================================================

	@Override
    public AbstractCommandImpl<?> element()
    {
	    return priorityQueue.element();
    }
	
	/**
	 * Retrieves and removes the head of this queue, 
	 * waiting if necessary until an element becomes available.
	 *  
	 * @see java.util.concurrent.BlockingQueue#take()
	 */
	@Override
    public AbstractCommandImpl<?> take() 
	throws InterruptedException
    {
	    return priorityQueue.take();
    }

	/**
	 * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty. 
	 * 
	 * @see java.util.Queue#peek()
	 */
	@Override
    public AbstractCommandImpl<?> peek()
    {
	    return priorityQueue.peek();
    }

	/**
	 * Retrieves and removes the head of this queue, or returns null if this queue is empty. 
	 * 
	 * @see java.util.Queue#poll()
	 */
	@Override
    public AbstractCommandImpl<?> poll()
    {
	    return priorityQueue.poll();
    }

	/**
	 * Retrieves and removes the head of this queue, waiting up to the specified wait 
	 * time if necessary for an element to become available. 
	 * @see java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)
	 */
	@Override
    public AbstractCommandImpl<?> poll(long timeout, TimeUnit unit)
    throws InterruptedException
    {
	    return priorityQueue.poll(timeout, unit);
    }

	/**
	 * Retrieves and removes the head of this queue. 
	 * This method differs from poll only in that it throws an exception if this queue is empty. 
	 * 
	 * @see java.util.Queue#remove()
	 */
	@Override
    public AbstractCommandImpl<?> remove()
    {
	    return priorityQueue.remove();
    }

	/**
	 * Drain all elements whose accessibility date has passed to the specified collection.
	 * 
	 * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection)
	 */
	@Override
    public int drainTo(Collection<? super AbstractCommandImpl<?>> c)
    {
		return priorityQueue.drainTo(c);
    }
	

	/**
	 * Drain up to maxElements elements whose accessibility date has passed to the specified collection.
	 * 
	 * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection, int)
	 */
	
	@Override
    public int drainTo(Collection<? super AbstractCommandImpl<?>> c, int maxElements)
    {
		return priorityQueue.drainTo(c, maxElements);
    }

	/**
	 * Clear the entire queue, regardless of the elements accessibility date.
	 * @see java.util.Collection#clear()
	 */
	@Override
    public void clear()
    {
		scheduledQueue.clear();
		priorityQueue.clear();
    }

	@Override
	@SuppressWarnings("unchecked")
    public boolean contains(Object o)
    {
		if(o instanceof Comparable)
		{
			return scheduledQueue.contains(o) ? true : priorityQueue.contains(o) ? true : false;
		}
		return false;
    }

	@Override
    public boolean containsAll(Collection<?> c)
    {
		for(Iterator<?> i = c.iterator(); i.hasNext(); )
			if(! contains(i.next()))
				return false;
		return true;
    }

	/**
	 * NOTE: even if this method returns false that the elements may not be
	 * accessible (by scheduled time) and therefore peek(), poll(), and remove()
	 * may still return null or throw exceptions
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
    public boolean isEmpty()
    {
	    return priorityQueue.isEmpty() && scheduledQueue.isEmpty();
    }

	/**
	 * Iterate over the collection of all elements regardless of accessibility date.
	 * The elements will be returned from the iterator in:
	 * 1.) order by priority (as determined by Comparator or natural ordering) if the accessibility date has passed
	 * 2.) order by accessibility date
	 * This correlates to the order that the elements would be accessed using take() or remove(), assuming that
	 * no changes were made to the queue during iteration. 
	 * 
	 * @see java.util.Collection#iterator()
	 */
	@Override
    public Iterator<AbstractCommandImpl<?>> iterator()
    {
		return new Iterator<AbstractCommandImpl<?>>()
		{
			private Iterator<AbstractCommandImpl<?>> wrappedIter = priorityQueue.iterator();
			boolean completedPriorityQueue = false;
			
			@Override
            public boolean hasNext()
            {
				boolean result = wrappedIter.hasNext();
				if(!result && !completedPriorityQueue)
				{
					completedPriorityQueue = true;
					wrappedIter = scheduledQueue.iterator();
					result = wrappedIter.hasNext();
				}
	            return result;
            }

			@Override
            public AbstractCommandImpl<?> next()
            {
				AbstractCommandImpl<?> result = wrappedIter.next();
				if(result == null && !completedPriorityQueue)
				{
					completedPriorityQueue = true;
					wrappedIter = scheduledQueue.iterator();
					result = wrappedIter.next();
				}
	            return result;
            }

			@Override
            public void remove()
            {
				wrappedIter.remove();
            }
			
		};
    }

	@Override
    public boolean remove(Object o)
    {
		return priorityQueue.remove(o) || scheduledQueue.remove(o);
    }

	@Override
    public boolean removeAll(Collection<?> c)
    {
	    return false;
    }

	@Override
    public boolean retainAll(Collection<?> c)
    {
	    return false;
    }

	@Override
    public int size()
    {
		return priorityQueue.size() + scheduledQueue.size();
    }

    public int accessibleSize()
    {
		return priorityQueue.size();
    }
    
    /**
     * Return the number of accessible queue elements of at least the 
     * specified priority
     * 
     * @param priority
     * @return
     */
    public int accessibleOfPriority(ScheduledPriorityQueueElement.Priority priority)
    {
    	AbstractCommandImpl<?>[] queueElements = 
    		priorityQueue.toArray(new AbstractCommandImpl<?>[priorityQueue.size()]);
    	
    	int count = 0;
    	for(AbstractCommandImpl<?> queueElement : queueElements)
    		if( queueElement.getPriority().compareTo(priority) >= 0)
    			++count;
    	
    	return count;
    }
	
    /**
     * Return an array of the accessible elements.
     * 
     * @see java.util.Collection#toArray()
     */
	@Override
    public Object[] toArray()
    {
	    return priorityQueue.toArray(new AbstractCommandImpl[priorityQueue.size()]);
    }

	/**
     * Return an array of the accessible elements.
     * 
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
    public <AsynchronousCommandProcessor> AsynchronousCommandProcessor[] toArray(AsynchronousCommandProcessor[] a)
    {
	    return priorityQueue.toArray(a);
    }

	/**
     * Return the remaining capacity of the accessible elements.
	 * 
	 * @see java.util.concurrent.BlockingQueue#remainingCapacity()
	 */
	@Override
    public int remainingCapacity()
    {
	    return priorityQueue.remainingCapacity();
    }

	/**
	 * A Comparator that uses the target date and the priority to sort
	 * the ScheduledPriorityQueueElement instances in the priority queue.
	 * The ordering is done first by the target date (lower dates compare higher), 
	 * then the priority (higher priority compares higher)
	 * used when tasks are estimated to overlap in execution.
	 * 
	 * "The head of this queue is the least element with respect to the specified ordering." 
	 * "Whatever the ordering used, the head of the queue is that element which would be 
	 * removed by a call to remove() or poll()."
	 * 
	 * @author VHAISWBECKEC
	 */
	class ScheduledPriorityQueueElementPriorityComparator
	implements Comparator<AbstractCommandImpl<?>>
	{
		/*
		 * Compares its two arguments for order. 
		 * Returns:
		 * negative integer - the first argument is less than the second item 
		 * zero - the first argument is equal to the second item
		 * positive integer - the first argument is greater than the second item 
		 */
		@Override
        public int compare(AbstractCommandImpl<?> element1, AbstractCommandImpl<?> element2)
        {
			long processingCommencementDateDelta = element1.getProcessingCommencementTargetDate().getTime() - 
				element2.getProcessingCommencementTargetDate().getTime();
			int result = 0;
			
			// if processingCommencementDateDelta < 0 then element1 target date is before element2 target date
			// element1 should be sorted as less than element2 unless element1 processing
			// is estimated to overlap with element2 processing (assuming synchronous execution)
			// and element2 has a higher priority
			if(processingCommencementDateDelta < 0)
			{
				logger.debug(element1.toString() + " commencement date is before " + element2.toString() + " commencement date" );

				// in the default case the estimated duration is NOT provided, so cannot be used in the
				// priority calculation, sort strictly by priority
				if(element1.getProcessingDurationEstimate() < 0)
				{
					result = -1 * ( element1.getPriority().compareTo(element2.getPriority()) );
				}
				else
				{
					Date element1EstimatedCompletionDate = 
						new Date(element1.getProcessingCommencementTargetDate().getTime() + element1.getProcessingDurationEstimate());
					logger.debug(element1.toString() + " estimated completion date is " + element1EstimatedCompletionDate );
					
					// if the estimated completion date of element1 is greater than the target date
					// of element2
					// and
					// element2 is higher priority
					// then element2 is sorted closer to the head (-1)
					if( element1EstimatedCompletionDate.compareTo(element2.getProcessingCommencementTargetDate()) > 0 &&
							element2.getPriority().compareTo(element1.getPriority()) > 0)
						result = -1;  // element2 is closer to the head than element1
					else
						result = 1;
				}
			}
			// if targetDateDelta > 0 then element1 target date is after element2 target date
			// element2 should be sorted as closer to head than element1 unless element2 processing
			// is estimated to overlap with element1 processing (assuming synchronous execution)
			// and element1 has a higher priority
			else if(processingCommencementDateDelta > 0)
			{
				logger.debug(element1.toString() + " commencement date is before " + element2.toString() + " commencement date" );
				
				// in the default case the estimated duration is NOT provided, so cannot be used in the
				// priority calculation, sort strictly by priority
				if(element2.getProcessingDurationEstimate() < 0)
				{
					result = -1 * ( element1.getPriority().compareTo(element2.getPriority()) );
				}
				else
				{
					Date element2EstimatedCompletionDate = 
						new Date(element2.getProcessingCommencementTargetDate().getTime() + element2.getProcessingDurationEstimate());
					logger.debug(element2.toString() + " estimated completion date is " + element2EstimatedCompletionDate );
					
					// if the estimated completion date of element2 is greater than the target date
					// of element1
					// and
					// element1 is higher priority
					// then element1 is sorted closer to the head (-1)
					if( element2EstimatedCompletionDate.compareTo(element1.getProcessingCommencementTargetDate()) > 0 &&
							element1.getPriority().compareTo(element2.getPriority()) > 0)
						result = -1;  // element1 is greater than element2
					else
						result = 1;
				}
			}
			else
			{
				logger.debug(element1.toString() + " commencement date is equal to " + element2.toString() + " commencement date" );
				
				// if the processing commencement dates are the same then
				// sort by priority only
				// enum.compareTo() = "Returns a negative integer, zero, or a positive integer as 
				// this object is less than, equal to, or greater than the specified object. 
				// Enum constants are only comparable to other enum constants of the same enum type. 
				// The natural order implemented by this method is the order in which the constants are declared."
				// i.e. if element1 priority compared to element2 priority is -1 means that element1
				// priority is lower (since priority is in increasing order) and element1 should come later
				// in the processing (i.e. closer to the tail) therefore return 1
				result = -1 * ( element1.getPriority().compareTo(element2.getPriority()) );
			}
			
			logger.debug(
				element1.toString() + " is " + 
				(result>0 ? "lower priority" : result<0 ? "higher priority" : "equal priority") + 
				" relative to " + element2.toString());
			
			return result;
        }
	}
	
	/**
	 * The Comparator that orders the scheduled queue by accessible date
	 * @author VHAISWBECKEC
	 *
	 */
	private class ScheduledPriorityQueueElementAccessibleDateComparator
	implements Comparator<ScheduledPriorityQueueElement>
	{
		@Override
        public int compare(ScheduledPriorityQueueElement o1, ScheduledPriorityQueueElement o2)
        {
			int result = o1.getAccessibilityDate().compareTo(o2.getAccessibilityDate());
			return result;
        }
	}
	
	/**
	 * A queue that automatically adds elements to the given destination
	 * once the accessibility date of its member elements is reached. 
	 */
	private class InaccessibleQueueElements
	{
		private PriorityQueue<AbstractCommandImpl<?>> accessibleDatePriorityQueue;
		private static final long serialVersionUID = 1L;
		private final ScheduleQueueTransferThread transferThread;
		private final Queue<AbstractCommandImpl<?>> destination;
		
		InaccessibleQueueElements(Queue<AbstractCommandImpl<?>> destination)
		{
			accessibleDatePriorityQueue = 
				new PriorityQueue<AbstractCommandImpl<?>>(10, new ScheduledPriorityQueueElementAccessibleDateComparator());
			this.destination = destination;
			transferThread = new ScheduleQueueTransferThread();
			transferThread.start();
		}
		
        public int size()
        {
	        return accessibleDatePriorityQueue.size();
        }

		public boolean remove(Object o)
        {
	        return accessibleDatePriorityQueue.remove(o);
        }

		public Iterator<AbstractCommandImpl<?>> iterator()
        {
	        return accessibleDatePriorityQueue.iterator();
        }

		public boolean isEmpty()
        {
	        return accessibleDatePriorityQueue.isEmpty();
        }

		public boolean contains(Object o)
        {
        	return accessibleDatePriorityQueue.contains(o);
        }

		public void clear()
        {
        	accessibleDatePriorityQueue.clear();
        }
		
		public ScheduledPriorityQueueElement peek()
		{
			return accessibleDatePriorityQueue.peek();
		}

		public ScheduledPriorityQueueElement poll()
		{
			return accessibleDatePriorityQueue.poll();
		}
		
		public boolean add(AbstractCommandImpl<?> e)
        {
			logger.info("Adding new scheduled queue element");
	        boolean result = accessibleDatePriorityQueue.add(e);
	        newScheduledElementNotification();
	        return result;
        }

		// get the date of the first element in the queue, the closest date in
		// the future
		private void newScheduledElementNotification()
		{
			Date nextAccessibleElementDate = accessibleDatePriorityQueue.peek().getAccessibilityDate();
			transferThread.nextScheduledElementNotification(nextAccessibleElementDate);
		}
		
		private void transferEligibleScheduledElements()
		{
			// remove element from the scheduled queue whose accessibility date has passed
			// and add them to the destination
			for(ScheduledPriorityQueueElement element = accessibleDatePriorityQueue.peek();
				element != null && element.getAccessibilityDate().before(new Date());
				element = accessibleDatePriorityQueue.peek())
			{
				logger.info("Transfering scheduled element to priority queue");
				destination.add(accessibleDatePriorityQueue.poll());
			}
		}
		
		private class ScheduleQueueTransferThread
		extends Thread
		{
			private ScheduleQueueTransferThread()
	        {
		        super();
		        this.setDaemon(true);
		        this.setName("ScheduleQueueTransferThread");
	        }

			void nextScheduledElementNotification(Date date)
			{
				synchronized(this)
				{
					logger.info("Transfer thread being notified of new scheduled queue elements");
					notifyAll();
				}
			}
			
			@Override
	        public void run()
	        {
				while(true)
				{
					try
                    {
						synchronized(this)
						{
							// if there is nothing left in the queue, schedule the next execution for infinity
							if(accessibleDatePriorityQueue.isEmpty())
							{
								logger.info("Transfer thread waiting forever for new scheduled queue elements");
								wait();
							}
							else
							{
								// if this is the first time through the loop, assume we have to run
								long timeout = accessibleDatePriorityQueue.peek().getAccessibilityDate().getTime() - (new Date()).getTime();
								if( timeout <= 0L ) timeout = 1L;		// a wait of 0L means wait forever and we do not want that!
								logger.info("Transfer thread waiting (" + timeout + ") for existing scheduled queue elements");
								
								wait(timeout);
							}
							
							transferEligibleScheduledElements();
						}
                    } 
					catch (InterruptedException e)
                    {
	                    e.printStackTrace();
                    }
				}
	        }
		}
	}

}
