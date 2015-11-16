/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 16, 2010
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
package gov.va.med.imaging.web.rest.exceptions;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.rest.exceptions.RestExceptionCodes;

import javax.ws.rs.ext.Provider;

/**
 * @author vhaiswwerfej
 *
 */
@Provider
public class RestConnectionExceptionMapper 
extends AbstractRestExceptionMapper<ConnectionException>
{
	// JMW 12/15/2010 P104 - For some reason, the ExceptionMapper is not working to map
	// the exception properly when a parent also has an exception mapper, this prevents
	// the appropriate status code from being sent. Checking for the specific exception
	// type as done below ensures the proper status code is sent
	
	@Override
	public Throwable getRelevantException(ConnectionException exception)
	{
		Throwable cause = exception.getCause();
		if(cause != null && cause instanceof SecurityCredentialsExpiredException)
			return cause;
		return exception;
	}

	@Override
	protected int getStatusCode(Throwable relevantException)
	{
		if(relevantException instanceof SecurityCredentialsExpiredException)
			return RestExceptionCodes.restInvalidSecurityCredentialsExceptionCode;
		
		return RestExceptionCodes.restConnectionExceptionCode;
	}
}
