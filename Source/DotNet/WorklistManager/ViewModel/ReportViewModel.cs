namespace VistA.Imaging.Telepathology.Worklist.ViewModel
{
	using System;
	using System.Collections.Generic;
	using GalaSoft.MvvmLight;
	using VistA.Imaging.Telepathology.Worklist.DataSource;
	using VistA.Imaging.Telepathology.Common.Model;
	using GalaSoft.MvvmLight.Command;
	using System.ComponentModel;
	using System.Diagnostics;
	using System.Windows;
	using VistA.Imaging.Telepathology.CCOW;
	using VistA.Imaging.Telepathology.Worklist.Views;
	using System.Linq;
	using System.Collections.ObjectModel;
	using VistA.Imaging.Telepathology.Common.VixModels;
	using VistA.Imaging.Telepathology.Common.Exceptions;
	using VistA.Imaging.Telepathology.Logging;

	public class ReportViewModel : ViewModelBase
	{
		private static MagLogger Log = new MagLogger(typeof(ReportViewModel));

		private PathologyCaseReportFieldsType ChangeList;

		private Report report;

		public Patient Patient { get; private set; }

		private PathologyElectronicSignatureNeedType EsigStatus;
		private PathologyElectronicSignatureNeedType LocalEsigStatus;

        private PathologySaveCaseReportResultType saveResult = null;

		public ReportViewModel(IWorkListDataSource dataSource, CaseListItem caseItem, bool isGlobalReadOnly = false, ReadingSiteType siteType = ReadingSiteType.interpretation)
		{
			DataSource = dataSource;

			// editor property
			this.IsEditorReadOnly = isGlobalReadOnly;
			this.CurrentSiteType = siteType;

			// case's properties
			this.CaseURN = caseItem.CaseURN;
			this.SiteAbbr = caseItem.SiteAbbr;
			this.SiteCode = caseItem.SiteCode;
			this.AccessionNumber = caseItem.AccessionNumber;
			ChangeList = new PathologyCaseReportFieldsType();
			
			// patient's info
			this.PatientName = caseItem.PatientName;
			this.PatientID = caseItem.PatientID;
			this.Patient = DataSource.GetPatient(caseItem.SiteCode, caseItem.PatientICN);

			// retrieve the Esignature status at the case's primary site
			GetESignatureStatus();	

			// consultations
			this.ConsultationList = caseItem.ConsultationList;

			// CCOW set patient context
			IContextManager contextManager = ViewModelLocator.ContextManager;
			contextManager.SetCurrentPatient(this.Patient);
			this.CCOWContextState = contextManager.GetPatientContextState(this.Patient);

			// get notified of CCOW context state change events
			contextManager.PropertyChanged += new PropertyChangedEventHandler(contextManager_PropertyChanged);

			// retrieve report data
			report = dataSource.GetReport(caseItem);

			// retrieve supplementary reports
			DateTime srDisplayDateStart;
			if (!DateTime.TryParse(DateSpecReceived, out srDisplayDateStart))
			{
				srDisplayDateStart = DateTime.Today;
			}
			SRViewModel = new SupplementaryReportViewModel(DataSource, SiteCode, AccessionNumber, this.CaseURN, this.ConsultationList, this.IsEditorReadOnly, this.CurrentSiteType);
			SRViewModel.SRDateStart = srDisplayDateStart;
			SRViewModel.Practitioner = Practitioner;
            SRViewModel.EsigStatus = this.EsigStatus;
            //srViewModel.LocalEsigStatus = this.LocalEsigStatus;

			// initialized codeing tab
			ReportCodingVM = new ReportCodingViewModel(this.DataSource, this.CaseURN, this.SiteCode, this.IsEditorReadOnly, this.CurrentSiteType);

			LaunchCPRSReportCommand = new RelayCommand(LaunchCPRSReport, () => this.State == ReportState.Verified);
			SaveReportCommand = new RelayCommand(SaveMainReportToDatabase, () => CanSaveMainReport);
			CompleteReportCommand = new RelayCommand(CompleteReport, () => CanCompleteMainReport);
			VerifyReportCommand = new RelayCommand(VerifyReport, () => CanVerifyMainReport);
			SearchUserCommand = new RelayCommand<string>((s) => SearchUser(s), (s) => CanSearchUser);

			if ((this.report == null) || (this.report.FieldList == null) || (this.report.FieldList.Count <= 0))
			{
				Log.Error("Missing report template.");
				MessageBox.Show("Cannot retrieve data fields for the main report. Please contact system administrator.",
								"Error", MessageBoxButton.OK, MessageBoxImage.Error);
			}

			// check the condition and set the GUI as read only or not
			SetMainReportReadOnly();
		}

		#region Commands
		public RelayCommand<string> SearchUserCommand
		{
			get;
			private set;
		}

		public bool CanSearchUser
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
					(this.HasReportContent) &&
					(this.CurrentSiteType == ReadingSiteType.interpretation) &&
					(!this.IsVerified))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		public RelayCommand SaveReportCommand
		{
			get;
			private set;
		}

		public bool CanSaveMainReport
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
					(this.HasReportContent) &&
					(this.CurrentSiteType == ReadingSiteType.interpretation) &&
					(!this.IsVerified) &&
					(this.IsModified))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		public RelayCommand CompleteReportCommand
		{
			get;
			private set;
		}

		public bool CanCompleteMainReport
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
					(this.HasReportContent) &&
					(this.CurrentSiteType == ReadingSiteType.interpretation) &&
					(!this.IsVerified) &&
					(!this.IsCompleted))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		public RelayCommand VerifyReportCommand
		{
			get;
			private set;
		}

		public bool CanVerifyMainReport
		{
			get
			{
				if ((!this.IsEditorReadOnly) &&
					(this.CurrentSiteType == ReadingSiteType.interpretation) &&
					(this.HasReportContent) && 
					(this.IsCompleted) &&
					(!this.IsVerified))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		public RelayCommand LaunchCPRSReportCommand
		{
			get;
			private set;
		}
		#endregion

		#region Control Properties

		/// <summary>
		/// Indicate the Report Editor is whether or not read only (Header, Main, Supplementary, Coding)
		/// </summary>
		public bool IsEditorReadOnly { get; set; }

		/// <summary>
		/// Indicate the current site type (interpretation, consultation)
		/// </summary>
		public ReadingSiteType CurrentSiteType { get; set; }

		public SupplementaryReportViewModel SRViewModel { get; set; }

		#endregion

		#region Report Editor Header Properties

		public string PatientName { get; set; }

		public string PatientID { get; set; }

		public string DateSpecTaken
		{
			get
			{
				return report.DateSpecTaken;
			}
		}

		public string DateSpecReceived
		{
			get
			{
				return report.DateSpecReceived;
			}
		}

		public string DateCompleted
		{
			get
			{
				return report.DateCompleted;
			}
			set
			{
				report.DateCompleted = value;
			}
		}

		public string Submitter
		{
			get
			{
				return report.Submitter;
			}
		}
		
		public string Pathologist
		{
			get
			{
				return report.Pathologist;
			}
		}

		public string Resident
		{
			get
			{
				return report.Resident;
			}
		}

		public string Practitioner
		{
			get
			{
				return report.Practitioner;
			}
		}

		/// <summary>
		/// Display the label for resident field based on report type
		/// </summary>
		public string ResidentLabel
		{
			get
			{
				string label = string.Empty;
				if (!string.IsNullOrWhiteSpace(this.AccessionNumber))
				{
					AccessionNumber ac = new AccessionNumber(this.AccessionNumber);
					if ((ac.Type == "CY") || (ac.Type == "EM"))
						label = "Tech";
					else if (ac.Type == "SP")
						label = "Resident";
				}

				label = label + ":";
				return label;
			}
		}

		public string AccessionNumber { get; set; }

		#endregion

		#region Case Properties
		public string SiteCode { get; set; }

		public string SiteAbbr { get; set; }

		public string CaseURN { get; set; }

		public ReportState State
		{
			get
			{
				return report.State;
			}
		}

		public string ReleasedDate
		{
			get
			{
				return report.ReleasedDate;
			}
			set
			{
				report.ReleasedDate = value;
			}
		}

		public string ReleasedBy
		{
			get
			{
				return report.ReleasedBy;
			}
			set
			{
				report.ReleasedBy = value;
			}
		}

		public bool IsCaseLocal
		{
			get
			{
				if (UserContext.LocalSite.PrimarySiteStationNUmber == this.SiteCode)
					return true;
				else
					return false;
			}
		}

		public bool IsVerified
		{
			get
			{
				if (this.State == ReportState.Verified)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		/// <summary>
		/// Check to see if the report has a valid template loaded
		/// </summary>
		public bool HasReportContent
		{
			get
			{
				if (this.report != null)
				{
					if (this.report.FieldList != null)
					{
						if (this.report.FieldList.Count > 0)
						{
							return true;
						}
					}
				}

				return false;
			}
		}

		public bool IsCompleted
		{
			get
			{
				if (this.State == ReportState.InProgress)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
		}

		/// <summary>
		/// Indicate whether or not the main report has been modified
		/// </summary>
		public bool IsModified
		{
			get
			{
				// check the change list first since changes in the report header will be updated right away
				if (ChangeList.Fields.Count > 0)
				{
					return true;
				}
				else
				{
					// check the field list next because changes will only be store in changelist when save
					foreach (ReportField field in FieldList)
					{
						if (field.IsFieldDirty())
						{
							return true;
						}
					}

					return false;
				}
			}
		}
		
		#endregion

		#region Report Editor Properties
		
		public IWorkListDataSource DataSource { get; set; }

		public string ReportTitle
		{
			get
			{
				string Title = SiteAbbr + " " + AccessionNumber;
				if (this.State == ReportState.InProgress)
					Title += " - In Progress";
				else if (State == ReportState.Pending)
					Title += " - Pending Verification";
				else
					Title += " - Released on " + ReleasedDate + " by " + ReleasedBy;

				return Title;
			}
		}

		public bool CanUserSeeVerifyButton
		{
			get
			{
				// consultation site will never see this button
				if (this.CurrentSiteType == ReadingSiteType.consultation)
					return false;

				// local read
				bool result = false;
			   
				// first check to see if the user holds the required key
				bool userHasVerifyKey = UserContext.UserHasKey(this.SiteCode, "LRVERIFY");
				if (!userHasVerifyKey)
					result = false;
				else
				{
					// if the user has the key, check for esignature status
					if (this.EsigStatus == null)
						result = false;
					else
					{
						//bool result = false;
						switch (this.EsigStatus.Status)
						{
							case ESigNeedType.not_enabled:
							case ESigNeedType.authorized_needs_signature:
								result = true;
								break;
							case ESigNeedType.not_authorized:
							case ESigNeedType.error:
								result = false;
								break;
							default:
								result = false;
								break;
						}
						//return result;
					}
				}

				if (UserContext.LocalSite.PrimarySiteStationNUmber != this.SiteCode)
				{
					// remote read require remote site keys and esig
					bool userHasLocalVerifyKey = UserContext.UserHasKey(UserContext.LocalSite.PrimarySiteStationNUmber, "LRVERIFY");
					if (!userHasLocalVerifyKey)
						result = false;
					else
					{
						// if the user has the key, check for esignature status
						if (this.LocalEsigStatus == null)
							result = false;
						else
						{
							//bool result = false;
							switch (this.LocalEsigStatus.Status)
							{
								case ESigNeedType.not_enabled:
								case ESigNeedType.authorized_needs_signature:
									result = true;
									break;
								case ESigNeedType.not_authorized:
								case ESigNeedType.error:
									result = false;
									break;
								default:
									result = false;
									break;
							}
							//return result;
						}
					}
				}
				return result;
			}
		}

		/// <summary>
		/// Gets the template for the report
		/// </summary>
		public ReportTemplate RepTemplate
		{
			get
			{
				return report.ReportTemplate;
			}
		}

		public PatientContextState CCOWContextState { get; set; }

		public CaseConsultationList ConsultationList { get; set; }

		/// <summary>
		/// Gets or sets the list of field for the report
		/// </summary>
		public ObservableCollection<ReportField> FieldList
		{
			get
			{
				return report.FieldList;
			}
		}

		public ReportCodingViewModel ReportCodingVM { get; set; }


		#endregion

		#region Helper Functions
		private void SetMainReportReadOnly()
		{
			if ((this.HasReportContent) && (report != null))
			{
				if ((this.IsEditorReadOnly) ||
				   (this.CurrentSiteType == ReadingSiteType.consultation))
				{
					report.SetReadOnly(true);
				}
			}

			// snomed is also readonly
			//if (this.ReportCodingVM != null)
			//{
			//    this.ReportCodingVM.CanUserModifySnomedCode = !ro;
			//}
		}

		void contextManager_PropertyChanged(object sender, PropertyChangedEventArgs e)
		{
			if (e.PropertyName == "ContextState")
			{
				IContextManager contextManager = ViewModelLocator.ContextManager;
				this.CCOWContextState = contextManager.GetPatientContextState(this.Patient);
			}
		}

		private void GetESignatureStatus()
		{
			if (this.CurrentSiteType == ReadingSiteType.consultation)
			{
				this.EsigStatus = new PathologyElectronicSignatureNeedType() { Status = ESigNeedType.not_authorized, Message = "Consulting site cannot verify main report." };
				return;
			}

			// first check to see if the user has already got the status for this type of report yet
			AccessionNumber acNum = new AccessionNumber(this.AccessionNumber);
			string eSigKey = this.SiteCode + "^" + acNum.Type;

			if (UserContext.UserCredentials.UserHasESignatureStatus(eSigKey))
				this.EsigStatus = UserContext.UserCredentials.GetESignatureStatusAtSite(eSigKey);
			else
			{
				// retrieve the Esignature status from database
				this.EsigStatus = DataSource.GetESignatureStatus(this.SiteCode, this.AccessionNumber);

				// update to the list
				UserContext.UserCredentials.ESignatureStatuses.Add(eSigKey, this.EsigStatus);
			}

			// if the interpretation site is remote then it also need local esignature status
			if (UserContext.LocalSite.PrimarySiteStationNUmber != this.SiteCode)
			{
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

		private bool ContainsIllegalCharacters()
		{
			foreach (ReportField field in FieldList)
			{
				if (field.StringValue.Contains('^'))
					return true;
			}

			return false;
		}

		private bool AreRequiredFieldsCompleted()
		{
			// check if all required fields have been filled out yet
			foreach (ReportField repfield in FieldList)
			{
				if ((repfield.IsRequired) && (repfield.StringValue == string.Empty))
					return false;
			}
			return true;
		}

		private void UpdateChangeList(string field, List<string> val)
		{
			if ((string.IsNullOrEmpty(field)) || (val == null))
			{
				return;
			}

			// look for the field, if it's already in the list then update new value, if not then add to the list
			//int id = ChangeList.Fields.FindIndex(repData => repData.FieldNumber == field);
			var id = ChangeList.Fields.Where(repData => repData.FieldNumber == field).FirstOrDefault();
			if (id != null)
			{
				//ChangeList.Fields[id].FieldValue = val;
				id.FieldValue = val;
			}
			else
			{
				ChangeList.Fields.Add(new PathologyCaseReportField() { FieldNumber = field, FieldValue = val });
			}
		}

		private void UpdateChangeList(string field, string val)
		{
			if ((string.IsNullOrEmpty(field)) || (string.IsNullOrEmpty(val)))
			{
				return;
			}

			// look for the field, if it's already in the list then update new value, if not then add to the list
			//int id = ChangeList.Fields.FindIndex(repData => repData.FieldNumber == field);
			var id = ChangeList.Fields.Where(repData => repData.FieldNumber == field).FirstOrDefault();

			List<string> valList = new List<string>();
			valList.Add(val);

			if (id != null)
			{
				//ChangeList.Fields[id].FieldValue = valList;
				id.FieldValue = valList;
			}
			else
			{
				ChangeList.Fields.Add(new PathologyCaseReportField() { FieldNumber = field, FieldValue = valList });
			}
		}

		private void SaveReportToDatabase()
		{
			// check the length of specimen
			var SpecField = FieldList.Where(f => f.FieldNumber == ".012").FirstOrDefault();
			if (SpecField != null)
			{
				if (SpecField.IsFieldDirty())
				{
					List<string> vals = SpecField.GetListContent();
					foreach (string val in vals)
					{
						if (val.Length >= 75)
						{
							MessageBox.Show("Specimen must be less than 75 characters.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
							return;
						}
					}
				}
			}

			// check for illegal characters 
			if (ContainsIllegalCharacters())
			{
				MessageBox.Show("Please remove ^ from your data.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}

			// go through each field in the field list and update if they are modified
			foreach (ReportField field in FieldList)
			{
				if (field.IsFieldDirty())
				{
					UpdateChangeList(field.FieldNumber, field.GetListContent());
				}
			}

			// only call the rpc if there something to change
			if ((ChangeList == null) || (ChangeList.Fields == null) || (ChangeList.Fields.Count == 0))
			{
				MessageBox.Show("There are no changes to be saved.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
			}
			else
			{
				// separate the save of path/resident/practitioner from the rest 
				var PathField = ChangeList.Fields.Where(pf => pf.FieldNumber == ".02").FirstOrDefault();
				var ResField = ChangeList.Fields.Where(pf => pf.FieldNumber == ".021").FirstOrDefault();
				var PracField = ChangeList.Fields.Where(pf => pf.FieldNumber == ".07").FirstOrDefault();
				PathologyCaseReportFieldsType headerChanges = new PathologyCaseReportFieldsType();

				if (PathField != null)
				{
					headerChanges.Fields.Add(PathField);
					ChangeList.Fields.Remove(PathField);
				}
				if (ResField != null)
				{
					headerChanges.Fields.Add(ResField);
					ChangeList.Fields.Remove(ResField);
				}
				if (PracField != null)
				{
					headerChanges.Fields.Add(PracField);
					ChangeList.Fields.Remove(PracField);
				}
				try
				{
					// try to save the headers first
					if (headerChanges.Fields.Count > 0)
					{
						DataSource.SaveReportChanges(this.CaseURN, headerChanges);

						// save is successful so update the records before and current values
						foreach (ReportField field in FieldList)
						{
							if (((PathField != null) && (field.FieldNumber == PathField.FieldNumber)) ||
								((ResField != null) && (field.FieldNumber == ResField.FieldNumber)) ||
								((PracField != null) && (field.FieldNumber == PracField.FieldNumber)))
							{
								field.SaveValue();
							}
						}
					}
				}
				catch (Exception ex1)
				{
					// readd the changes if not saved yet
					if (PathField != null)
						ChangeList.Fields.Add(PathField);
					if (ResField != null)
						ChangeList.Fields.Add(ResField);
					if (PracField != null)
						ChangeList.Fields.Add(PracField);

					Log.Error("Could not save changes to the users in the report header.", ex1);
					throw;
				}

				try
				{
					// try to save the body of the main report
					if (ChangeList.Fields.Count > 0)
						this.saveResult = DataSource.SaveReportChanges(this.CaseURN, this.ChangeList);

					// save is successful so update the records before and current values
					foreach (ReportField field in FieldList)
					{
						field.SaveValue();
					}

					// clear the change list
					ChangeList.Fields.Clear();
				}
				catch (Exception ex2)
				{
					Log.Error("Failed to save report data.", ex2);
					throw;
				}
			}
		}
		#endregion
	   
		public void SearchUser(string parameter)
		{
			SelectUserView searchUser = new SelectUserView(this.DataSource, this.SiteCode);
			searchUser.ShowDialog();

			if ((!searchUser.ModalResult) || (searchUser.SelectedUser == null))
			{
				// if the user cancelled the search, then leave the field as is
				return;
			}

			// if there is new value then update the value
			if (parameter == "Pathologist")
			{
				this.report.Pathologist = searchUser.SelectedUser.FieldName;
				UpdateChangeList(".02", searchUser.SelectedUser.FieldName);
				RaisePropertyChanged("Pathologist");
			}
			else if (parameter == "Resident")
			{
				this.report.Resident = searchUser.SelectedUser.FieldName;
				UpdateChangeList(".021", searchUser.SelectedUser.FieldName);
				RaisePropertyChanged("Resident");
			}
			else if (parameter == "Practitioner")
			{
				this.report.Practitioner = searchUser.SelectedUser.FieldName;
				UpdateChangeList(".07", searchUser.SelectedUser.FieldName);
				RaisePropertyChanged("Practitioner");
			}
		}

		public void LaunchCPRSReport()
		{
			// retrieve the released report from VistA
			string releasedReport = string.Empty;
			if (DataSource != null)
				releasedReport = DataSource.GetCPRSReport(this.CaseURN);

			// open new window
			ReleasedCPRSReportView view = new ReleasedCPRSReportView();
			view.Title = SiteAbbr + " " + AccessionNumber;
			view.ReleasedReport = releasedReport;
			view.ShowDialog();

            Log.Info("View released report for case " + this.AccessionNumber + " at site " + this.SiteCode);
		}

		public void SaveMainReportToDatabase()
		{
			try
			{
				SaveReportToDatabase();
                Log.Info(string.Format("Saved changes to the report of {1} at {0}.", this.SiteCode, this.AccessionNumber));
			}
			catch (Exception)
			{
				string message = "Failed to save the main report data to database.";
				MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
			}
		}

		public void CompleteReport()
		{
			// check for illegal characters 
			if (ContainsIllegalCharacters())
			{
				MessageBox.Show("Please remove ^ from your inputs.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}

			//check for required fields
			if (!AreRequiredFieldsCompleted())
			{
				MessageBox.Show("Required fields (*) must not be left empty.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}

			// check for modify report
			if (this.IsModified)
			{
				MessageBox.Show("Main report contains unsaved changes. Please save the changes first.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}

			// only complete if the report is in progress
			if (this.State == ReportState.InProgress)
			{
				// set the completion date
				DateTime completeDate = DateTime.Now;
				string format = "MM/dd/yy";
				DateCompleted = completeDate.ToString(format);
				UpdateChangeList(".03", DateCompleted);
				try
				{
					// save the completion date
					SaveReportToDatabase();
					report.ChangeState(ReportState.Pending);

                    // get the complete time from DB
                    DateCompleted = DataSource.GetReportFieldData(".03", this.CaseURN);
                    RaisePropertyChanged("DateCompleted");

                    RaisePropertyChanged("ReportTitle");

                    Log.Info(string.Format("Completed the report of {1} at {0}.", this.SiteCode, this.AccessionNumber));
				}
				catch (Exception)
				{
					string message = "Failed to complete the main report.";
					//Log.Error(message, ex);
					MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
				}
			}
			else
			{
				MessageBox.Show("You can only complete the main report if it's in progress.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
			}
		}

		public void VerifyReport()
		{
			// check for illegal characters 
			if (ContainsIllegalCharacters())
			{
				MessageBox.Show("Please remove ^ from your inputs.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}

			// check for required fields
			if (!AreRequiredFieldsCompleted())
			{
				MessageBox.Show("Required fields (*) must not be left empty.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}

			// check for changes
			if (this.IsModified)
			{
				MessageBox.Show("Main report contains unsaved changes. Please save the changes first.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}

			// All consultations must not be pending
			if (this.ConsultationList != null)
			{
				if (this.ConsultationList.ConsultationList != null)
				{
					// go through the consultation list and check if there are any pending consultation entry
					foreach (CaseConsultation con in this.ConsultationList.ConsultationList)
					{
						if (con.Type == "CONSULTATION")
						{
							if (con.Status == "PENDING")
							{
								MessageBox.Show("There are pending consultations for this case." + Environment.NewLine +
												"Report cannot be verified before all consultations has been completed.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
								return;
							}
						}
					}
				}
			}

			// All supplemetary at this time must be verified first
			if (SRViewModel != null)
			{
				if (!SRViewModel.IsAllSRVerified)
				{
					MessageBox.Show("All supplementary reports must be verified before verifying the main report.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
					return;
				}
			}

			// can only verify if the report is pending
			if (this.State == ReportState.Pending)
			{
				bool CanVerify = true;

				// ask for E-signature if it's enabled
				if ((CanVerify) && (this.EsigStatus.Status == ESigNeedType.authorized_needs_signature))
				{
                    if (!this.EsigStatus.IsSigned)
                    {
                        EverifyView eview = new EverifyView(DataSource, this.SiteCode);
                        eview.ShowDialog();
                        CanVerify = eview.Success;
                        if (CanVerify)
                        {
                            this.EsigStatus.IsSigned = true;
                        }
                    }
				}

				if (CanVerify)
				{
					// set the verifying date time and the verifier
					DateTime verifyDate = DateTime.Now;
					string format = "MM/dd/yy@HH:mm";
					this.ReleasedDate = verifyDate.ToString(format);
					this.ReleasedBy = UserContext.UserCredentials.Fullname;

					UpdateChangeList(".11", this.ReleasedDate);
					UpdateChangeList(".13", this.ReleasedBy);

					try
					{
						// try to save the verification info to the DB
						SaveReportToDatabase();
                        if ((this.saveResult != null) && (this.saveResult.Released))
                        {
                            report.ChangeState(ReportState.Verified);

                            this.ReleasedDate = DataSource.GetReportFieldData(".11", this.CaseURN);
                            RaisePropertyChanged("ReleasedDate");
                            RaisePropertyChanged("ReportTitle");

                            Log.Info(string.Format("Verified and released the report of {1} at {0}.", this.SiteCode, this.AccessionNumber));
                        }
                        else
                            throw new Exception("Failed to verify main report.");
					}
					catch (Exception)
					{
						// clear the change list because we dont want unusable date time
						ChangeList.Fields.Clear();
						string message = "Failed to verify the main report.";
						//Log.Error(message, ex);
						MessageBox.Show(message, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
						return;
					}

					// if the site is primary read but not a local site then create a duplicate
					if ((this.CurrentSiteType == ReadingSiteType.interpretation) &&
						(UserContext.LocalSite.PrimarySiteStationNUmber != this.SiteCode))
					{
						// ask for E-signature if it's enabled
						bool canMakeCopy = false;
						if (this.LocalEsigStatus.Status == ESigNeedType.authorized_needs_signature)
						{
							EverifyView localEView = new EverifyView(DataSource, UserContext.LocalSite.PrimarySiteStationNUmber);
							localEView.ShowDialog();
							canMakeCopy = localEView.Success;
						}

						if (!canMakeCopy)
						{
							string message = "You failed to enter your electronic signature for " + UserContext.LocalSite.SiteAbbreviation + "." + Environment.NewLine +
											 "Without a correct e-signature, a reference report cannot be generated.";
							MessageBox.Show(message, "Information", MessageBoxButton.OK, MessageBoxImage.Information);                            
						}
						else
						{
							string copyCaseAccession = DataSource.CreateCopyCase(UserContext.LocalSite.PrimarySiteStationNUmber, this.CaseURN);
							if (!string.IsNullOrWhiteSpace(copyCaseAccession))
							{
                                string[] accPieces = copyCaseAccession.Split('^');
                                if ((accPieces != null) && (accPieces.Length == 2))
                                {
                                    MessageBox.Show("A reference record has been created for this report.\r\nThe reference case is: " + UserContext.LocalSite.SiteAbbreviation + " " + accPieces[0],
                                                    "Information", MessageBoxButton.OK, MessageBoxImage.Information);

                                    Log.Info(string.Format("Local reference report {0} created at site {1}",
                                                           accPieces[0], UserContext.LocalSite.PrimarySiteStationNUmber));
                                }
							}
							else
							{
								MessageBox.Show("A reference report couldn't be generated. Please note down the information for this case and contact help.", "Information",
												MessageBoxButton.OK, MessageBoxImage.Information);
							}
						}
					}

                    //string msg = this.saveResult.Message + ".\nDo you want to send to additional recipients or mailgroups?";
                    string msg = "An alert has been sent to " + Practitioner + ".\nDo you want to send to additional recipients or mailgroups?";
                    MessageBoxResult result = MessageBox.Show(msg, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
					if (result == MessageBoxResult.Yes)
					{
						try
						{
							string subject = "The report for " + AccessionNumber + " has been verified.";
							Process.Start("mailto:?subject=" + subject);
						}
						catch (Exception ex1)
						{
							MessageBox.Show("Email client could not be initiated.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
							Log.Error("Failed to start email client process.", ex1);
						}
					}
				}
			}
			else
			{
				MessageBox.Show("You can only verify the main report if it's pending for verification.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
			}
		}
	}
}
