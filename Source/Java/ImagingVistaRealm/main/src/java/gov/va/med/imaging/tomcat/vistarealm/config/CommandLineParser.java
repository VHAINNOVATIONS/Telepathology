package gov.va.med.imaging.tomcat.vistarealm.config;


import java.util.*;

/**
 * A simple command line parser that expects arguments in either form:
 * 1.) <name>=<value>
 * 2.) <name>
 * The permissible arguments must be provided to this class in a Collection
 * of CommandLineOption instances.  The CommandLineOption implementations
 * indicate whether the argument is required and the expected type.  The type
 * may be one of:
 * 1.) java.lang.String
 * 2.) java.lang.Boolean
 * 3.) java.lang.Integer
 * 4.) java.lang.Long
 * 5.) java.lang.Float
 * 6.) java.lang.Double
 * 7.) null
 * A type of null indicates a flag, that is the presence or absence of the
 * argument is an indication to the application.
 * 
 * @author VHAISWBECKEC
 *
 */
class CommandLineParser
{
	private static final long serialVersionUID = -4659660966806570927L;
	private CommandLineOptions commandLineDefinition;
	private Map<CommandLineOption, Object> nameValuePairs = new HashMap<CommandLineOption, Object>();
	private List<String> parseErrorMessages = new ArrayList<String>();
	
	/**
	 * Construct a command line parser with the given command line definition.
	 * e.g.
	 * Collection<CommandLineOption> commandLineDefinition = new ArrayList<CommandLineOption>();
	 * commandLineDefinition.add( new CommandLineOptionImpl("uid", java.lang.String.class, true) );
	 * commandLineDefinition.add( new CommandLineOptionImpl("pwd", java.lang.String.class, false) );
	 * CommandLineParser commandLineParser = new CommandLineParser(commandLineDefinition);
	 * commandLineParser.parse(args, false);
	 * 
	 * The above example will create a command line parser that expects two arguments, 
	 * the first is required and should be referenced like:
	 *   commandLineParser.getValue("uid");
	 * the second is option and should be referenced like:
	 *   if( commandLineParser.isExists("pwd") )
	 *     commandLineParser.getValue("pwd");
	 * or the code must be prepared to handle a null value.
	 * 
	 * @param commandLineDefinition
	 */
	public CommandLineParser(Collection<CommandLineOption> commandLineDefinition)
	{
		this.commandLineDefinition = new CommandLineOptions(commandLineDefinition);
	}

	/**
	 * Parse the command line, validate the arguments and make them available through the
	 * getValue() and isExists() methods.
	 * 
	 * @param args the command line arguments, usually from the main() method
	 * @param caseSensitive if true then the argument names must match exactly, else
	 * a case insensitive match is used.
	 */
	public void parse(String[] args, boolean caseSensitive)
	{
		args = concatenateDelimitedArgs(args);
		for(String arg: args)
		{
			String[] nameValue = arg.split("=");
			String name = null;
			String value = null;
			
			if(nameValue.length > 0)
				name = nameValue[0];
			if(nameValue.length > 1)
				value = nameValue[1];
			
			if(name != null)
			{
				CommandLineOption commandLineArg = null;
				
				if(caseSensitive)
					commandLineArg = commandLineDefinition.valueOf(name);
				else
					commandLineArg = commandLineDefinition.valueOfIgnoreCase(name);
				
				if(commandLineArg != null)
				{
					try
					{
						Object typedObject = createTypedObject(value, commandLineArg.getType());
						
						nameValuePairs.put(commandLineArg, typedObject);
					} 
					catch (NumberFormatException x)
					{
						parseErrorMessages.add("Incorrect format for value of parameter '" + name + "', must be parsable to type '" + commandLineArg.getType().toString() + "'.");
					}
				}
				else
					parseErrorMessages.add("Unknown parameter '" + name + "'.");
				
			}
		}
		
		for( Iterator<? extends CommandLineOption> iter=commandLineDefinition.iterator(); iter.hasNext(); )
		{
			CommandLineOption arg = iter.next();
			
			if( arg.isRequired() && ! nameValuePairs.containsKey(arg) )
				parseErrorMessages.add(
						"Required parameter '" + arg.toString() + "' is not provided on the command line, use " + arg.toString() + 
						((arg.getType()==null) ? "." : "=<value>.") );
			
			if( nameValuePairs.containsKey(arg) && (arg.getType()!=null) && nameValuePairs.get(arg) == null )
				parseErrorMessages.add("Parameter '" + arg.toString() + "' must have a value and it does not, use " + arg.toString() + "=<value>.");
		}
		
		if(parseErrorMessages.size() > 0)
			parseErrorMessages.add("Valid parameters are: \n" + commandLineDefinition.getUselessMessage());
	}

