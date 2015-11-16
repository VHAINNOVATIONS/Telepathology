package gov.va.med.imaging.exchange.business.taglib.patient;

import gov.va.med.imaging.exchange.business.Patient;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractPatientTag 
extends BodyTagSupport
{
	private static final long serialVersionUID = 1L;
	
	public AbstractPatientTag()
	{
		super();
	}

	protected abstract Patient getPatient()
	throws JspException;

    @Override
    public int doStartTag() 
    throws JspException
    {
    	if(getPatient() != null)
    	{
    	    return BodyTag.EVAL_BODY_INCLUDE;
    	}
    	else
    		return Tag.SKIP_BODY;
    }
}