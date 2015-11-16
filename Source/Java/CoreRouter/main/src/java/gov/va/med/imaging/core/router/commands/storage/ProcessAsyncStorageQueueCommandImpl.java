package gov.va.med.imaging.core.router.commands.storage;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.InvalidUserCredentialsException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.facade.InternalRouter;
import gov.va.med.imaging.core.router.storage.StorageBusinessRouter;
import gov.va.med.imaging.core.router.storage.StorageContext;
// import gov.va.med.imaging.dicom.DicomContext;
// import gov.va.med.imaging.dicom.DicomRouter;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
// import gov.va.med.imaging.exchange.business.EmailMessage;
import gov.va.med.imaging.exchange.business.ServiceRegistration;
import gov.va.med.imaging.exchange.business.ServiceStatus;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.AsyncStorageRequest;
import gov.va.med.imaging.exchange.business.storage.Place;
// import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.notifications.NotificationFacade;
import gov.va.med.imaging.notifications.NotificationTypes;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

@RouterCommandExecution(asynchronous = true, distributable = false)
public class ProcessAsyncStorageQueueCommandImpl extends
		AbstractCommandImpl<Boolean> {

	private static final long serialVersionUID = 4923784727343L;
	private static final DicomServerConfiguration config = DicomServerConfiguration
			.getConfiguration();
	private static DurableQueue asyncQueue;
	private static DurableQueue asyncErrorQueue;
	private Logger logger = Logger
			.getLogger(ProcessAsyncStorageQueueCommandImpl.class);
	protected RoutingToken routingToken;
	private static ServiceRegistration registration;

	public ProcessAsyncStorageQueueCommandImpl() {
		super();
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException {
		if (!config.isArchiveEnabled()) {
			return true; // exit if archive is not enabled
		}
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getLocalSiteId());
//		InternalContext.getRouter().processServiceRegistration(
//				this.getRoutingToken(), getRegistration());
		logger.info("Checking for async storage requests");
		InternalRouter internalRouter = InternalContext.getRouter();
		StorageBusinessRouter storageBusinesssRouter = StorageContext
				.getBusinessRouter();
        String emailRecepient = DicomServerConfiguration.getConfiguration().getDgwEmailInfo().getEMailAddress();
        String threadID = " [" + Long.toString(Thread.currentThread().getId()) + "]";
        String[] eMailTOs=new String[1];
        eMailTOs[0] = emailRecepient;
		Place place = StorageServerDatabaseConfiguration.getConfiguration()
				.getPlace(getLocalSiteId());
		DurableQueueMessage queueMessage = internalRouter
				.dequeueDurableQueueMessage(getRoutingToken(), getAsyncQueue()
						.getId(), Integer.toString(place.getId()));
		AsyncStorageRequest request;
		while (queueMessage != null) {
			request = null;
			try {
				request = AsyncStorageRequest
						.deserializeUsingXStream(queueMessage.getMessage());
			} catch (Exception e) {
				logger.info("Error parsing async storage queue message.\r\n"
						+ queueMessage.getMessage());
				queueMessage.setQueueId(getAsyncErrorQueue().getId());
				internalRouter.enqueueDurableQueueMessage(getRoutingToken(), queueMessage);

				// In order for the periodic commmand to shut down if necessary
				// rethrow the exception if it's one of the "fatal" errors
				rethrowIfFatalException(e);
			}
			if (request != null) {
				try {
					storageBusinesssRouter.processAsyncStorageRequest(request);
				} catch (Exception e) {
					logger.error("Error processing async storage request", e);
					request.setLastError(e.getMessage());
					requeue(queueMessage, request, eMailTOs, threadID);
					
					// In order for the periodic commmand to shut down if necessary
					// rethrow the exception if it's one of the "fatal" errors
					rethrowIfFatalException(e);
				}
			}
			queueMessage = internalRouter.dequeueDurableQueueMessage(
					getRoutingToken(), getAsyncQueue().getId(), Integer
							.toString(place.getId()));
		}
		return true;
	}

	private void requeue(DurableQueueMessage queueMessage,
			AsyncStorageRequest request, String[] eMailTos, String threadId) throws MethodException,
			ConnectionException {
		request.setNumAttempts(request.getNumAttempts() + 1);
		queueMessage.setMessage(request.serializeUsingXStream());
		if (request.getNumAttempts() < getAsyncQueue().getNumRetries()) {
			logger
					.info("Requeueing async storage request for future processing");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, getAsyncQueue().getRetryDelayInSeconds());
			queueMessage.setMinDeliveryDateTime(cal.getTime());
			queueMessage.setQueueId(getAsyncQueue().getId());
		} else {
			logger.info("The maximum number of attempts for this async"
					+ "storage request has been exceeded.  "
					+ "Moving the request to the Error Queue");
			queueMessage.setMinDeliveryDateTime(null);
			queueMessage.setQueueId(getAsyncErrorQueue().getId());
			// post Error message to e-mail queue
			postToEmailQ(request, eMailTos, "Archive Error" /* subjectLine */, threadId);			
		}
		InternalContext.getRouter().enqueueDurableQueueMessage(
				getRoutingToken(), queueMessage);
	}

	/**
	 * @param request
	 * @param eMailTos
	 * @param subjectLine
	 * @param threadId
	 * 
	 * post Error message to e-mail queue
	 * postToEmailQ must be overridden from the project, where body is implemented (ImagingDicomRouter) -- PostToEmailQueueImpl?
	 */
	protected void postToEmailQ(AsyncStorageRequest request, String[] eMailTos, String subjectLine,
			String threadId) {
//		EmailMessage email = new EmailMessage(eMailTos,
//				subjectLine,
//				request.getLastError()); // the message body
//		DicomRouter rtr = DicomContext.getRouter();
//		try { 
//			rtr.postToEmailQueue(email, threadId);
//		} 
//		catch (MethodException me) {
//			logger.error("Error queueing Email for \n'" + request.getLastError() + "' - ThreadID" +threadId);	
//		} 
//		catch (ConnectionException ce) {
//			logger.error("DB Connection error while queueing Email for \n'" + request.getLastError() + "' - ThreadID" +threadId);	
//		}
		return;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	private DurableQueue getAsyncErrorQueue() throws ConnectionException,
			MethodException {
		if (asyncErrorQueue == null) {
			asyncErrorQueue = InternalContext.getRouter()
					.getDurableQueueByName(this.getRoutingToken(),
							AsyncQueueNames.asyncStorageErrorQueue);
		}
		return asyncErrorQueue;
	}

	private DurableQueue getAsyncQueue() throws ConnectionException,
			MethodException {
		if (asyncQueue == null) {
			asyncQueue = InternalContext.getRouter().getDurableQueueByName(
					this.getRoutingToken(),
					AsyncQueueNames.asyncStorageRequestQueue);
		}
		return asyncQueue;
	}

	@Override
	public Command<Boolean> getNewPeriodicInstance() throws MethodException {
		ProcessAsyncStorageQueueCommandImpl command = new ProcessAsyncStorageQueueCommandImpl();
		command.setPeriodic(DicomServerConfiguration.getConfiguration().isArchiveEnabled());
		command.setPeriodicExecutionDelay(this.getPeriodicExecutionDelay());
		command.setCommandContext(this.getCommandContext());
		return command;
	}

	public RoutingToken getRoutingToken() {
		if (routingToken == null)
			routingToken = getCommandContext().getLocalSite().getArtifactSource().createRoutingToken();
		return routingToken;
	}

	@Override
	protected String parameterToString() {
		return "";
	}

	public ServiceRegistration getRegistration() throws MethodException,
			ConnectionException {
		if (registration == null) {
			registration = new ServiceRegistration("HDIG Archiver", "1.0",
					ServiceStatus.Online, getLocalSiteId());
		}
		return registration;
	}

	public static void setRegistration(ServiceRegistration registration) {
		ProcessAsyncStorageQueueCommandImpl.registration = registration;
	}
	
	public List<Class<? extends MethodException>> getFatalPeriodicExceptionClasses()
	{
		List<Class<? extends MethodException>> fatalExceptions = new ArrayList<Class<? extends MethodException>>();
		fatalExceptions.add(InvalidUserCredentialsException.class);
		return fatalExceptions;
	}
	
	/**
	 * This method is called when a periodic command has thrown a fatal exception as defined by the list in getFatalPeriodicExceptionClasses(). At the point when this method is called
	 * the periodic command has already stopped executing and will not execute again.  This method is meant to allow the command to alert someone of the failure (such as by sending 
	 * an email message)
	 * @param t
	 */
	public void handleFatalPeriodicException(Throwable t)
	{
		String subject = "Invalid HDIG service account credentials";
		String message = "The ProcessAsyncStorageQueue periodic command has shut down due to invalid HDIG service account credentials.";
		NotificationFacade.sendNotification(NotificationTypes.InvalidServiceAccountCredentials, subject, message);
	}

}
