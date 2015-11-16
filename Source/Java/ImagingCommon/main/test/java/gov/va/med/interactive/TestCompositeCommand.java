/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Sep 9, 2010
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

package gov.va.med.interactive;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestCompositeCommand
	extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.interactive.CompositeCommand#doStringSubstitution(java.lang.String[], java.lang.String[])}.
	 */
	public void testDoStringSubstitution()
	{
		CompositeCommand cc = new CompositeCommand()
		{
			@Override
			public String[][] getCommandsText()
			{return null;}
		};
		
		String[] commandText = new String[]{};
		String[] commandParameterValues = new String[]{};
		cc.doStringSubstitution(commandText, commandParameterValues);
		
		commandText = new String[]{"hello world"};
		commandParameterValues = new String[]{};
		cc.doStringSubstitution(commandText, commandParameterValues);
		assertEquals("hello world", commandText[0]);
		
		commandText = new String[]{"hello %1"};
		commandParameterValues = new String[]{"earth"};
		cc.doStringSubstitution(commandText, commandParameterValues);
		assertEquals("hello earth", commandText[0]);

		commandText = new String[]{"hello %1 %2 %3 %4 %5 %6 %7 %8 %9 %10"};
		commandParameterValues = new String[]{"mercury", "venus", "earth", "mars", "jupiter", "saturn", "uranus", "neptune", "pluto", "Number10"};
		cc.doStringSubstitution(commandText, commandParameterValues);
		assertEquals("hello mercury venus earth mars jupiter saturn uranus neptune pluto Number10", commandText[0]);
	}

}
