package gov.va.med.imaging.storage.cache.interactive;


import java.util.concurrent.ArrayBlockingQueue;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandFactory;
import gov.va.med.interactive.CommandController;
import gov.va.med.interactive.CommandProcessor;
import javax.management.MBeanException;

/**
 * This is a class that will start a cache instance and allow it to be managed
 * through JMX.  It takes the place of CacheFactory (in the production, application 
 * server resident) code.  It also allows for interactive or command-line driven 
 * management through the CacheManagerImpl interface.
 * 
 * This class creates a command queue and then starts two threads:
 * 1.) A command line processor that first process the command line args and then
 * goes into an interactive mode to handle user commands.  This thread pushes commands
 * onto the queue.
 * 2.) A command processor that polls commands from the queue and processes them. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class InteractiveCacheContainer
extends CommandController<CacheManagerImpl>
{
	/**
	 * 
	 * @param managedObject
	 * @param commandFactory
	 */
	public InteractiveCacheContainer(
		CacheManagerImpl managedObject, 
		CommandFactory<CacheManagerImpl> commandFactory)
	{
		super(managedObject, commandFactory);
	}
	
	
	/**
	 * This main() method will start a cache instance and wait for user input.
	 * It is intended for development time to test managability.
	 * 
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		CommandController.initializeLogging();
		
		CacheManagerImpl cacheManager;
		try
		{
			cacheManager = CacheManagerImpl.getSingleton();
		}
		catch (MBeanException x1)
		{
			x1.printStackTrace();
			return;
		}
		catch (CacheException x1)
		{
			x1.printStackTrace();
			return;
		}
		try
		{
			InteractiveCacheCommandFactoryImpl commandFactory = InteractiveCacheCommandFactoryImpl.getSingleton();
			InteractiveCacheContainer icc = new InteractiveCacheContainer(cacheManager, commandFactory);
			
			icc.getCommandSource().pushCommands(argv);
			icc.run();
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return;
		}
	}
	

}
