/**
 * 
 */
package gov.va.med;

import java.io.Serializable;

/**
 * This interface is required for any request that will be routed in the VISA
 * architecture.  Object identifiers may implement this interface to acquire
 * the capability to be routed or an implementation of this class may be otherwise
 * attached to a request to provide the information needed for routing.
 * 
 * 
 * @author vhaiswbeckec
 *
 */
public interface RoutingToken
extends Serializable
{
	public final static String ROUTING_TOKEN_REGEX = "([0-9]?)(\\.[0-9]+)*,([\\*]|([^*]+))";
	
	public final static String ROUTING_WILDCARD = "*";
	
	/**
	 * Most commonly an OID but may be any string.  The character '*' is reserved
	 * as a wildcard character.
	 * 
	 * @return
	 */
	public abstract String getHomeCommunityId();

	/**
	 * May be any string.  The character '*' is reserved as a wildcard character.
	 * 
	 * @return
	 */
	public abstract String getRepositoryUniqueId();

	/**
	 * Must return true if the home community ID and the repository ID specify
	 * the same destination.
	 * 
	 * @param that
	 * @return
	 */
	public abstract boolean isEquivalent(RoutingToken that);
	
	/**
	 * Must return true if the given RoutingToken is included in the
	 * this RoutingToken.  Includes means either isEquivalent() returns true
	 * or this RoutingToken specifies a wildcard that includes the given
	 * RoutingToken.
	 * 
	 * @param that
	 * @return
	 */
	public abstract boolean isIncluding(RoutingToken that);

	/**
	 * Require that RoutingToken realizations implement equals(), this really won't 
	 * do that but it is a reminder that it should be done.
	 * 
	 * @param that
	 * @return
	 */
	public abstract boolean equals(Object that);
	
	/**
	 * Return a string representation of the routing token in comma
	 * separated format (i.e. 1.2.3.4.5.6,660)
	 * This is the REQUIRED format!
	 * 
	 * @return
	 */
	public abstract String toRoutingTokenString();
}
