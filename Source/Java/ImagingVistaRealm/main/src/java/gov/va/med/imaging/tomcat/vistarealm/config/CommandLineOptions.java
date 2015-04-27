/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm.config;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class encapsulates the collection of permissible command line options.
 * It provides some utility functions to find a command line option given an 
 * argument name.
 * 
 * @author VHAISWBECKEC
 *
 */
public class CommandLineOptions
{
	private final Collection<? extends CommandLineOption> permissableParameters;
	
	public CommandLineOptions(Collection<? extends CommandLineOption> permissableParameters)
	{
		super();
		this.permissableParameters = permissableParameters;
	}

	public CommandLineOption valueOf(String value)
	{
		return valueOf(value, false);
	}
	
	public CommandLineOption valueOfIgnoreCase(String value)
	{
		return valueOf(value, true);
	}

	public CommandLineOption valueOf(String value, boolean ignoreCase)
	{
		for( Iterator<? extends CommandLineOption> iter=permissableParameters.iterator(); iter.hasNext(); )
		{
			CommandLineOption clo = iter.next();
			
			if( ignoreCase ? clo.getName().equalsIgnoreCase(value) : clo.getName().equals(value) )
				return clo;
		}
		
		return null;
	}
	
	public String getUselessMessage()
	{
		StringBuilder sb = new StringBuilder();
		
		for( Iterator<? extends CommandLineOption> iter=permissableParameters.iterator(); iter.hasNext(); )
		{
			CommandLineOption clo = iter.next();
			
			if(sb.length() > 0)
				sb.append("\n");
				
			if(! clo.isRequired())
				sb.append("[");
			sb.append(clo.getName());
			if( clo.getType() != null )		// a flag has a type of null
				sb.append("=<" + clo.getType().toString() + ">");
			if(! clo.isRequired())
				sb.append("]");
		}
		return sb.toString();
	}

	public Iterator<? extends CommandLineOption> iterator()
	{
		return permissableParameters.iterator();
	}	

}