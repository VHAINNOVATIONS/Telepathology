/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 3, 2008
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
package gov.va.med.imaging.core;

import gov.va.med.imaging.core.interfaces.AsynchronousRouter;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandStatus;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.AbstractRetryableCommandImpl;
import gov.va.med.imaging.core.router.AsynchronousCommandExecutor;
import gov.va.med.imaging.core.router.PeriodicCommandList;
import gov.va.med.imaging.transactioncontext.InvalidTransactionContextMementoException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

/**
 * This class implements all of the asynchronous methods of the Router
 * interface.  The Router implementation may delegate work to this class
 * and vice versa.
 * 
 * @author VHAISWBECKEC
 *
 */
public class AsynchronousRouterImpl 
implements AsynchronousRouter
{
	private final static int asynchCommandCompletionNotificationServiceThreads = 10;

	final Logger logger = Logger.getLogger(Router.class);
	//	private final IImageConversion imageConversion;

	// A Queue where the asynch executor should put results when the tasks are complete
	private final BlockingQueue<AsynchronousCommandResult<?>> resultsQueue;
	
	// the asynchronous command processing executor
	// this is similar in function to an Executor but is specialized for
	// asynchronous router processes and does not derive from any java.util.concurrent.* classes
	private AsynchronousCommandExecutor asynchCommandCompletionService;
	
	// A thread pool that takes completed asynchronous tasks and notifies the client listener
	private ExecutorService asynchCommandCompletionNotificationService;
	
	private final RouterImpl router;
	
	AsynchronousRouterImpl(RouterImpl router)
    {
	    super();
	    this.router = router;
	    
		this.resultsQueue = new LinkedBlockingQueue<AsynchronousCommandResult<?>>();
		this.asynchCommandCompletionService = new AsynchronousCommandExecutor(resultsQueue);

		this.asynchCommandCompletionNotificationService = Executors.newFixedThreadPool(
			asynchCommandCompletionNotificationServiceThreads, 
			new ThreadFactory()
			{
				private int serialNumber = 0;
				private ThreadGroup group = new ThreadGroup("CommandCompletionNotificationService");
				@Override
                public Thread newThread(Runnable r)
                {
					Thread thread = new Thread(group, r, "CommandCompletionNotificationService" + "-" + serialNumber++);
					thread.setDaemon(true);
					return thread;
                }
			}
		);
		// the completion service completion threads takes the results from the completion service
		// and calls the AsynchronousCommandResultListener from the original client call (if one was provided)
		for(int i=0; i<asynchCommandCompletionNotificationServiceThreads; ++i)
			this.asynchCommandCompletionNotificationService.execute( new CommandCompletionNotificationThread() );
    }
	
	/**
	 * 
	 * @return
	 */
	private AsynchronousCommandExecutor getAsynchCommandCompletionService()
	{
		return asynchCommandCompletionService;
	}
	
	private BlockingQueue<AsynchronousCommandResult<?>> getResultsQueue()
    {
    	return resultsQueue;
    }

	RouterImpl getRouter()
    {
    	return router;
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.AsynchronousRouter#getCommandStatus(gov.va.med.imaging.core.interfaces.router.Command)
	 */
	public CommandStatus getCommandStatus(Command<?> command)
	{
		return getAsynchCommandCompletionService().getCommandStatus(command);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.AsynchronousRouter#doAsynchronously(gov.va.med.imaging.core.router.AbstractCommandImpl)
	 */
    public void doAsynchronously(Command<?> command)
    {
    	if(command == null)
    		return;

    	if(command instanceof AbstractCommandImpl<?>)
    	{
        	logger.info("Submitting asynchronous command '" + command.toString() + "' for asynchronous processing using '" + command.getClass().getSimpleName() + "'.");
        	// JMW 12/7/2012 P130 - initially when the command is scheduled it might not immediately execute (there can be a delay). Add it
        	// to the list of scheduled commands so it can be cancelled before running if desired.
        	if(command.isPeriodic())
        		PeriodicCommandList.get().addIfNotAlreadyScheduled(command);
    		getAsynchCommandCompletionService().execute((AbstractCommandImpl<?>)command);
    	}
    	else
        	logger.error("Error submitting asynchronous command '" + command.toString() + "' for asynchronous processing.  Command class is incompatible with the asynchronous router implementation.");
    		
    }
    
	/**
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	class CommandCompletionNotificationThread
	implements Runnable
	{
        private boolean running = true;
        private Thread notificationThread = null;
        
        public void kill()
        {
            running = false;
            if(notificationThread != null)
                 notificationThread.interrupt();
        }
        
        @Override
        public void run()
      	{
            notificationThread = Thread.currentThread();
            while(running)
            {
	    		boolean transactionContextSet = false;
				try
	            {
	                AsynchronousCommandResult<?> result = getResultsQueue().take();
	                // the result always wraps the command processor
	        		AbstractCommandImpl<?> command = (AbstractCommandImpl<?>)result.getCommand();
	        		
	        		// Push the transaction context onto this notification thread so that the
	        		// listeners are operating in the correct transaction context
	        		
	        		try
	        		{
	        			// push the transaction context of the client, making it the current TC
	        			TransactionContextFactory.pushTransactionContext(command.getTransactionContextMemento());
	        			transactionContextSet = true;
	        		} 
	        		catch (InvalidTransactionContextMementoException itcmX)
	        		{
	        			logger.error("Unable to push transaction context (make it the current context).  Completion listeners cannot be safely notified.", itcmX);
	        		}
	        			
	    			// Normally, listeners are notified regardless of result.
	    			// Retryable commands may choose not to notify their listeners until
	    			// all retries have been exhausted.
	                if( command instanceof AbstractRetryableCommandImpl<?> && 
	                	result.isRetryRequested() && 
	                	((AbstractRetryableCommandImpl<?>)command).getRemainingRetries() > 0 )
	                {
	                	logger.info("Asynchronous command [" + result.getCommand().toString() + "] completed with retry requested.");
	            		AbstractRetryableCommandImpl<?> retryableCommand = (AbstractRetryableCommandImpl<?>)result.getCommand();
	                	retryableCommand.updateAccessibilityDateForRetry();
	                	logger.info("Asynchronous command [" + command.toString() + "] retrying.");
	        			doAsynchronously(command);
	                }
	                else if(transactionContextSet)
	                {
	                	logger.info("Asynchronous command [" + result.getCommand().toString() + "] completed.");
	                    for( AsynchronousCommandResultListener listener : command.getListeners() ) 
		                    listener.commandComplete(result);
	                    
	                    // If the command is periodic, update the accessibility date and throw it back 
	                    // on the queue
	                    if(command.isPeriodic())
	                    {
	                    	// make sure no longer in the list of executing periodic commands
	                    	PeriodicCommandList.get().removeScheduledCommand(command);
	                    	boolean fatalException = false;
	                    	if(result.getThrowable() != null)
	                    	{
	                    		List<Class<? extends MethodException>> fatalExceptionClasses = command.getFatalPeriodicExceptionClasses();
	                    		if(fatalExceptionClasses != null)
	                    		{
	                    			for(Class<? extends Throwable> t : fatalExceptionClasses)
	                    			{	                    				
	                    				if(result.getThrowable().getClass().equals(t))
	                    				{
	                    					fatalException = true;
	                    					logger.warn("Periodic command [" + result.getCommand().toString() + "] completed with a fatal exception of type [" + result.getThrowable().getClass().getName() + "], will not be rescheduled.");
	                    					// don't reschedule the periodic command
	                    					command.handleFatalPeriodicException(result.getThrowable());
	                    					break;
	                    				}
	                    			}
	                    		}
	                    	}	            
	                    	if(!fatalException)
	                    	{	    
	                    		// check if the now completed command was set to terminate
	                    		if(command.isPeriodicProcessingTerminated())
	                    		{
	                    			logger.info("Periodic command [" + result.getCommand().toString() + "] completed but has been set to terminate - will not be added back to the asynch queue.");
	                    		}
	                    		else
	                    		{	
			                    	logger.info("Periodic command [" + result.getCommand().toString() + "] completed and is being added back to the asynch queue.");		                    	
			                    	Command<?> newCommand = command.getNewPeriodicInstance();		                    
			                    	GregorianCalendar accessibilityDate = new GregorianCalendar();
			                    	accessibilityDate.add(GregorianCalendar.MILLISECOND, command.getPeriodicExecutionDelay());
			                    	newCommand.setAccessibilityDate(accessibilityDate.getTime());
			                    	PeriodicCommandList.get().addScheduledPeriodicCommand(newCommand);
			                		doAsynchronously(newCommand);
	                    		}
	                    	}
	                    }
	                }
	            } 
				catch (InterruptedException e)
	            {
					logger.warn("CommandCompletionServiceCompletion interrupted waiting for results, ignoring...");
	            } 
				catch(Throwable t)
				{
					logger.error("CommandCompletionServiceCompletion received exception when calling AsynchronousCommandResultListener.", t);
				}
				finally
				{
					// restore the transaction context, this is mostly so that the thread no longer has access to the
					// security context which could lead to a security hole
					if(transactionContextSet)
						TransactionContextFactory.popTransactionContext();
				}
			}
        }
	}
}
