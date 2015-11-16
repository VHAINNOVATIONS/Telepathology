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
package gov.va.med.vista.siteservice.webservices;

import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO;
import gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO;
import gov.va.med.vistaweb.WebServices.SiteService.RegionTO;
import gov.va.med.vistaweb.WebServices.SiteService.SiteTO;
import gov.va.med.vista.siteservice.soap.SiteServiceSOAPContext;
import gov.va.med.vista.siteservice.soap.SiteServiceSOAPFacadeRouter;
import gov.va.med.vista.siteservice.translator.SiteServiceTranslator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implementation of the Site Service server
 * 
 * @author VHAISWWERFEJ
 *
 */
public class SiteServiceWebservice 
implements gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap, ApplicationContextAware
{
	
	public static final String defaultCoreRouterBeanName = "coreRouter";

	private static ApplicationContext appContext;
	private static Logger logger = Logger.getLogger(SiteServiceWebservice.class);
	
	private Router vixCore = null;
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context)
	throws BeansException 
	{
		appContext = context;
	}
	
	/**
	 * Return the ViX core router
	 * @return
	 */
	private Router getVixCore()
	{
		if(vixCore == null)
		{
			Object routerObj = appContext.getBean(defaultCoreRouterBeanName);
			vixCore = (Router)routerObj;
		}
		return vixCore;	
	}

	/* (non-Javadoc)
	 * @see gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap#getSite(java.lang.String)
	 */
	@Override
	public SiteTO getSite(String siteID) throws RemoteException 
	{
		logger.debug("Retrieving Site [" + siteID + "]");
		try
		{
			return getSiteInternal(siteID);
		}
		catch(MethodException mX)
		{
			logger.error("Error retrieving site [" + siteID + "]", mX);
			throw new RemoteException(mX.getMessage());
		}
		catch(ConnectionException cX)
		{
			logger.error("Error retrieving site [" + siteID + "]", cX);
			throw new RemoteException(cX.getMessage());
		}
	}
	
	/**
	 * Internal function to retrieve a site 
	 * @param siteID
	 * @return
	 * @throws MethodException Occurs if the ViX core throws an exception getting the site
	 * @throws ConnectionException 
	 */
	private gov.va.med.vistaweb.WebServices.SiteService.SiteTO getSiteInternal(String siteID)
	throws MethodException, ConnectionException
	{
		SiteServiceSOAPFacadeRouter router = SiteServiceSOAPContext.getSiteServiceFacadeRouter();
		Site site = router.getSite(siteID);
		gov.va.med.vistaweb.WebServices.SiteService.SiteTO result = 
			SiteServiceTranslator.convertSite(site, siteID);
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap#getSites(java.lang.String)
	 */
	@Override
	public ArrayOfSiteTO getSites(String siteIDs) throws RemoteException 
	{
		logger.debug("Retrieving sites [" + siteIDs + "]");		
		List<gov.va.med.vistaweb.WebServices.SiteService.SiteTO> sitesTo = new 
			ArrayList<gov.va.med.vistaweb.WebServices.SiteService.SiteTO>();
		
		String[] siteNumbers = SiteServiceTranslator.convertDelimitedStringsIntoSiteNumbers(siteIDs);
		for(String siteNumber : siteNumbers)
		{
			try
			{
				sitesTo.add(getSiteInternal(siteNumber));
			}
			catch(MethodException mX)
			{
				logger.error("Error retrieving site [" + siteNumber + "] from vix core", mX);
				//throw new RemoteException(mX.getMessage());
			}
			catch(ConnectionException cX)
			{
				logger.error("Error retrieving site [" + siteNumber + "] from vix core", cX);
				//throw new RemoteException(mX.getMessage());
			}
		}
		gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO result = 
			new gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO(
				sitesTo.toArray(new gov.va.med.vistaweb.WebServices.SiteService.SiteTO[sitesTo.size()]));			
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap#getVHA()
	 */
	@Override
	public ArrayOfRegionTO getVHA() throws RemoteException 
	{
		logger.debug("Retrieving all VA sites");
		try 
		{
			SiteServiceSOAPFacadeRouter router = SiteServiceSOAPContext.getSiteServiceFacadeRouter();
			List<Region> regions = router.getRegionList();
			gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO result = 
				SiteServiceTranslator.convertRegions(regions);
			return result;					
		}
		catch(MethodException mX)
		{
			logger.error("Error retrieving all sites ", mX);
			throw new RemoteException(mX.getMessage());
		}
		catch(ConnectionException cX)
		{
			logger.error("Error retrieving all sites", cX);
			throw new RemoteException(cX.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap#getVISN(java.lang.String)
	 */
	@Override
	public RegionTO getVISN(String regionID) throws RemoteException 
	{
		
		logger.debug("Retrieving VISN [" + regionID + "]");
		gov.va.med.vistaweb.WebServices.SiteService.RegionTO region = null;
		try 
		{
			SiteServiceSOAPFacadeRouter router = SiteServiceSOAPContext.getSiteServiceFacadeRouter();
			Region vaRegion = router.getRegion(regionID);
			region = SiteServiceTranslator.convertRegion(vaRegion, regionID);			
		}
		catch(MethodException mX)
		{
			logger.error("Error retrieving region [" + regionID + "] from vix core", mX);
			throw new RemoteException(mX.getMessage());
		}		
		catch(ConnectionException cX)
		{
			logger.error("Error retrieving region [" + regionID + "] from vix core", cX);
			throw new RemoteException(cX.getMessage());
		}
		return region;
	}
}
