using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Controls;
using System.Windows;
using System.Windows.Data;
using System.Collections.ObjectModel;
using System.Collections;
using System.ComponentModel;
using System.Collections.Specialized;
using System.Windows.Input;
using System.Windows.Controls.Primitives;

namespace Aga.Controls.Tree
{
    public delegate void RowExpandedEventHandler(TreeNode node);

    public class TreeList : ListView
	{
		#region Properties

		/// <summary>
		/// Internal collection of rows representing visible nodes, actually displayed in the ListView
		/// </summary>
        //internal ObservableCollectionAdv<TreeNode> Rows
        //{
        //    get;
        //    private set;
        //}

        public RowItemCollection Rows
        {
            get;
            private set;
        }


		private ITreeModel _model;
		public ITreeModel Model
		{
		  get { return _model; }
		  set 
		  {
			  //if (_model != value)
			  {
				  _model = value;
				  _root.Children.Clear();
				  //Rows.Clear();
                  Rows.ObservableRowItems.Clear();
				  
                  CreateChildrenNodes(_root); //do not expand by default
                  //CreateExpandedChildrenNodes(_root);
			  }
		  }
		}

		private TreeNode _root;
		public TreeNode Root // cpt 3/25/15: neede to be public to extract tree of active caselist
		{
			get { return _root; }
		}

		public ReadOnlyCollection<TreeNode> Nodes
		{
			get { return Root.Nodes; }
		}

		internal TreeNode PendingFocusNode
		{
			get;
			set;
		}

		public ICollection<TreeNode> SelectedNodes
		{
			get
			{
				return SelectedItems.Cast<TreeNode>().ToArray();
			}
		}

		public TreeNode SelectedNode
		{
			get
			{
				if (SelectedItems.Count > 0)
					return SelectedItems[0] as TreeNode;
				else
					return null;
			}
		}
		#endregion

        public event RowExpandedEventHandler RowExpanded;

		public TreeList()
		{
			//Rows = new ObservableCollectionAdv<TreeNode>();
            Rows = new RowItemCollection();
			_root = new TreeNode(this, null);
			_root.IsExpanded = true;
			//ItemsSource = Rows;
            ItemsSource = Rows.RowItemsView;
			ItemContainerGenerator.StatusChanged += ItemContainerGeneratorStatusChanged;
		}

		void ItemContainerGeneratorStatusChanged(object sender, EventArgs e)
		{
			if (ItemContainerGenerator.Status == GeneratorStatus.ContainersGenerated && PendingFocusNode != null)
			{
				var item = ItemContainerGenerator.ContainerFromItem(PendingFocusNode) as TreeListItem;
				if (item != null)
					item.Focus();
				PendingFocusNode = null;
			}
		}

		protected override DependencyObject GetContainerForItemOverride()
		{
			return new TreeListItem();
		}

		protected override bool IsItemItsOwnContainerOverride(object item)
		{
			return item is TreeListItem;
		}

		protected override void PrepareContainerForItemOverride(DependencyObject element, object item)
		{
			var ti = element as TreeListItem;
			var node = item as TreeNode;
			if (ti != null && node != null)
			{
				ti.Node = item as TreeNode;
				base.PrepareContainerForItemOverride(element, node.Tag);
			}
		}

		internal void SetIsExpanded(TreeNode node, bool value)
		{
			if (value)
			{
				if (!node.IsExpandedOnce)
				{
					node.IsExpandedOnce = true;
					node.AssignIsExpanded(value);
					CreateChildrenNodes(node);

                    if (RowExpanded != null)
                    {
                        RowExpanded(node);
                    }
				}
				else
				{
					node.AssignIsExpanded(value);
					CreateChildrenRows(node);
				}
			}
			else
			{
				DropChildrenRows(node, false);
				node.AssignIsExpanded(value);
			}
		}

		internal void CreateChildrenNodes(TreeNode node)
		{
			var children = GetChildren(node);
			if (children != null)
			{
				//int rowIndex = Rows.IndexOf(node);
                int rowIndex = Rows.ObservableRowItems.IndexOf(node);
				node.ChildrenSource = children as INotifyCollectionChanged;
                int alternateIndex = 0;
                foreach (object obj in children)
				{
					TreeNode child = new TreeNode(this, obj);
					child.HasChildren = HasChildren(child);

                    if (node.Parent == null)
                    {
                        // first level
                        child.AlternateIndex = alternateIndex;
                        alternateIndex = 1 - alternateIndex;
                    }
                    else
                    {
                        // same as parent
                        child.AlternateIndex = node.AlternateIndex;
                    }

					node.Children.Add(child);
				}
				//Rows.InsertRange(rowIndex + 1, node.Children.ToArray());
                Rows.ObservableRowItems.InsertRange(rowIndex + 1, node.Children.ToArray());
			}
		}

