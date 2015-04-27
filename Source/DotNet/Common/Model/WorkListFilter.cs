// -----------------------------------------------------------------------
// <copyright file="WorkListFilter.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: Jan 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Paul Pentapaty
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
    using System.Collections.ObjectModel;
    using System.ComponentModel;
    using System.IO;
    using System.Xml;
    using System.Xml.Serialization;

    public class WorkListFilter : INotifyPropertyChanged, IXmlSerializable, ICloneable
    {
        public WorkListFilter()
        {
            this.IsPublic = false;
            this.SortColumn = CaseListColumnType.None;
            this.SortDirection = ListSortDirection.Ascending;
            this.Kind = WorkListFilterKind.AdHoc;
        }

        public event PropertyChangedEventHandler PropertyChanged;

        public enum WorkListFilterKind
        {
            AdHoc,
            Stored
        }

        public string Name { get; set; }
        
        public bool IsPublic { get; set; }

        public CaseListColumnType SortColumn { get; set; }

        public ListSortDirection SortDirection { get; set; }

        public WorkListFilterKind Kind { get; set; }

        public ObservableCollection<WorkListFilterParameter> Parameters 
        { 
            get { return this._parameters; } 
        }

        public bool IsValid
        {
            get
            {
                foreach (WorkListFilterParameter item in this.Parameters)
                {
                    if (item.IsValid)
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        private ObservableCollection<WorkListFilterParameter> _parameters = new ObservableCollection<WorkListFilterParameter>();

        #region IXmlSerializable Members

        public System.Xml.Schema.XmlSchema GetSchema()
        {
            return null;
        }

        public void ReadXml(System.Xml.XmlReader reader)
        {
            this._parameters.Clear();

            this.Name = reader["Name"];
            this.IsPublic = bool.Parse(reader["IsPublic"]);

            WorkListFilterKind kind;
            if (Enum.TryParse<WorkListFilterKind>(reader["Kind"], out kind))
            {
                this.Kind = kind;
            }

            CaseListColumnType sortColumn;
            if (Enum.TryParse<CaseListColumnType>(reader["SortColumn"], out sortColumn))
            {
                this.SortColumn = sortColumn;
            }
            
            this.SortDirection = (ListSortDirection)Enum.Parse(typeof(ListSortDirection), reader["SortDir"]);

            reader.Read();
            if ((reader.MoveToContent() == XmlNodeType.Element) && (reader.LocalName == "WorklistFilterParameters"))
            {
                reader.Read();
                while ((reader.MoveToContent() == XmlNodeType.Element) && (reader.LocalName == "WorklistFilterParameter"))
                {
                    WorkListFilterParameter.FilterFieldType fieldType;
                    WorkListFilterParameter.ValueType valueType;
                    WorkListFilterParameter.OperatorType operatorType;

                    if ((Enum.TryParse<WorkListFilterParameter.FilterFieldType>(reader["Field"], out fieldType)) &&
                        (Enum.TryParse<WorkListFilterParameter.ValueType>(reader["Type"], out valueType)) &&
                        (Enum.TryParse<WorkListFilterParameter.OperatorType>(reader["Operator"], out operatorType)))
                    {
                        WorkListFilterParameter item = new WorkListFilterParameter();
                        item.Field = fieldType;
                        item.Type = valueType;
                        item.Operator = operatorType;
                        item.Value1 = reader["Value1"];
                        item.Value2 = reader["Value2"];

                        this.Parameters.Add(item);
                    
                    }

                    reader.Read();
                }

                reader.Read();
            }
        }

        public void WriteXml(System.Xml.XmlWriter writer)
        {
            writer.WriteAttributeString("Name", this.Name);
            writer.WriteAttributeString("Kind", Enum.GetName(this.Kind.GetType(), this.Kind));
            writer.WriteAttributeString("IsPublic", this.IsPublic.ToString());
            writer.WriteAttributeString("SortColumn", Enum.GetName(this.SortColumn.GetType(), this.SortColumn));
            writer.WriteAttributeString("SortDir", Enum.GetName(this.SortDirection.GetType(), this.SortDirection));

            writer.WriteStartElement("WorklistFilterParameters");
            foreach (WorkListFilterParameter item in this.Parameters)
            {
                writer.WriteStartElement("WorklistFilterParameter");

                writer.WriteAttributeString("Field", Enum.GetName(item.Field.GetType(), item.Field));
                writer.WriteAttributeString("Type", Enum.GetName(item.Type.GetType(), item.Type));
                writer.WriteAttributeString("Operator", Enum.GetName(item.Operator.GetType(), item.Operator));
                writer.WriteAttributeString("Value1", item.Value1);
                writer.WriteAttributeString("Value2", item.Value2);

                writer.WriteEndElement();
            }
            writer.WriteEndElement();
        }

        #endregion

        #region ICloneable Members

        public object Clone()
        {
            using (var ms = new MemoryStream())
            {
                XmlSerializer xs = new XmlSerializer(typeof(WorkListFilter));
                xs.Serialize(ms, this);
                ms.Position = 0;

                return (WorkListFilter)xs.Deserialize(ms);
            }
        }

        #endregion

        public void SetDefaultParameters()
        {
            this._parameters.Clear();
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.AccessionNumber, Type = WorkListFilterParameter.ValueType.SingleValueText });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.PatientID, Type = WorkListFilterParameter.ValueType.SingleValueText });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.PatientName, Type = WorkListFilterParameter.ValueType.SingleValueText });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.DateTime, Type = WorkListFilterParameter.ValueType.Date });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.ReservedBy, Type = WorkListFilterParameter.ValueType.SingleValueText });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.AcquisitionSite, Type = WorkListFilterParameter.ValueType.SingleValueText });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.ReportStatus, Type = WorkListFilterParameter.ValueType.ReportStatus });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.HasNotes, Type = WorkListFilterParameter.ValueType.YesNo });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.SpecimenCount, Type = WorkListFilterParameter.ValueType.SingleValueNumber });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.ConsultationStatus, Type = WorkListFilterParameter.ValueType.SingleValueText });
            this._parameters.Add(new WorkListFilterParameter { Field = WorkListFilterParameter.FilterFieldType.SnapshotCount, Type = WorkListFilterParameter.ValueType.SingleValueNumber });
        }

        public void CopyTo(WorkListFilter filter)
        {
            using (var ms = new MemoryStream())
            {
                XmlSerializer xs = new XmlSerializer(typeof(WorkListFilter));
                xs.Serialize(ms, this);
                ms.Position = 0;

                StreamReader sr = new StreamReader(ms);
                XmlReader xr = XmlReader.Create(sr);
                xr.Read();
                xr.MoveToElement();
                xr.MoveToContent();
                filter.ReadXml(xr);
            }
        }

        public WorkListFilterParameter.FilterMatchType MatchCase(Case caseObj)
        {
            if (!this.IsValid)
            {
                return WorkListFilterParameter.FilterMatchType.NotApplicable;
            }

            WorkListFilterParameter.FilterMatchType matchType = WorkListFilterParameter.FilterMatchType.NotApplicable;

            foreach (WorkListFilterParameter item in this.Parameters)
            {
                matchType = item.MatchCase(caseObj.GetField(item.Field));
                if (matchType == WorkListFilterParameter.FilterMatchType.NotMatch)
                {
                    // no need to check further
                    break;
                }
            }

            return matchType;
        }
    }
}
