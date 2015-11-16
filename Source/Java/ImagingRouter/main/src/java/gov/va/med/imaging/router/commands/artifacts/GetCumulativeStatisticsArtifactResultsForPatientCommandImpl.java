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
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.router.commands.AbstractCumulativeStatisticsCommandImpl;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.router.facade.ImagingContext;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetCumulativeStatisticsArtifactResultsForPatientCommandImpl
extends AbstractCumulativeStatisticsCommandImpl<ArtifactResults>
{
	private static final long serialVersionUID = -8751825363964626698L;
	
	private final RoutingToken patientTreatingSiteRoutingToken;
	private final PatientIdentifier patientIdentifier;
	private final StudyFilter studyFilter;
	private final StudyLoadLevel studyLoadLevel;
	private final boolean includeRadiology;
	private final boolean includeDocuments;

	public GetCumulativeStatisticsArtifactResultsForPatientCommandImpl(
			RoutingToken patientTreatingSiteRoutingToken,
			PatientIdentifier patientIdentifier, StudyFilter studyFilter,
			boolean includeRadiology, boolean includeDocuments,
			StudyLoadLevel studyLoadLevel)
	{
		super(false);
		this.patientTreatingSiteRoutingToken = patientTreatingSiteRoutingToken;
		this.patientIdentifier = patientIdentifier;
		this.studyFilter = studyFilter;
		this.studyLoadLevel = studyLoadLevel;
		this.includeRadiology = includeRadiology;
		this.includeDocuments = includeDocuments;
	}

	@Override
	protected PatientIdentifier getPatientIdentifier()
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
	public RoutingToken getRoutingToken()
	throws MethodException
	{
		try
		{
			// if the request is for VA Radiology data through the Exchange interface, need to use 
			// the VA Radiology site routing token with a wildcard to ensure we go against all
			// sites the patient has been seen at (will force a lookup in the treating facility list)
			return RoutingTokenImpl.createVARadiologySite(RoutingToken.ROUTING_WILDCARD);
		}
		catch(RoutingTokenFormatException rtfX)
		{
			throw new MethodException(rtfX);
		}
	}

	@Override
	protected RoutingToken getLocalRealmRadiologyRoutingToken()
	throws MethodException
	{
		// get the site to lookup the treating sites at
		return getPatientTreatingSiteRoutingToken();
	}

	@Override
	protected boolean shouldCallChildCommandForRoutingToken(
			RoutingToken routingToken)
	{
		if(getStudyFilter() != null && !getStudyFilter().isSiteAllowed(routingToken.getRepositoryUniqueId()))
		{
			getLogger().info("Site number [" + routingToken.toString() + "] is excluded in the StudyFilter, not loading study list from this site");
			return false;	
		}
		return true;
	}

	@Override
	protected void callChildCommandAsync(RoutingToken routingToken,
			@SuppressWarnings("rawtypes") AsynchronousCommandResultListener listener)
	{
		if(getStudyLoadLevel() == StudyLoadLevel.STUDY_ONLY)
		{
			ImagingContext.getRouter().getStudyOnlyPatientArtifactResultsFromSiteAsync(routingToken, 
					getPatientIdentifier(), getStudyFilter(), isIncludeRadiology(), isIncludeDocuments(), listener);
		}
		else if(getStudyLoadLevel() == StudyLoadLevel.STUDY_AND_IMAGES)
		{
			ImagingContext.getRouter().getStudyWithImagesPatientArtifactResultsFromSiteAsync(routingToken, 
					getPatientIdentifier(), getStudyFilter(), isIncludeRadiology(), isIncludeDocuments(), listener);
		}
		else if(getStudyLoadLevel() == StudyLoadLevel.STUDY_AND_REPORT)
		{
			ImagingContext.getRouter().getStudyWithReportPatientArtifactResultsFromSiteAsync(routingToken, 
					getPatientIdentifier(), getStudyFilter(), isIncludeRadiology(), isIncludeDocuments(), listener);
		}
		else // full
		{
			ImagingContext.getRouter().getPatientArtifactResultsFromSiteAsync(routingToken, 
					getPatientIdentifier(), getStudyFilter(), isIncludeRadiology(), isIncludeDocuments(), listener);
		}		
	}

	@Override
	protected void setInitialImagingSecurityContext()
	{
		// JMW 6/14/2013
		// I don't think this is necessary anymore because we explicitly set the context to use when using the VistaDataSource to 
		// CPRS context on the CVIX.  The facade sets the context to use for all other operations to be MAG WINDOWS
	}

	@Override
	protected void setSecondaryImagingSecurityContext()
	{
		// JMW 6/14/2013
		// I don't think this is necessary anymore because we explicitly set the context to use when using the VistaDataSource to 
		// CPRS context on the CVIX.  The facade sets the context to use for all other operations to be MAG WINDOWS
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
		sb.append("From treating site '" + getPatientTreatingSiteRoutingToken().toRoutingTokenString() + "'");
		sb.append(',');
		sb.append(this.getStudyFilter() == null ? "<null>" : this.getStudyFilter().toString());
		
		return sb.toString();
	}

}
