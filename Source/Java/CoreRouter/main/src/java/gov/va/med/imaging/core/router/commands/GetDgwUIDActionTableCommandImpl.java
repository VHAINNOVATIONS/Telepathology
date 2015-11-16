package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.DicomStorageDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.dicom.UIDActionConfig;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.lang.reflect.Method;
import java.util.List;

public class GetDgwUIDActionTableCommandImpl 
extends AbstractDataSourceCommandImpl<List<UIDActionConfig>, DicomStorageDataSourceSpi>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getDgwUIDActionTable";

	private final String type;
	private final String subType;
	private final String action;
	
	public GetDgwUIDActionTableCommandImpl(String type, String subType, String action)
	{
		this.type = type;
		this.subType = subType;
		this.action = action;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{String.class, String.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getType(), getSubType(), getAction()} ;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<UIDActionConfig> getCommandResult(
			DicomStorageDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getDgwUIDActionTable(getType(), getSubType(), getAction());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the subType
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return TransactionContextFactory.get().getSiteNumber();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<DicomStorageDataSourceSpi> getSpiClass() 
	{
		return DicomStorageDataSourceSpi.class;
	}

	@Override
	public RoutingToken getRoutingToken() 
	{
		RoutingToken routingToken = null;
		try 
		{
			routingToken = RoutingTokenImpl.createVARadiologySite(getLocalSiteId());
		} 
		catch (RoutingTokenFormatException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return routingToken;		
	}
}
