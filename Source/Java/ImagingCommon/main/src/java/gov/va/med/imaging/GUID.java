/*
 * Originally GUID.java 
 * created on Oct 28, 2004 @ 4:42:21 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging;


import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Oct 28, 2004 4:42:21 PM
 *<p>
 * Generate GUID based on:<br/>
 * <li>host ID</li>
 * <li>Process ID</li>
 * <li>Time</li>
 * <li>Sequence</li>
 * </p>
 * <p>The host ID is based on one of: MAC address, IP address, or a 
 * random number.  Only the MAC address can be considered reliable. </p>
 * <p>The process identifier is the hashCode of a new object, which is
 * unique on a single machine because it is based on the physical address of 
 * the object instance.</p>
 * <p>The time is the time in milliseconds, according to the system clock.</p>
 * <p>The sequence is a cyclically incrementing value starting with a random number
 * when the class is loaded.  The sequence is reset to the minimum value for an int,
 * only when it reaches the maximum value for a positive int.  The sequence 
 * discriminator is not reset for each unit of time discrimination.</p>
 * <p>The relationship between sequence and time is that the Thread on which the
 * constructor is running will sleep for one millisecond when the sequence cycles
 * to the minimum value and the time has not changes since the last GUID generation.  
 * This will lock all GUID generation for that millisecond
 * to assure that there is no possibility that 2^32 GUIDS generated could be 
 * generated in one millisecond and thereby result in duplicates.</p>
 * <p>Each of the above (host, process, time and sequence) is referred to as a 
 * discriminator (as in discriminating on the basis of time, host, process, or 
 * sequence).</p>
 * <p>The GUID can be expressed in three forms.  The first is the internal format,
 * accessible through the package-level accessor methods for each discriminator.  
 * The second format is a String-ified version of the discriminators and expressed in dot
 * seperated base16 notation.  The final version is a base-64 concatenation of the
 * discriminators.  The character set used for base64 representation is a 
 * function of the TranslationMap class.  The base64 representation is always
 * a 32 character String.  
 * The useLongFormat boolean determines whether the Stringified version of
 * the GUID uses base64 or base16 encoding.  In base64 (short format) mode
 * the GUIDs are case-sensitive 32 byte ASCII strings using characters
 * 0..9, a..z, A..Z, #, and &.  This format is not recommended because 
 * database settings can affect their apparent uniqueness because of the case 
 * sensitivity.
 * In base16 (long format) the GUIDs are case-insensitive 51 byte ASCII 
 * strings using standard hex numbers, that is 0..9,a..f.  This is the 
 * recommended format for GUIDs stored in a database because it is not
 * case sensitive.  The long format includes seperators (dots) between
 * the discriminators making them somewhat human readable.</p>
 * <p>The discriminators making up the GUID should not generally be relied on
 * to maintain the semantics of their origin.  The intent of this class is
 * to provide a unique ID and not to provide any location information.</p>
 * <p>This class always maintains the representation in its internal
 * format, but supports translation to the base-64 String-ified form through 
 * the toString() method and a constructor expecting a String-ified GUID.</p>
 * Incidentally, this class will generate globally unique IDs until the year 
 * 3400CE, after which we'll have to add a few more bits for the time discriminator.
 */
public class GUID implements Serializable
{
	public static final long serialVersionUID = 1;
	public static GUID nullGuid = null;
	public static final int LONG_STRING_REPRESENTATION_LENGTH = 52;
	public static final int BYTE_ARRAY_REPRESENTATION_LENGTH = 25;
	
	private static long uniqueHostIdentifier = 0;
	private static int uniqueProcessIdentifier = 0;
	//private static long lastTime = 0;
	private static int lastSequence = (int)(Math.random() * Integer.MAX_VALUE);
	private static Object sequenceLock = new Object();
	private static String longFormatSeperator = ".";
	// The useLongFormat boolean determines whether the Stringified version of
	// the GUID uses base64 or base16 encoding.  In base64 (short format) mode
	// the GUIDs are case-sensitive 32 byte ASCII strings using characters
	// 0..9, a..z, A..Z, #, and &.  This format is not recommended because 
	// database settings can affect their apparent uniqueness because of the case 
	// sensitivity.
	// In base16 (long format) the GUIDs are case-insensitive 51 byte ASCII 
	// strings using standard hex numbers, that is 0..9,a..f.  This is the 
	// recommended format for GUIDs stored in a database because it is not
	// case sensitive.  The long format includes seperators (dots) between
	// the discriminators making them somewhat human readable.
	private static boolean useLongFormat = true;
	
