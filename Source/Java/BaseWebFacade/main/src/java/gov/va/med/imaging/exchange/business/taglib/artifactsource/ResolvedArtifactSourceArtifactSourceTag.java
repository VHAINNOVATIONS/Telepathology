/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import javax.servlet.jsp.JspException;

/**
 * @author vhaiswbeckec
 *
 */
public class ResolvedArtifactSourceArtifactSourceTag
extends AbstractArtifactSourceTag
{
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.artifactsource.AbstractArtifactSourceTag#getArtifactSource()
	 */
	@Override
	protected ArtifactSource getArtifactSource() 
	throws JspException
	{
		ResolvedArtifactSource resolvedArtifactSource = 
			ResolvedArtifactSourceTagUtility.getResolvedArtifactSource(this);
		
		if(resolvedArtifactSource == null || resolvedArtifactSource.getArtifactSource() == null)
			throw new JspException("Unable to get the resolved artifact source, or the child artifact source.");
		return resolvedArtifactSource.getArtifactSource();
	}

}
