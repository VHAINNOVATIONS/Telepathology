/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;
import java.io.Serializable;
import java.util.Iterator;

/**
 * @author vhaiswbeckec
 *
 */
public interface ExternalArtifactSources
extends Iterable<RoutingToken>, Serializable
{
	/**
	 * Add a routing map from a routing token to an artifact source.
	 * Note that to resolve the RoutingToken to a ResolvedArtifactSource 
	 * (i.e. so the resolve() call works) still requires that the 
	 * ArtifactSource be mapped to one or more InterfaceURLs.
	 * 
	 * @param routingToken
	 * @param artifactSource
	 */
	public abstract void add(RoutingToken routingToken, ArtifactSourceImpl artifactSource);

	/**
	 * @param routingToken
	 * @param indirectionRoutingToken
	 */
	void addIndirection(RoutingToken routingToken, RoutingTokenImpl indirectionRoutingToken);
	
	/**
	 * Delete the mappings from the RoutingTokens to the artifact source 
	 * and from the ArtifactSource to all InterfaceURLs associated to it.
	 * 
	 * @param routingToken
	 */
	public abstract void remove(RoutingToken routingToken);
	
	public abstract void remove(String homeCommunityId, String repositoryId);
	
	/**
	 * 
	 * @param routingToken
	 * @return
	 */
	public abstract ArtifactSourceLookupResult getEquivalent(RoutingToken routingToken);

	/**
	 * 
	 * @param routingToken
	 * @return
	 */
	public abstract ArtifactSourceLookupResult getIncluding(RoutingToken routingToken);
	
	/**
	 * 
	 * @return
	 */
	public abstract Iterator<RoutingToken> iterator();

	/**
	 * 
	 */
	public abstract void clear();

}
