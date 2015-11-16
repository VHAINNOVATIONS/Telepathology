package gov.va.med.server;

import java.text.DateFormat;
import java.util.Date;

/**
 * A generic representation for server events.
 * This class is not specific to Tomcat and may be used
 * in application classes.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ServerLifecycleEvent
{
	public enum EventType
	{
		INIT, BEFORE_START, START, AFTER_START, BEFORE_STOP, STOP, AFTER_STOP
	}
	
	private final EventType eventType;
	private final Date date;
	
	public ServerLifecycleEvent(EventType eventType)
	{
		this.eventType = eventType;
		this.date = new Date();
	}

	public EventType getEventType()
    {
    	return eventType;
    }

	public Date getDate()
    {
    	return date;
    }

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		sb.append('-');
		sb.append(getEventType().toString());
		sb.append('@');
		sb.append( DateFormat.getDateTimeInstance().format(getDate()) );
		
		return sb.toString();
	}
	
	
}
