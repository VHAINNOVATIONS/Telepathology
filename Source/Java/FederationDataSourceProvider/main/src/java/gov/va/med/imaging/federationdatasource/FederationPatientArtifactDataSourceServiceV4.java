/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2010
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
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.PatientArtifactDataSourceSpi;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federation.proxy.FederationProxyUtilities;
import gov.va.med.imaging.federation.proxy.v4.FederationRestPatientArtifactProxyV4;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;
import gov.va.med.imaging.url.vftp.VftpConnection;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationPatientArtifactDataSourceServiceV4
extends AbstractFederationDataSourceService
implements PatientArtifactDataSourceSpi
{
	
	private final VftpConnection federationConnection;
	private ProxyServices federationProxyServices = null;
	
	private final static String DATASOURCE_VERSION = "4";
	private FederationRestPatientArtifactProxyV4 proxy = null;
	public final static String SUPPORTED_PROTOCOL = "vftp";
	private final static String FEDERATION_PROXY_SERVICE_NAME = "Federation";
	
	private final static Logger logger = Logger.getLogger(FederationPatientArtifactDataSourceServiceV4.class);

	public FederationPatientArtifactDataSourceServiceV4(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());

		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	protected Logger getLogger()
	{
		return logger;
	}

	@Override
	public ArtifactResults getPatientArtifacts(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier,
			StudyFilter studyFilter, StudyLoadLevel studyLoadLevel,
			boolean includeImages, boolean includeDocuments)
	throws MethodException, ConnectionException
	{
		getLogger().info("getPatientArtifacts for patient (" + patientIdentifier + 
				"), StudyLoadLevel (" + studyLoadLevel + "), TransactionContext (" + 				
				TransactionContextFactory.get().getDisplayIdentity() + ").");
		if(patientIdentifier.getPatientIdentifierType().isLocal())
			throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
		String patientIcn = patientIdentifier.getValue();
		try 
		{
			federationConnection.connect();			
		}
		catch(IOException ioX) 
		{
			getLogger().error("Error getting patient artifacts", ioX);
			throw new FederationConnectionException(ioX);
		}					
		if(studyFilter != null)
		{
			if(studyFilter.isStudyIenSpecified())
			{
				getLogger().info("Filtering study by study Id [" + studyFilter.getStudyId() + "]");
			}
		}
		ArtifactResults result = getProxy().getPatientArtifacts(patientIcn, studyFilter, globalRoutingToken, 
				studyLoadLevel, includeImages, includeDocuments);
		getLogger().info("getPatientArtifacts got [" + (result == null ? "0" : result.getArtifactSize()) + "] artifacts from site [" + getSite().getSiteNumber() + "]");			
		return result;		
	}

	@Override
	public boolean isVersionCompatible() throws SecurityException
	{
		if(getFederationProxyServices() == null)
			return false;		
		try
		{
			getLogger().debug("Found FederationProxyServices, looking for '" + ProxyServiceType.metadata + "' service type at site [" + getSite().getSiteNumber() + "].");
			getFederationProxyServices().getProxyService(ProxyServiceType.metadata);
			getLogger().debug("Found service type '" + ProxyServiceType.metadata + "' at site [" + getSite().getSiteNumber() + "], returning true for version compatible.");
			return true;
		}
		catch(gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException psnfX)
		{
			getLogger().warn("Cannot find proxy service type '" + ProxyServiceType.metadata + "' at site [" + getSite().getSiteNumber() + "]");
			return false;
		}
	}
	
	protected FederationRestPatientArtifactProxyV4 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestPatientArtifactProxyV4(proxyServices, 
					FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}
	
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
	
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
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
	
}
