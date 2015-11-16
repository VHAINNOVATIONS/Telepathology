/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Oct 28, 2010
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

import java.util.regex.Pattern;
import gov.va.med.exceptions.RoutingTokenFormatException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestRoutingTokenImpl
	extends TestCase
{
	public void testRegex()
	{
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1,1") );
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1.2,1") );
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1.2.3.4,1") );
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1,*") );
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1,1.2.3.4") );
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1,helloWorld") );
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1,hello,world") );
		assertTrue( Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, "1.2.3.4,hello,world") );
	}
	
	/**
	 * Test method for {@link gov.va.med.RoutingTokenImpl#parse(java.lang.String)}.
	 */
	public void testCreateFromToRoutingTokenString()
	throws RoutingTokenFormatException
	{
		RoutingToken rt = null;
		RoutingToken rtClone = null;
		String rtAsString = null;
		
		rt = RoutingTokenImpl.create("1.2.3");
		rtAsString = rt.toRoutingTokenString();
		assertTrue(rtAsString, Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, rtAsString) );
		rtClone = RoutingTokenImpl.parse(rtAsString);
		assertEquals(rt, rtClone);

		for(String[] stimulus : new String[][]
		{
			new String[]{"1","2"},
			new String[]{"1.2","2"},
			new String[]{"1.2.34","2"},
			new String[]{"1.2.34","1.2"},
			new String[]{"1.2.34","helloWorld"},
			new String[]{"1.2.34","hello.World"},
			new String[]{"1.2.34","hello,world"},
			new String[]{"1.2.34","1.2.abc.123,67"},
			new String[]{"1.2.34","*"}
		})
		{
			rt = RoutingTokenImpl.create(stimulus[0], stimulus[1]);
			rtAsString = rt.toRoutingTokenString();
			assertTrue(rtAsString, Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, rtAsString) );
			rtClone = RoutingTokenImpl.parse(rtAsString);
			assertEquals(rt, rtClone);
			
		}
		
	}

	/**
	 * Test method for {@link gov.va.med.RoutingTokenImpl#toRoutingTokenString()}.
	 * @throws RoutingTokenFormatException 
	 */
	public void testToRoutingTokenString() 
	throws RoutingTokenFormatException
	{
		RoutingToken rt = RoutingTokenImpl.create("1.2.3");
		assertTrue(rt.toRoutingTokenString(), Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, rt.toRoutingTokenString()) );
		rt = RoutingTokenImpl.create("1.2.3", "4.5.6");
		assertTrue(rt.toRoutingTokenString(), Pattern.matches(RoutingToken.ROUTING_TOKEN_REGEX, rt.toRoutingTokenString()) );
	}

}
