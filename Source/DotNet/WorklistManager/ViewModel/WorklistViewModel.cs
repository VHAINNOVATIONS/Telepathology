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


namespace VistA.Imaging.Telepathology.Worklist.ViewModel
{
    using System;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    using System.ComponentModel;
    using System.Linq;
    using System.Text;
    using System.Xml.Serialization;
    using GalaSoft.MvvmLight;
    using GalaSoft.MvvmLight.Command;
    using GalaSoft.MvvmLight.Messaging;
    using GalaSoft.MvvmLight.Threading;
    using VistA.Imaging.Telepathology.Common.Model;
    using VistA.Imaging.Telepathology.Logging;
    using VistA.Imaging.Telepathology.Worklist.Controls;
    using VistA.Imaging.Telepathology.Worklist.DataSource;
    using VistA.Imaging.Telepathology.Worklist.Messages;
    using System.Windows;
    using System.Threading.Tasks;
    using System.Windows.Threading;

    public enum ExamListViewType
    {
        Unread,
        Read,
        Patient
    }
    
    public delegate void CasesUpdatedHandler(IEnumerable<CaseListItem> items);

    public class WorklistViewModel : ViewModelBase, Aga.Controls.Tree.ITreeModel
    {
        private static MagLogger Log = new MagLogger(typeof(WorklistViewModel));

        static bool keepCaseOn = true;

        public const string DefaultPatientTitle = "[No Patient]";

        public event CasesUpdatedHandler CasesUpdated;

        ExamListViewType _type = ExamListViewType.Unread;
        public ExamListViewType Type
        {
            get
            {
                return _type;
            }

            set
            {
                _type = value;

                // for patient cases register for patient selected notifications
                if (_type == ExamListViewType.Patient)
                {
                    Title = DefaultPatientTitle;

                    AppMessages.PatientSelectedMessage.Register(
                            this,
                            (action) => this.SetLastSelectedPatient(action.Sender, action.Content));
                }
            }
        }

        public String Title { get; set; }

        public WorklistFilterViewModel WorklistFilterViewModel { get; set; }

        public Patient LastSelectedPatient { get; set; }

        public CaseListItem SelectedStudy { get; set; }

        public Boolean IsStudySelected { get { return SelectedStudy != null; } }

        static ObservableCollection<string> _priorities = null;
        public static ObservableCollection<string> Priorities
        {
            get
            {
                if (_priorities == null)
                {
                    _priorities = new ObservableCollection<string>();
                    _priorities.Add("ROUTINE");
                    _priorities.Add("HIGH");
                }

                return _priorities;
            }
        }

        static ObservableCollection<string> _methods = null;
        public static ObservableCollection<string> Methods
        {
            get
            {
                if (_methods == null)
                {
                    _methods = new ObservableCollection<string>();
                    _methods.Add("TRADITIONAL");
                    _methods.Add("ROBOTICS");
                    _methods.Add("WSI");
                }

                return _methods;
            }
        }

        void SetLastSelectedPatient(object sender, Patient patient)
        {
            if (sender != this)
            {
                Patient oldPatient = LastSelectedPatient;

                if (LastSelectedPatient == null)
                {
                    LastSelectedPatient = new Patient();
                }

                LastSelectedPatient.PatientICN = patient.PatientICN;
                LastSelectedPatient.PatientShortID = patient.PatientShortID;
                LastSelectedPatient.PatientSensitive = patient.PatientSensitive;

                // check if the list needs to be retrieved during next refresh.
                if ((Type == ExamListViewType.Patient) && ((oldPatient == null) || (LastSelectedPatient.PatientICN != oldPatient.PatientICN)))
                {
                    IsDisplayed = false;
                }
            }
        }

