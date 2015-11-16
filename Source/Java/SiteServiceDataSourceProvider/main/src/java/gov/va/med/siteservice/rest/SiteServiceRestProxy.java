/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 28, 2012
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
package gov.va.med.siteservice.rest;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.siteservice.rest.translator.SiteServiceRestTranslator;
import gov.va.med.imaging.exchange.siteservice.rest.types.SiteServiceVisnsType;
import gov.va.med.siteservice.RegionMapLoadException;
import gov.va.med.siteservice.SiteMapLoadException;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class SiteServiceRestProxy
{
	private final static Logger logger = Logger.getLogger(SiteServiceRestProxy.class);
	
	public static List<Site> getSites(String siteServiceUrl)
	throws SiteMapLoadException
	{
		logger.info("Executing site service v2 request to load sites from '" + siteServiceUrl + "'.");
		try
		{
			SiteServiceVisnsType visns = getVisns(siteServiceUrl);
			return SiteServiceRestTranslator.translateToSites(visns);
		} 
		catch (MethodException mX)
		{
			logger.error(mX);
			throw new SiteMapLoadException(mX.getMessage(), true);
		} 
		catch (ConnectionException cX)
		{
			logger.error(cX);
			throw new SiteMapLoadException(cX.getMessage(), true);
		}
	}
	
	public static List<Region> getRegions(String siteServiceUrl)
	throws RegionMapLoadException
	{
		logger.info("Executing site service v2 request to load regions from '" + siteServiceUrl + "'.");
		try
		{
			
			SiteServiceVisnsType visns = getVisns(siteServiceUrl);
			return SiteServiceRestTranslator.translateToRegions(visns);
		} 
		catch (MethodException mX)
		{
			logger.error(mX);
			throw new RegionMapLoadException(mX.getMessage(), true);
		} 
		catch (ConnectionException cX)
		{
			logger.error(cX);
			throw new RegionMapLoadException(cX.getMessage(), true);
		}
	}
	
	private static SiteServiceVisnsType getVisns(String siteServiceUrl)
	throws MethodException, ConnectionException
	{
		SiteServiceRestGetClient getClient = new SiteServiceRestGetClient(siteServiceUrl, MediaType.APPLICATION_XML_TYPE);
		SiteServiceVisnsType visns = getClient.executeRequest(SiteServiceVisnsType.class);
		logger.info("Recieved successful response from '" + siteServiceUrl + "', translating result.");
		return visns;
	}

}
