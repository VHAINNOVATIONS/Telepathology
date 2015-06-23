namespace VistA.Imaging.Telepathology.Worklist.ViewModel
{
	using System;
	using System.Collections;
	using System.Collections.ObjectModel;
	using System.ComponentModel;
	using System.Windows;
	using GalaSoft.MvvmLight;
	using GalaSoft.MvvmLight.Command;
	using VistA.Imaging.Telepathology.Common.Exceptions;
	using VistA.Imaging.Telepathology.Common.Model;
	using VistA.Imaging.Telepathology.Common.VixModels;
	using VistA.Imaging.Telepathology.Logging;
	using VistA.Imaging.Telepathology.Worklist.DataSource;

	public class TreeGroup : INotifyPropertyChanged
	{
		/// <remarks>
		/// The PropertyChanged event is raised by NotifyPropertyWeaver (http://code.google.com/p/notifypropertyweaver/)
		/// </remarks>
		/// <summary>
		/// Event to be raised when a property is changed
		/// </summary>
#pragma warning disable 0067
		// Warning disabled because the event is raised by NotifyPropertyWeaver (http://code.google.com/p/notifypropertyweaver/)
		public event PropertyChangedEventHandler PropertyChanged;
#pragma warning restore 0067

		public string GroupName { get; set; }

		public IEnumerable Items { get; set; }
	}

	public class ReportCodingViewModel : ViewModelBase
	{
		private static MagLogger Log = new MagLogger(typeof(ReportCodingViewModel));

         //<summary>
         //Initializes a new instance of the CaseOrganTissueViewModel class.
         //</summary>
		public ReportCodingViewModel()
		{
		    this.IsEditorReadOnly = true;
		    this.CurrentSiteType = ReadingSiteType.interpretation;

		    COT = new CaseOrganTissue();
		    CPTList = new PathologyCptCodesType();
		    this.SearchItems = new ObservableCollection<PathologyFieldValue>();
		    this.SearchLocationItems = new ObservableCollection<PathologyFieldValue>();

		    this.SearchSnomedItemCommand = new RelayCommand(SearchSnomedItem, () => this.CanSearchSnomedItem);
		    this.AddSnomedItemCommand = new RelayCommand(AddSnomedItem, () => this.CanAddSnomedItem);
		    this.RemoveSnomedItemCommand = new RelayCommand(RemoveSnomedItem, () => this.CanRemoveSnomedItem);

		    this.SearchLocationItemCommand = new RelayCommand(SearchLocationItem, () => this.CanSearchLocationItem);
		    this.AddCPTCommand = new RelayCommand(AddCPTItems, () => this.CanAddCPT);
		}

		public ReportCodingViewModel(IWorkListDataSource datasource, string caseURN, string siteNumber, bool isGlobalReadOnly = false, ReadingSiteType siteType = ReadingSiteType.interpretation)
		{
			this.IsEditorReadOnly = isGlobalReadOnly;
			this.CurrentSiteType = siteType;

			this.DataSource = datasource;
			this.CaseURN = caseURN;
			this.SiteStationNumber = siteNumber;

			COT = new CaseOrganTissue();
			CPTList = new PathologyCptCodesType();
			
			PathologySnomedCodesType snomedList = this.DataSource.GetSnomedCodeForCase(this.CaseURN);
			COT.InitializeList(snomedList);
			CPTList = this.DataSource.GetCptCodesForCase(this.CaseURN);
            
            this.SupportCPT = true;
            if (CPTList == null)
            {
                this.SupportCPT = false;
                MessageBox.Show("The current case does not support CPT coding.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                CPTList = new PathologyCptCodesType();
            }

			this.SearchItems = new ObservableCollection<PathologyFieldValue>();
			this.SearchLocationItems = new ObservableCollection<PathologyFieldValue>();

			// enable adding new organ intially
			this.SelectedItemType = "Organ/Tissue";
			this.SelectedSearchItem = null;

			this.SearchSnomedItemCommand = new RelayCommand(SearchSnomedItem, () => this.CanSearchSnomedItem);
			this.AddSnomedItemCommand = new RelayCommand(AddSnomedItem, () => this.CanAddSnomedItem);
			this.RemoveSnomedItemCommand = new RelayCommand(RemoveSnomedItem, () => this.CanRemoveSnomedItem);

			this.SearchLocationItemCommand = new RelayCommand(SearchLocationItem, () => this.CanSearchLocationItem );
			this.AddCPTCommand = new RelayCommand(AddCPTItems, () => this.CanAddCPT );
		}

		#region Commands
		public RelayCommand SearchSnomedItemCommand
		{
			get;
			private set;
		}

		public bool CanSearchSnomedItem
		{
			get
			{
				if ((this.CanUserModifySnomedCoding) && (this.IsSearchable))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}

		public RelayCommand AddSnomedItemCommand
		{
			get;
			private set;
		}

		public bool CanAddSnomedItem
		{
			get
			{
				if ((this.CanUserModifySnomedCoding) && (this.SelectedSearchItem != null))
					return true;
				else
					return false;
			}
		}

		public RelayCommand RemoveSnomedItemCommand
		{
			get;
			private set;
		}

		public bool CanRemoveSnomedItem
		{
			get
			{
				if ((this.CanUserModifySnomedCoding) && (this.SnomedTreeSelectedItem != null))
					return true;
				else
					return false;
			}
		}
		
		public RelayCommand SearchLocationItemCommand
		{
			get;
			private set;
		}

		public bool CanSearchLocationItem
		{
			get
			{
				if ((this.CanUserModifyCPTCoding) && (this.IsLocationSearchable))
					return true;
				else
					return false;
			}
		}

		public RelayCommand AddCPTCommand
		{
			get;
			private set;
		}

		public bool CanAddCPT
		{
			get
			{
				if ((this.CanUserModifyCPTCoding) && (this.SelectedSearchLocation != null) && (!string.IsNullOrEmpty(this.CPTText)))
					return true;
				else
					return false;
			}
		}
		#endregion

		#region Report Coding Properties
		public IWorkListDataSource DataSource { get; set; }

		public string CaseURN { get; set; }

		public string SiteStationNumber { get; set; }

		public ReadingSiteType CurrentSiteType { get; set; }

		public bool IsEditorReadOnly { get; set; }

		public bool CanUserModifySnomedCoding
		{
			get
			{
				if ((this.IsEditorReadOnly) || (this.CurrentSiteType == ReadingSiteType.consultation))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
		}

		public bool CanUserModifyCPTCoding
		{
			get
			{
				if ((this.IsEditorReadOnly) || (this.CurrentSiteType == ReadingSiteType.consultation) ||
					(UserContext.LocalSite.PrimarySiteStationNUmber != this.SiteStationNumber) || (!this.SupportCPT))
				{
					return false;
				}
				else
				{
					return true;
				}
			}
		}

        public bool SupportCPT { get; set; }

        public CaseOrganTissue COT { get; set; }

        public PathologyCptCodesType CPTList { get; set; }

        public string SelectedItemType { get; set; }

        public object SnomedTreeSelectedItem { get; set; }

        public object SnomedTreeSelectedItemParent { get; set; }

        public string SnomedSearchText { get; set; }

        public string LocationSearchText { get; set; }

        public bool IsSearchable
        {
            get
            {
                if ((!string.IsNullOrEmpty(this.SnomedSearchText)) && (!string.IsNullOrEmpty(this.SelectedItemType)))
                {
                    if (this.SnomedSearchText.Length >= 2)
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        public bool IsLocationSearchable
        {
            get
            {
                if (!string.IsNullOrEmpty(this.LocationSearchText))
                {
                    if (this.LocationSearchText.Length >= 2)
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        public string CPTText { get; set; }

        public ObservableCollection<PathologyFieldValue> SearchItems { get; set; }

        public ObservableCollection<PathologyFieldValue> SearchLocationItems { get; set; }

        public PathologyFieldValue SelectedSearchItem { get; set; }

        public PathologyFieldValue SelectedSearchLocation { get; set; }

		#endregion

        #region SNOMED Functions
        
        private void SearchSnomedItem()
        {
            // get the search type
            string type = string.Empty;
            if (this.SelectedItemType == "Organ/Tissue")
            {
                type = "topography";
            }
            else if (this.SelectedItemType == "Morphology")
            {
                type = "morphology";
            }
            else if (this.SelectedItemType == "Etiology")
            {
                type = "etiology";
            }
            else if (this.SelectedItemType == "Function")
            {
                type = "function";
            }
            else if (this.SelectedItemType == "Procedure")
            {
                type = "procedure";
            }
            else if (this.SelectedItemType == "Disease")
            {
                type = "disease";
            }

            // get search item
            this.SelectedSearchItem = null;
            PathologyFieldValuesType searchItems = DataSource.SearchPathologyItems(this.SiteStationNumber, type, this.SnomedSearchText);
            this.SearchItems = searchItems.FieldList;
            if (searchItems.FieldList.Count == 0)
            {
                MessageBox.Show("Could not find any code that matches the description.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }
            this.SelectedSearchItem = this.SearchItems[0];
        }

        private void AddSnomedItem()
        {
            // can only add if there is a type and an valid item selected
            if ((!string.IsNullOrEmpty(this.SelectedItemType)) && (this.SearchItems != null) && (this.SelectedSearchItem != null))
            {
                PathologyFieldValue addingField = this.SelectedSearchItem;

                if (this.SelectedItemType == "Organ/Tissue")
                {
                    // add the organ tissue to the database first to retrieve the organID
                    string organID = DataSource.AddSnomedOrganTissue(this.CaseURN, addingField.FieldURN);
                    if (string.IsNullOrWhiteSpace(organID))
                    {
                        MessageBox.Show("Failed to add new organ/tissue.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                        return;
                    }

                    // add new organ/tissue to the CaseOrganTissue List
                    this.COT.AddOrgan(addingField.FieldDescription, addingField.FieldCode, organID);
                }
                else
                {
                    if (this.SnomedTreeSelectedItemParent != null)
                    {
                        if (this.SelectedItemType == "Etiology")
                        {
                            string organID = ((SnomedMorphology)this.SnomedTreeSelectedItemParent).OrganID;
                            string morphID = ((SnomedMorphology)this.SnomedTreeSelectedItemParent).MorphologyID;
                            string itemID = DataSource.AddSnomedEtiologyToMorphology(this.CaseURN, organID, morphID, addingField.FieldURN);
                            if (string.IsNullOrWhiteSpace(itemID))
                            {
                                MessageBox.Show("Failed to save SNOMED item.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                                return;
                            }

                            // add new etiology to existing morphology
                            ((SnomedMorphology)this.SnomedTreeSelectedItemParent).AddEtiology(addingField.FieldDescription, addingField.FieldCode, itemID);
                        }
                        else
                        {
                            string organID = ((SnomedOrganTissue)this.SnomedTreeSelectedItemParent).OrganID;
                            // add the snomed item to the database first to retrieve the itemID
                            string itemID = DataSource.AddSnomedItemToOrganTissue(this.CaseURN, organID, addingField.FieldURN);
                            if (string.IsNullOrWhiteSpace(itemID))
                            {
                                MessageBox.Show("Failed to save SNOMED item.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
                                return;
                            }

                            if (this.SelectedItemType == "Morphology")
                            {
                                // add new morphology to existing organ
                                ((SnomedOrganTissue)this.SnomedTreeSelectedItemParent).AddMorphology(addingField.FieldDescription, addingField.FieldCode, itemID);
                            }
                            else if (this.SelectedItemType == "Function")
                            {
                                // add new function to existing organ
                                ((SnomedOrganTissue)this.SnomedTreeSelectedItemParent).AddFunction(addingField.FieldDescription, addingField.FieldCode, itemID);
                            }
                            else if (this.SelectedItemType == "Procedure")
                            {
                                // add new procedure to existing organ
                                ((SnomedOrganTissue)this.SnomedTreeSelectedItemParent).AddProcedure(addingField.FieldDescription, addingField.FieldCode, itemID);
                            }
                            else if (this.SelectedItemType == "Disease")
                            {
                                // add new disease to existing organ
                                ((SnomedOrganTissue)this.SnomedTreeSelectedItemParent).AddDisease(addingField.FieldDescription, addingField.FieldCode, itemID);
                            }
                        }
                    }
                }

                Log.Info("Added new SNOMED code for a case.");
            }
        }

        private void RemoveSnomed(object snomedItem)
        {
            try
            {
                if (snomedItem != null)
                {
                    // check what type of item is the selected item
                    if (snomedItem is SnomedEtiology)
                    {
                        // removing individual etiology
                        SnomedEtiology eti = snomedItem as SnomedEtiology;
                        this.DataSource.RemoveSnomedEtiology(this.CaseURN, eti.OrganID, eti.MorphologyID, eti.EtiologyID);
                        this.COT.RemoveEtiology(eti);
                    }
                    else if (snomedItem is SnomedMorphology)
                    {
                        // removing individual morphology which will also remove all its etiology
                        SnomedMorphology morph = snomedItem as SnomedMorphology;
                        this.DataSource.RemoveSnomedField(this.CaseURN, morph.OrganID, morph.MorphologyID, "morphology");
                        this.COT.RemoveMorphology(morph);
                    }
                    else if (snomedItem is SnomedFunction)
                    {
                        // removing individual function
                        SnomedFunction func = snomedItem as SnomedFunction;
                        this.DataSource.RemoveSnomedField(this.CaseURN, func.OrganID, func.FunctionID, "function");
                        this.COT.RemoveFunction(func);
                    }
                    else if (snomedItem is SnomedProcedure)
                    {
                        // removing individual procedure
                        SnomedProcedure proc = snomedItem as SnomedProcedure;
                        this.DataSource.RemoveSnomedField(this.CaseURN, proc.OrganID, proc.ProcedureID, "procedure");
                        this.COT.RemoveProcedure(proc);
                    }
                    else if (snomedItem is SnomedDisease)
                    {
                        // removing individual disease
                        SnomedDisease disease = snomedItem as SnomedDisease;
                        this.DataSource.RemoveSnomedField(this.CaseURN, disease.OrganID, disease.DiseaseID, "disease");
                        this.COT.RemoveDisease(disease);
                    }
                    else if (snomedItem is SnomedOrganTissue)
                    {
                        // removing individual organ tissue and all its children
                        SnomedOrganTissue organ = snomedItem as SnomedOrganTissue;
                        this.DataSource.RemoveSnomedOrganTissue(this.CaseURN, organ.OrganID);
                        this.COT.RemoveOrganTissue(organ);

                        // reset the selection
                        SelectedItemType = "Organ/Tissue";
                        SnomedSearchText = string.Empty;
                        SearchItems.Clear();
                        SelectedSearchItem = null;
                        //CanAddSnomedItem = true;
                    }

                    Log.Info("Removed SNOMED code from a case.");
                }
            }
            catch (MagVixFailureException vfe)
            {
                Log.Error("Failed to remove snomed item.", vfe);
                MessageBox.Show("Could not remove selected SNOMED item.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            catch (Exception ex)
            {
                Log.Error("Unknown error removing snomed item.", ex);
                MessageBox.Show("Could not remove selected SNOMED item.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void RemoveSnomedItem()
        {
            if (this.SnomedTreeSelectedItem != null)
            {
                // if the selected item is not one of the category item
                if (!(this.SnomedTreeSelectedItem is TreeGroup))
                {
                    string message = "Are you sure you want to remove the selected SNOMED item?";
                    MessageBoxResult result = MessageBox.Show(message, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                    if (result == MessageBoxResult.Yes)
                    {
                        RemoveSnomed(this.SnomedTreeSelectedItem);
                    }
                }
                else if ((this.SnomedTreeSelectedItemParent != null) && (!string.IsNullOrWhiteSpace(this.SelectedItemType)))
                {
                    // if the selected item is a category item, check the selected type and delete from the parent item
                    string message = "Are you sure you want to remove all the SNOMED items in this group?";

                    if (this.SelectedItemType == "Etiology")
                    {
                        // delete all the etiologies items of the parent morphology
                        SnomedMorphology morph = this.SnomedTreeSelectedItemParent as SnomedMorphology;
                        if ((morph != null) && (morph.Etiologies != null))
                        {
                            if (morph.Etiologies.Count == 0)
                            {
                                MessageBox.Show("There is nothing in this etiology group to delete.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                                return;
                            }

                            MessageBoxResult result = MessageBox.Show(message, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                            if (result == MessageBoxResult.Yes)
                            {
                                while (morph.Etiologies.Count != 0)
                                {
                                    RemoveSnomed(morph.Etiologies[0]);
                                }

                                if (morph.Etiologies.Count != 0)
                                {
                                    Log.Info("Some etiologies could not be deleted.");
                                }
                            }
                        }
                    }
                    else if (this.SelectedItemType == "Morphology")
                    {
                        // delete all the morphologies of the parent organ/tissue
                        SnomedOrganTissue organ = this.SnomedTreeSelectedItemParent as SnomedOrganTissue;
                        if ((organ != null) && (organ.Morphologies != null))
                        {
                            if (organ.Morphologies.Count == 0)
                            {
                                MessageBox.Show("There is nothing in this morphology group to delete.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                                return;
                            }

                            MessageBoxResult result = MessageBox.Show(message, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                            if (result == MessageBoxResult.Yes)
                            {
                                while (organ.Morphologies.Count != 0)
                                {
                                    RemoveSnomed(organ.Morphologies[0]);
                                }

                                if (organ.Morphologies.Count != 0)
                                {
                                    Log.Info("Some morphologies could not be deleted.");
                                }
                            }
                        }
                    }
                    else if (this.SelectedItemType == "Function")
                    {
                        // delete all the functions of the parent organ/tissue
                        SnomedOrganTissue organ = this.SnomedTreeSelectedItemParent as SnomedOrganTissue;
                        if ((organ != null) && (organ.Functions != null))
                        {
                            if (organ.Functions.Count == 0)
                            {
                                MessageBox.Show("There is nothing in this function group to delete.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                                return;
                            }

                            MessageBoxResult result = MessageBox.Show(message, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                            if (result == MessageBoxResult.Yes)
                            {
                                while (organ.Functions.Count != 0)
                                {
                                    RemoveSnomed(organ.Functions[0]);
                                }

                                if (organ.Functions.Count != 0)
                                {
                                    Log.Info("Some functions could not be deleted.");
                                }
                            }
                        }
                    }
                    else if (this.SelectedItemType == "Procedure")
                    {
                        // delete all the procedures of the parent organ/tissue
                        SnomedOrganTissue organ = this.SnomedTreeSelectedItemParent as SnomedOrganTissue;
                        if ((organ != null) && (organ.Procedures != null))
                        {
                            if (organ.Procedures.Count == 0)
                            {
                                MessageBox.Show("There is nothing in this procedure group to delete.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                                return;
                            }

                            MessageBoxResult result = MessageBox.Show(message, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                            if (result == MessageBoxResult.Yes)
                            {
                                while (organ.Procedures.Count != 0)
                                {
                                    RemoveSnomed(organ.Procedures[0]);
                                }

                                if (organ.Procedures.Count != 0)
                                {
                                    Log.Info("Some procedures could not be deleted.");
                                }
                            }
                        }
                    }
                    else if (this.SelectedItemType == "Disease")
                    {
                        // delete all the diseases of the parent organ/tissue
                        SnomedOrganTissue organ = this.SnomedTreeSelectedItemParent as SnomedOrganTissue;
                        if ((organ != null) && (organ.Diseases != null))
                        {
                            if (organ.Diseases.Count == 0)
                            {
                                MessageBox.Show("There is nothing in this disease group to delete.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                                return;
                            }

                            MessageBoxResult result = MessageBox.Show(message, "Confirmation", MessageBoxButton.YesNo, MessageBoxImage.Question, MessageBoxResult.No);
                            if (result == MessageBoxResult.Yes)
                            {
                                while (organ.Diseases.Count != 0)
                                {
                                    RemoveSnomed(organ.Diseases[0]);
                                }

                                if (organ.Diseases.Count != 0)
                                {
                                    Log.Info("Some diseases could not be deleted.");
                                }
                            }
                        }
                    }
                }
            }
        }
        
        #endregion

        #region CPT Functions

        private void SearchLocationItem()
		{
			this.SelectedSearchLocation = null;
            // the use first selects a Hospital Location 
  			PathologyFieldValuesType searchItems = DataSource.SearchPathologyItems(this.SiteStationNumber, "location", this.LocationSearchText);
			this.SearchLocationItems = searchItems.FieldList;
			if (searchItems.FieldList.Count == 0)
			{
				MessageBox.Show("Could not find any location that matches the description.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
				return;
			}
			this.SelectedSearchLocation = this.SearchLocationItems[0];
		}

		private void AddCPTItems()
		{
			if ((this.DataSource == null) || (string.IsNullOrWhiteSpace(this.CPTText)))
			{
				return;
			}

			// parse the input into codes separated by , 
			string[] groups = this.CPTText.Split(',');
			ObservableCollection<string> cptCodes = new ObservableCollection<string>();
			if (groups != null)
			{
                // each group should now contain either a single code or a combo
				foreach (string group in groups)
				{
					// check if the group contain a multiplier
					if (group.Contains("*"))
					{
						// combo are separated by *
						string[] combo = group.Trim().Split('*');
						if (combo == null)
						{
							MessageBox.Show("Could not parse the CPT combo.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
							return;
						}

						int code;
						int multiplier;
						bool isNum = int.TryParse(combo[0], out code);
						
                        // if the code is not a number or less than 0
                        if ((!isNum) || (code <0))
						{
							MessageBox.Show("CPT code must be numerical.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
							return;
						}

                        // if the multiplier is not a number or less than 0
						isNum = int.TryParse(combo[1], out multiplier);
						if ((!isNum) || (multiplier < 0))
						{
							MessageBox.Show("Multiplier must be numerical.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
							return;
						}

						for (int i = 0; i < multiplier; i++)
						{
							cptCodes.Add(combo[0]);
						}
					}
					else
					{
                        // this should be a single code
						if ((!string.IsNullOrWhiteSpace(group)) && (group.Trim().Length > 0))
						{
							// individual code, check if the code is numeric
							int num;
							bool isNum = int.TryParse(group.Trim(), out num);
							if ((isNum) && (num >= 0))
							{
								cptCodes.Add(group.Trim());
							}
							else
							{
								MessageBox.Show("CPT code must be numerical.", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
								return;
							}
						}
					}
				}

				if ((cptCodes == null) || (cptCodes.Count == 0))
				{
					MessageBox.Show("There are no CPT codes to save", "Information", MessageBoxButton.OK, MessageBoxImage.Information);
					return;
				}

				try
				{
					PathologyCptCodeResultsType addResult = this.DataSource.AddCptCodesForCase(this.CaseURN, SelectedSearchLocation.FieldURN, cptCodes);
					MessageBox.Show(ProcessAddCPTItemsResult(addResult), "Information", MessageBoxButton.OK, MessageBoxImage.Information);
                    Log.Info("Modified CPT codes for a case.");
				}
				catch (MagVixFailureException)
				{
					MessageBox.Show("Failed to save CPT codes", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
				}
			}
		}

		private string ProcessAddCPTItemsResult(PathologyCptCodeResultsType result)
		{
			if ((result != null) && (result.Items != null) && (result.Items.Count > 0))
			{
				string success = string.Empty;
				int successCount = 0;
				string failure = string.Empty;
				int failureCount = 0;

				foreach (CptCodeResult code in result.Items)
				{
					string res = String.Format("{0} {1}", code.CptCode, code.Description);
					if (code.Result)
					{
						// if successfully added, add to the success queue
						successCount++;
						if (!success.Contains(res))
						{
							success += res + Environment.NewLine;
						}
					}
					else
					{
						failureCount++;
						if (!failure.Contains(res))
						{
							failure += res + Environment.NewLine;
						}
					}
				}

				string successMess = "The following CPT code(s) are entered successfully and tracked:\n" + success;
				string failureMess = "Unable to enter the following CPT code(s):\n" + failure;

				if (successCount > 0)
				{
                    this.CPTText = string.Empty;
                    this.LocationSearchText = string.Empty;
                    // this.SearchLocationItems = null; *** preserve previously searched Hosp loc list

					CPTList = this.DataSource.GetCptCodesForCase(this.CaseURN);
                    if (CPTList == null)
                    {
                        Log.Debug("Failed to retrieve CPT list.");
                        CPTList = new PathologyCptCodesType();
                    }
				}

				if (failureCount <= 0)
				{
					// there is no failure
					return successMess;
				}
				else
				{
					// there is no success
					if (successCount <= 0)
					{
						return failureMess;
					}
					else
					{
						return successMess + Environment.NewLine + failureMess;
					}
				}
			}
			else
			{
				return "Nothing in the result.";
			}
        }

        #endregion


    }
}