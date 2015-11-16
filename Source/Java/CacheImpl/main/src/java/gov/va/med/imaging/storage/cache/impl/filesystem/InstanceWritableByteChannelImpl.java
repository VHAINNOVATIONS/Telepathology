package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.StackTraceAnalyzer;
import gov.va.med.imaging.storage.cache.InstanceWritableByteChannel;
import gov.va.med.imaging.storage.cache.TracableComponent;
import gov.va.med.imaging.storage.cache.exceptions.SimultaneousWriteException;
import gov.va.med.imaging.storage.cache.impl.AbstractByteChannelFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.zip.Checksum;

/**
 * A class that simply wraps a FileChannel that will be used for writing,
 * and releases a lock when the channel is closed.
 * 
 * @author VHAISWBECKEC
 *
 */
public class InstanceWritableByteChannelImpl 
implements InstanceWritableByteChannel, TracableComponent
{
	/**
	 * 
	 */
	private final AbstractByteChannelFactory factory;
	private final File file;
	private final FileChannel wrappedChannel;
	private FileLock lock = null;
	private long openedTime = 0L;					// keep this so that we could close the files ourselves if the client does not
	private long lastAccessedTime = 0L;
	private Checksum checksum;
	private StackTraceElement[] instantiatingStackTrace = null;
	
	InstanceWritableByteChannelImpl(AbstractByteChannelFactory factory, File file) 
	throws FileNotFoundException, SimultaneousWriteException, IOException
	{
		this(factory, file, null);
	}
	
	public InstanceWritableByteChannelImpl(AbstractByteChannelFactory factory, File file, Checksum checksum) 
	throws FileNotFoundException, SimultaneousWriteException, IOException
	{
		this.factory = factory;
		this.file = file;
		this.checksum = checksum;
		
		this.factory.getLogger().debug("InstanceWritableByteChannelImpl, opening '" + file.getPath() + "'" );
		if(this.factory.isTraceChannelInstantiation())
			instantiatingStackTrace = Thread.currentThread().getStackTrace();
		this.wrappedChannel = (new FileOutputStream(file)).getChannel();
		//lock = wrappedChannel.tryLock(0L, Long.MAX_VALUE, false);		// lock the file for exclusive access
		//if(lock == null)
		//{
		//	try{wrappedChannel.close();}catch(Exception x){}
		//	try{outStream.close();}catch(Exception x){}
		//	throw new SimultaneousWriteException();
		//}
		openedTime = System.currentTimeMillis();
		lastAccessedTime = openedTime;
	}
	
	File getFile()
	{
		return this.file;
	}
	
	@Override
	public StackTraceElement[] getInstantiatingStackTrace()
	{
		return instantiatingStackTrace;
	}
	
	public long getLastAccessedTime()
	{
		return lastAccessedTime;
	}
	
	public Checksum getChecksum()
	{
		return this.checksum;
	}

	public int write(ByteBuffer src) 
	throws IOException
	{
		Checksum localChecksumRef = getChecksum();		// just for performance
		if(localChecksumRef != null)
			for(ByteBuffer localBuffer = src.asReadOnlyBuffer(); localBuffer.hasRemaining(); localChecksumRef.update(localBuffer.get()) );
		
		lastAccessedTime = System.currentTimeMillis();
		return wrappedChannel.write(src);
	}
	
	public void close() 
	throws IOException
	{
		close(false);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.InstanceWritableByteChannel#error()
	 */
	public void error() 
	throws IOException
	{
		close(true);
	}
	
	private void close(boolean errorClose) 
	throws IOException
	{
		String closingClassMsg = null;
		try
		{
			StackTraceAnalyzer analyzer = new StackTraceAnalyzer(StackTraceAnalyzer.currentStack());
			StackTraceElement closer = analyzer.getFirstElementNotInClass(this.getClass().getName());
			closingClassMsg = closer.getClassName() + "." + closer.getMethodName() + "[" + closer.getLineNumber() + "]";
		}
		catch (Throwable x)
		{
		}
		this.factory.getLogger().debug("InstanceWritableByteChannelImpl, closing '" + file.getPath() + 
			"' " + (errorClose ? "WITH" : "without") + " delete " + 
			"by " + closingClassMsg + ".");
		IOException ioX = null;
		
		if(wrappedChannel.isOpen())
		{
			try{wrappedChannel.force(true);}		// force the file contents to disk
			catch(IOException forceIoX)
			{this.factory.getLogger().warn("Unable to force writable channel for instance file '" + this.getFile().getPath() + "'.", forceIoX); ioX = forceIoX;}
			
			try{if(lock != null) lock.release();}				// the lock release must occur before the close
			catch(IOException e)
			{this.factory.getLogger().warn("Unable to release lock for instance file '" + this.getFile().getPath() + "'.", e); ioX = e;} // the lock may already be released through some error or other timeout, log it but keep going
			
			try{ wrappedChannel.close(); }
			catch(IOException e)
			{this.factory.getLogger().warn("Unable to close writable channel for instance file '" + this.getFile().getPath() + "'.", e);} // the channel may already be closed through some error or other timeout, log it but keep going
		}
		else
		{
			String message = "Unable to close writable channel for instance file '" + this.getFile().getPath() + "', channel was already closed.";
			this.factory.getLogger().warn(message);
			throw new IOException(message);
		}
		
		if( errorClose )
			if(! this.file.delete())
				this.factory.getLogger().error("Unable to delete persistent cache item, file '" + (file.getAbsolutePath()) + "' may be corrupt and should be manually deleted." );
		
		// the following operation really must occur regardless of the 
		// success of the previous IO operations, else threads will lock and byte channels will repeatedly be closed when they are already closed
		this.factory.writableByteChannelClosed(this, errorClose);
		
		this.factory.getLogger().debug("InstanceWritableByteChannelImpl - '" + file.getPath() + "' closed " + (errorClose ? "WITH" : "without") + " delete");
		if(ioX != null)
			throw ioX;
	}
	
	public boolean isOpen()
	{
		return wrappedChannel != null && wrappedChannel.isOpen();
	}
}