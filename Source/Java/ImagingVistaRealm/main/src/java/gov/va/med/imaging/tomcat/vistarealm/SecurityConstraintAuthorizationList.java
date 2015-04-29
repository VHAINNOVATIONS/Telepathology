/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 * This class implements part of the requirements of Section 12.8.1 of the servlet spec v 2.4
 * 
 * Section 12.8.1:
 * "When a url-pattern and http-method pair occurs in multiple security constraints, the constraints (on the pattern and method) are defined by combining the individual
 * constraints. The rules for combining constraints in which the same pattern and method occur are as follows:
 * 
 * The combination of authorization constraints that name roles or that imply roles via the name “*” shall yield the union of the role names in the individual
 * constraints as permitted roles. 
 * A security constraint that does not contain an authorization constraint shall combine with authorization constraints that name or imply roles to 
 * allow unauthenticated access. 
 * The special case of an authorization constraint that names no roles shall combine with any other constraints to override their affects and cause 
 * access to be precluded.
 */
public class SecurityConstraintAuthorizationList<T extends SecurityConstraint> 
extends ArrayList<T>
{
	private static final long serialVersionUID = -462267780018953616L;
	private Logger logger = Logger.getLogger(this.getClass());

	public SecurityConstraintAuthorizationList(T[] securityConstraints)
	{
		super();
		
		for( T securityConstraint: securityConstraints )
			add(securityConstraint);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDenyAllAccess()
	{
		for( T securityConstraint: this )
			// if there is an auth-constraint and it has no roles then deny all access
			if( securityConstraint.getAuthConstraint() && securityConstraint.findAuthRoles() != null && securityConstraint.findAuthRoles().length == 0 )
			{
				logger.debug("isDenyAllAccess returns true, key security constraint is '" + securityConstraint.getDisplayName() + "'");
				return true;
			}
		logger.debug("isDenyAllAccess returns false");
		return false;
	}

	public boolean isAllowUnauthenticatedAccess()
	{
		boolean allowUnauthenticatedAccess = false;
		for( T securityConstraint: this )
		{
			// if there is an auth-constraint and it has no roles then deny all access
			if( securityConstraint.getAuthConstraint() && securityConstraint.findAuthRoles() != null && securityConstraint.findAuthRoles().length == 0 )
			{
				logger.debug("isAllowUnauthenticatedAccess returns false, key security constraint is '" + securityConstraint.getDisplayName() + "'");
				return false;
			}
			
			if( securityConstraint.getAuthConstraint() && securityConstraint.findAuthRole("*") )
				allowUnauthenticatedAccess = true;
		}
		logger.debug("isAllowUnauthenticatedAccess returns " + allowUnauthenticatedAccess);
		return allowUnauthenticatedAccess;
	}

	public boolean isAnyRoleAllowedAccess(List<String> roles)
	{
		String[] roleArray = new String[roles.size()];
		return isAnyRoleAllowedAccess(roles.toArray(roleArray));
	}
	
	public boolean isAnyRoleAllowedAccess(String[] roles)
	{
		boolean allowAccess = false;
		for( T securityConstraint: this )
		{
			// if there is an auth-constraint and it has no roles then deny all access
			if( securityConstraint.getAuthConstraint() && securityConstraint.findAuthRoles() != null && securityConstraint.findAuthRoles().length == 0 )
			{
				logger.debug("isAnyRoleAllowedAccess returns false, key security constraint is '" + securityConstraint.getDisplayName() + "'");
				return false;
			}
			if( securityConstraint.getAuthConstraint() && securityConstraint.findAuthRole("*") )
			{
				logger.debug("isAnyRoleAllowedAccess, found wildcard security constraint '" + securityConstraint.getDisplayName() + "'");
				allowAccess = true;
			}
			
			// check for each of the roles, if any are listed then grant access
			for(String role: roles)
			{
				logger.debug("isAnyRoleAllowedAccess, checking role '" + role + "'");
				if( securityConstraint.getAuthConstraint() && securityConstraint.findAuthRole(role) )
				{
					logger.debug("isAnyRoleAllowedAccess, found security constraint '" + securityConstraint.getDisplayName() + "' matching role '" + role + "'");
					allowAccess = true;
				}
			}
		}
		
		logger.debug("isAnyRoleAllowedAccess returns " + allowAccess + "'" );
		return allowAccess;
	}
	
}
