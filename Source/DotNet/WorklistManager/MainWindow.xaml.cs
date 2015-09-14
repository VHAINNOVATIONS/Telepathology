/**
 * 
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: 1/9/2012
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
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Interop;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Reflection;
using GalaSoft.MvvmLight.Threading;
using Microsoft.Practices.ServiceLocation;
using VistA.Imaging.Telepathology.Common.Exceptions;
using VistA.Imaging.Telepathology.Common.Model;
using VistA.Imaging.Telepathology.Common.VixModels;
using VistA.Imaging.Telepathology.Logging;
using VistA.Imaging.Telepathology.Worklist.Controls;
using VistA.Imaging.Telepathology.Worklist.DataSource;
using VistA.Imaging.Telepathology.Worklist.Messages;
using VistA.Imaging.Telepathology.Worklist.ViewModel;
using VistA.Imaging.Telepathology.Worklist.Views;
using System.Runtime.InteropServices;
using System.Net;
using System.Collections.ObjectModel;
using System.IO;

namespace VistA.Imaging.Telepathology.Worklist
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private static MagLogger Log = new MagLogger(typeof(MainWindow));

        private static Boolean ActiveViewer=false;

        public MainWindow()
        {
            string sysInfo = "System Information";
            sysInfo += "|VistA Imaging Pathology Worklist Manager";
            sysInfo += "|Log job started on: " + DateTime.Now.ToString("MM-dd-yyyy HH:mm:ss");
            sysInfo += "|Machine name: " + Environment.MachineName;
            sysInfo += "|Current system user: " + Environment.UserDomainName + "\\" + Environment.UserName;
            sysInfo += "|OS Version: " + Environment.OSVersion.VersionString;
            sysInfo += "|64-bit system: " + Environment.Is64BitOperatingSystem.ToString();
            sysInfo += "|Memory mapped: " + Environment.WorkingSet.ToString();
            sysInfo += "|" + new String('-', 75);
            Log.Info(sysInfo);

            InitializeComponent();
            // register event for access keys
            EventManager.RegisterClassHandler(typeof(UIElement), AccessKeyManager.AccessKeyPressedEvent, new AccessKeyPressedEventHandler(OnAccessKeyPressed));

            // register messages
            AppMessages.ViewConsultationStatusMessage.Register(
                this,
                (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnViewConsultationStatus(action.Item, action.Worklist)));

            AppMessages.EditConsultationStatusMessage.Register(
                this,
                (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnEditConsultationStatus(action)));

            AppMessages.EditReportMessage.Register(
                this,
                (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnEditReport(action.Item, action.Worklist)));

            AppMessages.ViewReportMessage.Register(
                this,
                (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnViewReport(action)));

            AppMessages.ViewSnapshotsMessage.Register(
                this,
                (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnViewSnapshots(action)));

            AppMessages.ActiveWorklistFilterEditMessage.Register(
                    this,
                    (message) => DispatcherHelper.CheckBeginInvokeOnUI(() => message.Execute(this.OnActiveWorklistFilterEdit(message.Data))));

            AppMessages.ViewHealthSummaryListMessage.Register(
                    this,
                    (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnViewHealthSummaryList(action)));

            AppMessages.ViewHealthSummaryMessage.Register(
                    this,
                    (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnViewHealthSummary(action)));

            AppMessages.ViewNotesMessage.Register(
                    this,
                    (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnViewNotes(action)));

            AppMessages.UpdateStatusesMessage.Register(
                    this,
                    (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.OnUpdateStatuses(action.GeneralStatus, action.UnreadTime, action.ReadTime)));
            
            ActiveViewer = false;
        }

        private void OnViewNotes(AppMessages.ViewNotesMessage.MessageData message)
        {
            try
            {
                MainViewModel viewModel = (MainViewModel)DataContext;

                // check to see if the patient is restricted
                bool canProceed = CanUserViewPatientData(message.SiteCode, message.PatientICN);
                if (!canProceed)
                {
                    MessageBox.Show("You cannot view information on this patient.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }

                bool readOnly = viewModel.WorklistsViewModel.CurrentWorkList.Type == ExamListViewType.Patient ? true : false;
                NotesViewModel dialogViewModel = new NotesViewModel(viewModel.DataSource,
                                                                    message.SiteCode,
                                                                    message.PatientICN,
                                                                    message.PatientID,
                                                                    message.AccessionNr,
                                                                    message.CaseURN,
                                                                    readOnly);
                NotesView dialog = new NotesView(dialogViewModel);
                dialog.Owner = (message.Owner != null) ? message.Owner : this;

                ViewModelLocator.ContextManager.IsBusy = true;
                dialog.ShowDialog();
            }
            catch (Exception ex)
            {
                Log.Error("Failed to show notes.", ex);
                MessageBox.Show("Could not display notes for the case.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                ViewModelLocator.ContextManager.IsBusy = false;
            }
        }

        private void OnViewHealthSummary(AppMessages.ViewHealthSummaryMessage.MessageData message)
        {
            try
            {
                MainViewModel viewModel = (MainViewModel)DataContext;

                // check to see if the patient is restricted
                bool canProceed = CanUserViewPatientData(message.SiteCode, message.PatientICN);
                if (!canProceed)
                {
                    MessageBox.Show("You cannot view information on this patient.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }

                HealthSummaryViewModel dialogViewModel = new HealthSummaryViewModel(viewModel.DataSource,
                                                                                    message.SiteName,
                                                                                    message.SiteCode,
                                                                                    message.PatientICN,
                                                                                    message.PatientID,
                                                                                    message.HealthSummaryType);
                HealthSummaryView dialog = new HealthSummaryView(dialogViewModel);
                dialog.Owner = (message.Owner != null) ? message.Owner : this;

                ViewModelLocator.ContextManager.IsBusy = true;
                dialog.ShowDialog();

                Log.Info(string.Format("Viewed {0} health summary.", message.HealthSummaryType.Name));
            }
            catch (Exception ex)
            {
                Log.Error("Failed to show health summary reports.", ex);
                MessageBox.Show("Could not display health summary reports.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                ViewModelLocator.ContextManager.IsBusy = false;
            }
        }

        private void OnViewHealthSummaryList(AppMessages.ViewHealthSummaryListMessage.MessageData message)
        {
            MainViewModel viewModel = (MainViewModel)DataContext;

            // check to see if the patient is restricted
            bool canProceed = CanUserViewPatientData(message.SiteID, message.PatientICN);
            if (!canProceed)
            {
                MessageBox.Show("You cannot view information on this patient.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            HealthSummaryTypeList availableTypes = viewModel.DataSource.GetHealthSummaryTypeList(message.SiteID);

            HealthSummaryListViewModel dialogViewModel = new HealthSummaryListViewModel(availableTypes)
            { 
                PatientID = message.PatientID, 
                PatientName = message.PatientName,
                PatientICN = message.PatientICN,
                SiteName = message.SiteName
            };

            HealthSummaryListView dialog = new HealthSummaryListView(dialogViewModel);
            dialog.Owner = this;
            dialog.ShowDialog();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            MainViewModel viewModel = (MainViewModel)DataContext;
            string logonInfo = "(" + UserContext.ServerName + ") in use by: " + UserContext.UserCredentials.Fullname;
            viewModel.AppTitle += ": " + logonInfo;
            viewModel.SiteAbbr = UserContext.LocalSite.SiteAbbreviation;

            viewModel.RequestClose += () => { Close(); };

            // timer for application timeout. Once the application times out, it will exit discarding changes
            HwndSource osMessageListener = HwndSource.FromHwnd(new WindowInteropHelper(this).Handle);
            osMessageListener.AddHook(new HwndSourceHook(UserActivityCheck));

            // setup intial duration
            ShutdownTimer.Duration = viewModel.DataSource.GetApplicationTimeout(); ;     // minute
            if (ShutdownTimer.Duration > 0)
            {
                ShutdownTimer.DoShutdownEvent += new ShutdownTimer.DoShutdown(ApplicationTimeoutShutdown);
                ShutdownTimer.StartTimer();
            }
        }

        private IntPtr UserActivityCheck(IntPtr hwnd, int msg, IntPtr wParam, IntPtr lParam, ref bool handled)
        {
            //  if the user is still active then reset the timer, add more if needed
            if ((msg >= 0x0200 && msg <= 0x020A) || (msg <= 0x0106 && msg >= 0x00A0) || msg == 0x0021)
            {
                ShutdownTimer.ResetTimer();
            }

            return IntPtr.Zero;
        }

        private void ApplicationTimeoutShutdown()
        {
            ShutdownTimer.StopTimer();

            // user get 1 extra minute countdown
            MagTimeout timeoutWindow = new MagTimeout();
            timeoutWindow.ShowDialog();
            if (timeoutWindow.Terminate)
            {
                // Exit the application
                try
                {
                    // save user preference and close contexts
                    if (UserContext.IsLoginSuccessful)
                    {
                        UserPreferences.Instance.Save();
                    }
                    ViewModelLocator.ContextManager.Close();
                    ViewModelLocator.DataSource.Close();
                }
                catch (Exception ex)
                {
                    Log.Error("Failed to clean up the application.", ex);
                }
                finally
                {
                    // terminate the process
                    Environment.Exit(0);
                }
            }
            else
            {
                // stay connected and reset the clock
                ShutdownTimer.ResetTimer();
            }
        }

        private void ViewSettingsItem_Click(object sender, RoutedEventArgs e)
        {
            SettingsViewModel settingsViewModel = new SettingsViewModel();
            SettingsWindow settingsWindow = new SettingsWindow(settingsViewModel);
            settingsWindow.Owner = this;
            settingsWindow.ShowDialog();
        }

        void OnEditConsultationStatus(AppMessages.EditConsultationStatusMessage.MessageData message)
        {
            MainViewModel viewModel = (MainViewModel)DataContext;

            if (message.CancelConsultationRequest)
            {
                try
                {
                    viewModel.DataSource.UpdateConsultationStatus(message.ConsultationID, "recalled");
                    message.Success = true;

                    Log.Info("Recalled consultation request for case " + message.Item.SiteAbbr + " " + message.Item.AccessionNumber);
                }
                catch (MagVixFailureException vfe)
                {
                    Log.Error("Failed to update consultation status.", vfe);
                    message.Success = false;
                }
            }
            else if (message.RefuseConsultationRequest)
            {
                try
                {
                    viewModel.DataSource.UpdateConsultationStatus(message.ConsultationID, "refused");
                    message.Success = true;

                    Log.Info("Declined consultation request for case " + message.Item.SiteAbbr + " " + message.Item.AccessionNumber);
                }
                catch (MagVixFailureException vfe)
                {
                    Log.Error("Failed to update consultation status.", vfe);
                    message.Success = false;
                }
            }
            else
            {
                message.Success = viewModel.DataSource.RequestConsultation(message.Item.CaseURN, message.ConsultingSite);
                if (message.Success)
                    Log.Info("Requested consultation for case " + message.Item.SiteAbbr + " " + message.Item.AccessionNumber);

            }

            if (!message.Success)
            {
                MessageBox.Show("Couldn't update consultation status.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        /// <summary>
        /// Check to see if the patient's data is sensitive or not and whether or not the user can see it
        /// </summary>
        /// <param name="siteCode">Site holding the data</param>
        /// <param name="patientICN">Patient's unique identification number</param>
        /// <returns>true if the user can view the data and false otherwise</returns>
        private bool CanUserViewPatientData(string siteCode, string patientICN)
        {
            // check for patient sensitive record
            MainViewModel viewModel = (MainViewModel)DataContext;
            
            if (viewModel.DataSource != null)
            {
                PatientSensitiveValueType senLevel = viewModel.DataSource.GetPatientSensitiveLevel(siteCode, patientICN);

                switch (senLevel.SensitiveLevel)
                {
                    case MagSensitiveLevel.DATASOURCE_FAILURE:
                    case MagSensitiveLevel.NO_ACTION_REQUIRED:
                        return true;
                    case MagSensitiveLevel.DISPLAY_WARNING:
                        // display warning
                        RestrictedPatientView resView = new RestrictedPatientView(senLevel.WarningMessage, false);
                        resView.ShowDialog();
                        if (resView.OKClick)
                            return true;
                        else
                            return false;
                    case MagSensitiveLevel.DISPLAY_WARNING_REQUIRE_OK:
                        // display warning require to click ok
                        RestrictedPatientView resViewOK = new RestrictedPatientView(senLevel.WarningMessage, true);
                        resViewOK.ShowDialog();
                        if (resViewOK.OKClick)
                        {
                            // log the access
                            try
                            {
                                viewModel.DataSource.PatientSensitiveAccessLog(siteCode, patientICN);
                                return true;
                            }
                            catch (MagVixFailureException vfe)
                            {
                                Log.Error("Couldn't write sensitive log.", vfe);
                                return false;
                            }
                        }
                        else
                        {
                            return false;
                        }
                    case MagSensitiveLevel.DISPLAY_WARNING_CANNOT_CONTINUE:
                    case MagSensitiveLevel.ACCESS_DENIED:
                        // display warning and cannot continue for 3 and 4
                        RestrictedPatientView resViewNO = new RestrictedPatientView(senLevel.WarningMessage, false);
                        resViewNO.ShowDialog();
                        return false;
                    default:
                        return false;
                }
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// Check to see what kind of site is the current user at
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        private bool IsSiteInterpretingForCase(CaseListItem item)
        {
            // if the user site is the same as the case's site, user can interpretate
            if (UserContext.LocalSite.PrimarySiteStationNUmber == item.SiteCode)
            {
                return true;
            }

            MainViewModel viewModel = (MainViewModel)DataContext;

            // retrieve a list of reading sites that read for the site own the case
            // this way, only sites that are in agreement with the acquisition can access the data 
            ReadingSiteList readSites = viewModel.DataSource.GetReadingSites(item.SiteCode);
            foreach (ReadingSiteInfo readSite in readSites.Items)
            {
                // if the user site is an interpretation site, then they can interpretate
                if ((readSite.SiteStationNumber == UserContext.LocalSite.PrimarySiteStationNUmber) && (readSite.SiteType != ReadingSiteType.consultation))
                {
                    return true;
                }
            }

            return false;
        }

        private ReadingSiteType GetReadingSiteType(CaseListItem item)
        {
            if ((item == null) || (string.IsNullOrWhiteSpace(item.SiteCode)))
                return ReadingSiteType.undefined;

            // if the user's site is the same as the case's site, the user's site is interpretation
            if (UserContext.LocalSite.PrimarySiteStationNUmber == item.SiteCode)
            {
                return ReadingSiteType.interpretation;
            }

            MainViewModel viewModel = (MainViewModel)DataContext;

            // retrieve a list of reading sites that read for the case's site
            // this way, only sites that are in agreement with the acquisition can access the data
            ReadingSiteList readSites = viewModel.DataSource.GetReadingSites(item.SiteCode);
            var readSite = readSites.Items.Where(site => site.SiteStationNumber == UserContext.LocalSite.SiteStationNumber).FirstOrDefault();
            if (readSite == null)
            {
                // current site is not on the agreement list
                return ReadingSiteType.undefined;
            }
            else
            {
                if (readSite.SiteType == ReadingSiteType.consultation)
                    return ReadingSiteType.consultation;
                else if (readSite.SiteType == ReadingSiteType.interpretation)
                    return ReadingSiteType.interpretation;
                else if (readSite.SiteType == ReadingSiteType.both)
                    return ReadingSiteType.interpretation;
                else
                    return ReadingSiteType.undefined;
            }
        }

        void OnEditReport(CaseListItem item, WorklistViewModel worklist)
        {
            try
            {
                MainViewModel viewModel = (MainViewModel)DataContext;

                // see what reading site type
                ReadingSiteType currentSiteType = GetReadingSiteType(item);
                if (currentSiteType == ReadingSiteType.undefined)
                {
                    MessageBox.Show("Current site is undefined from the case's acquisition site's reading list.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }

                // check to see if the patient is restricted
                bool canProceed = CanUserViewPatientData(item.SiteCode, item.PatientICN);
                if (!canProceed)
                {
                    MessageBox.Show("You cannot view information on this patient.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }

                // check to see if the patient is available at the local site
                // if the patient is not registered then always read only report
                bool patientRegistered = viewModel.DataSource.IsPatientRegisteredAtSite(UserContext.LocalSite.PrimarySiteStationNUmber, item.PatientICN);
                if (!patientRegistered)
                {
                    MessageBox.Show("This patient is not registered at this site. Report Editor will open as read only.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                }

                // everything is readonly if it's the patient tab
                bool isPatientTab = viewModel.WorklistsViewModel.CurrentWorkList.Type == ExamListViewType.Patient ? true : false;
                bool isTotalReadOnly = !patientRegistered || isPatientTab;

                // lock the case to prevent other people from accessing the report 
                PathologyCaseUpdateAttributeResultType locked = viewModel.DataSource.LockCaseForEditing(item.CaseURN, true);

                ReportView window;
                if (locked.BoolSuccess)
                {
                    // (Reserve Case now to) Show user in Reserved by column of WL
                    try
                    {
                        viewModel.DataSource.ReserveCase(item.CaseURN, true);
                        Log.Info(string.Format("Reserved case: {0} {1}.", item.SiteAbbr, item.AccessionNumber));
                    }
                    catch (Exception ex)
                    {
                        Log.Error("Cannot reserve case.", ex);
                        MessageBox.Show("Case cannot be reserved.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                    }
                    // refresh WL here to show Locked By on Case
                    viewModel.WorklistsViewModel.CurrentWorkList._refresh();

                    // open the report normally
                    window = new ReportView(new ReportViewModel(viewModel.DataSource, item, isTotalReadOnly, currentSiteType));

                    ViewModelLocator.ContextManager.IsBusy = true;
                    window.ShowDialog();

                    // (UnReserve Case now to) Remove user from Reserved by column of WL
                    try
                    {
                        viewModel.DataSource.ReserveCase(item.CaseURN, false);
                        Log.Info(string.Format("Unreserved case: {0} {1}.", item.SiteAbbr, item.AccessionNumber));
                    }
                    catch (Exception ex)
                    {
                        Log.Error("Cannot unreserve case.", ex);
                        MessageBox.Show("Case cannot be unreserved.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                    }

                    // unlock the case once the user has closed the report GUI
                    locked = viewModel.DataSource.LockCaseForEditing(item.CaseURN, false);
                }
                else
                {
                    // some one else is editing the report
                    string message = String.Format("{0}. Do you want to open it as read only?", locked.ErrorMessage);
                    MessageBoxResult result = MessageBox.Show(message, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                    if (result == MessageBoxResult.Yes)
                    {
                        // open the report as read only
                        window = new ReportView(new ReportViewModel(viewModel.DataSource, item, true, currentSiteType));

                        ViewModelLocator.ContextManager.IsBusy = true;
                        window.ShowDialog();
                    }
                }
            }
            finally
            {
                ViewModelLocator.ContextManager.IsBusy = false;
            }
        }

        // A.    enable any type of certification for web request's servers!!!
        //public bool AcceptAllCertifications(object sender, System.Security.Cryptography.X509Certificates.X509Certificate certification, System.Security.Cryptography.X509Certificates.X509Chain chain, System.Net.Security.SslPolicyErrors sslPolicyErrors)
        //{
        //    return true;
        //}
        
        // C.
        // public void myHttpMethod(Uri uri)
        //{
        //    HttpWebRequest request  = (HttpWebRequest)WebRequest.Create(uri);
        //    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
        //}

        [DllImport("user32.dll")]
        private static extern bool SetForegroundWindow(IntPtr hWnd);

        [DllImport("user32.dll")]
        private static extern bool ShowWindowAsync(IntPtr hWnd, int nCmdShow);

        private const int SW_RESTORE = 9;


        void OnViewSnapshots(CaseListItem item)
        {
            // check to see if the patient is restricted
            bool canProceed = CanUserViewPatientData(item.SiteCode, item.PatientICN);
            if (!canProceed)
            {
                MessageBox.Show("You cannot view information on this patient.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }
            ////**** This try-catch clause is for slide-atlas demo only *****
            //try
            //{
            //    Uri newUri = null;
            //    if (item.IsNoteAttached.Equals("Yes"))
            //        newUri = new Uri("https://slide-atlas.org/webgl-viewer?db=53468ebc0a3ee10dd6b81ca5&view=544f92fbdd98b51542be1f9c");
            //    else
            //        newUri = new Uri("https://slide-atlas.org/webgl-viewer?db=5074589002e31023d4292d83&view=544f92d6dd98b515418f3302");
            //    // A. 
            //    // ServicePointManager.ServerCertificateValidationCallback = new System.Net.Security.RemoteCertificateValidationCallback(AcceptAllCertifications);
            //    // WebRequest webRequest = WebRequest.Create(newUri.ToString());
            //    // WebResponse webResponse = webRequest.GetResponse();
            //    // // webRequest.Timeout = 3500;
 
            //    // B. WebClient webClient = new WebClient(); // /*System.Net.WebClient is other choice
            //    //    webClient.DownloadStringAsync(newUri, string.Empty);

            //    // C. Thread myThread = new Thread(new ThreadStart(myHttpMethod(newUri);
            //    // myThread.Start();
 
            //    // D.
            //    System.Diagnostics.Process.Start(newUri.ToString());

            //}
            //catch (Exception ex)
            //{
            //    Log.Error("Could not start web link.", ex);
            //}


            //**** This is for Vendor system integration getting/displaying (invoking vendor application) list of slides based on HL7 feed from scanner system *****.
            ViewCaseSlides(item);

            /* **** This section is commented out for not using MagImageDisplay *****

            // sync the context and open display to view snapshot images.
            try
            {
                MainViewModel viewModel = (MainViewModel)DataContext;

                Patient curPatient = viewModel.DataSource.GetPatient(item.SiteCode, item.PatientICN);

                // CCOW set patient context
                ViewModelLocator.ContextManager.SetCurrentPatient(curPatient);
                ViewModelLocator.ContextManager.IsBusy = true;

                string progPath = Environment.GetFolderPath(Environment.SpecialFolder.ProgramFilesX86);
                string exePath = @"\VistA\Imaging\MagImageDisplay.exe";

                Process[] processes = Process.GetProcessesByName("MagImageDisplay");
                if ((processes != null) && (processes.Length > 0))
                {
                    // should switch focus to display here
                    IntPtr hwnd = processes[0].MainWindowHandle;
                    ShowWindowAsync(hwnd, SW_RESTORE);
                    SetForegroundWindow(hwnd);
                }
                else
                {
                    // should open new instance of display
                    Process magDisplay = new Process();
                    magDisplay.StartInfo.FileName = progPath + exePath;
                    magDisplay.StartInfo.Arguments = string.Format("s={0} p={1}", UserContext.ServerName, UserContext.ServerPort);
                    magDisplay.Start();
                }

                Log.Info("View snapshots for case " + item.SiteAbbr + " " + item.AccessionNumber);
            }
            catch (Exception ex)
            {
                Log.Error("Could not change patient and launch VI Display.", ex);
                MessageBox.Show("Could not launch VistA Imaging Clinical Display.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                ViewModelLocator.ContextManager.IsBusy = false;
            } */
        }

        // remove slides from viewer application
        public static void EraseCaseSlides()
        {
            try
            {
                // set constants
                string progPath = Environment.GetFolderPath(Environment.SpecialFolder.ProgramFilesX86);
                string fileSpec = progPath + "\\Vista\\Imaging\\Telepathology\\Vendor\\Aperio\\NoSlides.sis";
                string filePathToInvokeExe;

                if (!File.Exists(fileSpec))
                {
                    var assembly = Assembly.GetExecutingAssembly();
                    var resourceStream = assembly.GetManifestResourceStream("VistA.Imaging.Telepathology.Worklist.Vendors.Aperio.NoSlides.sis");

                    if (resourceStream != null)
                    {
                        using (StreamReader reader = new StreamReader(resourceStream))
                        {
                            string contents = reader.ReadToEnd();
                            File.WriteAllText(fileSpec, contents);
                        }
                    }
                    else
                    {
                        throw new Exception("Could not find embedded resource 'NoSlides.sis'");
                    }
                }

                // remove slides from viewer application
                if (!ActiveViewer)
                   return;
                // fileSpec = "C:\\Temp\\NoSlides.sis";
                Log.Info("Removing slides from viewer application.");

                // invoke ImageScope with .sys file
                filePathToInvokeExe = @fileSpec;
                System.Diagnostics.Process.Start(filePathToInvokeExe);

                // ActiveViewer = false; -- viewer stays active if it was invoked by this app!

            }
            catch (Exception ex)
            {
                Log.Error("Could not remove slides from Aperio ImageScope.", ex);
                MessageBox.Show("Could not remove slides from Aperio ImageScope.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        public void ViewCaseSlides(CaseListItem item)
        {

            // This must be called only if Item has slides
            //
            // This try-catch clause is for Aperio system integration getting/displaying (invoking ImageScope.exe) list of slides based on HL7 feed from scanner system.
            // Note: .sys extension must be assigned to ImageScope in Windows Explorer
            try
            {
                // set constants
                string progPath = Environment.GetFolderPath(Environment.SpecialFolder.ProgramFilesX86);
                string fileSpec= progPath + "\\Vista\\Imaging\\Telepathology\\Vendor\\Aperio\\CaseSlides.sis";
                string filePathToInvokeExe;
                string slideTitle = "";

                    // show case slides (if any) in viewer application
                    // fileSpec = "C:\\Temp\\CaseSlides.sis";

                    slideTitle = "    <Title>";
                    slideTitle += item.PatientSensitive ? "<Sensitive Patient>," : (item.PatientName + "," + item.PatientID + ",");
                    slideTitle += item.AccessionNumber + "," + item.StudyDateTime + "; @";

                    // get Slides info
                    MainViewModel viewModel = (MainViewModel)DataContext;
                    CaseSlideList slideList = viewModel.DataSource.GetCaseSlidesInfo(item.CaseURN);
                    if (slideList.Items.Count > 0)
                    {
                        string dotSisTxt = "<SIS>\n  <CloseAllImages />\n";

                        foreach (CaseSlide cSlide in slideList.Items)
                        {
                            // compose one image entry
                            dotSisTxt += "  <Image>\n" + "    <URL>" + cSlide.Url + "</URL>\n";
                            dotSisTxt += slideTitle + cSlide.SlideNumber + " " + cSlide.ZoomFactor + "</Title>\n";
                            dotSisTxt += "    <Authtok>Basic VmlzdEE6VGVsZXBhdGgxMjMh</Authtok>\n  </Image>\n";
                        }
                        dotSisTxt += "</SIS>\n";

                        // (over)write .sis file for ImageScope
                        try
                        {
                            if (File.Exists(fileSpec))
                            {
                                File.Delete(fileSpec);
                            }

                            // Create a file to write to. 
                            using (StreamWriter sw = File.CreateText(fileSpec))
                            {
                                sw.Write(dotSisTxt);
                                sw.Close();
                            }

                        }
                        catch (Exception ex)
                        {
                            Log.Error("Error writing Slide info for case " + item.CaseURN + " of site: " + item.SiteAbbr + ", acc#: " + item.AccessionNumber + " -- " + ex);
                            throw new Exception("Error writing " + fileSpec + " to local storage.");
                        }
                        // invoke ImageScope with .sys file
                        filePathToInvokeExe = @fileSpec;
                        System.Diagnostics.Process.Start(filePathToInvokeExe);

                        Log.Info("Viewing slides for case " + item.CaseURN + " of site: " + item.SiteAbbr + ", acc#: " + item.AccessionNumber);
                        ActiveViewer = true;

                    }

            }
            catch (Exception ex)
            {
                Log.Error("Could not process patient/case data and launch Aperio ImageScope.", ex);
                MessageBox.Show("Could not process case slide data and launch Aperio ImageScope.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        void OnViewReport(CaseListItem item)
        {
            try
            {
                // retrieve the released report from VistA
                MainViewModel viewModel = (MainViewModel)DataContext;

                // check to see if the patient is restricted
                bool canProceed = CanUserViewPatientData(item.SiteCode, item.PatientICN);
                if (!canProceed)
                {
                    MessageBox.Show("You cannot view information on this patient.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }

                string releasedReport = viewModel.DataSource.GetCPRSReport(viewModel.WorklistsViewModel.CurrentWorkList.SelectedItems[0].CaseURN);

                // open new window
                ReleasedCPRSReportView view = new ReleasedCPRSReportView();
                view.Title = item.SiteAbbr + " " + item.AccessionNumber;
                view.ReleasedReport = releasedReport;

                ViewModelLocator.ContextManager.IsBusy = true;
                view.ShowDialog();

                Log.Info("View released report for case " + item.AccessionNumber + " at site " + item.SiteCode);
            }
            catch (Exception ex)
            {
                Log.Error("Unknown exception.", ex);
            }
            finally
            {
                ViewModelLocator.ContextManager.IsBusy = false;
            }
        }

        bool OnActiveWorklistFilterEdit(WorkListFilter filter)
        {
            WorklistFilterEditWindow window = new WorklistFilterEditWindow(new WorklistFilterEditViewModel(filter));
            window.Owner = this;
            window.ShowDialog();
            return (window.DialogResult.HasValue && window.DialogResult.Value);
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            if (mnuSavePref.IsChecked)
            {
                UserPreferences.Instance.LayoutPreferences.SaveOnExit = mnuSavePref.IsChecked;

                // save window state
                UserPreferences.Instance.LayoutPreferences.Save(this, "MainWindow");

                // save worklist layout preferences
                this.examGroupsView.SaveLayoutPreferences();
            }
        }

        public void ApplyUserPreferences()
        {
            // restore window state
            UserPreferences.Instance.LayoutPreferences.Restore(this, "MainWindow");

            // apply worklist layout preferences
            this.examGroupsView.ApplyLayoutPreferences();

            mnuSavePref.IsChecked = UserPreferences.Instance.LayoutPreferences.SaveOnExit;
        }

        private void About_Click(object sender, RoutedEventArgs e)
        {
            AboutWindow about = new AboutWindow();
            about.ShowDialog();
        }

        void OnViewConsultationStatus(CaseListItem item, WorklistViewModel worklist)
        {
            MainViewModel viewModel = (MainViewModel)DataContext;

            // check to see if the case already have an interpretation entry,
            // if there is no entry, create one
            if ((item.ConsultationList == null) || (item.ConsultationList.ConsultationList == null)
                || (item.ConsultationList.ConsultationList.Count == 0))
            {
                if (!viewModel.DataSource.RequestInterpretation(item.CaseURN, UserContext.LocalSite.PrimarySiteStationNUmber))
                {
                    MessageBox.Show("Case doesn't have an interpretation entry. Cannot proceed.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }
            }

            // retrieve a list of reading sites that read for the site on the case with Consultation type
            // this way, only sites that are in agreement with the acquisition can access the data 
            ReadingSiteList readSites = viewModel.DataSource.GetReadingSites(item.SiteCode);

            //foreach (ReadingSiteInfo readSite in readSites.Items)
            //{
            //    // if the user site is a consultation site, the user can't request more consultation on remote item,
            //    // however he/she must be able to refuse one that was just requested (report status id "Pending Verification") 
            //    if ((readSite.SiteStationNumber == UserContext.LocalSite.PrimarySiteStationNUmber) && (readSite.SiteType == ReadingSiteType.consultation))
            //    {
            //        MessageBox.Show("Your site is configured as a consultation site at " + item.AcquisitionSite + ".\r\nOnly primary interpreting site can request consultations.");

            //        if (item.ReportStatus == "Pending Verification")
            //        {
            //            MessageBoxResult result = MessageBox.Show("From site " + item.AcquisitionSite + " for Case <" + item.AccessionNumber +
            //                            "> Consultation is requested.\r\nDo you want to Decline this request?", "Choice", MessageBoxButton.YesNo, MessageBoxImage.Information, MessageBoxResult.No /* default to No */);
            //            if (result == MessageBoxResult.Yes)
            //            {
            //                // update Consult status to refused!
            //                ConsultationStatusViewModel consultSVM = new ConsultationStatusViewModel(item, readSites);
            //                consultSVM.SelectedSites = new ObservableCollection<SiteConsultationStatusViewModel>();
            //                SiteConsultationStatusViewModel itemVM = new SiteConsultationStatusViewModel();
            //                itemVM.Item = item;
            //                itemVM.SiteInfo = readSite; // ReadingSiteInfo
            //                itemVM.IsCurrentSite = false;
            //                itemVM.CanRefuseConsultation = true;
            //                itemVM.CanRequestConsultation = false;
            //                itemVM.IsRecalled = false;
            //                // Consultations are returned from originating DB if we got here
            //                int lastConsultIndex = item.ConsultationList.ConsultationList.Count;
            //                itemVM.ConsultationID = item.ConsultationList.ConsultationList[lastConsultIndex-1].ConsultationID;
            //                itemVM.IsPending = true;
            //                consultSVM.SelectedSites.Add(itemVM);

            //                consultSVM.RefuseConsultation();
            //                return;
            //            }
            //        }
            //        return;
            //    }
            //}

            // attempt lock the case to prevent other people from accessing the report 
            PathologyCaseUpdateAttributeResultType locked = viewModel.DataSource.LockCaseForEditing(item.CaseURN, true);

            if (locked.BoolSuccess)
            {
                // (Reserve Case now to) Show user in Locked By column of WL
               try
               {
                    viewModel.DataSource.ReserveCase(item.CaseURN, true);
                    Log.Info(string.Format("Reserved case: {0} {1}.", item.SiteAbbr, item.AccessionNumber));
               }
               catch (Exception ex)
               {
                    Log.Error("Cannot reserve case.", ex);
                    MessageBox.Show("Case cannot be reserved.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
               }
               // refresh WL here to show Locked By on Case
               viewModel.WorklistsViewModel.CurrentWorkList._refresh();

               ConsultationStatusViewModel dialogViewModel = new ConsultationStatusViewModel(item, readSites);
               ConsultationStatusView dialog = new ConsultationStatusView(dialogViewModel);
               dialog.Owner = this;
               dialog.ShowDialog();

               // (UnReserve Case now to) Remove user from Locked By column of WL
               try
               {
                    viewModel.DataSource.ReserveCase(item.CaseURN, false);
                    Log.Info(string.Format("Unreserved case: {0} {1}.", item.SiteAbbr, item.AccessionNumber));
               }
               catch (Exception ex)
               {
                    Log.Error("Cannot unreserve case.", ex);
                    MessageBox.Show("Case cannot be unreserved.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
               }

               // unlock the case once the user has closed the report GUI
               locked = viewModel.DataSource.LockCaseForEditing(item.CaseURN, false);
            }
            else
            {
               // some one else is locking the case
                MessageBox.Show("Case is locked by another user. Please, try it later!", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
            }
        }

        private void MessageLog_Click(object sender, RoutedEventArgs e)
        {
            // only check for loging right now but should also check for user keys next
            MessageLog logViewer = new MessageLog(UserContext.IsLoginSuccessful && UserContext.UserHasKey("MAG SYSTEM"));
            logViewer.ShowDialog();
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            Log.Info("Terminating VistA Imaging Pathology Worklist Manager.");
            // // empty viewer app context if it was invoked...
            // EraseCaseSlides();
            if (ActiveViewer)
            {
                // kill ImageScope
                foreach (Process Proc in Process.GetProcesses())
                    if (Proc.ProcessName.Equals("ImageScope"))
                        Proc.Kill();
            }
        }

        private void mnuSavePrefNow_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                // save user preference right away
                if (UserContext.IsLoginSuccessful)
                {
                    UserPreferences.Instance.LayoutPreferences.SaveOnExit = mnuSavePref.IsChecked;

                    // save window state
                    UserPreferences.Instance.LayoutPreferences.Save(this, "MainWindow");

                    // save worklist layout preferences
                    this.examGroupsView.SaveLayoutPreferences();
                }
            }
            catch (Exception ex)
            {
                Log.Error("Failed to save user preferences.", ex);
                MessageBox.Show("Failed to save your preferences.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void OnUpdateStatuses(string generalStatus = null, string unreadTime = null, string readTime = null)
        {
            MainViewModel viewModel = (MainViewModel)DataContext;

            if (generalStatus != null)
                viewModel.GeneralStatus = generalStatus;
            if (unreadTime != null)
                viewModel.UnreadListRefreshTime = unreadTime;
            if (readTime != null)
                viewModel.ReadListRefreshTime = readTime;
        }

        // custom event to work around .NET bug to prevent access key fireup without ALT key pressed
        private static void OnAccessKeyPressed(object sender, AccessKeyPressedEventArgs e)
        {
            // allowing default for enter and escape keys
            if (Keyboard.IsKeyDown(Key.Enter) || Keyboard.IsKeyDown(Key.Escape))
                return;

            if (!e.Handled && e.Scope == null && (e.Target == null || e.Target is Label))
            {
                // If Alt key is not pressed - handle the event
                if ((Keyboard.Modifiers & ModifierKeys.Alt) != ModifierKeys.Alt)
                {
                    e.Target = null;
                    e.Handled = true;
                }
            }
        }
    }
}
