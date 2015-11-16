package gov.va.med.server;

import java.util.Map;

/**
 * This class serves as a wrapper of the information used to identify a security realm
 * in the server.  The realm class is optional, and its accessibility will require that the 
 * calling client references the real so it is not usually provided.
 * The property map correlates property names, as in bean property names.  Access to the property
 * is contingent upon a standard named accessor, i.e. property siteId must have a getSiteId() method.
 * The values in the property map must be .equals() comparable to the value returned from the accessor
 * for a property to match.  All values must match for a realm to be matched.
 * If the property map is null or empty then any realm will match.  If the realm class is omitted then any
 * realm class will match.
 *  
 * @author vhaiswbeckec
 *
 */
public class SecurityRealmIdentification
{
	private final Class<?> realmClass;
	private final String realmClassName;
	private final Map<String, Object> propertyMap;
	/**
	 * @param realmClass
	 * @param propertyMap
	 */
	public SecurityRealmIdentification(Class<?> realmClass, Map<String, Object> propertyMap)
	{
		super();
		this.realmClass = realmClass;
		this.realmClassName = null;
		this.propertyMap = propertyMap;
	}
	
	public SecurityRealmIdentification(String realmClassName, Map<String, Object> propertyMap)
	{
		super();
		this.realmClass = null;
		this.realmClassName = realmClassName;
		this.propertyMap = propertyMap;
	}
	
	/**
	 * @return the realmClass
	 */
	public Class<?> getRealmClass()
	{
		return this.realmClass;
	}

	/**
	 * @return the realmClass
	 */
	public String getRealmClassName()
	{
		return this.realmClassName;
	}
	
	/**
	 * @return the propertyMap
	 */
	public Map<String, Object> getPropertyMap()
	{
		return this.propertyMap;
	}

}