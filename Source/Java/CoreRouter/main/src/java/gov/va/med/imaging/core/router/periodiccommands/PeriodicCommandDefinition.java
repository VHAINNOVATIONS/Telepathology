package gov.va.med.imaging.core.router.periodiccommands;

import gov.va.med.imaging.core.router.queue.ScheduledPriorityQueueElement;

public class PeriodicCommandDefinition
{
	private Class returnClass;
	private String commandClassName;
	private Object[] commandParameters;
	private String periodicDelayInterval;
	private ScheduledPriorityQueueElement.Priority priority;
	
	public Class getReturnClass()
	{
		return returnClass;
	}
	public void setReturnClass(Class returnClass)
	{
		this.returnClass = returnClass;
	}
	public String getCommandClassName()
	{
		return commandClassName;
	}
	public void setCommandClassName(String commandClassName)
	{
		this.commandClassName = commandClassName;
	}
	public Object[] getCommandParameters()
	{
		return commandParameters;
	}
	public void setCommandParameters(Object[] commandParameters)
	{
		this.commandParameters = commandParameters;
	}
	public String getPeriodicDelayInterval()
	{
		return periodicDelayInterval;
	}
	public void setPeriodicDelayInterval(String periodicDelayInterval)
	{
		this.periodicDelayInterval = periodicDelayInterval;
	}
	public ScheduledPriorityQueueElement.Priority getPriority()
	{
		return priority;
	}
	public void setPriority(ScheduledPriorityQueueElement.Priority priority)
	{
		this.priority = priority;
	}
}
