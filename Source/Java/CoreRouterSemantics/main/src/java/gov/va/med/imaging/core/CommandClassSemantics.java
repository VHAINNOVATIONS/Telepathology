/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.regex.Pattern;

/**
 * This classes defines the naming conventions for realizations of the
 * Command interface (i.e. router commands).
 * 
 * @author vhaiswbeckec
 *
 */
public class CommandClassSemantics
extends CoreRouterSemantics
{
	private static Pattern commandClassNamePattern = Pattern.compile(CoreRouterSemantics.getCommandClassNameRegex());
	
	private final String commandPackage;
	
	public static CommandClassSemantics create(String commandClassName, String commandPackage)
	throws CoreRouterSemanticsException
	{
		return new CommandClassSemantics(commandClassName, commandPackage);
	}
	
	/**
	 * @param action
	 * @param objectName
	 * @param preposition
	 * @param objectOfPreposition
	 * @return
	 */
	public static CommandClassSemantics create(
		Action action,
		String objectName, 
		Preposition preposition,
		String objectOfPreposition)
	{
		return new CommandClassSemantics(action, objectName, preposition, objectOfPreposition);
	}

	private CommandClassSemantics(
		Action action,
		String objectName, 
		Preposition preposition,
		String objectOfPreposition)			
	{
		super(action, objectName, preposition, objectOfPreposition);
		this.commandPackage = null;
	}
	
	private CommandClassSemantics(String commandClassName, String commandPackage)
	throws CoreRouterSemanticsException
	{
		this.commandPackage = commandPackage;
		parseElement(commandClassName);
	}

	/**
	 * Create an instance from a RouterMethodSemantics such that 
	 * directMapping() will return true.
	 * 
	 * @param routerMethod
	 * @return
	 */
	public static CommandClassSemantics transformFrom(CoreRouterSemantics routerMethod)
	{
		return new CommandClassSemantics(routerMethod);
	}
	
	public static CommandClassSemantics transformFrom(CoreRouterSemantics routerMethod, String commandPackage)
	{
		return new CommandClassSemantics(routerMethod, commandPackage);
	}
	
	private CommandClassSemantics(CoreRouterSemantics coreRouterElement)
	{
		this(coreRouterElement, null);
	}
	
	private CommandClassSemantics(CoreRouterSemantics coreRouterElement, String commandPackage)
	{
		setAll(coreRouterElement);
		this.commandPackage = commandPackage;
	}
	
	public Pattern getPattern()
	{
		return commandClassNamePattern;
	}

	protected int getActionIndex(){return CoreRouterSemantics.commandClassActionIndex;}
	protected int getObjectIndex(){return CoreRouterSemantics.commandClassObjectIndex;}
	protected int getObjectSimpleTypeIndex(){return CoreRouterSemantics.commandClassObjectSimpleTypeIndex;}
	protected int getObjectCollectionIndex(){return CoreRouterSemantics.commandClassObjectCollectionIndex;}
	protected int getPrepositionIndex(){return CoreRouterSemantics.commandClassPrepositionIndex;}
	protected int getObjectOfPrepositionIndex(){return CoreRouterSemantics.commandClassObjectOfPrepositionIndex;}

	@Override
	public String toString()
	{
		return toString(false);
	}
	
	public String toString(boolean includePackage)
	{
		StringBuilder commandPackageSb = new StringBuilder();
		if(includePackage && isCommandPackageSpecified())
		{
			commandPackageSb.append(getCommandPackage());
			commandPackageSb.append(".");
		}
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getAction().toString().toLowerCase());
		sb.append(this.getObject());
		if(this.getPreposition() != null)
			sb.append(this.getPreposition().toStringFirstCharUpperCase());
		if(this.getObjectOfPreposition() != null)
			sb.append(this.getObjectOfPreposition());
		sb.append(CoreRouterSemantics.commandSuffix);
		if(sb.length() > 0)
			return commandPackageSb.toString() + gov.va.med.imaging.core.RouterSemanticsUtility.standardizeToClassName(sb.toString());
		else
			return "";
	}
	
	public String toStringAsImplementation()
	{
		return toString() + implementationSuffix;
	}

	public String getCommandPackage() {
		return commandPackage;
	}
	
	public boolean isCommandPackageSpecified()
	{
		return (commandPackage != null && commandPackage.length() > 0);
	}
}
