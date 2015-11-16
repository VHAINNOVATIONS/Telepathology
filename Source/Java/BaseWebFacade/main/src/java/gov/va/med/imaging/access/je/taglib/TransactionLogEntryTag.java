/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 28, 2008
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
package gov.va.med.imaging.access.je.taglib;

import gov.va.med.imaging.access.TransactionLogEntry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author VHAISWBECKEC
 *
 */
public class TransactionLogEntryTag 
extends BodyTagSupport 
implements TransactionLogEntryParent
{
	private static final long serialVersionUID = 1L;

	private TransactionLogEnumerationParent getEnumerationParent()
	{
		return (TransactionLogEnumerationParent)TagSupport.findAncestorWithClass(this, TransactionLogEnumerationParent.class);
	}

	/**
     * @see gov.va.med.imaging.access.je.taglib.TransactionLogEntryParent#getTransactionLogEntry()
     */
	public TransactionLogEntry getTransactionLogEntry()
	{
		return getEnumerationParent() == null ? null : getEnumerationParent().getCurrentTransactionLogEntry();
	}
	
	@Override
    public int doStartTag() 
	throws JspException
    {
	    return getTransactionLogEntry() == null ? Tag.SKIP_BODY : Tag.EVAL_BODY_INCLUDE;
    }
}
