package gov.va.med.imaging.utils;

public class StringUtilities 
{
	private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";

	/**
	 * Removes control characters, and replaces potential M delimiters with
	 * more harmless stuff...
	 * @param stringToClean
	 * @return
	 */
	public static String escapeMumpsString(String stringToEscape)
	{

		// Escape common mumps delimiters
		String escapedString = stringToEscape.replace("~", "&#126;");
        escapedString = escapedString.replace("|", "&#124;");
        escapedString = escapedString.replace("`", "&#96;");
        escapedString = escapedString.replace("^", "&#94;");
        escapedString = escapedString.replace("\n", "&#10;");
        escapedString = escapedString.replace("\r", "&#13;");
		
        // Clean out control characters
        escapedString = escapedString.replaceAll(CONTROL_CHARACTER_REGEX, "");
        
        return escapedString;
	}

	public static String unescapeMumpsString(String stringToUnescape)
	{

		// Escape common mumps delimiters
		String unescapedString = stringToUnescape.replace("&#126;", "~");
		unescapedString  = unescapedString.replace("&#124;", "|");
		unescapedString  = unescapedString.replace("&#96;", "`");
		unescapedString  = unescapedString.replace("&#94;", "^");
		unescapedString  = unescapedString.replace("&#10;", "\n");
		unescapedString  = unescapedString.replace("&#13;", "\r");
		
        return unescapedString;
	}

}
