/*
 * Created on Apr 27, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.imaging.http;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import gov.va.med.imaging.http.exceptions.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chris Beckey
 */
public class ParsedHttpHeader
{
	AcceptElementList acceptEncodings;
	AcceptEncodingElementList encodingElements;
	CharsetElementList charsetElements;
	
	Map<String, String> miscRequestHeaders = new HashMap<String, String>();
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static ParsedHttpHeader parseHttpHeader(HttpServletRequest request)
	throws HttpHeaderParseException
	{
		ParsedHttpHeader newHttpHeader = new ParsedHttpHeader();
		newHttpHeader.parse(request);
		
		return newHttpHeader;
	}
	
	private ParsedHttpHeader()
	{	}
	
	/**
	 * @return Returns the Accept Element List.
	 */
	public AcceptElementList getAcceptElementList()
	{
		return acceptEncodings;
	}

	/**
	 * @return Returns the eel.
	 */
	public AcceptEncodingElementList getAcceptEncodingElementList()
	{
		return encodingElements;
	}

	public CharsetElementList getCharsetElementList()
	{
		return charsetElements;
	}

	/**
	 * 
	 * @param request
	 * @throws HttpHeaderParseException
	 */
	private void parse(HttpServletRequest request)
	throws HttpHeaderParseException
	{
		// get the client accept types in preferred order
		acceptEncodings = AcceptElementList.parseAcceptElementList(request.getHeader("Accept"));
		
		charsetElements = CharsetElementList.parseCharsetElementList(request.getHeaders("Accept-charset"));
		
		encodingElements = AcceptEncodingElementList.parseAcceptEncodingElementList(request.getHeaders("Accept-encoding"));
		
		// any other request headers are kept in a Map
		for( Enumeration<?> headerNameEnum = request.getHeaderNames(); headerNameEnum.hasMoreElements(); )
		{
			String headerName = headerNameEnum.nextElement().toString();
			if(! "Accept".equalsIgnoreCase(headerName) && 
				! "Accept-charset".equalsIgnoreCase(headerName) &&
				! "Accept-encoding".equalsIgnoreCase(headerName) )
					miscRequestHeaders.put(headerName, request.getHeader(headerName));
		}
	}
	
	/**
	 * A public accessor to get the miscellaneous request headers
	 * @param key
	 * @return
	 */
	public String getMiscRequestHeader(String key)
	{
		return miscRequestHeaders.get(key);
	}
}
