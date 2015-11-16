/**
 * 
 */
package gov.va.med.imaging.core.interfaces.exceptions;

public class CommandLineException extends Exception
{
	private static final long serialVersionUID = -7839997959775526573L;

	public CommandLineException()
	{
		super();
	}

	public CommandLineException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public CommandLineException(String arg0)
	{
		super(arg0);
	}

	public CommandLineException(Throwable arg0)
	{
		super(arg0);
	}
	
}