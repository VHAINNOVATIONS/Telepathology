package gov.va.med.imaging.tomcat.vistarealm;

import java.security.Principal;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Container;
import org.apache.catalina.Realm;
import org.apache.log4j.Logger;

/**
 * This class implements a Tomcat Realm that accepts only X.509 certificates.
 * Otherwise it is similar to the VistaRealm in that the created Principal 
 * instances are VistaRealmPrincipal and compatible with the TransactionContext
 * mechanism in VIX. 
 * 
 * This realm implementation is intended to be used when a service account is
 * available for the local VistA installation. 
 *  
 * This Realm will NOT delegate authentication to its container parent realm like
 * VistaRealm does.
 * 
 * Portions of this code and the comments are copied verbatim from
 * Tomcat/Catalina source.
 * 
 * A quick discussion of Realm calling sequence in Tomcat (or at least how I
 * think they work). -startup- 1.) constructor() 2.) setContainer() 3.)
 * MBeanRegistration.preRegister() 4.) MBeanRegistration.postRegister() 5.)
 * Lifecycle.start() 6.) backgroundProcess() runs periodically from here to
 * Lifecycle.stop()
 * 
 * -on client call- 1.) findSecurityConstraints() - determines if the web.xml
 * file has defined security-constraint elements for the resource should return
 * an array of applicable constraints (in descending order of specificity) 2.)
 * hasUserDataPermission() - to check the web.xml specified requirements for
 * data integrity and security in transmission 3.) authenticate() - depending on
 * the presented credentials, may call one of the four authenticate methods if
 * the user exists, should return a Principal realization 4.)
 * hasResourcePermission() - determines if the authenticated user has permission
 * to the specific resource named - on server stop - 1.) Lifecycle.stop()
 * 
 * Initialization Sequence:
 * 
 * =========================================================================
 * server.xml Realm element example with just required properties specified
 * <Realm
 * className="gov.va.med.imaging.tomcat.vistarealm.CertificateRealm"
 * siteNumber = "660"
 * siteAbbreviation = "SLC"
 * siteName = "Salt Lake City, UT"
 * serviceAccountUID="userId"
 * serviceAccountPWD="password"
 * />
 * 
 * =========================================================================
 * server.xml Realm element example with all properties specified
 * <Realm
 * className="gov.va.med.imaging.tomcat.vistarealm.CertificateRealm"
 * siteNumber="660"
 * siteAbbreviation="SLC"
 * siteName="Salt Lake City, UT"
 * usingPrincipalCache="true"
 * principalCacheLifespan="60000"
 * refreshPrincipalCacheEntryOnUse="true"
 * vistaConnectDelayKludge="1000"
 * serviceAccountUID="userId"
 * serviceAccountPWD="password"
 * />
 * 
 * @author VHAISWBECKEC
 * 
 */
