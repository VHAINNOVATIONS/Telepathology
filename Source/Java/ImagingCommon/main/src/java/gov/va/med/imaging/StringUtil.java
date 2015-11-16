/**
 * 
 */
package gov.va.med.imaging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author VHAISWBECKEC
 *
 */
public class StringUtil
{
	
    public static final String CRLF = "\r\n";
    public static final String CARET = "^";
    public static final String STICK = "|";
    public static final String COLON = ":";
    public static final String DOUBLECOLON = "::";
    public static final String SEMICOLON = ";";
    public static final String COMMA = ",";
    public static final String PERIOD = ".";
    public static final String SLASH = "/";
    public static final String SPACE = " ";
	public static final String EQUALS = "=";
	public static final String AMPERSAND = "&";
	public static final String ATSIGN = "@";
	public static final String NEW_LINE = "\n";
	public static final String TILDE = "~";

    public static final char CRCHAR = '\r';
    public static final char NEW_LINECHAR = '\n';
    public static final char TABCHAR = '\t';
    public static final char FORM_FEEDCHAR = '\f';
    public static final char BACKCHAR = '\b';
    public static final char DOUBLE_QUOTECHAR = '\"';
    public static final char SINGLE_QUOTECHAR = '\'';
    public static final char SLASHCHAR = '\\';
	public static final String DASH = "-";

    
	/**
	 * return true if both r1 and r2 are null or 
	 * if r1 equals r2
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean equalOrNull(String s1, String s2)
	{
		return (s1 == null && s2 == null) || (s1 != null && s1.equals(s2));
	}

	/**
	 * return true if both r1 and r2 are null or 
	 * if r1 equals r2, up to the index specified
	 * 
	 * @param r1
	 * @param r2
	 * @param endIndex
	 * @return
	 */
	public static boolean equalOrNull(String r1, String r2, int endIndex)
	{
		if(r1==null && r1==null)
			return true;
		if(r1==null || r2==null)
			return false;
		
		String comparableR1 = r1.substring(0, endIndex);
		
		String comparableR2 = r2.substring(0, endIndex);
		
		return comparableR1.equals(comparableR2);
	}

	/**
	 * Return true if both r1 and r2 are null or if r1 equals r2, up to the first
	 * occurence of endDelimiter.
	 * 
	 * e.g.
	 * equalsOrNull(null, null, null) = true
	 * equalsOrNull("Hello person named Fred", "Hello person named Barney", "person named") = true
	 * 
	 * @param r1
	 * @param r2
	 * @param endDelimiter
	 * @return
	 */
	public static boolean equalOrNullStrings(String r1, String r2, String endDelimiter)
	{
		if(endDelimiter == null)
			return equalOrNull(r1, r2);
		
		if(r1==null && r1==null)
			return true;
		if(r1==null || r2==null)
			return false;
		
		int comparableR1Index = r1.indexOf(endDelimiter);
		comparableR1Index = (comparableR1Index == -1 ? r1.length()-1 : comparableR1Index);
		String comparableR1 = r1.substring(0, comparableR1Index);
		
		int comparableR2Index = r2.indexOf(endDelimiter);
		comparableR2Index = (comparableR2Index == -1 ? r1.length()-1 : comparableR2Index);
		String comparableR2 = r2.substring(0, comparableR2Index);
		
		return comparableR1.equals(comparableR2);
	}
	
    public static String[] split(String s,String delimiter) {
        //Can't use StringTokenizer because it skips blank fields.
        //Can't use it with returnTokens because it screws up multi-char delimiters.
        //All in all, it's a pretty goddamn useless utility
		if (s.endsWith(delimiter))
		{
			s += delimiter;
		}
        ArrayList<String> al = new ArrayList<String>();
        int p1 = 0, p2 = 0;
        String prev = delimiter;
        while ((p2 = s.indexOf(delimiter,p1)) != -1) {
            String fld = s.substring(p1,p2);
            if (fld.equals(delimiter) && prev.equals(delimiter)) {
                al.add("");
            } else if (!fld.equals(delimiter)) {
                al.add(fld);
            }
            prev = fld;
            p1 = p2 + delimiter.length();
        }
        if (p1 != s.length()) al.add(s.substring(p1));
        //String rtn[] = (String[])al.toArray(new String[0]);
        //Since above line doesn't work with .NET we have to do it this way:
        String rtn[] = new String[al.size()];
        int i = 0;
        Iterator<String> iter = al.iterator();
        while (iter.hasNext()) {
            rtn[i++] = (String)iter.next();
        }
        return rtn;
    }

