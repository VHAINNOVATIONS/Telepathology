/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm.config;

/**
 * @author VHAISWBECKEC
 *
 * This class encapsulates the information about a single command line option.
 * 
 */
public class CommandLineOptionImpl 
implements CommandLineOption
{
	private final String name;
	private final Class type;
	private final boolean required;
	
	protected CommandLineOptionImpl(String name, Class type, boolean required)
	{
		this.name = name;
		this.type = type;
		this.required = required;
	}
	
	public String getName()
	{
		return name;
	}
	public Class getType()
	{
		return this.type;
	}
	public boolean isRequired()
	{
		return this.required;
	}
	public boolean isFlag()
	{
		return this.type == null;
	}
	
}
