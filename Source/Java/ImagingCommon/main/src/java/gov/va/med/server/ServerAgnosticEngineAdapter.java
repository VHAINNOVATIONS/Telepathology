/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 12, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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

package gov.va.med.server;

import java.security.Principal;
import java.security.cert.X509Certificate;

/**
 * This interface defines the calls available to a ServerAgnosticEngine implementation
 * to interact with the host-specific engine.
 * 
 * @author vhaiswbeckec
 *
 */
public interface ServerAgnosticEngineAdapter
{
	public abstract Principal authenticate(String username, byte[] credentials);
	
	public abstract Principal authenticate(
		String username, 
		String clientDigest, 
		String nOnce, 
		String nc, 
		String cnonce, 
		String qop, 
		String realm,
        String md5a2);
	
	public Principal authenticate(X509Certificate certs[]);

	public boolean hasRole(Principal principal, String role);
}
