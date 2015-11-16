package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.channels.CheckedReadableByteChannel;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.InstanceInaccessibleException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.Adler32;

import org.apache.log4j.Logger;

public abstract class AbstractAccessDirectCacheTest 
extends AbstractCacheTest
{
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 
	 * @return
	 */
	public Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * This tests for the existence of the persistent copy of the instance, i.e. the file.
	 * The existence of the Instance instance means little because the file may be deleted
	 * from underneath it when the eviction thread runs.
	 * 
	 * @param region
	 * @param groups
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	protected boolean instanceExists(String region, String[] groups, String key) 
	throws CacheException
	{
		Instance tempInstance = getCache().getInstance(region, groups, key);
		
		return tempInstance != null && tempInstance.isPersistent();
	}

	protected String createRetrieveAndCompareInstance(String region, String[] groups, String key, byte[] data) 
	throws CacheException, IOException
	{
		String instanceChecksumValue = null;
		try
		{
			instanceChecksumValue = createAndWriteInstance(region, groups, key, data);
		}
		catch(InstanceInaccessibleException iiX)
		{
			System.out.println("Instance inaccessible when trying to write instance, will just read it.");
		}
		
		retrieveAndCompareInstance(region, groups, key, data, instanceChecksumValue);
		
		deleteAndConfirmDeletionInstance(region, groups, key, false);
		
		return instanceChecksumValue;
	}

	/**
	 * 
	 * @param path
	 * @param data
	 * @return
	 * @throws CacheException
	 * @throws IOException
	 */
	protected String createAndWriteInstance(CacheItemPath path, byte[] data) 
	throws CacheException, IOException
	{
		return createAndWriteInstance(path.getRegionName(), path.getGroupsName(), path.getInstanceName(), data, false);
	}
	
	/**
	 * 
	 * @param region
	 * @param groups
	 * @param key
	 * @param data
	 * @throws CacheException
	 * @throws IOException
	 */
	protected String createAndWriteInstance(String region, String[] groups, String key, byte[] data) 
	throws CacheException, IOException
	{
		return createAndWriteInstance(region, groups, key, data, false);
	}
	
	/**
	 * @param region
	 * @param groups
	 * @param key
	 * @param errorClose - if true then close with an error rather than a regular close.
	 * 
	 * @throws CacheException
	 * @throws IOException
	 */
	protected String createAndWriteInstance(String region, String[] groups, String key, byte[] data, boolean errorClose) 
	throws CacheException, IOException
	{
		String checksumValue = null;
		Instance tempInstance = getCache().getOrCreateInstance(region, groups, key);
		InstanceWritableByteChannel writeChannel = null;
		long bytesWritten = 0L;
		
		if(tempInstance != null)
		{
			try
			{
				writeChannel = tempInstance.getWritableChannel();
				
				java.nio.ByteBuffer src = ByteBuffer.wrap(data);
				
				bytesWritten = writeChannel.write(src);
				
				checksumValue = tempInstance.getChecksumValue();
			} 
			finally
			{
				try
				{
					logger.debug("Wrote " + bytesWritten + 
							" bytes with checksum " + (writeChannel.getChecksum() == null ? "<not caclculate>" : writeChannel.getChecksum().getValue()) + 
							", to instance '" + key + "' " + (errorClose ? "(error close)" : "(normal close)") + ".");
					
					if(errorClose)
						writeChannel.error();
					else
						writeChannel.close();
				}
				catch(Throwable x){}
			}
		}
		else
			fail("Failed to create instance '" + key + "'.");
		
		return checksumValue;
	}

	/**
	 * 
	 * @param region
	 * @param groups
	 * @param key
	 * @param data
	 * @param errorClose
	 * @return
	 * @throws CacheException
	 * @throws IOException
	 */
	protected String updateExistingInstance(String region, String[] groups, String key, byte[] data, boolean errorClose) 
	throws CacheException, IOException
	{
		String checksumValue = null;
		Instance tempInstance = getCache().getInstance(region, groups, key);
		InstanceWritableByteChannel writeChannel = null;
		long bytesWritten = 0L;
		
		if(tempInstance != null)
		{
			try
			{
				writeChannel = tempInstance.getWritableChannel();
				
				java.nio.ByteBuffer src = ByteBuffer.wrap(data);
				
				bytesWritten = writeChannel.write(src);
				
				checksumValue = tempInstance.getChecksumValue();
			} 
			finally
			{
				try
				{
					logger.debug("Wrote " + bytesWritten + 
							" bytes with checksum " + (writeChannel.getChecksum() == null ? "<not caclculate>" : writeChannel.getChecksum().getValue()) + 
							", to instance '" + key + "' " + (errorClose ? "(error close)" : "(normal close)") + ".");
					
					if(errorClose)
						writeChannel.error();
					else
						writeChannel.close();
				}
				catch(Throwable x){}
			}
		}
		else
			fail("Failed to get instance '" + key + "'.");
		
		return checksumValue;
	}
	
	/**
	 * @param region
	 * @param groups
	 * @param key
	 * @throws CacheException
	 * @throws IOException
	 */
	protected void retrieveAndCompareInstance(String region, String[] groups, String key, byte[] compareData, String instanceChecksumValue) 
	throws CacheException, IOException
	{
		Instance readInstance = getCache().getInstance(region, groups, key);
		if(readInstance == null)
			fail("A reference to cache instance (" + key + ") cannot be obtained.");
		
		InstanceReadableByteChannel readChannel = null;
		byte[] dstBytes = new byte[compareData.length];
		
		String message = region + ":";
		for(String group: groups)
			message = message + group + ".";
		message = message + key;
		
		int bytesRead = 0;
		try
		{
			readChannel = readInstance.getReadableChannel();
			
			java.nio.ByteBuffer dst = ByteBuffer.wrap(dstBytes);
			
			bytesRead = readChannel.read(dst);
			assertEquals(message, compareData.length, bytesRead);
			
			for(int index=0; index<dstBytes.length; ++index)
				assertEquals(message + " @ byte " + index, compareData[index], dstBytes[index]);
		} 
		finally
		{
			if(readChannel != null && readChannel.getChecksum() != null)
				logger.debug("Read " + bytesRead + " bytes with checksum " + readChannel.getChecksum().getValue() + ", to instance '" + key + "' .");
			else
				logger.debug("Read " + bytesRead + " bytes with checksum <not calculated>, to instance '" + key + "' .");
			
			try{readChannel.close();}
			catch(Throwable x){}
		}
	}

	/**
	 * 
	 * @param region
	 * @param groups
	 * @param key
	 * @param inStream
	 * @return
	 * @throws CacheException
	 * @throws IOException
	 * @throws ChecksumFormatException 
	 */
	protected long createAndWriteInstance(String region, String[] groups, String key, InputStream inStream) 
	throws CacheException, IOException, ChecksumFormatException
	{
		return createAndWriteInstance(region, groups, key, inStream, false);
	}
	
	/**
	 * Create a cache instance, write the content of the stream to it and then
	 * close the instance with a normal close or an error.  If errorClose is true 
	 * then the instance should NOT exist after this call.
	 * 
	 * @param region
	 * @param groups
	 * @param key
	 * @param inStream
	 * @param errorClose - if true then error close the writable channel (in which case the file instance should be deleted)
	 * @return
	 * @throws CacheException
	 * @throws IOException
	 * @throws ChecksumFormatException 
	 */
	protected long createAndWriteInstance(String region, String[] groups, String key, InputStream inStream, boolean errorClose) 
	throws CacheException, IOException, ChecksumFormatException
	{
		long bytesWritten = 0L;
		InstanceWritableByteChannel writeChannel = null;
		
		try
		{
			logger.debug("Getting or creating instance '" + key + "'.");
			Instance instance = getCache().getOrCreateInstance(region, groups, key);
			
			CheckedReadableByteChannel inChannel = new CheckedReadableByteChannel( Channels.newChannel(inStream), new Adler32() );
			writeChannel = instance.getWritableChannel();

			assertNotNull(writeChannel);
			logger.debug("Writing instance '" + key + "'.");
			
			bytesWritten = pump(inChannel, writeChannel, 4096);
			
			String checksumAsString = instance.getChecksumValue();
			if(checksumAsString != null)
			{
				ChecksumValue cv = new ChecksumValue(checksumAsString);
				assertEquals(inChannel.getChecksum().getValue(), cv.getValue().longValue());
			}
		} 
		finally
		{
			logger.debug("Wrote " + bytesWritten + 
					" bytes with checksum " + (writeChannel.getChecksum() == null ? "<not caclculate>" : writeChannel.getChecksum().getValue()) + 
					", to instance '" + key + "' " + (errorClose ? "(error close)" : "(normal close)") + ".");
			
			if(errorClose)
				writeChannel.error();
			else
				writeChannel.close();
			
			writeChannel = null;
			System.out.println("Instance '" + key + "' written and closed.");
		}
		
		return bytesWritten;
	}
	
	/**
	 * 
	 * @param region
	 * @param groups
	 * @param key
	 * @param compareStream
	 * @throws CacheException
	 * @throws IOException
	 */
	protected long retrieveAndCompareInstance(String region, String[] groups, String key, InputStream compareStream) 
	throws CacheException, IOException
	{
		return retrieveAndCompareInstance(region, groups, key, compareStream, false);
	}	
	
	/**
	 * 
	 * @param region
	 * @param groups
	 * @param key
	 * @param compareStream
	 * @param errorClose
	 * @throws CacheException
	 * @throws IOException
	 */
	protected long retrieveAndCompareInstance(String region, String[] groups, String key, InputStream compareStream, boolean errorClose) 
	throws CacheException, IOException
	{
		InstanceReadableByteChannel inChannel = null;
		long bytesRead = 0L;
		
		try
		{
			Instance instance = getCache().getOrCreateInstance(region, groups, key);
			
			ReadableByteChannel compareChannel = Channels.newChannel(compareStream);
			inChannel = instance.getReadableChannel();
			assertNotNull("Failed to get a readable channel on the instance", inChannel);
			
			ByteBuffer buffy = ByteBuffer.allocate(4096);
			ByteBuffer compareBuffy = ByteBuffer.allocate(4096);
			
			for( int compareBytesRead = compareChannel.read(compareBuffy); compareBytesRead > 0; compareBytesRead = compareChannel.read(compareBuffy) )
			{
				int instanceBytesRead = inChannel.read(buffy);
				assertEquals(compareBytesRead, instanceBytesRead);
				assertEquals(buffy.array(), compareBuffy.array());
				
				buffy.clear();
				compareBuffy.clear();
				
				bytesRead += instanceBytesRead;
			}
		} 
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
		finally
		{
			logger.debug("Read " + bytesRead + 
					" bytes with checksum " + (inChannel.getChecksum() == null ? "<not caclculate>" : inChannel.getChecksum().getValue()) + 
					", to instance '" + key + "' " + (errorClose ? "(error close)" : "(normal close)") + ".");
			
			if(errorClose)
				inChannel.error();
			else
				inChannel.close();
		}
		
		return bytesRead;
	}
	
	/**
	 * @param region
	 * @param groups
	 * @param key
	 */
	protected void deleteAndConfirmDeletionInstance(String region, String[] groups, String key, boolean forceDelete)
	{
		Instance instanceAfter;
		try
		{
			Instance instance = getCache().getInstance(region, groups, key);
			assertNotNull(instance);
			getCache().deleteInstance(region, groups, key, forceDelete);
			instanceAfter = getCache().getInstance(region, groups, key);
			assertNull(instanceAfter);
			
			Logger.getLogger(AbstractAccessDirectCacheTest.class).info("Instance successfully deleted.");
		}
		catch (CacheException x)
		{
			System.err.println("deleteAndConfirmDeletionInstance(" + region + "," + groups.length + "," + key);
			x.printStackTrace();
			fail();
		}
		
	}
	
	/**
	 * A method to slowly (with delays) read an instance, thereby giving
	 * some other thread a chance to do something to the Instance.
	 * 
	 * @throws CacheException 
	 * @throws IOException 
	 * 
	 */
	protected void slowlyReadInstance(CacheItemPath path) 
	throws CacheException, IOException
	{
		Instance readInstance = getCache().getInstance(path.getRegionName(), path.getGroupsName(), path.getInstanceName());
		if(readInstance == null)
			fail("A reference to cache instance (" + path + ") cannot be obtained.");
		
		InstanceReadableByteChannel readChannel = null;
		
		try
		{
			readChannel = readInstance.getReadableChannel();
			
			java.nio.ByteBuffer dst = ByteBuffer.allocateDirect(32);
			
			while( readChannel.read(dst) >= 0 )
			{
				dst.clear();
				try{Thread.sleep(1000L);}catch(InterruptedException iX){break;}		// slow the read
			}
		} 
		finally
		{
			try{readChannel.close();}
			catch(Throwable x){}
		}
	}

	/**
	 * A method to slowly (with delays) write random data to an instance, thereby giving
	 * some other thread a chance to do something to the Instance.
	 * 
	 * @throws CacheException 
	 * @throws IOException 
	 * 
	 */
	protected void slowlyWriteInstance(CacheItemPath path) 
	throws CacheException, IOException
	{
		Instance writeInstance = getCache().getInstance(path.getRegionName(), path.getGroupsName(), path.getInstanceName());
		if(writeInstance == null)
			fail("A reference to cache instance (" + path + ") cannot be obtained.");
		
		InstanceWritableByteChannel writeChannel = null;
		
		try
		{
			writeChannel = writeInstance.getWritableChannelNoWait();
			long bytesWritten = 0L;
			
			for(int index=0; index < 64; ++index)
			{
				java.nio.ByteBuffer dst = ByteBuffer.wrap( AbstractCacheTest.createSampleData(32) );
				dst.position(0);
				dst.limit(dst.capacity());
				
				while(dst.position() < dst.limit()-1 )
					bytesWritten += writeChannel.write(dst);
				
				try{Thread.sleep(1000L);}catch(InterruptedException iX){}
			}
		} 
		finally
		{
			try{writeChannel.close();}
			catch(Throwable x){}
		}
		
	}


	/**
	 * Do a byte-for-byte comparison and assure that the contents of the byte buffers are equal.
	 * 
	 * @param in
	 * @param compare
	 */
	protected void assertEquals(byte[] in, byte[] compare)
	{
		assertEquals(in.length, compare.length);
		
		for(int i=0; i<in.length; ++i)
			assertEquals(compare[i], in[i]);
	}
	
	public static long pump (ReadableByteChannel readable, WritableByteChannel writable, int bufferSize) 
	throws IOException
	{
		long bytesWritten = 0L;
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
		buffer.clear();
		while( readable.read (buffer) >= 0 ) 
		{
			buffer.flip(  );
			bytesWritten += writable.write (buffer);
			buffer.clear(  );
		}
		
		return bytesWritten;
	}
}
