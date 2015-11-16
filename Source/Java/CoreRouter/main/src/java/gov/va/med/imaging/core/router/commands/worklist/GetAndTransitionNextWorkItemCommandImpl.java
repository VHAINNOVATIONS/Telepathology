package gov.va.med.imaging.core.router.commands.worklist;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

public class GetAndTransitionNextWorkItemCommandImpl 
extends AbstractWorkListDataSourceCommandImpl<WorkItem>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getAndTransitionNextWorkItem";
	
	private String type;
	private String expectedStatus;
	private String newStatus;
	private final String updatingUser; 
	private final String updatingApplication; 

	public GetAndTransitionNextWorkItemCommandImpl(String type, String expectedStatus, String newStatus, String updatingUser, String updatingApplication) {
		this.type = type;
		this.expectedStatus = expectedStatus;
		this.newStatus = newStatus;
		this.updatingUser = updatingUser;
		this.updatingApplication = updatingApplication;
		
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{String.class, String.class, String.class, String.class, String.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getType(), getExpectedStatus(), getNewStatus(), getUpdatingUser(), getUpdatingApplication(), getCommandContext().getLocalSite().getArtifactSource().getRepositoryId()} ;
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
	protected WorkItem getCommandResult(WorkListDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getAndTransitionNextWorkItem(
				getType(), 
				getExpectedStatus(), 
				getNewStatus(), 
				getUpdatingUser(), 
				getUpdatingApplication(),
				getCommandContext().getLocalSite().getArtifactSource().getRepositoryId());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	public String getType() {
		return type;
	}

	public String getExpectedStatus() {
		return expectedStatus;
	}

	public String getNewStatus() {
		return newStatus;
	}

	public String getUpdatingUser() {
		return updatingUser;
	}

	public String getUpdatingApplication() {
		return updatingApplication;
	}

	@Override
	protected WorkItem postProcessResult(WorkItem result)
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : 1);
		return result;
	}
}
