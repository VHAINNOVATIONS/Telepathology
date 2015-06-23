// -----------------------------------------------------------------------
// <copyright file="App.xaml.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: April 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Duc Nguyen
//  Description: Configurator Application settings
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

namespace VistA.Imaging.Telepathology.Configurator
{
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Data;
    using System.Linq;
    using System.Windows;
    using VistA.Imaging.Telepathology.Common.Model;
    using VistA.Imaging.Telepathology.Configurator.DataSource;
    using VistA.Imaging.Telepathology.Logging;
    using System.Threading;

    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        private static MagLogger Log = new MagLogger(typeof(App));

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            try
            {
                // initialize logging
                MagLogger.Initialize(new System.IO.FileInfo(AppDomain.CurrentDomain.SetupInformation.ConfigurationFile));

                // create main window so that app does shutdown
                MainWindow mainWindow = new MainWindow();

                IConfiguratorDatasource datasource = new ConfiguratorDatasource();
                mainWindow.Datasource = datasource;
                mainWindow.Login();
                // show main window first
                mainWindow.Show();
                mainWindow.Activate();
            }
            catch (Exception ex)
            {
                Log.Error("Unknown Error.", ex);
            }
        }
    }
}
