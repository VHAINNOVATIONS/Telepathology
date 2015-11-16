/**
 * 
 */
package gov.va.med.imaging.channels;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.math.BigInteger;
import java.nio.CharBuffer;

import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class ChecksumValueTest extends TestCase
{

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testSerialization() 
	throws IOException, ChecksumFormatException, ClassNotFoundException
	{
		serializeDeserializeAndCompare( new ChecksumValue("Adler32", BigInteger.valueOf(1L)) );
		serializeDeserializeAndCompare( new ChecksumValue("Adler32", BigInteger.valueOf(655321L)) );
		serializeDeserializeAndCompare( new ChecksumValue("Adler32", BigInteger.valueOf(-99189729812L)) );
		serializeDeserializeAndCompare( new ChecksumValue("Adler32", BigInteger.valueOf(0L)) );
		serializeDeserializeAndCompare( new ChecksumValue("Adler32", BigInteger.valueOf(876873276L)) );
		
		serializeDeserializeAndCompare( new ChecksumValue("CRC32", BigInteger.valueOf(1L)) );
		serializeDeserializeAndCompare( new ChecksumValue("CRC32", BigInteger.valueOf(655321L)) );
		serializeDeserializeAndCompare( new ChecksumValue("CRC32", BigInteger.valueOf(-99189729812L)) );
		serializeDeserializeAndCompare( new ChecksumValue("CRC32", BigInteger.valueOf(0L)) );
		serializeDeserializeAndCompare( new ChecksumValue("CRC32", BigInteger.valueOf(876873276L)) );
	}

	/**
	 * @param cv
	 * @throws IOException 
	 * @throws ChecksumFormatException 
	 * @throws ClassNotFoundException 
	 */
	private void serializeDeserializeAndCompare(ChecksumValue cv) 
	throws IOException, ChecksumFormatException, ClassNotFoundException
	{
		ByteArrayOutputStream out;
		ByteArrayInputStream in;
		ObjectOutputStream objectOut;
		ObjectInputStream objectIn;
		ChecksumValue clonedCv;
		
		out = new ByteArrayOutputStream(256);
		objectOut = new ObjectOutputStream(out);
			
		objectOut.writeObject(cv);
		
		in = new ByteArrayInputStream(out.toByteArray());
		objectIn = new ObjectInputStream(in);
		clonedCv = (ChecksumValue)objectIn.readObject();

		objectOut.close();
		objectIn.close();
		
		System.out.println("Comparing '" + cv.toString() + "' to '" + clonedCv.toString() + "' through [" + new String(out.toByteArray()) + "].");
		assertEquals(cv, clonedCv);
	}
}
