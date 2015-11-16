/**
 * 
 */
package gov.va.med.imaging.core;

/**
 * This enum lists the packages in which a preposition object class
 * must reside.
 * 
 * @author vhaiswbeckec
 *
 */
public enum PrepositionObjectVocabulary
{
	// the list of packages to search, in preferred order
	Business("gov.va.med.imaging.core.business"),
	Common("gov.va.med.imaging");
	
	private final String packageName;
	private Package packagz = null;
	
	PrepositionObjectVocabulary(String packageName)
	{
		this.packageName = packageName;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName()
	{
		return this.packageName;
	}
	
	public synchronized Package getPackage()
	{
		if(packagz == null)
			packagz = Package.getPackage(getPackageName());
		
		return packagz;
	}
	
	/**
	 * 
	 * @param simpleName
	 * @return
	 */
	public static Class<?> getClass(String simpleName)
	{
		Class<?> result = null;
		
		for(PrepositionObjectVocabulary objectPackage : PrepositionObjectVocabulary.values())
		{
			String objectClassName = objectPackage.getPackageName() + "." + simpleName;
			try
			{
				result = Class.forName(objectClassName);
				break;
			} 
			catch (ClassNotFoundException x)
			{
				// do nothing, this is just part of the searching
			}
		}
		
		return result;
	}
}
