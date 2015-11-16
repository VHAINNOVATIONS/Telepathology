/**
 * 
 */
package gov.va.med.imaging.storage.cache;

/**
 * A class implementing this interface collects information from the stack trace
 * on its creation.  On demand, this information may be made available,
 * usually for debugging.
 * The original intent of this interface is to track where an instance was created
 * when it may be destroyed by another thread.  In particular, instance bytes channels
 * may be closed by a cleanup thread if they are inactive too long. 
 *  
 * @author vhaiswbeckec
 *
 */
public interface TracableComponent
{
	public StackTraceElement[] getInstantiatingStackTrace();
}
