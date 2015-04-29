// -----------------------------------------------------------------------
// <copyright file="PathologyAcquisitionSiteType.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: Jan 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Paul Pentapaty, Duc Nguyen
//  Description: 
//        ;; +--------------------------------------------------------------------+
//        ;; Property of the US Government.
//        ;; No permission to copy or redistribute this software is given.
//        ;; Use of unreleased versions of this software requires the user
//        ;;  to execute a written test agreement with the VistA Imaging
//        ;;  Development Office of the Department of Veterans Affairs,
//        ;;  telephone (301) 734-0100.
//        ;;
//        ;; The Food and Drug Administration classifies this software as
//        ;; a Class II medical device.  As such, it may not be changed
//        ;; in any way.  Modifications to this software may result in an
//        ;; adulterated medical device under 21CFR820, the use of which
//        ;; is considered to be a violation of US Federal Statutes.
//        ;; +--------------------------------------------------------------------+
// </copyright>
// -----------------------------------------------------------------------

namespace VistA.Imaging.Telepathology.Communication
{
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    using VistA.Imaging.Telepathology.Common.Model;
    using VistA.Imaging.Telepathology.Common.VixModels;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    public interface IVistAClient
    {
        void InitializeConnection();

        void Close();

        void GetMagSecurityKeys();

        CaseList GetUnreleasedCases();

        CaseList GetReleasedCases();

        CaseList GetPatientCases(string patientICN);

        Patient GetPatient(string siteID, string patientICN);

        string GetNotes(string caseURN);

        void SaveNotes(string caseURN, string notes);

        bool IsPatientRegisteredAtSite(string siteID, string patientICN);

        void ReserveCase(string caseURN, bool reserveCase);

        bool RequestInterpretation(string caseURN, string siteID);

        bool RequestConsultation(string caseURN, ReadingSiteInfo consultingSite);

        void SavePreferences(string label, string data);

        string ReadPreferences(string label);

        #region REPORTING

        string GetCPRSReport(string caseURN);

        Report GetReport(CaseListItem caseObject);

        SupplementaryReportModel GetSupplementalReports(string caseURN);

        PathologySaveCaseReportResultType SaveReportChanges(string caseURN, PathologyCaseReportFieldsType changeList);

        void SaveSupReport(string caseID, string datetime, bool verified, string data);

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

        #region CONFIGURATOR

        int GetApplicationTimeout();

        void SetApplicationTimeout(int duration);

        int GetRetentionDays();

        void SetRetentionDays(int duration);

        bool IsPrimarySiteValid(string stationNumber);

        ObservableCollection<SiteInfo> GetInstitutionList();

        AcquisitionSiteList GetAcquisitionSites(string siteStationNumber);

        ReadingSiteList GetReadingSites(string siteStationNumber);

        void SaveReadingSite(string siteStationNumber, PathologyReadingSiteType readingSite);

        void RemoveReadingSite(string siteStationNumber, PathologyReadingSiteType readingSite);

        void SaveAcquisitionSite(string siteStationNumber, PathologyAcquisitionSiteType acquisitionSite);

        void RemoveAcquisitionSite(string siteStationNumber, PathologyAcquisitionSiteType acquisitionSite);

        void SaveReportTemplate(VixReportTemplateObject templateObj);

        ObservableCollection<ReportTemplate> GetReportTemplates(string siteID);

        bool CheckPendingConsultation(string acquisitionSiteID, string readingSiteID);

        string GetReportLockTimeoutHour(string siteID);

        void SetReportLockTimeoutHour(string siteID, string hours);

        #endregion

        void UpdateConsultationStatus(string consultationID, string status);

        CaseSpecimenList GetCaseDetail(string caseURN);

        PathologyCaseUpdateAttributeResultType LockCaseForEditing(string caseURN, bool lockCase);

        bool IsSiteSupportTelepathology(string SiteStationNumber);
    }
}
