package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.router.facade.ImagingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @deprecated I believe this is no longer used and should be removed
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=false)
public class ProcessPatientRegistrationsCommandImpl extends AbstractCommandImpl<Boolean>
{
	
	private static final long serialVersionUID = 1L;

	private RoutingToken getLocalRoutingToken()
	throws MethodException
	{
		try
		{
			String localSiteNumber = getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber();
			RoutingToken localRoutingToken = RoutingTokenImpl.createVARadiologySite(localSiteNumber);
			
			return localRoutingToken;
		}
		catch (RoutingTokenFormatException x)
		{
			getLogger().error(x);
			throw new MethodException(x);
		}
	}
	
	@Override
	public Boolean callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		RoutingToken routingToken = getLocalRoutingToken();
		
		// Try to get a patient registration from the datasource
		PatientRegistration patientRegistration = 
			ImagingContext.getRouter().getPatientRegistration(routingToken);
		
		while (patientRegistration != null)
		{
			// Process the patient registration
			ProcessPatientRegistration(patientRegistration, routingToken);
			
			// Get the next patient registration (if any) from the datasource
			patientRegistration = ImagingContext.getRouter().getPatientRegistration(routingToken);
		}
		
		return true;
	}

	/**
	 * 
	 * @param patientRegistration
	 * @param routingToken
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	private void ProcessPatientRegistration(
		PatientRegistration patientRegistration, 
		RoutingToken routingToken) 
	throws MethodException, ConnectionException
	{
		// Get the PatientEnterpriseExams
		PatientEnterpriseExams patientEnterpriseExams = 
			ImagingContext.getRouter().getPatientEnterpriseExams(routingToken, patientRegistration.getPatientIcn(), false);
		
		// Get the relevant prior cpt codes
		HashMap<String, String> relevantPriorCptCodes = getRelevantPriorCptCodes(routingToken, patientRegistration);
		
		// Find all relevant prior exams, based on cpt code
		List<Exam> relevantPriorExams = null;
		try
		{
			relevantPriorExams = findRelevantPriorExams(routingToken, patientEnterpriseExams, relevantPriorCptCodes);
		}
		catch (RoutingTokenFormatException x)
		{
			getLogger().error(x.getMessage());
			throw new MethodException(x);
		}
		
		// Create a new asynchronous request for each exam, in order to prefetch the images
		// for the exam
		for (Exam exam : relevantPriorExams)
		{
			ImagingContext.getRouter().prefetchExamImages(exam.getStudyUrn());
		}
		
	}


	/**
	 * 
	 * @param localSiteNumber
	 * @param patientRegistration
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	private HashMap<String, String> getRelevantPriorCptCodes(
		RoutingToken routingToken, 
		PatientRegistration patientRegistration)
	throws MethodException, ConnectionException
	{
		String[] cptCodes = ImagingContext.getRouter().getRelevantPriorCptCodes(
			routingToken, patientRegistration.getCptCode());
		HashMap<String, String> cptCodeMap = new HashMap<String, String>();
		
		for (String cptCode : cptCodes)
		{
			cptCodeMap.put(cptCode, cptCode);
		}
		
		return cptCodeMap;
	}

	/**
	 * 
	 * @param localRoutingToken
	 * @param patientEnterpriseExams
	 * @param relevantPriorCptCodes
	 * @return
	 * @throws RoutingTokenFormatException
	 */
	private List<Exam> findRelevantPriorExams(
		RoutingToken localRoutingToken, 
		PatientEnterpriseExams patientEnterpriseExams, 
		HashMap<String, String> relevantPriorCptCodes) 
	throws RoutingTokenFormatException
	{
		List<Exam> relevantPriorExams = new ArrayList<Exam>();
		for (ExamSite examSite : patientEnterpriseExams.getExamSites().values())
		{
			RoutingToken examSiteRoutingToken = examSite.getRoutingToken(); 
			
			if( !RoutingTokenImpl.isEquivalent(examSiteRoutingToken, localRoutingToken) )
				for (Exam exam : examSite)
					if (relevantPriorCptCodes.containsKey(exam.getCptCode()))
						relevantPriorExams.add(exam);	// This is a relevant prior exam. Add it to our list
		}
		
		return relevantPriorExams;
	}

	@Override
	public boolean equals(Object obj)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Command<Boolean> getNewPeriodicInstance()
	{
		Command<Boolean> cmd = 
			  getCommandContext().getCommandFactory().createCommand(
					  Boolean.class,
					  "ProcessPatientRegistrationsCommand", 
					  null,
					  new Class<?>[]{
						 	 
				  		},
					  new Object[]{} 
					  );
		
		// Make the command periodic
		cmd.setPeriodic(true);

		// Copy property values from this instance
		cmd.setPriority(this.getPriority().ordinal());
		cmd.setPeriodicExecutionDelay(this.getPeriodicExecutionDelay());
		cmd.setProcessingDurationEstimate(this.getProcessingDurationEstimate());
		cmd.setCommandContext(this.getCommandContext());

		return cmd;

	}
}
