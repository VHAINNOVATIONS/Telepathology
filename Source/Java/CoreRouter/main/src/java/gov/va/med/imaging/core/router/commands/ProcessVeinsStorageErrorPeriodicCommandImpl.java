package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.storage.StorageContext;
import gov.va.med.imaging.core.router.storage.StorageDataSourceRouter;
import gov.va.med.imaging.exchange.business.TimePeriod;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

@RouterCommandExecution(asynchronous = true, distributable = false)
public class ProcessVeinsStorageErrorPeriodicCommandImpl extends
		AbstractProcessVeinsPeriodicCommand {

	private static final long serialVersionUID = 9342734956037L;
	protected static final int MAX_MESSAGE_SIZE = 1024 * 1024; // 1MB

	private final String errorType;
	protected ErrorTypeNotificationConfiguration config;
	private Logger logger = Logger
			.getLogger(ProcessVeinsStorageErrorPeriodicCommandImpl.class);
	private int errorCount = -1;

	public ProcessVeinsStorageErrorPeriodicCommandImpl(String errorType) {
		this.errorType = errorType;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	protected int getErrorCount() throws MethodException, ConnectionException {
		if (errorCount < 0) {
			StorageDataSourceRouter router = StorageContext
					.getDataSourceRouter();
			errorCount = router.getStorageTransactionsWithinTimePeriod("W",
					"F", getEvaluationTimePeriod());
		}
		return errorCount;
	}

	@Override
	protected String getErrorType() {
		return this.errorType;
	}

	public Logger getLogger() {
		return logger;
	}

	/**
	 * Returns a list of message bodies. The default implementation breaks the
	 * error list up into roughly 1MB chunks.
	 * 
	 * This can be overriden in derived classes if the message body is not just
	 * a list of errors...
	 * 
	 * @param errorList
	 * @return
	 */
	protected List<String> getMessageBodies(List<String> errorList) {
		List<String> messageBodies = new ArrayList<String>();

		if (errorList != null && errorList.size() > 0) {
			String messageBody = "";
			for (String error : errorList) {
				messageBody = messageBody + error
						+ System.getProperty("line.separator")
						+ System.getProperty("line.separator");

				if (messageBody.length() >= MAX_MESSAGE_SIZE) {
					// We've reached or passed max message body size. Add the
					// messageBody to the list
					// and reset the messageBody before continuing
					messageBodies.add(messageBody);
					messageBody = "";
				}
			}
		}

		return messageBodies;
	}

	@Override
	protected String getMessageBody() throws MethodException,
			ConnectionException {
		TimePeriod period = this.getEvaluationTimePeriod();
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("There are ");
		messageBuilder.append(Integer.toString(getErrorCount()));
		messageBuilder
				.append(" Errors in the Storage Transaction file (2006.926)");
		messageBuilder.append(" over the period ");
		messageBuilder.append(period.toString());
		return messageBuilder.toString();
	}

	@Override
	public Command<Boolean> getNewPeriodicInstance() throws MethodException {
		ProcessVeinsStorageErrorPeriodicCommandImpl command = new ProcessVeinsStorageErrorPeriodicCommandImpl(
				errorType);
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

	@Override
	protected String getSubjectLine() throws MethodException,
			ConnectionException {
		return Integer.toString(getErrorCount())
				+ " Errors in the Storage Transaction File";
	}

	@Override
	protected String parameterToString() {
		return "";
	}

}
