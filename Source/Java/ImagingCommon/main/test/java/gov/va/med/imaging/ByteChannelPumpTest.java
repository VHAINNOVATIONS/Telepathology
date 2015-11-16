/**
 * 
 */
package gov.va.med.imaging;

import java.io.*;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gov.va.med.imaging.channels.ByteChannelPump;
import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class ByteChannelPumpTest 
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
		System.out.println("After - " + this.getName());
		System.out.println(ByteChannelPump.getNameToBufferSizeMapString());
		
		super.tearDown();
	}

	// =======================================================================================
	public void testFastReadToValidatingWriteTransfer() 
	throws IOException
	{
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, true);
	}
	
	public void testMultipleFastReadToValidatingWriteTransfer() 
	throws IOException
	{
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
		fastReadToValidatingWriteTransfer(8 * 1024, false);
	}
	

	public void testMultithreadFastReadToValidatingWriteTransfer() 
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
						fastReadToValidatingWriteTransfer(8 * 1024, false);
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

	// =======================================================================================
	public void testSlowReadToValidatingWriteTransfer() 
	throws IOException
	{
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, true);
	}
	
	public void testMultipleSlowReadToValidatingWriteTransfer() 
	throws IOException
	{
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
		slowReadToValidatingWriteTransfer(8 * 1024, false);
	}
	

	public void testMultithreadSlowReadToValidatingWriteTransfer() 
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
						slowReadToValidatingWriteTransfer(8 * 1024, false);
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
	
	// =======================================================================================
	public void testFastReadToFileWriteTransfer() 
	throws IOException
	{
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, true);
	}
	
	public void testMultipleFastReadToFileWriteTransfer() 
	throws IOException
	{
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
		fastReadToFileWriteTransfer(8 * 1024, false);
	}
	

	public void testMultithreadFastReadToFileWriteTransfer() 
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
						fastReadToFileWriteTransfer(8 * 1024, false);
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

	// =======================================================================================
	public void testSlowReadToFileWriteTransfer() 
	throws IOException
	{
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, true);
	}
	
	public void testMultipleSlowReadToFileWriteTransfer() 
	throws IOException
	{
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
		slowReadToFileWriteTransfer(8 * 1024, false);
	}
	

	public void testMultithreadSlowReadToFileWriteTransfer() 
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
						slowReadToFileWriteTransfer(8 * 1024, false);
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

	// =======================================================================================
	public void testFastReadToSlowValidatingWriteTransfer() 
	throws IOException
	{
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, true);
	}
	
	public void testMultipleFastReadToSlowValidatingWriteTransfer() 
	throws IOException
	{
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fastReadToSlowValidatingWriteTransfer(8 * 1024, false);
	}
	

	public void testMultithreadFastReadToSlowValidatingWriteTransfer() 
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
						fileReadToValidatingWriteTransfer(8 * 1024, false);
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
	
	// =======================================================================================
	public void testFileReadToValidatingWriteTransfer() 
	throws IOException
	{
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, true);
	}
	
	public void testMultipleFileReadToValidatingWriteTransfer() 
	throws IOException
	{
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
		fileReadToValidatingWriteTransfer(8 * 1024, false);
	}
	

	public void testMultithreadFileReadToValidatingWriteTransfer() 
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
						fileReadToValidatingWriteTransfer(8 * 1024, false);
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

	// =======================================================================================
	public void testFileReadToSlowValidatingWriteTransfer() 
	throws IOException
	{
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, true);
	}
	
	public void testMultipleFileReadToSlowValidatingWriteTransfer() 
	throws IOException
	{
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
		fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
	}
	

	public void testMultithreadFileReadToSlowValidatingWriteTransfer() 
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
						fileReadToSlowValidatingWriteTransfer(8 * 1024, false);
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
	// Helper methods so running multiple transfers is easier.
	// These must all take one integer arg.
	// 
	// =======================================================================================================
	/**
	 * @param testDataLength
	 * @throws IOException
	 */
	private void fastReadToValidatingWriteTransfer(int testDataLength, boolean detectZeroBytes) 
	throws IOException
	{
		ReadableByteChannel readChannel = TestDataGenerator.createFastReadableByteChannel(testDataLength);
		WritableByteChannel writeChannel = TestDataGenerator.createTestDataValidatingWritableByteChannel();
		
		ByteChannelPump bytePump = ByteChannelPump.getByteChannelPump(ByteChannelPump.TRANSFER_TYPE.ByteArrayToByteArray);
		bytePump.setDetectZeroBytesInBlockingMode(detectZeroBytes);
		bytePump.xfer(readChannel, writeChannel);
		
		readChannel.close();
		writeChannel.close();
	}

	private void fastReadToSlowValidatingWriteTransfer(int testDataLength, boolean detectZeroBytes) 
	throws IOException
	{
		ReadableByteChannel readChannel = TestDataGenerator.createFastReadableByteChannel(testDataLength);
		WritableByteChannel writeChannel = TestDataGenerator.createTestDataValidatingSlowWritableByteChannel();
		
		ByteChannelPump bytePump = ByteChannelPump.getByteChannelPump(ByteChannelPump.TRANSFER_TYPE.ByteArrayToByteArray);
		bytePump.setDetectZeroBytesInBlockingMode(detectZeroBytes);
		bytePump.xfer(readChannel, writeChannel);
		
		readChannel.close();
		writeChannel.close();
	}

	private void slowReadToValidatingWriteTransfer(int testDataLength, boolean detectZeroBytes) 
	throws IOException
	{
		ReadableByteChannel readChannel = TestDataGenerator.createSlowReadableByteChannel(testDataLength);
		WritableByteChannel writeChannel = TestDataGenerator.createTestDataValidatingWritableByteChannel();
		
		ByteChannelPump bytePump = ByteChannelPump.getByteChannelPump(ByteChannelPump.TRANSFER_TYPE.NetworkToByteArray);
		bytePump.setDetectZeroBytesInBlockingMode(detectZeroBytes);
		bytePump.xfer(readChannel, writeChannel);
		
		readChannel.close();
		writeChannel.close();
	}
	
	private void fastReadToFileWriteTransfer(int testDataLength, boolean detectZeroBytes) 
	throws IOException
	{
		ReadableByteChannel readChannel = TestDataGenerator.createFastReadableByteChannel(testDataLength);
		File tempFile = TestDataGenerator.createBlankDestinationFile();
		WritableByteChannel writeChannel = 
			new FileOutputStream(tempFile).getChannel();
		
		ByteChannelPump bytePump = ByteChannelPump.getByteChannelPump(ByteChannelPump.TRANSFER_TYPE.ByteArrayToByteArray);
		bytePump.setDetectZeroBytesInBlockingMode(detectZeroBytes);
		bytePump.xfer(readChannel, writeChannel);
		
		readChannel.close();
		writeChannel.close();
		
		TestDataGenerator.assertValidTestData(tempFile);
		tempFile.delete();
	}

	private void slowReadToFileWriteTransfer(int testDataLength, boolean detectZeroBytes) 
	throws IOException
	{
		ReadableByteChannel readChannel = TestDataGenerator.createSlowReadableByteChannel(testDataLength);
		File tempFile = TestDataGenerator.createBlankDestinationFile();
		WritableByteChannel writeChannel = 
			new FileOutputStream(tempFile).getChannel();
		
		ByteChannelPump bytePump = ByteChannelPump.getByteChannelPump(ByteChannelPump.TRANSFER_TYPE.ByteArrayToByteArray);
		bytePump.setDetectZeroBytesInBlockingMode(detectZeroBytes);
		bytePump.xfer(readChannel, writeChannel);
		
		readChannel.close();
		writeChannel.close();
		
		TestDataGenerator.assertValidTestData(tempFile);
		tempFile.delete();
	}
	
	private void fileReadToValidatingWriteTransfer(int testDataLength, boolean detectZeroBytes) 
	throws IOException
	{
		File tempFile = TestDataGenerator.createFile(testDataLength);
		ReadableByteChannel readChannel = new FileInputStream(tempFile).getChannel();
		WritableByteChannel writeChannel = TestDataGenerator.createTestDataValidatingWritableByteChannel();
		
		ByteChannelPump bytePump = ByteChannelPump.getByteChannelPump(ByteChannelPump.TRANSFER_TYPE.NetworkToByteArray);
		bytePump.setDetectZeroBytesInBlockingMode(detectZeroBytes);
		bytePump.xfer(readChannel, writeChannel);
		
		readChannel.close();
		writeChannel.close();
	}
	
	private void fileReadToSlowValidatingWriteTransfer(int testDataLength, boolean detectZeroBytes) 
	throws IOException
	{
		File tempFile = TestDataGenerator.createFile(testDataLength);
		ReadableByteChannel readChannel = new FileInputStream(tempFile).getChannel();
		WritableByteChannel writeChannel = TestDataGenerator.createTestDataValidatingSlowWritableByteChannel();
		
		ByteChannelPump bytePump = ByteChannelPump.getByteChannelPump(ByteChannelPump.TRANSFER_TYPE.NetworkToByteArray);
		bytePump.setDetectZeroBytesInBlockingMode(detectZeroBytes);
		bytePump.xfer(readChannel, writeChannel);
		
		readChannel.close();
		writeChannel.close();
	}
}
