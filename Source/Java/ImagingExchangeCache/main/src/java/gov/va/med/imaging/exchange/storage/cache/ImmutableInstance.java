package gov.va.med.imaging.exchange.storage.cache;

import java.util.Date;

import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * A wrapper class so that the external cache decorators do not allow
 * changes to the cache contents.
 * 
 * @author vhaiswbeckec
 *
 */
public class ImmutableInstance
{
	private Instance wrappedInstance;
	public ImmutableInstance(Instance wrappedInstance)
	{
		this.wrappedInstance = wrappedInstance;
	}
	
	public Date getLastAccessed() 
	throws CacheException
	{
		return wrappedInstance.getLastAccessed();
	}
	
	public InstanceReadableByteChannel getReadableChannel() 
	throws CacheException
	{
		return wrappedInstance.getReadableChannel();
	}
	
	public long getSize() 
	throws CacheException
	{
		return wrappedInstance.getSize();
	}
	
	public InstanceWritableByteChannel getWritableChannel() throws CacheException
	{
		return wrappedInstance.getWritableChannel();
	}
	
	public String getChecksumValue()
	{
		return wrappedInstance.getChecksumValue();
	}
}
