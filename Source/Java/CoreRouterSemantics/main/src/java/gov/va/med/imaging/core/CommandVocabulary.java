/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.Iterator;

/**
 * This enum lists the packages in which a command implementations
 * must reside.
 * 
 * @author vhaiswbeckec
 *
 */
public enum CommandVocabulary
{
	// the list of packages to search, in preferred order
	Commands("gov.va.med.imaging.core.router.commands"), 
	DataSourceCommands("gov.va.med.imaging.core.router.commands.datasource"),
	VistaRadCommands("gov.va.med.imaging.core.router.commands.vistarad"),
	VistaRadDataSourceCommands("gov.va.med.imaging.core.router.commands.vistarad.datasource"),
	DocumentCommands("gov.va.med.imaging.core.router.commands.documents"),
	DocumentDataSourceCommands("gov.va.med.imaging.core.router.commands.documents.datasource"),
	StorageCommands("gov.va.med.imaging.core.router.commands.storage"),
	StorageDataSourceCommands("gov.va.med.imaging.core.router.commands.storage.datasource"),
	Artifacts("gov.va.med.imaging.core.router.commands.artifacts"),
	ArtifactsDataSourceCommands("gov.va.med.imaging.core.router.commands.artifacts.datasource"),
	DicomImporter("gov.va.med.imaging.core.router.commands.dicom.importer"),
	WorkList("gov.va.med.imaging.core.router.commands.worklist");
	
	
	private final String packageName;
	private Package packagz = null;
	
	CommandVocabulary(String packageName)
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
		
		for(CommandVocabulary objectPackage : CommandVocabulary.values())
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
				return index < CommandVocabulary.values().length;
			}

			@Override
			public String next()
			{
				String value = CommandVocabulary.values()[index++].getPackageName();
				return value;
			}

			@Override
			public void remove(){}
			
		};
	}
	
}
