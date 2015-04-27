using System;
using System.IO;
using System.Windows;
using System.Windows.Shapes;
using VistA.Imaging.Telepathology.Common.Controls;
using VistA.Imaging.Telepathology.Common.Exceptions;
using VistA.Imaging.Telepathology.Common.Model;
using VistA.Imaging.Telepathology.Logging;
using VistA.Imaging.Telepathology.Configurator.DataSource;
using VistA.Imaging.Telepathology.Configurator.ViewModels;
using VistA.Imaging.Telepathology.Configurator.Views;
using System.Windows.Input;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;

namespace VistA.Imaging.Telepathology.Configurator
{
	public partial class MainWindow : Window
	{
		private static MagLogger Log = new MagLogger(typeof(MainWindow));

		private bool firstRendered;

		public MainWindow()
		{
            string sysInfo = "System Information";
            sysInfo += "|VistA Imaging Telepathology Configurator";
            sysInfo += "|Log job started on: " + DateTime.Now.ToString("MM-dd-yyyy HH:mm:ss");
            sysInfo += "|Machine name: " + Environment.MachineName;
            sysInfo += "|Current system user: " + Environment.UserDomainName + "\\" + Environment.UserName;
            sysInfo += "|OS Version: " + Environment.OSVersion.VersionString;
            sysInfo += "|64-bit system: " + Environment.Is64BitOperatingSystem.ToString();
            sysInfo += "|Memory mapped: " + Environment.WorkingSet.ToString();
			sysInfo += "|" + new String('-', 75);
            Log.Info(sysInfo);

			firstRendered = true;
			InitializeComponent();

            // register event for access keys
            EventManager.RegisterClassHandler(typeof(UIElement), AccessKeyManager.AccessKeyPressedEvent, new AccessKeyPressedEventHandler(OnAccessKeyPressed));
		}

		private void Window_Loaded(object sender, RoutedEventArgs e)
		{
			// update statuses
			UpdateLoginInfo();
		}

		public IConfiguratorDatasource Datasource { get; set; }

		private void UpdateLoginInfo()
		{
			// check if the user has logged into the database yet
			if (!UserContext.IsLoginSuccessful)
			{
				// update the title and status bar
				this.Title = "VistA Imaging Telepathology Configurator";
				(statusBar.Items[2] as StatusBarItem).Content  = "No connection to VistA";

				// update menu items
				mnuLogin.IsEnabled = true;
				mnuLogout.IsEnabled = false;
			}
			else
			{
				// update the title and status bar
				string logonInfo = "(" + UserContext.ServerName + ") in use by: " + UserContext.UserCredentials.Fullname;
				this.Title = "VistA Imaging Telepathology Configurator";
				this.Title += ": " + logonInfo;
                (statusBar.Items[2] as StatusBarItem).Content = UserContext.LocalSite.SiteAbbreviation;

				// update menu items
				mnuLogin.IsEnabled = false;
				mnuLogout.IsEnabled = true;
			}
		}

		private void About_Click(object sender, RoutedEventArgs e)
		{
			AboutWindow aboutWin = new AboutWindow();
			aboutWin.ShowDialog();
		}

		private void Login_Click(object sender, RoutedEventArgs e)
		{
			Login();
		}

		private void Logout_Click(object sender, RoutedEventArgs e)
		{
			Logout();
		}

		private void Exit_Click(object sender, RoutedEventArgs e)
		{
			Close();
		}

