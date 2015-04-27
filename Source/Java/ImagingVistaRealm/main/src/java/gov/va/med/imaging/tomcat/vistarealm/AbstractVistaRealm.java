package gov.va.med.imaging.tomcat.vistarealm;

/**
 * defines the core interface for all VIX realm implementations
 * @author VHAISWBECKEC
 *
 */
public interface AbstractVistaRealm
extends PreemptiveAuthorization
{
	public abstract String getRealmName();

	public abstract boolean isInitialized();

	public abstract String getRealmImplementationName();

	public abstract float getVersion();

	// ===========================================================================================================
	// JavaBean Property Accessors
	// These properties may be set from the server configuration.
	// ===========================================================================================================
	/**
	 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getSiteAbbr()
	 */
	public abstract String getSiteAbbreviation();

	/**
	 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getSiteName()
	 */
	public abstract String getSiteName();

	/**
	 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getSiteNumber()
	 */
	public abstract String getSiteNumber();

	/**
	 * 
	 * @return
	 */
	public abstract Boolean isUsingPrincipalCache();
	public abstract void setUsingPrincipalCache(Boolean usingPrincipalCache);

	/**
	 * the lifespan of a principal cache item in milliseconds
	 * 
	 * @return
	 */
	public abstract Long getPrincipalCacheLifespan();
	public abstract void setPrincipalCacheLifespan(Long principalCacheLifespan);

	/**
	 * If true then the incept date of a principal cache entry is refreshed each
	 * time it is used.
	 * 
	 * @return
	 */
	public abstract Boolean isRefreshPrincipalCacheEntryOnUse();
	public abstract void setRefreshPrincipalCacheEntryOnUse(Boolean refreshPrincipalCacheEntryOnUse);

	/**
	 * The number of entries currently in the principal cache
	 * 
	 * @return
	 */
	public abstract int getPrincipalCacheSize();

	/**
	 * Get the time that the principal cache was last cleaned of old entries
	 * 
	 * @return
	 */
	public abstract long getLastPrincipalCacheSweepDate();
	

	/**
	 * 
	 */
	public void evictOldPrincipalCacheEntries();

	/**
	 * Clear all principal cache entries
	 */
	public void clearPrincipalCache();


	/**
	 * The number of entries currently in the security constraints cache. Note
	 * that this is equivalent to the number of Context (web apps) that the
	 * realm is servicing and not the number of security constraints per realm
	 * 
	 * @return
	 */
	public abstract int getSecurityConstraintCacheSize();
}