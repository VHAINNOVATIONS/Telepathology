/**
 * 
 */
package gov.va.med.imaging.datasource.exceptions;

/**
 * @author VHAISWBECKEC
 *
 */
public class UnsupportedProtocolException 
extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public UnsupportedProtocolException()
    {
	super();
    }

    /**
     * @param message
     */
    public UnsupportedProtocolException(String message)
    {
	super(message);
    }

    /**
     * @param cause
     */
    public UnsupportedProtocolException(Throwable cause)
    {
	super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public UnsupportedProtocolException(String message, Throwable cause)
    {
	super(message, cause);
    }

}
