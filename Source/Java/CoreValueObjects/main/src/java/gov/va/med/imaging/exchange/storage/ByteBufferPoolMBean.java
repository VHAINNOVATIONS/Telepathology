/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 2, 2008
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

/**
 * @author VHAISWWERFEJ
 *
 */
public interface ByteBufferPoolMBean 
{
	/**
	 * The size of the buffers in this pool 
	 * @return
	 */
	public abstract int getBufferMaxFileSize();
	
	/**
	 * Number of buffers in the pool
	 * @return
	 */
	public abstract int getBufferListSize();
	
	/**
	 * The number of times a buffer is requested from the buffer pool list.
	 * @return
	 */
	public abstract int getRequestBufferCount();
	
	/**
	 * The number of times a buffer is returned to the buffer pool list.
	 * @return
	 */
	public abstract int getReturnBufferCount();
	
	/**
	 * The number of times a new buffer needed to be created
	 * @return
	 */
	public abstract int getCreateNewBufferCount();
	
	/**
	 * Reset the counters
	 */
	public abstract void resetCounters();
	
	public abstract int getMaximumBufferCount();
	
	public abstract int getPreferredBufferCount();
	
	/**
	 * Average size (in bytes) of file put into buffer
	 * @return
	 */
	public abstract double getAverageBufferSizeUse();
	
	/**
	 * Total bytes put into this buffer
	 * @return
	 */
	public abstract long getBufferSizeUse();
	

}
