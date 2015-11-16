/**
 * 
 */
package gov.va.med.imaging.exchange.business.taglib.artifactsource;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.exchange.business.taglib.AbstractUrlCollectionTag;
import java.net.URL;
import java.util.Iterator;
import javax.servlet.jsp.JspException;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceMetadataUrlCollectionTag
extends AbstractUrlCollectionTag
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @return
	 * @throws JspException
	 */
	protected ArtifactSource getArtifactSource() 
	throws JspException
	{
		return ArtifactSourceTagUtility.getArtifactSource(this);
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.taglib.AbstractUrlCollectionTag#getUrls()
	 */
	@Override
	protected Iterator<URL> getUrlIterator() 
	throws JspException
	{
		return getArtifactSource().metadataIterator();
	}

}
