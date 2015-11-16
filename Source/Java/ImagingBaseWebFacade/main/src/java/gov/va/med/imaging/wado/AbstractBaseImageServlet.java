package gov.va.med.imaging.wado;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientArtifactIdentifierImpl;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.ImagingBaseWebFacadeRouter;
import gov.va.med.imaging.InstanceChecksumNotification;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.channels.AbstractBytePump.TRANSFER_TYPE;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.AbstractBaseServlet;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageMetadata;
import gov.va.med.imaging.exchange.business.documents.DocumentRetrieveResult;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;
import gov.va.med.imaging.wado.query.WadoRequest;
import gov.va.med.imaging.webservices.common.WebservicesCommon;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * The abstract root of Image providing servlets in the VIX architecture.
 * 
 * @author VHAISWBECKEC
 */
public abstract class AbstractBaseImageServlet 
extends AbstractBaseServlet
{
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(AbstractBaseImageServlet.class);
	
	protected Logger getLogger()
	{
		return logger;
	}

	/**
	 * Constructor of the object.
	 */
	public AbstractBaseImageServlet()
	{
		super();
	}
	
	/**
	 * Method to get the users site number, must be implemented by extending class
	 * @return
	 */
	public abstract String getUserSiteNumber();

	/**
	 * Returns information about the servlet, such as 
	 * author, version, and copyright. 
	 *
	 * @return String information about this servlet
	 */
	public String getServletInfo()
	{
		return "The abstract root of Image providing servlets in the VIX architecture.";
	}

	/**
	 * Derived servlets must implement a doGet method
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
    protected abstract void doGet(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException;
	
	/**
	 * 
	 * @param resp
	 * @param imageUrn
	 * @param contentType
	 * @return
	 * @throws IOException
	 */	
	protected long streamImageInstanceByUrn(
			ImageURN imageUrn, 
			ImageQuality requestedImageQuality,
			List<ImageFormat> acceptableResponseContent, 
			OutputStream outStream,
			ImageMetadataNotification checksumNotification)
	throws IOException, SecurityCredentialsExpiredException, ImageServletException
	{
		return streamImageInstanceByUrn(imageUrn, requestedImageQuality, 
			acceptableResponseContent, outStream, checksumNotification, true);
	}
	
	/**
	 * 
	 * @param imageUrn
	 * @param requestedImageQuality
	 * @param acceptableResponseContent
	 * @param outStream
	 * @param checksumNotification
	 * @param logImageAccess
	 * @return
	 * @throws IOException
	 * @throws ImageServletException
	 */
	protected long streamImageInstanceByUrn(
			ImageURN imageUrn, 
			ImageQuality requestedImageQuality,
			List<ImageFormat> acceptableResponseContent, 
			OutputStream outStream,
			ImageMetadataNotification checksumNotification,
			boolean logImageAccess)
	throws IOException, SecurityCredentialsExpiredException, ImageServletException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		ImageFormatQualityList qualityList = new ImageFormatQualityList();
		qualityList.addAll(acceptableResponseContent, requestedImageQuality);
		
		getLogger().info("Getting image instance by URN [" + imageUrn.toString() + "] with request format: " + qualityList.getAcceptString(true, true));
		long bytesTransferred = 0;
		try 
		{
			bytesTransferred = (Long)getRouter().getInstanceByImageURN(
						imageUrn, 
						qualityList, 
						outStream, 
						checksumNotification);
			getLogger().debug("Wrote [" + bytesTransferred + "] bytes to output stream for image [" + imageUrn.toString() + "]");
			
			
			if(logImageAccess)
			{
				if(requestedImageQuality != ImageQuality.THUMBNAIL)
				{
					
					try
					{
						ImageAccessLogEvent logEvent = createImageAccessLogEvent(imageUrn);
						getLogger().info("Got image, logging image access (" + logEvent.getEventType().toString() + ") for " +
								"image [" + imageUrn.toString() + "] by ["
								//+ TransactionContextFactory.get().getTransactionId() + "].");
								//+ this.transactionId + "].");				
								+ transactionContext.getTransactionId() + "].");
						getRouter().logImageAccessEventRetryable(logEvent);
					}
					catch(ServletException sX)
					{
						getLogger().error("Error logging image access event, " + sX.getMessage(), sX);
					}
				}
			}
		} 
		catch (ImageNearLineException inle)
		{
			String message = "Image [" + imageUrn.toString() + "] found only in off-line storage.\n" +
				"Please resubmit image access request later, operator has been notified to load media.";
			getLogger().debug(message);
			transactionContext.setExceptionClassName(inle.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_CONFLICT, message);
		}
		catch (ImageConversionException ice) 
		{
			String message = 
				"Image [" + imageUrn.toString() + 
				"] found in a different format and conversion to the requested type failed.\n" + 
				ice.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(ice.getClass().getSimpleName());
			// JMW 6/23/08 - if there is an image conversion error, it will come out as an internal server error (500)
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ImageNotFoundException infX)
		{
			String message = "Image [" + imageUrn.toString() + "] not found.\n" + infX.getMessage();
			//TransactionContextFactory.get().setErrorMessage(message); // not needed here			
			getLogger().debug(message);
			transactionContext.setExceptionClassName(infX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);
		}
		catch(MethodException mX)
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				mX.getMessage();
			getLogger().error(message, mX);
			handleMethodException(mX);
			// the MethodException is not a SecurityCredentialsExpiredException and must still be handled.
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());			
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(Exception x) 
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				x.getMessage();
			getLogger().error(message, x);
			transactionContext.setExceptionClassName(x.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		return bytesTransferred;
	}

	/**
	 * This method will attempt to convert the MethodException into a SecurityCredentialsException
	 * and then throw the appropriate ImageServletException which contains an appropriate error code
	 * indicating the credentials exception.
	 * 
	 * If the MethodException cannot be converted into a SecurityCredentialsException then this method
	 * does nothing and the MethodException must be handled.
	 * 
	 * @param mX
	 * @throws ImageServletException
	 */
	protected void handleMethodException(MethodException mX)
	throws SecurityCredentialsExpiredException  
	{
		WebservicesCommon.throwSecurityCredentialsExceptionFromMethodException(mX);
		/*
		try
		{
			WebservicesCommon.throwSecurityCredentialsExceptionFromMethodException(mX);
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			TransactionContextFactory.get().setExceptionClassName(sceX.getClass().getSimpleName());
			// precondition failed (412) error
			throw new SecurityCredentialsExpiredException(HttpServletResponse.SC_PRECONDITION_FAILED, sceX.getMessage());
		}*/
	}
	
	protected SecurityCredentialsExpiredException findSecurityCredentialsException(MethodException mX)
	{
		try
		{
			WebservicesCommon.throwSecurityCredentialsExceptionFromMethodException(mX);
		}
		catch(SecurityCredentialsExpiredException sceX)
		{
			return sceX;
		}
		return null;
	}
	
	private ImageAccessLogEvent createImageAccessLogEvent(ImageURN imageUrn)
	{
		getLogger().info("RouterImpl.logImageAccess(" + imageUrn.toString() + ") by [" + TransactionContextFactory.get().getTransactionId() + "].");
		String imageId = imageUrn.getImageId();
		String siteNumber = imageUrn.getOriginatingSiteId();
		
		boolean dodImage = false;		
		if(ExchangeUtil.isSiteDOD(siteNumber))
			dodImage = true;

		String userSiteNumber = getUserSiteNumber();

		// JMW 12/27/2012 - this is only used for Exchange/XCA requests, not for internal VA (Clin Disp, VRad, etc) requests. As a result the patient ID should always be an ICN
		ImageAccessLogEvent logEvent = new ImageAccessLogEvent(imageId, "",
				imageUrn.getPatientId(), imageUrn.getOriginatingSiteId(), System.currentTimeMillis(), 
				"", "", ImageAccessLogEventType.IMAGE_ACCESS, dodImage, userSiteNumber);
		return logEvent;
	}
	
	protected int streamTxtFileInstanceByUrn(
			ImageURN imageUrn, 			
			OutputStream outStream,
			ImageMetadataNotification metadataNotification)
	throws IOException, ImageServletException, SecurityCredentialsExpiredException
	{
		getLogger().debug("Getting TXT file instance by URN [" + imageUrn.toString() + "]");
		TransactionContext transactionContext = TransactionContextFactory.get();
		try 
		{			
			int bytesReturned = 
				getRouter().getTxtFileByImageURN(imageUrn, 
					outStream, metadataNotification);
			getLogger().debug("Pushing TXT file instance by URN [" + imageUrn.toString() + "]..");
			return bytesReturned;
		}	
		catch(ImageNotFoundException infX)
		{						
			getLogger().error("Error getting TXT file for URN [" + imageUrn.toString() + "]", infX);				
			String message = "Error getting TXT file for URN [" + imageUrn.toString() + "].\n" + infX.getMessage();
			transactionContext.setErrorMessage(message);
			transactionContext.setExceptionClassName(infX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);						
		} 
		catch (ImageNearLineException inle)
		{
			String message = "TXT file [" + imageUrn.toString() + "] found only in off-line storage.\n" +
				"Please resubmit image access request later, operator has been notified to load media.";
			getLogger().debug(message);
			transactionContext.setExceptionClassName(inle.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_CONFLICT, message);
		}
		catch(MethodException mX) 
		{
			String message = 
				"Internal server error in accessing TXT file [" + imageUrn.toString() + "] \n" + 
				mX.getMessage();
			getLogger().debug(message);
			handleMethodException(mX);
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ConnectionException mX) 
		{
			String message = 
				"Internal server error in accessing TXT file [" + imageUrn.toString() + "] \n" + 
				mX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ServletException sX) 
		{
			String message = 
				"Internal server error in accessing TXT file [" + imageUrn.toString() + "] \n" + 
				sX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(sX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(Exception x) 
		{
			String message = 
				"Internal server error in accessing TXT file [" + imageUrn.toString() + "] \n" + 
				x.getMessage();
			getLogger().error(message, x);
			transactionContext.setExceptionClassName(x.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
	}
	
	/**
	 * This method actually streams the image to the result
	 * @param documentIdentifier
	 * @return
	 * @throws IOException
	 * @throws ImageServletException
	 */
	protected long streamDocument(GlobalArtifactIdentifier documentIdentifier, OutputStream outStream,
			ImageMetadataNotification checksumNotification)
	throws IOException, ImageServletException
	{
		DocumentRetrieveResult documentRetrieveResult = retrieveDocument(documentIdentifier, 
				checksumNotification);
		ByteStreamPump pump = ByteStreamPump.getByteStreamPump();
		InputStream input = documentRetrieveResult.getDocumentStream();
		long bytes = 0L;
		
		try
		{
			bytes = pump.xfer(input, outStream);
		}
		finally
		{
			// must close input stream or causes problems in the cache
			try
			{
				if(input != null)
					input.close();
			}
			catch(IOException ioX)
			{
				getLogger().warn("IOException caught when closing document stream '" + documentIdentifier.toString() + ", " + ioX.getMessage());
			}
		}
		//TODO: use checksumNotification - need to add to use in command
		return bytes;
	}
	
	/**
	 * 
	 * @deprecated Prefer to use retrieveDocument(GlobalArtifactIdentifier documentIdentifier,
			ImageMetadataNotification checksumNotification)
	 * @param documentIdentifier
	 * @return
	 * @throws IOException
	 * @throws ImageServletException 
	 */
	@Deprecated
	protected DocumentRetrieveResult retrieveDocument(GlobalArtifactIdentifier documentIdentifier)
	throws IOException, ImageServletException
	{
		return retrieveDocument(documentIdentifier, null);
	}
	
	/**
	 * 
	 * @param documentIdentifier
	 * @return
	 * @throws IOException
	 * @throws ImageServletException
	 */
	protected DocumentRetrieveResult retrieveDocument(GlobalArtifactIdentifier documentIdentifier,
			ImageMetadataNotification checksumNotification)
	throws IOException, ImageServletException
	{
		getLogger().debug("Getting Document instance by identifier [" + documentIdentifier.toString() + "]");
		TransactionContext transactionContext = TransactionContextFactory.get();
		try 
		{			
			if(checksumNotification == null)
				return getRouter().getDocument(documentIdentifier);
			else
				return getRouter().getDocument(documentIdentifier, checksumNotification);			
		}	
		catch(ImageNotFoundException infX)
		{						
			getLogger().error("Error getting Document instance for URN [" + documentIdentifier.toString() + "]", infX);				
			String message = "Error getting Document instance for URN [" + documentIdentifier.toString() + "].\n" + infX.getMessage();
			transactionContext.setErrorMessage(message);
			transactionContext.setExceptionClassName(infX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);						
		} 
		catch(MethodException mX) 
		{
			String message = 
				"Internal server error in accessing Document instance [" + documentIdentifier.toString() + "] \n" + 
				mX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ConnectionException mX) 
		{
			String message = 
				"Internal server error in accessing Document instance [" + documentIdentifier.toString() + "] \n" + 
				mX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ServletException sX) 
		{
			String message = 
				"Internal server error in accessing Document instance [" + documentIdentifier.toString() + "] \n" + 
				sX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(sX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
	}
	
	/**
	 * Given a site number and a patient ICN, get a photo ID of the patient and
	 * stream it to the given output stream.
	 * 
	 * @param siteNumber
	 * @param patientIcn
	 * @param outStream
	 * @throws IOException
	 * @throws ImageServletException
	 */
	protected long streamPatientIdImageByPatientIcn(
			String siteNumber, 
			PatientIdentifier patientIdentifier, 			
			OutputStream outStream)
	throws IOException, SecurityCredentialsExpiredException, ImageServletException
	{
		getLogger().debug("Getting patient ID file instance by patient [" + patientIdentifier + "]");
		TransactionContext transactionContext = TransactionContextFactory.get();
		long bytesTransferred = 0;
		try 
		{
			InputStream patientIdInStream = 
				getRouter().getPatientIdentificationImage(patientIdentifier,
						RoutingTokenImpl.createVADocumentSite(siteNumber) );
			
			if (patientIdInStream == null) 
			{
				String message = "No patient photo ID available for [" + patientIdentifier + "].";
				TransactionContextFactory.get().setErrorMessage(message);
				getLogger().debug(message);
				throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);
			}
			else	// SUCCESS (SC_OK) -- 200
			{
				String message = "Pushing patient photo ID for [" + patientIdentifier + "].";
				getLogger().debug(message);
				
				ByteStreamPump pump = ByteStreamPump.getByteStreamPump(TRANSFER_TYPE.FileToNetwork);
				
				bytesTransferred = pump.xfer(patientIdInStream, outStream);
				patientIdInStream.close();
			}
		} 
		catch (ImageNearLineException inle)
		{
			String message = "Patient ID photo for [" + patientIdentifier + "] found only in off-line storage.\n" +
				"Please resubmit image access request later, operator has been notified to load media.";
			getLogger().debug(message);
			transactionContext.setExceptionClassName(inle.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_CONFLICT, message);
		}
		catch (ImageNotFoundException inle)
		{
			String message = "Patient ID photo for [" + patientIdentifier + "] from site [" + siteNumber + "] not found.";
			getLogger().debug(message);
			transactionContext.setExceptionClassName(inle.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);
		}
		catch(MethodException mX) 
		{
			String message = 
				"Internal server error in getting patient ID photo [" + patientIdentifier + "] \n" + 
				mX.getMessage();
			getLogger().debug(message);
			handleMethodException(mX);
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ConnectionException mX) 
		{
			String message = 
				"Internal server error in getting patient ID photo [" + patientIdentifier + "] \n" + 
				mX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_BAD_GATEWAY, message);
		}
		catch(ServletException sX) 
		{
			String message = 
				"Internal server error in getting patient ID photo [" + patientIdentifier + "] \n" + 
				sX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(sX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch (RoutingTokenFormatException rtfX)
		{
			String message = 
				"Internal server error in getting patient ID photo [" + patientIdentifier + "] \n" + 
				rtfX.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(rtfX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}		
		return bytesTransferred;
	}
	
	/**
	 * Initialize the thread local transaction context from the WadoRequest
	 * @param wadoRequest
	 * @throws RoutingTokenFormatException 
	 */
	protected void initTransactionContext(WadoRequest wadoRequest) 
	throws RoutingTokenFormatException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setQueryFilter("n/a");
		ImageQuality imageQuality = ImageQuality.getImageQuality( wadoRequest.getWadoQuery().getImageQualityValue() );
		ImageURN imageURN = wadoRequest == null ? null : wadoRequest.getWadoQuery().getInstanceUrn();
		GlobalArtifactIdentifier gai = wadoRequest == null ? null : wadoRequest.getWadoQuery().getGlobalArtifactIdentifier();
		transactionContext.setQuality(imageQuality == null ? ImageQuality.THUMBNAIL.toString() : imageQuality.name());
		transactionContext.setQueryFilter("n/a");
		
		// if the protocolOverride and targetSite were passed in the query string then
		// stuff them onto the transaction context.
		// These are for testing, NOT production.
		String targetSite = wadoRequest.getParsedHttpHeader().getMiscRequestHeader("targetSite");
		String protocolOverride = wadoRequest.getParsedHttpHeader().getMiscRequestHeader("protocolOverride");
		if(targetSite != null && targetSite.length() > 0 && protocolOverride != null && protocolOverride.length() > 0)
		{
			RoutingToken routingToken = RoutingTokenImpl.createVARadiologySite(targetSite);
			transactionContext.setOverrideProtocol(protocolOverride);
			transactionContext.setOverrideRoutingToken(routingToken);
			getLogger().warn("Protocol override and target server are specified in the query string.  This activates test code and should never be seen in production.");
		}
		
		if (imageURN != null)
		{
			String patientIcn = "";
			String imageUrnAsString = "";
			// originating site id means location where the image is coming from 
			//TODO: look at this logic more closely to make more generic for common providers
			patientIcn = imageURN.getPatientId(); // no longer encode the patient ICN
			imageUrnAsString = TransactionContextFactory.decodeUrnForLogging(imageURN);//  decodeDodUrnForLogging(imageURN.toString());

			
			transactionContext.setPatientID(patientIcn);
			transactionContext.setUrn(imageUrnAsString);
			transactionContext.setModality(imageURN.getImageModality());
		}
		else if(gai != null)
		{
			transactionContext.setUrn(gai.toString(SERIALIZATION_FORMAT.RAW));
			if(gai instanceof PatientArtifactIdentifierImpl)
			{
				PatientArtifactIdentifierImpl pai = (PatientArtifactIdentifierImpl)gai;
				transactionContext.setPatientID(pai.getPatientIdentifier());
			}
		}
		transactionContext.setRequestType("image transfer");
	}
	
	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy()
	{
		super.destroy(); 
	}	

	/**
	 * An exception class that is used by this class and its derivations
	 * to notify of error conditions.
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	public class ImageServletException
	extends Exception
	{
		private static final long serialVersionUID = 1L;
		private final int responseCode;
		
		ImageServletException(int responseCode, String message)
        {
	        super(message);
	        this.responseCode = responseCode;
        }

		public int getResponseCode()
        {
        	return responseCode;
        }
	}
	
	protected long streamExamImageInstanceFromCacheByUrn(ImageURN imageUrn, 
		ImageQuality requestedImageQuality,
		List<ImageFormat> acceptableResponseContent, 
		OutputStream outStream,
		ImageMetadataNotification checksumNotification)
	throws IOException, SecurityCredentialsExpiredException, ImageServletException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		ImageFormatQualityList qualityList = new ImageFormatQualityList();
		qualityList.addAll(acceptableResponseContent, requestedImageQuality);
		
		getLogger().info("Getting exam image instance from cache by URN [" + imageUrn.toString() + "] with request format: " + qualityList.getAcceptString(true, true));
		long bytesTransferred = 0;
		try 
		{
			bytesTransferred = (Long)getRouter().getExamInstanceFromCacheByImageUrn(
						imageUrn, 
						checksumNotification,
						outStream,
						qualityList);
			getLogger().debug("Wrote [" + bytesTransferred + "] bytes to output stream for exam image [" + imageUrn.toString() + "]");					
		} 
		// not doing image conversion for this call, only possible exception is image not cached or method exception
		catch(ImageNotCachedException incX)
		{
			String message = "Image [" + imageUrn.toString() + "] not found in cache.\n" + incX.getMessage();			
			getLogger().debug(message);
			transactionContext.setExceptionClassName(incX.getClass().getSimpleName());
			// sending a 404 error when image not in the cache
			throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);
		}
		catch(MethodException mX)
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				mX.getMessage();
			getLogger().error(message, mX);
			handleMethodException(mX);
			// the MethodException is not a SecurityCredentialsExpiredException and must still be handled.
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());			
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(Exception x) 
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				x.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(x.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		return bytesTransferred;
	}	
	
	/**
	 * 
	 * @param imageUrn
	 * @param requestedImageQuality
	 * @param acceptableResponseContent
	 * @param outStream
	 * @param checksumNotification
	 * @param allowedFromCache
	 * @return
	 * @throws IOException
	 * @throws SecurityCredentialsExpiredException
	 * @throws ImageServletException
	 */
	protected long streamExamImageInstanceByUrn(
		ImageURN imageUrn, 
		ImageQuality requestedImageQuality,
		List<ImageFormat> acceptableResponseContent, 
		OutputStream outStream,
		ImageMetadataNotification checksumNotification,
		boolean allowedFromCache)
	throws IOException, SecurityCredentialsExpiredException, ImageServletException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		ImageFormatQualityList qualityList = new ImageFormatQualityList();
		qualityList.addAll(acceptableResponseContent, requestedImageQuality);
		
		getLogger().info("Getting exam image instance by URN [" + imageUrn.toString() + "] with request format: " + qualityList.getAcceptString(true, true));
		long bytesTransferred = 0;
		try 
		{
			if(allowedFromCache)
			{
				bytesTransferred = (Long)getRouter().getExamInstanceByImageUrn(
							imageUrn, 
							checksumNotification,
							outStream,
							qualityList);
			}
			else
			{
				bytesTransferred = (Long)getRouter().getExamInstanceByImageUrnNotFromCache(
						imageUrn, 
						checksumNotification,
						outStream,
						qualityList);
			}
			getLogger().debug("Wrote [" + bytesTransferred + "] bytes to output stream for exam image [" + imageUrn.toString() + "]");					
		} 
		catch (ImageNearLineException inle)
		{
			String message = "Image [" + imageUrn.toString() + "] found only in off-line storage.\n" +
				"Please resubmit image access request later, operator has been notified to load media.";
			getLogger().debug(message);
			transactionContext.setExceptionClassName(inle.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_CONFLICT, message);
		}
		catch (ImageConversionException ice) 
		{
			String message = 
				"Image [" + imageUrn.toString() + 
				"] found in a different format and conversion to the requested type failed.\n" + 
				ice.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(ice.getClass().getSimpleName());
			// JMW 6/23/08 - if there is an image conversion error, it will come out as an internal server error (500)
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ImageNotFoundException infX)
		{
			String message = "Image [" + imageUrn.toString() + "] not found.\n" + infX.getMessage();
			//TransactionContextFactory.get().setErrorMessage(message); // not needed here			
			getLogger().debug(message);
			transactionContext.setExceptionClassName(infX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);
		}
		catch(MethodException mX)
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				mX.getMessage();
			getLogger().error(message, mX);
			handleMethodException(mX);
			// the MethodException is not a SecurityCredentialsExpiredException and must still be handled.
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());			
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(Exception x) 
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				x.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(x.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		return bytesTransferred;
	}
	
	/**
	 * Get the image metadata (to satisfy an HTTP HEAD request).
	 * 
	 * @param imageUrn
	 * @param requestedImageQuality
	 * @param acceptableResponseContent
	 * @param allowedFromCache
	 * @return
	 */
	protected ImageMetadata getImageMetadataByURN(
		ImageURN imageUrn, 
		ImageQuality requestedImageQuality,
		List<ImageFormat> acceptableResponseContent, 
		boolean requireFromCache,
		boolean allowedFromCache)
	throws IOException, SecurityCredentialsExpiredException, ImageServletException
	{
		ImageMetadata result = null;
		TransactionContext transactionContext = TransactionContextFactory.get();
		ImageFormatQualityList requestedFormatQuality = new ImageFormatQualityList();
		requestedFormatQuality.addAll(acceptableResponseContent, requestedImageQuality);
		
		getLogger().info("Getting exam image instance by URN [" + imageUrn.toString() + "] with request format: " + requestedFormatQuality.getAcceptString(true, true));
		
		try {
			result = getRouter().headInstanceByImageUrnVerbose(imageUrn, requestedFormatQuality, null, false, false, true);
		} 
		catch (ImageNearLineException inle)
		{
			String message = "Image [" + imageUrn.toString() + "] found only in off-line storage.\n" +
				"Please resubmit image access request later, operator has been notified to load media.";
			getLogger().debug(message);
			transactionContext.setExceptionClassName(inle.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_CONFLICT, message);
		}
		catch (ImageConversionException ice) 
		{
			String message = 
				"Image [" + imageUrn.toString() + 
				"] found in a different format and conversion to the requested type failed.\n" + 
				ice.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(ice.getClass().getSimpleName());
			// JMW 6/23/08 - if there is an image conversion error, it will come out as an internal server error (500)
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(ImageNotFoundException infX)
		{
			String message = "Image [" + imageUrn.toString() + "] not found.\n" + infX.getMessage();
			//TransactionContextFactory.get().setErrorMessage(message); // not needed here			
			getLogger().debug(message);
			transactionContext.setExceptionClassName(infX.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_NOT_FOUND, message);
		}
		catch(MethodException mX)
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				mX.getMessage();
			getLogger().error(message, mX);
			handleMethodException(mX);
			// the MethodException is not a SecurityCredentialsExpiredException and must still be handled.
			transactionContext.setExceptionClassName(mX.getClass().getSimpleName());			
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		catch(Exception x) 
		{
			String message = 
				"Internal server error in accessing image [" + imageUrn.toString() + "] \n" + 
				x.getMessage();
			getLogger().debug(message);
			transactionContext.setExceptionClassName(x.getClass().getSimpleName());
			throw new ImageServletException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
		
		return result;
	}
	
	protected synchronized ImagingBaseWebFacadeRouter getRouter()
	throws ServletException
	{
		ImagingBaseWebFacadeRouter router;
		try
		{
			router = FacadeRouterUtility.getFacadeRouter(ImagingBaseWebFacadeRouter.class);
		} 
		catch (Exception x)
		{
			logger.error("Exception getting the facade router implementation.", x);
			return null;
		}
		
		return router;
	}
	
	/**
	 * @deprecated This is no longer used, use MetadataNotification instead
	 */
	@Deprecated
	public class ChecksumNotification 
	implements InstanceChecksumNotification
	{
		private HttpServletResponse resp;
		
		public ChecksumNotification(HttpServletResponse resp)
		{
			this.resp = resp;
		}
		
		public void instanceChecksum(String checksumValue)
		{
			if(checksumValue != null)
				resp.addHeader(TransactionContextHttpHeaders.httpHeaderChecksum, checksumValue);
		}
		
	}
	
	/**
	 * Metadata notification to set properties of the HTTP response that indicate
	 * key fields of the responded image. This includes setting the checksum, 
	 * image content type, content length and optionally the image quality and the full type 
	 * of the image (applicable if DICOM to include the sub type)
	 * @author VHAISWWERFEJ
	 *
	 */
	public class MetadataNotification
	implements ImageMetadataNotification
	{
		private final HttpServletResponse resp;
		private final boolean includeCustomHeaders;
		
		public MetadataNotification(HttpServletResponse resp, boolean includeCustomHeaders)
		{
			this.resp = resp;
			this.includeCustomHeaders = includeCustomHeaders;
		}
		
		/**
		 * Constructor that does not include custom headers in the response
		 * @param resp
		 */
		public MetadataNotification(HttpServletResponse resp)
		{
			this.resp = resp;
			this.includeCustomHeaders = false;
		}
		
		@Override
		public void imageMetadata(String checksumValue,
				ImageFormat imageFormat, int fileSize, ImageQuality imageQuality) 
		{
			TransactionContext transactionContext = TransactionContextFactory.get();
			if(checksumValue != null)
			{
				// in some cases this was getting called twice on a single image response, check for an existing
				// checksum value and update it if it exists
				if(resp.containsHeader(TransactionContextHttpHeaders.httpHeaderChecksum))
				{
					logger.warn("Header '" + TransactionContextHttpHeaders.httpHeaderChecksum + "' already has value, updating to new value '" + checksumValue + "'.");
					resp.setHeader(TransactionContextHttpHeaders.httpHeaderChecksum, checksumValue);
				}
				else
				{				
					resp.addHeader(TransactionContextHttpHeaders.httpHeaderChecksum, checksumValue);
				}
			}
			if(imageFormat != null)
			{
				resp.setContentType(imageFormat.getMime());
				if(includeCustomHeaders)
				{
					resp.addHeader(TransactionContextHttpHeaders.httpHeaderVistaImageFormat, 
						imageFormat.getMimeWithEnclosedMime());
				}
				transactionContext.setFacadeImageFormatSent(imageFormat.toString());
			}	
			if(imageQuality != null)
			{
				if(includeCustomHeaders)
				{
					resp.addHeader(TransactionContextHttpHeaders.httpHeaderImageQuality, 
						imageQuality.getCanonical() + "");
				}
				transactionContext.setFacadeImageQualitySent(imageQuality.toString());
			}				
			if(includeCustomHeaders)
			{
				String machineName = transactionContext.getMachineName();
				if(machineName == null)
					machineName = "<unknown>";
				resp.addHeader(TransactionContextHttpHeaders.httpHeaderMachineName, machineName);
			}
		}		
	}	
}