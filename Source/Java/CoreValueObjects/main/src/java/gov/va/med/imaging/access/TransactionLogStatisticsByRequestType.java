/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 15, 2008
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Accumulates and calculates the transaction entry statistics, grouped by request type:
 * metadata, thumbnail, reference, diagnostic
 * 
 * @author VHAISWBECKEC
 *
 */
public class TransactionLogStatisticsByRequestType 
extends TransactionLogStatistics
{
	enum REQUEST_TYPE {STUDY_LIST, IMAGE_THUMBNAIL, IMAGE_REFERENCE, IMAGE_DIAGNOSTIC};
	
	private Map<RequestGroup, StatisticsByRequestType> groupStatistics = 
		new HashMap<RequestGroup, StatisticsByRequestType>();
	
	/**
	 * 
	 */
	public TransactionLogStatisticsByRequestType()
	{
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogStatistics#update(gov.va.med.imaging.access.TransactionLogEntry)
	 */
	@Override
	public void update(TransactionLogEntry entry)
	{
		entry.getQueryType();
	}

	/**
	 * Simple value objects that define the grouping of the statistics.
	 */
	class RequestGroup
	{
		private final String protocol;
		private final REQUEST_TYPE requestType;
		
		RequestGroup(String protocol, REQUEST_TYPE requestType)
        {
	        super();
	        this.protocol = protocol;
	        this.requestType = requestType;
        }

		protected String getProtocol()
        {
        	return protocol;
        }

		protected REQUEST_TYPE getRequestType()
        {
        	return requestType;
        }
	}
	
	/**
	 * NOT THREAD SAFE
	 * 
	 * @author VHAISWBECKEC
	 */
	class StatisticsByRequestType
	{
		private int count = 0;
		private int countCached = 0;
		private long totalBytes = 0l;
		private long totalBytesCached = 0l;
		private long totalDuration = 0l;
		private long totalDurationCached = 0l;
		private Map<String, Integer> modalityCountMap = new HashMap<String, Integer>();
		
		StatisticsByRequestType(){}

		void incrementStatistics(boolean cached, long bytes, long duration, String modality)
		{
			if(cached)
			{
				++countCached;
				totalBytesCached += bytes;
				totalDurationCached += duration;
			}
			
			++count;
			totalBytes += bytes;
			totalDuration += duration;
			Integer modalityCount = modalityCountMap.get(modality);
			if(modalityCount == null)
				modalityCountMap.put(modality, new Integer(1));
			else
				modalityCountMap.put(modality, new Integer(modalityCount.intValue() + 1));
		}
		
		protected int getCount()
        {
        	return count;
        }

		protected int getCountCached()
        {
        	return countCached;
        }

		protected long getTotalBytes()
        {
        	return totalBytes;
        }

		protected long getTotalBytesCached()
        {
        	return totalBytesCached;
        }

		protected long getTotalDuration()
        {
        	return totalDuration;
        }

		protected long getTotalDurationCached()
        {
        	return totalDurationCached;
        }
		
		protected Iterator<String> getModalities()
		{
			return modalityCountMap.keySet().iterator();
		}
		
		protected Integer getCountByModality(String modality)
		{
			return modalityCountMap.get(modality);
		}
	}
}
