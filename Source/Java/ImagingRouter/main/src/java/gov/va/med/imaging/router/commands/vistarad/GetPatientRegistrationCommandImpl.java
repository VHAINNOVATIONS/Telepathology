package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class GetPatientRegistrationCommandImpl
extends AbstractDataSourceCommandImpl<PatientRegistration, VistaRadDataSourceSpi>
{
	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getNextPatientRegistration";
	
	private final RoutingToken routingToken;
	
	public GetPatientRegistrationCommandImpl(RoutingToken routingToken)
	{
		this.routingToken = routingToken;
	}	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() 
	{
		return SPI_METHOD_NAME;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected PatientRegistration getCommandResult(VistaRadDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.getNextPatientRegistration(getRoutingToken());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<VistaRadDataSourceSpi> getSpiClass() 
	{
		return VistaRadDataSourceSpi.class;
	}

	@Override
	protected String parameterToString() {
		return getSiteNumber();
	}
}