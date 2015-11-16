/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 24, 2010
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * Represents the result of requesting a set of studies. This result indicates if only a partial set of studies
 * could be returned. This type is NOT used when there is a general error (those are thrown as exceptions)
 * 
 * @author vhaiswwerfej
 *
 */
public class StudySetResult
extends ArtifactResult<Study, SortedSet<Study>>
{
	
	private StudySetResult(SortedSet<Study> studies, 
			ArtifactResultStatus artifactResultStatus, List<ArtifactResultError> artifactResultErrors)
	{
		super(studies, artifactResultStatus, artifactResultErrors);
	}

	public static StudySetResult create(SortedSet<Study> studies, 
			ArtifactResultStatus artifactResultStatus, List<ArtifactResultError> artifactResultErrors)
	{
		return new StudySetResult(studies, artifactResultStatus, artifactResultErrors);
	}
	
	public static StudySetResult createPartialResult(SortedSet<Study> studies, 
			List<ArtifactResultError> artifactResultErrors)
	{
		return new StudySetResult(studies, ArtifactResultStatus.partialResult, 
				artifactResultErrors);
	}
	
	public static StudySetResult createFullResult(SortedSet<Study> studies)
	{
		return new StudySetResult(studies, ArtifactResultStatus.fullResult, 
				null);
	}
	
	public static StudySetResult createErrorResult(List<ArtifactResultError> artifactResultErrors)
	{
		return new StudySetResult(null, ArtifactResultStatus.errorResult, artifactResultErrors);
	}
	
	public static StudySetResult createErrorResult(ArtifactResultError artifactResultError)
	{
		List<ArtifactResultError> artifactResultErrors = new ArrayList<ArtifactResultError>();
		artifactResultErrors.add(artifactResultError);		
		return new StudySetResult(null, ArtifactResultStatus.errorResult, artifactResultErrors);
	}
}
