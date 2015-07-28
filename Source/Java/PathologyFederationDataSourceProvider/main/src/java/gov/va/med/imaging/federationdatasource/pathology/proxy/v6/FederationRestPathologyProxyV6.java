/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 20, 2012
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
package gov.va.med.imaging.federationdatasource.pathology.proxy.v6;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.codegenerator.annotation.FederationGeneratedDataSourceProxyMethod;
import gov.va.med.imaging.federation.codegenerator.annotation.FederationGeneratedDataSourceProxyType;
import gov.va.med.imaging.federation.pathology.rest.endpoints.PathologyFederationRestUri;
import gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestProxy;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.federationdatasource.pathology.PathologyFederationProxyServiceType;
import gov.va.med.imaging.federationdatasource.pathology.proxy.commands.PathologyFederationAddCaseAssistanceCommand;
import gov.va.med.imaging.federationdatasource.pathology.proxy.commands.PathologyFederationGetCaseSpecimensCommand;
import gov.va.med.imaging.federationdatasource.pathology.proxy.commands.PathologyFederationGetPatientCasesCommand;
import gov.va.med.imaging.federationdatasource.pathology.proxy.commands.PathologyFederationGetSiteTemplateCommand;
import gov.va.med.imaging.federationdatasource.pathology.proxy.commands.PathologyFederationLockCaseCommand;
import gov.va.med.imaging.federationdatasource.pathology.proxy.commands.PathologyFederationSaveSiteTemplateCommand;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseSlide;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseSupplementalReport;
import gov.va.med.imaging.pathology.PathologyCaseTemplate;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.PathologyCptCode;
import gov.va.med.imaging.pathology.PathologyCptCodeResult;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.PathologyFieldValue;
import gov.va.med.imaging.pathology.PathologyReadingSite;
import gov.va.med.imaging.pathology.PathologySaveCaseReportResult;
import gov.va.med.imaging.pathology.PathologySite;
import gov.va.med.imaging.pathology.PathologySnomedCode;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus;
import gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeed;
import gov.va.med.imaging.pathology.enums.PathologyField;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;

