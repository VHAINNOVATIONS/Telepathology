/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 26, 2009
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
package gov.va.med.imaging.webservices.common;

import gov.va.med.imaging.core.interfaces.exceptions.CompositeMethodException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;

/**
 * Common methods for all web services - should not be specific to a particular service.
 * 
 * @author vhaiswwerfej
 *
 */
public class WebservicesCommon 
{
	/**
	 * If the MethodException is wrapping a SecurityCredentialsExpiredException exception, throw the
	 * SecurityCredentialsExpiredException with the correct message.  Otherwise this method will do 
	 * nothing and the MethodException should be thrown as appropriate
	 * 
	 * @param mX MethodException to examine
	 * @throws SecurityCredentialsExpiredException
	 */
	public static void throwSecurityCredentialsExceptionFromMethodException(MethodException mX)
	throws SecurityCredentialsExpiredException
	{
		if(mX instanceof CompositeMethodException)
		{
			CompositeMethodException cme = (CompositeMethodException)mX;
			SecurityCredentialsExpiredException t = cme.findException(SecurityCredentialsExpiredException.class, true);
			if(t != null)
				throw new SecurityCredentialsExpiredException(t.getMessage());
		}
		else if(mX.getClass() == MethodException.class)
		{
			if((mX.getCause() != null) 
					&& (mX.getCause().getClass() == SecurityCredentialsExpiredException.class))
			{
				throw new SecurityCredentialsExpiredException(mX.getCause().getMessage());
			}
		}
	}

}
