/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 31, 2012
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
package gov.va.med.imaging.exchange.business.taglib.welcomemessage;

import java.util.UUID;

import gov.va.med.RoutingToken;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.BaseWebFacadeRouter;
import gov.va.med.imaging.core.FacadeRouterUtility;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.RoutingTokenHelper;
import gov.va.med.imaging.exchange.business.WelcomeMessage;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class WelcomeMessageTag
extends BodyTagSupport
{
	private static final long serialVersionUID = 8678407269515422254L;
	private final static Logger logger = Logger.getLogger(WelcomeMessageTag.class);
	
	private String siteNumber = null;
	private WelcomeMessage welcomeMessage = null;
	private String errorMessage = null;
	
	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber()
	{
		return siteNumber;
	}

	/**
	 * @param siteNumber the siteNumber to set
	 */
	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * @return the welcomeMessage
	 */
	public WelcomeMessage getWelcomeMessage()
	{
		return welcomeMessage;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() 
	throws JspException
	{
		return BodyTag.SKIP_BODY;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() 
	throws JspException
	{
		return BodyTag.EVAL_PAGE;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() 
	throws JspException
	{
		this.errorMessage = null;
		this.welcomeMessage = null;
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setRequestType("getWelcomeMessage");
		transactionContext.setTransactionId(UUID.randomUUID().toString());
		
		ServletRequest servletRequest = this.pageContext.getRequest();
		transactionContext.setOriginatingAddress(servletRequest.getRemoteAddr() + ":" + servletRequest.getRemotePort());
		try
		{
			RoutingToken routingToken = RoutingTokenHelper.createSiteAppropriateRoutingToken(getSiteNumber()); 
			BaseWebFacadeRouter router = FacadeRouterUtility.getFacadeRouter (BaseWebFacadeRouter.class);
			this.welcomeMessage = router.getCachedWelcomeMessage(routingToken);				
		} 
		catch (MethodException mX)
		{
			errorMessage = "MethodException retrieving welcome message, " + mX.getMessage();
			logger.error (errorMessage, mX);
	        //throw new JspException(errorMessage);			
		} 
		catch (ConnectionException cX)
		{
			errorMessage = "ConnectionException retrieving welcome message, " + cX.getMessage();
			logger.error (errorMessage, cX);
	        //throw new JspException(errorMessage);
		} 
		catch (RoutingTokenFormatException rtfX)
		{
			errorMessage = "RoutingTokenFormatException retrieving welcome message, " + rtfX.getMessage();
			logger.error (errorMessage, rtfX);
	        //throw new JspException(errorMessage);
		}
		catch(Exception ex)
		{
			errorMessage = "Exception retrieving welcome message, " + ex.getMessage();
			logger.error (errorMessage, ex);
	        //throw new JspException(errorMessage);
		}
		
		return (this.welcomeMessage == null && this.errorMessage == null) ? BodyTag.SKIP_BODY : BodyTag.EVAL_BODY_INCLUDE; 
	}

}
