/*
 * Created on Sep 15, 2005
// Per VHA Directive 2004-038, this routine should not be modified.
//+---------------------------------------------------------------+
//| Property of the US Government.                                |
//| No permission to copy or redistribute this software is given. |
//| Use of unreleased versions of this software requires the user |
//| to execute a written test agreement with the VistA Imaging    |
//| Development Office of the Department of Veterans Affairs,     |
//| telephone (301) 734-0100.                                     |
//|                                                               |
//| The Food and Drug Administration classifies this software as  |
//| a medical device.  As such, it may not be changed in any way. |
//| Modifications to this software may result in an adulterated   |
//| medical device under 21CFR820, the use of which is considered |
//| to be a violation of US Federal Statutes.                     |
//+---------------------------------------------------------------+
 *
 */
package gov.va.med.imaging.dicom.dcftoolkit.common.exceptions;


/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DicomMoveSCPException extends DCFDicomException {

    /**
     * Constructor
     *
     * 
     */
    public DicomMoveSCPException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message
     */
    public DicomMoveSCPException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause
     */
    public DicomMoveSCPException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param message
     * @param cause
     */
    public DicomMoveSCPException(String message, Throwable cause) {
        super(message, cause);
    }

}
