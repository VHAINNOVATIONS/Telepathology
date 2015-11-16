/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.config;

import gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMemento;
import gov.va.med.imaging.storage.cache.impl.eviction.StorageThresholdEvictionStrategyMemento;
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
public class VixConfigurationStrategy 
{
	public static final long KILOBYTE = 1024L;
	public static final long MEGABYTE = KILOBYTE * KILOBYTE;
	public static final long GIGABYTE = KILOBYTE * MEGABYTE;
	public static final long TERABYTE = KILOBYTE * GIGABYTE;
	public static final long PETABYTE = KILOBYTE * TERABYTE;
	
	/**
	 * Must implement a public no-arg constructor, instances of this class are
	 * instantiated through reflection.
	 */
	public VixConfigurationStrategy()
	{
		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.CacheConfigurationStrategy#getEvictionStrategyMementoes()
	 */
	public List<? extends EvictionStrategyMemento> getEvictionStrategyMementoes()
	{
		List<EvictionStrategyMemento> evictionStrategies = new ArrayList<EvictionStrategyMemento>();
		
		EvictionStrategyMemento memento = null;
		
		memento = new LastAccessedEvictionStrategyMemento();
		memento.setName("one-hour-lifespan");
		((LastAccessedEvictionStrategyMemento)memento).setMaximumTimeSinceLastAccess(3600000L);
		memento.setInitialized(true);
		evictionStrategies.add(memento);
		
		memento = new LastAccessedEvictionStrategyMemento();
		memento.setName("one-day-lifespan");
		((LastAccessedEvictionStrategyMemento)memento).setMaximumTimeSinceLastAccess(86400000L);
		memento.setInitialized(true);
		evictionStrategies.add(memento);

		memento = new LastAccessedEvictionStrategyMemento();
		memento.setName("seven-day-lifespan");
		((LastAccessedEvictionStrategyMemento)memento).setMaximumTimeSinceLastAccess(604800000L);
		memento.setInitialized(true);
		evictionStrategies.add(memento);
		
		memento = new LastAccessedEvictionStrategyMemento();
		memento.setName("thirty-day-lifespan");
		((LastAccessedEvictionStrategyMemento)memento).setMaximumTimeSinceLastAccess(2592000000L);
		memento.setInitialized(true);
		evictionStrategies.add(memento);

		memento = new StorageThresholdEvictionStrategyMemento(
			"image-storage-threshold", 
			true,
			1L * GIGABYTE, 
			2L * GIGABYTE,
			20L * GIGABYTE,
			12L * 60L * 60L * 1000L, 	// wait 12 hours for first run
			24L * 60L * 60L * 1000L		// every day thereafter
		);
		evictionStrategies.add(memento);
		
		memento = new StorageThresholdEvictionStrategyMemento(
			"metadata-storage-threshold", 
			true,
			10L * MEGABYTE, 
			200L * MEGABYTE,
			1L * GIGABYTE,
			10L * 60L * 60L * 1000L, 	// wait 10 hours for first run
			24L * 60L * 60L * 1000L		// every day thereafter
		);
		evictionStrategies.add(memento);
		
		return evictionStrategies;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.CacheConfigurationStrategy#getRegionMementoes()
	 */
	public List<? extends RegionMemento> getRegionMementoes()
	{
		List<RegionMemento> regions = new ArrayList<RegionMemento>();
		
		PersistentRegionMemento memento = null;
		
		memento = new PersistentRegionMemento();
		memento.setName("va-metadata-region");
		memento.setEvictionStrategyNames(new String[]{"one-hour-lifespan", "metadata-storage-threshold"});
		memento.setSecondsReadWaitsForWriteCompletion(60);
		regions.add(memento);

		memento = new PersistentRegionMemento();
		memento.setName("va-image-region");
		memento.setEvictionStrategyNames(new String[]{"seven-day-lifespan", "image-storage-threshold"});
		memento.setSecondsReadWaitsForWriteCompletion(60);
		regions.add(memento);

		memento = new PersistentRegionMemento();
		memento.setName("dod-image-region");
		memento.setEvictionStrategyNames(new String[]{"thirty-day-lifespan", "image-storage-threshold"});
		memento.setSecondsReadWaitsForWriteCompletion(60);
		regions.add(memento);
		
		memento = new PersistentRegionMemento();
		memento.setName("dod-metadata-region");
		memento.setEvictionStrategyNames(new String[]{"one-day-lifespan", "metadata-storage-threshold"});
		memento.setSecondsReadWaitsForWriteCompletion(60);
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
		VixConfigurationStrategy strategy = new VixConfigurationStrategy();
		
		CacheConfigurationMemento memento = strategy.getMemento();
		
		XMLEncoder encoder = new XMLEncoder(System.out);
	
		encoder.writeObject(memento);
		
		encoder.flush();
		encoder.close();
	}
}
