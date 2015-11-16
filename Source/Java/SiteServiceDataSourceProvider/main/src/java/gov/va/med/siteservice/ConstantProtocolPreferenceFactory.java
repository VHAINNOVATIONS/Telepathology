package gov.va.med.siteservice;

import gov.va.med.OID;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.siteservice.siteprotocol.ProtocolPreference;

/**
 * An internal implementation of ProtocolPreferenceFactory
 * to allow overriding of the preferred preferences by request.
 * This class is used in testing and shouldn't be touched in
 * production. 
 */
class ConstantProtocolPreferenceFactory
implements SiteProtocolPreferenceFactory
{
	private static final long serialVersionUID = 1L;
	private final String[] preferredProtocols;
	
	public ConstantProtocolPreferenceFactory(String[] preferredProtocols)
	{
		this.preferredProtocols = preferredProtocols;
	}
	
	@Override
    public String[] getPreferredProtocols(String siteNumber)
    {
        return preferredProtocols;
    }

	@Override
    public boolean isSiteAlien(String siteNumber)
    {
        return false;
    }

	@Override
    public boolean isSiteLocal(String siteNumber)
    {
        return false;
    }

	@Override
	public boolean isSiteEnabled(String siteNumber)
	{
		return true;
	}

	@Override
	public String[] getPreferredProtocols(ArtifactSource artifactSource)
	{
        return preferredProtocols;
	}

	@Override
	public boolean add(ProtocolPreference ppm)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteProtocolPreferenceFactory#clear()
	 */
	@Override
	public void clear()
	{
	}

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteProtocolPreferenceFactory#getPreferredProtocols(gov.va.med.OID, java.lang.String)
	 */
	@Override
	public String[] getPreferredProtocols(OID homeCommunityId, String repositoryId)
	{
        return preferredProtocols;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteProtocolPreferenceFactory#remove(gov.va.med.OID, java.lang.String)
	 */
	@Override
	public void remove(OID homeCommunityId, String repositoryId)
	{
	}
	
}