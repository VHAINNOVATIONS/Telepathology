/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 10, 2013
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
package gov.va.med.imaging.router.commands.artifacts;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandRoutingTokenException;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandStatistics;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.router.commands.AbstractStudyCommandImpl;
import gov.va.med.imaging.router.facade.ImagingContext;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractArtifactResultsForPatientCommandImpl
extends AbstractStudyCommandImpl<ArtifactResults>
{

	private static final long serialVersionUID = 1584947056626607949L;
	
	private final RoutingToken patientTreatingSiteRoutingToken;
	private final PatientIdentifier patientIdentifier;
	private final StudyFilter studyFilter;
	private final StudyLoadLevel studyLoadLevel;
	private final boolean includeRadiology;
	private final boolean includeDocuments;
	
	public AbstractArtifactResultsForPatientCommandImpl(
			RoutingToken patientTreatingSiteRoutingToken,
			PatientIdentifier patientIdentifier, 
			StudyFilter studyFilter,
			boolean includeRadiology,
			boolean includeDocuments,
			StudyLoadLevel studyLoadLevel)
	{
		super();
		this.patientTreatingSiteRoutingToken = patientTreatingSiteRoutingToken;
		this.patientIdentifier = patientIdentifier;
		this.studyFilter = studyFilter;
		this.studyLoadLevel = studyLoadLevel;
		this.includeRadiology = includeRadiology;
		this.includeDocuments = includeDocuments;				
	}

	@Override
	public ArtifactResults callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		@SuppressWarnings("unchecked")
		CumulativeCommandStatistics<ArtifactResults> cumulativeCommandStatistics = 
			ImagingContext.getRouter().getCumulativeStatisticsArtifactResultsForPatient(getPatientTreatingSiteRoutingToken(),
					getPatientIdentifier(), 
					getStudyFilter(), isIncludeRadiology(), isIncludeDocuments(), getStudyLoadLevel());
		SortedSet<Study> studyResults = new TreeSet<Study>();
		SortedSet<DocumentSet> documentSetResults = new TreeSet<DocumentSet>();
		List<ArtifactResultError> errors = new ArrayList<ArtifactResultError>();
		boolean partialResults = false;
		
		for(ArtifactResults artifactResults : cumulativeCommandStatistics.getCumulativeResults())
		{
			if(artifactResults.containsStudySetResult())
			{
				StudySetResult studySetResult = artifactResults.getStudySetResult();
				studyResults.addAll(studySetResult.getArtifacts());
				if(studySetResult.isPartialResult())
					partialResults = true;
				if(studySetResult.getArtifactResultErrors() != null)
					errors.addAll(studySetResult.getArtifactResultErrors());
			}
			if(artifactResults.containsDocumentSetResult())
			{
				DocumentSetResult documentSetResult = artifactResults.getDocumentSetResult();
				documentSetResults.addAll(documentSetResult.getArtifacts());
				if(documentSetResult.isPartialResult())
					partialResults = true;
				if(documentSetResult.getArtifactResultErrors() != null)
					errors.addAll(documentSetResult.getArtifactResultErrors());
			}
		}
		if(cumulativeCommandStatistics.getChildGetErrorCount() > 0)
			partialResults = true;
		
		// if the data sources were queried by the VIX then their exceptions were trapped by the VIX and not part of the result of the above command
		for(CumulativeCommandRoutingTokenException t : cumulativeCommandStatistics.getErrors())
		{
			errors.add(t.toArtifactResultError());
		}
		
		return ArtifactResults.create(
				StudySetResult.create(studyResults, 
						(partialResults == true ? ArtifactResultStatus.partialResult : ArtifactResultStatus.fullResult), 
						errors),
				DocumentSetResult.create(documentSetResults, 
						(partialResults == true ? ArtifactResultStatus.partialResult : ArtifactResultStatus.fullResult), 
						errors));
	}



	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public StudyFilter getStudyFilter()
	{
		return studyFilter;
	}

	public StudyLoadLevel getStudyLoadLevel()
	{
		return studyLoadLevel;
	}
	
	public boolean isIncludeRadiology()
	{
		return includeRadiology;
	}

	public boolean isIncludeDocuments()
	{
		return includeDocuments;
	}

	public RoutingToken getPatientTreatingSiteRoutingToken()
	{
		return patientTreatingSiteRoutingToken;
	}

	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getPatientIdentifier());
		sb.append(',');
		sb.append(this.getStudyFilter() == null ? "<null>" : this.getStudyFilter().toString());
		
		return sb.toString();
	}

}
