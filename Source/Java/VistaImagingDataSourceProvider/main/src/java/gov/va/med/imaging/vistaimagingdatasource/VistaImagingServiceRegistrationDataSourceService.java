package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.ServiceRegistrationDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.ServiceRegistration;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

public class VistaImagingServiceRegistrationDataSourceService 
extends AbstractVersionableDataSource 
implements VistaSessionFactory,ServiceRegistrationDataSourceSpi  
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation

	// TODO CHANGE TO 3.0P34
	public final static String MAG_REQUIRED_VERSION = "3.0P34";
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
	public VistaImagingServiceRegistrationDataSourceService(
			ResolvedArtifactSource resolvedArtifactSource, String protocol) {
		super(resolvedArtifactSource, protocol);
	}

	@Override
	public ServiceRegistration create(ServiceRegistration registration)
			throws MethodException, ConnectionException {
		ServiceRegistrationDAO dao = new ServiceRegistrationDAO(this);
		return dao.create(registration);
	}

	@Override
	public boolean delete(ServiceRegistration registration)
			throws MethodException, ConnectionException {
		ServiceRegistrationDAO dao = new ServiceRegistrationDAO(this);
		dao.delete(registration.getId());
		return true;
	}

	@Override
	public List<ServiceRegistration> findAll() throws MethodException,
			ConnectionException {
		ServiceRegistrationDAO dao = new ServiceRegistrationDAO(this);
		return dao.findAll();
	}

	@Override
	public ServiceRegistration getById(int id)
			throws MethodException, ConnectionException {
		ServiceRegistrationDAO dao = new ServiceRegistrationDAO(this);
		return dao.getEntityById(Integer.toString(id));
	}

	@Override
	public List<ServiceRegistration> getByServiceId(String serviceId)
			throws MethodException, ConnectionException {
		ServiceRegistrationDAO dao = new ServiceRegistrationDAO(this);
		return dao.findByServiceId(serviceId);
	}
	
	protected ResolvedSite getResolvedSite() {
		return (ResolvedSite) getResolvedArtifactSource();
	}

	protected Site getSite() {
		return getResolvedSite().getSite();
	}

	@Override
	public VistaSession getVistaSession() throws IOException,
			ConnectionException, MethodException {
		TransactionContextFactory.get().setImagingSecurityContextType(
				ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	@Override
	public boolean isVersionCompatible() throws SecurityException {
		return true;
	}

	@Override
	public ServiceRegistration update(ServiceRegistration registration)
			throws MethodException, ConnectionException {
		ServiceRegistrationDAO dao = new ServiceRegistrationDAO(this);
		return dao.update(registration);
	}
}
