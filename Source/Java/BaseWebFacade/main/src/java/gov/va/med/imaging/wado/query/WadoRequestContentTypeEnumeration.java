/*
 * Originally WadoRequestContentTypeEnumeration.java 
 * created on Nov 23, 2004 @ 5:16:38 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.wado.query;

import gov.va.med.imaging.http.AcceptElementList;

import java.util.Enumeration;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 23, 2004 5:16:38 PM
 *
 * An enumeration of the content types specified in a WADO request, in the order
 * of preference.
 * From the WADO spec:
 * MIME type(s) desired by the Web Client for the response from the Server, as defined in the [IETF
 * RFC2616]. This parameter is OPTIONAL.
 * The parameter name shall be “contentType”.
 * The value shall be a list of MIME types, separated by a "," character, and potentially associated
 * with relative degree of preference, as specified in [IETF RFC2616].
 * The Web Client shall provide list of content types it supports in the "Accept" field of the GET
 * method. The value of the contentType parameter of the request shall be one of the values specified
 * in that field.
 * 
 * This class is is provided with a reference to the parsed HTTP header and the WADO query.  From
 * these it maintains references/index/pointers sufficent to enumerate the contentType as specified
 * in the HTTP header and WADO query in the following order:
 * each acceptType in the WADO query
 * each acceptType in the HTTP request header
 *   
 */
public class WadoRequestContentTypeEnumeration 
implements Enumeration
{
	private AcceptElementList queryContentList = null; 
	private AcceptElementList requestContentList = null;

	/**
	 * 
	 * @param queryContentList
	 * @param requestContentList
	 */
	WadoRequestContentTypeEnumeration(
		AcceptElementList queryContentList, 
		AcceptElementList requestContentList)
	{
		this.queryContentList = queryContentList;
		this.requestContentList = requestContentList;  
	}

	/**
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
