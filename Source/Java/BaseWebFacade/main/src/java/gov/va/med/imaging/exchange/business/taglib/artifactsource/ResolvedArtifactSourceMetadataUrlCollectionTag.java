/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.exchange.business.taglib.AbstractUrlCollectionTag;
import java.net.URL;
import java.util.Collection;
import javax.servlet.jsp.JspException;

/**
 * @author vhaiswbeckec
 *
 */
public class ResolvedArtifactSourceMetadataUrlCollectionTag
extends AbstractUrlCollectionTag
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	protected ResolvedArtifactSource getResolvedArtifactSource() 
	throws JspException
	{
		return ResolvedArtifactSourceTagUtility.getResolvedArtifactSource(this);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractUrlCollectionTag#getUrls()
	 */
	@Override
	protected Collection<URL> getUrls() 
	throws JspException
	{
		return getResolvedArtifactSource().getMetadataUrls();
	}
}
