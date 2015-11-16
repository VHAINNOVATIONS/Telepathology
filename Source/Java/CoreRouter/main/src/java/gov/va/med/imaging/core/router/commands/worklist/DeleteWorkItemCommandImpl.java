package gov.va.med.imaging.core.router.commands.worklist;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;

public class DeleteWorkItemCommandImpl 
extends AbstractWorkListDataSourceCommandImpl<Boolean>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "deleteWorkItem";
	
	private int id;
	
	public DeleteWorkItemCommandImpl(int id) {
		this.id = id;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{int.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getId()} ;
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
		return spi.deleteWorkItem(getId());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	public int getId() {
		return id;
	}

}
