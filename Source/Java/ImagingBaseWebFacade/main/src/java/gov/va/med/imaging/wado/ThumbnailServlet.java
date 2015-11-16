package gov.va.med.imaging.wado;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import gov.va.med.URNFactory;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.TransactionContextHelper;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.wado.AbstractBaseImageServlet;

public class ThumbnailServlet 
extends AbstractBaseImageServlet
{
	private static final long serialVersionUID = 1L;
	private static List<ImageFormat> thumbnailResponseType;
	private Logger logger = Logger.getLogger(this.getClass());
	
	static
	{
		 thumbnailResponseType = new ArrayList<ImageFormat>();
		 thumbnailResponseType.add(ImageFormat.JPEG);
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
			TransactionContext transactionContext = TransactionContextFactory.get();
			logger.info("Getting thumbnail image '" + pathInfo + "'.");
			try
	        {
		        ImageURN imageUrn = URNFactory.create(imageIdentifier, ImageURN.class);
		        TransactionContextHelper.setTransactionContextFields("getThumbnail", imageUrn.getPatientId(), imageIdentifier);
		    	long bytesTransferred = streamImageInstanceByUrn(
		    			imageUrn, ImageQuality.THUMBNAIL,
		    			thumbnailResponseType, 
		    			resp.getOutputStream(),
		    			new MetadataNotification(resp) );
		    	
		    	transactionContext.setEntriesReturned( bytesTransferred==0 ? 0 : 1 );
				transactionContext.setFacadeBytesSent(bytesTransferred);
				transactionContext.setResponseCode(HttpServletResponse.SC_OK + "");
	        } 
			catch (URNFormatException e)
	        {
				logger.info(e.getMessage());
				transactionContext.setResponseCode(HttpServletResponse.SC_BAD_REQUEST + "");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "'" + imageIdentifier + "' is not a valid URN.");
	        } 
			catch (ImageServletException isX)
	        {
				logger.error(isX.getMessage(), isX);
				transactionContext.setResponseCode(isX.getResponseCode() + "");
				resp.sendError(isX.getResponseCode(), isX.getMessage());
	        }
			catch(Exception ex)
			{
				logger.error(ex.getMessage(), ex);
				transactionContext.setResponseCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "");
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
