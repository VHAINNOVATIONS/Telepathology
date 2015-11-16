package gov.va.med.imaging.storage.cache;

import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class AbstractTestChannelTimeout 
extends AbstractCacheTest
{
	
	@Override
    protected void setUp() throws Exception
    {
	    super.setUp();
    }

	public void testChannelTimeout() 
	throws CacheException, IOException, InterruptedException
	{
		String region = "test-metadata";
		String[] groups = new String[]{"timeout"};
		String key = "test" + System.currentTimeMillis();
		byte[] someData = new byte[]{1,2,3,4,5,6,7,8,9};
		
	    // manually set the timeout to a know and somewhat reasonable value
	    // so that the test does not take too long
	    InstanceByteChannelFactory<?> instanceByteChannelfactory = getCache().getInstanceByteChannelFactory();
	    instanceByteChannelfactory.setMaxChannelOpenDuration(30000L);
	    
		// create a new instance
		Instance tempInstance = getCache().getOrCreateInstance(region, groups, key);
		WritableByteChannel writeChannel = null;
		
		// write the instance data
		writeChannel = tempInstance.getWritableChannel();
		java.nio.ByteBuffer src = ByteBuffer.wrap(someData);
		writeChannel.write(src);
		// do not close the channel, we're going to force it to timeout
		
		// the timeout is 30 seconds, wait 45 to allow for the sweep delay
		long secondsToWait = ((2L * instanceByteChannelfactory.getMaxChannelOpenDuration()) / 1000L) + 10L;
		String waitMsg = "Waiting " + secondsToWait + " seconds for channels to close, timeout is " + 
			(instanceByteChannelfactory.getMaxChannelOpenDuration() / 1000L) + " seconds.";
		System.out.println(waitMsg);
		Thread.sleep(secondsToWait * 1000L);
		
		try
		{
			java.nio.ByteBuffer additionalSrc = ByteBuffer.wrap(someData);
			// this should fail cause the channel has been closed
			writeChannel.write(additionalSrc);
			fail("Channel write should have failed.  " + waitMsg);
		}
		catch(IOException ioX)
		{
			// this should occur !!!
		}
	}
}
