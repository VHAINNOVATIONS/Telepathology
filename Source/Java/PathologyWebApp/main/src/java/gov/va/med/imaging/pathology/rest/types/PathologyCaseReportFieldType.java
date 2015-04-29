/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 9, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.pathology.rest.types;

import gov.va.med.imaging.rest.types.RestStringArrayType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyCaseReportFieldType
{
	private String fieldNumber;
	private RestStringArrayType values;
	
	public PathologyCaseReportFieldType()
	{
		super();
	}

	public PathologyCaseReportFieldType(String fieldNumber, RestStringArrayType values)
	{
		super();
		this.fieldNumber = fieldNumber;
		this.values = values;
	}

	public String getFieldNumber()
	{
		return fieldNumber;
	}

	public void setFieldNumber(String fieldNumber)
	{
		this.fieldNumber = fieldNumber;
	}

	public RestStringArrayType getValues()
	{
		return values;
	}

	public void setValues(RestStringArrayType values)
	{
		this.values = values;
	}


}
