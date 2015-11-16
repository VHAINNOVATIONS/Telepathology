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

package gov.va.med.interactive.mock;

import java.util.ArrayList;

public class MockManagedObject
extends ArrayList<MockManagedObject.ThingsDone>
{
	private static final long serialVersionUID = 1L;

	public void doSomething(String command, String[] args)
	{
		this.add(new ThingsDone(command, args));
	}
	
	public class ThingsDone
	{
		private final String command;
		private final String[] args;
		
		public ThingsDone(String command, String[] args)
		{
			super();
			this.command = command;
			this.args = args;
		}
		public String getCommand(){return this.command;}
		public String[] getArgs(){return this.args;}
	}
}