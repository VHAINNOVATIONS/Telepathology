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
package gov.va.med.imaging.federation.pathology.rest.endpoints;

/**
 * URI endpoints for Pathology Federation
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PathologyFederationRestUri
{

	/**
	 * Service application
	 */
	public final static String pathologyServicePath = "pathology"; 
	
	public final static String getCasesPath = "cases/{routingToken}/{released}/{days}";
	public final static String getCasesWithRequestingSitePath = "cases/{routingToken}/{released}/{days}/{requestingSiteId}";
	public final static String getCaseSpecimensPath = "case/specimens/{caseId}";
	public final static String getPatientCasesWithRequestingSitePath = "cases/patient/{routingToken}/{requestingSiteId}/{patientIcn}";
	public final static String getPatientCasesPath = "cases/patient/{routingToken}/{patientIcn}";
	public final static String getReadingSitesPath = "sites/reading/{routingToken}";
	public final static String getAcquisitionSitesPath = "sites/acqusition/{routingToken}";
	
	public final static String lockCasePath = "case/lock/{caseId}/{lock}";
	
	public final static String addCaseAssistancePath = "case/assistance/{caseId}/{assistanceType}/{stationNumber}";
	
	public final static String getTemplatesPath = "templates/{routingToken}/{apSections}";
	public final static String saveTemplatePath = "template/{routingToken}/{apSection}";
	
	public final static String updateReadingSitesPath = "sites/reading/{routingToken}/{delete}";
	public final static String updateAcquisitionSitesPath = "sites/acquisition/{routingToken}/{delete}";
	
	public final static String userPreferencesPath = "preferences/{routingToken}/{label}/{userId}";
	public final static String preferencesPath = "preferences/{routingToken}/{label}";
	
	public final static String getSnomedCodesPath = "snomed/{caseId}";
	
	public final static String saveSnomedCodesPath = "snomed/{caseId}/{tissueId}/{fieldId}";
	
	public final static String saveSnomedMorphologyCodesPath = "snomed/morphology/{caseId}/{tissueId}/{morphologyId}/{etiologyFieldId}";
	
	public final static String saveCptCodesPath = "cpt/{caseId}/{locationId}";
	public final static String getCptCodesPath = "cpt/{caseId}";
	
	public final static String getCaseReportPath = "case/report/{caseId}";
	
	public final static String getCaseSupplementalReportPath = "case/supplementalreport/{caseId}";
	public final static String getCaseTemplateDataPath = "case/template/{caseId}";
	
	public final static String reserveCasePath = "case/reserve/{caseId}/{lock}";
	
	public final static String checkElectronicSignatureNeededPath = "electronicSignature/{routingToken}/{apSection}";
	
	public final static String getFieldsPath = "fields/{routingToken}/{field}";
	
	public final static String updateConsultationStatusPath = "consultation/{consultationId}/{status}";
	
	public final static String saveCaseReportFieldsPath = "case/report/fields/{caseId}";
	public final static String saveCaseSupplementalReportPath = "case/supplementalreport/{caseId}/{verified}";
	
	public final static String getPathologySitesPath = "sites/{routingToken}";
	
	public final static String getLockExpiredMinutesPath = "lock/expired/{routingToken}";
	
	public final static String setLockExpiredMinutesPath = "lock/expired/{routingToken}/{minutes}";
	
	public final static String getUserKeysPath = "user/keys/{routingToken}";
	
	public final static String getSpecificCasesPath = "cases";
	
	public final static String checkPendingConsultationStatusPath = "consultations/{routingToken}/{stationNumber}";
	
	public final static String saveTissuePath = "tissues/{caseId}/{tissueId}";
	
	public final static String copyCasePath = "case/copy/{routingToken}/{caseId}";

	public final static String deleteTissuePath = "tissues/delete/{caseId}/{tissueId}";
	
	public final static String deleteSnomedCodePath = "snomed/delete/{caseId}/{tissueId}/{snomedId}/{field}";
	
	public final static String deleteSnomedEtiologyCode = "snomed/etiology/delete/{caseId}/{tissueId}/{snomedId}/{etiologyId}";
	
	public final static String saveCaseNotePath = "case/note/{caseId}";
	
	public final static String getCaseNotePath = "case/note/{caseId}";
	
}


