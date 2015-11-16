/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.exchange.business.Site;
import javax.servlet.jsp.JspException;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceAsSiteTag
extends AbstractSiteTag
{
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.artifactsource.AbstractSiteTag#getSite()
	 */
	@Override
	protected Site getSite() 
	throws JspException
	{
		ArtifactSource artifactSource = ArtifactSourceTagUtility.getArtifactSource(this);
		
		return artifactSource instanceof Site ? (Site)artifactSource : null;
	}

}
