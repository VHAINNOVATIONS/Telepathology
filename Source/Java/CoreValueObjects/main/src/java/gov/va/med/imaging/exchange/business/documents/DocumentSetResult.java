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
package gov.va.med.imaging.exchange.business.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import gov.va.med.imaging.exchange.business.ArtifactResult;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;

/**
 * @author vhaiswwerfej
 *
 */
public class DocumentSetResult 
extends ArtifactResult<DocumentSet, SortedSet<DocumentSet>>
{	
	private DocumentSetResult(SortedSet<DocumentSet> documentSets, 
			ArtifactResultStatus artifactResultStatus, List<ArtifactResultError> artifactResultErrors)
	{
		super(documentSets, artifactResultStatus, artifactResultErrors);
	}

	public static DocumentSetResult create(SortedSet<DocumentSet> documentSets, 
			ArtifactResultStatus artifactResultStatus, List<ArtifactResultError> artifactResultErrors)
	{
		return new DocumentSetResult(documentSets, artifactResultStatus, artifactResultErrors);
	}
	
	public static DocumentSetResult createPartialResult(SortedSet<DocumentSet> documentSets, 
			List<ArtifactResultError> artifactResultErrors)
	{
		return new DocumentSetResult(documentSets, ArtifactResultStatus.partialResult, 
				artifactResultErrors);
	}
	
	public static DocumentSetResult createFullResult(SortedSet<DocumentSet> documentSets)
	{
		return new DocumentSetResult(documentSets, ArtifactResultStatus.fullResult, 
				null);
	}
	
	public static DocumentSetResult createErrorResult(List<ArtifactResultError> artifactResultErrors)
	{
		return new DocumentSetResult(null, ArtifactResultStatus.errorResult, artifactResultErrors);
	}
	
	public static DocumentSetResult createErrorResult(ArtifactResultError artifactResultError)
	{
		List<ArtifactResultError> artifactResultErrors = new ArrayList<ArtifactResultError>();
		artifactResultErrors.add(artifactResultError);		
		return new DocumentSetResult(null, ArtifactResultStatus.errorResult, artifactResultErrors);
	}

}
