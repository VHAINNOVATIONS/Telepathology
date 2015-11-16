package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.io.IOException;

import org.junit.Test;

public abstract class AbstractTestSimpleDirectAccess 
extends AbstractAccessDirectCacheTest
{
	@Test
	public void testShallowSaveAndRetrieve() 
	throws CacheException, IOException
	{
		createRetrieveAndCompareInstance("test-metadata", new String[]{"fred"}, "barney", getSampleData());
	}
	
	@Test
	public void testModerateSaveAndRetrieve() 
	throws CacheException, IOException
	{
		createRetrieveAndCompareInstance("test-metadata", new String[]{"fred", "wilma"}, "betty", getSampleData());
	}
	
	@Test
	public void testDeepSaveAndRetrieve() 
	throws CacheException, IOException
	{
		createRetrieveAndCompareInstance("test-metadata", new String[]{"group1", "group2", "group3", "group4"}, "instance", getSampleData());
	}
	
	public void testLotsaRandomSaveRetrieveAndDelete()
	throws CacheException, IOException
	{
		for(int testPointIndex=0; testPointIndex < 1000; ++testPointIndex)
		{
			int groupDepth = (int)(Math.random() * 9.0) + 1;
			
			String[] groupNames = new String[groupDepth]; 
			for(int groupIndex=0; groupIndex<groupDepth; ++groupIndex)
				groupNames[groupIndex] = makeRandomName();
			String instanceName = makeRandomName();
			
			createRetrieveAndCompareInstance("test-metadata", groupNames, instanceName, getSampleData());
		}
	}
}
