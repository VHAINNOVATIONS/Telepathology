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
package gov.va.med.imaging.exchange.business.taglib.image;

import java.util.Iterator;

import gov.va.med.imaging.exchange.business.Image;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author VHAISWBECKEC
 *
 */
public class ImageCollectionElementTag 
extends AbstractImageTag
{
	private static final long serialVersionUID = 1L;

	private AbstractImageCollectionTag getParentImageCollectionTag()
	throws JspException
	{
		try
        {
	        return (AbstractImageCollectionTag)TagSupport.findAncestorWithClass(this, AbstractImageCollectionTag.class);
        } 
		catch (ClassCastException e)
        {
			throw new JspException("Parent tag of ImageCollectionElementTag must be of type AbstractImageCollectionTag");
        }
	}

	private Iterator<Image> getImageIterator() 
	throws JspException
	{
		AbstractImageCollectionTag parent = getParentImageCollectionTag();
		if(parent == null)
			return null;
		
		return parent.getImageIterator();
	}

	private Image image = null;
	/**
	 * An instance of this class displays one instance of an Image.
	 * 
	 * @see gov.va.med.imaging.exchange.business.taglib.image.AbstractImageTag#getImage()
	 */
	@Override
	public Image getImage() 
	throws JspException
	{
		return image;
	}

	/**
     * @see gov.va.med.imaging.exchange.business.taglib.image.AbstractImageTag#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException
    {
		Iterator<Image> imageIterator = getImageIterator();
		if(imageIterator == null)
			image = null;
		else if(imageIterator.hasNext())
			image = imageIterator.next();

	    return super.doStartTag();
    }

	
}
