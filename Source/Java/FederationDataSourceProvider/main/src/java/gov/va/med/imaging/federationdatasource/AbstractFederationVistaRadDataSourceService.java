/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 16, 2009
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.vistarad.*;
import gov.va.med.imaging.federation.proxy.FederationProxyUtilities;
import gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationVistaRadDataSourceService
extends AbstractFederationDataSourceService
implements VistaRadDataSourceSpi
{
	public final static String SUPPORTED_PROTOCOL = "vftp";
	
	private ProxyServices federationProxyServices = null;
	private final static String FEDERATION_PROXY_SERVICE_NAME = "Federation";
	private final static Logger logger = Logger.getLogger(AbstractFederationVistaRadDataSourceService.class);	
	
	public abstract String getDataSourceVersion();
	
	/**
	 * 
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException if the ResolvedArtifactSource is not an instance of ResolvedSite
	 */
	public AbstractFederationVistaRadDataSourceService(
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
		try
		{
			getLogger().debug("Found FederationProxyServices, looking for '" + ProxyServiceType.vistaRadMetadata + "' service type at site [" + getSite().getSiteNumber() + "].");
			getFederationProxyServices().getProxyService(ProxyServiceType.vistaRadMetadata);
			getLogger().debug("Found service type '" + ProxyServiceType.vistaRadMetadata + "' at site [" + getSite().getSiteNumber() + "], returning true for version compatible.");
			return true;
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			getLogger().warn("Cannot find proxy service type '" + ProxyServiceType.vistaRadMetadata + "' at site [" + getSite().getSiteNumber() + "]");
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getActiveExams(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public ActiveExams getActiveExams(RoutingToken globalRoutingToken, String listDescriptor) throws MethodException,
		ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getActiveExams");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getExam(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public Exam getExam(StudyURN studyUrn) throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getExam");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getExamImagesForExam(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public ExamImages getExamImagesForExam(StudyURN studyUrn) throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getExamImagesForExam");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getExamReport(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public String getExamReport(StudyURN studyUrn) throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getExamReport");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getExamRequisitionReport(gov.va.med.imaging.StudyURN)
	 */
	@Override
	public String getExamRequisitionReport(StudyURN studyUrn) throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getExamRequisitionReport");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getExamsForPatient(gov.va.med.RoutingToken, java.lang.String, boolean, boolean)
	 */
	@Override
	public ExamListResult getExamsForPatient(RoutingToken globalRoutingToken, String patientICN,
		boolean fullyLoadExams, boolean forceRefresh, boolean forceImagesFromJb) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getExamsForPatient");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getNextPatientRegistration(gov.va.med.RoutingToken)
	 */
	@Override
	public PatientRegistration getNextPatientRegistration(RoutingToken globalRoutingToken) throws MethodException,
		ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getNextPatientRegistration");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#getRelevantPriorCptCodes(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public String[] getRelevantPriorCptCodes(RoutingToken globalRoutingToken, String cptCode) throws MethodException,
		ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "getRelevantPriorCptCodes");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSourceSpi#postExamAccessEvent(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public boolean postExamAccessEvent(RoutingToken globalRoutingToken, String inputParameter) throws MethodException,
		ConnectionException
	{
		throw new UnsupportedServiceMethodException(VistaRadDataSourceSpi.class, "postExamAccessEvent");
	}
}
