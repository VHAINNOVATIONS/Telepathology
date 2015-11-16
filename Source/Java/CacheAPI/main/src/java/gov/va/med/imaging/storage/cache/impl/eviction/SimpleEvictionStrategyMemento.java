/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.eviction;

import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;

/**
 * @author VHAISWBECKEC
 *
 */
public class SimpleEvictionStrategyMemento 
extends EvictionStrategyMemento
{

	public SimpleEvictionStrategyMemento()
	{
		super();
	}

	public SimpleEvictionStrategyMemento(String name, boolean initialized)
	{
		super(name, initialized);
	}

}
