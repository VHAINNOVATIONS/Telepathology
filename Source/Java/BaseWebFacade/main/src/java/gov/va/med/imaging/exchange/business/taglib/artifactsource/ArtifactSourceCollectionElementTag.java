/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceCollectionElementTag
extends AbstractArtifactSourceTag
{

	private static final long serialVersionUID = 1L;

	private AbstractArtifactSourceCollectionTag getParentCollectionTag()
	{
		return (AbstractArtifactSourceCollectionTag)TagSupport.findAncestorWithClass(this, AbstractArtifactSourceCollectionTag.class);
	}
	
	/**
	 * @see gov.va.med.imaging.exchange.business.taglib.site.AbstractSiteTag#getSite()
	 */
	@Override
	protected ArtifactSource getArtifactSource() 
	throws JspException
	{
		AbstractArtifactSourceCollectionTag parent = getParentCollectionTag();
		if(parent == null)
			throw new JspException("SiteCollectionElementTag must have an ancestor AbstractSiteCollectionTag, and does not.");
		
		return parent.getCurrentArtifactSource();
	}
}
