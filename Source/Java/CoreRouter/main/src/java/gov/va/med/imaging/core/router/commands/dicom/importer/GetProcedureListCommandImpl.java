package gov.va.med.imaging.core.router.commands.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;

import java.util.List;

public class GetProcedureListCommandImpl 
extends AbstractDicomImporterDataSourceCommandImpl<List<Procedure>>
{

	private final String siteId;
	private final String imagingLocationIen;
	private final String procedureIen;
	
	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getProcedureList";

	public GetProcedureListCommandImpl(String siteId, String imagingLocationIen, String procedureIen)
	{
		this.siteId = siteId;
		this.imagingLocationIen = imagingLocationIen;
		this.procedureIen = procedureIen;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{String.class, String.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getSiteId(), getImagingLocationIen(), getProcedureIen()} ;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return "Site Id: " + siteId + 
				", imagingLocationIen: " + imagingLocationIen + 
				", procedureIen: " + procedureIen;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<Procedure> getCommandResult(
			DicomImporterDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getProcedureList(getSiteId(), getImagingLocationIen(), getProcedureIen());
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

	/**
	 * @return the imagingLocationIen
	 */
	public String getImagingLocationIen() {
		return imagingLocationIen;
	}

	/**
	 * @return the procedureIen
	 */
	public String getProcedureIen() {
		return procedureIen;
	}
	
	

}
