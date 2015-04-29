package gov.va.med.imaging.tomcat.vistarealm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.realm.Constants;
import org.apache.log4j.Logger;

/*
 * This class implements all of the REalm methods except the authenticate methods.
 * the authenticate methods are implemented by the derived classes.
 */
public abstract class AbstractVistaRealmImpl
implements Realm, org.apache.catalina.Lifecycle, AbstractVistaRealm, javax.management.MBeanRegistration
{
	// properties that define the VistA site we are protecting
	private String siteNumber = null; // this MUST be initialized to null
	private String siteAbbreviation = null;
	private String siteName = null;

	// cache tuning properties
	private boolean usingPrincipalCache = true;
	private long principalCacheLifespan = 120000; // how long should Principal
													// mapping instances live in
													// the cache
	private boolean refreshPrincipalCacheEntryOnUse = false; // if true then
																// the date of
																// the Principal
																// entry will be
																// set to
	// current time whenever it is accessed

	// Read-only Realm properties
	private long lastPrincipalCacheSweepDate = 0L;

	// maps a username against an existing Principal instance
	// gets, puts, etc are synchronized BUT there are some operations that
	// explicitly synchronize on principalCache to assure atomic operations
	private Map<FullyQualifiedPrincipalName, PrincipalCacheValue> principalCache = 
		Collections.synchronizedMap(new HashMap<FullyQualifiedPrincipalName, PrincipalCacheValue>());

	// maps a Context to a
	// map from URL/HttpMethod to the array of applicable SecurityConstraint
	private Map<Context, Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue>> securityConstraintCache = 
		Collections.synchronizedMap(new HashMap<Context, Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue>>());

	// An identifier that helps connect log messages each security request is assigned a (process) unique identifier
	// that is logged along with info messages.  the ID should not be confused with a VIX transaction identifier.
	// The realm ID is only valid for the local server.  The VIX transaction ID is valid for the transaction
	// from end to end.
	private ThreadLocal<String> realmSecurityIdentifier = null;

	protected void setRealmSecurityIdentifier()
	{
		realmSecurityIdentifier = new ThreadLocal<String>();
		realmSecurityIdentifier.set("ID" + System.currentTimeMillis() + (new Object().hashCode()));
	}
	
	protected String getRealmSecurityIdentifier()
	{
		return realmSecurityIdentifier == null ? null : realmSecurityIdentifier.get();
	}
	
	// ===========================================================================================================
	// Required implementations
	// ===========================================================================================================
	protected abstract Logger getLogger();

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#isInitialized()
     */
	public abstract boolean isInitialized();

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getRealmImplementationName()
     */
	public String getRealmImplementationName()
	{
		return this.getClass().getName();
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getVersion()
     */
	public float getVersion()
	{
		return 1.0f;
	}

	// ===========================================================================================================
	// MBeanRegistration Methods
	// ===========================================================================================================
	private ObjectName desiredMBeanName;
	private ObjectName registeredMBeanName;
	
	// create the name that we would like to be registered as
	private synchronized ObjectName getDesiredMBeanName() 
	throws MalformedObjectNameException, NullPointerException
	{
		if(desiredMBeanName == null)
		{
			Hashtable<String, String> keys = new Hashtable<String, String>();
			keys.put("realm", getRealm());
			desiredMBeanName = new ObjectName("gov.va.med.imaging.tomcat.vistarealm", keys);
		}
		
		return desiredMBeanName;
	}
	
	/**
     * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer, javax.management.ObjectName)
     */
    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) 
    throws Exception
    {
    	
    	try
    	{ 
    		getLogger().info(
    			"Realm instance '" + getDesiredMBeanName().toString() + 
    			"' being registered as '" + (name == null ? getDesiredMBeanName().toString() : name.toString()) + 
    			"'." ); 
    	} 
    	catch (Exception e){}
    	
    	if(name != null)		// if we are passed a name then use it
    		return name;
    	else	
    		return getDesiredMBeanName();
    }

	/**
     * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
     */
    @Override
    public void postRegister(Boolean registrationDone)
    {
    	try{ getLogger().info("Realm instance '" + getDesiredMBeanName().toString() + "' is now registered."); } 
    	catch (Exception e){}
    }

	/**
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    @Override
    public void preDeregister() throws Exception
    {
    	try{ getLogger().info("Realm instance '" + getDesiredMBeanName().toString() + "' being de-registered."); } 
    	catch (Exception e){}
    }

	/**
     * @see javax.management.MBeanRegistration#postDeregister()
     */
    @Override
    public void postDeregister()
    {
    	try{ getLogger().info("Realm instance '" + getDesiredMBeanName().toString() + "' has been de-registered."); } 
    	catch (Exception e){}
    }

    // =========================================================================================================
    // the realm is registered with the MBeanserver when the realm is started
    // and de-registered when the realm is stopped
    // =========================================================================================================
	protected void mBeanRegistration()
	{
		// register the management interface if the MBeanServer exists
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if (mbs != null)
		{
			try
			{
				ObjectInstance oi = mbs.registerMBean(this, null);
				registeredMBeanName = oi.getObjectName();
				getLogger().info( "Realm '" + getDesiredMBeanName().toString() + "' registered as '" + registeredMBeanName + "'." );
				
			} catch (InstanceAlreadyExistsException e)
			{
				getLogger().error(e);
			} catch (MBeanRegistrationException e)
			{
				getLogger().error(e);
			} catch (NotCompliantMBeanException e)
			{
				getLogger().error(e);
			} catch (MalformedObjectNameException e)
            {
				getLogger().error(e);
            } catch (NullPointerException e)
            {
				getLogger().error(e);
            }
		}
	}

	protected void mBeanUnRegistration()
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if (mbs != null)
		{
			try
			{
				mbs.unregisterMBean(registeredMBeanName);
				getLogger().info( "Realm '" + registeredMBeanName + "' de-registered." );
				registeredMBeanName = null;
			} catch (InstanceNotFoundException x)
			{
				getLogger().error(x);
			} catch (MBeanRegistrationException x)
			{
				getLogger().error(x);
			}
		}
	}
    
    
	/*
	 * ======================================================================================
	 * Information string, toString()
	 * ======================================================================================
	 */
	/**
	 * Return descriptive information about this Realm implementation and the
	 * corresponding version number, in the format <description>/<version>.
	 */
	public String getInfo()
	{
		return getRealmImplementationName() + "/" + getVersion();
	}

	/**
	 * @see java.lang.Object#toString()
	 * 
	 * Returns a String like: VistaRealm [660-SLC Salt Lake City, UT
	 * vista:slc.vista.med.va.gov:9300]
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(this.getInfo());
		sb.append(" [");
		sb.append(this.getSiteAbbreviation());
		sb.append("-");
		sb.append(this.getSiteNumber());
		sb.append(" ");
		sb.append(this.getSiteName());
		sb.append("]");

		return sb.toString();
	}

	// ===========================================================================================================
	// JavaBean Property Accessors
	// These properties may be set from the server configuration.
	// ===========================================================================================================
	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getSiteAbbreviation()
     */
	public String getSiteAbbreviation()
	{
		return siteAbbreviation;
	}

	public void setSiteAbbreviation(String localSiteAbbreviation)
	{
		this.siteAbbreviation = localSiteAbbreviation;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getSiteName()
     */
	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String localSiteName)
	{
		this.siteName = localSiteName;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getSiteNumber()
     */
	public String getSiteNumber()
	{
		return siteNumber;
	}

	public String getRealm()
	{
		return getSiteNumber();
	}
	
	// because of the way a Realm is initialized by Tomcat
	// the immutable properties cannot be declared final, so
	// we prohibit changes after the first in code
	public void setSiteNumber(String localSiteNumber)
	{
		if (this.siteNumber == null)
			this.siteNumber = localSiteNumber;
		else
			getLogger().error(
			        "The site number may not be changed once it has been set, attempt to change from '" + this.siteNumber + "' to '"
			                + localSiteNumber + "' is being ignored.");
	}

	// ========================================================================================
	// Cache Tuning Properties
	// ========================================================================================
	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#isUsingPrincipalCache()
     */
	public Boolean isUsingPrincipalCache()
	{
		return this.usingPrincipalCache;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#setUsingPrincipalCache(java.lang.Boolean)
     */
	public void setUsingPrincipalCache(Boolean usingPrincipalCache)
	{
		// if the cache is being turned off then clear it
		if (this.usingPrincipalCache && !usingPrincipalCache)
			clearPrincipalCache();

		this.usingPrincipalCache = usingPrincipalCache;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getPrincipalCacheLifespan()
     */
	public Long getPrincipalCacheLifespan()
	{
		return this.principalCacheLifespan;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#setPrincipalCacheLifespan(java.lang.Long)
     */
	public void setPrincipalCacheLifespan(Long principalCacheLifespan)
	{
		this.principalCacheLifespan = principalCacheLifespan;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#isRefreshPrincipalCacheEntryOnUse()
     */
	public Boolean isRefreshPrincipalCacheEntryOnUse()
	{
		return this.refreshPrincipalCacheEntryOnUse;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#setRefreshPrincipalCacheEntryOnUse(java.lang.Boolean)
     */
	public void setRefreshPrincipalCacheEntryOnUse(Boolean refreshPrincipalCacheEntryOnUse)
	{
		this.refreshPrincipalCacheEntryOnUse = refreshPrincipalCacheEntryOnUse;
	}

	// ============================================================================================
	// Read-Only properties, used by JMX for monitoring
	// ============================================================================================
	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getPrincipalCacheSize()
     */
	public int getPrincipalCacheSize()
	{
		return principalCache.size();
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getSecurityConstraintCacheSize()
     */
	public int getSecurityConstraintCacheSize()
	{
		return this.securityConstraintCache.size();
	}

	/**
	 * Get the number of cached security constraints for the named context.
	 * 
	 * @param context
	 * @return
	 */
	public int getContextSecurityConstraintsCacheSize(String context)
	{
		if (context == null)
			return 0;

		for (Context servicedContext : securityConstraintCache.keySet())
		{
			if (context.equals(servicedContext.getName()))
			{
				Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue> contextSecurityConstraintCache = securityConstraintCache
				        .get(servicedContext);
				return contextSecurityConstraintCache.size();
			}
		}

		return 0;
	}

	/**
     * @see gov.va.med.imaging.tomcat.vistarealm.AbstractVistaRealm#getLastPrincipalCacheSweepDate()
     */
	public long getLastPrincipalCacheSweepDate()
	{
		return this.lastPrincipalCacheSweepDate;
	}

	// ===========================================================================================================
	// Lifecycle Listeners
	// ===========================================================================================================
	private List<LifecycleListener> lifecycleListeners = new ArrayList<LifecycleListener>();

	public void addLifecycleListener(LifecycleListener listener)
	{
		lifecycleListeners.add(listener);
	}

	public void removeLifecycleListener(LifecycleListener listener)
	{
		lifecycleListeners.remove(listener);
	}

	public LifecycleListener[] findLifecycleListeners()
	{
		LifecycleListener[] a = new LifecycleListener[lifecycleListeners.size()];
		lifecycleListeners.toArray(a);

		return a;
	}

	private void notifyLifecycleListeners(LifecycleEvent event)
	{
		for (LifecycleListener listener : lifecycleListeners)
			listener.lifecycleEvent(event);
	}

	// ===========================================================================================================
	// Tomcat LifecycleListener Implementation
	// ===========================================================================================================
	/**
	 * This is where the realm initialization occurs.
	 * 
	 * Get the realm started, this includes restoring the realm state from
	 * context properties.
	 */
	@Override
	public void start() throws LifecycleException
	{
		// Server server = ServerFactory.getServer();
		// logger.info("VistaRealm starting on server " + server.getInfo());

		notifyLifecycleListeners(new LifecycleEvent(this, Lifecycle.BEFORE_START_EVENT));

		// register ourselves with the JMX MBean server if it exists
		mBeanRegistration();

		if (isInitialized())
		{
			getLogger().info("Realm[" + this.getRealmName() + "] initialized with authorization site " + this.toString() + ".");

			notifyLifecycleListeners(new LifecycleEvent(this, Lifecycle.START_EVENT));
			getLogger().info("Realm [" + this.getRealmName() + "] is started.");
			notifyLifecycleListeners(new LifecycleEvent(this, Lifecycle.AFTER_START_EVENT));
		} else
			getLogger().warn("[" + this.getRealmName() + "] is NOT initialized, use JMX interface to set required fields and initialize.");

	}

	/**
	 * We are stopping, notify our listeners and unregister ourselves with the
	 * MBean server
	 */
	@Override
	public void stop() throws LifecycleException
	{
		getLogger().info("Realm[" + this.getRealmName() + "] stopping.");
		notifyLifecycleListeners(new LifecycleEvent(this, Lifecycle.BEFORE_STOP_EVENT));
		mBeanUnRegistration();
		notifyLifecycleListeners(new LifecycleEvent(this, Lifecycle.AFTER_STOP_EVENT));
	}

	// ===================================================================================================
	// Realm Property change Listener
	// ================================================================================================== 
	/**
	 * PropertyChangeListener management and notification
	 */
	private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeListeners.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeListeners.remove(listener);
	}

	protected void notifyPropertyChangeListeners(PropertyChangeEvent event)
	{
		for (Iterator<PropertyChangeListener> propertyChangeListenerIter = propertyChangeListeners.iterator(); propertyChangeListenerIter
		        .hasNext();)
			(propertyChangeListenerIter.next()).propertyChange(event);
	}

	protected void notifyPropertyChangeListeners(String propertyName, Object oldValue, Object newValue)
	{
		notifyPropertyChangeListeners(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
	}

	
	// ===========================================================================================================
	// Realm implementation
	// ===========================================================================================================

	/**
	 * Is the given role one of the roles known to this Realm
	 */
	public boolean isKnownRole(String roleName)
	{
		return VistaRealmRoles.isKnownRoleName(roleName);
	}
	
	/**
	 * This must be overridden because the RealmBase implementation expects an
	 * instance of GenericPrincipal
	 * 
	 * @see org.apache.catalina.Realm.hasRole(Principal principal, String role)
	 */
	@Override
	public boolean hasRole(Principal principal, String role)
	{
		getLogger().debug("hasRole (" + (principal != null ? principal.getName() : "UNKNOWN") + ", " + role + ")");
		try
		{
			return ((VistaRealmPrincipal) principal).hasRole(role);
		} 
		catch (ClassCastException ccX)
		{
			getLogger().error("Expecting an instance of VistaRealmPrincipal and got an instance of " + principal.getClass().getName()
			        + ", which VistaRealm does not understand");
			return false;
		}
	}
	

	/**
	 * A Container is an object that can execute requests received from a
	 * client, and return responses based on those requests. Engine -
	 * Representation of the entire Catalina servlet engine. Host -
	 * Representation of a virtual host containing a number of Contexts. Context -
	 * Representation of a single ServletContext, which will typically contain
	 * one or more Wrappers for the supported servlets. Wrapper - Representation
	 * of an individual servlet definition.
	 */
	private Container container = null;

	public void setContainer(Container container)
	{
		this.container = container;
	}

	public Container getContainer()
	{
		return this.container;
	}

	/**
	 * The realm name is the site number.
	 * 
	 * @return
	 */
	public String getRealmName()
	{
		return getSiteNumber();
	}

	/*
	 * =============================================================================================================================
	 * Principal Cache Implementation
	 * =============================================================================================================================
	 */

	/**
	 * Execute a periodic task, such as reloading, etc.
	 * 
	 * Executed periodically to reload from database, clear cache, etc ...
	 * 
	 * @see org.apache.catalina.Realm.backgroundProcess()
	 */
	@Override
	public void backgroundProcess()
	{
		long now = System.currentTimeMillis();

		// logically running eviction will have no effect if
		// the cache is not being used, but having this test here both
		// saves time and allows us to turn off processing if there is an
		// error in it.
		if (isUsingPrincipalCache())
			evictOldPrincipalCacheEntries(now);
	}

	/**
	 * This may be called from MBean management to force an eviction NOTE: this
	 * method does not check if the cache is being used, but simply runs the
	 * eviction pass.
	 */
	public void evictOldPrincipalCacheEntries()
	{
		long now = System.currentTimeMillis();

		evictOldPrincipalCacheEntries(now);
	}

	/**
	 * Clear all principal cache entries
	 */
	public void clearPrincipalCache()
	{
		principalCache.clear();
	}

	/**
	 * 
	 * @param now
	 */
	private void evictOldPrincipalCacheEntries(long now)
	{
		lastPrincipalCacheSweepDate = now;
		// eviction age is the date in the past when a cache entry must have
		// neen touched/opened after to remain in the cache
		long evictionAge = now - this.getPrincipalCacheLifespan();

		// synchronize on the principalCache so that we do not get a
		// ConcurrentModificationException
		// in the iterator
		synchronized (principalCache)
		{
			for (Iterator<FullyQualifiedPrincipalName> iter = principalCache.keySet().iterator(); iter.hasNext();)
			{
				FullyQualifiedPrincipalName fqPrincipal = iter.next();

				PrincipalCacheValue principalCacheValue = principalCache.get(fqPrincipal);
				if (principalCacheValue.getDate() < evictionAge)
					iter.remove();
			}
		}
	}
	
	protected PrincipalCacheValue getPrincipalCacheEntry(FullyQualifiedPrincipalName fqPrincipal)
	{
		return principalCache.get(fqPrincipal);
	}
	
	protected void addPrincipalCacheEntryIfUnique(FullyQualifiedPrincipalName fqpn, PrincipalCacheValue principalCacheValue)
	{
		getLogger().info("Caching fully qualified principal name '" + fqpn.toString() + "'.");
		// synchronize on the principal cache so that the get, the check for existence
		// and the put are atomic
		synchronized(principalCache)
		{
			// If the principal is not already cached then cache it. 
			// Don't PUT over an existing instance.
			if( principalCache.get(fqpn) == null )
			{
				// clone the Principal object and cache the clone
				// DO NOT CACHE the instance that the applications see because they
				// may change the values in the Principal !!!!!
				principalCache.put( fqpn, principalCacheValue );
			}
		}
	}

	/**
	 * @param context
	 * @param constraintKey
	 * @return
	 */
	protected SecurityConstraint[] getCachedSecurityConstraint(Context context, SecurityConstraintCacheKey constraintKey)
	{
		// first see if there is a security constraint map for this context
		Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue> contextCache = getContextSecurityConstraintCache(context);
		if (contextCache != null)
		{
			SecurityConstraintCacheValue cacheValue = contextCache.get(constraintKey);
			if (cacheValue != null)
				return cacheValue.getConstraints();
		}

		return null;
	}

	private Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue> getContextSecurityConstraintCache(Context context)
	{
		return securityConstraintCache.get(context);
	}

	private Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue> getOrCreateContextSecurityConstraintCache(Context context)
	{
		Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue> result = securityConstraintCache.get(context);
		if (result == null)
		{
			result = Collections.synchronizedMap(new HashMap<SecurityConstraintCacheKey, SecurityConstraintCacheValue>());

			securityConstraintCache.put(context, result);
		}
		return result;
	}

	/**
	 * @param context
	 * @param constraintKey
	 * @param results
	 */
	protected void putToSecurityConstraintsCache(Context context, SecurityConstraintCacheKey constraintKey,
	        Collection<SecurityConstraintMatch> matchingSecurityConstraints)
	{
		SecurityConstraint[] securityConstraintsArray = new SecurityConstraint[matchingSecurityConstraints.size()];

		int index = 0;
		for (SecurityConstraintMatch matchingSecurityConstraint : matchingSecurityConstraints)
			securityConstraintsArray[index++] = matchingSecurityConstraint.getSecurityConstraint();

		putToSecurityConstraintsCache(context, constraintKey, securityConstraintsArray);
	}

	private void putToSecurityConstraintsCache(Context context, SecurityConstraintCacheKey constraintKey,
	        SecurityConstraint[] securityConstraints)
	{
		// first see if there is a security constraint map for this context
		Map<SecurityConstraintCacheKey, SecurityConstraintCacheValue> contextCache = getOrCreateContextSecurityConstraintCache(context);
		contextCache.put(constraintKey, new SecurityConstraintCacheValue(securityConstraints));
	}
	
	// =================================================================================================
	// PreemptiveAuthorization Implementation
	// =================================================================================================
	/**
	 * A non-authoritative, pre-emptive determination of whether the currently logged in user
	 * has privileges to the selected resource.
	 * 
     * @see gov.va.med.imaging.tomcat.vistarealm.PreemptiveAuthorization#isAuthorized(java.lang.Object, java.lang.String, java.lang.String)
     */
    @Override
    public PreemptiveAuthorization.Result isAuthorized(
    		Principal principal, 
    		Object context, 
    		String contextRequestPath, 
    		String requestMethod)
    {
    	getLogger().info("Preemptively checking authorization '" + principal.getName() + "' to '" + requestMethod + ":" + contextRequestPath + "'.");
    	if(! (context instanceof Context) )
    	{
    		getLogger().warn("Attempt to pre-emptively determine authorization with a context of type other than Tomcat standard Context");
    		return PreemptiveAuthorization.Result.Unknown;
    	}
    	Context tomcatContext = (Context)context;
    	
		String requestContextPath = tomcatContext.getName();
		String requestPathInfo = requestContextPath + contextRequestPath;
		
    	getLogger().info("Preemptively checking authorization, getting security constraints.");
		SecurityConstraint[] securityConstraints = findSecurityConstraints(tomcatContext, requestPathInfo, requestMethod, contextRequestPath);

    	getLogger().info("Preemptively checking authorization, checking resource permmissions.");
		return hasResourcePermission(tomcatContext, securityConstraints, requestPathInfo, principal) ?
				PreemptiveAuthorization.Result.True : PreemptiveAuthorization.Result.False;
    }

	
	// =================================================================================================
	// Realm Implementation
	// Other than authenticate(), which is done by derived classes
	// =================================================================================================

	/*
	 * Perform access control based on the specified authorization constraint.
	 * 
	 * Servlet specification sections implemented: 12.8
	 * 
	 * Specially notable requirements: "An authorization constraint that names
	 * no roles indicates that access to the constrained requests must not be
	 * permitted under any circumstances."
	 * 
	 * Section 12.8.1: "When a url-pattern and http-method pair occurs in
	 * multiple security constraints, the constraints (on the pattern and
	 * method) are defined by combining the individual constraints. The rules
	 * for combining constraints in which the same pattern and method occur are
	 * as follows:
	 * 
	 * The combination of authorization constraints that name roles or that
	 * imply roles via the name “*” shall yield the union of the role names in
	 * the individual constraints as permitted roles. A security constraint that
	 * does not contain an authorization constraint shall combine with
	 * authorization constraints that name or imply roles to allow
	 * unauthenticated access. The special case of an authorization constraint
	 * that names no roles shall combine with any other constraints to override
	 * their affects and cause access to be precluded.
	 * 
	 * The combination of user-data-constraints that apply to a common
	 * urlpattern and http-method shall yield the union of connection types
	 * accepted by the individual constraints as acceptable connection types. A
	 * security constraint that does not contain a user-data-constraint shall
	 * combine with other user data-constraints to cause the unprotected
	 * connection type to be an accepted connection type."
	 * 
	 * 
	 * @param request
	 *            Request we are processing
	 * @param response
	 *            Response we are creating
	 * @param securityConstraints
	 *            applicable security constraints as determined by the
	 *            web-resource-collection, see findSecurityConstraints()
	 *            security-constraint elements that were returned in the previous
	 *            findSecurityConstraints() call
	 *            i.e. this is the list of applicable security constraints in
	 *            descending order of applicability
	 * @param context
	 *            The Context to which client of this class is attached.
	 * 
	 * @return <code>true</code> if this constraint is satisfied and
	 *         processing should continue, or <code>false</code> otherwise.
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	public boolean hasResourcePermission(
			Request request, 
			Response response, 
			SecurityConstraint[] securityConstraints, 
	        Context context) 
	throws IOException
	{
		boolean result = false;		// assume no access
		
		String requestUri = request.getRequestURI();
		getLogger().info("hasResourcePermission (" + request.getRequestURI() + ", " + context.getName() + "," + securityConstraints.length
		        + ") rsID='" + getRealmSecurityIdentifier() + "'.");

		// dump debugging messages, check the debug level first to avoid executing a bunch of code
		if (getLogger().isDebugEnabled())
			for (SecurityConstraint c : securityConstraints)
			{
				getLogger().debug("  SecurityConstraint '" + c.getDisplayName() + "' roles");
				for (String roleName : c.findAuthRoles())
					getLogger().debug("    role '" + roleName + "'");
			}

		// User principal may have been previously authenticated
		// if the user has not been authenticated then access will only be
		// granted where no security-constraint is applicable
		Principal principal = request.getPrincipal();
		
		result = hasResourcePermission(
				context, 
				securityConstraints, 
				requestUri, 
				principal);
		
		// permission is granted, log an authorization message
		if(result)
		{
			getLogger().info("hasResourcePermission (" + requestUri + "...) rsID='" + getRealmSecurityIdentifier()
			        + "' - permission GRANTED");
		}
		// if permission was denied for any reason, send a 403 response, else just log an authorization message
		else
		{
			String deniedMessage = 
				"Access to resource '" + requestUri + 
				"' by user '" + (principal == null ? "<unknown>" : principal.getName()) + 
				"' is denied.  " + 
				"Security realm unique ID '" + getRealmSecurityIdentifier() + "'.";

			getLogger().info(deniedMessage);
			response.sendError(403, deniedMessage);
		}
		
		return result;
	}

	/**
     * @param securityConstraints
     * @param context
     * @param result
     * @param requestUri
     * @param principal
     * @return
     */
    private boolean hasResourcePermission(
    		Context context, 
    		SecurityConstraint[] securityConstraints, 
    		String requestUri,
            Principal principal)
    {
    	boolean result = false;
    	
	    // no security-constraints applicable therefore no restriction
		if( securityConstraints == null || securityConstraints.length == 0 )
		{
			getLogger().debug("hasResourcePermission (" + requestUri + "...) rsID='" + getRealmSecurityIdentifier()
			        + "' - no security constraints provided, permission GRANTED");
			result = true;
		}
		else
		{
			// if the login-config element exists and the authorization method is FORM
			// there is some special handling of error and login URLs
			if( isSpecialFormAuthRequest(context.getLoginConfig(), requestUri) )
			{
				getLogger().info("hasResourcePermission (" + requestUri + "...) rsID='" + getRealmSecurityIdentifier()
				        + "' - bypassing security constraints for special form-based authentication resources, permission GRANTED");
				result = true;
			}

			// create an instance of SecurityConstraintList, which is capable of
			// intelligently combining the authorization constraints of many SecurityConstraint
			SecurityConstraintAuthorizationList<SecurityConstraint> scList = 
				new SecurityConstraintAuthorizationList<SecurityConstraint>(securityConstraints);

			 // if the user is not authenticated then the only way in is if the application
			// allows unauthenticated access
			if( principal == null )
			{
				result = scList.isAllowUnauthenticatedAccess();
				getLogger().info("hasResourcePermission (" + requestUri + "...) rsID='" + getRealmSecurityIdentifier()
				        + "' - no principal, permission " + (result ? "is GRANTED" : "is NOT GRANTED")
				        + " on unauthenticated access.");
			}
			else
			{
				// we can only handle our own Principal realizations
				if( !(principal instanceof VistaRealmPrincipal) )
				{
					// an error because we should never get any other kind of principal,
					// and we need the role information stored in it
					getLogger().error("Attempt to determine resource permissions, hasResourcePermission(), with an unknown principal type.");
				}
				else
				{
					result = scList.isAnyRoleAllowedAccess( ((VistaRealmPrincipal)principal).getRoles() );
				}
			}
		}
	    return result;
    }

	/**
	 * If the authorization method is FORM and the requested page is one of the
	 * "special" pages (error and login) then allow access.
	 * 
	 * @param config
	 * @param requestURI
	 */
	private boolean isSpecialFormAuthRequest(LoginConfig config, String requestURI)
	{
		String authMethod = config.getAuthMethod();
		if (config != null && Constants.FORM_METHOD.equals(authMethod))
		{
			String loginPage = config.getLoginPage();
			if (loginPage.equals(requestURI))
			{
				getLogger().debug(" Allow access to login page " + loginPage);
				return true;
			}

			// this is little strange 'cause it means that the error page wont't
			// be returned to
			// a user before login. I'm leaving it like this on the assertion
			// that this is more
			// secure by not allowing any clues about the application to escape
			// in the error page
			String errorPage = config.getErrorPage();
			if (errorPage.equals(requestURI))
			{
				getLogger().debug(" Allow access to error page " + errorPage);
				return true;
			}

			if (requestURI.endsWith(Constants.FORM_ACTION))
			{
				getLogger().debug(" Allow access to username/password submission");
				return true;
			}
		}

		return false;
	}

	/**
	 * Enforce any user data constraint required by the security constraint
	 * guarding this request URI. Return <code>true</code> if this constraint
	 * was not violated and processing should continue, or <code>false</code>
	 * if we have created a response already.
	 * 
	 * NOTE: this is essentially a copy of the code in
	 * org.apache.catalina.realm.BasicRealm. Deriving from that class would have
	 * been a better solution however it makes assumptions related to password
	 * matching that our authentication store will not support.
	 * The section of the web configuration that this code reflects looks something like:
	 * 
	 * <security-constraint>
	 * 	<display-name>XCA Responding Gateway</display-name>
	 * 	<web-resource-collection>
	 * 		<web-resource-name>Everything</web-resource-name>
	 * 		<description>All resources within the XCA Responding Gateway are protected</description>
	 * 		<url-pattern>/*</url-pattern>
	 * 	</web-resource-collection>
	 * 	<auth-constraint>
	 * 		<description></description>
	 * 		<role-name>xca</role-name>
	 * 		<role-name>developer</role-name>
	 * 	</auth-constraint>
	 * 	<user-data-constraint>
	 * 		<description>Requires encryption</description>
	 * 		<transport-guarantee>CONFIDENTIAL</transport-guarantee>
	 * 	</user-data-constraint>
	 * </security-constraint>
	 * 
	 * @param request
	 *            Request we are processing
	 * @param response
	 *            Response we are creating
	 * @param constraints
	 *            Security constraint being checked
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	public boolean hasUserDataPermission(Request request, Response response, SecurityConstraint[] constraints) 
	throws IOException
	{
		getLogger().debug("hasUserDataPermission(" + request.getRequestURI() + ") rsID='" + getRealmSecurityIdentifier() + "'");

		// Are there any security-constraint elements ?
		if( constraints == null || constraints.length == 0 )
		{
			getLogger().info("  hasUserDataPermission (" + request.getRequestURI() + ") rsID='" + getRealmSecurityIdentifier()
			        + "', no security constraints provided, returning true");
			return true;
		}

		// for each security-constraint element in the web configuration
		for( SecurityConstraint constraint : constraints )
		{
			// get the user-data-constraints element content
			String userConstraint = constraint.getUserConstraint();
			if (userConstraint == null)
			{
				getLogger().info("  hasUserDataPermission (" + request.getRequestURI() + ") rsID='" + getRealmSecurityIdentifier()
				        + "', applicable security constraint provided with no user constraint, returning true");
				return true;
			}
			if( Constants.NONE_TRANSPORT.equals(userConstraint) )
			{
				getLogger().info("  hasUserDataPermission (" + request.getRequestURI() + ") rsID='" + getRealmSecurityIdentifier()
				        + "', applicable security constraint provided with no transport security required, returning true");
				return true;
			}
		}
		
		// The request is to a secure area that requires either INTEGRAL or CONFIDENTIAL
		// transport guarantee. If the request is not already secure then redirect it
		// to the secure port.
		if( request.isSecure() )
			return true;
		else
		{
			// Validate the request against the user data constraint
			/*
			 * if( request.getRequest().isSecure() ) { logger.debug(" User data
			 * constraint already satisfied for request [" + request.getRequestURI() +
			 * "]"); return true; }
			 */
			
			// Initialize variables we need to determine the appropriate action
			int redirectPort = request.getConnector().getRedirectPort();

			// Is redirecting disabled?
			if (redirectPort <= 0)
			{
				getLogger().info("  hasUserDataPermission (" + request.getRequestURI() + ") rsID='" + getRealmSecurityIdentifier()
				        + "', SSL redirect is disabled, returning false (HTTP 403)");
				response.sendError(403, request.getRequestURI());
				return false;
			}

			// Redirect to the corresponding SSL port
			StringBuffer file = new StringBuffer();
			String protocol = "https";
			String host = request.getServerName();
			// Protocol
			file.append(protocol).append("://").append(host);
			// Host with port
			if (redirectPort != 443)
				file.append(":").append(redirectPort);

			// URI
			file.append(request.getRequestURI());
			String requestedSessionId = request.getRequestedSessionId();
			if ((requestedSessionId != null) && request.isRequestedSessionIdFromURL())
			{
				file.append(";jsessionid=");
				file.append(requestedSessionId);
			}

			String queryString = request.getQueryString();
			if (queryString != null)
			{
				file.append('?');
				file.append(queryString);
			}
			getLogger().debug("  Redirecting request [" + request.getRequestURI() + "] to [" + file.toString() + "]");
			response.sendRedirect(file.toString());

			getLogger().info("  hasUserDataPermission (" + request.getRequestURI() + ") rsID='" + getRealmSecurityIdentifier()
			                + " returning false");
			return false;
		}
		
	}

	/*
	 * =======================================================================================================
	 */
	/**
	 * Return the SecurityConstraints configured to guard the request URI for
	 * this request, or <code>null</code> if there is no such constraint.
	 * 
	 * <security-constraint> <web-resource-collection> <web-resource-name>
	 * <description> <url-pattern> <http-method> <auth-constraint> <role-name>
	 * <user-data-constraint> <transport-guarantee>
	 * 
	 * The web-resource-collection element is used to identify a subset of the
	 * resources and HTTP methods on those resources within a web application to
	 * which a security constraint applies. If no HTTP methods are specified,
	 * then the security constraint applies to all HTTP methods.
	 * 
	 * The url-pattern element contains the url pattern of the mapping. Must
	 * follow the rules specified in Section 11.2 of the Servlet API
	 * Specification. Essentially that means; "/xxx/*" matches anything starting
	 * with /xxx "*.xxx" matches anything ending with .xxx anything else matches
	 * exactly.
	 * 
	 * The http-method contains an HTTP method (GET | POST |...). No http-method
	 * matches any method
	 * 
	 * @param request
	 *            Request we are processing
	 * @param context
	 *            Context the Request is mapped to
	 */
	public SecurityConstraint[] findSecurityConstraints(Request request, Context context)
	{
		// findSecurityConstraints is the first call made for each transaction,
		// create the unique ID
		setRealmSecurityIdentifier();

		// The mapping to applicable SecurityConstraint is done on the requestURI and the request method.
		// The requestPathInfo will contain the path, including the context.
		// e.g. /Vix/secure/TransactionLog.jspx 
		// where: "Vix" is the context name
		// and "secure/TransactionLog.jspx" is the servlet path and additional path info
		// the query string is NOT included
		// The requestMethod is (probably) an HTTP request method.
		String requestPathInfo = request.getRequestURI();
		String requestMethod = request.getMethod();
		
		// requestContextPath is the path to the Context
		String requestContextPath = request.getContextPath();
		
		// get the portion of the URL which is mapped in the web context
		// under <security-constraint> <web-resource-collection> <url-pattern>
		String contextRequestPath = requestPathInfo;
		
		// As far as I've been able to tell, this next test will always be true,
		// that is the requestPathInfo always starts with the context.
		// The contextRequestPath is the path without the context part of the path.
		if (requestPathInfo.startsWith(requestContextPath))
			contextRequestPath = requestPathInfo.substring(requestContextPath.length());
		
		
		return findSecurityConstraints(context, requestPathInfo, requestMethod, contextRequestPath);
	}

	/**
	 * Return an Array of SecurityConstraint applicable to the resource as specified by the
	 * Context, the request path and the method.
	 * 
     * @param context - the application Context
     * @param requestPathInfo - the request path (from the servlet request)
     * @param requestMethod - the request method, probably an HTTP method
     * @param contextRequestPath - the path within the context of the requested resource
     *                             the context name appended to the contextRequestPath should be the requestPathInfo
     * 
     * @return
     */
    private SecurityConstraint[] findSecurityConstraints(
    		Context context, 
    		String requestPathInfo, 
    		String requestMethod,
            String contextRequestPath)
    {
	    // The SecurityConstraintKey uses the requestPathInfo (with the context path) so tha the cached
		// keys include the context (otherwise we'd apply a premission in one web app to the same path
		// in another web app.
		SecurityConstraintCacheKey constraintKey = new SecurityConstraintCacheKey(requestPathInfo, requestMethod);

		getLogger().debug(
				"findSecurityConstraints(" + context.getName() + ", " + constraintKey.toString() + 
				") rsID='" + getRealmSecurityIdentifier() + "'.");

		SecurityConstraint[] result = null;
		// if security constraint caching is turned on then look there first
		// the cached security constraints are indexed by the context and the constraintKey, which
		// is a combination of request path and request method.
		result = getCachedSecurityConstraint(context, constraintKey);
		getLogger().debug("findSecurityConstraints(" + constraintKey.toString() + ") rsID='" + getRealmSecurityIdentifier()
		        + "' located " + (result == null ? 0 : result.length) + " constraints in cache");
		
		// the constraints for this URL were not cached
		if( result == null )
		{
			// Are there any defined security constraints?
			// if not then return null, i.e. if there are no security constraints
			// then
			// there are no applicable security constraints
			SecurityConstraint[] contextSecurityConstraints = context.findConstraints();
			getLogger().debug(
					"findSecurityConstraints(" + constraintKey.toString() + 
					") rsID='" + getRealmSecurityIdentifier() +
			        "'  " + (contextSecurityConstraints == null ? 0 : contextSecurityConstraints.length) + 
			        " security constraints defined for entire context '" + context.getName() + 
			        "', all requests will be granted if no security constraints are defined.");
			
			// some security constraints are defined, find the ones applicable to this request 
			if( contextSecurityConstraints != null || contextSecurityConstraints.length > 0 )
			{
				getLogger().debug("findSecurityConstraints finding security constraints applicable to path [" + contextRequestPath + "] from "
				        + contextSecurityConstraints.length + " constraints in context");

				// selects the security constraints that match the path info, and method
				ArrayList<SecurityConstraintMatch> contextMatchedSecurityConstraints = 
					getMatchingSecurityConstraints(contextSecurityConstraints, contextRequestPath, requestMethod);

				if (contextMatchedSecurityConstraints == null || contextMatchedSecurityConstraints.size() < 1)
					getLogger().debug("No applicable security constraints found for '" + constraintKey.toString() + "'.");
				else
					Collections.sort(contextMatchedSecurityConstraints);

				getLogger().debug("For '" + constraintKey.toString() + "' adding " + contextMatchedSecurityConstraints.size() + " security constraints to cache");

				putToSecurityConstraintsCache(context, constraintKey, contextMatchedSecurityConstraints);

				getLogger().debug("findSecurityConstraints(" + constraintKey.toString() + ") rsID='" + getRealmSecurityIdentifier() + "' - returning "
				        + contextMatchedSecurityConstraints.size() + " security constraints.");

				// returns null if the List is null or zero length
				result = resultsToArray(contextMatchedSecurityConstraints);
			}
		}
		
		// if the result is empty then we need to return null.  An empty array is not the same as a null array (they are handled differently).
		if((result == null) || (result.length <= 0))
			return null;
		
		return result;
    }

	/**
     * @param requestPathInfo
     * @param requestMethod
     * @param securityConstraints
     * @return
     */
    private ArrayList<SecurityConstraintMatch> getMatchingSecurityConstraints(
    	SecurityConstraint[] securityConstraints, 
    	String requestPathInfo, 
    	String requestMethod)
    {
	    ArrayList<SecurityConstraintMatch> results = 
	    	new ArrayList<SecurityConstraintMatch>();
	    
		// for each of the <security-constraint> elements (in the web app
		// deployment descriptor)
		for (SecurityConstraint securityConstraint : securityConstraints)
		{
			getLogger().debug("  Evaluating <security-constraint> '" + securityConstraint + "' against " + requestMethod + "-" + requestPathInfo);

			// Find the web-resource-collection elements within the security
			// constraints
			// A web-resource-collection element includes the url-pattern and
			// http-method elements. The request
			// must match against both url-pattern and http-method.
			SecurityCollection[] webResourceCollections = securityConstraint.findCollections();

			if (webResourceCollections == null)
			{
				getLogger().debug("  <security-constraint> '" + securityConstraint + "' has no web resources defined, ignoring ...");
				continue;
			}

			// logger.debug(" Checking constraint '" + securityConstraint +
			// "' against " + requestMethod + " " + pathInfo + " --> " +
			// securityConstraint.included(pathInfo, requestMethod));

			// search for any exact matches to the requestURI
			for (SecurityCollection webResourceCollection : webResourceCollections)
			{
				getLogger().debug("    Checking <web-resource-collection> '" + webResourceCollection + "' against " + requestMethod + "-"
				        + requestPathInfo);

				// get the url-pattern elements within the
				// web-resource-collection
				String[] urlPatterns = webResourceCollection.findPatterns();

				if (urlPatterns == null)
					continue;

				// for each pattern in the web-resource-collection
				// In the Web application deployment descriptor, the following
				// syntax is used to define
				// mappings:
				// • A string beginning with a ‘/’ character and ending with a
				// ‘/*’ suffix is used
				// for path mapping.
				// • A string beginning with a ‘*.’ prefix is used as an
				// extension mapping.
				// • A string containing only the ’/’ character indicates the
				// "default" servlet of
				// the application. In this case the servlet path is the request
				// URI minus the context
				// path and the path info is null.
				// • All other strings are used for exact matches only.
				//
				// Exact match takes precedence over path and extension.
				// Path match takes precedence over extension.
				// Default servlet matches anything else.
				for (int urlPatternIndex = 0; urlPatternIndex < urlPatterns.length; urlPatternIndex++)
				{
					String urlPattern = urlPatterns[urlPatternIndex];
					boolean patternIsDefaultMatch = "/".equals(urlPattern);
					boolean patternIsPathMatch = urlPattern.startsWith("/") && urlPattern.endsWith("/*");
					boolean patternIsAnyPathMatch = "/*".equals(urlPattern);
					boolean patternIsExtensionMatch = urlPattern.startsWith("*.");
					boolean patternIsExactMatch = !patternIsDefaultMatch && !patternIsAnyPathMatch && !patternIsPathMatch
					        && !patternIsExtensionMatch;

					getLogger().debug("    Checking <url-pattern> '" + urlPattern + (patternIsDefaultMatch ? "' as default match" : "")
					        + (patternIsAnyPathMatch ? "' as any path match" : "") + (patternIsExactMatch ? "' as exact match" : "")
					        + (patternIsPathMatch ? "' as path match" : "") + (patternIsExtensionMatch ? "' as extension match" : "")
					        + "' against " + requestMethod + " " + requestPathInfo);

					if (patternIsDefaultMatch && webResourceCollection.findMethod(requestMethod))
					{
						getLogger().debug("Default match '" + urlPattern + "' found");
						results.add(SecurityConstraintMatch.createDefaultSecurityConstraintMatch(securityConstraint, urlPattern));
					} else if (patternIsAnyPathMatch && webResourceCollection.findMethod(requestMethod))
					{
						getLogger().debug("Any path pattern match '" + urlPattern + "' found");
						results.add(SecurityConstraintMatch.createPathSecurityConstraintMatch(securityConstraint, urlPattern));
					}
					// if the url-pattern is an exact match for the request URI
					// then add it directly to the results array
					else if (patternIsExactMatch && requestPathInfo.equals(urlPattern) && webResourceCollection.findMethod(requestMethod))
					{
						getLogger().debug("Exact pattern match '" + urlPattern + "' found");
						results.add(SecurityConstraintMatch.createExactSecurityConstraintMatch(securityConstraint, urlPattern));
					} else if (patternIsPathMatch)
					{
						String[] urlPatternPathComponents = urlPattern.split("/");
						String[] requestComponents = requestPathInfo.split("/");
						int patternLength = urlPatternPathComponents.length;
						int requestComponentsLength = requestComponents.length;

						// if the request has fewer elements than the pattern
						// then it cannot match
						boolean matches = requestComponentsLength >= patternLength;
						for (int patternComponentIndex = 0; matches && patternComponentIndex < patternLength
						        && (!"*".equals(urlPatternPathComponents[patternComponentIndex])); ++patternComponentIndex)
						{
							String patternComponent = urlPatternPathComponents[patternComponentIndex];
							String requestComponent = requestComponents[patternComponentIndex];

							getLogger().debug("      Checking <url-pattern> path component '" + patternComponent + "' to request component '"
							        + requestComponent + "'");

							// if path component doesn't match, set matches to
							// false
							matches = patternComponent.equals(requestComponent);
						}
						// if we matched all of the pattern components
						if (matches)
						{
							getLogger().debug("Path pattern match '" + urlPattern + "' found");
							results.add(SecurityConstraintMatch.createPathSecurityConstraintMatch(securityConstraint, urlPattern));
						}
					} else if (patternIsExtensionMatch)
					{
						int extensionIndex = urlPattern.indexOf('.');
						String extension = urlPattern.substring(extensionIndex);
						if (requestPathInfo.endsWith(extension) && webResourceCollection.findMethod(requestMethod))
						{
							getLogger().debug("Extension pattern match '" + urlPattern + "' found");
							results.add(SecurityConstraintMatch.createExtensionSecurityConstraintMatch(securityConstraint, urlPattern));
						}
					}
				}
			}
		}
	    return results;
    }

	/**
	 * Convert an ArrayList to a SecurityContraint [].
	 */
	private SecurityConstraint[] resultsToArray(ArrayList<SecurityConstraintMatch> results)
	{
		if (results == null || results.size() < 1)
			return null;

		SecurityConstraint[] array = new SecurityConstraint[results.size()];

		int index = 0;
		for (SecurityConstraintMatch match : results)
		{
			array[index] = match.getSecurityConstraint();
			++index;
		}

		return array;
	}
	
	
	// =================================================================================================
	// Inner classes
	// Cache keys and values
	// =================================================================================================

	class FullyQualifiedPrincipalName
	{
		private final String realm;
		private final String name;

		public FullyQualifiedPrincipalName(VistaRealmPrincipal principal)
		{
			this.realm = principal.getRealm();
			this.name = principal.getAccessCode();
		}

		public FullyQualifiedPrincipalName(String realm, String name)
		{
			this.realm = realm;
			this.name = name;
		}

		public String getRealm()
		{
			return realm;
		}

		public String getName()
		{
			return name;
		}

		@Override
		public String toString()
		{
			return getRealm() + "/" + getName();
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((realm == null) ? 0 : realm.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final FullyQualifiedPrincipalName other = (FullyQualifiedPrincipalName) obj;
			if (name == null)
			{
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (realm == null)
			{
				if (other.realm != null)
					return false;
			} else if (!realm.equals(other.realm))
				return false;
			return true;
		}
	}

	/**
	 * 
	 */
	class PrincipalCacheValue
	{
		private long date = System.currentTimeMillis();
		private VistaRealmPrincipal principal = null;

		PrincipalCacheValue(VistaRealmPrincipal principal)
		{
			// this.principal = principal.clone();
			this.principal = principal;
		}

		public long getDate()
		{
			return this.date;
		}

		public VistaRealmPrincipal getPrincipal()
		{
			return this.principal;
		}

		/**
		 * Set the inception date on this cache entry to the current date
		 */
		void touch()
		{
			this.date = System.currentTimeMillis();
		}
	}

	/**
	 * 
	 * @author VHAISWBECKEC
	 * 
	 */
	class SecurityConstraintCacheKey
	{
		private String requestUri;
		private String httpMethod;

		public SecurityConstraintCacheKey(String requestUri, String httpMethod)
		{
			super();
			this.requestUri = requestUri;
			this.httpMethod = httpMethod;
		}

		public String getHttpMethod()
		{
			return this.httpMethod;
		}

		public String getRequestUri()
		{
			return this.requestUri;
		}

		@Override
		public int hashCode()
		{
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((this.httpMethod == null) ? 0 : this.httpMethod.hashCode());
			result = PRIME * result + ((this.requestUri == null) ? 0 : this.requestUri.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SecurityConstraintCacheKey other = (SecurityConstraintCacheKey) obj;
			if (this.httpMethod == null)
			{
				if (other.httpMethod != null)
					return false;
			} else if (!this.httpMethod.equals(other.httpMethod))
				return false;
			if (this.requestUri == null)
			{
				if (other.requestUri != null)
					return false;
			} else if (!this.requestUri.equals(other.requestUri))
				return false;
			return true;
		}

		@Override
		public String toString()
		{
			return this.getClass().getSimpleName() + ":" + this.httpMethod + "-" + this.requestUri;
		}
	}

	/**
	 * 
	 * @author VHAISWBECKEC
	 * 
	 */
	class SecurityConstraintCacheValue
	{
		private long date = System.currentTimeMillis();
		private SecurityConstraint[] constraints;

		public SecurityConstraintCacheValue(SecurityConstraint[] constraints)
		{
			super();
			// make a copy of the array so that if someone else messes with it,
			// our copy is still valid
			this.constraints = new SecurityConstraint[constraints.length];
			System.arraycopy(constraints, 0, this.constraints, 0, constraints.length);
		}

		public SecurityConstraint[] getConstraints()
		{
			return this.constraints;
		}

		public long getDate()
		{
			return this.date;
		}

		void touch()
		{
			this.date = System.currentTimeMillis();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.siteNumber == null) ? 0 : this.siteNumber.hashCode());
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
		AbstractVistaRealmImpl other = (AbstractVistaRealmImpl) obj;
		if (this.siteNumber == null)
		{
			if (other.siteNumber != null)
				return false;
		}
		else if (!this.siteNumber.equals(other.siteNumber))
			return false;
		return true;
	}
}
