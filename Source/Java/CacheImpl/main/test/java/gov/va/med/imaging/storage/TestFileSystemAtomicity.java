package gov.va.med.imaging.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;


public class TestFileSystemAtomicity 
{
	public static final String DIRECTORY_PATH = "/test";
	public static final String FILE_NAME = "testfile.txt";
	
	@Test
	public void testFileCreateDelete() 
	throws IOException
	{
		File directory = new File(DIRECTORY_PATH);
		if( !directory.exists() )
			directory.mkdirs();
		
		File file = new File(directory, FILE_NAME);
		TestCase.assertTrue( !file.exists() );
		
		TestCase.assertTrue( file.createNewFile() );
		TestCase.assertTrue( file.exists() );
		
		TestCase.assertEquals(1, directory.listFiles(new MyFileFilter()).length);
		
		TestCase.assertTrue( file.delete() );
		TestCase.assertTrue( !file.exists() );

		TestCase.assertEquals(0, directory.listFiles(new MyFileFilter()).length);
	}

	class MyFileFilter 
	implements FileFilter
	{
		@Override
		public boolean accept(File instanceFile) 
		{
			return !instanceFile.isDirectory() 
				&& FILE_NAME.equals(instanceFile.getName())
				&& instanceFile.exists();
		}
	}
}
