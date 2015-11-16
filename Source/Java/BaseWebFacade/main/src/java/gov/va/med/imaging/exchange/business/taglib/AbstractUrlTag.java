/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib;

import java.io.IOException;
import java.net.URL;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractUrlTag
extends TagSupport
{
	public abstract URL getUrl() 
	throws JspException;
	
	@Override
	public int doEndTag() 
	throws JspException
	{
		URL url = getUrl();
		
		if(url != null)
			try
			{
				this.pageContext.getResponse().getWriter().write(url.toExternalForm());
			}
			catch (IOException x)
			{
				throw new JspException(x.getMessage());
			}
		
		return Tag.EVAL_PAGE;
	}

}
