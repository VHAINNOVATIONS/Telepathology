/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 29, 2008
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
package gov.va.med.imaging.core.router.queue;

import java.util.Date;

/**
 * This interface defines the property accessors needed by the
 * BlockingScheduledPriorityQueue to correctly order those queue elements. 
 * This interface must be implemented by any class whose instances are
 * added to a BlockingScheduledPriorityQueue.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface ScheduledPriorityQueueElement
{
	public enum Priority
	{
		LOW ("Low priority asynchronous commands will execute at lower priority than all other commands (asynchronous and synchronous). ex: prefetch for distant encounters"), 
		NORMAL ("The normal priority for asynchronous commands, will execute at a lower priority than synchronous command. ex: prefetch requested by users directly"), 
		HIGH ("An asynchronous high-priority command is executed with the same priority as a synchronous command. ex: child commands of synchronous commands");
		
		public final String description;
		
		Priority(String description)
		{
			this.description = description;
		}
		
		public static Priority valueOfNormalized(int ordinal)
		{
			if( ordinal <= LOW.ordinal() ) 
				return LOW;
			if( ordinal >= HIGH.ordinal() ) 
				return HIGH;
			return NORMAL;
		}
	}
	
	/**
	 * Return the date that the element should not be available
	 * from a call to take() or poll().
	 * 
     * @return the availabilityDate
     */
    public abstract Date getAccessibilityDate();
    
	/**
	 * Return the target date (when the task represented by the queue element
	 * should be completed by).
	 * 
     * @return the targetDate
     */
    public abstract Date getProcessingCommencementTargetDate();
    
    /**
     * Return an estimate of time to complete the task represented by this
     * queue element.  This value is used to prioritize the elements in the 
     * work queue.
     * 
     * @return
     */
    public abstract long getProcessingDurationEstimate();
    
    /**
     * Get the priority of the queue element.
     * The ordering of queue elements is not strictly priority based.
     * 
     * @return
     */
    public abstract Priority getPriority();
}