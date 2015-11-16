/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 8, 2009
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

import gov.va.med.RoutingToken;
import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An ExamSite is a site a patient has been seen at that contains a status indicating if the
 * exams from that site have been loaded. The site is necessary to keep for a patient even
 * if the VIX was unable to load the exams. The ExamSiteStatus indicates if the data was
 * loaded properly.
 * 
 * @author vhaiswwerfej
 *
 */
public class ExamSite
extends ArrayList<Exam>
implements Serializable
{
	private static final long serialVersionUID = -5105548162632297416L;
	
	private final RoutingToken routingToken; // this might change to an ArtifactRepository
	private final ArtifactResultStatus artifactResultStatus;
	private List<ArtifactResultError> artifactResultErrors = null; // there are errors from the data source
	private final String siteName;
	
	public ExamSite(RoutingToken routingToken, ArtifactResultStatus artifactResultStatus, String siteName)
	{
		this.routingToken = routingToken;
		this.artifactResultStatus = artifactResultStatus;
		this.siteName = siteName;
	}
	
	public List<ArtifactResultError> getArtifactResultErrors()
	{
		return artifactResultErrors;
	}

	public void setArtifactResultErrors(
			List<ArtifactResultError> artifactResultErrors)
	{
		this.artifactResultErrors = artifactResultErrors;
	}
	
	public void addArtifactResultError(ArtifactResultError artifactResultError)
	{
		if(artifactResultErrors == null)
			artifactResultErrors = new ArrayList<ArtifactResultError>();
		artifactResultErrors.add(artifactResultError);
	}

	public ArtifactResultStatus getArtifactResultStatus()
	{
		return artifactResultStatus;
	}

	/**
	 * @return the site
	 */
	public RoutingToken getRoutingToken() 
	{
		return this.routingToken;
	}

	public boolean isExamsFullyLoaded()
	{
		for(Exam exam : this)
			if(!exam.isLoaded())
				return false;
		
		return true;
	}

	/**
	 * @param string
	 * @return
	 * @throws URNFormatException 
	 */
	public Exam getByStudyUrn(String urnAsString) 
	throws URNFormatException
	{
		if(urnAsString == null)
			return null;
		
		URN urn = URNFactory.create(urnAsString);
		
		for(Exam exam : this)
			if( urn.equals(exam.getStudyUrn()) )
				return exam;
		return null;
	}
	
	public void addOrUpdateExam(Exam exam)
	{
		if(exam != null)
		{
			boolean found = false;
			for(int i = 0; i < this.size(); i++)
			{
				Exam e = this.get(i);
				if(e.getStudyUrn().equals(exam.getStudyUrn()))
				{
					this.set(i, exam);
					found = true;
					break;
				}				
			}
			if(!found)
			{
				// add to list
				this.add(exam);
			}
		}
	}

	public String getSiteName()
	{
		return siteName;
	}
}
