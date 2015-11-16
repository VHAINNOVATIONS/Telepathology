/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Feb 4, 2008
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

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * This tag will generate a String that is a JavaScript method call, with the
 * enclosing image HREF as the only arg.  The method call is specified as the 'action' attribute.
 * Derived classes of this tag specify the image quality (Thumbnail, Reference, and Diagnostic).
 * 
 * @author VHAISWBECKEC
 */
public abstract class AbstractImageActionTag 
extends AbstractImageHRefTag
{
	private Logger logger = Logger.getLogger(this.getClass());
	private String action;
	
	/**
     * @return the action
     */
    public String getAction()
    {
    	return action;
    }

	/**
     * @param action the action to set
     */
    public void setAction(String action)
    {
    	this.action = action;
    }

	/**
	 * @see gov.va.med.imaging.exchange.business.taglib.image.AbstractImagePropertyTag#getElementValue()
	 */
	@Override
	protected String getElementValue() 
	throws JspException
	{
		logger.info("Building image action '" + getAction() + "', to href '" + super.getElementValue() + "'.");
		StringBuilder elementValue = new StringBuilder();
		
		elementValue.append(getAction());
		elementValue.append("('");
		elementValue.append(super.getElementValue());
		elementValue.append("');");

		return elementValue.toString();
	}
}
