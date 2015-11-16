/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSourceImpl;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public class ExternalArtifactSourceResolverImpl
implements ExternalArtifactSourceResolver, Iterable<ResolutionResult>
{
	private final ExternalArtifactSources externalArtifactSources; 
	private final SiteProtocolPreferenceFactory protocolPreferenceFactory;
	
	/**
	 * 
	 * @param externalArtifactSources
	 * @param protocolPreferenceFactory
	 */
	public ExternalArtifactSourceResolverImpl(
		ExternalArtifactSources externalArtifactSources, 
		SiteProtocolPreferenceFactory protocolPreferenceFactory)
	{
		this.externalArtifactSources = externalArtifactSources;
		this.protocolPreferenceFactory = protocolPreferenceFactory;
	}
	
	public ExternalArtifactSources getExternalArtifactSources()
	{
		return this.externalArtifactSources;
	}

	public SiteProtocolPreferenceFactory getProtocolPreferenceFactory()
	{
		return this.protocolPreferenceFactory;
	}

	/**
	 * @see gov.va.med.siteservice.ExternalArtifactSourceResolver#iterator()
	 */
	@Override
	public Iterator<ResolutionResult> iterator()
	{
		return new Iterator<ResolutionResult>()
		{
			Iterator<RoutingToken> wrappedIterator = getExternalArtifactSources().iterator();
			
			@Override
			public boolean hasNext()
			{
				return wrappedIterator.hasNext();
			}

			@Override
			public ResolutionResult next()
			{
				return resolve(wrappedIterator.next());
			}

			@Override
			public void remove(){}
		};
	}

	/**
	 * @see gov.va.med.siteservice.ExternalArtifactSourceResolver#resolve(gov.va.med.RoutingToken)
	 */
	@Override
	public ResolutionResult resolve(RoutingToken routingToken)
	{
		ArtifactSourceLookupResult artifactSourceLookupResult = this.getExternalArtifactSources().getIncluding(routingToken);
		if(artifactSourceLookupResult == null)
		{
			Logger.getLogger(this.getClass()).debug("RoutingToken '" + routingToken.toString() + "' is not mapped to an artifact source.");
			return null;
		}
		if(artifactSourceLookupResult.isIndirected())
			return new ResolutionResult(artifactSourceLookupResult.getRoutingToken());

		ArtifactSource artifactSource = artifactSourceLookupResult.getArtifactSource();
		
		ResolvedArtifactSource resolvedSource = ResolvedArtifactSourceImpl.create(artifactSource);
		
		// Build the list of potential URLs to contact in the correct order.
		// Iterate through the preferred protocols and create URL entries for each protocol that the
		// server supports.
//		String[] preferredProtocols = getProtocolPreferenceFactory().getPreferredProtocols(artifactSource);
//		if(preferredProtocols == null || preferredProtocols.length < 1)
//		{
//			Logger.getLogger(this.getClass()).error("ArtifactSource '" + artifactSource.toString() + "' has no preferred protocols defined and defaults were not provided.");
//			return null;
//		}
//		
//		List<URL> metadataUrls = buildResolvedUrlList(preferredProtocols, artifactSource.metadataIterator());
//		List<URL> artifactUrls = buildResolvedUrlList(preferredProtocols, artifactSource.artifactIterator());
//		
//		ResolvedArtifactSource resolvedSource = ResolvedArtifactSourceImpl.create(artifactSource, metadataUrls, artifactUrls);
		return new ResolutionResult(resolvedSource);
	}

	/**
	 * 
	 * @param preferredProtocols
	 * @param serverUrlIterator
	 * @return
	 */
	private List<URL> buildResolvedUrlList(String[] preferredProtocols, Iterator<URL> serverUrlIterator)
	{
		List<URL> resolvedUrls = new ArrayList<URL>();
		for(String preferredProtocol : preferredProtocols)
		{
			while(serverUrlIterator.hasNext() )
			{
				URL url = serverUrlIterator.next();
				String protocol = url.getProtocol();
				if(preferredProtocol.equals(protocol))
					resolvedUrls.add(url);
			}
		}
		return resolvedUrls;
	}

}
