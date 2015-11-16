package gov.va.med.siteservice.siteprotocol;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.WellKnownOID;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The immutable class representing the preferred protocols for 
 * a specific site or the default preferred protocols if the site number
 * is null.
 * 
 * @author vhaiswbeckec
 *
 */
public class ProtocolPreference
implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final OID homeCommunityId;
	private final String repositoryId;
	private final boolean siteLocal;
	private final boolean siteAlien;
	private final boolean enabled;
	private final String[] preferredProtocols;

	public static ProtocolPreference create(
		OID homeCommunityId, 
		String repositoryId, 
		boolean siteLocal, 
		boolean siteAlien, 
		boolean enabled,
		String[] prefererredProtocols)
	{
		return new ProtocolPreference(homeCommunityId, repositoryId, siteLocal, siteAlien, enabled, prefererredProtocols);
	}
	
	public static ProtocolPreference createForVARadiologySite(String siteId, boolean siteLocal, boolean siteAlien, boolean enabled, String[] prefererredProtocols)
	{
		return new ProtocolPreference(WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue(), siteId, siteLocal, 
				siteAlien, enabled, prefererredProtocols);
	}
	
	public static ProtocolPreference createForVADocumentSite(String siteId, boolean siteLocal, boolean siteAlien, boolean enabled, String[] prefererredProtocols)
	{
		return new ProtocolPreference(WellKnownOID.VA_DOCUMENT.getCanonicalValue(), siteId, siteLocal, 
				siteAlien, enabled, prefererredProtocols);
	}
	
	public static ProtocolPreference createForVARadiologyDefault(String[] prefererredProtocols)
	{
		return new ProtocolPreference(WellKnownOID.VA_RADIOLOGY_IMAGE.getCanonicalValue(), RoutingToken.ROUTING_WILDCARD, false, 
				false, true, prefererredProtocols);
	}
	
	public static ProtocolPreference createForVADocumentDefault(String[] prefererredProtocols)
	{
		return new ProtocolPreference(WellKnownOID.VA_DOCUMENT.getCanonicalValue(), RoutingToken.ROUTING_WILDCARD, false, 
				false, true, prefererredProtocols);
	}
	
	public static ProtocolPreference createForDodDocumentRepository(String repositoryId, String[] prefererredProtocols)
	{
		return new ProtocolPreference(WellKnownOID.HAIMS_DOCUMENT.getCanonicalValue(), repositoryId, false, 
				true, true, prefererredProtocols);
	}
	
	public static List<ProtocolPreference> createForDodDocumentDefault(String[] prefererredProtocols)
	{
		List<ProtocolPreference> pp = new ArrayList<ProtocolPreference>();
		for(OID oid : WellKnownOID.HAIMS_DOCUMENT.getAllValues())
			pp.add( new ProtocolPreference(oid, RoutingToken.ROUTING_WILDCARD, false, 
					true, true, prefererredProtocols) );
		
		return pp;
	}
	
	public static List<ProtocolPreference> createForDodRadiologyDefault(String[] prefererredProtocols)
	{
		List<ProtocolPreference> pp = new ArrayList<ProtocolPreference>();
		for(OID oid : WellKnownOID.BHIE_RADIOLOGY.getAllValues())
			pp.add( new ProtocolPreference(oid, RoutingToken.ROUTING_WILDCARD, false, 
					true, true, prefererredProtocols) );
		
		return pp;
	}
	
	public static ProtocolPreference createForAlienSite(OID homeCommunityId, String repositoryId, String[] prefererredProtocols)
	{
		return new ProtocolPreference(homeCommunityId, repositoryId, false, 
				true, true, prefererredProtocols);
	}
	
	public static ProtocolPreference createForDefaultAlienSite(String[] prefererredProtocols)
	{
		return new ProtocolPreference(null, null, false, true, true, prefererredProtocols);
	}
	
	/**
	 * 
	 * @param siteNumber
	 * @param siteLocal
	 * @param siteAlien
	 * @param prefererredProtocols
	 */
	private ProtocolPreference(
		OID homeCommunityId, 
		String repositoryId, 
		boolean siteLocal, 
		boolean siteAlien, 
		boolean enabled,
		String[] preferredProtocols)
    {
	    super();
	    
	    assert homeCommunityId != null : "ProtocolPreference cannot be created with a null home community ID.";
	    assert repositoryId != null : "ProtocolPreference cannot be created with a null repository ID, use '*' for wildcard match.";
	    assert preferredProtocols != null : "ProtocolPreference cannot be created with a null preferred protocols.";
	    assert preferredProtocols.length >= 1 : "ProtocolPreference cannot be created with an empty preferred protocols.";
	    
	    this.homeCommunityId = homeCommunityId;
	    this.repositoryId = repositoryId;
	    this.siteAlien = siteAlien;
	    this.siteLocal = siteLocal;
	    this.preferredProtocols = preferredProtocols;
	    this.enabled = enabled;
    }

	public boolean isVaRadiologyRepository()
	{
		return WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(this.homeCommunityId);
	}
	
	public boolean isVaDocumentRepository()
	{
		return WellKnownOID.VA_DOCUMENT.isApplicable(this.homeCommunityId);
	}

	public boolean isVaRepository()
	{
		return isVaRadiologyRepository() || isVaDocumentRepository();
	}
	
	public boolean isWildcardRepository()
	{
		return RoutingToken.ROUTING_WILDCARD.equals(this.getRepositoryId());
	}
	
	public OID getHomeCommunityId()
	{
		return this.homeCommunityId;
	}

	public String getRepositoryId()
    {
    	return repositoryId;
    }

	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * If this is a VA site then the repository ID is returned as the
	 * site number, otherwise return a null.
	 * @return
	 */
	public String getSiteNumber()
    {
		return 
			WellKnownOID.VA_DOCUMENT.isApplicable(getHomeCommunityId()) ||
			WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(getHomeCommunityId()) ?
				repositoryId : null;
    }

	public boolean isSiteLocal()
    {
    	return siteLocal;
    }

	public boolean isSiteAlien()
    {
    	return siteAlien;
    }

	public String[] getPrefererredProtocols()
    {
    	return preferredProtocols;
    }

	// ==========================================================

	/**
	 * Returns true if the home community and the repository ID are equal.
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @return
	 */
	public boolean equals(OID homeCommunityId, String repositoryId)
	{
		return 
			(homeCommunityId == null && getHomeCommunityId() == null || 
			 homeCommunityId != null && homeCommunityId.equals(getHomeCommunityId())) &&
			(repositoryId == null && getRepositoryId() == null ||
			 repositoryId != null && repositoryId.equals(getRepositoryId()));
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.homeCommunityId == null) ? 0 : this.homeCommunityId.hashCode());
		result = prime * result + ((this.repositoryId == null) ? 0 : this.repositoryId.hashCode());
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
		final ProtocolPreference other = (ProtocolPreference) obj;
		if (this.homeCommunityId == null)
		{
			if (other.homeCommunityId != null)
				return false;
		}
		else if (!this.homeCommunityId.equals(other.homeCommunityId))
			return false;
		if (this.repositoryId == null)
		{
			if (other.repositoryId != null)
				return false;
		}
		else if (!this.repositoryId.equals(other.repositoryId))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		sb.append('[');
		sb.append(getHomeCommunityId());
		sb.append(':');
		sb.append(getRepositoryId());
		sb.append(isSiteLocal() ? " local " : " remote ");
		sb.append(isSiteAlien() ? " alien " : " native ");
		sb.append(isEnabled() ? " enabled " : " disabled ");
		sb.append('{');
		if(preferredProtocols == null)
			sb.append("<no protocols configured>");
		else
			for(String prefererredProtocol : preferredProtocols)
				sb.append(" " + prefererredProtocol);
		sb.append('}');
		sb.append(']');

		return sb.toString();
	}
}
