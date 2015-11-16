/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

/**
 * This enum lists the packages in which a direct object class
 * must reside.
 * 
 * @author vhaiswbeckec
 *
 */
public enum ObjectVocabulary
{
	// the list of packages to search, in preferred order
	Business("gov.va.med.imaging.exchange.business"), 
	Dicom("gov.va.med.imaging.exchange.business.dicom"),
	VistaRad("gov.va.med.imaging.exchange.business.vistarad"),
	ArtifactSource("gov.va.med.imaging.artifactsource");
	
	
	private final String packageName;
	private Package packagz = null;
	
	ObjectVocabulary(String packageName)
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
		
		for(ObjectVocabulary objectPackage : ObjectVocabulary.values())
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

	/**
	 * Get the package names as a String, formatted as a 
	 * list of import statements.
	 * 
	 * @return
	 */
	public static Iterator<String> packageNames()
	{
		return new Iterator<String>()
		{
			private int index = 0;
			
			@Override
			public boolean hasNext()
			{
				return index < ObjectVocabulary.values().length;
			}

			@Override
			public String next()
			{
				String value = ObjectVocabulary.values()[index++].getPackageName();
				return value;
			}

			@Override
			public void remove(){}
			
		};
	}
	
	/**
	 * Returns true if the given Class is a valid business object,
	 * i.e. belongs to one of the defined business object packages.
	 * 
	 * @param objectType
	 * @return
	 */
	public static boolean isObjectClass(Class<?> objectType)
	{
		if(objectType == null)
			return false;

		String className = objectType.getName();
		String classSimpleName = objectType.getSimpleName();
		
		String packageName = className.substring(0, className.length() - classSimpleName.length());
		if(packageName.endsWith("."))
			packageName = packageName.substring( 0, packageName.length()-1 );
		
		for(Iterator<String> packageNameIter = packageNames(); packageNameIter.hasNext(); )
			if(packageNameIter.next().equals(packageName))
				return true;
		return false;
	}
}
