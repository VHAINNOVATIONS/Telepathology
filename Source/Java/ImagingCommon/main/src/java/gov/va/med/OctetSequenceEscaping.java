/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jun 11, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author vhaiswbeckec
 *
 */
public class OctetSequenceEscaping
{
	// A generic octet sequence encoding, using the percent sign as the delimiter.
	// This works for URL and URN encoding.
	private static final String OCTET_SEQUENCE_SUFFIX = "[A-Fa-f0-9]{2,2}";
	protected static final String DEFAULT_OCTET_SEQUENCE_REGEX = "%" + OCTET_SEQUENCE_SUFFIX;
	public static final Pattern DEFAULT_OCTET_SEQUENCE_PATTERN = Pattern.compile(DEFAULT_OCTET_SEQUENCE_REGEX);

	public static final Character DEFAULT_ESCAPE_CHARACTER = new Character('%');
	
	public static final List<Character> RFC2141_LEGAL_NSS_CHARS; 
	public static final List<Character> FILENAME_SAFE_CHARS;
	public static final List<Character> FILEPATH_SAFE_CHARS;		// filename chars plus path delimiter
	public static final List<Character> FILENAME_TO_RFC2141_CHARS;
	public static final List<Character> CDTP_LEGAL_CHARS;
	
	static
	{
		// rather tortured way to get a char array of all legal RFC2141 characters (except the % sign)
		// without having to type them all out
		RFC2141_LEGAL_NSS_CHARS = new ArrayList<Character>();
		for(char c = 'a'; c <= 'z'; ++c)
			RFC2141_LEGAL_NSS_CHARS.add( new Character(c) );
		for(char c='A'; c <='Z'; ++c)
			RFC2141_LEGAL_NSS_CHARS.add( new Character(c) );
		for(char c='0'; c <= '9'; ++c)
			RFC2141_LEGAL_NSS_CHARS.add( new Character(c) );
		for(char c : new char[]{'(',')','+',',','-','.',':','=','@',';','$','_','!','*'} )
			RFC2141_LEGAL_NSS_CHARS.add( new Character(c) );
		
		// Windows kernel forbids the use of characters in range 1-31 (i.e., 0x01-0x1F) 
		// and characters " * : < > ? \ / |
		// This list stops at hex 7F, unicode characters above that range may be legal
		// but not for our use.
		FILENAME_SAFE_CHARS = new ArrayList<Character>();
		for(char c = 'a'; c <= 'z'; ++c)
			FILENAME_SAFE_CHARS.add( new Character(c) );
		for(char c='A'; c <= 'Z'; ++c)
			FILENAME_SAFE_CHARS.add( new Character(c) );
		for(char c='0'; c <= '9'; ++c)
			FILENAME_SAFE_CHARS.add( new Character(c) );
		for(char c : new char[]{'(',')','+',',','-','.','=','@',';','$','_','!','#','&','\'','[',']','^','{','}','~'} )
			FILENAME_SAFE_CHARS.add( new Character(c) );
		
		FILEPATH_SAFE_CHARS = new ArrayList<Character>();
		FILEPATH_SAFE_CHARS.addAll(FILENAME_SAFE_CHARS);
		FILEPATH_SAFE_CHARS.add(new Character('/'));
		
		// creates a List of all the characters in the RFC2141 legal list that are NOT
		// on the filename safe list.
		FILENAME_TO_RFC2141_CHARS = new ArrayList<Character>();
		for(Character ch : RFC2141_LEGAL_NSS_CHARS)
			if(! FILENAME_SAFE_CHARS.contains(ch))
				FILENAME_TO_RFC2141_CHARS.add(ch);

		// The list of CDTP safe characters is exactly the same as file name characters
		// except that CDTP does not allow: '-'
		CDTP_LEGAL_CHARS = new ArrayList<Character>();
		for(char c = 'a'; c <= 'z'; ++c)
			CDTP_LEGAL_CHARS.add( new Character(c) );
		for(char c='A'; c <= 'Z'; ++c)
			CDTP_LEGAL_CHARS.add( new Character(c) );
		for(char c='0'; c <= '9'; ++c)
			CDTP_LEGAL_CHARS.add( new Character(c) );
		for(char c : new char[]{'(',')','+',',','.','=','@',';','$','_','!','#','&','\'','[',']','^','{','}','~'} )
			CDTP_LEGAL_CHARS.add( new Character(c) );
	}
	
