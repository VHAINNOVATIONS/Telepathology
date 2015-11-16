/**
 * 
 */
package gov.va.med;


/**
 * This interface is implemented by a number of resource identifiers and provides
 * the community, repository and document IDs as discrete components.
 * The fact that this interface extends RoutingToken implies that any class
 * implementing this interface is a route-able identifier.
 * 
 * GlobalArtifactIdentifier realizations should also implement the following static
 * methods:
 * public static GlobalArtifactIdentifier createFromGlobalArtifactIdentifiers(String, String, String);
 * public static boolean isApplicableHomeCommunityId();
 * 
 * 
 * @author vhaiswbeckec
 *
 */
public interface GlobalArtifactIdentifier
extends Comparable<GlobalArtifactIdentifier>, RoutingToken, Cloneable
{
	/**
	 * 
	 * @return
	 */
	public abstract String getDocumentUniqueId();
	
	/**
	 * 
	 * @param that
	 * @return
	 */
	public abstract boolean equalsGlobalArtifactIdentifier(GlobalArtifactIdentifier that);
	
	/**
	 * 
	 * @return
	 */
	public abstract GlobalArtifactIdentifier clone()
	throws CloneNotSupportedException;
	
	/**
	 * @return
	 */
	public abstract String toString(SERIALIZATION_FORMAT serializationFormat);
}
