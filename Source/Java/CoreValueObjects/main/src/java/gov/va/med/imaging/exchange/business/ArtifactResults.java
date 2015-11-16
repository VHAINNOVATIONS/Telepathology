/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2010
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

import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;

/**
 * Artifact results contain the two image types for a patient in their separate result structures. Each may contain
 * errors and partial results
 * 
 * @author vhaiswwerfej
 *
 */
public class ArtifactResults
{
	private final StudySetResult studySetResult;
	private final DocumentSetResult documentSetResult;
	
	private ArtifactResults(StudySetResult studySetResult, DocumentSetResult documentSetResult)
	{
		this.studySetResult = studySetResult;
		this.documentSetResult = documentSetResult;
	}
	
	public static ArtifactResults create(StudySetResult studySetResult, DocumentSetResult documentSetResult)
	{
		return new ArtifactResults(studySetResult, documentSetResult);
	}
	
	public static ArtifactResults createStudySetResult(StudySetResult studySetResult)
	{
		return new ArtifactResults(studySetResult, null);
	}
	
	public static ArtifactResults createDocumentSetResult(DocumentSetResult documentSetResult)
	{
		return new ArtifactResults(null, documentSetResult);
	}

	public StudySetResult getStudySetResult()
	{
		return studySetResult;
	}

	public DocumentSetResult getDocumentSetResult()
	{
		return documentSetResult;
	}

	public boolean containsStudySetResult()
	{
		return (studySetResult != null);
	}
	
	public boolean containsDocumentSetResult()
	{
		return (documentSetResult != null);
	}
	
	public boolean isPartialResult()
	{
		if(studySetResult != null && studySetResult.isPartialResult())
			return true;
		if(documentSetResult != null && documentSetResult.isPartialResult())
			return true;
		// if both are included in the result, check to see if one has errors
		if(studySetResult != null && documentSetResult != null)
		{
			// if one is full and the other is an error, then its a partial result
			if(studySetResult.isFullResult() && documentSetResult.isErrorResult())
				return true;
			if(studySetResult.isErrorResult() && documentSetResult.isFullResult())
				return true;
		}
		return false;
	}
	
	public int getArtifactSize()
	{
		int count = 0;
		if(studySetResult != null)
		{
			count += studySetResult.getArtifactSize();
		}
		if(documentSetResult != null)
		{
			count += documentSetResult.getArtifactSize();
		}
		
		return count;
	}

	@Override
	public String toString()
	{
		return toString(false);
	}
	
	public String toString(boolean showErrors)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("StudySetResult: " + (studySetResult == null ? "null" : studySetResult.toString(showErrors)));
		sb.append(" and DocumentSetResult: " + (documentSetResult == null ? "null" : documentSetResult.toString(showErrors)));
		return sb.toString();
	}
}
