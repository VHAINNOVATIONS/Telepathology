package gov.va.med;

import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;

/**
 * Controls the serialization type, used when calling
 * toString() and the toString() variations and when parsing
 * serialized representations of URNs.
 * i.	SERIALIZATION_FORMAT.RFC2141 indicates parsing where the string is in RFC2141 compliant form and stringification 
 *      into an RFC2141 compliant form
 * ii.	SERIALIZATION_FORMAT.VFTP indicates parsing where the string is in RFC2141 compliant form and stringification 
 *      into an RFC2141 compliant form.  VFTP escapes all of the characters in the NSS, RFC2141 encodes select portions
 *      depending on the URN implementation.
 * iii.	SERIALIZATION_FORMAT.PATCH83_VFTP is a format specific to Patch 83 federation.  The stringified URN must be in 
 *      a “vaXXX” namespace and the namespace specific string must be in base 32 form.
 * iv.	SERIALIZATION_FORMAT.CDTP is a format specific to clinical display and VistA Rad.  
 *      The stringified URN must be in “vaXXX” namespace and the namespace specific string must include nothing but filename safe 
 *      characters.  In addition, dashes in the namespace specific string must occur such that the first two tokens delimited 
 *      by the dashes provide a unique object identifier.
 * v.	SERIALIZATION_FORMAT.NATIVE means that the stringified format must match the original string from the source system.
 */
public enum SERIALIZATION_FORMAT
{
	RFC2141(false, false),		// RFC2141 illegal characters are escaped in an RFC2141 compliant format (i.e. %20 for space) 
	PATCH83_VFTP(false, true),	// Realization specific portions of the URN are base 32 encoded, 
	CDTP(false, true),			// Characters that are not legal in a file name are encoded using RFC2141 format, class specific formatting may also apply
	NATIVE(false, false),		// No encoding, additional identifiers are not included
	VFTP(true, false),			// VFTP after patch 83 includes all components in an RFC2141 compliant form, additional identifiers are not included
	RAW(false, true)			// No encoding, all components including additional identifiers are included
	;

	private final boolean nssAtomicallyEscaped;
	private final boolean reflective;
	
	/**
	 * If nssAtomicallyEscaped is true then the serialization is applied to the
	 * namespace specific string as a whole.  Otherwise each URN derivation must implement
	 * escaping/unescaping in the factory, constructors and toString(SERIALIZATION_FORMAT)
	 * methods. 
	 */
	SERIALIZATION_FORMAT(boolean nssAtomicallyEscaped, boolean reflective)
	{
		this.nssAtomicallyEscaped = nssAtomicallyEscaped;
		this.reflective = reflective;
	}
	
	/**
	 * @return the nssAtomicallyEscaped
	 */
	protected boolean isNssAtomicallyEscaped()
	{
		return this.nssAtomicallyEscaped;
	}

	/**
	 * If the SERIALIZATION_FORMAT is reflective then the result of a 
	 * toString(SERIALIZATION_FORMAT) passed to the URNFactory.parse(String)
	 * should produce a URN which .equals() the original.
	 * 
	 * @return the reflective
	 */
	public boolean isReflective()
	{
		return this.reflective;
	}

	/**
	 * Return TRUE if the NSS of a URN in this format is encoded in one operation
	 * and may be decoded in one operation.
	 * In particular this means that the components of the NSS are not parsable
	 * until the string is decoded.
	 * 
	 * @return
	 */
	public boolean isNSSAtomicallyEscaped()
	{
		return this.nssAtomicallyEscaped;
	}
	
	public String serialize(String value)
	{
		return SERIALIZATION_FORMAT.serialize(value, this);
	}
	
	public String deserialize(String value)
	{
		return SERIALIZATION_FORMAT.deserialize(value, this);
	}
	
	/**
	 * Applies encoding consistent with the given SERIALIZATION_FORMAT.
	 * Assumes that the source strings is in native format. 
	 * 
	 * @param additionalIdentifier
	 * @param serializationFormat
	 * @return
	 */
	public static String serialize(String value, SERIALIZATION_FORMAT serializationFormat)
	{
		if(value == null || value.length() == 0)
			return value;
		
		switch (serializationFormat)
		{
		case CDTP:
			return URN.CDTP_ESCAPING.escapeIllegalCharacters(value);
		case PATCH83_VFTP:
			return Base32ConversionUtility.base32Encode(value);
		case RFC2141:
		case VFTP:
			return URN.RFC2141_ESCAPING.escapeIllegalCharacters(value);
		case RAW:
		default:
			return value;
		}
	}

	/**
	 * 
	 * @param value
	 * @param serializationFormat
	 * @return
	 */
	public static String deserialize(String value, SERIALIZATION_FORMAT serializationFormat)
	{
		if(value == null || value.length() == 0)
			return value;
		
		switch (serializationFormat)
		{
		case CDTP:
			return URN.CDTP_ESCAPING.unescapeIllegalCharacters(value);
		case PATCH83_VFTP:
			return Base32ConversionUtility.base32Decode(value);
		case RFC2141:
		case VFTP:
			return URN.RFC2141_ESCAPING.unescapeIllegalCharacters(value);
		case RAW:
		default:
			return value;
		}
	}
}