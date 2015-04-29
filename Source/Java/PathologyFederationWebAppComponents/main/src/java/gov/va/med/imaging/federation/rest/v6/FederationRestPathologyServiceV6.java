/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 22, 2012
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
package gov.va.med.imaging.federation.rest.v6;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyAddCaseAssistanceCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyDeleteCaseSnomedCodeCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetAcquisitionSitesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetAllSitesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCaseCptCodesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCaseNoteCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCaseReportCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCaseSnomedCodesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCaseSpecimensCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCaseSupplementalReportsCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCaseTemplateDataCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetCasesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetElectronicSignatureNeededCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetFieldValuesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetKeysCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetLockMinutesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetPatientCasesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetPendingConsultationStatusCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetReadingSitesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetSpecificCasesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetTemplatesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyGetUserPreferencesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyLockCaseCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseConsultationStatusCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseCptCodesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseMorphologySnomedCodeCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseNoteCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseReportFieldsCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseSnomedCodeCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseSupplementalReportCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCaseTissuesCommandImpl;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostCopyCaseCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostLockMinutesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyPostUserPreferencesCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyReserveCaseCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologySaveTemplateCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyUpdateAcquisitionSiteCommand;
import gov.va.med.imaging.federation.commands.pathology.FederationPathologyUpdateReadingSiteCommand;
import gov.va.med.imaging.federation.pathology.rest.endpoints.PathologyFederationRestUri;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationAcquisitionSiteType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseAssistanceType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseConsultationUpdateStatusType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseReportFieldType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSaveSupplementalReportType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationFieldType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationReadingSiteType;
import gov.va.med.imaging.federation.rest.AbstractFederationRestService;
import gov.va.med.imaging.federation.rest.endpoints.FederationRestUri;
import gov.va.med.imaging.rest.types.RestStringArrayType;
import gov.va.med.imaging.rest.types.RestStringType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author VHAISWWERFEJ
 *
 */