	/**
	 * Create an escaping engine for those characters that are RFC2141 legal.  
	 * 
	 * @return
	 */
	public static OctetSequenceEscaping createRFC2141EscapeEngine()
	{
		return OctetSequenceEscaping.createFromWhitelist(RFC2141_LEGAL_NSS_CHARS);
	}
	
	/**
	 * Create an escaping engine for those characters that are filename legal.  
	 * 
	 * @return
	 */
	public static OctetSequenceEscaping createFilenameLegalEscapeEngine()
	{
		return OctetSequenceEscaping.createFromWhitelist(FILENAME_SAFE_CHARS);
	}
	
	/**
	 * Create an escaping engine for those characters that are filename legal.  
	 * 
	 * @return
	 */
	public static OctetSequenceEscaping createFilepathLegalEscapeEngine()
	{
		return OctetSequenceEscaping.createFromWhitelist(FILEPATH_SAFE_CHARS);
	}
	
	/**
	 * Finally, create an escaping engine for those characters that are NOT filename
	 * legal but are RFC2141 legal.  This will be used to convert NSS that have had filename
	 * escaping applied to them back to RFC2141 safe (i.e. the internal) form.
	 * 
	 * @return
	 */
	public static OctetSequenceEscaping createRFC2141toFilenameLegalEscapeEngine()
	{
		return OctetSequenceEscaping.createFromBlacklist(FILENAME_TO_RFC2141_CHARS);
	}

	/**
	 * @return
	 */
	public static OctetSequenceEscaping createCDTPEscapeEngine()
	{
		return OctetSequenceEscaping.createFromWhitelist(CDTP_LEGAL_CHARS);
	}
	
	/**
	 * Create an instance where any characters except those on the whitelist will be encoded
	 * as UTF-8 sequences delimited by the percent sign.
	 * 
	 * @param whitelist
	 * @return
	 */
	public static OctetSequenceEscaping createFromWhitelist(List<Character> whitelist)
	{
		if(! whitelist.contains(DEFAULT_ESCAPE_CHARACTER))
			whitelist.add(DEFAULT_ESCAPE_CHARACTER);
		return new OctetSequenceEscaping(whitelist, null, DEFAULT_ESCAPE_CHARACTER.charValue());
	}
	
	/**
	 * Create an instance where any characters on the blacklist will be encoded
	 * as UTF-8 sequences.
	 * 
	 * @param blacklist
	 * @return
	 */
	public static OctetSequenceEscaping createFromBlacklist(List<Character> blacklist)
	{
		if(blacklist.contains(DEFAULT_ESCAPE_CHARACTER))
			blacklist.remove(DEFAULT_ESCAPE_CHARACTER);
		return new OctetSequenceEscaping(null, blacklist, DEFAULT_ESCAPE_CHARACTER.charValue());
	}

	// =============================================================================
	private final List<Character> whitelist;
	private final List<Character> blacklist;
	private final char escapeChar;
	private final Pattern octetSequencePattern;

	
	private OctetSequenceEscaping(List<Character> whitelist, List<Character> blacklist, char escapeChar)
	throws PatternSyntaxException
	{
		this.whitelist = whitelist;
		this.blacklist = blacklist;
		this.escapeChar = escapeChar;
		this.octetSequencePattern = Pattern.compile(getEscapeChar() + OCTET_SEQUENCE_SUFFIX);
	}
	
	/**
	 * @return the whitelist
	 */
	private List<Character> getWhitelist()
	{
		return this.whitelist;
	}

	/**
	 * @return the blacklist
	 */
	private List<Character> getBlacklist()
	{
		return this.blacklist;
	}

	/**
	 * @return the escapeChar
	 */
	private char getEscapeChar()
	{
		return this.escapeChar;
	}

	/**
	 * @return the octetSequencePattern
	 */
	private Pattern getOctetSequencePattern()
	{
		return this.octetSequencePattern;
	}

