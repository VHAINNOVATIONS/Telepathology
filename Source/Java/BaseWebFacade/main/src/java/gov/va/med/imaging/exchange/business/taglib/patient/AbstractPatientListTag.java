/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 22, 2008
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
package gov.va.med.imaging.exchange.business.taglib.patient;

import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.exchange.business.Patient;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.JspAwareRequestContext;
import org.springframework.web.servlet.support.RequestContext;

/**
 * This class must be subclassed with something that will set the
 * List of Study instances to display.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractPatientListTag 
extends BodyTagSupport
implements TryCatchFinally
{
	public static final String REQUEST_CONTEXT_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.REQUEST_CONTEXT";
	/**
	 * {@link javax.servlet.jsp.PageContext} attribute for page-level
	 * {@link RequestContext} instance.
	 */
	private static final long serialVersionUID = 1L;
	private String emptyResultMessage = null;
	
	private Logger logger = Logger.getLogger(this.getClass());
	private RequestContext requestContext;
    private Iterator<Patient> patientIterator;
    private Patient currentPatient;
	
	/**
     * @return the studyList
     */
    protected abstract List<Patient> getPatientList()
    throws JspException;

	/**
	 * The message to show if the study list is empty
	 * @return
	 */
	public String getEmptyResultMessage()
    {
    	return emptyResultMessage;
    }

	public void setEmptyResultMessage(String emptyResultMessage)
    {
    	this.emptyResultMessage = emptyResultMessage;
    }
	
	/**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() 
    throws JspException
    {
    	List<Patient> patientList = getPatientList();
    	
    	if(patientList != null && patientList.size() > 0)
    	{
    		patientIterator = patientList.iterator();
    		currentPatient = patientIterator.next();
    	    return Tag.EVAL_BODY_INCLUDE;
    	}
    	
    	if(getEmptyResultMessage() != null)
    		try{pageContext.getOut().write(getEmptyResultMessage());}
    		catch(IOException ioX){logger.error("Unable to write empty result set message.");}

    	return Tag.SKIP_BODY;
    }

    Patient getCurrentPatient()
    {
    	return currentPatient;
    }
        
	@Override
    public int doAfterBody() 
	throws JspException
    {
	    if( patientIterator.hasNext() )
	    {
	    	currentPatient = patientIterator.next();
	    	return IterationTag.EVAL_BODY_AGAIN;
	    }
	    else
	    {
	    	currentPatient = null;
	    	return IterationTag.SKIP_BODY;
	    }
    }

	/**
     * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
     */
    @Override
    public int doEndTag() 
    throws JspException
    {
	    return Tag.EVAL_PAGE;
    }

	// ==============================================================================
	// TryCatchFinally Events
	// ==============================================================================

	/**
	 * @see javax.servlet.jsp.tagext.TryCatchFinally#doCatch(java.lang.Throwable)
	 */
	@Override
    public void doCatch(Throwable t) 
	throws Throwable
    {
		logger.error(t);
		throw new JspException(t);
    }

	@Override
    public void doFinally()
    {
	    
    }
    
}
