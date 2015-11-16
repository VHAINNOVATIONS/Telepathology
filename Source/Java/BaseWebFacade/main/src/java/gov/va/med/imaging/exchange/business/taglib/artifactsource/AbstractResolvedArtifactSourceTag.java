/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractResolvedArtifactSourceTag
extends BodyTagSupport
{
	private ResolvedArtifactSource resolvedArtifactSource;
	/**
	 * 
	 * @return
	 */
	protected abstract ResolvedArtifactSource getResolvedArtifactSource()
	throws JspException;

	@Override
    public int doStartTag() 
	throws JspException
    {
		resolvedArtifactSource = getResolvedArtifactSource();
		return resolvedArtifactSource == null ? BodyTag.SKIP_BODY : BodyTag.EVAL_BODY_INCLUDE;
    }

}
