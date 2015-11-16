/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractArtifactSourceTag
extends BodyTagSupport
{
	private ArtifactSource artifactSource;
	/**
	 * 
	 * @return
	 */
	protected abstract ArtifactSource getArtifactSource()
	throws JspException;

	@Override
    public int doStartTag() 
	throws JspException
    {
		artifactSource = getArtifactSource();
		return artifactSource == null ? BodyTag.SKIP_BODY : BodyTag.EVAL_BODY_INCLUDE;
    }

}
