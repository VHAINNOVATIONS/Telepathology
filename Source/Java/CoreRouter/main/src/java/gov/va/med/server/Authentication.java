/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 6, 2010
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

package gov.va.med.server;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public class Authentication
{
	// The name of the property that must match the siteId passed to us
	private static final String SITE_NUMBER_PROPERTY = "siteNumber";
	private static Logger logger = Logger.getLogger(Authentication.class);
	
	/**
	 * 
	 * @param siteId
	 * @param accessCode
	 * @param verifyCode
	 * @return
	 */
	public static Principal authenticate(String siteId, String accessCode, String verifyCode)
	{
		ServerAuthentication authentication = ServerAdapterImpl.getSingleton().getServerAuthentication();
		if(authentication == null)
		{
			logger.error("Failed to acquire a reference to the authentication server adapter.");
			return null;
		}
		
		SecurityRealmIdentification realmIdentification = createSecurityRealmIdentification(siteId);
		Principal principal = 
			authentication.authenticate(realmIdentification, accessCode, verifyCode.getBytes());
		
		return principal;
	}
	
	/**
	 * 
	 * @param siteId
	 * @param certs
	 * @return
	 */
	public static Principal authenticate(String siteId, X509Certificate[] certs)
	{
		ServerAuthentication authentication = ServerAdapterImpl.getSingleton().getServerAuthentication();
		if(authentication == null)
		{
			logger.error("Failed to acquire a reference to the authentication server adapter.");
			return null;
		}
		
		SecurityRealmIdentification realmIdentification = createSecurityRealmIdentification(siteId);
		Principal principal = 
			authentication.authenticate(realmIdentification, certs);
		
		return principal;
	}
	
	/**
	 * 
	 * @param siteId
	 * @param username
	 * @param clientDigest
	 * @param nc
	 * @param nOnce
	 * @param cnonce
	 * @param qop
	 * @param realm
	 * @param md5a2
	 * @return
	 */
	public static Principal authenticate(
		String siteId, 
		String username, 
		String clientDigest, 
		String nc, 
		String nOnce, 
		String cnonce, 
		String qop, 
		String realm, 
		String md5a2)
	{
		ServerAuthentication authentication = ServerAdapterImpl.getSingleton().getServerAuthentication();
		if(authentication == null)
		{
			logger.error("Failed to acquire a reference to the authentication server adapter.");
			return null;
		}
		
		SecurityRealmIdentification realmIdentification = createSecurityRealmIdentification(siteId);
		Principal principal = 
			authentication.authenticate(realmIdentification, username, clientDigest, nc, nOnce, cnonce, qop, realm, md5a2); 		
		return principal;
	}
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	private static SecurityRealmIdentification createSecurityRealmIdentification(String siteId)
	{
		Map<String, Object> realmIdentificationProperties = new HashMap<String, Object>();
		realmIdentificationProperties.put(SITE_NUMBER_PROPERTY, siteId);
		//SecurityRealmIdentification realmIdentification = new SecurityRealmIdentification((Class<?>)null, realmIdentificationProperties);
		SecurityRealmIdentification realmIdentification =
			new SecurityRealmIdentification("gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm",
				realmIdentificationProperties);

		return realmIdentification;
	}
}
