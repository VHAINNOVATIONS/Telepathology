/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 5, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.health;

import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteImpl;

import java.net.URL;

/**
 * @author vhaiswwerfej
 *
 */
public class VixSite
{
	private static final long serialVersionUID = 5818329770095915223L;
	
	private boolean siteVix = false;
	private boolean enabled = true;
	
	private String siteNumber; 
	private String siteName;
	private String siteAbbr;
	private String vistaServer; 
	private int vistaPort;
	private String acceleratorServer;
	private int acceleratorPort;
	private String regionId;
	
	public VixSite()
	{
		// used by XmlSerializer
	}
	
	public static VixSite createSiteVix(Site site)
	{
		VixSite vixSite = new VixSite(site.getSiteNumber(), site.getSiteName(), site.getSiteAbbr(),
			site.getVistaServer(), site.getVistaPort(), site.getAcceleratorServer(), site.getAcceleratorPort(), 
			site.getRegionId(), true);
		return vixSite;
	}
	
	public static VixSite createLocalVix(String siteNumber, String siteName, String siteAbbr, 
		String vixServer, int vixPort, boolean enabled)
	{
		VixSite vixSite = new VixSite("L" + siteNumber, siteName, siteAbbr, "", 0, vixServer, vixPort, "", true);
		vixSite.enabled = enabled;
		return vixSite;
	}
	
	public Site toSite()
	{
		return new SiteImpl(regionId, siteNumber, siteName, siteAbbr, new URL[0]);
	}

	private VixSite
	(
		String siteNumber, 
		String siteName, 
		String siteAbbr, 
		String vistaServer, 
		int vistaPort, 
		String acceleratorServer,
        int acceleratorPort,
        String regionId,
        boolean siteVix)
	{
		this.siteName = siteName;
		this.siteNumber = siteNumber;
		this.siteAbbr = siteAbbr;
		this.vistaServer = vistaServer;
		this.vistaPort = vistaPort;
		this.acceleratorPort = acceleratorPort;
		this.acceleratorServer = acceleratorServer;
		this.regionId = regionId;
		this.siteVix = siteVix;
	}
	
	

	/**
	 * @return the siteVix
	 */
	public boolean isSiteVix()
	{
		return siteVix;
	}

	/**
	 * @param siteVix the siteVix to set
	 */
	public void setSiteVix(boolean siteVix)
	{
		this.siteVix = siteVix;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber()
	{
		return siteNumber;
	}

	/**
	 * @param siteNumber the siteNumber to set
	 */
	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName()
	{
		return siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	/**
	 * @return the siteAbbr
	 */
	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	/**
	 * @param siteAbbr the siteAbbr to set
	 */
	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	/**
	 * @return the vistaServer
	 */
	public String getVistaServer()
	{
		return vistaServer;
	}

	/**
	 * @param vistaServer the vistaServer to set
	 */
	public void setVistaServer(String vistaServer)
	{
		this.vistaServer = vistaServer;
	}

	/**
	 * @return the vistaPort
	 */
	public int getVistaPort()
	{
		return vistaPort;
	}

	/**
	 * @param vistaPort the vistaPort to set
	 */
	public void setVistaPort(int vistaPort)
	{
		this.vistaPort = vistaPort;
	}

	/**
	 * @return the acceleratorServer
	 */
	public String getAcceleratorServer()
	{
		return acceleratorServer;
	}

	/**
	 * @param acceleratorServer the acceleratorServer to set
	 */
	public void setAcceleratorServer(String acceleratorServer)
	{
		this.acceleratorServer = acceleratorServer;
	}

	/**
	 * @return the acceleratorPort
	 */
	public int getAcceleratorPort()
	{
		return acceleratorPort;
	}

	/**
	 * @param acceleratorPort the acceleratorPort to set
	 */
	public void setAcceleratorPort(int acceleratorPort)
	{
		this.acceleratorPort = acceleratorPort;
	}

	/**
	 * @return the regionId
	 */
	public String getRegionId()
	{
		return regionId;
	}

	/**
	 * @param regionId the regionId to set
	 */
	public void setRegionId(String regionId)
	{
		this.regionId = regionId;
	}

}
