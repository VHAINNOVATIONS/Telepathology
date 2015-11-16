package gov.va.med.imaging.business.storage.hibernate;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.util.Date;

public class QueueMessage implements Serializable
{
	/** 
	 * This attribute maps to the column IEN in the QueueMessage table.
	 */
	protected int ien;

	/** 
	 * This attribute maps to the column Priority in the QueueMessage table.
	 */
	protected int priority;

	/** 
	 * This attribute represents whether the primitive attribute priority is null.
	 */
	protected boolean priorityNull = true;

	/** 
	 * This attribute maps to the column EnqueuedTimestamp in the QueueMessage table.
	 */
	protected Date enqueuedTimestamp;

	/** 
	 * This attribute maps to the column MinDeliveryDateTime in the QueueMessage table.
	 */
	protected Date minDeliveryDateTime;

	/** 
	 * This attribute maps to the column ExpirationDateTime in the QueueMessage table.
	 */
	protected Date expirationDateTime;

	/** 
	 * This attribute represents the foreign key relationship to the Queue table.
	 */
	protected Queue queue;

	/**
	 * Method 'QueueMessage'
	 * 
	 */
	public QueueMessage()
	{
	}

	/**
	 * Method 'getIen'
	 * 
	 * @return int
	 */
	public int getIen()
	{
		return ien;
	}

	/**
	 * Method 'setIen'
	 * 
	 * @param ien
	 */
	public void setIen(int ien)
	{
		this.ien = ien;
	}

	/**
	 * Method 'getPriority'
	 * 
	 * @return int
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * Method 'setPriority'
	 * 
	 * @param priority
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
		this.priorityNull = false;
	}

	/** 
	 * Sets the value of priorityNull
	 */
	public void setPriorityNull(boolean priorityNull)
	{
		this.priorityNull = priorityNull;
	}

	/** 
	 * Gets the value of priorityNull
	 */
	public boolean isPriorityNull()
	{
		return priorityNull;
	}

	/**
	 * Method 'getEnqueuedTimestamp'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getEnqueuedTimestamp()
	{
		return enqueuedTimestamp;
	}

	/**
	 * Method 'setEnqueuedTimestamp'
	 * 
	 * @param enqueuedTimestamp
	 */
	public void setEnqueuedTimestamp(java.util.Date enqueuedTimestamp)
	{
		this.enqueuedTimestamp = enqueuedTimestamp;
	}

	/**
	 * Method 'getMinDeliveryDateTime'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getMinDeliveryDateTime()
	{
		return minDeliveryDateTime;
	}

	/**
	 * Method 'setMinDeliveryDateTime'
	 * 
	 * @param minDeliveryDateTime
	 */
	public void setMinDeliveryDateTime(java.util.Date minDeliveryDateTime)
	{
		this.minDeliveryDateTime = minDeliveryDateTime;
	}

	/**
	 * Method 'getExpirationDateTime'
	 * 
	 * @return java.util.Date
	 */
	public java.util.Date getExpirationDateTime()
	{
		return expirationDateTime;
	}

	/**
	 * Method 'setExpirationDateTime'
	 * 
	 * @param expirationDateTime
	 */
	public void setExpirationDateTime(java.util.Date expirationDateTime)
	{
		this.expirationDateTime = expirationDateTime;
	}

	/**
	 * Method 'getQueue'
	 * 
	 * @return Queue
	 */
	public Queue getQueue()
	{
		return queue;
	}

	/**
	 * Method 'setQueue'
	 * 
	 * @param queue
	 */
	public void setQueue(Queue queue)
	{
		this.queue = queue;
	}

}
