/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 14, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.exchange.storage.cache.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.va.med.imaging.GUID;
import gov.va.med.imaging.storage.cache.Instance;
import gov.va.med.imaging.storage.cache.InstanceReadableByteChannel;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.events.InstanceLifecycleListener;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 *
 */
public class MockInstance 
implements Instance
{
	private List<InstanceLifecycleListener> listeners;
	private String name;
	private Date lastAccessed;
	
	MockInstance()
    {
		this(new GUID().toString());
    }

	MockInstance(String name)
	{
	    super();
		this.name = name;
	    listeners = new ArrayList<InstanceLifecycleListener>();
	    lastAccessed = new Date();
	}
	
	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getChecksumValue()
	 */
	@Override
	public String getChecksumValue()
	{
		return "42";
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getLastAccessed()
	 */
	@Override
	public Date getLastAccessed() 
	throws CacheException
	{
		return lastAccessed;
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getReadableChannel()
	 */
	@Override
	public InstanceReadableByteChannel getReadableChannelNoWait() throws CacheException
	{
		return new MockInstanceReadableByteChannel();
	}
	
	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getReadableChannel()
	 */
	@Override
	public InstanceReadableByteChannel getReadableChannel() throws CacheException
	{
		return new MockInstanceReadableByteChannel();
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getSize()
	 */
	@Override
	public long getSize() throws CacheException
	{
		return 42;
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getWritableChannel()
	 */
	@Override
	public InstanceWritableByteChannel getWritableChannelNoWait() throws CacheException
	{
		return new MockInstanceWritableByteChannel();
	}
	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#getWritableChannel()
	 */
	@Override
	public InstanceWritableByteChannel getWritableChannel() throws CacheException
	{
		return new MockInstanceWritableByteChannel();
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#isPersistent()
	 */
	@Override
	public boolean isPersistent() throws CacheException
	{
		return false;
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#delete()
	 */
	@Override
	public void delete(boolean forceDelete) throws CacheException
	{
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#registerListener(gov.va.med.imaging.storage.cache.events.InstanceLifecycleListener)
	 */
	@Override
	public void registerListener(InstanceLifecycleListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.Instance#unregisterListener(gov.va.med.imaging.storage.cache.events.InstanceLifecycleListener)
	 */
	@Override
	public void unregisterListener(InstanceLifecycleListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public String getMediaType() 
	{
		return null;
	}

}
