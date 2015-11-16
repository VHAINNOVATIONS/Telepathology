/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 24, 2012
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.PatientArtifactDataSourceSpi;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingPatientArtifactDataSourceServiceV3 
extends VistaImagingStudyGraphDataSourceServiceV2
implements PatientArtifactDataSourceSpi
{

	public VistaImagingPatientArtifactDataSourceServiceV3(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	@Override
	public ArtifactResults getPatientArtifacts(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier,
			StudyFilter studyFilter, StudyLoadLevel studyLoadLevel,
			boolean includeImages, boolean includeDocuments)
	throws MethodException, ConnectionException
	{
		StudySetResult studySetResult = getPatientStudies(globalRoutingToken, patientIdentifier, 
				studyFilter, studyLoadLevel);
		return ArtifactResults.createStudySetResult(studySetResult);
	}
}
