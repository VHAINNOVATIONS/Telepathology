package gov.va.med.imaging.storage.cache;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface InstanceWritableByteChannel
extends WritableByteChannel
{

	/**
	 * An abnormal close when an error has occured and the contents of the
	 * cached item are corrupt.
	 * Realizing classes must remove the persistent representation when this
	 * method is called.  More specifically, the Instance.isPersistent() method 
	 * must return false after this method is called.
	 * 
	 * @throws IOException
	 */
	public abstract void error() throws IOException;
	
	/**
	 * Get the Checksum instance that was associated to this channel instance, if
	 * one was assigned when the channel instance was created, or null if none 
	 * was assigned.
	 * 
	 * @return
	 */
	public java.util.zip.Checksum getChecksum();

	/**
	 * Return the time that this instance was last accessed.
	 * @return
	 */
	public abstract long getLastAccessedTime();
	
	/**
	 * 
	 * @return
	 */
	public abstract StackTraceElement[] getInstantiatingStackTrace();
}