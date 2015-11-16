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

import java.io.Serializable;

/**
 * Contains the configuration for a buffer pool.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class BufferConfiguration 
implements Serializable
{
	private static final long serialVersionUID = 919662636888825646L;
	
	private String bufferListName;
	private int maxBufferSize;
	private int initialBuffers;
	private int optimalBufferCount;
	private int maxBufferCount;
	
	public BufferConfiguration()
	{
		super();
	}
	
	public BufferConfiguration(String bufferListName,
			int maxBufferSize, int initialBuffers, int optimalBufferCount,
			int maxBufferCount) {
		super();
		this.bufferListName = bufferListName;
		this.maxBufferSize = maxBufferSize;
		this.initialBuffers = initialBuffers;
		this.optimalBufferCount = optimalBufferCount;
		this.maxBufferCount = maxBufferCount;
	}

	/**
	 * Returns the name of the buffer pool list
	 * 
	 * @return the bufferListName
	 */
	public String getBufferListName() {
		return bufferListName;
	}

	/**
	 * @param bufferListName the bufferListName to set
	 */
	public void setBufferListName(String bufferListName) {
		this.bufferListName = bufferListName;
	}

	/**
	 * Returns the size of each buffer in the list (the number of bytes for each buffer).
	 * 
	 * @return the maxBufferSize
	 */
	public int getMaxBufferSize() {
		return maxBufferSize;
	}

	/**
	 * @param maxBufferSize the maxBufferSize to set
	 */
	public void setMaxBufferSize(int maxBufferSize) {
		this.maxBufferSize = maxBufferSize;
	}

	/**
	 * Returns the number of buffers in the list created when the pool is created
	 * 
	 * @return the initialBuffers
	 */
	public int getInitialBuffers() {
		return initialBuffers;
	}

	/**
	 * @param initialBuffers the initialBuffers to set
	 */
	public void setInitialBuffers(int initialBuffers) {
		this.initialBuffers = initialBuffers;
	}

	/**
	 * Returns the optimal number of buffers in the pool (the number the pool will be reduced to
	 * during a cleanup).
	 * 
	 * @return the optimalBufferCount
	 */
	public int getOptimalBufferCount() {
		return optimalBufferCount;
	}

	/**
	 * @param optimalBufferCount the optimalBufferCount to set
	 */
	public void setOptimalBufferCount(int optimalBufferCount) {
		this.optimalBufferCount = optimalBufferCount;
	}

	/**
	 * Returns the maximum size of the buffer pool list (this field is not actually used)
	 * 
	 * @return the maxBufferCount
	 */
	public int getMaxBufferCount() {
		return maxBufferCount;
	}

	/**
	 * @param maxBufferCount the maxBufferCount to set
	 */
	public void setMaxBufferCount(int maxBufferCount) {
		this.maxBufferCount = maxBufferCount;
	}
}
