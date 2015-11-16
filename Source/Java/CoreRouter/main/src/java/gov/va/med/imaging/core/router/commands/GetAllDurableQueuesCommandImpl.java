package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;
import gov.va.med.imaging.exchange.business.DurableQueue;

import java.util.List;

public class GetAllDurableQueuesCommandImpl extends
		AbstractDataSourceCommandImpl<List<DurableQueue>, DurableQueueDataSourceSpi> {
	private static final long serialVersionUID = 8528305273375L;
	private static final String SPI_METHOD_NAME = "getAll";
	private final RoutingToken routingToken;

	public GetAllDurableQueuesCommandImpl(RoutingToken routingToken) {
		super();
		this.routingToken = routingToken;
	}

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
	protected List<DurableQueue> getCommandResult(DurableQueueDataSourceSpi spi)
			throws ConnectionException, MethodException {
		List<DurableQueue> result = spi.getAll();
		if (result == null) {
			getLogger().info("GetAllDurableQueuesCommand result : null");
		} else {
			getLogger().info(
					"GetAllDurableQueuesCommand result count: "
							+ Integer.toString(result.size()));
		}
		return result;
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
		return new Object[] { };
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[] { };
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	@Override
	protected String parameterToString() {
		return "";
	}

	@Override
	public String toString() {
		return BeanUtils.toString(this);
	}
}
