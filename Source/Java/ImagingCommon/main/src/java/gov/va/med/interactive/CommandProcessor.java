/**
 * 
 */
package gov.va.med.interactive;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The CommandProcessor pulls commands from a queue and executes them.
 * 
 * @author vhaiswbeckec
 * 
 */
public class CommandProcessor<M>
implements Runnable, ControllerListener
{
	private Color sky = Color.BLUE;
	private CommandController<M> commandController;
	private Object commandQueueSetNotificationLock = new Object();
	private Object managedObjectNotificationLock = new Object();

	public CommandProcessor()
	{
		// uncomment this for logging messages
		this.addCommandProcessorEventListener(new LoggingListener<M>());
	}

	public CommandController<M> getCommandController()
	{
		return this.commandController;
	}
	public void setCommandController(CommandController<M> commandController)
	{
		this.commandController = commandController;
		if( this.commandController.getCommandQueue() != null )
			commandQueueAvailable();
		if( this.commandController.getManagedObject() != null )
			managedObjectAvailable();
	}

	public M waitForManagedObject(){return waitForManagedObject(0L);}
	public M waitForManagedObject(long timeout)
	{
		M managedObject = getCommandController().getManagedObject();
		if(managedObject == null)
		{
			synchronized (managedObjectNotificationLock)
			{
				try
				{
					managedObjectNotificationLock.wait(timeout);
					managedObject = getCommandController().getManagedObject();
				}
				catch (InterruptedException x){}
			}
		}
		return managedObject;
	}
	
	public BlockingQueue<Command<M>> waitForCommandQueue(){return waitForCommandQueue(0L);}
	public BlockingQueue<Command<M>> waitForCommandQueue(long timeout)
	{
		BlockingQueue<Command<M>> commandQueue = getCommandController().getCommandQueue();
		if(commandQueue == null)
		{
			synchronized (commandQueueSetNotificationLock)
			{
				try
				{
					commandQueueSetNotificationLock.wait(timeout);
					commandQueue = getCommandController().getCommandQueue();
				}
				catch (InterruptedException x){}
			}
		}
		return commandQueue;
	}

	@Override
	public void commandQueueAvailable()
	{
		synchronized(commandQueueSetNotificationLock)
		{
			commandQueueSetNotificationLock.notifyAll();
		}
	}

	@Override
	public void commandQueueUnavailable()
	{
	}

	@Override
	public void managedObjectAvailable()
	{
		synchronized (managedObjectNotificationLock)
		{
			managedObjectNotificationLock.notifyAll();
		}
	}

	@Override
	public void managedObjectUnavailable()
	{
	}

	/**
	 * Adds the command to the execution queue, possibly waiting for the queue
	 * to become available.
	 * 
	 * @param command
	 */
	public void queueCommand(Command<M> command)
	{
		BlockingQueue<Command<M>> executionQueue = waitForCommandQueue();
		executionQueue.add(command);
	}
	
	/**
	 * 
	 */
	public void run()
	{
		notifyListenersOfProcessorStarting();
		
		// as long as the sky remains blue ...
		// or the user exits
		while (sky == Color.BLUE)
		{
			try
			{
				//System.out.println("Waiting for managed object.");
				M iterationManagedObject = waitForManagedObject();
				//System.out.println("Acquired managed object, waiting for command queue.");
				BlockingQueue<Command<M>> iterationQueue = waitForCommandQueue();
				//System.out.println("Acquired command queue.");

				Command<M> command = iterationQueue.poll(60, TimeUnit.SECONDS);
				if (command == null)
					continue;

				System.out.println("Executing command '" + command.toString() + "'.");
				notifyListenersOfCommandStart(command);
				try
				{
					command.processCommand(this, iterationManagedObject);
					notifyListenersOfCommandComplete(command);
				}
				catch (Exception x)
				{
					notifyListenersOfCommandException(command, x);
					x.printStackTrace();
				}

				//Thread.sleep(5000);
				
				if (command.exitAfterProcessing())
				{
					if( iterationQueue.peek() != null )
						System.err.println("Exiting after processing but command queue still has items.");
					stop();
				}
			}
			catch (InterruptedException x)
			{
			}
		}
		
		notifyListenersOfProcessorExiting();
	}

	/**
	 * 
	 */
	public void stop()
	{
		sky = Color.BLACK;
	}
	
	// ==============================================================================================================
	// Listener Notification Code
	// ==============================================================================================================
	private List<CommandProcessorEventListener<M>> eventListeners = new ArrayList<CommandProcessorEventListener<M>>();
	
	public void addCommandProcessorEventListener(CommandProcessorEventListener<M> listener)
	{
		eventListeners.add(listener);
	}
	public void removeCommandProcessorEventListener(CommandProcessorEventListener<M> listener)
	{
		eventListeners.remove(listener);
	}
	
	private void notifyListenersOfProcessorStarting()
	{
		for(CommandProcessorEventListener<M> listener : eventListeners)
			listener.processorStarting();
	}
	private void notifyListenersOfCommandStart(Command<M> command)
	{
		for(CommandProcessorEventListener<M> listener : eventListeners)
			listener.commandStarting(command);
	}
	private void notifyListenersOfCommandComplete(Command<M> command)
	{
		for(CommandProcessorEventListener<M> listener : eventListeners)
			listener.commandComplete(command);
	}
	private void notifyListenersOfCommandException(Command<M> command, Exception x)
	{
		for(CommandProcessorEventListener<M> listener : eventListeners)
			listener.commandException(command, x);
	}
	private void notifyListenersOfProcessorExiting()
	{
		for(CommandProcessorEventListener<M> listener : eventListeners)
			listener.processorExiting();
	}
	
	// Logging listener
	class LoggingListener<N> 
	implements CommandProcessorEventListener<N>
	{

		@Override
		public void commandComplete(Command<N> command){System.out.println("Command '" + command.toString() + "' Complete");}

		@Override
		public void commandException(Command<N> command, Exception x){System.out.println("Command '" + command.toString() + "' ended with exception " + x.toString());}

		@Override
		public void commandStarting(Command<N> command){System.out.println("Command '" + command.toString() + "' Starting");}

		@Override
		public void processorExiting(){System.out.println("Processor Exiting");}

		@Override
		public void processorStarting(){System.out.println("Processor Starting");}
	}
}