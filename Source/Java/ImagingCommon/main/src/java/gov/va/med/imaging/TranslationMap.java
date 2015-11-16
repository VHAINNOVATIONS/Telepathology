package gov.va.med.imaging;

/**
 * @author beckey
 * created: Jan 7, 2005 at 2:40:52 PM
 *
 * This class translates a byte into a mapped character and back
 * It is highly recommended that this class not be changed because
 * that could make decoding of existing GUIDs impossible.
 */
public class TranslationMap
{
	// if true, use the translationMap for mapping, else
	// do it arithmetically
	static private final boolean useTranslationMap = false;
	
	private TranslationMap(){}
	
	// NOTE: this table is no longer used, it remains here because it makes debugging 
	// easier.
	// a table of 64 printable characters which comprise the legal set of
	// GUID characters.
	// BTW, if this ever changes it will completely screw-up encoding/decoding
	private static final char[] translationMap = {
		'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f',
		'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
		'w','x','y','z','@','#','%','^','&','*','(',')','-','_','=','+',
		':',';','<','>','?','/','\\','|','{','}','[',']','~','.','!','$'
	};
	
	/**
	 * Translate a byte value (less than 64 i.e. 6 bits) into a character using a reversible mapping.
	 * This method will throw a IllegalArgumentException if the value is greater than 64.
	 * 
	 * @param index
	 * @return
	 */
	static public char translateByteToMappedChar(byte index)
	{
		if(index >= 64)
			throw new IllegalArgumentException("Attempt to translate byte value greater than 64");
			
		if(isUseTranslationMap())
		{
			return translationMap[index];
		}
		else
		{
			if(index <= 9)
				return (char)( '0' + index );			// '0' through '9'
			else if( index <= 35 )
				return (char)( 'a' + (index-10) );	// 'a' to 'z'
			else if( index <= 61 )
				return (char)( 'A' + (index-36) );	// 'A' to 'Z'
			else if( index == 62 )
				return '!';
			else if( index == 63 )
				return '$';
			
			return ' ';
		}		
	}
	
	/**
	 * Translate a character into a byte value.  This method and translateByteToMappedChar
	 * must provide reversible mapping, that is:
	 * translateMappedCharToByte(translateByteToMappedChar(n)) == n
	 * for all values of n less than 64 
	 * This method will throw a IllegalArgumentException if the character is not mapped.
	 * 
	 * @param member
	 * @return
	 */
	static public byte translateMappedCharToByte(char member)
	{
		if(isUseTranslationMap())
		{
			for(int index=0; index < translationMap.length; ++index)
				if( translationMap[index] == member )
					return (byte)index;
		}
		else
		{
			if(member >= '0' && member <= '9')
				return (byte)(member - '0');
			if(member >= 'a' && member <= 'z')
				return (byte)((member - 'a') + 10);
			if(member >= 'A' && member <= 'Z')
				return (byte)((member - 'A') + 36);
			if(member == '!' )
				return (byte)62;
			if(member == '$' )
				return (byte)63;
		}		
		throw new IllegalArgumentException("Attempt to translate unmapped char");
	}
	
	public static boolean isUseTranslationMap()
	{
		return useTranslationMap;
	}
}
