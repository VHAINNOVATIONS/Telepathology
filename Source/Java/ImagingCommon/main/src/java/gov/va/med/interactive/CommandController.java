/**
 * 
 */
package gov.va.med.interactive;

import gov.va.med.interactive.commands.CommandFileCommand;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The CommandController simply ties the source, factory and the processor
 * together and controls the thread starting.
 * 
 * @author vhaiswbeckec
 *
 */
public class CommandController<M>
implements Runnable, CommandProcessorEventListener<M>
{
	private final CommandSource<M> commandSource;
	private final CommandFactory<M> commandFactory;
	private final CommandProcessor<M> commandProcessor;
	private final boolean sourceOnWorkerThread;
	private final boolean processorOnWorkerThread;
	private final boolean waitForProcessorComplete;
	
	private BlockingQueue<Command<M>> commandQueue;
	private M managedObject = null;
	
	private final Object processorCompleteLock = new Object();
	
	/**
	 * The simplest constructor, defaults to 
	 * a command line command source on the main thread,
	 * a default command processor running on a worker thread
	 * 
	 * @param managedObject
	 * @param commandFactory
	 */
	public CommandController(
		M managedObject,
		CommandFactory<M> commandFactory)
	{
			this(
				managedObject, 
				new CommandLineCommandSource<M>(), 
				commandFactory, 
				new CommandProcessor<M>()
			);
	}

	/**
	 * @param managedObject
	 * @param commandFactory
	 * @param commandSource
	 */
	public CommandController(
		M managedObject,
		CommandFactory<M> commandFactory,
		CommandSource<M> commandSource,
		boolean sourceOnWorkerThread,
		boolean processorOnWorkerThread,
		boolean waitForProcessorComplete
		)
	{
			this(
				managedObject, 
				commandSource, 
				commandFactory, 
				new CommandProcessor<M>(),
				sourceOnWorkerThread,
				processorOnWorkerThread,
				(String[])null,
				waitForProcessorComplete
			);
	}
	
	/**
	 * 
	 * @param managedObject
	 * @param commandFactory
	 * @throws IOException 
	 */
	public CommandController(
		M managedObject,
		CommandFactory<M> commandFactory,
		String[] initialCommands) 
	throws IOException
	{
		this(
			managedObject,
			(CommandSource<M>)createCommandSource(initialCommands), 
			commandFactory, 
			new CommandProcessor<M>(),
			false, 
			true, 
			initialCommands,
			true
		);
	}
	
	/**
	 * Use the command line args to determine what kind of command source to create.
	 * @return
	 * @throws IOException 
	 */
	private static CommandSource<?> createCommandSource(String[] initialCommands) 
	throws IOException
	{
		// if the command line starts with -f some-resource-name
		// create a command line source that reads commands from a file
		if( initialCommands != null && initialCommands.length >= 2 && "-f".equals(initialCommands[0]) )
		{
			String[] subStrings = new String[initialCommands.length-2];
			System.arraycopy(initialCommands, 2, subStrings, 0, subStrings.length);
			return CommandFileCommandSource.create(initialCommands[1], subStrings);
		}
		else
			return new CommandLineCommandSource();
	}

	/**
	 * 
	 * @param managedObject
	 * @param commandSource
	 * @param commandFactory
	 * @param commandProcessor
	 */
	public CommandController(
		M managedObject,
		CommandSource<M> commandSource,
		CommandFactory<M> commandFactory,
		CommandProcessor<M> commandProcessor)
	{
		this(managedObject, commandSource, commandFactory, commandProcessor, false, true, null, true);
	}

	/**
	 * 
	 * @param managedObject - the thing being managed, passed to the commmands
	 * @param commandFactory - a command factory, so a command or command source can create a command
	 * @param commandProcessor - the thing that executes the commands
	 */
	public CommandController(
		M managedObject,
		CommandSource<M> commandSource,
		CommandFactory<M> commandFactory,
		CommandProcessor<M> commandProcessor,
		boolean sourceOnWorkerThread,
		boolean processorOnWorkerThread,
		String[] initialCommands,
		boolean waitForProcessorComplete)
	{
		assert(managedObject != null);
		assert(commandSource != null);
		assert(commandFactory != null);
		assert(commandProcessor != null);
		
		this.managedObject = managedObject;
		this.commandSource = commandSource;
		this.commandFactory = commandFactory;
		this.commandProcessor = commandProcessor;
		this.sourceOnWorkerThread = sourceOnWorkerThread;
		this.processorOnWorkerThread = processorOnWorkerThread;
		this.waitForProcessorComplete = waitForProcessorComplete;
		
		this.commandSource.setCommandFactory(commandFactory);
		this.commandQueue = new ArrayBlockingQueue<Command<M>>(100);
		
		// get command and processor events
		this.commandProcessor.addCommandProcessorEventListener(this);
		
		this.commandProcessor.setCommandController(this);
		this.commandSource.setCommandController(this);
		
		if(initialCommands != null)
			try
			{
				this.getCommandSource().pushCommands(initialCommands);
			}
			catch (CommandLineParseException x)
			{
				x.printStackTrace();
			}
	}
	
	public M getManagedObject()
	{
		return this.managedObject;
	}

	/**
	 * @return the commandQueue
	 */
	public BlockingQueue<Command<M>> getCommandQueue()
	{
		return this.commandQueue;
	}

	/**
	 * @return the commandSource
	 */
	public CommandSource<M> getCommandSource()
	{
		return this.commandSource;
	}

	/**
	 * @return the commandFactory
	 */
	public CommandFactory<M> getCommandFactory()
	{
		return this.commandFactory;
	}

	/**
	 * @return the commandProcessor
	 */
	public CommandProcessor<M> getCommandProcessor()
	{
		return this.commandProcessor;
	}

	/**
	 * @return the sourceOnWorkerThread
	 */
	public boolean isSourceOnWorkerThread()
	{
		return this.sourceOnWorkerThread;
	}

	/**
	 * @return the processorOnWorkerThread
	 */
	public boolean isProcessorOnWorkerThread()
	{
		return this.processorOnWorkerThread;
	}

	/**
	 * Start the source and processors and wait for them to finish.
	 * 
	 * @param argv
	 */
	public void run()
	{
		try
		{
			Thread commandSourceThread = null;
			Thread commandProcessorThread = null;
			
			// run the command source in either a separate thread or on the
			// main thread
			if(this.sourceOnWorkerThread)
			{
				commandSourceThread = new Thread(getCommandSource());
				commandSourceThread.setName("commandSource");
			}
			
			// run the command processor in either a separate thread or on the
			// main thread
			if(this.processorOnWorkerThread)
			{
				commandProcessorThread = new Thread(getCommandProcessor());
				commandProcessorThread.setName("commandProcessor");
			}
			
			// start the processor so it will run the commands as they are queued
			// if the processor is running on the main thread then wait to call it
			if(commandProcessorThread != null)
				commandProcessorThread.start();

			// start the command source on a separate thread or the main thread
			if(commandSourceThread != null)
				commandSourceThread.start();
			else
				getCommandSource().run();

			// if the command processor is running on the main thread
			// then call its run method directly
			if(commandProcessorThread == null)
				getCommandProcessor().run();
			else if(waitForProcessorComplete)
				waitForProcessorComplete();
		} 
		catch (Exception x)
		{
			x.printStackTrace();
		} 
	}

	/**
	 * If the processor is running on a separate thread and
	 * the calling client has elected NOT to wait for the processor
	 * to complete then the client may call this at some point
	 * later to wait for the processor to complete.
	 * Failure to wait for the processor may result in un-executed
	 * commands in the queue.
	 *   
	 * @throws InterruptedException
	 */
	public void waitForProcessorComplete() 
	throws InterruptedException
	{
		synchronized(processorCompleteLock)
		{
			processorCompleteLock.wait(0);
		}
	}
	
	// ====================================================================================
	// Command Processor Listener implementation
	// ====================================================================================
	
	@Override
	public void processorStarting()
	{
		//System.out.println("Command processor starting ");
	}
	
	@Override
	public void commandStarting(Command<M> command)
	{
		//System.out.println("Command starting ");
	}

	@Override
	public void commandComplete(Command<M> command)
	{
		//System.out.println("Command successful ");
	}

	@Override
	public void commandException(Command<M> command, Exception x)
	{
		//System.out.println("Command exception " + x.getMessage());
	}

	@Override
	public void processorExiting()
	{
		//System.out.println("Command processor exiting ...");
		synchronized (processorCompleteLock)
		{
			processorCompleteLock.notifyAll();
		}
	}

	// ====================================================================================
	// Miscellaneous utility methods 
	// ====================================================================================
	/**
	 * 
	 */
	public static void initializeLogging()
	{
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Layout layout = new org.apache.log4j.PatternLayout("%d{DATE} %5p [%t] (%F:%L) - %m%n");
		Appender appender = new org.apache.log4j.ConsoleAppender(layout);
		Logger.getRootLogger().addAppender(appender);
	}
}
