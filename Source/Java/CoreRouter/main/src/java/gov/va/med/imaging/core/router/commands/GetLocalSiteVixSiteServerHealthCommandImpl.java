/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 28, 2013
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
package gov.va.med.imaging.core.router.commands;

import java.util.Calendar;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.health.VixServerHealth;
import gov.va.med.imaging.health.VixServerHealthProperties;
import gov.va.med.imaging.health.VixServerHealthSource;
import gov.va.med.imaging.health.VixSiteServerHealth;

/**
 * Retrieve the health from the local site, do not include a site parameter as input
 * 
 * @author VHAISWWERFEJ
 *
 */
public class GetLocalSiteVixSiteServerHealthCommandImpl
extends AbstractCommandImpl<VixSiteServerHealth>
{
	private static final long serialVersionUID = -3522318146568943401L;
	
	private final VixServerHealthSource [] vixServerHealthSources;

	public GetLocalSiteVixSiteServerHealthCommandImpl(
			VixServerHealthSource[] vixServerHealthSources)
	{
		
		this.vixServerHealthSources = vixServerHealthSources;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public VixSiteServerHealth callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		getLogger().info("Determining VIX Server health");
		VixServerHealth vixServerHealth = VixServerHealth.getVixServerHealth(getVixServerHealthSources());
		vixServerHealth.addVixServerHealthProperty(VixServerHealthProperties.VIX_SERVER_HEALTH_VIX_VERSION, 
				getCommandContext().getRouter().getAppConfiguration().getVixSoftwareVersion());
		getLogger().info("VIX Server health determined, returning '" + (vixServerHealth == null ? "null" : vixServerHealth.getVixServerHealthProperties().size()) + "' properties.");
		return new VixSiteServerHealth(getCommandContext().getLocalSite().getSite(), vixServerHealth, Calendar.getInstance());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{		
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		return "";
	}

	public VixServerHealthSource[] getVixServerHealthSources()
	{
		return vixServerHealthSources;
	}

}
