/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSourceMemento;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author vhaiswbeckec
 *
 */
public class RoutingToArtifactSourceMapMemento
implements Serializable, Iterable<RoutingTokenImpl>
{
	private static final long serialVersionUID = 1L;

	private Map<RoutingTokenImpl, ArtifactSourceMemento> routingMap = 
		new HashMap<RoutingTokenImpl, ArtifactSourceMemento>();
	private String routingToArtifactSourceMapClassName;
	
	/**
	 * @return
	 */
	public static RoutingToArtifactSourceMapMemento createDefault()
	{
		RoutingToArtifactSourceMapMemento result = new RoutingToArtifactSourceMapMemento();
		result.setRoutingToArtifactSourceMapClassName("gov.va.med.siteservice.StaticExternalArtifactSources");
		
		return result;
	}
	
	/**
	 * @return the routingToArtifactSourceMapClassName
	 */
	public String getRoutingToArtifactSourceMapClassName()
	{
		return this.routingToArtifactSourceMapClassName;
	}

	/**
	 * @param routingToArtifactSourceMapClassName the routingToArtifactSourceMapClassName to set
	 */
	public void setRoutingToArtifactSourceMapClassName(String routingToArtifactSourceMapClassName)
	{
		this.routingToArtifactSourceMapClassName = routingToArtifactSourceMapClassName;
	}

	public void put(String homeCommunityId, String repositoryId, ArtifactSourceMemento artifactSourceMemento) 
	throws RoutingTokenFormatException
	{
		RoutingToken routingToken = RoutingTokenImpl.create(homeCommunityId, repositoryId);
		put((RoutingTokenImpl)routingToken, artifactSourceMemento);
	}

	public void put(RoutingTokenImpl routingToken, ArtifactSourceMemento artifactSourceMemento)
	{
		this.routingMap.put(routingToken, artifactSourceMemento);
	}
	
	public void putEquivalent(ArtifactSourceMemento artifactSourceMemento) 
	throws RoutingTokenFormatException
	{
		RoutingToken routingToken = RoutingTokenImpl.create(artifactSourceMemento.getHomeCommunityId(), artifactSourceMemento.getRepositoryId());
		put((RoutingTokenImpl)routingToken, artifactSourceMemento);
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @return
	 * @throws RoutingTokenFormatException
	 */
	public ArtifactSourceMemento getEquivalent(String homeCommunityId, String repositoryId) 
	throws RoutingTokenFormatException
	{
		RoutingToken routingToken = RoutingTokenImpl.create(homeCommunityId, repositoryId);
		return getEquivalent(routingToken);
	}

	/**
	 * 
	 * @param routingToken
	 * @return
	 */
	public ArtifactSourceMemento getEquivalent(RoutingToken routingToken)
	{
		for(RoutingTokenImpl rtm : routingMap.keySet())
			if( rtm.isEquivalent(routingToken) )
				return routingMap.get(rtm);
		
		return null;
	}
	
	/**
	 * Used for internal management of mapping when exact matching is required.
	 * getEquivalent() or getIncluding() are used when finding a mapping.
	 * 
	 * @param routingToken
	 * @return
	 */
	public ArtifactSourceMemento get(RoutingTokenImpl routingToken)
	{
		return routingMap.get(routingToken);
	}
	
	/**
	 * @param homeCommunityId
	 * @param repositoryId
	 * @throws RoutingTokenFormatException 
	 */
	public void removeEquivalent(String homeCommunityId, String repositoryId) 
	throws RoutingTokenFormatException
	{
		RoutingToken routingToken = RoutingTokenImpl.create(homeCommunityId, repositoryId);
		removeEquivalent(routingToken);
	}

	/**
	 * @param routingToken
	 */
	public void removeEquivalent(RoutingToken routingToken)
	{
		for(RoutingTokenImpl rtm : routingMap.keySet())
			if( rtm.isEquivalent(routingToken) )
				routingMap.remove(rtm);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<RoutingTokenImpl> iterator()
	{
		return routingMap.keySet().iterator(); 
	}
	
	public void clear()
	{
		this.routingMap.clear();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.routingMap == null) ? 0 : this.routingMap.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoutingToArtifactSourceMapMemento other = (RoutingToArtifactSourceMapMemento) obj;
		if (this.routingMap == null)
		{
			if (other.routingMap != null)
				return false;
		}
		else if (!this.routingMap.equals(other.routingMap))
			return false;
		return true;
	}
}
