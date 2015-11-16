/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.regex.Pattern;

/**
 * The required grammar of a command factory method is similar to the 
 * router method grammar with the addition of "create" and "Command"
 * added as a prefix and suffix respectively.
 * 
 * 
 * @author vhaiswbeckec
 *
 */
public class CommandFactoryMethodSemantics
extends CoreRouterSemantics
{
	private static Pattern commandFactoryMethodPattern = Pattern.compile(CoreRouterSemantics.getCommandFactoryMethodNameRegex()); 
	public Pattern getPattern()
	{
		return commandFactoryMethodPattern;
	}
	
	public static CommandFactoryMethodSemantics create(String commandFactoryMethodName)
	throws CoreRouterSemanticsException
	{
		return new CommandFactoryMethodSemantics(commandFactoryMethodName);
	}
	
	private  CommandFactoryMethodSemantics(String commandFactoryMethodName)
	throws CoreRouterSemanticsException
	{
		parseElement(commandFactoryMethodName);
	}

	/**
	 * Create an instance from a RouterMethodSemantics such that 
	 * directMapping() will return true.
	 * 
	 * @param routerMethod
	 * @return
	 */
	public static CommandFactoryMethodSemantics transformFrom(CoreRouterSemantics element)
	{
		return new CommandFactoryMethodSemantics(element);
	}
	
	private CommandFactoryMethodSemantics(CoreRouterSemantics element)
	{
		setAll(element);
	}
	
	protected int getActionIndex(){return CoreRouterSemantics.commandFactoryMethodActionIndex;}
	protected int getObjectIndex(){return CoreRouterSemantics.commandFactoryMethodObjectIndex;}
	protected int getObjectSimpleTypeIndex(){return CoreRouterSemantics.commandFactoryMethodObjectSimpleTypeIndex;}
	protected int getObjectCollectionIndex(){return CoreRouterSemantics.commandFactoryMethodObjectCollectionIndex;}
	protected int getPrepositionIndex(){return CoreRouterSemantics.commandFactoryMethodPrepositionIndex;}
	protected int getObjectOfPrepositionIndex(){return CoreRouterSemantics.commandFactoryMethodObjectOfPrepositionIndex;}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(this.getAction().toString().toLowerCase());
		sb.append(this.getObject());
		if(this.getPreposition() != null)
			sb.append(this.getPreposition().toStringFirstCharUpperCase());
		if(this.getObjectOfPreposition() != null)
			sb.append(this.getObjectOfPreposition());
		sb.append(CoreRouterSemantics.commandSuffix);
		
		if(sb.length() > 0)
			return commandFactoryMethodPrefix + RouterSemanticsUtility.standardizeToClassName(sb.toString());
		else
			return CoreRouterSemantics.commandFactoryMethodPrefix;
	}


}
