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
import javax.servlet.jsp.PageContext;


/**
 * A tag that cycles through a list of strings given to it on reference
 * 
 * @author VHAISWBECKEC
 *
 */
public class TransactionLogEntryTransactionNumberChange 
extends TransactionLogEntryElement
{
	private static final long serialVersionUID = 1L;
	private String classNames;

	/**
	 * 
	 * @param classNames
	 */
	public void setClassNames(String classNames)
	{
		this.classNames = classNames;
	}
	
	/**
	 * @return the classNames
	 */
	public String getClassNames()
	{
		return this.classNames;
	}

	/**
	 * @return the classNames
	 */
	public String[] getClassNamesArray()
	{
		return classNames == null ? null : classNames.split(",");
	}

	/**
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() 
	throws JspException
	{
		int result = super.doStartTag();
		
		return result;
	}

	/**
	 * @see gov.va.med.imaging.access.je.taglib.TransactionLogEntryElement#getElementValue()
	 */
	@Override
	protected String getElementValue()
	{
		String previousClassName = 
			(String)this.pageContext.getAttribute("TransactionLogEntryTransactionNumberChange.lastClassName", PageContext.REQUEST_SCOPE);
		String previousTransactionId = 
			(String)this.pageContext.getAttribute("TransactionLogEntryTransactionNumberChange.transactionId", PageContext.REQUEST_SCOPE);
		TransactionLogEntry transactionLogEntry = getTransactionLogEntry();
		
		// IF first time we have run OR
		// something is wrong 'cause transactionLogEntry shouldn't be null OR
		// the transaction ID isn't in the log entry ||
		// the transaction ID has changed since the last transaction
		// THEN set the class name to the next element in the array (first element if previousClassName is null)
		if( previousClassName == null ||
			transactionLogEntry == null || 
			transactionLogEntry.getTransactionId() == null || 
			!transactionLogEntry.getTransactionId().equals(previousTransactionId) )
		{
			String nextClassName = getNextClassName(previousClassName);
			this.pageContext.setAttribute("TransactionLogEntryTransactionNumberChange.lastClassName", nextClassName, PageContext.REQUEST_SCOPE );
			if(transactionLogEntry != null)
				this.pageContext.setAttribute("TransactionLogEntryTransactionNumberChange.transactionId", transactionLogEntry.getTransactionId(), PageContext.REQUEST_SCOPE );
			return nextClassName;
		} 
		else
			return previousClassName;
	}
	
	/**
	 * 
	 * @param currentClassName
	 * @return
	 */
	private String getNextClassName(String currentClassName)
	{
		String[] classNamesArray = getClassNamesArray();
		
		if(currentClassName != null)
		{		
			boolean returnNext = false;
			for(String className : classNamesArray)
				if(returnNext)
					return className;
				else if(currentClassName.equals(className))
					returnNext = true;
		}
		
		return classNamesArray == null || classNamesArray.length < 1 ? "" : classNamesArray[0];
	}
}
