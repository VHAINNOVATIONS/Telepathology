/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 24, 2010
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
package gov.va.med.imaging.federation.proxy.v4;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.ExamListResult;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.federation.rest.endpoints.FederationVistaRadRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy;
import gov.va.med.imaging.federation.rest.proxy.FederationRestGetClient;
import gov.va.med.imaging.federation.rest.proxy.FederationRestPostClient;
import gov.va.med.imaging.federation.rest.translator.FederationRestTranslator;
import gov.va.med.imaging.federation.rest.types.FederationActiveExamsType;
import gov.va.med.imaging.federation.rest.types.FederationExamImagesType;
import gov.va.med.imaging.federation.rest.types.FederationExamResultType;
import gov.va.med.imaging.federation.rest.types.FederationExamType;
import gov.va.med.imaging.federation.rest.types.FederationPatientRegistrationType;
import gov.va.med.imaging.federation.rest.types.FederationStringArrayType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.rest.types.RestBooleanReturnType;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestVistaRadProxyV4 
extends AbstractFederationRestProxy 
{
	public FederationRestVistaRadProxyV4(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType() 
	{
		return ProxyServiceType.vistaRadMetadata;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getRestServicePath()
	 */
	@Override
	protected String getRestServicePath() 
	{
		return FederationVistaRadRestUri.vistaradServicePath;
	}
	
	public PatientRegistration getNextPatientRegistration(RoutingToken routingToken)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getNextPatientRegistration, Transaction [" + transactionContext.getTransactionId() + "] initiated, to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getNextPatientRegistration");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradGetNextPatientRegistration, urlParameterKeyValues ); 
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationPatientRegistrationType patientRegistration = getClient.executeRequest(FederationPatientRegistrationType.class);
		getLogger().info("getNextPatientRegistration, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (patientRegistration == null ? "null" :  "not null") + "] patient registration webservice objects.");
		PatientRegistration result = FederationRestTranslator.translate(patientRegistration); 
		getLogger().info("getNextPatientRegistration, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  "not null") + "] patient registration business objects.");		
		return result;
	}
	
	public ExamImages getExamImagesForExam(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();		
		getLogger().info("getExamImagesForExam, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExamImagesForExam");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{examId}", studyUrn.toString());
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradGetExamImages, urlParameterKeyValues ); 
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationExamImagesType examImages = getClient.executeRequest(FederationExamImagesType.class);
		getLogger().info("getExamImagesForExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (examImages == null ? "null" :  "not null") + "] exam images webservice objects.");
		ExamImages result = null;
		try
		{
			result = FederationRestTranslator.translate(examImages);
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating exam images", urnfX);
			throw new MethodException(urnfX);
		}
		getLogger().info("getExamImagesForExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  result.size()) + "] exam images business objects.");		
		return result;
	}
	
	public String[] getRelevantPriorCptCodes(RoutingToken routingToken, String cptCode)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getRelevantPriorCptCodes, Transaction [" + transactionContext.getTransactionId() + "] initiated, cpt code '" + cptCode + "', to '" + routingToken.toRoutingTokenString() + "'.");
		String [] cptCodes = null;
		setDataSourceMethodAndVersion("getRelevantPriorCptCodes");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{cptCode}", cptCode);
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradCptCodes, urlParameterKeyValues ); 
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationStringArrayType result = getClient.executeRequest(FederationStringArrayType.class);
		getLogger().info("getRelevantPriorCptCodes, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (cptCodes == null ? "null" :  cptCodes.length) + "] cpt codes.");
		cptCodes = FederationRestTranslator.translateStringArray(result);
		return cptCodes;
	}
	
	public boolean postExamAccess(RoutingToken routingToken, String inputParameter)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("postExamAccess, Transaction [" + transactionContext.getTransactionId() + "] initiated to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("postExamAccess");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradPostImageAccess, urlParameterKeyValues ); 
		
		FederationRestPostClient postClient = new FederationRestPostClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		RestBooleanReturnType result = postClient.executeRequest(RestBooleanReturnType.class, inputParameter);
		getLogger().info("executePassthroughMethod, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.isResult()) + "] from webservice.");
		return (result == null ? false : result.isResult());
	}
	
	public ExamListResult getExamsForPatient(RoutingToken routingToken, String patientIcn,
			boolean fullyLoadExams, boolean forceRefresh, boolean forceImagesFromJb)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getExamsForPatient, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getExamsForPatient");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{patientIcn}", patientIcn);
		urlParameterKeyValues.put("{fullyLoaded}", fullyLoadExams + "");
		urlParameterKeyValues.put("{forceRefresh}", forceRefresh + "");
		urlParameterKeyValues.put("{forceImagesFromJb}", forceImagesFromJb + "");
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradGetExamsPath, urlParameterKeyValues ); 
		
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationExamResultType examResult = getClient.executeRequest(FederationExamResultType.class);
		
		getLogger().info("getExamsForPatient, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (examResult == null ? "null" :  "not null") + "] FederationExamResultType webservice objects.");
		ExamListResult result = null;
		try
		{
			result = FederationRestTranslator.translate(examResult); 
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating exams", urnfX);
			throw new MethodException(urnfX);
		}
		getLogger().info("getExamsForPatient, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : "not null") + "] ExamListResult business object.");		
		return result;
	}
	
	public Exam getExam(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getExam, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExam");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		// JMW 11/29/2012 p119 - encode the StudyURN so the patient identifier is included
		urlParameterKeyValues.put("{examId}", encodeGai(studyUrn));		
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradGetExamPath, urlParameterKeyValues ); 
		
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationExamType exam = getClient.executeRequest(FederationExamType.class);
				
		getLogger().info("getExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (exam == null ? "null" :  "not null") + "] exam webservice objects.");
		Exam result = null;
		try
		{
			result = FederationRestTranslator.translate(exam);		
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating exam", urnfX);
			throw new MethodException(urnfX);
		}
		getLogger().info("getExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  "not null") + "] exam business objects.");		
		return result;
	}

	public ActiveExams getActiveExams(RoutingToken routingToken, String listDescriptor)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getActiveExams, Transaction [" + transactionContext.getTransactionId() + "] initiated, list Descriptor '" + listDescriptor + "' to '" + routingToken.toRoutingTokenString() + "'.");
		setDataSourceMethodAndVersion("getActiveExams");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{routingToken}", routingToken.toRoutingTokenString());
		urlParameterKeyValues.put("{listDescriptor}", encodeString(listDescriptor));
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradGetActiveExamsPath, urlParameterKeyValues ); 
		
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		FederationActiveExamsType activeExams = getClient.executeRequest(FederationActiveExamsType.class);
				
		getLogger().info("getActiveExams, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (activeExams == null ? "null" : "not null") + "] active exams webservice objects.");
		ActiveExams result = FederationRestTranslator.translate(activeExams);		
		getLogger().info("getActiveExams, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  result.size()) + "] active exams business objects.");		
		return result;
	}
	
	public String getExamRadiologyReport(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getExamRadiologyReport, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExamRadiologyReport");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{examId}", studyUrn.toString());
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradGetExamReportPath, urlParameterKeyValues ); 
		
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		String result = getClient.executeRequest(String.class);
				
		getLogger().info("getExamRadiologyReport, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	public String getExamRequisitionReport(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		getLogger().info("getExamRequisitionReport, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExamRequisitionReport");
		Map<String, String> urlParameterKeyValues = new HashMap<String, String>();
		urlParameterKeyValues.put("{examId}", studyUrn.toString());
		String url = getWebResourceUrl(FederationVistaRadRestUri.vistaradGetExamRequisitionReportPath, urlParameterKeyValues ); 
		
		FederationRestGetClient getClient = new FederationRestGetClient(url, MediaType.APPLICATION_XML_TYPE, federationConfiguration);
		String result = getClient.executeRequest(String.class);
				
		getLogger().info("getExamRequisitionReport, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "4";
	}
}
