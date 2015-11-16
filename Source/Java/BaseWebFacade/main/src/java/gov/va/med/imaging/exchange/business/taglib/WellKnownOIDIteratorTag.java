/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jun 5, 2008
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
package gov.va.med.imaging.exchange.business.taglib;

import gov.va.med.WellKnownOID;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author VHAISWBECKEC
 *
 */
public class WellKnownOIDIteratorTag 
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	private WellKnownOID[] values;
	private int index;

	@Override
    public int doStartTag() 
	throws JspException
    {
		try
        {
	        values = WellKnownOID.values();
	        index = 0;
	        
	        return values == null || values.length == 0 ? Tag.SKIP_BODY : Tag.EVAL_BODY_INCLUDE;
        } 
		catch (Exception e)
        {
        	throw new JspException( e.getMessage() );
        } 
    }
	
	public WellKnownOID getCurrentOID()
	{
		return values[index];
	}
	
	@Override
    public int doAfterBody() 
	throws JspException
    {
		++index;
		return index < values.length ? IterationTag.EVAL_BODY_AGAIN : Tag.SKIP_BODY;
    }

	
}
