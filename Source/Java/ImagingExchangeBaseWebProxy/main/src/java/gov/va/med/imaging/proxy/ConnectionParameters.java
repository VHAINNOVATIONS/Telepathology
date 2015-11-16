package gov.va.med.imaging.proxy;

import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.business.Site;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class ConnectionParameters
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public final static String defaultProtocol = "http";
	
	// default values for ViX and DOD image exchange applications and paths
	public final static String defaultDODXChangeApplication = "VaImagingExchange";
	public final static String defaultDODImageXChangePath = "/RetrieveImage.ashx";
	public final static String defaultDODMetadataXChangePath = "/ImageMetadataService.asmx";
	
	public final static String defaultImageXChangeApplication = "ImagingExchangeWebApp";
	public final static String defaultImageMetadataXChangePath = "/xchange-ws/ImageMetadata.V1";
	public final static String defaultImageXChangePath = "/xchange/xchange";
	
	/**
	 * Convenience factory method to create a connection using defualt values for the protocol, application, and path 
	 * 
	 * @param host
	 * @param port
	 * @param uid
	 * @param credentials
	 * @return
	 */
	public final static ConnectionParameters createVAMetadataConnectionParameters(String host, int port, String uid, Object credentials)
	{
		return new ConnectionParameters(defaultProtocol, host, port, defaultImageXChangeApplication, defaultImageMetadataXChangePath, uid, credentials);
	}
	
	/**
	 * Convenience factory method to create a connection using defualt values for the protocol, application, and path 
	 * 
	 * @param host
	 * @param port
	 * @param uid
	 * @param credentials
	 * @return
	 */
	public final static ConnectionParameters createVAImageConnectionParameters(String host, int port, String uid, Object credentials)
	{
		return new ConnectionParameters(defaultProtocol, host, port, defaultImageXChangeApplication, defaultImageXChangePath, uid, credentials);
	}
	
	/**
	 * Convenience factory method to create a connection using defualt values for the protocol, application, and path 
	 * 
	 * @param host
	 * @param port
	 * @param uid
	 * @param credentials
	 * @return
	 */
	public final static ConnectionParameters createDODMetadataConnectionParameters(String host, int port, String uid, Object credentials)
	{
		return new ConnectionParameters(defaultProtocol, host, port, defaultDODXChangeApplication, defaultDODMetadataXChangePath, uid, credentials);
	}
	
	/**
	 * Convenience factory method to create a connection using defualt values for the protocol, application, and path 
	 * 
	 * @param host
	 * @param port
	 * @param uid
	 * @param credentials
	 * @return
	 */
	public final static ConnectionParameters createDODImageConnectionParameters(String host, int port, String uid, Object credentials)
	{
		return new ConnectionParameters(defaultProtocol, host, port, defaultDODXChangeApplication, defaultDODImageXChangePath, uid, credentials);
	}
	
	public final static ConnectionParameters createMetadataConnectionParameters(Site site, String host, int port, String uid, Object credentials)
	{
		if(ExchangeUtil.isSiteDOD(site))
		{
			return createDODMetadataConnectionParameters(host, port, uid, credentials);
		}
		else
		{
			return createMetadataConnectionParameters(site, host, port, uid, credentials);
		}
	}
	
	public final static ConnectionParameters createImageConnectionParameters(Site site, String host, int port, String uid, Object credentials)
	{
		if(ExchangeUtil.isSiteDOD(site))
		{
			return createDODImageConnectionParameters(host, port, uid, credentials);
		}
		else
		{
			return createImageConnectionParameters(site, host, port, uid, credentials);
		}
	}
	
	// ============================================================================================================================================
	// Instance Fields, Constructors and Methods
	// ============================================================================================================================================
	private String protocol;
	private String host;
	private int port;
	private String application; 
	private String path;
	private String uid;
	private Object credentials;
	
	/**
	 * An enum used when sending change notifications
	 */
	public enum FIELDS
	{
		all, protocol, host, port, application, path, uid, credentials;
	}
	
	public ConnectionParameters()
	{
		super();
	}
	
	public ConnectionParameters(
			String protocol, 
			String host, 
			int port, 
			String application, 
			String path,
			String uid,
			Object credentials)
	{
		super();
		setParameters(protocol, host, port, application, path, uid, credentials);
	}
	
	public void setParameters(			
			String protocol, 
			String host, 
			int port, 
			String application, 
			String path,
			String uid,
			Object credentials)
	{
		setProtocol(protocol);
		setHost(host);
		setPort(port);
		setApplication(application);
		setPath(path);
		setUid(uid);
		setCredentials(credentials);
	}

	/**
	 * Copy the fields from the given ConnectionParameters instance to this instance
	 * 
	 * @param parameters
	 */
	public void setParameters(ConnectionParameters parameters)
	{
		this.setParameters(
			parameters.getProtocol(), 
			parameters.getHost(), 
			parameters.getPort(), 
			parameters.getApplication(), 
			parameters.getPath(), 
			parameters.getUid(), 
			parameters.getCredentials() 
		);
	}
	
	public String getApplication()
	{
		return this.application;
	}
	public void setApplication(String imageMetadataApplication)
	{
		this.application = imageMetadataApplication;
		notifyConnectionParameterChangeEventListeners(FIELDS.application);
	}
	
	public String getHost()
	{
		return this.host;
	}
	public void setHost(String imageMetadataHost)
	{
		this.host = imageMetadataHost;
		notifyConnectionParameterChangeEventListeners(FIELDS.host);
	}
	
	public String getPath()
	{
		return this.path;
	}
	public void setPath(String imageMetadataPath)
	{
		this.path = imageMetadataPath;
		notifyConnectionParameterChangeEventListeners(FIELDS.path);
	}
	
	public int getPort()
	{
		return this.port;
	}
	public void setPort(int imageMetadataPort)
	{
		this.port = imageMetadataPort;
		notifyConnectionParameterChangeEventListeners(FIELDS.port);
	}
	
	public String getProtocol()
	{
		return this.protocol;
	}
	public void setProtocol(String imageMetadataProtocol)
	{
		this.protocol = imageMetadataProtocol;
		notifyConnectionParameterChangeEventListeners(FIELDS.protocol);
	}
	
	public Object getCredentials()
	{
		return this.credentials;
	}

	public void setCredentials(Object credentials)
	{
		this.credentials = credentials;
		notifyConnectionParameterChangeEventListeners(FIELDS.credentials);
	}

	public String getUid()
	{
		return this.uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
		notifyConnectionParameterChangeEventListeners(FIELDS.uid);
	}

	public String getConnectionURL()
	{
		return getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getApplication() + getPath();
	}
	
	/**
	 * Clear all of the connection parameters
	 */
	public void clear()
	{
		this.setProtocol("http");
		this.setHost("");
		this.setPort(8080);
		this.setUid("");
		this.setCredentials("");
		this.setApplication("");
		this.setPath("");
	}
	
	/**
	 * Make ourselves a duplicate of the given ConnectionParameters instance
	 * 
	 * @param clinicalDisplayMetadataParameters
	 */
	public void duplicate(ConnectionParameters that)
	{
		this.setProtocol(that.getProtocol());
		this.setHost(that.getHost());
		this.setPort(that.getPort());
		this.setUid(that.getUid());
		this.setCredentials(that.getCredentials());
		this.setApplication(that.getApplication());
		this.setPath(that.getPath());
	}
	
	// ====================================================================================================================
	// Listener management
	// ====================================================================================================================
	// note that the Listener list is NOT synchronized
	private transient List<ConnectionParameterChangeEventListener> listeners = new ArrayList<ConnectionParameterChangeEventListener>();

	public void addConnectionParameterChangeEventListener(ConnectionParameterChangeEventListener listener)
	{
		synchronized(listeners)
		{
			listeners.add(listener);
		}
		//System.out.println(this.hashCode() + ".addConnectionParameterChangeEventListener(" + listener.toString() + "), " + listeners.size() + " listeners.");
	}
	
	public void removeConnectionParameterChangeEventListener(ConnectionParameterChangeEventListener listener)
	{
		synchronized(listeners)
		{
			listeners.remove(listener);
		}
		//System.out.println(this.hashCode() + ".removeConnectionParameterChangeEventListener(" + listener.toString() + ")" + listeners.size() + " listeners.");
	}
	
	protected void notifyConnectionParameterChangeEventListeners(FIELDS field)
	{
		notifyConnectionParameterChangeEventListeners( new ConnectionParameterChangeEvent(this, field) );
	}

	protected void notifyConnectionParameterChangeEventListeners(ConnectionParameterChangeEvent event)
	{
		//System.out.println(this.hashCode() + ".notifyConnectionParameterChangeEventListeners(" + event.toString() + ")");
		synchronized(listeners)
		{
			//System.out.println(this.hashCode() + ".notifyConnectionParameterChangeEventListeners, notifying " + listeners.size() + " listeners.");
			for(ConnectionParameterChangeEventListener listener : listeners )
				listener.connectionParameterChange(event);
		}
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
		final ConnectionParameters other = (ConnectionParameters) obj;
		if (this.application == null)
		{
			if (other.application != null)
				return false;
		} 
		else if (!this.application.equals(other.application))
			return false;
		
		if (this.credentials == null)
		{
			if (other.credentials != null)
				return false;
		} 
		else if (!this.credentials.equals(other.credentials))
			return false;
		
		if (this.host == null)
		{
			if (other.host != null)
				return false;
		} 
		else if (!this.host.equals(other.host))
			return false;
		
		if (this.path == null)
		{
			if (other.path != null)
				return false;
		} 
		else if (!this.path.equals(other.path))
			return false;
		
		if (this.port != other.port)
			return false;
		
		if (this.protocol == null)
		{
			if (other.protocol != null)
				return false;
		} 
		else if (!this.protocol.equals(other.protocol))
			return false;
		
		if (this.uid == null)
		{
			if (other.uid != null)
				return false;
		} 
		else if (!this.uid.equals(other.uid))
			return false;
		
		return true;
	}
	
	/**
	 * The clone method DOES NOT duplicate the Listeners
	 */
	@Override
	public Object clone()
	{
		return new ConnectionParameters( getProtocol(), getHost(), getPort(), getApplication(), getPath(), getUid(), getCredentials() );
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.protocol);
		sb.append("://");
		sb.append(this.uid);
		sb.append("@");
		sb.append(this.host);
		sb.append(":");
		sb.append(this.port);
		sb.append("/");
		sb.append(this.application);
		sb.append(this.path);
		
		return sb.toString();
	}
}
