/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 23, 2009
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
package gov.va.med.imaging.tomcat.vistarealm;

import gov.va.med.imaging.tomcat.vistarealm.exceptions.ConnectionFailedException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.tomcat.vistarealm.exceptions.MethodException;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.catalina.Container;

/**
 * This realm is used to authenticate clients from external communities (ex: XCA).
 * This realm requires a username and password that corresponds to a pair of VistA access and verify codes
 * to authenticate the user and configure the VistaRealmPrincipal.  If the access and verify codes are
 * valid, then the certificate is validated. 
 * 
 * This realm does not use any MAG RPC calls (so it can be run against station 200).
 * 
 * User roles should be configured as they will not be loaded from the database
 * 
 * server.xml Realm element example with all properties specified
 * <Realm
 * className="gov.va.med.imaging.tomcat.vistarealm.ExternalCommunityCertificateRealm"
 * siteNumber="660"
 * siteAbbreviation="SLC"
 * siteName="Salt Lake City, UT"
 * usingPrincipalCache="true"
 * principalCacheLifespan="60000"
 * refreshPrincipalCacheEntryOnUse="true"
 * vistaConnectDelayKludge="1000"
 * serviceAccountUID="userId"
 * serviceAccountPWD="password"
 * vistaServer="localhost"
 * vistaPort="9300"
 * authenticateCertificate="true"
 * />
 * 
 * @author vhaiswwerfej
 *
 */
