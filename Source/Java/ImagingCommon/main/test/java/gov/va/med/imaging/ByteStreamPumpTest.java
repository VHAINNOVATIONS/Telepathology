/**
 * 
 */
package gov.va.med.imaging;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gov.va.med.imaging.channels.ByteStreamPump;
import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class ByteStreamPumpTest 
extends TestCase
{
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		System.out.println("After-" + this.getName());
		System.out.println(ByteStreamPump.getNameToBufferSizeMapString());
		super.tearDown();
	}

	public void testByteBufferToByteBufferTransfer() 
	throws IOException
	{
		byteBufferToByteBufferTransfer(8 * 1024);
	}
	
	public void testMultipleByteBufferToByteBufferTransfer() 
	throws IOException
	{
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
		byteBufferToByteBufferTransfer(8 * 1024);
	}
	
	public void testFileToByteBufferTransfer() 
	throws IOException
	{
		fileToByteBufferTransfer(8 * 1024);
	}

	public void testMultipleFileToByteBufferTransfer() 
	throws IOException
	{
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
		fileToByteBufferTransfer(8 * 1024);
	}

	public void testSlowInputStreamToByteBufferTransfer() 
	throws IOException
	{
		slowInputStreamToByteBufferTransfer(8 * 1024);
	}

	public void testMultipleSlowInputStreamToByteBufferTransfer() 
	throws IOException
	{
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
		slowInputStreamToByteBufferTransfer(8 * 1024);
	}

	public void testSlowInputStreamToFileTransfer() 
	throws IOException
	{
		slowInputStreamToFileTransfer(8 * 1024);
	}

	public void testMultipleSlowInputStreamToFileTransfer() 
	throws IOException
	{
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
		slowInputStreamToFileTransfer(8 * 1024);
	}
	
	public void testMultithreadSlowInputStreamToFileTransfer() 
	throws InterruptedException, IOException
	{
		// use an executor that will not queue tasks
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(int taskIndex=0; taskIndex < 10; ++taskIndex)
		{
			executor.submit(new Runnable()
			{
				public void run()
				{
					try
					{
						slowInputStreamToFileTransfer(8 * 1024);
					} 
					catch (IOException x)
					{
						x.printStackTrace();
						fail(x.getMessage());
					}
				}
			}
			);
		}
		
		executor.shutdown();
		executor.awaitTermination(60L, TimeUnit.SECONDS);
	}
	
	// =====================================================================================================
	public void testByteBufferMultipleByteBufferTransfer() 
	throws IOException
	{
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
	}

	public void testMultipleByteBufferMultipleByteBufferTransfer() 
	throws IOException
	{
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
		byteBufferToMultipleByteBufferTransfer(8 * 1024);
	}

	public void testMultithreadByteBufferMultipleByteBufferTransfer() 
	throws InterruptedException, IOException
	{
		// use an executor that will not queue tasks
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(int taskIndex=0; taskIndex < 10; ++taskIndex)
		{
			executor.submit(new Runnable()
			{
				public void run()
				{
					try
					{
						byteBufferToMultipleByteBufferTransfer(8 * 1024);
					} 
					catch (IOException x)
					{
						x.printStackTrace();
						fail(x.getMessage());
					}
				}
			}
			);
		}
		
		executor.shutdown();
		executor.awaitTermination(60L, TimeUnit.SECONDS);
	}
	
	// =======================================================================================================
	// Helper methods so running multiple transfers is easier
	// =======================================================================================================
	/**
	 * @param testDataLength
	 * @throws IOException
	 */
	private void byteBufferToByteBufferTransfer(int testDataLength) 
	throws IOException
	{
		byte[] sourceData = TestDataGenerator.createByteArray(testDataLength);
		
		ByteArrayInputStream inStream = new ByteArrayInputStream(sourceData);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(sourceData.length);
		
		ByteStreamPump bytePump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToByteArray);
		bytePump.xfer(inStream, outStream);
		
		TestDataGenerator.assertValidTestData(outStream.toByteArray());
	}
	
	/**
	 * @param testDataLength
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void fileToByteBufferTransfer(int testDataLength) 
	throws IOException, FileNotFoundException
	{
		File testFile = TestDataGenerator.createFile(testDataLength);
		
		FileInputStream inStream = new FileInputStream(testFile);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(testDataLength);
		
		ByteStreamPump bytePump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.FileToByteArray);
		bytePump.xfer(inStream, outStream);
		
		TestDataGenerator.assertValidTestData(outStream.toByteArray());
		
		testFile.delete();
	}
	
	private void slowInputStreamToFileTransfer(int testDataLength) 
	throws IOException
	{
		InputStream inStream = TestDataGenerator.createSlowInputStream(testDataLength, 32);
		File destinationFile = TestDataGenerator.createBlankDestinationFile();
		FileOutputStream outStream = new FileOutputStream(destinationFile);
		
		ByteStreamPump bytePump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToFile);
		bytePump.xfer(inStream, outStream);
		
		TestDataGenerator.assertValidTestData(destinationFile);
		
		destinationFile.delete();
	}
	
	public void slowInputStreamToByteBufferTransfer(int testDataLength) 
	throws IOException
	{
		InputStream inStream = TestDataGenerator.createSlowInputStream(testDataLength, 32);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(testDataLength);
		
		ByteStreamPump bytePump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.NetworkToByteArray);
		bytePump.xfer(inStream, outStream);
		
		TestDataGenerator.assertValidTestData(outStream.toByteArray());
	}
	
	private void byteBufferToMultipleByteBufferTransfer(int testDataLength) 
	throws IOException
	{
		byte[] sourceData = TestDataGenerator.createByteArray(testDataLength);
		
		ByteArrayInputStream inStream = new ByteArrayInputStream(sourceData);
		ByteArrayOutputStream[] outStreams = new ByteArrayOutputStream[]
		{
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length),
			new ByteArrayOutputStream(sourceData.length)
		};
		
		ByteStreamPump bytePump = ByteStreamPump.getByteStreamPump(ByteStreamPump.TRANSFER_TYPE.ByteArrayToByteArray);
		bytePump.xfer(inStream, outStreams);
		
		for(ByteArrayOutputStream outStream : outStreams)
			TestDataGenerator.assertValidTestData(outStream.toByteArray());
	}
	
}
