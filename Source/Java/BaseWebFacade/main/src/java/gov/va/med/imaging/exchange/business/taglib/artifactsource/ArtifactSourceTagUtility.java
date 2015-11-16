/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceTagUtility
{
	/**
	 * 
	 * @param subject
	 * @return
	 */
	protected static AbstractArtifactSourceTag getParentArtifactSourceTag(Tag subject)
	{
		return (AbstractArtifactSourceTag)TagSupport.findAncestorWithClass(subject, AbstractArtifactSourceTag.class);
	}

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	protected static ArtifactSource getArtifactSource(Tag subject) 
	throws JspException
	{
		AbstractArtifactSourceTag artifactSourceTag = ArtifactSourceTagUtility.getParentArtifactSourceTag(subject);
		if(artifactSourceTag == null)
			throw new JspException("An ArtifactSourceProperty tag does not have an ancestor ArtifactSource tag.");
		
		ArtifactSource artifactSource = artifactSourceTag.getArtifactSource();
		
		if(artifactSource == null)
			throw new JspException("An ArtifactSourceProperty tag was unable to get the ArtifactSource from its parent tag.");
		
		return artifactSource;
	}

}
