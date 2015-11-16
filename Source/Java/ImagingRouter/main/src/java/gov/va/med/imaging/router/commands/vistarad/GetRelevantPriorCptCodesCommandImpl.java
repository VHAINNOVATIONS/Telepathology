package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

public class GetRelevantPriorCptCodesCommandImpl
extends AbstractDataSourceCommandImpl<String[], VistaRadDataSourceSpi>
{
	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getRelevantPriorCptCodes";
	private final String cptCode;
	private final RoutingToken routingToken;
	
	public String getCptCode()
	{
		return cptCode;
	}
	
	public GetRelevantPriorCptCodesCommandImpl(RoutingToken routingToken, String cptCode)
	{
		this.cptCode = cptCode;
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
		return new Object[]{getRoutingToken(), getCptCode()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class, String.class};
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
	protected String[] getCommandResult(VistaRadDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.getRelevantPriorCptCodes(getRoutingToken(), getCptCode());
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
		return getCptCode();
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected String[] postProcessResult(String[] result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.length);
		return result;
	}

	

}