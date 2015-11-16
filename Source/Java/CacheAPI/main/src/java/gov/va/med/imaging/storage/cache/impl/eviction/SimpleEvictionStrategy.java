
package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * A null implementation of an EvictionStrategy.  This realization never evicts
 * from the managed regions.  It is intended as a base class for "real" eviction
 * strategies or for testing.
 * 
 */
public class SimpleEvictionStrategy 
implements EvictionStrategy, SimpleEvictionStrategyMBean 
{
	public final static String namePropertyKey = "name";
	public final static String initializedPropertyKey = "initialized";
	
	private final String name;
	private final Set<Region> managedRegions;
	private boolean initialized = false;
	private boolean regionsModified = false;		// an optimization only, see isRegionsModified()

	/**
	 * Required factory method
	 * 
	 * @param prop
	 * @return
	 */
	public static SimpleEvictionStrategy create(Properties prop)
	{
		String name = (String)prop.get(namePropertyKey);
		return new SimpleEvictionStrategy(name);
	}
	
	public static SimpleEvictionStrategy create(SimpleEvictionStrategyMemento memento)
	{
		return new SimpleEvictionStrategy(memento);
	}
	
	protected SimpleEvictionStrategy(String name)
	{
		this.name = name;
		managedRegions = new java.util.HashSet<Region>();
	}

	/**
	 * Derived classes must set the initialized flag, it is not set
	 * within this constructor to allow derived classes to initialize
	 * before being told that they are initialized.
	 * 
	 * @param memento
	 */
	protected SimpleEvictionStrategy(SimpleEvictionStrategyMemento memento)
	{
		this( memento.getName() );
		// DO NOT set the initialized flag here
		// derived classes may need to do some initialization before being told to
		// initialize.
	}
	
	public String getName()
	{
		return this.name;
	}
	
	// ============================================================================================================
	// Region Management
	// ============================================================================================================
	/**
	 * @see gov.va.med.imaging.storage.cache.EvictionStrategy#addRegion(gov.va.med.imaging.storage.cache.Region)
	 */
	public void addRegion(Region region)
	{
		synchronized(managedRegions)
		{
			managedRegions.add(region);
			regionsModified = true;
		}
	}

	/**
	 * Returns an unmodifiable view into the Region Set
	 * 
	 * @return
	 */
	protected Set<Region> getRegions()
	{
		return Collections.unmodifiableSet(managedRegions);
	}
	
	/**
	 * @see gov.va.med.imaging.storage.cache.EvictionStrategy#getRegionNames()
	 */
	public String[] getRegionNames()
	{
		synchronized(managedRegions)
		{
			String[] result = new String[managedRegions.size()];
			int index = 0;
			
			for(Region region : managedRegions)
				result[index++] = region.getName();
			
			return result;
		}
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.EvictionStrategy#removeRegion(gov.va.med.imaging.storage.cache.Region)
	 */
	public void removeRegion(Region region)
	{
		synchronized(managedRegions)
		{
			managedRegions.remove(region);
			regionsModified = true;
		}
	}

	/**
	 * An optimization for derived classes to determine if they
	 * need to re-generate region dependent data since a previous sweep
	 * this is a destructive read in that calling this method
	 * always set sets the underlying flag to false
	 */
	protected synchronized boolean isRegionsModified()
	{
		boolean retVal = regionsModified;
		regionsModified = false;
		return retVal;
	}
	
	
	// ============================================================================================================
	// Initialization Status Management
	// ============================================================================================================
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.EvictionStrategyMBean#isInitialized()
	 */
	public boolean isInitialized()
	{
		return this.initialized;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.EvictionStrategyMBean#setInitialized(java.lang.Boolean)
	 */
	public void setInitialized(boolean initialized)
	throws CacheException
	{
		this.initialized = initialized;  
	}

	public SimpleEvictionStrategyMemento createMemento()
	{
		return new SimpleEvictionStrategyMemento(getName(), isInitialized());
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getName());
		sb.append(" : ");
		sb.append(getClass().getName());
		
		return sb.toString();
	}
	
}
