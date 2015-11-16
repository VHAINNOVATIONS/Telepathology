package gov.va.med.imaging.exchange.business.taglib.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.taglib.image.AbstractImageCollectionTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class SeriesImageCollectionTag 
extends AbstractImageCollectionTag
{
	private static final long serialVersionUID = 1L;

	private AbstractSeriesTag getParentSeriesTag()
	throws JspException
	{
		try
        {
	        return (AbstractSeriesTag)TagSupport.findAncestorWithClass(this, AbstractSeriesTag.class);
        } 
		catch (ClassCastException e)
        {
			throw new JspException("SeriesImageCollectionTag must have an ancestor  of type AbstractSeriesTag");
        }
	}
	
	protected Series getSeries()
	throws JspException
	{
		return getParentSeriesTag().getSeries();
	}

	@Override
    protected Collection<Image> getImageCollection() 
    throws JspException
    {
		List<Image> images = new ArrayList<Image>();
		for(Image image : getSeries())
			images.add(image);
	    return images;
    }

}
