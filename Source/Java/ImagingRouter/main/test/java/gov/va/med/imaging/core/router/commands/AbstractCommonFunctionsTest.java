/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 1, 2011
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
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.business.TestSite;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.interfaces.router.CommandContextTestImpl;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceTestImpl;
import gov.va.med.imaging.exchange.business.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractCommonFunctionsTest
{
	
	protected List<Site> getTestSites()
	{
		List<Site> sites = new ArrayList<Site>();
		sites.add(getConsolidatedSite());
		sites.add(getLocalSite());
		
		return sites;
	}
	
	protected Site getConsolidatedSite()
	{		
		String consolidatedSiteNumber = "589AF";
		return new TestSite("ConsolidatedSite", consolidatedSiteNumber, "CON");
	}
	
	protected Site getLocalSite()
	{
		String siteNumber = "660";
		return new TestSite("LocalSite", siteNumber, "LOCAL");
	}

	protected CommandContext getCommandContext()
	{
		SiteResolutionDataSourceSpi siteResolution = 
			new SiteResolutionDataSourceTestImpl(getTestSites());
		
		
		CommandContext commandContext = new CommandContextTestImpl(siteResolution);
		return commandContext;
	}
}
