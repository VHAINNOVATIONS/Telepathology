/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 11, 2012
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
package gov.va.med.imaging.pathology.rest;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.commands.PathologyAddCaseAssistanceCommand;
import gov.va.med.imaging.pathology.commands.PathologyDeleteCaseSnomedCodeCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetAcquisitionSitesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCaseCptCodesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCaseNoteCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCaseReportCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCaseSnomedCodesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCaseSpecimensCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCaseSupplementalReportsCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCaseTemplateDataCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetCasesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetElectronicSignatureNeededCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetFieldValuesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetKeysCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetLockMinutesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetPatientCasesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetPatientCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetPendingConsultationStatusCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetReadingSitesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetSitesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetSpecificCasesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetTemplatesCommand;
import gov.va.med.imaging.pathology.commands.PathologyGetUserPreferencesCommand;
import gov.va.med.imaging.pathology.commands.PathologyLockCaseCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostAcquisitionSiteCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseConsultationStatusCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseCptCodesCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseMorphologySnomedCodeCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseNoteCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseReportFieldsCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseSnomedCodeCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseSupplementalReportCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostCaseTissuesCommandImpl;
import gov.va.med.imaging.pathology.commands.PathologyPostCopyCaseCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostLockMinutesCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostReadingSiteCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostTemplateCommand;
import gov.va.med.imaging.pathology.commands.PathologyPostUserPreferencesCommand;
import gov.va.med.imaging.pathology.commands.PathologyReserveCaseCommand;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.pathology.rest.types.PathologyAcquisitionSiteType;
import gov.va.med.imaging.pathology.rest.types.PathologyAcquisitionSitesType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseConsultationUpdateStatusType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseReportFieldsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseReserveResultType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseSupplementalReportsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseTemplateInputFieldsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseTemplateType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseUpdateAttributeResultType;
import gov.va.med.imaging.pathology.rest.types.PathologyCasesType;
import gov.va.med.imaging.pathology.rest.types.PathologyCopyCaseResultType;
import gov.va.med.imaging.pathology.rest.types.PathologyCptCodeResultsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCptCodesType;
import gov.va.med.imaging.pathology.rest.types.PathologyElectronicSignatureNeedType;
import gov.va.med.imaging.pathology.rest.types.PathologyFieldType;
import gov.va.med.imaging.pathology.rest.types.PathologyFieldValuesType;
import gov.va.med.imaging.pathology.rest.types.PathologyPatientType;
import gov.va.med.imaging.pathology.rest.types.PathologyReadingSiteType;
import gov.va.med.imaging.pathology.rest.types.PathologyReadingSitesType;
import gov.va.med.imaging.pathology.rest.types.PathologySaveCaseReportResultType;
import gov.va.med.imaging.pathology.rest.types.PathologySitesType;
import gov.va.med.imaging.pathology.rest.types.PathologySnomedCodesType;
import gov.va.med.imaging.pathology.rest.types.PathologySpecimensType;
import gov.va.med.imaging.pathology.rest.types.PathologyTemplateInputType;
import gov.va.med.imaging.pathology.rest.types.PathologyTemplatesType;
import gov.va.med.imaging.rest.types.RestBooleanReturnType;
import gov.va.med.imaging.rest.types.RestIntegerType;
import gov.va.med.imaging.rest.types.RestStringArrayType;
import gov.va.med.imaging.rest.types.RestStringType;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author VHAISWWERFEJ
 *
 */
