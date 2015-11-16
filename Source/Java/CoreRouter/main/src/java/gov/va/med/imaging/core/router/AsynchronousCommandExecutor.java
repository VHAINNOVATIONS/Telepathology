package gov.va.med.imaging.core.router;

import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandStatus;
import gov.va.med.imaging.core.router.commands.configuration.CommandConfiguration;
import gov.va.med.imaging.core.router.queue.AsynchronousCommandProcessorPriorityBlockingQueue;

import java.util.Queue;

import org.apache.log4j.Logger;

/**
 * Thread pool manager to handle worker threads and process them as they are requested.
 * This class is similar to a java.util.ExecutorCompletionService implementation
 * but does not derive or use that class. 
 * 
 * @author VHAISWWERFEJ
 * @author VHAISWBECKEC
 *
 */
public class AsynchronousCommandExecutor
{
	private enum AsynchronousCommandExecutorThreadState {NEW, WAITING, BUSY};
	
	private final AsynchronousCommandProcessorPriorityBlockingQueue workQueue;
	private final ThreadGroup threadGroup;
	private final Queue<AsynchronousCommandResult<?>> resultsQueue;
	private final Logger logger = Logger.getLogger(this.getClass());
	private Integer threadSerialNumber = 0;
	private boolean shuttingDown = false;
	
	private int minimumThreadCount = -1; // default to 100 minimum threads
	private final int bareMinimumThreadCount = 100; // if it can't read from config file, use this value
	
	/**
	 * Create an instance with no results queue.
	 * Results will not be available.
	 */
	public AsynchronousCommandExecutor()
	{
		this.workQueue = new AsynchronousCommandProcessorPriorityBlockingQueue();
		this.threadGroup = new ThreadGroup("AsynchronousCommandExecutor");
		this.resultsQueue = null;
	}
	
	/**
	 * 
	 * @param resultsQueue
	 */
	public AsynchronousCommandExecutor(Queue<AsynchronousCommandResult<?>> resultsQueue)
	{
		this.workQueue = new AsynchronousCommandProcessorPriorityBlockingQueue();
		this.threadGroup = new ThreadGroup("AsynchronousCommandExecutor");
		this.resultsQueue = resultsQueue;
	}

    private AsynchronousCommandProcessorPriorityBlockingQueue getWorkQueue()
    {
    	return workQueue;
    }

    private ThreadGroup getThreadGroup()
    {
    	return threadGroup;
    }

	private Queue<AsynchronousCommandResult<?>> getResultsQueue()
    {
    	return resultsQueue;
    }

	protected Logger getLogger()
    {
    	return logger;
    }

	protected boolean isShuttingDown()
    {
    	return shuttingDown;
    }

	private int getMinimumThreadCount()
    {
		if(minimumThreadCount <= 0)
		{
			minimumThreadCount = CommandConfiguration.getCommandConfiguration().getMinimumThreadCount();
			if(minimumThreadCount <= 0)
				minimumThreadCount = bareMinimumThreadCount;
		}
	    return minimumThreadCount;
    }
	
	public void setMinimumThreadCount(int minimumThreadCount) 
	{
		this.minimumThreadCount = minimumThreadCount;
	}

	/**
	 * Get the status of the given command.
	 * NOTE: this uses the Command interface and the .equals() as defined in
	 * the Command implementations to determine which Command reference is the subject
	 * of the status inquiry.
	 * NOTE: this method makes no attempt to assure the consistency of results, nor does
	 * it attempt to synchronize the state of the various queues involved.  Practically this
	 * means that the status of a command as returned from this command may not follow
	 * the expected lifecycle, nor will a command between states be correctly reported.
	 * 
	 * The result may be either WAITING, EXECUTING, COMPLETE or UNKNOWN.
	 * 
	 * @param command
	 * @return
	 */
	public CommandStatus getCommandStatus(Command<?> command)
	{
		// check the queues in order of operation for most consistent
		// results
		if( getWorkQueue().contains(command) )
			return CommandStatus.WAITING;
		
		if(isCommandExecuting(command))
			return CommandStatus.EXECUTING;
		
		if( isCommandInCompleteQueue(command) )
			return CommandStatus.COMPLETE;
		
		return CommandStatus.UNKNOWN;
	}

	public boolean isCommandInCompleteQueue(Command<?> command)
	{
		if(command != null && getResultsQueue() != null)
			for(AsynchronousCommandResult<?> result : (AsynchronousCommandResult<?> [])getResultsQueue().toArray() )
				if(command .equals( result.getCommand() ) )
					return true;
		
		return false;
	}
	
	/**
	 * 
	 * @param commandProcessor
	 */
	public void execute(AbstractCommandImpl<?> commandProcessor)
    {
		if(isShuttingDown())
			return;
		
    	workQueue.offer(commandProcessor);
    	
    	adjustThreadPool();
    }
	
