/**
 * 
 */
package gov.va.med.imaging.exchange;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * @author Administrator
 * 
 * A simple subclass to make a single task scheduler available to the application.
 * Scheduled tasks may choose to use this Timer to schedule task execution, or may
 * create a new Timer if needed.  This implementation runs as a daemon thread and
 * therefore does not prevent application exit.
 *
 */
public class TaskScheduler
extends Timer
{
	private static TaskScheduler singleton = null;
	
	private TaskScheduler()
	{
		super(true);
	}
	
	public static synchronized TaskScheduler getTaskScheduler()
	{
		if(singleton == null)
			singleton = new TaskScheduler();
		
		return singleton;
	}
}
