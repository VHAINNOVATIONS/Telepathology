/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;

import java.util.List;
import java.util.Map;

/**
 * The Principal class used in the Transaction context when running in a client application.
 * This class has more latitude by allowing changes to UID and PWD but is specifically excluded
 * from use in a server environment.
 * This class serves simply as a marker for the TransactionContext, it is nothing but a VistaRealmPrincipal
 * derivation (i.e. no overrides).
 * 
 * @author VHAISWBECKEC
 *
 */
public class ClientPrincipal 
extends VistaRealmPrincipal
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param realm
	 * @param authenticatedByDelegate
	 * @param credentialType
	 */
	public ClientPrincipal(String realm, boolean authenticatedByDelegate, AuthenticationCredentialsType credentialType)
	{
		super(realm, authenticatedByDelegate, credentialType);
	}

	public ClientPrincipal(
		String realm, boolean authenticatedByDelegate, AuthenticationCredentialsType credentialType, 
		String accessCode, String verifyCode, String duz, String fullName, String ssn, 
		String siteNumber, String siteName, 
		List<String> roles, Map<String, Object> applicationProperties)
	{
		super(
			realm, 
			authenticatedByDelegate, 
			credentialType, 
			accessCode, verifyCode, duz, fullName, ssn, siteNumber, siteName, 
			roles, 
			applicationProperties);
	}
}