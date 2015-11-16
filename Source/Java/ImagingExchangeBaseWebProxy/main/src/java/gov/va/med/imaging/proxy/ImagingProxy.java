package gov.va.med.imaging.proxy;


import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.channels.exceptions.ChecksumFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.Requestor;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException;
import gov.va.med.imaging.proxy.services.ProxyService;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import java.util.zip.GZIPInputStream;

import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 * This class is a simple proxy for the web access to images.
 * The functionality in here is shared by the three webservice proxies (exchange, federation and clinicaldisplay).
 * This class may be used as a concrete class, though there is currently nowhere that it is used as such
 * @author VHAISWBECKEC
 *
 */
public abstract class ImagingProxy
{
	// the list of HTTP headers fields set by the proxy and understood by
	// the server
	/*
	// JMW 8/19/2010
	// these header values no longer used, using values from TransactionContextHttpHeaders class 
	// (identical to these here) 
	public final static String httpHeaderDuz = "xxx-duz";
	public final static String httpHeaderFullName = "xxx-fullname";
	public final static String httpHeaderSiteName = "xxx-sitename";
	public final static String httpHeaderSiteNumber = "xxx-sitenumber";
	public final static String httpHeaderSSN = "xxx-ssn";
	public final static String httpHeaderPurposeOfUse = "xxx-purpose-of-use";
	public final static String httpHeaderChecksum = "xxx-checksum";
	public final static String httpHeaderTransactionId = "xxx-transaction-id";
	public final static String httpHeaderBrokerSecurityTokenId = "xxx-securityToken";
	public final static String httpHeaderCacheLocationId = "xxx-cacheLocationId";
	public final static String httpHeaderUserDivision = "xxx-userDivision";
	
	public final static String httpHeaderImageChecksum = "xxx-image-checksum";
	public final static String httpHeaderTXTChecksum = "xxx-txt-checksum";
	public final static String httpHeaderImageQuality = "xxx-image-quality";
	public final static String httpHeaderImageSize = "xxx-image-length";
	public final static String httpHeaderTxtSize = "xxx-txt-length";
	*/
	
	protected Logger logger = Logger.getLogger(ImagingProxy.class);
	
	
	// YYYYMMDDHHMMSS.FFFFFF+ZZZZ
	// The components of this string, from left to right, are 
	// YYYY = Year, 
	// MM = Month, 
	// DD = Day, 
	// HH = Hour, 
	// MM = Minute, 
	// SS = Second,
	// FFFFFF = milliseconds
	// ZZZZ - timezone offset
	private final static String dicomDateFormat = "yyyyMMddHHmmss.SSSSzzzzz";
	// do not re-use a simple date format instance, they are not thread safe
	public final static DateFormat getDicomDateFormat()
	{
		return new SimpleDateFormat(dicomDateFormat);
	}
	
	private final static String requestDateFormat = "MM/dd/yyyy";
	public final static DateFormat getRequestDateFormat()
	{
		return new SimpleDateFormat(requestDateFormat);
	}
	
	// ==============================================================================================================================
	//
	// ==============================================================================================================================
	protected String responseContentType="";
	protected String responseEncoding="";
	protected String responseMessage="";
	protected String responseChecksum=null;
	protected String imageChecksum=null;
	protected String txtChecksum = null;
	protected ImageQuality requestedQuality = null;
	protected boolean requestGzip = false;
	protected int fileLength = 0;
	protected int txtLength = 0;
	
	protected final ProxyServices proxyServices;
	private final boolean instanceUrlEscaped;
	
	protected ImagingProxy(ProxyServices proxyService, boolean instanceUrlEscaped)
	{
		assert proxyService != null;
		this.proxyServices = proxyService;
		this.instanceUrlEscaped = instanceUrlEscaped;
	}
	
	/**
	 * Returns the ProxyService for the ProxyServiceType.image, if none is found
	 * then the exception is caught and null is returned.
	 */
	public ProxyService getImageProxyService()
	{
		try
		{
			return proxyServices.getProxyService(ProxyServiceType.image);
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			return null;
		}
	}
	
