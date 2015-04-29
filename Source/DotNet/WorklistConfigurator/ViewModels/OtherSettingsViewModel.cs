
namespace VistA.Imaging.Telepathology.Configurator.ViewModels
{
    using System.Windows;
    using GalaSoft.MvvmLight;
    using GalaSoft.MvvmLight.Command;
    using VistA.Imaging.Telepathology.Common.Exceptions;
    using VistA.Imaging.Telepathology.Common.Model;
    using VistA.Imaging.Telepathology.Logging;
    using VistA.Imaging.Telepathology.Configurator.DataSource;
    using System;

    public class OtherSettingsViewModel : ViewModelBase
    {
        private static MagLogger Log = new MagLogger(typeof(OtherSettingsViewModel));

        private string savedReportTimeout;

        private string savedAppTimeout;

        private string savedRetentionDays;

        // private string ReportTimeoutHour;
        
        /// <summary>
        /// Initializes a new instance of the OtherSettingsViewModel class.
        /// </summary>
        public OtherSettingsViewModel()
        {
            this.Datasource = null;
            this.savedReportTimeout = string.Empty;
            this.savedAppTimeout = string.Empty;
            this.savedRetentionDays = string.Empty;
            this.ReportTimeoutHour = this.savedReportTimeout;

            this.SaveCommand = new RelayCommand(SaveChanges, () => this.IsChanged);
            this.ResetChangesCommand = new RelayCommand(ResetChanges, () => this.IsChanged);
        }

        public OtherSettingsViewModel(IConfiguratorDatasource datasource)
        {
            this.Datasource = datasource;
            this.savedReportTimeout = this.Datasource.GetReportLockTimeoutHour(UserContext.LocalSite.PrimarySiteStationNUmber);
            this.savedAppTimeout = this.Datasource.GetApplicationTimeout().ToString();
            this.savedRetentionDays = this.Datasource.GetRetentionDays().ToString();
            
            this.ReportTimeoutHour = this.savedReportTimeout;
            this.ApplicationTimeoutMinutes = this.savedAppTimeout;
            this.RetentionDays = this.savedRetentionDays;

            this.SaveCommand = new RelayCommand(SaveChanges, () => (this.Datasource != null) && (this.IsChanged));
            this.ResetChangesCommand = new RelayCommand(ResetChanges, () => this.IsChanged);
        }

        public RelayCommand SaveCommand
        {
            get;
            private set;
        }

        public RelayCommand ResetChangesCommand
        {
            get;
            private set;
        }

        public IConfiguratorDatasource Datasource { get; set; }

        public string ReportTimeoutHour { get; set; }

        public string ApplicationTimeoutMinutes { get; set; }

        public string RetentionDays { get; set; }

        public bool IsChanged
        {
            get
            {
                if (!string.IsNullOrWhiteSpace(this.ReportTimeoutHour))
                {
                    if (this.savedReportTimeout != this.ReportTimeoutHour)
                    {
                        return true;
                    }
                }

                if (!string.IsNullOrWhiteSpace(this.ApplicationTimeoutMinutes))
                {
                    if (this.savedAppTimeout != this.ApplicationTimeoutMinutes)
                    {
                        return true;
                    }
                }

                if (!string.IsNullOrWhiteSpace(this.RetentionDays))
                {
                    if (this.savedRetentionDays != this.RetentionDays)
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        private void SaveChanges()
        {
            // check for empty string
            if ((string.IsNullOrWhiteSpace(this.ReportTimeoutHour)) || 
                (string.IsNullOrWhiteSpace(this.ApplicationTimeoutMinutes)) || 
                (string.IsNullOrWhiteSpace(this.RetentionDays)))
            {
                MessageBox.Show("Please enter a valid value.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            // check for valid number format
            int reportTimeoutVal;
            bool isNumber = int.TryParse(this.ReportTimeoutHour, out reportTimeoutVal);
            if (!isNumber)
            {
                MessageBox.Show("The value you entered is not a valid number.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            int appTimeoutVal;
            isNumber = int.TryParse(this.ApplicationTimeoutMinutes, out appTimeoutVal);
            if (!isNumber)
            {
                MessageBox.Show("The value you entered is not a valid number.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            int caseDurationVal;
            isNumber = int.TryParse(this.RetentionDays, out caseDurationVal);
            if (!isNumber)
            {
                MessageBox.Show("The value you entered is not a valid number.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            // check for positive number
            if (((reportTimeoutVal < 1) || (reportTimeoutVal > 600)) || 
                ((appTimeoutVal < 0) || (appTimeoutVal > 600)) || 
                ((caseDurationVal < 1) || (caseDurationVal > 90)))
            {
                MessageBox.Show("Please enter a value within the specified range.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            bool saveRepLock = true;
            bool saveAppTimeout = true;
            bool saveRenDays = true;

            if (this.ReportTimeoutHour != savedReportTimeout)
            {
                try
                {
                    this.Datasource.SetReportLockTimeoutHour(UserContext.LocalSite.PrimarySiteStationNUmber, this.ReportTimeoutHour);
                    //updated the saved data
                    this.savedReportTimeout = this.ReportTimeoutHour;
                    saveRepLock = true;

                    Log.Info("Changes to the report lock duration has been saved to site " + UserContext.LocalSite.PrimarySiteStationNUmber);
                }
                catch (MagVixFailureException vfe)
                {
                    Log.Error("Failed to save report lock duration to VistA.", vfe);
                    saveRepLock = false;
                }
            }

            if (this.ApplicationTimeoutMinutes != savedAppTimeout)
            {
                try
                {
                    this.Datasource.SetApplicationTimeout(appTimeoutVal);
                    this.savedAppTimeout = this.ApplicationTimeoutMinutes;
                    saveAppTimeout = true;

                    Log.Info("Changes to the worklist timeout has been saved to site " + UserContext.LocalSite.PrimarySiteStationNUmber);
                }
                catch (MagVixFailureException vfe)
                {
                    Log.Error("Failed to save worklist timeout to VistA.", vfe);
                    saveAppTimeout = false;
                }
            }

            if (this.RetentionDays != savedRetentionDays)
            {
                try
                {
                    this.Datasource.SetRetentionDays(caseDurationVal);
                    this.savedRetentionDays = this.RetentionDays;
                    saveRenDays = true;

                    Log.Info("Changes to the read list retention has been saved to site " + UserContext.LocalSite.PrimarySiteStationNUmber);
                }
                catch (MagVixFailureException vfe)
                {
                    Log.Error("Failed to save read list retention days to VistA.", vfe);
                    saveRenDays = false;
                }
            }

            string message = "The following field(s) cannot be saved:" + Environment.NewLine;
            if (!saveRepLock)
                message += "Report lock duration" + Environment.NewLine;
            if (!saveAppTimeout)
                message += "Worklist timeout" + Environment.NewLine;
            if (!saveRenDays)
                message += "Read list retention" + Environment.NewLine;

            if ((!saveRepLock) || 
                (!saveAppTimeout) || (!saveRenDays))
                MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
        }

        private void ResetChanges()
        {
            MessageBoxResult result = MessageBox.Show("Are you sure you want to reset the changes?", "Confirmation", 
                                                      MessageBoxButton.OKCancel, MessageBoxImage.Question, MessageBoxResult.Cancel);
            if (result == MessageBoxResult.OK)
            {
                this.ReportTimeoutHour = this.savedReportTimeout;
                this.RetentionDays = this.savedRetentionDays;
                this.ApplicationTimeoutMinutes = this.savedAppTimeout;
            }
        }
    }
}