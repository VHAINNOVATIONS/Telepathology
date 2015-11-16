/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: December 10, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
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
package gov.va.med.imaging.dicom.utilities.exceptions;

/**
 *
 * @author William Peterson
 *
 */
public class GenericDicomUtilitiesTGAFileException extends GenericDicomUtilitiesException {

    /**
     * Constructor
     *
     * 
     */
    public GenericDicomUtilitiesTGAFileException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param message
     */
    public GenericDicomUtilitiesTGAFileException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param cause
     */
    public GenericDicomUtilitiesTGAFileException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     *
     * @param message
     * @param cause
     */
    public GenericDicomUtilitiesTGAFileException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
