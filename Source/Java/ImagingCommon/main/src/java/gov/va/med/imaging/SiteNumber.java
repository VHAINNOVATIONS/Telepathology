/**
 * 
 */
package gov.va.med.imaging;

/**
 * @author VHAISWBATEL
 * 
 */
public class SiteNumber
{
	
	private final String siteNumber;
	
	
	/**
	 * Hold a Site Number for the Command Generator.
	 * @param siteNumber The Site Number of interest.
	 */
	public SiteNumber (String siteNumber) {this.siteNumber = siteNumber;}
	
	
	/**
	 * Get the SiteNumber this class represents.
	 * @return the SiteNumber this class represents.
	 */
	public String getSiteNumber () {return siteNumber;}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return siteNumber;	
	}
	
	
	
} // class SiteNumber
