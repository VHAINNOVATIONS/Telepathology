/**
 * 
 */
package gov.va.med.imaging.channels;

import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.zip.Checksum;

/**
 * This class is a value-object and formatter for a checksum w/algorithm.
 * Checksums are transported as Strings in the format:
 * {<algorithm>}<value>
 * where: 
 * <algorithm> is the name of the algorithm.
 * <value> is the checksum value calculated using the named algorithm
 * 
 * This class works both directions; 
 * 1.) constructed with the algorithm name and value the toString() method 
 * returns a String in the above form
 * 2.) constructed from a String in the above format, the algorithm name
 * and the value are available as properties
 * 
 * @author VHAISWBECKEC
 *
 */
public class ChecksumValue
implements Serializable
{
	private static final long serialVersionUID = -1406186379909589089L;
	public final static String algorithmStartDelimiter="{";
	public final static String algorithmEndDelimiter="}";
	
	private String algorithm;
	private BigInteger value;
	
	/**
	 * 
	 * @param checksum
	 */
	public ChecksumValue(Checksum checksum)
	{
		this(checksum.getClass().getSimpleName(), BigInteger.valueOf(checksum.getValue()) );
	}
	
	public ChecksumValue(String algorithm, long value)
	{
		this(algorithm, BigInteger.valueOf(value));
	}
	
	/**
	 * Construct a ChecksumValue object from an algorithm and value.
	 * 
	 * @param algorithm
	 * @param value
	 */
	public ChecksumValue(String algorithm, BigInteger value)
	{
		super();
		this.algorithm = algorithm;
		this.value = value;
	}

	/**
	 * Construct a ChecksumValue object from a stringified representation of the
	 * checksum.
	 * 
	 * @param stringified
	 * @throws ChecksumFormatException
	 */
	public ChecksumValue(String stringified)
	throws ChecksumFormatException
	{
		if(stringified == null)
			throw new ChecksumFormatException("Attempt to create a checksum from a null String.");
		
		if( stringified.startsWith(algorithmStartDelimiter) )
		{
			stringified = stringified.substring(1);
			String[] parts = stringified.split(algorithmEndDelimiter);
			if(parts.length == 2)
			{
				this.algorithm = parts[0].trim();
				try
				{
					// try base 10
					try{this.value = new BigInteger(parts[1].trim());}
					catch(NumberFormatException nfx1)
					// try base 16
					{this.value = new BigInteger(parts[1].trim(), 16);}
				}
				catch(NumberFormatException nfX)
				{
					throw new ChecksumFormatException("The String '" + stringified + "' is not a validly formatted ChecksumValue String representation.  The value is not a number.");
				}
			}
			else
				throw new ChecksumFormatException("The String '" + stringified + 
					"' is not a validly formatted ChecksumValue String representation.  There should be only one instance of the character '" + 
					algorithmEndDelimiter + "'");
		}
		else
			throw new ChecksumFormatException("The String '" + stringified + "' is not a validly formatted ChecksumValue String representation." + 
					"The String must start with the character '" + algorithmStartDelimiter + "'.");
	}
	
	public String getAlgorithm()
	{
		return this.algorithm;
	}
	
	public BigInteger getValue()
	{
		return this.value;
	}

	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
	}

	public void setValue(BigInteger value)
	{
		this.value = value;
	}

	/**
	 * Return a String in the form
	 * “{“<checksum algorithm>”}”<checksum value>
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(algorithmStartDelimiter);
		sb.append(algorithm);
		sb.append(algorithmEndDelimiter);
		sb.append( value.toString() );
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.algorithm == null) ? 0 : this.algorithm.hashCode());
		result = PRIME * result + ((this.value == null) ? 0 : this.value.hashCode());
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
		final ChecksumValue other = (ChecksumValue) obj;
		if (this.algorithm == null)
		{
			if (other.algorithm != null)
				return false;
		} else if (!this.algorithm.equals(other.algorithm))
			return false;
		if (this.value == null)
		{
			if (other.value != null)
				return false;
		} else if (!this.value.equals(other.value))
			return false;
		return true;
	}

	
}
