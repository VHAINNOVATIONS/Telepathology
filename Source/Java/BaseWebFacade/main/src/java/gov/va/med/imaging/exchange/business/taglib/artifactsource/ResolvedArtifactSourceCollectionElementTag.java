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
public class ResolvedArtifactSourceCollectionElementTag
extends AbstractResolvedArtifactSourceTag
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param subject
	 * @return
	 */
	protected AbstractResolvedArtifactSourceCollectionTag getParentResolvedArtifactSourceCollectionTag()
	{
		return (AbstractResolvedArtifactSourceCollectionTag)
			TagSupport.findAncestorWithClass(this, AbstractResolvedArtifactSourceCollectionTag.class);
	}

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	@Override
	protected ResolvedArtifactSource getResolvedArtifactSource() 
	throws JspException
	{
		AbstractResolvedArtifactSourceCollectionTag resolvedArtifactSourceCollectionTag = 
			getParentResolvedArtifactSourceCollectionTag();
		if(resolvedArtifactSourceCollectionTag == null)
			throw new JspException("A ResolvedArtifactSourceCollectionElement tag does not have an ancestor ResolvedArtifactSourceCollection tag.");
		
		ResolvedArtifactSource resolvedArtifactSource = 
			resolvedArtifactSourceCollectionTag.getCurrentResolvedArtifactSource();
		
		if(resolvedArtifactSource == null)
			throw new JspException("A ResolvedArtifactSourceProperty tag was unable to get the ResolvedArtifactSource from its parent tag.");
		
		return resolvedArtifactSource;
	}
	
}
