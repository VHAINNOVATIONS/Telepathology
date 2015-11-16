package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.facade.InternalRouter;
import gov.va.med.imaging.core.router.periodiccommands.PeriodicCommandEngineAdapter;
import gov.va.med.imaging.core.router.queue.ScheduledPriorityQueueElement;
import gov.va.med.imaging.exchange.business.ServiceRegistration;
import gov.va.med.imaging.exchange.business.ServiceStatus;
import gov.va.med.imaging.exchange.configuration.AppConfiguration;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;

import java.util.List;

import org.apache.log4j.Logger;

@RouterCommandExecution(asynchronous = true, distributable = false)
public class ProcessVeinsPeriodicCommandImpl extends
		AbstractCommandImpl<Boolean> {

	private static final long serialVersionUID = 8263746918376L;
	private static boolean isPrimaryNode;
	private static String siteNumber;
	private static ServiceRegistration serviceRegistration;
	private static boolean childCommandsInitialized;

	static {
		AppConfiguration config = new AppConfiguration();
		try {
			config.init();
			siteNumber = config.getLocalSiteNumber();
		} catch (ApplicationConfigurationException ace) {
		}
	}

	public ServiceRegistration getServiceRegistration()
			throws MethodException, ConnectionException {
		if (serviceRegistration == null) {
			serviceRegistration = new ServiceRegistration("VEINS", "1.0",
					ServiceStatus.Online, getLocalSiteId());
		}
		return serviceRegistration;
	}

	public static String getSiteNumber() {
		return siteNumber;
	}

	public static boolean isPrimaryNode() {
		return isPrimaryNode;
	}

	public static void setPrimaryNode(boolean isPrimaryNode) {
		ProcessVeinsPeriodicCommandImpl.isPrimaryNode = isPrimaryNode;
	}

	public static void setServiceRegistration(
			ServiceRegistration serviceRegistration) {
		ProcessVeinsPeriodicCommandImpl.serviceRegistration = serviceRegistration;
	}

	public static void setSiteNumber(String siteNumber) {
		ProcessVeinsPeriodicCommandImpl.siteNumber = siteNumber;
	}

	private Logger logger = Logger
			.getLogger(ProcessVeinsPeriodicCommandImpl.class);

	private RoutingToken routingToken;

	public ProcessVeinsPeriodicCommandImpl() {
		super();
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException {
		InternalRouter router = InternalContext.getRouter();
		router.processServiceRegistration(getRoutingToken(),
				getServiceRegistration());
		ProcessVeinsPeriodicCommandImpl.isPrimaryNode = true;
		if (!childCommandsInitialized) {
			List<ErrorTypeNotificationConfiguration> configs = router
					.getAllErrorTypeNotificationConfiguration(getRoutingToken());
			initializeStorageErrorPeriodicCommand(getConfig(configs, "1"));
			initializeStorageErrorPeriodicCommand(getConfig(configs, "2"));
			initializeFailedArchivePeriodicCommand(getConfig(configs, "3"));
			childCommandsInitialized = true;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	private ErrorTypeNotificationConfiguration getConfig(
			List<ErrorTypeNotificationConfiguration> configs, String errorType) {
		for (ErrorTypeNotificationConfiguration config : configs) {
			if (config.getErrorType().equals(errorType)) {
				return config;
			}
		}
		return null;
	}

	@Override
	public Command<Boolean> getNewPeriodicInstance() throws MethodException {
		ProcessVeinsPeriodicCommandImpl command = new ProcessVeinsPeriodicCommandImpl();
		command.setPeriodic(true);
		command.setPeriodicExecutionDelay(this.getPeriodicExecutionDelay());
		command.setCommandContext(this.getCommandContext());
		return command;
	}

	public RoutingToken getRoutingToken() {
		if (routingToken == null) {
			try {
				routingToken = RoutingTokenImpl
						.createVARadiologySite(siteNumber);
			} catch (RoutingTokenFormatException rtfe) {
				logger.error(rtfe);
			}
		}
		return routingToken;
	}

	private void initializeStorageErrorPeriodicCommand(
			ErrorTypeNotificationConfiguration config) {
		PeriodicCommandEngineAdapter.getSingletonInstance()
				.initializePeriodicCommand(
						ProcessVeinsStorageErrorPeriodicCommandImpl.class,
						"ProcessVeinsStorageErrorPeriodicCommand",
						new Object[] {config.getErrorType()},
						ScheduledPriorityQueueElement.Priority.NORMAL,
						1000);
	}

	private void initializeFailedArchivePeriodicCommand(
			ErrorTypeNotificationConfiguration config) {
		PeriodicCommandEngineAdapter.getSingletonInstance()
				.initializePeriodicCommand(
						ProcessVeinsFailedArchivePeriodicCommandImpl.class,
						"ProcessVeinsFailedArchivePeriodicCommand",
						new Object[] {},
						ScheduledPriorityQueueElement.Priority.NORMAL,
						1000);
	}

	@Override
	protected String parameterToString() {
		return "";
	}

	public void setRoutingToken(RoutingToken routingToken) {
		this.routingToken = routingToken;
	}
}
