/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import java.util.Date;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandContext;

/**
 * A void command is the equivalent of a null (i.e. no command).
 * It is needed whenever a command must be specified in code but
 * no real command is desired.  In particular it is used as the default
 * in the FacadeRouterMethod annotation as the default command class.
 * 
 * @author vhaiswbeckec
 *
 */
public class NullCommand 
implements Command<java.lang.Void>
{
	private CommandContext commandContext;
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.Command#setCommandContext(gov.va.med.imaging.core.interfaces.router.CommandContext)
	 */
	@Override
	public void setCommandContext(CommandContext commandContext)
	{
		this.commandContext = commandContext;
	}

	@Override
	public void addListener(AsynchronousCommandResultListener listener)
	{
	}

	@Override
	public void setAccessibilityDate(Date accessibilityDate)
	{
	}

	@Override
	public void setPriority(int priority)
	{
	}

	@Override
	public void setProcessingDurationEstimate(long processingDurationEstimate)
	{
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.Command#call()
	 */
	@Override
	public AsynchronousCommandResult<Void> call()
	{
		return new AsynchronousCommandResult<Void>(this);
	}

	@Override
	public Void callSynchronously() throws MethodException, ConnectionException
	{
		return null;
	}

	public int getPeriodicExecutionDelay()
	{
		return 0;
	}

	public boolean isPeriodic()
	{
		return false;
	}

	public void setPeriodic(boolean isPeriodic)
	{
	}

	public void setPeriodicExecutionDelay(int periodicExecutionDelay)
	{
	}
	
	public Command getNewPeriodicInstance()
	throws MethodException
	{
		throw new MethodException("getNewPeriodicInstance is undefined for this command. It must be implemented if the command will be used in an asynchronous periodic fashion.");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.Command#setChildCommand(boolean)
	 */
	@Override
	public void setChildCommand(boolean childCommand) 
	{
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.Command#setParentCommandIdString(java.lang.String)
	 */
	@Override
	public void setParentCommandIdString(String parentCommandId) 
	{
	}

	@Override
	public RoutingToken getRoutingToken() throws MethodException
	{
		return null;
	}

	@Override
	public boolean isPeriodicProcessingTerminated()
	{
		return false;
	}

	@Override
	public void setPeriodicProcessingTerminated(
			boolean periodicProcessingTerminated)
	{
		
	}
}
