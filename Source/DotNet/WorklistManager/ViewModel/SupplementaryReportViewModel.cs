
namespace VistA.Imaging.Telepathology.Worklist.ViewModel
{
	using GalaSoft.MvvmLight;
	using VistA.Imaging.Telepathology.Worklist.DataSource;
	using VistA.Imaging.Telepathology.Common.Model;
	using System.ComponentModel;
	using GalaSoft.MvvmLight.Command;
	using System;
	using System.Diagnostics;
	using System.Windows;
	using System.Collections.Generic;
	using System.Collections.ObjectModel;
	using VistA.Imaging.Telepathology.Worklist.Views;
	using System.Linq;
	using System.Text.RegularExpressions;
	using VistA.Imaging.Telepathology.Common.Exceptions;
	using VistA.Imaging.Telepathology.Logging;
	using VistA.Imaging.Telepathology.Common.VixModels;

	public class SupplementaryReportViewModel : ViewModelBase
	{
		private static MagLogger Log = new MagLogger(typeof(SupplementaryReportViewModel));

		private IWorkListDataSource DataSource;

		private SupplementaryReportModel srModel = new SupplementaryReportModel();

		private bool addSR = false;

		public SupplementaryReportViewModel(IWorkListDataSource dataSource, string siteID, string accessionNumber, string caseURN, CaseConsultationList consultationList,
											bool isGlobalReadOnly = false, ReadingSiteType siteType = ReadingSiteType.interpretation)
		{
			this.DataSource = dataSource;

			// case info
			this.SiteID = siteID;
			this.AccessionNumber = accessionNumber;
			this.CaseURN = caseURN;
			this.ConsultationList = consultationList;
			this.CurrentSiteType = siteType;
			this.IsEditorReadOnly = isGlobalReadOnly;

            GetConsultationID();
		
			// retrieve all the supplementary reports for the case
			srModel = dataSource.GetSupplementalReports(this.CaseURN);



			ClearSRCommand = new RelayCommand(ClearSR, () => this.CanClearSR);
            AddNewSRCommand = new RelayCommand(AddNewSR, () => this.CanAddNewSR);
			UpdateSRCommand = new RelayCommand(UpdateSR, () => this.CanUpdateSR);
			VerifySRCommand = new RelayCommand(VerifySR, () => this.CanVerifySR);
            CompleteConsultationCommand = new RelayCommand(CompleteConsultation, () => this.CanCompleteConsultation);

			ClearSR();

			// retrieve the Esignature status at the case's primary site
			GetESignatureStatus();	
		}

		#region Commands
		public RelayCommand ClearSRCommand
		{
			get;
			private set;
		}

        public bool CanClearSR
        {
            get
            {
                if (this.SelectedSR == null)
                    return false;
                return true;
            }
        }

		public RelayCommand AddNewSRCommand
		{
			get;
			private set;
		}

		public bool CanAddNewSR
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
                    (this.SelectedSR == null))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		public RelayCommand UpdateSRCommand
		{
			get;
			private set;
		}

