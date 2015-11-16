package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.GUID;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.MutableNamedObject;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * A collection of tests for PersistentSet, all testing concurrency.
 * These do not test the correctness of the results as those are order
 * of operation dependent.  These tests look for lockups when multiple threads
 * are doing CRUD on a PersistentSet.
 * 
 * @author VHAISWBECKEC
 *
 */
public class TestPersistentSet 
extends TestCase
{
	public final void testClearWhileReading()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<? extends MutableNamedObject> set = generatePopulatedSet(100);
			List<String> namesToAccess = extractNames(set, 1);	// get all of the names
			
			AccessWorkerThread accessWorker = new AccessWorkerThread(set, namesToAccess, delay);
			ClearSetThread clearSetWorker = new ClearSetThread(set);
			
			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			
			accessWorker.start();
			clearSetWorker.start();
			while( (accessWorker.isAlive() || clearSetWorker.isAlive()) && now < start + waitForCompletion)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			
			if( now > start + waitForCompletion)
				fail( generateMessage(set, accessWorker, clearSetWorker) );
		}
	}
	
	public final void testClearWhileWriting()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			
			AddWorkerThread addWorker = new AddWorkerThread(set, 100, delay);
			ClearSetThread clearSetWorker = new ClearSetThread(set);
			
			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			
			addWorker.start();
			clearSetWorker.start();
			while( (addWorker.isAlive() || clearSetWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, addWorker, clearSetWorker) );
		}
	}
	
	public final void testClearWhileRemoving()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			List<String> namesToDelete = extractNames(set, 2);	// get every other name
			
			DeleteWorkerThread deleteWorker = new DeleteWorkerThread(set, namesToDelete, delay);
			ClearSetThread clearSetWorker = new ClearSetThread(set);
			
			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			
			deleteWorker.start();
			clearSetWorker.start();
			while( (deleteWorker.isAlive() || clearSetWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, deleteWorker, clearSetWorker) );
		}
	}

	public final void testRemoveWhileRemoving()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			List<String> namesToDelete = extractNames(set, 2);	// get every other name
			
			DeleteWorkerThread deleteWorker1 = new DeleteWorkerThread(set, namesToDelete, delay);
			DeleteWorkerThread deleteWorker2 = new DeleteWorkerThread(set, namesToDelete, delay);
			
			deleteWorker2.start();
			deleteWorker1.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (deleteWorker1.isAlive() || deleteWorker2.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, deleteWorker1, deleteWorker2) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	public final void testRemoveWhileAdding()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			List<String> namesToDelete = extractNames(set, 2);	// get every other name
			
			AddWorkerThread addWorker = new AddWorkerThread(set, 100, 2L);
			DeleteWorkerThread deleteWorker = new DeleteWorkerThread(set, namesToDelete, delay);
			
			deleteWorker.start();
			addWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (addWorker.isAlive() || deleteWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, addWorker, deleteWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}

	public final void testRemoveWhileAccessing()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;

		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			List<String> namesToDelete = extractNames(set, 2);	// get every other name
			
			List<String> namesToAccess = extractNames(set, 1);	// get all of the names
			AccessWorkerThread accessWorker = new AccessWorkerThread(set, namesToAccess, delay);
			DeleteWorkerThread deleteWorker = new DeleteWorkerThread(set, namesToDelete, delay);
			
			deleteWorker.start();
			accessWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (accessWorker.isAlive() || deleteWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, accessWorker, deleteWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}

	public final void testContainsAllWhileReading()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			Collection<? extends MutableNamedObject> testCollection = extractSet(set, 2);	// get every other name
			
			List<String> namesToAccess = extractNames(set, 1);	// get all of the names
			AccessWorkerThread accessWorker = new AccessWorkerThread(set, namesToAccess, delay);
			ContainsAllThread containsAllWorker = new ContainsAllThread(set, testCollection);
			
			accessWorker.start();
			containsAllWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (accessWorker.isAlive() || containsAllWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, accessWorker, containsAllWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	public final void testContainsAllWhileWriting()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			Collection<? extends MutableNamedObject> testCollection = extractSet(set, 2);	// get every other name
			
			AddWorkerThread addWorker = new AddWorkerThread(set, 100, delay);
			ContainsAllThread containsAllWorker = new ContainsAllThread(set, testCollection);
			
			addWorker.start();
			containsAllWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (addWorker.isAlive() || containsAllWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, addWorker, containsAllWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	public final void testContainsAllWhileRemoving()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			Collection<? extends MutableNamedObject> testCollection = extractSet(set, 2);	// get every other name
			
			List<String> namesToDelete = extractNames(set, 1);	// get all of the names
			DeleteWorkerThread deleteWorker = new DeleteWorkerThread(set, namesToDelete, delay);
			ContainsAllThread containsAllWorker = new ContainsAllThread(set, testCollection);
			
			deleteWorker.start();
			containsAllWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (deleteWorker.isAlive() || containsAllWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, deleteWorker, containsAllWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}

	public final void testRetainAllWhileReading()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			Collection<? extends MutableNamedObject> testCollection = extractSet(set, 2);	// get every other name
			
			List<String> namesToAccess = extractNames(set, 1);	// get all of the names
			AccessWorkerThread accessWorker = new AccessWorkerThread(set, namesToAccess, delay);
			RetainAllThread retainAllWorker = new RetainAllThread(set, testCollection);
			
			accessWorker.start();
			retainAllWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (accessWorker.isAlive() || retainAllWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, accessWorker, retainAllWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	public final void testRetainAllWhileWriting()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			Collection<? extends MutableNamedObject> testCollection = extractSet(set, 2);	// get every other name
			
			AddWorkerThread addWorker = new AddWorkerThread(set, 100, delay);
			RetainAllThread retainAllWorker = new RetainAllThread(set, testCollection);
			
			addWorker.start();
			retainAllWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (addWorker.isAlive() || retainAllWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, addWorker, retainAllWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	public final void testRetainAllWhileDeleting()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			Collection<? extends MutableNamedObject> testCollection = extractSet(set, 2);	// get every other name
			
			List<String> namesToDelete = extractNames(set, 1);	// get all of the names
			DeleteWorkerThread deleteWorker = new DeleteWorkerThread(set, namesToDelete, delay);
			RetainAllThread retainAllWorker = new RetainAllThread(set, testCollection);
			
			deleteWorker.start();
			retainAllWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (deleteWorker.isAlive() || retainAllWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, deleteWorker, retainAllWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}

	public final void testContainsWhileAccessing()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			List<? extends MutableNamedObject> elementsToCheck = extractSet(set, 2);
			
			List<String> namesToAccess = extractNames(set, 2);	// get every-other name
			ContainsCollectionThread<NullMutableNamedObject> containsWorker = 
				new ContainsCollectionThread<NullMutableNamedObject>(set, elementsToCheck, delay);
			AccessWorkerThread accessWorker = new AccessWorkerThread(set, namesToAccess, delay);
			
			accessWorker.start();
			containsWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (accessWorker.isAlive() || containsWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, accessWorker, containsWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	public final void testContainsWhileWriting()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			List<? extends MutableNamedObject> elementsToCheck = extractSet(set, 2);
			
			ContainsCollectionThread<NullMutableNamedObject> containsWorker = 
				new ContainsCollectionThread<NullMutableNamedObject>(set, elementsToCheck, delay);
			AddWorkerThread addWorker = new AddWorkerThread(set, 100, delay);
			
			addWorker.start();
			containsWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (addWorker.isAlive() || containsWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, addWorker, containsWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	public final void testContainsWhileDeleting()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			List<? extends MutableNamedObject> elementsToCheck = extractSet(set, 2);
			
			List<String> namesToDelete = extractNames(set, 2);	// get every-other name
			ContainsCollectionThread<NullMutableNamedObject> containsWorker = 
				new ContainsCollectionThread<NullMutableNamedObject>(set, elementsToCheck, delay);
			DeleteWorkerThread deleteWorker = new DeleteWorkerThread(set, namesToDelete, delay);
			
			deleteWorker.start();
			containsWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (deleteWorker.isAlive() || containsWorker.isAlive()) && now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, deleteWorker, containsWorker) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}

	public final void testRemoveWhileIterating()
	{
		PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);

		TestCase.assertEquals(100, set.size());
		for(Iterator<SoftReference<? extends NullMutableNamedObject>> iter = set.iterator(); iter.hasNext(); )
		{
			SoftReference<? extends NullMutableNamedObject> element = iter.next();
			TestCase.assertNotNull(element);

			iter.remove();
		}
		TestCase.assertEquals(0, set.size());
		
		set.clear();	// clear once we're done so that the next iteration starts fresh
	}

	public final void testEverything()
	{
		long[] delays = new long[]{0,1,2,10};
		long waitForCompletion = 4000L;
		
		for(long delay : delays)
		{
			PersistentSet<NullMutableNamedObject> set = generatePopulatedSet(100);
			PersistentSet<NullMutableNamedObject> setToAdd = generatePopulatedSet(100);
			List<? extends MutableNamedObject> elementsToCheck = extractSet(set, 2);
			List<String> namesToAccess = extractNames(set, 2);	// get every-other name
			List<String> namesToDelete = extractNames(set, 4);	// get every-fourth name
			
			ThreadGroup containsCollectionThreadGroup = new ThreadGroup("containsCollection");
			List<ContainsCollectionThread<NullMutableNamedObject>> containsWorkers = 
				new ArrayList<ContainsCollectionThread<NullMutableNamedObject>>();
			for(int threadIndex=0; threadIndex<50; ++threadIndex)
				containsWorkers.add( new ContainsCollectionThread<NullMutableNamedObject>(containsCollectionThreadGroup, set, elementsToCheck, delay));
			
			ThreadGroup accessWorkersThreadGroup = new ThreadGroup("access");
			List<AccessWorkerThread> accessWorkers = 
				new ArrayList<AccessWorkerThread>();
			for(int threadIndex=0; threadIndex<50; ++threadIndex)
				accessWorkers.add( new AccessWorkerThread(accessWorkersThreadGroup, set, namesToAccess, delay));
			
			ThreadGroup addWorkersThreadGroup = new ThreadGroup("add");
			List<AddWorkerThread> addWorkers = 
				new ArrayList<AddWorkerThread>();
			for(int threadIndex=0; threadIndex<50; ++threadIndex)
				addWorkers.add( new AddWorkerThread(addWorkersThreadGroup, set, 100, delay));
			
			ThreadGroup deleteWorkersThreadGroup = new ThreadGroup("delete");
			List<DeleteWorkerThread> deleteWorkers = 
				new ArrayList<DeleteWorkerThread>();
			for(int threadIndex=0; threadIndex<50; ++threadIndex)
				deleteWorkers.add( new DeleteWorkerThread(deleteWorkersThreadGroup, set, namesToDelete, delay));
			
			for(AccessWorkerThread accessWorker : accessWorkers)
				accessWorker.start();
			for(ContainsCollectionThread<NullMutableNamedObject> containsWorker : containsWorkers)
				containsWorker.start();
			for(AddWorkerThread addWorker : addWorkers)
				addWorker.start();
			for(DeleteWorkerThread deleteWorker : deleteWorkers)
				deleteWorker.start();

			long start = System.currentTimeMillis();
			long now = System.currentTimeMillis();
			while( (accessWorkersThreadGroup.activeCount()>0 || 
					addWorkersThreadGroup.activeCount()>0 || 
					containsCollectionThreadGroup.activeCount()>0 || 
					deleteWorkersThreadGroup.activeCount()>0 ) 
				&& now < start + 2000L)
			{
				try{Thread.sleep(100);} catch(InterruptedException iX){}
				now = System.currentTimeMillis();
			}
			if( now > start + waitForCompletion)
				fail( generateMessage(set, accessWorkersThreadGroup, containsCollectionThreadGroup) );
			
			set.clear();	// clear once we're done so that the next iteration starts fresh
		}
	}
	
	
	// ==================================================================================
	// Private Helper Methods
	// ==================================================================================
	
	/**
	 * Create a PersistentSet of MutableNamedObject
	 * @param memberCount
	 * @return
	 */
	private PersistentSet<NullMutableNamedObject> generatePopulatedSet(int memberCount)
	{
		PersistentSet<NullMutableNamedObject> set = new NullMutableNamedObjectSet(null, 0, false);
		for(int index=0; index < memberCount; ++index)
			set.add( new SoftReference<NullMutableNamedObject>(new NullMutableNamedObject()) );
		
		return set;
	}
	
	/**
	 * Extract every 'skip' names from a PersistentSet<MutableNamedObject>
	 * @param set
	 * @param skip
	 * @return
	 */
	private List<String> extractNames(PersistentSet<? extends MutableNamedObject> set, int skip)
    {
		List<String> names = new ArrayList<String>();
		
		int index = 0;
		for( SoftReference<? extends MutableNamedObject> mno : set)
		{
			if(index % skip == 0)
				names.add( mno.get().getName() );
			++index;
		}
		
	    return names;
    }
	
	/**
	 * Extract every 'skip' member from a PersistentSet<MutableNamedObject>
	 * @param set
	 * @param skip
	 * @return
	 */
	private List<? extends MutableNamedObject> extractSet(PersistentSet<? extends MutableNamedObject> set, int skip)
    {
		List<MutableNamedObject> names = new ArrayList<MutableNamedObject>();
		
		int index = 0;
		for( SoftReference<? extends MutableNamedObject> mno : set)
		{
			if(index % skip == 0)
				names.add( mno.get() );
			++index;
		}
		
	    return names;
    }
	
	private String generateMessage(PersistentSet<? extends MutableNamedObject> set, Thread... threads)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Potential lockup, test ran too long.");
		for(Thread thread : threads)
		{
			if(thread.isAlive())
			{
				Thread.State threadState = thread.getState();
				sb.append(thread.getName() + " is " + threadState.toString() + ", and should be dead.\n");
				sb.append("Stack trace follows: \n");
				for( StackTraceElement stackTraceElement : thread.getStackTrace() )
					sb.append(stackTraceElement.toString() + "\n");
			}
			else
				sb.append(thread.getName() + " is not alive. \n" );
			
		}
			
		try
        {
            sb.append( "Set " + (set.isWriteLocked() ? "is write locked." : "is not write locked.") + "\n" );
        } 
		catch (InterruptedException e)
        {
            sb.append( "Unable to get Set's write locked status. \n" );
        }
		
		return sb.toString(); 
	}
	
	private String generateMessage(PersistentSet<? extends MutableNamedObject> set, ThreadGroup... threadGroups)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Potential lockup, test ran too long.");
		for(ThreadGroup threadGroup : threadGroups)
		{
			Thread[] groupThreads = null;
			int actualThreadGroupCount = 0;
			do
			{
				groupThreads = new Thread[threadGroup.activeCount()];
				actualThreadGroupCount = threadGroup.enumerate(groupThreads);
			}	
			while(actualThreadGroupCount < groupThreads.length);
			
			for(Thread thread : groupThreads)
			{
				if(thread.isAlive())
				{
					Thread.State threadState = thread.getState();
					sb.append(thread.getName() + " is " + threadState.toString() + ", and should be dead.\n");
					sb.append("Stack trace follows: \n");
					for( StackTraceElement stackTraceElement : thread.getStackTrace() )
						sb.append(stackTraceElement.toString() + "\n");
				}
				else
					sb.append(thread.getName() + " is not alive. \n" );
			}			
		}
			
		try
        {
            sb.append( "Set " + (set.isWriteLocked() ? "is write locked." : "is not write locked.") + "\n" );
        } 
		catch (InterruptedException e)
        {
            sb.append( "Unable to get Set's write locked status. \n" );
        }
		
		return sb.toString(); 
	}
	
	// ==================================================================================
	// Inner classes
	// ==================================================================================
	
	/**
	 * A class that implements MutableNamedObject, used for testing
	 * PersistentSet instances.
	 * 
	 * @author VHAISWBECKEC
	 */
	class NullMutableNamedObject implements MutableNamedObject
	{
		private final String name;
		
		NullMutableNamedObject()
		{
			this.name = (new GUID().toString());
		}
		
		NullMutableNamedObject(String name)
		{
			this.name = name;
		}
		
		@Override
        public String getName()
        {
	        return name;
        }

		@Override
        public void delete(boolean forceDelete) throws CacheException
        {
	        
        }
	}
	
	/**
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class NullMutableNamedObjectSet 
	extends PersistentSet<NullMutableNamedObject>
	{
		private static final long serialVersionUID = 1L;

		NullMutableNamedObjectSet(InstanceByteChannelFactory byteChannelFactory, int secondsReadWaitsForWriteCompletion,
                boolean setModificationTimeOnRead)
        {
	        super(byteChannelFactory, secondsReadWaitsForWriteCompletion, setModificationTimeOnRead);
        }

		@Override
        protected NullMutableNamedObject getOrCreate(String name, boolean create) 
		throws CacheException
        {
			return create ? new NullMutableNamedObject(name) : null;
        }

		@Override
        protected void internalSynchronizeChildren() 
		throws CacheException
        {
        }
	}
	
	class AddWorkerThread
	extends Thread
	{
		private final PersistentSet<NullMutableNamedObject> set;
		private final int memberCount;
		private List<NullMutableNamedObject> membersAdded = new ArrayList<NullMutableNamedObject>();
		private final long delay;
		
		AddWorkerThread(ThreadGroup group, PersistentSet<NullMutableNamedObject> set, int memberCount, long delay)
		{
			super(group, "AddWorkerThread" + System.currentTimeMillis());			
			this.set = set;
			this.memberCount = memberCount;
			this.delay = delay;
		}
		
		AddWorkerThread(PersistentSet<NullMutableNamedObject> set, int memberCount, long delay)
		{
			this(null, set, memberCount, delay);			
		}
		
		@Override
        public void run()
        {
			for(int index = 0; index < memberCount; ++index)
			{
				NullMutableNamedObject newMember = new NullMutableNamedObject();
				set.add( new SoftReference<NullMutableNamedObject>(newMember) );
				membersAdded.add(newMember);
				
				if(delay > 0)
					try{Thread.sleep(delay);}
					catch(InterruptedException iX){}
			}
        }
		
		public List<NullMutableNamedObject> getMembersAdded()
		{
			return membersAdded;
		}
	}
	
	/**
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class DeleteWorkerThread
	extends Thread
	{
		private final PersistentSet<NullMutableNamedObject> set;
		private List<String> memberNamesToDelete;
		private final long delay;
		
		DeleteWorkerThread(ThreadGroup group, PersistentSet<NullMutableNamedObject> set, List<String> memberNamesToDelete, long delay)
		{
			super(group, "DeleteWorkerThread" + System.currentTimeMillis());			
			this.set = set;
			this.memberNamesToDelete = memberNamesToDelete;
			this.delay = delay;
		}
		
		DeleteWorkerThread(PersistentSet<NullMutableNamedObject> set, List<String> memberNamesToDelete, long delay)
		{
			this(null, set, memberNamesToDelete, delay);
		}
		
        @Override
        public void run()
        {
        	if(memberNamesToDelete != null && memberNamesToDelete.size() > 0)
        		for(String memberNameToDelete : memberNamesToDelete)
        		{
        			set.removeByName(memberNameToDelete);
    				if(delay > 0)
    					try{Thread.sleep(delay);}
    					catch(InterruptedException iX){}
        		}
        }
	}
	
	class AccessWorkerThread
	extends Thread
	{
		private final PersistentSet<? extends MutableNamedObject> set;
		private List<String> memberNamesToAccess;
		private final long delay;
		
		AccessWorkerThread(ThreadGroup group, PersistentSet<? extends MutableNamedObject> set, List<String> memberNamesToAccess, long delay)
		{
			super(group, "AccessWorkerThread" + System.currentTimeMillis());			
			this.set = set;
			this.memberNamesToAccess = memberNamesToAccess;
			this.delay = delay;
		}
		
		AccessWorkerThread(PersistentSet<? extends MutableNamedObject> set, List<String> memberNamesToAccess, long delay)
		{
			this(null, set, memberNamesToAccess, delay);
		}
		
        @Override
        public void run()
        {
        	if(memberNamesToAccess != null && memberNamesToAccess.size() > 0)
        		for(String memberNameToAccess : memberNamesToAccess)
        		{
        			try{set.getChild(memberNameToAccess, false);}
        			catch(CacheException cX){cX.printStackTrace();}
    				if(delay > 0)
    					try{Thread.sleep(delay);}
    					catch(InterruptedException iX){}
        		}
        }
	}
	
	class ClearSetThread<S extends MutableNamedObject>
	extends Thread
	{
		private final PersistentSet<S> set;
		
		private ClearSetThread(PersistentSet<S> set)
        {
	        super("ClearSetThread" + System.currentTimeMillis());
	        this.set = set;
        }

		@Override
        public void run()
        {
			set.clear();
        }
	}
	
	class ContainsAllThread<S extends MutableNamedObject>
	extends Thread
	{
		private final PersistentSet<S> set;
		private final Collection<S> testCollection;
		
		private ContainsAllThread(PersistentSet<S> set, Collection<S> testCollection)
        {
	        super("ContainsAllThread" + System.currentTimeMillis());
	        this.set = set;
	        this.testCollection = testCollection;
        }

		@Override
        public void run()
        {
			set.containsAll(testCollection);
        }
	}
	
	class RetainAllThread<S extends MutableNamedObject>
	extends Thread
	{
		private final PersistentSet<S> set;
		private final Collection<S> testCollection;
		
		private RetainAllThread(PersistentSet<S> set, Collection<S> testCollection)
        {
	        super("RetainAllThread" + System.currentTimeMillis());
	        this.set = set;
	        this.testCollection = testCollection;
        }

		@Override
        public void run()
        {
			set.retainAll(testCollection);
        }
	}

	class ContainsCollectionThread<S extends MutableNamedObject>
	extends Thread
	{
		private final PersistentSet<S> set;
		private final Collection<? extends MutableNamedObject> testCollection;
		private final long delay;
		
		private ContainsCollectionThread(PersistentSet<S> set, Collection<? extends MutableNamedObject> testCollection, long delay)
        {
	        this(null, set, testCollection, delay);
        }

		private ContainsCollectionThread(ThreadGroup group, PersistentSet<S> set, Collection<? extends MutableNamedObject> testCollection, long delay)
		{
			super(group, "ContainsCollectionThread" + System.currentTimeMillis());
	        this.set = set;
	        this.testCollection = testCollection;
	        this.delay = delay;
		}
		
		@Override
        public void run()
        {
			for(MutableNamedObject testElement : testCollection)
			set.contains(testElement);
			if(delay > 0L)
				try{Thread.sleep(delay);}catch(InterruptedException iX){}
        }
	}
}
