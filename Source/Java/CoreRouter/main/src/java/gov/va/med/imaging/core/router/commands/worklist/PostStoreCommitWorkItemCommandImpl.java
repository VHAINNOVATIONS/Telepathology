package gov.va.med.imaging.core.router.commands.worklist;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.WorkListDataSourceSpi;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.dicom.StorageCommitWorkItem;

public class PostStoreCommitWorkItemCommandImpl 
extends AbstractWorkListDataSourceCommandImpl<StorageCommitWorkItem>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "createSCWorkItem";
	
	private final StorageCommitWorkItem sCWI;
	
	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{StorageCommitWorkItem.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getWorkItem()} ;
	}

	public PostStoreCommitWorkItemCommandImpl(StorageCommitWorkItem scWI) {
		this.sCWI = scWI;
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
	protected StorageCommitWorkItem getCommandResult(WorkListDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.createSCWorkItem(getWorkItem());
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

	public StorageCommitWorkItem getWorkItem() {
		return sCWI;
	}

}
