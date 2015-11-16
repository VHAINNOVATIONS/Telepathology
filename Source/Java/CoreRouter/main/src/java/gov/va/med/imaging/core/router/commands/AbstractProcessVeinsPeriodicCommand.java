package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.facade.InternalRouter;
import gov.va.med.imaging.exchange.business.TimePeriod;
import gov.va.med.imaging.notifications.Notification;
import gov.va.med.imaging.notifications.email.NotificationEmailProtocol;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

public abstract class AbstractProcessVeinsPeriodicCommand extends
		AbstractCommandImpl<Boolean> {
	private static final long serialVersionUID = 9934723485934L;

	private Logger logger = Logger
			.getLogger(ProcessVeinsPeriodicCommandImpl.class);
	private RoutingToken routingToken;
	private ErrorTypeNotificationConfiguration config;
	private TimePeriod evaluationTimePeriod;

	public AbstractProcessVeinsPeriodicCommand() {
		super();
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException {
		if (!ProcessVeinsPeriodicCommandImpl.isPrimaryNode())
			return false;
		if (executedWithinPreviousTimePeriod())
			return false;
		InternalRouter router = InternalContext.getRouter();
		int errorCount = getErrorCount();
		getConfig().setLastExecutionDateTime(new Date());
		if (errorCount > 0) {
			sendNotification();
			getConfig().setLastEmailSentDateTime(new Date());
		}
		router.postErrorTypeNotificationConfiguration(getRoutingToken(),
				getConfig());
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	protected boolean executedWithinPreviousTimePeriod()
			throws MethodException, ConnectionException {
		return getPreviousExecutionTimePeriod().contains(
				getConfig().getLastExecutionDateTime());
	}

	protected ErrorTypeNotificationConfiguration getConfig()
			throws MethodException, ConnectionException {
		if (config == null) {
			InternalRouter router = InternalContext.getRouter();
			config = router.getErrorTypeNotificationConfiguration(
					getRoutingToken(), getErrorType());
		}
		return config;
	}

	public List<InternetAddress> getEmailAddresses() throws MethodException,
			ConnectionException {
		ArrayList<InternetAddress> emailAddresses = new ArrayList<InternetAddress>();
		for (String email : getConfig().getEmailAddresses()) {
			try {
				for (InternetAddress emailAddress : InternetAddress
						.parse(email)) {
					emailAddresses.add(emailAddress);
				}
			} catch (AddressException ae) {
				throw new MethodException(ae);
			}
		}
		return emailAddresses;
	}

	protected abstract int getErrorCount() throws MethodException,
			ConnectionException;

	protected abstract String getErrorType();

	protected TimePeriod getEvaluationTimePeriod() throws MethodException,
			ConnectionException {
		if (evaluationTimePeriod == null) {
			Date dailyExecutionTime = getConfig().getDailyExecutionTime();
			Calendar now = Calendar.getInstance();
			if (dailyExecutionTime == null) {
				Calendar startTime = Calendar.getInstance();
				startTime.add(Calendar.SECOND, getConfig()
						.getExecutionDelayInSeconds()
						* -1);
				evaluationTimePeriod = new TimePeriod(startTime, now);
			} else {
				Calendar startTime = getTodaysExecutionTime();
				if (startTime.after(now)) {
					startTime.add(Calendar.HOUR, -24);
				}
				evaluationTimePeriod = new TimePeriod(startTime, now);
			}
			Date lastExecution = getConfig().getLastExecutionDateTime();
			if (lastExecution != null
					&& evaluationTimePeriod.after(lastExecution)) {
				evaluationTimePeriod.setStartDate(lastExecution);
			}
		}
		return evaluationTimePeriod;
	}

	protected abstract String getMessageBody() throws MethodException,
			ConnectionException;

	protected Calendar getNextExecutionTime() throws MethodException,
			ConnectionException {
		Date dailyExecutionTime = getConfig().getDailyExecutionTime();
		Date previousExecutionTime = getConfig().getLastExecutionDateTime();
		Calendar nextExecutionTime;
		if (dailyExecutionTime == null) {
			nextExecutionTime = Calendar.getInstance();
			nextExecutionTime.setTime(previousExecutionTime);
			nextExecutionTime.add(Calendar.SECOND, getConfig()
					.getExecutionDelayInSeconds());
		} else {
			Calendar now = Calendar.getInstance();
			nextExecutionTime = this.getTodaysExecutionTime();
			if (nextExecutionTime.before(now)) {
				nextExecutionTime.add(Calendar.HOUR, 24);
			}
		}
		return nextExecutionTime;
	}

	protected TimePeriod getPreviousExecutionTimePeriod()
			throws MethodException, ConnectionException {
		Date dailyExecutionTime = getConfig().getDailyExecutionTime();
		TimePeriod previousExecutionTimePeriod;
		Calendar now = Calendar.getInstance();
		if (dailyExecutionTime == null) {
			Calendar startTime = Calendar.getInstance();
			startTime.add(Calendar.SECOND, getConfig()
					.getExecutionDelayInSeconds()
					* -1);
			previousExecutionTimePeriod = new TimePeriod(startTime, now);
		} else {
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(dailyExecutionTime);
			startTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
			startTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
			startTime
					.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
			if (startTime.after(now)) {
				startTime.add(Calendar.HOUR, -24);
			}
			previousExecutionTimePeriod = new TimePeriod(startTime, now);
		}
		return previousExecutionTimePeriod;
	}

	public RoutingToken getRoutingToken() {
		if (routingToken == null) {
			try {
				routingToken = RoutingTokenImpl
						.createVARadiologySite(ProcessVeinsPeriodicCommandImpl
								.getSiteNumber());
			} catch (RoutingTokenFormatException rtfe) {
				logger.error(rtfe);
			}
		}
		return routingToken;
	}

	protected abstract String getSubjectLine() throws MethodException,
			ConnectionException;

	protected Calendar getTodaysExecutionTime() throws MethodException,
			ConnectionException {
		Calendar now = Calendar.getInstance();
		Calendar dailyExecutionTime = Calendar.getInstance();
		dailyExecutionTime.setTime(getConfig().getDailyExecutionTime());
		dailyExecutionTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
		dailyExecutionTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
		dailyExecutionTime.set(Calendar.DAY_OF_MONTH, now
				.get(Calendar.DAY_OF_MONTH));
		return dailyExecutionTime;
	}

	@Override
	protected String parameterToString() {
		return "";
	}

	protected void sendNotification() throws MethodException,
			ConnectionException {
		NotificationEmailProtocol nEmailProtocol = new NotificationEmailProtocol();
		Notification eMailMsg = Notification.getGenericNotification(
				getSubjectLine(), getMessageBody());
		nEmailProtocol.sendTo(getEmailAddresses(), eMailMsg);
	}
}
