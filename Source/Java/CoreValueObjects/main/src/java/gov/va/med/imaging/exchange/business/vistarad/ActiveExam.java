/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 5, 2009
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
package gov.va.med.imaging.exchange.business.vistarad;

import gov.va.med.MockDataGenerationField;
import gov.va.med.StudyURNFactory;
import gov.va.med.URNFactory;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;

/**
 * Represents an active Radiology exam (an exam that is still on a worklist waiting to be read).
 * 
 * @author vhaiswwerfej
 *
 */
public class ActiveExam 
{	
	@MockDataGenerationField(pattern="2[0-9]{4,9}", defaultValue="22222")
	private final String examId; // this should not be a URN!
	
	@MockDataGenerationField(pattern="[1-9][0-9]{9}V[0-9]{4}", defaultValue="6553211234V1234")
	private final String patientIcn;
	
	@MockDataGenerationField(pattern="[1-9][0-9]{2}", defaultValue="660")
	private final String siteNumber;
	
	@MockDataGenerationField(pattern="[A-Za-z0-9 ]{32,128}", defaultValue="I have no clue")
	private String rawValue;

	public ActiveExam(String siteNumber, String examId, String patientIcn) 
	{
		super();
		this.examId = examId;
		this.patientIcn = patientIcn;
		this.siteNumber = siteNumber;
	}

	/**
	 * @return the rawValue
	 */
	public String getRawValue() {
		return rawValue;
	}

	/**
	 * @param rawValue the rawValue to set
	 */
	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}

	/**
	 * @return the examId
	 */
	public String getExamId() {
		return examId;
	}

	/**
	 * @return the patientIcn
	 */
	public String getPatientIcn() {
		return patientIcn;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() {
		return siteNumber;
	}
	
	public StudyURN getStudyUrn()
	throws URNFormatException
	{
		return StudyURNFactory.create(getSiteNumber(), getExamId(), getPatientIcn(), StudyURN.class);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		sb.append('(');
		sb.append( Integer.toHexString(hashCode()) );
		sb.append(')');
		sb.append('\n');
		sb.append('\t');
		try
		{
			sb.append(this.getStudyUrn().toString());
		}
		catch (URNFormatException x)
		{
			sb.append(x.getMessage());
			x.printStackTrace();
		}
		sb.append('\n');

		sb.append('\t');
		sb.append(this.getExamId());
		sb.append('\n');
		
		sb.append('\t');
		sb.append(this.getPatientIcn());
		sb.append('\n');
		
		sb.append('\t');
		sb.append(this.getRawValue());
		sb.append('\n');
		
		sb.append('\t');
		sb.append(this.getSiteNumber());
		sb.append('\n');
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.examId == null) ? 0 : this.examId.hashCode());
		result = prime * result + ((this.patientIcn == null) ? 0 : this.patientIcn.hashCode());
		result = prime * result + ((this.rawValue == null) ? 0 : this.rawValue.hashCode());
		result = prime * result + ((this.siteNumber == null) ? 0 : this.siteNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActiveExam other = (ActiveExam) obj;
		if (this.examId == null)
		{
			if (other.examId != null)
				return false;
		}
		else if (!this.examId.equals(other.examId))
			return false;
		if (this.patientIcn == null)
		{
			if (other.patientIcn != null)
				return false;
		}
		else if (!this.patientIcn.equals(other.patientIcn))
			return false;
		if (this.rawValue == null)
		{
			if (other.rawValue != null)
				return false;
		}
		else if (!this.rawValue.equals(other.rawValue))
			return false;
		if (this.siteNumber == null)
		{
			if (other.siteNumber != null)
				return false;
		}
		else if (!this.siteNumber.equals(other.siteNumber))
			return false;
		return true;
	}
}
