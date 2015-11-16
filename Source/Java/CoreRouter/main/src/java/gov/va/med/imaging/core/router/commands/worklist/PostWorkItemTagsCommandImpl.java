package gov.va.med.imaging.core.router.commands.worklist;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.exchange.business.WorkItemTag;

import java.util.List;

public class PostWorkItemTagsCommandImpl 
extends AbstractWorkListDataSourceCommandImpl<Boolean>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "postWorkItemTags";
	
	private final int workItemId;
	private final List<String> allowedStatuses;
	private final List<WorkItemTag> newTags;
	private final String updatingUser;
	private final String updatingApplication;

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{int.class, List.class, List.class, String.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getWorkItemId(), getAllowedStatuses(), getNewTags(), getUpdatingUser(), getUpdatingApplication()} ;
	}

	public PostWorkItemTagsCommandImpl(int workItemId,
			List<String> allowedStatuses, List<WorkItemTag> newTags, String updatingUser, String updatingApplication) {
		this.workItemId = workItemId;
		this.allowedStatuses = allowedStatuses;
		this.newTags = newTags;
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
		return spi.postWorkItemTags(getWorkItemId(), getAllowedStatuses(), getNewTags(), getUpdatingUser(), getUpdatingApplication());
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

	public List<WorkItemTag> getNewTags() {
		return newTags;
	}

	public List<String> getAllowedStatuses() {
		return allowedStatuses;
	}

	public String getUpdatingUser() {
		return updatingUser;
	}

	public String getUpdatingApplication() {
		return updatingApplication;
	}

	
}