        public static WorklistViewModel Create(ExamListViewType examListViewType, IWorkListDataSource dataSource)
        {
            WorklistViewModel viewModel = new WorklistViewModel(examListViewType, dataSource);

            switch (examListViewType)
            {
                case ExamListViewType.Read: viewModel.Title = "Readlist"; break;
                case ExamListViewType.Unread: viewModel.Title = "Results"; break;
                case ExamListViewType.Patient: viewModel.Title = "Patient"; break;
            }

            return viewModel;
        }

        public CaseListItem Root { get; private set; }

        private IWorkListDataSource _dataSource = null;
        public WorklistViewModel(ExamListViewType examlistType, IWorkListDataSource dataSource)
        {
            this.Type = examlistType;
            this._dataSource = dataSource;
            Root = new CaseListItem();
            IsActive = false;

            this.RefreshCommand = new RelayCommand(Refresh);

            // this.ReserveCaseCommand = new RelayCommand(ReserveCases, () => CanReserveCases());

            // this.UnreserveCaseCommand = new RelayCommand(UnreserveCases, () => CanUnreserveCases());

            this.RequestConsultationCommand = new RelayCommand(RequestConsultation, () => CanRequestConsultation());

            this.EditReportCommand = new RelayCommand(EditReport, () => CanEditReport());

            this.ViewReportCommand = new RelayCommand(ViewReport, () => CanViewReport());

            this.ViewSnapshotsCommand = new RelayCommand(ViewSnapshots, () => CanViewSnapshots());

            this.ViewNotesCommand = new RelayCommand(ViewNotes, () => CanViewNotes());

            this.ViewInCaptureCommand = new RelayCommand(ViewInCapture, () => CanViewInCapture());

            //this.DoubleClickCommand = new RelayCommand(this.ReserveCases, () => CanReserveCases());

            this.ViewDefaultHealthSummaryCommand = new RelayCommand(ViewDefaultHealthSummary, () => CanViewDefaultHealthSummary());

            this.ViewHealthSummaryListCommand = new RelayCommand(ViewHealthSummaryList, () => CanViewHealthSummaryList());

            this.WorklistFilterViewModel = new WorklistFilterViewModel(this.Type);
            this.WorklistFilterViewModel.PropertyChanged += new PropertyChangedEventHandler(WorklistFilterViewModel_PropertyChanged);

            // configure periodic refresh, periodically refreshes active worklist
            _refreshTimer = new DispatcherTimer();
            _refreshTimer.Interval = TimeSpan.FromMilliseconds(3000);
            _refreshTimer.Tick += new EventHandler(_timer_Tick);
            _refreshTimer.Start();
        }

        private DispatcherTimer _refreshTimer;
        void _timer_Tick(object sender, EventArgs e)
        {
            if (IsActive)
                _refresh();
        }

        #region Commands

        #region Refresh Cases

        public RelayCommand RefreshCommand { get; private set; }

        #endregion

        //#region Reserve Cases

        //public RelayCommand ReserveCaseCommand { get; private set; }

        //public void ReserveCases()
        //{
        //    try
        //    {
        //        _refreshTimer.Stop();
        //        foreach (CaseListItem item in this.SelectedItems)
        //        {
        //            // attempt to reserve case 
        //            _dataSource.ReserveCase(item.CaseURN, true);
        //            Log.Info(string.Format("Reserved case: {0} {1}.", item.SiteAbbr, item.AccessionNumber));
        //        }
        //        _refreshTimer.Start();

        //        this.Refresh(this.SelectedItems, keepCaseOn);
        //    }
        //    catch (Exception ex)
        //    {
        //        Log.Error("Cannot reserve case.", ex);
        //        MessageBox.Show("Case cannot be reserved.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
        //    }
        //}

        //public bool CanReserveCases()
        //{
        //    return ((UserContext.UserHasKey("LRLAB")) &&
        //            (this.Type != ExamListViewType.Patient) &&
        //            (SelectedItems != null) &&
        //            (SelectedItems.Count == 1) &&
        //            (this.SelectedItems[0].Kind == CaseListItemKind.Case));
        //}

        //#endregion

        //#region Un-Reserve Cases

