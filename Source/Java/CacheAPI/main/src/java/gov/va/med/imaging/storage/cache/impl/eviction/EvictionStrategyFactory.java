/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.EvictionTimer;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * A factory class for creating Eviction Strategies.  There is one EvictionStrategyFactory per VM,
 * i.e. a Singleton.
 *
 */
public class EvictionStrategyFactory
{
	public final static String evictionStrategyClassnameKey = "evictionStrategyClassname";

	private static EvictionStrategyFactory singleton;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	// the list of all known eviction strategy classes and the memento
	// classes required to create them
	private Map<Class<? extends EvictionStrategy>, Class<? extends EvictionStrategyMemento>> evictionStrategiesMap = null;
	
	public static synchronized EvictionStrategyFactory getSingleton()
	{
		if(singleton == null)
		{
			singleton = new EvictionStrategyFactory();
		}
		return singleton;
	}

	/**
	 * Get (and possibly populate) the known eviction strategies map.
	 * @return
	 */
	public synchronized Map<Class<? extends EvictionStrategy>, Class<? extends EvictionStrategyMemento>> getKnownEvictionStrategies()
	{
		if(evictionStrategiesMap == null)
		{
			evictionStrategiesMap = new HashMap<Class<? extends EvictionStrategy>, Class<? extends EvictionStrategyMemento>>();
			
			evictionStrategiesMap.put(SimpleEvictionStrategy.class, SimpleEvictionStrategyMemento.class);
			evictionStrategiesMap.put(StorageThresholdEvictionStrategy.class, StorageThresholdEvictionStrategyMemento.class);
			evictionStrategiesMap.put(LastAccessedEvictionStrategy.class, LastAccessedEvictionStrategyMemento.class);
			
		}
		return evictionStrategiesMap;
	}
	
	/**
	 * Get an array lising all of the known eviction strategy names.
	 * 
	 * @return
	 */
	public String[] getKnownEvictionStrategyNames()
	{
		String[] strategyNames = new String[getKnownEvictionStrategies().keySet().size()];
		int index = 0;
		
		for(Class<? extends EvictionStrategy> strategyClass : getKnownEvictionStrategies().keySet())
			strategyNames[index++] = strategyClass.getName();
		
		return strategyNames;
	}
	
	/**
	 * Return the eviction strategy Class with the given name.  The name must be one of those
	 * returned by @see {@link #getKnownEvictionStrategyNames()}
	 * 
	 * @param evictionStrategyClassname
	 * @return
	 */
	public Class<? extends EvictionStrategy> findEvictionStrategyClassByName(String evictionStrategyClassname)
	{
		if(evictionStrategyClassname == null)
			return null;
		
		for(Class<? extends EvictionStrategy> evictionStrategyClass : getKnownEvictionStrategies().keySet())
		{
			 if( evictionStrategyClassname.equals(evictionStrategyClass.getName()) ||
					evictionStrategyClassname.equals(evictionStrategyClass.getSimpleName()) )
				return evictionStrategyClass;
		}
		return null;
	}
	
	/**
	 * Return the memento class thet the eviction strategy class withthe given name produces, and
	 * is instantiable from.  The name must be one of those returned by @see {@link #getKnownEvictionStrategyNames()}
	 * 
	 * @param evictionstrategyClassname
	 * @return
	 */
	private Class<? extends EvictionStrategyMemento> findEvictionStrategyMementoClassByName(String evictionStrategyClassname)
	{
		Class<? extends EvictionStrategy> evictionStrategyClass = findEvictionStrategyClassByName(evictionStrategyClassname);
		
		if(evictionStrategyClass == null)
			return null;
		
		
		return getKnownEvictionStrategies().get(evictionStrategyClass);
	}
	
	/**
	 * Create a blank memento for the given eviction strategy class name.  The name must be one of those
	 * returned by @see {@link #getKnownEvictionStrategyNames()}
	 * 
	 * @param evictionStrategyClassname
	 * @return
	 */
	public EvictionStrategyMemento createBlankMemento(String evictionStrategyClassname)
	{
		Class mementoClass = findEvictionStrategyMementoClassByName(evictionStrategyClassname);
		try
		{
			return (EvictionStrategyMemento)mementoClass.newInstance();
		} 
		catch (InstantiationException x)
		{
			logger.error(x);
			return null;
		} 
		catch (IllegalAccessException x)
		{
			logger.error(x);
			return null;
		}
	}
	
	/**
	 * Create an eviction strategy instance from the given memento.  The type of the memento instance
	 * determines the eviction strategy type returned.
	 * 
	 * @param memento
	 * @param timer
	 * @return
	 * @throws CacheException 
	 */
	public EvictionStrategy createEvictionStrategy(EvictionStrategyMemento memento, EvictionTimer timer) 
	throws CacheException
	{
		// WARNING: ordering of comparisons is significant because the
		// memento classes may derive from one another
		if(memento instanceof LastAccessedEvictionStrategyMemento)
			return LastAccessedEvictionStrategy.create((LastAccessedEvictionStrategyMemento)memento, timer);

		if(memento instanceof StorageThresholdEvictionStrategyMemento)
			return StorageThresholdEvictionStrategy.create((StorageThresholdEvictionStrategyMemento)memento, timer);
		
		if(memento instanceof SimpleEvictionStrategyMemento)
			return SimpleEvictionStrategy.create((SimpleEvictionStrategyMemento)memento);

		return null;
	}
}
