/*
 * Created on Apr 27, 2005
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
 * This exception is thrown when there is a problem while changing the status of
 * listening on a selected TCP Port#.  This exception may occur when either stopping
 * the listening object, starting the listening object, or changing the DCF 
 * Association Manager.  It is also thrown when catching a DCF Exception
 * during the listening status change.  
 *
 *
 * @author William Peterson
 *
 */
public class DicomListenException extends Exception {

    //CODEME Add coding to exceptions to improve messaging for support.
    /**
     * 
     */
    public DicomListenException() {
        super();
    }

    /**
     * @param message
     */
    public DicomListenException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public DicomListenException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public DicomListenException(String message, Throwable cause) {
        super(message, cause);
    }

}
