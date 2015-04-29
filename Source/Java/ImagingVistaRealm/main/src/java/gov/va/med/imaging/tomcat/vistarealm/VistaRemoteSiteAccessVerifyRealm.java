/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov 12, 2009
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
import gov.va.med.imaging.tomcat.vistarealm.exceptions.RpcException;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.catalina.Container;
import org.apache.catalina.Realm;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaRemoteSiteAccessVerifyRealm 
extends AbstractVistaRealmImpl
implements Realm, org.apache.catalina.Lifecycle, VistaRemoteSiteAccessVerifyRealmMBean
{
	
	// If we need to do a Vista connection for authentication then delay for
	// some amount of time after the connection to allow Vista to get its act together again. This is most
	// definitely a kludge to work around a problem we can't directly address.
	private int vistaConnectDelayKludge = 1000;
	private static final int maxVistaConnectDelayKludge = 3000;
	private static final int minVistaConnectDelayKludge = 0;
	
	private Container parentContainer = null;
	private Realm parentContainerRealm = null;
	
	private String claimsSiteNumber = null;
	private Boolean generateBseToken = Boolean.TRUE;

	private Logger logger = Logger.getLogger(this.getClass());
	
	public VistaRemoteSiteAccessVerifyRealm()
	{
		logger.info(VistaRemoteSiteAccessVerifyRealm.class.getCanonicalName() + " ctor()");
		this.setSiteNumber("<all>");// the site number must not be null (prevent exceptions) but this realm fronts
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getLogger()
	 */
	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

	/**
	 * @return the claimsSiteNumber
	 */
	public String getClaimsSiteNumber()
	{
		return claimsSiteNumber;
	}

	/**
	 * @param claimsSiteNumber the claimsSiteNumber to set
	 */
	public void setClaimsSiteNumber(String claimsSiteNumber)
	{
		this.claimsSiteNumber = claimsSiteNumber;
	}

	/**
	 * @return the generateBseToken
	 */
	public Boolean getGenerateBseToken()
	{
		return generateBseToken;
	}

	/**
	 * @param generateBseToken the generateBseToken to set
	 */
	public void setGenerateBseToken(Boolean generateBseToken)
	{
		this.generateBseToken = generateBseToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#isInitialized()
	 */
	@Override
	public boolean isInitialized() 
	{
		boolean result = true;
		//Container container = this.getContainer();
		//String containerName = container == null ? null : container.getName();					

		return result;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.lang.String, byte[])
	 */
	@Override
	public Principal authenticate(String username, byte[] credentials) 
	{
		return authenticate(username, new String(credentials));
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Principal authenticate(String username, String clientDigest, String nOnce, String nc, 
			String cnonce, String qop, String realm, String md5a2) 
	{
		getLogger().info("authenticate (" + username + ", digest)");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public Principal authenticate(String username, String password) 
	{
		VistaRealmPrincipal principal = null;

		// System.err.println("Begin stack trace from VistaRealm.authenticate()
		// - THIS IS NOT A THROWN EXCEPTION, just a stack trace");
		// Thread.dumpStack();
		// System.err.println("End stack trace from VistaRealm.authenticate()");

		UsernamePieces usernamePieces = new UsernamePieces(username);
		VistaRemoteSiteAccessVerifyRealmSite realmSite = createRealmSite(usernamePieces);
		
		getLogger().info("Realm '" + getSiteNumber() + "'-authenticate (" + username + ", <password not shown>)");
		
		// get a lock that is mapped to the user name
		// this will prevent multiple threads from authenticating the same user at the same time
		// and should result in succeeding threads getting a cache hit instead of a VistA call
		ReentrantLock lock = getUsernameLock(username);
		try
		{
			if(lock != null) 
				lock.lock();
			else
				getLogger().error("Unable to acquire lock for username '" + username + "', this could potentially lead to concurrency problems.");
			
			// if the principal cache is turned on then
			// look in the principal cache first,
			// if it is there then check the password and return it
			username = usernamePieces.getUsername();
			if (isUsingPrincipalCache())
			{
				FullyQualifiedPrincipalName fqPrincipal = new FullyQualifiedPrincipalName(realmSite.getSiteNumber(), username);
				PrincipalCacheValue cacheEntry = getPrincipalCacheEntry( fqPrincipal );
				if (cacheEntry != null)
				{
					principal = cacheEntry.getPrincipal().clone(); // clone the
					// Principal so that each thread has its own copy, added clone
					// on 15Oct2007 CTB
					
					// make sure that the PWD supplied in the call matches that in
					// the cache.
					if (principal.getVerifyCode().equals(password))
					{
						getLogger().info("User (" + username + ", <password not shown>) found in VistaRealmPrincipal cache");
	
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
						getLogger().warn("User (" + username + ", <password not shown>) found in VistaRealmPrincipal cache WITH DIFFERENT PASSWORD!");
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
						principal = new VistaRealmPrincipal(realmSite.getSiteNumber(), false,
						        VistaRealmPrincipal.AuthenticationCredentialsType.Password);
						principal.setAccessCode(username);
						principal.setVerifyCode(password);						
						principal.setPreemptiveAuthorization(this);
						
						
						if(claimsSiteNumber != null && claimsSiteNumber.equals(realmSite.getSiteNumber()))
						{
							getLogger().info("Claims user authentication");
							broker.localConnectWithoutImaging(realmSite, principal);
							principal.addRole("claims"); // add the claims role to allow the user to access without having the necessary keys
							// JMW 10/5/2009 - get the broker security token using the non-VistA Imaging RPC
							if(getGenerateBseToken())
							{
								String brokerSecurityToken = broker.getBrokerSecurityTokenWithoutImaging(realmSite.getSiteNumber());
								principal.setSecurityToken(brokerSecurityToken);
							}
						}
						else
						{
							broker.localConnect(realmSite, principal);	
							// security keys are mapped to application roles
							String[] securityKeys = broker.getUserKeys();
							// get the mapped roles and add them to the Principal
							principal.addRoles(VistaRealmRoles.getMappedRoleNames(securityKeys));
							if(getGenerateBseToken())
							{
								try
								{
									// JMW 12/21/2010 - need to get broker security token if VistA Imaging installed as well
									String brokerSecurityToken = broker.getBrokerSecurityTokenWithImaging(realmSite.getSiteNumber());
									principal.setSecurityToken(brokerSecurityToken);
								}
								catch(RpcException rpcX)
								{
									getLogger().warn("Error getting BSE token from remote site '" + realmSite.getSiteNumber() + "', BSE will be disabled.", rpcX);
								}
							}
						}
						if (getLogger().isDebugEnabled())
							for (String roleName : principal.getRoles())
								getLogger().debug("Authenticated user '" + username + "has role '" + roleName + "'.");
						
	
						getLogger().info("authenticate (" + username + ", <password not shown>), user authenticated in Vista");
						principal.setAuthenticatedByVista(true);
					} catch (ConnectionFailedException e)
					{
						getLogger().error("authenticate of (" + username + ", <password not shown>), failed due to connection problem", e);
						principal = null;
					} catch (InvalidCredentialsException e)
					{
						getLogger().info("authenticate of (" + username + ", <password not shown>), failed due to invalid credentials");
						principal = null;
					} catch (MethodException e)
					{
						getLogger().error("authenticate of (" + username
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
				
				// set the thread local security context for later access from
				// application code
				VistaRealmSecurityContext.set(principal);
				getLogger().info("VistaRealmSecurityContext set on thread (" + Thread.currentThread().getName() + ")");
			}
	
			return principal;
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
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.Realm#authenticate(java.security.cert.X509Certificate[])
	 */
	@Override
	public Principal authenticate(X509Certificate certs[]) 
	{
		getLogger().debug("Authenticating using X509 certificate.");
		return null;
	}

	public int getVistaConnectDelayKludge() 
	{
		return vistaConnectDelayKludge;
	}
	
	public void setVistaConnectDelayKludge(int vistaConnectDelayKludge)
	{
		vistaConnectDelayKludge = Math.max(minVistaConnectDelayKludge, vistaConnectDelayKludge);
		vistaConnectDelayKludge = Math.min(maxVistaConnectDelayKludge, vistaConnectDelayKludge);

		this.vistaConnectDelayKludge = vistaConnectDelayKludge;
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
	
	// ===========================================================================================================
	// Realm implementation
	// ===========================================================================================================

	/**
	 * A Container is an object that can execute requests received from a
	 * client, and return responses based on those requests. Engine -
	 * Representation of the entire Catalina servlet engine. Host -
	 * Representation of a virtual host containing a number of Contexts. Context -
	 * Representation of a single ServletContext, which will typically contain
	 * one or more Wrappers for the supported servlets. Wrapper - Representation
	 * of an individual servlet definition.
	 */

	@Override
	public void setContainer(Container container)
	{
		super.setContainer(container);

		// if the container has a parent then get its realm
		// this class will authenticate against that realm as well as its own
		// authentication repository
		if (getContainer() != null)
		{
			getLogger().info("Container name is '" + getContainer().getName() + "' container type is '" + getContainer().getClass().getName() + "'");
			
			parentContainer = getContainer().getParent();
			if (parentContainer != null)
			{
				getLogger().info("Parent container name is '" + parentContainer.getName() + 
					"' parent container type is '" + parentContainer.getClass().getName() + "'");
				parentContainerRealm = parentContainer.getRealm();
				getLogger().info("Parent container realm type is '" + 
					(parentContainerRealm == null ? "null" : parentContainerRealm.getClass().getName()) + 
					"'");
			}
			else
			{
				getLogger().info("Parent container is null.  Delegation to parent realm will be disabled.");
			}
		}
	}
	
	/*
	public synchronized Container getParentContainer()
	{
		if(parentContainer == null)
			parentContainer = getContainer() == null ? null : getContainer().getParent();
		
		return parentContainer;
	}*/
	
	/*
	public synchronized Realm getParentContainerRealm()
	{
		if(parentContainerRealm == null)
		{
			Container parentContainer = getParentContainer();
			parentContainerRealm = parentContainer == null ? null : parentContainer.getRealm();
		}
		
		return parentContainerRealm;
	}*/
	
	private VistaRemoteSiteAccessVerifyRealmSite createRealmSite(UsernamePieces usernamePieces)
	{
		return new VistaRemoteSiteAccessVerifyRealmSite(usernamePieces.getSiteNumber(), 
				usernamePieces.getServer(), usernamePieces.getPort(), usernamePieces.getSiteName());
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
    	
    	sb.append(this.getClass().getSimpleName());
    	
	    return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getSiteName()
	 */
	@Override
	public String getSiteName() 
	{
		return "<site name>";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealmImpl#getSiteAbbreviation()
	 */
	@Override
	public String getSiteAbbreviation() 
	{
		return "<site abbr>";
	}

	class UsernamePieces
	{
		String username;
		String server;
		int port;
		String siteNumber;
		String siteName;
		
		UsernamePieces(String inputUsername)
		{
			String serverPortPiece = StringUtils.MagPiece(inputUsername, StringUtils.SEMICOLON, 1);
			server = StringUtils.MagPiece(serverPortPiece, StringUtils.COLON, 1);
			port = Integer.parseInt(StringUtils.MagPiece(serverPortPiece, StringUtils.COLON, 2));
			siteNumber = StringUtils.MagPiece(inputUsername, StringUtils.SEMICOLON, 2);
			siteName = StringUtils.MagPiece(inputUsername, StringUtils.SEMICOLON, 3);
			username = StringUtils.MagPiece(inputUsername, StringUtils.SEMICOLON, 4);			
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @return the server
		 */
		public String getServer() {
			return server;
		}

		/**
		 * @return the port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @return the siteNumber
		 */
		public String getSiteNumber() {
			return siteNumber;
		}

		/**
		 * @return the siteName
		 */
		public String getSiteName() {
			return siteName;
		}
	}
	
	class VistaRemoteSiteAccessVerifyRealmSite implements VistaRealmSite
	{
		
		final String siteNumber;
		final String vistaServer;
		final Integer vistaPort;
		final String siteName;
		
		VistaRemoteSiteAccessVerifyRealmSite(String siteNumber, String vistaServer, Integer vistaPort,
				String siteName)
		{
			this.siteNumber = siteNumber;
			this.vistaServer = vistaServer;
			this.vistaPort = vistaPort;
			this.siteName = siteName;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getSiteAbbreviation()
		 */
		@Override
		public String getSiteAbbreviation() 
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getSiteName()
		 */
		@Override
		public String getSiteName() 
		{
			return this.siteName;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getSiteNumber()
		 */
		@Override
		public String getSiteNumber() 
		{
			return siteNumber;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getVistaPort()
		 */
		@Override
		public Integer getVistaPort() 
		{
			return vistaPort;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getVistaServer()
		 */
		@Override
		public String getVistaServer() 
		{
			return vistaServer;
		}
		
	}
}
