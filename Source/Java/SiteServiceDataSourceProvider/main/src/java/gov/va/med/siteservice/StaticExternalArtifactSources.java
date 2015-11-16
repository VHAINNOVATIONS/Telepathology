/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author vhaiswbeckec
 *
 */
public class StaticExternalArtifactSources
implements ExternalArtifactSources, Serializable, Iterable<RoutingToken>
{
	private static final long serialVersionUID = 1L;
	private SortedMap<RoutingTokenImpl, ArtifactSourceLookupResult> mappedRouting = 
		new TreeMap<RoutingTokenImpl, ArtifactSourceLookupResult>(new PrecedenceComparator());
	private transient Logger logger;
	
	/**
	 * 
	 * @param memento
	 */
	public StaticExternalArtifactSources()
	{
	}
	
	/**
	 * @return the logger
	 */
	public synchronized Logger getLogger()
	{
		if(this.logger == null) this.logger = Logger.getLogger(this.getClass());
		return this.logger;
	}

	/**
	 * @see gov.va.med.siteservice.ExternalArtifactSourceResolver#addRoutingToArtifactSource(gov.va.med.RoutingToken, gov.va.med.imaging.artifactsource.ResolvedArtifactSource)
	 */
	@Override
	public void add(RoutingToken routingToken, ArtifactSourceImpl artifactSource)
	{
		if(routingToken == null || artifactSource == null)
		{
			getLogger().error("routingToken is '" + 
				(routingToken == null ? "<null>" : routingToken.toString()) + 
				"', indirectionRoutingToken is '" + 
				(artifactSource == null ? "<null>" : artifactSource.toString()) + "'.");
			return;
		}
		
		RoutingTokenImpl routingTokenImpl = RoutingTokenImpl.create(routingToken);
		getLogger().trace("Adding routing token '" + routingTokenImpl.toString() + "'->'" + artifactSource.toString() + "'.");
		ArtifactSourceLookupResult newValue = new ArtifactSourceLookupResult(artifactSource);
		ArtifactSourceLookupResult oldValue = this.mappedRouting.put(routingTokenImpl, newValue );
		if(oldValue != null)
			getLogger().warn("Replaced '" + oldValue + "' with '" + newValue + "'.");
	}
	
	@Override
	public void addIndirection(RoutingToken routingToken, RoutingTokenImpl indirectionRoutingToken)
	{
		if(routingToken == null || indirectionRoutingToken == null)
		{
			getLogger().error("routingToken is '" + 
				(routingToken == null ? "<null>" : routingToken.toString()) + 
				"', indirectionRoutingToken is '" + 
				(indirectionRoutingToken == null ? "<null>" : indirectionRoutingToken.toString()) + "'.");
			return;
		}
		RoutingTokenImpl routingTokenImpl = RoutingTokenImpl.create(routingToken);
		getLogger().trace("Adding routing token '" + 
			(routingTokenImpl == null ? "<null>" : routingTokenImpl.toString()) + 
			"'->'" + 
			(indirectionRoutingToken == null ? "<null>" : indirectionRoutingToken.toString()) + "'.");
		ArtifactSourceLookupResult newValue = new ArtifactSourceLookupResult(indirectionRoutingToken);
		ArtifactSourceLookupResult oldValue = this.mappedRouting.put(routingTokenImpl, newValue );
		if(oldValue != null)
			getLogger().warn("Replaced '" + oldValue + "' with '" + newValue + "'.");
	}
	
	/**
	 * @see gov.va.med.siteservice.ExternalArtifactSources#clear()
	 */
	@Override
	public void clear()
	{
		this.mappedRouting.clear();
	}
	
	/**
	 * @see gov.va.med.siteservice.ExternalArtifactSourceResolver#deleteRoutingToArtifactSource(gov.va.med.RoutingToken, gov.va.med.imaging.artifactsource.ResolvedArtifactSource)
	 */
	public synchronized void remove(RoutingToken routingToken)
	{
		getLogger().trace("Removing routing token '" + routingToken.toString() + "'.");
		ArtifactSourceLookupResult artifactSource = this.getEquivalent(routingToken);
		if(artifactSource != null)
			this.mappedRouting.remove(routingToken);
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 */
	public void remove(String homeCommunityId, String repositoryId)
	{
		RoutingToken deadMan = null;
		for(RoutingToken rtm : this.mappedRouting.keySet())
			if( rtm.getHomeCommunityId().equals(homeCommunityId) && rtm.getRepositoryUniqueId().equals(repositoryId) )
			{
				deadMan = rtm;
				break;
			}
		
		if(deadMan != null)
			this.mappedRouting.remove(deadMan);
	}

	@Override
	public Iterator<RoutingToken> iterator()
	{
		return new Iterator<RoutingToken>()
		{
			Iterator<RoutingTokenImpl> wrappedIter = mappedRouting.keySet().iterator(); 
			
			@Override
			public boolean hasNext()
			{
				return wrappedIter.hasNext();
			}

			@Override
			public RoutingToken next()
			{
				return wrappedIter.next();
			}

			@Override
			public void remove(){}			
		};
	}

	/**
	 * Get an ArtifactSource which is mapped exactly to the
	 * given routing token.  This method calls the RoutingToken.isEquivalent()
	 * method to find the first RoutingToken that is equivalent to the
	 * given.
	 */
	@Override
	public ArtifactSourceLookupResult getEquivalent(RoutingToken routingToken)
	{
		return get(routingToken, true);
	}

	/**
	 * Get an ArtifactSource which is mapped to a RoutingToken that includes the
	 * given routing token.  This method calls the RoutingToken.isIncluding()
	 * method to find the first RoutingToken that is equivalent to the
	 * given.
	 */
	@Override
	public ArtifactSourceLookupResult getIncluding(RoutingToken routingToken)
	{
		return get(routingToken, false);
	}

	/**
	 * 
	 * @param routingToken
	 * @param requireEquivalence
	 * @return
	 */
	public ArtifactSourceLookupResult get(RoutingToken routingToken, boolean requireEquivalence)
	{
		Set<Entry<RoutingTokenImpl,ArtifactSourceLookupResult>> entries = mappedRouting.entrySet();

		for(Entry<RoutingTokenImpl, ArtifactSourceLookupResult> entry : entries)
		{
			RoutingTokenImpl mapKey = entry.getKey();
			
			if( (requireEquivalence && mapKey.isEquivalent(routingToken)) || (!requireEquivalence && mapKey.isIncluding(routingToken)) )
				return entry.getValue();
		}
		return null;
		
	}
	
	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 * This comparator assures that more general RoutingToken instances are sorted after
	 * more specific.
	 */
	class PrecedenceComparator
	implements Comparator<RoutingToken>,Serializable
	{
		private static final long serialVersionUID = -3972810848919767605L;

		/**
		 * Compares its two arguments for order. 
		 * Returns a negative integer, zero, or a positive integer as the first argument 
		 * is less than, equal to, or greater than the second.
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(RoutingToken routingToken1, RoutingToken routingToken2)
		{
			if( RoutingTokenImpl.isIncluding(routingToken1, routingToken2) )
				return 1;
			if( RoutingTokenImpl.isIncluding(routingToken2, routingToken1) )
				return -1;
			
			int commCompare = routingToken1.getHomeCommunityId().compareTo(routingToken2.getHomeCommunityId());
			if(commCompare != 0)
				return commCompare;
			
			if( RoutingToken.ROUTING_WILDCARD.equals(routingToken1.getRepositoryUniqueId()) )
				return 1;
			
			if( RoutingToken.ROUTING_WILDCARD.equals(routingToken2.getRepositoryUniqueId()) )
				return -1;
			
			return routingToken1.getRepositoryUniqueId().compareTo(routingToken2.getRepositoryUniqueId());
		}
		
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mappedRouting == null) ? 0 : this.mappedRouting.hashCode());
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
		final StaticExternalArtifactSources that = (StaticExternalArtifactSources) obj;
		if (this.mappedRouting == null)
		{
			if (that.mappedRouting != null)
				return false;
		}
		
		for(RoutingToken key : this.mappedRouting.keySet())
		{
			ArtifactSourceLookupResult value = this.mappedRouting.get(key);
			ArtifactSourceLookupResult thatArtifactSource = that.mappedRouting.get( key );
			
			if(thatArtifactSource == null)
				return value == null;
			if(! value.equals(thatArtifactSource))
				return false;
		}
		
		return true;
	}

}
