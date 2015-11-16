/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 30, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.wado;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractBasePhotoIdImageServlet
extends AbstractBaseImageServlet
{
	private static final long serialVersionUID = 5671945087972517066L;
	private final static Logger logger = Logger.getLogger(AbstractBasePhotoIdImageServlet.class);
	
	/**
	 * Constructor of the object.
	 */
	public AbstractBasePhotoIdImageServlet() 
	{
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() 
	{
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	
	protected Logger getLogger()
	{
		return logger;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		String name = (getWebAppVersion() == null ? getWebAppName() : getWebAppName() + " " + getWebAppVersion());
		transactionContext.setRequestType(name + " photo Id transfer");
		String siteNumber = null;
		PatientIdentifier patientIdentifier = null;
		try
		{
			siteNumber = getSiteNumber(request);
			patientIdentifier = getPatientIdentifier(request);
		}
		catch(MethodException mX)
		{
			getLogger().error(mX);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, mX.getMessage());
			return;
			
		}
		if(siteNumber == null || patientIdentifier == null)
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The patient was not specified in the URL and must be.");
			return;
		}
		transactionContext.setPatientID(patientIdentifier.toString());
		transactionContext.setQueryFilter("n/a");
		OutputStream outStream = response.getOutputStream();
		
		getLogger().info("Requesting photo ID for patient '" + patientIdentifier + "' from site '" + siteNumber + "'.");
		
		try
		{
			long bytesTransferred = streamPatientIdImageByPatientIcn(siteNumber, patientIdentifier, outStream);
			transactionContext.setEntriesReturned( bytesTransferred==0 ? 0 : 1 );
			transactionContext.setFacadeBytesSent(bytesTransferred);
			transactionContext.setResponseCode(HttpServletResponse.SC_OK + "");
		}
		catch(ImageServletException isX)
		{
			String msg = isX.getMessage();
			logger.error(msg);
			transactionContext.setResponseCode(isX.getResponseCode() + "");
			transactionContext.setErrorMessage(msg);
			response.sendError(isX.getResponseCode(), isX.getMessage());
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			String msg = "SecurityCredentials expired: " + sceX.getMessage();
			// logging of error already done
			// just need to set appropriate error code
			transactionContext.setResponseCode(HttpServletResponse.SC_PRECONDITION_FAILED + "");
			transactionContext.setErrorMessage(msg);
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, msg);
		}
	}
	
	protected abstract String getWebAppName();
	
	protected abstract String getWebAppVersion();
	
	protected abstract String getSiteNumber(HttpServletRequest request)
	throws MethodException;
	
	protected abstract PatientIdentifier getPatientIdentifier(HttpServletRequest request)
	throws MethodException;
	
	
}