	/**
	 * Determine if the character given is a legal character, that is either on the
	 * whitelist or not on the blacklist (or the whitelist and blacklist are both
	 * null but that shouldn't happen).
	 * 
	 * @param ch
	 * @return
	 */
	private boolean isLegalCharacter(char ch)
	{
		return getWhitelist() != null ?  
			getWhitelist().contains( new Character(ch) ):
			getBlacklist() != null ? 
				!getBlacklist().contains( new Character(ch) ):
				true;
	}
	
	/**
	 * From RFC2141 (where this escaping was originally used):
	 * 
	 * "Depending on the rules governing a namespace, valid identifiers in a
	 * namespace might contain characters that are not members of the URN
	 * character set above (<URN chars>).  Such strings MUST be translated
	 * into canonical NSS format before using them as protocol elements or
	 * otherwise passing them on to other applications. Translation is done
	 * by encoding each character outside the URN character set as a
	 * sequence of one to six octets using UTF-8 encoding [5], and the
	 * encoding of each of those octets as "%" followed by two characters
	 * from the <hex> character set above. The two characters give the
	 * hexadecimal representation of that octet."
	 * 
	 * Find all of the illegal characters in the value and escape them as hex 
	 * octet-sequence representations.
	 * 
	 * IMPORTANT: It MUST be true that for any String value S:
	 * unescapeIllegalCharacters(escapeIllegalCharacters(S)).equals(S)
	 * 
	 * @param unescapedValue
	 * @return
	 * @see #unescapeIllegalCharacters(String)
	 */
	public String escapeIllegalCharacters(String unescapedValue)
	{
		if(unescapedValue == null || unescapedValue.length() == 0)
			return unescapedValue;
		
		String escapedValue = unescapedValue;
		
		for(char ch : unescapedValue.toCharArray())
		{
			if(! isLegalCharacter(ch))
			{
				int codePoint = Character.codePointAt(new char[]{ch}, 0);
				
				String octetSequence = Integer.toString(codePoint, 16);
				if(octetSequence.length() == 1)
					octetSequence = "0" + octetSequence;
				octetSequence = getEscapeChar() + octetSequence;
				
				escapedValue = escapedValue.replace(Character.toString(ch), octetSequence);
			}
		}
		
		return escapedValue;
	}		
	
	/**
	 * 
	 * @param escaped
	 * @return
	 * @see #escapeIllegalCharacters(String)
	 * 
	 */
	public String unescapeIllegalCharacters(String escaped)
	{
		if(escaped == null || escaped.length() == 0)
			return escaped;
		
		Matcher escapeMatcher = getOctetSequencePattern().matcher(escaped);

		// will contain the unescaped sequence when we're done
		String unescaped = escaped;
		
		// finds escape sequences in the form "%hh"
		while( escapeMatcher.find() )
		{
			String octetSequence = escapeMatcher.group();
			// parse the last two characters of the octet sequence into an integer value
			int characterValue = Integer.parseInt(octetSequence.substring(1), 16);
			// convert the integer value into the character set mapped character
			char[] character = Character.toChars(characterValue);
			
			// If the unescaped value is NOT a legal RFC2141 character then
			// replace the escape sequence with the character.
			// If the unescaped sequence IS a legal RFC2141 character then do
			// nothing.
			// This behavior is to accurately do the opposite of what the escapeIllegalCharacters
			// method does.
			String unescapedValue = String.copyValueOf(character);
			if(unescapedValue.length() == 1 && !isLegalCharacter(unescapedValue.charAt(0)))
				unescaped = unescaped.replace(octetSequence, unescapedValue);
		}
		
		return unescaped;
	}

	/**
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isLegalRFC2141NSS(char ch)
	{
		return OctetSequenceEscaping.RFC2141_LEGAL_NSS_CHARS.contains(new Character(ch));
	}

	/**
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isLegalFilename(char ch)
	{
		return OctetSequenceEscaping.FILENAME_SAFE_CHARS.contains(new Character(ch));
	}
	
	/**
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isLegalCdtp(char ch)
	{
		return OctetSequenceEscaping.CDTP_LEGAL_CHARS.contains(new Character(ch));
	}
}
