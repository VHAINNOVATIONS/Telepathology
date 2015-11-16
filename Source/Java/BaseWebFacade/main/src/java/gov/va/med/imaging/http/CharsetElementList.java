/*
 * Originally CharsetElementList.java 
 * created on Nov 18, 2004 @ 2:36:36 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

import gov.va.med.imaging.http.exceptions.HttpHeaderParseException;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 18, 2004 2:36:36 PM
 *
 * The Accept-Charset request-header field can be used to indicate what
 * character sets are acceptable for the response. This field allows
 * clients capable of understanding more comprehensive or special-
 * purpose character sets to signal that capability to a server which is
 * capable of representing documents in those character sets.
 * 
 * Accept-Charset = "Accept-Charset" ":"
 * 1#( ( charset | "*" )[ ";" "q" "=" qvalue ] )
 * 
 * Character set values are described in section 3.4. Each charset MAY
 * be given an associated quality value which represents the user's
 * preference for that charset. The default value is q=1. An example is
 *       Accept-Charset: iso-8859-5, unicode-1-1;q=0.8
 * 
 * The special value "*", if present in the Accept-Charset field,
 * matches every character set (including ISO-8859-1) which is not
 * mentioned elsewhere in the Accept-Charset field. If no "*" is present
 * in an Accept-Charset field, then all character sets not explicitly
 * mentioned get a quality value of 0, except for ISO-8859-1, which gets
 * a quality value of 1 if not explicitly mentioned.
 */

public class CharsetElementList
extends ArrayList
{
	/**
	 * Create a CharsetElementList from the enumeration created from
	 * a HTTP Request getHeader().  The values in the enumeration will
	 * each be comma delimited lists of charsets.
	 * 
	 * @param charsetEnum
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static CharsetElementList parseCharsetElementList(java.util.Enumeration charsetEnum)
	throws HttpHeaderParseException
	{
		CharsetElementList charsets = new CharsetElementList();
		while( charsetEnum != null && charsetEnum.hasMoreElements() )
			charsets.add((String)charsetEnum.nextElement());
		
		return charsets;
	}

	public static CharsetElementList parseCharsetElementList(String charsetList)
	throws HttpHeaderParseException
	{
		CharsetElementList charsets = new CharsetElementList();
		charsets.add(charsetList);
		
		return charsets;
	}
	
	private CharsetElementList()
	{	}

	/**
	 * Add a list of comma delimited charsets.
	 * 
	 * @param charsetElement
	 */
	private void add(String charsetElement)
	throws HttpHeaderParseException
	{
		parse(charsetElement);
	}
	
	/**
	 * Parse a list of comma delimited charsets and add
	 * them to our list
	 * 
	 * @param charsetElement
	 */
	private void parse(String charsetElement)
	throws HttpHeaderParseException
	{
		for( StringTokenizer st = new StringTokenizer(charsetElement, ",");
		     st.hasMoreTokens(); )
		{
			this.add(CharsetElement.parseCharsetElement((String)st.nextElement()));
		}
	}
	
	/**
	 * Return the charset element list as an HTTP compliant,
	 * comm-delimited, String.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer(64);
		
		for(java.util.Iterator iter = iterator(); iter.hasNext(); )
		{
			if(sb.length() > 0)
				sb.append(",");
			sb.append(iter.next().toString());
		}
		return sb.toString();
	}
	
	/**
	 * Just a test driver
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		try
		{
			System.out.println(parseCharsetElementList("iso-8859-1") );
			System.out.println(parseCharsetElementList("iso-8859-1;q=0.1, unicode-1-1;q=0.8") );
			System.out.println(parseCharsetElementList("iso-8859-5, unicode-1-1;q=0.8") );
		}
		catch (HttpHeaderParseException httpParseX)
		{
			httpParseX.printStackTrace();
		}
		
	}
}
