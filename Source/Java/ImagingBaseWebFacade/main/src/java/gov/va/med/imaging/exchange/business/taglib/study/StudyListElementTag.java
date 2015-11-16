/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 30, 2008
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
package gov.va.med.imaging.exchange.business.taglib.study;

import gov.va.med.imaging.exchange.business.Study;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This concrete implementation of an AbstractStudyTag may be used within
 * any derivation of an AbstractStudyListTag element.
 * The parent AbstractStudyListTag will create the Iterator<Study> property
 * that this element depends on. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class StudyListElementTag 
extends AbstractStudyTag
{
	private static final long serialVersionUID = 1L;
	
    private AbstractStudyListTag getParentListTag()
	throws JspException
	{
		try
        {
			return (AbstractStudyListTag)TagSupport.findAncestorWithClass(this, AbstractStudyListTag.class);
        } 
		catch (ClassCastException e)
        {
			throw new JspException("StudyListElementTag must have an ancestor of type AbstractStudyListTag");
        }
	}

    // get our study to display from the parent list iterator
    @Override
	public Study getBusinessObject() 
	throws JspException
    {
		return getParentListTag().getCurrent();
    }
}
