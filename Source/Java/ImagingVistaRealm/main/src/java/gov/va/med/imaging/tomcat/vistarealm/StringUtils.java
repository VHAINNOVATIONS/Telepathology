package gov.va.med.imaging.tomcat.vistarealm;

//import gov.va.med.hds.util.date.FileManDateFormat;
//import gov.va.med.hds.util.date.PointInTime;
//import gov.va.med.hds.util.date.PointInTimeFormat;
//import gov.va.med.hds.util.date.ImprecisePointInTimeException;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class StringUtils 
{ 
    public static final String CRLF = "\r\n";
    public static final String CARET = "^";
    public static final String STICK = "|";
    public static final String COLON = ":";
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

	
    public static String[] Split(String s,String delimiter) {
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

	public static String Piece(String s, String delimiter, int pieceNum) 
	{
		// JMW 8/7/2006 changed to pieceNum > flds.length from pieceNum >= flds.length
        String[] flds = Split(s,delimiter);
        if (pieceNum > flds.length) return null;
        return flds[pieceNum-1];
    }
	
	public static String MagPiece(String s, String delimiter, int pieceNum) {
		String[] flds = Split(s,delimiter);
        if (pieceNum > flds.length) return s;
        return flds[pieceNum-1];
	}

/* *** from System.Collections.SortedList */

	public static String[] SortByPiece(String[] s, String delimiter, int pieceNum) 
	{
/* ***	SortedList sl = new SortedList();
		//pieceNum--;
		for (int i=0; i<s.length; i++)
			sl.Add(Piece(s[i],delimiter,pieceNum),s[i]);
		for (int i=0; i<sl.get_Count(); i++) 
			s[i] = (String)sl.GetByIndex(i);
 */
		TreeMap<String, String> tm = new TreeMap<String, String>();
		for (int i=0; i<s.length; i++) {
			String key=Piece(s[i], delimiter, pieceNum);
			tm.put(key, s[i]);
		}
		Object[] iter = tm.keySet().toArray();
		for (int i=0; i<tm.size(); i++)
			s[i] = (String)tm.get(iter[i]);

		return s;
	}

	public static String getMonthNumberString(String sMonth) 
	{
		sMonth = sMonth.toUpperCase(); // ToUpper();
		if (sMonth.startsWith("JAN")) return "01";
		if (sMonth.startsWith("FEB")) return "02";
		if (sMonth.startsWith("MAR")) return "03";
		if (sMonth.startsWith("APR")) return "04";
		if (sMonth.startsWith("MAY")) return "05";
		if (sMonth.startsWith("JUN")) return "06";
		if (sMonth.startsWith("JUL")) return "07";
		if (sMonth.startsWith("AUG")) return "08";
		if (sMonth.startsWith("SEP")) return "09";
		if (sMonth.startsWith("OCT")) return "10";
		if (sMonth.startsWith("NOV")) return "11";
		if (sMonth.startsWith("DEC")) return "12";
		return null;
	}


	public static String VistaDateTimeString2VistaTimestamp(String s)
	{
		if (s.indexOf('@') == -1) s += "@00:00";
		String[] parts = StringUtils.Split(s,StringUtils.ATSIGN);
		String dateStr = parts[0];
		dateStr = dateStr.replaceAll(", ", ","); //*** Replace(", ",",")
		dateStr = dateStr.replace(' ',',');
		String[] flds = StringUtils.Split(dateStr,StringUtils.COMMA);
		String month = getMonthNumberString(flds[0]);
		String day = flds[1];
		if (day.length() == 1) day = "0" + day;
		String year = flds[2];
		flds = StringUtils.Split(parts[1],StringUtils.COLON);
		String hours = flds[0];
		if (hours.length() == 1) hours = "0" + hours;
		String minutes = flds[1];
		if (minutes.length() == 1) minutes = "0" + minutes;
		String seconds = "";
		if (flds.length > 2) seconds = flds[2];
		if (seconds.length() == 1) seconds = "0" + seconds; 
		int y = Integer.parseInt(year) - 1700;
		String result = y + month + day + "." + hours + minutes + seconds;
		return result;
	}

	public static String DateTimeString2VistaTimestamp(String s)
	{
		if (s.indexOf("@") != -1) s = s.replace('@',' ');
		String[] parts = StringUtils.Split(s,StringUtils.SPACE);
		if (parts.length == 0) return "";
		String[] flds = StringUtils.Split(parts[0],StringUtils.SLASH);
		if (flds.length == 1) return "";
		int year = Integer.parseInt(flds[2]) - 1700;
		String ts = String.valueOf(year) + flds[0] + flds[1];
		if (parts.length == 2) 
		{
			flds = StringUtils.Split(parts[1],StringUtils.COLON);
			ts += "." + flds[0] + flds[1];
		}
		return ts;
	}

	public static String removeBlankLines(String s) 
	{
		String[] lines = Split(s,CRLF);
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
	
	
} 
