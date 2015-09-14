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
using System.Windows.Shapes;
using System.Windows.Threading;
using System.ComponentModel;

namespace VistA.Imaging.Telepathology.Worklist.Views
{
    /// <summary>
    /// Interaction logic for MagTimeout.xaml
    /// </summary>
    public partial class EditReportTimeout : Window, INotifyPropertyChanged
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

        private int timeLeft = 60;

        public EditReportTimeout()
        {
            InitializeComponent();

            TimeLeft = String.Format("{0} seconds.", timeLeft);
            Terminate = true;
            DispatcherTimer timer = new DispatcherTimer();
            timer.Interval = TimeSpan.FromSeconds(1); // each tick is 1 second
            timer.Tick += new EventHandler(timer_Tick);
            timer.Start();
        }

        void timer_Tick(object sender, EventArgs e)
        {
            timeLeft--;
            TimeLeft = String.Format("{0} seconds.", timeLeft);

            if (timeLeft <= 0)
            {
                Terminate = true;
                Close();
            }
        }

        private void TerminateApplication_Click(object sender, RoutedEventArgs e)
        {
            Terminate = true;
            Close();
        }

        private void StayInReport_Click(object sender, RoutedEventArgs e)
        {
            Terminate = false;
            Close();
        }

        public bool Terminate 
        { 
            get; 
            set; 
        }

        public string TimeLeft
        {
            get;
            set;
        }
    }
}
