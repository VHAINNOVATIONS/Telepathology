package gov.va.med.imaging;

/**
 * A String representation of an IPv4 whose primary purpose is to
 * serve for range checking of IP addresses.
 * compareTo always returns -1, 0, 1 if the class compared to is an IPv4String or a String in IPv4 format,
 * oterwise return -99
 * 
 * @author VHAISWBECKEC
 *
 */
public class IPv4String 
implements Comparable
{
	/**
	 * Static instances defining multicast address ranges
	 * See http://tools.ietf.org/html/rfc3171 
	 */
	public final static IPv4String localNetworkControlMinimum = create("224.0.0.0");
	public final static IPv4String localNetworkControlMaximum = create("224.0.0.255");
	
	public final static IPv4String interNetworkControlMinimum = create("224.0.1.0");
	public final static IPv4String interNetworkControlMaximum = create("224.0.1.255");
	
	public final static IPv4String adhocMinimum = create("224.0.2.0");
	public final static IPv4String adhocMaximum = create("224.0.255.0");
	
	public final static IPv4String stMulticastGroupsMinimum = create("224.1.0.0");
	public final static IPv4String stMulticastGroupsMaximum = create("224.1.255.255");
	
	public final static IPv4String sdpSapMinimum = create("224.2.0.0");
	public final static IPv4String sdpSapMaximum = create("224.2.255.255");
	
	public final static IPv4String disTransientMinimum = create("224.252.0.0");
	public final static IPv4String disTransientMaximum = create("224.255.255.255");
	
	public final static IPv4String reserved1Minimum = create("225.0.0.0");
	public final static IPv4String reserved1Maximum = create("231.255.255.255");
	
	public final static IPv4String sourceSpecificMulticastMinimum = create("232.0.0.0");
	public final static IPv4String sourceSpecificMulticastMaximum = create("232.255.255.255");
	
	public final static IPv4String glopMinimum = create("233.0.0.0");
	public final static IPv4String glopMaximum = create("233.255.255.255");
	
	public final static IPv4String reserved2Minimum = create("234.0.0.0");
	public final static IPv4String reserved2Maximum = create("238.255.255.255");
	
	public final static IPv4String administrativelyScopedMinimum = create("239.0.0.0");
	public final static IPv4String administrativelyScopedMaximum = create("239.255.255.255");
	
	private int[] octets = null;

	/**
	 * Create an IPv4String from the given String.
	 * 
	 * @param value a String in the format xxx.xxx.xxx.xxx, where xxx is a value from 0 to 255
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static IPv4String create(String value)
	throws ArrayIndexOutOfBoundsException
	{
		String[] stringOctets = value.split("\\u002e");
		if(stringOctets.length == 4)
		{
			int[] octetBytes = new int[4];		// bytes stored as int, cause bytes are signed and that screws everything up
			for(int index=0; index<4; ++index)
			{
				int octetValue = Integer.parseInt(stringOctets[index]);
				if(octetValue < 0 || octetValue > 255)
					throw new java.lang.ArrayIndexOutOfBoundsException("A string representation of an IPv4 must consist of four octet values (0 to 255) delimited by dots.");
				octetBytes[index] = octetValue;
			}
			
			return new IPv4String(octetBytes);
		}
		
		throw new java.lang.ArrayIndexOutOfBoundsException("A string representation of an IPv4 must consist of four octet values (0 to 255) delimited by dots.");
	}
	
	private IPv4String(int[] octets)
	{
		this.octets = octets;
	}
	
	public int[] getOctets()
	{
		return octets;
	}
	
	/**
	 * 
	 */
	public int compareTo(Object o)
	{
		if(o instanceof String)
		{
			try
			{
				IPv4String that = IPv4String.create((String)o);
				return compareTo(that);
			} 
			catch (ArrayIndexOutOfBoundsException aioobX)
			{
				// an attempt to compare to a String that is not in IPv4 format
				// drop through and return a -99
			}
		}
		
		else if(o instanceof IPv4String)
		{
			IPv4String that = (IPv4String)o;
			for(int index=0; index<4; ++index)
				if(this.octets[index] < that.octets[index])
					return -1;
				else if(this.octets[index] > that.octets[index])
					return 1;
			
			return 0;
		}
		
		return -99;		// no clue, return something distinctive
	}

	/**
	 * The hashCode is an int representation of an IPv4.
	 */
	@Override
	public int hashCode()
	{
		return (octets[0] << 24) + (octets[1] << 16) + (octets[2] << 8) + octets[3]; 
	}

	/**
	 * Returns something that looks like an IPv4 address in a String
	 */
	@Override
	public String toString()
	{
		return "" + octets[0] + "." + octets[1] + "." + octets[2] + "." + octets[3]; 
	}

	/**
	 * Returns true if the Object is an instance of IPv4String
	 * and the octets are identical, or if the Object is an instance
	 * of String that may be interpreted as an IPv4String and the
	 * resulting octets are identical.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof IPv4String)
			return ((IPv4String)obj).hashCode() == hashCode();
		else if(obj instanceof String)
		{
			try
			{
				return create((String)obj).hashCode() == hashCode();
			}
			// let the exception drop through, return false
			catch(ArrayIndexOutOfBoundsException aioobX){}
		}
		return false;
	}
}