	/**
	 * Returns the ProxyService for the ProxyServiceType.metadata, if none is found
	 * then the exception is caught and null is returned.
	 */
	public ProxyService getMetadataProxyService()
	{
		try
		{
			return proxyServices.getProxyService(ProxyServiceType.metadata);
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			return null;
		}
	}

	public String getResponseContentType()
	{
		return this.responseContentType;
	}

	public String getResponseMessage()
	{
		return this.responseMessage;
	}

	public String getResponseChecksum()
	{
		return this.responseChecksum;
	}
	
	public ImageQuality getRequestedQuality() {
		return requestedQuality;
	}

	public ChecksumValue getResponseChecksumValue()
	{
		try
		{
			return getResponseChecksum() == null ?
					null : new ChecksumValue(getResponseChecksum());
		} 
		catch (ChecksumFormatException x)
		{
			logger.warn("Error parsing checksum '" + getResponseChecksum() + "'.", x);
			return null;
		}
	}

	public String getImageChecksum() {
		return imageChecksum;
	}

	public String getTxtChecksum() {
		return txtChecksum;
	}

	/**
	 * @return the fileLength
	 */
	public int getFileLength() {
		return fileLength;
	}

	/**
	 * @return the txtLength
	 */
	public int getTxtLength() {
		return txtLength;
	}

	/**
	 * Set or get whether the request should set the accept-content to 
	 * include gzip or not.
	 * 
	 * @return
	 */
	public boolean isRequestGzip()
    {
    	return requestGzip;
    }

	public void setRequestGzip(boolean requestGzip)
    {
    	this.requestGzip = requestGzip;
    }

	/**
	 * Create a URL string for an image specific to this proxy. 
	 *  
	 * @param imageUrn
	 * @return
	 */
	public String createImageUrl(String imageUrn, ImageFormatQualityList requestFormatQualityList)
	throws ProxyServiceNotFoundException
	{
		return Utilities.createImageUrl(this.proxyServices, imageUrn, 
				requestFormatQualityList, getInstanceRequestProxyServiceType());
	}
	
	public String createTxtUrl(String imageUrn, String accept)
	throws ProxyServiceNotFoundException
	{
		return Utilities.createTxtUrl(this.proxyServices, imageUrn, accept,
				getTextFileRequestProxyServiceType());
	}
	
	/**
	 * 
	 * @param imageUrn
	 * @param imageQuality
	 * @param accept
	 * @return
	 * @throws ProxyException 
	 */
	public SizedInputStream getInstance(String imageUrn, ImageFormatQualityList requestFormatQualityList, boolean includeVistaSecurityContext) 
	throws ImageNearLineException, ImageNotFoundException, 
	SecurityCredentialsExpiredException, ImageConversionException, MethodException, ConnectionException
	{
		return getInstance(imageUrn, requestFormatQualityList, null, includeVistaSecurityContext);
	}

