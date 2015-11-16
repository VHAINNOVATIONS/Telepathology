/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Apr 17, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.wado;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * @deprecated JMW - I don't think this is used anywhere, prefer to extend AbstractBasePhotoIdImageServlet rather than use this one
 *
 */
public class PhotoIdentificationServlet 
extends AbstractBaseImageServlet
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 
	 */
	public PhotoIdentificationServlet()
	{
	}

	/**
	 * @see gov.va.med.imaging.wado.AbstractBaseImageServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException
	{
		TransactionContext transactionContext = TransactionContextFactory.get(); 
		String pathInfo = req.getPathInfo();
		if(pathInfo == null || pathInfo.isEmpty())
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The patient ICN was not specified in the URL and must be.");
		}
		else
		{
			pathInfo = pathInfo.substring(1);
			String[] resourceIds = pathInfo.split("/");
			
			String siteNumber = null;
			String patientIcn = null;
			
			if(resourceIds.length > 1)
			{
				siteNumber = resourceIds[0];
				patientIcn = resourceIds[1];
			}
			else
			{
				siteNumber = transactionContext.getRealm();	// default to our local site number
				patientIcn = resourceIds[0];
			}
			logger.info("Getting patient ID image '" + patientIcn + "' from site '" + siteNumber + "'.");
			try
	        {
				
		    	streamPatientIdImageByPatientIcn(siteNumber, 
		    			PatientIdentifier.icnPatientIdentifier(patientIcn), resp.getOutputStream());
	        } 
			catch (ImageServletException isX)
	        {
				resp.sendError(isX.getResponseCode(), isX.getMessage());
	        }
			catch(Exception ex)
			{
				logger.error(ex.getMessage(), ex);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.wado.AbstractBaseImageServlet#getUserSiteNumber()
	 */
	@Override
	public String getUserSiteNumber() 
	{
		TransactionContext context = TransactionContextFactory.get();
		return context.getLoggerSiteNumber();
	}
}
