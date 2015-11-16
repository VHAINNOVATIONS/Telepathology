/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jul 1, 2008
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
package gov.va.med.imaging.access;

import gov.va.med.imaging.exchange.enums.ByteTransferPath;
import gov.va.med.imaging.exchange.enums.ByteTransferType;

/**
 * @author VHAISWBECKEC
 *
 * A TransactionLogEntry derivation that may be used to build the
 * arithmetic mean of the bytesTransferred, elapsedTime and itemCount
 * properties of some number of TransactionLogEntry instances. 
 */
public class TransactionLogMaxima 
extends TransactionLogStatistics
{
	private ByteTransferPath byteTransferPath = ByteTransferPath.DS_IN_FACADE_OUT;
	private long maxBytesSent = 0L;
	private long maxBytesReceived = 0L;
	private long maxElapsedTime = 0L;
	private int maxItemCount = 0;
	private long maxStartTime = 0L;
	private int maxItemsReceived = 0;

	/**
	 * MT Constructor.  Assumes you're interested in DS Bytes Received, Facade Bytes Sent.
	 */
	public TransactionLogMaxima ()
	{
		this.byteTransferPath = ByteTransferPath.DS_IN_FACADE_OUT;
	}
	
	/**
	 * Instantiate with the type of bytes transferred path you're interested in gathering statistics on.  Choose from
	 * ByteTransferPath.DS_IN_FACADE_OUT or ByteTransferPath.FACADE_IN_DS_OUT.
	 * @param byteTransferPath The ByteTransferPath type of bytes transferred path that you're interested in gathering statistics on.
	 */
	public TransactionLogMaxima (ByteTransferPath byteTransferPath)
	{
		this.byteTransferPath = byteTransferPath;
	}
	
	/**
	 * @see gov.va.med.imaging.access.TransactionLogStatistics#update(gov.va.med.imaging.access.TransactionLogEntry)
	 */
	@Override
	public void update(TransactionLogEntry entry)
	{
		if(entry == null)
			return;

		Long bytesSent = null;
		Long bytesReceived = null;
		
		switch (byteTransferPath)
		{
		   case DS_IN_FACADE_OUT:
			    bytesSent = getByteCount (entry, ByteTransferType.FACADE_BYTES_SENT);
			    bytesReceived = getByteCount (entry, ByteTransferType.DATASOURCE_BYTES_RECEIVED);
		        break;
		        
		   case FACADE_IN_DS_OUT:
			    bytesSent = getByteCount (entry, ByteTransferType.DATASOURCE_BYTES_SENT);
			    bytesReceived = getByteCount (entry, ByteTransferType.FACADE_BYTES_RECEIVED);
		        break;
		        
           default:
		        bytesSent = null;
	            bytesReceived = null;
	            break;
		}
           
		maxBytesSent = 
			Math.max(maxBytesSent, bytesSent == null ? 0 : bytesSent.longValue() );
		maxBytesReceived = 
			Math.max(maxBytesReceived, bytesReceived == null ? 0 : bytesReceived.longValue() );
		maxElapsedTime =
			Math.max(maxElapsedTime, entry.getElapsedTime() == null ? 0 : entry.getElapsedTime().longValue() );
		maxItemCount =
			Math.max(maxItemCount, entry.getItemCount() == null ? 0 : entry.getItemCount().intValue() );
		maxStartTime = 
			Math.max(maxStartTime, entry.getStartTime() == null ? 0 : entry.getStartTime() );
		maxItemsReceived =
			Math.max(maxItemsReceived, entry.getDataSourceItemsReceived() == null ? 0 : entry.getDataSourceItemsReceived());
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesSent()
	 */
	@Override
	public Long getFacadeBytesSent()
	{
		return maxBytesSent;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesReceived()
	 */
	@Override
	public Long getFacadeBytesReceived()
	{
		return maxBytesReceived;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesSent()
	 */
	@Override
	public Long getDataSourceBytesSent()
	{
		return maxBytesSent;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesReceived()
	 */
	@Override
	public Long getDataSourceBytesReceived()
	{
		return maxBytesReceived;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getElapsedTime()
	 */
	@Override
	public Long getElapsedTime()
	{
		return maxElapsedTime;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getItemCount()
	 */
	@Override
	public Integer getItemCount()
	{
		return maxItemCount;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getStartTime()
	 */
	@Override
	public Long getStartTime()
	{
		return maxStartTime;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getTransactionId()
	 */
	@Override
	public String getTransactionId()
	{
		return "Maxima";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceItemsReceived()
	 */
	@Override
	public Integer getDataSourceItemsReceived() 
	{
		return maxItemsReceived;
	}
}
