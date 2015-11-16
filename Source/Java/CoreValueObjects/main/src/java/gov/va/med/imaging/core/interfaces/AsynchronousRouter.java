/**
 * 
 */
package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandStatus;

/**
 * @author vhaiswbeckec
 *
 */
public interface AsynchronousRouter
{

	/**
	 * Get a best-effort status of a given command.  This is a pass-through to the AsynchronousCommandExecutor
	 * method of the same name.
	 * 
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandExecutor#getCommandStatus(Command)
	 * @param command
	 * @return
	 */
	public abstract CommandStatus getCommandStatus(Command<?> command);

	/**
	 * Submit a command for asynchronous execution, optionally providing a queue where the results
	 * may be communicated back to the client.
	 * 
	 * @param command - an AsynchronousCommand instance, created by this Router's AsynchronousCommandFactory
	 * as returned by the getAsynchronousCommandFactory().
	 * @param resultQueue - an optional Queue reference where the client can obtain the result of the asynchronous
	 * command
	 * @see gov.va.med.imaging.core.interfaces.Router#doAsynchronously(gov.va.med.imaging.core.interfaces.AsynchronousRouterCommandTypes, java.util.Queue)
	 */
	public abstract void doAsynchronously(Command<?> command);

}