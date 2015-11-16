/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 4, 2008
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
package gov.va.med.imaging.federation.proxy;

import gov.va.med.imaging.federation.webservices.types.FederationStudyType;
import gov.va.med.imaging.proxy.AbstractResult;

import java.io.Serializable;

/**
 * @author VHAISWWERFEJ
 *
 */
public class StudyResult 
extends AbstractResult 
implements Serializable 
{
	private static final long serialVersionUID = 6043365898190228721L;
	
	private FederationStudyType[] studies;
	
	public StudyResult(String transactionId, FederationStudyType []studies)
	{
		super(transactionId);
		this.studies = studies;
	}

	public FederationStudyType[] getStudies() {
		return studies;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.AbstractResult#equivalent(java.lang.Object)
	 */
	@Override
	public boolean equivalent(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StudyResult that = (StudyResult)obj;
		
		// if the studies list is null in both instances then the results are equivalent
		if(this.studies == null && that.studies == null)
			return true;

		// if the studies list is null in one instances but not the other then the results are not equivalent
		if(this.studies == null && that.studies != null ||
				this.studies != null && that.studies == null)
			return false;

		// if the number of studies differ then the results are not equivalent
		if(this.studies.length != that.studies.length)
			return false;
		
		// at this point we know that both instances have studies and both instances have the
		// same number of studies
		// equivalence is determined on the same studies, the same study IDs and the studies
		// with the same IDs are .equals()
		for(FederationStudyType study : this.studies)
		{
			boolean studyMatchFound = false;
			for(FederationStudyType thatStudy : that.studies)
			{
				// if the study IDs match then the rest of the studyType must match
				if( study.getStudyId().equals(thatStudy.getStudyId()) )
				{
					studyMatchFound = true;
					if(! study.equals(thatStudy))
						return false;
					break;
				}
			}
			// no study with the same study ID was found in the 'that' results
			if(!studyMatchFound)
				return false;
		}
		
		return true;
	}

}
