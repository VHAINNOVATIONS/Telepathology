/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import java.io.Serializable;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * A generic representation of any source of image or document data.
 * This could be a repository of data or it could be a gateway, through
 * which repositories are made available.
 * A gateway has the repository ID specified as the wildcard character,
 * a repository has a "real" repository identifier.
 * An artifact source has one or more servers associated to a
 * protocol.  Each protocol may have two servers, one for metadata
 * and another for artifact data.
 * 
 * @author vhaiswbeckec
 *
 */
public interface ArtifactSource
extends Serializable
{
	/**
	 * Return a durable and unique identifier of the ArtifactSource.
	 * By convention the identifier of the ArtifactSource is the class name
	 * concatenated to a string name that is unique within the instances
	 * of the class.
	 * Each class is responsible for providing a unique name within its 
	 * instances.
	 * The Identifier property will be used to determine equality.
	 * 
	 * @return
	 */
	public abstract String getIdentifier();
	
	/**
	 * The name fields is required to be unique within the domain of the
	 * class type.  This accessor must return the part of the identifier
	 * that is unique within the class type.
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @return
	 */
	public abstract OID getHomeCommunityId();
	
	/**
	 * 
	 * @return
	 */
	public abstract String getRepositoryId();
	
	/**
	 * Return true if this ArtifactRepository instance represents the repository
	 * given in the homeCommunityId and the repositoryId
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @return
	 */
	public boolean isRepresents(OID homeCommunityId, String repositoryId);

	/**
	 * A set of convenience methods that look at the home community ID
	 * for one of a known set of values.  The enumeration WellKnownOID is
	 * used to determine the ownership of the home community ID.
	 * @return
	 */
	public boolean isVaRadiology();
	public boolean isVaDocument();
	public boolean isDodRadiology();
	public boolean isDodDocument();
	
	
	/**
	 * @param protocol
	 * @return
	 */
	public abstract URL getAvailableMetadataServer(String protocol);
	
	/**
	 * @param protocol
	 * @return
	 */
	public abstract URL getAvailableArtifactServer(String protocol);
	
	/**
	 * An iterator over all metadata URLs for this artifact source.
	 * 
	 * @return
	 */
	public abstract Iterator<URL> metadataIterator();
	
	public abstract int getMetadataServerCount();
	
	/**
	 * An iterator over all artifact URLs for this artifact source.
	 * 
	 * @return
	 */
	public abstract Iterator<URL> artifactIterator();
	
	public abstract int getArtifactServerCount();
	
	/**
	 * 
	 * @return
	 */
	public abstract ArtifactSourceMemento getMemento();
	
	/**
	 * Create a RoutingToken that will direct a command to this
	 * artifact source.
	 * 
	 * @return
	 */
	public RoutingToken createRoutingToken();

	/**
	 * Get a List of the metadata URLs, as known to the ArtifactSource
	 * @return
	 */
	public abstract List<URL> getMetadataUrls();

	/**
	 * Get a List of the artifact URLs, as known to the ArtifactSource
	 * @return
	 */
	public abstract List<URL> getArtifactUrls();
}
