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
package gov.va.med.imaging.pathology;

import gov.va.med.imaging.pathology.enums.PathologyField;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologySnomedCode
{
	private final String tissueId; // the ID of the tissue within the particular case
	private final String tissue; // name of the tissue
	private final String tissueCode; // the code of the tissue (not IEN)
	private final PathologyField field;
	private final String snomedValue;
	private final String snomedCode; // the snomed code (not IEN)
	private final String snomedId; 
	private final String etiologyId; // if this is a morphology
	private final String etiologySnomedCode;
	private final String etiologySnomedValue;
	
	public static PathologySnomedCode createMorphologySnomedCode(String tissueId, String tissueCode, 
			String tissue, String snomedId, String snomedCode, String snomedValue)
	{
		return createMorphologySnomedCode(tissueId, tissueCode, tissue, snomedId, snomedCode, snomedValue, null, null, null);
	}
	
	public static PathologySnomedCode createMorphologySnomedCode(String tissueId, String tissueCode, String tissue, 
			String snomedId, String snomedCode, String snomedValue, String etiologyId, String etiologySnomedCode, String etiologySnomedValue)
	{
		return new PathologySnomedCode(tissueId, tissueCode, tissue, snomedId, PathologyField.morphology, snomedCode,
				snomedValue, etiologyId, etiologySnomedCode, etiologySnomedValue);
	}
	
	public static PathologySnomedCode createSnomedCode(String tissueId, String tissueCode, String tissue, 
			String snomedId, PathologyField field, String snomedCode, String snomedValue)
	{
		return new PathologySnomedCode(tissueId, tissueCode, tissue, snomedId, field, snomedCode, snomedValue, null, null, null);
	}
	
	public static PathologySnomedCode createTissue(String id, String tissueCode, String tissue)
	{
		return new PathologySnomedCode(id, tissueCode, tissue, null, null, null, null, null, null, null);
	}
	
	private PathologySnomedCode(String tissueId, String tissueCode, String tissue, String snomedId,
			PathologyField field, String snomedCode, String snomedValue, String etiologyId, String etiologySnomedCode, String etiologySnomedValue)
	{
		super();
		this.tissueId = tissueId;
		this.tissue = tissue;
		this.snomedId = snomedId;
		this.field = field;
		this.snomedValue = snomedValue;
		this.etiologySnomedValue = etiologySnomedValue;
		this.tissueCode = tissueCode;
		this.snomedCode = snomedCode;
		this.etiologySnomedCode = etiologySnomedCode;
		this.etiologyId = etiologyId;
	}

	public String getTissueId()
	{
		return tissueId;
	}

	public String getTissue()
	{
		return tissue;
	}

	public String getSnomedId()
	{
		return snomedId;
	}

	public PathologyField getField()
	{
		return field;
	}

	public String getSnomedValue()
	{
		return snomedValue;
	}

	public String getEtiologySnomedValue()
	{
		return etiologySnomedValue;
	}
	
	public boolean isMorphology()
	{
		return field == PathologyField.morphology;
	}
	
	public boolean hasEtiology()
	{
		return etiologySnomedValue != null;
	}
	
	public boolean hasSnomedValue()
	{
		return snomedValue != null;
	}

	public String getTissueCode()
	{
		return tissueCode;
	}

	public String getSnomedCode()
	{
		return snomedCode;
	}

	public String getEtiologySnomedCode()
	{
		return etiologySnomedCode;
	}

	public String getEtiologyId()
	{
		return etiologyId;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(tissue);
		sb.append(" [" + tissueId + "," + tissueCode + "]");
		
		if(field != null && snomedValue != null)
		{
			sb.append(", ");
			sb.append(field.name().toUpperCase());
			sb.append(":");
			sb.append(snomedValue);
			sb.append(" [" + snomedCode + "]");
			
			if(snomedId != null && snomedId.length() > 0)
			{
				sb.append(" [" + snomedId + "]");
			}
		}
		
		
		
		if(etiologySnomedValue != null)
		{
			sb.append(", ETIOLOGY [" + etiologyId + "]:");
			sb.append(etiologySnomedValue);
			sb.append(" [" + etiologySnomedCode + "]");
		}		

		return sb.toString();
	}

}
