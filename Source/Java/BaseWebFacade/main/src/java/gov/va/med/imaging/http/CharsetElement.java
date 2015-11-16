/*
 * Originally CharsetElement.java 
 * created on Nov 18, 2004 @ 4:43:41 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

import java.util.StringTokenizer;

import gov.va.med.imaging.http.exceptions.*;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 18, 2004 4:43:41 PM
 *
 * From HTTP 1.1 Spec RFC 2616 ---------------------------------------
 * The Accept-Charset request-header field can be used to indicate what
 * character sets are acceptable for the response. This field allow
 * clients capable of understanding more comprehensive or special-purpose 
 * character sets to signal that capability to a server which is
 * capable of representing documents in those character sets.
 * 
 * Accept-Charset = "Accept-Charset" ":"
 *               1#( ( charset | "*" )[ ";" "q" "=" qvalue ] )
 *
 * Character set values are described in section 3.4. Each charset MAY
 * be given an associated quality value which represents the user's
 * preference for that charset. The default value is q=1. An example is
 * 
 *       Accept-Charset: iso-8859-5, unicode-1-1;q=0.8
 * 
 * The special value "*", if present in the Accept-Charset field,
 * matches every character set (including ISO-8859-1) which is not
 * mentioned elsewhere in the Accept-Charset field. If no "*" is present
 * in an Accept-Charset field, then all character sets not explicitly
 * mentioned get a quality value of 0, except for ISO-8859-1, which gets
 * a quality value of 1 if not explicitly mentioned.
 * ----------------------------------------------------------------------
 */
public class CharsetElement
implements Cloneable, Comparable

{
	private String charset = null;
	private QValueAcceptParameter qValue = null;
	public static final String ISO8859 = "ISO-8859-1"; 
	public static final String defaultCharset = ISO8859; 
	
	/**
	 * 
	 * @param rawElement
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static CharsetElement parseCharsetElement(String rawElement)
	throws HttpHeaderParseException
	{
		CharsetElement newCharsetElement = new CharsetElement();

		newCharsetElement.parse(rawElement);
		
		return newCharsetElement;
	}

	/**
	 * 
	 * @param charset
	 * @param qValue
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static CharsetElement createCharsetElement(String charset, QValueAcceptParameter qValue)
	throws HttpHeaderParseException
	{
		CharsetElement newCharsetElement = new CharsetElement();

		newCharsetElement.setCharset( charset );
		newCharsetElement.setQValue(qValue);

		return newCharsetElement;
	}

	/**
	 * 
	 * @param charset
	 * @param qualityValue
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static CharsetElement createCharsetElement(String charset, String qualityValue)
	throws HttpHeaderParseException
	{
		return createCharsetElement(charset, QValueAcceptParameter.parseQValueParameter(qualityValue));
	}
	
	/**
	 * 
	 *
	 */
	private CharsetElement() {}

	/**
	 * @return
	 */
	public String getCharset()
	{
		return charset;
	}

	/**
	 * @param string
	 */
	private void setCharset(String string)
	{
		charset = string;
	}

	/**
	 * @return
	 */
	public QValueAcceptParameter getQValue()
	{
		return qValue;
	}

	/**
	 * @param parameter
	 */
	private void setQValue(QValueAcceptParameter parameter)
	{
		qValue = parameter;
	}


	/**
	 * Parse a String in the form 
	 * 1#( ( charset | "*" )[ ";" "q" "=" qvalue ] )
	 * into charset and qValue propeties
	 * @param rawElement
	 */
	protected void parse(String rawElement)
	throws HttpHeaderParseException
	{
		StringTokenizer st = new StringTokenizer(rawElement, ";");
		if( st.hasMoreTokens() )
		{
			this.setCharset(st.nextToken());
			if( st.hasMoreTokens() )
				this.setQValue( QValueAcceptParameter.parseQValueParameter(st.nextToken()) );
			else
				this.setQValue( QValueAcceptParameter.createQValueParameter(QValueAcceptParameter.maxQValue) );
		}
		else
			throw new HttpCharsetHeaderParseException("Charset header element does not specify a character set");
	}
	
	/**
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		CharsetElement that = (CharsetElement)o;
		
		int charsetCompare = this.getCharset().compareTo(that.getCharset());
		
		return charsetCompare == 0 ? 
			this.getQValue().compareTo(that.getQValue()) :
			charsetCompare;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() throws CloneNotSupportedException
	{
		try
		{
			return CharsetElement.createCharsetElement(this.charset, this.qValue);
		}
		catch (HttpHeaderParseException httpParseX)
		{
			throw new CloneNotSupportedException("Exception while cloning [" + httpParseX.getMessage() + "]");
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if( obj instanceof CharsetElement )
		{
			CharsetElement that = (CharsetElement)obj;
			
			return this.getCharset().equals(that.getCharset()) &&
			       this.getQValue().equals(that.getQValue()); 
		}
		
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return this.getCharset() + ";" + this.getQValue().toString();
	}

}
