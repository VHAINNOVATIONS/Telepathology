/**
 * 
 */
package gov.va.med.imaging.core.interfaces.router;

/**
 * This enum defines the states of a command as understood by a processor.
 * An asynchronous command may be in any of these states, a synchronous command
 * is never in the WAITING or COMPLETE state.
 * 
 * @author vhaiswbeckec
 *
 */
public enum CommandStatus
{
	UNKNOWN,	// the manager has either never been given a reference to the command or has completed processing 
	WAITING, 	// the manager has been passed a reference to an asynchronous command but has not commenced execution
	EXECUTING,	// the manager is currently executing the command
	COMPLETE	// the manager has completed execution of an asynchronous command but the notification processing has not commenced
}
