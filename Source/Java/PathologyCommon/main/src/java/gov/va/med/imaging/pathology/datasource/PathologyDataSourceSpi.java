/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 5, 2012
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
package gov.va.med.imaging.pathology.datasource;

import java.util.Date;
import java.util.List;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.PathologyCaseSupplementalReport;
import gov.va.med.imaging.pathology.PathologyCaseTemplate;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseURN;
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

/**
 * Interface that defines the methods available related to Pathology
 * 
 * @author VHAISWWERFEJ
 *
 */
@SPI(description="Defines the interface for pathology operations.")
public interface PathologyDataSourceSpi 
extends VersionableDataSourceSpi
{
	
	/**
	 * Retrieve the pathology cases from a site
	 * @param globalRoutingToken The source to retrieve the cases from
	 * @param released Released or unreleased cases
	 * @param days The number of days to retrieve unreleased cases for
	 * @param requestingSiteId The requesting site ID for consultation sites, this may be null
	 * @return The list of cases that match the input
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<PathologyCase> getCases(RoutingToken globalRoutingToken, boolean released, int days, String requestingSiteId)
	throws MethodException, ConnectionException;
	
	/**
	 * Retrieve the specimens for a case
	 * @param pathologyCaseUrn The case identifier
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<PathologyCaseSpecimen> getCaseSpecimens(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException; 
	
	/**
	 * Retrieve the cases specific to a patient
	 * @param globalRoutingToken The source to retrieve the cases from
	 * @param patientIdentifier The patient identifier
	 * @param requestingSiteId The requesting site ID for consultation sites
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<PathologyCase> getPatientCases(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier, String requestingSiteId)
	throws MethodException, ConnectionException;
	
	/**
	 * Retrieve the list of pathology reading/acquisition sites
	 * @param globalRoutingToken The source to retrieve the sites from
	 * @param reading True for reading, false for acquisition sites
	 * @return list of sites
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<AbstractPathologySite> getSites(RoutingToken globalRoutingToken, boolean reading)
	throws MethodException, ConnectionException;
	
	/**
	 * Lock a pathology case
	 * @param pathologyCaseUrn The case identifier to lock
	 * @param lock True to lock, false to unlock
	 * @return Result of the action (indicates if action was successful)
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract PathologyCaseUpdateAttributeResult lockCase(PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException;
	
	/**
	 * Request assistance for a case
	 * @param pathologyCaseUrn The case identifier to add assistance for
	 * @param assistanceType The type of assistance to add
	 * @param stationNumber The station number of the site to do the assistance
	 * @return Result of the action (indicates if action was successful)
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract PathologyCaseUpdateAttributeResult addCaseAssistance(PathologyCaseURN pathologyCaseUrn, PathologyCaseAssistance assistanceType, String stationNumber)
	throws MethodException, ConnectionException;
	
	/**
	 * Get template information for AP sections
	 * @param globalRoutingToken The source to retrieve the templates from
	 * @param apSections List of AP sections to retrieve templates for
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<String> getSiteTemplate(RoutingToken globalRoutingToken, List<String> apSections)
	throws MethodException, ConnectionException;
	
	/**
	 * Save a site template in XML
	 * @param globalRoutingToken The source to save the template to
	 * @param xmlTemplate
	 * @param apSection
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void saveSiteTemplate(RoutingToken globalRoutingToken, String xmlTemplate, String apSection)
	throws MethodException, ConnectionException;
	
	/**
	 * Update reading site list
	 * @param globalRoutingToken
	 * @param readingSites
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void updateReadingSite(RoutingToken globalRoutingToken, PathologyReadingSite readingSite, boolean delete)
	throws MethodException, ConnectionException;
	
	/**
	 * Update acquisition site list
	 * @param globalRoutingToken
	 * @param acquisitionSites
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void updateAcquisitionSite(RoutingToken globalRoutingToken, PathologyAcquisitionSite acquisitionSite, boolean delete)
	throws MethodException, ConnectionException;
	
	/**
	 * Get a pathology case report
	 * @param pathologyCaseUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract String getPathologyCaseReport(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Get case supplemental reports
	 * @param pathologyCaseUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<PathologyCaseSupplementalReport> getCaseSupplementalReports(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Get case template field values
	 * @param pathologyCaseUrn
	 * @param fields
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public PathologyCaseTemplate getCaseTemplateData(PathologyCaseURN pathologyCaseUrn, List<String> fields)
	throws MethodException, ConnectionException;
	
	/**
	 * Reserve a case
	 * @param pathologyCaseUrn
	 * @param lock
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public PathologyCaseReserveResult reserveCase(PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException;
	
	/**
	 * Determine if the logged in user needs an electronic signature for a specific AP section
	 * @param routingToken
	 * @param apSection
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public PathologyElectronicSignatureNeed checkElectronicSignatureNeeded(RoutingToken routingToken, 
			String apSection)
	throws MethodException, ConnectionException;
	
	/**
	 * Get pathology field values
	 * @param globalRoutingToken
	 * @param pathologyField
	 * @param searchParameter
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<PathologyFieldValue> getPathologyFields(RoutingToken globalRoutingToken, 
			PathologyField pathologyField, String searchParameter)
	throws MethodException, ConnectionException;
	
	/**
	 * Update the case consultation status
	 * @param pathologyCaseConsultationUrn
	 * @param consultationUpdateStatus
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public void updateConsultationStatus(PathologyCaseConsultationURN pathologyCaseConsultationUrn,
			PathologyCaseConsultationUpdateStatus consultationUpdateStatus)
	throws MethodException, ConnectionException;
	
	/**
	 * Save report fields for a case
	 * @param pathologyCaseUrn
	 * @param fields
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public PathologySaveCaseReportResult saveCaseReportFields(PathologyCaseURN pathologyCaseUrn, List<PathologyCaseReportField> fields)
	throws MethodException, ConnectionException;
	
	/**
	 * Save a supplemental report for a case
	 * @param pathologyCaseUrn
	 * @param reportContents
	 * @param date
	 * @param verified
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public void saveCaseSupplementalReport(PathologyCaseURN pathologyCaseUrn, String reportContents, Date date, boolean verified)
	throws MethodException, ConnectionException;
	
	/**
	 * Get all sites in the system (not pathology specific)
	 * @param globalRoutingToken
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<PathologySite> getSites(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	/**
	 * Get the lock expire timeout (in minutes)
	 * @param globalRoutingToken
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public Integer getLockExpiresMinutes(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	/**
	 * Set the lock expired timeout (in minutes)
	 * @param globalRoutingToken
	 * @param hours
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public void setLockExpiresMinutes(RoutingToken globalRoutingToken, int minutes)
	throws MethodException, ConnectionException;
	
	/**
	 * Get user preferences
	 * @param globalRoutingToken
	 * @param userId
	 * @param label
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public String getPreferences(RoutingToken globalRoutingToken, String userId, String label)
	throws MethodException, ConnectionException;
	
	/**
	 * Save user preferences
	 * @param globalRoutingToken
	 * @param userId
	 * @param label
	 * @param xml
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public void savePreferences(RoutingToken globalRoutingToken, String userId, String label, String xml)
	throws MethodException, ConnectionException;
	
	/**
	 * Get snomed codes for a case
	 * @param pathologyCaseUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<PathologySnomedCode> getCaseSnomedCodes(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Save a Snomed code for a case
	 * @param pathologyCaseUrn 
	 * @param topographyId
	 * @param morphologyId
	 * @param pathologyFieldUrn
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public String saveCaseSnomedCode(PathologyCaseURN pathologyCaseUrn, String tissueId, PathologyFieldURN pathologyFieldUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Save etiology fields to an existing morphology
	 * @param pathologyCaseUrn
	 * @param tissueId
	 * @param morphologyId
	 * @param etiologyFieldUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public String saveCaseEtiologySnomedCodeForMorphology(PathologyCaseURN pathologyCaseUrn, String tissueId, String morphologyId, PathologyFieldURN etiologyFieldUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Save tissues for a specific case
	 * @param pathologyCaseUrn
	 * @param tissueFieldUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public String saveCaseTissue(PathologyCaseURN pathologyCaseUrn, PathologyFieldURN tissueFieldUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Save CPT codes for a specific case
	 * @param pathologyCaseUrn
	 * @param locationFieldUrn
	 * @param cptCodes
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<PathologyCptCodeResult> saveCaseCptCodes(PathologyCaseURN pathologyCaseUrn, PathologyFieldURN locationFieldUrn, List<String> cptCodes)
	throws MethodException, ConnectionException;
	
	/**
	 * Get CPT codes for a specific case
	 * @param pathologyCaseUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<PathologyCptCode> getCaseCptCodes(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Get pathology user keys (not MAG keys)
	 * @param globalRoutingToken
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<String> getPathologyUserKeys(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;

	/**
	 * Get specific cases
	 * @param cases
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public List<PathologyCase> getSpecificCases(List<PathologyCaseURN> cases)
	throws MethodException, ConnectionException;
	
	/**
	 * Returns true if there are pending consultations for the stationNumber site, false if there are none
	 * @param globalRoutingToken
	 * @param stationNumber
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public Boolean checkPendingConsultationStatus(RoutingToken globalRoutingToken, String stationNumber)
	throws MethodException, ConnectionException;
	
	/**
	 * Copy an existing case to another site, used for workload credit
	 * @param globalRoutingToken The routing token that represents the site to create the new case at
	 * @param pathologyCaseUrn The URN of the original case that is being copied
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public PathologyCaseURN copyCase(RoutingToken globalRoutingToken, PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
	
	public void deleteSnomedCode(PathologyCaseURN pathologyCaseUrn, String tissueId, String snomedId, 
			PathologyField snomedField, String etiologyId)
	throws MethodException, ConnectionException;
	
	public void saveCaseNote(PathologyCaseURN pathologyCaseUrn, String note)
	throws MethodException, ConnectionException;
	
	public String getCaseNote(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException;
}
