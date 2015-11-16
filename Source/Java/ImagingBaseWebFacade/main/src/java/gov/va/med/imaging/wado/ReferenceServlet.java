package gov.va.med.imaging.wado;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.InstanceChecksumNotification;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.ImageMetadata;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.wado.AbstractBaseImageServlet;
import gov.va.med.imaging.wado.AbstractBaseImageServlet.ImageServletException;

public class ReferenceServlet 
extends AbstractBaseImageServlet
{
	private static final long serialVersionUID = 1L;
	private static List<ImageFormat> referenceResponseType;
	private Logger logger = Logger.getLogger(this.getClass());
	
	static
	{
		 referenceResponseType = new ArrayList<ImageFormat>();
		 referenceResponseType.add(ImageFormat.JPEG);
	}

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException
    {
		String pathInfo = req.getPathInfo();
		if(pathInfo == null || pathInfo.isEmpty())
		{
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The image URN was not specified in the URL and must be.");
		}
		else
		{
			String imageIdentifier = pathInfo.charAt(0) == '/' ? pathInfo.substring(1) : pathInfo;
			logger.info("Getting reference image '" + pathInfo + "'.");
			try
	        {
		        ImageURN imageUrn = URNFactory.create(imageIdentifier, ImageURN.class);
		    	long bytesTransferred = streamImageInstanceByUrn(
		    			imageUrn, ImageQuality.REFERENCE,
		    			referenceResponseType, 
		    			resp.getOutputStream(),
		    			new MetadataNotification(resp) );
	        } 
			catch (URNFormatException e)
	        {
				logger.info(e.getMessage());
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "'" + imageIdentifier + "' is not a valid URN.");
	        } 
			catch (ImageServletException isX)
	        {
				logger.error(isX.getMessage(), isX);
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
