/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 19, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.server.tomcat;

import gov.va.med.imaging.tomcat.vistarealm.RealmErrorContext;

import java.io.IOException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.log4j.Logger;


/**
 * This extends the Basic authentication valve for Tomcat and checks for values on thread local to add as response headers.
 * This allows a client to know why an authentication failed
 * 
 * @author VHAISWWERFEJ
 *
 */
public class TomcatBasicAuthenticatorValve
extends org.apache.catalina.authenticator.BasicAuthenticator
{
	private final static Logger logger = Logger.getLogger(TomcatBasicAuthenticatorValve.class);
	public final static String httpHeaderAuthenticateErrorMessage = "xxx-authenticate-error-message";
	public final static String httpHeaderAuthenticateErrorName = "xxx-authenticate-error-name";
	
	
	public TomcatBasicAuthenticatorValve()
	{
		super();
		logger.info("TomcatBasicAuthenticatorValve <ctor>");
	}

	@Override
	public boolean authenticate(Request request, Response response, LoginConfig loginConfig)
			throws IOException
	{
		// must clear before calling to ensure not using a previous attempts data (since threads are recycled)
		RealmErrorContext.clear();
		logger.debug("TomcatBaiscAuthenticatorValve.authenticate(Request, Response, LoginConfig)");
		boolean result = super.authenticate(request, response, loginConfig);
		if(!result)
		{						
			String errorMessage = RealmErrorContext.getProperty(RealmErrorContext.realmErrorContextExceptionMessage);
			String exceptionClass = RealmErrorContext.getProperty(RealmErrorContext.realmErrorContextExceptionName);
			logger.debug("authentication failed with error '" + (exceptionClass == null ? "<null>" : exceptionClass) + "' and message '" + (errorMessage == null ? "<null>" : errorMessage) + "'.");
			if(errorMessage != null)
				response.addHeader(httpHeaderAuthenticateErrorMessage, errorMessage);
			if(exceptionClass != null)
				response.addHeader(httpHeaderAuthenticateErrorName, exceptionClass);
		}
		RealmErrorContext.unsetRealmErrorContext();
		return result;
	}

}