        internal void CreateExpandedChildrenNodes(TreeNode node)
        {
            var children = GetChildren(node);
            if (children != null)
            {
                //int rowIndex = Rows.IndexOf(node);
                int rowIndex = Rows.ObservableRowItems.IndexOf(node);
                node.ChildrenSource = children as INotifyCollectionChanged;
                foreach (object obj in children)
                {
                    TreeNode child = new TreeNode(this, obj);
                    child.HasChildren = HasChildren(child);
                    node.Children.Add(child);
                }

                //Rows.InsertRange(rowIndex + 1, node.Children.ToArray());
                TreeNode[] newRows = node.Children.ToArray();
                Rows.ObservableRowItems.InsertRange(rowIndex + 1, newRows);

                //expand all child nodes
                foreach (TreeNode item in newRows)
                {
                    item.AlternateIndex = node.AlternateIndex;

                    SetIsExpanded(item, true);

                    // expand children
                    var nodes = item.AllVisibleChildren.ToArray();
                    if (nodes != null)
                    {
                        foreach (TreeNode child in nodes)
                        {
                            child.AlternateIndex = node.AlternateIndex;

                            SetIsExpanded(child, true);
                        }
                    }
                }
            }
        }
        
        private void CreateChildrenRows(TreeNode node)
		{
			//int index = Rows.IndexOf(node);
            int index = Rows.ObservableRowItems.IndexOf(node);
			if (index >= 0 || node == _root) // ignore invisible nodes
			{
				var nodes = node.AllVisibleChildren.ToArray();
				//Rows.InsertRange(index + 1, nodes);
                Rows.ObservableRowItems.InsertRange(index + 1, nodes);
			}
		}

		internal void DropChildrenRows(TreeNode node, bool removeParent)
		{
			//int start = Rows.IndexOf(node);
            int start = Rows.ObservableRowItems.IndexOf(node);
			if (start >= 0 || node == _root) // ignore invisible nodes
			{
				int count = node.VisibleChildrenCount;
				if (removeParent)
					count++;
				else
					start++;
				//Rows.RemoveRange(start, count);
                Rows.ObservableRowItems.RemoveRange(start, count);
			}
		}

		private IEnumerable GetChildren(TreeNode parent)
		{
			if (Model != null)
				return Model.GetChildren(parent.Tag);
			else
				return null;
		}

		private bool HasChildren(TreeNode parent)
		{
			if (parent == Root)
				return true;
			else if (Model != null)
				return Model.HasChildren(parent.Tag);
			else
				return false;
		}

		internal void InsertNewNode(TreeNode parent, object tag, int rowIndex, int index)
		{
			TreeNode node = new TreeNode(this, tag);
			if (index >= 0 && index < parent.Children.Count)
				parent.Children.Insert(index, node);
			else
			{
				index = parent.Children.Count;
				parent.Children.Add(node);
			}
			//Rows.Insert(rowIndex + index + 1, node);
            Rows.ObservableRowItems.Insert(rowIndex + index + 1, node);
		}

        public void RefreshNode(TreeNode parentNode)
        {
            // delete all rows
            DropChildrenRows(parentNode, false);
            parentNode.Children.Clear();

            CreateExpandedChildrenNodes(parentNode);
        }

        public TreeNode GetNodeFromTag(object tag)
        {
            return Rows.ObservableRowItems.Where(x => (x.Tag == tag)).FirstOrDefault();
        }

        public void Sort(string sortColumn, ListSortDirection sortDir)
        {
            if (_model != null)
            {
                // create is list of selected nodes.
                List<object> expandedTags = new List<object>();
                foreach (TreeNode node in Rows.ObservableRowItems)
                {
                    if (node.IsExpanded)
                        expandedTags.Add(node.Tag);
                }

                _model.Sort(sortColumn, (sortDir == ListSortDirection.Ascending));
                _root.Children.Clear();
                Rows.ObservableRowItems.Clear();
                CreateChildrenNodes(_root);

                List<TreeNode> currentNodes = Rows.ObservableRowItems.ToList<TreeNode>();
                foreach (TreeNode node in currentNodes)
                {
                    if (expandedTags.Exists(o => o == node.Tag))
                        SetIsExpanded(node, true);
                }
            }
        }
	}
}
