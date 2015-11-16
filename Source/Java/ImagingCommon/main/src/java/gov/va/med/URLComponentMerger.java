/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Oct 14, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author vhaiswbeckec
 *
 */
public class URLComponentMerger
implements Serializable
{
	private static final long serialVersionUID = 1L;

	public enum URLComponentMergerPrecedence
	implements Serializable
	{
		URLFirst, 					// use the URL components, add the merger components when the URL component does not exist
		URLFirstOverrideProtocol,	// always use the merger protocol, otherwise use the URL components, add the merger components when the URL component does not exist
		MergerComponentsFirst		// add or override all URL components with the merger components that exist
	}
	
	private final String protocol;
	private final String username;
	private final String password;
	private final String host;
	private final int port;
	private final String file;
	private final URLComponentMergerPrecedence precedence;
	
	/**
	 * Create an instance to merge the components into
	 * URLs.  The precedence parameter controls whether the 
	 * URL or the fields in this class take precedence.
	 * Any String fields that are null will not be merged, the URL
	 * value will always be used.
	 * If the 'port' field is less than 0 then the URL value will be used
	 * or no value if the URL has no port specified.
	 * 
	 * @param protocol
	 * @param username
	 * @param password
	 * @param host
	 * @param port
	 * @param file
	 * @param precedence
	 */
	public URLComponentMerger(
		String protocol, 
		String username, 
		String password, 
		String host, 
		int port, 
		String file,
		URLComponentMergerPrecedence precedence)
	{
		super();
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.file = file;
		this.precedence = precedence;
	}
	
	
	/**
	 * @return the protocol
	 */
	public String getProtocol()
	{
		return this.protocol;
	}

	private final static String utf8 = "UTF-8"; 

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return this.username;
	}

	/**
	 * return the username URL (UTF-8) encoded (if the username is not null)
	 * @return
	 */
	private String getUsernameEncoded()
	{
		if(getUsername() == null)
			return null;
		try
		{
			return URLEncoder.encode(getUsername(), utf8);
		}
		catch (UnsupportedEncodingException e)
		{
			return getUsername();
		}
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return this.password;		
	}
	
	/**
	 * return the password URL (UTF-8) encoded (if the password is not null)
	 * @return
	 */
	private String getPasswordEncoded()
	{
		if(getPassword() == null)
			return null;
		try
		{
			return URLEncoder.encode(getPassword(), utf8);
		}
		catch (UnsupportedEncodingException e)
		{
			return getPassword();
		}
	}


	/**
	 * @return the host
	 */
	public String getHost()
	{
		return this.host;
	}


	/**
	 * @return the port
	 */
	public int getPort()
	{
		return this.port;
	}


	/**
	 * @return the file
	 */
	public String getFile()
	{
		return this.file;
	}


	/**
	 * @return the precedence
	 */
	public URLComponentMergerPrecedence getPrecedence()
	{
		return this.precedence;
	}


	/**
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException 
	 */
	public URL merge(URL url) 
	throws MalformedURLException
	{
		StringBuilder sb = new StringBuilder();
		
		// the protocol
		sb.append(selectProtocol(url));
		sb.append("://");
		
		// the user ID and password
		String userInfo = selectUserInfo(url);
		if(userInfo != null && userInfo.length() > 0)
		{
			sb.append(userInfo);
			sb.append('@');
		}
		
		// the host s a required field
		sb.append( selectHost(url) ); 

		// the port is not a required field
		String selectedPort = selectPort(url);
		if(selectedPort != null && selectedPort.length() > 0)
			sb.append( ":" + selectedPort ); 

		String selectedFile = selectFile(url);
		if(selectedFile != null && selectedFile.length() > 0)
			sb.append( "/" + selectedFile ); 
		
		URL fixedUpUrl = new URL( sb.toString() );
		return fixedUpUrl;
	}

	/**
	 * File is not a required field, this method may return null.
	 * 
	 * @return
	 */
	private String selectFile(URL url)
	{
		String selected = null;
		switch(getPrecedence())
		{
		case MergerComponentsFirst:
			selected = 
				getFile() != null ? getFile() :
				url.getFile() != null && url.getFile().length() > 0 ? url.getFile() : null;
			break;
		case URLFirst:
		case URLFirstOverrideProtocol:
			selected = 
				url.getFile() != null && url.getFile().length() > 0 ? url.getFile() : 
				getFile() != null ? getFile() : null;
			break;
		}
		
		// remove the starting slash if it exists
		if(selected != null && selected.length() > 0 && selected.startsWith("/"))
			selected = selected.substring(1);
		
		return selected;
	}


	/**
	 * Port is not a required field, this method may return null.
	 * Note that the merger component port will override the URL port
	 * if the mode is URLFirstOverrideProtocol.
	 * 
	 * @param url
	 * @return
	 */
	private String selectPort(URL url)
	{
		String selected = null;
		switch(getPrecedence())
		{
		case MergerComponentsFirst:
			selected = 
				getPort() >= 0 ? Integer.toString(getPort()) :
				url.getPort() >= 0 ? Integer.toString(url.getPort()) : null;
			break;
		case URLFirst:
			selected = 
				url.getPort() >= 0 ? Integer.toString(url.getPort()) :
				getPort() >= 0 ? Integer.toString(getPort()) : null;
			break;
		case URLFirstOverrideProtocol:
			selected = 
				getPort() >= 0 ? Integer.toString(getPort()) :
				url.getPort() >= 0 ? Integer.toString(url.getPort()) : null;
			break;
		}
		return selected;
	}


	/**
	 * The host is a required field, this method will never return null.
	 * 
	 * @param url
	 * @return
	 */
	private String selectHost(URL url)
	{
		String selected = null;
		switch(getPrecedence())
		{
		case MergerComponentsFirst:
			selected = getHost() == null ? url.getHost() : getHost();
			break;
		case URLFirst:
		case URLFirstOverrideProtocol:
			selected = url.getHost();
		}
		return selected;
	}


	/**
	 * UserInfo is not a required field, this method may return null.
	 * 
	 * @return
	 */
	private String selectUserInfo(URL url)
	{
		StringBuilder sbUserId = new StringBuilder();
		String urlUserInfo = url.getUserInfo();
		String[] urlUserInfoComponents = 
			urlUserInfo != null && urlUserInfo.length() > 0 ?
			urlUserInfo.split(":") : new String[]{null, null};
		String urlUserId = urlUserInfoComponents[0];
		String urlUserPassword = urlUserInfoComponents.length > 1 ? urlUserInfoComponents[1] : null;
		
		switch(getPrecedence())
		{
		case MergerComponentsFirst:
			if( getUsername() != null && getUsername().length() > 0 )
				sbUserId.append( getUsernameEncoded() );
			else if(urlUserId != null && urlUserId.length() > 0)
				sbUserId.append( urlUserId );
				
			// don't add the password if the user ID isn't specified
			if( sbUserId.length() > 0 )
				if(getPassword() != null && getPassword().length() > 0)
				{
					sbUserId.append( ':' );
					sbUserId.append(getPasswordEncoded());
				}
				else if(urlUserPassword != null && urlUserPassword.length() > 0)
				{
					sbUserId.append( ':' );
					sbUserId.append(urlUserPassword);
				}
			break;
			
		case URLFirst:
		case URLFirstOverrideProtocol:
			if(urlUserId != null && urlUserId.length() > 0)
				sbUserId.append( urlUserId );
			else if( getUsername() != null && getUsername().length() > 0 )
				sbUserId.append( getUsernameEncoded() );
				
			// don't add the password if the user ID isn't specified
			if( sbUserId.length() > 0 )
				if(urlUserPassword != null && urlUserPassword.length() > 0)
				{
					sbUserId.append( ':' );
					sbUserId.append(urlUserPassword);
				}
				else if(getPassword() != null && getPassword().length() > 0)
				{
					sbUserId.append( ':' );
					sbUserId.append(getPasswordEncoded());
				}
			break;
		}

		return sbUserId.length() > 0 ? sbUserId.toString() : null;
	}

	/**
	 * Determine which protocol to use, the protocol is a required field,
	 * this method will never return null.
	 * 
	 * @param url
	 * @return
	 */
	private String selectProtocol(URL url)
	{
		String selectedProtocol = null;
		switch(getPrecedence())
		{
		case MergerComponentsFirst:
			selectedProtocol = getProtocol() == null ? url.getProtocol() : getProtocol();
			break;
		case URLFirst:
			selectedProtocol = url.getProtocol();
			break;
		case URLFirstOverrideProtocol:
			selectedProtocol = getProtocol() == null ? url.getProtocol() : getProtocol();
			break;
		}
		return selectedProtocol;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.getPrecedence() + "(" +
			this.getProtocol() + "://" + 
			this.getUsername() + ":" +
			this.getPassword() + "@" +
			this.getHost() + ":" + 
			this.getPort() + "/" + 
			this.getFile();
	}

	// ==========================================================================
	// Generated hashCode() and equals()
	// ==========================================================================
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.file == null) ? 0 : this.file.hashCode());
		result = prime * result + ((this.host == null) ? 0 : this.host.hashCode());
		result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
		result = prime * result + this.port;
		result = prime * result + ((this.precedence == null) ? 0 : this.precedence.hashCode());
		result = prime * result + ((this.protocol == null) ? 0 : this.protocol.hashCode());
		result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final URLComponentMerger other = (URLComponentMerger) obj;
		if (this.file == null)
		{
			if (other.file != null)
				return false;
		}
		else if (!this.file.equals(other.file))
			return false;
		if (this.host == null)
		{
			if (other.host != null)
				return false;
		}
		else if (!this.host.equals(other.host))
			return false;
		if (this.password == null)
		{
			if (other.password != null)
				return false;
		}
		else if (!this.password.equals(other.password))
			return false;
		if (this.port != other.port)
			return false;
		if (this.precedence == null)
		{
			if (other.precedence != null)
				return false;
		}
		else if (!this.precedence.equals(other.precedence))
			return false;
		if (this.protocol == null)
		{
			if (other.protocol != null)
				return false;
		}
		else if (!this.protocol.equals(other.protocol))
			return false;
		if (this.username == null)
		{
			if (other.username != null)
				return false;
		}
		else if (!this.username.equals(other.username))
			return false;
		return true;
	}
	
	
}
