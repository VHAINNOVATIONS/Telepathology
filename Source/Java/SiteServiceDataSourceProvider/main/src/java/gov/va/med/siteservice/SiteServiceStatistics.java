/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 25, 2010
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
package gov.va.med.siteservice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Site service MBean implementation
 * 
 * @author vhaiswwerfej
 *
 */
public class SiteServiceStatistics 
implements SiteServiceStatisticsMBean
{	
	private final SiteService siteService;
	
	public SiteServiceStatistics(SiteService siteService)
	{
		this.siteService = siteService;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteServiceMBean#getLastUpdate()
	 */
	@Override
	public String getLastUpdate()
	{
		Date lastUpdate = siteService.getLastCacheUpdate();
		if(lastUpdate != null)
		{
			String format = "MM/dd/yyyy HH:mm:ss.SSS";
			DateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(siteService.getLastCacheUpdate());
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteServiceMBean#getSiteServiceUrl()
	 */
	@Override
	public String getSiteServiceUrl()
	{
		return siteService.getSiteServiceSource();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.siteservice.SiteServiceMBean#refreshCache()
	 */
	@Override
	public void refreshCache()
	{
		siteService.refreshCache();
	}

	@Override
	public String getSiteServiceDataSourceVersion()
	{
		return siteService.getSiteServiceDataSourceVersion();
	}

}
