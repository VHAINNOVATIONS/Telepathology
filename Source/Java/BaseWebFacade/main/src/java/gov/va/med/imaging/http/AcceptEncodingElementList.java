/*
 * Created on Apr 27, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.imaging.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import gov.va.med.imaging.http.exceptions.HttpHeaderParseException;

/**
 * @author Chris Beckey
 */
public class AcceptEncodingElementList
extends ArrayList
{
	public static AcceptEncodingElementList parseAcceptEncodingElementList(Enumeration enumerator)
	throws HttpHeaderParseException
	{
		AcceptEncodingElementList newAcceptEncodingElementList = new AcceptEncodingElementList();
		
		while( enumerator != null && enumerator.hasMoreElements() )
			newAcceptEncodingElementList.parse((String)enumerator.nextElement());

		return newAcceptEncodingElementList;
	}
	
	/**
	 * 
	 * @param rawList
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static AcceptEncodingElementList parseAcceptEncodingElementList(String rawList)
	throws HttpHeaderParseException
	{
		AcceptEncodingElementList newAcceptEncodingElementList = new AcceptEncodingElementList();
		newAcceptEncodingElementList.parse(rawList);
		
		return newAcceptEncodingElementList;
	}
	
	/**
	 * 
	 *
	 */
	private AcceptEncodingElementList()
	{
	}
	
	/**
	 * 
	 * @param acceptHeader
	 * @throws HttpHeaderParseException
	 */
	private void parse(String acceptHeader)
	throws HttpHeaderParseException
	{
		for( 
				StringTokenizer commaTokenizer = new StringTokenizer(acceptHeader, ",");
				commaTokenizer.hasMoreTokens(); )
			this.add( AcceptEncodingElement.parseAcceptEncodingElement(commaTokenizer.nextToken()) );
	}
}
