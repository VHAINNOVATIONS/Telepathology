// -----------------------------------------------------------------------
// <copyright file="ConsultationStatusViewModel.cs" company="Department of Veterans Affairs">
//  Package: MAG - VistA Imaging
//  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
//  Date Created: May 2012
//  Site Name:  Washington OI Field Office, Silver Spring, MD
//  Developer: Paul Pentapaty, Duc Nguyen
//  Description: Basic acquisition site information
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

namespace VistA.Imaging.Telepathology.Worklist.ViewModel
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using GalaSoft.MvvmLight;
    using System.Collections.ObjectModel;
    using VistA.Imaging.Telepathology.Common.Model;
    using GalaSoft.MvvmLight.Command;
    using VistA.Imaging.Telepathology.Worklist.Messages;
    using System.Windows;

    public class SiteConsultationStatusViewModel : WorkspaceViewModel
    {
        public bool IsCurrentSite;
        public CaseListItem Item { get; set; }

        public ReadingSiteInfo SiteInfo { get; set; }

        public bool IsPending { get; set; }

        public bool IsCompleted { get; set; }

        public bool IsRefused { get; set; }

        public bool IsRecalled { get; set; }

        public bool CanRequestConsultation { get; set; }

        public bool CanRefuseConsultation { get; set; }

        public string ConsultationID { get; set; }

        public bool IsActive
        {
            get
            {
                return this.SiteInfo == null ? false : this.SiteInfo.Active;
            }
        }

        public string ConsultationStatus
        {
            get
            {
                return this.IsPending ? "PENDING" : this.IsCompleted ? "COMPLETED" : this.IsRefused ? "DECLINED" : null; ;
            }
        }

        public string SiteDisplayName
        {
            get
            {
                return this.SiteInfo == null ? null : this.SiteInfo.Active? this.SiteInfo.SiteDisplayName : string.Format("{0} [INACTIVE]", this.SiteInfo.SiteDisplayName);
            }
        }
    }

    /// <summary>
    /// View model for editing existing consultations
    /// </summary>
    public class ConsultationStatusViewModel : WorkspaceViewModel
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ConsultationStatusViewModel"/> class with cases
        /// </summary>
        /// <param name="readSites">list of known reading sites</param>
        public ConsultationStatusViewModel(CaseListItem item, ReadingSiteList sitelist)
        {
            this.Sites = new ObservableCollection<SiteConsultationStatusViewModel>();
            this.SelectedSites = new ObservableCollection<SiteConsultationStatusViewModel>();

            this.CancelConsultationCommand = new RelayCommand(CancelConsultation, () => CanCancelConsultation());
            this.RequestConsultationCommand = new RelayCommand(RequestConsultation, () => CanRequestConsultation());
            this.RefuseConsultationCommand = new RelayCommand(RefuseConsultation, () => CanRefuseConsultation());

            this.AccessionNr = item.AccessionNumber;
            this.PatientName = item.PatientName;
            this.PatientID = item.PatientID;

            bool canRequestConsultation = true;
            foreach (ReadingSiteInfo readingSiteInfo in sitelist.Items)
            {
                if ((readingSiteInfo.SiteStationNumber == UserContext.LocalSite.PrimarySiteStationNUmber) && (readingSiteInfo.SiteType == ReadingSiteType.consultation))
                {
                    canRequestConsultation = false;
                    break;
                }
            }

            // process consultation sites
            foreach (ReadingSiteInfo siteInfo in sitelist.Items)
            {
                SiteConsultationStatusViewModel itemVM = new SiteConsultationStatusViewModel();
                itemVM.Item = item;
                itemVM.SiteInfo = siteInfo;
                Site localSite = UserContext.LocalSite;
                itemVM.CanRequestConsultation = canRequestConsultation;

                foreach (CaseConsultation consult in item.ConsultationList.ConsultationList)
                {
                    if ((siteInfo.SiteStationNumber == consult.SiteID) && (consult.Type == "CONSULTATION"))
                    {
                        itemVM.ConsultationID = consult.ConsultationID;

                        if (consult.Status == "PENDING")
                        {
                            itemVM.IsPending = true;

                            // if request pending for current site
                            if ((siteInfo.SiteStationNumber == UserContext.LocalSite.PrimarySiteStationNUmber) &&
                                (consult.SiteID == UserContext.LocalSite.PrimarySiteStationNUmber))
                            {
                                itemVM.CanRefuseConsultation = true;
                            }
                        }
                        else if (consult.Status == "REFUSED")
                        {
                            itemVM.IsRefused = true;
                        }
                        else if (consult.Status == "COMPLETED")
                        {
                            itemVM.IsCompleted = true;
                        }
                    }
                }

                if (!itemVM.IsPending && !itemVM.IsCompleted && !siteInfo.Active)
                {
                    // not active and not pending. ignore this site
                    continue;
                }

                this.Sites.Add(itemVM);
            }

            // process consultation requests for current site
            foreach (CaseConsultation consult in item.ConsultationList.ConsultationList)
            {
                if ((consult.SiteID == UserContext.LocalSite.PrimarySiteStationNUmber) && 
                    (consult.Type == "CONSULTATION") && (consult.Status == "PENDING"))
                {
                    SiteConsultationStatusViewModel itemVM = new SiteConsultationStatusViewModel();
                    itemVM.Item = item;
                    itemVM.SiteInfo = new ReadingSiteInfo
                    {
                        SiteStationNumber = UserContext.LocalSite.PrimarySiteStationNUmber,
                        SiteAbr = UserContext.LocalSite.SiteAbbreviation,
                        SiteName = UserContext.LocalSite.SiteName,
                        Active = true                    
                    };
                    itemVM.IsCurrentSite = true;
                    itemVM.ConsultationID = consult.ConsultationID;
                    itemVM.IsPending = true;
                    
                    // make sure item is not a duplicate; don't add dups
                    Boolean addToList = true;
                    foreach (SiteConsultationStatusViewModel itemInList in this.Sites)
                    {
                        if (itemInList.SiteDisplayName == itemVM.SiteDisplayName)
                            addToList = false;
                    }
                    if (addToList)
                        this.Sites.Add(itemVM);
                }
            }
        }

        public string AccessionNr { get; set; }

        public string PatientName { get; set; }

        public string PatientID { get; set; }

        /// Gets or sets a list of available sites
        /// </summary>
        public ObservableCollection<SiteConsultationStatusViewModel> Sites { get; set; }

        /// <summary>
        /// Gets or sets a list of selected sites
        /// </summary>
        public ObservableCollection<SiteConsultationStatusViewModel> SelectedSites { get; set; }

        /// <summary>
        /// Gets or sets a single selected reading site
        /// </summary>
        public SiteConsultationStatusViewModel SelectedSite { get; set; }

        #region Cancel Consultation

        /// <summary>
        /// Gets the command for cancelling consultation
        /// </summary>
        public RelayCommand CancelConsultationCommand
        {
            get;
            private set;
        }

        /// <summary>
        /// Request consultation handler
        /// </summary>
        void CancelConsultation()
        {
            MessageBoxResult result = MessageBox.Show("Are you sure you want to cancel the request(s)?", "Confirmation",
                                                        MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
            if (result == MessageBoxResult.Yes)
            {
                foreach (SiteConsultationStatusViewModel item in this.SelectedSites)
                {
                    AppMessages.EditConsultationStatusMessage.MessageData data = new AppMessages.EditConsultationStatusMessage.MessageData
                    {
                        Item = item.Item,
                        ConsultingSite = item.SiteInfo,
                        ConsultationID = item.ConsultationID,
                        CancelConsultationRequest = true
                    };

                    AppMessages.EditConsultationStatusMessage.Send(data);

                    if (data.Success)
                    {
                        item.IsPending = false;
                    }
                }

                CloseCommand.Execute(null);
            }
        }

        bool CanCancelConsultation()
        {
            // all selected sites must be in pending state
            if (this.SelectedSites.Count > 0)
            {
                foreach (SiteConsultationStatusViewModel item in this.SelectedSites)
                {
                    if (!item.CanRequestConsultation || !item.IsPending)
                    {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        #endregion

        #region Request Consultation

        public RelayCommand RequestConsultationCommand
        {
            get;
            private set;
        }

        public void RequestConsultation()
        {
            foreach (SiteConsultationStatusViewModel item in this.SelectedSites)
            {
                AppMessages.EditConsultationStatusMessage.MessageData data = new AppMessages.EditConsultationStatusMessage.MessageData
                {
                    Item = item.Item,
                    ConsultingSite = item.SiteInfo,
                };

                AppMessages.EditConsultationStatusMessage.Send(data);

                if (data.Success)
                {
                    item.IsPending = true;
                }
            }

            CloseCommand.Execute(null);
        }

        public bool CanRequestConsultation()
        {
            // all selected sites must be in non-pending state and sctive
            if (this.SelectedSites.Count > 0)
            {
                foreach (SiteConsultationStatusViewModel item in this.SelectedSites)
                {
                    if (!item.CanRequestConsultation || item.IsPending || !item.IsActive)
                    {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        #endregion

        #region Refuse Consultation

        public RelayCommand RefuseConsultationCommand
        {
            get;
            private set;
        }

        public void RefuseConsultation()
        {
            // This question might be in OnViewConsultationStatus of MainWindow.xaml.cs
             MessageBoxResult result = MessageBox.Show("Are you sure you want to decline the request(s)?", "Confirmation",
                                                        MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);

             if (result == MessageBoxResult.Yes)
            {
                foreach (SiteConsultationStatusViewModel item in this.SelectedSites)
                {
                    AppMessages.EditConsultationStatusMessage.MessageData data = new AppMessages.EditConsultationStatusMessage.MessageData
                    {
                        Item = item.Item,
                        ConsultingSite = item.SiteInfo,
                        ConsultationID = item.ConsultationID,
                        RefuseConsultationRequest = true
                    };

                    AppMessages.EditConsultationStatusMessage.Send(data);

                    if (data.Success)
                    {
                        item.IsPending = false;
                    }
                }

                CloseCommand.Execute(null);
            }
        }

        public bool CanRefuseConsultation()
        {
            // all selected sites must be in pending state
            if (this.SelectedSites.Count > 0)
            {
                foreach (SiteConsultationStatusViewModel item in this.SelectedSites)
                {
                    if (!item.IsPending || !item.CanRefuseConsultation)
                    {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        #endregion

    }
}
