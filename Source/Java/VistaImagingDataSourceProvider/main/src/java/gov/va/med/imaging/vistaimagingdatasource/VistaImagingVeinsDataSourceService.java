package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.VeinsDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

public class VistaImagingVeinsDataSourceService 
extends AbstractVersionableDataSource
implements VistaSessionFactory, VeinsDataSourceSpi 
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation

	@Override
	public List<ErrorTypeNotificationConfiguration> findAllErrorTypeNotificationConfiguration()
			throws MethodException, ConnectionException {
		ErrorTypeNotificationConfigurationDAO dao = new ErrorTypeNotificationConfigurationDAO(
				this);
		return dao.findAll();
	}

	@Override
	public void updateErrorTypeNotificationConfiguration(
			ErrorTypeNotificationConfiguration config) throws MethodException,
			ConnectionException {
		ErrorTypeNotificationConfigurationDAO dao = new ErrorTypeNotificationConfigurationDAO(
				this);
		dao.update(config);
	}

	@Override
	public ErrorTypeNotificationConfiguration getErrorTypeNotificationConfiguration(
			String errorType) throws MethodException, ConnectionException {
		ErrorTypeNotificationConfigurationDAO dao = new ErrorTypeNotificationConfigurationDAO(
				this);
		return dao.getByErrorType(errorType);
	}

	// TODO CHANGE TO 3.0P34
	public final static String MAG_REQUIRED_VERSION = "3.0P83";
	public final static int DEFAULT_PATIENT_SENSITIVITY_LEVEL = 2;
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";

	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingVeinsDataSourceService(
			ResolvedArtifactSource resolvedArtifactSource, String protocol) {
		super(resolvedArtifactSource, protocol);
	}

	@Override
	public boolean isVersionCompatible() throws SecurityException {
		return true;
	}

	protected ResolvedSite getResolvedSite() {
		return (ResolvedSite) getResolvedArtifactSource();
	}

	protected Site getSite() {
		return getResolvedSite().getSite();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.vistaimagingdatasource.dicom.storage.SessionFactory
	 *      #getVistaSession()
	 */
	@Override
	public VistaSession getVistaSession() throws IOException,
			ConnectionException, MethodException {
		TransactionContextFactory.get().setImagingSecurityContextType(
				ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}
}
