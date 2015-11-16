/*
 * Created on Apr 27, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.imaging.http;

import gov.va.med.imaging.http.exceptions.HttpHeaderParseException;

/**
 * @author Chris Beckey
 */
public class AcceptEncodingElement
{
	public static AcceptEncodingElement parseAcceptEncodingElement(String rawEncodingElement)
	throws HttpHeaderParseException
	{
		AcceptEncodingElement newAcceptEncodingElement = new AcceptEncodingElement();
		newAcceptEncodingElement.parse(rawEncodingElement);
		
		return newAcceptEncodingElement;
	}
	
	private AcceptEncodingElement()
	{
	}
	
	private void parse(String rawEncodingElement)
	throws HttpHeaderParseException
	{
		
	}
}
