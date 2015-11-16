package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.commands.storage.AsyncQueueNames;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.facade.InternalRouter;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.storage.Place;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

@RouterCommandExecution(asynchronous = true, distributable = false)
public class ProcessVeinsFailedArchivePeriodicCommandImpl extends
		AbstractProcessVeinsPeriodicCommand {

	private static final long serialVersionUID = 4812650283645L;
	private static final String errorType = "3";
	private static DurableQueue asyncErrorQueue;

	private int errorCount = -1;
	private int placeId;
	private Logger logger = Logger
			.getLogger(ProcessVeinsPeriodicCommandImpl.class);

	public ProcessVeinsFailedArchivePeriodicCommandImpl() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	protected DurableQueue getAsyncErrorQueue() throws ConnectionException,
			MethodException {
		if (asyncErrorQueue == null) {
			asyncErrorQueue = InternalContext.getRouter()
					.getDurableQueueByName(this.getRoutingToken(),
							AsyncQueueNames.asyncStorageErrorQueue);
		}
		return asyncErrorQueue;
	}

	@Override
	protected int getErrorCount() throws MethodException, ConnectionException {
		if (errorCount < 0) {
			InternalRouter router = InternalContext.getRouter();
			errorCount = router.getDurableQueueMessageCount(getRoutingToken(),
					getAsyncErrorQueue().getId(), Integer
							.toString(getPlaceId()));
		}
		return errorCount;
	}

	@Override
	protected String getErrorType() {
		return errorType;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public String getMessageBody() throws MethodException, ConnectionException {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder
				.append("You can view the messages at the following web page:\r\n");
		messageBuilder.append("http://");
		try {
			messageBuilder.append(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException uhe) {
			throw new MethodException(uhe);
		}
		messageBuilder.append(":8080/QueueMaintenanceWebApp/queueMessages.jsp");
		return messageBuilder.toString();
	}

	@Override
	public Command<Boolean> getNewPeriodicInstance() throws MethodException {
		ProcessVeinsFailedArchivePeriodicCommandImpl command = new ProcessVeinsFailedArchivePeriodicCommandImpl();
		command.setPeriodic(true);
		Calendar nextExecution = Calendar.getInstance();
		try {
			nextExecution = getNextExecutionTime();
		} catch (Exception e) {
			nextExecution.add(Calendar.HOUR_OF_DAY, 24);
		}
		Date now = new Date();
		long delay = nextExecution.getTimeInMillis() - now.getTime();
		command.setPeriodicExecutionDelay((int) delay);
		command.setCommandContext(this.getCommandContext());
		return command;
	}

	public int getPlaceId() {
		if (placeId == 0) {
			StorageServerDatabaseConfiguration storageConfig = StorageServerDatabaseConfiguration
					.getConfiguration();
			Place place = storageConfig
					.getPlace(ProcessVeinsPeriodicCommandImpl.getSiteNumber());
			placeId = place.getId();
		}
		return placeId;
	}

	public String getSubjectLine() throws MethodException, ConnectionException {
		return Integer.toString(getErrorCount())
				+ " Errors in the Async Storage Request Error Queue";
	}

	@Override
	protected String parameterToString() {
		return "";
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