	// the private class members that comprise the globally unique identifier
	private long hostDiscriminator;						// 6 significant bytes max
	private int processDiscriminator;					// 4 significant bytes max
	private long timeDiscriminator;						// 8 significant bytes max
	private int sequenceDiscriminator;				// 4 significant bytes max
	private byte checksum;										// 1 significant nibble (4 bits)
	
    private static final Logger logger = Logger.getLogger (GUID.class);

	/**
	 * The static initializer gets a unique identifier for the host and
	 * for the process.  
	 * The host ID is based on one of; the MAC address,
	 * the IP address or a random number.  Only the MAC address can be
	 * considered reliable.
	 * 
	 * The process identifier is the hashCode of a new object, which is
	 * unique on a single machine because it is based
	 * on the physical address of the object instance. 
	 * 
	 */
	static
	{
		String hostId = null;
		
		uniqueProcessIdentifier = (new Object()).hashCode();
		try
		{
			hostId = NetworkInfo.getMacAddress();
		}
		catch (IOException e)
		{
			try
			{
				hostId = InetAddress.getLocalHost().getHostAddress();
			}
			catch (UnknownHostException uhX)
			{
				long randomNumber = new Double(Math.random() * (double)0xFFFFFFFF).longValue();
				hostId = Long.toHexString(randomNumber); 
			}
		}
		
		// remove dots, dashes, slashes, colons, etc that may be in the UID or host discriminator
		logger.debug("GUID Generation - Raw hostID (should be a MAC Address) = [" + hostId + "]");
		hostId = hostId.replaceAll("[\u0020-\u002F\u003A-\u0040]", "");
		logger.debug("GUID Generation - hostID (edited) = [" + hostId + "]");
		uniqueHostIdentifier = Long.parseLong(hostId, 16);
		
		logger.debug("GUID Generation - uniqueHostIdentifier = [" + Long.toHexString(uniqueHostIdentifier) + "]");
		logger.debug("GUID Generation - uniqueProcessIdentifier = [" + Integer.toHexString(uniqueProcessIdentifier) + "]");
		
		nullGuid = new GUID(0L, 0, 0L, 0, (byte)0);
		logger.debug("nullGuid static instance created [" + nullGuid.toString() + "]");
	}
	
	/**
	 * Costruct a new globally unique identifier.
	 *
	 */
	public GUID()
	{
		this.hostDiscriminator = uniqueHostIdentifier;
		this.processDiscriminator = uniqueProcessIdentifier;
		synchronized(sequenceLock)
		{
			if(lastSequence == Integer.MAX_VALUE)
			{
				lastSequence = Integer.MIN_VALUE;
				try
				{Thread.sleep(1);}		// make sure that the time discriminator will be different
				catch (InterruptedException e)
				{ /* ignore the exception */ }
			}
			this.sequenceDiscriminator = ++lastSequence;
			this.timeDiscriminator = System.currentTimeMillis();
		}
		
		this.checksum = calculateChecksum();
	}

	/**
	 * @return
	 */
	private byte calculateChecksum()
	{
		Xor4 xor = new Xor4();
		
		xor.update(hostDiscriminator);
		xor.update(processDiscriminator);
		xor.update(sequenceDiscriminator);
		xor.update(timeDiscriminator);
		
		return xor.value;
	}
	
	/**
	 * Construct a GUID from an existing String-ified representation.  The String
	 * should have been created by a previous instance of GUID and must consist of 
	 * valid characters as defined by the TranslationMap class.  The String must
	 * be 32 characters long or this constructor will throw an IllegalArgumentException.
	 * 
	 * @param stringRepresentation
	 */
	public GUID(String stringRepresentation)
	{
		if(useLongFormat)
			decodeLongFormat(stringRepresentation);
		else
			decodeShortFormat(stringRepresentation);
	}
	
