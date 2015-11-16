/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 15, 2010
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
package gov.va.med.imaging.federationdatasource.v5;

import java.io.IOException;
import java.util.SortedSet;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientMeansTestResult;
import gov.va.med.imaging.federation.proxy.IFederationProxy;
import gov.va.med.imaging.federation.proxy.v5.FederationRestPatientProxyV5;
import gov.va.med.imaging.federationdatasource.FederationPatientDataSourceServiceV4;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationPatientDataSourceServiceV5 
extends FederationPatientDataSourceServiceV4 
{
	private final static String DATASOURCE_VERSION = "5";
	private FederationRestPatientProxyV5 proxy = null;

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationPatientDataSourceServiceV5(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}

	@Override
	protected FederationRestPatientProxyV5 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestPatientProxyV5(proxyServices, 
					getFederationConfiguration());
		}
		return proxy;
	}

	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}

	@Override
	protected IFederationProxy getFederationProxy() 
	throws ConnectionException 
	{
		return getProxy();
	}

	@Override
	public Patient getPatientInformation(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException
	{
		getLogger().info("getPatientInformation for patient (" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try 
		{
			federationConnection.connect();
		}
		catch(IOException ioX)
		{
			getLogger().error("Error finding patient information", ioX);
			throw new FederationConnectionException(ioX);
		}
		if(patientIdentifier.getPatientIdentifierType().isLocal())
			throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
		return getProxy().getPatientInformation(globalRoutingToken, patientIdentifier.getValue());
	}

	@Override
	public PatientMeansTestResult getPatientMeansTest(
			RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException
	{
		getLogger().info("getPatientMeansTest for patient Icn (" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try 
		{
			federationConnection.connect();
		}
		catch(IOException ioX)
		{
			getLogger().error("Error finding patient means test", ioX);
			throw new FederationConnectionException(ioX);
		}
		if(patientIdentifier.getPatientIdentifierType().isLocal())
			throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
		return getProxy().getPatientMeansTest(globalRoutingToken, patientIdentifier.getValue());
	}
	
	@Override
	public SortedSet<Patient> findPatients(RoutingToken globalRoutingToken, String searchName)
	throws MethodException, ConnectionException 
	{
		// JMW v5 of Federation should include the patient sensitive value in the result, should have been enabled in P122 but was forgotten
		getLogger().info("findPatients searching for patient (" + searchName + ") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try 
		{
			federationConnection.connect();
		}
		catch(IOException ioX)
		{
			getLogger().error("Error searching for patient", ioX);
			throw new FederationConnectionException(ioX);
		}
		return getProxy().findPatients(globalRoutingToken, searchName);
	}
}
