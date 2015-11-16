/**
 * 
 */
package gov.va.med.imaging.proxy.services;

import java.net.URL;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;

/**
 * Wraps a ResolvedArtifactSource along with a type and a protocol into
 * something that looks like a ProxyService. 
 * 
 * @author vhaiswbeckec
 *
 */
public class ResolvedArtifactSourceProxyService
extends AbstractProxyService
{
	private final ResolvedArtifactSource resolvedArtifactSource;
	private final String protocol;
	private final ProxyServiceType type;
	
	/**
	 * 
	 */
	public ResolvedArtifactSourceProxyService(
		ResolvedArtifactSource resolvedArtifactSource, 
		String protocol,
		ProxyServiceType type)
	{
		assert resolvedArtifactSource != null;
		assert protocol != null;
		
		this.resolvedArtifactSource = resolvedArtifactSource;
		this.protocol = protocol;
		this.type = type;
	}
	
	private URL getURL()
	{
		if(ProxyServiceType.metadata == getProxyServiceType())
			getResolvedArtifactSource().getMetadataUrl(getProtocol());
		else if(ProxyServiceType.image == getProxyServiceType())
			getResolvedArtifactSource().getArtifactUrl(getProtocol());
		
		return null;
	}
	
	public ResolvedArtifactSource getResolvedArtifactSource()
	{
		return this.resolvedArtifactSource;
	}
	
	public String getProtocol()
	{
		return this.protocol;
	}

	@Override
	public String getApplicationPath()
	{
		String path = getURL().getPath();
		if(path == null)
			return null;
		
		String[] pathElements = path.split("/");
		return pathElements[0];
	}

	@Override
	public String getOperationPath()
	{
		String path = getURL().getPath();
		if(path == null)
			return null;

		int firstSlash = path.indexOf('/');
		return firstSlash >= 0 ? path.substring(firstSlash) : null;
	}

	@Override
	public String getConnectionURL()
	{
		return getURL().toExternalForm();
	}

	@Override
	public Object getCredentials()
	{
		String userInfo = getURL().getUserInfo();
		if(userInfo == null)
			return null;
		String[] userInfoElements = userInfo.split(":");
		return userInfoElements.length >= 2 ? userInfoElements[1] : null;
	}

	@Override
	public String getHost()
	{
		return getURL().getHost();
	}

	@Override
	public int getPort()
	{
		return getURL().getPort();
	}

	@Override
	public ProxyServiceType getProxyServiceType()
	{
		return type;
	}

	@Override
	public String getUid()
	{
		String userInfo = getURL().getUserInfo();
		if(userInfo == null)
			return null;
		String[] userInfoElements = userInfo.split(":");
		return userInfoElements[0];
	}

}
