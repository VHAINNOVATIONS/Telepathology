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
using System.Configuration;
using VistA.Imaging.Telepathology.Communication;
using VistA.Imaging.Telepathology.Worklist.ViewModel;
using System.Text.RegularExpressions;
using System.IO;
using System.Diagnostics;
using VistA.Imaging.Telepathology.Logging;
using VistA.Imaging.Telepathology.Common.VixModels;
using System.Collections.ObjectModel;

namespace VistA.Imaging.Telepathology.Worklist.DataSource
{
    public class WorkListDataSource : IWorkListDataSource
    {
        private static MagLogger Log = new MagLogger(typeof(WorkListDataSource));


        class CaseState
        {
            public string LockState { get; set; }
            public string Status { get; set; }
        }

        Dictionary<string, CaseState> _dictionary = new Dictionary<string, CaseState>();

        public string UserInitials { get { return "SPK"; } }
        public string SiteID { get { return "600"; } }
        public string SiteName { get { return "Salt Lake City"; } }

        private BrokerClient client = new BrokerClient();

        private ViXClient vixClient = new ViXClient();

        private VistAClient vistaClient = new VistAClient();

        public WorkListDataSource()
        {
        }

        public void InitializeConnection()
        {
            Log.Info("Intializing connection...");

            vistaClient.InitializeConnection();

            Log.Info("Intializing connection...completed.");
        }

        public void GetMagSecurityKeys()
        {
            vistaClient.GetMagSecurityKeys();
        }

        public VERGENCECONTEXTORLib.Contextor Contextor
        {
            get
            {
                return this.vistaClient.Contextor;
            }
        }

        public void Close()
        {
            if (this.vistaClient != null)
            {
                this.vistaClient.Close();
            }
        }

        public int GetApplicationTimeout()
        {
            return vistaClient.GetApplicationTimeout();
        }

        public CaseList GetUnreleasedCases()
        {
            CaseList caseList = null;

            caseList = vistaClient.GetUnreleasedCases();

            return caseList;
        }

        public CaseList GetReleasedCases()
        {
            return vistaClient.GetReleasedCases();
        }

        public CaseList GetPatientCases(string patientICN)
        {
            return vistaClient.GetPatientCases(patientICN);
        }

        public void UpdateCases(CaseList cases)
        {
            vistaClient.UpdateCases(cases);
        }

        public Patient GetPatient(string siteID, string patientDFN)
        {
            // get patient information using local site
            return vistaClient.GetPatient(siteID, patientDFN);
        }

        public bool IsPatientRegisteredAtSite(string siteID, string patientICN)
        {
            return vistaClient.IsPatientRegisteredAtSite(siteID, patientICN);
        }

        public HealthSummaryTypeList GetHealthSummaryTypeList(string siteID)
        {
            return vistaClient.GetHealthSummaryTypeList(siteID);
        }

        public string GetHealthSummary(string patientICN, string healthSummaryType)
        {
            return vistaClient.GetHealthSummary(patientICN, healthSummaryType);
        }

        public string GetNotes(string caseURN)
        {
            return vistaClient.GetNotes(caseURN);
        }

        public void SaveNotes(string caseURN, string notes)
        {
            vistaClient.SaveNotes(caseURN, notes);
        }

        public void SavePreferences(string label, string data)
        {
            vistaClient.SavePreferences(label, data);
        }

        public string ReadPreferences(string label)
        {
            return vistaClient.ReadPreferences(label);
        }


        #region REPORTING

        public string GetCPRSReport(string caseURN)
        {
            return vistaClient.GetCPRSReport(caseURN);
        }

        public Report GetReport(CaseListItem caseObject)
        {
            return vistaClient.GetReport(caseObject);
        }

        public string GetReportFieldData(string fieldNumber, string caseURN)
        {
            return vistaClient.GetReportFieldData(fieldNumber, caseURN);
        }

        public SupplementaryReportModel GetSupplementalReports(string caseURN)
        {
            return vistaClient.GetSupplementalReports(caseURN);
        }

        public void SaveSupReport(string caseID, string datetime, bool verified, string data)
        {
            vistaClient.SaveSupReport(caseID, datetime, verified, data);
        }

        public PathologySaveCaseReportResultType SaveReportChanges(string caseURN, PathologyCaseReportFieldsType changeList)
        {
            return vistaClient.SaveReportChanges(caseURN, changeList);
        }

        public ReadingSiteList GetReadingSites(string siteID)
        {
            return vistaClient.GetReadingSites(siteID);
        }

