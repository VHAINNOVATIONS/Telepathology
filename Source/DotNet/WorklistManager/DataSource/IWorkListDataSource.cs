/**
 * 
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: 1/9/2012
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * Developer:  Paul Pentapaty
 * Description: 
 * 
 *       ;; +--------------------------------------------------------------------+
 *       ;; Property of the US Government.
 *       ;; No permission to copy or redistribute this software is given.
 *       ;; Use of unreleased versions of this software requires the user
 *       ;;  to execute a written test agreement with the VistA Imaging
 *       ;;  Development Office of the Department of Veterans Affairs,
 *       ;;  telephone (301) 734-0100.
 *       ;;
 *       ;; The Food and Drug Administration classifies this software as
 *       ;; a Class II medical device.  As such, it may not be changed
 *       ;; in any way.  Modifications to this software may result in an
 *       ;; adulterated medical device under 21CFR820, the use of which
 *       ;; is considered to be a violation of US Federal Statutes.
 *       ;; +--------------------------------------------------------------------+
 *       
 * 
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using VistA.Imaging.Telepathology.Common.Model;
using VistA.Imaging.Telepathology.Worklist.ViewModel;
using VistA.Imaging.Telepathology.Common.VixModels;
using System.Collections.ObjectModel;

namespace VistA.Imaging.Telepathology.Worklist.DataSource
{
    public interface IWorkListDataSource
    {
        void InitializeConnection();

        void GetMagSecurityKeys();

        int GetApplicationTimeout();

        CaseList GetUnreleasedCases();

        CaseList GetReleasedCases();

        CaseList GetPatientCases(string patientICN);

        void UpdateCases(CaseList cases);

        Patient GetPatient(string siteID, string patientICN);

        bool IsPatientRegisteredAtSite(string siteID, string patientICN);

        HealthSummaryTypeList GetHealthSummaryTypeList(string siteID);

        string GetHealthSummary(string patientICN, string healthSummaryType);

        string GetNotes(string caseURN);

        void SaveNotes(string caseURN, string notes);

        void ReserveCase(string caseURN, bool reserveCase);

        bool RequestInterpretation(string caseURN, string siteID);

        bool RequestConsultation(string caseID, ReadingSiteInfo consultingSite);

        // CaseConsultationList GetInterpretationList(string siteID, string accessionNumber);

        string UserInitials { get; }

        string SiteID { get; }

        string SiteName { get; }

        VERGENCECONTEXTORLib.Contextor Contextor { get; }

        void SavePreferences(string label, string data);

        string ReadPreferences(string label);

        void Close();

        #region REPORTING

        string GetCPRSReport(string caseURN);

        Report GetReport(CaseListItem caseObject);

        string GetReportFieldData(string fieldNumber, string caseURN);

        SupplementaryReportModel GetSupplementalReports(string caseURN);

        PathologySaveCaseReportResultType SaveReportChanges(string caseURN, PathologyCaseReportFieldsType changeList);

        void SaveSupReport(string caseID, string datetime, bool verified, string data);

        ReadingSiteList GetReadingSites(string siteID);

        PathologyElectronicSignatureNeedType GetESignatureStatus(string siteID, string reportType);

        bool VerifyESignature(string siteID, string eSignature);

        PatientSensitiveValueType GetPatientSensitiveLevel(string siteID, string patientICN);

        void PatientSensitiveAccessLog(string siteID, string patientICN);

        PathologySnomedCodesType GetSnomedCodeForCase(string caseURN);

        PathologyFieldValuesType SearchPathologyItems(string siteID, string fieldType, string searchParameter);

        string AddSnomedItemToOrganTissue(string caseURN, string organID, string fieldURN);

        string AddSnomedEtiologyToMorphology(string caseURN, string organID, string morphologyID, string etiologyURN);

        string AddSnomedOrganTissue(string caseURN, string organURN);

        void RemoveSnomedEtiology(string caseID, string organID, string snomedID, string etiologyID);

        void RemoveSnomedField(string caseID, string organID, string snomedID, string field);

        void RemoveSnomedOrganTissue(string caseID, string organID);

        PathologyCptCodesType GetCptCodesForCase(string caseURN);

        PathologyCptCodeResultsType AddCptCodesForCase(string caseURN, string locationURN, ObservableCollection<string> cptCodes);

        string CreateCopyCase(string destinationSiteID, string sourceCaseURN);

        #endregion

        void SetPatientContext(string externalApp, string patientDFN);

        void UpdateConsultationStatus(string consultationID, string status);

        CaseSpecimenList GetCaseDetail(string caseURN);

        CaseSlideList GetCaseSlidesInfo(string caseURN);

        PathologyCaseUpdateAttributeResultType LockCaseForEditing(string caseURN, bool lockCase);

        string GetReportLockTimeoutHour(string siteID);
    }
}
