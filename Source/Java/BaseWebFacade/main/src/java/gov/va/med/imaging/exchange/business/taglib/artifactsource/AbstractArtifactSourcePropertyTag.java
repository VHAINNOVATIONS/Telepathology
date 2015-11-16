/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractArtifactSourcePropertyTag
extends BodyTagSupport
{
	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	protected ArtifactSource getArtifactSource() 
	throws JspException
	{
		return ArtifactSourceTagUtility.getArtifactSource(this);
	}
	
	protected Writer getWriter() 
	throws IOException
	{
		return pageContext.getOut();
	}
	
	public abstract String getElementValue() 
	throws JspException;

	@Override
    public int doEndTag() 
	throws JspException
    {
    	try
        {
	        getWriter().write(getElementValue());
        } 
    	catch (IOException e)
        {
    		throw new JspException(e);
        }
    	
    	return Tag.EVAL_PAGE;
    }
}