	/**
	 * Decode from a byte array assuming the following order:
	 * 
	 * long hostDiscriminator
	 * int processDiscriminator
	 * long timeDiscriminator
	 * int sequenceDiscriminator
	 * byte checksum
	 * 
	 * @param byteArrayRepresentation
	 */
	public GUID(byte[] byteArrayRepresentation)
	{
		if(byteArrayRepresentation.length != BYTE_ARRAY_REPRESENTATION_LENGTH)
			throw new IllegalArgumentException("The byte array representation of a GUID must be 25 bytes long");
		
		byte[] hostDiscByteArray = new byte[8];
		System.arraycopy(byteArrayRepresentation, 0, hostDiscByteArray, 0, 8);
		this.hostDiscriminator = ByteArrayConversion.byteArrayToLong(hostDiscByteArray);
		
		byte[] procDiscByteArray = new byte[4];
		System.arraycopy(byteArrayRepresentation, 8, procDiscByteArray, 0, 4);
		this.processDiscriminator = ByteArrayConversion.byteArrayToInt(procDiscByteArray);

		byte[] timeDiscByteArray = new byte[8];
		System.arraycopy(byteArrayRepresentation, 12, timeDiscByteArray, 0, 8);
		this.timeDiscriminator = ByteArrayConversion.byteArrayToLong(timeDiscByteArray);
		
		byte[] seqDiscByteArray = new byte[4];
		System.arraycopy(byteArrayRepresentation, 20, seqDiscByteArray, 0, 4);
		this.sequenceDiscriminator = ByteArrayConversion.byteArrayToInt(seqDiscByteArray);
		
		this.checksum = byteArrayRepresentation[24];
	}
	
	/**
	 * A private constructor to create the NULL GUID instance.
	 * Using this constructor should be approached with extreme caution.
	 * 
	 * @param hostDiscriminator
	 * @param processDiscriminator
	 * @param timeDiscriminator
	 * @param sequenceDiscriminator
	 * @param checksum
	 */
	private GUID(	
			long hostDiscriminator,
			int processDiscriminator,
			long timeDiscriminator, 
			int sequenceDiscriminator, 
			byte checksum )
	{
		this.hostDiscriminator = hostDiscriminator;
		this.processDiscriminator = processDiscriminator;
		this.timeDiscriminator = timeDiscriminator;
		this.sequenceDiscriminator = sequenceDiscriminator;
		this.checksum = checksum;
	}

	/**
	 * Get the value used as the host discriminator for this instance
	 * of the GUID.  This accessor is provided for debugging only as there
	 * is no guarantee that it will be preserved in future versions.
	 * 
	 * @return the host discriminator
	 */
	long getHostDiscriminator()
	{
		return hostDiscriminator;
	}
	
	/**
	 * Get the value used as the process  discriminator for this instance
	 * of the GUID.  This accessor is provided for debugging only as there
	 * is no guarantee that it will be preserved in future versions.
	 * 
	 * @return the process identifier discriminator
	 */
	int getProcessDiscriminator()
	{
		return processDiscriminator;
	}
	
	/**
	 * Get the value used as the sequence discriminator for this instance
	 * of the GUID.  This accessor is provided for debugging only as there
	 * is no guarantee that it will be preserved in future versions.
	 * 
	 * @return the sequence discriminator
	 */
	int getSequenceDiscriminator()
	{
		return sequenceDiscriminator;
	}
	
	/**
	 * Get the value used as the time discriminator for this instance
	 * of the GUID.  This accessor is provided for debugging only as there
	 * is no guarantee that it will be preserved in future versions.
	 * 
	 * @return the time discriminator
	 */
	long getTimeDiscriminator()
	{
		return timeDiscriminator;
	}

	/**
	 * Get the stored checksum value
	 * @return
	 */
	byte getChecksum()
	{
		return checksum;
	}
	
	/**
	 * Take a one dimensional byte array and encode it into
	 * a byte array where each byte has six significant bits.
	 * 
	 * For each three bytes, create a four byte representation where each
	 * of the four bytes has 6 significant bits.
	 * 
	 * Example:
	 * |       |       |      |
	 * 111111111111111111111111
	 * |     |     |     |    |
	 * 
	 * @param longForm
	 * @return
	 */
	private byte[] encodeToSixBitByteArray(byte[] byteArrayRepresentation)
	{
		byte[] buffy = new byte[(byteArrayRepresentation.length * 4) / 3 ];
		
		int outBufferIndex = 0;
		for(int index=0; index < byteArrayRepresentation.length; index += 3)
		{
			byte[] decodedBlock = new byte[3];
			decodedBlock[0] = byteArrayRepresentation[index];
			decodedBlock[1] = ( (index + 1 >= byteArrayRepresentation.length) ? 0x00 : byteArrayRepresentation[index + 1] );
			decodedBlock[2] = ( (index + 2 >= byteArrayRepresentation.length) ? 0x00 : byteArrayRepresentation[index + 2] );
			
			byte[] encodedBlock = new byte[4];
			
			// the leftmost 6 bits of the first input byte
			encodedBlock[0] = (byte)( (decodedBlock[0] & 0xFC) >>> 2 );		
			// the rightmost 2 bits of the first input byte and the leftmost 4 bits of the second
			encodedBlock[1] = (byte)( ((decodedBlock[0] & 0x03) << 4) + ((decodedBlock[1] & 0xF0) >>> 4));	
			// the rightmost 4 bits of the second input byte and the leftmost 2 bits of the third
			encodedBlock[2] = (byte)( ((decodedBlock[1] & 0x0F) << 2) + ((decodedBlock[2] & 0xC0) >>> 6) );
			// the rightmost 6 bits of the third input byte
			encodedBlock[3] = (byte)( (decodedBlock[2] & 0x3F) );
			
			buffy[outBufferIndex++] = encodedBlock[0];
			buffy[outBufferIndex++] = encodedBlock[1];
			buffy[outBufferIndex++] = encodedBlock[2];
			buffy[outBufferIndex++] = encodedBlock[3];
		}
		
		return buffy;
	}
	
