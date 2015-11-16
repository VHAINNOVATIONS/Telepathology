/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 5, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * Exception occurs if an image (binary) was requested from the cache but not found in the cache.
 * This exception should only be used in specific cases (such as commands).  It does not
 * need to be used in all cache situations where an image is not found, other situations might
 * allow for more elegant handling rather than an exception handler. 
 * 
 * @author vhaiswwerfej
 *
 */
public class ImageNotCachedException 
extends MethodException
{
	private final static long serialVersionUID = 1L;
	
	public ImageNotCachedException()
	{
		super();
	}
	
	public ImageNotCachedException(String msg)
	{
		super(msg);
	}
	
	public ImageNotCachedException(Throwable cause)
	{
		super(cause);
	}
	
	public ImageNotCachedException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