	/**
	 * Someday, replace this method with a Thread Management Strategy
	 */
	private void adjustThreadPool()
    {
    	//int highPriorityElements = workQueue.accessibleOfPriority(ScheduledPriorityQueueElement.Priority.HIGH);
    	//if(highPriorityElements > 0)
    	//	getLogger().warn("There are currently " + highPriorityElements + " high priority elements waiting for procesing.");
    	
		for(int activeCount = getThreadGroup().activeCount(); activeCount < getMinimumThreadCount(); ++activeCount)
			startNewThread();
		
    }

	public void shutdown()
    {
		this.shuttingDown = true;
    }
	
	public int getWaitingThreadCount()
	{
		return getThreadCountInState(AsynchronousCommandExecutorThreadState.WAITING);
	}
	
	public int getBusyThreadCount()
	{
		return getThreadCountInState(AsynchronousCommandExecutorThreadState.BUSY);
	}
	
    private int getThreadCountInState(AsynchronousCommandExecutorThreadState state)
    {
    	Thread[] threads = new Thread[getThreadGroup().activeCount() + 10];
    	int countInState = 0;
    	getThreadGroup().enumerate(threads);
    	
    	for(Thread thread : threads)
    		if( ((AsynchronousCommandExecutorThread)thread).getThreadState() == state )
    			countInState++;
    	
    	return countInState;
    }

    private boolean isCommandExecuting(Command<?> command)
    {
    	if(command == null)
    		return false;
    	
    	Thread[] threads = new Thread[getThreadGroup().activeCount() + 10];
    	getThreadGroup().enumerate(threads);
    	
    	for(Thread thread : threads)
    		if( command.equals( ((AsynchronousCommandExecutorThread)thread).getExecutingCommand() ) )
    			return true;
    	
    	return false;
    }
    
    /**
     * 
     * @return
     */
	private void startNewThread()
	{
		AsynchronousCommandExecutorThread thread = null;
		synchronized (threadSerialNumber)
		{
			// this will happen sometime around the time the sun collapses to a brown dwarf
			if(threadSerialNumber.intValue() == Integer.MAX_VALUE)
				threadSerialNumber = 0;
			
			thread = 
				new AsynchronousCommandExecutorThread(threadGroup, threadGroup.getName() + "-" + threadSerialNumber);
			thread.setDaemon(true);
			thread.start();
			
			threadSerialNumber = threadSerialNumber.intValue() + 1;
		}
    }
	
	/**
	 * 
	 * @author VHAISWBECKEC
	 */
	class AsynchronousCommandExecutorThread
	extends Thread
	{
		private AbstractCommandImpl<?> executingCommand = null;
		private AsynchronousCommandExecutorThreadState state;
		private boolean sepuku = false;

		AsynchronousCommandExecutorThread(ThreadGroup group, String name)
        {
	        super(group, name);
	        this.state  = AsynchronousCommandExecutorThreadState.NEW;
        }

		protected AsynchronousCommandExecutorThreadState getThreadState()
        {
        	return state;
        }

		void hariKari()
		{
			sepuku = true;
			if( this.getState() == Thread.State.WAITING && this.getThreadState() == AsynchronousCommandExecutorThreadState.WAITING )
				this.interrupt();
		}
		
		protected boolean isSepuku()
        {
        	return sepuku;
        }

		/**
		 * @return the executingCommand or null if thread is waiting for a task
		 */
		public AbstractCommandImpl<?> getExecutingCommand()
		{
			return this.executingCommand;
		}

		@Override
        public void run()
        {
			while(! isShuttingDown() && !isSepuku())
			{
				try
	            {
					this.state = AsynchronousCommandExecutorThreadState.WAITING;
					// will wait here for the next work queue item
					executingCommand = getWorkQueue().take();
					try
					{
						this.state = AsynchronousCommandExecutorThreadState.BUSY;
						boolean executeCommand = true;
						if(executingCommand.isPeriodic())
						{
							if(executingCommand.isPeriodicProcessingTerminated())
							{
								// make sure this is removed from the list of executing commands
								PeriodicCommandList.get().removeScheduledCommand(executingCommand);
								executeCommand = false;
								logger.warn("Periodic Command '" + executingCommand.getClass().getName() + "' has been terminated and will not execute.");
							}
							else
							{
								// if the executing command is not already in the list of scheduled commands, add it to track it while it runs
								PeriodicCommandList.get().addIfNotAlreadyScheduled(executingCommand);
							}
						}
						if(executeCommand)
						{
							AsynchronousCommandResult<?> result = executingCommand.call();
							if(getResultsQueue() != null)
								getResultsQueue().add(result);	
						}						
					}
					catch(Throwable t)
					{
						getLogger().error(
							t.getClass().getName() + 
							" '" + t.getMessage() + "'" + 
							(t.getCause() != null ? (" caused by '" + t.getCause().getClass().getName() + "'") : "")
						);
						
						if(t.getCause() != null)
							t.getCause().printStackTrace();
						else
							t.printStackTrace();
					}
	            } 
				catch (InterruptedException e)
				{
					if(! isSepuku())
						logger.warn("Unexpected InterruptedException, ignoring ....");
				}
				
				executingCommand = null;
			}
        }
	}
}