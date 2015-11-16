/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.WellKnownOID;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.exceptions.OIDFormatException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * The base class for all ArtifactSource realizations.  Repositories and
 * gateways are both represented by this class.  A gateway will have a wildcard
 * for the repository ID and a repository will have a "real" repository ID.
 * The metadata and the artifact servers are stored as two separate
 * Sets.
 * 
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceImpl
implements ArtifactSource
{
	private static final long serialVersionUID = 6487342321516780259L;
	
	protected final static String intraNameDelimiter = "-";
	protected final static String nameDelimiter = ":";
	
	private final String name;
	private final OID homeCommunityId;
	private final String repositoryId;
	private final URLUniqueProtocolSet availableMetadataServers = new URLUniqueProtocolSet();
	private final URLUniqueProtocolSet availableArtifactServers = new URLUniqueProtocolSet();
	
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * 
	 * @param identifier
	 * @param servers
	 */
	public ArtifactSourceImpl(
		OID homeCommunityId, 
		String repositoryId, 
		URL[] metadataServers, 
		URL[] artifactServers)
	{
	    super();
	    	
		this.homeCommunityId = homeCommunityId;
		this.repositoryId = repositoryId;
	    this.name = homeCommunityId.toString() + intraNameDelimiter + repositoryId;
	    
		for(URL server : metadataServers)
			addMetadataServer(server);
		
		for(URL server : artifactServers)
			addArtifactServer(server);
	}
	
	/**
	 * @param memento
	 */
	public ArtifactSourceImpl(ArtifactSourceMemento memento)
	{
	    super();
	    this.name = memento.getName();
	    try
		{
			this.homeCommunityId = OID.create(memento.getHomeCommunityId());
		}
		catch (OIDFormatException x1)
		{
			throw new IllegalArgumentException("The home community ID '" + memento.getHomeCommunityId() + "' is not a valid OID and it must be.");
		}
		this.repositoryId = memento.getRepositoryId();
	    
		for(String url : memento.getMetadataUrls())
			try
			{
				addMetadataServer( new URL(url) );
			}
			catch (MalformedURLException x)
			{
				getLogger().error("The metadata URL '" + url + "' is not a valid URL and is being ignored.", x);
			}
		
		for(String url : memento.getArtifactUrls())
			try
			{
				addArtifactServer( new URL(url) );
			}
			catch (MalformedURLException x)
			{
				getLogger().error("The artifact URL '" + url + "' is not a valid URL and is being ignored.", x);
			}
	}

	protected Logger getLogger()
	{
		return this.logger;
	}

	public String getName()
	{
		return getHomeCommunityId().toString() + intraNameDelimiter + getRepositoryId();
	}

	@Override
	public OID getHomeCommunityId()
	{
		return this.homeCommunityId;
	}

	public String getRepositoryId()
	{
		return this.repositoryId;
	}

	@Override
	public boolean isRepresents(OID homeCommunityId, String repositoryId)
	{
		return homeCommunityId == null || repositoryId == null ? 
			false :
			homeCommunityId.equals(getHomeCommunityId()) && repositoryId.equals(getRepositoryId());
	}
	
	public boolean isVaRadiology(){return WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(getHomeCommunityId());}
	public boolean isVaDocument(){return WellKnownOID.VA_DOCUMENT.isApplicable(getHomeCommunityId());}
	public boolean isDodRadiology(){return WellKnownOID.BHIE_RADIOLOGY.isApplicable(getHomeCommunityId());}
	public boolean isDodDocument(){return WellKnownOID.HAIMS_DOCUMENT.isApplicable(getHomeCommunityId());}
	
	@Override
	public String getIdentifier()
	{
		return this.getClass().getName() + nameDelimiter + getName();
	}

	/**
	 * 
	 */
	@Override
	public URL getAvailableMetadataServer(String protocol)
	{
		if(protocol == null)
			return null;
		
		for(URL server : this.availableMetadataServers)
			if(server != null && protocol.equals(server.getProtocol()))
				return server;
		
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public URL getAvailableArtifactServer(String protocol)
	{
		if(protocol == null)
			return null;
		
		for(URL server : this.availableArtifactServers)
			if(server != null && protocol.equals(server.getProtocol()))
				return server;
		
		return null;
	}
	
	
	
	@Override
	public Iterator<URL> artifactIterator()
	{
		return this.availableArtifactServers.iterator();
	}

	@Override
	public int getArtifactServerCount()
	{
		return this.availableArtifactServers.size();
	}

	@Override
	public Iterator<URL> metadataIterator()
	{
		return this.availableMetadataServers.iterator();
	}

	@Override
	public int getMetadataServerCount()
	{
		return this.availableMetadataServers.size();
	}

	/**
	 * 
	 * @param server - null values are ignored.
	 */
	private void addMetadataServer(URL server)
	{
		if(server == null)
			return;
		this.availableMetadataServers.add(server);
	}
	
	/**
	 * 
	 * @param server - null values are ignored.
	 */
	private void addArtifactServer(URL server)
	{
		if(server == null)
			return;
		this.availableArtifactServers.add(server);
	}
	
	@Override
	public RoutingToken createRoutingToken()
	{
		try
		{
			return RoutingTokenImpl.create(getHomeCommunityId().toString(), getRepositoryId());
		}
		catch (RoutingTokenFormatException x)
		{
			logger.error("Unable to create routing token from artifact source '" + this.toString() + "'.");
			return null;
		}
	}

	/**
	 * @return
	 */
	@Override
	public List<URL> getMetadataUrls()
	{
		List<URL> urlList = new ArrayList<URL>();
		urlList.addAll( this.availableMetadataServers );
		return urlList;
	}
	
	@Override
	public List<URL> getArtifactUrls()
	{
		List<URL> urlList = new ArrayList<URL>();
		urlList.addAll( this.availableArtifactServers );
		return urlList;
	}
	
	@Override
	public ArtifactSourceMemento getMemento()
	{
		return new ArtifactSourceMemento(this);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		sb.append('(');
		sb.append( this.getIdentifier() );
		sb.append(')');
		
		return sb.toString();
	}

	/**
	 * A HashSet containing URLs that defines equality as
	 * equal protocols.
	 * If a new URL is added with a protocol equal to an existing
	 * URL protocol then the new URL replaces the existing one.
	 * Null URLs and null protocol values are not allowed.
	 *
	 */
	class URLUniqueProtocolSet 
	extends HashSet<URL>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean add(URL url)
		{
			if(url == null || url.getProtocol() == null)
				return false;
			
			URL existing = getByProtocol(url.getProtocol());
			if(existing != null)
			{
				getLogger().warn("Existing URL '" + existing.toString() + "' is being replaced by '" + url.toString() + "' because the protocols are the same.");
				remove(existing);
			}
			
			return super.add(url);
		}

		public URL getByProtocol(String protocol)
		{
			for(URL url : this)
				if(url.getProtocol().equals(protocol))
					return url;
			
			return null;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(! (obj instanceof URLUniqueProtocolSet) )
				return false;
			URLUniqueProtocolSet that = (URLUniqueProtocolSet)obj;
			if(this.size() != that.size())
				return false;
			for(URL url : this)
				if(! that.contains(url))
					return false;
			
			return true;
		}

		/**
		 * @return
		 */
		public Set<URL> getCopy()
		{
			return Collections.unmodifiableSet(this);
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((this.availableArtifactServers == null) ? 0 : this.availableArtifactServers.hashCode());
		result = prime * result
			+ ((this.availableMetadataServers == null) ? 0 : this.availableMetadataServers.hashCode());
		result = prime * result + ((this.homeCommunityId == null) ? 0 : this.homeCommunityId.hashCode());
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
		if (! (obj instanceof ArtifactSource) )
			return false;
		final ArtifactSource that = (ArtifactSource) obj;
		if (this.homeCommunityId == null)
		{
			if (that.getHomeCommunityId() != null)
				return false;
		}
		else if (!this.homeCommunityId.equals(that.getHomeCommunityId()))
			return false;
		if (this.repositoryId == null)
		{
			if (that.getRepositoryId() != null)
				return false;
		}
		else if (!this.repositoryId.equals(that.getRepositoryId()))
			return false;
		return true;
	}
}
