package gov.va.med.imaging.exchange.business.taglib.study;

import javax.servlet.jsp.JspException;

public class StudyOriginTag 
extends AbstractStudyPropertyTag
{
	private static final long serialVersionUID = 1L;

	@Override
    protected String getElementValue() 
	throws JspException
    {
		return getStudy().getOrigin();
    }

}
