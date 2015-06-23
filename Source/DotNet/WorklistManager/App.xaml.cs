/**
 * 
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: 1/30/2012
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * Developer:  Paul Pentapaty
 * Description: 
 * 
 *       ;; +--------------------------------------------------------------------+
 *       ;; Property of the US Government.
 *       ;; No permission to copy or redistribute this software is given.
 *       ;; Use of unreleased versions of this software requires the user
 *       ;;  to execute a written test agreement with the VistA Imaging
 *       ;;  Development Office of the Department of Veterans Affairs,
 *       ;;  telephone (301) 734-0100.
 *       ;;
 *       ;; The Food and Drug Administration classifies this software as
 *       ;; a Class II medical device.  As such, it may not be changed
 *       ;; in any way.  Modifications to this software may result in an
 *       ;; adulterated medical device under 21CFR820, the use of which
 *       ;; is considered to be a violation of US Federal Statutes.
 *       ;; +--------------------------------------------------------------------+
 *       
 * 
 */

using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;
using GalaSoft.MvvmLight.Threading;
using VistA.Imaging.Telepathology.Worklist.Views;
using VistA.Imaging.Telepathology.Worklist.ViewModel;
using VistA.Imaging.Telepathology.Worklist.Messages;
using VistA.Imaging.Telepathology.Common.Model;
using VistA.Imaging.Telepathology.Worklist.DataSource;
using VistA.Imaging.Telepathology.CCOW;
using System.Diagnostics;
using VistA.Imaging.Telepathology.Logging;
using System.Windows.Threading;
using VistA.Imaging.Telepathology.Common.Exceptions;

namespace VistA.Imaging.Telepathology.Worklist
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        private static MagLogger Log = new MagLogger(typeof(App));

        public App()
        {
            DispatcherHelper.Initialize();
        }

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

#if (!DEBUG)
            AppDomain.CurrentDomain.UnhandledException += AppDomainUnhandledException;
#endif

            try
            {
                // initialize logging
                MagLogger.Initialize(new System.IO.FileInfo(AppDomain.CurrentDomain.SetupInformation.ConfigurationFile));

                AppMessages.ApplicationLogoutMessage.Register(
                    this,
                    (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnAppMessage(action)));

                // create main window so that app does shutdown
                MainWindow mainWindow = new MainWindow();

                IWorkListDataSource datasource = ViewModelLocator.DataSource;

                Log.Info("Logging in to VistA...");
                datasource.InitializeConnection();

                //datasource.GetMagSecurityKeys();

                // check for LRLAB key and MAG SYSTEM
                bool hasMagSys = UserContext.UserHasKey("MAG SYSTEM");
                bool hasLRLAB = UserContext.UserHasKey("LRLAB");
                if ((!hasMagSys) && (!hasLRLAB))
                {
                    MessageBox.Show("You are not authorized to use this application. The apllication will be terminated.",
                                    "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    ViewModelLocator.DataSource.Close();
                    Close();
                    Log.Debug("User doesn't have LRLAB and MAG SYSTEM keys. Application terminated.");
                    Environment.Exit(0);
                }

                if (!hasLRLAB)
                {
                    MessageBox.Show("You are not allowed to perform reading activities on a case.",
                                    "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    Log.Debug("User doesn't have LRLAB key. Application will continue with limited access.");
                    Log.Info("The application will continue with limited access.");
                }
                
                // initialize user preferences
                UserPreferences.Instance.Initialize();

                AppMessages.ApplicationInitializedMessage.Send();

                // join CCOW Context
                InitializeCCOW();

                // show main window
                mainWindow.Show();
                mainWindow.ApplyUserPreferences();
                mainWindow.Activate();

            }
            catch (Exception ex)
            {
                string message = "The application cannot be initialized and will be closed now.";
                MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                Log.Error(message, ex);
                // clear out all login, user preferences before closing
                ViewModelLocator.DataSource.Close();
                Close();
                HandleException(ex);
            }
        }


        void InitializeCCOW()
        {
            IContextManager contextManager = ViewModelLocator.ContextManager;

            IWorkListDataSource datasource = ViewModelLocator.DataSource;

            contextManager.Contextor = datasource.Contextor;

            contextManager.Run();
        }

        private static void AppDomainUnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            HandleException(e.ExceptionObject as Exception);
        }

        private static void HandleException(Exception ex)
        {
            if (ex == null)
                return;

            Log.Error("Exception error, application will exit.", ex);
            Environment.Exit(1);
        }

        private void Application_Exit(object sender, ExitEventArgs e)
        {
            Close();
        }

        private void Close()
        {
            if (UserContext.IsLoginSuccessful)
            {
                UserPreferences.Instance.Save();
            }

            ViewModelLocator.ContextManager.Close();
            ViewModelLocator.DataSource.Close();
        }

        private void OnAppMessage(AppMessages.MessageTypes message)
        {
            Logout();
        }

        private void Logout()
        {
            // shutdown current instance
            Application.Current.Shutdown();

            // launch a new instance
            try
            {
                Process process = new Process();

                process.StartInfo.UseShellExecute = true;
                process.StartInfo.RedirectStandardOutput = false;
                process.StartInfo.RedirectStandardError = false;
                process.StartInfo.FileName = System.Reflection.Assembly.GetEntryAssembly().Location;

                process.Start();
            }
            catch (Exception ex)
            {
                Log.Error("Failed to launch new instance of the application.", ex);
            }
        }
    }
}
