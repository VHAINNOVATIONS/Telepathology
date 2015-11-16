/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 28, 2010
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

package gov.va.med;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestClassTranslator
	extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.ClassTranslator#convert(java.lang.Object)}.
	 */
	public void testCreate()
	{
		ClassTranslator<String, Integer> stringToInteger = ClassTranslator.create(String.class, Integer.class);
		ClassTranslator<String, Byte> stringToByte = ClassTranslator.create(String.class, Byte.class);
		ClassTranslator<String, Boolean> stringToBoolean = ClassTranslator.create(String.class, Boolean.class);
		ClassTranslator<String, Float> stringToFloat = ClassTranslator.create(String.class, Float.class);
		ClassTranslator<String, Short> stringToShort = ClassTranslator.create(String.class, Short.class);
		ClassTranslator<String, Double> stringToDouble = ClassTranslator.create(String.class, Double.class);
		ClassTranslator<String, Long> stringToLong = ClassTranslator.create(String.class, Long.class);
		
		assertEquals(new Integer(42), stringToInteger.convert("42"));
		assertEquals(new Byte((byte)42), stringToByte.convert("42"));
		assertEquals(new Short((short)42), stringToShort.convert("42"));
		assertEquals(new Long(42), stringToLong.convert("42"));
		assertEquals(new Float(42), stringToFloat.convert("42"));
		assertEquals(new Double(42), stringToDouble.convert("42"));
		assertEquals(new Boolean(true), stringToBoolean.convert("true"));
	}

}
