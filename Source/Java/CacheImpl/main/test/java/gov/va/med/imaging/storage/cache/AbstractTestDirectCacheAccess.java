package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractTestDirectCacheAccess 
extends AbstractAccessDirectCacheTest
{
	public void testImageFiles() 
	throws IOException, CacheException, ChecksumFormatException
	{
		String region = "test-image";
		String[] groups = new String[]{"test-patient"}; 
		String[] imageFileNames = new String[]{
				"images/brain_001.dcm", "images/brain_002.dcm", "images/brain_003.dcm", "images/brain_004.dcm", "images/brain_005.dcm",
				"images/brain_006.dcm", "images/brain_007.dcm", "images/brain_008.dcm", "images/brain_009.dcm", "images/brain_010.dcm",
				"images/brain_011.dcm", "images/brain_012.dcm", "images/brain_013.dcm", "images/brain_014.dcm", "images/brain_015.dcm",
				"images/brain_016.dcm", "images/brain_017.dcm", "images/brain_018.dcm", "images/brain_019.dcm", "images/brain_020.dcm"
				};
		
		byte[] junkData = new byte[]{1,2,3,4,5,6,7,8};

		// create new cache instances and write the content from test resource streams
		// then read back the content and compare (byte-for-byte) with the 
		// original test resource stream
		for(String imageFileName : imageFileNames)
		{
			InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(imageFileName);
			assertNotNull(inStream);
			String key = getKeyFromFilename(imageFileName);
			
			createAndWriteInstance(region, groups, key, inStream);
			inStream.close();

			InputStream compareStream = this.getClass().getClassLoader().getResourceAsStream(imageFileName);
			retrieveAndCompareInstance(region, groups, key, compareStream);
			
			compareStream.close();
			
			updateExistingInstance(region, groups, key, junkData, false);
			InputStream junkDataCompareStream = new ByteArrayInputStream(junkData);
			retrieveAndCompareInstance(region, groups, key, junkDataCompareStream);
		}

		// its possible to get invalid failures here if the execution of this class is
		// really slow and the cache eviction is really fast
		for(String imageFileName : imageFileNames)
		{
			String key = getKeyFromFilename(imageFileName);
			
			assertTrue( "Instance '" + key + "' does not exist and should.", instanceExists(region, groups, key) );
		}
		
		// wait at least two minutes for the eviction thread to kick the images out
		System.out.print("Waiting for eviction timer to remove images.");
		try
		{
			Thread.sleep(2L * 60L * 1000L);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		for(String imageFileName : imageFileNames)
		{
			String key = getKeyFromFilename(imageFileName);
			
			assertFalse( "Instance '" + imageFileName + "' exists and should not.", instanceExists(region, groups, key) );
		}
	}
	
	private String getKeyFromFilename(String filename)
	{
		return filename.substring(
				filename.lastIndexOf("/") >= 0 ? filename.lastIndexOf("/")+1 : 0, 
				filename.indexOf(".") >= 0 ? filename.indexOf(".") : filename.length());
	}
	
}
