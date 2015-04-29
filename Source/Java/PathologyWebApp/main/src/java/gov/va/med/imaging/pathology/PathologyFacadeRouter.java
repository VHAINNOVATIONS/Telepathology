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
package gov.va.med.imaging.pathology;

import java.util.Date;
import java.util.List;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus;
import gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeed;
import gov.va.med.imaging.pathology.enums.PathologyField;
/**
 * @author VHAISWWERFEJ
 *
 */

@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface PathologyFacadeRouter
extends FacadeRouter
{
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyReadingSitesCommand")
	public abstract List<AbstractPathologySite> getPathologyReadingSites(
		RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyAcquisitionSitesCommand")
	public abstract List<AbstractPathologySite> getPathologyAcquisitionSites(
		RoutingToken routingToken)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCasesCommand")
	public abstract List<PathologyCase> getPathologyCases(
		RoutingToken routingToken, 
		boolean released, 
		int days)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCasesCommand")
	public abstract List<PathologyCase> getPathologyCases(
		RoutingToken routingToken, 
		boolean released, 
		int days, 
		String requestingSiteId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPatientInformationCommand")
	public abstract Patient getPatient(
			RoutingToken routingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyPatientCasesCommand")
	public abstract List<PathologyCase> getPatientCases(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		String requestingSiteId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyPatientCasesCommand")
	public abstract List<PathologyCase> getPatientCases(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseLockCommand")
	public abstract PathologyCaseUpdateAttributeResult lockPathologyCase(
			PathologyCaseURN pathologyCaseUrn, 
			boolean lock)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseAssistanceCommand")
	public abstract PathologyCaseUpdateAttributeResult addPathologyCaseAssistance(
			PathologyCaseURN pathologyCaseUrn, 
			PathologyCaseAssistance assistanceType, 
			String stationNumber)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCaseSpecimensCommand")
	public abstract List<PathologyCaseSpecimen> getPathologyCaseSpecimens(
			PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologySiteTemplateCommand")
	public abstract List<String> getPathologyTemplates(
			RoutingToken routingToken, 
			List<String> apSections)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyReadingSiteCommand")
	public abstract void updateReadingSite(
			RoutingToken routingToken, 
			PathologyReadingSite readingSite,
			boolean delete)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyAcquisitionSiteCommand")
	public abstract void updateAcquisitionSite(
			RoutingToken routingToken, 
			PathologyAcquisitionSite acquisitionSite,
			boolean delete)
	throws MethodException, ConnectionException;
		
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologySiteTemplateCommand")
	public abstract void saveSiteTemplate(
			RoutingToken routingToken,
			String apSection,
			String xmlTemplate)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCaseSupplementalReportsCommand")
	public abstract List<PathologyCaseSupplementalReport> getPathologyCaseSupplementalReports(
			PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCaseReportCommand")
	public abstract String getPathologyCaseReport(
			PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCaseTemplateDataCommand")
	public abstract PathologyCaseTemplate getPathologyCaseTemplateData(
			PathologyCaseURN pathologyCaseUrn, List<String> fields)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseReserveCommand")
	public abstract PathologyCaseReserveResult reservePathologyCase(
			PathologyCaseURN pathologyCaseUrn, 
			boolean lock)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyElectronicSignatureNeedCommand")
	public abstract PathologyElectronicSignatureNeed checkElectronicSignatureNeeded(
			RoutingToken routingToken, 
			String apSection)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyFieldValuesCommand")
	public abstract List<PathologyFieldValue> getPathologyFieldValues(
			RoutingToken routingToken, 
			PathologyField pathologyField,
			String searchParameter)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseConsultationStatusCommand")
	public void updatePathologyCaseConsultationStatus(
			PathologyCaseConsultationURN pathologyCaseConsultationUrn,
			PathologyCaseConsultationUpdateStatus pathologyCaseConsultationUpdateStatus)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseReportFieldsCommand")
	public PathologySaveCaseReportResult savePathologyCaseReportFields(
			PathologyCaseURN pathologyCaseUrn,
			List<PathologyCaseReportField> fields)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseSupplementalReportCommand")
	public void savePathologyCaseSupplementalReport(
			PathologyCaseURN pathologyCaseUrn, 
			String reportContents, 
			Date date, 
			boolean verified)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologySitesCommand")
	public List<PathologySite> getPathologySites(
			RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyLockMinutesCommand")
	public Integer getLockMinutes(
			RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyLockMinutesCommand")
	public void saveLockMinutes(
			RoutingToken routingToken, 
			int minutes)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyPreferencesCommand")
	public void saveUserPreferences(
			RoutingToken routingToken,
			String userId, 
			String label, 
			String xml)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyPreferencesCommand")
	public void savePreferences(
			RoutingToken routingToken,
			String label, 
			String xml)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyPreferencesCommand")
	public String getUserPreferences(
			RoutingToken routingToken,
			String userId, 
			String label)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyPreferencesCommand")
	public String getPreferences(
			RoutingToken routingToken,
			String label)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCaseSnomedCodesCommand")
	public List<PathologySnomedCode> getPathologyCaseSnomedCodes(
			PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseMorphologySnomedCodeCommand")
	public String savePathologyCaseMorphologySnomedCode(
			PathologyCaseURN pathologyCaseUrn, 
			String tissueId,
			String morphologyId, 
			PathologyFieldURN etiologyFieldUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseSnomedCodeCommand")
	public String savePathologyCaseSnomedCode(
			PathologyCaseURN pathologyCaseUrn, 
			String tissueId,
			PathologyFieldURN pathologyFieldUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseCptCodesCommand")
	public List<PathologyCptCodeResult> savePathologyCaseCptCodes(
			PathologyCaseURN pathologyCaseUrn, 
			PathologyFieldURN locationFieldUrn, 
			List<String> cptCodes)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCaseCptCodesCommand")
	public List<PathologyCptCode> getPathologyCaseCptCodes(
			PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyUserKeysCommand")
	public List<String> getPathologyKeys(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologySpecificCasesCommand")
	public List<PathologyCase> getSpecificCases(List<PathologyCaseURN> cases)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyPendingConsultationsCommand")
	public Boolean checkPendingConsultationStatus(RoutingToken routingToken, String stationNumber)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseTissueCommand")
	public String savePathologyCaseTissues(
			PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN pathologyTissueUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCopyCaseCommand")
	public PathologyCaseURN copyPathologyCase(
			RoutingToken globalRoutingToken,
			PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="DeletePathologyCaseEtiologySnomedCodeCommand")
	public void deleteSnomedEtiologyCode(
			PathologyCaseURN pathologyCaseUrn, 
			String tissueId, 
			String snomedId, 
			String etiologyId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="DeletePathologyCaseSnomedCodeCommand")
	public void deleteSnomedCode(
			PathologyCaseURN pathologyCaseUrn, 
			String tissueId, 
			String snomedId,
			PathologyField snomedField)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="DeletePathologyCaseTissueCommand")
	public void deleteTissue(
			PathologyCaseURN pathologyCaseUrn, 
			String tissueId)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="PostPathologyCaseNoteCommand")
	public void saveCaseNote(
			PathologyCaseURN pathologyCaseUrn, 
			String note)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetPathologyCaseNoteCommand")
	public String getCaseNote(
			PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
}
