/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 3, 2012
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
package gov.va.med.imaging.core.router;

import gov.va.med.imaging.core.interfaces.router.Command;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Maintains a list of periodic commands, either executing or waiting to be executed
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PeriodicCommandList
extends ArrayList<Command<?>>
{
	private static final long serialVersionUID = -7471967762165364017L;
	
	private PeriodicCommandList()
	{
		super();
	}
	
	private static PeriodicCommandList periodicCommandList= null;
	
	public synchronized static PeriodicCommandList get()
	{
		if(periodicCommandList == null)
		{
			periodicCommandList = new PeriodicCommandList();
		}
		return periodicCommandList;
	}
	
	public void removeScheduledCommand(Command<?> command)
	{
		synchronized(this)
		{
			Iterator<Command<?>> iterator = this.iterator();
			while (iterator.hasNext())
			{
				Command<?> scheduledCommand = iterator.next();
				if (scheduledCommand == command)
				{
					iterator.remove();
					break;
				}
			}
		}
	}
	
	public void addScheduledPeriodicCommand(Command<?> command)
	{
		synchronized(this)
		{
			this.add(command);
		}
	}
	
	public void addIfNotAlreadyScheduled(Command<?> command)
	{
		synchronized(this)
		{
			Iterator<Command<?>> iterator = this.iterator();
			while (iterator.hasNext())
			{
				Command<?> scheduledCommand = iterator.next();
				if (scheduledCommand == command)
				{
					return;
				}
			}
			
			this.add(command);

		}
	}
	
	/**
	 * This method checks to see if another instance of the passed-in object's class
	 * is already in the list. 
	 *   * If a separate instance of the passed-in object's class is in the list, this
	 *     method will return true.
	 *   * If there are no other instances of the class in the list, or if
	 *     the only instance in the list is the object provided, this method will 
	 *     return false. 
	 * @param command
	 * @return
	 */
	public boolean isAnotherInstancePresent(Command<?> command)
	{
		synchronized(this)
		{
			for (Command<?> scheduledCommand : this)
			{
				// If the scheduled object and the requesting object are of the same type, 
				// but are different instances, then another class of the same type as the 
				// requestor is present. Return true.
				if (command.getClass().equals(scheduledCommand.getClass()) && command != scheduledCommand)
				{
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * This method kills all instances of a class except for the instance one passed in. 
	 * @param command
	 * @return
	 */
	public boolean terminateOtherInstances(Command<?> command)
	{
		synchronized(this)
		{
			for (Command<?> scheduledCommand : this)
			{
				// If the scheduled object and the requesting object are of the same type, 
				// but are different instances, kill the scheduled one.
				if (command.getClass().equals(scheduledCommand.getClass()) && command != scheduledCommand)
				{
					scheduledCommand.setPeriodicProcessingTerminated(true);
				}
			}
			return false;
		}
	}
	
	/**
	 * All instances in the list of this commandClass (exactly this command class) will be set to terminate meaning they won't execute if they
	 * are waiting to execute and they won't reschedule themselves if they are currently executing.
	 */
	public void terminateScheduledPeriodicCommand(Class<? extends Command<?>> commandClass)
	{
		synchronized(this)
		{
			for(Command<?> c : this)
			{
				if(c.getClass().equals(commandClass))
				{
					c.setPeriodicProcessingTerminated(true);
				}
			}		
		}
	}
}