        //public RelayCommand UnreserveCaseCommand { get; private set; }

        //public void UnreserveCases()
        //{
        //    if ((this.SelectedItems != null) && (this.SelectedItems.Count > 0))
        //    {
        //        try
        //        {
        //            _refreshTimer.Stop();
        //            foreach (CaseListItem item in this.SelectedItems)
        //            {
        //                // attempt to un- reserve the case
        //                _dataSource.ReserveCase(item.CaseURN, false);
        //                Log.Info(string.Format("Unreserved case: {0} {1}.", item.SiteAbbr, item.AccessionNumber));
        //            }
        //            _refreshTimer.Start();

        //            this.Refresh(this.SelectedItems, keepCaseOn);
        //        }
        //        catch (Exception ex)
        //        {
        //            Log.Error("Cannot unreserve case.", ex);
        //            MessageBox.Show("Case cannot be unreserved.", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
        //        }
        //    }
        //}

        //public bool CanUnreserveCases()
        //{
        //    if ((UserContext.UserHasKey("LRLAB")) &&
        //        (this.Type != ExamListViewType.Patient) && 
        //        (this.SelectedItems != null) &&
        //        (this.SelectedItems.Count == 1) &&
        //        (this.SelectedItems[0].Kind == CaseListItemKind.Case))
        //    {
        //        // all selected items have to reserved by the current user
        //        foreach (CaseListItem item in this.SelectedItems)
        //        {
        //            if (item.ReserveState != "1")
        //            {
        //                return false;
        //            }
        //        }

        //        return true;
        //    }

        //    return false;
        //}

        //#endregion

        #region Edit Report

        public RelayCommand EditReportCommand { get; private set; }

        public void EditReport()
        {
            if ((this.SelectedItems != null) &&
                (this.SelectedItems.Count == 1))
            {
                _refreshTimer.Stop();
                AppMessages.EditReportMessage.Send(new AppMessages.EditReportMessage.MessageData { Item = SelectedItems[0], Worklist = this });
                _refreshTimer.Start();

                if ((this.SelectedItems.Count > 0) && (this.SelectedItems[0].ReportStatus == "Released"))
                {
                    this.Refresh();
                }
                else
                {
                    this.Refresh(this.SelectedItems, keepCaseOn);
                }
            }
        }

        public bool CanEditReport()
        {
            return ((UserContext.UserHasKey("LRLAB")) &&
                    (this.Type != ExamListViewType.Patient) &&
                    (SelectedItems != null) &&
                    (SelectedItems.Count == 1) &&
                    (this.SelectedItems[0].Kind == CaseListItemKind.Case));
        }

        #endregion

        #region View Report

        public RelayCommand ViewReportCommand { get; private set; }

        public void ViewReport()
        {
            if ((SelectedItems != null) &&
                (SelectedItems.Count == 1))
            {
                _refreshTimer.Stop();
                AppMessages.ViewReportMessage.Send(SelectedItems[0]);
                _refreshTimer.Start();
            }
        }

        public bool CanViewReport()
        {
            return ((UserContext.UserHasKey("LRLAB")) &&
                    (SelectedItems != null) &&
                    (SelectedItems.Count == 1) &&
                    (this.SelectedItems[0].Kind == CaseListItemKind.Case) &&
                    (SelectedItems[0].ReportStatus == "Released"));
        }

        #endregion

        #region View Snapshots
        public RelayCommand ViewSnapshotsCommand { get; private set; }

        public void ViewSnapshots()
        {
            if ((SelectedItems != null) && (SelectedItems.Count == 1))
            {
                _refreshTimer.Stop();
                AppMessages.ViewSnapshotsMessage.Send(SelectedItems[0]);
                _refreshTimer.Start();
            }
        }

