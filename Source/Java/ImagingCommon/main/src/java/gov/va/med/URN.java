package gov.va.med;

import gov.va.med.imaging.exceptions.ImageURNFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.utility.Base32;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * An implementation of URN that is consistent with RFC 2141.
 * @see http://www.ietf.org/rfc/rfc2141.txt
 * 
 * NOTICE: Please read the comments on each of the constructors.  This class, and its derivatives
 * are created both by a static factory within this class and directly by static create methods or
 * "new" instantiations.  The static factory requires a specific constructor and expects certain
 * behavior within that constructor.
 * 
 * This class is the superclass of the "opaque" identifiers which are presented to the outside world (the DOD).
 * This class is responsible for parsing and building the generic URN portion of a imaging URN.
 * Subclasses (Image and Study) provide the Namespace-Specific-String (NSS) parsing and interpretation.
 * This class does contain helper methods (Pattern instances, etc) where the NSS interpretation is similar
 * between subclasses, but this should not be construed as meaning that this class is responsible for the
 * semantics of the NSS.
 * 
 * The outside world must get only the String representation of this class (and its subclasses).  The toString()
 * method is overridden in this class in a way that should produce a proper, parsable URN representation for 
 * subclasses.
 * 
 * This class and all derivations implement an RFC2141 compliant URN.  The default behavior of all classes
 * is to follow RFC2141 the specification.  The internal storage of the URN components must be in complaint
 * format and the toString() and parsing functions must also operate in a RFC2141 compliant manner.
 * 
 *   =========================================================================================
 *   From RFC 2141 
 *   
 *   All URNs have the following syntax (phrases enclosed in quotes are REQUIRED):
 *   
 *   <URN> ::= "urn:" <NID> ":" <NSS>
 *   where <NID> is the Namespace Identifier, and <NSS> is the Namespace Specific String.  
 *   The leading "urn:" sequence is case-insensitive. The Namespace ID determines 
 *   the _syntactic_ interpretation of the Namespace Specific String (as discussed in [1]).
 *   
 *   NID ::= <let-num> [ 1,31<let-num-hyp> ]
 *   
 *   As required by RFC 2141, there is a single canonical representation of the NSS portion 
 *   of an URN.   The format of this single canonical form follows:
 *   <NSS>         ::= 1*<URN chars>
 *   <URN chars>   ::= <trans> | "%" <hex> <hex>
 *   <trans>       ::= <upper> | <lower> | <number> | <other> | <reserved>
 *   <hex>         ::= <number> | "A" | "B" | "C" | "D" | "E" | "F" | "a" | "b" | "c" | "d" | "e" | "f"
 *   <other>       ::= "(" | ")" | "+" | "," | "-" | "." | ":" | "=" | "@" | ";" | "$" | "_" | "!" | "*" | "'"
 *   
 * from RFC 2141
 * "Depending on the rules governing a namespace, valid identifiers in a 
 * namespace might contain characters that are not members of the URN
 * character set above (<URN chars>).  Such strings MUST be translated
 * into canonical NSS format before using them as protocol elements or
 * otherwise passing them on to other applications. Translation is done
 * by encoding each character outside the URN character set as a
 * sequence of one to six octets using UTF-8 encoding, and the
 * encoding of each of those octets as "%" followed by two characters
 * from the <hex> character set above. The two characters give the
 * hexadecimal representation of that octet."
 * 
 * NOTE: Implicitly this means that the % character is an allowable character
 * if it is followed by (2 to 8) hex digits.
 * 
 * BIG NOTE: There is a problem here if we ever needed to support characters outside
 * the ASCII characters set (i.e. beyond UTF-8 single octet encoding).  The problem is that
 * the regular expression can't determine where an encoded character ends if the length
 * of the octet sequence is not fixed.  For example "%20AC" could be decoded as a  either
 * " AC" (i.e an ASCII space followed by "AC") or as a euro symbol (represented by octet
 * sequence %20AC.)  For the foreseeable future this is not an issue, the regular 
 * expression assumes exactly 2 hex digits following the percent sign.
 * 
 *
 *   ============================================================================================================
 *   Differences from RFC-2141
 *   
 *   This implementation also allows up to 5 additional identifiers to be appended to the stringified representation
 *   of a URN.  This feature is required because the VIX needs study and patient identifiers with image URNs
 *   that come from the DoD.  The default stringification of a URN should not include these additional
 *   identifiers.
 *   
 *    Required Stringification Methods
 *    toString() - form a stringified form of the URN in RFC-2141 compliant form
 *    toStringAsVAInternal() - form a stringified representation with the additional identifiers appended
 *    toStringAsNative() - implementation dependent, form a stringified representation of the URN in a form
 *                         consistent with that of the source system, by default this is the same as toString()
 *    toStringAsBase32() - select portions of the namespace specific string may be encoded as Base32, classes overriding
 *                         this method MUST also provide a createFromBase32() static factory method
 *    
 *    For consistency in implementation, the internal representation of the namespace specific string is always
 *    the RFC2141 compliant form.  Derivations that override getNamespaceSpecificString() and parseNamespaceSpecificString
 *    store the components used to build the NSS individually. 
 *    
 *    Required Factory, Constructor and parsing methods
 *    
 */
