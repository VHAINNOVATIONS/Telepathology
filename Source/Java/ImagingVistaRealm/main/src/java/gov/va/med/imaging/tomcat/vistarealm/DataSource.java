/*
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author VHAANNGilloJ
 * 
 */
public class DataSource
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_PROTOCOL = "vista";

	public enum Status
	{
		NONE, ACTIVE, INACTIVE, REFUSED, TIMEOUT, UNKNOWN;
	}

	private String connectionString;
	private String protocol;
	private String modality;
	private String provider;
	private String source;
	private String uid;
	private String password;
	private String context = VistaConnection.DEFAULT_CONTEXT;
	private String vendor;
	private String version;
	private Status status = Status.NONE;
	private String description;
	private int port = VistaConnection.DEFAULT_PORT;
	private int timeout = VistaConnection.DEFAULT_TIMEOUT;

	// private Site site;

	public DataSource()
	{
	}

	/**
	 * Parse the components of the connection from a URL where: 
	 * 1.) the protocol, host and port are the analogous portions of the URL 
	 * 2.) the UID and Password are parsed from the user info assuming the form "<uid>:<password>" 
	 * 3.) the context is encoded as the URL path
	 * 4.) the modality is encoded as the reference (anchor)
	 * 
	 * @param vistaUrl
	 */
	public DataSource(URL vistaUrl)
	{
		this.protocol = vistaUrl.getProtocol();
		this.source = vistaUrl.getHost();
		this.port = vistaUrl.getPort();
		if (vistaUrl.getUserInfo() != null)
		{
			String[] userInfoComponents = vistaUrl.getUserInfo().split(":");
			this.uid = userInfoComponents[0];
			this.password = userInfoComponents.length > 0 ? userInfoComponents[1] : null;
		}
		else
		{
			this.uid = null;
			this.password = null;
		}
		this.context = vistaUrl.getPath();
		this.modality = vistaUrl.getRef();
	}

	public DataSource(String source, int port, String protocol, String modality, String uid, String password,
		String context)
	{
		this.source = source;
		this.port = port;
		this.protocol = protocol;
		this.modality = modality;
		this.uid = uid;
		this.password = password;
		this.context = context;
	}

	public void setConnectionString(String connStr)
	{
		this.connectionString = connStr;
	}

	public String getConnectionString()
	{
		return connectionString;
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	public String getProvider()
	{
		return provider;
	}

	public void setModality(String modality)
	{
		this.modality = modality;
	}

	public String getModality()
	{
		return modality;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getSource()
	{
		return source;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getUid()
	{
		return uid;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return password;
	}

	public void setContext(String context)
	{
		this.context = context;
	}

	public String getContext()
	{
		return context;
	}

	public void setVendor(String vendor)
	{
		this.vendor = vendor;
	}

	public String getVendor()
	{
		return vendor;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getVersion()
	{
		return version;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public void setStatus(String status)
	{
		Status newStatus = Status.valueOf(status.toUpperCase());
		if (newStatus != null)
			this.status = newStatus;
		else
			this.status = Status.NONE;
	}

	public Status getStatus()
	{
		return status;
	}

	public String getStatusString()
	{
		return getStatus().toString();
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getPort()
	{
		return port;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public int getTimeout()
	{
		return timeout;
	}

	// public void setSite(Site site) {this.site = site;}
	// public Site getSite() {return site;}

	public String toString()
	{

		if (this.connectionString != null)
			return this.connectionString;

		StringBuilder sb = new StringBuilder();
		if (this.getModality() != null)
		{
			sb.append( "Modality: " );
			sb.append( this.getModality() );
		}
		if (this.getProtocol() != null)
		{
			if(sb.length() > 0) sb.append(", ");
			sb.append( "Protocol: " );
			sb.append( this.getProtocol() );
		}
		if (this.source != null)
		{
			if(sb.length() > 0) sb.append(", ");
			sb.append( "Source: " );
			sb.append( this.getSource() );
		}
		if (this.port != -1)
		{
			if(sb.length() > 0) sb.append(", ");
			sb.append( "Port: " );
			sb.append( this.getPort() );
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String toUrlString()
	{
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(getProtocol());
		sbUrl.append("://");
		if(getUid() != null || getPassword() != null)
		{
			sbUrl.append(getUid());
			if(getPassword() != null)
			{
				sbUrl.append(':');
				sbUrl.append(getPassword());
			}
			sbUrl.append('@');
		}
		sbUrl.append(getSource());
		sbUrl.append(':');
		sbUrl.append(getPort());

		sbUrl.append('/');
		sbUrl.append(getContext() == null ? "" : getContext());
		
		if(getModality() != null)
		{
			sbUrl.append('#');
			sbUrl.append(getModality());
		}

		return sbUrl.toString();
	}
	
	/**
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public URL toUrl() 
	throws MalformedURLException
	{
		return new URL( toUrlString() );
	}
}
