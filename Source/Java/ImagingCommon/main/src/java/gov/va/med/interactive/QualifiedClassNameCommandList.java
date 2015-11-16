/**
 * 
 */
package gov.va.med.interactive;

import java.util.ResourceBundle;

/**
 * @author vhaiswbeckec
 *
 */
public class QualifiedClassNameCommandList
extends ResourceBundleCommandList
implements CommandList
{
	public QualifiedClassNameCommandList create(String resourceBundleName)
	{
		return new QualifiedClassNameCommandList(resourceBundleName);
	}

	// ==========================================================================
	private final ResourceBundle resourceBundle;

	private QualifiedClassNameCommandList(String resourceBundleName)
	{
		this.resourceBundle = ResourceBundle.getBundle(resourceBundleName);	
	}

	/**
	 * @see gov.va.med.interactive.ResourceBundleCommandList#getResourceBundle()
	 */
	@Override
	protected ResourceBundle getResourceBundle()
	{
		return resourceBundle;
	}
	
	/**
	 * 
	 * @param commandName
	 * @return
	 * @throws ClassNotFoundException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Command<?>> getCommandClass(String commandName) 
	throws ClassNotFoundException
	{
		String className = getString(commandName + "." + COMMAND_CLASS);
		if(className != null)
			return (Class<? extends Command<?>>)Class.forName(className);
		
		return null;
	}
}