        public bool CanUpdateSR
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
                    (this.SelectedSR != null) && 
					(!this.IsSelectedSRReadOnly))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

        public bool IsSelectedSRReadOnly
        {
            get
            {
                if (this.SelectedSR != null)
                {
                    // all verified reports are read only 
                    if (this.SelectedSR.Verified == "Yes")
                        return true;

                    // cannot modify consultation report if site is interpretation
                    if (this.CurrentSiteType == ReadingSiteType.interpretation)
                    {
                        if (this.SelectedSR.Content.Contains("---Consultation from:"))
                            return true;
                    }
                    else if (this.CurrentSiteType == ReadingSiteType.consultation)
                    {
                        // consulting site cannot modify consultation report if the consultation header is not from same site
                        string localHeader = "---Consultation from: " + UserContext.LocalSite.SiteName + "---";
                        if (this.SelectedSR.Content.Contains(localHeader))
                        {
                            // cannot modify if the consultation is already completed
                            if (this.SelectedSR.Content.Contains("---Completed by:"))
                                return true;
                            else
                                return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

		public RelayCommand VerifySRCommand
		{
			get;
			private set;
		}

		public bool CanVerifySR
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
					(this.CanUserVerifySelectedSR))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

        public bool CanUserVerifySelectedSR
        {
            get
            {
                // only interpretation site can verify
                if (this.CurrentSiteType != ReadingSiteType.interpretation)
                    return false;

                // if there's something selected
                if (this.SelectedSR == null)
                {
                    return false;
                }

                // only unverified reports
                if (this.SelectedSR.Verified == "Yes")
                {
                    return false;
                }

                // if the supplementary report is a consultation, then only verifiable if completed
                if (this.SelectedSR.Content.Contains("---Consultation from:"))
                {
                    if (this.SelectedSR.Content.Contains("---Completed by:"))
                        return true;
                    else
                        return false;
                }

                return true;
            }
        }

		public RelayCommand CompleteConsultationCommand
		{
			get;
			private set;
		}

		public bool CanCompleteConsultation
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
                    (this.CanUserCompleteSelectedConsultation))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

        public bool CanUserCompleteSelectedConsultation
        {
            get
            {
                if (this.CurrentSiteType != ReadingSiteType.consultation)
                    return false;

                if (this.SelectedSR == null)
                    return false;

                if (string.IsNullOrWhiteSpace(this.ConsultationID))
                    return false;

                string localHeader = "---Consultation from: " + UserContext.LocalSite.SiteName + "---";
                if (!this.SelectedSR.Content.Contains(localHeader))
                    return false;
                
                if (this.SelectedSR.Content.Contains("---Completed by:"))
                    return false;
                
                return true;
            }
        }

        //public bool CanUserCompleteConsultation
        //{
        //    get
        //    {
        //        if (this.SRSelectedIndex > -1)
        //        {
        //            if (!this.IsInterpretingSite)
        //            {
        //                if ((this.ConsultationList != null) && (this.ConsultationList.ConsultationList != null))
        //                {
        //                    var consult = this.ConsultationList.ConsultationList.Where(con => con.ConsultationID == this.ConsultationID).FirstOrDefault();
        //                    if (consult != null)
        //                    {
        //                        if (consult.Status == "PENDING")
        //                        {
        //                            return true;
        //                        }
        //                    }
        //                }
        //            }
        //        }
        //        return false;
        //    }
        //}
		#endregion

        private void GetESignatureStatus()
        {
            if (this.CurrentSiteType == ReadingSiteType.consultation)
            {
                AccessionNumber acNum = new AccessionNumber(this.AccessionNumber);
                string localESigKey = UserContext.LocalSite.PrimarySiteStationNUmber + "^" + acNum.Type;
                if (UserContext.UserCredentials.UserHasESignatureStatus(localESigKey))
                    this.LocalEsigStatus = UserContext.UserCredentials.GetESignatureStatusAtSite(localESigKey);
                else
                {
                    // retrieve the Esignature status from database
                    this.LocalEsigStatus = DataSource.GetESignatureStatus(UserContext.LocalSite.PrimarySiteStationNUmber, this.AccessionNumber);

                    // update to the list
                    UserContext.UserCredentials.ESignatureStatuses.Add(localESigKey, this.LocalEsigStatus);
                }
            }
        }

		
        private void GetConsultationID()
        {
            if (this.CurrentSiteType == ReadingSiteType.interpretation)
                this.ConsultationID = string.Empty;
            else
            {
                // check the consultation list to see if there's pending status
                if ((this.ConsultationList == null) || (this.ConsultationList.ConsultationList == null))
                    this.ConsultationID = string.Empty;
                else
                {
                    var consult = this.ConsultationList.ConsultationList.Where(con => (con.SiteID == UserContext.LocalSite.SiteStationNumber) &&
                                                                                      (con.Type == "CONSULTATION") &&
                                                                                      (con.Status == "PENDING")).FirstOrDefault();
                    if (consult == null)
                        this.ConsultationID = string.Empty;
                    else
                    {
                        this.ConsultationID = consult.ConsultationID;
                    }
                }
            }
        }
		

		private void ClearSR()
		{
			//SRSelectedIndex = -1;
            this.SelectedSR = null;
			SRSelectedDate = DateTime.Today;
			SRSelectedContent = string.Empty;
		}

		private void AddNewSR()
		{
			addSR = true;
			UpdateSR();
		}

		private bool ContainsIllegalCharacters()
		{
			if (this.SRSelectedContent.Contains('^'))
					return true;

			return false;
		}

		private void UpdateSR()
		{
			// check for illegal characters 
			if (ContainsIllegalCharacters())
			{
				MessageBox.Show("Please remove ^ from your supplementary report.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                addSR = false;
				return;
			}

			// check if there's a date
			if (SRSelectedDate == null)
			{
				MessageBox.Show("Please enter a valid date.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				addSR = false;
				return;
			}

			// check if there is any content
			if (string.IsNullOrWhiteSpace(this.SRSelectedContent))
			{
				MessageBox.Show("Please enter something for the supplementary report.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				addSR = false;
				return;
			}

			// if update SR then that's mean the index is already selected
			if (!addSR)
			{
                if (this.SelectedSR != null)
				{
					try
					{
						DataSource.SaveSupReport(this.CaseURN, SRSelectedDate.Value.ToString("yyyyMMddHHmm"), false, this.SRSelectedContent);
                        this.SelectedSR.Content = SRSelectedContent;
                        ClearSR();

                        Log.Info(string.Format("Updated supplementary report for {1} at {0}.",this.SiteID, this.AccessionNumber));
					}
					catch (MagVixFailureException vfe)
					{
						Log.Error("Failed to update supplementary report.", vfe);
						MessageBox.Show("Couldn't update supplementary report.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
					}
				}
			}
			else
			{
				// if add new one then no selected index and add time to the date
				DateTime selDate = SRSelectedDate.GetValueOrDefault(DateTime.Now);
				DateTime currentTime = DateTime.Now;

				// if this is a consulting site with pending consultation, add a boiler plate
				string text = string.Empty;
				if (this.CurrentSiteType == ReadingSiteType.consultation)
				{
					if (!string.IsNullOrWhiteSpace(this.ConsultationID))
						text += "---Consultation from: " + UserContext.LocalSite.SiteName + "---" + Environment.NewLine;

                    // should also check if there's no other sr in the list has the text already
                    foreach (SupplementaryReport sr in this.SRList)
                    {
                        if (sr.Content.Contains(text))
                        {
                            text = string.Empty;
                            break;
                        }
                    }
				}

				SupplementaryReport newSR = new SupplementaryReport()
				{
					Index = SRList.Count,
					SRDate = selDate.ToString("MM-dd-yyyy") + " " + currentTime.ToString("HH:mm"),
					Modified = "",
					Verified = "",
					VerifiedBy = "",
					IsDirty = true,
					Content = text + SRSelectedContent
				};

				try
				{
					// store to acquisition site
					string saveDateTime = this.SRSelectedDate.Value.ToString("yyyyMMdd") + currentTime.ToString("HHmm");
					DataSource.SaveSupReport(this.CaseURN, saveDateTime, false, newSR.Content);
					SRList.Add(newSR);
					ClearSR();

                    Log.Info(string.Format("New supplementary report created for {0} at {1}.", this.AccessionNumber, this.SiteID ));
				}
				catch (MagVixFailureException vfe)
				{
					Log.Error("Failed to add supplementary report.", vfe);
					MessageBox.Show("Couldn't add supplementary report.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
				}

				addSR = false;
			}
		}

		private void CompleteConsultation()
		{
			// check if any existing entry is selected in the list
			if (this.SelectedSR != null)
			{
				// check if the content of the report has been change, if so save first
				if (this.SelectedSR.Content != SRSelectedContent)
				{
					MessageBox.Show("The consultation report has been changed. Please save the changes before completing the report.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
					return;
				}

				// check to make sure the content is definitely a consultation
				// the first line must have ---Consultation from: [Consulting Site Abbreviation]---
				string searchHeader = "---Consultation from: " + UserContext.LocalSite.SiteName + "---";
				if (!this.SRSelectedContent.Contains(searchHeader))
				{
					MessageBox.Show("Consultation report must have the following line at the beginning." + Environment.NewLine + searchHeader,
									"Information", MessageBoxButton.OK, MessageBoxImage.Information);
					return;
				}

                // check for esig if needed
                bool CanComplete = true;

                // ask for E-signature if it's enabled
                if (this.LocalEsigStatus.Status == ESigNeedType.authorized_needs_signature)
                {
                    if (!this.LocalEsigStatus.IsSigned)
                    {
                        EverifyView eview = new EverifyView(DataSource, UserContext.LocalSite.PrimarySiteStationNUmber);
                        eview.ShowDialog();
                        CanComplete = eview.Success;
                        if (CanComplete)
                            this.LocalEsigStatus.IsSigned = true;
                    }
                }

                if (!CanComplete)
                {
                    MessageBox.Show("You cannot complete consultation without proper e-signature verification.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }

				// update the consultation status
				try
				{
					DataSource.UpdateConsultationStatus(this.ConsultationID, "completed");
					var consult = this.ConsultationList.ConsultationList.Where(con => con.ConsultationID == this.ConsultationID).FirstOrDefault();
					consult.Status = "COMPLETED";
                    Log.Info(string.Format("Completed consultation for {0} at {1}.", this.AccessionNumber, this.SiteID));
				}
				catch (MagVixFailureException vfe)
				{
					Log.Error("Failed to update consultation status.", vfe);
					MessageBox.Show("Couldn't update the consultation status.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
					return;
				}

				// create a local duplicate of the case before proceeding
				// only proceed if the case is created successfully
				string copyCaseAccession = DataSource.CreateCopyCase(UserContext.LocalSite.PrimarySiteStationNUmber, this.CaseURN);
				string completionDateTime = string.Empty;
				if (!string.IsNullOrWhiteSpace(copyCaseAccession))
				{
					// try tro retrieve the server time for verifying
					string[] accPieces = copyCaseAccession.Split('^');
					if ((accPieces != null) && (accPieces.Length == 2))
					{
						completionDateTime = DataSource.GetReportFieldData(".11", accPieces[1]);

						MessageBox.Show("A reference record has been created for this consultation.\r\nThe reference case is: " + UserContext.LocalSite.SiteAbbreviation + " " + accPieces[0],
										"Information", MessageBoxButton.OK, MessageBoxImage.Information);

                        Log.Info(string.Format("Local reference report {0} created at site {1}",
                                                       accPieces[0], UserContext.LocalSite.PrimarySiteStationNUmber));
					}
					else
					{
						MessageBox.Show("A reference record has been created for this consultation.\r\nThe reference case is: " + UserContext.LocalSite.SiteAbbreviation + " " + copyCaseAccession,
										"Information", MessageBoxButton.OK, MessageBoxImage.Information);
					}

                    Log.Info(string.Format("Reference report created for {0} at {1}.", this.AccessionNumber, this.SiteID));
				}
				else
				{
					MessageBox.Show("A reference report couldn't be generated. Please note down the information for this case and contact help.", "Information",
									MessageBoxButton.OK, MessageBoxImage.Information);
				}

				// store either new released datetime or current time
				completionDateTime = string.IsNullOrWhiteSpace(completionDateTime) ? DateTime.Now.ToString("dd-MM-yyyy HH:mm") : completionDateTime;

				// add a boiler plate footer to the report
				string footer = string.Empty;
				footer += Environment.NewLine + "---Completed by: " + UserContext.UserCredentials.Fullname + "---" + Environment.NewLine;
				footer += "---On: " + completionDateTime + "---" + Environment.NewLine;
				footer += "---At: " + UserContext.LocalSite.SiteName + "---";
				this.SelectedSR.Content += footer;
				
				// try to save the report with footer to database
				try
				{
					DataSource.SaveSupReport(this.CaseURN, SRSelectedDate.Value.ToString("yyyyMMddHHmm"), false, this.SelectedSR.Content);
				}
				catch (MagVixFailureException vfe)
				{
					// if unable to save the report, remove the footer
					Log.Error("Failed to save supplementary report.", vfe);
					this.SelectedSR.Content.Replace(footer, string.Empty);
					MessageBox.Show("Couldn't update the consultation report.\r\nPlease retry at a later time.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
					return;
				}

                ClearSR();
			}
			else
			{
				MessageBox.Show("Please selected a valid consultation to complete.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
			}
		}

		private void VerifySR()
		{
			// check if an existing entry is selected in the list
			if (this.SelectedSR != null)
			{
				// check if the content of the report has been change, if so save first
				if (this.SelectedSR.Content != SRSelectedContent)
				{
					MessageBox.Show("The supplementary report has been changed. Please save the changes before verifying the report.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
					return;
				}

                bool CanVerify = true;

                // ask for E-signature if it's enabled
                if (this.EsigStatus.Status == ESigNeedType.authorized_needs_signature)
                {
                    if (!this.EsigStatus.IsSigned)
                    {
                        EverifyView eview = new EverifyView(DataSource, this.SiteID);
                        eview.ShowDialog();
                        CanVerify = eview.Success;
                        if (CanVerify)
                            this.EsigStatus.IsSigned = true;
                    }
                }

                if (!CanVerify)
                {
                    MessageBox.Show("You cannot verify supplementary report without proper e-signature verification.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    return;
                }

				// only the interpreting site can actually verify the report
				try
				{
					DataSource.SaveSupReport(this.CaseURN, SRSelectedDate.Value.ToString("yyyyMMddHHmm"), true, this.SelectedSR.Content);
					this.SelectedSR.Verified = "Yes";
					this.SelectedSR.VerifiedBy = UserContext.UserCredentials.Fullname;

					MessageBoxResult result = MessageBox.Show("An alert has been sent to " + Practitioner + ".\r\nDo you want to send to additional recipient or mailgroups?",
															  "Infomation", MessageBoxButton.YesNo, MessageBoxImage.Information, MessageBoxResult.No);
					if (result == MessageBoxResult.Yes)
					{
						try
							{
								string subject = SRSelectedDate.GetValueOrDefault().ToShortDateString() + " supplementary report for " + AccessionNumber + " has been verified.";
								Process.Start("mailto:?subject=" + subject);
							}
						catch (Exception ex)
						{
							MessageBox.Show("Email client could not be initiated.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
							Log.Error("Failed to start email client process.", ex);
						}
					}

                    ClearSR();

                    Log.Info(string.Format("A supplementary report has been verified and released for {0} at {1}.", this.AccessionNumber, this.SiteID));
				}
				catch (MagVixFailureException vfe)
				{
					Log.Error("Failed to verify the supplementary report.", vfe);
					MessageBox.Show("Couldn't verify the supplementary report.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
					return;
				}
			}
			else
			{
				MessageBox.Show("Please select a valid item in the supplementary report list to verify", "Information", MessageBoxButton.OK,MessageBoxImage.Information);
			}
		}


        public bool CanUserSeeVerifyButton
        {
            get
            {
                // only interpretation site can see the button
                if (this.CurrentSiteType != ReadingSiteType.interpretation)
                    return false;


                // since there's no copying supplementary report, we only need to check if the user has the
                // privillege at the case's site
                bool userHasVerifyKey = UserContext.UserHasKey(this.SiteID, "LRVERIFY");
                if (!userHasVerifyKey)
                    return false;
                else
                {
                    // if the user has the key, check for esignature status
                    if (this.EsigStatus == null)
                        return false;
                    else
                    {
                        switch (this.EsigStatus.Status)
                        {
                            case ESigNeedType.not_enabled:
                            case ESigNeedType.authorized_needs_signature:
                                return true;
                            case ESigNeedType.not_authorized:
                            case ESigNeedType.error:
                                return false;
                            default:
                                return false;
                        }
                    }
                }
                
            }
        }

        public bool CanUserSeeCompleteButton
        {
            get
            {
                // only consultation site can see the button
                if (this.CurrentSiteType != ReadingSiteType.consultation)
                    return false;


                // since we need to make a copy of the reference case, we need the privillege at the local site
                bool userHasVerifyKey = UserContext.UserHasKey(UserContext.LocalSite.PrimarySiteStationNUmber, "LRVERIFY");
                if (!userHasVerifyKey)
                    return false;
                else
                {
                    // if the user has the key, check for esignature status
                    if (this.LocalEsigStatus == null)
                        return false;
                    else
                    {
                        switch (this.LocalEsigStatus.Status)
                        {
                            case ESigNeedType.not_enabled:
                            case ESigNeedType.authorized_needs_signature:
                                return true;
                            case ESigNeedType.not_authorized:
                            case ESigNeedType.error:
                                return false;
                            default:
                                return false;
                        }
                    }
                }

            }
        }		

		public string SRSelectedContent { get; set; }

        public bool IsAllSRVerified
		{
			get
			{
				if ((this.SRList != null) && (this.SRList.Count > 0))
				{
					foreach (SupplementaryReport sr in this.SRList)
					{
						if (sr.Verified != "Yes")
						{
							return false;
						}
					}
				}

				return true;
			}
		}

        public string CaseURN { get; set; }

		public bool IsEditorReadOnly { get; set; }

		public ReadingSiteType CurrentSiteType { get; set; }

		public CaseConsultationList ConsultationList { get; set; }

        public ObservableCollection<SupplementaryReport> SRList
        {
            get
            {
                return srModel.SRList;
            }
        }

        public SupplementaryReport SelectedSR { get; set; }

        public DateTime SRDateStart { get; set; }

        public DateTime SRDateEnd
        {
            get
            {
                return DateTime.Now;
            }
        }

        public string Practitioner { get; set; }

        public string AccessionNumber { get; set; }
        
        public string SiteID { get; set; }

        public DateTime? SRSelectedDate { get; set; }

        public string ConsultationID { get; set; }

        public PathologyElectronicSignatureNeedType EsigStatus { get; set; }

        public PathologyElectronicSignatureNeedType LocalEsigStatus { get; set; }
	}
}