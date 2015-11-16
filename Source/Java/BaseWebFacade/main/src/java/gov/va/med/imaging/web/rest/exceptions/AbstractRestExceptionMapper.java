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

import gov.va.med.imaging.rest.exceptions.RestExceptionMessage;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractRestExceptionMapper<E extends Throwable>
implements ExceptionMapper<E>
{

	@Override
	public Response toResponse(E exception) 
	{
		Throwable relevantException = getRelevantException(exception);
		return Response.status(getStatusCode(relevantException)).
			header(TransactionContextHttpHeaders.httpHeaderMachineName, 
					TransactionContextFactory.get().getMachineName()).
			entity(new RestExceptionMessage(relevantException)).type(MediaType.APPLICATION_XML_TYPE).build();
	}

	/**
	 * Get the appropriate status code based on the exception passed, the exception passed should be the result of
	 * getRelevantException(). This method should look at the provided exception to return the appropriate status code
	 *  
	 * @param relevantException
	 * @return
	 */
	protected abstract int getStatusCode(Throwable relevantException);
	
	/**
	 * If the exception wraps another exception, this method should return the exception that is cared about and should
	 * be thrown to the requester. This method should either throw the exception as is or an exception it wraps
	 * 
	 * @param exception
	 * @return
	 */
	public abstract Throwable getRelevantException(E exception);
}