public class CertificateRealm
extends AbstractVistaRealmImpl
implements Realm, org.apache.catalina.Lifecycle, AbstractVistaRealm, CertificateRealmMBean
{
	// Known Roles are now defined in the VistaRealmRoles Enum in the
	// VistaRealmClient project.
	// Partially this was for a code cleanup, and partially to make them
	// available
	// outside of the realm itself.

	private Logger logger = Logger.getLogger(this.getClass());
	
	private Container parentContainer;
	private Realm parentContainerRealm;

	private String serviceAccountUID;
	private String serviceAccountPWD;
	private List<VistaRealmRoles> serviceAccountRoles;
	
	/**
	 * 
	 */
	public CertificateRealm()
	{
		logger.info(CertificateRealm.class.getCanonicalName() + " ctor()");
	}
	
	public synchronized Container getParentContainer()
	{
		if(parentContainer == null)
			parentContainer = getContainer() == null ? null : getContainer().getParent();
		
		return parentContainer;
	}
	
	public synchronized Realm getParentContainerRealm()
	{
		if(parentContainerRealm == null)
		{
			Container parentContainer = getParentContainer();
			parentContainerRealm = parentContainer == null ? null : parentContainer.getRealm();
		}
		
		return parentContainerRealm;
	}	
	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getSiteAbbreviation()
	 */
	@Override
	public String getSiteAbbreviation() 
	{
		// if the site abbreviation has not been set, attempt to get it from the parent VistaAccessVerifyRealm 
		if(super.getSiteAbbreviation() == null)
		{
			logger.debug("Realm site abbreviation is null, attempting to set from parent");
			Realm parentRealm = getParentContainerRealm();
			if(parentRealm instanceof gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm)
			{				
				gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm accessVerifyRealm = (gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm)parentRealm;
				logger.debug("Setting site abbreviation from parent VistaAccessVerifyRealm to [" + accessVerifyRealm.getSiteAbbreviation() + "]");
				this.setSiteAbbreviation(accessVerifyRealm.getSiteAbbreviation());
			}
		}
		return super.getSiteAbbreviation();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getSiteName()
	 */
	@Override
	public String getSiteName() 
	{
		// if the site name has not been set, attempt to get it from the parent VistaAccessVerifyRealm
		if(super.getSiteName() == null)
		{
			logger.debug("Realm site name is null, attempting to set from parent");
			Realm parentRealm = getParentContainerRealm();
			if(parentRealm instanceof gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm)
			{				
				gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm accessVerifyRealm = (gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm)parentRealm;
				logger.debug("Setting site name from parent VistaAccessVerifyRealm to [" + accessVerifyRealm.getSiteName() + "]");
				this.setSiteName(accessVerifyRealm.getSiteName());
			}
		}
		return super.getSiteName();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getSiteNumber()
	 */
	@Override
	public String getSiteNumber() 
	{
		// if the site number has not been set, attempt to get it from the parent VistaAccessVerifyRealm
		if(super.getSiteNumber() == null)
		{
			logger.debug("Realm site number is null, attempting to set from parent");
			Realm parentRealm = getParentContainerRealm();
			if(parentRealm instanceof gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm)
			{				
				gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm accessVerifyRealm = (gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm)parentRealm;
				logger.debug("Setting site number from parent VistaAccessVerifyRealm to [" + accessVerifyRealm.getSiteNumber() + "]");
				this.setSiteNumber(accessVerifyRealm.getSiteNumber());
			}
		}
		return super.getSiteNumber();
	}

	public String getServiceAccountUID()
	{
		return this.serviceAccountUID;
	}

	public void setServiceAccountUID(String serviceAccountUID)
	{
		this.serviceAccountUID = serviceAccountUID;
	}

	public String getServiceAccountPWD()
	{
		return this.serviceAccountPWD;
	}

	public void setServiceAccountPWD(String serviceAccountPWD)
	{
		this.serviceAccountPWD = serviceAccountPWD;
	}

	/**
	 * ServiceAccountRoles are stored as a list of enumerations, but externally they
	 * are set/get as a comma delimited String.
	 * @return
	 */
	public String getServiceAccountRoles()
	{
		if(this.serviceAccountRoles == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for(VistaRealmRoles role : this.serviceAccountRoles)
		{
			if(sb.length() > 0)
				sb.append(',');
			sb.append(role.toString());
		}
		
		return sb.toString();
	}

	public List<String> getServiceAccountRolesNames()
	{
		if(this.serviceAccountRoles == null)
			return null;
		List<String> roleNames = new ArrayList<String>(serviceAccountRoles.size());
		for(VistaRealmRoles role : this.serviceAccountRoles)
			roleNames.add(role.getRoleName());
		return roleNames;
	}
	
	public void setServiceAccountRoles(String serviceAccountRoles)
	{
		if(serviceAccountRoles == null)
			return;
		String[] serviceAccountRoleNames = serviceAccountRoles.split(",");
		this.serviceAccountRoles = new ArrayList<VistaRealmRoles>(serviceAccountRoleNames.length);
		for(String serviceAccountRoleName : serviceAccountRoleNames)
		{
			VistaRealmRoles role = VistaRealmRoles.getRoleByName(serviceAccountRoleName);
			if(role == null)
				logger.error("Unknown role name '" + serviceAccountRoleName + "' configured in CertificateRealm.");
			else
				this.serviceAccountRoles.add(role);
		}
	}

	@Override
    protected Logger getLogger()
    {
	    return logger;
    }

	/**
	 * Is the realm initialized (i.e. capable of authenticating/authorizing 
	 * users).
	 * 
	 * @return
	 */
	public boolean isInitialized()
	{
		boolean result = true;
		Container container = this.getContainer();
		String containerName = container == null ? null : container.getName();
		
		if (getSiteAbbreviation() == null)
		{
			logger.warn("VistaRealm[" + containerName + "] - site abbreviation is not set and must be before authentication will succeed.");
			result = false;
		}
		if (getSiteName() == null)
		{
			logger.warn("VistaRealm[" + containerName + "] - site name is not set and must be before authentication will succeed.");
			result = false;
		}
		if (getSiteNumber() == null)
		{
			logger.warn("VistaRealm[" + containerName + "] - site number is not set and must be before authentication will succeed.");
			result = false;
		}
		if (getServiceAccountUID() == null)
		{
			logger.warn("VistaRealm[" + containerName + "] - service account UID is not set and must be before authentication will succeed.");
			result = false;
		}
		if (getServiceAccountPWD() == null)
		{
			logger.warn("VistaRealm[" + containerName + "] - service account PWD is not set and must be before authentication will succeed.");
			result = false;
		}
		if (getServiceAccountRolesNames() == null)
		{
			logger.warn("VistaRealm[" + containerName + "] - service account role names is not set and must be before authentication will succeed.");
			result = false;
		}
		
		return result;
	}

	/*
	/*
	 * ======================================================================================
	 * Authentication Methods
	 * ======================================================================================
	 */

	/**
	 * Return the Principal associated with the specified username and
	 * credentials, if there is one; otherwise return <code>null</code>.
	 * 
	 * @param username
	 *            Username of the Principal to look up, A valid VistaImaging
	 *            access code
	 * @param credentials
	 *            Password or other credentials to use in authenticating this
	 *            username, The verify code matching the given access code
	 */
	public Principal authenticate(String username, String password)
	{
		logger.info("authenticate (" + username + ", password)");
		return null;
	}
	
	/**
	 * Return the Principal associated with the specified username and
	 * credentials, if there is one; otherwise return <code>null</code>.
	 * 
	 * @param username
	 *            Username of the Principal to look up
	 * @param credentials
	 *            Password or other credentials to use in authenticating this
	 *            username
	 */
	public Principal authenticate(String username, byte[] credentials)
	{
		logger.info("authenticate (" + username + ", byte[])");
		return null;
	}

	/**
	 * Return the Principal associated with the specified username, which
	 * matches the digest calculated using the given parameters using the method
	 * described in RFC 2069; otherwise return <code>null</code>.
	 * 
	 * @param username
	 *            Username of the Principal to look up
	 * @param digest
	 *            Digest which has been submitted by the client
	 * @param nonce
	 *            Unique (or supposedly unique) token which has been used for
	 *            this request
	 * @param realm
	 *            Realm name
	 * @param md5a2
	 *            Second MD5 digest used to calculate the digest : MD5(Method +
	 *            ":" + uri)
	 */
	public Principal authenticate(String username, String clientDigest, String nOnce, String nc, String cnonce, String qop, String realm,
	        String md5a2)
	{
		logger.info("authenticate (" + username + ", digest)");
		return null;
	}

	/**
	 * Return the Principal associated with the specified chain of X509 client
	 * certificates. If there is none, return <code>null</code>.
	 * 
	 * For this method to be called the client must have presented an X509
	 * certificate, which has been signed by a trusted Certificate Authority. At
	 * this point, all we need to do is get the user name from the certificate
	 * and assign the role.
	 * 
	 * @param certs
	 *            Array of client certificates, with the first one in the array
	 *            being the certificate of the client itself.
	 */
	public Principal authenticate(X509Certificate certs[])
	{
		logger.debug("Authenticating using X509 certificate.");
		VistaRealmPrincipal principal = null;
		List<java.security.cert.X509Certificate> certsList = new ArrayList<java.security.cert.X509Certificate>();
		boolean possessesTrustedCertificate = false;
		String certificateDistinguishedName = null;

		// check all of the certificates, if one is valid then that becomes the
		// source for the Principal information
		for (X509Certificate cert : certs)
		{
			logger.debug("X509Certificate subject '" + cert.getSubjectDN().getName() + ".");
			certsList.add(cert); // build the list that will populate the
			// Principal
			// we may not use this list but building it now
			// avoids a second iterator
			try
			{
				// the validity check will throw an exception if it is invalid
				cert.checkValidity();
				certificateDistinguishedName = cert.getSubjectX500Principal().getName();
				possessesTrustedCertificate = true;
				logger.debug("X509Certificate is valid.");
			} 
			catch (CertificateExpiredException e)
			{
				logger.warn("Authentication by certificate of " + cert.getSubjectX500Principal().getName() + " failed due to "
				        + e.getMessage());
			} 
			catch (CertificateNotYetValidException e)
			{
				logger.warn("Authentication by certificate of " + cert.getSubjectX500Principal().getName() + " failed due to "
				        + e.getMessage());
			}
		}

		// possessesTrustedCertificate will be false unless at least one certificate is valid
		if (possessesTrustedCertificate)
		{
			logger.debug("User '" + certificateDistinguishedName + "' has been authenticated by X509Certificate.");
			principal = new VistaRealmPrincipal(getRealmName(), getServiceAccountUID(), getServiceAccountPWD(), certsList, getServiceAccountRolesNames(), null);
			principal.setPreemptiveAuthorization(this);
			VistaRealmSecurityContext.set(principal);
			getLogger().info("VistaRealmSecurityContext set on thread (" + Thread.currentThread().getName() + ")");
		}

		return principal;
	}
}


