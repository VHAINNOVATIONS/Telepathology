package gov.va.med.imaging.pathology.commands.datasource;

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyPatientInfoItem;
import gov.va.med.imaging.pathology.commands.AbstractPathologyDataSourceCommandImpl;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

public class GetPathologyPatientInfoDataSourceCommandImpl 
extends AbstractPathologyDataSourceCommandImpl<List<PathologyPatientInfoItem>>
{

	private static final long serialVersionUID = -4714090684343217349L;
	
	private final PathologyCaseURN pathologyCaseUrn;
	
	public GetPathologyPatientInfoDataSourceCommandImpl(PathologyCaseURN pathologyCaseUrn)
	{
		this.pathologyCaseUrn = pathologyCaseUrn;
	}
	
	public PathologyCaseURN getPathologyCaseUrn() {
		return pathologyCaseUrn;
	}

	@Override
	public RoutingToken getRoutingToken() {
		return getPathologyCaseUrn();
	}

	@Override
	protected String getSpiMethodName() {
		return "getPatientInfo";
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{PathologyCaseURN.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[] {getPathologyCaseUrn()};
	}

	@Override
	protected List<PathologyPatientInfoItem> getCommandResult(
			PathologyDataSourceSpi spi) 
	throws ConnectionException, MethodException 
	{
		return spi.getPatientInfo(getPathologyCaseUrn());
	}

	@Override
	protected List<PathologyPatientInfoItem> postProcessResult(
		List<PathologyPatientInfoItem> result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.size());
		return result;
	}

}
