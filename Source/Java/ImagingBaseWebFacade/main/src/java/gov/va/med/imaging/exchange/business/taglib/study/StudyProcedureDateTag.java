package gov.va.med.imaging.exchange.business.taglib.study;

import java.text.DateFormat;

import javax.servlet.jsp.JspException;

public class StudyProcedureDateTag 
extends AbstractStudyPropertyTag
{
	private static final long serialVersionUID = 1L;
	private final DateFormat df = DateFormat.getDateInstance();
	
	@Override
    protected String getElementValue() 
	throws JspException
    {
		return getStudy().getProcedureDate() == null ? "" : df.format(getStudy().getProcedureDate());
    }

}
