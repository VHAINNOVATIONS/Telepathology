/**
 * 
 */
package gov.va.med.vista.siteservice.soap;

import gov.va.med.imaging.core.FacadeRouterUtility;

/**
 * @author vhaiswbeckec
 *
 */
public class SiteServiceSOAPContext
{
	private static SiteServiceSOAPFacadeRouter router = null;
	public static SiteServiceSOAPFacadeRouter getSiteServiceFacadeRouter()
	{
		try
		{
			router = FacadeRouterUtility.getFacadeRouter(SiteServiceSOAPFacadeRouter.class);
		} 
		catch (Exception x)
		{
			String msg = "Error getting SiteServiceFacadeRouter instance.  Application deployment is probably incorrect.";			 
		}
		return router;
	}	
	

}
