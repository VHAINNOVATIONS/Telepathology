/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 15, 2012
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

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class PathologySpecimenType
{
	private String specimen;
	private String smearPrep;
	private String stain;
	private int numSlides;
	private Date lastStainDate;
	
	public PathologySpecimenType()
	{
		super();
	}

	public PathologySpecimenType(String specimen, String smearPrep,
			String stain, int numSlides, Date lastStainDate)
	{
		super();
		this.specimen = specimen;
		this.smearPrep = smearPrep;
		this.stain = stain;
		this.numSlides = numSlides;
		this.lastStainDate = lastStainDate;
	}

	public String getSpecimen()
	{
		return specimen;
	}

	public void setSpecimen(String specimen)
	{
		this.specimen = specimen;
	}

	public String getSmearPrep()
	{
		return smearPrep;
	}

	public void setSmearPrep(String smearPrep)
	{
		this.smearPrep = smearPrep;
	}

	public String getStain()
	{
		return stain;
	}

	public void setStain(String stain)
	{
		this.stain = stain;
	}

	public int getNumSlides()
	{
		return numSlides;
	}

	public void setNumSlides(int numSlides)
	{
		this.numSlides = numSlides;
	}

	public Date getLastStainDate()
	{
		return lastStainDate;
	}

	public void setLastStainDate(Date lastStainDate)
	{
		this.lastStainDate = lastStainDate;
	}

}
