package gov.va.med.imaging;

/*******************************************************************************
 * 
 * The integral types are byte, short, int, and long, whose values are 
 * 8-bit, 16-bit, 32-bit, and 64-bit signed two's-complement integers, 
 * respectively, and char, whose values are 16-bit unsigned integers 
 * representing Unicode characters
 * 
 ******************************************************************************/

public class Xor4
{
	byte value = 0;
	int length = 0;

	public Xor4()
	{
		super();
		value = 0;
	}

	public void reset()
	{
		value = 0;
		length = 0;
	}

	/**
	 * update with a long (64 bit)
	 * @param l
	 */
	public void update(long l)
	{
		update((int)( (l & 0xFFFFFFFF00000000L) >> 32 ));
		update((int)(  l & 0x00000000FFFFFFFFL ));
	}

	/**
	 * Update with an int (32 bit)
	 * @param i
	 */
	public void update(int i)
	{
		update((short)( (i & 0xFFFF0000) >> 16 ));
		update((short)( i & 0x0000FFFF ));
	}

	/**
	 * Update with a char (16 bit)
	 * @param c
	 */
	public void update(char c)
	{
		update((byte)( (c & 0xFF00) >> 8 ));
		update((byte)( c & 0x00FF ));
	}
	
	/**
	 * Update with a short (16 bit)
	 * @param c
	 */
	public void update(short c)
	{
		update((byte)( (c & 0xFF00) >> 8 ));
		update((byte)( c & 0x00FF ));
	}
	
	/**
	 * Update with a byte (8 bits)
	 * 
	 * @param b
	 */
	public void update(byte b)
	{
		value ^= (b & 0xF0) >> 4;
		value ^= b & 0x0F;
		length += 2;
	}

	/**
	 * The value is a char, with a value ranging from 0x0 to 0xF
	 * 
	 * @return
	 */
	public byte getValue()
	{
		return value;
	}
}

