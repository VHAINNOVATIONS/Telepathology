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

import java.util.SortedSet;
import java.util.TreeSet;
import gov.va.med.imaging.exchange.enums.ByteTransferPath;
import gov.va.med.imaging.exchange.enums.ByteTransferType;

/**
 * @author VHAISWBECKEC
 *
 * A TransactionLogEntry derivation that may be used to build the
 * arithmetic mean of the bytesTransferred, elapsedTime and itemCount
 * properties of some number of TransactionLogEntry instances. 
 */
public class TransactionLogMedian 
extends TransactionLogStatistics
{
	private ByteTransferPath byteTransferPath = ByteTransferPath.DS_IN_FACADE_OUT;
	private SortedSet<Long> numBytesSent = new TreeSet<Long>();
	private SortedSet<Long> numBytesReceived = new TreeSet<Long>();
	private SortedSet<Long> elapsedTime = new TreeSet<Long>();
	private SortedSet<Integer> itemCount = new TreeSet<Integer>();
	private SortedSet<Integer> dataSourceItemsReceived = new TreeSet<Integer>();
	
	/**
	 * MT Constructor.  Assumes you're interested in DS Bytes Received, Facade Bytes Sent.
	 */
	public TransactionLogMedian ()
	{
		this.byteTransferPath = ByteTransferPath.DS_IN_FACADE_OUT;
	}
	
	/**
	 * Instantiate with the type of bytes transferred path you're interested in gathering statistics on.  Choose from
	 * ByteTransferPath.DS_IN_FACADE_OUT or ByteTransferPath.FACADE_IN_DS_OUT.
	 * @param byteTransferPath The ByteTransferPath type of bytes transferred path that you're interested in gathering statistics on.
	 */
	public TransactionLogMedian (ByteTransferPath byteTransferPath)
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
           
		if( bytesSent != null)
			numBytesSent.add( bytesSent );
		
		if( bytesReceived != null)
			numBytesReceived.add( bytesReceived );
		
		if( entry.getElapsedTime() != null)
			elapsedTime.add( entry.getElapsedTime() );
		
		if( entry.getItemCount() != null)
			itemCount.add( entry.getItemCount() );
		
		if(entry.getDataSourceItemsReceived() != null)
			dataSourceItemsReceived.add(entry.getDataSourceItemsReceived());
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesSent()
	 */
	@Override
	public Long getFacadeBytesSent()
	{
		return calculateMedian(numBytesSent).longValue();
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesReceived()
	 */
	@Override
	public Long getFacadeBytesReceived()
	{
		return calculateMedian(numBytesReceived).longValue();
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesSent()
	 */
	@Override
	public Long getDataSourceBytesSent()
	{
		return calculateMedian(numBytesSent).longValue();
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesReceived()
	 */
	@Override
	public Long getDataSourceBytesReceived()
	{
		return calculateMedian(numBytesReceived).longValue();
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getElapsedTime()
	 */
	@Override
	public Long getElapsedTime()
	{
		return calculateMedian(elapsedTime).longValue();
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getItemCount()
	 */
	@Override
	public Integer getItemCount()
	{
		return calculateMedian(itemCount).intValue();
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getTransactionId()
	 */
	@Override
	public String getTransactionId()
	{
		return "Median";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceItemsReceived()
	 */
	@Override
	public Integer getDataSourceItemsReceived() 
	{
		return calculateMedian(dataSourceItemsReceived).intValue();
	}

	private Double calculateMedian(SortedSet<? extends Number> sampleSet)
    {
		if(sampleSet == null)
			return new Double(0);
		if(sampleSet.size() == 0)
			return new Double(0);
		if(sampleSet.size() == 1)
			return new Double(sampleSet.first().doubleValue());
		
		boolean evenSizedSampleSet = (sampleSet.size() % 2) == 0; 
		int mid = sampleSet.size() / 2;	// either the index of the mid point element of an odd sized set
										// or the lesser index of the two midpoint elements of an even sized set 

		int index = 0;
		double midpointMean = 0.0D;
		for(Number sample : sampleSet)
		{
			if(index == mid)
			{
				if(!evenSizedSampleSet)
					return sample.doubleValue();
				else
					midpointMean = sample.doubleValue();
			}
			else if(index == mid+1)
			{
				midpointMean = (midpointMean + sample.doubleValue())/2.0D;
				return midpointMean;
			}
			++index;
		}
		
	    return Double.NaN;	// should never get here
    }
}
