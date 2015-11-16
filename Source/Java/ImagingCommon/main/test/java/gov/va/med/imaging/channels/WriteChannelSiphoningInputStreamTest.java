/**
 * 
 */
package gov.va.med.imaging.channels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 * 
 */
public class WriteChannelSiphoningInputStreamTest
	extends TestCase
{

	/**
	 * For automated builds (and when checked in), leave this as 1.
	 * For manual testing it may be useful to run tests repeatedly
	 * to find errors that only occur sporadically.
	 */
	private static final int TEST_ITERATIONS = 1;

	/**
	 * Test method for
	 * {@link gov.va.med.imaging.channels.WriteChannelSiphoningInputStream#read()}.
	 */
	public void testRead()
	{
		for (int testIteration = 0; testIteration < TEST_ITERATIONS; ++testIteration)
		{
			TestInputData inData = new TestInputData();

			byte[] destination = new byte[32 * 1024];
			ByteArrayOutputStream siphonedCopy = new ByteArrayOutputStream(32 * 1024);
			WritableByteChannel siphoningChannel = Channels.newChannel(siphonedCopy);
			WriteChannelSiphoningInputStream siphon = 
				new WriteChannelSiphoningInputStream(inData.getTestDataAsInputStream(), siphoningChannel);
			int byteCount = 0;
			try
			{
				for (int value = siphon.read(); value != -1; value = siphon.read())
					destination[byteCount++] = (byte) value;
				siphon.synchronousClose(10L, TimeUnit.SECONDS);
			}
			catch (IOException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}
			catch (InterruptedException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}

			assertFalse(siphon.isAnySiphonChannelExceptions());
			// the destination and the siphoned copies should be identical
			assertEquals(byteCount, siphonedCopy.size());
			assertContentsEquals(destination, siphonedCopy, byteCount);
		}
	}

	// do this test only once because it takes a lot of time
	public void testReadWithAsynchronousClose()
	{
		TestInputData inData = new TestInputData();

		byte[] destination = new byte[32 * 1024];
		ByteArrayOutputStream siphonedCopy = new ByteArrayOutputStream(32 * 1024);
		WritableByteChannel siphoningChannel = Channels.newChannel(siphonedCopy);
		WriteChannelSiphoningInputStream siphon = 
			new WriteChannelSiphoningInputStream(inData.getTestDataAsInputStream(), siphoningChannel);
		int byteCount = 0;
		try
		{
			for (int value = siphon.read(); value != -1; value = siphon.read())
				destination[byteCount++] = (byte) value;
			siphon.close();
		}
		catch (IOException x)
		{
			x.printStackTrace();
			fail(x.getMessage());
		}

		try
		{
			Thread.sleep(10000L);
		}
		catch (InterruptedException x)
		{
			x.printStackTrace();
		}
		
		assertFalse(siphon.isAnySiphonChannelExceptions());
		// the destination and the siphoned copies should be identical
		assertEquals(byteCount, siphonedCopy.size());
		assertContentsEquals(destination, siphonedCopy, byteCount);
	}
	
	/**
	 * Test method for
	 * {@link gov.va.med.imaging.channels.WriteChannelSiphoningInputStream#read(byte[])}.
	 */
	public void testReadByteArray()
	{
		for (int testIteration = 0; testIteration < TEST_ITERATIONS; ++testIteration)
		{
			TestInputData inData = new TestInputData();

			byte[] destination = new byte[32 * 1024];
			ByteArrayOutputStream siphonedCopy = new ByteArrayOutputStream(32 * 1024);
			WritableByteChannel siphoningChannel = Channels.newChannel(siphonedCopy);
			WriteChannelSiphoningInputStream siphon = 
				new WriteChannelSiphoningInputStream(inData.getTestDataAsInputStream(), siphoningChannel);
			int byteCount = 0;
			byte[] temp = new byte[1024];
			try
			{
				for (int bytesRead = siphon.read(temp); bytesRead != -1; bytesRead = siphon.read(temp))
				{
					System.arraycopy(temp, 0, destination, byteCount, bytesRead);
					byteCount += bytesRead;
				}
				siphon.synchronousClose(1L, TimeUnit.SECONDS);
			}
			catch (IOException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}
			catch (InterruptedException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}

			assertContentsEquals(destination, siphonedCopy, byteCount);
		}
	}

	/**
	 * Test method for
	 * {@link gov.va.med.imaging.channels.WriteChannelSiphoningInputStream#read(byte[], int, int)}.
	 */
	public void testReadByteArrayIntInt()
	{
		for (int testIteration = 0; testIteration < TEST_ITERATIONS; ++testIteration)
		{
			TestInputData inData = new TestInputData();

			byte[] destination = new byte[32 * 1024];
			ByteArrayOutputStream siphonedCopy = new ByteArrayOutputStream(32 * 1024);
			WritableByteChannel siphoningChannel = Channels.newChannel(siphonedCopy);
			WriteChannelSiphoningInputStream siphon = 
				new WriteChannelSiphoningInputStream(inData.getTestDataAsInputStream(), siphoningChannel);
			int byteCount = 0;
			byte[] temp = new byte[1024];
			try
			{
				for (int bytesRead = siphon.read(temp, 32, 42); bytesRead != -1; bytesRead = siphon.read(temp, 32, 42))
				{
					System.arraycopy(temp, 32, destination, byteCount, bytesRead);
					byteCount += bytesRead;
				}
				siphon.synchronousClose(1L, TimeUnit.SECONDS);
			}
			catch (IOException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}
			catch (InterruptedException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}

			assertContentsEquals(destination, siphonedCopy, byteCount);
		}
	}

	public void testMarkAndReset() throws IOException
	{
		for (int testIteration = 0; testIteration < TEST_ITERATIONS; ++testIteration)
		{
			TestInputData inData = new TestInputData();

			byte[] destination = new byte[32 * 1024];
			ByteArrayOutputStream siphonedCopy = new ByteArrayOutputStream(32 * 1024);
			WritableByteChannel siphoningChannel = Channels.newChannel(siphonedCopy);
			WriteChannelSiphoningInputStream siphon = 
				new WriteChannelSiphoningInputStream(inData.getTestDataAsInputStream(), siphoningChannel);

			assertTrue(siphon.available() >= 0);
			assertTrue(siphon.markSupported());

			int byteCount = 0;
			byte[] temp = new byte[1024];
			boolean markedTested = false;
			try
			{
				for (int bytesRead = siphon.read(temp); bytesRead != -1; bytesRead = siphon.read(temp))
				{
					System.arraycopy(temp, 0, destination, byteCount, bytesRead);
					byteCount += bytesRead;
					if (byteCount > 42 && !markedTested)
						siphon.mark(1024);

					if (byteCount > 128 && !markedTested)
					{
						siphon.reset();
						markedTested = true;
					}
					if (byteCount > 128 && markedTested)
						siphon.mark(32);
				}
				siphon.synchronousClose(1L, TimeUnit.SECONDS);
			}
			catch (IOException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}
			catch (InterruptedException x)
			{
				x.printStackTrace();
				fail(x.getMessage());
			}

			assertContentsEquals(destination, siphonedCopy, byteCount);
		}
	}

	private void assertContentsEquals(byte[] destination, ByteArrayOutputStream siphonedCopy, int byteCount)
	{
		// the destination and the siphoned copies should be identical
		byte[] siphonedCopyResult = siphonedCopy.toByteArray();
		assertEquals(byteCount, siphonedCopyResult.length);

		for (int index = 0; index < byteCount; ++index)
			assertEquals(destination[index], siphonedCopyResult[index]);
	}
}
