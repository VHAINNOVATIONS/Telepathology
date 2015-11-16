package gov.va.med.imaging.core.router.commands.worklist;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.exchange.business.WorkItem;

public class PutWorkItemCommandImpl 
extends AbstractWorkListDataSourceCommandImpl<Boolean>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "updateWorkItem";
	
	private final int workItemId;
	private final String expectedStatus; 
	private final String newStatus; 
	private final String newMessage; 
	private final String updatingUser; 
	private final String updatingApplication; 
	
	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{int.class, String.class, String.class, String.class, String.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getWorkItemId(), getExpectedStatus(), getNewStatus(), getNewMessage(), getUpdatingApplication(), getUpdatingApplication()} ;
	}
	
	public PutWorkItemCommandImpl(int workItemId, String expectedStatus,String newStatus, String newMessage, String updatingUser, String updatingApplication) {
		this.workItemId = workItemId;
		this.expectedStatus = expectedStatus;
		this.newStatus = newStatus;
		this.newMessage = newMessage;
		this.updatingUser = updatingUser;
		this.updatingApplication = updatingApplication;
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
	protected Boolean getCommandResult(WorkListDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.updateWorkItem(getWorkItemId(), getExpectedStatus(), getNewStatus(), getNewMessage(), getUpdatingUser(), getUpdatingApplication());
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

	public int getWorkItemId() {
		return workItemId;
	}

	public String getExpectedStatus() {
		return expectedStatus;
	}
	
	public String getNewStatus() {
		return newStatus;
	}

	public String getNewMessage() {
		return newMessage;
	}

	public String getUpdatingUser() {
		return updatingUser;
	}

	public String getUpdatingApplication() {
		return updatingApplication;
	}

}