	/**
	 * Make an HTTP request to get the image named by the given URN.
	 * The checksum will contain the calculated checksum only after
	 * the entire stream contents have been read.
	 * 
	 * @param imageUrn
	 * @param imageQuality
	 * @param imageFormat
	 * @param checksum
	 * @return an InputStream containing the contents of the image.
	 * If the checksum parameter is not null then the InputStream will be of type 
	 * java.util.zip.CheckedInputStream.
	 */
	public SizedInputStream getInstance(String imageUrn, ImageFormatQualityList requestFormatQualityList, 
			Checksum checksum, boolean includeVistaSecurityContext) 
	throws ImageNearLineException, ImageNotFoundException, 
	SecurityCredentialsExpiredException, ImageConversionException, MethodException, ConnectionException
	{		
		String imageUrlString = null;
		ImageFormat firstImageFormat = requestFormatQualityList.get(0).getImageFormat();
		this.requestedQuality = requestFormatQualityList.get(0).getImageQuality();	
		try
		{
			if(firstImageFormat == ImageFormat.TEXT_DICOM)
			{
				imageUrlString = createTxtUrl(imageUrn, firstImageFormat.getMime());
			}
			else 
			{
				imageUrlString = createImageUrl(imageUrn, requestFormatQualityList);
			}
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			logger.error("Error finding proxy service", psnfX);
			throw new ConnectionException(psnfX.getMessage());
		}
		TransactionContext transactionContext = TransactionContextFactory.get();
		String transactionIdentifier = transactionContext.getTransactionId();
		logger.info("Transaction [" + transactionIdentifier + "] initiated, retreiving [" + imageUrlString + "]");
		transactionContext.addDebugInformation("getInstance to URL '" + imageUrlString + "'.");

		int result = 0;
		HttpClient client = null;
		GetMethod getMethod = null;
		
		try
		{
			client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(30000); // DKB - 30 sec to get a connection
            client.getHttpConnectionManager().getParams().setSoTimeout(120000); // 120 sec to wait for data
			client.getParams().setAuthenticationPreemptive(true);
			//getMethod = new GetMethod(imageUrlString);
			getMethod = new GetMethod();
			getMethod.setURI(new URI(imageUrlString, instanceUrlEscaped));
			
			Header acceptHeader = new Header("Accept", requestFormatQualityList.getAcceptString(false));
			getMethod.setRequestHeader(acceptHeader);
			
			Header acceptLanguageHeader = new Header("Accept-Language", "en-us,en");
			getMethod.setRequestHeader(acceptLanguageHeader);
			
			Header acceptCharsetHeader = new Header("Accept-Charset", "ISO-8859-1,utf-8");
			getMethod.setRequestHeader(acceptCharsetHeader);

			// ++ CTB 14MAy2008
			if(isRequestGzip())
			{
				Header encodingHeader = new Header("Accept-Encoding", "gzip");
				getMethod.setRequestHeader(encodingHeader);
			}
			
			getMethod.setFollowRedirects(true);

			getMethod.setRequestHeader( HTTPConstants.HEADER_CACHE_CONTROL, "no-cache,no-store,no-transform");
			getMethod.setRequestHeader( HTTPConstants.HEADER_PRAGMA, "no-cache");
			
			// add the security information to the http headers
			addSecurityContextToHeader(client, getMethod, includeVistaSecurityContext);
			
			addOptionalGetInstanceHeaders(getMethod);
			
			result = client.executeMethod(getMethod);

            // How many bytes received?
            transactionContext.setDataSourceBytesReceived (getMethod.getBytesReceived ());
            
			// How long did it take to get a response coming back?
			Long timeSent = getMethod.getTimeRequestSent ();
			Long timeReceived = getMethod.getTimeFirstByteReceived ();
			if (timeSent != null && timeReceived != null)
			{
			   long timeTook = timeReceived.longValue () - timeSent.longValue ();
			   transactionContext.setTimeToFirstByte (new Long (timeTook));
			}
			
			Header contentTypeHeader=getMethod.getResponseHeader("Content-Type");
			if (contentTypeHeader != null)
				responseContentType = contentTypeHeader.getValue(); // default is empty string
			
			// ++ CTB 14MAy2008
			Header encodingTypeHeader=getMethod.getResponseHeader("Content-Encoding");
			if (encodingTypeHeader != null)
				responseEncoding = encodingTypeHeader.getValue(); // default is empty string
			
			Header checksumHeader=getMethod.getResponseHeader(TransactionContextHttpHeaders.httpHeaderChecksum);
			if (checksumHeader != null)
				responseChecksum = checksumHeader.getValue(); // default is empty string
			else
				responseChecksum = null;
			
			// get the checksums for a zip file (only used in Federation)
			Header imageChecksumHeader = getMethod.getResponseHeader(TransactionContextHttpHeaders.httpHeaderImageChecksum);
			if(imageChecksumHeader != null)
				imageChecksum = imageChecksumHeader.getValue();
			else
				imageChecksum = null;
			
			Header txtChecksumHeader = getMethod.getResponseHeader(TransactionContextHttpHeaders.httpHeaderTXTChecksum);
			if(txtChecksumHeader != null)
				txtChecksum = txtChecksumHeader.getValue();
			else
				txtChecksum = null;
			
			Header imgFileLengthHeader = getMethod.getResponseHeader(TransactionContextHttpHeaders.httpHeaderImageSize);
			if(imgFileLengthHeader != null)
			{
				try
				{
					fileLength = Integer.parseInt(imgFileLengthHeader.getValue());
				}
				catch(NumberFormatException nfX)
				{
					logger.debug("Error parsing image length header value [" + imgFileLengthHeader.getValue() + "].", nfX);
				}
			}
			else
				fileLength = 0;
			
			Header txtFileLengthHeader = getMethod.getResponseHeader(TransactionContextHttpHeaders.httpHeaderTxtSize);
			if(txtFileLengthHeader != null)
			{
				try
				{
					txtLength = Integer.parseInt(txtFileLengthHeader.getValue());
				}
				catch(NumberFormatException nfX)
				{
					logger.debug("Error parsing TXT file length header value [" + imgFileLengthHeader.getValue() + "].", nfX);
				}
			}
			else
				txtLength = 0;
			
			Header responseQualityHeader = getMethod.getResponseHeader(TransactionContextHttpHeaders.httpHeaderImageQuality);
			if(responseQualityHeader != null)
			{
				try
				{
					requestedQuality = ImageQuality.getImageQuality(responseQualityHeader.getValue());
				}
				catch(NumberFormatException nfX) 
				{
					logger.debug("Error parsing quality header value [" + responseQualityHeader.getValue() + "].", nfX);
				}
			}			
			Header responseMachineNameHeader = 
				getMethod.getResponseHeader(TransactionContextHttpHeaders.httpHeaderMachineName);
			if(responseMachineNameHeader != null)
			{
				String machineName = responseMachineNameHeader.getValue();
				logger.info("Received response from machine name '" + machineName + "'.");
				transactionContext.setDataSourceResponseServer(machineName);
			}
		} 
		catch (HttpException e)
		{
			e.printStackTrace();
			responseMessage = "HTTPException on execute GET method, got response code '" + result + "', " + e.getMessage();
			throw new ConnectionException(responseMessage, e);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			responseMessage = "IOException on execute GET method, got response code '" + result + "', " + e.getMessage();
			throw new ConnectionException(responseMessage, e);
		}
		
		// 100 series codes should have been dealt with by the underlying HTTP proxy.
		// If not then we have no choice but to pass the error message on up and let the client
		// decide if they can re-try.
		if (result >= HttpStatus.SC_CONTINUE && result < HttpStatus.SC_OK)
		{
			String message = readResponseMessageBody(responseContentType, getMethod);
			if(message == null)
				message = "Server response was '" + result + "', responses other than 200 (OK) are considered errors.";
			
			logger.warn("Transaction [" + transactionIdentifier + "] response code = '" + result + "' message is '" + message + "'");
			throw new ConnectionException(message);
		}
		
		else if(result == HttpStatus.SC_OK)
		{
			try
			{
				int bytesRead = 0;
				InputStream input = null;
				
				try
				{
					Header contentLengthHeader = getMethod.getResponseHeader("content-length");
					if (contentLengthHeader!=null) 
					{
						String headerLengthValue = contentLengthHeader.getValue();
						bytesRead = Integer.parseInt(headerLengthValue);
					}
				} 
				catch (Exception e)
				{
					responseMessage = "Exception on get response header/value";
					logger.error(e);
				}
				
				logger.info("Transaction [" + transactionIdentifier + "] returned SUCCESS. Content Length [" + bytesRead + "]");
				InputStream inStream = getMethod.getResponseBodyAsStream();
				
				// ++ CTB 14MAy2008
				// if the response body is gzip encoded then create a GZIP decoding input stream and
				// return that
				if(responseEncoding.indexOf("gzip") >= 0)
				{
					GZIPInputStream zipIn = new GZIPInputStream(inStream);
					inStream = zipIn;
				}
				
				// if a checksum instance was provided, attach it to a CheckedInputStream instance
				// and return it, else return the raw stream
				if(checksum != null)
				{
					input = new CheckedInputStream(inStream, checksum);
				}
				else
					input = inStream;
					//return inStream;
				return new SizedInputStream(input, bytesRead);
			} 
			catch (IOException e)
			{
				logger.error(e);
			}
		}
		// 200 series error codes that are not explicitly an OK
		// some of these can be lived with, some not.  For now we call them all errors.
		else if (result > HttpStatus.SC_OK  && result < HttpStatus.SC_MULTIPLE_CHOICES)
		{
			String message = readResponseMessageBody(responseContentType, getMethod);
			if(message == null)
				message = "Server response was '" + result + "', responses other than 200 (OK) are considered errors.";
			
			logger.warn("Transaction [" + transactionIdentifier + "] response code = '" + result + "' message is '" + message + "'");
			throw new ConnectionException(message);
		}
		
		// 300 series error codes
		// result is probably a client error, report it, the user may be able to modify the request and re-submit
		else if (result >= HttpStatus.SC_MULTIPLE_CHOICES  && result < HttpStatus.SC_BAD_REQUEST)
		{
			String message = readResponseMessageBody(responseContentType, getMethod);
			if(message == null)
				message = "Server response was '" + result + "', responses other than 200 (OK) are considered errors.";
			
			logger.warn("Transaction [" + transactionIdentifier + "] response code = '" + result + "' message is '" + message + "'");
			throw new ConnectionException(message);
		}
		
		// 400 series error codes
		// result is a client error, report it, the user may be able to modify the request and re-submit
		else if (result >= HttpStatus.SC_BAD_REQUEST  && result < HttpStatus.SC_INTERNAL_SERVER_ERROR)
		{
			String message = "Server response was '" + result + "', responses other than 200 (OK) are considered errors.";
			if("text/html".equals(responseContentType))
				message = readHTTPMessage(client, getMethod);
			else if (("application/dicom".equals(responseContentType)) || 
					("image/jpeg/dicom".equals(responseContentType)))
				message += readHTTPMessage(client, getMethod);
			logger.warn("Transaction [" + transactionIdentifier + "] response code = '" + result + "' message is '" + message + "'");
			
			// 409 result
			if(result == HttpStatus.SC_CONFLICT)
			{
				throw new ImageNearLineException(imageUrn);
			}		
			// The Exchange interface does not respond with a 404 for file not founds but Federation does
			else if(result == HttpStatus.SC_NOT_FOUND)
			{
				throw new ImageNotFoundException(message);
			}
			else if(result == HttpStatus.SC_PRECONDITION_FAILED)
			{
				throw new SecurityCredentialsExpiredException(message);
			}
			else if(result == HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE)
			{
				throw new ImageConversionException(message);
			}

			
			throw new ConnectionException(message);
		}
		
		// 500 series error codes
		// result is a server error, report it but not much we can do to correct it
		else if (result >= HttpStatus.SC_INTERNAL_SERVER_ERROR)
		{
			String message = "Server response was '" + result + "', responses other than 200 (OK) are considered errors.";
			if("text/html".equals(responseContentType))
				message += readHTTPMessage(client, getMethod);
			
			if(result == HttpStatus.SC_BAD_GATEWAY)
			{
				logger.error("ConnectionException transaction [" + transactionIdentifier + "], response code = '" + result + "' message is '" + message + "'");	
				throw new ConnectionException(message);
			}
			else
			{			
				logger.error("MethodException transaction [" + transactionIdentifier + "], response code = '" + result + "' message is '" + message + "'");			
				throw new MethodException(message);
			}
		}
		
		return null;
	}
	
