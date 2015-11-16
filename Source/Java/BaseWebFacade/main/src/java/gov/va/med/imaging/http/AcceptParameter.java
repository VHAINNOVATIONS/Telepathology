/*
 * Originally HttpHeaderModifier.java 
 * created on Nov 19, 2004 @ 2:42:10 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.http;

import gov.va.med.imaging.http.exceptions.HttpHeaderParseException;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 19, 2004 2:42:10 PM
 *
 * A class to represent generic name/value pairs in Http Header Accept element.
 * This class is a simple subclass of the HttpParameter class, providing nothing
 * more than a type specific to Accept elements.
 */
public class AcceptParameter
extends HttpParameter
{
	/**
	 * Takes any String in a format: name"="value and parses it into a name/value
	 * pair.
	 * @param modifier
	 * @return
	 */
	public static AcceptParameter parseAcceptParameter(String parameter)
	throws HttpHeaderParseException
	{
		AcceptParameter newParameter = new AcceptParameter();
		newParameter.parse(parameter);
		
		return newParameter;
	}
	
	protected AcceptParameter()
	{
		super();
	}

	/**
	 * Type checking equals implementation
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof AcceptParameter)		// more specific type checking
			return super.equals(obj);
		else
			return false;
	}
}
