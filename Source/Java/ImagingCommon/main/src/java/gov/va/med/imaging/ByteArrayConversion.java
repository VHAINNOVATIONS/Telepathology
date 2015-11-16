package gov.va.med.imaging;

/**
 * @author beckey
 * created: Jan 7, 2005 at 12:01:55 PM
 *
 * This class consists of a number of static helper functions to convert between
 * byte array and native representations.
 * 
 */
public class ByteArrayConversion
{
	private ByteArrayConversion(){}
	
	/**
	 * Convert a long value to a byte array.
	 * @param value
	 * @return a byte array with the same bits as the given long
	 */
	public static byte[] longToByteArray(long value)		// a 64 bit (8 byte) integer value
	{
		byte[] retVal = new byte[8];
		
		//System.out.println("value [" + Long.toHexString(value) + "] ");
		// for each byte in the value
		for(int n=7; n >= 0; n--)
		{
			int shift = n * 8;
			long mask = 0xFFL << (n * 8);
			
			//System.out.print("shift [" + shift + "] ");
			//System.out.print("mask [" + Long.toHexString(mask) + "] ");
			//System.out.print("byte value of long[" + Long.toHexString(value & mask) + "] ");
			//System.out.print("byte value shifted[" + Long.toHexString((value & mask) >>> shift) + "] ");
			retVal[n] = (byte)((value & mask) >>> (shift));
			//System.out.println("retVal[" + n + "] = [" + retVal[n] + "]" );
		}
		
		return retVal;
	}
	
	/**
	 * Take a byte array of 8 members and convert it to a long
	 * 
	 * @param byteValue
	 * @return a long with the same bits as the given byte array
	 */
	public static long byteArrayToLong(byte[] byteValue)
	{
		if(byteValue.length != 8)
			throw new IllegalArgumentException("Byte array must be exactly 8 members");
		
		long retVal = 0L;
		
		for(int n=0; n < 8; ++n)
		{
			int shift = n * 8;
			long mask = 0xFFL << (n * 8);
			
			retVal += ( ((long)byteValue[n]) << shift ) & mask;
		}
		
		return retVal;
	}

	/**
	 * Convert an int to a 4 member byte array.
	 * @param value
	 * @return  a byte array with the same bits as the given int
	 */
	public static byte[] intToByteArray(int value)		// a 32 bit integer value
	{
		byte[] retVal = new byte[4];
		
		for(int n=3; n >= 0; n--)
		{
			int shift = n * 8;
			long mask = 0xFF << (n * 8);

			retVal[n] = (byte)((value & mask) >>> (shift));
		}
		return retVal;		
	}

	/**
	 * Convert a 4 member byte aray into an int.
	 * @param byteValue
	 * @return a int with the same bits as the given byte array
	 */
	public static int byteArrayToInt(byte[] byteValue)
	{
		if(byteValue.length != 4)
			throw new IllegalArgumentException("Byte array must be exactly 4 members");
		
		int retVal = 0;
		
		for(int n=0; n < 4; ++n)
		{
			int shift = n * 8;
			long mask = 0xFFL << (n * 8);
			
			retVal += ( ((long)byteValue[n]) << shift ) & mask;
		}
		
		return retVal;
	}

	/**
	 * Convert a short alue into a byte array representation.
	 * @param value
	 * @return  a byte array with the same bits as the given byte
	 */
	public static byte[] shortToByteArray(short value)	// a 16 bit integer value
	{
		byte[] retVal = new byte[2];
		
		for(int n=1; n >= 0; n--)
		{
			int shift = n * 8;
			long mask = 0xFF << (n * 8);

			retVal[n] = (byte)((value & mask) >>> (shift));
		}
		
		return retVal;		
	}

	/**
	 * Convert a byte array of 2 members into a short.
	 * @param byteValue
	 * @return a short with the same bits as the given byte array
	 */
	public static short byteArrayToShort(byte[] byteValue)
	{
		if(byteValue.length != 2)
			throw new IllegalArgumentException("Byte array must be exactly 2 members");
		
		short retVal = 0;
		
		for(int n=0; n < 2; ++n)
		{
			int shift = n * 8;
			long mask = 0xFFL << (n * 8);
			
			retVal += ( ((long)byteValue[n]) << shift ) & mask;
		}
		
		return retVal;
	}

	/**
	 * Convert a two-dimensional array into a one-dimensional array.
	 * Convertng back to a two dimensional array is possible only if the original
	 * array lengths are known.
	 * 
	 * @param discriminators
	 * @return a 1D array containing all members of the 2D array
	 */
	public static byte[] flattenTwoDimensionalByteArray(byte[][] discriminators)
	{
		int combinedLength = 0;
		for(int x=0; x < discriminators.length; ++x)
			combinedLength += discriminators[x].length; 
		
		byte[] retVal = new byte[combinedLength];

		int dimension2 = 0;	// major axis of the discriminator array
		for(int index=0; index < retVal.length;)
		{
			for(int dimension1 = 0; dimension1 < discriminators[dimension2].length; ++dimension1)
			{
				retVal[index] = discriminators[dimension2][dimension1];
				++index;
			}
			++dimension2;
		}
		return retVal;
	}

}
