/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 21, 2008
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
package gov.va.vistaweb.siteservice.test;

import java.net.URL;

import gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO;
import gov.va.med.vistaweb.WebServices.SiteService.SiteServiceLocator;
import gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap;
import junit.framework.TestCase;

/**
 * @author VHAISWWERFEJ
 *
 */
public class TestSiteService 
extends TestCase 
{
	
	
	public void testGetSites()
	{
		try {
			SiteServiceSoap service = null;
			SiteServiceLocator locator = new SiteServiceLocator();
			URL url = new URL("http://localhost/VistaWebSvcs/SiteService.asmx");
			service = locator.getSiteServiceSoap(url);
			ArrayOfRegionTO regions =  service.getVHA();
			System.out.println("Got [" + regions.getRegionTO().length + "] regions");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			TestCase.fail(ex.getMessage());
		}
	}

}
