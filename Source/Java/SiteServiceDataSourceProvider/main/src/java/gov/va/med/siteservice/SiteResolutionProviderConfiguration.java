package gov.va.med.siteservice;

import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.siteservice.siteprotocol.SiteProtocolPreferenceFactoryImpl;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class SiteResolutionProviderConfiguration
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// ===============================================================================
	// Member Fields
	// ===============================================================================
	private SiteServiceConfiguration siteServiceConfiguration = new SiteServiceConfiguration();
	private Map<String, ProtocolServerConfiguration> protocolConfiguration = new HashMap<String, ProtocolServerConfiguration>();
	// the ExternalArtifactSources are directly serialized, no memento needed
	private ExternalArtifactSources externalArtifactSources = new StaticExternalArtifactSources();
	private SiteProtocolPreferenceFactory siteProtocolPreferenceFactory = new SiteProtocolPreferenceFactoryImpl();
	
	public SiteResolutionProviderConfiguration()
	{
		
	}

	public SiteServiceConfiguration getSiteServiceConfiguration()
	{
		return this.siteServiceConfiguration;
	}

	public void setSiteServiceConfiguration(SiteServiceConfiguration siteServiceConfiguration)
	{
		this.siteServiceConfiguration = siteServiceConfiguration;
	}

	/**
	 * @return the siteProtocolPreferenceFactory
	 */
	public SiteProtocolPreferenceFactory getSiteProtocolPreferenceFactory()
	{
		return this.siteProtocolPreferenceFactory;
	}

	/**
	 * @param siteProtocolPreferenceFactory the siteProtocolPreferenceFactory to set
	 */
	public void setSiteProtocolPreferenceFactory(SiteProtocolPreferenceFactory siteProtocolPreferenceFactory)
	{
		this.siteProtocolPreferenceFactory = siteProtocolPreferenceFactory;
	}

	/**
	 * @return the protocolConfiguration
	 */
	public Map<String, ProtocolServerConfiguration> getProtocolConfiguration()
	{
		return this.protocolConfiguration;
	}
	
	public void setProtocolConfiguration(Map<String, ProtocolServerConfiguration> protocolConfiguration)
	{
		this.protocolConfiguration = protocolConfiguration;
	}

	public void addProtocolConfiguration(String protocol, ProtocolServerConfiguration protocolConfiguration)
	{
		this.protocolConfiguration.put(protocol, protocolConfiguration);
	}
	
	public ProtocolServerConfiguration getProtocolConfiguration(String protocol)
	{
		return protocolConfiguration.get(protocol);
	}

	public void removeProtocolConfiguration(String protocol)
	{
		protocolConfiguration.remove(protocol);
	}
	
	/**
	 * 
	 * @return
	 */
	public ExternalArtifactSources getExternalArtifactSources()
	{
		return this.externalArtifactSources;
	}

	public void setExternalArtifactSources(ExternalArtifactSources externalArtifactSources)
	{
		this.externalArtifactSources = externalArtifactSources;
	}
	
	/**
	 * @param homeCommunityId
	 * @param repositoryId
	 * @throws RoutingTokenFormatException 
	 */
	public void removeArtifactRepository(String homeCommunityId, String repositoryId) 
	throws RoutingTokenFormatException
	{
		this.externalArtifactSources.remove(homeCommunityId, repositoryId);
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("SiteResolutionProviderMemento ======================================== \n");
		sb.append("\tProtocol Configuration \n");
		for(String protocol : protocolConfiguration.keySet())
		{
			sb.append("\t" + protocol + "=>" + protocolConfiguration.get(protocol).toString() + "============");
			sb.append("\t" + "============");
		}
		if(getSiteProtocolPreferenceFactory() != null)
			sb.append(getSiteProtocolPreferenceFactory().toString());
		
		if(getExternalArtifactSources() != null)
			sb.append(getExternalArtifactSources().toString());
		
		sb.append("====================================================================== \n");
		
		return sb.toString();
	}

	// ==================================================================================================================
	// Eclipse generated code
	// ==================================================================================================================
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((this.externalArtifactSources == null) ? 0 : this.externalArtifactSources.hashCode());
		result = prime * result + ((this.protocolConfiguration == null) ? 0 : this.protocolConfiguration.hashCode());
		result = prime * result
			+ ((this.siteProtocolPreferenceFactory == null) ? 0 : this.siteProtocolPreferenceFactory.hashCode());
		result = prime * result
			+ ((this.siteServiceConfiguration == null) ? 0 : this.siteServiceConfiguration.hashCode());
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
		final SiteResolutionProviderConfiguration other = (SiteResolutionProviderConfiguration) obj;
		if (this.externalArtifactSources == null)
		{
			if (other.externalArtifactSources != null)
				return false;
		}
		else if (!this.externalArtifactSources.equals(other.externalArtifactSources))
			return false;
		if (this.protocolConfiguration == null)
		{
			if (other.protocolConfiguration != null)
				return false;
		}
		else if (!this.protocolConfiguration.equals(other.protocolConfiguration))
			return false;
		if (this.siteProtocolPreferenceFactory == null)
		{
			if (other.siteProtocolPreferenceFactory != null)
				return false;
		}
		else if (!this.siteProtocolPreferenceFactory.equals(other.siteProtocolPreferenceFactory))
			return false;
		if (this.siteServiceConfiguration == null)
		{
			if (other.siteServiceConfiguration != null)
				return false;
		}
		else if (!this.siteServiceConfiguration.equals(other.siteServiceConfiguration))
			return false;
		return true;
	}
}
