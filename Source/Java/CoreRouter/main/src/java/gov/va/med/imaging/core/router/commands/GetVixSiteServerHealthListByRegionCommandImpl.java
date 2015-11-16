/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 2, 2011
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

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.health.VixServerHealthHelper;
import gov.va.med.imaging.health.VixServerHealthSource;
import gov.va.med.imaging.health.VixSiteServerHealth;

/**
 * @author vhaiswwerfej
 *
 */
public class GetVixSiteServerHealthListByRegionCommandImpl
extends AbstractCommandImpl<List<VixSiteServerHealth>>
{
	private static final long serialVersionUID = -2015578687873547888L;
	
	private final Boolean forceRefresh;
	private final String regionNumber;
	private final VixServerHealthSource [] vixServerHealthSources;
	
	public GetVixSiteServerHealthListByRegionCommandImpl(String regionNumber, 
			Boolean forceRefresh, VixServerHealthSource [] vixServerHealthSources)
	{
		this.forceRefresh = forceRefresh;
		this.regionNumber = regionNumber;
		this.vixServerHealthSources = vixServerHealthSources;
	}

	public Boolean getForceRefresh()
	{
		return forceRefresh;
	}

	public String getRegionNumber()
	{
		return regionNumber;
	}

	@Override
	public List<VixSiteServerHealth> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		getLogger().info("Retrieving VIX Server health for region '" + getRegionNumber() + "', forceRefresh=" + getForceRefresh());
		
		List<Site> vixSites = getVIXSitesInRegion();
		List<VixSiteServerHealth> serverHealth = new ArrayList<VixSiteServerHealth>();
		VixServerHealthHelper helper = VixServerHealthHelper.getVixServerHealthHelper();
		serverHealth = helper.getSitesServerHealth(vixSites, getForceRefresh(), getVixServerHealthSources());
		
		return serverHealth;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof GetVixSiteServerHealthListByRegionCommandImpl)
		{
			GetVixSiteServerHealthListByRegionCommandImpl that = (GetVixSiteServerHealthListByRegionCommandImpl)obj;
			return (this.getRegionNumber() == that.getRegionNumber()) && (this.getForceRefresh() == that.getForceRefresh());
		}
		return false;
	}
	
	private List<Site> getVIXSitesInRegion()
	throws MethodException, MethodConnectionException
	{
		Region region = null;
		try
		{
			region = getCommandContext().getSiteResolver().resolveRegion(getRegionNumber());
		}
		catch (ConnectionException e)
        {
        	getLogger().error("Configured site resolution service is unable to contact data source.", e);
        	throw new MethodConnectionException(e);
        }
        List<Site> vixSites = new ArrayList<Site>();
        VixServerHealthHelper helper = VixServerHealthHelper.getVixServerHealthHelper();
        List<String> excludedSites = helper.getExcludedSiteNumbers();
        for(Site site : region.getSites())
    	{
    		if(!ExchangeUtil.isSiteDOD(site))
    		{
    			if(site.hasAcceleratorServer())
				{
    				boolean excludeSite = false;
    				for(String siteNumber : excludedSites)
    				{
    					if(siteNumber.equals(site.getSiteNumber()))
    					{
    						excludeSite = true;
    					}
    				}
    				if(!excludeSite)
    					vixSites.add(site);
				}
    		}
    	}
        // add to the list of sites the local Vix Servers        
        //vixSites.addAll(helper.getEnabledLocalVixSites()); // don't get these for the region        
		return vixSites;
	}

	@Override
	protected String parameterToString()
	{
		return getRegionNumber() + ", " + getForceRefresh().toString();
	}

	public VixServerHealthSource[] getVixServerHealthSources()
	{
		return vixServerHealthSources;
	}
}
