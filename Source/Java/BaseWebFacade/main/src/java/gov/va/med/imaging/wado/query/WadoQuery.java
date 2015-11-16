/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gov.va.med.imaging.wado.query;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.LowerCaseHashMap;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.http.AcceptElementList;
import gov.va.med.imaging.http.exceptions.HttpHeaderParseException;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;
import gov.va.med.imaging.wado.query.exceptions.*;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chris Beckey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WadoQuery 
{
	public final static String requestQueryKey = "requestType";	// required
	public final static String requestQueryRequiredValue = "WADO";		// required value of requestType
	public final static String requestQueryXChangeValue = "XCHANGE";	// required value of requestType for XChange requests
	public final static String requestQueryVAValue = "VA";				// required value of requestType for VA requests
	public final static String studyQueryKey = "studyUID";		// required
	public final static String seriesQueryKey = "seriesUID";	// required
	public final static String objectQueryKey = "objectUID";	// required
	
	public final static String contentQueryKey = "contentType";	// optional, comma seperated list of MIME type
	public final static String imageQualityKey = "imageQuality";// optional if 'contentType' is not 'application/dicom', 1..100, 99&100 is best quality, defaults to 90
//	public final static String defaultImageQualityValue = "90"; // force compression, set DIAG cannonical
	public final static String charsetQueryKey = "charset";		// optional, comma seperated list
	
	public final static String anonymizeQueryKey = "anonymize";	// optional 
	public final static String anonymizeQueryRequiredValue = "yes";	// required value of anonymize
	
	public final static String annotationQueryKey = "annotation";	// optional
	public final static String[] annotationQueryPermissableValues = {"patient", "technique"};	// optional

	// VA extensions to WADO
	public final static String patientQueryKey = "patientUID";	// non-standard extension
	public final static String patientGuidQueryKey = "patientGUID";	// non-standard extension
	public final static String studyGuidQueryKey = "studyGUID";		// non-standard extension
	public final static String seriesGuidQueryKey = "seriesGUID";	// non-standard extension
	public final static String instanceGuidQueryKey = "instanceGUID";	// non-standard extension
	public final static String instanceUrnQueryKey = "imageurn";		// non-standard extension, used by DOD image exchange
																		// may also be used to specify an instance GUID
	// determines if the request is for a TXT file (from clinical display)
	public final static String isTxtFileQueryKey = "textFile";
	
	// query parameter string to determine if the image should come from the cache - only used for VistARad
	private final static String isAllowedFromCacheQueryKey = "fromCache";
	
	private String requestTypeValue = null;
	private String patientValue = null;
	private String studyValue = null;
	private String seriesValue = null;
	private String objectValue = null;
	private GUID patientGuidValue = null;
	private GUID studyGuidValue = null;
	private GUID seriesGuidValue = null;
	private GUID instanceGuidValue = null;
	private ImageURN instanceUrnValue = null;
	private GlobalArtifactIdentifier globalArtifactIdentifier = null;
	private AcceptElementList contentTypeList = null;
	private List<ImageFormat> contentTypeWithSubTypeList = null;
	private String imageQualityValue = null;
	private String[] charsetValue = null;
	private boolean anonymize = false;
	private String annotationValue = null;
	private boolean getTxtFile = false;
	private boolean allowedFromCache = true; // determines if the image can come from the cache (for VistARad)

	/**
	 * 
	 * @param request
	 * @return
	 */
	public static WadoQuery createParsedNoncompliantWadoQuery(HttpServletRequest request)
	throws WadoQueryComplianceException, HttpHeaderParseException
	{
		return createParsedWadoQuery(request, ComplianceType.NONE);
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws WadoQueryComplianceException
	 */
	public static WadoQuery createParsedCompliantWadoQuery(HttpServletRequest request)
	throws WadoQueryComplianceException, HttpHeaderParseException
	{
		return createParsedWadoQuery(request, ComplianceType.WADO);
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws WadoQueryComplianceException
	 */
	public static WadoQuery createParsedVACompliantWadoQuery(HttpServletRequest request)
	throws WadoQueryComplianceException, HttpHeaderParseException
	{
		return createParsedWadoQuery(request, ComplianceType.VA);
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws WadoQueryComplianceException
	 */
	public static WadoQuery createParsedXChangeCompliantWadoQuery(HttpServletRequest request)
	throws WadoQueryComplianceException, HttpHeaderParseException
	{
		return createParsedWadoQuery(request, ComplianceType.XCHANGE);
	}
	
	public static WadoQuery createParsedWadoQuery(HttpServletRequest request, ComplianceType compliance)
	throws WadoQueryComplianceException, HttpHeaderParseException
	{
		WadoQuery newWadoQuery = new WadoQuery();
		newWadoQuery.parse(request, compliance);
		
		return newWadoQuery;
	}
	
	public static WadoQuery createParsedExchangedQuery(HttpServletRequest request)
	throws WadoQueryComplianceException, HttpHeaderParseException
	{
		return createParsedWadoQuery(request, ComplianceType.FEDERATION);
	}
	
	
	protected WadoQuery()
	{
		
	}

	/**
	 * Parse an HTTP query string into WADO semantics.
	 * If enforceWADOCompliance is true then this method will throw exceptions
	 * if the WADO specification is not met by the query string.
	 * If enforceVACompliance is true then this method will throw exceptions
	 * if the VA extensions to the WADO specification is not met by the query string.
	 * 
	 * 
	 * @param request
	 * @param enforceWADOCompliance
	 * @param enforceVACompliance
	 * @throws WadoQueryComplianceException
	 * @throws HttpHeaderParseException
	 */
	private void parse(HttpServletRequest request, ComplianceType complianceType)
	throws WadoQueryComplianceException, HttpHeaderParseException 
	{
		Map<String, String[]> parameterMap = request.getParameterMap();
		// clone the map with the key values all lower cased, so that our stuff will appear case-insensitive
		LowerCaseHashMap<String, String[]> lowerCasedMap = new LowerCaseHashMap<String, String[]>(parameterMap);
		
		// single instance String parameters, standard WADO
		if(parameterMap.containsKey(requestQueryKey))
			requestTypeValue = ((String[])parameterMap.get(requestQueryKey))[0];
		if(parameterMap.containsKey(studyQueryKey))
			studyValue = ((String[])parameterMap.get(studyQueryKey))[0];
		if(parameterMap.containsKey(seriesQueryKey))
			seriesValue = ((String[])parameterMap.get(seriesQueryKey))[0];
		if(parameterMap.containsKey(objectQueryKey))
			objectValue = ((String[])parameterMap.get(objectQueryKey))[0];
		if(parameterMap.containsKey(imageQualityKey))
			imageQualityValue = ((String[])parameterMap.get(imageQualityKey))[0];
		
		if(parameterMap.containsKey(annotationQueryKey))
			annotationValue = ((String[])parameterMap.get(annotationQueryKey))[0];

		// single instance String parameters, VA extensions
		if(parameterMap.containsKey(patientQueryKey))
			patientValue = ((String[])parameterMap.get(patientQueryKey))[0];
		if(parameterMap.containsKey(patientGuidQueryKey))
			patientGuidValue = new GUID( ((String[])parameterMap.get(patientGuidQueryKey))[0] );
		if(parameterMap.containsKey(studyGuidQueryKey))
			studyGuidValue = new GUID( ((String[])parameterMap.get(studyGuidQueryKey))[0] );
		if(parameterMap.containsKey(seriesGuidQueryKey))
			seriesGuidValue = new GUID( ((String[])parameterMap.get(seriesGuidQueryKey))[0] );
		if(parameterMap.containsKey(instanceGuidQueryKey))
			instanceGuidValue = new GUID( ((String[])parameterMap.get(instanceGuidQueryKey))[0] );
		if(lowerCasedMap.containsKey(instanceUrnQueryKey))
		{
			String imageUrnAsString = null;
			try
			{
				SERIALIZATION_FORMAT serializationFormat = SERIALIZATION_FORMAT.NATIVE;
				switch(complianceType)
				{
					case CDTP:
					case VRTP:
						serializationFormat = SERIALIZATION_FORMAT.CDTP;
						break;
					case PATCH83_VFTP:
						serializationFormat = SERIALIZATION_FORMAT.PATCH83_VFTP;
						break;
					case FEDERATION:
						//serializationFormat = SERIALIZATION_FORMAT.VFTP;
						//serializationFormat = SERIALIZATION_FORMAT.CDTP;
						serializationFormat = SERIALIZATION_FORMAT.RAW;
						break;
				}				
				
				imageUrnAsString = ((String[])lowerCasedMap.get(instanceUrnQueryKey))[0];
				URN untypedUrn = URNFactory.create( imageUrnAsString, serializationFormat );
				if( ImageURN.class.isAssignableFrom(untypedUrn.getClass()) )
					instanceUrnValue = (ImageURN)untypedUrn;
				else if(GlobalArtifactIdentifier.class.isAssignableFrom(untypedUrn.getClass()))
				{
					instanceUrnValue = null;
					globalArtifactIdentifier = (GlobalArtifactIdentifier)untypedUrn;
				}
				else
					throw new URNFormatException("The image URN " + imageUrnAsString + " is a valid URN but is not a valid ImageURN.");
			} 
			catch (URNFormatException e)
			{
				throw new HttpHeaderParseException("URN '" + imageUrnAsString + "' is not in valid URN form.", e);
			} 
		}
		if(parameterMap.containsKey(isTxtFileQueryKey))
		{
			String txtFileString = ((String[])parameterMap.get(isTxtFileQueryKey))[0];
			if(txtFileString != null)
			{
				getTxtFile = Boolean.parseBoolean(txtFileString);
			}
		}
		if(parameterMap.containsKey(isAllowedFromCacheQueryKey))
		{
			String allowedFromCacheString = ((String[])parameterMap.get(isAllowedFromCacheQueryKey))[0];
			if(allowedFromCacheString != null)
			{
				allowedFromCache = Boolean.parseBoolean(allowedFromCacheString);
			}
		}
		

		// String array parameters;
		contentTypeList = AcceptElementList.parseAcceptElementList( (String[])parameterMap.get(contentQueryKey) );
		charsetValue = (String[])parameterMap.get(charsetQueryKey);
		
		if(request.getHeader(TransactionContextHttpHeaders.httpHeaderContentTypeWithSubType) != null)
		{
			contentTypeWithSubTypeList = parseImageFormatList(request.getHeader(TransactionContextHttpHeaders.httpHeaderContentTypeWithSubType));
		}
		else
		{
			contentTypeWithSubTypeList = null;
		}
				
		
		// boolean parameters
		String[] anonymizeStringValue = (String[])parameterMap.get(anonymizeQueryKey);
		anonymize = (anonymizeStringValue != null && anonymizeQueryRequiredValue.equals(anonymizeStringValue[0]));
		
		// if enforceCompliance is true then throw exceptions if this instance
		// is not compliant with the specification
		switch(complianceType)
		{
		case WADO:
			evaluateWADORequiredKeyCompliance();
			evaluateInvalidValueCompliance( anonymizeStringValue == null ? null : anonymizeStringValue[0] );
			break;
		case VA:
			evaluateVARequiredKeyCompliance();
			evaluateInvalidValueCompliance( anonymizeStringValue == null ? null : anonymizeStringValue[0] );
			break;
		case XCHANGE:
			evaluateXChangeRequiredKeyCompliance();
			break;
		case FEDERATION:
		case PATCH83_VFTP:
			evaluateFederationRequiredKeyCompliance();
			break;
		case ACCELERATOR:
			evaluateAcceleratorRequiredKeyCompliance();
			break;
		case CDTP:
		case VRTP:		
			//TODO: generate actual validation rules for each of these types
			evaluateXChangeRequiredKeyCompliance();
			break;
		case NONE:
			break;
		}
	}
	
	private List<ImageFormat> parseImageFormatList(String imageFormatList)
	{
		List<ImageFormat> formats = new ArrayList<ImageFormat>();
		for(StringTokenizer commaTokenizer = new StringTokenizer(imageFormatList, ","); commaTokenizer.hasMoreTokens();)
		{
			formats.add(ImageFormat.valueOfMimeType(commaTokenizer.nextToken().trim()));
		}
		return formats;
	}

	/**
	 * Evaluates this instance of the WADO Query and 
	 * throws an exception if not in compliance.  This method may be called 
	 * externally, or may be called in the constructors of this class
	 * if the enforceCompliance flag is true (the default).
	 * 
	 * @throws WADOQueryComplianceException
	 */
	public void evaluateWADORequiredKeyCompliance()
	throws WadoQueryRequiredKeyMissingException 
	{
		StringBuffer sbRequiredKeysMissing = new StringBuffer();
		
		// evaluate required keys		
		if(requestTypeValue == null)
			sbRequiredKeysMissing.append( (sbRequiredKeysMissing.length()>0?",":"") + requestQueryKey);
		if(studyValue == null)
			sbRequiredKeysMissing.append( (sbRequiredKeysMissing.length()>0?",":"") + studyQueryKey);
		if(seriesValue == null)
			sbRequiredKeysMissing.append( (sbRequiredKeysMissing.length()>0?",":"") + seriesQueryKey);
		if(objectValue == null)
			sbRequiredKeysMissing.append( (sbRequiredKeysMissing.length()>0?",":"") + objectQueryKey);

		if(sbRequiredKeysMissing.length() > 0)
			throw new WadoQueryRequiredKeyMissingException(sbRequiredKeysMissing.toString());
	}

	/**
	 * Evaluates this instance of the WADO Query and 
	 * throws an exception if not in compliance with the VA Specification.
	 * This method may be called externally, or may be called in the factory of this class
	 * if the enforceVACompliance flag is true (the default).
	 * 
	 * @throws WADOQueryComplianceException
	 */
	public void evaluateVARequiredKeyCompliance()
	throws WadoQueryRequiredKeyMissingException 
	{
		StringBuffer sbRequiredKeysMissing = new StringBuffer();
		
		// evaluate required keys		
		if(requestTypeValue == null)
			sbRequiredKeysMissing.append( (sbRequiredKeysMissing.length()>0?",":"") + requestQueryKey);

		if(sbRequiredKeysMissing.length() > 0)
			throw new WadoQueryRequiredKeyMissingException(sbRequiredKeysMissing.toString());
	}
	
	private void evaluateXChangeRequiredKeyCompliance()
	throws WadoQueryRequiredKeyMissingException 
	{
		// if image quality does not exist then create it with default cannonical value 
		if(imageQualityValue == null)
//			imageQualityValue = defaultImageQualityValue;
			imageQualityValue = Integer.toString(ImageQuality.DIAGNOSTIC.getCanonical()); // "90"
	}
	
	private void evaluateFederationRequiredKeyCompliance()
	throws WadoQueryRequiredKeyMissingException
	{
		if((instanceUrnValue == null) && (globalArtifactIdentifier == null))
			throw new WadoQueryRequiredKeyMissingException("Missing image URN or GlobalArtifactIdentifier");
		
		if((!isGetTxtFile()) && (imageQualityValue == null))
			throw new WadoQueryRequiredKeyMissingException("Missing image quality");
	}
	
	private void evaluateAcceleratorRequiredKeyCompliance()
	throws WadoQueryComplianceException
	{
		
	}

	/**
	 * 
	 * @param anonymizeStringValue
	 */
	public void evaluateInvalidValueCompliance(String anonymizeStringValue)
	throws WadoQueryInvalidValueException
	{
		StringBuffer sbInvalidValues = new StringBuffer();
		
		if(! requestQueryRequiredValue.equalsIgnoreCase(requestTypeValue) )
		{
			if(sbInvalidValues.length() > 0) sbInvalidValues.append(".");
			sbInvalidValues.append("[" + requestQueryKey + "] must be [" + requestQueryRequiredValue + "]" );
		}
		
		if(anonymizeStringValue != null && !anonymizeQueryRequiredValue.equals(anonymizeStringValue))
		{
			if(sbInvalidValues.length() > 0) sbInvalidValues.append(".");
			sbInvalidValues.append("[" + anonymizeQueryKey + "] must be null or [" + anonymizeQueryRequiredValue + "]" );
		}
		
		if(annotationValue != null)
		{
			boolean annotationPermissableValue = false;
			for( 
				int index=0; 
				index < annotationQueryPermissableValues.length && ! annotationPermissableValue; 
				++index )
			{
				if( annotationQueryPermissableValues[index].equalsIgnoreCase(annotationValue) )
					annotationPermissableValue = true;
			}
			
			if(!annotationPermissableValue)
			{
				if(sbInvalidValues.length() > 0) sbInvalidValues.append(".");
				sbInvalidValues.append("[" + annotationQueryKey + "] must be one of [");
				for( int index=0; index < annotationQueryPermissableValues.length; ++index )
					sbInvalidValues.append( annotationQueryPermissableValues[index] );
				sbInvalidValues.append( "]" );
			}
		}
		
		if(sbInvalidValues.length() > 0)
			throw new WadoQueryInvalidValueException(sbInvalidValues.toString());

		return;	
	}
	
	/**
	 * If the WADO anonymize parameters is set in the request then
	 * this returns true.
	 * 
	 * @return
	 */
	public boolean isAnonymize() 
	{
		return anonymize;
	}

	/**
	 * Return the desired charset.
	 * 
	 * @return
	 */
	public String[] getCharsetValue() 
	{
		return charsetValue;
	}

	/**
	 * Return the client agent acceptable content type list in the
	 * order of preference.  See the WADO spec and the HTTP spec for 
	 * how the ordering is defined.
	 * 
	 * @return
	 */
	public AcceptElementList getContentTypeList() 
	{
		return contentTypeList;
	}	

	/**
	 * @return the contentTypeWithSubTypeList
	 */
	public List<ImageFormat> getContentTypeWithSubTypeList() {
		return contentTypeWithSubTypeList;
	}

	/**
	 * Get the value of the requested Object UID.
	 * 
	 * @return
	 */
	public String getObjectValue() 
	{
		return objectValue;
	}

	/**
	 * Get the value of the requested patient UID.
	 * 
	 * @return
	 */
	public String getPatientValue() 
	{
		return patientValue;
	}

	/**
	 * Get the value of the request type.
	 * 
	 * @return
	 */
	public String getRequestTypeValue() 
	{
		return requestTypeValue;
	}

	/**
	 * Get the value of the series UID
	 * 
	 * @return
	 */
	public String getSeriesValue() 
	{
		return seriesValue;
	}

	/**
	 * Get the value of the requested Study UID
	 * 
	 * @return
	 */
	public String getStudyValue() 
	{
		return studyValue;
	}

	/**
	 * Get the value of the patient GUID as a String.
	 * Returns null if none specified
	 * Note: this is a VA extension and is not standard WADO
	 * 
	 * @return
	 */
	public GUID getPatientGuid()
	{
		return patientGuidValue;
	}
	
	/**
	 * Get the value of the series GUID as a String.
	 * Returns null if none specified
	 * Note: this is a VA extension and is not standard WADO
	 * 
	 * @return
	 */
	public GUID getSeriesGuid()
	{
		return seriesGuidValue;
	}
	
	/**
	 * Get the value of the study GUID as a String.
	 * Returns null if none specified
	 * Note: this is a VA extension and is not standard WADO
	 * 
	 * @return
	 */
	public GUID getStudyGuid()
	{
		return studyGuidValue;
	}

	/**
	 * Get the value of the object GUID as a String.
	 * Returns null if none specified
	 * Note: this is a VA extension and is not standard WADO
	 * 
	 * @return
	 */
	public GUID getInstanceGuid()
	{
		return instanceGuidValue;
	}
	
	/**
	 * Get the value of the instance Image URN.
	 * Returns null if none specified
	 * Note: this is a VA extension and is not standard WADO
	 * 
	 * @return
	 */
	public ImageURN getInstanceUrn()
	{
		return instanceUrnValue;
	}
	
	public GlobalArtifactIdentifier getGlobalArtifactIdentifier()
	{
		return globalArtifactIdentifier;
	}

	/**
	 * 
	 * @return
	 */
	public String getImageQualityValue()
	{
		return imageQualityValue;
	}	
	
	public boolean isGetTxtFile() {
		return getTxtFile;
	}

	/**
	 * Returns true if the image may be from the cache, false otherwise
	 * 
	 * @return the allowedFromCache
	 */
	public boolean isAllowedFromCache()
	{
		return allowedFromCache;
	}

	@Override
	public String toString()
	{
		StringBuilder ahnold = new StringBuilder();
		ahnold.append("InstanceGuid=[" );
		ahnold.append( this.getInstanceGuid() );
		ahnold.append("]");

		ahnold.append("  InstanceUrn=[" );
		ahnold.append( this.getInstanceUrn() );
		ahnold.append("]");

		ahnold.append("  PatientGuid=[" );
		ahnold.append( this.getPatientGuid() );
		ahnold.append("]");
		
		ahnold.append("  PatientValue=[");
		ahnold.append( this.getPatientValue());
		ahnold.append("]");
		
		ahnold.append("  RequestTypeValue=[");
		ahnold.append( this.getRequestTypeValue());
		ahnold.append("]");
		
		ahnold.append("  CharsetValue=[");
		ahnold.append( this.getCharsetValue());
		ahnold.append("]");
		
		return ahnold.toString();
	}
	
}
