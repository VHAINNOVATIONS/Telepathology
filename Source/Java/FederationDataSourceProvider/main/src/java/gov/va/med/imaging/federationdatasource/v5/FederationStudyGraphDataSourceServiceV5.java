package gov.va.med.imaging.federationdatasource.v5;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.federation.proxy.v5.FederationRestStudyProxyV5;
import gov.va.med.imaging.federationdatasource.FederationStudyGraphDataSourceServiceV4;
import gov.va.med.imaging.proxy.services.ProxyServices;

public class FederationStudyGraphDataSourceServiceV5 
extends FederationStudyGraphDataSourceServiceV4 
{
	private final static String DATASOURCE_VERSION = "5";
	private FederationRestStudyProxyV5 proxy = null;

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
	public FederationStudyGraphDataSourceServiceV5(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}

	@Override
	protected FederationRestStudyProxyV5 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestStudyProxyV5(proxyServices, 
					getFederationConfiguration());
		}
		return proxy;
	}

	
}
