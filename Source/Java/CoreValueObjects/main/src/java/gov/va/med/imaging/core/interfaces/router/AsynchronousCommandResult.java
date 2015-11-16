/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 26, 2008
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
package gov.va.med.imaging.core.interfaces.router;

/**
 * Object that contains the result of running a task. This contains the status of
 * the task derived from the presence of result, throwable or retryable.
 * The TransactionContext reference is retained in the AsynchronousCommandProcessor derived classes.  
 * 
 * @author VHAISWWERFEJ
 * @author VHAISWBECKEC
 *
 */
public class AsynchronousCommandResult<R>
{
	private final Command<R> command;
	private final R result;
	private final Throwable error;
	private final boolean retryRequested;

	/**
	 * Constructor when the result is void or not used.
	 * @param listener
	 */
	public AsynchronousCommandResult(Command<R> command)
	{
		this.command = command;
		this.result = null;
		this.error = null;
		this.retryRequested = false;
	}

	/**
	 * Constructor when the asynchronous command was successful.
	 * @param result
	 * @param listener
	 */
	public AsynchronousCommandResult(Command<R> command, R result)
	{
		this.command = command;
		this.result = result;
		this.error = null;
		this.retryRequested = false;
	}

	/**
	 * Constructor when the asynchronous failed with an error or exception
	 * @param t
	 * @param listener
	 */
	public AsynchronousCommandResult(Command<R> command, Throwable t)
    {
		this.command = command;
		this.result = null;
	    this.error = t;
		this.retryRequested = false;
    }

	/**
	 * Constructor when the command failed but the command processor wants to retry it.
	 * 
	 * @param command
	 * @param retryRequested
	 */
	public AsynchronousCommandResult(Command<R> command, boolean retryRequested)
    {
		this(command, null, retryRequested);
    }
	
	/**
	 * Constructor when the command failed but the command processor wants to retry it.
	 * 
	 * @param command
	 * @param listener
	 */
	public AsynchronousCommandResult(Command<R> command, Throwable t, boolean retryRequested)
    {
		this.command = command;
		this.result = null;
	    this.error = t;
		this.retryRequested = retryRequested;
    }
	
	public Throwable getThrowable()
    {
    	return error;
    }

	/**
	 * Return an Exception, if the command completed in error.
	 * If the error was not a derivative of Exception than wrap it
	 * in an Exception and return it.
	 * The point here is to make everything a checked exception.
	 * @return
	 */
	public Exception getException()
    {
    	return (error instanceof Exception) ?
    			(error instanceof RuntimeException ? new Exception(error) : (Exception)error) : 
    			new Exception(error);
    }
	
    public R getResult()
    {
    	return result;
    }

	public Command<R> getCommand()
    {
    	return command;
    }

	public boolean isError()
	{
		return getThrowable() != null;
	}
	public boolean isSuccess()
	{
		return getThrowable() == null && ! retryRequested;
	}
	public boolean isRetryRequested()
	{
		return retryRequested;
	}

	@Override
    public String toString()
    {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getSimpleName());
		sb.append(" - ");
		sb.append(isError() ? "ERROR" : isRetryRequested() ? "RETRY REQUESTED" : "SUCCESS");
		sb.append(" (");
		if(isError())
		{
			sb.append( getThrowable() == null ? "<null>" : getThrowable().getMessage() );
			if(getThrowable() != null && getThrowable().getCause() != null)
			{
				sb.append(" (caused by: " + getThrowable().getCause().getMessage() + ")");
				for( StackTraceElement ste : getThrowable().getCause().getStackTrace() )
					sb.append("/t" + ste.getFileName() + "." + ste.getMethodName() + ":" + ste.getLineNumber() + ")");
			}
		}
		if(isSuccess())
		{
			sb.append( getResult() == null ? "<null>" : getResult().toString() );
		}
		if(isRetryRequested())
		{
			sb.append( getResult() == null ? "<null>" : getResult().toString() );
		}
		
		sb.append(")");
	    return sb.toString();
    }
}
