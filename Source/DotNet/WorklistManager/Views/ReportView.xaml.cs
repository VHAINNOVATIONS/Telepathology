using System;
using System.Windows;
using System.Windows.Interop;
using VistA.Imaging.Telepathology.Common.Exceptions;
using VistA.Imaging.Telepathology.Common.Model;
using VistA.Imaging.Telepathology.Logging;
using VistA.Imaging.Telepathology.Worklist.Controls;
using VistA.Imaging.Telepathology.Worklist.ViewModel;
using System.Windows.Input;
using System.Windows.Controls;
using System.Windows.Threading;

namespace VistA.Imaging.Telepathology.Worklist.Views
{
    /// <summary>
    /// Description for ReportView.
    /// </summary>
    public partial class ReportView : Window
    {
        private static MagLogger Log = new MagLogger(typeof(ReportView));

        private DispatcherTimer renewalTimer = new DispatcherTimer();

        /// <summary>
        /// Initializes a new instance of the ReportView class.
        /// </summary>
        public ReportView()
        {
            InitializeComponent();
            SetRenewalTimer();
        }

        public ReportView(ReportViewModel viewModel)
        {
            InitializeComponent();
            DataContext = viewModel;
            
            SupplementaryReportView srView = new SupplementaryReportView(viewModel.SRViewModel);
            tabSR.Content = srView;

            SetRenewalTimer();
        }

        private void SetRenewalTimer()
        {
            ReportViewModel viewModel = DataContext as ReportViewModel;
            if (viewModel == null)
                return;

            // set the timer to automatically renew the clock
            int minLock;
            if (UserContext.ReportLockDurations.ContainsKey(viewModel.SiteCode))
            {
                // if the time is already retrieved then use it
                minLock = UserContext.ReportLockDurations[viewModel.SiteCode];
            }
            else
            {
                // if the time is not retrieved then fetch it
                string min = viewModel.DataSource.GetReportLockTimeoutHour(viewModel.SiteCode);
                try
                {
                    minLock = Convert.ToInt32(min);
                }
                catch (Exception)
                {
                    // default if errors
                    minLock = 30;
                }

                UserContext.ReportLockDurations.Add(viewModel.SiteCode, minLock);
            }

            if (minLock == 1)
                minLock++;

            // set timer property
            renewalTimer.Tick += new EventHandler(renewalTimer_Tick);
            renewalTimer.Interval = new TimeSpan(0, minLock - 1, 0);    // renew when 1 min left
            renewalTimer.Start();
        }

        private void renewalTimer_Tick(object sender, EventArgs e)
        {
            ReportViewModel viewModel = DataContext as ReportViewModel;
            if (viewModel == null)
                return;

            renewalTimer.Stop();

            EditReportTimeout timeoutWindow = new EditReportTimeout();
            timeoutWindow.ShowDialog();
            if (timeoutWindow.Terminate)
            {
                if (viewModel.IsModified)
                {
                    try
                    {
                        viewModel.SaveMainReportToDatabase();
                    }
                    catch (Exception)
                    {
                        //Log.Error("Failed to save changes to the report -- on timeout.", vfe);
                        MessageBox.Show("Failed to save the main report data to database -- on timeout.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                    }
                }

                Close();
            }
            else // stay in Report editor
            {
                // renew the lock timer
                viewModel.DataSource.LockCaseForEditing(viewModel.CaseURN, false);
                viewModel.DataSource.LockCaseForEditing(viewModel.CaseURN, true);
                renewalTimer.Start();
            }
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            // check if the report has been modified or not
            if ((DataContext as ReportViewModel).IsModified)
            {
                MessageBoxResult result = MessageBox.Show("There are unsaved changes to the report.\nDo you want to save them and close the report?",
                                                          "Confirmation",
                                                          MessageBoxButton.YesNoCancel, MessageBoxImage.Question, MessageBoxResult.Cancel);
                if (result == MessageBoxResult.Yes)             // save report to database
                {
                    try
                    {
                        (DataContext as ReportViewModel).SaveMainReportToDatabase();
                    }
                    catch (Exception)
                    {
                        //Log.Error("Failed to save changes to the report.", vfe);
                        MessageBox.Show("Failed to save the main report data to database.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                    }
                }
                else if (result == MessageBoxResult.No)         // discard everything
                { }
                else                                            // cancel and stay on the form
                {
                    e.Cancel = true; ;
                }
            }
        }

        private void btnClose_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            // timer for application timeout. Once the application times out, it will exit discarding changes
            HwndSource osMessageListener = HwndSource.FromHwnd(new WindowInteropHelper(this).Handle);
            osMessageListener.AddHook(new HwndSourceHook(UserActivityCheck));
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
    }
}