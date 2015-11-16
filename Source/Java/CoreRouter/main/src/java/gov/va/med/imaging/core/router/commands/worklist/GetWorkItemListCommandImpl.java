package gov.va.med.imaging.core.router.commands.worklist;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.WorkItemFilter;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.List;

public class GetWorkItemListCommandImpl 
extends AbstractWorkListDataSourceCommandImpl<List<WorkItem>>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getWorkItemList";

	private final WorkItemFilter workItemFilter;
	
	

	public GetWorkItemListCommandImpl(WorkItemFilter workItemFilter) {
		this.workItemFilter = workItemFilter;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{WorkItemFilter.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getWorkItemFilter()} ;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<WorkItem> getCommandResult(WorkListDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getWorkItemList(getWorkItemFilter());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public WorkItemFilter getWorkItemFilter() {
		return workItemFilter;
	}

	@Override
	protected List<WorkItem> postProcessResult(List<WorkItem> result)
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.size());
		return result;
	}
	
}
