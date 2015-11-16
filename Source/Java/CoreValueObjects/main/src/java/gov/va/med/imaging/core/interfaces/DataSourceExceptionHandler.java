/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 21, 2009
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
package gov.va.med.imaging.core.interfaces;

/**
 * Interface type for handling exceptions. By implementing this interface the object will decide if it can
 * handle a specific exception and then attempt to handle it
 * 
 * @author vhaiswwerfej
 *
 */
public interface DataSourceExceptionHandler 
{	
	/**
	 * Determines if a specific exceptionk can be handled.  Usually implemented by looking at the class
	 * of the exception
	 * @param ex
	 * @return
	 */
	public boolean isExceptionHandled(Exception ex);
	
	/**
	 * Attempt to handle the exception
	 * @param ex
	 * @return True if the exception was handled in some way, false if it was not handled and the exception should then be thrown.
	 */
	public boolean handleException(Exception ex);

}
