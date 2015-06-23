// -----------------------------------------------------------------------
// <copyright file="CaseListItem.cs" company="Department of Veterans Affairs">
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

namespace VistA.Imaging.Telepathology.Common.Model
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel;
    using System.Linq;

    /// <summary>
    /// Item kind
    /// </summary>
    public enum CaseListItemKind
    {
        /// <summary>
        /// Case type
        /// </summary>
        Case,

        /// <summary>
        /// Specimen Type
        /// </summary>
        Specimen,

        /// <summary>
        /// Slide type
        /// </summary>
        SmearPrep,

        /// <summary>
        /// Slide type
        /// </summary>
        Stain,

        /// <summary>
        /// Place holder for case details
        /// </summary>
        PlaceHolder
    }

    /// <summary>
    /// Item for case list
    /// </summary>
    [Serializable]
    public class CaseListItem : INotifyPropertyChanged
    {
        public CaseListItem()
        {
        }
                
        public CaseListItem(Case caseObj)
        {
            this.Initialize(caseObj);
        }

        public void Initialize(Case caseObj)
        {
            this.Kind = CaseListItemKind.Case;
            this.CaseURN = caseObj.CaseURN;
            this.StudyDateTime = caseObj.AccessionDateTime;
            this.HasNotes = caseObj.HasNotes;
            this.IsNoteAttached = caseObj.IsNoteAttached ? "Yes" : "No";
            this.ReportStatus = caseObj.ReportStatus;
            this.Priority = caseObj.Priority;
            this.PatientName = caseObj.PatientName;
            this.PatientID = caseObj.PatientShortID;
            this.PatientICN = caseObj.PatientICN;
            this.PatientSensitive = caseObj.PatientSensitive;
            this.SpecimenCount = caseObj.SpecimenCount;
            this.AcquisitionSite = caseObj.SiteAbbr;
            this.AccessionNumber = caseObj.AccessionNumber;
            this.SiteAbbr = caseObj.SiteAbbr;
            this.SiteCode = caseObj.SiteID;
            this.ReserveState = caseObj.ReservedState;
            this.ReservedBy = caseObj.ReservedBy;
            this.ConsultationStatus = caseObj.ConsultationStatus;
            this.ConsultationList = caseObj.ConsultationList;
            HasPriority = true;
            this.SnapshotCount = caseObj.SnapshotCount;
            this.HasMethod = true;
            this.Method = caseObj.Method;
            this.SlidesAvailable = Boolean.Parse(caseObj.SlidesAvailable) ? "Yes" : "No";
        }
        
        public CaseListItem(CaseSpecimen caseSpecimen, CaseListItem parent, CaseListItemKind kind)
        {
            this.Kind = kind;

            // parent level attributes
            this.PatientICN = parent.PatientICN;
            this.CaseURN = parent.CaseURN;
            this.AccessionNumber = parent.AccessionNumber;
            this.SiteCode = parent.SiteCode;

            switch (this.Kind)
            {
                case CaseListItemKind.Specimen:
                    this.Specimen = caseSpecimen.Specimen.Trim();
                    break;

                case CaseListItemKind.SmearPrep:
                    this.SmearPrep = caseSpecimen.SmearPrep.Trim();
                    break;

                case CaseListItemKind.Stain:
                    this.Stain = caseSpecimen.Stain;
                    this.StainDateTime = caseSpecimen.StainDate.Trim();
                    break;
            }
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

        public string CaseURN { get; set; }
        public string AccessionNumber { get; set; }
        public string HasNotes { get; set; }
        public string IsNoteAttached { get; set; }
        public string HasImages { get; set; }
        public string ReportStatus { get; set; }
        public string PatientName { get; set; }
        public string PatientID { get; set; }
        public string PatientICN { get; set; }
        public bool PatientSensitive { get; set; }
        
        public string PatientDispSSN
        {
            get
            {
                if (PatientSensitive)
                    return string.Empty;
                else
                    return PatientID;
            }
        }
       
        public string AcquisitionSite { get; set; }
        public string ReserveState { get; set; }
        public string ReservedBy { get; set; }
        public CaseListItemKind Kind { get; set; }
        public string SiteCode { get; set; }
        public string ConsultationStatus { get; set; }
        public CaseConsultationList ConsultationList { get; set; }
        public string SpecimenCount { get; set; }

        public string SlidesAvailable { get; set; }

        public string StudyDateTime { get; set; }

        public string SnapshotCount { get; set; }

        public bool HasPriority { get; set; }

        public string Priority { get; set; }

        public bool HasMethod { get; set; }

        public string Method { get; set; }

        public string Specimen { get; set; }

        public string SmearPrep { get; set; }

        public string Stain { get; set; }

        public string StainDateTime { get; set; }

        public string DateTime 
        {
            get
            {
                return (this.Kind == CaseListItemKind.Stain)? this.StainDateTime : this.StudyDateTime;
            }
        }

        public string Description
        {
            get
            {
                string value; 
                switch (this.Kind)
                {
                    case CaseListItemKind.Case: value = this.AccessionNumber; break;
                    case CaseListItemKind.Specimen: value = this.Specimen; break;
                    case CaseListItemKind.SmearPrep: value = this.SmearPrep; break;
                    case CaseListItemKind.Stain: value = this.Stain; break;
                    case CaseListItemKind.PlaceHolder: value = "Retrieving case information..."; break;
                    default: value = string.Empty; break;
                }

                return !string.IsNullOrEmpty(value) ? value : "---------";
            }
        }
        
        public string SiteAbbr { get; set; }

        private readonly List<CaseListItem> _children = new List<CaseListItem>();
        public List<CaseListItem> Slides
        {
            get { return _children; }
        }

        public void CreateChildren(CaseSpecimenList specimenData)
        {
            // clear speciment list
            this.Slides.Clear();

            // group by specimens first
            List<List<CaseSpecimen>> specimens = specimenData.Items.GroupBy(item => item.Specimen).Select(group => new List<CaseSpecimen>(group).Where(spec => !string.IsNullOrEmpty(spec.Specimen)).ToList()).ToList();
            foreach (List<CaseSpecimen> specimenGroup in specimens)
            {
                if (specimenGroup.Count == 0) continue;

                // add specimen node
                CaseListItem specimenRoot = new CaseListItem(specimenGroup[0], this, CaseListItemKind.Specimen);

                // group by smear prep
                List<List<CaseSpecimen>> smearPreps = specimenGroup.GroupBy(item => item.SmearPrep).Select(group => new List<CaseSpecimen>(group).Where(spec => !string.IsNullOrEmpty(spec.SmearPrep)).ToList()).ToList();
                foreach (List<CaseSpecimen> smearPrepGroup in smearPreps)
                {
                    if (smearPrepGroup.Count == 0) continue;

                    // add smear prep node
                    CaseListItem smearPrepRoot = new CaseListItem(smearPrepGroup[0], specimenRoot, CaseListItemKind.SmearPrep);

                    // add remaining stains
                    foreach (CaseSpecimen stain in smearPrepGroup)
                    {
                        if (string.IsNullOrEmpty(stain.Stain)) continue;

                        CaseListItem stainNode = new CaseListItem(stain, smearPrepRoot, CaseListItemKind.Stain);

                        smearPrepRoot.Slides.Add(stainNode);
                    }

                    specimenRoot.Slides.Add(smearPrepRoot);
        }

                this.Slides.Add(specimenRoot);
            }
        }

        public static void CreateNodes(CaseList caseList, CaseListItem root, WorkListFilter filter)
        {
            CaseListItem caseNode = null;

            root.Slides.Clear();

            foreach (Case caseItem in caseList.Cases)
            {
                caseNode = null;

                if (filter == null)
                {
                    caseNode = new CaseListItem(caseItem);
                }
                else
                {
                    if (filter.MatchCase(caseItem) != WorkListFilterParameter.FilterMatchType.NotMatch)
                    {
                        caseNode = new CaseListItem(caseItem);
                    }
                }

                if (caseNode != null)
                {
                    // todo: add children using cached data

                    // create place holder if no children
                    if (caseNode.Slides.Count == 0)
                    {
                        caseNode.Slides.Add(new CaseListItem { Kind = CaseListItemKind.PlaceHolder });
                    }

                    root.Slides.Add(caseNode);
                }

            }
        }
    }
}