        public bool CanViewSnapshots()
        {
            // **** just for demo, return true without snapshots
            //if ((SelectedItems != null) && (SelectedItems.Count == 1) && 
            //    (this.SelectedItems[0].Kind == CaseListItemKind.Case) )
            //    return true;

            if ((SelectedItems != null) && (SelectedItems.Count == 1) && 
                (this.SelectedItems[0].Kind == CaseListItemKind.Case) &&
                // (!string.IsNullOrWhiteSpace(this.SelectedItems[0].SnapshotCount)))
                (this.SelectedItems[0].SlidesAvailable.Equals("Yes")))
            {
                int count;
                bool result = Int32.TryParse(this.SelectedItems[0].SnapshotCount, out count);
                if ((result) && (count > 0))
                    return true;
            }

            return false;
        }
        #endregion

        #region View Notes

        public RelayCommand ViewNotesCommand { get; private set; }

        public void ViewNotes()
        {
            if (CanViewNotes())
            {
                _refreshTimer.Stop();
                AppMessages.ViewNotesMessage.Send(new AppMessages.ViewNotesMessage.MessageData
                {
                    PatientICN = this.SelectedItems[0].PatientICN,
                    PatientID = this.SelectedItems[0].PatientID,
                    PatientName = this.SelectedItems[0].PatientName,
                    SiteName = this.SelectedItems[0].SiteAbbr,
                    SiteCode = this.SelectedItems[0].SiteCode,
                    AccessionNr = this.SelectedItems[0].AccessionNumber,
                    CaseURN = this.SelectedItems[0].CaseURN
                });
                _refreshTimer.Start();
            }
        }

        public bool CanViewNotes()
        {
            return ((UserContext.UserHasKey("LRLAB")) &&
                    (SelectedItems != null) &&
                    (SelectedItems.Count == 1) &&
                    (this.SelectedItems[0].Kind == CaseListItemKind.Case));
        }

        #endregion

        #region Request Consultation

        public RelayCommand RequestConsultationCommand { get; private set; }

        public void RequestConsultation()
        {
            if ((this.SelectedItems != null) &&
                (this.SelectedItems.Count == 1))
            {
                _refreshTimer.Stop();
                AppMessages.ViewConsultationStatusMessage.Send(new AppMessages.ViewConsultationStatusMessage.MessageData { Item = SelectedItems[0], Worklist = this });
                _refreshTimer.Start();
                this.Refresh(this.SelectedItems, !keepCaseOn); // *** does it always come off list?
            }
        }

        public bool CanRequestConsultation()
        {
            return ((UserContext.UserHasKey("LRLAB")) && 
                    (this.Type != ExamListViewType.Patient) &&
                    (this.SelectedItems != null) &&
                    (this.SelectedItems.Count == 1) &&
                    (this.SelectedItems[0].Kind == CaseListItemKind.Case) &&
                    (this.SelectedItems[0].ReportStatus == "Pending Verification"));
        }

        #endregion

        #region View in capture

        public RelayCommand ViewInCaptureCommand { get; private set; }

        public void ViewInCapture()
        {
            _refreshTimer.Stop();
            _dataSource.SetPatientContext("MagImageCapture.exe", this.SelectedItems[0].PatientID);
            _refreshTimer.Start();
        }

        public bool CanViewInCapture()
        {
            return ((this.SelectedItems != null) &&
                    (this.SelectedItems.Count == 1));
        }

        #endregion

        //#region Double-click

        //public RelayCommand DoubleClickCommand { get; private set; }

        //#endregion

        #region Default Health Summary Command

        public RelayCommand ViewDefaultHealthSummaryCommand { get; private set; }

