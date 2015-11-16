/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 1, 2010
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
package gov.va.med.imaging.router.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandRoutingTokenException;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandStatistics;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.router.facade.ImagingContext;

/**
 * This command calls GetCumulativeStatisticsStudySetResultForPatientCommand which is called for each
 * site the patient has been seen at. Then this command rolls up the result into a single
 * StudySetResult object which will indicate if the result is a partial result or not.
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractStudySetResultForPatientCommandImpl
extends AbstractCommandImpl<StudySetResult>
{
	private static final long serialVersionUID = 6906769757830563634L;
	
	private final PatientIdentifier patientIdentifier;
	private final StudyFilter studyFilter;
	private final StudyLoadLevel studyLoadLevel;
	
	public AbstractStudySetResultForPatientCommandImpl(PatientIdentifier patientIdentifier, StudyFilter studyFilter, 
			StudyLoadLevel studyLoadLevel)
	{
		this.patientIdentifier = patientIdentifier;
		this.studyFilter = studyFilter;
		this.studyLoadLevel = studyLoadLevel;
	}
	
	@Override
	public StudySetResult callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		@SuppressWarnings("unchecked")
		CumulativeCommandStatistics<StudySetResult> cumulativeCommandStatistics = 
			ImagingContext.getRouter().getCumulativeStatisticsStudySetResultForPatient(getPatientIdentifier(), 
					getStudyFilter(), getStudyLoadLevel());
		SortedSet<Study> fullResults = new TreeSet<Study>();
		List<ArtifactResultError> errors = new ArrayList<ArtifactResultError>();
		boolean partialResults = false;
		for(StudySetResult result : cumulativeCommandStatistics.getCumulativeResults())
		{
			fullResults.addAll(result.getArtifacts());
			if(result.isPartialResult())
				partialResults = true;
			if(result.getArtifactResultErrors() != null)
				errors.addAll(result.getArtifactResultErrors());
		}
		if(cumulativeCommandStatistics.getChildGetErrorCount() > 0)
			partialResults = true;
		
		// if the data sources were queried by the VIX then their exceptions were trapped by the VIX and not part of the result of the above command
		for(CumulativeCommandRoutingTokenException t : cumulativeCommandStatistics.getErrors())
		{
			errors.add(t.toArtifactResultError());
		}
		
		return StudySetResult.create(fullResults, 
				(partialResults == true ? ArtifactResultStatus.partialResult : ArtifactResultStatus.fullResult), 
				errors);		
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
}
