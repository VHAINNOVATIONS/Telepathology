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
import gov.va.med.MockDataGenerationType;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a list of active exams and the raw headers from the responding source
 * @author vhaiswwerfej
 *
 */
@MockDataGenerationType(componentValueType="gov.va.med.imaging.exchange.business.vistarad.ActiveExam")
public class ActiveExams 
extends ArrayList<ActiveExam>
{
	private static final long serialVersionUID = 2518396552683554722L;
	
	@MockDataGenerationField(defaultValue="RawHeader1", pattern="RawHeader1 [A-Z][a-z]{32}")
	private final String rawHeader1;
	@MockDataGenerationField(defaultValue="RawHeader2", pattern="RawHeader2 [A-Z][a-z]{32}")
	private final String rawHeader2;
	@MockDataGenerationField(defaultValue="660", pattern="[1-9][0-9]{2}")
	private final String siteNumber;

	public ActiveExams(String siteNumber, String rawHeader1, String rawHeader2)
	{
		super();
		this.siteNumber = siteNumber;
		this.rawHeader1 = rawHeader1;
		this.rawHeader2 = rawHeader2;
	}

	/**
	 * @return the rawHeader1
	 */
	public String getRawHeader1() {
		return rawHeader1;
	}

	/**
	 * @return the rawHeader2
	 */
	public String getRawHeader2() {
		return rawHeader2;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() {
		return siteNumber;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("(0x");
		sb.append( Integer.toHexString(this.hashCode()) );
		sb.append(')');
		sb.append('\n');
		sb.append('\t');
		sb.append("RawHeader2 :");
		sb.append(this.getRawHeader1());
		sb.append('\n');
		sb.append('\t');
		sb.append("RawHeader2 :");
		sb.append(this.getRawHeader2());
		sb.append('\n');
		sb.append('\t');
		sb.append("'" + this.size() + "' worklist items");
		sb.append('\n');
		if(size() > 0)
			for(ActiveExam activeExam : this)
			{
				sb.append('\t');
				sb.append(activeExam == null ? "<null>" : activeExam.toString());
				sb.append('\n');
			}
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.rawHeader1 == null) ? 0 : this.rawHeader1.hashCode());
		result = prime * result + ((this.rawHeader2 == null) ? 0 : this.rawHeader2.hashCode());
		result = prime * result + ((this.siteNumber == null) ? 0 : this.siteNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ActiveExams other = (ActiveExams) obj;
		if (this.rawHeader1 == null)
		{
			if (other.rawHeader1 != null)
				return false;
		}
		else if (!this.rawHeader1.equals(other.rawHeader1))
			return false;
		if (this.rawHeader2 == null)
		{
			if (other.rawHeader2 != null)
				return false;
		}
		else if (!this.rawHeader2.equals(other.rawHeader2))
			return false;
		if (this.siteNumber == null)
		{
			if (other.siteNumber != null)
				return false;
		}
		else if (!this.siteNumber.equals(other.siteNumber))
			return false;
		
		Iterator<ActiveExam> thisIter = iterator();
		Iterator<ActiveExam> otherIter = other.iterator();
		while(thisIter.hasNext() && otherIter.hasNext())
			if(! thisIter.next().equals(otherIter.next()))
				return false;
		if(thisIter.hasNext() || otherIter.hasNext())
			return false;
		
		return true;
	}
}
