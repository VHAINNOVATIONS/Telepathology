package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.facade.InternalRouter;
import gov.va.med.imaging.exchange.business.ServiceRegistration;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;

import java.util.List;

import org.apache.log4j.Logger;

@RouterCommandExecution(asynchronous = true, distributable = false)
public class ProcessServiceRegistrationCommandImpl extends
		AbstractCommandImpl<Boolean> {

	private static final long serialVersionUID = 5283949501748L;

	private Logger logger = Logger
			.getLogger(ProcessServiceRegistrationCommandImpl.class);
	private RoutingToken routingToken;
	private final ServiceRegistration registration;

	public ProcessServiceRegistrationCommandImpl(RoutingToken routingToken,
			ServiceRegistration registration) {
		super();
		this.registration = registration;
		this.routingToken = routingToken;
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException {
		InternalRouter internalRouter = InternalContext.getRouter();
		ServiceRegistration node;
		if (registration.getId() == 0) {
			// don't have the id of the record to update. Check if a record
			// exists.
			node = findMatchingNode(internalRouter);
			if (node == null) {
				// nothing exists yet, so create a new node
				internalRouter.postServiceRegistration(routingToken,
						registration);
				return true;
			} else {
				registration.setId(node.getId());
			}
		}
		logger.info("Updating service registration for " + registration.getServiceId());
		internalRouter.putServiceRegistration(routingToken, registration);
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	private ServiceRegistration findMatchingNode(InternalRouter internalRouter)
			throws MethodException, ConnectionException {
		List<ServiceRegistration> nodes = internalRouter
				.getServiceRegistrationByServiceId(routingToken, registration
						.getServiceId());
		if (nodes != null) {
			for (ServiceRegistration node : nodes) {
				if (node.equals(registration)) {
					return node;
				}
			}
		}
		return null;
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
}
