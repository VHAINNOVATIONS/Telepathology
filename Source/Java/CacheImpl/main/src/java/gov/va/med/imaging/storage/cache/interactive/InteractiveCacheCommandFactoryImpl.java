/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.CommandListCommandFactory;

/**
 * @author vhaiswbeckec
 *
 */
public class InteractiveCacheCommandFactoryImpl
extends CommandListCommandFactory<CacheManagerImpl>
{
	/**
	 * Implement typical singleton pattern
	 */
	private static InteractiveCacheCommandFactoryImpl singleton;
	public static synchronized InteractiveCacheCommandFactoryImpl getSingleton()
	{
		if(singleton == null)
			try{singleton = new InteractiveCacheCommandFactoryImpl();}
			catch(Exception x){x.printStackTrace();}
		
		return singleton;
	}
	
	protected InteractiveCacheCommandFactoryImpl() 
	throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		super();
	}
	
	@Override
	public String[] getCommandPackageNames()
	{
		return new String[]
		{
			"gov.va.med.interactive.commands",						// standard commands
			"gov.va.med.imaging.storage.cache.interactive.commands"	// application commands
		};
	}
}
