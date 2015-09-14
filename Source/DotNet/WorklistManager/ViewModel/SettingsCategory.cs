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

using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Windows.Controls;

namespace VistA.Imaging.Telepathology.Worklist.ViewModel
{
    public class SettingsCategory : INotifyPropertyChanged
    {
        public SettingsCategory()
        {
            this.Title = string.Empty;
            this.Data = null;
            this.Children = new ObservableCollection<SettingsCategory>();
            this.IsSelected = false;
        }
        public event PropertyChangedEventHandler PropertyChanged;

        public string Title { get; set; }

        public object Data { get; set; }

        public bool IsSelected { get; set; }

        public ObservableCollection<SettingsCategory> Children { get; set; }

        //readonly ObservableCollection<SettingsCategory> _children = new ObservableCollection<SettingsCategory>();

        //public ObservableCollection<SettingsCategory> Children { get { return _children; } }
    }
}
