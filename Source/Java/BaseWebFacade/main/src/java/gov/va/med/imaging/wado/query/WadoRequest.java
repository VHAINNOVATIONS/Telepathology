/*
 * Originally WADORequest.java 
 * created on Nov 17, 2004 @ 4:01:50 PM
 * by Chris Beckey mailto:c.beckey@seetab.com
 *
 */
package gov.va.med.imaging.wado.query;

import gov.va.med.imaging.http.*;
import gov.va.med.imaging.http.exceptions.*;
import gov.va.med.imaging.wado.query.exceptions.*;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chris Beckey mailto:c.beckey@seetab.com
 * @since Nov 17, 2004 4:01:50 PM
 *
 * This class joins together the WadoQuery and the ParsedHttpHeader
 * to give the definitive source of what was requested.
 * 
 */
public class WadoRequest
{
	private ParsedHttpHeader httpHeader = null;
	private WadoQuery wadoQuery = null;
	private ComplianceType compliance = null;

	/**
	 * Parse the HTTP request and validate according to WADO specification.
	 *  
	 * @param req
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static WadoRequest createParsedCompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.WADO);
	}

	/**
	 * Parse the HTTP request and validate according to WADO specification.
	 *  
	 * @param req
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static WadoRequest createParsedNoncompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.NONE);
	}
	
	/**
	 * Parse the HTTP request and validate according to WADO specification.
	 *  
	 * @param req
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static WadoRequest createParsedXChangeCompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.XCHANGE);
	}
	
	public static WadoRequest createParsedCDTPCompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.CDTP);
	}
	
	public static WadoRequest createParsedVRTPCompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.VRTP);
	}
	public static WadoRequest createParsedPatch83VFTPCompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.PATCH83_VFTP);
	}
	
	/**
	 * Parse the HTTP request and validate according to VA extensions.
	 *  
	 * @param req
	 * @return
	 * @throws HttpHeaderParseException
	 */
	public static WadoRequest createParsedVACompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.VA);
	}
	
	public static WadoRequest createParsedFederationCompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.FEDERATION);
	}
	
	public static WadoRequest createParsedImageAcceleratorCompliantWadoRequest(HttpServletRequest req)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		return createParsedWadoRequest(req, ComplianceType.ACCELERATOR);
	}

	public static WadoRequest createParsedWadoRequest(HttpServletRequest req, ComplianceType compliance)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		WadoRequest newWadoRequest = new WadoRequest();
		newWadoRequest.parse(req, compliance);
		
		return newWadoRequest;
	}
	
	/**
	 * Generally WadoRequest instances should not be created directly, but from the create() static
	 * methods. 
	 */
	public WadoRequest()
	{
	}
	
	/**
	 * Parse the HTTP request, if wadoValidation is true then assure that the
	 * query is complaint with WADO specification.  If VA extensions are used then
	 * set wadoValidation to false and allow non-compliance
	 *  
	 * @param req
	 * @param wadoValidation
	 * @throws HttpHeaderParseException
	 * @throws WadoQueryComplianceException
	 */
	private void parse(HttpServletRequest req, ComplianceType compliance)
	throws HttpHeaderParseException, WadoQueryComplianceException
	{
		this.compliance = compliance;
		httpHeader = ParsedHttpHeader.parseHttpHeader(req);
		wadoQuery = WadoQuery.createParsedWadoQuery(req, compliance);
		switch(compliance)
		{
		case WADO:
			validateWADO();
			break;
		case VA:
			validateVA();
			break;
		case XCHANGE:
			validateXChange();
			break;
		case FEDERATION:
			validateFederation();
			break;
		case ACCELERATOR:
			validateAccelerator();
			break;
		case NONE:
			validateNone();
			break;
		}
		
	}
	
	/**
	 * Validates that the WADO query is consistent with the HTTP request header.
	 * This enforces rules as follows:
	 * 
	 * 1.) WADO 8.1.5 The Web Client shall provide list of content types it supports 
	 * in the "Accept" field of the GET method. The value of the contentType parameter 
	 * of the request shall be one of the values specified in that field.  This parameter 
	 * is OPTIONAL.
	 * 
	 * 2.) WADO 8.1.6 The Web Client may provide a list of character sets it supports 
	 * in the "Accept-charset" field of the GET method. If this field is present, the 
	 * value of the charset parameter of the request shall be one of the values specified 
	 * in it. 
	 * 
	 * @throws WadoQueryComplianceException
	 */
	public void validateWADO()
	throws WadoQueryComplianceException
	{
		AcceptElementList queryContentList = wadoQuery.getContentTypeList();
		AcceptElementList requestContentList = httpHeader.getAcceptElementList();
		
		if(! queryContentList.isEntirelySubsumedIn(requestContentList))
			throw new WadoQueryComplianceException("WADO query specifies content type that is not part of accept");
	}

	public void validateXChange()
	throws WadoQueryComplianceException
	{
		
	}
	
	private void validateNone()
	throws WadoQueryComplianceException
	{
		
	}

	private void validateVA()
	throws WadoQueryComplianceException
	{
		
	}
	
	private void validateFederation()
	throws WadoQueryComplianceException
	{
		
	}

	private void validateAccelerator()
	throws WadoQueryComplianceException
	{
		
	}

	/**
	 * 
	 * @return
	 */
	public WadoRequestContentTypeEnumeration contentTypeEnumeration()
	{
		WadoRequestContentTypeEnumeration enumerator = new WadoRequestContentTypeEnumeration(
			wadoQuery.getContentTypeList(), httpHeader.getAcceptElementList() );
		
		return enumerator;
	}

	/**
	 * Return the parsed query string, parsed according to WADO semantics
	 * @return
	 */
	public WadoQuery getWadoQuery()
	{
		return wadoQuery;
	}
	
	public ComplianceType getComplianceType()
	{
		return compliance;
	}
	
	public ParsedHttpHeader getParsedHttpHeader()
	{
		return this.httpHeader;
	}
	
	/**
	 * Merge two String arrays into one according to the following rules:
	 * 1.) order by original order, with preference given to the "primary" source array
	 * 2.) the resulting String array is the intersection of the two original string arrays
	 * 
	 * Ex:
	 * primary   = A,C,G,J
	 * secondary = A,B,C,D,I
	 * result    = A,C,G,J,D,I 
	 * 
	 * @param primary
	 * @param secondary
	 * @return
	 */
	private String[] mergeStringArrays(String[] primary, String[] secondary)
	{
		Set mergedSet = new LinkedHashSet();
		for(int primaryIndex=0; primaryIndex < primary.length; ++primaryIndex)
			mergedSet.add(primary[primaryIndex]);

		for(int secondaryIndex=0; secondaryIndex < secondary.length; ++secondaryIndex)
			if(! mergedSet.contains(secondary[secondaryIndex]) )
				mergedSet.add(secondary[secondaryIndex]);

		return (String [])mergedSet.toArray();
	}

	@Override
	public String toString()
	{
		StringBuilder ahnold = new StringBuilder();
		ahnold.append("WadoQuery = [" );
		ahnold.append( this.getWadoQuery().toString() );
		ahnold.append("]");

		return ahnold.toString();
	}
}
