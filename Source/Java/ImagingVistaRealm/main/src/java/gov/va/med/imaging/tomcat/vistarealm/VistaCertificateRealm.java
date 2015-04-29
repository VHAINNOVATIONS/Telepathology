package gov.va.med.imaging.tomcat.vistarealm;

import java.lang.management.ManagementFactory;
import java.security.Principal;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.catalina.Container;
import org.apache.catalina.Realm;
import org.apache.log4j.Logger;

/**
 * This class implements a Tomcat Realm that accepts only X.509 certificates.
 * Otherwise it is similar to the VistaRealm in that the created Principal 
 * instances are VistaRealmPrincipal and compatible with the TransactionContext
 * mechanism in VIX. 
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
 * className="gov.va.med.imaging.tomcat.vistarealm.VistaRealm"
 * siteNumber = "660"
 * siteAbbreviation = "SLC"
 * siteName = "Salt Lake City, UT"
 * />
 * 
 * =========================================================================
 * server.xml Realm element example with all properties specified
 * <Realm
 * className="gov.va.med.imaging.tomcat.vistarealm.VistaRealm"
 * siteNumber="660"
 * siteAbbreviation="SLC"
 * siteName="Salt Lake City, UT"
 * usingPrincipalCache="true"
 * principalCacheLifespan="60000"
 * refreshPrincipalCacheEntryOnUse="true"
 * vistaConnectDelayKludge="1000"
 * />
 * 
 * @author VHAISWBECKEC
 * 
 */
public class VistaCertificateRealm
extends AbstractVistaRealmImpl
implements Realm, org.apache.catalina.Lifecycle, AbstractVistaRealm, VistaCertificateRealmMBean
{
	// Known Roles are now defined in the VistaRealmRoles Enum in the
	// VistaRealmClient project.
	// Partially this was for a code cleanup, and partially to make them
	// available
	// outside of the realm itself.

	private Logger logger = Logger.getLogger(this.getClass());
	
	private Container parentContainer;
	private Realm parentContainerRealm;

	/**
	 * 
	 */
	public VistaCertificateRealm()
	{
		logger.info(VistaCertificateRealm.class.getCanonicalName() + " ctor()");
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
		String name = null;

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
				name = (name == null ? cert.getSubjectDN().getName() : name);
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
			logger.debug("User '" + name + "' has been authenticated by X509Certificate.");
			List<String> roles = new ArrayList<String>();
			roles.add(VistaRealmRoles.PeerVixsRole.getRoleName());
			principal = new VistaRealmPrincipal(getRealmName(), name, certsList, roles, null);
			principal.setPreemptiveAuthorization(this);
			VistaRealmSecurityContext.set(principal);
		}

		return principal;
	}

	/**
	// ========================================================================================
	// MBean Registration Stuff
	// ========================================================================================
	/**
	 * Self-register with the MBeanServer. Once this method runs once, it will
	 * not re-register, just return immediately.
	 * 
	 */
	
	/*
	private ObjectName registeredInstanceName = null;

	protected void mBeanRegistration()
	{
		// if we're already registered then just return
		if (registeredInstanceName != null)
			return;

		// register the management interface if the MBeanServer exists
		// note that the regions are created by the FileSystemCache, which is
		// reponsible
		// for registering their management MBeans
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if (mbs != null)
		{
			try
			{
				ObjectInstance oi = mbs.registerMBean(this, null);
				registeredInstanceName = oi.getObjectName();
			} catch (InstanceAlreadyExistsException e)
			{
				logger.error(e);
			} catch (MBeanRegistrationException e)
			{
				logger.error(e);
			} catch (NotCompliantMBeanException e)
			{
				logger.error(e);
			}
		}
	}

	protected void mBeanUnRegistration()
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if (mbs != null && registeredInstanceName != null)
		{
			try
			{
				mbs.unregisterMBean(registeredInstanceName);
				registeredInstanceName = null;
			} catch (InstanceNotFoundException x)
			{
				logger.error(x);
			} catch (MBeanRegistrationException x)
			{
				logger.error(x);
			}
		}
	}
	*/
}


