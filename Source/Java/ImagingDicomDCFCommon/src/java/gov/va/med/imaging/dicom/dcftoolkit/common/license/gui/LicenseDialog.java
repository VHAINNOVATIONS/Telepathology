/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: October 5, 2005
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

import gov.va.med.imaging.dicom.dcftoolkit.common.license.KeyFilter;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
/**
 *
 * @author William Peterson
 *
 */
public class LicenseDialog {
    
    private static final Logger logger = Logger.getLogger (LicenseDialog.class);
    private boolean cancellation = false;

   /**
     * Constructor
     *
     * 
     */
    public LicenseDialog() {
        super();
        // 
    }

    public File browseForFile(Component parent) { 
        File filename = null;

        //Create FileChooser object
        JFileChooser chooser = new JFileChooser();
        //Setup JFileChooser.
        chooser.setFileFilter(new KeyFilter());
        chooser.setCurrentDirectory(new File("."));
        chooser.setMultiSelectionEnabled(false);
        
        //Use FileChooser to select correct file.        
        int result = chooser.showOpenDialog(parent);
        if(result == JFileChooser.APPROVE_OPTION){
            filename = chooser.getSelectedFile();
        }
        return filename;
    } 
       
    public void loadFile(File filename){
        String rootPath = "";
        try{
            //get the correct path via environment variable            
            Process pc = Runtime.getRuntime().exec("cmd.exe /c echo %DCF_ROOT%");
            BufferedReader br = new BufferedReader
                 ( new InputStreamReader( pc.getInputStream() ) );
            rootPath = br.readLine();
            
            //Create path to new license location.
            String nuPath = rootPath+"\\cfg\\systeminfo";
            File oldFile = new File(nuPath);
            //Delete current systeminfo file.
            oldFile.delete();
            
            // Create channel on the source
            FileChannel srcChannel = new FileInputStream(filename).getChannel();
        
            // Create channel on the destination
            FileChannel dstChannel = new FileOutputStream(oldFile).getChannel();
        
            // Copy file contents from source to destination
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        
            // Close the channels
            srcChannel.close();
            dstChannel.close();

        }
        catch(IOException ioe){
            //   
        }
    }
        
    public void cancel(){
        this.cancellation = true;
    }
    
    public boolean isCancelled(){
        return this.cancellation;
    }
}