	/**
	 * Deal with quoted strings in command line parameters.  Anywhere we find a quote,
	 * concatenate the next parameters until we find a matching end quote.
	 * 
	 * @param args
	 * @return
	 */
	private String[] concatenateDelimitedArgs(String[] args)
	{
		List<String> result = new ArrayList<String>();
		for(String arg : args)
		{
			boolean inQuotedString = false;
			
			String concatenatedString = null;		// contains the complete string with quoted values
			
			int firstQuoteIndex = arg.indexOf('"');			// the index of the first quote, if there is one
			int lastQuoteIndex = arg.lastIndexOf('"');		// the index of the last quote, if there is one
			boolean contiguous = firstQuoteIndex <= 0 || 
				(firstQuoteIndex >= 0 && lastQuoteIndex >= 0 && firstQuoteIndex < lastQuoteIndex);
															// true if arg has both quotes or no quotes
			
			// if the start quote has been found previously
			// concatenate the new arg to it
			// if the end quote is found then write it to the result
			if( inQuotedString)
			{
				concatenatedString = concatenatedString + " " + arg;
				if(firstQuoteIndex >= 0)		// the quoted string is ending
				{
					result.add(concatenatedString);
					inQuotedString = false;
					concatenatedString = null;
				}
			}
			else if(!contiguous)
			{
				concatenatedString = arg;
				inQuotedString = true;
			}
			else
			{
				result.add(arg);
			}
		}
		
		return result.toArray( new String[result.size()] );
	}
	
	/**
	 * Determine of all required parameters are present and if the given parameters are
	 * of the correct type.
	 * 
	 * @return
	 */
	public List<String> getParseErrorMessages()
	{
		return parseErrorMessages;
	}

	/**
	 * @param value
	 * @param type
	 * @return
	 */
	private Object createTypedObject(String value, Class type)
	{
		if(java.lang.String.class == type )
			return value;
		else if(java.lang.Integer.class == type )
			return java.lang.Integer.parseInt(value);
		
		else if(java.lang.Long.class == type )
			return java.lang.Long.parseLong(value);
		
		else if(java.lang.Boolean.class == type )
			return java.lang.Boolean.parseBoolean(value);
		
		else if(java.lang.Float.class == type )
			return java.lang.Float.parseFloat(value);
		
		else if(java.lang.Double.class == type )
			return java.lang.Double.parseDouble(value);
		
		return null;
	}

	/**
	 * Return true if the named arguments exists on the command line that was most
	 * recently parsed.
	 * 
	 * @param parameterName
	 * @return
	 */
	public boolean isExists(String parameterName)
	{
		CommandLineOption option = commandLineDefinition.valueOf(parameterName);
		return option == null ? null : isExists(option);
	}
	
	/**
	 * Return true if the named arguments exists on the command line that was most
	 * recently parsed.
	 * 
	 * @param arg
	 * @return
	 */
	public boolean isExists(CommandLineOption arg)
	{
		for(CommandLineOption commandLineArg : nameValuePairs.keySet())
			if(commandLineArg == arg)
				return true;
		
		return false;
	}

	/**
	 * Return the value of the named argument, or null if it does not exist.
	 * The return type is guaranteed to be of the type defined in the command
	 * line definition.
	 * Note: flag type parameters (those with no value) will also return null. 
	 *  
	 * @param parameterName
	 * @return
	 */
	public Object getValue(String parameterName)
	{
		CommandLineOption option = commandLineDefinition.valueOf(parameterName);
		return option == null ? null : getValue(option);
	}

	/**
	 * Return the value of the named argument, or null if it does not exist.
	 * The return type is guaranteed to be of the type defined in the command
	 * line definition.
	 * Note: flag type parameters (those with no value) will also return null. 
	 * 
	 * @param arg
	 * @return
	 */
	public Object getValue(CommandLineOption arg)
	{
		for(CommandLineOption commandLineArg : nameValuePairs.keySet())
			if(commandLineArg == arg)
				return nameValuePairs.get(arg);
		
		return null;
	}
	
}