/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 20, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.vista.siteservice.translator;

import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.url.vista.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Translator for Site Service
 * 
 * @author VHAISWWERFEJ
 *
 */
public class SiteServiceTranslator 
{
	private final static Logger logger = Logger.getLogger(SiteServiceTranslator.class);
	private final static String siteNumberDelimiter = "^";

	public static gov.va.med.vistaweb.WebServices.SiteService.SiteTO convertSite(Site site, String siteNumber)
	{
		gov.va.med.vistaweb.WebServices.SiteService.SiteTO siteTo = 
			new gov.va.med.vistaweb.WebServices.SiteService.SiteTO();
		try 
		{
			siteTo.setRegionID(site.getRegionId());
			siteTo.setMoniker(site.getSiteAbbr());
			siteTo.setName(site.getSiteName());
			siteTo.setSitecode(site.getSiteNumber());
			siteTo.setHostname(site.getVistaServer());
			siteTo.setPort(site.getVistaPort());
		}
		catch(Exception ex)
		{
			logger.error(ex.getClass().getName() + " Error translating site (" + siteNumber + ")");
			siteTo.setSitecode(siteNumber);			
			siteTo.setFaultTO(createFault(ex, "Invalid site code?"));
		}
		return siteTo;
	}
	
	public static gov.va.med.vistaweb.WebServices.SiteService.SiteTO [] convertSites(List<Site> sites)
	{
		List<gov.va.med.vistaweb.WebServices.SiteService.SiteTO> sitesTo = 
			new ArrayList<gov.va.med.vistaweb.WebServices.SiteService.SiteTO>();		
		for(Site site : sites)
		{
			sitesTo.add(convertSite(site, ""));
		}
		return sitesTo.toArray(new gov.va.med.vistaweb.WebServices.SiteService.SiteTO[sitesTo.size()]);
	}
	
	public static gov.va.med.vistaweb.WebServices.SiteService.RegionTO convertRegion(Region region, String regionId)
	{		
		gov.va.med.vistaweb.WebServices.SiteService.RegionTO result = 
			new gov.va.med.vistaweb.WebServices.SiteService.RegionTO();
		try
		{			
			result.setID(region.getRegionNumber());
			result.setName(region.getRegionName());
			
			gov.va.med.vistaweb.WebServices.SiteService.SiteTO [] sites = convertSites(region.getSites());
			result.setSites(new gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO(sites));
		}
		catch(Exception ex)
		{
			logger.error(ex.getClass().getName() + " Error translating region (" + regionId + ")");
			result.setID(regionId);
			result.setFaultTO(createFault(ex, "Invalid VISN number?"));
		}
		return result;		
	}
	
	public static gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO convertRegions(List<Region> regions)
	{
		gov.va.med.vistaweb.WebServices.SiteService.RegionTO [] regionsTo = new
			gov.va.med.vistaweb.WebServices.SiteService.RegionTO[regions.size()];
		for(int i = 0; i < regions.size(); i++)
		{
			Region region = regions.get(i);
			regionsTo[i] = convertRegion(region, "");
		}
		
		gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO result = new 
			gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO(regionsTo);
		
		return result;
	}
	
	/**
	 * Converted the delimited string into an array of site numbers
	 * @param delimitedSiteNumbers
	 * @return
	 */
	public static String[] convertDelimitedStringsIntoSiteNumbers(String delimitedSiteNumbers)
	{
		return StringUtils.Split(delimitedSiteNumbers, siteNumberDelimiter);
	}
	
	/**
	 * Create a fault based on an exception
	 * @param ex An exception that occurred
	 * @param suggestion Suggestion for the cause of the problem
	 * @return
	 */
	private static gov.va.med.vistaweb.WebServices.SiteService.FaultTO createFault(Exception ex, String suggestion)
	{		
		gov.va.med.vistaweb.WebServices.SiteService.FaultTO fault = 
			new gov.va.med.vistaweb.WebServices.SiteService.FaultTO(ex.getClass().toString(), ex.getMessage(), null, suggestion);
		return fault;
	}
}
