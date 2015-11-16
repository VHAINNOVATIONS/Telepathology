/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 18, 2008
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
 * @author VHAISWBECKEC
 *
 */
package gov.va.med.imaging.core.interfaces.router;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

import java.util.Date;


/**
 * An interface for all commands.
 * The Generic types are:
 * R - the result of the command
 * 
 * @author VHAISWBECKEC
 *
 */
public interface Command<R>
{
	/**
	 * Used internally by the command factory and the router to set
	 * the command context, which provides environment access
	 * to the command implementations.
	 * 
	 * @param commandContext
	 */
	public void setCommandContext(CommandContext commandContext);
	
	/**
	 * If the command instance is to be run asynchronously then
	 * it may include a list of Listeners to be notified when processing
	 * is complete.  
	 * 
	 * @param listener
	 */
    public void addListener(AsynchronousCommandResultListener listener);
    
    /**
	 * If the command instance is to be run asynchronously then
	 * it may include an accessibility date, before which it may not
	 * be executed.
     * 
     * @param accessibilityDate
     */
	public void setAccessibilityDate(Date accessibilityDate);
	
	/**
	 * If the command instance is to be run asynchronously then
	 * it may include a priority (0, 1, 2).  The priority specifies
	 * the ordering of tasks on the queue, not the thread priority.
	 * 
	 * @param priority
	 */
	public void setPriority(int priority);
	
	/**
	 * Set an estimated processing duration.  This value affects the order
	 * of tasks as they are removed from the queue for processing.
	 * 
	 * @param processingDurationEstimate
	 */
	public void setProcessingDurationEstimate(long processingDurationEstimate);
	
	/**
	 * An asynchronous execution of this commmand.
	 * 
	 * @return
	 */
	public AsynchronousCommandResult<R> call();
	
	/**
	 * A synchronous execution of this commmand.
	 * 
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public R callSynchronously()
	throws MethodException, ConnectionException;
	
	public boolean isPeriodic();
	public void setPeriodic(boolean isPeriodic);
	
	public int getPeriodicExecutionDelay();
	public void setPeriodicExecutionDelay(int periodicExecutionDelay);
	
	public Command<R> getNewPeriodicInstance() throws MethodException;
	
	public void setChildCommand(boolean childCommand);
	
	public void setParentCommandIdString(String parentCommandId);

	/**
	 * Make the commands routing token available so that the router
	 * can direct the call.
	 * 
	 * @return
	 */
	public RoutingToken getRoutingToken()
	throws MethodException;

	/**
	 * Determines if the periodic command has been terminated meaning it should not execute or be rescheduled to execute
	 * @return
	 */
	public boolean isPeriodicProcessingTerminated();
	
	/**
	 * Set the periodic command to terminate. This does not change the command while it is processing, it will complete as it normally would. 
	 * If this is true then the command will not be rescheduled for execution
	 * @param periodicProcessingTerminated
	 */
	public void setPeriodicProcessingTerminated(boolean periodicProcessingTerminated);
	
}