        public void ViewDefaultHealthSummary()
        {
            if ((this.SelectedItems != null) && (this.SelectedItems.Count > 0))
            {
                // get default health summary type
                HealthSummaryType defaultHSType = UserPreferences.Instance.HealthSummaryPreferences.GetDefaultHealthSummaryType();
                if (defaultHSType != null)
                {
                    _refreshTimer.Stop();
                    AppMessages.ViewHealthSummaryMessage.Send(new AppMessages.ViewHealthSummaryMessage.MessageData
                    {
                        HealthSummaryType = defaultHSType,
                        PatientICN = this.SelectedItems[0].PatientICN,
                        PatientID = this.SelectedItems[0].PatientID,
                        PatientName = this.SelectedItems[0].PatientName,
                        SiteName = this.SelectedItems[0].SiteAbbr,
                        SiteCode = this.SelectedItems[0].SiteCode
                    });
                    _refreshTimer.Start();
                }
                else
                {
                    // todo: display message

                    // display health summary list
                    ViewHealthSummaryList();
                }
            }
        }

        public bool CanViewDefaultHealthSummary()
        {
            return ((UserContext.UserHasKey("LRLAB")) &&
                    (this.SelectedItems != null) &&
                    (this.SelectedItems.Count == 1) &&
                    (this.SelectedItems[0].Kind == CaseListItemKind.Case));
        }

        #endregion

        #region View Health Summary List Command

        public RelayCommand ViewHealthSummaryListCommand { get; private set; }

        public void ViewHealthSummaryList()
        {
            if ((this.SelectedItems != null) && (this.SelectedItems.Count > 0))
            {
                _refreshTimer.Stop();
                AppMessages.ViewHealthSummaryListMessage.Send(new AppMessages.ViewHealthSummaryListMessage.MessageData
                {
                    SiteID = this.SelectedItems[0].SiteCode,
                    PatientICN = this.SelectedItems[0].PatientICN,
                    PatientID = this.SelectedItems[0].PatientID,
                    PatientName = this.SelectedItems[0].PatientName,
                    SiteName = this.SelectedItems[0].SiteAbbr
                });
                _refreshTimer.Start();
            }
        }

        public bool CanViewHealthSummaryList()
        {
            return ((UserContext.UserHasKey("LRLAB")) &&
                    (this.SelectedItems != null) &&
                    (this.SelectedItems.Count == 1) &&
                    (this.SelectedItems[0].Kind == CaseListItemKind.Case));
        }

        #endregion

        #endregion

        void WorklistFilterViewModel_PropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            if (e.PropertyName == "CurrentFilter")
            {
                // broadcast change in current filter
                AppMessages.WorklistFilterChangeMessage.Send(this.WorklistFilterViewModel.CurrentFilter, this);
            }
        }

        public System.Collections.IEnumerable GetChildren(object parent)
        {
            if (parent == null)
                parent = Root;
            return (parent as CaseListItem).Slides;
        }

        public bool HasChildren(object parent)
        {
            if (parent is CaseListItem)
                return (parent as CaseListItem).Slides.Count > 0;
            else
                return false;
        }

        public void Sort(string field, bool ascending)
        {
            Sort(Root, field, ascending);
        }

        public void Sort(CaseListItem parent, string field, bool ascending)
        {
            CaseListColumnType column = (CaseListColumnType)Enum.Parse(typeof(CaseListColumnType), field);

            parent.Slides.Sort(delegate(CaseListItem item1, CaseListItem item2)
            {
                return CaseListItemComparer.Compare(item1, item2, column, ascending);
            });

            foreach (CaseListItem child in parent.Slides)
            {
                Sort(child, field, ascending);
            }
        }

        public bool IsDisplayed { get; set; }
        bool _isActive = false;

        public bool IsActive
        {
            get
            {
                return _isActive;
            }

            set
            {
                if (_isActive != value)
                {
                    _isActive = value;

                    if (_isActive &&
                        (!IsDisplayed || (Type == ExamListViewType.Patient))) //Patient tab is always refreshed
                    {
                        // refresh cases
                        Refresh();

                        IsDisplayed = true;
                    }
                    else
                    {
                        AppMessages.UpdateStatusesMessage.Send(new AppMessages.UpdateStatusesMessage.MessageData() { GeneralStatus = CaseListStatus });
                    }
                }
            }
        }

        CaseList caseList = new CaseList();

