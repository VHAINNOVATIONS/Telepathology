// -----------------------------------------------------------------------
// <copyright file="ReportTemplate.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: Mar 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Duc Nguyen
//  Description: Template for the main report
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
    using System.IO;
    using System.Text;
    using System.Xml;
    using System.Xml.Serialization;

    /// <summary>
    /// Skeleton for a main report
    /// </summary>
    [XmlRoot("ReportTemplate")]
    public class ReportTemplate
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ReportTemplate"/> class
        /// </summary>
        public ReportTemplate()
        {
            this.ReportTypeShort = string.Empty;
            this.ReportTypeLong = string.Empty;
            this.ReportFields = new List<ReportFieldTemplate>();
        }

        /// <summary>
        /// Gets or sets the short description of the report type
        /// </summary>
        [XmlElement("ReportTypeShort")]
        public string ReportTypeShort { get; set; }

        /// <summary>
        /// Gets or sets the long description of the report type
        /// </summary>
        [XmlElement("ReportTypeLong")]
        public string ReportTypeLong { get; set; }

        /// <summary>
        /// Gets the report template label
        /// </summary>
        [XmlIgnore]
        public string TemplateLabel
        {
            get
            {
                return this.ReportTypeShort + " " + this.ReportTypeLong;
            }
        }

        /// <summary>
        /// Gets or sets a list of report fields
        /// </summary>
        [XmlArray("ReportFields")]
        [XmlArrayItem("ReportFieldTemplate")]
        public List<ReportFieldTemplate> ReportFields { get; set; }

        public void DeserializeTemplateFromXmlString(string xmlString)
        {
            XmlSerializer deserializer = new XmlSerializer(typeof(ReportTemplate));
            TextReader reader = new StringReader(xmlString);
            ReportTemplate template = (ReportTemplate)deserializer.Deserialize(reader);
            this.ReportFields = template.ReportFields;
            this.ReportTypeLong = template.ReportTypeLong;
            this.ReportTypeShort = template.ReportTypeShort;
            reader.Close();
        }

        public string SerializeToXML()
        {
            string result = string.Empty;

            XmlSerializer serializer = new XmlSerializer(typeof(ReportTemplate));

            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Encoding = new UTF8Encoding(false, false);
            settings.Indent = false;
            settings.OmitXmlDeclaration = true;

            try
            {
                using (StringWriter textWriter = new StringWriter())
                {
                    using (XmlWriter xmlWriter = XmlWriter.Create(textWriter, settings))
                    {
                        serializer.Serialize(xmlWriter, this);
                    }
                    return textWriter.ToString();
                }
            }
            catch (Exception ex)
            {
                // log
                return string.Empty;
            }
        }

        public static ReportTemplate DeserializeTemplateFromXML(string xmlString)
        {
            ReportTemplate result;
            XmlSerializer deserializer = new XmlSerializer(typeof(ReportTemplate));
            StringReader reader = new StringReader(xmlString);
            try
            {
                result = (ReportTemplate)deserializer.Deserialize(reader);
                return result;
            }
            catch (Exception ex)
            {
                // log
                return null;
            }
        }
    }

    /// <summary>
    /// Intermediate object for template between the application and VIX
    /// </summary>
    [XmlRoot("pathologyTemplatesType")]
    public class VixReportTemplateObject
    {
        public VixReportTemplateObject()
        {
            this.TemplateXML = string.Empty;
            this.TemplateSite = string.Empty;
            this.TemplateType = string.Empty;
        }

        [XmlIgnore]
        public string TemplateSite { get; set; }
        
        [XmlIgnore]
        public string TemplateType { get; set; }

        /// <summary>
        /// Gets or sets the xml template
        /// </summary>
        [XmlElement("template")]
        public string TemplateXML { get; set; }

        //public string SaveTemplateToFile()
        //{
        //    if (!string.IsNullOrEmpty(this.TemplateXML))
        //    {
        //        string folderPath = Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData);
        //        string templateLocation = Path.Combine(folderPath, "VistA\\Imaging\\Telepathology\\RepTemplates");
        //        string templatePath = Path.Combine(templateLocation, this.TemplateSite + "^" + this.TemplateType + "^Template.xml");
        //        if (!Directory.Exists(templateLocation))
        //        {
        //            Directory.CreateDirectory(templateLocation);
        //        }

        //        try
        //        {
        //            XmlDocument xDoc = new XmlDocument();
        //            xDoc.LoadXml(this.TemplateXML);
        //            xDoc.Save(templatePath);
        //            return templatePath;
        //        }
        //        catch (Exception ex)
        //        {
        //            return string.Empty;
        //        }
                    
        //    }

        //    return string.Empty;
        //}

        public ReportTemplate GetReportTemplate()
        {
            ReportTemplate repTemplate = new ReportTemplate();
            if (!string.IsNullOrEmpty(this.TemplateXML))
            {
                repTemplate = ReportTemplate.DeserializeTemplateFromXML(this.TemplateXML);
            }
            return repTemplate;
        }

        //public void SetReportTemplate()
        //{
        //    if ((string.IsNullOrEmpty(this.TemplateSite)) || (string.IsNullOrEmpty(this.TemplateSite)))
        //    {
        //        return;
        //    }

        //    string folderPath = Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData);
        //    string templateLocation = Path.Combine(folderPath, "VistA\\Imaging\\Telepathology\\RepTemplates");
        //    string templatePath = Path.Combine(templateLocation, this.TemplateSite + "^" + this.TemplateType + "^Template.xml");

        //    if (File.Exists(templatePath))
        //    {
        //        XmlDocument xmlTemplate = new XmlDocument();
        //        xmlTemplate.Load(templatePath);
        //        this.TemplateXML = xmlTemplate.OuterXml;
        //    }
        //}
    }
}
