/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Nov 11, 2010
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

package gov.va.med.imaging.tomcat.authentication;

import java.io.IOException;
import java.security.Principal;
import javax.servlet.ServletException;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 * 
 */
public class ServiceAccountAuthenticator
extends org.apache.catalina.valves.ValveBase
implements Valve
{
	protected static final String info = "gov.va.med.imaging.tomcat.authentication.ServiceAccountAuthenticator/1.0";

	private transient Logger logger;
	
	// Service account identification properties
	private String userId = null;
	private String password = null;
	
	public ServiceAccountAuthenticator()
	{
		getLogger().info("Created " + getInfo());
	}

	@Override
	public String getInfo()
	{
		return info;
	}

	private synchronized Logger getLogger()
	{
		if(logger == null)
			logger = Logger.getLogger(this.getClass());
		return logger;
	}

	/**
	 * @return the userid
	 */
	public String getUserId()
	{
		return this.userId;
	}
	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return this.password;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserId(String userid)
	{
		this.userId = userid;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * @see org.apache.catalina.valves.ValveBase#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request request, Response response) 
	throws IOException, ServletException
	{
		String username = getUserId();
		String password = getPassword();
		getLogger().info("ServiceAccountAuthenticator.authenticate (" + username + ", <password not shown>)");
		Principal principal = getContainer().getRealm().authenticate(username, password);
		if (principal != null)
		{
			getNext().invoke(request, response);
		}
		else
		{
			response.setStatus(401);
			response.setError();
		}
	}
}
