/**
 * 
 */
package gov.va.med.siteservice;

import java.io.Serializable;
import java.util.Arrays;

public class ProtocolPreferenceMemento
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String homeCommunityId;
	private String repositoryId;
	private boolean siteLocal;
	private boolean siteAlien;
	private String[] preferredProtocols;
	
	public ProtocolPreferenceMemento(){}

	/**
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param siteLocal
	 * @param siteAlien
	 * @param preferredProtocols
	 */
	public ProtocolPreferenceMemento(
		String homeCommunityId, 
		String repositoryId, 
		boolean siteLocal,
		boolean siteAlien, 
		String[] preferredProtocols)
	{
		super();
		this.homeCommunityId = homeCommunityId;
		this.repositoryId = repositoryId;
		this.siteLocal = siteLocal;
		this.siteAlien = siteAlien;
		this.preferredProtocols = preferredProtocols;
	}

	public String getHomeCommunityId()
	{
		return this.homeCommunityId;
	}

	public String getRepositoryId()
	{
		return this.repositoryId;
	}

	public boolean isSiteLocal()
	{
		return this.siteLocal;
	}

	public boolean isSiteAlien()
	{
		return this.siteAlien;
	}

	public String[] getPreferredProtocols()
	{
		return this.preferredProtocols;
	}

	public void setHomeCommunityId(String homeCommunityId)
	{
		this.homeCommunityId = homeCommunityId;
	}

	public void setRepositoryId(String repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public void setSiteLocal(boolean siteLocal)
	{
		this.siteLocal = siteLocal;
	}

	public void setSiteAlien(boolean siteAlien)
	{
		this.siteAlien = siteAlien;
	}

	public void setPreferredProtocols(String[] prefererredProtocols)
	{
		this.preferredProtocols = prefererredProtocols;
	}

	// =========================================================================
	// Eclipse Generated Code
	// =========================================================================
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.homeCommunityId == null) ? 0 : this.homeCommunityId.hashCode());
		result = prime * result + Arrays.hashCode(this.preferredProtocols);
		result = prime * result + ((this.repositoryId == null) ? 0 : this.repositoryId.hashCode());
		result = prime * result + (this.siteAlien ? 1231 : 1237);
		result = prime * result + (this.siteLocal ? 1231 : 1237);
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
		final ProtocolPreferenceMemento other = (ProtocolPreferenceMemento) obj;
		if (this.homeCommunityId == null)
		{
			if (other.homeCommunityId != null)
				return false;
		}
		else if (!this.homeCommunityId.equals(other.homeCommunityId))
			return false;
		if (!Arrays.equals(this.preferredProtocols, other.preferredProtocols))
			return false;
		if (this.repositoryId == null)
		{
			if (other.repositoryId != null)
				return false;
		}
		else if (!this.repositoryId.equals(other.repositoryId))
			return false;
		if (this.siteAlien != other.siteAlien)
			return false;
		if (this.siteLocal != other.siteLocal)
			return false;
		return true;
	}
}