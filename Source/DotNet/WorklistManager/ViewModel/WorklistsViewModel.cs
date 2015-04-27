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
using System.Linq;
using System.Text;
using System.ComponentModel;
using System.Collections.ObjectModel;

using VistA.Imaging.Telepathology.Common.Model;
using GalaSoft.MvvmLight;
using VistA.Imaging.Telepathology.Worklist.DataSource;
using GalaSoft.MvvmLight.Command;
using System.Diagnostics;
using VistA.Imaging.Telepathology.Worklist.Messages;

namespace VistA.Imaging.Telepathology.Worklist.ViewModel
{
    public class WorklistsViewModel : ViewModelBase
    {
        IWorkListDataSource _dataSource = null;

        public Dictionary<ExamListViewType, WorklistViewModel> WorklistViewModels { get; private set; }

        public WorklistsViewModel(IWorkListDataSource dataSource)
        {
            _dataSource = dataSource;

            // create worklists
            WorklistViewModels = new Dictionary<ExamListViewType, WorklistViewModel>();
            WorklistViewModels[ExamListViewType.Unread] = new WorklistViewModel(ExamListViewType.Unread, dataSource){ Title = "Unread" };
            WorklistViewModels[ExamListViewType.Patient] = new WorklistViewModel(ExamListViewType.Patient, dataSource) { Title = "[No Patient]" };
            WorklistViewModels[ExamListViewType.Read] = new WorklistViewModel(ExamListViewType.Read, dataSource) { Title = "Read" };

            this.RefreshCommand = new RelayCommand(Refresh);

            this.ReserveCaseCommand = new RelayCommand(ReserveCases, () => CanReserveCases());

            this.UnreserveCaseCommand = new RelayCommand(UnreserveCases, () => CanUnreserveCases());

            this.RequestConsultationCommand = new RelayCommand(RequestConsultation, () => CanRequestConsultation());

            this.EditReportCommand = new RelayCommand(EditReport, () => CanEditReport());

            this.ViewReportCommand = new RelayCommand(ViewReport, () => CanViewReport());

            this.ViewSnapshotsCommand = new RelayCommand(ViewSnapshots, () => CanViewSnapshots());

            this.ViewNotesCommand = new RelayCommand(ViewNotes, () => CanViewNotes());

            this.ViewDefaultHealthSummaryCommand = new RelayCommand(ViewDefaultHealthSummary, () => CanViewDefaultHealthSummary());

            this.ViewHealthSummaryListCommand = new RelayCommand(ViewHealthSummaryList, () => CanViewHealthSummaryList());
        }

        #region Commands

        #region Refresh Cases

        public RelayCommand RefreshCommand { get; private set; }

        public void Refresh()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.Refresh();
        }

        #endregion

        #region Reserve Cases

        public RelayCommand ReserveCaseCommand { get; private set; }

        public void ReserveCases()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.ReserveCases();
        }

        public bool CanReserveCases()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanReserveCases() : false;
        }

        #endregion

        #region Un-Reserve Cases

        public RelayCommand UnreserveCaseCommand { get; private set; }

        public void UnreserveCases()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.UnreserveCases();
        }

        public bool CanUnreserveCases()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanUnreserveCases() : false;
        }

        #endregion

        #region Edit Report

        public RelayCommand EditReportCommand { get; private set; }

        public void EditReport()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.EditReport();
        }

        public bool CanEditReport()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanEditReport() : false;
        }

        #endregion

        #region View Report

        public RelayCommand ViewReportCommand { get; private set; }

        public void ViewReport()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.ViewReport();
        }

        public bool CanViewReport()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanViewReport() : false;
        }

        #endregion

        #region View Snapshots
        public RelayCommand ViewSnapshotsCommand { get; private set; }

        public void ViewSnapshots()
        {
            if (this.CurrentWorkList != null) 
                this.CurrentWorkList.ViewSnapshots();
        }

        public bool CanViewSnapshots()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanViewSnapshots() : false;
        }
        #endregion

        #region View Notes

        public RelayCommand ViewNotesCommand { get; private set; }

        public void ViewNotes()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.ViewNotes();
        }

        public bool CanViewNotes()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanViewNotes() : false;
        }

        #endregion

        #region Request Consultation

        public RelayCommand RequestConsultationCommand { get; private set; }

        public void RequestConsultation()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.RequestConsultation();
        }

        public bool CanRequestConsultation()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanRequestConsultation() : false;
        }

        #endregion

        #region Default Health Summary Command

        public RelayCommand ViewDefaultHealthSummaryCommand { get; private set; }

        public void ViewDefaultHealthSummary()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.ViewDefaultHealthSummary();
        }

        public bool CanViewDefaultHealthSummary()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanViewDefaultHealthSummary() : false;
        }

        #endregion

        #region View Health Summary List Command

        public RelayCommand ViewHealthSummaryListCommand { get; private set; }

        public void ViewHealthSummaryList()
        {
            if (this.CurrentWorkList != null) this.CurrentWorkList.ViewHealthSummaryList();
        }

        public bool CanViewHealthSummaryList()
        {
            return (this.CurrentWorkList != null) ? this.CurrentWorkList.CanViewHealthSummaryList() : false;
        }

        #endregion

        #endregion


        //public void RefreshWorklists()
        //{
        //    foreach (WorklistViewModel worklistViewModel in WorklistViewModels.Values)
        //    {
        //        if (worklistViewModel.IsActive)
        //        {
        //            worklistViewModel.Refresh();
        //        }
        //        else
        //        {
        //            // set as dirty.
        //            worklistViewModel.IsDisplayed = false;
        //        }
        //    }
        //}

        public WorklistViewModel CurrentWorkList { get; set; }
    }
}
