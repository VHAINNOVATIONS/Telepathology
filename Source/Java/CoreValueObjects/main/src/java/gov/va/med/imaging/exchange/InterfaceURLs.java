package gov.va.med.imaging.exchange;

import java.net.URL;

/**
 * For each interface that a site supports there are two URLs needed,
 * one for the metadata and one for the images themselves.
 * This class encapsulates those two URLs into a single object.
 * 
 * NOTE: the setIndex property is to force proper operation of
 * SortedSet from within the SiteResolutionDataSource implementations.
 * The natural ordering of this class is by setIndex.  It is up to the
 * code that creates the containing SortedSet to provide a proper value
 * for setIndex.
 * 
 * @author VHAISWBECKEC
 *
 */
public class InterfaceURLs
implements Comparable<InterfaceURLs>, Cloneable
{
	private final long setIndex;
	private final URL metadataUrl;
	private final URL imageUrl;
	
	public InterfaceURLs(URL metadataUrl, URL imageUrl, long setIndex)
    {
        super();
        this.metadataUrl = metadataUrl;
        this.imageUrl = imageUrl;
        this.setIndex = setIndex;
    }

	/**
     * @return the metadataUrl
     */
    public URL getMetadataUrl()
    {
    	return metadataUrl;
    }

	/**
     * @return the imageUrl
     */
    public URL getImageUrl()
    {
    	return imageUrl;
    }

	@Override
    public int compareTo(InterfaceURLs that)
    {
	    return this.setIndex < that.setIndex ? -1 :
	    	this.setIndex > that.setIndex ? 1 : 0;
    }

	public InterfaceURLs clone()
	{
		InterfaceURLs interfaceUrl = new InterfaceURLs(
			this.getMetadataUrl(),
			this.getImageUrl(),
			this.setIndex );
		
		return interfaceUrl;
	}
	
	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
	    result = prime * result + ((metadataUrl == null) ? 0 : metadataUrl.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    final InterfaceURLs other = (InterfaceURLs) obj;
	    if (imageUrl == null)
	    {
		    if (other.imageUrl != null)
			    return false;
	    } else if (!imageUrl.equals(other.imageUrl))
		    return false;
	    if (metadataUrl == null)
	    {
		    if (other.metadataUrl != null)
			    return false;
	    } else if (!metadataUrl.equals(other.metadataUrl))
		    return false;
	    return true;
    }
	
}