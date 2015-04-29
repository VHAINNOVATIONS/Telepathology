/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jul 15, 2008
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

import java.security.Principal;

/**
 * @author VHAISWBECKEC
 *
 */
public interface PreemptiveAuthorization
{
	public enum Result{
		True,			// without changes to the security context, a request for the target resource will succeed
		False, 			// without changes to the security context, a request for the target resource will fail
		Unknown			// some condition prevented determination of whether the security context would succeed,
						// the suggested course of action is to allow the request to be attempted
	};
	
	/**
	 * A non-authoritative, pre-emptive determination of whether the currently logged in user
	 * has privileges to the selected resource.
	 * 
	 * @param principal
	 * @param context
	 * @param contextRelativePath
	 * @param method
	 * @return
	 */
	public PreemptiveAuthorization.Result isAuthorized(
		Principal principal, 
		Object context, 
		String contextRelativePath, 
		String method);
}
