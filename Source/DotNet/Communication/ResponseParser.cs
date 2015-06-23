// -----------------------------------------------------------------------
// <copyright file="ResponseParser.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: May 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Paul Pentapaty, Duc Nguyen
//  Description: Parsing utils to parse the response from database to data that the application can use
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
	using System.IO;
	using System.Linq;
	using System.Text;
	using System.Text.RegularExpressions;
	using System.Windows;
	using System.Xml;
	using System.Xml.Linq;
	using System.Xml.Serialization;
	using VistA.Imaging.Telepathology.Common.Exceptions;
	using VistA.Imaging.Telepathology.Common.Model;
	using VistA.Imaging.Telepathology.Common.VixModels;
	using VistA.Imaging.Telepathology.Logging;

	internal class ResponseParser
	{
		private static MagLogger Log = new MagLogger(typeof(ResponseParser));
	
		/// <summary>
		/// Parse the response to get current user information
		/// </summary>
		/// <param name="rawData">raw response about the user infomation retrieved from the database</param>
		internal static void ParseGetUserResponse(string rawData)
		{
			if (string.IsNullOrWhiteSpace(rawData))
			{
				Log.Error("Raw data doesn't have content.");
				return;
			}

			// split the response into lines
			string[] lines = Regex.Split(rawData, "\r\n");
			if ((lines != null) && (lines.Length > 0))
			{
				// the first line will contains all the user information
				string[] caretTokens = lines[0].Split('^');
				if ((caretTokens != null) && (caretTokens.Length >= 10))
				{
					UserContext.UserCredentials.Duz = caretTokens[0];
					UserContext.UserCredentials.Fullname = caretTokens[1];
					UserContext.UserCredentials.Initials = caretTokens[2];
					UserContext.UserCredentials.Ssn = caretTokens[3];
					UserContext.LocalSite.SiteStationNumber = caretTokens[4];
					UserContext.LocalSite.PrimarySiteStationNUmber = caretTokens[5];
					UserContext.LocalSite.SiteAbbreviation = caretTokens[7];
					UserContext.LocalSite.SiteName = caretTokens[8];
					UserContext.LocalSite.IsProductionAccount = (caretTokens[9] == "0") ? false : true;
					UserContext.SiteServiceUrl = caretTokens[6];
					UserContext.SiteServiceUrl = UserContext.SiteServiceUrl.Replace("SiteService", "ImagingExchangeSiteService");

                    // loging user's information
                    string logmsg = "User's Information:";
                    logmsg += "|Name: " + UserContext.UserCredentials.Fullname;
                    logmsg += "|Site Station Number: " + UserContext.LocalSite.SiteStationNumber;
                    logmsg += "|Primary Site Station Number: " + UserContext.LocalSite.PrimarySiteStationNUmber;
                    logmsg += "|Site Name: " + UserContext.LocalSite.SiteName;
                    logmsg += "|Production Account: " + UserContext.LocalSite.IsProductionAccount.ToString();
                    logmsg += "|Site Service URL: " + UserContext.SiteServiceUrl;
                    Log.Info(logmsg);
				}
				else
				{
					Log.Debug("Missing pieces in User information.");
				}

                if (lines.Length > 1)
                {
                    string localLRKeys = string.Empty;

                    // for each subsequence line contains a security keys for the user associate with LAB Package
                    for (int i = 1; i < lines.Length; i++)
                    {
                        if (lines[i] != string.Empty)
                        {
                            UserContext.UserCredentials.SecurityKeys.Add(lines[i]);
                            localLRKeys += Environment.NewLine + lines[i];
                        }
                    }

                    Log.Debug(string.Format("LR Keys:" + Environment.NewLine + "{0}", localLRKeys));
                }
			}
		}

		internal static List<string> ParseGetUserKeys(string rawData)
		{
			try
			{
				RestStringArrayType keys = DeserializeFromXml<RestStringArrayType>(rawData);
				return new List<string>(keys.Values);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return new List<string>();
			}
		}

		internal static int ParseGetApplicationTimeout(string rawData)
		{
			if (!string.IsNullOrWhiteSpace(rawData))
			{
				int minutes;
				bool success = int.TryParse(rawData, out minutes);
				if (success)
					return minutes;
				return -1;
			}

			return -1;
		}

		internal static int ParseGetRetentionDays(string rawData)
		{
			if (!string.IsNullOrWhiteSpace(rawData))
			{
				string[] lines = Regex.Split(rawData.Trim(), "\r\n");
				if ((lines != null) && (lines.Length >= 2))
				{
                    string[] pieces = lines[0].Trim().Split('^');
					if ((pieces != null) && (pieces.Length >= 1) && (pieces[0] == "1"))
					{
						int minutes;
						bool success = int.TryParse(lines[1], out minutes);
						if (success)
							return minutes;

						Log.Error("Value not in right format. " + rawData);
						return -1;
					}

					Log.Error("Error response: " + rawData);
					return -1;
				}

				Log.Error("Eror response: " + rawData);
				return -1;
			}

			return 0;
		}

		internal static bool ParseSetApplicationTimeout(string rawData)
		{
			if (!string.IsNullOrWhiteSpace(rawData))
			{
				string[] pieces = rawData.Split('^');
				if ((pieces != null) || (pieces.Length >= 1))
				{
					if (pieces[0] == "1")
					{
						return true;
					}
				}
			}

			return false;
		}

		internal static ObservableCollection<SiteInfo> ParseGetInstitutionListResponse(string rawData)
		{
			// parse the rawdata into a list of institutions
			PathologySitesType siteList;
			try
			{
				siteList = DeserializeFromXml<PathologySitesType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error(rpf.ErrorMessage, rpf.InnerException);
				siteList = new PathologySitesType();
			}

			List<SiteInfo> unsortedList = new List<SiteInfo>();
			foreach (VixSiteObject site in siteList.Items)
			{
				unsortedList.Add(new SiteInfo() { SiteAbr = site.SiteAbbr, 
												  SiteName = site.SiteName, 
												  SiteStationNumber = site.StationNumber, 
												  Active = true });
			}

			// sort the list by site name
			List<SiteInfo> sorted;
			sorted = unsortedList.OrderBy(o => o.SiteName).ToList();
			return new ObservableCollection<SiteInfo>(sorted);
		}

		internal static ObservableCollection<ReportTemplate> ParseGetReportTemplates(string rawData)
		{
			PathologyTemplatesType templateStrings;
			try
			{
				templateStrings = DeserializeFromXml<PathologyTemplatesType>(rawData);
				return templateStrings.GetTemplateObjects();
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return new ObservableCollection<ReportTemplate>();
			}
		}

		internal static VixReportTemplateObject ParseGetReportTemplate(string rawData)
		{
			VixReportTemplateObject myTemplate;
			try
			{
				string trimData = rawData.Replace("\n", string.Empty);
				myTemplate = DeserializeFromXml<VixReportTemplateObject>(trimData);
				return myTemplate;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return new VixReportTemplateObject();
			}
		}

		/// <summary>
		/// Parse the xml to provide a list of acquisition sites information
		/// </summary>
		/// <param name="rawData">raw response from the VIX</param>
		/// <returns>a list of acquisition sites</returns>
		internal static AcquisitionSiteList ParseGetAcquisitionSitesResponse(string rawData)
		{
			AcquisitionSiteList myList;
			try
			{
				myList = DeserializeFromXml<AcquisitionSiteList>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error(rpf.ErrorMessage, rpf.InnerException);
				myList = new AcquisitionSiteList();
			}

			return myList;
		}

		/// <summary>
		/// Parse the xml to provide a list of reading site information
		/// </summary>
		/// <param name="rawData">raw response from the VIX</param>
		/// <returns>a list of reading sites</returns>
		internal static ReadingSiteList ParseGetReadingSiteResponse(string rawData)
		{
			ReadingSiteList myList;
			try
			{
				myList = DeserializeFromXml<ReadingSiteList>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error(rpf.ErrorMessage, rpf.InnerException);
				myList = new ReadingSiteList();
			}

			return myList;
		}

        /// <summary>
        /// Parse the xml to provide a list of cases for unread/read/patient list
        /// </summary>
        /// <param name="rawData">raw response from the VIX</param>
        /// <returns>a list of cases</returns>
        internal static CaseList ParseGetCaseListResponse(string rawData)
        {
            CaseList myList;
            try
            {
                myList = DeserializeFromXml<CaseList>(rawData);
                return myList;
            }
            catch (MagResponseParsingFailureException rpf)
            {
                Log.Error("Could not parse response.", rpf);
                return new CaseList();
            }
        }

        ///// <summary>
        ///// Parse the xml to provide a list of cases for unread/read/patient list
        ///// </summary>
        ///// <param name="rawData">raw response from the VIX</param>
        ///// <returns>a list of cases</returns>
        //internal static CaseConsultationList ParseGetInterpretationListResponse(string rawData)
        //{
        //    CaseConsultationList myList;
        //    try
        //    {
        //        myList = DeserializeFromXml<CaseConsultationList>(rawData);
        //        return myList;
        //    }
        //    catch (MagResponseParsingFailureException rpf)
        //    {
        //        Log.Error("Could not parse response.", rpf);
        //        return new CaseConsultationList();
        //    }
        //}

        internal static CaseSpecimenList ParseGetCaseDetail(string rawData)
        {
            CaseSpecimenList myList;
            try
            {
                myList = DeserializeFromXml<CaseSpecimenList>(rawData);
                return myList;
            }
            catch (MagResponseParsingFailureException rpf)
            {
                Log.Error("Could not parse response.", rpf);
                return new CaseSpecimenList();
            }
        }

        internal static CaseSlideList ParseGetCaseSlidesInfo(string rawData)
        {
            CaseSlideList myList;
            try
            {
                myList = DeserializeFromXml<CaseSlideList>(rawData);
                return myList;
            }
            catch (MagResponseParsingFailureException rpf)
            {
                Log.Error("Could not parse response.", rpf);
                return new CaseSlideList();
            }
        }

		internal static bool ParseRequestInterpretation(string rawData)
		{
			PathologyCaseUpdateAttributeResultType result = ParseCaseUpdateAttributeResult(rawData);
			if (!result.BoolSuccess)
			{
				MessageBox.Show("Failed to request interpretation.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
				return false;
			}

			return true;
		}

		internal static PathologyCaseUpdateAttributeResultType ParseCaseUpdateAttributeResult(string rawData)
		{
			PathologyCaseUpdateAttributeResultType result;
			try
			{
				result = DeserializeFromXml<PathologyCaseUpdateAttributeResultType>(rawData);
				return result;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Failed to parse response.", rpf);
				return new PathologyCaseUpdateAttributeResultType();
			}
		}

		/// <summary>
		/// Parse the xml to provide patient information
		/// </summary>
		/// <param name="rawData">raw response from the VIX</param>
		/// <returns>a patient object with all information filled</returns>
		internal static Patient ParseGetPatientInfo(string rawData)
		{
			Patient myPatient;
			try
			{
				myPatient = DeserializeFromXml<Patient>(rawData);
                if (string.IsNullOrWhiteSpace(myPatient.PatientICN))
                    myPatient.PatientICN = myPatient.PatientDFN;
				return myPatient;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return new Patient();
			}
		}

		/// <summary>
		/// Parse the xml to provide the generated report for a case
		/// </summary>
		/// <param name="rawData">raw response from the VIX</param>
		/// <returns>string contains the report for the case</returns>
		internal static string ParseGetCPRSReport(string rawData)
		{
			string report = string.Empty;
			try
			{
				XDocument reportXML = XDocument.Parse(rawData);
				report = reportXML.Root.Element("value").Value;

                // TODO: Need to modify the RPC to return a proper format so that the 
                // client can parse out the information instead of hardcoding the search pattern
                // to ---- CYTOPATHOLOGY ----,---- ELECTRON MICROSCOPY ----,---- SURGICAL PATHOLOGY ----
                string[] rep = report.Split('\n');
                if (rep != null)
                {
                    if (rep.Length > 0)
                    {
                        bool foundMainRep = false;
                        int i = 0;
                        while (!foundMainRep)
                        {
                            if ((rep[i].Contains("---- CYTOPATHOLOGY ----")) ||
                                (rep[i].Contains("---- ELECTRON MICROSCOPY ----")) ||
                                (rep[i].Contains("---- SURGICAL PATHOLOGY ----")))
                            {
                                foundMainRep = true;
                            }
                            else
                                rep[i] = string.Empty;
                            i++;
                        }

                        report = string.Empty;
                        foreach (string line in rep)
                        {
                            if (!string.IsNullOrEmpty(line))
                                report += line + Environment.NewLine;
                        }
                    }
                }
			}
			catch (Exception ex)
			{
				report = string.Empty;
				Log.Error("Could not parse response.", ex);
			}

			return report;
		}

		internal static string ParseReadPreferences(string rawData)
		{
			try
			{
				RestStringType pref = DeserializeFromXml<RestStringType>(rawData);
				return pref.Value;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return null;
			}
		}

		internal static string ParseGetNotes(string rawData)
		{
			try
			{
				RestStringType notes = DeserializeFromXml<RestStringType>(rawData);
				return notes.Value;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return "ERROR: Notes cannot be parsed.";
			}
		}


		internal static PathologyCaseTemplateType ParseGetReport(string rawData)
		{
			PathologyCaseTemplateType rawRep = null;
			rawRep = DeserializeFromXml<PathologyCaseTemplateType>(rawData);
			return rawRep;
		}

		internal static PathologyElectronicSignatureNeedType ParseGetESignatureStatus(string rawData)
		{
            PathologyElectronicSignatureNeedType result;
			try
			{
                result = DeserializeFromXml<PathologyElectronicSignatureNeedType>(rawData);
			}
            catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
                result = new PathologyElectronicSignatureNeedType() { Status = ESigNeedType.error, Message = "Could not parse status result." };
			}

			return result;
		}

		internal static bool ParseVerifyESignature(string rawData)
		{
			bool result = ParseBooleanReturnType(rawData);
			return result;
		}

		internal static bool ParseBooleanReturnType(string rawData)
		{
			try
			{
				RestBooleanReturnType result = DeserializeFromXml<RestBooleanReturnType>(rawData);
				return result.BoolResult;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse data.", rpf);
				return false;
			}
		}

		internal static PatientSensitiveValueType ParseGetPatientSensitiveLevel(string rawData)
		{
			PatientSensitiveValueType result;
			try
			{
				result = DeserializeFromXml<PatientSensitiveValueType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				result = new PatientSensitiveValueType();
				Log.Error("Could not parse response.", rpf);
			}

			return result;
		}

		internal static RestBooleanReturnType ParsePatientSensitiveAccessLog(string rawData)
		{
			RestBooleanReturnType result;
			try
			{
				result = DeserializeFromXml<RestBooleanReturnType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Failed to parse response.", rpf);
				result = new RestBooleanReturnType();
			}

			return result;
		}

		internal static RestBooleanReturnType ParseUpdateConsultationStatus(string rawData)
		{
			RestBooleanReturnType result;
			try
			{
				result = DeserializeFromXml<RestBooleanReturnType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				result = new RestBooleanReturnType();
				Log.Error("Could not parse response.", rpf);
			}

			return result;
		}

		internal static bool ParseCheckPendingConsultation(string rawData)
		{
			try
			{
				RestBooleanReturnType result = DeserializeFromXml<RestBooleanReturnType>(rawData);
				return result.BoolResult;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return false;
			}
		}

		internal static string ParseGetReportLockTimeoutHour(string rawData)
		{
			try
			{
				int vixHour = ParseIntergerType(rawData);
                if (vixHour > 0)
                    return vixHour.ToString();
                else
                {
                    return "-1";
                }
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse data.", rpf);
				return "-1";
			}
		}

		internal static int ParseIntergerType(string rawData)
		{
			RestIntegerType myInt = null;
			myInt = DeserializeFromXml<RestIntegerType>(rawData);
			return myInt.IntValue;
		}

		/// <summary>
		/// Parse the xml to provide a list of supplementary reports for the case
		/// </summary>
		/// <param name="rawData">raw response from the VIX</param>
		/// <returns>a list contains all the supplementary report</returns>
		internal static SupplementaryReportList ParseGetSupplementalReportList(string rawData)
		{
			SupplementaryReportList myList;
			try
			{
				myList = DeserializeFromXml<SupplementaryReportList>(rawData);
				return myList;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return new SupplementaryReportList();
			}
		}

		internal static PathologyCaseUpdateAttributeResultType ParseLockCaseForEditing(string xmlContent)
		{
			PathologyCaseUpdateAttributeResultType result;
			try
			{
				result = DeserializeFromXml<PathologyCaseUpdateAttributeResultType>(xmlContent);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				result = new PathologyCaseUpdateAttributeResultType();
			}
	
			return result;
		}

		internal static PathologyCaseUpdateAttributeResultType ParseRequestConsultation(string rawData)
		{
			PathologyCaseUpdateAttributeResultType result;
			try
			{
				result = DeserializeFromXml<PathologyCaseUpdateAttributeResultType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				result = new PathologyCaseUpdateAttributeResultType();
			}

			return result;
		}

		internal static PathologySnomedCodesType ParseGetSnomedCodeForCase(string rawData)
		{
			PathologySnomedCodesType result;
			try
			{
				result = DeserializeFromXml<PathologySnomedCodesType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				result = new PathologySnomedCodesType();
			}

			return result;
		}

		internal static PathologyCptCodesType ParseGetCptCodesForCase(string rawData)
		{
			PathologyCptCodesType result;
			try
			{
				result = DeserializeFromXml<PathologyCptCodesType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				result = null;
			}

			return result;
		}

		internal static string ParseAddSnomedItem(string rawData)
		{
			try
			{
				RestStringType newID = DeserializeFromXml<RestStringType>(rawData);
				return newID.Value;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return null;
			}
		}

		internal static PathologyFieldValuesType ParseSearchSnomedItem(string rawData)
		{
			PathologyFieldValuesType result;
			try
			{
				result = DeserializeFromXml<PathologyFieldValuesType>(rawData);
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				result = new PathologyFieldValuesType();
				MessageBox.Show("Couldnt find items", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
			}

			return result;
		}

		internal static string ParseCreateCopyCase(string rawData)
		{
			string newAccession = null;
			try
			{
				PathologyCopyCaseResultType result = DeserializeFromXml<PathologyCopyCaseResultType>(rawData);
				newAccession = result.AccessionNumber + "^" + result.CaseURN;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Failed to parse response.", rpf);
			}

			return newAccession;
		}

		internal static PathologyCptCodeResultsType ParseAddCptCodesForCase(string rawData)
		{
			PathologyCptCodeResultsType result = null;
			result = DeserializeFromXml<PathologyCptCodeResultsType>(rawData);
			return result;
		}

		/// <summary>
		/// Deserialize XML items into custom objects
		/// </summary>
		/// <typeparam name="T">Type of the object to be produced</typeparam>
		/// <param name="xmlContent">xml presentation of the object</param>
		/// <returns>a new object type T initialized with all the information from the xml</returns>
		internal static T DeserializeFromXml<T>(string xmlContent)
		{
			T result;
			XmlSerializer serializer = new XmlSerializer(typeof(T));
			StringReader reader = new StringReader(xmlContent);
			try
			{
				result = (T)serializer.Deserialize(reader);
				return result;
			}
			catch (Exception ex)
			{
				throw new MagResponseParsingFailureException("Failed to deserialize xml string.", ex);
			}
		}

		/// <summary>
		/// Serializing object to xml format
		/// </summary>
		/// <typeparam name="T">Type of the object being serialized</typeparam>
		/// <param name="value">The object being serialized</param>
		/// <returns>XML string representing the object</returns>
		internal static string SerializeToXml<T>(T value)
		{
			string result = string.Empty;

			XmlSerializer serializer = new XmlSerializer(typeof(T));

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
						serializer.Serialize(xmlWriter, value);
					}
					return textWriter.ToString();
				}
			}
			catch (Exception ex)
			{
				throw new MagResponseParsingFailureException("Serialization failure.", ex);
			}
		}

		internal static HealthSummaryTypeList ParseGetHealthSummaryTypeListResponse(string rawData)
		{
			HealthSummaryTypeList myList;
			try
			{
				myList = DeserializeFromXml<HealthSummaryTypeList>(rawData);
				return myList;
			}
			catch (MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return new HealthSummaryTypeList();
			}
		}

		internal static string ParseGetHealthSummary(string rawData)
		{
			try
			{
				RestStringType result = DeserializeFromXml<RestStringType>(rawData);
				return result.Value;
			}
			catch(MagResponseParsingFailureException rpf)
			{
				Log.Error("Could not parse response.", rpf);
				return "ERROR: Could not retrieve health summary.";
			}
		}

        internal static PathologySaveCaseReportResultType ParseSaveReportChanges(string rawData)
        {
            try
            {
                PathologySaveCaseReportResultType result = DeserializeFromXml<PathologySaveCaseReportResultType>(rawData);
                return result;
            }
            catch (MagResponseParsingFailureException rpf)
            {
                Log.Error("Could not parse response.", rpf);
                return null;
            }
        }
	}
}
