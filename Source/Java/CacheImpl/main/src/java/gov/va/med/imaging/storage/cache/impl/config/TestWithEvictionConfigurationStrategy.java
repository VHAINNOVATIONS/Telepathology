/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.config;

import gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMemento;
import gov.va.med.imaging.storage.cache.impl.memento.PersistentRegionMemento;
import gov.va.med.imaging.storage.cache.memento.CacheConfigurationMemento;
import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;
import gov.va.med.imaging.storage.cache.memento.RegionMemento;

import java.beans.XMLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestWithEvictionConfigurationStrategy 
{
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.CacheConfigurationStrategy#getEvictionStrategyMementoes()
	 */
	private List<? extends EvictionStrategyMemento> getEvictionStrategyMementoes()
	{
		List<EvictionStrategyMemento> evictionStrategies = new ArrayList<EvictionStrategyMemento>();
		
		LastAccessedEvictionStrategyMemento memento = null;
		
		memento = new LastAccessedEvictionStrategyMemento();
		memento.setName("ten-second-lifespan");
		memento.setMaximumTimeSinceLastAccess(10L * 1000L);
		memento.setInitialized(true);
		evictionStrategies.add(memento);

		memento = new LastAccessedEvictionStrategyMemento();
		memento.setName("one-minute-lifespan");
		memento.setMaximumTimeSinceLastAccess(60L * 1000L);
		memento.setInitialized(true);
		evictionStrategies.add(memento);
		
		return evictionStrategies;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.CacheConfigurationStrategy#getRegionMementoes()
	 */
	private List<? extends RegionMemento> getRegionMementoes()
	{
		List<RegionMemento> regions = new ArrayList<RegionMemento>();
		
		RegionMemento memento = null;
		
		memento = new PersistentRegionMemento();
		memento.setName("test-image");
		memento.setEvictionStrategyNames(new String[]{"one-minute-lifespan"});
		regions.add(memento);
		
		memento = new PersistentRegionMemento();
		memento.setName("test-metadata");
		memento.setEvictionStrategyNames(new String[]{"ten-second-lifespan"});
		regions.add(memento);
		
		return regions;
	}

	public CacheConfigurationMemento getMemento()
	{
		CacheConfigurationMemento memento = new CacheConfigurationMemento(getEvictionStrategyMementoes(), getRegionMementoes());
		
		return memento;
	}
	
	public static void main(String[] argv)
	{
		TestWithEvictionConfigurationStrategy strategy = new TestWithEvictionConfigurationStrategy();
		
		CacheConfigurationMemento memento = strategy.getMemento();
		
		XMLEncoder encoder = new XMLEncoder(System.out);
	
		encoder.writeObject(memento);
		
		encoder.flush();
		encoder.close();
	}
}
