/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Nov 23, 2010
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

package gov.va.med.imaging.tomcat.vistarealm;

import java.security.Principal;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class IntegrationTestAccessVerifyRealm
	extends TestCase
{
	private VistaAccessVerifyRealm realm;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		realm = new VistaAccessVerifyRealm();
		realm.setAuthenticateAgainstVista(Boolean.TRUE);
		realm.setSetCprsContext(Boolean.FALSE);
		realm.setSiteNumber("600");
		realm.setSiteAbbreviation("WDC");
		realm.setSiteName("Washington DC");
		realm.setUsingPrincipalCache(Boolean.FALSE);		// force VistA access on all authenticate calls
		
		realm.setVistaServer("127.0.0.1");
		realm.setVistaPort( new Integer(9300) );
		
		assertNotNull(realm);
	}

	public void testAdditionalUserRoles()
	throws Exception
	{
		Principal principal = realm.authenticate("boating1", "boating1.");
		assertNotNull(principal);
		VistaRealmPrincipal vrPrincipal = (VistaRealmPrincipal)principal;
		assertNotNull(vrPrincipal);
		assertFalse( vrPrincipal.getRoles().contains("extra") );
		
		realm.setAdditionalUserRoles("boating1:extra");
		principal = realm.authenticate("boating1", "boating1.");
		assertNotNull(principal);
		vrPrincipal = (VistaRealmPrincipal)principal;
		assertNotNull(vrPrincipal);
		assertTrue( vrPrincipal.getRoles().contains("extra") );
		
		realm.setAdditionalUserRoles("boating1:extraOne,extraTwo");
		principal = realm.authenticate("boating1", "boating1.");
		assertNotNull(principal);
		vrPrincipal = (VistaRealmPrincipal)principal;
		assertNotNull(vrPrincipal);
		assertTrue( vrPrincipal.getRoles().contains("extraOne") );
		assertTrue( vrPrincipal.getRoles().contains("extraTwo") );
	}
	
	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		realm = null;
		super.tearDown();
	}

}
