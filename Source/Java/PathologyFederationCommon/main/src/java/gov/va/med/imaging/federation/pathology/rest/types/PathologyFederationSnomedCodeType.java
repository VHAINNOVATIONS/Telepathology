/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 19, 2012
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
package gov.va.med.imaging.federation.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologyFederationSnomedCodeType
{

	private String tissueId;
	private String tissueCode;
	private String tissue; // name of the tissue
	private PathologyFederationFieldType field;
	private String snomedCode;
	private String snomedValue;
	private String snomedId;
	private String etiologyId;
	private String etiologySnomedCode;
	private String etiologySnomedValue;
	
	public PathologyFederationSnomedCodeType()
	{
		super();
	}

	public PathologyFederationSnomedCodeType(String tissueId,
			String tissueCode, String tissue,
			PathologyFederationFieldType field, String snomedCode,
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
		this.etiologySnomedCode = etiologySnomedCode;
		this.etiologySnomedValue = etiologySnomedValue;
		this.etiologyId = etiologyId;
	}

	public String getTissueId()
	{
		return tissueId;
	}

	public void setTissueId(String tissueId)
	{
		this.tissueId = tissueId;
	}

	public String getTissue()
	{
		return tissue;
	}

	public void setTissue(String tissue)
	{
		this.tissue = tissue;
	}

	public PathologyFederationFieldType getField()
	{
		return field;
	}

	public void setField(PathologyFederationFieldType field)
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

	public String getSnomedId()
	{
		return snomedId;
	}

	public void setSnomedId(String snomedId)
	{
		this.snomedId = snomedId;
	}

	public String getEtiologySnomedValue()
	{
		return etiologySnomedValue;
	}

	public void setEtiologySnomedValue(String etiologySnomedValue)
	{
		this.etiologySnomedValue = etiologySnomedValue;
	}

	public String getTissueCode()
	{
		return tissueCode;
	}

	public void setTissueCode(String tissueCode)
	{
		this.tissueCode = tissueCode;
	}

	public String getSnomedCode()
	{
		return snomedCode;
	}

	public void setSnomedCode(String snomedCode)
	{
		this.snomedCode = snomedCode;
	}

	public String getEtiologySnomedCode()
	{
		return etiologySnomedCode;
	}

	public void setEtiologySnomedCode(String etiologySnomedCode)
	{
		this.etiologySnomedCode = etiologySnomedCode;
	}

	public String getEtiologyId()
	{
		return etiologyId;
	}

	public void setEtiologyId(String etiologyId)
	{
		this.etiologyId = etiologyId;
	}
	
}
