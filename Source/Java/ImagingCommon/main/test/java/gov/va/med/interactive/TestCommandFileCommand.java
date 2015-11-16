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

import gov.va.med.interactive.commands.CommandFileCommand;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestCommandFileCommand
extends TestCase
{
	/**
	 * Test method for {@link gov.va.med.interactive.commands.CommandFileCommand#getCommandsText()}.
	 */
	public void testGetCommandsText()
	{
		CommandFileCommand<MockManagedObject> cfc = 
			new CommandFileCommand<MockManagedObject>(new String[]{"gov/va/med/interactive/CommandFileCommand_Test1.txt"});
		String[][] commandsText = cfc.getCommandsText();
		
		assertEquals("A", commandsText[0][0]);
		assertEquals("B", commandsText[1][0]);
		assertEquals("B1", commandsText[1][1]);
		assertEquals("C", commandsText[2][0]);
		assertEquals("C1", commandsText[2][1]);
		assertEquals("C2", commandsText[2][2]);
	}

	
	public static class MockManagedObject
	{
		
	}
}
