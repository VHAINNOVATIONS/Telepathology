// -----------------------------------------------------------------------
// <copyright file="ViXClient.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: May 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Paul Pentapaty, Duc Nguyen
//  Description: Communication client that uses the VIX
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
    using System.Diagnostics;
    using System.IO;
    using System.Net;
    using System.Reflection;
    using System.ServiceModel;
    using System.Xml;
    using RestSharp;
    using VistA.Imaging.Telepathology.Common.Exceptions;
    using VistA.Imaging.Telepathology.Common.Model;
    using VistA.Imaging.Telepathology.Common.VixModels;
    using VistA.Imaging.Telepathology.Logging;

    public enum VixServiceTypes
    {
        Pathology,
        Patient,
        User
    }

    public class ViXClient
    {
        private static MagLogger Log = new MagLogger(typeof(ViXClient));

        public string VixServerName { get; private set; }

        public string VixServerPort { get; private set; }

        public string VixBasePathologyUrl { get; private set; }
        
        public string VixBasePatientUrl { get; private set; }
        
        public string VixBaseUserUrl { get; private set; }

        private const string VixMetadataOperationPathQueryString = "services/Service[@type='{0}']/Operation[@type='Metadata']/OperationPath";

        private const string IDSQueryFormatString = "http://{0}:{1}/IDSWebApp/VersionsService?type={2}";

        private const string SecureIDSQueryFormatString = "https://{0}:{1}/IDSWebApp/VersionsService?type={2}";

        private const string BaseUrlFormatString = "http://{0}:{1}/{2}{3}";

        private const string SecureBaseUrlFormatString = "https://{0}:{1}/{2}{3}";

        private const string VixUserName = "alexdelarge";

        private const string VixPassword = "655321";

        private const string VixApplicationPathQueryString = "services/Service[@type='{0}']/ApplicationPath";

        public ViXClient()
        {            
            // Since we are using secured transaction, need to ignore the certificate error
            ServicePointManager.ServerCertificateValidationCallback += IgnoreCertificateErrorHandler;
        }

        public RestClient GetRestClient(string transactionId, VixServiceTypes serviceType)
        {
            RestClient client = new RestClient();

            switch (serviceType)
            {
                case VixServiceTypes.Pathology:
                    client.BaseUrl = this.VixBasePathologyUrl;
                    break;
                case VixServiceTypes.Patient:
                    client.BaseUrl = this.VixBasePatientUrl;
                    break;
                case VixServiceTypes.User:
                    client.BaseUrl = this.VixBaseUserUrl;
                    break;
                default :
                    client.BaseUrl = this.VixBasePathologyUrl;
                    break;
            }

            client.Authenticator = new HttpBasicAuthenticator(VixUserName, VixPassword);

            client.AddDefaultHeader("xxx-duz", UserContext.UserCredentials.Duz);
            client.AddDefaultHeader("xxx-fullname", UserContext.UserCredentials.Fullname);
            client.AddDefaultHeader("xxx-sitename", UserContext.UserCredentials.SiteName);
            client.AddDefaultHeader("xxx-sitenumber", UserContext.LocalSite.PrimarySiteStationNUmber);
            client.AddDefaultHeader("xxx-ssn", UserContext.UserCredentials.Ssn);
            client.AddDefaultHeader("xxx-option-context", "MAGTP_WORKLIST_MGR");
            client.AddDefaultHeader("xxx-transaction-id", transactionId);
            client.AddDefaultHeader("xxx-securityToken", UserContext.UserCredentials.SecurityToken);
            client.AddDefaultHeader("xxx-client-version", FileVersionInfo.GetVersionInfo(Assembly.GetExecutingAssembly().Location).ProductVersion);

            return client;
        }

        public void InitializeConnection()
        {
            Log.Info("Initializing VIX connection...");

            // try to retrieve a list of connections with the new site service interface
            HttpWebResponse response;
            try
            {
                string serviceUrl = UserContext.SiteServiceUrl.Replace("/ImagingExchangeSiteService.asmx", "/restservices/siteservice/site/" + UserContext.LocalSite.PrimarySiteStationNUmber);
                HttpWebRequest request = WebRequest.Create(serviceUrl) as HttpWebRequest;
                response = request.GetResponse() as HttpWebResponse;
            }
            catch (WebException wex)
            {
                Log.Error("Failed to request site service.", wex);
                using (var stream = wex.Response.GetResponseStream())
                using (var reader = new StreamReader(stream))
                {
                    Log.Error(reader.ReadToEnd());
                }
                throw new MagVixFailureException("Could not retrieve site service.", wex);
            }
            catch (Exception ex)
            {
                Log.Error("Failed to request site service.", ex);
                throw new MagVixFailureException("Could not retrieve site service.", ex);
            }

            // try to parse the connections to find a secure connection port and server for secure VIXS
            try
            {
                XmlDocument xmlResponse = new XmlDocument();
                xmlResponse.Load(response.GetResponseStream());
                string xpath = "descendant::connection[protocol='VIXS']";
                XmlNode vixsConnection = xmlResponse.SelectSingleNode(xpath);
                if (vixsConnection != null)
                {
                    this.VixServerName = vixsConnection["server"].InnerText;

                    this.VixServerPort = vixsConnection["port"].InnerText;

                    if (string.IsNullOrWhiteSpace(this.VixServerName))
                    {
                        throw new MagVixFailureException("Cannot find VIXS server.");
                    }
                    if (string.IsNullOrWhiteSpace(this.VixServerPort))
                    {
                        throw new MagVixFailureException("Cannot find VIXS port.");
                    }
                }
                else
                {
                    throw new MagVixFailureException("A secure connection cannot be established.");
                }
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Failed to retrieve VIXS info.", vfe);
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Cannot parse site service response.", ex);
            }
            
            // get application path and path to rest services
            this.VixBasePathologyUrl = GetBaseURL(VixServiceTypes.Pathology);
            this.VixBasePatientUrl = GetBaseURL(VixServiceTypes.Patient);
            this.VixBaseUserUrl = GetBaseURL(VixServiceTypes.User);

            Log.Info("VIX connection initialized successfully.");
        }

        private string GetBaseURL(VixServiceTypes serviceType)
        {
            Log.Debug("Retrieving base URLs for " + serviceType.ToString() + " service...");

            string ServiceType;
            string BaseUrl = string.Empty;

            // change facace based on type
            switch (serviceType)
            {
                case VixServiceTypes.Pathology:
                    ServiceType = "Pathology";
                    break;
                case VixServiceTypes.Patient:
                    ServiceType = "Patient";
                    break;
                case VixServiceTypes.User:
                    ServiceType = "User";
                    break;
                default:
                    ServiceType = "Pathology";
                    break;
            }

            string IDSUrl = string.Format(SecureIDSQueryFormatString, this.VixServerName, this.VixServerPort, ServiceType);

            // getting the application path
            HttpWebRequest webRequest = (HttpWebRequest)WebRequest.Create(IDSUrl);
            webRequest.Credentials = new NetworkCredential(VixUserName, VixPassword);
            HttpWebResponse response;
            try
            {
                response = (HttpWebResponse)webRequest.GetResponse();
            }
            catch (Exception ex)
            {
                Log.Error("IDS: " + IDSUrl, ex);
                throw new MagVixFailureException("Cannot request IDS URL", ex);
            }

            // getting the application and operation path
            string VixApplicationPath = string.Empty;
            string VixOperationPath = string.Empty;
            using (Stream webStream = response.GetResponseStream())
            using (XmlTextReader xmlReader = new XmlTextReader(webStream))
            using (StreamReader webReader = new StreamReader(webStream))
            {
                XmlDocument xmlDoc = new XmlDocument();

                try
                {
                    xmlDoc.Load(xmlReader);
                    string nodeAppPath = string.Format(VixApplicationPathQueryString, ServiceType);
                    string nodeOperationPath = string.Format(VixMetadataOperationPathQueryString, ServiceType);
                    VixApplicationPath = xmlDoc.SelectSingleNode(nodeAppPath).InnerText;
                    VixOperationPath = xmlDoc.SelectSingleNode(nodeOperationPath).InnerText;
                }
                catch (Exception ex)
                {
                    Log.Error("Failed to get app/op path.", ex);
                    throw new MagVixFailureException("Failed to retrieve application and operation path", ex);
                }
            }

            // get the base url
            BaseUrl = string.Format(SecureBaseUrlFormatString, this.VixServerName, this.VixServerPort, VixApplicationPath, VixOperationPath);

            return BaseUrl;
        }

        public bool IgnoreCertificateErrorHandler(object sender, System.Security.Cryptography.X509Certificates.X509Certificate certificate,
            System.Security.Cryptography.X509Certificates.X509Chain chain, System.Net.Security.SslPolicyErrors sslPolicyErrors)
        {
            // if you want you can examine the certificate here to determine if it should be accepted.  The certificate will have a root issued to “VixFederation”.  
            return true;
        }


        public void InitializeSiteInformation()
        {
            // retrieve a list of acquisition sites for the current site
            // the local site will use these sites to retrieve data from
            AcquisitionSiteList acquiList = GetAcquisitionSites(UserContext.LocalSite.PrimarySiteStationNUmber);
            //AcquisitionSiteList acquiList = new AcquisitionSiteList();

            // add local site as an acquisition site
            AcquisitionSiteInfo localSite = new AcquisitionSiteInfo()
            {
                Active = true,
                SiteAbr = UserContext.LocalSite.SiteAbbreviation,
                SiteName = UserContext.LocalSite.SiteName,
                SiteStationNumber = UserContext.LocalSite.SiteStationNumber,
                PrimeSiteAbr = UserContext.LocalSite.SiteAbbreviation,
                PrimeSiteName = UserContext.LocalSite.SiteName,
                PrimeSiteStationNumber = UserContext.LocalSite.PrimarySiteStationNUmber,
            };

            // add local site to list of acq sites
            acquiList.Items.Add(localSite);
            UserContext.AcquisitionList = acquiList;

            // get the lab security keys for the user at acquisition sites
            foreach (AcquisitionSiteInfo acSite in UserContext.AcquisitionList.Items)
            {
                GetUserKeys(acSite.PrimeSiteStationNumber);
            }
        }

        private IRestResponse ExecuteGet(string URI, VixServiceTypes serviceType)
        {
            RestRequest request = new RestRequest(URI, Method.GET);
            request.AddHeader("Accept", "text/plain,application/xml");
            request.AddParameter("context", UserContext.ApplicationContext, ParameterType.UrlSegment);
            string transactionId = "{" + Guid.NewGuid().ToString() + "}";

            RestClient client = GetRestClient(transactionId, serviceType);

            Log.Debug("Executing VIX GET. Transaction ID: " + transactionId + "...");
            IRestResponse response = client.Execute(request);
            return response;
        }

        private IRestResponse ExecutePost(string URI, VixServiceTypes serviceType, string requestInput)
        {
            RestRequest request = new RestRequest(URI, Method.POST);
            request.AddHeader("Accept", "text/plain,application/xml");
            request.AddParameter("context", UserContext.ApplicationContext, ParameterType.UrlSegment);
            request.AddParameter("application/xml", requestInput, ParameterType.RequestBody);
            string transactionId = "{" + Guid.NewGuid().ToString() + "}";

            RestClient client = GetRestClient(transactionId, serviceType);

            Log.Debug("Executing VIX POST. Transaction ID: " + transactionId + "...");
            IRestResponse response = client.Execute(request);

            return response;
        }

        private IRestResponse ExecuteDelete(string URI, VixServiceTypes serviceType, string requestInput = "")
        {
            RestRequest request = new RestRequest(URI, Method.DELETE);
            request.AddHeader("Accept", "text/plain,application/xml");
            request.AddParameter("context", UserContext.ApplicationContext, ParameterType.UrlSegment);
            request.AddParameter("application/xml", requestInput, ParameterType.RequestBody);
            string transactionId = "{" + Guid.NewGuid().ToString() + "}";

            RestClient client = GetRestClient(transactionId, serviceType);

            Log.Debug("Executing VIX DELETE. Transaction ID: " + transactionId + "...");
            IRestResponse response = client.Execute(request);

            return response;
        }

        /// <summary>
        /// Validate to make sure the response is valid with expected result;
        /// </summary>
        /// <param name="response">Response from the VIX call</param>
        public void ValidateRestResponse(IRestResponse response)
        {
            if (response == null)
                throw new MagVixFailureException("Null response.");
            if (response.StatusCode != HttpStatusCode.OK)
                throw new MagVixFailureException("Response is not ok: " + response.StatusCode.ToString() + Environment.NewLine + response.Content);
            if (string.IsNullOrWhiteSpace(response.Content))
                throw new MagVixFailureException("Response content is empty.");
            if (response.Content.StartsWith("<html>"))
                throw new MagVixFailureException("VIX error: " + response.Content);
            if (response.Content.Contains("<restExceptionMessage>"))
                throw new MagVixFailureException("VIX exception error: " + response.Content);
        }

        public bool IsSiteSupportTelepathology(string SiteStationNumber)
        {
            Log.Debug("Checking if site " + SiteStationNumber + " supports telepathology...");

            // call a general vix services, if exception returns then site not supportive
            if (string.IsNullOrWhiteSpace(SiteStationNumber))
            {
                return false;
            }

            string URI = String.Format("pathology/lock/{0}", SiteStationNumber);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                return true;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Not supportive for telepathology", vfe);
                return false;
            }
        }

        #region VIX WORKLIST

        /// <summary>
        /// Retrieve a list of unread cases from vista
        /// </summary>
        /// <param name="siteID">site id to retrieve the list from</param>
        /// <returns>a case list of the site</returns>
        public CaseList GetUnreleasedCases(string siteID, int dayrange)
        {
            Log.Debug("Retrieving unread cases for site " + siteID + "...");

            string URI;
            if (siteID == UserContext.LocalSite.PrimarySiteStationNUmber)
            {
               URI = String.Format("pathology/cases/unreleased/{0}/{1}", siteID, dayrange);
            }
            else
            {
                URI = String.Format("pathology/cases/unreleased/{0}/{1}/{2}", siteID, dayrange, UserContext.LocalSite.SiteStationNumber);
            }
            IRestResponse response;
            CaseList result;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetCaseListResponse(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                result = new CaseList();
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve unread cases.", ex);
                result = new CaseList();
            }

            return result;
        }

        /// <summary>
        /// Retrieve a list of released cases from vista
        /// </summary>
        /// <param name="siteID">site storing the cases</param>
        /// <returns>a list of read cases from the site</returns>
        public CaseList GetReleasedCases(string siteID, int dayrange)
        {
            Log.Debug("Retrieving released cases for site " + siteID + "...");

            string URI;
            if (siteID == UserContext.LocalSite.PrimarySiteStationNUmber)
            {
                URI = String.Format("pathology/cases/released/{0}/{1}", siteID, dayrange);
            }
            else
            {
                URI = String.Format("pathology/cases/released/{0}/{1}/{2}", siteID, dayrange, UserContext.LocalSite.SiteStationNumber);
            }

            CaseList result;
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetCaseListResponse(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                result = new CaseList();
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve released cases.", ex);
                result = new CaseList();
            }

            return result;
        }

        /// <summary>
        /// Retrieving patient's cases
        /// </summary>
        /// <param name="siteID">site store the cases</param>
        /// <param name="patientICN">patient unique id</param>
        /// <returns>a list of case related to the patient</returns>
        public CaseList GetPatientCases(string siteID, string patientICN)
        {
            Log.Debug("Retrieving patient related cases for site " + siteID);

            string URI;
            if (siteID == UserContext.LocalSite.PrimarySiteStationNUmber)
            {
                URI = String.Format("pathology/cases/patient/{0}/{1}", siteID, patientICN);
            }
            else
            {
                URI = String.Format("pathology/cases/patient/{0}/{1}/{2}", siteID, patientICN, UserContext.LocalSite.SiteStationNumber);
            }
            CaseList result;
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetCaseListResponse(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                result = new CaseList();
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve patient's cases.", ex);
                result = new CaseList();
            }

            return result;
        }

        /// <summary>
        /// Retrieving a specific list of cases
        /// </summary>
        /// <param name="caseURNs">list of case urns</param>
        /// <returns>list of cases</returns>
        public CaseList GetCases(List<string> caseURNs)
        {
            Log.Debug("Retrieving cases...");

            string URI = String.Format("pathology/cases");
            CaseList result;
            IRestResponse response;
            try
            {
                RestStringArrayType urns = new RestStringArrayType(caseURNs);
                string body = ResponseParser.SerializeToXml<RestStringArrayType>(urns);

                response = ExecutePost(URI, VixServiceTypes.Pathology, body);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetCaseListResponse(response.Content);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                Log.Error("Could not serialize urn lists.", rpf);
                result = new CaseList();
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                result = new CaseList();
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve cases.", ex);
                result = new CaseList();
            }

            return result;
        }

        /// <summary>
        /// Retrieve case deteail
        /// </summary>
        /// <param name="caseURN">case id to be retrieved</param>
        /// <returns>detail of the case</returns>
        public CaseSpecimenList GetCaseDetail(string caseURN)
        {
            Log.Debug("Retrieving case details...");

            CaseSpecimenList myCase = new CaseSpecimenList();

            string URI = String.Format("pathology/case/specimens/{0}", caseURN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                myCase = ResponseParser.ParseGetCaseDetail(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve case detail.", ex);
            }

            return myCase;
        }
        
        /// <summary>
        /// Reserve a case for reading
        /// </summary>
        /// <param name="caseURN">case being read</param>
        /// <param name="reserveCase">reserve or unreserve</param>
        public void ReserveCase(string caseURN, bool reserveCase)
        {
            Log.Debug("Reserving case...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                throw new MagVixFailureException("Missing parameter: caseURN.");
            }

            string URI = String.Format("pathology/case/reserve/{0}/{1}", caseURN, reserveCase);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to reserve case.", ex);
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
            Log.Debug("Requesting interpretation entry...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return false;
            }

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return false;
            }

            string URI = String.Format("pathology/case/interpretation/{0}/{1}", caseURN, siteID);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                PathologyCaseUpdateAttributeResultType res = ResponseParser.ParseRequestConsultation(response.Content);
                if (!res.BoolSuccess)
                {
                    throw new MagVixFailureException(res.ErrorMessage);
                }

                return true;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return false;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to request an interpretation entry.", ex);
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
            Log.Debug("Requesting consultation...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return false;
            }

            if (consultingSite == null)
            {
                Log.Error("Missing parameter: consultingSite.");
                return false;
            }

            string URI = String.Format("pathology/case/consultation/{0}/{1}", caseURN, consultingSite.SiteStationNumber);
            IRestResponse response = null;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                PathologyCaseUpdateAttributeResultType res = ResponseParser.ParseRequestConsultation(response.Content);
                if (!res.BoolSuccess)
                {
                    throw new MagVixFailureException(res.ErrorMessage);
                }

                return true;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response", vfe);
                return false;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to request consultation.", ex);
                return false;
            }
        }

        /// <summary>
        /// Retrieve user's lab keys at specific site
        /// </summary>
        /// <param name="siteID">site to get keys from</param>
        private void GetUserKeys(string siteID)
        {
            Log.Debug("Retrieving user's LR keys at site " + siteID + "...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return;
            }

            string URI = String.Format("pathology/keys/{0}", siteID);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                List<string> labKeys = ResponseParser.ParseGetUserKeys(response.Content);

                if (UserContext.UserCredentials.LabSecurityKeys.ContainsKey(siteID))
                {
                    UserContext.UserCredentials.LabSecurityKeys.Remove(siteID);
                }
                UserContext.UserCredentials.LabSecurityKeys.Add(siteID, labKeys);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve user keys.", ex);
                return;
            }
        }

        /// <summary>
        /// Saves the preferences.
        /// </summary>
        /// <param name="siteID">The site at which the preferences are stored.</param>
        /// <param name="label">a label for the preferences.</param>
        /// <param name="userDUZ">The user for whom the preferences are stored. logged in user duz is used if null or empty</param>
        public void SavePreferences(string siteID, string label, string userDUZ, string data)
        {
            Log.Debug("Saving user preferences...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                throw new MagVixFailureException("Missing parameter: siteID.");
            }

            if (string.IsNullOrWhiteSpace(label))
            {
                throw new MagVixFailureException("Missing parameter: label.");
            }

            if (string.IsNullOrWhiteSpace(data))
            {
                throw new MagVixFailureException("Missing parameter: data.");
            }

            string URI;
            if (string.IsNullOrWhiteSpace(userDUZ))
            {
                // current logged in user
                URI = String.Format("pathology/preferences/{0}/{1}", siteID, label);
            }
            else
            {
                URI = String.Format("pathology/preferences/{0}/{1}/{2}", siteID, label, userDUZ);
            }

            IRestResponse response;
            try
            {
                //string text = string.Format("<restStringType><value>{0}</value></restStringType>", EscapeXml(data));
                RestStringType pref = new RestStringType() { Value = data };
                string body = ResponseParser.SerializeToXml<RestStringType>(pref);

                response = ExecutePost(URI, VixServiceTypes.Pathology, body);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Could not serialize preferences.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save preferences.", ex);
            }
        }

        /// <summary>
        /// Retrieve stored user preferences
        /// </summary>
        /// <param name="label">preference label</param>
        /// <returns>user preferences</returns>
        public string ReadPreferences(string siteID, string label, string userDUZ)
        {
            Log.Debug("Retrieving user preferences...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(label))
            {
                Log.Error("Missing parameter: label.");
                return null;
            }

            string URI;
            if (string.IsNullOrWhiteSpace(userDUZ))
            {
                // current logged in user
                URI = String.Format("pathology/preferences/{0}/{1}", siteID, label);
            }
            else
            {
                URI = String.Format("pathology/preferences/{0}/{1}/{2}", siteID, label, userDUZ);
            }

            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                // PPP: Commented. This call will fail if no user preferences exist.
                //ValidateRestResponse(response); 
                string pref = ResponseParser.ParseReadPreferences(response.Content);
                return pref;
                //try
                //{
                //    XmlDocument doc = new XmlDocument();
                //    doc.LoadXml(response.Content);
                //    XmlNode node = doc.SelectSingleNode("//value");
                //    return (node != null) ? UnescapeXml(node.InnerXml) : null;
                //}
                //catch (Exception ex)
                //{
                //    return null;
                //}
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return null;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve preferences.", ex);
                return null;
            }
        }

        /// <summary>
        /// Retrieve a list of health summary type
        /// </summary>
        /// <param name="siteID">site storing the summaries</param>
        /// <returns>a list of summary type</returns>
        public HealthSummaryTypeList GetHealthSummaryTypeList(string siteID)
        {
            Log.Debug("Retrieving health summary type list...");

            HealthSummaryTypeList healthSummaryTypeList = new HealthSummaryTypeList();
            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return healthSummaryTypeList;
            }

            string URI = String.Format("healthsummaries/{0}", siteID);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Patient);
                ValidateRestResponse(response);
                healthSummaryTypeList = ResponseParser.ParseGetHealthSummaryTypeListResponse(response.Content);
                if ((healthSummaryTypeList != null) && (healthSummaryTypeList.Items != null))
                {
                    foreach (HealthSummaryType type in healthSummaryTypeList.Items)
                    {
                        type.SiteID = siteID;
                    }
                }
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("UNexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve health summary type list.", ex);
            }

            return healthSummaryTypeList;
        }

        /// <summary>
        /// Retrieve health summary
        /// </summary>
        /// <param name="patientICN">patient id</param>
        /// <param name="healthSummaryType">type of health summary</param>
        /// <returns>summary for the patient</returns>
        public string GetHealthSummary(string patientICN, string healthSummaryType)
        {
            Log.Debug("Retrieving health summary for the patient...");

            string text = "ERROR: Could not retrieve health summary.";

            if (string.IsNullOrWhiteSpace(patientICN))
            {
                Log.Error("Missing parameter: patientICN.");
                return text;
            }

            if (string.IsNullOrWhiteSpace(healthSummaryType))
            {
                Log.Error("Missing parameter: health summary type.");
                return text;
            }

            string URI = String.Format("healthsummary/{0}/{1}", patientICN, healthSummaryType);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Patient);
                ValidateRestResponse(response);
                text = ResponseParser.ParseGetHealthSummary(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve health summary.", ex);
            }

            return text;
        }

        /// <summary>
        /// Retrieve notes for a case
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <returns>note for the case</returns>
        public string GetNotes(string caseURN)
        {
            Log.Debug("Retrieving notes for case...");

            string text = "ERROR: Notes cannot be retrieved.";
            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return text;
            }

            string URI = String.Format("pathology/case/note/{0}", caseURN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                text = ResponseParser.ParseGetNotes(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve case notes.", ex);
            }

            return text;
        }

        /// <summary>
        /// Save case note to vista
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <param name="notes">notes to be saved</param>
        public void SaveNotes(string caseURN, string notes)
        {
            Log.Debug("Saving notes for case...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                throw new MagVixFailureException("Missing parameter: caseURN.");
            }

            if (notes == null)
            {
                throw new MagVixFailureException("Missing parameter: notes.");
            }

            string URI = String.Format("pathology/case/note/{0}", caseURN);
            IRestResponse response;
            try
            {
                RestStringType notesType = new RestStringType() { Value = notes };
                string body = ResponseParser.SerializeToXml<RestStringType>(notesType);
                response = ExecutePost(URI, VixServiceTypes.Pathology, body);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Cannot serialize notes.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save notes.", ex);
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
            // -1 = RPC/API failed
            // 0 = No display or action required
            // 1 = Display warning message
            // 2 = Display warning message - require OK to continue
            // 3 = Display warning message - do not continue

            Log.Debug("Retrieving patient sensitive level...");

            PatientSensitiveValueType result = new PatientSensitiveValueType();

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return result;
            }

            if (string.IsNullOrWhiteSpace(patientICN))
            {
                Log.Error("Missing parameter: patientICN.");
                return result;
            }

            string URI = String.Format("sensitive/check/{0}/{1}", siteID, patientICN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Patient);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetPatientSensitiveLevel(response.Content);
                return result;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return result;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve patient's sensitivity level.", ex);
                return result;
            }
        }

        /// <summary>
        /// Log user access to a sensitive patient record
        /// </summary>
        /// <param name="siteID">Site holding the record</param>
        /// <param name="patientICN">Patient's unique identification number</param>
        public void PatientSensitiveAccessLog(string siteID, string patientICN)
        {
            Log.Debug("Logging access to patient sensitive record...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                throw new MagVixFailureException("Missing parameter: siteID.");
            }

            if (string.IsNullOrWhiteSpace(patientICN))
            {
                throw new MagVixFailureException("Missing parameter: patientICN.");
            }

            string URI = String.Format("sensitive/log/{0}/{1}", siteID, patientICN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Patient);
                ValidateRestResponse(response);
                RestBooleanReturnType result = ResponseParser.ParsePatientSensitiveAccessLog(response.Content);
                if (!result.BoolResult)
                {
                    throw new MagVixFailureException("Failed to log access to patient sensitive record.");
                }
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to log access to patient sensitive record.", ex);
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
            Log.Debug("Checking patient registration...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return false;
            }

            if (string.IsNullOrWhiteSpace(patientICN))
            {
                Log.Error("Missing parameter: patientICN.");
                return false;
            }

            string URI = String.Format("information/{0}/{1}", siteID, patientICN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Patient);
                // checking to see if the response code is 200 (patient is registered0
                ValidateRestResponse(response);
                return true;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return false;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to check patient registration.", ex);
                return false;
            }
        }

        /// <summary>
        /// Retrieve patient's information
        /// </summary>
        /// <param name="siteID">Site has information</param>
        /// <param name="patientICN">Patient's ID</param>
        /// <returns>patient object with info</returns>
        public Patient GetPatient(string siteID, string patientICN)
        {
            Log.Debug("Retrieving patient info...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return new Patient();
            }

            if (string.IsNullOrWhiteSpace(patientICN))
            {
                Log.Error("Missing parameter: patientICN.");
                return new Patient();
            }

            string URI = String.Format("pathology/patient/{0}/{1}", siteID, patientICN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                return ResponseParser.ParseGetPatientInfo(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpectetd response.", vfe);
                return new Patient();
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve patient's info.", ex);
                return new Patient();
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
            Log.Debug("Locking case for editing...");

            PathologyCaseUpdateAttributeResultType result = new PathologyCaseUpdateAttributeResultType();
            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return result;
            }

            string URI = String.Format("pathology/case/lock/{0}/{1}", caseURN, lockCase);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseLockCaseForEditing(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to lock the case.", ex);
            }

            return result;
        }

        #endregion

        #region VIX CONFIGURATOR
        /// <summary>
        /// Check to see if the primary site is valid for adding
        /// </summary>
        /// <param name="stationNumber">The station number of the site</param>
        /// <returns>True if valida and false otherwise</returns>
        public bool IsPrimarySiteValid(string stationNumber)
        {
            Log.Debug("Validating primary site...");

            if (string.IsNullOrWhiteSpace(stationNumber))
            {
                Log.Error("Missing parameter: stationNumber.");
                return false;
            }

            // create soap client from config and override endpoint address
            ImagingExchangeSiteService.ImagingExchangeSiteServiceSoapClient soapClient = new ImagingExchangeSiteService.ImagingExchangeSiteServiceSoapClient("ImagingExchangeSiteServiceSoap");
            soapClient.Endpoint.Address = new EndpointAddress(UserContext.SiteServiceUrl);

            // Find the VIX host and port number with the checking site station number
            ImagingExchangeSiteService.ImagingExchangeSiteTO siteInfo;
            try
            {
                siteInfo = soapClient.getSite(stationNumber);
            }
            catch (Exception ex)
            {
                Log.Error("Could not get site info.", ex);
                return false;
            }

            if (!string.IsNullOrWhiteSpace(siteInfo.siteName))
            {
                return true;
            }

            return false;
        }

        /// <summary>
        /// Retrieve a list of available institutions in the user local VistA
        /// </summary>
        /// <returns>A collection of SiteInfo objects</returns>
        public ObservableCollection<SiteInfo> GetInstitutionList()
        {
            Log.Debug("Retrieving institution list...");

            ObservableCollection<SiteInfo> institutionList = new ObservableCollection<SiteInfo>();

            string URI = String.Format("pathology/sites/{0}", UserContext.LocalSite.PrimarySiteStationNUmber);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                institutionList = ResponseParser.ParseGetInstitutionListResponse(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve institution list.", ex);
            }

            Log.Debug(institutionList.Count + " institutions retrieved.");
            return institutionList;
        }

        /// <summary>
        /// Retrieve all the report templates saved at the acquisition site
        /// </summary>
        /// <param name="siteID">Site holding all the templates</param>
        /// <returns>A collection of report template objects</returns>
        public ObservableCollection<ReportTemplate> GetReportTemplates(string siteID)
        {
            Log.Debug("Retrieving all report templates...");

            ObservableCollection<ReportTemplate> templates = new ObservableCollection<ReportTemplate>();

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
            }

            string URI = String.Format("pathology/templates/{0}/{1}", siteID, "CY^EM^SP");
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                templates = ResponseParser.ParseGetReportTemplates(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve saved report templates.", ex);
            }

            return templates;
        }

        /// <summary>
        /// Retrieve a list of acquisition sites for the inquired station number
        /// </summary>
        /// <param name="siteStationNumber">Site being questioned</param>
        /// <returns>AcquisitionSiteList object contains all available acquisition site for the questioned site</returns>
        public AcquisitionSiteList GetAcquisitionSites(string siteStationNumber)
        {
            Log.Debug("Retrieving acquisition sites...");

            if (string.IsNullOrWhiteSpace(siteStationNumber))
            {
                Log.Error("Missing parameter: siteStationNumber.");
            }

            AcquisitionSiteList result = new AcquisitionSiteList();
            string URI = String.Format("pathology/acquisition/{0}", siteStationNumber);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetAcquisitionSitesResponse(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Response error.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve acquisition site list.", ex);
            }

            Log.Debug(result.Items.Count.ToString() + " acquisition sites retrieved.");
            return result;
        }

        /// <summary>
        /// Retrieve a list of reading sites for the inquiring site
        /// </summary>
        /// <param name="siteStationNumber">Site being questioned</param>
        /// <returns>ReadingSiteList object contains all available reading site for the questioned site</returns>
        public ReadingSiteList GetReadingSites(string siteStationNumber)
        {
            Log.Debug("Retrieving reading sites...");

            if (string.IsNullOrWhiteSpace(siteStationNumber))
            {
                Log.Error("Missing parameter: siteStationNumber.");
            }

            string URI = String.Format("pathology/reading/{0}", siteStationNumber);
            IRestResponse response;
            ReadingSiteList result = new ReadingSiteList();
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetReadingSiteResponse(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve reading site list.", ex);
            }

            Log.Debug(result.Items.Count.ToString() + " reading sites retrieved.");
            return result;
        }

        /// <summary>
        /// Save new and update existing reading site.
        /// </summary>
        /// <param name="siteStationNumber">Site where the data will be stored at.</param>
        /// <param name="readingSite">Reading site data to be stored.</param>
        public void SaveReadingSite(string siteStationNumber, PathologyReadingSiteType readingSite)
        {
            Log.Debug("Saving reading site to VistA...");

            if (string.IsNullOrWhiteSpace(siteStationNumber))
            {
                throw new MagVixFailureException("Missing parameter: site station number.");
            }

            if (readingSite == null)
            {
                throw new MagVixFailureException("Missing parameter: reading site.");
            }
            
            string URI = String.Format("pathology/reading/{0}", siteStationNumber);
            IRestResponse response;
            try
            {
                string requestBody = ResponseParser.SerializeToXml<PathologyReadingSiteType>(readingSite);
                response = ExecutePost(URI, VixServiceTypes.Pathology, requestBody);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Could not serialize reading site object.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save reading site.", ex);
            }
        }

        /// <summary>
        /// Removing a reading site from the reading site list in VistA
        /// </summary>
        /// <param name="siteStationNumber">Site where the data will be removed from.</param>
        /// <param name="readingSite">Reading site data being removed.</param>
        public void RemoveReadingSite(string siteStationNumber, PathologyReadingSiteType readingSite)
        {
            Log.Debug("Removing reading site from VistA...");

            if (string.IsNullOrWhiteSpace(siteStationNumber))
            {
                throw new MagVixFailureException("Missing parameter: site station number.");
            }

            if (readingSite == null)
            {
                throw new MagVixFailureException("Missing parameter: reading site.");
            }

            string URI = String.Format("pathology/reading/delete/{0}", siteStationNumber);
            IRestResponse response;
            try
            {
                string requestBody = ResponseParser.SerializeToXml<PathologyReadingSiteType>(readingSite);
                response = ExecutePost(URI, VixServiceTypes.Pathology, requestBody);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Could not serialize reading site object.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to remove reading site.", ex);
            }
        }

        /// <summary>
        /// Save new and update existing acquisition site
        /// </summary>
        /// <param name="siteStationNumber">Location of the acquisition site going to be stored at</param>
        /// <param name="acquisitionSite">Acquisition site to be updated</param>
        public void SaveAcquisitionSite(string siteStationNumber, PathologyAcquisitionSiteType acquisitionSite)
        {
            Log.Debug("Saving acquisition site to VistA...");

            // input check
            if (string.IsNullOrWhiteSpace(siteStationNumber))
            {
                throw new MagVixFailureException("Missing parameter: site station number.");
            }

            if (acquisitionSite == null)
            {
                throw new MagVixFailureException("Missing parameter: acquisition site.");
            }

            string URI = String.Format("pathology/acquisition/{0}", siteStationNumber);
            IRestResponse response;
            try
            {
                string requestBody = ResponseParser.SerializeToXml<PathologyAcquisitionSiteType>(acquisitionSite);
                response = ExecutePost(URI, VixServiceTypes.Pathology, requestBody);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Could not serialize reading site object.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save acquisition site.", ex);
            }
        }

        /// <summary>
        /// Removing an acquisition site from the acquisition site list in VistA
        /// </summary>
        /// <param name="siteStationNumber">The site to be removed from</param>
        /// <param name="acquisitionSite">The site being removed</param>
        public void RemoveAcquisitionSite(string siteStationNumber, PathologyAcquisitionSiteType acquisitionSite)
        {
            Log.Debug("Removing acquisition site from VistA...");

            if (string.IsNullOrWhiteSpace(siteStationNumber))
            {
                throw new MagVixFailureException("Missing parameter: siteStationNumber.");
            }

            if (acquisitionSite == null)
            {
                throw new MagVixFailureException("Missing parameter: acquisitionSite.");
            }

            string URI = String.Format("pathology/acquisition/delete/{0}", siteStationNumber);
            IRestResponse response;
            try
            {
                string requestBody = ResponseParser.SerializeToXml<PathologyAcquisitionSiteType>(acquisitionSite);
                response = ExecutePost(URI, VixServiceTypes.Pathology, requestBody);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Could not serialize reading site object.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to remove acquisition site.", ex);
            }
        }

        /// <summary>
        /// Save report template back to VistA
        /// </summary>
        /// <param name="templateObj">Template object containing the template to be stored</param>
        public void SaveReportTemplate(VixReportTemplateObject templateObj)
        {
            Log.Debug("Saving report template to VistA...");

            // input check
            if (templateObj == null)
            {
                throw new MagVixFailureException("Missing parameter: report template.");
            }

            if ((string.IsNullOrWhiteSpace(templateObj.TemplateSite)))
            {
                throw new MagVixFailureException("Missing parameter: site station number.");
            }

            if (string.IsNullOrWhiteSpace(templateObj.TemplateXML))
            {
                throw new MagVixFailureException("Missing parameter: template xml.");
            }

            PathologyTemplateInputType template = new PathologyTemplateInputType() { XmlTemplate = templateObj.TemplateXML };
            string URI = String.Format("pathology/template/{0}/{1}", templateObj.TemplateSite, templateObj.TemplateType);
            IRestResponse response;
            try
            {
                string body = ResponseParser.SerializeToXml<PathologyTemplateInputType>(template);
                response = ExecutePost(URI, VixServiceTypes.Pathology, body);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Failed to serialize report template.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save report template.", ex);
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
            Log.Debug("Checking for pending consultation status...");

            if (string.IsNullOrWhiteSpace(acquisitionSiteID))
            {
                Log.Error("Missing parameter: acquisitionSiteID.");
                return true;
            }

            if (string.IsNullOrWhiteSpace(readingSiteID))
            {
                Log.Error("Missing parameter: readingSiteID.");
                return true;
            }

            string URI = String.Format("pathology/consultations/{0}/{1}", acquisitionSiteID, readingSiteID);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                bool result = ResponseParser.ParseCheckPendingConsultation(response.Content);
                return result;
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to check consultation status.", ex);
                return true;
            }
        }

        /// <summary>
        /// Retrieve saved value for lock timeout.
        /// </summary>
        /// <param name="siteID">Site holding the data</param>
        /// <returns>The saved value or "Error"</returns>
        public string GetReportLockTimeoutHour(string siteID)
        {
            Log.Debug("Retrieving saved lock timeout hours...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                return "Error";
            }

            string URI = String.Format("pathology/lock/{0}", siteID);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                string result = ResponseParser.ParseGetReportLockTimeoutHour(response.Content);
                return result;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return "Error";
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve lock timeout hour.", ex);
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
            Log.Debug("Saving report timeout hours...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                throw new MagVixFailureException("Missing parameter: site location.");
            }

            if (string.IsNullOrWhiteSpace(hours))
            {
                throw new MagVixFailureException("Missing parameter: hours.");
            }

            string URI = String.Format("pathology/lock/{0}/{1}", siteID, hours);
            IRestResponse response;
            try
            {
                response = ExecutePost(URI, VixServiceTypes.Pathology, string.Empty);
                ValidateRestResponse(response);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save timeout hours.", ex);
            }
        }

        #endregion

        #region VIX REPORT
        /// <summary>
        /// Retrieve a template for a specific report type
        /// </summary>
        /// <param name="siteID">Site storing the template</param>
        /// <param name="templateType">type of the report</param>
        /// <returns>template for the report</returns>
        private VixReportTemplateObject GetReportTemplate(string siteID, string templateType)
        {
            Log.Debug("Retrieving template...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return new VixReportTemplateObject();
            }

            if (string.IsNullOrWhiteSpace(templateType))
            {
                Log.Error("Missing parameter: templateType");
                return new VixReportTemplateObject();
            }

            VixReportTemplateObject myTemplate;
            string URI = String.Format("pathology/templates/{0}/{1}", siteID, templateType);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                myTemplate = ResponseParser.ParseGetReportTemplate(response.Content);
                myTemplate.TemplateSite = siteID;
                myTemplate.TemplateType = templateType;
                return myTemplate;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return new VixReportTemplateObject();
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve report template.", ex);
                return new VixReportTemplateObject();
            }
        }

        /// <summary>
        /// Retrieve report for a case as displayed in CPRS
        /// </summary>
        /// <param name="caseURN">case identification</param>
        /// <returns>a string contains the report</returns>
        public string GetCPRSReport(string caseURN)
        {
            Log.Debug("Getting released report...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return "Report could not be retrieved.";
            }

            // return the report that is generated for CPRS using common API
            string URI = String.Format("pathology/case/report/{0}", caseURN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                string report = ResponseParser.ParseGetCPRSReport(response.Content);
                return report;
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return "Report could not be retrieved.";
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve released report.", ex);
                return "Report could not be retrieved.";
            }
        }

        /// <summary>
        /// Retrieve report data for a case
        /// </summary>
        /// <param name="item">Case item</param>
        /// <returns>Report data for the case in question</returns>
        public Report GetReport(CaseListItem item)
        {
            Log.Debug("Retrieving report data...");

            // Always return a valid object for binding
            Report rep = new Report();

            if (item == null)
            {
                Log.Error("Missing parameter: caselistitem.");
                return rep;
            }

            // first we need to get the report template based on the type indicate in the accession number
            AccessionNumber accNum = new AccessionNumber(item.AccessionNumber);
            VixReportTemplateObject templateObj = GetReportTemplate(item.SiteCode, accNum.Type);
            
            // in case there is an error in the tranmission
            if (templateObj.TemplateXML == string.Empty)
            {
                Log.Error("Failed to get template.");
                return rep;
            }

            // retrieve a list of fields to grab data from the template
            rep.ReportTemplate = templateObj.GetReportTemplate();
            //List<string> fields = rep.GetFieldList();
            List<string> fields = rep.GetAllFieldList();

            string URI = String.Format("pathology/case/template/{0}", item.CaseURN);
            IRestResponse response;
            try
            {
                PathologyCaseTemplateInputFieldsType fieldList = new PathologyCaseTemplateInputFieldsType(fields);
                string xml = ResponseParser.SerializeToXml<PathologyCaseTemplateInputFieldsType>(fieldList);
                response = ExecutePost(URI, VixServiceTypes.Pathology, xml);
                ValidateRestResponse(response);
                PathologyCaseTemplateType rawRep = ResponseParser.ParseGetReport(response.Content);
                rep.LoadReportData(rawRep);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                Log.Error("Failed to parse report fields.", rpf);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve report data.", ex);
            }

            return rep;
        }

        public string GetReportFieldData(string fieldNumber, string caseURN)
        {
            Log.Debug("Retrieving report field data...");

            string Result = string.Empty;

            if (string.IsNullOrWhiteSpace(fieldNumber))
            {
                Log.Error("Missing report field number.");
                return Result;
            }

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing case URN");
                return Result;
            }

            List<string> field = new List<string>();
            field.Add(fieldNumber);

            string URI = String.Format("pathology/case/template/{0}", caseURN);
            IRestResponse response;
            try
            {
                PathologyCaseTemplateInputFieldsType fieldList = new PathologyCaseTemplateInputFieldsType(field);
                string xml = ResponseParser.SerializeToXml<PathologyCaseTemplateInputFieldsType>(fieldList);
                response = ExecutePost(URI, VixServiceTypes.Pathology, xml);
                ValidateRestResponse(response);
                PathologyCaseTemplateType rawRep = ResponseParser.ParseGetReport(response.Content);
                
                if ((rawRep != null) && (rawRep.Fields != null) && (rawRep.Fields.Count > 0))
                    Result = rawRep.Fields[0].Content;
            }
            catch (MagResponseParsingFailureException rpf)
            {
                Log.Error("Failed to parse report field.", rpf);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve report field data.", ex);
            }

            return Result;
        }

        /// <summary>
        /// Retrieve a list of supplementary report for a case
        /// </summary>
        /// <param name="caseURN">Case ID</param>
        /// <returns>list of supplementary reports for the case</returns>
        public SupplementaryReportModel GetSupplementalReports(string caseURN)
        {
            Log.Debug("Retrieving supplementary report...");

            SupplementaryReportModel srModel = new SupplementaryReportModel();
            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return srModel;
            }

            string URI = String.Format("pathology/case/supplementalreports/{0}", caseURN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                SupplementaryReportList supList = ResponseParser.ParseGetSupplementalReportList(response.Content);
                srModel.LoadSupplementReports(supList);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve supplementary reports.", ex);
            }

            return srModel;
        }

        /// <summary>
        /// Save changes to the main report to database
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <param name="changeList">list of changes</param>
        public PathologySaveCaseReportResultType SaveReportChanges(string caseURN, PathologyCaseReportFieldsType changeList)
        {
            Log.Debug("Saving report changes...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                throw new MagVixFailureException("Missing parameter: caseURN.");
            }

            if ((changeList == null) || (changeList.Fields == null))
            {
                throw new MagVixFailureException("Missing parameter: caseURN.");
            }

            string URI = String.Format("pathology/case/report/{0}", caseURN);
            PathologySaveCaseReportResultType result = null;

            IRestResponse response;
            try
            {
                string changes = ResponseParser.SerializeToXml<PathologyCaseReportFieldsType>(changeList);
                response = ExecutePost(URI, VixServiceTypes.Pathology, changes);
                ValidateRestResponse(response);
                result = ResponseParser.ParseSaveReportChanges(response.Content);
                return result;
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Couldn't parse changes.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save changes to the report.", ex);
            }
        }

        /// <summary>
        /// Save supplementary report to VistA
        /// </summary>
        /// <param name="caseURN">Case ID</param>
        /// <param name="datetime">Date time of the supplementary report</param>
        /// <param name="verified">verify the report or not</param>
        /// <param name="data">report content</param>
        public void SaveSupReport(string caseURN, string datetime, bool verified, string data)
        {
            Log.Debug("Saving supplementary report...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                throw new MagVixFailureException("Missing parameter: caseURN.");
            }

            if (string.IsNullOrWhiteSpace(datetime))
            {
                throw new MagVixFailureException("Missing parameter: supplementary report date.");
            }

            if (string.IsNullOrWhiteSpace(data))
            {
                throw new MagVixFailureException("Missing parameter: report data");
            }

            string URI = String.Format("pathology/case/supplementalreport/{0}/{1}/{2}", caseURN, datetime, verified);
            IRestResponse response;
            try
            {
                RestStringType supRep = new RestStringType() { Value = data };
                string changes = ResponseParser.SerializeToXml<RestStringType>(supRep);
                response = ExecutePost(URI, VixServiceTypes.Pathology, changes);
                ValidateRestResponse(response);
            }
            catch (MagResponseParsingFailureException rpf)
            {
                throw new MagVixFailureException("Failed to serialize report data.", rpf);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to save supplementary report.", ex);
            }
        }

        /// <summary>
        /// Update the status for a consultation request
        /// </summary>
        /// <param name="consultationID">ID of the consultation request</param>
        /// <param name="status">new status</param>
        public void UpdateConsultationStatus(string consultationID, string status)
        {
            Log.Debug("Updating consultation status...");

            if (string.IsNullOrWhiteSpace(consultationID))
            {
                throw new MagVixFailureException("Missing parameter: consultation ID.");
            }

            if (string.IsNullOrWhiteSpace(status))
            {
                throw new MagVixFailureException("Missing parameter: status.");
            }

            string URI = String.Format("pathology/case/consultation/{0}/{1}", consultationID, status);
            IRestResponse response;
            try
            {
                response = ExecutePost(URI, VixServiceTypes.Pathology, string.Empty);
                ValidateRestResponse(response);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to update consultation status.", ex);
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
            Log.Debug("Checking e-signature status...");

            PathologyElectronicSignatureNeedType result = new PathologyElectronicSignatureNeedType() { Status = ESigNeedType.error, Message = "Could not retrieve E-signature status." };

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return result;
            }

            if (string.IsNullOrWhiteSpace(reportType))
            {
                Log.Error("Missing parameter: reportType.");
                return result;
            }

            string URI = String.Format("pathology/esigneeded/{0}/{1}", siteID, reportType);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetESignatureStatus(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected result.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to check e-signature status.", ex);
            }
            
            return result;
        }

        /// <summary>
        /// Verify the esignature
        /// </summary>
        /// <param name="siteID">site to be verified at</param>
        /// <param name="eSignature">signature</param>
        /// <returns>true if success</returns>
        public bool VerifyESignature(string siteID, string eSignature)
        {
            Log.Debug("Verifying E-signature...");

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return false;
            }

            if (string.IsNullOrWhiteSpace(eSignature))
            {
                Log.Error("Missing parameter: Esignature.");
                return false;
            }

            string URI = String.Format("verifyElectronicSignature/{0}/{1}", siteID, eSignature);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.User);
                ValidateRestResponse(response);
                return ResponseParser.ParseVerifyESignature(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return false;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to verify esignature.", ex);
                return false;
            }
        }

        /// <summary>
        /// Retrieve a list of snomed codes for the case
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <returns>list of codes</returns>
        public PathologySnomedCodesType GetSnomedCodeForCase(string caseURN)
        {
            Log.Debug("Retrieving SNOMED codes...");

            PathologySnomedCodesType result = new PathologySnomedCodesType();
            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return result;
            }

            string URI = String.Format("pathology/case/snomed/{0}", caseURN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                result = ResponseParser.ParseGetSnomedCodeForCase(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve SNOMED items.", ex);
            }

            return result;
        }

        /// <summary>
        /// Search VistA for items contains a specific string
        /// </summary>
        /// <param name="siteID">site has the data</param>
        /// <param name="fieldType">type of field searching for</param>
        /// <param name="searchParameter">search string</param>
        /// <returns>List of items match the search</returns>
        public PathologyFieldValuesType SearchPathologyItems(string siteID, string fieldType, string searchParameter)
        {
            Log.Debug("Retrieving search query...");

            PathologyFieldValuesType fieldList = new PathologyFieldValuesType();

            if (string.IsNullOrWhiteSpace(siteID))
            {
                Log.Error("Missing parameter: siteID.");
                return fieldList;
            }

            if (string.IsNullOrWhiteSpace(fieldType))
            {
                Log.Error("Missing parameter: fieldType.");
                return fieldList;
            }

            if (string.IsNullOrWhiteSpace(searchParameter))
            {
                Log.Error("Missing parameter: search parameter.");
                return fieldList;
            }
                        
            string URI = String.Format("pathology/fields/{0}/{1}/{2}", siteID, fieldType, searchParameter);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                fieldList = ResponseParser.ParseSearchSnomedItem(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to search.", ex);
            }

            return fieldList;
        }

        /// <summary>
        /// Retrieve a list of cpt codes for a case
        /// </summary>
        /// <param name="caseURN">case id</param>
        /// <returns>list of cpt codes</returns>
        public PathologyCptCodesType GetCptCodesForCase(string caseURN)
        {
            Log.Debug("Retrieving CPT codes...");

            PathologyCptCodesType codeList = null;

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return codeList;
            }

            string URI = String.Format("pathology/case/cpt/{0}", caseURN);
            IRestResponse response;
            try
            {
                response = ExecuteGet(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
                codeList = ResponseParser.ParseGetCptCodesForCase(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to retrieve CPT codes.", ex);
            }

            return codeList;
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
            Log.Debug("Adding new CPT codes...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                throw new MagVixFailureException("Missing parameter: Case URN.");
            }

            if (string.IsNullOrWhiteSpace(locationURN))
            {
                throw new MagVixFailureException("Missing parameter: Ordering Facility.");
            }

            if (cptCodes == null)
            {
                throw new MagVixFailureException("Missing parameter: CPT codes.");
            }

            string URI = String.Format("pathology/case/cpt/{0}/{1}", caseURN, locationURN);
            IRestResponse response;
            try
            {
                RestStringArrayType cptArray = new RestStringArrayType();
                cptArray.Values = cptCodes;
                string body = ResponseParser.SerializeToXml<RestStringArrayType>(cptArray);
                response = ExecutePost(URI, VixServiceTypes.Pathology, body);
                ValidateRestResponse(response);
                PathologyCptCodeResultsType result = ResponseParser.ParseAddCptCodesForCase(response.Content);
                return result;
            }
            catch (MagResponseParsingFailureException rpf)
            {
                if (rpf.ErrorMessage.Contains("Serialization"))
                {
                    throw new MagVixFailureException("Could not parse CPT codes to response parameter.", rpf);
                }
                else
                {
                    throw new MagVixFailureException("Could not parse CPT codes result.", rpf);
                }
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to add new CPT codes.", ex);
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
            Log.Debug("Adding SNOMED item...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(organID))
            {
                Log.Error("Missing parameter: organID.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(fieldURN))
            {
                Log.Error("Missing parameter: fieldURN.");
                return null;
            }

            string URI = String.Format("pathology/case/snomed/{0}/{1}/{2}", caseURN, organID, fieldURN);
            IRestResponse response;
            try
            {
                response = ExecutePost(URI, VixServiceTypes.Pathology, string.Empty);
                ValidateRestResponse(response);
                return ResponseParser.ParseAddSnomedItem(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected reponse.", vfe);
                return null;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to add new SNOMED item.", ex);
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
            Log.Debug("Adding Etiology item...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(organID))
            {
                Log.Error("Missing parameter: organID.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(morphologyID))
            {
                Log.Error("Missing parameter: morphologyID.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(etiologyURN))
            {
                Log.Error("Missing parameter: etiologyURN.");
                return null;
            }

            string URI = String.Format("pathology/case/snomed/{0}/{1}/{2}/{3}", caseURN, organID, morphologyID, etiologyURN);
            IRestResponse response;
            try
            {
                response = ExecutePost(URI, VixServiceTypes.Pathology, string.Empty);
                ValidateRestResponse(response);
                return ResponseParser.ParseAddSnomedItem(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return null;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to add new etiology.", ex);
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
            Log.Error("Adding new organ/tissue...");

            if (string.IsNullOrWhiteSpace(caseURN))
            {
                Log.Error("Missing parameter: caseURN.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(organURN))
            {
                Log.Error("Missing parameter: organURN.");
                return null;
            }

            string URI = String.Format("pathology/case/tissues/{0}/{1}", caseURN, organURN);
            IRestResponse response;
            try
            {
                response = ExecutePost(URI, VixServiceTypes.Pathology, string.Empty);
                ValidateRestResponse(response);
                return ResponseParser.ParseAddSnomedItem(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected Response.", vfe);
                return null;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to add new organ tissue.", ex);
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
            Log.Debug("Removing etiology...");

            if (string.IsNullOrWhiteSpace(caseID))
            {
                throw new MagVixFailureException("Missing parameter: caseID");
            }
            if (string.IsNullOrWhiteSpace(organID))
            {
                throw new MagVixFailureException("Missing parameter: organID");
            }
            if (string.IsNullOrWhiteSpace(snomedID))
            {
                throw new MagVixFailureException("Missing parameter: snomedID");
            }
            if (string.IsNullOrWhiteSpace(etiologyID))
            {
                throw new MagVixFailureException("Missing parameter: etiologyID");
            }

            string URI = String.Format("pathology/case/snomed/etiology/{0}/{1}/{2}/{3}", caseID, organID, snomedID, etiologyID);
            IRestResponse response;
            try
            {
                response = ExecuteDelete(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to delete etiology.", ex);
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
            Log.Debug("Removing SNOMED item...");

            if (string.IsNullOrWhiteSpace(caseID))
            {
                throw new MagVixFailureException("Missing parameter: caseID");
            }
            if (string.IsNullOrWhiteSpace(organID))
            {
                throw new MagVixFailureException("Missing parameter: organID");
            }
            if (string.IsNullOrWhiteSpace(snomedID))
            {
                throw new MagVixFailureException("Missing parameter: snomedID");
            }
            if (string.IsNullOrWhiteSpace(field))
            {
                throw new MagVixFailureException("Missing parameter: field");
            }

            string URI = String.Format("pathology/case/snomed/{0}/{1}/{2}/{3}", caseID, organID, snomedID, field);
            IRestResponse response;
            try
            {
                response = ExecuteDelete(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to delete SNOMED item.", ex);
            }
        }

        /// <summary>
        /// Remove organ tissue from the case
        /// </summary>
        /// <param name="caseID">case id</param>
        /// <param name="organID">organ index</param>
        public void RemoveSnomedOrganTissue(string caseID, string organID)
        {
            Log.Debug("Removing organ/tissue...");

            if (string.IsNullOrWhiteSpace(caseID))
            {
                throw new MagVixFailureException("Missing parameter: caseID");
            }
            if (string.IsNullOrWhiteSpace(organID))
            {
                throw new MagVixFailureException("Missing parameter: organID");
            }

            string URI = String.Format("pathology/case/snomed/{0}/{1}", caseID, organID);
            IRestResponse response;
            try
            {
                response = ExecuteDelete(URI, VixServiceTypes.Pathology);
                ValidateRestResponse(response);
            }
            catch (MagVixFailureException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new MagVixFailureException("Could not complete request to delete organ tissue.", ex);
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
            Log.Debug("Copying case...");
            if (string.IsNullOrWhiteSpace(destinationSiteID))
            {
                Log.Error("Missing parameter: destinationSiteID.");
                return null;
            }

            if (string.IsNullOrWhiteSpace(sourceCaseURN))
            {
                Log.Error("Missing parameter: sourceCaseURN");
                return null;
            }

            string URI = String.Format("pathology/case/copy/{0}/{1}", destinationSiteID, sourceCaseURN);
            IRestResponse response;
            try
            {
                response = ExecutePost(URI, VixServiceTypes.Pathology, string.Empty);
                ValidateRestResponse(response);
                return ResponseParser.ParseCreateCopyCase(response.Content);
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Unexpected response.", vfe);
                return null;
            }
            catch (Exception ex)
            {
                Log.Error("Could not complete request to copy case.", ex);
                return null;
            }
        }

        #endregion
    }
}
