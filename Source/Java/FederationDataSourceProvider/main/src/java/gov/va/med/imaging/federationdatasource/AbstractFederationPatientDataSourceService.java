/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 4, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.federationdatasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.ImageStorageFacade;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.federation.proxy.FederationProxyUtilities;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.federation.storage.FederationStorageUtility;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.io.InputStream;
import java.util.List;
import java.util.SortedSet;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationPatientDataSourceService
extends AbstractFederationDataSourceService
implements PatientDataSourceSpi 
{
	private final static Logger logger = Logger.getLogger(AbstractFederationPatientDataSourceService.class);
	
	private ProxyServices federationProxyServices = null;
	private final static String FEDERATION_PROXY_SERVICE_NAME = "Federation";
	
	public abstract String getDataSourceVersion();
	private ImageStorageFacade storageFacade = null;
	
	
	protected abstract IFederationProxy getFederationProxy()
	throws ConnectionException;
	
	/**
	 * 
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException if the ResolvedArtifactSource is not an instance of ResolvedSite
	 */
	public AbstractFederationPatientDataSourceService(
		ResolvedArtifactSource resolvedArtifactSource,
		String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}

	/**
	 * The artifact source must be checked in the constructor to assure that it is an instance
	 * of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	protected Site getSite()
	{
		return getResolvedSite().getSite();
	}
	
	protected Logger getLogger()
	{
		return logger;
	}
	
	/**
	 * Returns the proxy services available, if none are available then null is returned
	 */
	protected ProxyServices getFederationProxyServices()
	{
		if(federationProxyServices == null)
		{
			federationProxyServices = 
				FederationProxyUtilities.getFederationProxyServices(getSite(), 
						getFederationProxyName(), getDataSourceVersion());
		}
		return federationProxyServices;
	}
	
	protected String getFederationProxyName()
	{
		return FEDERATION_PROXY_SERVICE_NAME;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	{
		if(getFederationProxyServices() == null)
			return false;		
		return true;
	}
	
	private ImageStorageFacade getStorageFacade()
	throws ConnectionException
	{
		if(storageFacade == null)
		{
			storageFacade = new FederationStorageUtility(getFederationProxy(), getSite());
		}
		return storageFacade;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSource#getPatientIdentificationImage(java.lang.String)
	 */
	@Override
	public InputStream getPatientIdentificationImage(PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException 
	{
		logger.info("getPatientIdentificationImage(" + patientIdentifier + ")  TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		if(patientIdentifier.getPatientIdentifierType().isLocal())
			throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
		DataSourceInputStream photoIdStream = getStorageFacade().openPhotoId(patientIdentifier.getValue(), null);
		if(photoIdStream == null)
		{
			throw new MethodException("Null sized stream returned from storage facade");
		}
		return photoIdStream.getInputStream();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSourceSpi#findPatients(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public SortedSet<Patient> findPatients(RoutingToken globalRoutingToken, String searchName) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getPatientSensitivityLevel");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSourceSpi#getPatientSensitivityLevel(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public PatientSensitiveValue getPatientSensitivityLevel(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException
	{
			throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getPatientSensitivityLevel");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSourceSpi#getTreatingSites(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public List<String> getTreatingSites(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier, boolean includeTrailingCharactersForSite200) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getPatientSensitivityLevel");
	}
	
	protected void verifyPatientIdentifierIsIcn(PatientIdentifier patientIdentifier)
	throws MethodException
	{
		if(patientIdentifier.getPatientIdentifierType() != PatientIdentifierType.icn)
			throw new MethodException("Patient identifier '" + patientIdentifier + "' is not in ICN format. Federation only supports ICNs");
	}
	
}
