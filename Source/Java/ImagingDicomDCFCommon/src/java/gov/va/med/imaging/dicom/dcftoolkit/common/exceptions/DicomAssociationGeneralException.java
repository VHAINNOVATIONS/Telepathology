/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 25, 2005
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

package gov.va.med.imaging.dicom.dcftoolkit.common.exceptions;

/**
 *
 * @author William Peterson
 *
 */
public class DicomAssociationGeneralException extends DCFDicomException {

    /**
     * Constructor
     *
     * 
     */
    public DicomAssociationGeneralException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param message
     */
    public DicomAssociationGeneralException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param cause
     */
    public DicomAssociationGeneralException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param message
     * @param cause
     */
    public DicomAssociationGeneralException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
