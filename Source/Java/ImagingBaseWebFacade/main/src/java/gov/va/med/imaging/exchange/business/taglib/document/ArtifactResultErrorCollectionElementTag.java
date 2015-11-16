/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.document;

import gov.va.med.imaging.exchange.business.ArtifactResultError;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactResultErrorCollectionElementTag
extends AbstractArtifactResultErrorTag
{
	private static final long serialVersionUID = 1L;

	protected AbstractArtifactResultErrorCollectionTag getParentTag()
	{
		return (AbstractArtifactResultErrorCollectionTag)TagSupport.findAncestorWithClass(this, AbstractArtifactResultErrorCollectionTag.class);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractBusinessObjectTag#getBusinessObject()
	 */
	@Override
	public ArtifactResultError getBusinessObject() 
	throws JspException
	{
		return getParentTag().getCurrent();
	}

}
