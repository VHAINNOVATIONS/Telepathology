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

import javax.servlet.jsp.JspException;

import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.taglib.image.AbstractImageTag;

/**
 * A derivation of the AbstractImageTag that sets its image property 
 * from an enclosing AbstractStudyTag instance.
 * 
 * @author VHAISWBECKEC
 *
 */
public class StudyFirstImageTag 
extends AbstractImageTag
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	private AbstractStudyTag getParentStudyTag()
	throws JspException
	{
		try
        {
	        return (AbstractStudyTag)this.getParent();
        } 
		catch (ClassCastException e)
        {
			throw new JspException("Parent tag of AbstractStudyPropertyTag must be of type AbstractStudyTag");
        }
	}

	private Image image = null;
	/**
     * @see gov.va.med.imaging.exchange.business.taglib.image.AbstractImageTag#getImage()
     */
    @Override
    public synchronized Image getImage() 
    throws JspException
    {
    	/*
		// doing this caused the same image to always be shown.
    	if(image == null)
    	{
    		Study study = getParentStudyTag().getBusinessObject();
    		image = study == null ? null : study.getFirstImage();
    	}
    	*/
    	Study study = getParentStudyTag().getBusinessObject();
		image = study == null ? null : study.getFirstImage();
	    return image;
    }

}