public class ExternalCommunityCertificateRealm 
extends CertificateRealm 
implements VistaRealm, VistaRealmSite, ExternalCommunityCertificateRealmMBean 
{
	
	private String vistaServer = null;
	private Integer vistaPort = null;
	private Boolean authenticateCertificate = Boolean.TRUE;
	private int vistaConnectDelayKludge = 1000;
	private static final int maxVistaConnectDelayKludge = 3000;
	private static final int minVistaConnectDelayKludge = 0;
	
	/**
	 * @return the authenticateCertificate
	 */
	public Boolean getAuthenticateCertificate() {
		return authenticateCertificate;
	}
	/**
	 * @param authenticateCertificate the authenticateCertificate to set
	 */
	public void setAuthenticateCertificate(Boolean authenticateCertificate) {
		this.authenticateCertificate = authenticateCertificate;
	}
	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealm#getVistaPort()
     */
    @Override
    public Integer getVistaPort()
    {
	    return vistaPort;
    }
	public void setVistaPort(Integer vistaPort)
	{
		if(this.vistaPort == null)
			this.vistaPort = vistaPort;
		else
			getLogger().error("The vista port may not be changed once it has been set, attempt to change from '" + 
				this.vistaPort + "' to '" + vistaPort + "' is being ignored.");
	}
	
	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealm#getVistaServer()
     */
    @Override
    public String getVistaServer()
    {
	    return vistaServer;
    }
	public void setVistaServer(String localVistaServer)
	{
		if(this.vistaServer == null)
			this.vistaServer = localVistaServer;
		else
			getLogger().error("The vista server may not be changed once it has been set, attempt to change from '" + 
				this.vistaServer + "' to '" + localVistaServer + "' is being ignored.");
	}
	
	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealm#getVistaConnectDelayKludge()
     */
	public int getVistaConnectDelayKludge()
	{
		return this.vistaConnectDelayKludge;
	}

	public void setVistaConnectDelayKludge(int vistaConnectDelayKludge)
	{
		vistaConnectDelayKludge = Math.max(minVistaConnectDelayKludge, vistaConnectDelayKludge);
		vistaConnectDelayKludge = Math.min(maxVistaConnectDelayKludge, vistaConnectDelayKludge);

		this.vistaConnectDelayKludge = vistaConnectDelayKludge;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.CertificateRealm#authenticate(java.security.cert.X509Certificate[])
	 */
	@Override
	public Principal authenticate(X509Certificate[] certs) 
	{
		// authenticate against VistA first
		
		VistaRealmPrincipal principal = null;

		// System.err.println("Begin stack trace from VistaRealm.authenticate()
		// - THIS IS NOT A THROWN EXCEPTION, just a stack trace");
		// Thread.dumpStack();
		// System.err.println("End stack trace from VistaRealm.authenticate()");

		getLogger().info("Realm '" + getSiteNumber() + "'-authenticate (" + getServiceAccountUID() + ", <password not shown>)");

		// get a lock that is mapped to the user name
		// this will prevent multiple threads from authenticating the same user at the same time
		// and should result in succeeding threads getting a cache hit instead of a VistA call
		ReentrantLock lock = getUsernameLock(getServiceAccountUID());
		try
		{
			if(lock != null) 
				lock.lock();
			else
				getLogger().error("Unable to acquire lock for username '" + getServiceAccountUID() + "', this could potentially lead to concurrency problems.");
			
			// if the principal cache is turned on then
			// look in the principal cache first,
			// if it is there then check the password and return it
			if (isUsingPrincipalCache())
			{
				FullyQualifiedPrincipalName fqPrincipal = new FullyQualifiedPrincipalName(getSiteNumber(), getServiceAccountUID());
				PrincipalCacheValue cacheEntry = getPrincipalCacheEntry( fqPrincipal );
				if (cacheEntry != null)
				{
					principal = cacheEntry.getPrincipal().clone(); // clone the
					// Principal so that each thread has its own copy, added clone
					// on 15Oct2007 CTB
					
					// make sure that the PWD supplied in the call matches that in
					// the cache.
					if (principal.getVerifyCode().equals(getServiceAccountPWD()))
					{
						getLogger().info("User (" + getServiceAccountUID() + ", <password not shown>) found in VistaRealmPrincipal cache");
	
						// if the principal is found in the cache then it has all
						// the roles it should have
						if (isRefreshPrincipalCacheEntryOnUse())
							cacheEntry.touch();
	
						// clear the application properties, all application
						// properties are assumed to be
						// local to the transaction context
						principal.clearApplicationProperties();
					} 
					else
					{
						principal = null; // NOTE: big security hole if this is not nulled out
						getLogger().warn("User (" + getServiceAccountUID() + ", <password not shown>) found in VistaRealmPrincipal cache WITH DIFFERENT PASSWORD!");
					}
				}
				else
					getLogger().info("Principal '" + fqPrincipal.toString() + "' not found in cache.");
			}
			else
				getLogger().info("Principal cache is not being used.");
	
			// if we have not been configured then do not try to do Vista connect
			if (isInitialized() )
			{
				// if the principal is not already set then try to retrieve the
				// information from VistA
				if (principal == null)
				{
					RpcBroker broker = new RpcBroker();
	
					try
					{
						principal = new VistaRealmPrincipal(getSiteNumber(), false,
						        VistaRealmPrincipal.AuthenticationCredentialsType.Password);
						principal.setAccessCode(getServiceAccountUID());
						principal.setVerifyCode(getServiceAccountPWD());						
						principal.setPreemptiveAuthorization(this);						
	
						// the localConnect() method sets fields in the principal
						// instance
						broker.localConnectWithoutImaging(this, principal);
						
						// JMW 10/5/2009 - get the broker security token using the non-VistA Imaging RPC
						String brokerSecurityToken = broker.getBrokerSecurityTokenWithoutImaging(null);
						principal.setSecurityToken(brokerSecurityToken);
	
						getLogger().info("authenticate (" + getServiceAccountUID() + ", <password not shown>), user authenticated in Vista");
						principal.setAuthenticatedByVista(true);
					} catch (ConnectionFailedException e)
					{
						getLogger().error("authenticate of (" + getServiceAccountUID() + ", <password not shown>), failed due to connection problem", e);
						principal = null;
					} catch (InvalidCredentialsException e)
					{
						getLogger().info("authenticate of (" + getServiceAccountUID() + ", <password not shown>), failed due to invalid credentials");
						principal = null;
					} catch (MethodException e)
					{
						getLogger().error("authenticate of (" + getServiceAccountUID()
						        + ", <password not shown>), failed due to method exception (possible change in Vista version?)", e);
						principal = null;
					} finally
					{
						try
						{
							broker.disconnect();
						} catch (Exception x)
						{
						}
						// kludge ...
						int delay = getVistaConnectDelayKludge();
						if (delay > 0 && delay < 3000)
							try
							{
								Thread.sleep(delay);
							} catch (InterruptedException iX)
							{
							}
					}
				}
			}
			// VistaRealm connection to the local VistA has not been configured,
			// show a message to remind operations
			else
				getLogger().warn("VistaRealm has not been configured, set all Vista related properties in the configuration console before attempting ViXS transactions.");
	
	
			// set the thread local security context, if we have a Principal
			if (principal != null)
			{
				// put the principal into the cache (if it is turned on) 
				if( isUsingPrincipalCache() )
				{
					VistaRealmPrincipal principalClone = principal.clone();
					FullyQualifiedPrincipalName fqpn = new FullyQualifiedPrincipalName(principalClone);
					
					getLogger().info("Caching fully qualified principal name '" + fqpn.toString() + "'.");
					PrincipalCacheValue principalCacheValue = new PrincipalCacheValue(principalClone);
					addPrincipalCacheEntryIfUnique(fqpn, principalCacheValue);
				}
				
				if(!getAuthenticateCertificate().booleanValue())
				{
				// set the thread local security context for later access from
				// application code
				VistaRealmSecurityContext.set(principal); // don't do this here, will be done in the CertificateRealm
				getLogger().info("VistaRealmSecurityContext set on thread (" + Thread.currentThread().getName() + ")");
				}
			}
			else
			{
				getLogger().info("Principal is null indicating the login was not successful, blocking access and not checking certificate");
				return null;
			}
		}
		finally
		{
			if(lock != null) 
				try{lock.unlock();} 
				catch(IllegalMonitorStateException imsX)
				{
					getLogger().error(
					"Unexpected error unlocking username lock, this may indicate that the VistaRealm is getting confused with multiple threads under the same security context."
					);
				}
		}
		
		if(getAuthenticateCertificate().booleanValue())
		{
			// we have authenticated against VistA and everything is fine, now check the certificate
			getLogger().info("User has been authenticated by VistA, now authenticating certificate");
			Principal certPrincipal = super.authenticate(certs);
			
			if(certPrincipal != null)
			{
				if(certPrincipal.getClass() == VistaRealmPrincipal.class)
				{
					VistaRealmPrincipal vrp = (VistaRealmPrincipal)certPrincipal;
					vrp.setAuthenticatedByVista(true);
					// copy values from VistA realm principal
					vrp.setSiteName(principal.getSiteName());
					vrp.setSiteNumber(principal.getSiteNumber());
					vrp.setDuz(principal.getDuz());
					vrp.setFullName(principal.getFullName());
					vrp.setSsn(principal.getSsn());				
				}
			}
			return certPrincipal;
		}
		else
		{
			getLogger().info("Not authenticating the certificate");
			return principal;
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.CertificateRealm#isInitialized()
	 */
	@Override
	public boolean isInitialized() 
	{
		Container container = this.getContainer();
		String containerName = container == null ? null : container.getName();
		
		if (getVistaPort() == null)
		{
			getLogger().warn("VistaRealm[" + containerName + "] - vista port is not set and must be before authentication will succeed.");
			return false;
		}
		if (getVistaServer() == null)
		{
			getLogger().warn("VistaRealm[" + containerName + "] - vista server is not set and must be before authentication will succeed.");
			return false;
		}
		return super.isInitialized();
	}



	private WeakHashMap<ReentrantLock, String> userSpecificSynchronizationMap = 
		new WeakHashMap<ReentrantLock, String>();
	
	private ReentrantLock getUsernameLock(String username)
	{
		if(username == null)
			return null;
		
		synchronized(userSpecificSynchronizationMap)
		{
			for(ReentrantLock userLock : userSpecificSynchronizationMap.keySet())
			{
				String knownUser = userSpecificSynchronizationMap.get(userLock);
				if(username.equals(knownUser))
					return userLock;
			}
			ReentrantLock userLock = new ReentrantLock(true);
			userSpecificSynchronizationMap.put(userLock, username);
			return userLock;
		}
	}

}
