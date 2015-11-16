/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceFactory;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * THIS CLASS IS NOT USED YET
 * 
 * @author VHAISWBECKEC
 *
 */
public class FileSystemInstanceFactory 
extends InstanceFactory
{
	private FileSystemByteChannelFactory byteChannelFactory;
	
	public FileSystemInstanceFactory(FileSystemByteChannelFactory byteChannelFactory)
	{
		super();
		this.byteChannelFactory = byteChannelFactory;
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.InstanceFactory#create(java.lang.String)
	 */
	@Override
	public Instance create(String name) 
	throws CacheException
	{
		return null;
	}

}
