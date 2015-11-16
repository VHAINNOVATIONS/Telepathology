/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import javax.servlet.jsp.JspException;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceMetadataUrlCountTag
extends AbstractArtifactSourcePropertyTag
{
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.artifactsource.AbstractArtifactSourcePropertyTag#getElementValue()
	 */
	@Override
	public String getElementValue() throws JspException
	{
		return Integer.toString(getArtifactSource().getMetadataServerCount());
	}

}