    public static boolean isNumeric(String s) {
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

	public static String prepend(String s, char c, int lth)
	{
		if (s.length() > lth)
		{
			throw new IllegalArgumentException("Input string longer than requested string");
		}
		if (s.length() == lth)
		{
			return s;
		}
		while (s.length() < lth)
		{
			s += c;
		}
		return new StringBuffer(s).reverse().toString();
	}

	public static boolean isNumericChar(char c)
	{
		return (c >= '0' && c <= '9');
	}

	public static boolean isAlphaChar(char c)
	{
		return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
	}

	public static boolean isAlphaNumericChar(char c)
	{
		return (isAlphaChar(c) || isNumericChar(c));
	}

	public static boolean isWhiteSpace(char c)
	{
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

	public static String removeNonNumericChars(String s)
	{
		if (s == null)
		{
			return null;
		}
		String rtn = "";
		for (int i=0; i<s.length(); i++)
		{
			char c = s.charAt(i);
			if (c >= '0' && c <= '9')
			{
				rtn += c;
			}
		}
		return rtn;
	}
	
	public static String removeBlankLines(String s) 
	{
		String[] lines = split(s,CRLF);
		boolean lastLineBlank = false;
		String newS = "";
		for (int i=0; i<lines.length; i++) 
		{
			if (!lines[i].equals("")) 
			{
				newS += lines[i] + CRLF;
				lastLineBlank = false;
			} 
			else 
			{
				if (!lastLineBlank) newS += CRLF;
				lastLineBlank = true;
			}
		}
		return newS;
	}

	public static boolean isEmpty(String s)
	{
		return s == null || s.length() == 0;
	}	
	
	public static String[] breakString(String input, int partLength) {
		int numParts = input.length() / partLength;
		if (input.length() % partLength > 0)
			numParts++;
		String[] parts = new String[numParts];
		int startIndex, endIndex;
		for (int i = 0; i < numParts; i++) {
			startIndex = i * partLength;
			endIndex = startIndex + partLength;
			if (endIndex > input.length()){
				endIndex = input.length();
			}
			parts[i] = input.substring(i * partLength, endIndex);
		}

		return parts;
	}
	
	
	public static String Piece(String s, String delimiter, int pieceNum) 
	{
		// JMW 8/7/2006 changed to pieceNum > flds.length from pieceNum >= flds.length
        String[] flds = split(s,delimiter);
        if (pieceNum > flds.length) return null;
        return flds[pieceNum-1];
    }
	
    public static String Piece(String line, char delimiter, int interestedPiece){
        
        //parse the line and return only the interested piece based on delimiters.
        String[] pieces;
        String strDelimiter = "\\"+String.valueOf(delimiter);
      //Cannot use split() method in StringUtil.  Does not work properly.
        pieces = line.split(strDelimiter);
        if(interestedPiece > pieces.length) return null;
        return pieces[interestedPiece-1];
    }

	public static String MagPiece(String s, String delimiter, int pieceNum) {
		String[] flds = split(s,delimiter);
        if (pieceNum > flds.length) return s;
        return flds[pieceNum-1];
	}

	public static String[] sortByPiece(String[] s, String delimiter, int pieceNum) 
	{
		TreeMap<String,String> tm = new TreeMap<String, String>();
		for (int i=0; i<s.length; i++) {
			String key=Piece(s[i], delimiter, pieceNum);
			tm.put(key, s[i]);
		}
		Object[] iter = tm.keySet().toArray();
		for (int i=0; i<tm.size(); i++)
			s[i] = tm.get(iter[i]);

		return s;
	}    
	
    public static String displayEncodedChars(String encodedString){
        StringBuffer decodedString = new StringBuffer();
        char[] chars = encodedString.toCharArray();
            
        for(int x=0; x<chars.length; x++){
            if(chars[x] == TABCHAR){
                decodedString.append("\\t");
            }
            else if(chars[x] == CRCHAR){
                decodedString.append("\\r");
            }
            else if(chars[x] == NEW_LINECHAR){
                decodedString.append("\\n");
            }
            else if(chars[x] == FORM_FEEDCHAR){
                decodedString.append("\\f");
            }
            else if(chars[x] == BACKCHAR){
                decodedString.append("\\b");
            }
            else if(chars[x] == DOUBLE_QUOTECHAR){
                decodedString.append("\\\"");
            }
            else if(chars[x] == SINGLE_QUOTECHAR){
                decodedString.append("\\'");
            }
            else if(chars[x] == SLASHCHAR){
                decodedString.append("\\\\");
            }
            else{
                decodedString.append(chars[x]);
            }
        }
        return decodedString.toString();
    }
    
    
    public static String formatPatientIDWithDashes(String patientID){
    	String expression = "^\\d{9}$";
    	CharSequence inputSSN = patientID;
    	Pattern pattern = Pattern.compile(expression);
    	Matcher matcher = pattern.matcher(inputSSN);
    	if(matcher.matches()){
    		StringBuffer sb = new StringBuffer(inputSSN);
    		sb.insert(3, DASH);
    		sb.insert(6, DASH);
    		return sb.toString();
    	}
    	else{
    		return patientID;
    	}
    }
    
    /**
     * Method intVal Calulates integer value based on first characters in a
     * string: "123abcde" yields 123
     *
     * @param str
     *            input string
     * @return integer value of start of string
     */
    public synchronized static int intVal (String str)
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
