/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 9, 2008
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
package gov.va.med.imaging.system;

import java.io.IOException;

import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * The realm name returned from here identifies the site number that
 * this Vix fronts for.  The realm name is defined in the instantiation
 * of the VistaRealm.
 */
public class RealmNameTag 
extends TagSupport
{
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 
	 */
	public RealmNameTag()
	{
	}

	@Override
    public int doStartTag() 
	throws JspException
    {
		TransactionContext tc = TransactionContextFactory.get();
		if(tc != null)
		{
	        try
            {
	        	pageContext.getOut().write( tc.getRealm() );
            } 
	        catch (IOException e)
            {
	        	logger.error(e);
	        	throw new JspException(e);
            }
		}
	    return Tag.EVAL_PAGE;
    }

	
}
