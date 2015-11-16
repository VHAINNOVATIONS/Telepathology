/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.config;

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
public class TestWithNoEvictionConfigurationStrategy 
{
	/**
	 * Must implement a public no-arg constructor, instances of this class are
	 * instantiated through reflection.
	 */
	public TestWithNoEvictionConfigurationStrategy()
	{
		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.CacheConfigurationStrategy#getEvictionStrategyMementoes()
	 */
	public List<? extends EvictionStrategyMemento> getEvictionStrategyMementoes()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.CacheConfigurationStrategy#getRegionMementoes()
	 */
	public List<? extends RegionMemento> getRegionMementoes()
	{
		List<RegionMemento> regions = new ArrayList<RegionMemento>();
		
		RegionMemento memento = null;
		
		memento = new PersistentRegionMemento();
		memento.setName("test-image");
		memento.setEvictionStrategyNames(null);
		regions.add(memento);
		
		memento = new PersistentRegionMemento();
		memento.setName("test-metadata");
		memento.setEvictionStrategyNames(null);
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
		TestWithNoEvictionConfigurationStrategy strategy = new TestWithNoEvictionConfigurationStrategy();
		
		CacheConfigurationMemento memento = strategy.getMemento();
		
		XMLEncoder encoder = new XMLEncoder(System.out);
	
		encoder.writeObject(memento);
		
		encoder.flush();
		encoder.close();
	}
}
