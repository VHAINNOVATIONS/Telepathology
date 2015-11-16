/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 30, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.ResolvedSite;

/**
 * A Command implementation for logging image access events.
 * The result of successful processing is a void, represented by the
 * "Object" class used as the generic type.  The result will always
 * be a null. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class GetResolvedSiteCommandImpl 
extends AbstractCommandImpl<ResolvedSite>
{
	private static final long serialVersionUID = 1L;

	// NOTE: this siteID is not a RoutingToken, it is the subject
	// of the command
	private final String siteId;
	
	/**
	 * 
	 * @param command
	 * @param router
	 * @param asynchronousMethodProcessor
	 * @param resultQueue
	 */
	public GetResolvedSiteCommandImpl(String siteId)
    {
	    super();
	    this.siteId = siteId;
    }

	/**
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public ResolvedSite callSynchronouslyInTransactionContext()
	throws MethodException
	{
		getLogger().info("Command [" + this.getClass().getSimpleName() + "] - processing.");
		
		// this command is always called on the local site
		//ResolvedSite localSite = getCommandContext().getLocalSite();
		
		// this processor does not need to do any asynchronous tasks within itself,
		// the execution of this method will occur asynchronously to the call to the router
		try
		{
	        ResolvedArtifactSource resolvedSite = getCommandContext().getResolvedArtifactSource( 
				RoutingTokenImpl.createVARadiologySite(getSiteId()) );
			return resolvedSite instanceof ResolvedSite ? (ResolvedSite)resolvedSite : null;
		}
		catch (RoutingTokenFormatException x)
		{
			throw new MethodException(x);
		}
        
	}

	/**
	 * 
	 * @see gov.va.med.imaging.core.interfaces.router.commands.PostImageAccessEventCommand#getEvent()
	 */
	public String getSiteId()
	{
		return this.siteId;
	}

	/**
	 * This is a non-idempotent method, the .equals() must always return false.
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj)
    {
	    return false;
    }


	@Override
    protected String parameterToString()
    {
		StringBuffer sb = new StringBuffer();
		sb.append(getSiteId() == null ? "<null site id>" : getSiteId());

		return sb.toString();
    }
}
