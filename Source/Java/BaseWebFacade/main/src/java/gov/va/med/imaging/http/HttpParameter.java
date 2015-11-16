/*
 * Originally HttpParameter.java 
 * created on Nov 22, 2004 @ 1:51:03 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

import gov.va.med.imaging.http.exceptions.*;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 22, 2004 1:51:03 PM
 *
 * Add comments here
 */
public class HttpParameter
{
	private String attribute = null;
	private String value = null;
	
	/**
	 * Takes any String in a format: name"="value and parses it into a name/value
	 * pair.
	 * @param modifier
	 * @return
	 */
	public static HttpParameter parseHttpParameter(String modifier)
	throws HttpHeaderParseException
	{
		HttpParameter newHttpParameter = new HttpParameter();
		newHttpParameter.parse(modifier);
		
		return newHttpParameter;
	}
	
	protected HttpParameter()
	{
	}
	
	/**
	 * Parse the modifier (in the form key"="value).  
	 * This method does not throw any exceptions.
	 * If the parameter is null then this method does nothing.
	 * If the equals sign is not present then the key value is set to the parameter.trim()
	 * If the equals sign is present then the key and value are set to the parsed parameter, trimmed.
	 * 
	 * Ex: modifier='q=32.45'                key="q"   value="32.45"
	 *     modifier=null                     key=null  value=null
	 *     modifier='xyz'                    key="xyz" value=null
	 *     modifier='abc='                   key="abc" value=""
	 *     modifier='abc="hello world"'      key="abc" value="hello world"
	 *  
	 * @param modifier
	 * @throws HttpHeaderParseException
	 */
	protected void parse(String modifier)
	throws HttpHeaderParseException
	{
		if(modifier == null)
			return;
			
		int equalsIndex = modifier.indexOf('=');
		if(equalsIndex > 0)
		{	
			String name = modifier.substring(0, equalsIndex).trim();
			setAttribute(name);
			if(equalsIndex+1 < modifier.length())
			{
				setValue( modifier.substring(equalsIndex+1).trim() );
			}
		}
		else
			setAttribute(modifier.trim());
	}
	
	/**
	 * @return
	 */
	public String getAttribute()
	{
		return attribute;
	}

	/**
	 * @return
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param string
	 */
	protected void setAttribute(String string)
	throws HttpHeaderParseException
	{
		attribute = string.trim();
	}

	/**
	 * 
	 * @param string
	 */
	protected void setValue(String string)
	throws HttpHeaderParseException
	{
		String trimmed = string.trim();					// trim leftover white space
		
		int firstQuoteIndex = trimmed.indexOf('"'); 
		int secondQuoteIndex = -1;
		 
		if(firstQuoteIndex >= 0 && 							// if there is a quote
			firstQuoteIndex < trimmed.length()-1 &&			// and the first quote is not the last char
		    (secondQuoteIndex = trimmed.indexOf('"', firstQuoteIndex+1)) >= 0 )	// and there is a second quote
		{
			value = trimmed.substring(firstQuoteIndex+1, secondQuoteIndex);
		}
		else
			value = trimmed;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getAttribute() + "=\"" + getValue() + "\"";
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof HttpParameter)
		{
			HttpParameter that = (HttpParameter)obj;
			return 
				( this.getAttribute()==null && that.getAttribute()==null ||
				  this.getAttribute()!=null && this.getAttribute().equals(that.getAttribute()) ) &&
				( this.getValue()==null && that.getValue()==null ||
				  this.getValue()!=null && this.getValue().equals(that.getValue()) );
		}
		else
			return false;
	}

	/**
	 * A simple test driver
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		try
		{
			System.out.println( "q=32.45    ---" + parseHttpParameter("q=32.45").toString() );
			System.out.println( "abc        ---" + parseHttpParameter("abc").toString() );
			System.out.println( "abc=       ---" + parseHttpParameter("abc=").toString() );
			System.out.println( "abc=xyz    ---" + parseHttpParameter("abc=xyz").toString() );
			System.out.println( "abc=\"xyz\"---" + parseHttpParameter("abc=\"xyz\"").toString() );
			System.out.println( "null       ---" + parseHttpParameter(null).toString() );
		}
		catch (HttpHeaderParseException e)
		{
			e.printStackTrace();
		}
	}
}
