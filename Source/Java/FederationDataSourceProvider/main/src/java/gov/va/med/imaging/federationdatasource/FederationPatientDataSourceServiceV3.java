/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 22, 2009
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

import gov.va.med.HealthSummaryURN;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.UnsupportedServiceMethodException;
import gov.va.med.imaging.exchange.business.HealthSummaryType;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.federation.proxy.FederationProxyV3;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;
import gov.va.med.imaging.url.vftp.VftpConnection;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationPatientDataSourceServiceV3 
extends AbstractFederationPatientDataSourceService 
{
	private final VftpConnection federationConnection;
	
	private final static String DATASOURCE_VERSION = "3";
	private FederationProxyV3 proxy = null;
	public final static String SUPPORTED_PROTOCOL = "vftp";

	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationPatientDataSourceServiceV3(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationPatientDataSourceService#getDataSourceVersion()
	 */
	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federationdatasource.AbstractFederationPatientDataSourceService#getFederationProxy()
	 */
	@Override
	protected IFederationProxy getFederationProxy() 
	throws ConnectionException 
	{
		return getProxy();
	}

	private FederationProxyV3 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();			
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationProxyV3(proxyServices, FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSource#findPatients(java.lang.String)
	 */
	@Override
	public SortedSet<Patient> findPatients(RoutingToken globalRoutingToken, String searchName)
	throws MethodException, ConnectionException 
	{
		// JMW 2/6/2012 - p122 - added sensitive status to result which is not part of v4 FederationPatient interface
		// make this method not used so the data source provider will fall back to using a non-Federation data source
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "findPatients");
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSource#getPatientSensitivityLevel(java.lang.String)
	 */
	@Override
	public PatientSensitiveValue getPatientSensitivityLevel(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException 
	{
		getLogger().info("getPatientSensitivityLevel for patient Icn (" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		verifyPatientIdentifierIsIcn(patientIdentifier);
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error finding patient sensitivity level", ioX);
			throw new FederationConnectionException(ioX);
		}
		return getProxy().getPatientSensitiveValue(getSite(), patientIdentifier.getValue());		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PatientDataSource#getTreatingSites(java.lang.String)
	 */
	@Override
	public List<String> getTreatingSites(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier,
			boolean includeTrailingCharactersForSite200)
	throws MethodException, ConnectionException 
	{
		getLogger().info("getTreatingSites for patient (" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		if(patientIdentifier.getPatientIdentifierType().isLocal())
			throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
		try 
		{
			federationConnection.connect();
		}
		catch(IOException ioX)
		{
			getLogger().error("Error finding treating sites", ioX);
			throw new FederationConnectionException(ioX);
		}
		return getProxy().getPatientSitesVisited(getSite(), patientIdentifier.getValue());
	}
	
	@Override
	public boolean logPatientSensitiveAccess(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "logPatientSensitiveAccess");
	}
	
	@Override
	public Patient getPatientInformation(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getPatientInformation");
	}
	
	@Override
	public PatientMeansTestResult getPatientMeansTest(
			RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getPatientMeansTest");
	}
	
	@Override
	public List<HealthSummaryType> getHealthSummaryTypes(
			RoutingToken globalRoutingToken) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getHealthSummaryTypes");
	}

	@Override
	public String getHealthSummary(HealthSummaryURN healthSummaryUrn,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		throw new UnsupportedServiceMethodException(PatientDataSourceSpi.class, "getHealthSummary");
	}
}
