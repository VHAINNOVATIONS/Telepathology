/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Sep 27, 2010
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

import java.io.StringBufferInputStream;
import java.util.Iterator;
import gov.va.med.interactive.mock.MockCommandFactory;
import gov.va.med.interactive.mock.MockManagedObject;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestCommandFileCommandSource
extends TestCase
{
	
	public void testParsing()
	throws Exception
	{
		String commands = "1,2,3";
		StringBufferInputStream inStream = new StringBufferInputStream(commands);
		String[][] commandsText = CommandFileCommandSource.readCommandText(inStream, null);
		assertEquals("Wrong number of lines found", 1, commandsText.length);
		assertEquals("1", commandsText[0][0]);
		assertEquals("2", commandsText[0][1]);
		assertEquals("3", commandsText[0][2]);
		
		commands = "1,\"2,3\"";
		inStream = new StringBufferInputStream(commands);
		commandsText = CommandFileCommandSource.readCommandText(inStream, null);
		assertEquals("Wrong number of lines found", 1, commandsText.length);
		assertEquals("1", commandsText[0][0]);
		assertEquals("2,3", commandsText[0][1]);

		commands = "addProtocolPreference, 1.3.6.1.4.1.3768, *, vftp, false, false";
		inStream = new StringBufferInputStream(commands);
		commandsText = CommandFileCommandSource.readCommandText(inStream, null);
		assertEquals("Wrong number of lines found", 1, commandsText.length);
		assertEquals("addProtocolPreference", commandsText[0][0]);
		assertEquals("1.3.6.1.4.1.3768", commandsText[0][1]);
		assertEquals("*", commandsText[0][2]);
		assertEquals("vftp", commandsText[0][3]);
		assertEquals("false", commandsText[0][4]);
		assertEquals("false", commandsText[0][5]);

		commands = "addProtocolPreference,1.3.6.1.4.1.3768,*,\"vftp,vistaimaging\",false,false";
		inStream = new StringBufferInputStream(commands);
		commandsText = CommandFileCommandSource.readCommandText(inStream, null);
		assertEquals("Wrong number of lines found", 1, commandsText.length);
		assertEquals("addProtocolPreference", commandsText[0][0]);
		assertEquals("1.3.6.1.4.1.3768", commandsText[0][1]);
		assertEquals("*", commandsText[0][2]);
		assertEquals("vftp,vistaimaging", commandsText[0][3]);
		assertEquals("false", commandsText[0][4]);
		assertEquals("false", commandsText[0][5]);
	}

	public void test1() 
	throws Exception
	{
		// The MockManagedObject keeps an ordered list of all the things that are done to it.
		MockManagedObject managedObject = new MockManagedObject();
		
		CommandSource<MockManagedObject> source = 
			(CommandSource<MockManagedObject>) CommandFileCommandSource.create("resource://gov/va/med/interactive/CommandFileCommandSourceTest1.commands", null);
		CommandFactory<MockManagedObject> factory = new MockCommandFactory();
		CommandProcessor<MockManagedObject> processor = new CommandProcessor<MockManagedObject>();
		CommandController<MockManagedObject> controller = 
			new CommandController<MockManagedObject>(managedObject, source, factory, processor, true, true, null, true);

		controller.run();
		
		Iterator<MockManagedObject.ThingsDone> iter = managedObject.iterator();
		MockManagedObject.ThingsDone thingDone = iter.next();
		assertEquals("MockCommand", thingDone.getCommand());
		assertNull( thingDone.getArgs() );
		
		thingDone = iter.next();
		assertEquals("MockCommand", thingDone.getCommand());
		assertNotNull( thingDone.getArgs() );
		assertEquals("1", thingDone.getArgs()[0]);
		
		thingDone = iter.next();
		assertEquals("MockCommand", thingDone.getCommand());
		assertNotNull( thingDone.getArgs() );
		assertEquals("1", thingDone.getArgs()[0]);

		thingDone = iter.next();
		assertEquals("MockCommand", thingDone.getCommand());
		assertNotNull( thingDone.getArgs() );
		assertEquals("2", thingDone.getArgs()[0]);
		assertEquals("3", thingDone.getArgs()[1]);

		thingDone = iter.next();
		assertEquals("MockCommand", thingDone.getCommand());
		assertNotNull( thingDone.getArgs() );
		assertEquals("Hello", thingDone.getArgs()[0]);
		assertEquals("World", thingDone.getArgs()[1]);

		thingDone = iter.next();
		assertEquals("MockCommand", thingDone.getCommand());
		assertNotNull( thingDone.getArgs() );
		assertEquals("Hello World", thingDone.getArgs()[0]);
		assertEquals("123", thingDone.getArgs()[1]);
		assertEquals("", thingDone.getArgs()[2]);
	}
}
