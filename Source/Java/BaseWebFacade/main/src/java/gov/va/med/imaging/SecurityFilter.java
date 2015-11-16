/**
 * 
 */
package gov.va.med.imaging;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.exchange.business.TransactionContextLogEntrySnapshot;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
//import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * The SecurityFilter sets some properties of the transaction context based
 * on initialization parameters in the web configuration file.  
 * This class also copies HTTP headers into the transaction context.
 * 
 * @author vhaiswbeckec
 *
 */
public class SecurityFilter 
implements Filter
{
	private Logger logger = Logger.getLogger(this.getClass());
	private boolean generateTransactionId = false;		// for a web app where the transaction initiates this may be true
	private boolean enableProtocolOverride = false;		// for test drivers, enable this in the web.xml to allow 
														// protocolOverride and targetSite query parameter
	
	private String machineName = null;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) 
	throws ServletException
	{
		generateTransactionId = Boolean.parseBoolean( config.getInitParameter("generateTransactionId") );
		enableProtocolOverride = Boolean.parseBoolean( config.getInitParameter("enableProtocolOverride") );
		
		logger.info("SecurityFilter for " + config.getServletContext().getServletContextName() + 
				(generateTransactionId ? " will" : " will not") + " generate transaction IDs if they do not exist.");
		logger.info("SecurityFilter for " + config.getServletContext().getServletContextName() + 
				(enableProtocolOverride ? " will" : " will not") + " allow protocol and target site ovveride.");
		
		// Determine the hostname for later logging.
		try
		{
 		   machineName = java.net.InetAddress.getLocalHost ().getHostName ();
		   if (machineName == null) machineName = java.net.InetAddress.getLocalHost ().getHostAddress ();
		   //if ("localhost".equalsIgnoreCase (machineName) || "127.0.0.1".equals (machineName)) machineName = null;
		}
		catch (java.net.UnknownHostException uhx)
		{
   		   machineName = "<unknown>";
		}
	}

	// =======================================================================================================
	// Accessors so that Spring can initialize 
	// =======================================================================================================
	public boolean isGenerateTransactionId()
    {
    	return generateTransactionId;
    }
	public void setGenerateTransactionId(boolean generateTransactionId)
    {
    	this.generateTransactionId = generateTransactionId;
    }

	public boolean isEnableProtocolOverride()
    {
    	return enableProtocolOverride;
    }
	public void setEnableProtocolOverride(boolean enableProtocolOverride)
    {
    	this.enableProtocolOverride = enableProtocolOverride;
    }

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(
		ServletRequest request, 
		ServletResponse response,
		FilterChain chain) 
	throws IOException, ServletException
	{
    	Long startTime = System.currentTimeMillis();
		// The principal should be accessible in the request and would be accessible if we knew this was
		// an HTTP request.
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setStartTime(startTime);
		
		// Determine the hostname for logging.
		// The init method never seems to get called.  Spring vs. Container loading?
		try
		{
 		   machineName = java.net.InetAddress.getLocalHost ().getHostName ();
		   if (machineName == null) machineName = java.net.InetAddress.getLocalHost ().getHostAddress ();
		   //if ("localhost".equalsIgnoreCase (machineName) || "127.0.0.1".equals (machineName)) machineName = null;
		}
		catch (java.net.UnknownHostException uhx)
		{
   		   machineName = "<unknown>";
		}
		
		// Record the hostname.
		transactionContext.setMachineName (machineName);
		
		if(request instanceof HttpServletRequest)
		{
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			logger.info(
				"TransactionContext " + 
				(Boolean.valueOf(transactionContext.isAuthenticatedByDelegate()) ? "is authenticated by delegate" : "is authenticated by VistA") + 
				".  VistaRealmSecurityContext, getting credentials from HTTP header information..." );
			
			transactionContext.setOriginatingAddress(httpRequest.getRemoteAddr() + ":" + httpRequest.getRemotePort());
			
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderDuz) != null)
				transactionContext.setDuz(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderDuz));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderFullName) != null)
				transactionContext.setFullName(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderFullName));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderSiteName) != null)
				transactionContext.setSiteName(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderSiteName));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderSiteNumber) != null)
				transactionContext.setSiteNumber(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderSiteNumber));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderSSN) != null)
				transactionContext.setSsn(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderSSN));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderTransactionId) != null)
				transactionContext.setTransactionId(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderTransactionId));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderPurposeOfUse) != null)
				transactionContext.setPurposeOfUse(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderPurposeOfUse));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderBrokerSecurityTokenId) != null)
				transactionContext.setBrokerSecurityToken(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderBrokerSecurityTokenId));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderCacheLocationId) != null)
				transactionContext.setCacheLocationId(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderCacheLocationId));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderUserDivision) != null)
				transactionContext.setUserDivision(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderUserDivision));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderClientVersion) != null)
				transactionContext.setClientVersion(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderClientVersion));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderRequestingVixSiteNumber) != null)
				transactionContext.setRequestingVixSiteNumber(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderRequestingVixSiteNumber));
			if(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderOptionContext) != null)
				transactionContext.setImagingSecurityContextType(httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderOptionContext));
			String httpHeaderAllowAddFederationCompression = httpRequest.getHeader(TransactionContextHttpHeaders.httpHeaderAllowAddFederationCompression);
			if(httpHeaderAllowAddFederationCompression != null && httpHeaderAllowAddFederationCompression.length() > 0)
			{
				transactionContext.setAllowAddFederationCompression(Boolean.parseBoolean(httpHeaderAllowAddFederationCompression));
			}
			
			if(transactionContext.getTransactionId() == null && generateTransactionId)
			{
				logger.info("Generated transaction ID.");
				transactionContext.setTransactionId( (new GUID()).toLongString() );
			}
			
			// If protocol override is enabled, and it should not be in a production
			// setting, then copy the protocol and target server into the transaction
			// context.
			if(enableProtocolOverride)
			{
				// "secret" request parameters that allow some control of the router
				String protocolOverride = request.getParameter("protocolOverride");
				String targetSite = request.getParameter("targetSite");
				
				if(targetSite != null && targetSite.length() > 0 && protocolOverride != null && protocolOverride.length() > 0)
				{
					logger.warn("Preferred protocols for transaction [" + transactionContext.getTransactionId() + 
						"] explicitly set to '" + protocolOverride + 
						"', and target server '" + targetSite + "'.");
					
					RoutingToken routingToken;
					try
					{
						routingToken = RoutingTokenImpl.createVARadiologySite(targetSite);
						transactionContext.setOverrideProtocol(protocolOverride);
						transactionContext.setOverrideRoutingToken(routingToken);
					}
					catch (RoutingTokenFormatException x)
					{
						throw new ServletException(x);
					}
				}
				
			}
		}	
		else
			logger.error("SecurityFilter servlet filter passed non-HTTP request, unable to provide security information.");
			
		logger.info("Transaction ID [" + transactionContext.getTransactionId() + "]");
		
		try
		{
			chain.doFilter(request, response);		// the remainder of the servlet chain and the servlet get called within here
		}
		catch(Exception ex)
		{
			// JMW 7/8/08 - we want to catch the exception so we can put
			// it into the transaction context (if there is no previous message).
			if((transactionContext.getErrorMessage() == null) ||
				(transactionContext.getErrorMessage().length() <= 0))
			{
				// CPT 8/14/08 - handle "exception_cause_message == null" case (e.g. NullPointerException)
				String msg=null;
				try 
				{
					if(ex.getCause() != null)
						msg = ex.getCause().getMessage();
					else
						msg = ex.toString();
				}
				catch (Exception e) 
				{
					msg = "Undelegated Exception";
				}
				logger.info("Caught exception [" + msg + "] in SecurityFilter and putting into transaction context");
				transactionContext.setErrorMessage(msg);
				transactionContext.setExceptionClassName(ex.getClass().getSimpleName());
			}
			if(ex.getClass() == IOException.class)
			{
				throw (IOException)ex;
			}
			else 
			{
				throw new ServletException(ex);
			}
		}
		finally
		{
			//write the current thread's TransactionContext to the Transaction Log
			logger.info("Writing entry to transaction log for transaction '" + transactionContext.getTransactionId() + "'");
			
			try 
			{
				BaseWebFacadeRouter router = FacadeRouterUtility
						.getFacadeRouter(BaseWebFacadeRouter.class);
				router.postTransactionLogEntryImmediate(new TransactionContextLogEntrySnapshot(transactionContext));
			} catch (Exception xAny) 
			{
				logger.error("postTransactionLogEntryImmediate Failed: "
						+ xAny.getMessage());
				// don't throw the exception so the client doesn't see it, this transaction will just be dropped
				//throw new ServletException(xAny);
			}

			// Clear the security context so that the thread has no remaining
			// references and has no
			// established security context when it is reused.
			// Once the transaction context is cleared, called to it will do
			// nothing but log a warning (and return null)
			transactionContext.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{
	}

}
