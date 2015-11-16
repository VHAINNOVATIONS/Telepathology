/**
 * 
 */
package gov.va.med.imaging.storage.cache.memento;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class EvictionStrategyMemento
{
	private String name;
	private boolean initialized;
	
	public EvictionStrategyMemento()
	{
	}

	public EvictionStrategyMemento(String name, boolean initialized)
	{
		super();
		this.name = name;
		this.initialized = initialized;
	}

	public boolean isInitialized()
	{
		return this.initialized;
	}

	public void setInitialized(boolean initialized)
	{
		this.initialized = initialized;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (this.initialized ? 1231 : 1237);
		result = PRIME * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EvictionStrategyMemento other = (EvictionStrategyMemento) obj;
		if (this.initialized != other.initialized)
			return false;
		if (this.name == null)
		{
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}
	
	
}
