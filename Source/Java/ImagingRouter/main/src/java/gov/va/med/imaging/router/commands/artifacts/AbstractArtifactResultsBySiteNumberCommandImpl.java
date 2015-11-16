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
package gov.va.med.imaging.router.commands.artifacts;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.CoreArtifactResultError;
import gov.va.med.imaging.core.interfaces.router.CumulativeCommandRoutingTokenException;
import gov.va.med.imaging.core.router.commands.configuration.CommandConfiguration;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ArtifactResultErrorCode;
import gov.va.med.imaging.exchange.enums.ArtifactResultErrorSeverity;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.router.commands.AbstractImagingCommandImpl;
import gov.va.med.imaging.router.commands.CommonStudyCacheFunctions;
import gov.va.med.imaging.router.commands.CommonStudyFunctions;
import gov.va.med.imaging.router.commands.documents.CommonDocumentFunctions;
import gov.va.med.imaging.router.commands.mbean.DODRequests;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * Abstract command for handling ArtifactResults for a specific site number.
 * This command is a bit of a hybrid, on a VIX it will call the PatientArtifact SPI and on 
 * the CVIX it will call the DocumentSet and StudyGraph SPIs and rollup the results
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractArtifactResultsBySiteNumberCommandImpl
extends AbstractImagingCommandImpl<ArtifactResults>
{
	private static final long serialVersionUID = 1484928603723643561L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier; 
	private final StudyFilter filter;
	private final boolean includeRadiology;
	private final boolean includeDocuments;
	private final StudyLoadLevel studyLoadLevel;
	
	private static final long MAX_WAIT_TIME = 120000L;  // Wait 120 seconds for asynchronous results to be returned.
	
	private CountDownLatch countdownLatch;
	private StudySetResult asynchronousStudySetResult = null;
	private DocumentSetResult asynchronousDocumentSetResult = null;
	
	private int childGetSuccessCount = 0;
	private int childGetErrorCount = 0;
	//private List<CumulativeCommandRoutingTokenException> errors = 
	//	Collections.synchronizedList(new ArrayList<CumulativeCommandRoutingTokenException>());
	
	public AbstractArtifactResultsBySiteNumberCommandImpl(RoutingToken routingToken,
		PatientIdentifier patientIdentifier, 
		StudyFilter filter, 
		StudyLoadLevel studyLoadLevel,
		boolean includeRadiology, 
		boolean includeDocuments)
	{
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
		this.filter = filter;
		this.includeRadiology = includeRadiology;
		this.includeDocuments = includeDocuments;
		this.studyLoadLevel = studyLoadLevel;
	}
	
	private ArtifactResults getArtifactResultsWithPatientArtifactSpi()
	throws MethodException, ConnectionException
	{
		ArtifactResults result = null;
		switch(studyLoadLevel)
		{
			case FULL:
				result = ImagingContext.getRouter().getFullyLoadedPatientArtifactResultsFromSite(getRoutingToken(), 
						getPatientIdentifier(), getFilter(), isIncludeRadiology(), isIncludeDocuments());
				break;
			case STUDY_AND_IMAGES:
				result = ImagingContext.getRouter().getStudyWithImagesPatientArtifactResultsFromSite(getRoutingToken(), 
						getPatientIdentifier(), getFilter(), isIncludeRadiology(), isIncludeDocuments());
				break;
			case STUDY_AND_REPORT:
				result = ImagingContext.getRouter().getStudyWithReportPatientArtifactResultsFromSite(getRoutingToken(), 
						getPatientIdentifier(), getFilter(), isIncludeRadiology(), isIncludeDocuments());
				break;
			case STUDY_ONLY:
				result = ImagingContext.getRouter().getStudyOnlyPatientArtifactResultsFromSite(getRoutingToken(), 
						getPatientIdentifier(), getFilter(), isIncludeRadiology(), isIncludeDocuments());
				break;
		}
		return result;
	}
	
	/**
	 * Call the appropriate command async based on the study load level
	 */
	private void executeAppropriateStudySetResultCommandAsync(RoutingToken bhieRadiologyToken)
	{
		switch(getStudyLoadLevel())
		{
			case FULL:
				ImagingContext.getRouter().getStudySetResultBySiteNumber(bhieRadiologyToken, 
						getPatientIdentifier(), getFilter(), 
						new StudySetResultCommandChildListener(this, bhieRadiologyToken));
				break;
			case STUDY_AND_IMAGES:
				ImagingContext.getRouter().getStudySetResultWithImagesBySiteNumber(bhieRadiologyToken, 
						getPatientIdentifier(), getFilter(), 
						new StudySetResultCommandChildListener(this, bhieRadiologyToken));
				break;
			case STUDY_AND_REPORT:
				ImagingContext.getRouter().getStudySetResultWithReportsBySiteNumber(bhieRadiologyToken, 
						getPatientIdentifier(), getFilter(), 
						new StudySetResultCommandChildListener(this, bhieRadiologyToken));
				break;
			case STUDY_ONLY:
				ImagingContext.getRouter().getShallowStudySetResultBySiteNumber(bhieRadiologyToken, 
						getPatientIdentifier(), getFilter(), 
						new StudySetResultCommandChildListener(this, bhieRadiologyToken));
				break;
			
		}
	}

	@Override
	public ArtifactResults callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		// JMW 12/6/2010 Patch 104
		// set the serviced source so the transaction context has the right value. Setting this to the site
		// where the user asked for data from - this is not necessarily the routing token that gets used to find
		// the data
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		boolean callingArtifactSpi = CommandConfiguration.getCommandConfiguration().isCallArtifactSpi();		
		boolean vaRequest = !ExchangeUtil.isSiteDOD(getRoutingToken().getRepositoryUniqueId());
		
		if(callingArtifactSpi || vaRequest)
		{
			ArtifactResults result = getArtifactResultsWithPatientArtifactSpi();
			if(result == null)
			{
				// not sure what to do here, throwing an exception
				throw new MethodException("Got null ArtifactResult from site '" + getRoutingToken().toRoutingTokenString() + "'.");
			}
			else
			{
				if(result.getStudySetResult() != null && result.getStudySetResult().getArtifacts() != null)
				{
					// need to update the study data with the consolidated site values, then create a new artifact Result object holder					
					StudySetResult updatedStudySetResult = 
						CommonStudyFunctions.updateConsolidatedSitesInStudySetResult(result.getStudySetResult(), getCommandContext());
					result = ArtifactResults.create(updatedStudySetResult, result.getDocumentSetResult());					
					// do something if this returns false
					CommonStudyCacheFunctions.cacheStudyList(getCommandContext(), getRoutingToken().getRepositoryUniqueId(), result.getStudySetResult().getArtifacts());
				}
				if(result.getDocumentSetResult() != null && (result.getDocumentSetResult().getArtifacts() != null))
				{
					//TODO: cache document sets - not caching
					DocumentSetResult updatedDocumentSetResult = 
						CommonDocumentFunctions.updateConsolidatedSitesInDocumentSetResult(result.getDocumentSetResult(), getCommandContext());
					result = ArtifactResults.create(result.getStudySetResult(), updatedDocumentSetResult);					
				}
			}
			return result;
		}
		else
		{
			DODRequests dodRequests = DODRequests.getRoiCommandsStatistics();
			dodRequests.incrementTotalDodPatientArtifactRequests();
			boolean ensurePatientSeenAtDoD = CommandConfiguration.getCommandConfiguration().isEnsurePatientSeenAtDoD();
			if(ensurePatientSeenAtDoD)
			{
				// get the treating sites for the patient and verify the patient has been seen at 200DOD or 200 (a real DoD facility)
				try
				{
					getLogger().info("Ensuring patient '" + getPatientIdentifier() + "' has been seen at the DoD by looking up treating sites.");
					InternalContext.getRouter().getTreatingSitesFromDataSource(getRoutingToken(), getPatientIdentifier(),
							true);
				}
				catch(PatientNotFoundException pnfX)
				{
					dodRequests.incrementNonCorrelatedDodPatientArtifactRequests();
					// if we get a patient not found exception from station 200 that means the patient has not been seen there and isn't really correlated with the DoD
					getLogger().warn("Patient '" + getPatientIdentifier() + "' not found, indicates patient not really seen in the DoD, returning empty result set.");
					return ArtifactResults.create(null, null);		
				}
			}	
			
			int commandCount = 0;
			if(isIncludeDocuments())
				commandCount++;
			if(isIncludeRadiology())
				commandCount++;
			
			this.countdownLatch = new CountDownLatch(commandCount); // 2 sources of data, right?
			int childCommandsCount = commandCount;
			
			RoutingToken bhieDocumentsToken = null;
			if(isIncludeDocuments())
			{
				try
				{
					// need to create the appropriate routing token so this request goes to BHIE
					bhieDocumentsToken = 
						RoutingTokenImpl.createDoDDocumentSite(getRoutingToken().getRepositoryUniqueId());
					//TODO: might need more filtering here					
					if(patientIdentifier.getPatientIdentifierType().isLocal())
						throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
					DocumentFilter documentFilter = new DocumentFilter(getPatientIdentifier().getValue(), 
							getFilter().getFromDate(), getFilter().getToDate());
					ImagingContext.getRouter().getDocumentSetResultBySiteNumber(bhieDocumentsToken,
							documentFilter, new DocumentSetResultCommandChildListener(this, 
									bhieDocumentsToken));
				}
				catch(RoutingTokenFormatException rtfX)
				{
					getLogger().error("Error creating BHIE Documents Routing Token, " + rtfX.getMessage(), rtfX);
				}
			}
			// need to create the appropriate routing token so this request goes to the BIA
			RoutingToken bhieRadiologyToken = null;				
			if(isIncludeRadiology())
			{
				try
				{
					bhieRadiologyToken = 
						RoutingTokenImpl.createDoDRadiologySite(getRoutingToken().getRepositoryUniqueId());
					executeAppropriateStudySetResultCommandAsync(bhieRadiologyToken);
				}
				catch(RoutingTokenFormatException rtfX)
				{
					getLogger().error("Error creating BHIE Radiology Routing Token, " + rtfX.getMessage(), rtfX);
				}				
			} 
			
			long timeout = getAsynchronousCommandWaitTimeout();			
			// Wait for results to be accumulated
			try
			{
				countdownLatch.await(timeout, 
						TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException x)
			{
				String msg = "'" + this.getClass().getSimpleName() + "' interrupted waiting for child commands to complete, abandoning command execution.";
				getLogger().warn(msg);
				throw new MethodException(msg);
			}
			
			long orphaned = countdownLatch.getCount();		// should be zero
			getLogger().debug(
				childCommandsCount + " Asynchronous Commands: " + 
				childGetSuccessCount + " Completed OK, " + 
				childGetErrorCount + " Failed, " + 
				orphaned + " Orphaned.");
			
			// if there are orphan child commands, 
			// commandCount should never be 0 or less but just in case
			if((orphaned > 0) && (commandCount > 0))
			{
				// at least one async command failed to respond
				String errorMsg = "Did not receive a response from '" + getRoutingToken().toRoutingTokenString() + "' after '" + timeout + "' ms.";
				if(orphaned >= commandCount)
				{
					// didn't get a response from anywhere, throw an Exception
					throw new MethodException(errorMsg);
				}
				
				// if here then 1 of the 2 results did not return in time
				// create a new error result for the one that did not return to force 
				// a partial result
				if((asynchronousStudySetResult == null) && (isIncludeRadiology()))
				{
					ArtifactResultError artifactResultError = new CoreArtifactResultError(errorMsg, ArtifactResultErrorCode.timeoutException, 
							bhieRadiologyToken == null ? "null" : bhieRadiologyToken.toRoutingTokenString(), ArtifactResultErrorSeverity.error);
					asynchronousStudySetResult = StudySetResult.createErrorResult(artifactResultError);
				}
				else if((asynchronousDocumentSetResult == null) && (isIncludeDocuments()))
				{
					ArtifactResultError artifactResultError = new CoreArtifactResultError(errorMsg, ArtifactResultErrorCode.timeoutException, 
							bhieDocumentsToken == null ? "null" : bhieDocumentsToken.toRoutingTokenString(), ArtifactResultErrorSeverity.error);
					asynchronousDocumentSetResult = DocumentSetResult.createErrorResult(artifactResultError);
				}
			}
			
			// don't need to do any consolidated site fixup - it already happens
			return ArtifactResults.create(asynchronousStudySetResult, asynchronousDocumentSetResult);		
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public StudyFilter getFilter()
	{
		return filter;
	}

	public boolean isIncludeRadiology()
	{
		return includeRadiology;
	}

	public boolean isIncludeDocuments()
	{
		return includeDocuments;
	}

	public StudyLoadLevel getStudyLoadLevel()
	{
		return studyLoadLevel;
	}
	
	private long getAsynchronousCommandWaitTimeout()
	{
		long timeout = 
			CommandConfiguration.getCommandConfiguration().getAsynchronousCommandWaiterTimeoutMs();
		if(timeout <= 0)
		{
			timeout = MAX_WAIT_TIME;
		}
		return timeout;
	}
	
	class StudySetResultCommandChildListener
	implements AsynchronousCommandResultListener<StudySetResult>
	{
		private AbstractArtifactResultsBySiteNumberCommandImpl parentCommand;
		private RoutingToken childRoutingToken;
		
		StudySetResultCommandChildListener(AbstractArtifactResultsBySiteNumberCommandImpl parentCommand,
				RoutingToken routingToken)
		{
			this.parentCommand = parentCommand;
			this.childRoutingToken = routingToken;
		}

		@Override
		public void commandComplete(
				AsynchronousCommandResult<StudySetResult> result)
		{
			if(result.isSuccess())
			{
				parentCommand.asynchronousStudySetResult = result.getResult();
				parentCommand.childGetSuccessCount++;
			}
			else if(result.isError())
			{
				getLogger().info("child command results in error, adding no results.");
				SortedSet<Study> emptyStudyResult = new TreeSet<Study>();
				CumulativeCommandRoutingTokenException studyException = 
					new CumulativeCommandRoutingTokenException(childRoutingToken, result.getThrowable());
				
				List<ArtifactResultError> artifactResultErrors = new ArrayList<ArtifactResultError>();
				artifactResultErrors.add(studyException.toArtifactResultError());
				
				parentCommand.asynchronousStudySetResult = StudySetResult.create(emptyStudyResult, 
						ArtifactResultStatus.partialResult, artifactResultErrors); 
				//parentCommand.errors.add(new CumulativeCommandRoutingTokenException(childRoutingToken, 
				//		result.getThrowable()));
				parentCommand.childGetErrorCount++;				
			}
			parentCommand.countdownLatch.countDown();
		}		
	}
	
	class DocumentSetResultCommandChildListener
	implements AsynchronousCommandResultListener<DocumentSetResult>
	{
		private AbstractArtifactResultsBySiteNumberCommandImpl parentCommand;
		private RoutingToken childRoutingToken;
		
		DocumentSetResultCommandChildListener(AbstractArtifactResultsBySiteNumberCommandImpl parentCommand,
				RoutingToken routingToken)
		{
			this.parentCommand = parentCommand;
			this.childRoutingToken = routingToken;
		}

		@Override
		public void commandComplete(
				AsynchronousCommandResult<DocumentSetResult> result)
		{
			if(result.isSuccess())
			{
				parentCommand.asynchronousDocumentSetResult= result.getResult();
				parentCommand.childGetSuccessCount++;
			}
			else if(result.isError())
			{							
				getLogger().info("child command results in error, adding no results.");
				
				SortedSet<DocumentSet> emptyDocumentSetResult = new TreeSet<DocumentSet>();
				CumulativeCommandRoutingTokenException documentException = 
					new CumulativeCommandRoutingTokenException(childRoutingToken, result.getThrowable());
				
				List<ArtifactResultError> artifactResultErrors = new ArrayList<ArtifactResultError>();
				artifactResultErrors.add(documentException.toArtifactResultError());
				
				parentCommand.asynchronousDocumentSetResult = DocumentSetResult.create(emptyDocumentSetResult, 
						ArtifactResultStatus.partialResult, artifactResultErrors); 
								
				//parentCommand.errors.add(new CumulativeCommandRoutingTokenException(childRoutingToken, 
				//		result.getThrowable()));
				parentCommand.childGetErrorCount++;		
			}
			parentCommand.countdownLatch.countDown();
		}		
	}
}
