/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 20, 2010
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.health.VixServerHealthHelper;
import gov.va.med.imaging.health.VixServerHealthSource;
import gov.va.med.imaging.health.VixSiteServerHealth;

/**
 * Retrieve the health of a specified VIX server
 * 
 * @author vhaiswwerfej
 *
 */
public class GetVixSiteServerHealthBySiteNumberCommandImpl 
extends AbstractCommandImpl<VixSiteServerHealth> 
{
	private static final long serialVersionUID = 773339534533962323L;
	
	private final RoutingToken routingToken;
	private final Boolean forceRefresh;
	private final VixServerHealthSource [] vixServerHealthSources;
	
	public GetVixSiteServerHealthBySiteNumberCommandImpl(RoutingToken routingToken, 
			Boolean forceRefresh, VixServerHealthSource [] vixServerHealthSources)
	{
		this.routingToken = routingToken;
		this.forceRefresh = forceRefresh;
		this.vixServerHealthSources = vixServerHealthSources;
	}

	public RoutingToken getRoutingToken() {
		return routingToken;
	}

	/**
	 * @return the forceRefresh
	 */
	public Boolean getForceRefresh() 
	{
		return forceRefresh;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public VixSiteServerHealth callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		getLogger().info("Retrieving VIX Server health for site '" + getRoutingToken().getRepositoryUniqueId() + "', forceRefresh=" + getForceRefresh());
		VixServerHealthHelper helper = VixServerHealthHelper.getVixServerHealthHelper();
		ResolvedSite site = getCommandContext().getSiteResolver().resolveSite(getRoutingToken().getRepositoryUniqueId());
		if(site.getSite().hasAcceleratorServer())
		{
			return helper.getSiteServerHealth(site.getSite(), forceRefresh, 
					getVixServerHealthSources());
		}
		else
		{
			String msg = "Site '" + getRoutingToken().getRepositoryUniqueId() + "' does not have a VIX, cannot get VIX health from this site.";
			getLogger().error(msg);
			throw new MethodException(msg);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		return getRoutingToken().toString();
	}

	public VixServerHealthSource[] getVixServerHealthSources()
	{
		return vixServerHealthSources;
	}

}
