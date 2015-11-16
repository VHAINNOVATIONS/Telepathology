package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BusinessKey;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;

/**
 * A Command implementation for putting messages on the persistent VistA queue.
 * The result of successful processing is a void, represented by the "Object"
 * class used as the generic type. The result will always be a null.
 * 
 * @author vhaiswgraver
 */
public class EnqueueDurableQueueMessageCommandImpl
		extends
		AbstractDataSourceCommandImpl<DurableQueueMessage, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 5246856204953684513L;
	private static final String SPI_METHOD_NAME = "enqueue";
	private final DurableQueueMessage message;
	private final RoutingToken routingToken;

	public EnqueueDurableQueueMessageCommandImpl(RoutingToken routingToken,
			DurableQueueMessage message) {
		super();
		this.message = message;
		this.routingToken = routingToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return BeanUtils.equals(this, obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected DurableQueueMessage getCommandResult(
			DurableQueueDataSourceSpi spi) throws ConnectionException,
			MethodException {
		DurableQueueMessage result = spi.enqueue(message);
		getLogger()
				.info("Enqueued message " + Integer.toString(result.getId()));
		return result;
	}

	@BusinessKey
	public DurableQueueMessage getMessage() {
		return message;
	}

	public RoutingToken getRoutingToken() {
		return routingToken;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() {
		return getRoutingToken().getRepositoryUniqueId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<DurableQueueDataSourceSpi> getSpiClass() {
		return DurableQueueDataSourceSpi.class;
	}

	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[] { getMessage() };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { DurableQueueMessage.class };
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() {
		return this.getMessage().toString();
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}
}
