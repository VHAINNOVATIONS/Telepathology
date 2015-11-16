/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: october 5, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETRB
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+
 */
package gov.va.med.imaging.dicom.dcftoolkit.common.license.gui;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class LicenseDialogView extends JFrame implements ActionListener {

    
    private static final Logger logger = Logger.getLogger (LicenseDialogView.class);

    protected JTextField licenseKeyField;
    protected JButton browseButton;
    protected JButton loadButton;
    protected JButton cancelButton;
    
    private LicenseDialog dialog;
    
    private File file;

    /**
     * Constructor
     *
     * @throws java.awt.HeadlessException
     */
    public LicenseDialogView() throws HeadlessException {
        super();
        // 
    }

    /**
     * Constructor
     *
     * @param gc
     */
    public LicenseDialogView(GraphicsConfiguration gc) {
        super(gc);
        //
    }

    /**
     * Constructor
     *
     * @param title
     * @throws java.awt.HeadlessException
     */
    public LicenseDialogView(String title) throws HeadlessException {
        super(title);
        //
    }

    /**
     * Constructor
     *
     * @param title
     * @param gc
     */
    public LicenseDialogView(String title, GraphicsConfiguration gc) {
        super(title, gc);
        //
    }
    
    public LicenseDialogView(LicenseDialog dlg) {
        super("Laurel Bridge DICOM Toolkit License Installer");
        setSize(480, 180);
        dialog = dlg;
        this.setWindows();
        addControls();
        browseButton.addActionListener( this );
        loadButton.addActionListener(this);
        cancelButton.addActionListener( this );
     }



    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        //
        String cmd = e.getActionCommand();
        
        if(cmd.equals("Browse")){
            //Call LicenseDialog to open FileChooser.
            this.file = dialog.browseForFile(this);
            //Place the selected filename into the textbox
            if(!(this.file == null)){
                this.licenseKeyField.setText(this.file.getName());
            }
        }

        if (cmd.equals("Load")){
            //Get filename String from textbox.
            dialog.loadFile(this.file);
            setVisible(false);
        }

        if (cmd.equals("Cancel")){
            this.dispose();
        }
    }
    private void addControls() {
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        //contentPane.setLayout(new FlowLayout());
        
        //FUTURE Add status line and have it show various stages of logging on.
        
        c.fill = GridBagConstraints.NONE;
        //c.anchor = GridBagConstraints.NORTH;
        JLabel label3 = new JLabel("License File:", Label.RIGHT);
        label3.setFont(this.setFont());
        c.gridx = 0;
        c.gridy = 0;
        contentPane.add(label3, c);
        licenseKeyField = new JTextField("", 32);
        licenseKeyField.setMinimumSize(new Dimension(280, 24));
        licenseKeyField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        c.gridx = 1;
        //c.gridx = 1;
        contentPane.add(licenseKeyField, c);
        
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new Insets(40, 2, 5, 2);
        c.ipadx = 12;
        c.ipady = 6;
        loadButton = new JButton("Load");
        loadButton.setFont(this.setFont());
        c.gridx = 0; 
        c.gridy = 3;
        contentPane.add(loadButton, c);

        browseButton = new JButton("Browse");
        browseButton.setFont(this.setFont());
        c.gridx = 1;
        contentPane.add(browseButton, c);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(this.setFont());
        c.gridx = 2;
        contentPane.add(cancelButton, c);
        
     }
    
    private void setWindows(){
        try{
            //UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch(Exception e){
            //
        }
    }
    
    private Font setFont(){
        return new Font("SansSerif", Font.BOLD, 12);
    }
}
