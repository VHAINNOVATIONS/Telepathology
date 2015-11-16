/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 30, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.storage;

import gov.va.med.imaging.exchange.TaskScheduler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * Extension of ArrayList that contains ByteBuffers. This list holds the ByteBuffers and knows how 
 * large of buffers it creates and holds on to. 
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ByteBufferPool 
extends TimerTask
implements Comparable<ByteBufferPool>, 
ByteBufferPoolMBean
{
	private static final long serialVersionUID = -2871304395412112847L;
	private final static Logger logger = Logger.getLogger(ByteBufferPool.class);
	
	private final static long BYTE_BUFFER_POOL_TIMER_REFRESH = 1000 * 60 * 15; // 15 minutes
	
	private List<ByteBuffer> byteBuffers = null;
	
	private final int maxBufferSizeInBytes;
	private final String name;
	
	// the preferred max number of buffers in this pool (when a cleanup occurs, bring down to this amount)
	private final int preferredBufferMaxCount;
	// the maximum number of buffers in the pool allowed (should never exceed this value)
	private final int maxBufferCount;
	
	// Counters for JMX MBeans
	int requestBufferCount = 0;
	int returnBufferCount = 0;
	int createNewBufferCount = 0;
	long bufferSizeUsage = 0;
	
	/**
	 * Create an empty buffer list of the specified size
	 * @param name
	 * @param maxBufferSizeInbytes
	 * @return
	 */
	public static ByteBufferPool createByteBufferList(String name, int maxBufferSizeInbytes, int preferredPoolSize)
	{
		logger.info("Creating ByteBufferMinimumSizeList of [" + maxBufferSizeInbytes + " buffer sizes named [" + name + "]");
		ByteBufferPool list = new ByteBufferPool(name, maxBufferSizeInbytes, preferredPoolSize);		
		return list;
	}
	
	/**
	 * Create a buffer list of the specified size with the specified number of buffers in it initially.
	 * @param name
	 * @param maxBufferSizeInbytes
	 * @param initialBuffers
	 * @return
	 */
	public static ByteBufferPool createByteBufferList(String name, int maxBufferSizeInbytes, 
		int preferredPoolSize, int initialBuffers)
	{
		ByteBufferPool list = createByteBufferList(name, maxBufferSizeInbytes, preferredPoolSize);
		for(int i = 0; i < initialBuffers; i++)
		{
			ByteBuffer b = list.createNewBuffer(0);
			list.returnBufferToList(b);
		}
		 // reset the counters so adding the buffers doesn't increment the counts
		list.resetCounters();
		return list;
	}
	
	/**
	 * Internal constructor
	 * @param name
	 * @param maxBufferSize
	 */
	private ByteBufferPool(String name, int maxBufferSize, int preferredPoolSize)
	{
		byteBuffers = new ArrayList<ByteBuffer>();
		this.name = name;
		this.maxBufferSizeInBytes = maxBufferSize;
		// set these to better defaults, allow input parameters for these
		this.maxBufferCount = 100;
		this.preferredBufferMaxCount = preferredPoolSize;		
		
		logger.info("Scheduling byte buffer pool '" + name + "' every [" + BYTE_BUFFER_POOL_TIMER_REFRESH + "] ms");
		TaskScheduler.getTaskScheduler().schedule(this, BYTE_BUFFER_POOL_TIMER_REFRESH, BYTE_BUFFER_POOL_TIMER_REFRESH);
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() 
	{
		synchronized (byteBuffers) 
		{
			if(byteBuffers.size() > preferredBufferMaxCount)
			{
				logger.info("ByteBuffer Pool '" + name + "' has [" + byteBuffers.size() + "] buffers, more than preferred amount [" + preferredBufferMaxCount + "]");
				int buffersToRemove = byteBuffers.size() - preferredBufferMaxCount;
				logger.info("Removing [" + buffersToRemove + "] buffers from buffer pool '" + name + "'");
				// Want to start from the highest number because it is more efficient to remove
				// from the end of the list so pointers do not need to be reorganized.
				for(int i = byteBuffers.size() - 1; i >= preferredBufferMaxCount; i--)
				{
					logger.debug("Removing buffer at index [" + i + "] from buffer pool '" + name + "'");
					byteBuffers.remove(i);
				}
			}
			else
			{
				logger.debug("ByteBuffer pool '" + name + "' contains [" + byteBuffers.size() + "] buffers, less than [" + preferredBufferMaxCount + "] preferred buffers, not cleaning up");
			}
		}
	}

	/**
	 * Create a new buffer of the size this buffer list contains
	 * @return
	 */
	public ByteBuffer createNewBuffer(int bufferUseSize)
	{
		logger.info("Creating new ByteBuffer in '" + this + "'");
		if(this.maxBufferSizeInBytes <= 0)
		{
			return null;
		}
		createNewBufferCount++;
		this.bufferSizeUsage += bufferUseSize;
		ByteBuffer buffer = ByteBuffer.allocate(this.maxBufferSizeInBytes);
		return buffer;
	}

	/**
	 * @return the maxBufferSize
	 */
	public int getMaxBufferSizeInBytes() {
		return maxBufferSizeInBytes;
	}
	
	public ByteBuffer removeBufferFromList(int bufferUseSize)
	{
		synchronized (byteBuffers) 
		{			
			if(byteBuffers.size() > 0)
			{
				this.bufferSizeUsage += bufferUseSize;
				requestBufferCount++;
				return byteBuffers.remove(0);
			}
			return null;
		}
	}
	
	public boolean returnBufferToList(ByteBuffer buff)
	{
		synchronized (byteBuffers) 
		{
			returnBufferCount++;
			return byteBuffers.add(buff);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() 
	{
		return "ByteBufferPool named [" + this.name + "] currently holding [" + byteBuffers.size() + "] buffers";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ByteBufferPool that) 
	{
		if(this.maxBufferSizeInBytes < that.maxBufferSizeInBytes)
			return -1;
		if(this.maxBufferSizeInBytes > that.maxBufferSizeInBytes)
			return 1;
		return 0;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#getBufferListSize()
	 */
	@Override
	public int getBufferListSize() 
	{
		return this.byteBuffers.size();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#getBufferMaxFileSize()
	 */
	@Override
	public int getBufferMaxFileSize() 
	{
		return maxBufferSizeInBytes;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#getCreateNewBufferCount()
	 */
	@Override
	public int getCreateNewBufferCount() 
	{
		return createNewBufferCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#getRequestBufferCount()
	 */
	@Override
	public int getRequestBufferCount() 
	{
		return requestBufferCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#getReturnBufferCount()
	 */
	@Override
	public int getReturnBufferCount() 
	{
		return returnBufferCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#resetCounters()
	 */
	@Override
	public void resetCounters() 
	{
		this.requestBufferCount = 0;
		this.returnBufferCount = 0;
		this.createNewBufferCount = 0;
		this.bufferSizeUsage = 0;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#getMaximumBufferCount()
	 */
	@Override
	public int getMaximumBufferCount() 
	{
		return maxBufferCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferMinimumSizeListMBean#getPreferredBufferCount()
	 */
	@Override
	public int getPreferredBufferCount() 
	{
		return preferredBufferMaxCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferPoolMBean#getAverageBufferSizeUse()
	 */
	@Override
	public double getAverageBufferSizeUse() 
	{
		int count = requestBufferCount + createNewBufferCount;
		if(count > 0)
		{
			return (double)bufferSizeUsage / count;
		}
		return 0.0f;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.ByteBufferPoolMBean#getBufferSizeUse()
	 */
	@Override
	public long getBufferSizeUse() 
	{
		return bufferSizeUsage;
	}
}