	/**
	 * Function adds optional headers to the request. This function should be overridden by other classes to add these
	 * optional headers
	 * @param getMethod
	 */
	protected abstract void addOptionalGetInstanceHeaders(GetMethod getMethod);	
	
	/**
	 * Returns the ProxyServiceType to use when doing a getInstance request
	 * @return
	 */
	protected abstract ProxyServiceType getInstanceRequestProxyServiceType();
	
	/**
	 * Returns the ProxyServiceType used when doing a getInstance for a text file request.
	 * @return
	 */
	protected abstract ProxyServiceType getTextFileRequestProxyServiceType();

	/**
	 * @param client
	 * @param getMethod 
	 * @return
	 */
	protected String readResponseMessageBody(String contentType, GetMethod getMethod)
	{
		if(contentType != null && contentType.toLowerCase().startsWith("text"))
		{
			try
			{
				return getMethod.getResponseBodyAsString();
			} 
			catch (IOException x)
			{
				logger.error(x);
			}
		}
		
		return null;
	}

	/**
	 * @param client
	 * @param getMethod 
	 * @return
	 */
	protected String readHTTPMessage(HttpClient client, GetMethod getMethod)
	{
			String response = " " + getMethod.getStatusText();
			return response;
	}

	/**
	 * Adds the user ID and password to the HTTP headers using BASIC security.
	 * Adds the parameter of the thread local security context to the HTTP header.
	 * 
	 * @param getMethod
	 */
	protected void addSecurityContextToHeader(HttpClient client, GetMethod getMethod, 
			boolean includeVistaSecurityContext)
	{
		// BTW, don't import the Credentials class because there is an app specific Credentials
		// that derives from the java Principal class, which is where the thread local
		// secutity context is stored
		org.apache.commons.httpclient.Credentials imageXChangeCredentials = null;
		
		try
		{
			ProxyService imageService = proxyServices.getProxyService(ProxyServiceType.image);
		
			if(imageService.getCredentials() instanceof String)
				imageXChangeCredentials = 
					new UsernamePasswordCredentials(imageService.getUid(), (String)(imageService.getCredentials()) );
			
			AuthScope imageXChangeAuthScope = new AuthScope(imageService.getHost(), imageService.getPort(), AuthScope.ANY_REALM);
			client.getState().setCredentials(imageXChangeAuthScope, imageXChangeCredentials);
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			logger.warn("Cannot find image proxy service to set credentials, continuing without", psnfX);
		}
		//Header authorizationHeader = new Header("Authorization", "Basic ");
		//getMethod.setRequestHeader(authorizationHeader);
		
		// the thread local security credentials (the VistA specific stuff) is written into
		// app specific HTTP headers
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		// 3-11-2008 DKB - modified to send all headers except the DUZ if includeVistaSecurityContext is false
		// the silver BIA was throwing an error because we were not sending httpHeaderFullName, httpHeaderSiteName,
		// httpHeaderSiteNumber and httpHeaderSSN
		if(includeVistaSecurityContext)
		{
			if(transactionContext.getDuz() != null)
				getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderDuz, 
						transactionContext.getDuz()));
			
