package gov.va.med.imaging.exchange.realm.tomcat;

import gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmSecurityContext;

import java.security.Principal;

import junit.framework.TestCase;

import org.apache.catalina.LifecycleException;
import org.apache.log4j.Logger;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class VistaRealmTest 
extends TestCase
{
	private Logger logger = Logger.getLogger(this.getClass());
	
	public VistaRealmTest()
	{
		super();
	}

	public void testAuthenticateStringString() 
	throws LifecycleException
	{
		Principal p = null;
		VistaAccessVerifyRealm realm = new VistaAccessVerifyRealm();
		realm.setSiteNumber("660");
		realm.setSiteAbbreviation("SLC");
		realm.setSiteName("Salt Lake City, UT");
		realm.setVistaServer("localhost");
		realm.setVistaPort(9300);
		realm.start();
		
		System.out.println("Authenticating 'boating'");
		p = realm.authenticate("boating1", "boating1.");
		System.out.println("User 'boating' " + (p != null ? "authenticated" : "not authenticated") );
		assertNotNull(p);
		if(p!=null)
		{
			VistaRealmPrincipal threadPrincipal = VistaRealmSecurityContext.get();
			assertEquals(p, threadPrincipal);
		}
		
		System.out.println("Authenticating 'access'");
		p = realm.authenticate("access", "verify");
		System.out.println("User 'access' " + (p != null ? "authenticated" : "not authenticated") );
		assertNull(p);
		
		realm.stop();
	}
}
