package gov.va.med.imaging.core.router.commands.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingProvider;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;

import java.util.List;

public class GetOrderingProviderListCommandImpl 
extends AbstractDicomImporterDataSourceCommandImpl<List<OrderingProvider>>
{
	private final String siteId;
	private final String searchString;
	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getOrderingProviderList";

	public GetOrderingProviderListCommandImpl(String siteId, String searchString)
	{
		this.siteId = siteId;
		this.searchString = searchString;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{String.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getSiteId(), getSearchString()} ;
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
	protected List<OrderingProvider> getCommandResult(
			DicomImporterDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getOrderingProviderList(getSiteId(), getSearchString());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}
	
	public String getSiteId() {
		return siteId;
	}

	public String getSearchString() {
		return searchString;
	}
	

}
