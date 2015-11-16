package gov.va.med.imaging.protocol.vista;

import gov.va.med.imaging.url.vista.StringUtils;

public class DicomTranslatorUtility
{
	public static final char CRCHAR = '\r';
	public static final char NEW_LINECHAR = '\n';
	public static final char TABCHAR = '\t';
	public static final char FORM_FEEDCHAR = '\f';
	public static final char BACKCHAR = '\b';
	public static final char DOUBLE_QUOTECHAR = '\"';
	public static final char SINGLE_QUOTECHAR = '\'';
	public static final char SLASHCHAR = '\\';
	
	public static String[] createResultsArray(String returnValue)
	{
		return createResultsArrayStrippingHeaderLines(returnValue, 0);
	}
	
	public static String[] createResultsArrayStrippingHeaderLines(String returnValue, int numHeaderLines)
	{
		// Split the output from the RPC into a string array, and strip off the first two
		// header lines.
		String[] allLines = StringUtils.Split(returnValue, StringUtils.CRLF);
		String[] dataLines = new String[allLines.length - numHeaderLines];
		System.arraycopy(allLines, numHeaderLines, dataLines, 0, dataLines.length);
		
		return dataLines;
	}

	public static String displayEncodedChars(String encodedString)
	{
		StringBuffer decodedString = new StringBuffer();
		char[] chars = encodedString.toCharArray();

		for (int x = 0; x < chars.length; x++)
		{
			if (chars[x] == TABCHAR)
			{
				decodedString.append("\\t");
			}
			else if (chars[x] == CRCHAR)
			{
				decodedString.append("\\r");
			}
			else if (chars[x] == NEW_LINECHAR)
			{
				decodedString.append("\\n");
			}
			else if (chars[x] == FORM_FEEDCHAR)
			{
				decodedString.append("\\f");
			}
			else if (chars[x] == BACKCHAR)
			{
				decodedString.append("\\b");
			}
			else if (chars[x] == DOUBLE_QUOTECHAR)
			{
				decodedString.append("\\\"");
			}
			else if (chars[x] == SINGLE_QUOTECHAR)
			{
				decodedString.append("\\'");
			}
			else if (chars[x] == SLASHCHAR)
			{
				decodedString.append("\\\\");
			}
			else
			{
				decodedString.append(chars[x]);
			}
		}
		return decodedString.toString();
	}

    /**
     * Method intVal Calculates integer value based on first characters in a
     * string: "123abcde" yields 123
     *
     * @param str
     *            input string
     * @return integer value of start of string
     */
    public static int intVal (String str)
    {
      int charpos = 0;
      int ready = 0;
      char letter;
      int value = 0;
      int neg = 1;

      while ((ready < 1) && (charpos < str.length ()))
      {
        letter = str.charAt (charpos++);
        switch (letter)
        {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          value = value * 10 + letter - '0';
          break;
        case '-':
          neg = -neg;
          break;
        default:
          ready = 1;
          break;
        }
      }
      return value * neg;
    }


}
