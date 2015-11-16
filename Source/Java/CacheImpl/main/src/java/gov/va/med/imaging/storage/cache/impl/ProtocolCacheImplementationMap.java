/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import java.util.HashMap;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.memento.CacheMemento;

/**
 * @author VHAISWBECKEC
 *
 * A map from the protocol name to the cache and configurator classes taht support that
 * protocol.
 * 
 * NOTE: the protocol string is case-insensitive, it is nternally converted to lower case.
 */
public class ProtocolCacheImplementationMap
extends HashMap<String, ProtocolCacheImplementationMap.ProtocolMapEntry>
{
	private static final long serialVersionUID = -5114037358662556921L;

	public void put(String protocol, 
			Class<? extends Cache> cacheClass, 
			Class<? extends CacheConfigurator> configuratorClass,
			Class<? extends CacheMemento> mementoClass)
	{
		super.put(
			protocol.toLowerCase(), 
			new ProtocolMapEntry(cacheClass, configuratorClass, mementoClass)
		);
	}
	
	/**
	 * Get a ProtocolMapEntry from the protocol.
	 * 
	 * @param protocol
	 * @return
	 */
	public ProtocolMapEntry get(String protocol)
	{
		if(protocol == null)
			return null;
		
		return super.get(protocol.toLowerCase());
	}
	
	/**
	 * Get the ProtocolMapEntry from a CacheMemento instance
	 * 
	 * @param memento
	 * @return
	 */
	public ProtocolMapEntry get(CacheMemento memento)
	{
		if(memento == null)
			return null;

		for(String protocol:this.keySet())
		{
			ProtocolMapEntry value = get(protocol);
			if( value.getMementoClass().isInstance(memento) )
				return value;
		}
		
		return null;
	}
	
	public Class<? extends Cache> getCacheClass(String protocol)
	{
		ProtocolMapEntry mapEntry = get(protocol);
		return mapEntry == null ? null : mapEntry.getCacheClass();
	}
	
	public Class<? extends CacheConfigurator> getConfiguratorClass(String protocol)
	{
		ProtocolMapEntry mapEntry = get(protocol);
		return mapEntry == null ? null : mapEntry.getConfiguratorClass();
	}

	public Class<? extends Cache> getCacheClass(CacheMemento memento)
	{
		ProtocolMapEntry mapEntry = get(memento);
		return mapEntry == null ? null : mapEntry.getCacheClass();
	}
	
	public Class<? extends CacheConfigurator> getConfiguratorClass(CacheMemento memento)
	{
		ProtocolMapEntry mapEntry = get(memento);
		return mapEntry == null ? null : mapEntry.getConfiguratorClass();
	}
	
	public class ProtocolMapEntry
	{
		private Class<? extends Cache> cacheClass;
		private Class<? extends CacheConfigurator> configuratorClass;
		private Class<? extends CacheMemento> mementoClass;
		
		public ProtocolMapEntry(
				Class<? extends Cache> cacheClass, 
				Class<? extends CacheConfigurator> configuratorClass,
				Class<? extends CacheMemento> mementoClass)
		{
			this.cacheClass = cacheClass;
			this.configuratorClass = configuratorClass;
			this.mementoClass = mementoClass;
		}

		public Class<? extends Cache> getCacheClass()
		{
			return this.cacheClass;
		}

		public Class<? extends CacheConfigurator> getConfiguratorClass()
		{
			return this.configuratorClass;
		}

		public Class<? extends CacheMemento> getMementoClass()
		{
			return this.mementoClass;
		}
		
	}
}
