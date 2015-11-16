package gov.va.med.imaging.core.router.commands.worklist;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.exchange.business.WorkItem;

public class PostWorkItemCommandImpl 
extends AbstractWorkListDataSourceCommandImpl<WorkItem>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "createWorkItem";
	
	private final WorkItem workItem;
	
	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{WorkItem.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getWorkItem()} ;
	}

	public PostWorkItemCommandImpl(WorkItem workItem) {
		this.workItem = workItem;
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
		return spi.createWorkItem(getWorkItem());
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

	public WorkItem getWorkItem() {
		return workItem;
	}

}
