/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 8, 2012
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

/**
 * This object is the result of adding a cpt code, this object describes the result of adding the CPT code.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PathologyCptCodeResult
{
	private final String cptCode;
	private final boolean successfullyAdded;
	private final String description;
	
	public PathologyCptCodeResult(String cptCode, boolean successfullyAdded,
			String description)
	{
		super();
		this.cptCode = cptCode;
		this.successfullyAdded = successfullyAdded;
		this.description = description;
	}
	
	public String getCptCode()
	{
		return cptCode;
	}
	
	public boolean isSuccessfullyAdded()
	{
		return successfullyAdded;
	}
	
	public String getDescription()
	{
		return description;
	}
}
