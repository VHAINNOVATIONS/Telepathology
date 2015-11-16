/**
 * 
 */
package gov.va.med;

import java.io.Serializable;

/**
 * A simple serializable value object that can be used to persist
 * a RoutingToken
 * @author vhaiswbeckec
 *
 */
public class RoutingTokenMemento
implements Serializable, Comparable<RoutingTokenMemento>
{
	private static final long serialVersionUID = 1L;
	
	private String routingTokenClassName;
	private String homeCommunityId;
	private String repositoryUniqueId;
	
	/**
	 * 
	 */
	public RoutingTokenMemento()
	{
		super();
	}
	
	/**
	 * @param homeCommunityId
	 * @param repositoryUniqueId
	 */
	public RoutingTokenMemento(RoutingToken routingToken)
	{
		super();
		this.routingTokenClassName = routingToken.getClass().getName();
		this.homeCommunityId = routingToken.getHomeCommunityId();
		this.repositoryUniqueId = routingToken.getRepositoryUniqueId();
	}
	
	public String getRoutingTokenClassName()
	{
		return this.routingTokenClassName;
	}

	public void setRoutingTokenClassName(String routingTokenClassName)
	{
		this.routingTokenClassName = routingTokenClassName;
	}

	public String getHomeCommunityId()
	{
		return this.homeCommunityId;
	}
	public String getRepositoryUniqueId()
	{
		return this.repositoryUniqueId;
	}
	public void setHomeCommunityId(String homeCommunityId)
	{
		this.homeCommunityId = homeCommunityId;
	}
	public void setRepositoryUniqueId(String repositoryUniqueId)
	{
		this.repositoryUniqueId = repositoryUniqueId;
	}

	@Override
	public int compareTo(RoutingTokenMemento that)
	{
		if( RoutingToken.ROUTING_WILDCARD.equals(this.getHomeCommunityId()) )
			return 1;
		if( RoutingToken.ROUTING_WILDCARD.equals(that.getHomeCommunityId()) )
			return -1;
		
		int hcCompare = this.getHomeCommunityId().compareTo(that.getHomeCommunityId());
		if(hcCompare != 0)
			return hcCompare;
		
		if( RoutingToken.ROUTING_WILDCARD.equals(this.getRepositoryUniqueId()) )
			return 1;
		if( RoutingToken.ROUTING_WILDCARD.equals(that.getRepositoryUniqueId()) )
			return -1;
		
		return this.getHomeCommunityId().compareTo(that.getRepositoryUniqueId());
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "{" + this.getHomeCommunityId() + ":" + this.getRepositoryUniqueId() + "}";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.homeCommunityId == null) ? 0 : this.homeCommunityId.hashCode());
		result = prime * result + ((this.repositoryUniqueId == null) ? 0 : this.repositoryUniqueId.hashCode());
		result = prime * result + ((this.routingTokenClassName == null) ? 0 : this.routingTokenClassName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RoutingTokenMemento other = (RoutingTokenMemento) obj;
		if (this.homeCommunityId == null)
		{
			if (other.homeCommunityId != null)
				return false;
		}
		else if (!this.homeCommunityId.equals(other.homeCommunityId))
			return false;
		if (this.repositoryUniqueId == null)
		{
			if (other.repositoryUniqueId != null)
				return false;
		}
		else if (!this.repositoryUniqueId.equals(other.repositoryUniqueId))
			return false;
		if (this.routingTokenClassName == null)
		{
			if (other.routingTokenClassName != null)
				return false;
		}
		else if (!this.routingTokenClassName.equals(other.routingTokenClassName))
			return false;
		return true;
	}
	
	
}
