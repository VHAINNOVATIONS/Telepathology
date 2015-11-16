/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 28, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.proxy.services;

/**
 * Abstract implementation of ProxyService which provides the properties and getters needed by a ProxyService.
 * Data Source ProxyService implementations should extend this abstract ProxyService if possible (although it
 * is not required). Implementations should not assume use of the AbstractProxyService but only the ProxyService 
 * interface.
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractProxyService 
implements ProxyService 
{
	protected String applicationPath;
	protected Object credentials;
	protected String host;
	protected int port;
	protected String operationPath;
	protected String protocol;
	protected String uid;
	protected ProxyServiceType proxyServiceType;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getApplicationPath()
	 */
	@Override
	public String getApplicationPath() 
	{
		return applicationPath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getCredentials()
	 */
	@Override
	public Object getCredentials() 
	{
		return credentials;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getHost()
	 */
	@Override
	public String getHost() 
	{
		return host;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getOperationPath()
	 */
	@Override
	public String getOperationPath() 
	{
		return operationPath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getPort()
	 */
	@Override
	public int getPort() 
	{
		return port;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getProtocol()
	 */
	@Override
	public String getProtocol() 
	{
		return protocol;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getProxyServiceType()
	 */
	@Override
	public ProxyServiceType getProxyServiceType() 
	{
		return proxyServiceType;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getUid()
	 */
	@Override
	public String getUid() 
	{
		return uid;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.services.ProxyService#getConnectionURL()
	 */
	@Override
	public String getConnectionURL() 
	{
		String appPath = getApplicationPath();
		String operationPath = getOperationPath();
		
		boolean containsSlash = false;
		
		// if the application path and the operation path do not contain a slash, put it in
		if(appPath.endsWith("/"))
		{
			containsSlash = true;
		}
		else if(operationPath.startsWith("/"))
		{
			containsSlash = true;
		}
		
		StringBuilder url = new StringBuilder();
		url.append(getProtocol());
		url.append("://");
		url.append(getHost());
		url.append(":");
		url.append(getPort());
		url.append("/");
		url.append(getApplicationPath());
		if(!containsSlash)
		{
			url.append("/");
		}
		url.append(getOperationPath());
		
		
		//return getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getApplicationPath() + getOperationPath();
		return url.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getProtocol());
		sb.append("://");
		sb.append(this.getUid());
		sb.append("@");
		sb.append(this.getHost());
		sb.append(":");
		sb.append(this.getPort());
		sb.append("/");
		sb.append(this.getApplicationPath());
		sb.append(this.getOperationPath());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((applicationPath == null) ? 0 : applicationPath.hashCode());
		result = prime * result
				+ ((credentials == null) ? 0 : credentials.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result
				+ ((operationPath == null) ? 0 : operationPath.hashCode());
		result = prime * result + port;
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
		result = prime
				* result
				+ ((proxyServiceType == null) ? 0 : proxyServiceType.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractProxyService other = (AbstractProxyService) obj;
		if (applicationPath == null) {
			if (other.applicationPath != null)
				return false;
		} else if (!applicationPath.equals(other.applicationPath))
			return false;
		if (credentials == null) {
			if (other.credentials != null)
				return false;
		} else if (!credentials.equals(other.credentials))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (operationPath == null) {
			if (other.operationPath != null)
				return false;
		} else if (!operationPath.equals(other.operationPath))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (proxyServiceType == null) {
			if (other.proxyServiceType != null)
				return false;
		} else if (!proxyServiceType.equals(other.proxyServiceType))
			return false;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
}