	/**
	 * Take a byte array where each byte has six significant bits and
	 * compress it into a smaller array where each byte has 8 significant
	 * bits
	 * 
	 * @param sixBitByteArray
	 * @return
	 */
	private byte[] decodeFromSixBitByteArray(byte[] sixBitByteArray)
	{
		byte[] buffy = new byte[(sixBitByteArray.length * 3) / 4 ];
		
		int outBufferIndex = 0;
		for(int index=0; index < sixBitByteArray.length; index += 4)
		{
			byte[] encodedBlock = new byte[4];
			encodedBlock[0] = sixBitByteArray[index];
			encodedBlock[1] = ( (index + 1 >= sixBitByteArray.length) ? 0x00 : sixBitByteArray[index + 1] );
			encodedBlock[2] = ( (index + 2 >= sixBitByteArray.length) ? 0x00 : sixBitByteArray[index + 2] );
			encodedBlock[3] = ( (index + 3 >= sixBitByteArray.length) ? 0x00 : sixBitByteArray[index + 3] );
			
/*			System.out.print("Decoding block [" + 
				Integer.toHexString( (int)encodedBlock[0] ) + "," +
				Integer.toHexString( (int)encodedBlock[1] ) + "," +
				Integer.toHexString( (int)encodedBlock[2] ) + "," +
				Integer.toHexString( (int)encodedBlock[3] ) + "] into "
			);
*/			
			byte[] decodedBlock = new byte[3];
			
			// first decoded block is the significant bits of the first encoded byte plus two significant bits from the second
			decodedBlock[0] = (byte)( ((encodedBlock[0] & 0x3F) << 2) + ((encodedBlock[1] & 0x30) >>> 4) );
			// the second decoded block is the remaining 4 significant bits of the second byte plus the first 4 significant bits of the third
			decodedBlock[1] = (byte)( ((encodedBlock[1] & 0x0F) << 4) + ((encodedBlock[2] & 0x3C) >>> 2) );
			// third decoded block is last two bits of the third encoded plus first  
			decodedBlock[2] = (byte)( ((encodedBlock[2] & 0x03) << 6) + (encodedBlock[3] & 0x3F) );	

/*			System.out.println("[" + 
					Integer.toHexString( (int)decodedBlock[0] & 0xFF ) + "," +
					Integer.toHexString( (int)decodedBlock[1] & 0xFF ) + "," +
					Integer.toHexString( (int)decodedBlock[2] & 0xFF ) + "]"
			);
*/			
			
			buffy[outBufferIndex++] = decodedBlock[0];
			buffy[outBufferIndex++] = decodedBlock[1];
			buffy[outBufferIndex++] = decodedBlock[2];
		}
		
		return buffy;
	}
	
	/**
	 * Encode the GUID as a 32 character String using a base 64 encoding
	 * 
	 * @return
	 */
	private String encodeShortFormat()
	{
		byte[] encodedByteArray = encodeToSixBitByteArray(getOneDimensionalByteArrayRepresentation());
		StringBuffer buffy = new StringBuffer(encodedByteArray.length);
		for(int n=0; n < encodedByteArray.length; ++n)
			buffy.append( TranslationMap.translateByteToMappedChar(encodedByteArray[n]) );
		
		return buffy.toString();
	}
	
