/**
 * 
 */
package gov.va.med.imaging.datasource;

import java.net.URL;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractVersionableDataSource
implements VersionableDataSourceSpi
{
	private final ResolvedArtifactSource resolvedArtifactSource;
	private String protocol;
	private Object configuration;
	
	/**
	 * All versionable data sources should include a constructor and/or a static factory
	 * method taking a ResolvedArtifactSource and a protocol.
	 * 
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public AbstractVersionableDataSource(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		this.resolvedArtifactSource = resolvedArtifactSource;
		this.protocol = protocol;
	}

	/**
	 * Get the ResolvedArtifactSource that this data source was created with.
	 * 
	 * @return
	 */
	public ResolvedArtifactSource getResolvedArtifactSource()
	{
		return this.resolvedArtifactSource;
	}

	/**
	 * Get the protocol that this data source was created with.
	 * 
	 * @return
	 */
	public String getProtocol()
	{
		return this.protocol;
	}

	/**
	 * Get the metadata URL that the ResolvedArtifactSource includes that uses the
	 * protocol.
	 * 
	 * @return
	 */
	public URL getMetadataUrl()
	{
		return resolvedArtifactSource.getMetadataUrl(getProtocol());		
	}

	/**
	 * Get the artifact URL that the ResolvedArtifactSource includes that uses the
	 * protocol.
	 * 
	 * @return
	 */
	public URL getArtifactUrl()
	{
		return resolvedArtifactSource.getArtifactUrl(getProtocol());		
	}
	
	/**
	 * Default implementation returns false always.
	 * 
	 * @see gov.va.med.imaging.datasource.VersionableDataSourceSpi#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() throws SecurityException
	{
		return false;
	}

	/**
	 * 
	 */
	public void setConfiguration(Object configuration)
	{
		this.configuration = configuration;
	}

	protected Object getConfiguration()
	{
		return this.configuration;
	}
}
