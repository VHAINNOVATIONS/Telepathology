/**
 * 
 */
package gov.va.med.imaging;

import gov.va.med.imaging.ReadWriteLockListTest.ThreadedListTest;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class ReadWriteLockArrayListTest 
extends TestCase
{
	private long startTime;
	private int repeatTests = 100;
	
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		startTime = System.nanoTime();
	}

	@Override
	protected void tearDown() 
	throws Exception
	{
		System.out.println(this.getName() + "," + repeatTests + "," + (System.nanoTime() - startTime));
		super.tearDown();
	}

	/**
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void testSize() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());
		
		assertEquals(0, list.size() );
		list.add("a");
		assertEquals(1, list.size() );
		list.add("b");
		assertEquals(2, list.size() );
		list.add("c");
		assertEquals(3, list.size() );
	}

	/**
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void testIsEmpty() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());
		
		assertTrue(list.isEmpty());
		list.add("a");
		assertFalse(list.isEmpty());
		list.add("b");
		assertFalse(list.isEmpty());
		list.clear();
		assertTrue(list.isEmpty());
	}

	/**
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void testClear() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());
		
		assertTrue(list.isEmpty());
		list.add("a");
		assertFalse(list.isEmpty());
		list.add("b");
		assertFalse(list.isEmpty());
		list.clear();
		assertTrue(list.isEmpty());
		list.add("a");
		assertFalse(list.isEmpty());
	}


	/**
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void testContains() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());
		
		list.add("a");
		list.add("b");
		list.add("c");
		
		assertTrue( list.contains("a") );
		assertFalse( list.contains("d") );
		list.remove("a");
		assertFalse( list.contains("a") );
	}

	public void testMultithreadedAdd() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());
		
		int result = runThreadsAndWait( 
			new ThreadedListTest[] {
				new ThreadedListTest( (ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedAdd.thread1 starting");
						addRange(getList(), 1, 32);		// adds Strings "1" to "32"
						//System.out.println("testMultithreadedAdd.thread1 done");
						return ThreadedListTest.SUCCESS;
					}
				},
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedAdd.thread2 starting");
						addRange(getList(), 33, 64);		// adds Strings "33" to "64"
						//System.out.println("testMultithreadedAdd.thread2 done");
						return ThreadedListTest.SUCCESS;			
					}
				},
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedAdd.thread3 starting");
						addRange(getList(), 65, 96);		// adds Strings "65" to "96"
						//System.out.println("testMultithreadedPut.thread2 done");
						return ThreadedListTest.SUCCESS;			
					}
				}
			}, true
		);
		assertTrue(result == ThreadedListTest.SUCCESS);
		
		assertTrue(containsAllInRange(list, 1, 96));
	}

	public void testMultithreadedContains() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());
		
		addRange(list, 1, 32);		// adds Strings "1" to "32"

		int result = runThreadsAndWait( 
			new ThreadedListTest[] {
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedContains.thread1 starting");
						assertTrue(containsAllInRange(getList(), 1, 32));
						//System.out.println("testMultithreadedContains.thread1 done");
						return ThreadedListTest.SUCCESS;			
					}
				},
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedContains.thread2 starting");
						assertTrue(containsAllInRange(getList(), 1, 32));
						//System.out.println("testMultithreadedContains.thread2 done");
						return ThreadedListTest.SUCCESS;			
					}
				},
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedContains.thread3 starting");
						assertTrue(containsAllInRange(getList(), 1, 32));
						//System.out.println("testMultithreadedContains.thread3 done");
						return ThreadedListTest.SUCCESS;			
					}
				}
			}, true
		);
		assertTrue(result == ThreadedListTest.SUCCESS);
		
	}

	public void testMultithreadedAddContains() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());

		addRange(list, 1, 32);		// adds Strings "1" to "32"
		
		int result = runThreadsAndWait( 
				new ThreadedListTest[] {
					new ThreadedListTest((ReadWriteLockList)list)
					{
						public int runX()
						{
							//System.out.println("testMultithreadedPutGet.thread1 starting");
							addRange(getList(), 33, 64, true);
							assertTrue( containsAllInRange(getList(), 1, 32) );
							assertTrue( containsAllInRange(getList(), 33, 64) );
							
							//System.out.println("testMultithreadedPutGet.thread1 done");
							return ThreadedListTest.SUCCESS;			
						}
					},
					new ThreadedListTest((ReadWriteLockList)list)
					{
						public int runX()
						{
							//System.out.println("testMultithreadedPutGet.thread1 starting");
							addRange(getList(), 65, 96, true);
							assertTrue( containsAllInRange(getList(), 1, 32) );
							assertTrue( containsAllInRange(getList(), 65, 96) );
							
							//System.out.println("testMultithreadedPutGet.thread1 done");
							return ThreadedListTest.SUCCESS;			
						}
					},
					new ThreadedListTest((ReadWriteLockList)list)
					{
						public int runX()
						{
							//System.out.println("testMultithreadedPutGet.thread1 starting");
							addRange(getList(), 97, 128, true);
							assertTrue( containsAllInRange(getList(), 1, 32) );
							assertTrue( containsAllInRange(getList(), 97, 128) );
							
							//System.out.println("testMultithreadedPutGet.thread1 done");
							return ThreadedListTest.SUCCESS;			
						}
					}
				}, true
			);
		assertTrue(result == ThreadedListTest.SUCCESS);
	}
	
	
	
	public void testMultithreadedClearAndAddAll() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		List<String> list = 
			(List<String>) ReadWriteLockCollections.readWriteLockList(new ArrayList<String>());

		addRange(list, 1, 32, true);		// adds Strings "1" to "32"
		
		int result = runThreadsAndWait( 
			new ThreadedListTest[] {
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedClearAndAddAll.thread1 starting");
						List<String> newList = new ArrayList<String>();
						addRange(newList, 33, 64, true);		// adds Strings "33" to "64"
						
						getList().clearAndAddAll(newList);
						
						containsAllInRange(getList(), 33, 64);
						containsNoneInRange(getList(), 1, 32);
						
						//System.out.println("testMultithreadedClearAndAddAll.thread1 done");
						return ThreadedListTest.SUCCESS;			
					}
				},
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedClearAndAddAll.thread2 starting");
						List<String> newList = new ArrayList<String>();
						addRange(newList, 33, 64, true);		// adds Strings "33" to "64"
						
						getList().clearAndAddAll(newList);
						
						containsAllInRange(getList(), 33, 64);
						containsNoneInRange(getList(), 1, 32);
						
						//System.out.println("testMultithreadedClearAndAddAll.thread2 done");
						return ThreadedListTest.SUCCESS;			
					}
				},
				new ThreadedListTest((ReadWriteLockList)list)
				{
					public int runX()
					{
						//System.out.println("testMultithreadedClearAndAddAll.thread3 starting");
						List<String> newList = new ArrayList<String>();
						addRange(newList, 33, 64, true);		// adds Strings "33" to "64"
						
						getList().clearAndAddAll(newList);
						
						containsAllInRange(getList(), 33, 64);
						containsNoneInRange(getList(), 1, 32);
						
						//System.out.println("testMultithreadedClearAndAddAll.thread3 done");
						return ThreadedListTest.SUCCESS;			
					}
				}
			}, true
		);
		
		assertTrue(result == ThreadedListTest.SUCCESS);
	}

	/* =========================================================================================
	 * Mass List add, update, contains helper methods
	 * =========================================================================================
	 */
	/**
	 * Adds a bunch of Strings to a list
	 * @param list
	 */
	private void addRange(List<String> list, int start, int finish)
	{
		addRange(list, start, finish, false);
	}
	
	private void addRange(List<String> list, int start, int finish, boolean validateContains)
	{
		for(int index=start; index <= finish; ++index)
		{
			String value = Integer.toHexString(index);
			list.add(value);
			if(validateContains)
				assertTrue( list.contains(value) );
		}
	}
	
	/**
	 * Verify that a List contains all of the strings from "<start>" to "<finish>"
	 * Eg: if:
	 *   start=1
	 *   finish=32
	 *   the list must contain all the strings in the series "1", "2", "3" .. "32"
	 *   
	 * @param list
	 * @param start
	 * @param finish
	 * @return
	 */
	private boolean containsAllInRange(List<String> list, int start, int finish)
	{
		boolean result = true;
		for(int index=start; index <= finish; ++index)
		{
			String value = Integer.toHexString(index);
			result &= list.contains(value);
		}
		
		return result;
	}

	private boolean containsNoneInRange(List<String> list, int start, int finish)
	{
		boolean result = true;
		for(int index=start; index <= finish; ++index)
		{
			String value = Integer.toHexString(index);
			result &= !list.contains(value);
		}
		
		return result;
	}

	/* =========================================================================================
	 * Multithreading helpers
	 * =========================================================================================
	 */
	private int runThreadsAndWait(ThreadedListTest[] runnables, boolean varyPriority)
	{
		ThreadGroup testWorkerThreadGroup = new ThreadGroup("Test");
		
		Thread[] threads = new Thread[runnables.length];
		
		for(int runnablesIndex=0; runnablesIndex<runnables.length; ++runnablesIndex)
		{
			threads[runnablesIndex] = new Thread(testWorkerThreadGroup, runnables[runnablesIndex]);
			
			if(varyPriority)
			{
				threads[runnablesIndex].setPriority( (Thread.NORM_PRIORITY-1) +  (runnablesIndex % 3) );
			}
		}
		
		for(int threadsIndex=0; threadsIndex<threads.length; ++threadsIndex)
			threads[threadsIndex].start();

		waitForThreadGroup(testWorkerThreadGroup, runnables.length);
		
		for(int runnablesIndex=0; runnablesIndex<runnables.length; ++runnablesIndex)
			if(runnables[runnablesIndex].getResult() != ThreadedListTest.SUCCESS)
				return runnables[runnablesIndex].getResult();
		
		return ThreadedListTest.SUCCESS;
	}

	private int waitForThreadGroup(ThreadGroup testWorkerThreadGroup, int initialSize)
	{
		Thread[] threads = new Thread[initialSize];
		
		testWorkerThreadGroup.enumerate(threads, false);

		for( int threadIndex=0; threadIndex < initialSize; ++threadIndex )
		{
			try
			{
				if(threads[threadIndex] != null)
					threads[threadIndex].join();
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return ThreadedListTest.EXCEPTION;
			}
		}
		
		return ThreadedListTest.SUCCESS;
	}

	abstract class ThreadedListTest
	implements Runnable
	{
		public static final int SUCCESS = 0;
		public static final int FAILURE = 2;
		public static final int EXCEPTION = 1;
		
		private ReadWriteLockList<String> list = null;
		private int result = 0;
		
		ThreadedListTest(ReadWriteLockList<String> list)
		{
			this.list = list;
		}
		
		public void run()
		{
			try
			{
				runX();
			} 
			catch (Throwable e)
			{
				e.printStackTrace();
				result = EXCEPTION;
			}
		}
		
		public abstract int runX() throws Exception;

		public int getResult()
		{
			return this.result;
		}

		public ReadWriteLockList<String> getList()
		{
			return this.list;
		}
	}

}