	/**
	 * Decode a base64 String represenation into the constituent parts
	 * 
	 * @param stringRepresentation
	 */
	private void decodeShortFormat(String stringRepresentation)
	{
		if(stringRepresentation.length() != 32)
			throw new IllegalArgumentException("GUID [" + stringRepresentation + "] string representation length must be 32 characters, and it ain't.");
		
		// first get a byte array using the character map
		byte[] sixBitByteArrayRepresentation = new byte[stringRepresentation.length()];
		//System.out.println("Decoding [" + stringRepresentation + "]");
		
		for(int n=0; n < stringRepresentation.length(); ++n)
		{
			sixBitByteArrayRepresentation[n] = TranslationMap.translateMappedCharToByte( stringRepresentation.charAt(n) );
			//System.out.print(Integer.toHexString((int)sixBitByteArrayRepresentation[n]) + " ");
		}
		//System.out.println("");
		// compress the 6 byte representations back up to 8
		byte[] discriminators = decodeFromSixBitByteArray(sixBitByteArrayRepresentation);
		
		// set this properties from the byte array
		setOneDimensionalByteArrayRepresentation(discriminators);
	}
	
	/**
	 * The GUID may be represented as a one dimensional array of bytes
	 * 
	 * @return
	 */
	private byte[] getOneDimensionalByteArrayRepresentation()
	{
		byte[][] discriminators = {
				ByteArrayConversion.longToByteArray(this.hostDiscriminator),
				ByteArrayConversion.intToByteArray(this.processDiscriminator),
				ByteArrayConversion.longToByteArray(this.timeDiscriminator),
				ByteArrayConversion.intToByteArray(this.sequenceDiscriminator)
		};

		return ByteArrayConversion.flattenTwoDimensionalByteArray(discriminators);
	}
	
	/**
	 * Set the internal value of the GUID from a one-dimensional byte
	 * array representation.
	 * 
	 * @param discriminators
	 */
	private void setOneDimensionalByteArrayRepresentation(byte[] discriminators)
	{
		byte[] hostAsByteArray = new byte[8];
		byte[] processAsByteArray = new byte[4];
		byte[] timeAsByteArray = new byte[8];
		byte[] sequenceAsByteArray = new byte[4];
		
		System.arraycopy(discriminators, 0, hostAsByteArray, 0, 8);
		System.arraycopy(discriminators, 8, processAsByteArray, 0, 4);
		System.arraycopy(discriminators, 12, timeAsByteArray, 0, 8);
		System.arraycopy(discriminators, 20, sequenceAsByteArray, 0, 4);
		
		hostDiscriminator = ByteArrayConversion.byteArrayToLong(hostAsByteArray);
		processDiscriminator = ByteArrayConversion.byteArrayToInt(processAsByteArray);
		timeDiscriminator = ByteArrayConversion.byteArrayToLong(timeAsByteArray);
		sequenceDiscriminator = ByteArrayConversion.byteArrayToInt(sequenceAsByteArray);
	}
	
	/**
	 * 
	 * @return
	 */
	private String encodeLongFormat()
	{
		StringBuffer buffy = new StringBuffer();
		
		buffy.append(zeroLeftFill(Long.toHexString(this.hostDiscriminator), 16));
		buffy.append(longFormatSeperator);
		
		buffy.append(zeroLeftFill(Integer.toHexString(this.processDiscriminator), 8));
		buffy.append(longFormatSeperator);
		
		buffy.append(zeroLeftFill(Long.toHexString(this.timeDiscriminator),16));
		buffy.append(longFormatSeperator);
		
		buffy.append(zeroLeftFill(Integer.toHexString(this.sequenceDiscriminator),8));
		buffy.append(Integer.toHexString(this.checksum));

		return buffy.toString();
	}
	
	private String zeroLeftFill(String discriminator, int length)
	{
		int fill = length - discriminator.length();
		char[] fillChars = new char[fill];
		for(int i=0; i < fill; ++i)
			fillChars[i] = '0';
		return new String(fillChars) + discriminator;
	}
	
