// -----------------------------------------------------------------------
// <copyright file="Case.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: Jan 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Paul Pentapaty, Duc Nguyen
//  Description: Pathology Case
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

namespace VistA.Imaging.Telepathology.Common.Model
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel;
    using System.Xml.Serialization;

    /// <summary>
    /// Store the information for a case in the worklist
    /// </summary>
    public class Case : INotifyPropertyChanged
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="Case"/> class
        /// </summary>
        public Case()
        {
            this.AccessionNumber = string.Empty;
            this.CaseURN = string.Empty;
            this.ConsultationList = new CaseConsultationList();
            this.HasNotes = "No";
            this.Method = "Traditional";
            this.PatientICN = string.Empty;
            this.PatientName = string.Empty;
            this.PatientShortID = string.Empty;
            this.PatientSensitive = false;
            this.Priority = "Routine";
            this.Reserver = string.Empty;
            this.ReservedState = "0";
            this.SiteAbbr = string.Empty;
            this.SiteID = string.Empty;
            this.SlidesAvailable = "No";
            this.SnapshotCount = "0";
            this.SpecimenCount = "0";
            this.SpecimenTakenDate = string.Empty;
            this.ReportStatus = "In Progress";
            this.ConsultationStatus = string.Empty; ;
        }

        public void CopyFrom(Case caseObj)
        {
            this.Method = caseObj.Method;
            this.Priority = caseObj.Priority;
            this.ReservedState = caseObj.ReservedState;
            this.Reserver = caseObj.Reserver;
            this.ReportStatus = caseObj.ReportStatus;
            this.SlidesAvailable = caseObj.SlidesAvailable;
            this.SnapshotCount = caseObj.SnapshotCount;
            this.SpecimenCount = caseObj.SpecimenCount;
            this.HasNotes = caseObj.HasNotes;
            this.IsNoteAttached = caseObj.IsNoteAttached;
            this.ConsultationList.ConsultationList.Clear();
            foreach (CaseConsultation item in caseObj.ConsultationList.ConsultationList)
            {
                this.ConsultationList.ConsultationList.Add(item);
            }
            this.ConsultationStatus = this.ConsultStatus;
        }

        /// <remarks>
        /// The PropertyChanged event is raised by NotifyPropertyWeaver (http://code.google.com/p/notifypropertyweaver/)
        /// </remarks>
        /// <summary>
        /// Event to be raised when a property is changed
        /// </summary>
