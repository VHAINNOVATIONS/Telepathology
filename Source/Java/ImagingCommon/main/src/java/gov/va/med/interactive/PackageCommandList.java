/**
 * 
 */
package gov.va.med.interactive;

import java.util.ResourceBundle;

/**
 * Load the command lists from the package name specified in the constructor or
 * the package that this class resides in if no package is given.
 * 
 * @author vhaiswbeckec
 *
 */
public class PackageCommandList
extends ResourceBundleCommandList
implements CommandList
{
	private final String packageName;
	private final String bundleName;
	private final ResourceBundle resourceBundle;

	protected PackageCommandList()
	{
		this(null);
	}
	
	protected PackageCommandList(String packageName)
	{
		this.packageName = (packageName == null ? this.getClass().getPackage().getName() : packageName); 
		this.bundleName = this.packageName + ".commands";
		this.resourceBundle = ResourceBundle.getBundle(bundleName);	
	}
	

	/**
	 * @return the resourceBundle
	 */
	@Override
	public ResourceBundle getResourceBundle()
	{
		return this.resourceBundle;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName()
	{
		return this.packageName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Command<?>> getCommandClass(String commandName) 
	throws ClassNotFoundException
	{
		String className = getPackageName() + "." + commandName;
		if(className != null)
			return (Class<? extends Command<?>>)Class.forName(className);
		
		return null;
	}
	
}
