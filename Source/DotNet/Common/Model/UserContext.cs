// -----------------------------------------------------------------------
// <copyright file="UserContext.cs" company="Department of Veterans Affairs">
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
    using System.Collections.Generic;

    /// <summary>
    /// TODO: Update summary.
    /// </summary>
    public static class UserContext
    {
        static UserContext()
        {
            //UserContext._allSites = new List<Site>();
            UserContext.LocalSite = new Site();
            UserContext.AcquisitionList = new AcquisitionSiteList();
            UserContext.ReportLockDurations = new Dictionary<string, int>();
        }

        public static string ServerName { get; set; }

        public static string ServerPort { get; set; }

        public static bool IsLoginSuccessful { get; set; }

        public static String LoginErrorMessage { get; set; }
        
        public static UserCredentials UserCredentials { get; set; }

        public static bool UserHasKey(string key)
        {
            if (UserCredentials == null)
                return false;

            return UserCredentials.UserHasKey(key);
        }

        public static bool UserHasKey(string siteID, string key)
        {
            if (UserCredentials == null)
                return false;

            return UserCredentials.UserHasKey(siteID, key);
        }

        public static string ApplicationContext { get; set; }

        public static string SiteServiceUrl { get; set; }

        public static Site LocalSite { get; set; }

        public static AcquisitionSiteList AcquisitionList { get; set; }

        public static Dictionary<string, int> ReportLockDurations { get; set; }

        /// <summary>
        /// Adding new acquisition site to retrieve the worklist from
        /// </summary>
        /// <param name="site">site to retrieve the worklist from</param>
        public static void AddAcquisitionSite(AcquisitionSiteInfo site)
        {
            UserContext.AcquisitionList.Items.Add(site);
        }

        /// <summary>
        /// Reset the user context to condition prior to aunthentication
        /// </summary>
        public static void ResetUserContext()
        {
            ServerName = string.Empty;
            ServerPort = string.Empty;
            IsLoginSuccessful = false;
            LoginErrorMessage = string.Empty;
            UserCredentials = new UserCredentials();
            ApplicationContext = string.Empty;
            SiteServiceUrl = string.Empty;
            LocalSite = new Site();
            AcquisitionList = new AcquisitionSiteList();
            ReportLockDurations.Clear();
        }
    }
}
