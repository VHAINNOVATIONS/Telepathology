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

package gov.va.med.imaging.exceptions;

/**
 * @author beckey
 * created: Dec 17, 2004 at 3:09:34 PM
 *
 * This class is the root of all Imaging exceptions.
 * 
 */
public abstract class ImagingException
extends Exception
{

    /**
     * 
     */
    public ImagingException()
    {
        super();
    }

    /**
     * @param message
     */
    public ImagingException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ImagingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ImagingException(Throwable cause)
    {
        super(cause);
    }
}
