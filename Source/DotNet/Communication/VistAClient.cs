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
    using System;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    using System.Linq;
    using VistA.Imaging.Telepathology.Common.Exceptions;
    using VistA.Imaging.Telepathology.Common.Model;
    using VistA.Imaging.Telepathology.Common.VixModels;
    using VistA.Imaging.Telepathology.Logging;
    using System.Threading.Tasks;
    using System.Threading;
    using System.Diagnostics;
    using System.Windows;
using System.Collections.Concurrent;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    public class VistAClient : IVistAClient
    {
        private static MagLogger Log = new MagLogger(typeof(VistAClient));

        private BrokerClient client = new BrokerClient();

        private ViXClient vixClient = null;

        public void InitializeConnection()
        {
            Log.Info("Initializing communications with VistA...");

            UserContext.ApplicationContext = "MAGTP_WORKLIST_MGR";

            // login to local site
            if (client.AuthenticateUser())
            {
                try
                {
                    Log.Info("Initializing broker connection...");
            
                    client.SetApplicationContext();

                    client.UpdateSecurityToken();

                    client.InitializeUserContext();

                    client.GetMagSecurityKeys();

                    Log.Info("Broker connection initialized succesfully.");
                }
                catch (MagBrokerFailureException bfe)
                {
                    string msg = "Broker initialization failed.";
                    Log.Error(msg, bfe);
                    throw new MagInitializationFailureException(msg, bfe);
                }

                // Exit the application if the site doesn't have a VIX service
                if (string.IsNullOrWhiteSpace(UserContext.SiteServiceUrl))
                {
                    throw new MagInitializationFailureException("VIX initialization failed. ERR: Site Service URL is not available.");
                }

                try
                {
                    // initialize Vix 
                    vixClient = new ViXClient();

                    vixClient.InitializeConnection();

                    // initlaizes acquisition site information for the current site
                    vixClient.InitializeSiteInformation();
                }
                catch (Exception ex)
                {
                    vixClient = null;
                    throw new MagInitializationFailureException("VIX initialization failed.", ex);
                }
            }
            else
            {
                throw new MagInitializationFailureException("Application cannot be initialized. ERR: Authentication cannot be completed.");
            }
        }

        public void Close()
        {
            this.client.Close();
        }

        public string ServerName
        {
            get
            {
                return this.client.ServerName;
            }
        }

        public int ServerPort
        {
            get
            {
                return this.client.ServerPort;
            }
        }

        public VERGENCECONTEXTORLib.Contextor Contextor
        {
            get
            {
                return client.Contextor;
            }
        }

        public void GetMagSecurityKeys()
        {
            if (client != null)
            {
                client.GetMagSecurityKeys();
            }
        }

        /// <summary>
        /// Retrieve a list of unread cases from vista
        /// </summary>
        /// <param name="siteID">site id to retrieve the list from</param>
        /// <returns>a case list of the site</returns>
        public CaseList GetUnreleasedCases()
        {
            // consolidate unread list
            CaseList unreadList = new CaseList();

            if (vixClient != null)
            {
                // get the retention day range
                int dayRange = client.GetRetentionDays();

                // gather cases from each acquisition site
                List<string> grabbedSites = new List<string>();
                var watch = Stopwatch.StartNew();
                foreach (AcquisitionSiteInfo site in UserContext.AcquisitionList.Items)
                {
                    // only retrieve active site that hasn't been retrieved yet
                    // since can have duplicate primary acquisition site
                    if ((site.Active) && (!grabbedSites.Contains(site.PrimeSiteStationNumber)))
                    {
                        CaseList siteCaseList = vixClient.GetUnreleasedCases(site.PrimeSiteStationNumber, dayRange);
                        grabbedSites.Add(site.PrimeSiteStationNumber);
                        if (siteCaseList != null)
                        {
                            unreadList.Cases.AddRange(siteCaseList.Cases);
                        }
                    }
                }


                //Parallel.ForEach(UserContext.AcquisitionList.Items, site =>
                //{
                //    Log.Debug("Start thread: " + site.PrimeSiteStationNumber + " " + DateTime.Now.ToString("MM-dd-yyyy HH:mm:ss"));
                //    if ((site.Active) && (!grabbedSites.Contains(site.PrimeSiteStationNumber)))
                //    {
                //        CaseList siteCaseList = vixClient.GetUnreleasedCases(site.PrimeSiteStationNumber);
                //        grabbedSites.Add(site.PrimeSiteStationNumber);
                //        if (siteCaseList != null)
                //        {
                //            unreadList.Cases.AddRange(siteCaseList.Cases);
                //        }
                //    }
                //    Log.Debug("End thread: " + site.PrimeSiteStationNumber + " " + DateTime.Now.ToString("MM-dd-yyyy HH:mm:ss"));
                //}
                //    );

                Log.Debug("Unread time elapsed: " + watch.ElapsedMilliseconds.ToString());

                // sort cases by accession date/time
                if (unreadList.Cases.Count > 0)
                {
                    List<Case> orderedCases = unreadList.Cases.OrderBy(d => d.SpecimenTakenDate).ThenBy(a => a.AccessionNumber).ThenBy(p => p.PatientName).ToList();
                    unreadList.Cases = orderedCases;
                }
            }
            else
            {
                Log.Error("VIX client is null.");
            }

            return unreadList;
        }

        //private CaseList CombineLists(BlockingCollection<CaseList> results)
        //{
        //    CaseList consolidatedList = new CaseList();
        //    foreach (CaseList list in results)
        //    {
        //        if (list != null)
        //            consolidatedList.Cases.AddRange(list.Cases);
        //    }

        //    if (consolidatedList.Cases.Count > 0)
        //    {
        //        List<Case> orderedCases = consolidatedList.Cases.OrderBy(d => d.SpecimenTakenDate).ThenBy(a => a.AccessionNumber).ThenBy(p => p.PatientName).ToList();
        //        consolidatedList.Cases = orderedCases;
        //    }

        //    return consolidatedList;
        //}
        
        /// <summary>
        /// Retrieve a list of released cases from vista
        /// </summary>
        /// <param name="siteID">site storing the cases</param>
        /// <returns>a list of read cases from the site</returns>
        public CaseList GetReleasedCases()
        {
            CaseList caseList = new CaseList();

            if (vixClient != null)
            {
                // get the retention day range
                int dayRange = client.GetRetentionDays();

                // gather cases from each acquisition site
                List<string> grabbedSites = new List<string>();
                var watch = Stopwatch.StartNew();
                foreach (AcquisitionSiteInfo site in UserContext.AcquisitionList.Items)
                {
                    if ((site.Active) && (!grabbedSites.Contains(site.PrimeSiteStationNumber)))
                    {
                        CaseList siteCaseList = vixClient.GetReleasedCases(site.PrimeSiteStationNumber, dayRange);
                        grabbedSites.Add(site.PrimeSiteStationNumber);
                        if (siteCaseList != null)
                        {
                            caseList.Cases.AddRange(siteCaseList.Cases);
                        }
                    }
                }

                //Parallel.ForEach(UserContext.AcquisitionList.Items, site =>
                //{
                //    if ((site.Active) && (!grabbedSites.Contains(site.PrimeSiteStationNumber)))
                //    {
                //        CaseList siteCaseList = vixClient.GetReleasedCases(site.PrimeSiteStationNumber);
                //        grabbedSites.Add(site.PrimeSiteStationNumber);
                //        if (siteCaseList != null)
                //        {
                //            caseList.Cases.AddRange(siteCaseList.Cases);
                //        }
                //    }
                //}
                //   );

                Log.Debug("Read time elapsed: " + watch.ElapsedMilliseconds.ToString());

                // sort cases by accession number
                if (caseList.Cases.Count > 0)
                {
                    List<Case> orderedCases = caseList.Cases.OrderBy(d => d.SpecimenTakenDate).ThenBy(a => a.AccessionNumber).ThenBy(p => p.PatientName).ToList();
                    caseList.Cases = orderedCases;
                }
            }

            return caseList;
        }

        /// <summary>
        /// Update a specific list of cases
        /// </summary>
        /// <param name="caseList">list of cases to be updated</param>
        public void UpdateCases(CaseList caseList)
        {
            if (this.vixClient != null)
            {
                // group cases by site id
                List<List<Case>> casesBySiteID = caseList.Cases.GroupBy(item => item.SiteID).Select(group => new List<Case>(group)).ToList();

                foreach (List<Case> cases in casesBySiteID)
                {
                    // build list of case URNS
                    List<string> caseURNs = cases.Select(item => item.CaseURN).ToList();

                    CaseList updatedCaseList = this.vixClient.GetCases(caseURNs);
                    if (updatedCaseList != null)
                    {
                        foreach (Case updatedCase in updatedCaseList.Cases)
                        {
                            // search for original case
                            Case origCase = cases.Where(x => (x.CaseURN == updatedCase.CaseURN)).FirstOrDefault();
                            if (origCase != null)
                            {
                                origCase.CopyFrom(updatedCase);
                            }
                        }
                    }
                }
            }
            else
            {
                Log.Error("VIX client is null.");
            }
        }

        public Patient GetPatient(string siteID, string patientICN)
        {
            if (vixClient != null)
            {
                Patient patient = vixClient.GetPatient(siteID, patientICN);
                
                // get patient dfn using local site
                client.GetPatientDetails(patient);
                return patient;
            }
            else
            {
                Log.Error("VIX client is null.");
                return new Patient();
            }
        }

        /// <summary>
        /// Check to see if the patient is registered at local site
        /// </summary>
        /// <param name="siteID">Site checking</param>
        /// <param name="patientICN">Patient's ID</param>
        /// <returns>true if patient at site and false otherwise.</returns>
        public bool IsPatientRegisteredAtSite(string siteID, string patientICN)
        {
            if (vixClient != null)
            {
                return vixClient.IsPatientRegisteredAtSite(siteID, patientICN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return false;
            }
        }

        /// <summary>
        /// Retrieving patient's cases
        /// </summary>
        /// <param name="siteID">site store the cases</param>
        /// <param name="patientICN">patient unique id</param>
        /// <returns>a list of case related to the patient</returns>
        public CaseList GetPatientCases(string patientICN)
        {
            CaseList caseList = new CaseList();

            if (vixClient != null)
            {
                // gather cases from each acquisition site
                List<string> grabbedSites = new List<string>();
                var watch = Stopwatch.StartNew();
                foreach (AcquisitionSiteInfo site in UserContext.AcquisitionList.Items)
                {
                    if ((site.Active) && (!grabbedSites.Contains(site.PrimeSiteStationNumber)))
                    {
                        CaseList siteCaseList = vixClient.GetPatientCases(site.PrimeSiteStationNumber, patientICN);
                        grabbedSites.Add(site.PrimeSiteStationNumber);
                        if (siteCaseList != null)
                        {
                            caseList.Cases.AddRange(siteCaseList.Cases);
                        }
                    }
                }
                //Parallel.ForEach(UserContext.AcquisitionList.Items, site =>
                //{
                //    if ((site.Active) && (!grabbedSites.Contains(site.PrimeSiteStationNumber)))
                //    {
                //        CaseList siteCaseList = vixClient.GetPatientCases(site.PrimeSiteStationNumber, patientICN);
                //        grabbedSites.Add(site.PrimeSiteStationNumber);
                //        if (siteCaseList != null)
                //        {
                //            caseList.Cases.AddRange(siteCaseList.Cases);
                //        }
                //    }
                //}
                //   );

                Log.Debug("Patient time elapsed: " + watch.ElapsedMilliseconds.ToString());
                // sort cases by accession number
                if (caseList.Cases.Count > 0)
                {
                    List<Case> orderedCases = caseList.Cases.OrderBy(d => d.SpecimenTakenDate).ThenBy(a => a.AccessionNumber).ThenBy(p => p.PatientName).ToList();
                    caseList.Cases = orderedCases;
                }
            }

            return caseList;
        }

        /// <summary>
        /// Retrieve case deteail
        /// </summary>
        /// <param name="caseURN">case id to be retrieved</param>
        /// <returns>detail of the case</returns>
        public CaseSpecimenList GetCaseDetail(string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetCaseDetail(caseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new CaseSpecimenList();
            }
        }

        /// <summary>
        /// Retrieve case slides info
        /// </summary>
        /// <param name="caseURN">case id to be retrieved</param>
        /// <returns>detail of the case</returns>
        public CaseSlideList GetCaseSlidesInfo(string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetCaseSlidesInfo(caseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new CaseSlideList();
            }
        }

        /// <summary>
        /// Reserve a case for reading
        /// </summary>
        /// <param name="caseURN">case being read</param>
        /// <param name="reserveCase">reserve or unreserve</param>
        public void ReserveCase(string caseURN, bool reserveCase)
        {
            if (vixClient != null)
            {
                vixClient.ReserveCase(caseURN, reserveCase);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Requesting an interpretation entry for a case
        /// </summary>
        /// <param name="caseURN">case to be interpretated</param>
        /// <param name="siteID">site to interpret the case</param>
        /// <returns>true if success</returns>
        public bool RequestInterpretation(string caseURN, string siteID)
        {
            if (vixClient != null)
            {
                return vixClient.RequestInterpretation(caseURN, siteID);
            }
            else
            {
                Log.Error("VIX client is null.");
                return false;
            }
        }

        /// <summary>
        /// Requestiong a consultation at a consulting site by creating a consultation entry in the interpretation file
        /// </summary>
        /// <param name="caseURN">case to be consulted</param>
        /// <param name="consultingSite">site being consulted</param>
        /// <returns>true if successful</returns>
        public bool RequestConsultation(string caseURN, ReadingSiteInfo consultingSite)
        {
            if (vixClient != null)
            {
                return vixClient.RequestConsultation(caseURN, consultingSite);
            }
            else
            {
                Log.Error("VIX client is null.");
                return false;
            }
        }

        #region REPORTING
        /// <summary>
        /// Retrieve report for a case as displayed in CPRS
        /// </summary>
        /// <param name="caseURN">case identification</param>
        /// <returns>a string contains the report</returns>
        public string GetCPRSReport(string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetCPRSReport(caseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return "Report could not be retrieved.";
            }
        }

        /// <summary>
        /// Retrieve report data for a case
        /// </summary>
        /// <param name="item">Case item</param>
        /// <returns>Report data for the case in question</returns>
        public Report GetReport(CaseListItem caseObject)
        {
            if (vixClient != null)
            {
                return vixClient.GetReport(caseObject);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new Report();
            }
        }

        public string GetReportFieldData(string fieldNumber, string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetReportFieldData(fieldNumber, caseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return "";
            }
        }

        /// <summary>
        /// Retrieve a list of supplementary report for a case
        /// </summary>
        /// <param name="caseURN">Case ID</param>
        /// <returns>list of supplementary reports for the case</returns>
        public SupplementaryReportModel GetSupplementalReports(string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetSupplementalReports(caseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new SupplementaryReportModel();
            }
        }

        /// <summary>
        /// Save changes to the main report to database
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <param name="changeList">list of changes</param>
        public PathologySaveCaseReportResultType SaveReportChanges(string caseURN, PathologyCaseReportFieldsType changeList)
        {
            if (vixClient != null)
            {
                return vixClient.SaveReportChanges(caseURN, changeList);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Save supplementary report to VistA
        /// </summary>
        /// <param name="caseURN">Case ID</param>
        /// <param name="datetime">Date time of the supplementary report</param>
        /// <param name="verified">verify the report or not</param>
        /// <param name="data">report content</param>
        public void SaveSupReport(string caseID, string datetime, bool verified, string data)
        {
            if (vixClient != null)
            {
                vixClient.SaveSupReport(caseID, datetime, verified, data);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Check if e-signature is required
        /// </summary>
        /// <param name="siteID">Site need to check</param>
        /// <param name="reportType">Report type</param>
        /// <returns>status of e-signature requirement</returns>
        public PathologyElectronicSignatureNeedType GetESignatureStatus(string siteID, string reportType)
        {
            if (vixClient != null)
            {
                return vixClient.GetESignatureStatus(siteID, reportType);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new PathologyElectronicSignatureNeedType() { Status = ESigNeedType.error, Message = "Could not retrieve E-signature status." };
            }
        }

        /// <summary>
        /// Verify the esignature
        /// </summary>
        /// <param name="siteID">site to be verified at</param>
        /// <param name="eSignature">signature</param>
        /// <returns>true if success</returns>
        public bool VerifyESignature(string siteID, string eSignature)
        {
            if (vixClient != null)
            {
                return vixClient.VerifyESignature(siteID, eSignature);
            }
            else
            {
                Log.Error("VIX client is null.");
                return false;
            }
        }

        /// <summary>
        /// Gets the sensitivity level of the patient's record to determine if the record can be accessed.
        /// </summary>
        /// <param name="siteID">Site holding the record</param>
        /// <param name="patientICN">Patient's unique identification number</param>
        /// <returns>Sensitive Level of the patient</returns>
        public PatientSensitiveValueType GetPatientSensitiveLevel(string siteID, string patientICN)
        {
            if (vixClient != null)
            {
                return vixClient.GetPatientSensitiveLevel(siteID, patientICN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new PatientSensitiveValueType();
            }
        }

        /// <summary>
        /// Log user access to a sensitive patient record
        /// </summary>
        /// <param name="siteID">Site holding the record</param>
        /// <param name="patientICN">Patient's unique identification number</param>
        public void PatientSensitiveAccessLog(string siteID, string patientICN)
        {
            if (vixClient != null)
            {
                vixClient.PatientSensitiveAccessLog(siteID, patientICN);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null");
            }
        }

        /// <summary>
        /// Retrieve a list of snomed codes for the case
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <returns>list of codes</returns>
        public PathologySnomedCodesType GetSnomedCodeForCase(string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetSnomedCodeForCase(caseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new PathologySnomedCodesType();
            }
        }

        /// <summary>
        /// Search VistA for items contains a specific string
        /// </summary>
        /// <param name="siteID">site has the data</param>
        /// <param name="fieldType">type of field searching for</param>
        /// <param name="searchParameter">search string</param>
        /// <returns>List of iteeat movms match the search</returns>
        public PathologyFieldValuesType SearchPathologyItems(string siteID, string fieldType, string searchParameter)
        {
            if (vixClient != null)
            {
                return vixClient.SearchPathologyItems(siteID, fieldType, searchParameter);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new PathologyFieldValuesType();
            }
        }

        /// <summary>
        /// Add more snomed item into an organ tissue
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <param name="organID">organ id</param>
        /// <param name="fieldURN">field id</param>
        /// <returns>index of the newly added snomed item</returns>
        public string AddSnomedItemToOrganTissue(string caseURN, string organID, string fieldURN)
        {
            if (vixClient != null)
            {
                return vixClient.AddSnomedItemToOrganTissue(caseURN, organID, fieldURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return null;
            }
        }

        /// <summary>
        /// Add new SNOMED etiology to a specific morphology
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <param name="organID">organ idex</param>
        /// <param name="morphologyID">morphology index</param>
        /// <param name="etiologyURN">etiology id</param>
        /// <returns>new index for the etiology</returns>
        public string AddSnomedEtiologyToMorphology(string caseURN, string organID, string morphologyID, string etiologyURN)
        {
            if (vixClient != null)
            {
                return vixClient.AddSnomedEtiologyToMorphology(caseURN, organID, morphologyID, etiologyURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return null;
            }
        }

        /// <summary>
        /// Add new SNOMED organ tissue
        /// </summary>
        /// <param name="caseURN">case ID</param>
        /// <param name="organURN">organ ID</param>
        /// <returns>new index for the organ</returns>
        public string AddSnomedOrganTissue(string caseURN, string organURN)
        {
            if (vixClient != null)
            {
                return vixClient.AddSnomedOrganTissue(caseURN, organURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return null;
            }
        }

        /// <summary>
        /// Removing selected etiology
        /// </summary>
        /// <param name="caseID">case ID</param>
        /// <param name="organID">organ index</param>
        /// <param name="snomedID">morphology index</param>
        /// <param name="etiologyID">etiology index</param>
        public void RemoveSnomedEtiology(string caseID, string organID, string snomedID, string etiologyID)
        {
            if (vixClient != null)
            {
                vixClient.RemoveSnomedEtiology(caseID, organID, snomedID, etiologyID);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Removing selected snomed item
        /// </summary>
        /// <param name="caseID">case ID</param>
        /// <param name="organID">organ index</param>
        /// <param name="snomedID">snomed index</param>
        /// <param name="field">field type</param>
        public void RemoveSnomedField(string caseID, string organID, string snomedID, string field)
        {
            if (vixClient != null)
            {
                vixClient.RemoveSnomedField(caseID, organID, snomedID, field);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Remove organ tissue from the case
        /// </summary>
        /// <param name="caseID">case id</param>
        /// <param name="organID">organ index</param>
        public void RemoveSnomedOrganTissue(string caseID, string organID)
        {
            if (vixClient != null)
            {
                vixClient.RemoveSnomedOrganTissue(caseID, organID);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Retrieve a list of cpt codes for a case
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <returns>list of cpt codes</returns>
        public PathologyCptCodesType GetCptCodesForCase(string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetCptCodesForCase(caseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return null;
            }
        }

        /// <summary>
        /// Add new CPT codes for a case
        /// </summary>
        /// <param name="caseURN">case ID</param>
        /// <param name="locationURN">ordering location id</param>
        /// <param name="cptCodes">new cpt codes</param>
        /// <returns>result for those codes</returns>
        public PathologyCptCodeResultsType AddCptCodesForCase(string caseURN, string locationURN, ObservableCollection<string> cptCodes)
        {
            if (vixClient != null)
            {
                return vixClient.AddCptCodesForCase(caseURN, locationURN, cptCodes);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Create a copy report at the consulting site referencing the original case
        /// </summary>
        /// <param name="destinationSiteID">Where the copy case will be stored at</param>
        /// <param name="sourceCaseURN">original case id</param>
        /// <returns>new accession number for the copied case</returns>
        public string CreateCopyCase(string destinationSiteID, string sourceCaseURN)
        {
            if (vixClient != null)
            {
                return vixClient.CreateCopyCase(destinationSiteID, sourceCaseURN);
            }
            else
            {
                Log.Error("VIX client is null.");
                return null;
            }
        }

        #endregion

        #region CONFIGURATOR
        /// <summary>
        /// Gets the application timeout duration
        /// </summary>
        /// <returns>duration in minutes</returns>
        public int GetApplicationTimeout()
        {
            return client.GetApplicationTimeout();
        }

        /// <summary>
        /// Sets the application timeout duration
        /// </summary>
        /// <param name="duration">duration in minutes</param>
        public void SetApplicationTimeout(int duration)
        {
            client.SetApplicationTimeout(duration);
        }

        /// <summary>
        /// Gets the cut off day for cases on the worklist
        /// </summary>
        /// <param name="stationNumber">station number of the site</param>
        /// <returns>retention days</returns>
        public int GetRetentionDays()
        {
            return client.GetRetentionDays();
        }

        /// <summary>
        /// Sets the cut off day for cases on the worklist
        /// </summary>
        /// <param name="stationNumber">station number of the site</param>
        /// <param name="days">day range</param>
        public void SetRetentionDays(int days)
        {
            client.SetRetentionDays(days);
        }

        /// <summary>
        /// Check to see if the primary site is valid for adding
        /// </summary>
        /// <param name="stationNumber">The station number of the site</param>
        /// <returns>True if valida and false otherwise</returns>
        public bool IsPrimarySiteValid(string stationNumber)
        {
            if (vixClient != null)
            {
                return vixClient.IsPrimarySiteValid(stationNumber);
            }
            else
            {
                Log.Error("VIX client is null.");
                return false;
            }
        }

        /// <summary>
        /// Retrieve a list of available institutions in the user local VistA via the VIX
        /// </summary>
        /// <returns>A collection of SiteInfo objects</returns>
        public ObservableCollection<SiteInfo> GetInstitutionList()
        {
            if (vixClient != null)
            {
                return vixClient.GetInstitutionList();
            }
            else
            {
                Log.Error("VIX client is null.");
                return new ObservableCollection<SiteInfo>();
            }
        }

        /// <summary>
        /// Retrieve a list of acquisition sites for the inquired station number via the VIX
        /// </summary>
        /// <param name="siteStationNumber">Site being questioned</param>
        /// <returns>AcquisitionSiteList object contains all available acquisition site for the questioned site</returns>
        public AcquisitionSiteList GetAcquisitionSites(string siteStationNumber)
        {
            if (vixClient != null)
            {
                return vixClient.GetAcquisitionSites(siteStationNumber);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new AcquisitionSiteList();
            }
        }

        /// <summary>
        /// Retrieve a list of reading sites for the inquiring site
        /// </summary>
        /// <param name="siteStationNumber">Site being questioned</param>
        /// <returns>ReadingSiteList object contains all available reading site for the questioned site</returns>
        public ReadingSiteList GetReadingSites(string siteStationNumber)
        {
            if (vixClient != null)
            {
                return vixClient.GetReadingSites(siteStationNumber);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new ReadingSiteList();
            }
        }

        /// <summary>
        /// Save report template back to VistA
        /// </summary>
        /// <param name="templateObj">Template object containing the template to be stored</param>
        public void SaveReportTemplate(VixReportTemplateObject templateObj)
        {
            if (vixClient != null)
            {
                vixClient.SaveReportTemplate(templateObj);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Retrieve all the report templates saved at the acquisition site
        /// </summary>
        /// <param name="siteID">Site holding all the templates</param>
        /// <returns>A collection of report template objects</returns>
        public ObservableCollection<ReportTemplate> GetReportTemplates(string siteID)
        {
            if (vixClient != null)
            {
                return vixClient.GetReportTemplates(siteID);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new ObservableCollection<ReportTemplate>();
            }
        }

        /// <summary>
        /// Save new and update existing reading site.
        /// </summary>
        /// <param name="siteStationNumber">Site where the data will be stored at.</param>
        /// <param name="readingSite">Reading site data to be stored.</param>
        public void SaveReadingSite(string siteStationNumber, PathologyReadingSiteType readingSite)
        {
            if (vixClient != null)
            {
                vixClient.SaveReadingSite(siteStationNumber, readingSite);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Removing a reading site from the reading site list in VistA
        /// </summary>
        /// <param name="siteStationNumber">Site where the data will be removed from.</param>
        /// <param name="readingSite">Reading site data being removed.</param>
        public void RemoveReadingSite(string siteStationNumber, PathologyReadingSiteType readingSite)
        {
            if (vixClient != null)
            {
                vixClient.RemoveReadingSite(siteStationNumber, readingSite);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Save new and update existing acquisition site
        /// </summary>
        /// <param name="siteStationNumber">Location of the acquisition site going to be stored at</param>
        /// <param name="acquisitionSite">Acquisition site to be updated</param>
        public void SaveAcquisitionSite(string siteStationNumber, PathologyAcquisitionSiteType acquisitionSite)
        {
            if (vixClient != null)
            {
                vixClient.SaveAcquisitionSite(siteStationNumber, acquisitionSite);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Removing an acquisition site from the acquisition site list in VistA
        /// </summary>
        /// <param name="siteStationNumber">The site to be removed from</param>
        /// <param name="acquisitionSite">The site being removed</param>
        public void RemoveAcquisitionSite(string siteStationNumber, PathologyAcquisitionSiteType acquisitionSite)
        {
            if (vixClient != null)
            {
                vixClient.RemoveAcquisitionSite(siteStationNumber, acquisitionSite);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Check to see if an acquisition site has pending consultation at reading site
        /// </summary>
        /// <param name="acquisitionSiteID">Site making the check</param>
        /// <param name="readingSiteID">Site being checked on</param>
        /// <returns>false if there are no consultation, true otherwise</returns>
        public bool CheckPendingConsultation(string acquisitionSiteID, string readingSiteID)
        {
            if (vixClient != null)
            {
                return vixClient.CheckPendingConsultation(acquisitionSiteID, readingSiteID);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Retrieve saved value for lock timeout.
        /// </summary>
        /// <param name="siteID">Site holding the data</param>
        /// <returns>The saved value or "Error"</returns>
        public string GetReportLockTimeoutHour(string siteID)
        {
            if (vixClient != null)
            {
                return vixClient.GetReportLockTimeoutHour(siteID);
            }
            else
            {
                Log.Error("VIX client is null.");
                return "Error";
            }
        }

        /// <summary>
        /// Save lock timeout hours to VistA
        /// </summary>
        /// <param name="siteID">Site holding the data</param>
        /// <param name="hours">Hour value being stored</param>
        public void SetReportLockTimeoutHour(string siteID, string hours)
        {
            if (vixClient != null)
            {
                vixClient.SetReportLockTimeoutHour(siteID, hours);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }
        
        #endregion

        /// <summary>
        /// Update the status for a consultation request
        /// </summary>
        /// <param name="consultationID">ID of the consultation request</param>
        /// <param name="status">new status</param>
        public void UpdateConsultationStatus(string consultationID, string status)
        {
            if (vixClient != null)
            {
                vixClient.UpdateConsultationStatus(consultationID, status);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Lock a case when the report GUI is open to prevent collision
        /// </summary>
        /// <param name="caseURN">case identifier</param>
        /// <param name="lockCase">true for locking and false of unlocking</param>
        /// <returns>true if successfully done</returns>
        public PathologyCaseUpdateAttributeResultType LockCaseForEditing(string caseURN, bool lockCase)
        {
            if (vixClient != null)
            {
                return vixClient.LockCaseForEditing(caseURN, lockCase);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new PathologyCaseUpdateAttributeResultType();
            }
        }

        /// <summary>
        /// Retrieve stored user preferences
        /// </summary>
        /// <param name="label">preference label</param>
        /// <returns>user preferences</returns>
        public string ReadPreferences(string label)
        {
            string data = null;

            if (vixClient != null)
            {
                // read for logged in user only
                data = vixClient.ReadPreferences(UserContext.LocalSite.PrimarySiteStationNUmber, label, null);
            }
            else
            {
                Log.Error("VIX client is null.");
            }

            return data;
        }

        /// <summary>
        /// Save user visual preferences to VistA
        /// </summary>
        /// <param name="label"></param>
        /// <param name="data"></param>
        public void SavePreferences(string label, string data)
        {
            if (vixClient != null)
            {
                // save for logged in user only
                vixClient.SavePreferences(UserContext.LocalSite.PrimarySiteStationNUmber, label, null, data);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        /// <summary>
        /// Retrieve a list of health summary type
        /// </summary>
        /// <param name="siteID">site storing the summaries</param>
        /// <returns>a list of summary type</returns>
        public HealthSummaryTypeList GetHealthSummaryTypeList(string siteID)
        {
            if (vixClient != null)
            {
                return vixClient.GetHealthSummaryTypeList(siteID);
            }
            else
            {
                Log.Error("VIX client is null.");
                return new HealthSummaryTypeList(); ;
            }
        }

        /// <summary>
        /// Retrieve health summary
        /// </summary>
        /// <param name="patientICN">patient id</param>
        /// <param name="healthSummaryType">type of health summary</param>
        /// <returns>summary for the patient</returns>
        public string GetHealthSummary(string patientICN, string healthSummaryType)
        {
            if (vixClient != null)
            {
                return vixClient.GetHealthSummary(patientICN, healthSummaryType);
            }
            else
            {
                Log.Error("VIX client is null.");
                return "ERROR: Could not retrieve health summary.";
            }
        }

        /// <summary>
        /// Retrieve notes for a case
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <returns>note for the case</returns>
        public string GetNotes(string caseURN)
        {
            if (vixClient != null)
            {
                return vixClient.GetNotes(caseURN);
            }
            else
            {
                Log.Error("VIX Client is null.");
                return "ERROR: Notes cannot be retrieved.";
            }
        }

        /// <summary>
        /// Save case note to vista
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <param name="notes">notes to be saved</param>
        public void SaveNotes(string caseURN, string notes)
        {
            if (vixClient != null)
            {
                vixClient.SaveNotes(caseURN, notes);
            }
            else
            {
                throw new MagVixFailureException("VIX client is null.");
            }
        }

        public bool IsSiteSupportTelepathology(string SiteStationNumber)
        {
            if (vixClient != null)
            {
                return vixClient.IsSiteSupportTelepathology(SiteStationNumber);
            }
            else
            {
                Log.Error("VIX Client is null.");
                return false;
            }
        }
    }
}
