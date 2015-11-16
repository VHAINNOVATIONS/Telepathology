package gov.va.med.server.tomcat;


import gov.va.med.server.*;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.*;
import org.apache.catalina.*;
import org.apache.catalina.core.StandardServer;
import org.apache.log4j.Logger;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class TomcatRealmAuthentication
implements ServerAuthentication, org.apache.catalina.LifecycleListener
{
	private static final String GET_ACCESSOR_PREFIX = "get";
	private static final Class<?>[] NULL_PARAMETER_TYPES = new Class<?>[]{};
	private static final Object[] NULL_PARAMETERS = new Object[]{};
    private static final Logger logger = Logger.getLogger (TomcatRealmAuthentication.class);
	
	private ServerAdapter serverAdapter;
    private SortedSet<Realm> realms;
    private boolean serverStarted = false;
    
    /**
     * 
     */
    public TomcatRealmAuthentication(){}
    
    // ==================================================================================================
    // LifecycleListener implementation
    // ==================================================================================================
	/**
	 * @see org.apache.catalina.LifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)
	 */
	@Override
	public void lifecycleEvent(LifecycleEvent event)
	{
		if ("BEFORE_START".equalsIgnoreCase( event.getType() ))
		{
			this.serverAdapter = ServerAdapterImpl.getSingleton();
			this.serverAdapter.setServerAuthentication(this);
			this.serverStarted = true;
		}
		else if ("AFTER_STOP".equalsIgnoreCase( event.getType() ))
			this.serverStarted = false;
	}
    
	/**
	 * @return the serverStarted
	 */
	protected boolean isServerStarted()
	{
		return this.serverStarted;
	}

    // ==================================================================================================
    // ServerAuthentication implementation
    // ==================================================================================================
    
	/**
	 * @return the realms
	 */
	protected synchronized SortedSet<Realm> getRealms()
	{
		if(this.realms == null)
	    	this.realms = initializeRealms();		// initializes the set of realms
			
		return this.realms;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.server.ServerAuthentication#authenticate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Principal authenticate(
		SecurityRealmIdentification realmIdentification, 
		String username, 
		String clientDigest, 
		String nOnce, 
		String nc, 
		String cnonce,
		String qop, 
		String realmName, 
		String md5a2)
	{
		Realm realm = findRealm(realmIdentification);
		
		return realm == null ? null : realm.authenticate(
			username, 
			clientDigest, 
			nOnce, 
			nc, 
			cnonce,
			qop, 
			realmName, 
			md5a2);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.server.ServerAuthentication#authenticate(java.security.cert.X509Certificate[])
	 */
	@Override
	public Principal authenticate(SecurityRealmIdentification realmIdentification, X509Certificate[] certs)
	{
		Realm realm = findRealm(realmIdentification);
		
		return realm == null ? null : realm.authenticate(certs);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.server.ServerAuthentication#hasRole(java.security.Principal, java.lang.String)
	 */
	@Override
	public boolean hasRole(SecurityRealmIdentification realmIdentification, Principal principal, String role)
	{
		Realm realm = findRealm(realmIdentification);
		
		return realm == null ? null : realm.hasRole(principal, role);
	}

	/**
     * 
     * @param siteId
     * @param accessCode
     * @param verifyCode
	 * @see gov.va.med.server.ServerAuthentication#authenticate(gov.va.med.server.SecurityRealmIdentification, java.lang.String, byte[])
	 */
	@Override
	public Principal authenticate(
		SecurityRealmIdentification realmIdentification, 
		String username, 
		byte[] credentials)
	{
		Realm realm = findRealm(realmIdentification);
		
		return realm == null ? null : realm.authenticate(username, credentials);	// Try to authenticate against the realm
	}

	/**
	 * 
	 * @param realmIdentification
	 * @return
	 */
	private Realm findRealm(SecurityRealmIdentification realmIdentification)
	{
		for(Realm realm : this.getRealms() )
			if(matches(realm, realmIdentification))
				return realm;
		
		return null;
	}
	
	/**
	 * 
	 * @param realm
	 * @param realmIdentification
	 * @return
	 */
	private boolean matches(Realm realm, SecurityRealmIdentification realmIdentification)
	{
		// if the realm class is specified then the realm must be an instance
		// note, isInstance will include derived classes and realizations
		if(realmIdentification.getRealmClass() != null && !realmIdentification.getRealmClass().isInstance(realm))
			return false;

		// if the realm class name is specified then the realm must be an instance
		if( realmIdentification.getRealmClassName() != null && realmIdentification.getRealmClassName().equals(realm.getClass().getName()) )
			return false;
		
		Class<? extends Realm> candidateClass = realm.getClass();
		if(realmIdentification.getPropertyMap() != null)
		{
			for(String propertyKey : realmIdentification.getPropertyMap().keySet())
			{
				if(propertyKey == null || propertyKey.length() == 0)
					continue;
				
				String accessorName = 
					GET_ACCESSOR_PREFIX + 
					Character.toUpperCase(propertyKey.charAt(0)) +  
					(propertyKey.length()>1 ? propertyKey.substring(1) : "");
				try
				{
					Method accessorMethod = candidateClass.getMethod(accessorName, NULL_PARAMETER_TYPES);
					Object result = accessorMethod.invoke(realm, NULL_PARAMETERS);
					Object expectedPropertyValue = realmIdentification.getPropertyMap().get(propertyKey);
					if( result != null && !result.equals(expectedPropertyValue) ||
						result == null && expectedPropertyValue != null)
						return false;
				}
				catch (NoSuchMethodException x){/* not an error, just not a Realm we are interested in */}
				catch (Exception x)
				{
					// we cannot tell if this is the realm we want or not, assume it is not
					Logger.getLogger(SecurityRealmIdentification.class).error(x.getMessage());
					return false;
				}
			}
		}
		return true;		// class name is the same or null and all properties match or none specified
	}
	
	/**
	 * 
	 * @return
	 */
	private SortedSet<Realm> initializeRealms()
	{
		SortedSet<Realm> realms = new TreeSet<Realm>( new RealmHierarchyComparator() );
    	StandardServer server = (StandardServer)ServerFactory.getServer();
    	for (Service service : server.findServices())
    		recurseContainers(service.getContainer(), realms);
    	
    	return realms;
	}
	
	/**
	 * A comparator that orders Realm instances by their relations
	 * to one another where child realms always come before their
	 * parent realms. 
	 * 
	 * RealmA is a child of RealmB if the container associated to
	 * RealmA is an ancestor of the container associated to RealmB.
	 */
	class RealmHierarchyComparator 
	implements Comparator<Realm>
	{
		/**
		 * Returns a 
		 * negative integer, zero,     or a positive integer  as the first argument is 
		 * less than,        equal to, or greater             than the second.
		 */
		@Override
		public int compare(Realm realmA, Realm realmB)
		{
			Container containerA = realmA.getContainer();
			Container containerB = realmB.getContainer();
			
			return compare(containerA, containerB);
		}
		
		public int compare(Container containerA, Container containerB)
		{
			return 
				isDescendantContainer(containerA, containerB) ? -1 :
				isDescendantContainer(containerA, containerB) ? 1 :
				0;
		}

		public boolean isDescendantContainer(Container childContainer, Container ancestorContainer)
		{
			if(childContainer == null)
				return false;
			
			if(childContainer == ancestorContainer)
				return true;
			
			return isDescendantContainer(childContainer.getParent(), ancestorContainer);
		}
		
	}
	
	/**
	 * 
	 * @param container
	 * @param realms
	 */
	private void recurseContainers(Container container, SortedSet<Realm> realms)
	{
		try
		{
			for (Container childContainer : container.findChildren())
				recurseContainers(childContainer, realms);
	
			if (container instanceof org.apache.catalina.core.StandardHost ||
				container instanceof org.apache.catalina.core.StandardEngine ||
				container instanceof org.apache.catalina.core.StandardService)
				return;
			
			realms.add( container.getRealm() );
		}
		catch (Throwable t)
		{
			logger.error(t.getMessage(), t);
		}
	}
}
