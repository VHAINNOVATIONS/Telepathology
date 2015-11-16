package gov.va.med.imaging.core.router.periodiccommands;

import java.security.Principal;
import java.util.List;

import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.queue.ScheduledPriorityQueueElement;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.notifications.NotificationFacade;
import gov.va.med.imaging.notifications.NotificationTypes;
import gov.va.med.imaging.transactioncontext.InvalidTransactionContextMementoException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextMemento;
import gov.va.med.server.ServerAgnosticEngine;
import gov.va.med.server.ServerAgnosticEngineAdapter;
import gov.va.med.server.ServerLifecycleEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PeriodicCommandEngineAdapter extends
		gov.va.med.imaging.core.interfaces.router.AbstractFacadeRouterImpl
		implements ServerAgnosticEngine {

	private static Logger logger = Logger.getLogger(PeriodicCommandEngineAdapter.class);
	private static ServerAgnosticEngineAdapter engineAdapter;
	private static PeriodicCommandEngineAdapter periodicCommandEngineAdapter;

	public PeriodicCommandEngineAdapter() {
		super();
		periodicCommandEngineAdapter = this;
	}

	protected Logger getLogger() {
		return logger;
	}

	@SuppressWarnings("unchecked")
	public static void initializePeriodicCommand(Class returnClass,
			String commandClassName, Object[] commandParameters,
			ScheduledPriorityQueueElement.Priority priority, int delayInterval) 
	{
		Class<?>[] commandParameterTypes = periodicCommandEngineAdapter.deriveParameterTypesFromParameters(commandParameters);
		Command cmd = periodicCommandEngineAdapter.getCommandFactory().createCommand(returnClass,
				commandClassName, null, commandParameterTypes, commandParameters);
		cmd.setPriority(priority.ordinal());
		cmd.setPeriodicExecutionDelay(delayInterval);
		cmd.setPeriodic(true);
		
		// Send each command to the black box for asynchronous
		// execution.
		periodicCommandEngineAdapter.getRouter().doAsynchronously(cmd);
	}
	
	private Class<?>[] deriveParameterTypesFromParameters(Object[] initArgs)
	throws IllegalArgumentException
	{
		if(initArgs == null || initArgs.length == 0)
			return new Class<?>[]{};
		
		Class<?>[] parameterTypes = new Class<?>[initArgs.length];
		
		for(int n=0; n<initArgs.length; ++n)
		{
			if(initArgs[n] == null)
				throw new IllegalArgumentException("The parameter list contains a null value, such ambigious types are not allowed.");
			parameterTypes[n] = initArgs[n] == null ? java.lang.Void.class : initArgs[n].getClass();
		}
		
		return parameterTypes;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void serverEvent(ServerLifecycleEvent event) 
	{
		if (event.getEventType().equals(ServerLifecycleEvent.EventType.START)) 
		{
			initializeAndStartPeriodicCommands();
		}
	}

	public static void initializeAndStartPeriodicCommands() 
	{
		logger.info("Initializing periodic commands");

		TransactionContextMemento memento = TransactionContextFactory.get().getMemento();
		try
		{
			// set up a security realm for the commands
			DicomServerConfiguration config = DicomServerConfiguration.getConfiguration();
			String accessCode = config.getAccessCodeString();
			String verifyCode = config.getVerifyCodeString();
	
			// Authenticate on the main thread.
			Principal principal = engineAdapter.authenticate(accessCode, verifyCode.getBytes());
			
			// If a principal was returned, authentication was successful, so we can 
			// go ahead and start the periodic commands. If it's null, however, the 
			// credentials were invalid. 
			if (principal != null)
			{
				// Kick off each of the periodic commands...
				List<PeriodicCommandDefinition> commandDefinitions = PeriodicCommandConfiguration.getConfiguration().getCommandDefinitions();
				
				for (PeriodicCommandDefinition definition : commandDefinitions) 
				{
					logger.info("Attempting to start periodic command: " + definition.getCommandClassName());
					
					initializePeriodicCommand(definition.getReturnClass(),
							definition.getCommandClassName(), 
							definition.getCommandParameters(),
							ScheduledPriorityQueueElement.Priority.NORMAL,
							Integer.parseInt(definition.getPeriodicDelayInterval()));

					logger.info("Successfully started periodic command: " + definition.getCommandClassName());
				}
			}
			else
			{
				// Send notification
				String subject = "Periodic command initialization failure: invalid service account credentials";
				String message = "The system was unable to start periodic commands because the service account credentials" +
								 " are invalid.";
				NotificationFacade.sendNotification(NotificationTypes.InvalidServiceAccountCredentials, subject, message);
			}
		}
		catch (Exception e)
		{
			logger.error("Exception starting periodic commands", e);
		}
		finally
        {
              TransactionContextFactory.restoreTransactionContext(memento);
        }

	}

	@Override
	public void setServerAgnosticEngineAdapter(ServerAgnosticEngineAdapter engineAdapter) 
	{
		PeriodicCommandEngineAdapter.engineAdapter = engineAdapter;
	}

	public static PeriodicCommandEngineAdapter getSingletonInstance() 
	{
		return periodicCommandEngineAdapter;
	}
}