			String securityToken = transactionContext.getBrokerSecurityToken();
        	if(securityToken != null && securityToken.length() > 0)
        		getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderBrokerSecurityTokenId, 
        				securityToken));
        	
        	String cacheLocationId = transactionContext.getCacheLocationId();
        	if(cacheLocationId != null && cacheLocationId.length() > 0)
        		getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderCacheLocationId, 
        				cacheLocationId));
        	
        	String userDivision = transactionContext.getUserDivision();
        	if(userDivision != null && userDivision.length() > 0)
        		getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderUserDivision, 
        				userDivision));
		}
		if(transactionContext.getFullName() != null)
			getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderFullName, 
					transactionContext.getFullName()));
		if(transactionContext.getSiteName() != null)
			getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderSiteName, 
					transactionContext.getSiteName()));
		if(transactionContext.getSiteNumber() != null)
			getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderSiteNumber, 
					transactionContext.getSiteNumber()));
		if(transactionContext.getSsn() != null)
			getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderSSN, 
					transactionContext.getSsn()));
		
		if(transactionContext.getTransactionId() != null)
			getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderTransactionId, 
					transactionContext.getTransactionId()));
		// 1/8/07 JMW - Add the purpose of use to the request for images
		getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderPurposeOfUse, 
				Requestor.PurposeOfUse.routineMedicalCare.getDescription()));		
	}	
}
