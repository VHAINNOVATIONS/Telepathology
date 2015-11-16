/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 29, 2009
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
package gov.va.med.imaging.federation.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.zip.GZIPInputStream;

import org.apache.axis.AxisFault;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.ImagingProxy;
import gov.va.med.imaging.proxy.Utilities;
import gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationProxy 
extends ImagingProxy 
implements IFederationProxy 
{
	protected final FederationConfiguration federationConfiguration;
	
	private final static int defaultMetadataTimeoutMs = 600000;
	
	protected AbstractFederationProxy(ProxyServices proxyServices, 
			FederationConfiguration federationConfiguration)
	{
		super(proxyServices, false);
		this.federationConfiguration = federationConfiguration;
	}
	

	private final static String defaultHttpAccept = "image/jpeg";
	
	protected int getMetadataTimeoutMs()
	{
		if(federationConfiguration != null)
		{
			if(federationConfiguration.getMetadataTimeoutMs() != null)
				return federationConfiguration.getMetadataTimeoutMs();
		}
		return defaultMetadataTimeoutMs;			
	}
	
	protected abstract String getDataSourceVersion();

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.federation.proxy.IFederationProxy#getPatientIdentifierImage(java.lang.String)
	 */
	@Override
	public SizedInputStream getPatientIdentifierImage(String patientIcn, String siteNumber)
	throws ImageNotFoundException, ConnectionException, MethodException 
	{
		String imageUrlString = null;
		try
		{
			imageUrlString = Utilities.createPhotoIdUrl(proxyServices, patientIcn, siteNumber);
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			logger.error("Error finding proxy service for photo Id", psnfX);
			throw new ConnectionException(psnfX.getMessage());
		}
		TransactionContext transactionContext = TransactionContextFactory.get();
		String transactionIdentifier = transactionContext.getTransactionId();
		logger.info("Transaction [" + transactionIdentifier + "] initiated, retreiving [" + imageUrlString + "]");

		int result = 0;
		HttpClient client = null;
		GetMethod getMethod = null;
		
		try
		{
			client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(30000); // DKB - 30 sec to get a connection
            client.getHttpConnectionManager().getParams().setSoTimeout(120000); // 120 sec to wait for data
			client.getParams().setAuthenticationPreemptive(true);
			getMethod = new GetMethod(imageUrlString);
			
			Header acceptHeader = new Header("Accept", defaultHttpAccept);
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
			addSecurityContextToHeader(client, getMethod, true);
			
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
				throw new ImageNotFoundException("Cannot find photo for patient '" + patientIcn + "', received 409 result");
			}		
			// The Exchange interface does not respond with a 404 for file not founds but Federation does
			else if(result == HttpStatus.SC_NOT_FOUND)
			{
				throw new ImageNotFoundException(message);
			}

			
			throw new ConnectionException(message);
		}
		
		// 500 series error codes
		// result is a server error, report it but not much we can do to correct it
		else if (result >= HttpStatus.SC_INTERNAL_SERVER_ERROR)
		{
			String message = readResponseMessageBody(responseContentType, getMethod);
			if(message == null)
				message = "Server response was '" + result + "', responses other than 200 (OK) are considered errors.";
			
			if(message.contains("InputStream must be non-null"))
			{ 
				logger.warn("Transaction [" + transactionIdentifier + "] returned error with null input stream indicating image did not exist, throwing ImageNotFoundException.");
				// JMW 5/10/2010 - special case for handling bug in P83
				// P83 VIX has a bug where if the patient doesn't have a photo ID the data source returns a null
				// input stream, the router command doesn't handle the null response properly and throws an exception
				// the code in here checks the error message for this particular problem and throws it as a ImageNotFoundException 
				throw new ImageNotFoundException("Response returned error with null input stream indicating image did not exist, throwing ImageNotFoundException.");
			}
			
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
	 * Attempt to translate the remote exception into a Method or Connection exception.
	 * If the conversion fails then a ConnectionException is thrown.
	 * 
	 * THIS METHOD WILL ALWAYS THROW AN EXCEPTION, NO MATTER WHAT!
	 * 
	 * @param rX
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	protected void translateRemoteException(RemoteException rX)
	throws MethodException, ConnectionException
	{
		if(rX.getClass() == AxisFault.class)
		{
			AxisFault af = (AxisFault)rX;
			if(af.getFaultCode() != null)
			{
				if(MethodException.class.getName().equals(af.getFaultCode().getLocalPart()))
				{
					throw new MethodException(rX);
				}
				else if(ConnectionException.class.getName().equals(af.getFaultCode().getLocalPart()))
				{
					throw new MethodException(rX);
				}
			}
		}
		// if got to here, then couldn't convert the exception
		// safer to throw connection exception since it will force the router to try again
		throw new ConnectionException(rX);		
	}
	
	protected void setDataSourceMethodAndVersion(String methodName)
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setDataSourceMethod(methodName);
		transactionContext.setDataSourceVersion(getDataSourceVersion());
	}
}
