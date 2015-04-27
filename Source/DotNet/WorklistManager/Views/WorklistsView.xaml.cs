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
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Diagnostics;

using VistA.Imaging.Telepathology.Worklist.ViewModel;
using VistA.Imaging.Telepathology.Worklist.Messages;
using GalaSoft.MvvmLight.Threading;

namespace VistA.Imaging.Telepathology.Worklist.Views
{
    /// <summary>
    /// Interaction logic for WorklistsView.xaml
    /// </summary>
    public partial class WorklistsView : UserControl
    {
        public WorklistsView()
        {
            InitializeComponent();
        }

        public bool LayoutPreferencesApplied { get; set; }

        WorklistView CreateExamListView(WorklistViewModel viewModel)
        {
            WorklistView view = new WorklistView(viewModel);

            TabItem ti = new TabItem { Content = view };
            ti.DataContext = viewModel;
            this.tabExamList.Items.Add(ti);

            return view;
        }

        private void tabExamList_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (e.Source is TabControl)
            {
                WorklistsViewModel viewModel = (WorklistsViewModel)DataContext;
                if (viewModel == null) return;

                foreach (TabItem ti in this.tabExamList.Items)
                {
                    WorklistView view = (WorklistView) ti.Content;
                    if ((view != null) && (view.DataContext != null))
                    {
                        ((WorklistViewModel)view.DataContext).IsActive = false;
                    }
                }

                WorklistView currentView = (WorklistView)((TabItem)this.tabExamList.SelectedItem).Content;
                if (currentView != null)
                {
                    if (currentView.DataContext != null)
                    {
                        viewModel.CurrentWorkList = (WorklistViewModel)currentView.DataContext;
                        ((WorklistViewModel)currentView.DataContext).IsActive = true;
                        
                        // set last selected patient of recently activated Unread / Read list
                        if ((viewModel.CurrentWorkList.Type != ExamListViewType.Patient) &&
                            (viewModel.CurrentWorkList.SelectedItems != null) && (viewModel.CurrentWorkList.SelectedItems.Count > 0))
                        {
                            AppMessages.PatientSelectedMessage.Send(viewModel.CurrentWorkList.SelectedItems[0].PatientICN, viewModel.CurrentWorkList.SelectedItems[0].PatientID, this);
                        }
                    }

                    // apply layout preferences if needed
                    if (this.LayoutPreferencesApplied && !currentView.LayoutPreferencesApplied)
                    {
                        currentView.ApplyLayoutPreferences();
                    }
                }
            }
        }

        private void UserControl_DataContextChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
        }

        private void UserControl_Loaded(object sender, RoutedEventArgs e)
        {
            if (DataContext != null)
            {
                WorklistsViewModel viewModel = (WorklistsViewModel)DataContext;
                WorklistView defaultView = null;

                // add default tabs
                defaultView = CreateExamListView(viewModel.WorklistViewModels[ExamListViewType.Unread]);
                CreateExamListView(viewModel.WorklistViewModels[ExamListViewType.Patient]);
                CreateExamListView(viewModel.WorklistViewModels[ExamListViewType.Read]);

                this.tabExamList.SelectedItem = defaultView;
            }
        }

        public void SaveLayoutPreferences()
        {
            foreach (TabItem item in this.tabExamList.Items)
            {
                ((WorklistView) item.Content).SaveLayoutPreferences();
            }
        }

        public void ApplyLayoutPreferences()
        {
            this.LayoutPreferencesApplied = true;

            // apply preferences to current worklist contrl
            if (this.tabExamList.SelectedItem != null)
            {
                ((WorklistView)(((TabItem)this.tabExamList.SelectedItem)).Content).ApplyLayoutPreferences();
            }

            //foreach (TabItem item in this.tabExamList.Items)
            //{
            //    ((WorklistView)item.Content).ApplyLayoutPreferences();
            //}
        }
    }
}
