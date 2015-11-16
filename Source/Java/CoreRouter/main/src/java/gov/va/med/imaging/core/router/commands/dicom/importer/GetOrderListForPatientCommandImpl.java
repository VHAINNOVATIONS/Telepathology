package gov.va.med.imaging.core.router.commands.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderFilter;

import java.util.List;

public class GetOrderListForPatientCommandImpl 
extends AbstractDicomImporterDataSourceCommandImpl<List<Order>>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getOrderListForPatient";

	private final OrderFilter orderFilter;
	
	public GetOrderListForPatientCommandImpl(OrderFilter orderFilter)
	{
		this.orderFilter = orderFilter;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{OrderFilter.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getOrderFilter()} ;
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
	protected List<Order> getCommandResult(
			DicomImporterDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getOrderListForPatient(getOrderFilter());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	/**
	 * @return the patientId
	 */
	public OrderFilter getOrderFilter() {
		return orderFilter;
	}

}
