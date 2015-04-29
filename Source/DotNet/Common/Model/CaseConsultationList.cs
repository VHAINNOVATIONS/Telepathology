// -----------------------------------------------------------------------
// <copyright file="CaseConsultationList.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: Jun 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Duc Nguyen
//  Description: A list of all the consultation status for a case
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
    using System.Collections.Generic;
//    using System.Collections.ObjectModel;
    using System.ComponentModel;
    using System.Xml.Serialization;

    /// <summary>
    /// Store a list of consultation for a case
    /// </summary>
    public class CaseConsultationList : INotifyPropertyChanged
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="CaseConsultationList"/> class
        /// </summary>
        public CaseConsultationList()
        {
//            this.ConsultationList = new ObservableCollection<CaseConsultation>();
            this.ConsultationList = new List<CaseConsultation>();
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
        /// Gets or sets a list contains all the consultation information for a case
        /// </summary>
        [XmlElement("consultation")]
//        public ObservableCollection<CaseConsultation> ConsultationList { get; set; }
        public List<CaseConsultation> ConsultationList { get; set; }
    }
}
