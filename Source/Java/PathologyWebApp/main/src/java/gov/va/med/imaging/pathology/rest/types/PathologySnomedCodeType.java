/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 18, 2012
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
@XmlType(propOrder={"tissueId", "tissueCode", "tissue", "field", "snomedCode", "snomedValue", "snomedId", "etiologyId", "etiologySnomedCode", "etiologySnomedValue"})
public class PathologySnomedCodeType
{
	private String tissueId;
	private String tissueCode; // the code of the tissue (not IEN)
	private String tissue; // name of the tissue	
	private PathologyFieldType field;
	private String snomedCode; // the snomed code (not IEN)
	private String snomedValue;
	private String snomedId; // if this is a morphology		
	private String etiologyId;
	private String etiologySnomedCode;
	private String etiologySnomedValue;
	
	public PathologySnomedCodeType()
	{
		super();
	}

	public PathologySnomedCodeType(String tissueId, String tissueCode,
			String tissue, PathologyFieldType field, String snomedCode,
			String snomedValue, String snomedId, String etiologyId,
			String etiologySnomedCode, String etiologySnomedValue)
	{
		super();
		this.tissueId = tissueId;
		this.tissueCode = tissueCode;
		this.tissue = tissue;
		this.field = field;
		this.snomedCode = snomedCode;
		this.snomedValue = snomedValue;
		this.snomedId = snomedId;
		this.etiologyId = etiologyId;
		this.etiologySnomedCode = etiologySnomedCode;
		this.etiologySnomedValue = etiologySnomedValue;
	}

	@XmlElement(nillable=true)
	public String getTissueId()
	{
		return tissueId;
	}

	public void setTissueId(String tissueId)
	{
		this.tissueId = tissueId;
	}

	@XmlElement(nillable=true)
	public String getTissue()
	{
		return tissue;
	}

	public void setTissue(String tissue)
	{
		this.tissue = tissue;
	}

	@XmlElement(nillable=true)
	public PathologyFieldType getField()
	{
		return field;
	}

	public void setField(PathologyFieldType field)
	{
		this.field = field;
	}

	public String getSnomedValue()
	{
		return snomedValue;
	}

	public void setSnomedValue(String snomedValue)
	{
		this.snomedValue = snomedValue;
	}

	@XmlElement(nillable=true)
	public String getSnomedId()
	{
		return snomedId;
	}

	public void setSnomedId(String snomedId)
	{
		this.snomedId = snomedId;
	}

	@XmlElement(nillable=true)
	public String getEtiologySnomedValue()
	{
		return etiologySnomedValue;
	}

	public void setEtiologySnomedValue(String etiologySnomedValue)
	{
		this.etiologySnomedValue = etiologySnomedValue;
	}

	@XmlElement(nillable=true)
	public String getTissueCode()
	{
		return tissueCode;
	}

	public void setTissueCode(String tissueCode)
	{
		this.tissueCode = tissueCode;
	}

	@XmlElement(nillable=true)
	public String getSnomedCode()
	{
		return snomedCode;
	}

	public void setSnomedCode(String snomedCode)
	{
		this.snomedCode = snomedCode;
	}

	@XmlElement(nillable=true)
	public String getEtiologySnomedCode()
	{
		return etiologySnomedCode;
	}

	public void setEtiologySnomedCode(String etiologySnomedCode)
	{
		this.etiologySnomedCode = etiologySnomedCode;
	}

	@XmlElement(nillable=true)
	public String getEtiologyId()
	{
		return etiologyId;
	}

	public void setEtiologyId(String etiologyId)
	{
		this.etiologyId = etiologyId;
	}
}
