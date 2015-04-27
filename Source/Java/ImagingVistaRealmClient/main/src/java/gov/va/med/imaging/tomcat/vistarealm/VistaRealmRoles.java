/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 11, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author VHAISWBECKEC
 *
 */
public enum VistaRealmRoles
{
	ClinicalDisplayUser("clinical-display-user"),
	VistaUserRole("vista-user", "MAGDISP CLIN", "MAGDISP ADMIN", "MAG SYSTEM", 
			"MAGV IMPORT MEDIA STAGER", "MAGV IMPORT STAGE MEDIA ADV", "MAGV IMPORT RECON CONTRACT", 
			"MAGV IMPORT RECON ARTIFACT", "MAGV IMPORT REPORTS"),
    AdministratorRole("administrator", "MAG VIX ADMIN"),
    PeerVixsRole("peer-vixs"),
    AlienCommunity("alien"),
	Manager("manager"),
	Developer("developer"),
	Tester("tester"), // DKB
	PhotoIdOnlyRole("photo-only", "MAG PAT PHOTO ONLY");
	
	private final String roleName;
	private final String[] vistaSecurityKeyNames;
	
	/**
	 * @param roleName
	 * @param vistaPrivelige
	 */
	VistaRealmRoles(String roleName, String... vistaSecurityKeyNames)
	{
		this.roleName = roleName;
		if(vistaSecurityKeyNames != null)
		{
			this.vistaSecurityKeyNames = new String[vistaSecurityKeyNames.length];
			System.arraycopy(vistaSecurityKeyNames, 0, this.vistaSecurityKeyNames, 0, vistaSecurityKeyNames.length);
		}
		else
			this.vistaSecurityKeyNames = null;
	}

	/**
	 * The role name is probably what you want.
	 * this.toString() will return the Java standard enumeration name
	 * but that is an internal thing and is only mapped to roles by this
	 * enumeration.
	 * 
	 * @return
	 */
	public String getRoleName()
    {
    	return roleName;
    }

	/**
	 * Get a list of the Vista Security key names that are mapped to this
	 * role.
	 * 
	 * @return
	 */
	public String[] getVistaSecurityKeyNames()
    {
    	return vistaSecurityKeyNames;
    }

	// ===========================================================================
	// Static methods to help find roles given names, Vista security keys, etc ...
	// ===========================================================================
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static VistaRealmRoles getRoleByName(String name)
	{
		if(name == null)
			return null;
		
		for( VistaRealmRoles role : VistaRealmRoles.values() )
			if(name.equals(role.getRoleName()))
				return role;
		
		return null;
	}
	
	/**
	 * Create a list of role names, given the VistaRealmRoles enumeration
	 * @param roles
	 * @return
	 */
	public static List<String> createRoleNameList(VistaRealmRoles... roles)
	{
		List<String> result = new ArrayList<String>();
		
		for(VistaRealmRoles role : roles)
			result.add(role.getRoleName());
		
		return result;
	}
	
	/**
	 * Simply returns true if the rolename is known, else returns false
	 * @param roleName
	 * @return
	 */
	public static boolean isKnownRoleName(String roleName)
	{
		return getRoleByName(roleName) != null;
	}

	/**
	 * Given an array of security key names (from Vista) return
	 * a List of role names.
	 * 
	 * @param securityKeys
	 * @return
	 */
	public static List<String> getMappedRoleNames(String[] securityKeys)
    {
		List<String> roleNames = new ArrayList<String>();
		for( VistaRealmRoles role : getMappedRoles(securityKeys) )
			roleNames.add(role.getRoleName());
		
		return roleNames;
    }

	/**
	 * Given an array of security key names (from Vista) return
	 * a Set of VistaRealmRole instances mapped to those keys.
	 * 
	 * @param securityKeys
	 * @return
	 */
	public static Set<VistaRealmRoles> getMappedRoles(String[] securityKeys)
    {
		Set<VistaRealmRoles> result = new HashSet<VistaRealmRoles>();
		
		for( int index=0; securityKeys != null && index < securityKeys.length; ++index )
			for( VistaRealmRoles role : VistaRealmRoles.values() )
				for( String vistaSecurityKeyName : role.getVistaSecurityKeyNames() )
					if( securityKeys[index] != null && securityKeys[index].equals(vistaSecurityKeyName) )
						result.add(role);
		
		return result;
    }
}
