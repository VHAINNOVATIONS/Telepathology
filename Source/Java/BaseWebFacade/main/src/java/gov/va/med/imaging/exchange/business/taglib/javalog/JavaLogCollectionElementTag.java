/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 16, 2009
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
package gov.va.med.imaging.exchange.business.taglib.javalog;

import gov.va.med.imaging.javalogs.JavaLogFile;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Java Log Collection tag for getting the element in a collection of java logs
 * 
 * @author vhaiswwerfej
 *
 */
public class JavaLogCollectionElementTag 
extends AbstractJavaLogTag 
{
	private static final long serialVersionUID = 1L;

	private AbstractJavaLogCollectionTag getParentCollectionTag()
	{
		return (AbstractJavaLogCollectionTag)TagSupport.findAncestorWithClass(this, AbstractJavaLogCollectionTag.class);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.javalog.AbstractJavaLogTag#getFile()
	 */
	@Override
	protected JavaLogFile getFile() 
	throws JspException 
	{
		AbstractJavaLogCollectionTag parent = getParentCollectionTag();
		if(parent == null)
			throw new JspException("JavaLogCollectionElementTag must have an ancestor AbstractJavaLogCollectionTag, and does not.");
		
		return parent.getCurrentFile();
	}
}
