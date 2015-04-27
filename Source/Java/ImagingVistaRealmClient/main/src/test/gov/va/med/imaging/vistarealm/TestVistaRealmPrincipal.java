/**
 * 
 */
package gov.va.med.imaging.vistarealm;

import java.io.*;

import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestVistaRealmPrincipal 
extends AbstractSerializationTest<VistaRealmPrincipal>
{

	public void testSerialization() 
	throws IOException, ClassNotFoundException
	{
		VistaRealmPrincipal prototype = null;
		VistaRealmPrincipal copy = null;
		
		prototype = new VistaRealmPrincipal("realm", false, AuthenticationCredentialsType.Password);
		prototype.setAccessCode("access");
		prototype.setDuz("duz");
		prototype.setFullName("full name");
		prototype.setSiteName("site name");
		prototype.setSiteNumber("655321");
		prototype.setSsn("111-22-3333");
		prototype.setVerifyCode("verify");
		copy = serializeDeserializeAndAssertEquality(prototype);
		assertNotNull(copy);
		assertEquals(prototype.getDuz(), copy.getDuz());
		assertEquals(prototype.getRealm(), copy.getRealm());
		
		
		prototype = new VistaRealmPrincipal("REALM", true, AuthenticationCredentialsType.Password);
		prototype.setApplicationProperty("pickle", "juice");
		prototype.setApplicationProperty("null", null);
		copy = serializeDeserializeAndAssertEquality(prototype);
		assertNotNull(copy);
		assertEquals(prototype.getRealm(), copy.getRealm());
		assertEquals( prototype.getApplicationProperty("pickle"), copy.getApplicationProperty("pickle") );
		assertNull( copy.getApplicationProperty("null") );
		
	}
	
	public void testCloning()
	{
		VistaRealmPrincipal prototype = null;
		VistaRealmPrincipal clone = null;
		
		prototype = new VistaRealmPrincipal("realm", false, AuthenticationCredentialsType.Password);
		prototype.setAccessCode("access");
		prototype.setDuz("duz");
		prototype.setFullName("full name");
		prototype.setSiteName("site name");
		prototype.setSiteNumber("655321");
		prototype.setSsn("111-22-3333");
		prototype.setVerifyCode("verify");
		prototype.setApplicationProperty("key1", new Integer(42));
		prototype.setApplicationProperty("key2", new Boolean(true));
		prototype.setApplicationProperty("key3", new Double(42.32));
		
		clone = prototype.clone();
		clone.setApplicationProperty("key1", new Integer(43));
		clone.setApplicationProperty("key2", new Boolean(false));
		clone.setApplicationProperty("key3", new Double(12.34));

		assertFalse( "Integer fields are linked", prototype.getApplicationProperty("key1").equals(clone.getApplicationProperty("key1")) );
		assertFalse( "Boolean fields are linked", prototype.getApplicationProperty("key2").equals(clone.getApplicationProperty("key2")) );
		assertFalse( "Double fields are linked", prototype.getApplicationProperty("key3").equals(clone.getApplicationProperty("key3")) );
	}
}
