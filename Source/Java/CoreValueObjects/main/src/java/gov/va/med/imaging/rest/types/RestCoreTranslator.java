/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 29, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.rest.types;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public class RestCoreTranslator
{

	public static boolean translate(RestBooleanReturnType returnType)
	{
		return returnType.isResult();
	}
	
	public static RestBooleanReturnType translate(boolean value)
	{
		return new RestBooleanReturnType(value);
	}
	
	public static RestStringType translate(String value)
	{
		return new RestStringType(value);
	}
	
	public static String translate(RestStringType returnType)
	{
		if(returnType == null)
			return null;
		return returnType.getValue();
	}
	
	public static RestIntegerType translate(int value)
	{
		return new RestIntegerType(value);
	}
	
	public static int translate(RestIntegerType value)
	{
		if(value == null)
			return Integer.MIN_VALUE;
		return value.getValue();
	}
	
	public static List<String> translate(RestStringArrayType value)
	{
		if(value == null)
			return null;
		List<String> result = new ArrayList<String>();
		String [] values = value.getValue();
		if(values != null)
		{
			for(String v : values)
			{
				result.add(v);
			}
		}
		
		return result;
	}
	
	public static RestStringArrayType translateStrings(List<String> values)
	{
		if(values == null)
			return null;
		
		return new RestStringArrayType(values.toArray(new String[values.size()]));
	}
	
	public static RestPatientIdentifierType translate(PatientIdentifier patientIdentifier)
	{
		return new RestPatientIdentifierType(patientIdentifier.getValue(),
				patientIdentifier.getPatientIdentifierType().name());
	}
	
	public static PatientIdentifier translate(RestPatientIdentifierType patientIdentifier)
	{
		for(PatientIdentifierType patientIdentifierType : PatientIdentifierType.values())
		{
			if(patientIdentifierType.name().equals(patientIdentifier.getType()))
			{
				return new PatientIdentifier(patientIdentifier.getValue(), patientIdentifierType);
			}
		}
		return new PatientIdentifier(patientIdentifier.getValue(), null);
	}
}
