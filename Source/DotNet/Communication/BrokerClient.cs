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
    using System.Configuration;
    using System.Text.RegularExpressions;
    using VistA.Imaging.Telepathology.CCOWRPCBroker;
    using VistA.Imaging.Telepathology.Common.Exceptions;
    using VistA.Imaging.Telepathology.Common.Model;
    using VistA.Imaging.Telepathology.Logging;

    public class BrokerClient
    {
        private static MagLogger Log = new MagLogger(typeof(BrokerClient));

        private Client client = new Client();

        public BrokerClient()
        {
        }

        public VERGENCECONTEXTORLib.Contextor Contextor
        {
            get
            {
                if ((client != null) && (client.CurrentConnection != null) && (client.CurrentConnection.Contextor != null))
                {
                    return (VERGENCECONTEXTORLib.Contextor)client.CurrentConnection.Contextor;
                }

                return  null;
            }
        }

        public bool AuthenticateUser()
        {
            Log.Info("Authenticating...");

            // Put an initial version of the UserCredential object in context.
            // It will be replaced with the full version upon successful login
            UserContext.UserCredentials = new UserCredentials();
            UserContext.IsLoginSuccessful = false;

            client.ApplicationLabel = ConfigurationManager.AppSettings["ApplicationLabel"];
            client.PassCode = ConfigurationManager.AppSettings["Passcode"];

            if (client.Connect() != null)
            {
                UserContext.IsLoginSuccessful = true;
                UserContext.UserCredentials.Fullname = client.CurrentConnection.UserName;
                UserContext.UserCredentials.Duz = client.CurrentConnection.UserDUZ;
                UserContext.UserCredentials.SiteName = client.CurrentConnection.SiteName;
                UserContext.UserCredentials.SiteNumber = client.CurrentConnection.Division;
                UserContext.ServerName = ServerName;
                UserContext.ServerPort = ServerPort.ToString();

                Log.Info(UserContext.UserCredentials.Fullname + " access is verified.");
                Log.Info("Connected to: " + ServerName);
                return true;
            }

            return false;
        }

        public void Close()
        {
            if (this.client != null)
            {
                this.client.Close();
                UserContext.ResetUserContext();
            }
        }

        public void UpdateSecurityToken()
        {
            if (client.CurrentConnection != null)
            {
                Log.Debug("Calling RPC: MAG BROKER SECURITY.");
                
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAG BROKER SECURITY" });

                if ((response == null) || (response.RawData == null))
                {
                    string message = "Failed to establish broker security. " +
                                     "ERR: Receive no response from VistA: response = null.";
                    throw new MagBrokerFailureException(message);
                }
                else
                {
                    UserContext.UserCredentials.SecurityToken =
                        string.Format("{0}^{1}^{2}^{3}",
                                      "TELEPATHOLOGY WORKLIST",
                                      response.RawData,
                                      client.CurrentConnection.SiteNumber,
                                      client.CurrentConnection.Port);
                }
            }
            else
            {
                throw new MagBrokerFailureException("Failed to establish broker security. ERR: There is no current connection.");
            }
        }

        public void SetApplicationContext()
        {
            if (client.CurrentConnection != null)
            {
                Log.Debug("Calling RPC: XWB CREATE CONTEXT.");
                
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "XWB CREATE CONTEXT" }.AddParameter("'#0zg+q>@{S5,Ngq#z{0"));
                if ((response == null) || (response.RawData == null))
                {
                    string message = "Failed to create application context. " +
                                     "ERR: Received no response from VistA: response = null.";
                    throw new MagBrokerFailureException(message);
                }
                else
                {
                    if (response.RawData != "1")
                    {
                        throw new MagBrokerFailureException("Failed to create application context. ERR: " + response.RawData);
                    }
                }
            }
            else
            {
                throw new MagBrokerFailureException("Failed to create application context. ERR: There is no current connection.");
            }
        }

        public void GetPatientDetails(Patient patient)
        {
            if (client.CurrentConnection != null)
            {
                Log.Debug("Calling RPC: VAFCTFU CONVERT ICN TO DFN.");

                Response response = client.CurrentConnection.Execute(new Request { MethodName = "VAFCTFU CONVERT ICN TO DFN" }.AddParameter(patient.PatientICN));
                if (response != null)
                {
                    patient.LocalDFN = response.RawData;
                }
            }
        }

        List<string> GetPreferences(string label)
        {
            if (client.CurrentConnection != null)
            {
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAGTP GET PREFERENCES" }.AddParameter(label));
                if (response != null)
                {
                    //return response.RawData;
                }
            }

            return null;
        }

        void SetPreferences(string siteID, string label, List<string> values)
        {
            if (client.CurrentConnection != null)
            {
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAGTP PUT PREFERENCES" }.AddParameter(label).AddParameter(values));
                if (response != null)
                {
                    //return response.RawData;
                }
            }
        }

        public void GetMagSecurityKeys()
        {
            if (client.CurrentConnection != null)
            {
                Log.Debug("Calling RPC: MAGGUSERKEYS.");
                try
                {
                    Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAGGUSERKEYS" });
                    if ((response == null) || (response.RawData == null))
                    {
                        string message = "Failed to retrieve user security keys. " +
                                     "ERR: Receive no response from VistA: response = null.";
                        throw new MagBrokerFailureException(message);
                    }
                    else
                    {
                        Log.Debug(string.Format("MAG Keys:" + Environment.NewLine + "{0}", response.RawData));
                        if (!string.IsNullOrWhiteSpace(response.RawData))
                        {
                            string[] keys = Regex.Split(response.RawData, Environment.NewLine);
                            if (keys != null)
                            {
                                if (UserContext.UserCredentials.SecurityKeys == null)
                                {
                                    UserContext.UserCredentials.SecurityKeys = new List<string>();
                                }

                                foreach (string key in keys)
                                {
                                    UserContext.UserCredentials.SecurityKeys.Add(key);
                                }
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    Log.Error("Failed to retrieve user security keys.", ex);
                }
            }
        }

        public string ServerName
        {
            get
            {
                return (client.CurrentConnection != null) ? client.CurrentConnection.Server : "Error";
            }
        }

        public int ServerPort
        {
            get
            {
                return (client.CurrentConnection != null) ? client.CurrentConnection.Port : 0;
            }
        }

        /// <summary>
        /// Initializes the user context at the local site
        /// </summary>
        public void InitializeUserContext()
        {
            if (client.CurrentConnection != null)
            {
                Log.Debug("Calling RPC: MAGTP USER.");
                
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAGTP USER" });
                if ((response == null) || (response.RawData == null))
                {
                    string message = "Failed to initialize user context. " +
                                     "ERR: Receive no response from VistA: response = null.";
                    throw new MagBrokerFailureException(message);
                }
                else
                {
                    ResponseParser.ParseGetUserResponse(response.RawData);
                }
            }
            else
            {
                throw new MagBrokerFailureException("Failed to initialize user context. ERR: There is no current connection.");
            }
        }

        public int GetRetentionDays()
        {
            Log.Debug("Calling RPC: MAGTP GET RETENTION DAYS");

            if (client.CurrentConnection != null)
            {
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAGTP GET RETENTION DAYS" }.AddParameter(UserContext.LocalSite.SiteStationNumber));
                if ((response == null) || (response.RawData == null))
                {
                    Log.Error("Received no reponse.");
                    return -1;
                }
                else
                {
                    int result = ResponseParser.ParseGetRetentionDays(response.RawData);
                    return result;
                }
            }
            else
            {
                Log.Error("There is no connection.");
                return 0;
            }
        }

        public void SetRetentionDays(int days)
        {
            Log.Debug("Calling RPC: MAGTP SET RETENTION DAYS");

            if (client.CurrentConnection != null)
            {
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAGTP SET RETENTION DAYS" }.AddParameter(days.ToString()).AddParameter(UserContext.LocalSite.SiteStationNumber));
                if ((response == null) || (response.RawData == null))
                {
                    throw new MagBrokerFailureException("Receive no response from Vista: response = null");
                }
                else
                {
                    if (response.RawData == "0")
                        Log.Error("Failed to set retention days.");
                }
            }
            else
            {
                throw new MagBrokerFailureException("There is no connection.");
            }
        }

        public int GetApplicationTimeout()
        {
            Log.Debug("Calling RPC: MAGG GET TIMEOUT.");

            if (client.CurrentConnection != null)
            {
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAGG GET TIMEOUT" }.AddParameter("TELEPATHOLOGY"));
                if ((response == null) || (response.RawData == null))
                {
                    Log.Error("Received no reponse.");
                    return -1; 
                }
                else
                {
                    int result = ResponseParser.ParseGetApplicationTimeout(response.RawData);
                    return result;
                }
            }
            else
            {
                Log.Error("There is no connection.");
                return 0;
            }
        }

        public void SetApplicationTimeout(int duration)
        {
            Log.Debug("Calling RPC: MAG3 SET TIMEOUT.");

            if (client.CurrentConnection != null)
            {
                Response response = client.CurrentConnection.Execute(new Request { MethodName = "MAG3 SET TIMEOUT" }.AddParameter("TELEPATHOLOGY").AddParameter(duration.ToString()));
                if ((response == null) || (response.RawData == null))
                {
                    throw new MagBrokerFailureException("Receive no response from Vista: response = null");
                }
                else
                {
                    bool result = ResponseParser.ParseSetApplicationTimeout(response.RawData);
                    if (!result)
                    {
                        throw new MagBrokerFailureException("Failed to set application timeout.");
                    }
                }
            }
            else
            {
                throw new MagBrokerFailureException("There is no connection.");
            }
        }
    }
}
