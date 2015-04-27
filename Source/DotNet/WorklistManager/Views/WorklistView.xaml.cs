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
using System.ComponentModel;

using Aga.Controls.Tree;
using VistA.Imaging.Telepathology.Worklist.ViewModel;
using VistA.Imaging.Telepathology.Worklist.Controls;
using System.Collections.ObjectModel;
using VistA.Imaging.Telepathology.Common.Model;
using System.Diagnostics;
using VistA.Imaging.Telepathology.Worklist.Messages;
using GalaSoft.MvvmLight.Threading;

namespace VistA.Imaging.Telepathology.Worklist.Views
{
    /// <summary>
    /// Interaction logic for WorklistView.xaml
    /// </summary>
    public partial class WorklistView : UserControl
    {
        private GridViewColumnHeader _CurSortCol = null;
        private SortAdorner _CurAdorner = null;
        private WorkListViewComparer _sorter = new WorkListViewComparer();
        private const string DefaultColumnTag = "DateTime";

        public bool LayoutPreferencesApplied { get; set; }

        public WorklistView(WorklistViewModel viewModel)
        {
            DataContext = viewModel;
            viewModel.PropertyChanged += new PropertyChangedEventHandler(viewModel_PropertyChanged);
            viewModel.CasesUpdated += new CasesUpdatedHandler(viewModel_CasesUpdated);

            InitializeComponent();

            AppMessages.WorklistFilterChangeMessage.Register(
                    this,
                    (action) => DispatcherHelper.CheckBeginInvokeOnUI(() => this.ApplyFilter(action.Content, action.Sender)));

            this._tree.RowExpanded += new RowExpandedEventHandler(_tree_RowExpanded);

            // notification of column reorder event
            //GridView gridView = (GridView)this._tree.View;
            //gridView.Columns.CollectionChanged += new System.Collections.Specialized.NotifyCollectionChangedEventHandler(Columns_CollectionChanged);
        }

        void viewModel_CasesUpdated(IEnumerable<CaseListItem> items)
        {
            foreach (CaseListItem item in items)
            {
                // get tree node for the item
                TreeNode node = this._tree.GetNodeFromTag(item);
                if (node != null)
                {
                    this._tree.RefreshNode(node);
                }
            }
        }

        void Columns_CollectionChanged(object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e)
        {
            if (e.Action == System.Collections.Specialized.NotifyCollectionChangedAction.Move)
            {
                // check if first column has been moved to another location
                if ((e.OldStartingIndex == 0) && (e.NewStartingIndex != 0))
                {
                    // move to first position
                    GridView gridView = (GridView)this._tree.View;
                    gridView.Columns.Move(e.NewStartingIndex, 0);
                }
            }
        }

        void _tree_RowExpanded(TreeNode node)
        {
            CaseListItem item = (CaseListItem)node.Tag;
            if ((item.Slides.Count == 1) && (item.Slides[0].Kind == CaseListItemKind.PlaceHolder))
            {
                // get case details
                WorklistViewModel viewModel = (WorklistViewModel)DataContext;

                viewModel.FillCaseDetails(item);

                this._tree.RefreshNode(node);
            }
        }

        void viewModel_PropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            if (e.PropertyName == "Nodes")
            {
                // re-populate tree
                RefreshTree();
            }
        }

        private void GridViewColumnHeader_Click(object sender, RoutedEventArgs e)
        {
            GridViewColumnHeader column = sender as GridViewColumnHeader;
            String field = column.Tag as String;

            if (_CurSortCol != null)
            {
                AdornerLayer.GetAdornerLayer(_CurSortCol).Remove(_CurAdorner);
            }
            else if (_CurAdorner != null)
            {
                // clear sort icon from default column
                GridView gridView = (GridView)this._tree.View;
                GridViewColumn defColumn = gridView.Columns.Where(x => ((GridViewColumnHeader)x.Header).Tag.ToString() == DefaultColumnTag).FirstOrDefault();
                if (defColumn != null)
                {
                    AdornerLayer.GetAdornerLayer((GridViewColumnHeader)defColumn.Header).Remove(_CurAdorner);
                }
            }

            ListSortDirection newDir = ListSortDirection.Ascending;
            if (_CurSortCol == column && _CurAdorner.Direction == newDir)
                newDir = ListSortDirection.Descending;

            _CurSortCol = column;
            _CurAdorner = new SortAdorner(_CurSortCol, newDir);
            AdornerLayer.GetAdornerLayer(_CurSortCol).Add(_CurAdorner);

            //_sorter.SortColumn = field;
            //_sorter.SortDirection = newDir;
            //this._tree.Rows.Sort(_sorter);
            this._tree.Sort(field, newDir);

            //this._tree.Rows.RowItemsView.CustomSort = _sorter;
            //this._tree.Rows.RowItemsView.SortDescriptions.Clear();
            //this._tree.Rows.RowItemsView.SortDescriptions.Add(new SortDescription(_sorter.SortColumn, _sorter.SortDirection));
            //this._tree.Rows.RowItemsView.Refresh();
            //return;

            //_tree.Items.SortDescriptions.Add(new SortDescription(field, newDir));
            //ListCollectionView  view = (ListCollectionView ) CollectionViewSource.GetDefaultView(this._tree.ItemsSource);
            //if (view != null)
            //{
            //    _sorter.SortColumn = field;
            //    _sorter.SortDirection = newDir;

            //    view.CustomSort = _sorter;
            //    this._tree.Items.Refresh();
            //}
        }

