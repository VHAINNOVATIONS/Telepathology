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

import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandProcessor;


public class MockCommand
extends Command<MockManagedObject>
{
	private String command;
	private String[] args;
	private boolean processed = false;
	
	public MockCommand(String command){this.command = command; this.args = null;}
	public MockCommand(String command, String[] args){this.command = command; this.args = args;}
	
	@Override
	public void processCommand(CommandProcessor<MockManagedObject> processor, MockManagedObject managedObject)
	throws Exception
	{
		processed = true;
		managedObject.doSomething(getCommand(), getArgs());
	}
	
	public String getCommand(){return this.command;}
	public String[] getArgs(){return this.args;}
	public boolean isProcessed(){return this.processed;}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getCommand());
		sb.append("(");
		for(int index=0; getArgs() != null && index < getArgs().length; index++)
			sb.append( (index == 0 ? "":",") + getArgs()[index] );
		sb.append(")");
		return sb.toString();
	}
}