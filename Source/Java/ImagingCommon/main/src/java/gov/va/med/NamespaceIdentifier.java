package gov.va.med;

import java.io.Serializable;

/**
 * A String wrapper class whose sole function is to
 * differentiate methods and constructors expecting
 * a namespace identifier rather than a concatenated
 * string version of a URN.
 */
public class NamespaceIdentifier
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final String namespace;
	
	public NamespaceIdentifier(String namespace)
	{
		this.namespace = namespace.toLowerCase();
	}

	@Override
	protected Object clone() 
	throws CloneNotSupportedException
	{
		return new NamespaceIdentifier(this.getNamespace());
	}

	public String getNamespace()
	{
		return this.namespace;
	}

	@Override
	public String toString()
	{
		return namespace;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.namespace == null) ? 0 : this.namespace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		final NamespaceIdentifier other = (NamespaceIdentifier) obj;
		if (this.namespace == null)
		{
			if (other.namespace != null)
				return false;
		}
		else if (!this.namespace.equals(other.namespace))
			return false;
		return true;
	}
}