        public string CaseListStatus
        {
            get
            {
                // get general statistics and the case count(s) per site
                string genStat = string.Format("Viewing {0} of {1} {2} Cases:", Root.Slides.Count, caseList.Cases.Count, this.Type);
                foreach (AcquisitionSiteInfo site in UserContext.AcquisitionList.Items)
                {
                    if (site.Active)
                    {
                        int count = 0;
                        try
                        {
                            count = caseList.Cases.Count(c => c.SiteID == site.PrimeSiteStationNumber);
                        }
                        finally
                        {
                            genStat += string.Format(" {0} ({1})", site.SiteAbr, count);
                        }
                    }
                }

                return genStat;
            }
        }
        
        public List<Case> Cases
        {
            get
            {
                return caseList.Cases;
            }
        }

        public void _refresh()
        {
            // get cases
            GetCases();

            // create new nodes
            CreateNodes();
        }

        public void Refresh()
        {
            _refreshTimer.Stop();
            _refresh();
            _refreshTimer.Start();
        }

        private void Refresh(ObservableCollection<CaseListItem> items, bool keepDisplayed)
        {
            // get cases using selected items
            CaseList caseList = new CaseList();

            foreach (CaseListItem item in items)
            {
                Case caseObj = this.caseList.Cases.Where(x => (x.CaseURN == item.CaseURN)).FirstOrDefault();
                if (caseObj != null)
                {
                    caseList.Cases.Add(caseObj);
                }
            }

            // update cases (read in each from relevant data source)
            _dataSource.UpdateCases(caseList);

            // update nodes
            foreach (Case caseObj in caseList.Cases)
            {
                CaseListItem item = items.Where(x => (x.CaseURN == caseObj.CaseURN)).FirstOrDefault();
                if (item != null)
                {
                    if (CanDisplayCase(caseObj) || keepDisplayed)
                    {
                        item.Initialize(caseObj);
                    }
                    else
                    {
                        // no longer belongs to the worklist.

                        // remove from case list
                        Case caseObjRemove = this.Cases.Where(x => (x.CaseURN == caseObj.CaseURN)).FirstOrDefault();
                        if (caseObjRemove != null)
                        {
                            this.Cases.Remove(caseObjRemove);
                        }

                        // remove from selected list
                        items.Remove(item);

                        // remove from item list
                        item = Root.Slides.Where(x => (x.CaseURN == caseObj.CaseURN)).FirstOrDefault();
                        if (item != null)
                        {
                            Root.Slides.Remove(item);

                            RaisePropertyChanged("Nodes");
                        }
                    }
                }
            }
        }

        private bool CanDisplayCase(Case caseObj)
        {
            // can display if retrieved from current site
            if (caseObj.SiteID == UserContext.LocalSite.PrimarySiteStationNUmber)
            {
                return true;
            }

            switch (this.Type)
            {
                case ExamListViewType.Unread:
                    {
                        foreach (CaseConsultation caseConsult in caseObj.ConsultationList.ConsultationList)
                        {
                            if ((caseConsult.SiteID == UserContext.LocalSite.PrimarySiteStationNUmber) &&
                                (caseConsult.Type == "CONSULTATION") && (caseConsult.Status == "PENDING"))
                            {
                                return true;
                            }
                        }

                        return false;
                    }

                case ExamListViewType.Read:
                    {
                        foreach (CaseConsultation caseConsult in caseObj.ConsultationList.ConsultationList)
                        {
                            if ((caseConsult.SiteID == UserContext.LocalSite.PrimarySiteStationNUmber) &&
                                (caseConsult.Type == "CONSULTATION") && (caseConsult.Status == "COMPLETED"))
                            {
                                return true;
                            }
                        }

                        return false;
                    }

                default: return true;
            }
        }

