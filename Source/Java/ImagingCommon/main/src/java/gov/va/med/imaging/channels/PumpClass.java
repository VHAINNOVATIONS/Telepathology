package gov.va.med.imaging.channels;

import gov.va.med.imaging.channels.AbstractBytePump.MEDIUM;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
class PumpClass
{
	private String name;
	private MEDIUM medium;
	
	public PumpClass(String name, MEDIUM medium)
	{
		super();
		this.name = name;
		this.medium = medium;
	}

	public MEDIUM getMedium()
	{
		return this.medium;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.medium == null) ? 0 : this.medium.hashCode());
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
		final PumpClass other = (PumpClass) obj;
		if (this.medium == null)
		{
			if (other.medium != null)
				return false;
		} else if (!this.medium.equals(other.medium))
			return false;
		if (this.name == null)
		{
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append(".");
		sb.append("medium=" + this.medium);
		sb.append(".");
		sb.append("name=" + this.name);
		
		return sb.toString();
	}
}