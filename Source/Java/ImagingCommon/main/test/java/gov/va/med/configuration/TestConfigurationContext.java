/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Aug 3, 2010
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

package gov.va.med.configuration;

import javax.naming.NamingException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestConfigurationContext
	extends TestCase
{
	private ConfigurationContext root;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		root = new ConfigurationContext(null, "ROOT");
	}
	
	public void testBindingAndLookup() 
	throws NamingException
	{
		root.bind("String", "HelloWorld");
		root.bind("Integer", new Integer(42));
		root.bind("Float", new Float(99.999));
		
		assertEquals("HelloWorld", root.lookup(String.class, "String"));
		assertEquals(new Integer(42), root.lookup(Integer.class, "Integer"));
		assertEquals(new Float(99.999), root.lookup(Float.class, "Float"));
	}

	public void testBindingAndLookupWithTypeConversion() 
	throws NamingException
	{
		root.bind("ShortString", "42");
		root.bind("DoubleString", "99.999");
		
		assertEquals(new Integer(42), root.lookup(Integer.class, "ShortString"));
		assertEquals(new Float(99.999), root.lookup(Float.class, "DoubleString"));
	}

	public void testSubContextBindingAndLookup() 
	throws NamingException
	{
		root.createSubcontext("SubContext", false);
		root.bind("SubContext/String", "YataYataYata");
		root.bind("SubContext/Integer", new Integer(67));
		try{root.bind("OtherSubContext/Float", new Float(5280.78)); fail("Unknown subcontext was not caught");}
		catch(NamingException nX){}
		
		assertEquals("YataYataYata", root.lookup(String.class, "SubContext/String"));
		assertEquals(new Integer(67), root.lookup(Integer.class, "SubContext/Integer"));
		try{ root.lookup(Float.class, "OtherSubContext/Float"); fail("Unknown subcontext was not caught"); }
		catch(NamingException nX){}
	}
	
	public void testSubContextPath() 
	throws NamingException
	{
		ConfigurationContext sub = 
			root.createSubcontext("A", false).createSubcontext("B", false).createSubcontext("C", false).createSubcontext("D", false);
		assertEquals("ROOT/A/B/C/D", sub.getAbsoluteName().toString());
		
		sub = root.createSubcontext("E/F/G/H", false);
		assertEquals("ROOT/E/F/G/H", sub.getAbsoluteName().toString());
	}
}