        public void Expand(IEnumerable<CaseListItem> items)
        {
            List<CaseListItem> updatedItems = new List<CaseListItem>();

            // get case details for item
            foreach (CaseListItem item in items)
            {
                if ((item.Slides.Count == 1) && (item.Slides[0].Kind == CaseListItemKind.PlaceHolder))
                {
                    // get case details
                    this.FillCaseDetails(item);

                    updatedItems.Add(item);
                }
            }

            // notify listeners
            if ((updatedItems.Count > 0) && (this.CasesUpdated != null))
            {
                this.CasesUpdated(items);
            }
        }

        void GetCases()
        {
            string unreadTime = null;
            string readTime = null;

            // clear current cases
            if (caseList != null)
            {
                caseList.Clear();
            }
            
            // clear title
            if (Type == ExamListViewType.Patient)
            {
                Title = DefaultPatientTitle;
            }

            // get cases 
            if (Type == ExamListViewType.Unread)
            {
                caseList = _dataSource.GetUnreleasedCases();

                unreadTime = DateTime.Now.ToString("MM-dd-yyyy HH:mm:ss");
            }
            else if (Type == ExamListViewType.Patient)
            {
                if (LastSelectedPatient != null)
                {
                    // get patient information
                    Patient patient = _dataSource.GetPatient(UserContext.LocalSite.SiteStationNumber, LastSelectedPatient.PatientICN);
                    if (patient != null)
                    {
                        // update patient object
                        patient.PatientShortID = LastSelectedPatient.PatientShortID;

                        // get all cases belonging to the patient
                        caseList = _dataSource.GetPatientCases(LastSelectedPatient.PatientICN);

                        // update patient sensitive flag
                        if ((caseList != null) && (caseList.Cases.Count > 0))
                            patient.PatientSensitive = caseList.Cases[0].PatientSensitive;
                        else
                            patient.PatientSensitive = false;

                        // set title
                        if (Type == ExamListViewType.Patient)
                        {
                            if (patient.PatientSensitive)
                                Title = patient.PatientName;
                            else
                                Title = string.Format("{0} ({1}) {2} {3}", patient.PatientName, patient.PatientShortID, patient.Age, patient.Sex);
                        }
                    }
                    else
                    {
                        Title = DefaultPatientTitle;

                        caseList.Clear();
                    }
                }
            }
            else if (Type == ExamListViewType.Read)
            {
                caseList = _dataSource.GetReleasedCases();

                readTime = DateTime.Now.ToString("MM-dd-yyyy HH:mm:ss");
            }

            AppMessages.UpdateStatusesMessage.Send(new AppMessages.UpdateStatusesMessage.MessageData() { UnreadTime = unreadTime, ReadTime = readTime});
        }

        WorkListFilter _filter = null;
        public WorkListFilter Filter
        {
            get
            {
                return _filter;
            }

            set
            {
                //if (_filter != value)
                {
                    _filter = value;

                    // create new nodes
                    CreateNodes();
                }
            }
        }

        public void CreateNodes()
        {
            // clear current nodes
            Root.Slides.Clear();

            if (caseList != null)
            {
                CaseListItem.CreateNodes(caseList, Root, _filter);
            }

            AppMessages.UpdateStatusesMessage.Send(new AppMessages.UpdateStatusesMessage.MessageData() { GeneralStatus = CaseListStatus });

            RaisePropertyChanged("Nodes");
        }

        ObservableCollection<CaseListItem> _selectedItems;
        public ObservableCollection<CaseListItem> SelectedItems
        {
            get
            {
                return _selectedItems;
            }

            set
            {
                _selectedItems = value;

                // set last selected patient
                if ((Type != ExamListViewType.Patient) &&
                    (_selectedItems != null) && (_selectedItems.Count > 0))
                {
                    AppMessages.PatientSelectedMessage.Send(_selectedItems[0].PatientICN, _selectedItems[0].PatientID, this);
                }
            }
        }

        internal void FillCaseDetails(CaseListItem item)
        {
            CaseSpecimenList specimenData = _dataSource.GetCaseDetail(item.CaseURN);
            item.CreateChildren(specimenData);
        }

       
    }
}
