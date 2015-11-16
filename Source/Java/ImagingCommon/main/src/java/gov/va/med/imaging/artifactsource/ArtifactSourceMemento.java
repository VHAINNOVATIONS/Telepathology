/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceMemento
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String artifactSourceClassName;
	private String name;
	private String homeCommunityId;
	private String repositoryId;
	private String[] metadataUrls;
	private String[] artifactUrls;
	
	/**
	 * 
	 */
	public ArtifactSourceMemento()
	{
		super();
	}
	
	public ArtifactSourceMemento(ArtifactSource artifactSource)
	{
		super();
		this.artifactSourceClassName = artifactSource.getClass().getName();
		this.name = artifactSource.getName();
		this.homeCommunityId = artifactSource.getHomeCommunityId().toString();
		this.repositoryId = artifactSource.getRepositoryId();
		
		this.artifactUrls = new String[artifactSource.getArtifactServerCount()];
		int index = 0;
		Iterator<URL> iter = artifactSource.artifactIterator();
		while(iter.hasNext())
			this.artifactUrls[index++] = iter.next().toString();
		
		this.metadataUrls = new String[artifactSource.getMetadataServerCount()];
		index = 0;
		iter = artifactSource.artifactIterator();
		while(iter.hasNext())
			this.metadataUrls[index++] = iter.next().toString();
	}
	
	/**
	 * @param artifactSourceClassName
	 * @param identifier
	 * @param metadataUrls
	 * @param artifactUrls
	 */
	public ArtifactSourceMemento(
		String artifactSourceClassName, 
		String name,
		String homeCommunityId,
		String repositoryId,
		String[] metadataUrls,
		String[] artifactUrls)
	{
		super();
		this.artifactSourceClassName = artifactSourceClassName;
		this.name = name;
		this.homeCommunityId = homeCommunityId;
		this.repositoryId = repositoryId;
		this.metadataUrls = metadataUrls;
		this.artifactUrls = artifactUrls;
	}
	
	/**
	 * @param name
	 * @param identifier2
	 * @param metadataIterator
	 * @param artifactIterator
	 */
	public ArtifactSourceMemento(
		String artifactSourceClassName, 
		String name, 
		String homeCommunityId,
		String repositoryId,
		Iterator<URL> metadataIterator,
		Iterator<URL> artifactIterator)
	{
		super();
		this.artifactSourceClassName = artifactSourceClassName;
		this.name = name;
		this.homeCommunityId = homeCommunityId;
		this.repositoryId = repositoryId;
		
		List<String> urls = new ArrayList<String>();
		while(metadataIterator.hasNext())
			urls.add(metadataIterator.next().toString());
		this.metadataUrls = urls.toArray(new String[urls.size()]);

		urls.clear();
		while(artifactIterator.hasNext())
			urls.add(artifactIterator.next().toString());
		this.artifactUrls = urls.toArray(new String[urls.size()]);
	}

	public String getArtifactSourceClassName()
	{
		return this.artifactSourceClassName;
	}
	
	/**
	 * NOTE: this is the 'name' and not the identifier.
	 * By convention, the identifier is the class name and the
	 * 'name' concatenated with a delimiter.
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}
	public String getHomeCommunityId()
	{
		return this.homeCommunityId;
	}
	public String getRepositoryId()
	{
		return this.repositoryId;
	}
	public void setHomeCommunityId(String homeCommunityId)
	{
		this.homeCommunityId = homeCommunityId;
	}
	public void setRepositoryId(String repositoryId)
	{
		this.repositoryId = repositoryId;
	}
	
	public String[] getMetadataUrls()
	{
		return this.metadataUrls;
	}
	public String[] getArtifactUrls()
	{
		return this.artifactUrls;
	}
	
	public void setArtifactSourceClassName(String artifactSourceClassName)
	{
		this.artifactSourceClassName = artifactSourceClassName;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public void setMetadataUrls(String[] metadataUrls)
	{
		this.metadataUrls = metadataUrls;
	}
	public void setArtifactUrls(String[] artifactUrls)
	{
		this.artifactUrls = artifactUrls;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((this.artifactSourceClassName == null) ? 0 : this.artifactSourceClassName.hashCode());
		result = prime * result + Arrays.hashCode(this.artifactUrls);
		result = prime * result + ((this.homeCommunityId == null) ? 0 : this.homeCommunityId.hashCode());
		result = prime * result + Arrays.hashCode(this.metadataUrls);
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
		final ArtifactSourceMemento other = (ArtifactSourceMemento) obj;
		if (this.artifactSourceClassName == null)
		{
			if (other.artifactSourceClassName != null)
				return false;
		}
		else if (!this.artifactSourceClassName.equals(other.artifactSourceClassName))
			return false;
		if (!Arrays.equals(this.artifactUrls, other.artifactUrls))
			return false;
		if (this.homeCommunityId == null)
		{
			if (other.homeCommunityId != null)
				return false;
		}
		else if (!this.homeCommunityId.equals(other.homeCommunityId))
			return false;
		if (!Arrays.equals(this.metadataUrls, other.metadataUrls))
			return false;
		if (this.name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!this.name.equals(other.name))
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
		sb.append("{");
		sb.append(this.getHomeCommunityId());
		sb.append(":");
		sb.append(this.getRepositoryId());
		sb.append(" metadata URLS[");
		for(String metadataUrl : metadataUrls)
			sb.append(metadataUrl + ",");
		sb.append("]");
		sb.append(" artifactURLS[");
		for(String artifactUrl : artifactUrls)
			sb.append(artifactUrl + ",");
		sb.append("]");
		sb.append("}");
		return sb.toString();
	}


	
}
