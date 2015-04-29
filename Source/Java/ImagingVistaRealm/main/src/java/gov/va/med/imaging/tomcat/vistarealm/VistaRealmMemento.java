package gov.va.med.imaging.tomcat.vistarealm;

import java.io.Serializable;

/**
 * A serializable representation of the state of a VistaRealm instance.
 * This is used to store and load the persistent state and thereby recreate
 * an instance of VistaRealm between server sessions.
 * 
 * @author VHAISWBECKEC
 *
 */
public class VistaRealmMemento
implements Serializable
{
	private static final long serialVersionUID = -859854281004252556L;
	
	private boolean usingPrincipalCache;
	private boolean usingSecurityConstraintCache;
	private long principalCacheLifespan;
	private long securityConstraintCacheLifespan;
	private boolean refreshPrincipalCacheEntryOnUse;
	private boolean refreshSecurityConstraintCacheEntryOnUse;
	private int vistaRealmDelayKludge;
	private boolean initialized;

	public VistaRealmMemento()
	{}

	public long getPrincipalCacheLifespan()
	{
		return this.principalCacheLifespan;
	}

	public void setPrincipalCacheLifespan(long principalCacheLifespan)
	{
		this.principalCacheLifespan = principalCacheLifespan;
	}

	public boolean isRefreshPrincipalCacheEntryOnUse()
	{
		return this.refreshPrincipalCacheEntryOnUse;
	}

	public void setRefreshPrincipalCacheEntryOnUse(
			boolean refreshPrincipalCacheEntryOnUse)
	{
		this.refreshPrincipalCacheEntryOnUse = refreshPrincipalCacheEntryOnUse;
	}

	public boolean isRefreshSecurityConstraintCacheEntryOnUse()
	{
		return this.refreshSecurityConstraintCacheEntryOnUse;
	}

	public void setRefreshSecurityConstraintCacheEntryOnUse(
			boolean refreshSecurityConstraintCacheEntryOnUse)
	{
		this.refreshSecurityConstraintCacheEntryOnUse = refreshSecurityConstraintCacheEntryOnUse;
	}

	public long getSecurityConstraintCacheLifespan()
	{
		return this.securityConstraintCacheLifespan;
	}

	public void setSecurityConstraintCacheLifespan(
			long securityConstraintCacheLifespan)
	{
		this.securityConstraintCacheLifespan = securityConstraintCacheLifespan;
	}

	public boolean isUsingPrincipalCache()
	{
		return this.usingPrincipalCache;
	}

	public void setUsingPrincipalCache(boolean usingPrincipalCache)
	{
		this.usingPrincipalCache = usingPrincipalCache;
	}

	public boolean isUsingSecurityConstraintCache()
	{
		return this.usingSecurityConstraintCache;
	}

	public void setUsingSecurityConstraintCache(boolean usingSecurityConstraintCache)
	{
		this.usingSecurityConstraintCache = usingSecurityConstraintCache;
	}

	public int getVistaRealmDelayKludge()
	{
		return this.vistaRealmDelayKludge;
	}

	public void setVistaRealmDelayKludge(int vistaRealmDelayKludge)
	{
		this.vistaRealmDelayKludge = vistaRealmDelayKludge;
	}

	public boolean isInitialized()
	{
		return this.initialized;
	}

	public void setInitialized(boolean initialized)
	{
		this.initialized = initialized;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("VistaRealmMemento \n" ); 
		sb.append("  initialized = '" + initialized + "'\n");
		sb.append("  vistaRealmDelayKludge = '" + vistaRealmDelayKludge + "'\n");
		sb.append("  Principal Cache\n");
		sb.append("    usingPrincipalCache = '" + usingPrincipalCache + "'\n");
		sb.append("    principalCacheLifespan = '" + principalCacheLifespan + "'\n");
		sb.append("    refreshPrincipalCacheEntryOnUse = '" + refreshPrincipalCacheEntryOnUse + "'\n");
		sb.append("  Security Constraint Cache\n");
		sb.append("    usingSecurityConstraintCache = '" + usingSecurityConstraintCache + "'\n");
		sb.append("    securityConstraintCacheLifespan = '" + securityConstraintCacheLifespan + "'\n");
		sb.append("    refreshSecurityConstraintCacheEntryOnUse = '" + refreshSecurityConstraintCacheEntryOnUse + "'\n");
		
		return sb.toString();
	}
}