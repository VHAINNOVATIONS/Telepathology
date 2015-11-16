package gov.va.med.imaging.exchange.business.taglib.study;

import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.taglib.series.AbstractSeriesCollectionTag;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This class is a concrete implementation of AbstractSeriesCollectionTag
 * where this tag is within an AbstractStudyTag element.
 * 
 * @author VHAISWBECKEC
 *
 */
public class StudySeriesCollectionTag 
extends AbstractSeriesCollectionTag
{
	private static final long serialVersionUID = 1L;

	private AbstractStudyTag getParentStudyTag()
	throws JspException
	{
		try
        {
			return (AbstractStudyTag)TagSupport.findAncestorWithClass(this, AbstractStudyTag.class);
        } 
		catch (ClassCastException e)
        {
			throw new JspException("StudySeriesCollectionTag must have an ancestor of type AbstractStudyTag");
        }
	}
	
	protected Study getStudy()
	throws JspException
	{
		return getParentStudyTag().getBusinessObject();
	}

	@Override
    protected Collection<Series> getSeriesCollection() 
    throws JspException
    {
	    return getStudy() == null ? null : getStudy().getSeries();
    }

}
