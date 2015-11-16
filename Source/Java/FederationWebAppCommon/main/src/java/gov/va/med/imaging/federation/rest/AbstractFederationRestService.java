/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 19, 2010
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
package gov.va.med.imaging.federation.rest;

import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationRestService 
{
	private final static Logger logger = Logger.getLogger(AbstractFederationRestService.class);
	
	protected Logger getLogger()
	{
		return logger;
	}
	
	protected abstract String getInterfaceVersion();
	
	/**
	 * Wraps a result in a Response and includes any necessary responsee headers
	 * @param result
	 * @return
	 */
	protected Response wrapResultWithResponseHeaders(Object result)
	{		
		return Response.status(Status.OK).header(TransactionContextHttpHeaders.httpHeaderMachineName, 
				TransactionContextFactory.get().getMachineName()).entity(result).build();
	}
}
