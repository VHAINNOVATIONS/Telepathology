package gov.va.med.imaging.federationdatasource;

import java.io.IOException;
import java.util.SortedSet;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federation.proxy.v4.FederationRestStudyProxyV4;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.federation.exceptions.FederationConnectionException;
import gov.va.med.imaging.url.vftp.VftpConnection;

public class FederationStudyGraphDataSourceServiceV4 
extends AbstractFederationStudyGraphDataSourceService 
{
	private final VftpConnection federationConnection;
	
	private final static String DATASOURCE_VERSION = "4";
	private FederationRestStudyProxyV4 proxy = null;
	public final static String SUPPORTED_PROTOCOL = "vftp";

	@Override
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationStudyGraphDataSourceServiceV4(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());

		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}

	protected FederationRestStudyProxyV4 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestStudyProxyV4(proxyServices, 
					FederationDataSourceProvider.getFederationConfiguration());
		}
		return proxy;
	}

	@Override
	public StudySetResult getPatientStudies(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier,
			StudyFilter filter, StudyLoadLevel studyLoadLevel)
	throws MethodException, ConnectionException 
	{
		getLogger().info("getPatientStudies for patient (" + patientIdentifier + 
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
			getLogger().error("Error getting patient studies", ioX);
			throw new FederationConnectionException(ioX);
		}					
		if(filter != null)
		{
			if(filter.isStudyIenSpecified())
			{
				getLogger().info("Filtering study by study Id [" + filter.getStudyId() + "]");
			}
		}
		StudySetResult result = getProxy().getStudies(patientIcn, 
				filter, globalRoutingToken, studyLoadLevel);
		SortedSet<Study> studies = result.getArtifacts();
		getLogger().info("getPatientStudies got [" + (studies == null ? "0" : studies.size()) + "] studies from site [" + getSite().getSiteNumber() + "]");			
		return result;		
	}

	@Override
	public boolean isVersionCompatible() 
	throws SecurityException 
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
	
}