	/**
	 * Given a String in the format:
	 * <host-discriminator>.<process-discriminator>.<time-discriminator>.<sequence-discriminator>
	 * where:
	 * <host-discriminator> - a hex representation of a long (max 64 bits, 16 hex chars)
	 * <process-discriminator> - a hex representation of a int (max 32 bits, 8 hex chars)
	 * <time-discriminator> - a hex representation of a long (max 64 bits, 16 hex chars)
	 * <sequence-discriminator> - a hex represenation of a int (max 32 bits, 8 hex chars)
	 * 
	 * @param longFormatRepresentation
	 */
	private void decodeLongFormat(String longFormatRepresentation)
	throws NumberFormatException
	{
		int endOfHost = longFormatRepresentation.indexOf(longFormatSeperator);
		this.hostDiscriminator = Long.parseLong(longFormatRepresentation.substring(0, endOfHost), 0x10 );
		
		int endOfProcess = longFormatRepresentation.indexOf(longFormatSeperator, endOfHost+1); 
		this.processDiscriminator = Integer.parseInt(longFormatRepresentation.substring(endOfHost+1, endOfProcess), 0x10 );

		int endOfTime = longFormatRepresentation.indexOf(longFormatSeperator, endOfProcess+1); 
		this.timeDiscriminator = Long.parseLong(longFormatRepresentation.substring(endOfProcess+1, endOfTime), 0x10 );
		
		this.sequenceDiscriminator = 
			Integer.parseInt(longFormatRepresentation.substring(endOfTime+1, 51), 0x10 );
		
		this.checksum = Byte.parseByte(longFormatRepresentation.substring(51), 0x10);
	}
	
	/**
	 * Return the GUID as a base-64 encoded String.  The return value from
	 * this method may be persisted and later used to recreate a GUID using
	 * the single String arg constructor. 
	 * 
	 * @see java.lang.Object#toString()
	 * @return a String representation that is unique, may be persisted, and may be used 
	 * to construct a instance later
	 */
	public String toString()
	{
		if(useLongFormat)
			return encodeLongFormat();
		else
			return encodeShortFormat();
	}
	
	/**
	 * Return the GUID in a dot-seperated String format where the discriminators
	 * are expressed as a hexadecimal values. 
	 * 
	 * @return the GUID in discrete discriminators with dot delimiters
	 */
	public String toLongString()
	{
		return encodeLongFormat();
	}

	public String toShortString()
	{
		return encodeShortFormat();
	}
	
	/**
	 * 
	 * Encode into a byte array in the following order:
	 * 
	 * long hostDiscriminator
	 * int processDiscriminator
	 * long timeDiscriminator
	 * int sequenceDiscriminator
	 * byte checksum
	 * 
	 * @return
	 */
	public byte[] byteArray()
	{
		byte[] byteArrayRepresentation = new byte[BYTE_ARRAY_REPRESENTATION_LENGTH];
		
		byte[] hostDiscByteArray = ByteArrayConversion.longToByteArray(this.hostDiscriminator);
		byte[] processDiscByteArray = ByteArrayConversion.intToByteArray(this.processDiscriminator);
		byte[] timeDiscByteArray = ByteArrayConversion.longToByteArray(this.timeDiscriminator);
		byte[] sequenceDiscByteArray = ByteArrayConversion.longToByteArray(this.sequenceDiscriminator);
		
		System.arraycopy(hostDiscByteArray, 0, byteArrayRepresentation, 0, 8);
		System.arraycopy(processDiscByteArray, 0, byteArrayRepresentation, 8, 4);
		System.arraycopy(timeDiscByteArray, 0, byteArrayRepresentation, 12, 8);
		System.arraycopy(sequenceDiscByteArray, 0, byteArrayRepresentation, 20, 4);
		byteArrayRepresentation[24] = this.checksum;
		
		return byteArrayRepresentation;
	}
	
	/**
	 * Return a cloned version of the GUID.
	 * 
	 * @return a copy of this
	 */
	protected Object clone() 
	throws CloneNotSupportedException
	{
		GUID clone = new GUID();
		
		clone.hostDiscriminator = this.hostDiscriminator;
		clone.processDiscriminator = this.processDiscriminator;
		clone.timeDiscriminator = this.timeDiscriminator;
		clone.sequenceDiscriminator = this.sequenceDiscriminator;
		
		return clone;
	}
	
	/**
	 * GUIDs are considered equal if all discriminiators are equal.
	 * A GUID created using the one-string arg constructor will always
	 * be equal to the GUID from which the string representation was 
	 * obtained.
	 * 
	 * @return true iff the given object is a GUID with the same 
	 * discriminator values
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof GUID)
		{
			GUID that = (GUID)obj;
			return this.hostDiscriminator == that.hostDiscriminator &&
				this.processDiscriminator == that.processDiscriminator &&
				this.timeDiscriminator == that.timeDiscriminator &&
				this.sequenceDiscriminator == that.sequenceDiscriminator;
		}
		
		return false;
	}
}
