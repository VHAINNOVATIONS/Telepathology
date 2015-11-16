package gov.va.med.vista.siteservice.soap;

import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.Site;

import java.util.List;

/**
 * 
 * @author vhaiswbeckec
 *
 */
@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface SiteServiceSOAPFacadeRouter
extends FacadeRouter
{
	/**
	 * Checks to see if the ViX can communicate with the specified site
	 * @param siteNumber The site number to communicate with
	 * @return The status of the site
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetRegionListCommand")
	public abstract List<Region> getRegionList()
	throws MethodException, ConnectionException;

	/**
	 * Checks to see if the ViX can communicate with the specified site
	 * @param siteNumber The site number to communicate with
	 * @return The status of the site
	 */
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetRegionCommand")
	public abstract Region getRegion(String regionId)
	throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, commandClassName="GetSiteCommand")
	public abstract Site getSite(String siteId)
	throws MethodException, ConnectionException;

}