		/// <summary>
		/// Log user into the system
		/// </summary>
		public void Login()
		{
			Log.Info("Logging in to VistA...");

			// login into vista if the datasource is available and not logged in
			if ((this.Datasource != null) && (!UserContext.IsLoginSuccessful))
			{
                try
                {
                    this.Datasource.InitializeConnection();
                }
                catch (Exception ex)
                {
                    string message = "Unable to authenticate user and initialize the application.";
                    Log.Info(message);
                    Log.Error(message, ex);

                    if (ex is MagInitializationFailureException)
                    {
                        MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                        ClearContext();
                    }
                    else if (ex is MagVixFailureException)
                    {
                        MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                        Logout();
                        ClearContext();
                    }
                    else
                    {
                        MessageBox.Show("Unable to intialize the application. Application will be terminated.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                        Environment.Exit(1);
                    }
                }

				// check if the user is authorized to use the application
				if (UserContext.IsLoginSuccessful)
				{
					//Datasource.GetMagSecurityKeys();
					if (!UserContext.UserHasKey("MAG SYSTEM"))
					{
						MessageBox.Show("You are not authorized to use this application and will be logged out.",
										"Information", MessageBoxButton.OK, MessageBoxImage.Information);
						Logout();
					}
				}

				UpdateLoginInfo();

				if (UserContext.IsLoginSuccessful)
				{
					// update data models
					readingSetupView.DataContext = new ReadingSiteSetupViewModel(Datasource);
					acquisitionSetupView.DataContext = new AcquisitionSiteSetupViewModel(Datasource);
					repTemplateView.DataContext = new ReportTemplateViewModel(Datasource);
					otherSettingsView.DataContext = new OtherSettingsViewModel(Datasource);
				}
			}
		}

		/// <summary>
		/// Log the user out of the system
		/// </summary>
		public void Logout()
		{
            try
            {
                bool templateUnsaved = (repTemplateView.DataContext as ReportTemplateViewModel).IsChanged;
                bool otherUnsaved = (otherSettingsView.DataContext as OtherSettingsViewModel).IsChanged;

                if ((templateUnsaved) || (otherUnsaved))
                //if (otherUnsaved)
                {
                    MessageBoxResult result = MessageBox.Show("There are unsaved changes. Are you sure you want to exit the application?", "Confirmation",
                                                              MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                    if (result != MessageBoxResult.Yes)
                    {
                        return;
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error("Could not cast data context for template and other settings.", ex);
            }

			Log.Info("Disconnecting from VistA...");

			// logout of vista if the datasource is available and logging in
			if ((this.Datasource != null) && (UserContext.IsLoginSuccessful))
			{
				try
				{
					this.Datasource.CloseConnection();
				}
				catch (Exception ex)
				{
					Log.Error("Fail to disconect from VistA. Application will exit.", ex);
					Environment.Exit(1);
				}

				ClearContext();
				UpdateLoginInfo();

				Log.Info("VistA disconnected.");
			}
		}

		/// <summary>
		/// Clear all the application context after logout of failure
		/// </summary>
		private void ClearContext()
		{
			UserContext.ResetUserContext();
			Datasource = new ConfiguratorDatasource();
			readingSetupView.DataContext = new ReadingSiteSetupViewModel();
			acquisitionSetupView.DataContext = new AcquisitionSiteSetupViewModel();
			repTemplateView.DataContext = new ReportTemplateViewModel();
			otherSettingsView.DataContext = new OtherSettingsViewModel();

			Log.Debug("All contexts are cleared.");
		}

		private void MessageLog_Click(object sender, RoutedEventArgs e)
		{
			// only check for loging right now but should also check for user keys next
			MessageLog logViewer = new MessageLog(UserContext.IsLoginSuccessful && UserContext.UserHasKey("MAG SYSTEM"));
			logViewer.ShowDialog();
		}

		private void Window_ContentRendered(object sender, EventArgs e)
		{
			// wait until the window has finished rendered for the first time before showing the login screen
			if (firstRendered)
			{
				firstRendered = false;
				Login();
			}
		}

		private void Window_Closed(object sender, EventArgs e)
		{
			Log.Info("Terminating VistA Imaging Pathology Worklist Configurator...");
		}

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            try
            {
                bool templateUnsaved = (repTemplateView.DataContext as ReportTemplateViewModel).IsChanged;
                bool otherUnsaved = (otherSettingsView.DataContext as OtherSettingsViewModel).IsChanged;

                if ((templateUnsaved) || (otherUnsaved))
                //if (otherUnsaved)
                {
                    MessageBoxResult result = MessageBox.Show("There are unsaved changes. Are you sure you want to exit the application?", "Confirmation",
                                                              MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                    if (result != MessageBoxResult.Yes)
                    {
                        e.Cancel = true;
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error("Could not cast data context for template and other settings.", ex);
            }
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