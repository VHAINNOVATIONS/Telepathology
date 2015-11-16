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

import gov.va.med.imaging.access.je.taglib.TransactionLogEntryParent;
import gov.va.med.imaging.exchange.business.WelcomeMessage;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class WelcomeMessageEntryElement
extends TagSupport
{
	private static final long serialVersionUID = 3953770312534234790L;

	protected TransactionLogEntryParent getTransactionLogEntryParent()
	{
		return (TransactionLogEntryParent)TagSupport.findAncestorWithClass(this, TransactionLogEntryParent.class);
	}

	protected WelcomeMessage getWelcomeMessage()
	{
		WelcomeMessageTag welcomeMessageTag = (WelcomeMessageTag)TagSupport.findAncestorWithClass(this, WelcomeMessageTag.class);
		return welcomeMessageTag == null ? null : welcomeMessageTag.getWelcomeMessage();
	}
	
	protected WelcomeMessageTag getWelcomeMessageTag()
	{
		WelcomeMessageTag welcomeMessageTag = (WelcomeMessageTag)TagSupport.findAncestorWithClass(this, WelcomeMessageTag.class);
		return welcomeMessageTag;
	}
	
	protected abstract String getElementValue();
	
	@Override
    public int doEndTag() 
	throws JspException
    {
		try
        {
	        pageContext.getOut().write(getElementValue());
        } 
		catch (IOException e)
        {
			throw new JspException(e);
        }
		
	    return Tag.EVAL_PAGE;
    }

}