#pragma warning disable 0067
        // Warning disabled because the event is raised by NotifyPropertyWeaver (http://code.google.com/p/notifypropertyweaver/)
        public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067

        /// <summary>
        /// Gets or sets the accession number of a case
        /// </summary>
        [XmlElement("accessionNumber")]
        public string AccessionNumber { get; set; }

        /// <summary>
        /// Gets or sets the unique identifier of a case by the VIX
        /// </summary>
        [XmlElement("caseId")]
        public string CaseURN { get; set; }

        /// <summary>
        /// Gets or sets a list of consultation information for a case
        /// </summary>
        [XmlElement("consultations")]
        public CaseConsultationList ConsultationList { get; set; }

        private string ConvertSiteListToString(List<string> siteList)
        {
            string text = "";

            foreach (string site in siteList)
            {
                text = string.IsNullOrEmpty(text)? site : string.Format("{0},{1}", text, site);
            }

            return text;
        }

        [XmlIgnore]
        public string ConsultStatus
        {
            get
            {
                List<string> comp = new List<string>();
                List<string> pend = new List<string>();
                List<string> refu = new List<string>();
                List<string> cand = new List<string>();

                foreach (CaseConsultation con in this.ConsultationList.ConsultationList)
                {
                    if (con.Type == "CONSULTATION")
                    {
                        if (con.Status == "PENDING")
                        {
                            pend.Add(con.SiteAbbr);
                        }
                        else if (con.Status == "REFUSED")
                        {
                            refu.Add(con.SiteAbbr);
                        }
                        else if (con.Status == "COMPLETED")
                        {
                            comp.Add(con.SiteAbbr);
                        }
                        else if (con.Status == "RECALLED")
                        {
                            cand.Add(con.SiteAbbr);
                        }
                    }
                }

                string status = string.Empty;
                string siteList = string.Empty;

                siteList = ConvertSiteListToString(comp);
                if (!string.IsNullOrEmpty(siteList))
                {
                    siteList = string.Format("COMP:{0}", siteList);
                    status = string.IsNullOrEmpty(status) ? siteList : string.Format("{0}\r\n{1}", status, siteList);
                }

                siteList = ConvertSiteListToString(pend);
                if (!string.IsNullOrEmpty(siteList))
                {
                    siteList = string.Format("PEND:{0}", siteList);
                    status = string.IsNullOrEmpty(status) ? siteList : string.Format("{0}\r\n{1}", status, siteList);
                }

                siteList = ConvertSiteListToString(refu);
                if (!string.IsNullOrEmpty(siteList))
                {
                    siteList = string.Format("DCLN:{0}", siteList);
                    status = string.IsNullOrEmpty(status) ? siteList : string.Format("{0}\r\n{1}", status, siteList);
                }

                siteList = ConvertSiteListToString(cand);
                if (!string.IsNullOrEmpty(siteList))
                {
                    siteList = string.Format("CAND:{0}", siteList);
                    status = string.IsNullOrEmpty(status) ? siteList : string.Format("{0}\r\n{1}", status, siteList);
                }

                return status;
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether or not the case has notes
        /// </summary>
        [XmlElement("hasNotes")]
        public string HasNotes { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether or not a note is attached
        /// </summary>
        [XmlElement("noteAttached")]
        public bool IsNoteAttached { get; set; }

        /// <summary>
        /// Gets or sets the method of reading a case
        /// </summary>
        [XmlElement("method")]
        public string Method { get; set; }

        /// <summary>
        /// Gets or sets the national id for the patient of the case
        /// this will value will be encoded icn(XXX) or dfn(xxx)
        /// </summary>
        [XmlElement("patientId")]
        public string PatientICN { get; set; }

        /// <summary>
        /// Gets or sets the name of the patient of the case
        /// </summary>
        [XmlElement("patientName")]
        public string PatientName { get; set; }

        /// <summary>
        /// Gets or sets the short id of the patient of the case
        /// </summary>
        [XmlElement("patientSsn")]
        public string PatientShortID { get; set; }

        [XmlElement("patientSensitive")]
        public bool PatientSensitive { get; set; }

        /// <summary>
        /// Gets or sets the priority of the case
        /// </summary>
        [XmlElement("priority")]
        public string Priority { get; set; }

        /// <summary>
        /// Gets or sets the reservation information of a case
        /// </summary>
        public string ReservedBy
        {
            get
            {
                string initial = string.Empty;
                if (!string.IsNullOrWhiteSpace(Reserver))
                {
                    string[] parts = Reserver.Split('-');
                    if ((parts != null) && (parts.Length >= 2))
                        initial = parts[0];
                }

                return initial;
            }
        }

        [XmlElement("reservedBy")]
        public string Reserver { get; set; }

        /// <summary>
        /// Gets or sets the reservation state of a case
        /// </summary>
        [XmlElement("reservedState")]
        public string ReservedState { get; set; }

        /// <summary>
        /// Gets or sets the abbreviation of the site owning the case
        /// </summary>
        [XmlElement("siteAbbr")]
        public string SiteAbbr { get; set; }

        /// <summary>
        /// Gets or sets the id of the site owning the case
        /// </summary>
        [XmlElement("siteId")]
        public string SiteID { get; set; }

        /// <summary>
        /// Gets or sets a value indicating the availability of the slides for the case
        /// </summary>
        [XmlElement("slidesAvailable")]
        public string SlidesAvailable { get; set; }

        /// <summary>
        /// Gets or sets the number of specimen a case has
        /// </summary>
        [XmlElement("specimenCount")]
        public string SpecimenCount { get; set; }

        /// <summary>
        /// Gets or sets the date and time the specimen is taken
        /// </summary>
        [XmlElement("specimenTakenDate")]
        public string SpecimenTakenDate { get; set; }

        /// <summary>
        /// Gets the time piece
        /// </summary>
        [XmlIgnore]
        public string AccessionDateTime
        {
            get
            {
                if (!string.IsNullOrEmpty(this.SpecimenTakenDate))
                {
                    DateTime studyDate;
                    if (DateTime.TryParse(this.SpecimenTakenDate, out studyDate))
                    {
                        return studyDate.ToString("MM-dd-yyyy HH:mm:ss");
                    }
                    else
                    {
                        return string.Empty;
                    }
                }
                else
                {
                    return string.Empty;
                }
            }
        }

        /// <summary>
        /// Gets or sets the status of the report for the case
        /// </summary>
        [XmlElement("reportStatus")]
        public string ReportStatus { get; set; }

        /// <summary>
        /// Gets or sets the status of the consultation for the case
        /// </summary>
        [XmlElement("consultationStatus")]
        public string ConsultationStatus
        {
            get { return (this.ConsultStatus); }
            set { this.ConsultationStatus = this.ConsultStatus; }
        }


        public string GetField(WorkListFilterParameter.FilterFieldType fieldType)
        {
            switch (fieldType)
            {
                case WorkListFilterParameter.FilterFieldType.AccessionNumber: return this.AccessionNumber;
                case WorkListFilterParameter.FilterFieldType.PatientID: return this.PatientShortID;
                case WorkListFilterParameter.FilterFieldType.PatientName: return this.PatientName;
                case WorkListFilterParameter.FilterFieldType.DateTime: return this.AccessionDateTime;
                case WorkListFilterParameter.FilterFieldType.ReservedBy: return this.ReservedBy;
                case WorkListFilterParameter.FilterFieldType.AcquisitionSite: return this.SiteAbbr;
                case WorkListFilterParameter.FilterFieldType.SlidesAvailable: return this.SlidesAvailable;
                case WorkListFilterParameter.FilterFieldType.ReportStatus: return this.ReportStatus;
                case WorkListFilterParameter.FilterFieldType.HasNotes: return this.IsNoteAttached.ToString();
                case WorkListFilterParameter.FilterFieldType.SpecimenCount: return this.SpecimenCount;
                case WorkListFilterParameter.FilterFieldType.ConsultationStatus: return this.ConsultStatus;
                case WorkListFilterParameter.FilterFieldType.SnapshotCount: return this.SnapshotCount;
            }

            return null;
        }

        [XmlElement("numberOfImages")]
        public string SnapshotCount { get; set; }
    }
}
