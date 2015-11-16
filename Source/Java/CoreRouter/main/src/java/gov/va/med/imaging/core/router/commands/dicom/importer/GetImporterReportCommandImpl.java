package gov.va.med.imaging.core.router.commands.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.exchange.business.dicom.importer.Report;
import gov.va.med.imaging.exchange.business.dicom.importer.ReportParameters;

import java.util.List;

public class GetImporterReportCommandImpl 
extends AbstractDicomImporterDataSourceCommandImpl<Report>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getImporterReport";

	private final ReportParameters reportParameters;
	
	public GetImporterReportCommandImpl(ReportParameters reportParameters)
	{
		this.reportParameters = reportParameters;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{ReportParameters.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getReportParameters()} ;
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
	protected Report getCommandResult(DicomImporterDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getImporterReport(getReportParameters());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}

	public ReportParameters getReportParameters() {
		return reportParameters;
	}

}
