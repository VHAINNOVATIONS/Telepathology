/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: June 1, 2006
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
package gov.va.med.imaging.dicom.dcftoolkit.common.license;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 *
 * @author William Peterson
 *
 */
public class KeyFilter extends FileFilter {

    /**
     * Constructor
     *
     * 
     */
    public KeyFilter() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        // TODO Auto-generated method stub
        
        return f.getName().toLowerCase().endsWith(".key")
            ||f.isDirectory();
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        // TODO Auto-generated method stub
        return "Key Files";
    }

}
