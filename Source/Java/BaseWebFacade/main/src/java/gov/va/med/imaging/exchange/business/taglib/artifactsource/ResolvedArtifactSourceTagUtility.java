/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class ResolvedArtifactSourceTagUtility
{
	/**
	 * 
	 * @param subject
	 * @return
	 */
	protected static AbstractResolvedArtifactSourceTag getParentResolvedArtifactSourceTag(Tag subject)
	{
		return (AbstractResolvedArtifactSourceTag)
			TagSupport.findAncestorWithClass(subject, AbstractResolvedArtifactSourceTag.class);
	}

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	protected static ResolvedArtifactSource getResolvedArtifactSource(Tag subject) 
	throws JspException
	{
		AbstractResolvedArtifactSourceTag resolvedArtifactSourceTag = 
			ResolvedArtifactSourceTagUtility.getParentResolvedArtifactSourceTag(subject);
		if(resolvedArtifactSourceTag == null)
			throw new JspException("A ResolvedArtifactSourceProperty tag does not have an ancestor ResolvedArtifactSource tag.");
		
		ResolvedArtifactSource resolvedArtifactSource = resolvedArtifactSourceTag.getResolvedArtifactSource();
		
		if(resolvedArtifactSource == null)
			throw new JspException("A ResolvedArtifactSourceProperty tag was unable to get the ResolvedArtifactSource from its parent tag.");
		
		return resolvedArtifactSource;
	}

}