@Path("")
public class PathologyRestService
{
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/reading/{siteId}")
	public PathologyReadingSitesType getReadingSites(
			@PathParam("siteId") String siteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetReadingSitesCommand(siteId).execute();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/acquisition/{siteId}")
	public PathologyAcquisitionSitesType getAcquisitionSites(
			@PathParam("siteId") String siteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetAcquisitionSitesCommand(siteId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/cases/released/{siteId}/{days}/{requestingSiteId}")
	public PathologyCasesType getReleasedPathologyCases(
			@PathParam("siteId") String siteId,
			@PathParam("days") Integer days,
			@PathParam("requestingSiteId") String requestingSiteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCasesCommand(siteId, true, days,
				requestingSiteId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/cases/released/{siteId}/{days}")
	public PathologyCasesType getReleasedPathologyCases(
			@PathParam("siteId") String siteId,
			@PathParam("days") Integer days)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCasesCommand(siteId, true, days).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/cases/unreleased/{siteId}/{days}/{requestingSiteId}")
	public PathologyCasesType getUnreleasedPathologyCases(
			@PathParam("siteId") String siteId,
			@PathParam("days") Integer days,
			@PathParam("requestingSiteId") String requestingSiteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCasesCommand(siteId, false, days, 
				requestingSiteId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/cases/unreleased/{siteId}/{days}")
	public PathologyCasesType getUnreleasedPathologyCases(
			@PathParam("siteId") String siteId,
			@PathParam("days") Integer days)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCasesCommand(siteId, false, days).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/cases/patient/{siteId}/{patientId}/{requestingSiteId}")
	public PathologyCasesType getPatientCases(
			@PathParam("siteId") String siteId,
			@PathParam("patientId") String patientId,
			@PathParam("requestingSiteId") String requestingSiteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetPatientCasesCommand(siteId, 
				PatientIdentifier.fromString(patientId), requestingSiteId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/cases/patient/{siteId}/{patientId}")
	public PathologyCasesType getPatientCases(
			@PathParam("siteId") String siteId,
			@PathParam("patientId") String patientId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetPatientCasesCommand(siteId, 
				PatientIdentifier.fromString(patientId)).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/patient/{siteId}/{patientId}")
	public PathologyPatientType getPatient(
			@PathParam("siteId") String siteId,
			@PathParam("patientId") String patientId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetPatientCommand(siteId, 
				PatientIdentifier.fromString(patientId)).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/lock/{caseId}/{lock}")
	public PathologyCaseUpdateAttributeResultType lockCase(
			@PathParam("caseId") String caseId,
			@PathParam("lock") boolean lock)
	throws MethodException, ConnectionException
	{
		return new PathologyLockCaseCommand(caseId, lock).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/consultation/{caseId}/{stationNumber}")
	public PathologyCaseUpdateAttributeResultType requestConsultation(
			@PathParam("caseId") String caseId,			
			@PathParam("stationNumber") String stationNumber)
	throws MethodException, ConnectionException
	{
		return new PathologyAddCaseAssistanceCommand(caseId, 
				PathologyCaseAssistance.consultation, stationNumber).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/interpretation/{caseId}/{stationNumber}")
	public PathologyCaseUpdateAttributeResultType requestInterpretation(
			@PathParam("caseId") String caseId,			
			@PathParam("stationNumber") String stationNumber)
	throws MethodException, ConnectionException
	{
		return new PathologyAddCaseAssistanceCommand(caseId, 
				PathologyCaseAssistance.interpretation, stationNumber).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/specimens/{caseId}")
	public PathologySpecimensType getCaseSpecimens(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCaseSpecimensCommand(caseId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/templates/{siteId}/{apSections}")
	public PathologyTemplatesType getTemplates(
			@PathParam("siteId") String siteId,
			@PathParam("apSections") String apSections)
	throws MethodException, ConnectionException
	{
		return new PathologyGetTemplatesCommand(siteId, apSections).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/reading/{siteId}")
	public RestBooleanReturnType updateReadingSite(
			@PathParam("siteId") String siteId,
			PathologyReadingSiteType readingSite)
	throws MethodException, ConnectionException
	{
		return new PathologyPostReadingSiteCommand(siteId, readingSite, false).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/reading/delete/{siteId}")
	public RestBooleanReturnType deleteReadingSite(
			@PathParam("siteId") String siteId,
			PathologyReadingSiteType readingSite)
	throws MethodException, ConnectionException
	{
		return new PathologyPostReadingSiteCommand(siteId, readingSite, true).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/acquisition/{siteId}")
	public RestBooleanReturnType updateAcquisitionSite(
			@PathParam("siteId") String siteId,
			PathologyAcquisitionSiteType acquisitionSite)
	throws MethodException, ConnectionException
	{
		return new PathologyPostAcquisitionSiteCommand(siteId, acquisitionSite, false).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/acquisition/delete/{siteId}")
	public RestBooleanReturnType deleteAcquisitionSite(
			@PathParam("siteId") String siteId,
			PathologyAcquisitionSiteType acquisitionSite)
	throws MethodException, ConnectionException
	{
		return new PathologyPostAcquisitionSiteCommand(siteId, acquisitionSite, true).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/template/{siteId}/{apSection}")
	public RestBooleanReturnType saveTemplate(
			@PathParam("siteId") String siteId,
			@PathParam("apSection") String apSection,
			PathologyTemplateInputType templateInput)
	throws MethodException, ConnectionException
	{
		return new PathologyPostTemplateCommand(siteId, apSection, templateInput).execute();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/supplementalreports/{caseId}")
	public PathologyCaseSupplementalReportsType getCaseSupplementalReports(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCaseSupplementalReportsCommand(caseId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/report/{caseId}")
	public RestStringType getCaseReport(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCaseReportCommand(caseId).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/template/{caseId}")
	public PathologyCaseTemplateType getCaseTemplate(
			@PathParam("caseId") String caseId,
			PathologyCaseTemplateInputFieldsType fields)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCaseTemplateDataCommand(caseId, fields).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/reserve/{caseId}/{lock}")
	public PathologyCaseReserveResultType reserveCase(
			@PathParam("caseId") String caseId,
			@PathParam("lock") boolean lock)
	throws MethodException, ConnectionException
	{
		return new PathologyReserveCaseCommand(caseId, lock).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/esigneeded/{siteId}/{apSection}")
	public PathologyElectronicSignatureNeedType checkElectronicSignatureNeeded(
			@PathParam("siteId") String siteId, 
			@PathParam("apSection") String apSection)
	throws MethodException, ConnectionException
	{
		return new PathologyGetElectronicSignatureNeededCommand(siteId, apSection).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/fields/{siteId}/{field}/{searchParameter}")
	public PathologyFieldValuesType getFieldValues(
			@PathParam("siteId") String siteId,
			@PathParam("field") PathologyFieldType fieldType,
			@PathParam("searchParameter") String searchParameter)
	throws MethodException, ConnectionException
	{
		return new PathologyGetFieldValuesCommand(siteId, fieldType, searchParameter).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/consultation/{caseConsultationId}/{status}")
	public RestBooleanReturnType updateCaseConsultationStatus(
			@PathParam("caseConsultationId") String caseConsultationId,
			@PathParam("status") PathologyCaseConsultationUpdateStatusType status)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCaseConsultationStatusCommand(caseConsultationId, status).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/report/{caseId}")
	public PathologySaveCaseReportResultType saveCaseReportFields(
			@PathParam("caseId") String caseId,
			PathologyCaseReportFieldsType fields)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCaseReportFieldsCommand(caseId, fields).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/supplementalreport/{caseId}/{date}/{verified}")
	public RestBooleanReturnType saveCaseSupplementalReport(
			@PathParam("caseId") String caseId,
			@PathParam("date") String date,
			@PathParam("verified") boolean verified,
			RestStringType reportContents)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCaseSupplementalReportCommand(caseId, 
				reportContents, date, verified).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/sites/{siteId}")
	public PathologySitesType getSites(
			@PathParam("siteId") String siteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetSitesCommand(siteId).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/lock/{siteId}/{minutes}")
	public RestBooleanReturnType setLockMinutes(
			@PathParam("siteId") String siteId,
			@PathParam("minutes") int minutes)
	throws MethodException, ConnectionException
	{
		return new PathologyPostLockMinutesCommand(siteId, minutes).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/lock/{siteId}")
	public RestIntegerType getLockMinutes(
			@PathParam("siteId") String siteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetLockMinutesCommand(siteId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/preferences/{siteId}/{label}/{userId}")
	public RestStringType getUserPreferences(
			@PathParam("siteId") String siteId,
			@PathParam("label") String label,
			@PathParam("userId") String userId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetUserPreferencesCommand(siteId, userId, label).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/preferences/{siteId}/{label}")
	public RestStringType getPreferences(
			@PathParam("siteId") String siteId,
			@PathParam("label") String label)
	throws MethodException, ConnectionException
	{
		return new PathologyGetUserPreferencesCommand(siteId, null, label).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/preferences/{siteId}/{label}")
	public RestBooleanReturnType savePreferences(
			@PathParam("siteId") String siteId,
			@PathParam("label") String label,
			RestStringType xml)
	throws MethodException, ConnectionException
	{
		return new PathologyPostUserPreferencesCommand(siteId, null, label, xml).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/preferences/{siteId}/{label}/{userId}")
	public RestBooleanReturnType saveUserPreferences(
			@PathParam("siteId") String siteId,
			@PathParam("label") String label,
			@PathParam("userId") String userId,
			RestStringType xml)
	throws MethodException, ConnectionException
	{
		return new PathologyPostUserPreferencesCommand(siteId, userId, label, xml).execute();
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/snomed/{caseId}")
	public PathologySnomedCodesType getSnomedCodes(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCaseSnomedCodesCommand(caseId).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/snomed/{caseId}/{tissueId}/{fieldId}")
	public RestStringType saveSnomedCode(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("fieldId") String fieldId)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCaseSnomedCodeCommand(caseId, 
				tissueId, fieldId).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/snomed/{caseId}/{tissueId}/{morphologyId}/{etiologyFieldId}")
	public RestStringType saveMorphologySnomedCode(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("morphologyId") String morphologyId,
			@PathParam("etiologyFieldId") String etiologyFieldId)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCaseMorphologySnomedCodeCommand(caseId, 
				tissueId, morphologyId, etiologyFieldId).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/cpt/{caseId}/{locationId}")
	public PathologyCptCodeResultsType saveCaseCptCodes(
			@PathParam("caseId") String caseId,
			@PathParam("locationId") String locationId,
			RestStringArrayType cptCodes)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCaseCptCodesCommand(caseId, locationId, cptCodes).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/cpt/{caseId}")
	public PathologyCptCodesType getCaseCptCodes(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetCaseCptCodesCommand(caseId).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/keys/{siteId}")
	public RestStringArrayType getUserKeys(
			@PathParam("siteId") String siteId)
	throws MethodException, ConnectionException
	{
		return new PathologyGetKeysCommand(siteId).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/cases")
	public PathologyCasesType getSpecificPathologyCases(
			RestStringArrayType caseIds)
	throws MethodException, ConnectionException
	{
		return new PathologyGetSpecificCasesCommand(caseIds).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/consultations/{siteId}/{stationNumber}")
	public RestBooleanReturnType checkConsultations(
			@PathParam("siteId") String siteId,
			@PathParam("stationNumber") String stationNumber)
	throws MethodException, ConnectionException
	{
		return new PathologyGetPendingConsultationStatusCommand(siteId, 
				stationNumber).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/tissues/{caseId}/{fieldId}")
	public RestStringType saveCaseTissues(
			@PathParam("caseId") String caseId,
			@PathParam("fieldId") String fieldId)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCaseTissuesCommandImpl(caseId, fieldId).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/copy/{siteId}/{caseId}")
	public PathologyCopyCaseResultType copyCase(
			@PathParam("siteId") String siteId,
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		return new PathologyPostCopyCaseCommand(siteId, caseId).execute();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/snomed/etiology/{caseId}/{tissueId}/{snomedId}/{etiologyId}")
	public RestBooleanReturnType deleteCaseSnomedEtiologyCode(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("snomedId") String snomedId,
			@PathParam("etiologyId") String etiologyId)
	throws MethodException, ConnectionException
	{
		return new PathologyDeleteCaseSnomedCodeCommand(caseId, tissueId, 
				snomedId, null, etiologyId).execute();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/snomed/{caseId}/{tissueId}/{snomedId}/{field}")
	public RestBooleanReturnType deleteCaseSnomedCode(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("snomedId") String snomedId,
			@PathParam("field") PathologyFieldType snomedField)
	throws MethodException, ConnectionException
	{
		return new PathologyDeleteCaseSnomedCodeCommand(caseId, tissueId, 
				snomedId, snomedField, null).execute();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/snomed/{caseId}/{tissueId}")
	public RestBooleanReturnType deleteCaseTissue(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId)
	throws MethodException, ConnectionException
	{
		return new PathologyDeleteCaseSnomedCodeCommand(caseId, tissueId, 
				null, null, null).execute();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@Path("pathology/case/note/{caseId}")
	public RestBooleanReturnType saveCaseNote(
			@PathParam("caseId") String caseId,
			RestStringType note)
	throws MethodException, ConnectionException
	{		
		return new PathologyPostCaseNoteCommand(caseId, note).execute();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("pathology/case/note/{caseId}")
	public RestStringType getCaseNote(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{		
		return new PathologyGetCaseNoteCommand(caseId).execute();
	}
}
