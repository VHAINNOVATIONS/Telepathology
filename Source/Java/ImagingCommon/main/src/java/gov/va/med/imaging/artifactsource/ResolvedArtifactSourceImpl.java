/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author vhaiswbeckec
 *
 */
public class ResolvedArtifactSourceImpl
implements ResolvedArtifactSource, Serializable
{
	private static final long serialVersionUID = 1L;
	private final ArtifactSource artifactSource;
	private final List<URL> artifactUrls;
	private final List<URL> metadataUrls;
	
	/**
	 * 
	 * @param <E>
	 * @param artifactSource
	 * @param metadataUrls
	 * @param artifactUrls
	 * @return
	 */
	public static ResolvedArtifactSourceImpl create(
		ArtifactSource artifactSource, 
		List<URL> metadataUrls, 
		List<URL> artifactUrls)
	{
		return new ResolvedArtifactSourceImpl(artifactSource, metadataUrls, artifactUrls);
	}
	
	/**
	 * If the ArtifactSource is itself resolved then a ResolvedArtifactSource instance can be
	 * created from the ArtifactSource.  This is used in resolving external artifact sources where
	 * the full URLs are in the configuration, in preferred order.
	 *  
	 * @param artifactSource
	 * @return
	 */
	public static ResolvedArtifactSourceImpl create(ArtifactSource artifactSource)
	{
		return new ResolvedArtifactSourceImpl(artifactSource, artifactSource.getMetadataUrls(), artifactSource.getArtifactUrls());
	}
	
	/**
	 * 
	 * @param artifactSource
	 * @param artifactUrls
	 * @param metadataUrls
	 */
	protected ResolvedArtifactSourceImpl(ArtifactSource artifactSource, Collection<URL> metadataUrls, Collection<URL> artifactUrls)
	{
		super();
		if(artifactSource == null)
			throw new UnsupportedOperationException("A resolved artifact source must have a non-null artifact source reference.");
		this.artifactSource = artifactSource;
		
		this.metadataUrls = metadataUrls == null ? new ArrayList<URL>(0) : new ArrayList<URL>(metadataUrls);
		this.artifactUrls = artifactUrls == null ? new ArrayList<URL>(0) : new ArrayList<URL>(artifactUrls);
	}	
	
	@Override
	public boolean isAlien()
	{
		return true;
	}

	@Override
	public boolean isLocal()
	{
		return false;
	}

	@Override
	public boolean isEnabled()
	{
		// not 100% sure about this - seems wrong to always return true but since this represnts an ArtifactSource and not a resolvedSite, this might be ok to always return true
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ResolvedArtifactSource#getArtifactSource()
	 */
	@Override
	public ArtifactSource getArtifactSource()
	{
		return artifactSource;
	}

	@Override
	public URL getArtifactUrl(String protocol)
	{
		if(protocol == null)
			return null;
		
		for( URL artifactUrl : artifactUrls )
			if( protocol.equalsIgnoreCase(artifactUrl.getProtocol()) )
				return artifactUrl;
		
		return null;
	}

	@Override
	public List<URL> getArtifactUrls()
	{
		return artifactUrls;
	}

	@Override
	public URL getMetadataUrl(String protocol)
	{
		if(protocol == null)
			return null;
		
		for( URL metadataUrl : metadataUrls )
			if( protocol.equalsIgnoreCase(metadataUrl.getProtocol()) )
				return metadataUrl;
		
		return null;
	}

	@Override
	public List<URL> getMetadataUrls()
	{
		return metadataUrls;
	}
	
	protected void addArtifactUrl(URL url)
	{
		artifactUrls.add(url);
	}
	
	protected void addMetadataUrl(URL url)
	{
		metadataUrls.add(url);
	}
	
	protected boolean removeArtifactUrl(URL url)
	{
		return artifactUrls.remove(url);
	}
	
	protected boolean removeMetadataUrl(URL url)
	{
		return metadataUrls.remove(url);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( getArtifactSource().toString() );
		sb.append( " metadata URLs {" );
		boolean firstUrl = true;
		for(URL url : getMetadataUrls())
		{
			if(!firstUrl)
				sb.append(',');
			firstUrl = false;
			
			sb.append(url.toExternalForm());
		}
		
		sb.append( "}" );
		
		sb.append( " artifact URLs {" );
		firstUrl = true;
		for(URL url : getArtifactUrls())
		{
			if(!firstUrl)
				sb.append(',');
			firstUrl = false;
			
			sb.append(url.toExternalForm());
		}
		sb.append( "}" );
		
		return sb.toString();
	}
}
