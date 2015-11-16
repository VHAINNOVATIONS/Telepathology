/**
 * 
 */
package gov.va.med.imaging.storage.cache.memento;

import java.io.Serializable;
import java.util.List;

/**
 * This class is the container for serializing and deserializing configuration strategies
 * for caches.  A configuration strategy is the definition of the regions and eviction
 * strategies that a cache should have.
 * 
 * The region and eviction strategies may be implementation specific but that should be
 * avoided if possible.
 * 
 * @author VHAISWBECKEC
 *
 */
public class CacheConfigurationMemento 
implements Serializable
{
	private static final long serialVersionUID = -1612861631046608357L;
	private List<? extends EvictionStrategyMemento> evictionStrategyMementoes;
	private List<? extends RegionMemento> regionMementoes;
	
	/**
	 * Required public no-arg constructor
	 */
	public CacheConfigurationMemento()
	{
	}

	public CacheConfigurationMemento(List<? extends EvictionStrategyMemento> evictionStrategyMementoes, List<? extends RegionMemento> regionMementoes)
	{
		super();
		this.evictionStrategyMementoes = evictionStrategyMementoes;
		this.regionMementoes = regionMementoes;
	}

	public List<? extends EvictionStrategyMemento> getEvictionStrategyMementoes()
	{
		return this.evictionStrategyMementoes;
	}

	public void setEvictionStrategyMementoes(List<? extends EvictionStrategyMemento> evictionStrategyMementoes)
	{
		this.evictionStrategyMementoes = evictionStrategyMementoes;
	}

	public List<? extends RegionMemento> getRegionMementoes()
	{
		return this.regionMementoes;
	}

	public void setRegionMementoes(List<? extends RegionMemento> regionMementoes)
	{
		this.regionMementoes = regionMementoes;
	}
	
	
}
