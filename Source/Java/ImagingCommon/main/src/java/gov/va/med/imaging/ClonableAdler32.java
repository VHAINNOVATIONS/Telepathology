package gov.va.med.imaging;

import java.util.zip.Checksum;

/**
 * @author beckey
 * created: May 9, 2005 at 4:09:38 PM
 *
 * Algorithm explanation is at: http://www.pbcrypto.com/view.php?algorithm=adler32
 * The following text was copied from there.
 * 
 * Adler-32 is composed of two sums accumulated per byte: s1 is the sum
 * of all bytes, s2 is the sum of all s1 values. Both sums are done
 * modulo 65521. s1 is initialised to 1, s2 to zero. The Adler-32 checksum
 * is stored as s2*65536 + s1 in most-significant-byte first (network) order. 
 * 
 * The algorithm is much faster than the CRC32 algorithm yet still provides 
 * an extremely low probability of undetected errors.
 * 
 * The modulo on unsigned long accumulators can be delayed for 5552 bytes,
 * so the modulo operation time is negligible. If the bytes are a, b, c,
 * the second sum is 3a + 2b + c + 3, and so is position and order sensitive,
 * unlike the first sum, which is just a checksum. That 65521 is prime is
 * important to avoid a possible large class of two-byte errors that leave
 * the check unchanged. (The Fletcher checksum uses 255, which is not prime
 * and which also makes the Fletcher check insensitive to single byte
 * changes 0-255).
 * 
 * The sum s1 is initialised to 1 instead of zero to make the length of the
 * sequence part of s2, so that the length does not have to be checked
 * separately. (Any sequence of zeroes has a Fletcher checksum of zero.)
 */
public class ClonableAdler32
		implements Cloneable, Checksum
{
	private static final int BIGPRIME = 65521;
	private int s1;
	private int s2;
	
	public ClonableAdler32()
	{
		reset();
	}
	
	/**
	 * The Adler-32 checksum is stored as s2*65536 + s1 in most-significant-byte first (network) order. 
	 */
	public long getValue()
	{
		return ((long)s2 << 16) + s1;
	}
	
	public void reset()
	{
		s1 = 1;
		s2 = 0;
	}
	
	/**
	 * Adler-32 is composed of two sums accumulated per byte: s1 is the sum
	 * of all bytes, s2 is the sum of all s1 values. Both sums are done
	 * modulo 65521.
	 */
	public void update(int b)
	{
		s1 = (s1 + (b & 0xFF)) % BIGPRIME;
		
		s2 = (s2 + s1) % BIGPRIME;
	}
	
	/**
	 * 
	 * @param b
	 */
	public void update(byte[] b)
	{
		update(b, 0, b.length);
	}
	
	/**
	 * 
	 */
	public void update(byte[] b, int off, int len)
	{
		for(int n=off; n < off+len; ++n)
			update((int)b[n]);
	}

	/**
	 * 
	 */
	public Object clone() 
	throws CloneNotSupportedException
	{
		ClonableAdler32 that = new ClonableAdler32();
		that.s1 = this.s1;
		that.s2 = this.s2;
		
		return that;
	}
	
	/**
	 * Return True if the current values of s1 and s2 are equal.
	 */
	public boolean equals(Object obj)
	{
		try
		{
			ClonableAdler32 that = (ClonableAdler32)obj;
			return (this.s1 == that.s1 && this.s2 == that.s2);
		}
		catch(ClassCastException ccX)
		{
			return false;
		}
	}
}
