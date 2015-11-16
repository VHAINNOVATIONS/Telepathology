/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 1, 2008
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

import gov.va.med.imaging.ImagingMBean;
import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.exchange.storage.ByteBufferPool;
import gov.va.med.imaging.exchange.storage.KnownSizeByteBuffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

/**
 * Manager of the byte buffer list, finds the correct buffer list to use.
 * 
 * @author VHAISWWERFEJ
 * @deprecated Buffer pool no longer used, use DataSourceByteBufferPoolFactory instead
 *
 */
@Deprecated
public class DataSourceByteBufferPoolManager 
implements DataSourceByteBufferPoolManagerMBean
{

	private final static Logger logger = Logger.getLogger(DataSourceByteBufferPoolManager.class);
	
	private Set<ByteBufferPool> buffers = null;
	private int overloadedBufferRequestsCount = 0;
	private int highestOverloadedBufferRequestSize = 0;
	
	public DataSourceByteBufferPoolManager()
	{
		//createBufferLists();
		// change to load buffers from file
	}
	
	private static DataSourceByteBufferPoolManager manager = null;
	
	public synchronized static DataSourceByteBufferPoolManager getByteBufferPoolManager()
	{
		if(manager == null)
		{
			manager = new DataSourceByteBufferPoolManager();
			manager.initializeBufferPoolManager();
			registerResourceMBeans();
		}
		return manager;
	}
	
	/**
	 * Initialize the buffer pools, either from the configuration file or using default lists
	 */
	private void initializeBufferPoolManager()
	{
		manager.createBufferLists();
	}
	
	private List<BufferConfiguration> getBufferConfigurations()
	{
		return ByteBufferPoolConfiguration.getByteBufferPoolConfiguration().getBuffers();
	}
	
	private void createBufferLists()
	{
		logger.info("Creating buffers in buffer pool list");
		buffers = new TreeSet<ByteBufferPool>();
		
		for(BufferConfiguration configuration : getBufferConfigurations())
		{
			buffers.add(ByteBufferPool.createByteBufferList(configuration.getBufferListName(), 
				configuration.getMaxBufferSize(), configuration.getOptimalBufferCount(),
				configuration.getInitialBuffers()));
		}
	}
	
	private Set<ByteBufferPool> getBuffers()
	{
		return buffers;
	}
	
	/**
	 * Read the input stream into a buffer. This function gets a buffer to use based on the known size
	 * of the input data
	 * 
	 * @param identifier
	 * @param inputStream
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public KnownSizeByteBuffer readIntoBuffer(String identifier, InputStream inputStream, 
		int size)
	throws IOException
	{
		if(size > 0)
		{
			logger.info("Reading file [" + identifier + "] into buffer of size [" + size + "]");
			ByteBuffer storeBuffer = getBuffer(size);
			
			byte[] byteBuffer = new byte[1024];
			int len;
			int bytesRead = 0;
	        while ((len = inputStream.read(byteBuffer)) > 0) 
	        {
	        	storeBuffer.put(byteBuffer, 0, len);
	        	bytesRead += len;
	        }
	        logger.info("Done reading file [" + identifier + "] into buffer, read [" + bytesRead + "] bytes");
	        return new KnownSizeByteBuffer(identifier, storeBuffer, bytesRead);
		}
		else
		{
			return readIntoBuffer(identifier, inputStream);
		}
	}
	
	/**
	 * Read the input stream into a buffer. This function reads the data into a temporary buffer
	 * before putting it into a buffer pool buffer because the size of the data is unknown.
	 * 
	 * @param identifier
	 * @param inputStream
	 * @return
	 * @throws IOException occurs if there is an error reading the image into the buffer
	 */
	public KnownSizeByteBuffer readIntoBuffer(String identifier, InputStream inputStream)
	throws IOException
	{
		logger.info("Reading file [" + identifier + "] into buffer of unknown size");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] byteBuffer = new byte[1024];
		int len;
		int bytesRead = 0;
        while ((len = inputStream.read(byteBuffer)) > 0) {
        	bytesRead += len;
        	buffer.write(byteBuffer, 0, len);
        }
        ByteBuffer storeBuffer = getBuffer(bytesRead);
        storeBuffer.put(buffer.toByteArray());
        logger.info("Done reading file [" + identifier + "] into buffer, read [" + bytesRead + "] bytes");
        return new KnownSizeByteBuffer(identifier, storeBuffer, bytesRead);
	}
	
	/**
	 * Opens an SizedInputStream to the buffer
	 * 
	 * @param buffer
	 * @return
	 */
	public SizedInputStream openStreamToBuffer(KnownSizeByteBuffer buffer)
	{
		logger.info("Opening sized input stream to buffer [" + buffer.getIdentifier() + "]");
		ByteArrayInputStream inStream = new ByteArrayInputStream(buffer.getBuffer().array(), 0, buffer.getKnownSize());
        SizedInputStream sizedStream = new SizedInputStream(inStream, buffer.getKnownSize());
        return sizedStream;
	}
	
	/**
	 * Gets a buffer to use based on the size of the data being stored. It will get the buffer from one 
	 * of the pools selecting the smallest buffer that meets the size.  If no pool can meet the size 
	 * requirement, then a new buffer of the exact requested size is created.
	 * 
	 * @param minSize
	 * @return
	 */
	private ByteBuffer getBuffer(int minSize)
	{
		logger.info("Retrieving buffer from cache of minimum size [" + minSize + "]");
		ByteBufferPool bufferList = getBufferToUse(minSize);
		if(bufferList == null)
		{
			logger.error("Creating buffer of size [" + minSize + "], not associated with a buffer list, new buffer list of minimum size might be needed");
			overloadedBufferRequestsCount++;
			if(minSize > highestOverloadedBufferRequestSize)
				highestOverloadedBufferRequestSize = minSize;
			return ByteBuffer.allocate(minSize); 
		}
		
		synchronized (bufferList) 
		{
			if(bufferList.getBufferListSize() > 0)
			{
				logger.info("Retreiving existing buffer from buffer list pool '" + bufferList + "'");
				return bufferList.removeBufferFromList(minSize);
			}
			else
			{
				return bufferList.createNewBuffer(minSize);
			}
		}
	}
	
	/**
	 * Determine what buffer to use based on the size of the request. If no buffer meets the minimum
	 * size request, null is returned.
	 * 
	 * @param minimumSize
	 * @return
	 */
	private ByteBufferPool getBufferToUse(int minimumSize)
	{		
		for(ByteBufferPool buffy : getBuffers())
		{
			if(minimumSize < buffy.getMaxBufferSizeInBytes())
			{
				return buffy;
			}
		}
		return null;
	}
	
	/**
	 * Determine the buffer pool list to release the buffer to. This is based on the size of the buffer.
	 * If there is no pool for the specified size, then null is returned
	 * 
	 * @param bufferSize
	 * @return
	 */
	private ByteBufferPool getBufferListToReleaseTo(int bufferSize)
	{
		for(ByteBufferPool buffy : getBuffers())
		{
			if(bufferSize == buffy.getMaxBufferSizeInBytes())
			{
				return buffy;
			}
		}
		return null;
		/*
		if(bufferSize == buffer100k.getMaxBufferSizeInBytes())
		{
			return buffer100k;
		}
		else if(bufferSize ==  buffer1024k.getMaxBufferSizeInBytes())
		{
			return buffer1024k;
		}
		else if(bufferSize ==  buffer5120k.getMaxBufferSizeInBytes())
		{
			return buffer5120k;
		}
		else if(bufferSize ==  buffer10240k.getMaxBufferSizeInBytes())
		{
			return buffer10240k;
		}
		else
		{
			return null;
		}
		*/
	}

	/**
	 * Release the buffer back into the correct pool (if there is one).
	 * @param buffer
	 */
	public void releaseBuffer(ByteBuffer buffer)
	{
		logger.info("releasing buffer back into cache");
		ByteBufferPool bufferToReleaseTo = getBufferListToReleaseTo(buffer.capacity());
		// if the buffer has a size too big for any in the pool, don't want to put it in the pool because
		// it will have a random size and will make finding it more difficult
		if(bufferToReleaseTo != null)
		{
			logger.info("Putting buffer into buffer pool '" + bufferToReleaseTo + "'");
			buffer.clear();
			synchronized (bufferToReleaseTo) 
			{
				bufferToReleaseTo.returnBufferToList(buffer);
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getBufferPoolCount()
	 */
	@Override
	public int getBufferPoolCount() 
	{
		return buffers.size();
	}
	
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getOverloadedBufferSizeRequests()
	 */
	@Override
	public int getOverloadedBufferSizeRequests() 
	{
		return overloadedBufferRequestsCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getHighestOverloadedBufferSizeRequest()
	 */
	@Override
	public int getHighestOverloadedBufferSizeRequest() 
	{
		return highestOverloadedBufferRequestSize;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getBufferNames()
	 */
	@Override
	public String getBufferNames() 
	{
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for(ByteBufferPool pool : buffers)
		{
			sb.append(prefix);
			sb.append(pool.getName());
			prefix= ",";
		}
		return sb.toString();
	}

	private static ObjectName bufferPoolManagerMBeanName = null;
	/**
	 * This method should only be called once, else MBean exceptions will occur.
	 */
	private synchronized static void registerResourceMBeans()
    {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		
		if(bufferPoolManagerMBeanName == null)
		{
			DataSourceByteBufferPoolManager bufferManager = getByteBufferPoolManager();
			if(bufferManager instanceof DataSourceByteBufferPoolManagerMBean)
			{
				try
	            {
					// VistaImaging.ViX:type=Cache,name=ImagingExchangeCache
					Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
					mBeanProperties.put( "type", "ByteBufferPoolManager" );
					//mBeanProperties.put( "name", "Manager-" + Integer.toHexString(bufferManager.hashCode()) );
					mBeanProperties.put( "name", "Manager");
		            bufferPoolManagerMBeanName = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
		            mBeanServer.registerMBean(bufferManager, bufferPoolManagerMBeanName);
		            
		            for(ByteBufferPool buffer : bufferManager.getBuffers())
		            {
		            	mBeanProperties = new Hashtable<String, String>();
						mBeanProperties.put( "type", "ByteBufferPoolManager" );
						mBeanProperties.put( "name", buffer.getName() );
			            bufferPoolManagerMBeanName = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
			            mBeanServer.registerMBean(buffer, bufferPoolManagerMBeanName);
		            }
	            } 
				catch (Exception e){ Logger.getLogger(DataSourceByteBufferPoolManager.class).error(e.toString()); }
			}
		}
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getTotalCreateNewBufferCount()
	 */
	@Override
	public int getTotalCreateNewBufferCount() 
	{
		int count = 0;
		for(ByteBufferPool bufferPool : getBuffers())
		{
			count += bufferPool.getCreateNewBufferCount();
		} 
		return count;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getTotalRequestBufferCount()
	 */
	@Override
	public int getTotalRequestBufferCount() 
	{
		int count = 0;
		for(ByteBufferPool bufferPool : getBuffers())
		{
			count += bufferPool.getRequestBufferCount();
		} 
		return count;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getTotalReturnBufferCount()
	 */
	@Override
	public int getTotalReturnBufferCount() 
	{
		int count = 0;
		for(ByteBufferPool bufferPool : getBuffers())
		{
			count += bufferPool.getReturnBufferCount();
		} 
		return count;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#getTotalBufferSizeUse()
	 */
	@Override
	public long getTotalBufferSizeUse() 
	{
		int count = 0;
		for(ByteBufferPool bufferPool : getBuffers())
		{
			count += bufferPool.getBufferSizeUse();
		} 
		return count;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.storage.DataSourceByteBufferPoolManagerMBean#resetCounters()
	 */
	@Override
	public void resetCounters() 
	{
		for(ByteBufferPool bufferPool : getBuffers())
		{
			bufferPool.resetCounters();
		} 
	}	
}
