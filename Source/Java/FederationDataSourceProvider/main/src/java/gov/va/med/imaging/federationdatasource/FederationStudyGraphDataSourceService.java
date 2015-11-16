/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 3, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federation.proxy.FederationProxy;
import gov.va.med.imaging.federation.proxy.StudyResult;
import gov.va.med.imaging.federation.translator.FederationDatasourceTranslator;
import gov.va.med.imaging.proxy.exchange.StudyParameters;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;
import gov.va.med.imaging.url.federation.exceptions.FederationMethodException;
import gov.va.med.imaging.url.vftp.VftpConnection;

import java.io.IOException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationStudyGraphDataSourceService 
extends AbstractFederationStudyGraphDataSourceService
{
	
	private final VftpConnection federationConnection;
	private final static FederationDatasourceTranslator federationTranslator = new FederationDatasourceTranslator();
	private final static Logger logger = Logger.getLogger(FederationStudyGraphDataSourceService.class);
	
	// Versioning fields
	
	private final static String DATASOURCE_VERSION = "0";
	public final static String SUPPORTED_PROTOCOL = "vftp";
	
	
	private FederationProxy proxy = null;
	

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationStudyGraphDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationStudyGraphDataSourceService#getDataSourceVersion()
	 */
	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSourceSpi#getPatientStudies(java.lang.String, gov.va.med.imaging.exchange.business.StudyFilter)
	 */
	@Override
	public StudySetResult getPatientStudies(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier,
			StudyFilter filter, StudyLoadLevel studyLoadLevel) 
	throws UnsupportedOperationException, MethodException, ConnectionException 
	{
		logger.info("getPatientStudies for patient (" + patientIdentifier.toString() + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		if(patientIdentifier.getPatientIdentifierType().isLocal())
			throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
		String patientIcn = patientIdentifier.getValue();
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			logger.error("Error getting patient studies", ioX);
			throw new FederationConnectionException(ioX);
		}
		try 
		{			
			if(filter != null)
			{
				if(filter.isStudyIenSpecified())
				{
					logger.info("Filtering study by study Id [" + filter.getStudyId() + "]");
				}
			}					
			StudyParameters parameters = new StudyParameters(patientIcn, filter.getFromDate(), filter.getToDate(), filter.getStudyId());			
			StudyResult studyResult = getProxy().getStudies(parameters, getSite().getSiteNumber());
			gov.va.med.imaging.federation.webservices.types.FederationStudyType[] federationStudies = studyResult.getStudies();
			logger.info("getPatientStudies got [" + (federationStudies == null ? "0" : federationStudies.length) + "] studies from site [" + getSite().getSiteNumber() + "]");
			return StudySetResult.createFullResult(federationTranslator.transformStudies(federationStudies, filter));	
		}
		catch(IOException ioX) 
		{
			logger.error("Error getting patient studies", ioX);
			//throw new FederationMethodException(ioX);
			throw new FederationConnectionException("Error getting patient studies", ioX);
		}
		catch(ServiceException sX) {
			logger.error("Error getting patient studies", sX);
			throw new FederationMethodException(sX);
		}
	}

	private FederationProxy getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationProxy(proxyServices, FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}

}
