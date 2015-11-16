package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceUnavailableException;
import gov.va.med.imaging.storage.cache.exceptions.SimultaneousWriteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public abstract class AbstractTestMultithreadAccess 
extends AbstractAccessDirectCacheTest
{
	public void testSingleThread() 
	throws InterruptedException, ExecutionException
	{
		test(1, false);
	}
	
	public void testTwoThreadNoOverlap() 
	throws InterruptedException, ExecutionException
	{
		test(2, false);
	}

	public void testTenThreadNoOverlap() 
	throws InterruptedException, ExecutionException
	{
		test(10, false);
	}
	
//	public void testHundredThreadNoOverlap() 
//	throws InterruptedException, ExecutionException
//	{
//		test(100, false);
//	}
	
	public void testTwoThreadOverlappedOperations() 
	throws InterruptedException, ExecutionException
	{
		test(2, true);
	}
	
	public void testTenThreadOverlappedOperations() 
	throws InterruptedException, ExecutionException
	{
		test(10, true);
	}
	
	public void testErrorClose() 
	throws IOException, InterruptedException
	{
		byte[] sampleData = getSampleData();
		String regionName = "test-metadata";
		String[] groups = new String[]{"ErrorTestGroup", "ErrorTestSubGroup"};
		String key = "ErrorTestKey";
		
		try
		{
			// create an instance and close it with an error, which should
			// delete the instance
			createAndWriteInstance(regionName, groups, key, sampleData, true);
			Cache cache = getCache();
			assertNotNull("Cache instance is null after writing an instance, WTF?!!!", cache);
			
			Instance instance = cache.getInstance(regionName, groups, key);
			assertNotNull("Instance is null and should not be.", instance);
			
			instance.getReadableChannel();
			fail( "Instance closed on error exists and should not." );
		}
		catch(InstanceUnavailableException iuX)
		{
			// this is expected
		}
		catch(InstanceInaccessibleException iiX)
		{
			// or this, depending on timing
		}
		catch(CacheException cX)
		{
			getLogger().error(cX);
		}
	}
	
//	public void testHundredThreadOverlappedOperations() 
//	throws InterruptedException, ExecutionException
//	{
//		test(100, true);
//	}
	
	private void test(int threadCount, boolean overlap) 
	throws InterruptedException, ExecutionException
	{
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

		Collection<Callable<WorkerResult>> workers = createWorkers(threadCount, overlap);
		List<Future<WorkerResult>> results = executorService.invokeAll(workers);

		waitForResults(results);
	}
	
	// ===========================================================================================================
	// Helper Methods
	// ===========================================================================================================
	
	private Collection<Callable<WorkerResult>> createWorkers(int count, boolean overlapData)
	{
		Collection<Callable<WorkerResult>> workerThreads = new ArrayList<Callable<WorkerResult>>();
		
		for(int index=0; index<count; ++index)
			workerThreads.add( new Worker( getTestIdentifiers(overlapData ? 0 : index) ) );

		return workerThreads;
	}

	private void waitForResults(List<Future<WorkerResult>> results) 
	throws InterruptedException
	{
		for(Future<WorkerResult> result:results)
		{
			try
			{
				WorkerResult workerResult = result.get();
				getLogger().info( getName() + " - worker result is " + workerResult.toString() );
			} 
			catch (ExecutionException x)
			{
				getLogger().info( getName() + " - worker exception is " + x.getCause(), x.getCause() );
			}
		}
		getLogger().info( getName() + " completed waiting for worker threads, all results should be available" );
	}

	
	enum WorkerResult
	{
		SUCCESS, FAILURE
	}
	
	class Worker 
	implements Callable<WorkerResult>
	{
		private InstanceIdentifier[] identifiers;
		
		Worker(InstanceIdentifier[] identifiers)
		{
			this.identifiers = identifiers;
		}
		
		public WorkerResult call() 
		throws Exception
		{
			for(InstanceIdentifier identifier: identifiers)
			{
				String instanceChecksumValue = null;
				try
				{
					instanceChecksumValue = 
						createRetrieveAndCompareInstance(identifier.getRegion(), identifier.getGroups(), identifier.getKey(), getSampleData());
				} 
				catch(SimultaneousWriteException swX)
				{
					try
					{
						retrieveAndCompareInstance(identifier.getRegion(), identifier.getGroups(), identifier.getKey(), getSampleData(), instanceChecksumValue);
					}
					catch (CacheException e)
					{
						e.printStackTrace();
						fail(e.getMessage());
					} 
					catch (IOException e)
					{
						e.printStackTrace();
						fail(e.getMessage());
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
			
			return WorkerResult.SUCCESS;
		}
	}
	
	class InstanceIdentifier
	{
		private String region;
		private String[] groups;
		private String key;
		
		public InstanceIdentifier(String region, String[] groups, String key)
		{
			super();
			this.region = region;
			this.groups = groups;
			this.key = key;
		}

		public String getRegion()
		{
			return this.region;
		}
		
		public String[] getGroups()
		{
			return this.groups;
		}

		public String getKey()
		{
			return this.key;
		}
	}
	
	// ===================================================================================================================
	// Test Data
	// ===================================================================================================================
	private InstanceIdentifier[][] testIdentifiers = null;
	private final int identifierGroups = 10;
	private final int identifiersPerGroup = 10;
	
	private synchronized InstanceIdentifier[][] getTestIdentifiers()
	{
		if(testIdentifiers == null)
		{
			testIdentifiers = new InstanceIdentifier[identifierGroups][identifiersPerGroup];
			for(int groupIndex=0; groupIndex < identifierGroups; ++groupIndex)
			{
				for(int identifierIndex=0; identifierIndex < identifiersPerGroup; ++identifierIndex)
				{
					testIdentifiers[groupIndex][identifierIndex] = 
			  			new InstanceIdentifier("test-metadata", new String[]{"Group" + groupIndex, "SubGroup" + groupIndex}, "Key" + identifierIndex);
				}
			}

		}
		return testIdentifiers;
	}
	
	public InstanceIdentifier[] getTestIdentifiers(int identifierGroupIndex)
	{
		// assure that the identifer group asked for is within our range
		identifierGroupIndex = identifierGroupIndex % identifierGroups;
		
		return getTestIdentifiers()[identifierGroupIndex];
	}
	
	
}
