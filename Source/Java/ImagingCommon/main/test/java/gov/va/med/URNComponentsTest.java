/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Dec 21, 2010
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

import gov.va.med.imaging.exceptions.URNFormatException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class URNComponentsTest
	extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.URNComponents#parse(java.lang.String, gov.va.med.URN.SERIALIZATION_FORMAT)}.
	 * @throws URNFormatException 
	 */
	public void testParseStringSERIALIZATION_FORMAT() 
	throws URNFormatException
	{
		String[] testStimuli = new String[]
		{
			"urn:bhieimage:123%5bICN%5d%5b456%5d"
		};
		
		for(String testStimulus : testStimuli)
		{
			URNComponents urn = URNComponents.parse(testStimulus, SERIALIZATION_FORMAT.VFTP);
			assertNotNull(urn);
		}
		
	}

}
