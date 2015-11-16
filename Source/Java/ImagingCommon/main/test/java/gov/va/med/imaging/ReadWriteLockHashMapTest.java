/**
 * 
 */
package gov.va.med.imaging;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class ReadWriteLockHashMapTest extends TestCase
{
	/**
	 * Test method for {@link biz.happycat.concurrency.ReadWriteLockHashMap#size()}.
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void testSize() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		
		assertEquals(0, map.size() );
		map.put("a", "1");
		assertEquals(1, map.size() );
		map.put("b", "2");
		assertEquals(2, map.size() );
		map.put("c", "3");
		assertEquals(3, map.size() );
	}

	/**
	 * Test method for {@link biz.happycat.concurrency.ReadWriteLockHashMap#isEmpty()}.
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
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		
		assertTrue(map.isEmpty());
		map.put("a", "1");
		assertFalse(map.isEmpty());
		map.put("b", "2");
		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}

	/**
	 * Test method for {@link biz.happycat.concurrency.ReadWriteLockHashMap#clear()}.
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
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		
		assertTrue(map.isEmpty());
		map.put("a", "1");
		assertFalse(map.isEmpty());
		map.put("b", "2");
		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
		map.put("a", "1");
		assertFalse(map.isEmpty());
	}


	/**
	 * Test method for {@link biz.happycat.concurrency.ReadWriteLockHashMap#containsKey(java.lang.Object)}.
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void testContainsKeyObject() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		
		assertTrue( map.containsKey("a") );
		assertFalse( map.containsKey("d") );
		map.remove("a");
		assertFalse( map.containsKey("a") );
	}

	/**
	 * Test method for {@link biz.happycat.concurrency.ReadWriteLockHashMap#containsValue(java.lang.Object)}.
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public void testContainsValueObject() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		
		assertTrue( map.containsValue("1") );
		assertFalse( map.containsValue("4") );
		map.remove("a");
		assertFalse( map.containsValue("1") );
	}


	public void testMultithreadedPut() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		
		int result = runThreadsAndWait( 
			new ThreadedMapTest[] {
				new ThreadedMapTest( (ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedPut.thread1 starting");
						addRange(getMap(), 0, 9);
						System.out.println("testMultithreadedPut.thread1 done");
						return ThreadedMapTest.SUCCESS;
					}
				},
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedPut.thread2 starting");
						addRange(getMap(), 10, 19);
						System.out.println("testMultithreadedPut.thread2 done");
						return ThreadedMapTest.SUCCESS;			
					}
				},
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedPut.thread2 starting");
						addRange(getMap(), 20, 29);
						System.out.println("testMultithreadedPut.thread3 done");
						return ThreadedMapTest.SUCCESS;			
					}
				}
			}, true
		);
		assertTrue(result == ThreadedMapTest.SUCCESS);
		
		assertTrue( containsAllKeysInRange(map, 0, 29) );
		assertTrue( validateKeysAndValuesInRange(map,0, 29) );
	}

	public void testMultithreadedGet() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		addRange(map, 0, 29);
		
		
		int result = runThreadsAndWait( 
			new ThreadedMapTest[] {
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedGet.thread1 starting");
						assertTrue( validateKeysAndValuesInRange(getMap(),0, 29) );
						System.out.println("testMultithreadedGet.thread1 done");
						return ThreadedMapTest.SUCCESS;			
					}
				},
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedGet.thread2 starting");
						assertTrue( validateKeysAndValuesInRange(getMap(),0, 29) );
						System.out.println("testMultithreadedGet.thread2 done");
						return ThreadedMapTest.SUCCESS;			
					}
				},
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedGet.thread3 starting");
						assertTrue( validateKeysAndValuesInRange(getMap(),0, 29) );
						System.out.println("testMultithreadedGet.thread3 done");
						return ThreadedMapTest.SUCCESS;			
					}
				}
			}, true
		);
		assertTrue(result == ThreadedMapTest.SUCCESS);
		
	}

	public void testMultithreadedPutGet() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		addRange(map, 0, 9);
		
		int result = runThreadsAndWait( 
				new ThreadedMapTest[] {
					new ThreadedMapTest((ReadWriteLockMap)map)
					{
						public int runX()
						{
							System.out.println("testMultithreadedPutGet.thread1 starting");
							addRange(getMap(), 10, 19, true);
							assertTrue( validateKeysAndValuesInRange(getMap(),0, 9) );
							assertTrue( validateKeysAndValuesInRange(getMap(),10, 19) );
							System.out.println("testMultithreadedPutGet.thread1 done");
							return ThreadedMapTest.SUCCESS;			
						}
					},
					new ThreadedMapTest((ReadWriteLockMap)map)
					{
						public int runX()
						{
							System.out.println("testMultithreadedPutGet.thread2 starting");
							addRange(getMap(), 20, 29, true);
							assertTrue( validateKeysAndValuesInRange(getMap(),0, 9) );
							assertTrue( validateKeysAndValuesInRange(getMap(),20, 29) );
							System.out.println("testMultithreadedPutGet.thread2 done");
							return ThreadedMapTest.SUCCESS;			
						}
					},
					new ThreadedMapTest((ReadWriteLockMap)map)
					{
						public int runX()
						{
							System.out.println("testMultithreadedPutGet.thread3 starting");
							addRange(getMap(), 30, 39, true);
							assertTrue( validateKeysAndValuesInRange(getMap(),0, 9) );
							assertTrue( validateKeysAndValuesInRange(getMap(),30, 39) );
							System.out.println("testMultithreadedPutGet.thread3 done");
							return ThreadedMapTest.SUCCESS;			
						}
					}
				}, true
			);
		assertTrue(result == ThreadedMapTest.SUCCESS);
	}
	
	
	
	public void testMultithreadedClearAndPutAll() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map<String, String> map = 
			(Map<String, String>) ReadWriteLockCollections.readWriteLockMap(new HashMap<String, String>());
		//ReadWriteLockHashMap<String, String> map = new ReadWriteLockHashMap<String, String>();
		
		addRange(map, 0, 9);
		assertTrue( validateKeysAndValuesInRange(map,0, 9) );
		
		int result = runThreadsAndWait( 
			new ThreadedMapTest[] {
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedClearAndPutAll.thread1 starting");
						Map<String,String> newMap = new HashMap<String, String>();
						addRange(newMap, 10, 19);
						
						getMap().clearAndPutAll(newMap);
						
						assertTrue( validateKeysAndValuesInRange(getMap(), 10, 19) );
						assertTrue( containsNoKeysInRange(getMap(), 0, 9) );
						
						System.out.println("testMultithreadedClearAndPutAll.thread1 done");
						return ThreadedMapTest.SUCCESS;			
					}
				},
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedClearAndPutAll.thread2 starting");
						Map<String,String> newMap = new HashMap<String, String>();
						addRange(newMap, 10, 19);
						
						getMap().clearAndPutAll(newMap);
						
						assertTrue( validateKeysAndValuesInRange(getMap(), 10, 19) );
						assertTrue( containsNoKeysInRange(getMap(), 0, 9) );
						
						System.out.println("testMultithreadedClearAndPutAll.thread2 done");
						return ThreadedMapTest.SUCCESS;			
					}
				},
				new ThreadedMapTest((ReadWriteLockMap)map)
				{
					public int runX()
					{
						System.out.println("testMultithreadedClearAndPutAll.thread3 starting");
						Map<String,String> newMap = new HashMap<String, String>();
						addRange(newMap, 10, 19);
						
						getMap().clearAndPutAll(newMap);
						
						assertTrue( validateKeysAndValuesInRange(getMap(), 10, 19) );
						assertTrue( containsNoKeysInRange(getMap(), 0, 9) );
						
						System.out.println("testMultithreadedClearAndPutAll.thread3 done");
						return ThreadedMapTest.SUCCESS;			
					}
				}
			}, true
		);
		
		assertTrue(result == ThreadedMapTest.SUCCESS);
	}

	/* =========================================================================================
	 * Mass List add, update, contains helper methods
	 * =========================================================================================
	 */
	/**
	 * Just a standardized way to correlate keys and values without
	 * relying on the Map ... that being the thing under test we
	 * really shouldn't rely on it.
	 */
	private String getValueFromKey(String key)
	{
		return "value" + key;
	}
	/**
	 * Adds a bunch of Strings to a list
	 * @param list
	 */
	private void addRange(Map<String, String> map, int start, int finish)
	{
		addRange(map, start, finish, false);
	}
	
	private void addRange(Map<String, String> map, int start, int finish, boolean validateContains)
	{
		for(int index=start; index <= finish; ++index)
		{
			String value = Integer.toHexString(index);
			map.put(value, getValueFromKey(value));
			if(validateContains)
				assertTrue( getValueFromKey(value).equals( map.get(value) ) );
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
	private boolean containsAllKeysInRange(Map<String, String> map, int start, int finish)
	{
		boolean result = true;
		for(int index=start; index <= finish; ++index)
		{
			String value = Integer.toHexString(index);
			result &= map.containsKey(value);
		}
		
		return result;
	}

	private boolean containsNoKeysInRange(Map<String, String> map, int start, int finish)
	{
		boolean result = true;
		for(int index=start; index <= finish; ++index)
		{
			String value = Integer.toHexString(index);
			result &= !map.containsKey(value);
		}
		
		return result;
	}

	private boolean validateKeysAndValuesInRange(Map<String, String> map, int start, int finish)
	{
		boolean result = true;
		for(int index=start; index <= finish; ++index)
		{
			String key = Integer.toHexString(index);
			String expectedValue = getValueFromKey(key);
			result &= expectedValue.equals( map.get(key) );
		}
		
		return result;
	}
	
	
	/* =========================================================================================
	 * Multithreading helpers
	 * =========================================================================================
	 */
	private int runThreadsAndWait(ThreadedMapTest[] runnables, boolean varyPriority)
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
			if(runnables[runnablesIndex].getResult() != ThreadedMapTest.SUCCESS)
				return runnables[runnablesIndex].getResult();
		
		return ThreadedMapTest.SUCCESS;
	}

	private int waitForThreadGroup(ThreadGroup testWorkerThreadGroup, int initialSize)
	{
		Thread[] threads = new Thread[initialSize];
		
		for(boolean anyThreadsAlive = true; anyThreadsAlive; )
		{
			anyThreadsAlive = false;
			testWorkerThreadGroup.enumerate(threads, false);
	
			for( int threadIndex=0; threadIndex < initialSize; ++threadIndex )
			{
				anyThreadsAlive |= (threads[threadIndex] != null && threads[threadIndex].isAlive());
			}
			
			try{Thread.sleep(1000);} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return ThreadedMapTest.EXCEPTION;
			}
		}
		return ThreadedMapTest.SUCCESS;
	}

	abstract class ThreadedMapTest
	implements Runnable
	{
		public static final int SUCCESS = 0;
		public static final int FAILURE = 2;
		public static final int EXCEPTION = 1;
		
		private ReadWriteLockMap<String, String> map = null;
		private int result = 0;
		
		ThreadedMapTest(ReadWriteLockMap<String, String> map)
		{
			this.map = map;
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

		public ReadWriteLockMap<String, String> getMap()
		{
			return this.map;
		}
	}

}