@Path(FederationRestUri.federationRestUriV6 + "/" + PathologyFederationRestUri.pathologyServicePath)
public class FederationRestPathologyServiceV6
extends AbstractFederationRestService
{

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.rest.AbstractFederationRestService#getInterfaceVersion()
	 */
	@Override
	protected String getInterfaceVersion()
	{
		return "V6";
	}
	
	@GET
	@Path(PathologyFederationRestUri.getCasesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getCases(
			@PathParam("routingToken") String routingToken,
			@PathParam("released") boolean released,
			@PathParam("days") int days)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCasesCommand command = new FederationPathologyGetCasesCommand(routingToken, 
				released, days, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getCasesWithRequestingSitePath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getCases(
			@PathParam("routingToken") String routingToken,
			@PathParam("released") boolean released,
			@PathParam("days") int days,
			@PathParam("requestingSiteId") String requestingSiteId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCasesCommand command = new FederationPathologyGetCasesCommand(routingToken, 
				released, days, requestingSiteId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getCaseSpecimensPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getCaseSpecimens(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCaseSpecimensCommand command = new FederationPathologyGetCaseSpecimensCommand(
				caseId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getPatientCasesWithRequestingSitePath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getPatientCases(
			@PathParam("routingToken") String routingToken,
			@PathParam("requestingSiteId") String requestingSiteId,
			@PathParam("patientIcn") String patientIcn)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetPatientCasesCommand command = 
			new FederationPathologyGetPatientCasesCommand(routingToken, 
				patientIcn, requestingSiteId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getPatientCasesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getPatientCases(
			@PathParam("routingToken") String routingToken,
			@PathParam("patientIcn") String patientIcn)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetPatientCasesCommand command = 
			new FederationPathologyGetPatientCasesCommand(routingToken, 
				patientIcn, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getReadingSitesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getReadingSites(
			@PathParam("routingToken") String routingToken)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetReadingSitesCommand command = new FederationPathologyGetReadingSitesCommand(routingToken, 
				getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getAcquisitionSitesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getAcquisitionSites(
			@PathParam("routingToken") String routingToken)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetAcquisitionSitesCommand command = new FederationPathologyGetAcquisitionSitesCommand(routingToken, 
				getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.lockCasePath)
	@Produces(MediaType.APPLICATION_XML)
	public Response lockCase(
			@PathParam("caseId") String caseId,
			@PathParam("lock") boolean lock)
	throws MethodException, ConnectionException
	{
		FederationPathologyLockCaseCommand command = new FederationPathologyLockCaseCommand(caseId, 
				lock, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.addCaseAssistancePath)
	@Produces(MediaType.APPLICATION_XML)
	public Response addCaseAssistance(
			@PathParam("caseId") String caseId,
			@PathParam("assistanceType") PathologyFederationCaseAssistanceType assistanceType,
			@PathParam("stationNumber") String stationNumber)
	throws MethodException, ConnectionException
	{
		FederationPathologyAddCaseAssistanceCommand command = new FederationPathologyAddCaseAssistanceCommand(caseId, 
				assistanceType, stationNumber, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getTemplatesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getTemplates(
			@PathParam("routingToken") String routingToken,
			@PathParam("apSections") String apSections)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetTemplatesCommand command = new FederationPathologyGetTemplatesCommand(routingToken, 
				apSections, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.saveTemplatePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveTemplate(
			@PathParam("routingToken") String routingToken,
			@PathParam("apSection") String apSection,
			String xmlTemplate)
	throws MethodException, ConnectionException
	{
		FederationPathologySaveTemplateCommand command = 
			new FederationPathologySaveTemplateCommand(routingToken, 
				apSection, xmlTemplate, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.updateReadingSitesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateReadingSites(
			@PathParam("routingToken") String routingToken,
			@PathParam("delete") boolean delete,
			PathologyFederationReadingSiteType readingSite)
	throws MethodException, ConnectionException
	{
		FederationPathologyUpdateReadingSiteCommand command = 
			new FederationPathologyUpdateReadingSiteCommand(routingToken, 
				readingSite, delete, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.updateAcquisitionSitesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateAcquisitionSites(
			@PathParam("routingToken") String routingToken,
			@PathParam("delete") boolean delete,
			PathologyFederationAcquisitionSiteType acquisitionSite)
	throws MethodException, ConnectionException
	{
		FederationPathologyUpdateAcquisitionSiteCommand command = 
			new FederationPathologyUpdateAcquisitionSiteCommand(routingToken, 
					acquisitionSite, delete, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.userPreferencesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveUserPreferences(
			@PathParam("routingToken") String routingToken,
			@PathParam("label") String label,
			@PathParam("userId") String userId,
			RestStringType xml)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostUserPreferencesCommand command = 
			new FederationPathologyPostUserPreferencesCommand(routingToken, userId, label, xml, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.preferencesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response savePreferences(
			@PathParam("routingToken") String routingToken,
			@PathParam("label") String label,
			RestStringType xml)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostUserPreferencesCommand command = 
			new FederationPathologyPostUserPreferencesCommand(routingToken, null, label, xml, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.userPreferencesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getUserPreferences(
			@PathParam("routingToken") String routingToken,
			@PathParam("label") String label,
			@PathParam("userId") String userId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetUserPreferencesCommand command =
			new FederationPathologyGetUserPreferencesCommand(routingToken, userId, label, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	@GET
	@Path(PathologyFederationRestUri.preferencesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getPreferences(
			@PathParam("routingToken") String routingToken,
			@PathParam("label") String label)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetUserPreferencesCommand command =
			new FederationPathologyGetUserPreferencesCommand(routingToken, null, label, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getSnomedCodesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getCaseSnomedCodes(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCaseSnomedCodesCommand command = 
			new FederationPathologyGetCaseSnomedCodesCommand(caseId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.saveSnomedCodesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveCaseSnomedCode(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("fieldId") String fieldId)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseSnomedCodeCommand command = 
			new FederationPathologyPostCaseSnomedCodeCommand(caseId, tissueId, fieldId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.saveSnomedMorphologyCodesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveCaseSnomedCodeForMorphology(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("morphologyId") String morphologyId,
			@PathParam("etiologyFieldId") String etiologyFieldId)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseMorphologySnomedCodeCommand command = 
			new FederationPathologyPostCaseMorphologySnomedCodeCommand(caseId, tissueId, morphologyId, 
					etiologyFieldId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}

	@POST
	@Path(PathologyFederationRestUri.saveCptCodesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveCaseCptCodes(
			@PathParam("caseId") String caseId,
			@PathParam("locationId") String locationId,
			RestStringArrayType cptCodes)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseCptCodesCommand command = 
			new FederationPathologyPostCaseCptCodesCommand(caseId, locationId, cptCodes, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getCaseReportPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getPathologyCaseReport(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCaseReportCommand command = 
			new FederationPathologyGetCaseReportCommand(caseId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getCaseSupplementalReportPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getCaseSupplementalReports(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCaseSupplementalReportsCommand command =
			new FederationPathologyGetCaseSupplementalReportsCommand(caseId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.getCaseTemplateDataPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response getCaseTemplateData(
			@PathParam("caseId") String caseId,
			RestStringArrayType fields)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCaseTemplateDataCommand command = 
			new FederationPathologyGetCaseTemplateDataCommand(caseId, fields, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.reserveCasePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response reserveCase(
			@PathParam("caseId") String caseId,
			@PathParam("lock") Boolean lock)
	throws MethodException, ConnectionException
	{
		FederationPathologyReserveCaseCommand command = 
			new FederationPathologyReserveCaseCommand(caseId, lock, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.checkElectronicSignatureNeededPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response checkElectronicSignatureNeeded(
			@PathParam("routingToken") String routingToken,
			@PathParam("apSection") String apSection)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetElectronicSignatureNeededCommand command = 
			new FederationPathologyGetElectronicSignatureNeededCommand(routingToken, apSection, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.getFieldsPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response getPathologyFields(
			@PathParam("routingToken") String routingToken,
			@PathParam("field") PathologyFederationFieldType field,
			String searchParameter)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetFieldValuesCommand command = 
			new FederationPathologyGetFieldValuesCommand(routingToken, field, searchParameter, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.updateConsultationStatusPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response updateConsultationStatus(
			@PathParam("consultationId") String consultationId,
			@PathParam("status") PathologyFederationCaseConsultationUpdateStatusType status)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseConsultationStatusCommand command = 
			new FederationPathologyPostCaseConsultationStatusCommand(consultationId, status, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.saveCaseReportFieldsPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveCaseReportFields(
			@PathParam("caseId") String caseId,
			PathologyFederationCaseReportFieldType [] fields)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseReportFieldsCommand command = 
			new FederationPathologyPostCaseReportFieldsCommand(caseId, fields, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.saveCaseSupplementalReportPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveCaseSupplementalReport(
			@PathParam("caseId") String caseId,
			@PathParam("verified") Boolean verified,
			PathologyFederationCaseSaveSupplementalReportType report)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseSupplementalReportCommand command = 
			new FederationPathologyPostCaseSupplementalReportCommand(caseId, verified, report,
					getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getPathologySitesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getSites(
			@PathParam("routingToken") String routingToken)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetAllSitesCommand command = 
			new FederationPathologyGetAllSitesCommand(routingToken, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getLockExpiredMinutesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getLockExpiresMinutes(
			@PathParam("routingToken") String routingToken)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetLockMinutesCommand command = 
			new FederationPathologyGetLockMinutesCommand(routingToken, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.setLockExpiredMinutesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response setLockExpiresMinutes(
			@PathParam("routingToken") String routingToken,
			@PathParam("minutes") Integer minutes)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostLockMinutesCommand command = 
			new FederationPathologyPostLockMinutesCommand(routingToken, minutes, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getCptCodesPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getCaseCptCodes(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCaseCptCodesCommand command = 
			new FederationPathologyGetCaseCptCodesCommand(caseId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getUserKeysPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response getPathologyUserKeys(
			@PathParam("routingToken") String routingToken)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetKeysCommand command =
			new FederationPathologyGetKeysCommand(routingToken, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.getSpecificCasesPath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response getSpecificCases(
			RestStringArrayType caseUrns)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetSpecificCasesCommand command = 
			new FederationPathologyGetSpecificCasesCommand(caseUrns, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.checkPendingConsultationStatusPath)
	@Produces(MediaType.APPLICATION_XML)
	public Response checkPendingConsultationStatus(
			@PathParam("routingToken") String routingToken,
			@PathParam("stationNumber") String stationNumber)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetPendingConsultationStatusCommand command = 
			new FederationPathologyGetPendingConsultationStatusCommand(routingToken, 
					stationNumber, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.saveTissuePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveCaseTissues(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseTissuesCommandImpl command = 
			new FederationPathologyPostCaseTissuesCommandImpl(caseId, 
					tissueId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.copyCasePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response copyCase(
			@PathParam("routingToken") String routingToken,
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCopyCaseCommand command = 
			new FederationPathologyPostCopyCaseCommand(routingToken, caseId, 
					getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.deleteTissuePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response deleteTissue(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId)
	throws MethodException, ConnectionException
	{
		FederationPathologyDeleteCaseSnomedCodeCommand command = 
			new FederationPathologyDeleteCaseSnomedCodeCommand(caseId, tissueId, 
					null, null, null, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.deleteSnomedCodePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response deleteSnomedCode(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("snomedId") String snomedId,
			@PathParam("field") PathologyFederationFieldType field)
	throws MethodException, ConnectionException
	{
		FederationPathologyDeleteCaseSnomedCodeCommand command = 
			new FederationPathologyDeleteCaseSnomedCodeCommand(caseId, tissueId, 
					snomedId, field, null, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.deleteSnomedEtiologyCode)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response deleteSnomedEtiologyCode(
			@PathParam("caseId") String caseId,
			@PathParam("tissueId") String tissueId,
			@PathParam("snomedId") String snomedId,
			@PathParam("etiologyId") String etiologyId)
	throws MethodException, ConnectionException
	{
		FederationPathologyDeleteCaseSnomedCodeCommand command = 
			new FederationPathologyDeleteCaseSnomedCodeCommand(caseId, tissueId, 
					snomedId, null, etiologyId, getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@POST
	@Path(PathologyFederationRestUri.saveCaseNotePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response saveCaseNote(
			@PathParam("caseId") String caseId,
			RestStringType note)
	throws MethodException, ConnectionException
	{
		FederationPathologyPostCaseNoteCommand command = 
			new FederationPathologyPostCaseNoteCommand(caseId, note, 
					getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
	
	@GET
	@Path(PathologyFederationRestUri.getCaseNotePath)
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response getCaseNote(
			@PathParam("caseId") String caseId)
	throws MethodException, ConnectionException
	{
		FederationPathologyGetCaseNoteCommand command = 
			new FederationPathologyGetCaseNoteCommand(caseId, 
					getInterfaceVersion());
		return wrapResultWithResponseHeaders(command.execute());
	}
}