        public void ApplyFilter(WorkListFilter filter, object sender)
        {
            WorklistViewModel viewModel = (WorklistViewModel)DataContext;
            if ((viewModel != null) && (viewModel == sender))
            {
                viewModel.Filter = filter;
                viewModel.CreateNodes();
            }

            RefreshTree();
        }

        static ObservableCollection<CaseListItem> _oldSelectedItems=null;

        private void saveOldSelectedNodes(WorklistViewModel viewModel)
        {
            if (viewModel.SelectedItems.Count > 0)
            {
                _oldSelectedItems = new ObservableCollection<CaseListItem>();
                foreach (CaseListItem selectedItem in viewModel.SelectedItems)
                {
                    _oldSelectedItems.Add(selectedItem);
                }
            }
        }

        private void restoreOldSelectedNodes()
        {
            // restore old DataContext's SelectedItems, by looking for tag (CaseListItem) match(es); on hit(s) populate _tree.selectedNodes by matched node.Tag-s
            if ((_oldSelectedItems != null))
            {
                if (this._tree.SelectedNodes.Count > 0)
                    this._tree.SelectedItems.Clear();
                for (int i = 0; i < _tree.Root.Children.Count; i++)
                {
                    List<TreeNode> treeNodes = matchingCaseListItems(this._tree.Root.Children[i], _oldSelectedItems);
                    foreach (TreeNode node in treeNodes)
                    {
                        this._tree.SelectedItems.Add(node);
                    }
                }
                _oldSelectedItems = null;
            }
        }

        private List<TreeNode> matchingCaseListItems(TreeNode treeNode, ObservableCollection<CaseListItem> oldSelectedItems)
        {
            List<TreeNode> tNodes = new List<TreeNode>();
            CaseListItem cli = (CaseListItem)treeNode.Tag;
            foreach (CaseListItem selectedItem in oldSelectedItems)
            {
                if (cli.CaseURN.Equals(selectedItem.CaseURN))
                    tNodes.Add(treeNode);
            }
            return tNodes;
        }

        public void RefreshTree()
        {
            WorklistViewModel viewModel = (WorklistViewModel)DataContext;
            if (viewModel != null)
            {
                if (_CurSortCol == null)
                {
                    // never sorted manually. display as is.
                        this._tree.Model = viewModel; // context switch to new tree!!!
                }
                else
                {
                    this._tree.Sort(_CurSortCol.Tag as string, _CurAdorner.Direction);
                }
                if (_oldSelectedItems != null)
                   restoreOldSelectedNodes();
            }
        }

        private void _tree_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            WorklistViewModel viewModel = (WorklistViewModel)DataContext;
            if (viewModel != null)
            {
                if ((e != null) && (e.AddedItems.Count == 0) && (e.RemovedItems.Count > 0))
                    // new _tree under construction, selections are lost
                    saveOldSelectedNodes(viewModel);

                ICollection<TreeNode> selectedNodes = this._tree.SelectedNodes;
                Trace.WriteLine("Selected Node Count = " + selectedNodes.Count);

                ObservableCollection<CaseListItem> selectedItems = new ObservableCollection<CaseListItem>();
                foreach (TreeNode node in selectedNodes)
                {
                    selectedItems.Add((CaseListItem)node.Tag);
                }

                viewModel.SelectedItems = selectedItems;
            }
        }

        public void SaveLayoutPreferences()
        {
            WorklistViewModel viewModel = (WorklistViewModel)DataContext;
            if (viewModel != null)
            {
                UserPreferences.Instance.LayoutPreferences.SaveWorklistColumnPreferences(viewModel.Type.ToString(), this._tree.View as GridView);
            }
        }

        public void ApplyLayoutPreferences()
        {
            this.LayoutPreferencesApplied = true;

            // apply layout preferences
            WorklistViewModel viewModel = (WorklistViewModel)DataContext;
            if (viewModel != null)
            {
                UserPreferences.Instance.LayoutPreferences.ApplyWorklistColumnPreferences(viewModel.Type.ToString(), this._tree.View as GridView);
            }

            // display default sort column - DateTime
            GridView gridView = (GridView)this._tree.View;
            GridViewColumn defColumn = gridView.Columns.Where(x => ((GridViewColumnHeader)x.Header).Tag.ToString() == DefaultColumnTag).FirstOrDefault();
            if (defColumn != null)
            {
                GridViewColumnHeader header = defColumn.Header as GridViewColumnHeader;
                if (header.Column != null)
                {
                    _CurAdorner = new SortAdorner(header, ListSortDirection.Ascending);
                    AdornerLayer.GetAdornerLayer(header).Add(_CurAdorner);
                }
            }
        }
    }

    public class SortAdorner : Adorner
    {
        private readonly static Geometry _DescGeometry =
            Geometry.Parse("M 0,0 L 10,0 L 5,5 Z");

        private readonly static Geometry _AscGeometry =
            Geometry.Parse("M 0,5 L 10,5 L 5,0 Z");

        public ListSortDirection Direction { get; private set; }

        public SortAdorner(UIElement element, ListSortDirection dir)
            : base(element)
        { Direction = dir; }

        protected override void OnRender(DrawingContext drawingContext)
        {
            base.OnRender(drawingContext);

            if (AdornedElement.RenderSize.Width < 20)
                return;

            drawingContext.PushTransform(
                new TranslateTransform(
                  AdornedElement.RenderSize.Width - 15,
                  (AdornedElement.RenderSize.Height - 5) / 2));

            drawingContext.DrawGeometry(Brushes.Black, null,
                Direction == ListSortDirection.Ascending ?
                  _AscGeometry : _DescGeometry);

            drawingContext.Pop();
        }
    }
}
