/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 16, 2008
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

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.router.AbstractCommandImpl;

import java.util.Date;

public class MockCommandImpl
extends AbstractCommandImpl<String>
{
	private static final long serialVersionUID = -3442575939031680499L;
	
	private final ScheduledPriorityQueueElement.Priority priority;
	private final Date accessibilityDate;
	private final Date processingCommencementTargetDate;
	private final long processingDurationEstimate;
	private final String name;
	
	private boolean executed = false;

	public MockCommandImpl(String name)
    {
        super();
        this.name = name;
        this.priority = ScheduledPriorityQueueElement.Priority.NORMAL;
        this.accessibilityDate = new Date();
        this.processingCommencementTargetDate = new Date();
        this.processingDurationEstimate = 1000L;
    }

	public MockCommandImpl(String name, Priority priority)
    {
        super();
        this.name = name;
        this.priority = priority;
        this.accessibilityDate = new Date();
        this.processingCommencementTargetDate = new Date();
        this.processingDurationEstimate = 1000L;
    }

	public MockCommandImpl(String name, Priority priority, Date accessibilityDate)
    {
        super();
        this.name = name;
        this.priority = priority;
        this.accessibilityDate = accessibilityDate;
        this.processingCommencementTargetDate = new Date();
        this.processingDurationEstimate = 1000L;
    }

	public MockCommandImpl(String name, Priority priority, Date accessibilityDate, Date processingCommencementTargetDate,
            long processingDurationEstimate)
    {
        super();
        this.name = name;
        this.priority = priority;
        this.accessibilityDate = accessibilityDate;
        this.processingCommencementTargetDate = processingCommencementTargetDate;
        this.processingDurationEstimate = processingDurationEstimate;
    }

	public MockCommandImpl(String name, Priority priority, Date processingCommencementTargetDate, long processingDurationEstimate)
    {
        super();
        this.name = name;
        this.priority = priority;
        this.accessibilityDate = new Date();
        this.processingCommencementTargetDate = processingCommencementTargetDate;
        this.processingDurationEstimate = processingDurationEstimate;
    }

	protected String getName()
    {
    	return name;
    }

	@Override
	public ScheduledPriorityQueueElement.Priority getPriority()
    {
    	return priority;
    }

	@Override
	public Date getAccessibilityDate()
    {
    	return accessibilityDate;
    }

	@Override
	public Date getProcessingCommencementTargetDate()
    {
    	return processingCommencementTargetDate;
    }

	@Override
	public long getProcessingDurationEstimate()
    {
    	return processingDurationEstimate;
    }

	@Override
	public String toString()
	{
		return 
			getName() + "[" + getPriority() + "," + getAccessibilityDate() + "," + 
			getProcessingCommencementTargetDate() + "," + getProcessingDurationEstimate() + "]";
	}

	@Override
    public String callSynchronouslyInTransactionContext() 
    throws MethodException
    {
		System.out.println("Mock command '" + getName() + "' is executing.");
		this.executed = true;
        return "String";
    }

	/**
	 * @return the executed
	 */
	public boolean isExecuted()
	{
		return this.executed;
	}

	@Override
    protected String parameterToString()
    {
	    return "String";
    }

	@Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

	@Override
    public boolean equals(Object obj)
    {
        if (this == obj)
	        return true;
        final MockCommandImpl other = (MockCommandImpl) obj;
        if (name == null)
        {
	        if (other.name != null)
		        return false;
        } else if (!name.equals(other.name))
	        return false;
        return true;
    }
}