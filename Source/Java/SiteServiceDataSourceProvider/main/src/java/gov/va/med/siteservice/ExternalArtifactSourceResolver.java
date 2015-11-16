/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import java.util.Iterator;

/**
 * @author vhaiswbeckec
 *
 */
public interface ExternalArtifactSourceResolver
{
	/**
	 * Return an iterator over the entire collection of 
	 * ResolvedArtifactSource known to the realization of this 
	 * interface.
	 * 
	 * @return
	 */
	public abstract Iterator<ResolutionResult> iterator();
	
	/**
	 * Return the ResolvedArtifactSource representing the gateway or repository 
	 * that can provide data for the given RoutingToken
	 * 
	 * @param routingToken
	 * @return a ResolvedArtifactSource or null if none was found
	 */
	public abstract ResolutionResult resolve(RoutingToken routingToken);
}
