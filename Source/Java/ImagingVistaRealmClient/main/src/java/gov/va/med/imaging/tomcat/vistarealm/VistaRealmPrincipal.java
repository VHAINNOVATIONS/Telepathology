package gov.va.med.imaging.tomcat.vistarealm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 
 * @author Julian Werfel
 * 
 * CTB - added implements of Principal because this will be used as the
 * Principal returned from the VistaRealm
 *
 */
public class VistaRealmPrincipal 
implements Principal, Cloneable, Serializable
{
	private static final long serialVersionUID = 6018999512058296885L;

	private static final VistaRealmPrincipal nullInstance;
	
	static
	{
		nullInstance = new VistaRealmPrincipal( null, false, AuthenticationCredentialsType.Password );
	}
	
	public static VistaRealmPrincipal getNullInstance()
	{
		return nullInstance;
	}
	
	public enum AuthenticationCredentialsType
	implements Serializable
	{
		Password, X509Certificate
	}
	private Boolean authenticatedByVista;
	private Boolean authenticatedByDelegate; 
	private String realm;
	private AuthenticationCredentialsType credentialsType;
	private List<String> roles = new ArrayList<String>();
	private String fullName = "";
	private String siteNumber = "";
	private String siteName = "";
	private String accessCode = "";
	private String verifyCode = "";
	private String ssn = "";
	private String duz = "";
	private String securityToken = "";
	private String cacheLocationId = "";
	private String userDivision = "";
	private final Map<String, Object> applicationProperties = Collections.synchronizedMap( new HashMap<String, Object>() );
	private List<X509Certificate> certs = new ArrayList<X509Certificate>();
	private transient PreemptiveAuthorization preemptiveAuthorization;

	/**
	 * Construct a minimally populated VistaRealmPrincipal instance.
	 * 
	 * @param realm
	 * @param authenticatedByDelegate
	 * @param credentialType
	 */
	public VistaRealmPrincipal(String realm, boolean authenticatedByDelegate, AuthenticationCredentialsType credentialType) 
	{
		super();
		this.realm = realm;		// unknown security realm
		this.authenticatedByDelegate = authenticatedByDelegate;
		this.credentialsType = credentialType;
		this.authenticatedByVista = false;
	}

	/**
	 * Construct a VistaRealmPrincipal instance authenticated by certificate.
	 * 
	 * @param realm
	 * @param accessCode
	 * @param certs
	 * @param roles
	 * @param applicationProperties
	 */
	public VistaRealmPrincipal(
		String realm,
		String accessCode,
		List<X509Certificate> certs, 
		List<String> roles, 
		Map<String, Object> applicationProperties)
	{
		super();
		this.realm = realm;
		this.authenticatedByDelegate = true; // not ideal but necessary
		this.credentialsType = AuthenticationCredentialsType.X509Certificate;
		this.certs = certs;
		this.authenticatedByVista = false;
		setAccessCode(accessCode);
		addRoles(roles);
		
		// make a deep copy of the generic parameters map if it exists
		if(applicationProperties != null)
			for(String propertyKeys : applicationProperties.keySet() )
				setApplicationProperty( propertyKeys, applicationProperties.get(propertyKeys) );
	}

	public VistaRealmPrincipal(
			String realm,
			String accessCode,
			String verifyCode,
			List<X509Certificate> certs, 
			List<String> roles, 
			Map<String, Object> applicationProperties)
		{
			this(realm, accessCode, certs, roles, applicationProperties);
			setVerifyCode(verifyCode);
		}
	
	/**
	 * Construct a fully populated VistaRealmPrincipal instance.
	 * 
	 * @param realm
	 * @param authenticatedByDelegate
	 * @param credentialType
	 * @param accessCode
	 * @param verifyCode
	 * @param duz
	 * @param fullName
	 * @param ssn
	 * @param siteNumber
	 * @param siteName
	 * @param roles
	 * @param applicationProperties
	 */
	public VistaRealmPrincipal(
			String realm, 
			boolean authenticatedByDelegate,
			AuthenticationCredentialsType credentialType,
			String accessCode, String verifyCode, 
			String duz, String fullName, String ssn, 
			String siteNumber, String siteName,
			List<String> roles,
			Map<String, Object> applicationProperties)
	{
		super();
		this.realm = realm;
		this.authenticatedByDelegate = authenticatedByDelegate;
		this.credentialsType = credentialType;
		this.authenticatedByVista = false;
		setAccessCode(accessCode);
		setVerifyCode(verifyCode);
		setDuz(duz);
		setFullName(fullName);
		setSiteNumber(siteNumber);
		setSiteName(siteName);
		setSsn(ssn);
		addRoles(roles);
		
		// make a deep copy of the generic parameters map if it exists
		if(applicationProperties != null)
			for(String propertyKeys : applicationProperties.keySet() )
			{
				Object propertyValue = applicationProperties.get(propertyKeys);
				if(propertyValue instanceof Cloneable)
				{
					Method cloneMethod;
					try
					{
						cloneMethod = propertyValue.getClass().getDeclaredMethod("clone", (Class<?>)null);
					}
					catch (Exception x)
					{
						x.printStackTrace();
						cloneMethod = null;
					}
					
					if(cloneMethod != null)
					{
						Object clonedValue = null;
						try
						{
							clonedValue = cloneMethod.invoke(propertyValue, (Object[])null);
						}
						catch (Exception x)
						{
							System.err.println("Failed to copy application property '" + propertyKeys + "', because its clone() method failed.");
						}
						setApplicationProperty( propertyKeys, clonedValue );
					}
					else
						System.err.println("Unable to copy application property '" + propertyKeys + "', because its clone() method does not exist but it is marked cloneable.");
				}
				// Strings can get away with a reference because they are immutable
				else if(propertyValue instanceof java.lang.String)
					setApplicationProperty( propertyKeys, propertyValue );
				
				// make a deep copy of everything else, so references do not get confused
				else if(propertyValue instanceof java.lang.Boolean)
					setApplicationProperty( propertyKeys, new Boolean( ((Boolean)propertyValue).booleanValue()) );
				else if(propertyValue instanceof java.lang.Byte)
					setApplicationProperty( propertyKeys, new Byte( ((Byte)propertyValue).byteValue()) );
				else if(propertyValue instanceof java.lang.Character)
					setApplicationProperty( propertyKeys, new Character( ((Character)propertyValue).charValue()) );
				else if(propertyValue instanceof java.lang.Short)
					setApplicationProperty( propertyKeys, new Short( ((Short)propertyValue).shortValue()) );
				else if(propertyValue instanceof java.lang.Integer)
					setApplicationProperty( propertyKeys, new Integer( ((Integer)propertyValue).intValue()) );
				else if(propertyValue instanceof java.lang.Long)
					setApplicationProperty( propertyKeys, new Long( ((Long)propertyValue).longValue()) );
				else if(propertyValue instanceof java.lang.Float)
					setApplicationProperty( propertyKeys, new Float( ((Float)propertyValue).floatValue()) );
				else if(propertyValue instanceof java.lang.Double)
					setApplicationProperty( propertyKeys, new Double( ((Double)propertyValue).doubleValue()) );
				else
					System.err.println("Unable to copy application property '" + propertyKeys + "', because it is an unknown type and does not support cloneable.");
			}
	}

	public String getRealm()
	{
		return this.realm;
	}

	/**
	 * @param authenticatedByVista the authenticatedByVista to set
	 */
	public void setAuthenticatedByVista(Boolean authenticatedByVista) {
		this.authenticatedByVista = authenticatedByVista;
	}
	
	public boolean isAuthenticatedByVista()
	{
		return this.authenticatedByVista;
	}

	/**
	 * Return true if the Principal was authenticated against a delegate REalm rather than
	 * by the local VistA installation.
	 * 
	 * @return the authenticatedByDelegate
	 */
	public boolean isAuthenticatedByDelegate()
	{
		return this.authenticatedByDelegate;
	}

	public List<String> getRoles()
	{
		return this.roles;
	}
	
	/**
	 * Indicates the type of credentials used in the authentication and also the type of credentials
	 * available from this instance.
	 * 
	 * @return the credentialsType
	 */
	public AuthenticationCredentialsType getCredentialsType()
	{
		return this.credentialsType;
	}

	// do all the role additions through here to
	// assure that there are no duplicates
	public synchronized void addRole(String role)
	{
		if(roles.indexOf(role) < 0)
			roles.add(role);
	}

	public synchronized void addRoles(Collection<String> roles)
	{
		for(String role: roles)
			this.roles.add(role);
	}
	
	public synchronized void removeRole(String role)
	{
		roles.remove(role);
	}
	
	public String getAccessCode() {
		return accessCode;
	}

	public synchronized void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public synchronized void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteNumber() {
		return siteNumber;
	}

	public synchronized void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public synchronized void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getSsn() {
		return ssn;
	}

	public synchronized void setSsn(String ssn) {
		this.ssn = ssn;
	}

	/**
	 * @return the securityToken
	 */
	public String getSecurityToken() {
		return securityToken;
	}

	/**
	 * @param securityToken the securityToken to set
	 */
	public synchronized void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}
	
	/**
	 * @return the cacheLocationId
	 */
	public String getCacheLocationId() {
		return cacheLocationId;
	}

	/**
	 * @param cacheLocationId the cacheLocationId to set
	 */
	public synchronized void setCacheLocationId(String cacheLocationId) {
		this.cacheLocationId = cacheLocationId;
	}

	/**
	 * @return the userDivision
	 */
	public String getUserDivision() {
		return userDivision;
	}

	/**
	 * @param userDivision the userDivision to set
	 */
	public synchronized void setUserDivision(String userDivision) {
		this.userDivision = userDivision;
	}

	public String getDuz() {
		return duz;
	}

	public synchronized void setDuz(String duz) {
		this.duz = duz;
	}

	public String getFullName() {
		return fullName;
	}

	public synchronized void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName()
	{
		return getAccessCode();
	}

	/**
     * @return the certs
     */
    public List<X509Certificate> getCerts()
    {
    	return certs;
    }

	/**
	 * Set an application property that will be maintained as part of the
	 * Principal instance for the transaction.
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized void setApplicationProperty(String key, Object value)
	{
		applicationProperties.put(key, value);
	}
	
	/**
	 * Get an application property.
	 * 
	 * @param key
	 * @return
	 */
	public Object getApplicationProperty(String key)
	{
		return applicationProperties.get(key);
	}
	
	/**
	 * Clear all of the application properties.  The application properties are cleared by the
	 * realm before returning the Principal.  In effect this makes all application properties
	 * transaction specific. 
	 *
	 */
	public synchronized void clearApplicationProperties()
	{
		applicationProperties.clear();
	}

	/**
     * @return the preemptiveAuthorization
     */
    public PreemptiveAuthorization getPreemptiveAuthorization()
    {
    	return preemptiveAuthorization;
    }
    public synchronized void setPreemptiveAuthorization(PreemptiveAuthorization preemptiveAuthorization)
    {
    	this.preemptiveAuthorization = preemptiveAuthorization;
    }

	/**
	 * 
	 * @param context
	 * @param contextRelativePath
	 * @param method
	 * @return
	 */
	public PreemptiveAuthorization.Result isAuthorized(
		Object context, 
		String contextRelativePath, 
		String method)
	{
		if(getPreemptiveAuthorization() != null)
			return getPreemptiveAuthorization().isAuthorized(this, context, contextRelativePath, method);
		
		return PreemptiveAuthorization.Result.Unknown;
	}
    /**
     * Does the user represented by this Principal possess the specified role?
     *
     * @param role Role to be tested
     */
    public boolean hasRole(String role) 
    {
        if("*".equals(role)) // Special 2.4 role meaning everyone
            return true;
        if (role == null)
            return (false);

        for(Iterator<String> roleIter = roles.iterator(); roleIter.hasNext(); )
        	if(role.equalsIgnoreCase(roleIter.next()))
        		return true;
        
        return false;
    }

	/**
	 * Return a deep copy of this instance.
	 */
	@Override
	public VistaRealmPrincipal clone()
	{
		// create a clone by calling the constructor
		VistaRealmPrincipal clone = new VistaRealmPrincipal(
				this.realm, 
				this.isAuthenticatedByDelegate(),
				this.credentialsType,
				this.accessCode, 
				this.verifyCode, 
				this.duz, 
				this.fullName, 
				this.ssn, 
				this.siteNumber, 
				this.siteName, 
				this.roles,
				this.applicationProperties
		);
		clone.setAuthenticatedByVista(this.isAuthenticatedByVista());		
		clone.setSecurityToken(this.getSecurityToken());
		return clone;
	}
	
	/**
	 * @param principalClone
	 */
	public synchronized void setAll(VistaRealmPrincipal clone)
	{
		this.accessCode = clone.accessCode;
		
		this.applicationProperties.clear();
		this.applicationProperties.putAll( clone.applicationProperties );
		
		this.authenticatedByDelegate = clone.authenticatedByDelegate;
		this.authenticatedByVista = clone.authenticatedByVista;
		this.credentialsType = clone.credentialsType;
		this.duz = clone.duz;
		this.fullName = clone.fullName;
		this.realm = clone.realm;
		
		this.roles.clear();
		this.roles.addAll(clone.roles);
		
		this.siteName = clone.siteName;
		this.siteNumber = clone.siteNumber;
		this.ssn = clone.ssn;
		this.verifyCode = clone.verifyCode;
		this.securityToken = clone.securityToken;
		this.cacheLocationId = clone.cacheLocationId;
		this.userDivision = clone.userDivision;
	}
	
	
	
    /**
     * Return a String representation of this object, which exposes only
     * information that should be public.
     */
    public String toString() 
    {
        StringBuilder sb = new StringBuilder("VistaRealmPrincipal ");
        sb.append(getRealm());
        sb.append(", ").append(getSiteName());
        sb.append("(").append(getSiteNumber()).append(") ");
        sb.append("-").append(getFullName());
        sb.append("(").append(getSsn()).append(") ");
        sb.append("[").append(getAccessCode()).append(",").append(getVerifyCode()).append("]");
        
        int roleIndex = 0;
        for(String role : roles)
            sb.append( ((roleIndex++ == 0) ? "{" : ", ") + role );
        
        sb.append("}");
        
        return (sb.toString());
    }

    /**
     * Returns a hashcode of the instance members that make up the uniqueness
     * of a Principal.  Instances that have the same securityHashCode are
     * the same person.
     *   
     * @return
     */
    public String getSecurityHashCode()
    {
		final int PRIME = 31;
		int result = 1;
		
		result = PRIME * result + (this.authenticatedByDelegate ? 1231 : 1237);
		result = PRIME * result + (this.authenticatedByVista ? 1231 : 1237);
		result = PRIME * result + ((this.accessCode == null) ? 0 : this.accessCode.hashCode());
		result = PRIME * result + ((this.verifyCode == null) ? 0 : this.verifyCode.hashCode());
		result = PRIME * result + ((this.credentialsType == null) ? 0 : this.credentialsType.hashCode());
		result = PRIME * result + ((this.duz == null) ? 0 : this.duz.hashCode());
		result = PRIME * result + ((this.realm == null) ? 0 : this.realm.hashCode());
		result = PRIME * result + ((this.securityToken == null) ? 0 : this.securityToken.hashCode());
		result = PRIME * result + ((this.cacheLocationId == null) ? 0 : this.cacheLocationId.hashCode());
		result = PRIME * result + ((this.userDivision == null) ? 0 : this.userDivision.hashCode());
		
		return Integer.toHexString(result);
    }
    
    // ==========================================================================================
    // Eclipse Generated hashCode() and equals()
    // If these methods are modified it is suggested that that fact be noted in comments so that
    // they are not regenerated over the changes.
    // ==========================================================================================
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.accessCode == null) ? 0 : this.accessCode.hashCode());
		result = PRIME * result + ((this.applicationProperties == null) ? 0 : this.applicationProperties.hashCode());
		result = PRIME * result + (this.authenticatedByDelegate ? 1231 : 1237);
		result = PRIME * result + (this.authenticatedByVista ? 1231 : 1237);
		result = PRIME * result + ((this.credentialsType == null) ? 0 : this.credentialsType.hashCode());
		result = PRIME * result + ((this.duz == null) ? 0 : this.duz.hashCode());
		result = PRIME * result + ((this.fullName == null) ? 0 : this.fullName.hashCode());
		result = PRIME * result + ((this.realm == null) ? 0 : this.realm.hashCode());
		result = PRIME * result + ((this.roles == null) ? 0 : this.roles.hashCode());
		result = PRIME * result + ((this.siteName == null) ? 0 : this.siteName.hashCode());
		result = PRIME * result + ((this.siteNumber == null) ? 0 : this.siteNumber.hashCode());
		result = PRIME * result + ((this.ssn == null) ? 0 : this.ssn.hashCode());
		result = PRIME * result + ((this.verifyCode == null) ? 0 : this.verifyCode.hashCode());
		result = PRIME * result + ((this.securityToken == null) ? 0 : this.securityToken.hashCode());
		result = PRIME * result + ((this.cacheLocationId == null) ? 0 : this.cacheLocationId.hashCode());
		result = PRIME * result + ((this.userDivision == null) ? 0 : this.userDivision.hashCode());
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
		final VistaRealmPrincipal other = (VistaRealmPrincipal) obj;
		if (this.accessCode == null)
		{
			if (other.accessCode != null)
				return false;
		} else if (!this.accessCode.equals(other.accessCode))
			return false;
		if (this.applicationProperties == null)
		{
			if (other.applicationProperties != null)
				return false;
		} else if (!this.applicationProperties.equals(other.applicationProperties))
			return false;
		
		if (this.authenticatedByDelegate == null)
		{
			if (other.authenticatedByDelegate != null)
				return false;
		} else if (!this.authenticatedByDelegate.equals(other.authenticatedByDelegate))
			return false;

		if (this.authenticatedByVista == null)
		{
			if (other.authenticatedByVista != null)
				return false;
		} else if (!this.authenticatedByVista.equals(other.authenticatedByVista))
			return false;
		
		if (this.credentialsType == null)
		{
			if (other.credentialsType != null)
				return false;
		} else if (!this.credentialsType.equals(other.credentialsType))
			return false;
		if (this.duz == null)
		{
			if (other.duz != null)
				return false;
		} else if (!this.duz.equals(other.duz))
			return false;
		if (this.fullName == null)
		{
			if (other.fullName != null)
				return false;
		} else if (!this.fullName.equals(other.fullName))
			return false;
		if (this.realm == null)
		{
			if (other.realm != null)
				return false;
		} else if (!this.realm.equals(other.realm))
			return false;
		if (this.roles == null)
		{
			if (other.roles != null)
				return false;
		} else if (!this.roles.equals(other.roles))
			return false;
		if (this.siteName == null)
		{
			if (other.siteName != null)
				return false;
		} else if (!this.siteName.equals(other.siteName))
			return false;
		if (this.siteNumber == null)
		{
			if (other.siteNumber != null)
				return false;
		} else if (!this.siteNumber.equals(other.siteNumber))
			return false;
		if (this.ssn == null)
		{
			if (other.ssn != null)
				return false;
		} else if (!this.ssn.equals(other.ssn))
			return false;
		if (this.verifyCode == null)
		{
			if (other.verifyCode != null)
				return false;
		} else if (!this.verifyCode.equals(other.verifyCode))
			return false;
		if (this.securityToken == null)
		{
			if (other.securityToken != null)
				return false;
		} else if (!this.securityToken.equals(other.securityToken))
			return false;
		if (this.cacheLocationId == null)
		{
			if (other.cacheLocationId != null)
				return false;
		} else if (!this.cacheLocationId.equals(other.cacheLocationId))
			return false;
		if (this.userDivision == null)
		{
			if (other.userDivision != null)
				return false;
		} else if (!this.userDivision.equals(other.userDivision))
			return false;
		return true;
	}

	/**
	 * Override the serialization methods with synchronized versions
	 * so that changes to the properties map do not result in a
	 * ConcurrentModificationException
	 *  
	 * @param out
	 * @throws IOException
	 */
	private synchronized void writeObject(ObjectOutputStream out) 
	throws IOException
	{
		out.defaultWriteObject();
	}

	private synchronized void readObject(ObjectInputStream in) 
	throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}
}
