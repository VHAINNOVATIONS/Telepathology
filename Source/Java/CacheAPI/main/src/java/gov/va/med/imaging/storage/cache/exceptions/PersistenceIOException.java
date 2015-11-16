/**
 * 
 */
package gov.va.med.imaging.storage.cache.exceptions;

/**
 * @author VHAISWBECKEC
 * 
 * An exception that is thrown to indicate that a persistent copy of an instance or group
 * is not readable or writable.  For file based persistence instances of this class should be 
 * constructed using the single, Throwable, arg constructor wrapping an IOException instance.  
 */
public class PersistenceIOException 
extends PersistenceException
{
	private static final long serialVersionUID = 7266004726024496915L;

	public PersistenceIOException()
	{
		super();
	}

	public PersistenceIOException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PersistenceIOException(String message)
	{
		super(message);
	}

	public PersistenceIOException(Throwable cause)
	{
		super(cause);
	}

}
