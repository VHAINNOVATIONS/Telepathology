// -----------------------------------------------------------------------
// <copyright file="CaseSlides.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: May 2015
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Csaba Titton
//  Description: Hold slides information for a case
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
    using System.ComponentModel;
    using System.Xml.Serialization;

    /// <summary>
    /// Slide info definition class. Note: a pathology Case can have 0.. Slides.
    /// </summary>

    public class CaseSlide : INotifyPropertyChanged
    {
        public CaseSlide()
        {
	        this.SlideNumber=string.Empty;
	        this.DateTimeScanned=string.Empty;
	        this.Url=string.Empty;
	        this.ZoomFactor=string.Empty;
	        this.ScanApplication=string.Empty;
	        this.SlideStatus=string.Empty;
	        this.ViewApplication=string.Empty;
	        this.Description=string.Empty;
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

        [XmlElement("slideNumber")]
        public string SlideNumber { get; set; }

        [XmlElement("dateTimeScanned")]
        public string DateTimeScanned { get; set; }

        [XmlElement("url")]
        public string Url { get; set; }

        [XmlElement("zoomFactor")]
        public string ZoomFactor { get; set; }

        [XmlElement("scanApplication")]
        public string ScanApplication { get; set; }

        [XmlElement("slideStatus")]
        public string SlideStatus { get; set; }

        [XmlElement("viewApplication")]
        public string ViewApplication { get; set; }

        [XmlElement("description")]
        public string Description { get; set; }
    }

    [XmlRoot("pathologyCaseSlidesType")]
    public class CaseSlideList
    {
        public CaseSlideList()
        {
            this.Items = new List<CaseSlide>();
        }

        [XmlElement("pathologyCaseSlide")]
        public List<CaseSlide> Items { get; set; }
    }
}
