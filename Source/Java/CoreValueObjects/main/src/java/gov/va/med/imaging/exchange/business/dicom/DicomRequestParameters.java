/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

*/
package gov.va.med.imaging.exchange.business.dicom;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Core Value Object class that encapsulates a HashMap.  The HashMap contains a list 
 * of name-value pairs from an incoming DICOM Dimse Message.  This includes a C-Find
 * Request and a C-Move Request.  This information is passed to the Data Source to
 * determine all matching items to create Responses to the Request.
 * 
 * The RequestMappingSet is used later to help build proper Response message(s) based
 * on the Request message.  More specifically, it is used to pass the same DICOM attributes
 * that exist in the Request message.
 * 
 * @author vhaiswlouthj
 *
 */
public class DicomRequestParameters extends HashMap<String, String>{

	private static final long serialVersionUID = -7715943853356091547L;
	private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
	private HashSet<DicomMap> requestMappingSet = null;

	public String put(String key, String value)
	{
		String cleanedKey = cleanControlCharacters(key); 
		String cleanedValue = cleanControlCharacters(value); 
		super.put(cleanedKey, cleanedValue);
		return cleanedValue;
	}
	
	// This code replaces the loop over each char found in the original code...
	private String cleanControlCharacters(String stringToClean)
	{
        return stringToClean.replaceAll(CONTROL_CHARACTER_REGEX, "");
	}

	/**
	 * @return the requestMappingSet
	 */
	public HashSet<DicomMap> getRequestMappingSet() {
		return requestMappingSet;
	}

	/**
	 * @param requestMappingSet the requestMappingSet to set
	 */
	public void setRequestMappingSet(HashSet<DicomMap> requestMappingSet) {
		this.requestMappingSet = requestMappingSet;
	}
	
	
}
