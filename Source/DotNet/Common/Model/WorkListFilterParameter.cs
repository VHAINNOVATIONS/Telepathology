// -----------------------------------------------------------------------
// <copyright file="WorkListFilterParameter.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: Jan 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Paul Pentapaty, Duc Nguyen
//  Description: Filter Parameter for the worklist
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
    using System.ComponentModel;
    using System.Reflection;

    public class WorkListFilterParameter
    {
        /// <summary>
        /// Parameter match type
        /// </summary>
        public enum FilterMatchType
        {
            /// <summary>
            /// Not applicable
            /// </summary>
            NotApplicable,

            /// <summary>
            /// found a match
            /// </summary>
            Match,

            /// <summary>
            /// Not match
            /// </summary>
            NotMatch
        }

        /// <summary>
        /// Field type 
        /// </summary>
        public enum FilterFieldType
        {
            /// <summary>
            /// No field
            /// </summary>
            None,

            /// <summary>
            /// Accession number
            /// </summary>
            [DescriptionAttribute("Accession Number")]
            AccessionNumber,

            /// <summary>
            /// Patient ID
            /// </summary>
            [DescriptionAttribute("Patient ID")]
            PatientID,

            /// <summary>
            /// Patient name
            /// </summary>
            [DescriptionAttribute("Patient Name")]
            PatientName,

            /// <summary>
            /// Reservation state
            /// </summary>
            [DescriptionAttribute("Reserved State")]
            ReservedState,

            /// <summary>
            /// Reserved by 
            /// </summary>
            [DescriptionAttribute("Reserved By")]
            ReservedBy,

            /// <summary>
            /// Date time
            /// </summary>
            [DescriptionAttribute("Accession Date")]
            DateTime,

            /// <summary>
            /// Report status
            /// </summary>
            [DescriptionAttribute("Report Status")]
            ReportStatus,

            /// <summary>
            /// Acquisition site
            /// </summary>
            [DescriptionAttribute("Site")]
            AcquisitionSite,

            /// <summary>
            /// Has note
            /// </summary>
            [DescriptionAttribute("Notes")]
            HasNotes,

            [DescriptionAttribute("Consultation Status")]
            ConsultationStatus,

            [DescriptionAttribute("Specimen Count")]
            SpecimenCount,

            [DescriptionAttribute("Image Count")]
            SnapshotCount,

            [DescriptionAttribute("Slides Available")]
            SlidesAvailable
        }

        public enum OperatorType
        {
            None,
            [DescriptionAttribute("Is Equal To")]
            IsEqualTo,
            [DescriptionAttribute("Is Not Equal To")]
            IsNotEqualTo,
            [DescriptionAttribute("Contains")]
            Contains,
            [DescriptionAttribute("Does Not Contain")]
            DoesNotContain,
            [DescriptionAttribute("Starts With")]
            StartsWith,
            [DescriptionAttribute("Is Between")]
            IsBetween,
            [DescriptionAttribute("Is Earlier Than")]
            IsEarlierThan,
            [DescriptionAttribute("Is Later Than")]
            IsLaterThan,
            [DescriptionAttribute("Is One Of")]
            IsOneOf,
            [DescriptionAttribute("Is None Of")]
            IsNoneOf,
            [DescriptionAttribute("Is Empty")]
            IsNull,
            [DescriptionAttribute("Is Not Empty")]
            IsNotNull,
            [DescriptionAttribute("Is Today")]
            IsToday,
            [DescriptionAttribute("Is Greater Than")]
            IsGreaterThan,
            [DescriptionAttribute("Is Less Than")]
            IsLessThan
        }

        public enum ValueType
        {
            Date,
            SingleValueText,
            MultipleValueText,
            SingleValueNumber,
            MultipleValueNumber,
            YesNo,
            ReportStatus
        }

        public WorkListFilterParameter()
        {
            Operator = OperatorType.None;
        }

        public FilterFieldType Field { get; set; }
        public OperatorType Operator { get; set; }
        public ValueType Type { get; set; }
        public string Value1 { get; set; }
        public string Value2 { get; set; }

        public bool IsValid
        {
            get
            {
                return (!string.IsNullOrEmpty(Value1) || 
                        !string.IsNullOrEmpty(Value2) ||
                        (Operator == WorkListFilterParameter.OperatorType.IsToday));
            }
        }

        public FilterMatchType MatchCase(string fieldValue)
        {
            switch (this.Field)
            {
                case FilterFieldType.AccessionNumber:
                case FilterFieldType.PatientID:
                case FilterFieldType.PatientName:
                case FilterFieldType.ReservedState:
                case FilterFieldType.ReservedBy:
                case FilterFieldType.DateTime:
                case FilterFieldType.ReportStatus:
                case FilterFieldType.AcquisitionSite:
                case FilterFieldType.SlidesAvailable:
                case FilterFieldType.HasNotes:
                case FilterFieldType.SpecimenCount:
                case FilterFieldType.SnapshotCount:
                case FilterFieldType.ConsultationStatus:
                    FilterMatchType matchType = IsMatch(fieldValue);
                    return matchType;
                default:
                    return FilterMatchType.NotApplicable;
            }
        }

        private FilterMatchType IsMatch(string fieldValue)
        {
            if (!IsValid) return FilterMatchType.NotApplicable;

            switch (Type)
            {
                case ValueType.Date:
                    {
                        DateTime dtCase, compDate1, compDate2;
                        try
                        {
                            dtCase = DateTime.Parse(fieldValue);

                            compDate1 = DateTime.Today;
                            compDate2 = DateTime.Today;
                            if ((Operator == OperatorType.IsEqualTo) || (Operator == OperatorType.IsBetween) ||
                                (Operator == OperatorType.IsEarlierThan) || (Operator == OperatorType.IsLaterThan))
                                compDate1 = DateTime.Parse(Value1);

                            if (Operator == OperatorType.IsBetween)
                                compDate2 = DateTime.Parse(Value2);
                        }
                        catch (Exception)
                        {
                            return FilterMatchType.NotMatch;
                        }

                        switch (Operator)
                        {
                            case OperatorType.IsEqualTo:
                                // check if the date for the case is equal to the date of the parameter value
                                return (dtCase.Date == compDate1.Date) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            case OperatorType.IsToday:
                                // check if the date for the case is today date
                                return (dtCase.Date == DateTime.Today.Date) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            case OperatorType.IsBetween:
                                // check if the date is between two dates
                                bool isLaterThan = (dtCase.CompareTo(compDate1) >= 0);
                                bool isEarlierThan = (dtCase.CompareTo(compDate2.AddDays(1)) <= 0);
                                return (isEarlierThan && isLaterThan) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            case OperatorType.IsEarlierThan:
                                // check if the date is earlier
                                return (dtCase.CompareTo(compDate1) <= 0) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            case OperatorType.IsLaterThan:
                                // check if the date is after
                                return (dtCase.CompareTo(compDate1.AddDays(1)) >= 0) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            default:
                                return FilterMatchType.NotMatch;
                        }
                    }
                case ValueType.YesNo:
                    {
                        // check for valid values
                        if ((string.IsNullOrWhiteSpace(fieldValue)) || (string.IsNullOrWhiteSpace(Value1)))
                            return FilterMatchType.NotMatch;

                        // check to see if the values match
                        if (this.Operator == OperatorType.IsEqualTo)
                        {
                            bool hasNotes;
                            bool res = bool.TryParse(fieldValue, out hasNotes);
                            if (res)
                            {
                                if (((Value1 == "Yes") && (hasNotes)) || ((Value1 == "No") && (!hasNotes)))
                                    return FilterMatchType.Match;
                            }
                        }

                        return FilterMatchType.NotMatch;
                    }
                case ValueType.ReportStatus:
                    {
                        // check for valid values
                        //if ((string.IsNullOrWhiteSpace(fieldValue)) || (string.IsNullOrWhiteSpace(Value1)))
                        //    return FilterMatchType.NotMatch;

                        //if (this.Operator == OperatorType.IsEqualTo)
                        //{
                        //    if (Value1.Trim().ToLower() == fieldValue.ToLower())
                        //    {
                        //        return FilterMatchType.Match;
                        //    }
                        //}

                        return FilterMatchType.NotMatch;
                    }
                case ValueType.SingleValueText:
                    {
                        if (fieldValue == null)
                            return FilterMatchType.NotMatch;

                        // all operations are case insensitive
                        switch (this.Operator)
                        {
                            case OperatorType.IsEqualTo:
                                {
                                    return string.Equals(fieldValue, Value1, StringComparison.CurrentCultureIgnoreCase) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                                }
                            case OperatorType.IsNotEqualTo:
                                {
                                    return string.Equals(fieldValue, Value1, StringComparison.CurrentCultureIgnoreCase) ? FilterMatchType.NotMatch : FilterMatchType.Match;
                                }
                            case OperatorType.Contains:
                                {
                                    return fieldValue.ToLower().Contains(Value1.ToLower()) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                                }
                            case OperatorType.DoesNotContain:
                                return fieldValue.ToLower().Contains(Value1.ToLower()) ? FilterMatchType.NotMatch : FilterMatchType.Match;
                            default:
                                return FilterMatchType.NotMatch;
                        }
                    }
                case ValueType.SingleValueNumber:
                    {
                        if (fieldValue == null)
                            return FilterMatchType.NotMatch;

                        // convert to number if a valid number then continue
                        bool isFieldNumber, isValNumber;
                        int fieldVal, val1;
                        isFieldNumber = Int32.TryParse(fieldValue, out fieldVal);
                        isValNumber = Int32.TryParse(Value1, out val1);
                        if ((!isFieldNumber) || (!isValNumber)) 
                            return FilterMatchType.NotMatch;

                        switch (this.Operator)
                        {
                            case OperatorType.IsEqualTo:
                                return (fieldVal == val1) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            case OperatorType.IsNotEqualTo:
                                return (fieldVal != val1) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            case OperatorType.IsGreaterThan:
                                return (fieldVal > val1) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            case OperatorType.IsLessThan:
                                return (fieldVal < val1) ? FilterMatchType.Match : FilterMatchType.NotMatch;
                            default:
                                return FilterMatchType.NotMatch;
                        }
                    }
                default:
                    {
                        return FilterMatchType.NotMatch;
                    }
            }
        }

        public static string ConvertEnumToString(FilterFieldType fieldType)
        {
            FieldInfo fi = fieldType.GetType().GetField(fieldType.ToString());
            if (fi != null)
            {
                var attributes = (DescriptionAttribute[])fi.GetCustomAttributes(typeof(DescriptionAttribute), false);

                return ((attributes.Length > 0) && (!String.IsNullOrEmpty(attributes[0].Description))) ?
                            attributes[0].Description : fieldType.ToString();
            }

            return null;
        }
    }
}
