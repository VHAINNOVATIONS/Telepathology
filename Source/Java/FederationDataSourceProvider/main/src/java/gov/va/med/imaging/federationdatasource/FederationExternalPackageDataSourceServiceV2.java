/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 1, 2009
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
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federation.proxy.FederationProxyV2;
import gov.va.med.imaging.federation.translator.FederationDatasourceTranslatorV2;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;
import gov.va.med.imaging.url.vftp.VftpConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationExternalPackageDataSourceServiceV2 
extends AbstractFederationExternalPackageDataSourceService 
{
	
	private final VftpConnection federationConnection;
	
	private final static String DATASOURCE_VERSION = "2";
	private FederationProxyV2 proxy = null;
	public final static String SUPPORTED_PROTOCOL = "vftp";
	
	private final static FederationDatasourceTranslatorV2 federationTranslator = new FederationDatasourceTranslatorV2();

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationExternalPackageDataSourceServiceV2(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationExternalPackageDataSourceService#getDataSourceVersion()
	 */
	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.ExternalPackageDataSource#getStudyFromCprsIdentifier(java.lang.String, gov.va.med.imaging.CprsIdentifier)
	 */
	@Override
	public List<Study> getStudiesFromCprsIdentifier(RoutingToken globalRoutingToken, String patientIcn,
			CprsIdentifier cprsIdentifier) 
	throws MethodException, ConnectionException 
	{
		getLogger().info("getStudyFromCprsIdentifier for Cprs Identifier (" + cprsIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting study from CPRS identifier", ioX);
			throw new FederationConnectionException(ioX);
		}
		
		gov.va.med.imaging.federation.webservices.types.v2.FederationStudyType studyType = 
			getProxy().getStudyFromCprsIdentifier(getSite(),  patientIcn, cprsIdentifier);
		
		//getLogger().info("getPatientSensitivityLevel got value [" + (responseType == null ? "null" : responseType.getPatientSensitivityLevel().getValue()) + "] from site [" + getSite().getSiteNumber() + "]");
		Study study = federationTranslator.transformStudy(studyType, StudyLoadLevel.FULL);
		List<Study> studies = new ArrayList<Study>(1);
		studies.add(study);
		return studies;
	}
	
	private FederationProxyV2 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationProxyV2(proxyServices,
					FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}
}
