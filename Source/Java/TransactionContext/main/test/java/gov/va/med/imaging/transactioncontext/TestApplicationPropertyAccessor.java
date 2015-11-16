/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jun 24, 2008
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
package gov.va.med.imaging.transactioncontext;

import gov.va.med.ApplicationPropertyAccessor;
import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestApplicationPropertyAccessor 
extends TestCase
{
	public final void testStringGetApplicationPropertyAs() 
	throws SecurityException, NoSuchMethodException
	{
		ApplicationPropertyAccessor<String> propertyAccessor = 
			new ApplicationPropertyAccessor<String>("toString", 
			null
		);
		
		String result = null;
		result = propertyAccessor.getValueAs("42");
		assertEquals("42", result);
		
		result = propertyAccessor.getValueAs(new Integer(42));
		assertEquals("42", result);
		
		result = propertyAccessor.getValueAs(new Double(42));
		assertEquals("42.0", result);
		
		result = propertyAccessor.getValueAs(new Short((short)42));
		assertEquals("42", result);
		
		result = propertyAccessor.getValueAs(new Byte((byte)8));
		assertEquals("8", result);
		
		result = propertyAccessor.getValueAs(new Float(42));
		assertEquals("42.0", result);
	}
	
	/**
	 * Test method for {@link gov.va.med.ApplicationPropertyAccessor#getApplicationPropertyAs(java.lang.Object)}.
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public final void testBooleanGetApplicationPropertyAs() 
	throws SecurityException, NoSuchMethodException
	{
		ApplicationPropertyAccessor<Boolean> propertyAccessor = 
			new ApplicationPropertyAccessor<Boolean>("booleanValue", 
			Boolean.class.getMethod("parseBoolean", new Class[]{String.class})
		);
		
		Boolean result = null;
		result = propertyAccessor.getValueAs("42");
		assertEquals(false, result.booleanValue());
		result = propertyAccessor.getValueAs("true");
		assertEquals(true, result.booleanValue());
		result = propertyAccessor.getValueAs("false");
		assertEquals(false, result.booleanValue());
		
		result = propertyAccessor.getValueAs(new Float(42));
		assertEquals(null, result);
		
		result = propertyAccessor.getValueAs(new Double(42));
		assertEquals(null, result);
		
		result = propertyAccessor.getValueAs(new Long(42));
		assertEquals(null, result);
		
		result = propertyAccessor.getValueAs(new Integer(42));
		assertEquals(null, result);
		
		result = propertyAccessor.getValueAs(new Short((short)42));
		assertEquals(null, result);
	}
	
	public final void testIntegerGetApplicationPropertyAs() 
	throws SecurityException, NoSuchMethodException
	{
		ApplicationPropertyAccessor<Integer> propertyAccessor = 
			new ApplicationPropertyAccessor<Integer>("intValue", 
			Integer.class.getMethod("parseInt", new Class[]{String.class})
		);
		
		Integer result = null;
		result = propertyAccessor.getValueAs("42");
		assertEquals(42, result.intValue());
		
		result = propertyAccessor.getValueAs(new Float(42));
		assertEquals(42, result.intValue());
		
		result = propertyAccessor.getValueAs(new Double(42));
		assertEquals(42, result.intValue());
		
		result = propertyAccessor.getValueAs(new Long(42));
		assertEquals(42, result.intValue());
		
		result = propertyAccessor.getValueAs(new Integer(42));
		assertEquals(42, result.intValue());
		
		result = propertyAccessor.getValueAs(new Short((short)42));
		assertEquals(42, result.intValue());
	}

}
