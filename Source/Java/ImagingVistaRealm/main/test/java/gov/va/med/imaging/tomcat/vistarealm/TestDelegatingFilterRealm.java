/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm;

import java.util.Map;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestDelegatingFilterRealm
extends TestCase
{
	private DelegatingFilterRealm realm = new DelegatingFilterRealm();
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	public DelegatingFilterRealm getRealm()
	{
		return this.realm;
	}

	/**
	 * Test method for {@link gov.va.med.imaging.tomcat.vistarealm.DelegatingFilterRealm#setDelegatedProperties(java.lang.String)}.
	 */
	public void testSetDelegatedProperties()
	{
		Map<String, String> properties = null;
		getRealm().setDelegatedProperties("");
		properties = getRealm().getDelegatedProperties();
		assertTrue(properties != null);
		assertTrue(properties.size() == 0);
		
		getRealm().setDelegatedProperties("a=\"b\"");
		properties = getRealm().getDelegatedProperties();
		assertTrue(properties != null);
		assertTrue(properties.size() == 1);
		assertTrue( "b".equals(properties.get("a")) );
		
		getRealm().setDelegatedProperties("inhibitThisAuthentication=\"true\"");
		properties = getRealm().getDelegatedProperties();
		assertTrue(properties != null);
		assertTrue(properties.size() == 1);
		assertTrue( "true".equals(properties.get("inhibitThisAuthentication")) );
		
		getRealm().setDelegatedProperties("inhibitThisAuthentication=\"true\";inhibitParentDelegation=\"false\"");
		properties = getRealm().getDelegatedProperties();
		assertTrue(properties != null);
		assertTrue(properties.size() == 2);
		assertTrue( "true".equals(properties.get("inhibitThisAuthentication")) );
		assertTrue( "false".equals(properties.get("inhibitParentDelegation")) );
	}

	public static void main(String[] argv)
	{
		TestDelegatingFilterRealm test = new TestDelegatingFilterRealm();
		test.testSetDelegatedProperties();
	}
}