public class URN
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected static final OctetSequenceEscaping RFC2141_ESCAPING;
	protected static final OctetSequenceEscaping FILENAME_ESCAPING;
	protected static final OctetSequenceEscaping FILENAME_TO_RFC2141_ESCAPING;
	protected static final OctetSequenceEscaping CDTP_ESCAPING;

	// a REGEX matching on a sequence of 1 or more base 32 legal characters
    public static final String BASE32_COMPONENT_REGEX = "[" + Base32.BASE32_CHARS + "]+";

	public static final String urnComponentDelimiter = ":";						// the delimiter of the URN components

	// a URI that starts with 'urn' and follows the URN syntax is a URN
	public static final String urnSchemaIdentifier = "urn";						// the scheme name that identifies a URI as a URN
	protected static final String urnSchemaIdentifierRegex = "[uU][rR][nN]";		// by URN definition, is case insensitive
	public static final Pattern urnSchemaIdentifierPattern = Pattern.compile(urnSchemaIdentifierRegex);
	
	// NID is <let-num> [ 1,31<let-num-hyp> ]
	protected static final String urnNamespaceIdentifierRegex = "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,31}";
	public static final Pattern urnNamespaceIdentifierPattern = Pattern.compile(urnNamespaceIdentifierRegex);

	// The character sets, regular expressions and patterns realizing the RFC 2141 NSS definition
	// of the namespace specific string.
	// NOTE: if the value of RFC2141_LEGAL_NSS_CHARSET changes then the value of RFC2141_LEGAL_NSS_CHARS
	// must be manually changed to match.
	protected static final String RFC2141_LEGAL_NSS_CHARSET = "a-zA-Z0-9\\(\\)\\+,\\-\\.:=@;$_!\\*'";
	protected static final String RFC2141_LEGAL_NSS_REGEX = "[" + RFC2141_LEGAL_NSS_CHARSET + "]*";
	public static final Pattern RFC2141_LEGAL_NSS_PATTERN = Pattern.compile(RFC2141_LEGAL_NSS_REGEX);
	protected static final String RFC2141_ILLEGAL_NSS_REGEX = "[^" + RFC2141_LEGAL_NSS_CHARSET + "]*";
	public static final Pattern RFC2141_ILLEGAL_NSS_PATTERN = Pattern.compile(RFC2141_ILLEGAL_NSS_REGEX);
	
	// The namespace specific string allows any character.  Portions of the NSS that are delimited with square
	// brackets will be parsed as additionalIdentifiers.
	// old stuff -- "[a-zA-Z0-9\\(\\)\\+,\\-\\.\\^:=@;$_!\\*'%[0-9a-fA-F]{2,2}]*";
	protected static final String namespaceSpecificStringRegex = "[^\\[]*";
	public static final Pattern namespaceSpecificStringPattern = Pattern.compile(namespaceSpecificStringRegex);
	
	protected static final String appendedIdentifierRegex = "[a-zA-Z0-9][a-zA-Z0-9\\(\\)\\+,\\-\\.:=@;$_!\\*'%]*";
	public static final Pattern appendedIdentifierPattern = Pattern.compile(appendedIdentifierRegex);
	
	// Within a namespace specific string the dash character is commonly used to delimit
	// identifier components.  Additional components, not part of the identifier
	// may be added if delimited by square brackets.
	public static final char namespaceSpecificStringDelimiter = '-';
	public static final String namespaceSpecificStringDelimiterRegex = "\\-";
	public static final char appendedIdentifierStartDelimiter = '[';
	public static final String appendedIdentifierStartDelimiterRegex = "\\[";
	public static final char appendedIdentifierEndDelimiter = ']';
	public static final String appendedIdentifierEndDelimiterRegex = "\\]";
	
	// Yeah, it is kinda' cheesy with the repeated group, anyone that wants to
	// figure out how to do repeating groups in regex matching, have at it.
	protected static final String appendedIdentifiersRegex =
		"(?:" + 
		"(?:" + appendedIdentifierStartDelimiterRegex + ")" +
		"(" + appendedIdentifierRegex + ")" + 
		"(?:" + appendedIdentifierEndDelimiterRegex + ")" +
		")?"+ 
		"(?:" + 
		"(?:" + appendedIdentifierStartDelimiterRegex + ")" +
		"(" + appendedIdentifierRegex + ")" + 
		"(?:" + appendedIdentifierEndDelimiterRegex + ")" +
		")?"+ 
		"(?:" + 
		"(?:" + appendedIdentifierStartDelimiterRegex + ")" +
		"(" + appendedIdentifierRegex + ")" + 
		"(?:" + appendedIdentifierEndDelimiterRegex + ")" +
		")?"+ 
		"(?:" + 
		"(?:" + appendedIdentifierStartDelimiterRegex + ")" +
		"(" + appendedIdentifierRegex + ")" + 
		"(?:" + appendedIdentifierEndDelimiterRegex + ")" +
		")?"+ 
		"(?:" + 
		"(?:" + appendedIdentifierStartDelimiterRegex + ")" +
		"(" + appendedIdentifierRegex + ")" + 
		"(?:" + appendedIdentifierEndDelimiterRegex + ")" +
		")?";
	public static final Pattern appendedIdentifiersPattern = Pattern.compile(appendedIdentifiersRegex);
	
	// the stringified form of the NSS portion with additional identifiers,
	// this is used by some derived classes to parse NSS strings built internally
	public final static String extendedNamespaceSpecificStringRegex = 
		"(" + namespaceSpecificStringRegex + ")" +
		appendedIdentifiersRegex;
	
	public final static int urnXnssComponentGroup = 1;		// the namespace specific string i.e. a VA image identifier
	public final static int urnXnssAdditionalIdentifierComponentGroup1 = 2;	// the first of five of additional identifiers
	public final static int urnXnssAdditionalIdentifierComponentGroup2 = 3;	// the second of five of additional identifiers
	public final static int urnXnssAdditionalIdentifierComponentGroup3 = 4;	// the third of five of additional identifiers
	public final static int urnXnssAdditionalIdentifierComponentGroup4 = 5;	// the fourth of five of additional identifiers
	public final static int urnXnssAdditionalIdentifierComponentGroup5 = 6;	// the fifth of five of additional identifiers
	public final static int URN_XNSS_MAX_ADDITIONAL_IDENTIFIER_GROUPS = 5;
	
	public final static Pattern EXTENDED_NSS_PATTERN = Pattern.compile(extendedNamespaceSpecificStringRegex);
	
	// e.g. of stringified form is:
	// urn:vaimage:111-222-333-444[55-666][7-8][999-aaa]
	public final static String urnRegex = 
		"(?:" + urnSchemaIdentifierRegex + ")" + urnComponentDelimiter + 
		"(" + urnNamespaceIdentifierRegex + ")" + urnComponentDelimiter +
		extendedNamespaceSpecificStringRegex;
	public final static Pattern urnPattern = Pattern.compile(urnRegex);

	// once a string has been parsed the Matcher makes the component pieces
	// available as groups with the following indexes.
	// Derived classes may additionally parse the namespace specific string, that
	// functionality is not addressed in this class.
	public final static int urnNamespaceComponentGroup = 1;	// the namespace identifier e.g. "vaimage"
	public final static int urnNssComponentGroup = 1 + urnXnssComponentGroup;		// the namespace specific string i.e. a VA image identifier
	public final static int urnAdditionalIdentifierComponentGroup1 = 1 + urnXnssAdditionalIdentifierComponentGroup1;	// the first of N number of additional identifiers
	public final static int urnAdditionalIdentifierComponentGroup2 = 1 + urnXnssAdditionalIdentifierComponentGroup2;	// the first of N number of additional identifiers
	public final static int urnAdditionalIdentifierComponentGroup3 = 1 + urnXnssAdditionalIdentifierComponentGroup3;	// the first of N number of additional identifiers
	public final static int urnAdditionalIdentifierComponentGroup4 = 1 + urnXnssAdditionalIdentifierComponentGroup4;	// the first of N number of additional identifiers
	public final static int urnAdditionalIdentifierComponentGroup5 = 1 + urnXnssAdditionalIdentifierComponentGroup5;	// the first of N number of additional identifiers

	static
	{
		Logger.getLogger(URN.class).info("urnNamespaceIdentifierRegex = '" + urnNamespaceIdentifierRegex + "'");
		Logger.getLogger(URN.class).info("namespaceSpecificStringRegex = '" + namespaceSpecificStringRegex + "'");
		Logger.getLogger(URN.class).info("extendedNamespaceSpecificStringRegex = '" + extendedNamespaceSpecificStringRegex + "'");
		Logger.getLogger(URN.class).info("urnRegex = '" + urnRegex + "'");

		RFC2141_ESCAPING = OctetSequenceEscaping.createRFC2141EscapeEngine();
		FILENAME_ESCAPING = OctetSequenceEscaping.createFilenameLegalEscapeEngine();
		// Finally, create an escaping engine for those characters that are NOT filename
		// legal but are RFC2141 legal.  This will be used to convert NSS that have had filename
		// escaping applied to them back to RFC2141 safe (i.e. the internal) form.
		FILENAME_TO_RFC2141_ESCAPING = OctetSequenceEscaping.createRFC2141toFilenameLegalEscapeEngine();
		CDTP_ESCAPING = OctetSequenceEscaping.createCDTPEscapeEngine();
	}
	
	/**
	 * Simply returns true if the given string is the URN schema identifier,
	 * that is 'urn' as a non-case sensitive match.
	 * 
	 * @param schema
	 */
	public static final boolean isUrnSchemaIdentifier(String schema)
	{
		return Pattern.matches(urnSchemaIdentifierRegex, schema);
	}
	
	// ==============================================================================================
	// Instance Members
	// ==============================================================================================
	private NamespaceIdentifier namespaceIdentifier;
	private String namespaceSpecificString;
	private String[] additionalIdentifiers;
	private transient Logger logger = null;
	
	/**
	 * This constructor is intended for use by a child class when the
	 * child class is being created with the namespace specific string
	 * in its constituent parts. 
	 * e.g. new ImageURN(siteId, patientId, instanceId, modality);
	 * 
	 * public ImageURN(String siteId, String patientId, String instanceId, String modality)
	 * throws URNFormatException
	 * {
	 * 		super( new NamespaceIdentifier("vaimage") );
	 *		this.siteId = siteId;
	 *      ...
	 * }
	 * 
	 * Derived classes may not use the namespace specific string if
	 * this constructor is used, as it is set to null and declared final.
	 * The namespace specific component must be stored in the derived class
	 * and the getNamespaceSpecificString() method must be overridden.
	 * 
	 * This constructor is NOT REQUIRED by the static factory.
	 * This constructor should not be overridden by classes that 
	 * are immutable and opaque (e.g. BHIE identifiers).
	 * 
	 * @param namespaceIdentifier
	 * @param namespaceSpecificString
	 * @throws ImageURNFormatException 
	 */
	protected URN(NamespaceIdentifier namespaceIdentifier) 
	throws URNFormatException
	{
		if(namespaceIdentifier == null || namespaceIdentifier.getNamespace() == null)
			throw new URNFormatException("Unable to create a URN with a null namespace identifier.");
		
		this.namespaceIdentifier = namespaceIdentifier;
		setNamespaceSpecificString(null);
		this.additionalIdentifiers = null;
	}
	
	/**
	 * Derived class that have an unparsed namespace specific string, optionally, additional identifiers
	 * can use the storage within this class for their member property storage.
	 * 
	 * @param namespaceIdentifier
	 * @param namespaceSpecificString
	 * @param additionalIdentifiers
	 * @throws URNFormatException
	 */
	protected URN(
		NamespaceIdentifier namespaceIdentifier, 
		String namespaceSpecificString, 
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		if(namespaceIdentifier == null || namespaceIdentifier.getNamespace() == null)
			throw new URNFormatException("Unable to create a URN with a null namespace identifier.");
		
		this.namespaceIdentifier = namespaceIdentifier;
		parseNamespaceSpecificString(namespaceIdentifier, namespaceSpecificString, SERIALIZATION_FORMAT.NATIVE);
		this.additionalIdentifiers = additionalIdentifiers;
	}
	
	/**
	 * This constructor is used by the static factory methods within this class to
	 * create an instance of this class after parsing
	 * a string into its URN components.
	 * This constructor should be overridden by derived classes if
	 * the derived class is parsing the namespace specific string and is NOT
	 * using this classes namespaceSpecificString field for storage.
	 * 
	 * e.g. ImageURN imageUrn = (ImageURN)URN.create("urn:vaimage:66677-7766-8877-9988");
	 * 
	 * protected URN(URNComponents urnComponents)
	 * throws URNFormatException
	 * {
	 * 		super( urnComponents );
	 * 		parseNamespaceSpecificString(urnComponents.getNamespaceSpecificString());
	 * }
	 * 
	 * 
	 * This constructor is REQUIRED by the static factory.
	 * Derived classes overriding this constructor MUST also override
	 * the getNamespaceSpecificString() method if they do not call this
	 * constructor (i.e. super(URNComponents).
	 * 
	 * @param urnComponents
	 */
	protected URN(URNComponents urnComponents, SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		if(urnComponents.getNamespaceIdentifier() == null || urnComponents.getNamespaceIdentifier().getNamespace() == null)
			throw new URNFormatException("Unable to create a URN with a null namespace identifier.");
		
		this.namespaceIdentifier = urnComponents.getNamespaceIdentifier();
		// set the additional identifiers before parsing the namespace specific
		// string because some derived classes may use the values in the additional
		// identifiers in the parsing
		this.additionalIdentifiers = deserializeAdditionalIdentifiers(urnComponents.getAdditionalIdentifers(), serializationFormat);
		parseNamespaceSpecificString(urnComponents.getNamespaceIdentifier(), urnComponents.getNamespaceSpecificString(), serializationFormat );
	}

	/**
	 * The "do-nothing" default implementation.  Derived classes may override to deserialize the additional
	 * identifiers.
	 * This method is called immediately before parseNamespaceSpecificString().
	 * 
	 * @param additionalIdentifers
	 * @param serializationFormat
	 * @return
	 */
	protected String[] deserializeAdditionalIdentifiers(
		String[] additionalIdentifiers, 
		SERIALIZATION_FORMAT serializationFormat)
	{
		return additionalIdentifiers;
	}

	/**
	 * Return the namespace identifier that this URN was created as.
	 * Note that namespace identifier is a final field and that
	 * it is required in all constructors.
	 * 
	 * @return
	 */
	public NamespaceIdentifier getNamespaceIdentifier()
	{
		return namespaceIdentifier;
	}

	/**
	 * Return the additional identifiers as an array of String
	 * instances.
	 * 
	 * @return
	 */
	public String[] getAdditionalIdentifiers()
	{
		return this.additionalIdentifiers;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public String getAdditionalIdentifier(int index)
	{
		return 
			this.additionalIdentifiers == null || this.additionalIdentifiers.length < index ?
				null :
				this.additionalIdentifiers[index];
	}

	protected void setAdditionalIdentifiers(String[] additionalIdentifiers)
	{
		// add the additional individually and in-order so that the 
		// processing of their values in the set is executed
		for(int index=0; index < additionalIdentifiers.length; ++index)
			setAdditionalIdentifier(index, additionalIdentifiers[index]);
	}

	/**
	 * 
	 * @param index
	 * @param additionalIdentifier
	 */
	protected void setAdditionalIdentifier(int index, String additionalIdentifier)
	{
		additionalIdentifier = URN.RFC2141_ESCAPING.escapeIllegalCharacters(additionalIdentifier);
		
		if( this.additionalIdentifiers == null )
		{
			this.additionalIdentifiers = new String[index+1];
			this.additionalIdentifiers[index] = additionalIdentifier;
		}
		else if( index >= this.additionalIdentifiers.length )
		{
			String[] temp = new String[index + 1];
			System.arraycopy(this.additionalIdentifiers, 0, temp, 0, this.additionalIdentifiers.length);
			temp[index] = additionalIdentifier;
			this.additionalIdentifiers = temp;
		}
		else
			this.additionalIdentifiers[index] = additionalIdentifier;
	}

	/**
	 * Return the additional identifiers as a square-bracket delimited
	 * concatenated String.
	 * 
	 * @return
	 */
	public final String getAdditionalIdentifiersString()
	{
		return getAdditionalIdentifiersString(SERIALIZATION_FORMAT.NATIVE);
	}
	
	public final String getAdditionalIdentifiersString(SERIALIZATION_FORMAT serializationFormat)
	{
		StringBuilder ahnold = new StringBuilder();
		
		if(getAdditionalIdentifiers() != null)
		{
			int lastUsedIndex = 0;
			for(int index=0; index < getAdditionalIdentifiers().length; ++index)
			{
				String additionalIdentifier = getAdditionalIdentifier(index);
				if(additionalIdentifier != null)
				{
					// put the blanks in, if any, from the last used index
					for(int blankIndex=lastUsedIndex; blankIndex < index-1; ++blankIndex)
						ahnold.append("[]");
					ahnold.append(URN.appendedIdentifierStartDelimiter);
					ahnold.append( SERIALIZATION_FORMAT.serialize(additionalIdentifier, serializationFormat) );
					ahnold.append(URN.appendedIdentifierEndDelimiter);
					lastUsedIndex = index;
				}
			}
		}
		
		return ahnold.toString();
	}
	
	/**
	 * Return the NSS portion of the URN.
	 * Derived classes that parse the namespace specific string
	 * MUST also override this method to build the namespace
	 * specific string from its parsed components.
	 * The namespace specific string should be the NSS portion of
	 * the RFC-2141 stringified form of the URN, it should not
	 * include any additional identifiers.
	 * The default implementation of toString() calls this method
	 * to build the NSS part of the RFC-2141 form.
	 * @return
	 */
	public final String getNamespaceSpecificString()
	{
		return getNamespaceSpecificString(SERIALIZATION_FORMAT.RFC2141);
	}
	
	public String getNamespaceSpecificString(SERIALIZATION_FORMAT serializationFormat)
	{
		return serializationFormat.serialize( RFC2141_ESCAPING.unescapeIllegalCharacters(this.namespaceSpecificString) );
	}
	
	public void setNamespaceSpecificString(String namespaceSpecificString)
	{
		this.namespaceSpecificString = RFC2141_ESCAPING.escapeIllegalCharacters(namespaceSpecificString);
	}
	
	/**
	 * By default the URN class doesn't really parse the NSS,
	 * it just stores it as a single String.
	 * Derived classes where the NSS is not opaque may override this
	 * method to store the individual components of the NSS.
	 * The NSS should not include the additional identifiers and this method
	 * will throw a URNException if it does.
	 * NOTE: The namespace specific string that is passed into this method
	 * does not have escape octet strings substituted for the illegal characters.
	 * It is up to the derived classes to call URN.escapeIllegalCharacters() if
	 * their may be illegal (according to RFC 2141) characters in the NSS.
	 * 
	 * @param serializationFormat - specifies the format that the namespace specific string is
	 * presented to this method as.
	 */
	public void parseNamespaceSpecificString(NamespaceIdentifier namespace, String namespaceSpecificString, SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		if(namespaceSpecificString == null)
			this.namespaceSpecificString = null;
		else
		{
			switch(serializationFormat)
			{
			case PATCH83_VFTP:
				setNamespaceSpecificString( Base32ConversionUtility.base32Decode(namespaceSpecificString) );
				break;
				
			case RFC2141:
				setNamespaceSpecificString( namespaceSpecificString );
				break;
				
			case CDTP:
				setNamespaceSpecificString(namespaceSpecificString);
				break;
				
			case VFTP:		// VFTP encodes the entire NSS in RFC2141 safe manner
			case RAW:		// RAW does not encode anything, but includes all identifiers
				String nss = serializationFormat.deserialize(namespaceSpecificString);
				Matcher nssMatcher = EXTENDED_NSS_PATTERN.matcher(nss);
				if(nssMatcher.matches())
				{
					setNamespaceSpecificString( nssMatcher.group(URN.urnXnssComponentGroup) );
					String additionalIdentifier = null;
					additionalIdentifier = nssMatcher.group(urnXnssAdditionalIdentifierComponentGroup1);
					if(additionalIdentifier != null && additionalIdentifier.length() > 0)
						setAdditionalIdentifier(0, additionalIdentifier);
					additionalIdentifier = nssMatcher.group(urnXnssAdditionalIdentifierComponentGroup2);
					if(additionalIdentifier != null && additionalIdentifier.length() > 0)
						setAdditionalIdentifier(1, additionalIdentifier);
					additionalIdentifier = nssMatcher.group(urnXnssAdditionalIdentifierComponentGroup3);
					if(additionalIdentifier != null && additionalIdentifier.length() > 0)
						setAdditionalIdentifier(2, additionalIdentifier);
					additionalIdentifier = nssMatcher.group(urnXnssAdditionalIdentifierComponentGroup4);
					if(additionalIdentifier != null && additionalIdentifier.length() > 0)
						setAdditionalIdentifier(3, additionalIdentifier);
					additionalIdentifier = nssMatcher.group(urnXnssAdditionalIdentifierComponentGroup5);
					if(additionalIdentifier != null && additionalIdentifier.length() > 0)
						setAdditionalIdentifier(4, additionalIdentifier);
				}
				else
					throw new URNFormatException(
						"The namespace specific string '" + namespaceSpecificString + 
						"' does not match the required format of '" + nssMatcher.pattern().toString() + "'."
					);
				break;
				
			case NATIVE:
			default:
				setNamespaceSpecificString( namespaceSpecificString );
				break;
			}
			
			if( !namespaceSpecificStringPattern.matcher(getNamespaceSpecificString()).matches() )
				throw new URNFormatException("The namespace specific string '" + namespaceSpecificString + "' is not valid.");
		}
	}
	
	/**
	 * A Logger instance that all of the URN derived classes may use.
	 * 
	 * @return
	 */
	protected synchronized Logger getLogger()
	{
		if(this.logger == null)
			this.logger = Logger.getLogger(URN.class);
		return this.logger;
	}
	
	@Override
	public String toString()
	{
		return toString(SERIALIZATION_FORMAT.RFC2141);
	}
	
	/**
	 * It is strongly encouraged that derived classes should use the
	 * toStringXXX methods defined in this class whenever possible.  Otherwise
	 * maintenance becomes a real headache in trying to figure which method
	 * is being used to serialize.
	 * 
	 * @param serializationFormat
	 * @return
	 */
	public final String toString(SERIALIZATION_FORMAT serializationFormat)
	{
		switch(serializationFormat)
		{
		case PATCH83_VFTP:
			return toStringPatch83VFTP();
		case CDTP:
			return toStringCDTP();
		case NATIVE:
			return toStringNative();
		case VFTP:
			return toStringVFTP();
		case RFC2141:
			return toStringRFC2141();
		case RAW:
			return toStringRaw();
		default:
			return toString();
		}
	}
	
	/**
	 * Serialize the namespace specific string and the additional identifiers, encode the entire
	 * NSS using RFC2141 compliant escaping.
	 * 
	 * @return
	 */
	protected String toStringVFTP()
	{
		OctetSequenceEscaping rfc2141Escaping = OctetSequenceEscaping.createRFC2141EscapeEngine();
		
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		StringBuilder sbNss = new StringBuilder();
		sbNss.append( this.getNamespaceSpecificString() );
		
		sbNss.append( URN.buildAdditionalIdentifiersString(SERIALIZATION_FORMAT.VFTP, getAdditionalIdentifiers()) );
		//if(getAdditionalIdentifiers() != null)
		//	for(String additionalIdentifier : getAdditionalIdentifiers())
		//	{
		//		sbNss.append(URN.appendedIdentifierStartDelimiter);
		//		sbNss.append(additionalIdentifier);
		//		sbNss.append(URN.appendedIdentifierEndDelimiter);
		//	}
		ahnold.append( rfc2141Escaping.escapeIllegalCharacters(sbNss.toString()) );
		return ahnold.toString();
	}

	/**
	 * 
	 * @return
	 */
	protected String toStringRaw()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		ahnold.append( this.getNamespaceSpecificString(SERIALIZATION_FORMAT.RAW) );
		ahnold.append( URN.buildAdditionalIdentifiersString(SERIALIZATION_FORMAT.RAW, additionalIdentifiers) );
		//if(getAdditionalIdentifiers() != null)
			//for(String additionalIdentifier : getAdditionalIdentifiers())
			//{
			//	ahnold.append(URN.appendedIdentifierStartDelimiter);
			//	ahnold.append(additionalIdentifier);
			//	ahnold.append(URN.appendedIdentifierEndDelimiter);
			//}
		return ahnold.toString();
	}
	
	/**
	 * Build an RFC-2141 compliant, stringified form of the URN. This form
	 * may include escaped octet sequences where the original string had characters that
	 * are illegal according to RFC 2141.
	 * 
	 * NOTE: this method may produce a value that is not equal to the original string
	 * passed to URNFactory to build the URN.
	 * 
	 * @see #toStringNative()
	 * @see #toStringCDTP()
	 */
	public String toStringRFC2141()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		ahnold.append( SERIALIZATION_FORMAT.RFC2141.serialize(this.getNamespaceSpecificString(SERIALIZATION_FORMAT.RFC2141)) );
		
		return ahnold.toString();
	}
	
	/**
	 * @return
	 */
	protected String toStringPatch83VFTP()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		// restore any RFC2141 illegal characters and then Base32 encode the NSS
		ahnold.append( this.getNamespaceSpecificString(SERIALIZATION_FORMAT.PATCH83_VFTP) );
		
		return ahnold.toString();
	}

	/**
	 * This method is required to generate a String that is equal to the original
	 * value used to create the URN.
	 * 
	 * This method returns a stringified view of the URN with octet sequences in the NSS 
	 * (and in the form %xx) representing characters that are not legal RFC 2141 characters 
	 * converted to the equivalent character using the UTF-8 character set.
	 * 
	 * By default this is equivalent to toStringUnescaped().  Derived classes must override
	 * this method when the toStringToUnescaped() method does not return the original value because
	 * of internal encoding, additional identifiers or for any other reason.
	 *
	 * NOTE: The result of this method may not be a legal RFC 2141 representation of a URN.
	 * NOTE: The URNFactory recognizes and encodes illegal characters.
	 * NOTE: Additional identifiers are not included.  
	 * 
	 * @see #toString()
	 * @see #toStringCDTP()
	 * @return
	 */
	protected String toStringNative()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		// restore any RFC2141 illegal characters
		ahnold.append( RFC2141_ESCAPING.unescapeIllegalCharacters(this.getNamespaceSpecificString(SERIALIZATION_FORMAT.NATIVE)) );
		
		return ahnold.toString();
	}
	
	/**
	 * The VA internal form of a stringified URN includes any additional
	 * data such that the identifiers can be parsed from the String.  For 
	 * example this includes the Study and Patient identifiers in an ImageURN
	 * and the Patient ID in a StudyURN.
	 * 
	 * Using this implementation of toStringAsVAInternal, any URN derivation that 
	 * returns a non-null value from getAdditionalIdentifiers() will have those values
	 * tacked onto the resulting string delimited by square brackets.  BHIE derivations of 
	 * ImageURN and StudyURN encode the additional information using this method.
	 * It is suggested that this encoding always be used because the URNFactory recognizes it.
	 * 
	 * The VAInternal stringified form is not required to include an RFC2141 compliant
	 * namespace specific string.  The schema must still be "urn" and the namespace
	 * identifier must be valid according to RFC 2141.
	 * 
	 * NOTE: The result of this method may not be a legal RFC 2141 representation of a URN.
	 * NOTE: The URNFactory recognizes and encodes illegal characters.
	 * NOTE: Additional identifiers are included.
	 * NOTE: The NSS portion in the resulting String is guaranteed to consist of only legal filename
	 * characters.
	 * 
	 * @see #FILENAME_SAFE_CHARS
	 * @return
	 */
	public String toStringCDTP()
	{
		StringBuilder ahnold = new StringBuilder();
		
		// build the scheme identifier
		ahnold.append(urnSchemaIdentifier);
		ahnold.append(urnComponentDelimiter);

		// build the namespace identifier
		ahnold.append(this.getNamespaceIdentifier());
		ahnold.append(urnComponentDelimiter);
		
		String nss = this.getNamespaceSpecificString();
		// escape any filename illegal characters
		nss = FILENAME_ESCAPING.escapeIllegalCharacters(nss);
		ahnold.append(nss);
		
		// !!! JULIAN, READ THIS !!!!!!
		// you could probably replace the next three lines with this one: 
		// ahnold.append( URN.buildAdditionalIdentifiersString(SERIALIZATION_FORMAT.CDTP, additionalIdentifiers) );
		// but I figured this wasn't the kind of thing you wanted changed in the last couple of weeks
		// before I left the project.
		String additionalIdentifiers = this.getAdditionalIdentifiersString();
		// escape any filename illegal characters
		additionalIdentifiers = FILENAME_ESCAPING.escapeIllegalCharacters(additionalIdentifiers);
		ahnold.append(additionalIdentifiers);
		
		return ahnold.toString();
	}

	/**
	 * 
	 * @param serializationFormat
	 * @param additionalIdentifiers
	 * @return
	 */
	static public String buildAdditionalIdentifiersString(SERIALIZATION_FORMAT serializationFormat, String... additionalIdentifiers)
	{
		if(additionalIdentifiers == null)
			return "";
		
		StringBuilder stringifiedIdentifiers = new StringBuilder();
		
		int preceedingIdentifiers = 0;
		for(String additionalIdentifier : additionalIdentifiers)
		{
			// don't write blank unless they are needed for fill
			// note that if the caller sends a zero-length string then that will get written
			if(additionalIdentifier == null)
			{
				++preceedingIdentifiers;
				continue;
			}

			// write any blank additional identifiers needed as filler
			for(; preceedingIdentifiers > 0; preceedingIdentifiers--)
				stringifiedIdentifiers.append("" + URN.appendedIdentifierStartDelimiter + URN.appendedIdentifierEndDelimiter);
			
			stringifiedIdentifiers.append(URN.appendedIdentifierStartDelimiter);
			stringifiedIdentifiers.append( additionalIdentifier == null ? "" : serializationFormat.serialize(additionalIdentifier) );
			stringifiedIdentifiers.append(URN.appendedIdentifierEndDelimiter);
		}
		
		return stringifiedIdentifiers.toString();
	}
	
	/**
	 * Create a URI that represents this URN in its default form,
	 * that is without additional identifiers.
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	public URI toURI() 
	throws URISyntaxException
	{
		return new URI(this.toString());
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.getAdditionalIdentifiers());
		result = prime * result + ((this.getNamespaceIdentifier() == null) ? 0 : this.getNamespaceIdentifier().hashCode());
		result = prime * result
			+ ((this.getNamespaceSpecificString() == null) ? 0 : this.getNamespaceSpecificString().hashCode());
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
		URN other = (URN) obj;
		if (!Arrays.equals(this.getAdditionalIdentifiers(), other.getAdditionalIdentifiers()))
			return false;
		if (this.getNamespaceIdentifier() == null)
		{
			if (other.getNamespaceIdentifier() != null)
				return false;
		}
		else if (!this.getNamespaceIdentifier().equals(other.getNamespaceIdentifier()))
			return false;
		if (this.getNamespaceSpecificString() == null)
		{
			if (other.getNamespaceSpecificString() != null)
				return false;
		}
		else if (!this.getNamespaceSpecificString().equals(other.getNamespaceSpecificString()))
			return false;
		return true;
	}
}
