package gov.va.med.imaging.core.router.commands.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.Study;

import java.util.List;

public class GetStudyImportStatusCommandImpl 
extends AbstractDicomImporterDataSourceCommandImpl<Study>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getStudyImportStatus";

	private final Study study;
	
	public GetStudyImportStatusCommandImpl(Study study)
	{
		this.study = study;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{Study.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getStudy()} ;
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
	protected Study getCommandResult(DicomImporterDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getStudyImportStatus(getStudy());
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
	public Study getStudy() {
		return study;
	}

}
