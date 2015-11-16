package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.core.router.facade.InternalRouter;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;

import java.util.List;

import org.apache.log4j.Logger;

@RouterCommandExecution(asynchronous = true, distributable = false)
public class PostMoveAllDurableQueueMessagesCommandImpl extends
		AbstractCommandImpl<Boolean> {

	private static final long serialVersionUID = 9472325650382L;

	private Logger logger = Logger
			.getLogger(PostMoveAllDurableQueueMessagesCommandImpl.class);
	private final int sourceQueueId;
	private final int targetQueueId;
	private final String messageGroupId;
	private final RoutingToken routingToken;

	public PostMoveAllDurableQueueMessagesCommandImpl(
			RoutingToken routingToken, int sourceQueueId, int targetQueueId) {
		super();
		this.routingToken = routingToken;
		this.sourceQueueId = sourceQueueId;
		this.targetQueueId = targetQueueId;
		this.messageGroupId = null;
	}

	public PostMoveAllDurableQueueMessagesCommandImpl(
			RoutingToken routingToken, int sourceQueueId, int targetQueueId,
			String messageGroupId) {
		super();
		this.routingToken = routingToken;
		this.sourceQueueId = sourceQueueId;
		this.targetQueueId = targetQueueId;
		this.messageGroupId = messageGroupId;
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException {
		InternalRouter internalRouter = InternalContext.getRouter();
		List<DurableQueueMessage> messages = internalRouter
				.getDurableQueueMessages(routingToken, sourceQueueId,
						messageGroupId, 0, 0);
		for (DurableQueueMessage message : messages) {
			internalRouter.postMoveDurableQueueMessage(routingToken, message
					.getId(), targetQueueId);
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	public RoutingToken getRoutingToken() {
		return routingToken;
	}

	@Override
	protected String parameterToString() {
		return Integer.toString(sourceQueueId) + ", "
				+ Integer.toString(targetQueueId);
	}
}
