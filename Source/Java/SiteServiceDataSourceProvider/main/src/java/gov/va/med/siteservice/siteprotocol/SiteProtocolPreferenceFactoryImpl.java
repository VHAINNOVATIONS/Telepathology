package gov.va.med.siteservice.siteprotocol;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.siteservice.SiteProtocolPreferenceFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class SiteProtocolPreferenceFactoryImpl 
implements SiteProtocolPreferenceFactory
{
	private static transient Logger logger = Logger.getLogger(SiteProtocolPreferenceFactoryImpl.class);
	private static final long serialVersionUID = 1L;
	
	private final SortedSet<ProtocolPreference> preferences;

	/**
	 * The default constructor, which all of the other constructors should
	 * call first, includes a comparator so that protocol preferences with wildcards come
	 * after those with "real" repository IDs.
	 */
	public SiteProtocolPreferenceFactoryImpl()
	{
		super();
		preferences = new TreeSet<ProtocolPreference>(new Comparator<ProtocolPreference>()
		{
			@Override
			public int compare(ProtocolPreference o1, ProtocolPreference o2)
			{
				int communityIdCompare = o1.getHomeCommunityId().compareTo(o2.getHomeCommunityId());
				if(communityIdCompare != 0)
					return communityIdCompare;
				
				int repositoryIdCompare = 
					RoutingToken.ROUTING_WILDCARD.equals(o1.getRepositoryId()) ?
						1 :
						RoutingToken.ROUTING_WILDCARD.equals(o2.getRepositoryId()) ? -1 :
							o1.getRepositoryId().compareTo(o2.getRepositoryId());
				
				return repositoryIdCompare;
			}
		}
		);
	}
	
	
	
	/**
	 * Unlike regular Set behavior, adding a ProtocolPreference that
	 * already exists replaces an existing ProtocolPreference
	 * if they are .equals(), i.e. the same site number.
	 * 
	 * @see java.util.HashSet#add(java.lang.Object)
	 */
	@Override
    public boolean add(ProtocolPreference e)
    {
    	if(e == null)
    		return false;
    	
		ProtocolPreference existingPreference = findBySiteNumber(e.getSiteNumber());
		if(existingPreference != null)
			preferences.remove(existingPreference);
		
	    return preferences.add(e);
    }

    public void addAll(Collection<ProtocolPreference> c)
    {
    	for(ProtocolPreference preference : c)
    		add(preference);
    }
    
	/**
	 * Clear all except the default ProtocolPreference
	 * @see java.util.HashSet#clear()
	 */
    @Override
    public void clear()
    {
		ProtocolPreference defaultPreference = findDefaultSite();
	    preferences.clear();
	    add(defaultPreference);
    }

	/**
	 * A remove() that disallows removal of the default ProtocolPreference
	 * 
	 * @see java.util.HashSet#remove(java.lang.Object)
	 */
    public boolean remove(ProtocolPreference protocolPreference)
    {
		if(protocolPreference instanceof ProtocolPreference)
			if( ((ProtocolPreference)protocolPreference).getSiteNumber() == null )
				return false;
		
	    return preferences.remove(protocolPreference);
    }

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteProtocolPreferenceFactory#remove(gov.va.med.OID, java.lang.String)
	 */
	@Override
	public void remove(OID homeCommunityId, String repositoryId)
	{
		ProtocolPreference deadMan = null;
		for(ProtocolPreference protocolPreference : preferences)
			if( protocolPreference.getHomeCommunityId().equals(homeCommunityId) &&
				protocolPreference.getRepositoryId().equals(repositoryId) )
			{
				deadMan = protocolPreference;
				break;
			}
		if(deadMan != null)
			preferences.remove(deadMan);
	}


	@Override
    public String[] getPreferredProtocols(String siteNumber)
    {
		ProtocolPreference protocolPreference = findBySiteNumber(siteNumber);
		if(protocolPreference == null)
			protocolPreference = findDefaultSite();
		
		String[] preferredProtocols = protocolPreference.getPrefererredProtocols();
		return preferredProtocols == null ? null : preferredProtocols.clone();
    }

	@Override
	public String[] getPreferredProtocols(ArtifactSource artifactSource)
	{
		if(artifactSource == null)
			return null;

		return getPreferredProtocols(artifactSource.getHomeCommunityId(), artifactSource.getRepositoryId());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteProtocolPreferenceFactory#getPreferredProtocols(gov.va.med.OID, java.lang.String)
	 */
	@Override
	public String[] getPreferredProtocols(OID homeCommunityId, String repositoryId)
	{
		if(homeCommunityId == null || repositoryId == null)
			return null;
		
		for(ProtocolPreference protocolPreference : preferences)
			if( protocolPreference.getHomeCommunityId().equals(homeCommunityId) &&
				protocolPreference.getRepositoryId().equals(repositoryId) )
					return protocolPreference.getPrefererredProtocols().clone();
		
		ProtocolPreference defaultProtocolPreference = (getDefaultPreferredProtocols(homeCommunityId));
		
		return defaultProtocolPreference == null ? null : defaultProtocolPreference.getPrefererredProtocols().clone();
	}


	/**
	 * @param artifactSource
	 * @return
	 */
	private ProtocolPreference getDefaultPreferredProtocols(ArtifactSource artifactSource)
	{
		if(artifactSource == null)
			return null;
		return getDefaultPreferredProtocols(artifactSource.getHomeCommunityId());
	}
	
	/**
	 * 
	 * @param homeCommunityId
	 * @return
	 */
	private ProtocolPreference getDefaultPreferredProtocols(OID homeCommunityId)
	{
		if(homeCommunityId == null)
			return null;
		
		for(ProtocolPreference protocolPreference : preferences)
			if( protocolPreference.isWildcardRepository() && 
				protocolPreference.getHomeCommunityId().equals(homeCommunityId) )
					return protocolPreference;
		
		return null;
	}

	@Override
    public boolean isSiteAlien(String siteNumber)
    {
		ProtocolPreference protocolPreference = findBySiteNumber(siteNumber);
		
	    return protocolPreference == null ? false : protocolPreference.isSiteAlien();
    }

	@Override
    public boolean isSiteLocal(String siteNumber)
    {
		ProtocolPreference protocolPreference = findBySiteNumber(siteNumber);
		
	    return protocolPreference == null ? false : protocolPreference.isSiteLocal();
    }
	
	@Override
	public boolean isSiteEnabled(String siteNumber)
	{
		ProtocolPreference protocolPreference = findBySiteNumber(siteNumber);
		
	    return protocolPreference == null ? true : protocolPreference.isEnabled();
	}



	/**
	 * Find the protocol preferences by site number if a site is either exceptional or
	 * local, else return null.
	 * @param siteNumber
	 * @return
	 */
	private ProtocolPreference findBySiteNumber(String siteNumber)
	{
		if(siteNumber == null)
			return null;
		
		for(ProtocolPreference protocolPreference : preferences)
			if( protocolPreference.isVaRepository() && siteNumber.equals(protocolPreference.getSiteNumber()) )
				return protocolPreference;
		return null;
	}
	
	/**
	 * return the protocol preferences where the site number is null.
	 * 
	 * @return
	 */
	private ProtocolPreference findDefaultSite()
	{
		for(ProtocolPreference protocolPreference : preferences)
			if( protocolPreference.isVaRepository() && protocolPreference.isWildcardRepository() )
					return protocolPreference;
		return null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.preferences == null) ? 0 : this.preferences.hashCode());
		return result;
	}

	/**
	 * This method is a combination of generated and hand-written code, don't
	 * regenerate it!
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
		final SiteProtocolPreferenceFactoryImpl other = (SiteProtocolPreferenceFactoryImpl) obj;
		if (this.preferences == null)
		{
			if (other.preferences != null)
				return false;
		}
		else if( this.preferences.size() != other.preferences.size() )
			return false;
		
		Iterator<ProtocolPreference> thisPreferencesIter = preferences.iterator();
		Iterator<ProtocolPreference> otherPreferencesIter = preferences.iterator();
		
		// it is a sorted set so the members must be in the same order
		while(thisPreferencesIter.hasNext() && otherPreferencesIter.hasNext())
			if( ! thisPreferencesIter.next().equals(otherPreferencesIter.next()) )
				return false;
			
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("HashSetProtocolPreferenceFactory \n");
		for(ProtocolPreference protocolPreference : this.preferences)
			sb.append(protocolPreference.toString() + "\n");
		
		return sb.toString();
	}
}