        public PathologyElectronicSignatureNeedType GetESignatureStatus(string siteID, string reportType)
        {
            return vistaClient.GetESignatureStatus(siteID, reportType);
        }

        public bool VerifyESignature(string siteID, string eSignature)
        {
            return vistaClient.VerifyESignature(siteID, eSignature);
        }

        public PatientSensitiveValueType GetPatientSensitiveLevel(string siteID, string patientICN)
        {
            return vistaClient.GetPatientSensitiveLevel(siteID, patientICN);
        }

        public void PatientSensitiveAccessLog(string siteID, string patientICN)
        {
            vistaClient.PatientSensitiveAccessLog(siteID, patientICN);
        }

        public string CreateCopyCase(string destinationSiteID, string sourceCaseURN)
        {
            return vistaClient.CreateCopyCase(destinationSiteID, sourceCaseURN);
        }

        #endregion

        public void ReserveCase(string caseURN, bool reserveCase)
        {
            vistaClient.ReserveCase(caseURN, reserveCase);
        }

        public bool RequestInterpretation(string caseURN, string siteID)
        {
            return vistaClient.RequestInterpretation(caseURN, siteID);
        }

        public bool RequestConsultation(string caseURN, ReadingSiteInfo consultingSite)
        {
            return vistaClient.RequestConsultation(caseURN, consultingSite);
        }

        public void SetPatientContext(string externalApp, string patientDFN)
        {
            try
            {
                Process process = new Process();

                process.StartInfo.UseShellExecute = false;
                process.StartInfo.RedirectStandardOutput = false;
                process.StartInfo.RedirectStandardError = false;
                process.StartInfo.CreateNoWindow = true;
                process.StartInfo.FileName = externalApp;
                process.StartInfo.Arguments = string.Format("{0} {1} {2} {3}", patientDFN, 0, client.ServerName, client.ServerPort);

                process.Start();
            }
            catch (Exception)
            {
            }
        }

        public void UpdateConsultationStatus(string consultationID, string status)
        {
            vistaClient.UpdateConsultationStatus(consultationID, status);
        }

        public CaseSpecimenList GetCaseDetail(string caseURN)
        {
            return vistaClient.GetCaseDetail(caseURN);
        }

        public PathologyCaseUpdateAttributeResultType LockCaseForEditing(string caseURN, bool lockCase)
        {
            return vistaClient.LockCaseForEditing(caseURN, lockCase);
        }

        public string GetReportLockTimeoutHour(string siteID)
        {
            return vistaClient.GetReportLockTimeoutHour(siteID);
        }

        public PathologySnomedCodesType GetSnomedCodeForCase(string caseURN)
        {
            return vistaClient.GetSnomedCodeForCase(caseURN);
        }

        public PathologyFieldValuesType SearchPathologyItems(string siteID, string fieldType, string searchParameter)
        {
            return vistaClient.SearchPathologyItems(siteID, fieldType, searchParameter);
        }

        public string AddSnomedItemToOrganTissue(string caseURN, string organID, string fieldURN)
        {
            return vistaClient.AddSnomedItemToOrganTissue(caseURN, organID, fieldURN);
        }

        public string AddSnomedEtiologyToMorphology(string caseURN, string organID, string morphologyID, string etiologyURN)
        {
            return vistaClient.AddSnomedEtiologyToMorphology(caseURN, organID, morphologyID, etiologyURN);
        }

        public string AddSnomedOrganTissue(string caseURN, string organURN)
        {
            return vistaClient.AddSnomedOrganTissue(caseURN, organURN);
        }

        public void RemoveSnomedEtiology(string caseID, string organID, string snomedID, string etiologyID)
        {
            vistaClient.RemoveSnomedEtiology(caseID, organID, snomedID, etiologyID);
        }

        public void RemoveSnomedField(string caseID, string organID, string snomedID, string field)
        {
            vistaClient.RemoveSnomedField(caseID, organID, snomedID, field);
        }

        public void RemoveSnomedOrganTissue(string caseID, string organID)
        {
            vistaClient.RemoveSnomedOrganTissue(caseID, organID);
        }

        public PathologyCptCodesType GetCptCodesForCase(string caseURN)
        {
            return vistaClient.GetCptCodesForCase(caseURN);
        }

        public PathologyCptCodeResultsType AddCptCodesForCase(string caseURN, string locationURN, ObservableCollection<string> cptCodes)
        {
            return vistaClient.AddCptCodesForCase(caseURN, locationURN, cptCodes);
        }
    }
}
