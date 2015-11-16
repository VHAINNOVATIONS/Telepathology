/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 27, 2010
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

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.router.commands.AbstractCumulativeStatisticsCommandImpl;
import gov.va.med.imaging.core.router.commands.configuration.CommandConfiguration;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * Returns all of the studies for a patient from every site the patient has been seen at in the VA
 * 
 * @author vhaiswwerfej
 *
 */
public class GetCumulativeStatisticsStudySetResultForPatientCommandImpl
extends AbstractCumulativeStatisticsCommandImpl<StudySetResult>
{
	private static final long serialVersionUID = 2260077883448680177L;
	
	private final PatientIdentifier patientIdentifier;
	private final StudyFilter studyFilter;
	private final StudyLoadLevel studyLoadLevel;
	
	public GetCumulativeStatisticsStudySetResultForPatientCommandImpl(PatientIdentifier patientIdentifier, 
			StudyFilter studyFilter, StudyLoadLevel studyLoadLevel)
	{
		super(true);
		this.patientIdentifier = patientIdentifier;
		this.studyFilter = studyFilter;
		this.studyLoadLevel = studyLoadLevel;
	}

	@Override
	protected void callChildCommandAsync(RoutingToken routingToken,
			AsynchronousCommandResultListener listener)
	{
		if(getStudyLoadLevel() == StudyLoadLevel.STUDY_ONLY)
		{
			ImagingContext.getRouter().getShallowStudySetResultBySiteNumber(routingToken, 
					getPatientIdentifier(), getStudyFilter(), listener);
		}
		else if(getStudyLoadLevel() == StudyLoadLevel.STUDY_AND_IMAGES)
		{
			ImagingContext.getRouter().getStudySetResultWithImagesBySiteNumber(routingToken, 
					getPatientIdentifier(), getStudyFilter(), listener);	
		}
		else if(getStudyLoadLevel() == StudyLoadLevel.STUDY_AND_REPORT)
		{
			ImagingContext.getRouter().getStudySetResultWithReportsBySiteNumber(routingToken, 
					getPatientIdentifier(), getStudyFilter(), listener);
		}
		else // full
		{
			ImagingContext.getRouter().getStudySetResultBySiteNumber(routingToken, 
					getPatientIdentifier(), getStudyFilter(), listener);
		}			
	}

	@Override
	protected boolean shouldCallChildCommandForRoutingToken(RoutingToken routingToken)
	{
		if(getStudyFilter() != null && !getStudyFilter().isSiteAllowed(routingToken.getRepositoryUniqueId()))
		{
			getLogger().info("Site number [" + routingToken.toString() + "] is excluded in the StudyFilter, not loading study list from this site");
			return false;	
		}
		return true;
	}

	public StudyFilter getStudyFilter()
	{
		return studyFilter;
	}

	@Override
	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public StudyLoadLevel getStudyLoadLevel()
	{
		return studyLoadLevel;
	}

	@Override
	protected void setInitialImagingSecurityContext()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		if(CommandConfiguration.getCommandConfiguration().isUseCprsContextToGetPatientTreatingFacilitiyList())
		{		
			transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.CPRS_CONTEXT.toString());
		}
		else
		{
			transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.MAG_WINDOWS.toString());
		}
	}

	@Override
	protected void setSecondaryImagingSecurityContext()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setImagingSecurityContextType(ImagingSecurityContextType.MAG_WINDOWS.toString());
	}	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((patientIdentifier == null) ? 0 : patientIdentifier.hashCode());
		result = prime * result
				+ ((studyFilter == null) ? 0 : studyFilter.hashCode());
		result = prime * result
				+ ((studyLoadLevel == null) ? 0 : studyLoadLevel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetCumulativeStatisticsStudySetResultForPatientCommandImpl other = (GetCumulativeStatisticsStudySetResultForPatientCommandImpl) obj;
		if (patientIdentifier == null)
		{
			if (other.patientIdentifier != null)
				return false;
		}
		else if (!patientIdentifier.equals(other.patientIdentifier))
			return false;
		if (studyFilter == null)
		{
			if (other.studyFilter != null)
				return false;
		}
		else if (!studyFilter.equals(other.studyFilter))
			return false;
		if (studyLoadLevel == null)
		{
			if (other.studyLoadLevel != null)
				return false;
		}
		else if (!studyLoadLevel.equals(other.studyLoadLevel))
			return false;
		return true;
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

}
