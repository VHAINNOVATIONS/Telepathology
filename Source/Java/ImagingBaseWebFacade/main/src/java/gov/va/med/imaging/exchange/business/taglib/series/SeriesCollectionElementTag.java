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
package gov.va.med.imaging.exchange.business.taglib.series;

import gov.va.med.imaging.exchange.business.Series;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The concrete implementation of a AbstractSeriesTag where the 
 * tag is within a AbstractSeriesListTag
 * @author VHAISWBECKEC
 *
 */
public class SeriesCollectionElementTag 
extends AbstractSeriesTag
{
	private static final long serialVersionUID = 1L;

	private AbstractSeriesCollectionTag getParentSeriesListTag()
	throws JspException
	{
		try
        {
	        return (AbstractSeriesCollectionTag)TagSupport.findAncestorWithClass(this, AbstractSeriesCollectionTag.class);
        } 
		catch (ClassCastException e)
        {
			throw new JspException("SeriesCollectionElementTag must have an ancestor of type AbstractSeriesListTag");
        }
	}

	private Series series;
	/**
	 * @see gov.va.med.imaging.exchange.business.taglib.series.AbstractSeriesTag#getSeries()
	 */
	@Override
	protected Series getSeries() 
	throws JspException
	{
		return series;
	}
	
	/**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() 
    throws JspException
    {
		AbstractSeriesCollectionTag parent = getParentSeriesListTag();
		if( parent == null || parent.getSeriesIterator() == null )
			series = null;
		else
			series = parent.getSeriesIterator().hasNext() ? parent.getSeriesIterator().next() : null;
			
	    return super.doStartTag();
    }
}