import java.util.Date;
import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
@FederationGeneratedDataSourceProxyType
public abstract class FederationRestPathologyProxyV6
extends AbstractFederationRestProxy 
{
	
	public FederationRestPathologyProxyV6(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getRestServicePath()
	 */
	@Override
	protected String getRestServicePath()
	{
		return PathologyFederationRestUri.pathologyServicePath;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.proxy.AbstractFederationRestImageProxy#getProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getProxyServiceType()
	{
		return new PathologyFederationProxyServiceType();
		//return ProxyServiceType.pathology;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.proxy.AbstractFederationProxy#getDataSourceVersion()
	 */
	@Override
	protected String getDataSourceVersion()
	{
		return "6";
	}

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getCasesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseType[]",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),released:String.valueOf(released),days:String.valueOf(days)",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate", 
			inputParametersDescription="globalRoutingToken.toRoutingTokenString(),released:released,days:days")
	public abstract List<PathologyCase> getCases(RoutingToken globalRoutingToken,
			boolean released, int days) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getCasesWithRequestingSitePath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseType[]",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),released:String.valueOf(released),days:String.valueOf(days),requestingSiteId:requestingSiteId",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate", 
			inputParametersDescription="globalRoutingToken.toRoutingTokenString(),released:released,days:days,requestingSiteId:requestingSiteId")
	public abstract List<PathologyCase> getCases(RoutingToken globalRoutingToken,
			boolean released, int days, String requestingSiteId) 
	throws MethodException, ConnectionException;
	

	public List<PathologyCaseSpecimen> getCaseSpecimens(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException
	{
		return new PathologyFederationGetCaseSpecimensCommand(getDataSourceVersion(), proxyServices, 
				federationConfiguration, pathologyCaseUrn).execute();
	}
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getPatientCasesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseType[]",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),patientIcn:patientIdentifier.getValue()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate", 
			inputParametersDescription="globalRoutingToken.toRoutingTokenString(),patientIcn:patientIdentifier.getValue()")
	public abstract List<PathologyCase> getPatientCases(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier) 
	throws MethodException, ConnectionException;
	
	public List<PathologyCase> getPatientCases(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier, String requestingSiteId) 
	throws MethodException, ConnectionException
	{
		return new PathologyFederationGetPatientCasesCommand(getDataSourceVersion(), proxyServices, 
				federationConfiguration, globalRoutingToken, patientIdentifier.getValue(), requestingSiteId).execute();
	}

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getAcquisitionSitesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationAcquisitionSiteType[]",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate", 
			inputParametersDescription="globalRoutingToken.toRoutingTokenString()")
	public abstract List<PathologyAcquisitionSite> getAcquisitionSites(
			RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getReadingSitesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationReadingSiteType[]",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate", 
			inputParametersDescription="globalRoutingToken.toRoutingTokenString()")
	public abstract List<PathologyReadingSite> getReadingSites(
			RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	public PathologyCaseUpdateAttributeResult lockCase(
			PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException
	{
		return new PathologyFederationLockCaseCommand(getDataSourceVersion(), proxyServices, 
				federationConfiguration, pathologyCaseUrn, lock).execute();
	}
	
	public PathologyCaseUpdateAttributeResult addCaseAssistance(
			PathologyCaseURN pathologyCaseUrn,
			PathologyCaseAssistance assistanceType, String stationNumber)
	throws MethodException, ConnectionException
	{
		return new PathologyFederationAddCaseAssistanceCommand(getDataSourceVersion(), proxyServices, 
				federationConfiguration, pathologyCaseUrn, assistanceType, stationNumber).execute();
	}
	
	public List<String> getSiteTemplate(RoutingToken globalRoutingToken, List<String> apSections)
	throws MethodException, ConnectionException
	{
		return new PathologyFederationGetSiteTemplateCommand(getDataSourceVersion(), proxyServices, 
				federationConfiguration, globalRoutingToken, apSections).execute();
	}
	
	public void saveSiteTemplate(RoutingToken globalRoutingToken, String xmlTemplate, String apSection)
	throws MethodException, ConnectionException
	{
		new PathologyFederationSaveSiteTemplateCommand(getDataSourceVersion(), proxyServices, 
				federationConfiguration, globalRoutingToken, xmlTemplate, apSection).execute();
	}
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.updateReadingSitesPath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),delete:String.valueOf(delete)",
			inputParametersDescription="globalRoutingToken.toRoutingTokenString(),delete:delete",
			translateInputParametersNameAndMethod="readingSite:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate:site",
			postParameter="site")
	public abstract void updateReadingSite(RoutingToken globalRoutingToken, 
			PathologyReadingSite readingSite, boolean delete)
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.updateAcquisitionSitesPath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),delete:String.valueOf(delete)",
			inputParametersDescription="globalRoutingToken.toRoutingTokenString(),delete:delete",
			translateInputParametersNameAndMethod="acquisitionSite:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate:site",
			postParameter="site")
	public abstract void updateAcquisitionSite(RoutingToken globalRoutingToken, 
			PathologyAcquisitionSite acquisitionSite, boolean delete)
	throws MethodException, ConnectionException;
	/*
	{
		new PathologyFederationUpdateAcquisitionSitesCommand(getDataSourceVersion(), proxyServices, 
				federationConfiguration, globalRoutingToken, acquisitionSites).execute();
	}*/

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.userPreferencesPath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),userId:userId,label:label",
			inputParametersDescription="userId,label",
			translateInputParametersNameAndMethod="xml:RestCoreTranslator.translate:xmlString",
			postParameter="xmlString")
	public abstract void saveUserPreferences(RoutingToken globalRoutingToken, String userId,
			String label, String xml) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.preferencesPath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),label:label",
			inputParametersDescription="label",
			translateInputParametersNameAndMethod="xml:RestCoreTranslator.translate:xmlString",
			postParameter="xmlString")
	public abstract void savePreferences(RoutingToken globalRoutingToken, 
			String label, String xml) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.userPreferencesPath,
			translationResultMethodName="RestCoreTranslator.translate",
			federationReturnType="RestStringType", 
			inputParametersDescription="userId,label",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),userId:userId,label:label")
	public abstract String getUserPreferences(RoutingToken globalRoutingToken,
			String userId, String label) 
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.preferencesPath,
			translationResultMethodName="RestCoreTranslator.translate",
			federationReturnType="RestStringType", 
			inputParametersDescription="label",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),label:label")
	public abstract String getPreferences(RoutingToken globalRoutingToken,
			String label) 
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getSnomedCodesPath,
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate",
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationSnomedCodeType[]", 
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString()")
	public abstract List<PathologySnomedCode> getCaseSnomedCodes(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveSnomedCodesPath,
			federationReturnType="RestStringType", 
			postParameter="new RestNullPostType()",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),tissueId:tissueId,fieldId:pathologyFieldUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),tissueId:tissueId,fieldId:pathologyFieldUrn.toString()",
			translationResultMethodName="RestCoreTranslator.translate")
	public abstract String saveCaseSnomedCode(PathologyCaseURN pathologyCaseUrn,
			String tissueId, PathologyFieldURN pathologyFieldUrn)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveSnomedMorphologyCodesPath,
			federationReturnType="RestStringType", 
			postParameter="new RestNullPostType()",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),tissueId:tissueId,morphologyId:morphologyId,etiologyFieldId:etiologyFieldUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),tissue:tissueId,morphology:morphologyId,etiology:etiologyFieldUrn.toString()",
			translationResultMethodName="RestCoreTranslator.translate")
	public abstract String saveCaseSnomedCodeForMorphology(
			PathologyCaseURN pathologyCaseUrn, String tissueId,
			String morphologyId, PathologyFieldURN etiologyFieldUrn)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveCptCodesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCptCodeResultType[]",
			translateInputParametersNameAndMethod="cptCodes:RestCoreTranslator.translateStrings:cptArray",
			postParameter="cptArray",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),locationId:locationFieldUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),locationId:locationFieldUrn.toString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract List<PathologyCptCodeResult> saveCaseCptCodes(PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN locationFieldUrn, List<String> cptCodes)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getCaseReportPath,
			federationReturnType="RestStringType",
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString()",
			translationResultMethodName="RestCoreTranslator.translate")
	public abstract String getPathologyCaseReport(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getCaseSupplementalReportPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSupplementalReportType[]",
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract List<PathologyCaseSupplementalReport> getCaseSupplementalReports(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getCaseTemplateDataPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseTemplateType",
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			translateInputParametersNameAndMethod="fields:RestCoreTranslator.translateStrings:fieldArray",
			postParameter="fieldArray",
			inputParametersDescription="caseId:pathologyCaseUrn.toString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract PathologyCaseTemplate getCaseTemplateData(
			PathologyCaseURN pathologyCaseUrn, List<String> fields)
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.reserveCasePath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseReserveResultType",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),lock:String.valueOf(lock)",
			postParameter="new RestNullPostType()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),lock",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract PathologyCaseReserveResult reserveCase(
			PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.checkElectronicSignatureNeededPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationElectronicSignatureNeedType",
			restQueryParameters="routingToken:routingToken.toRoutingTokenString(),apSection:apSection",
			inputParametersDescription="routingToken,apSection",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract PathologyElectronicSignatureNeed checkElectronicSignatureNeeded(
			RoutingToken routingToken, String apSection)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getFieldsPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationFieldValueType[]",
			translateInputParametersNameAndMethod="pathologyField:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate:field",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),field:field.toString(),searchParam:searchParameter",
			postParameter="searchParameter",
			inputParametersDescription="globalRoutingToken,pathologyField",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract List<PathologyFieldValue> getPathologyFields(
			RoutingToken globalRoutingToken, PathologyField pathologyField,
			String searchParameter) 
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.updateConsultationStatusPath,
			federationReturnType="RestBooleanReturnType",
			translateInputParametersNameAndMethod="consultationUpdateStatus:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate:status",
			restQueryParameters="consultationId:pathologyCaseConsultationUrn.toString(),status:String.valueOf(status)",
			inputParametersDescription="consultation:pathologyCaseConsultationUrn.toString(),status:consultationUpdateStatus")
	public abstract void updateConsultationStatus(
			PathologyCaseConsultationURN pathologyCaseConsultationUrn,
			PathologyCaseConsultationUpdateStatus consultationUpdateStatus)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveCaseReportFieldsPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationSaveCaseReportResultType",
			translateInputParametersNameAndMethod="fields:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translateCaseReportFields:reportFields",
			postParameter="reportFields",
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="pathologyCaseUrn.toString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract PathologySaveCaseReportResult saveCaseReportFields(PathologyCaseURN pathologyCaseUrn,
			List<PathologyCaseReportField> fields) 
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveCaseSupplementalReportPath,
			federationReturnType="RestBooleanReturnType",
			translateInputParametersNameAndMethod="reportContents,date:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate:report",
			postParameter="report",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),verified:String.valueOf(verified)",
			inputParametersDescription="pathologyCaseUrn.toString(),verified:verified,date:date")
	public abstract void saveCaseSupplementalReport(PathologyCaseURN pathologyCaseUrn,
			String reportContents, Date date, boolean verified)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getPathologySitesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationSiteType[]",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString()",
			inputParametersDescription="globalRoutingToken.toRoutingTokenString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract List<PathologySite> getSites(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getLockExpiredMinutesPath,
			federationReturnType="RestIntegerType",
			translationResultMethodName="RestCoreTranslator.translate",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString()",
			inputParametersDescription="globalRoutingToken.toRoutingTokenString()")
	public abstract Integer getLockExpiresMinutes(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.setLockExpiredMinutesPath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),minutes:String.valueOf(minutes)",
			inputParametersDescription="globalRoutingToken.toRoutingTokenString(),minutes:minutes",
			postParameter="new RestNullPostType()")
	public abstract void setLockExpiresMinutes(RoutingToken globalRoutingToken, int minutes)
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getCptCodesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCptCodeType[]",
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="pathologyCaseUrn.toString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract List<PathologyCptCode> getCaseCptCodes(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getUserKeysPath,
			federationReturnType="RestStringArrayType",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString()",
			inputParametersDescription="globalRoutingToken.toRoutingTokenString()",
			translationResultMethodName="RestCoreTranslator.translate")
	public abstract List<String> getPathologyUserKeys(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getSpecificCasesPath,
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseType[]",
			restQueryParameters="",
			translateInputParametersNameAndMethod="cases:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translateCaseUrns:urns",
			postParameter="urns",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate")
	public abstract List<PathologyCase> getSpecificCases(List<PathologyCaseURN> cases)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.checkPendingConsultationStatusPath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),stationNumber:stationNumber",
			translationResultMethodName="RestCoreTranslator.translate", 
			inputParametersDescription="stationNumber:stationNumber")
	public abstract Boolean checkPendingConsultationStatus(
			RoutingToken globalRoutingToken, String stationNumber)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveTissuePath,
			federationReturnType="RestStringType",
			postParameter="new RestNullPostType()",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),tissueId:tissueFieldUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),tissueId:tissueFieldUrn.toString()",
			translationResultMethodName="RestCoreTranslator.translate")
	public abstract String saveCaseTissues(PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN tissueFieldUrn) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.copyCasePath,
			federationReturnType="RestStringType",
			postParameter="new RestNullPostType()",
			restQueryParameters="routingToken:globalRoutingToken.toRoutingTokenString(),caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="routingToken:globalRoutingToken.toRoutingTokenString(),caseId:pathologyCaseUrn.toString()",
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translateCaseId")
	public abstract PathologyCaseURN copyCase(RoutingToken globalRoutingToken, PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.deleteTissuePath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),tissueId:tissueId",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),tissueId:tissueId",
			postParameter="new RestNullPostType()")
	public abstract void deleteTissue(PathologyCaseURN pathologyCaseUrn,
			String tissueId) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.deleteSnomedCodePath,
			federationReturnType="RestBooleanReturnType",
			translateInputParametersNameAndMethod="snomedField:gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate:field",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),tissueId:tissueId,snomedId:snomedId,field:field.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),tissueId:tissueId,snomedId:snomedId,snomedField:snomedField.name()",
			postParameter="new RestNullPostType()")
	public abstract void deleteSnomedCode(PathologyCaseURN pathologyCaseUrn,
			String tissueId, String snomedId, PathologyField snomedField) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.deleteSnomedEtiologyCode,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="caseId:pathologyCaseUrn.toString(),tissueId:tissueId,snomedId:snomedId,etiologyId:etiologyId",
			inputParametersDescription="caseId:pathologyCaseUrn.toString(),tissueId:tissueId,snomedId:snomedId,etiologyId:etiologyId",
			postParameter="new RestNullPostType()")
	public abstract void deleteSnomedEtiologyCode(PathologyCaseURN pathologyCaseUrn,
			String tissueId, String snomedId, String etiologyId) 
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveCaseNotePath,
			federationReturnType="RestBooleanReturnType",
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString()",
			translateInputParametersNameAndMethod="note:RestCoreTranslator.translate:n",
			postParameter="n")
	public abstract void saveCaseNote(PathologyCaseURN pathologyCaseUrn, String note)
	throws MethodException, ConnectionException;

	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.saveCaseNotePath,
			federationReturnType="RestStringType",
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString()",
			translationResultMethodName="RestCoreTranslator.translate")
	public abstract String getCaseNote(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@FederationGeneratedDataSourceProxyMethod(
			federationMethodUri=PathologyFederationRestUri.getCaseSlidesPath,
			translationResultMethodName="gov.va.med.imaging.federation.pathology.rest.translator.PathologyFederationRestTranslator.translate",
			federationReturnType="gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSlideType[]", 
			restQueryParameters="caseId:pathologyCaseUrn.toString()",
			inputParametersDescription="caseId:pathologyCaseUrn.toString()")
	public abstract List<PathologyCaseSlide> getCaseSlides(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException;
}
