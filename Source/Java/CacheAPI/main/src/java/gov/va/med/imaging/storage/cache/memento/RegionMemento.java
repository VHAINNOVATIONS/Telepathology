/**
 * 
 */
package gov.va.med.imaging.storage.cache.memento;

import java.io.Serializable;

/**
 * @author VHAISWBECKEC
 *
 */
public class RegionMemento
implements Serializable
{
	private static final long serialVersionUID = 3627680403901651508L;
	private String name;
	private String[] evictionStrategyNames;

	public RegionMemento()
	{
		super();
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String[] getEvictionStrategyNames()
	{
		return this.evictionStrategyNames;
	}

	public void setEvictionStrategyNames(String[] evictionStrategyName)
	{
		this.evictionStrategyNames = evictionStrategyName;
	}
	
}
