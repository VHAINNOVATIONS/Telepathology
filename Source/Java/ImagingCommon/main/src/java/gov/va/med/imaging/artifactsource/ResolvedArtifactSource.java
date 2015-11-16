/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * A ResolvedArtifactSource is an ArtifactSource that has been resolved to a 
 * set of URLs, in the preferred order of contact.
 * The Router will use the URLs protocol to determine the service implementation
 * to use to contact the site.  Where multiple URLs are specified (i.e. multiple
 * protocols are available) the Router will try each in order until it succeeds or
 * runs out of options.
 * 
 * Resolving an ArtifactSource to a set of URLs is the responsibility of a
 * SiteResolutionDataSource, each of which may implement their own
 * ResolvedArtifactSource class.
 * 
 * @author vhaiswbeckec
 *
 */
public interface ResolvedArtifactSource
{
	/**
	 * A local artifact sources is one that is on the local LAN and which the
	 * router would select data sources that are not optimized for reduced
	 * network usage.
	 * 
	 * @return
	 */
	public abstract boolean isLocal();
	
	/**
	 * An alien site is one that is outside the enterprise.  Note that this
	 * is related to but is not exactly the same as comparing the home community
	 * identifier.
	 * 
	 * @return
	 */
	public abstract boolean isAlien();
	
	/**
	 * The set of URLs in the preferred order, by protocol,
	 * of contact for artifacts.
	 * 
	 * @return
	 */
	public abstract List<URL> getArtifactUrls();
	
	/**
	 * Get the first artifact URL using the specified protocol.
	 * 
	 * @param protocol
	 * @return
	 */
	public abstract URL getArtifactUrl(String protocol);
	
	/**
	 * The set of URLs in the preferred order, by protocol,
	 * of contact for metadata.
	 * 
	 * @return
	 */
	public abstract List<URL> getMetadataUrls();
	
	/**
	 * Get the first metadata URL using the specified protocol.
	 * 
	 * @param protocol
	 * @return
	 */
	public abstract URL getMetadataUrl(String protocol);
	
	/**
	 * A reference to the ArtifactSource that was used as the source for
	 * building the interface URLs.
	 * @return
	 */
	public abstract ArtifactSource getArtifactSource();
	
	/**
	 * A site might be disabled by site resolution meaning no data should be loaded from that site.
	 * @return
	 */
	public abstract boolean isEnabled();
}
