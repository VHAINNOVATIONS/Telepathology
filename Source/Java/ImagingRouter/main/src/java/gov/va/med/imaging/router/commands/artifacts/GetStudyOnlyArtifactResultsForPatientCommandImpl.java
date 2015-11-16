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

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetStudyOnlyArtifactResultsForPatientCommandImpl
extends AbstractArtifactResultsForPatientCommandImpl
{

	private static final long serialVersionUID = -3207505695810369480L;

	/**
	 * @param patientIdentifier
	 * @param studyFilter
	 * @param includeRadiology
	 * @param includeDocuments
	 * @param studyLoadLevel
	 */
	public GetStudyOnlyArtifactResultsForPatientCommandImpl(
			RoutingToken patientTreatingSiteRoutingToken,
			PatientIdentifier patientIdentifier, StudyFilter studyFilter,
			boolean includeRadiology, boolean includeDocuments)
	{
		super(patientTreatingSiteRoutingToken, patientIdentifier, studyFilter, 
				includeRadiology, includeDocuments, StudyLoadLevel.STUDY_ONLY);
	}

}
