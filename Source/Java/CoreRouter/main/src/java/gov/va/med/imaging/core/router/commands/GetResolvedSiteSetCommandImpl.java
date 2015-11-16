/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.ResolvedSite;

import java.util.Iterator;
import java.util.Set;

/**
 * A command that gets an iterator over all of the resolved sites known to this
 * VIX.
 * This command should not be invoked asynchronously as the site resolver
 * caches data.
 * 
 * @author vhaiswbeckec
 *
 */
public class GetResolvedSiteSetCommandImpl 
extends AbstractCommandImpl<Set<ResolvedSite>>
{
	private static final long serialVersionUID = -3817923767048899150L;

	/**
	 * @param commandContext
	 */
	public GetResolvedSiteSetCommandImpl()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Set<ResolvedSite> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		try
        {
			Set<ResolvedSite> resolvedSiteSet = new java.util.HashSet<ResolvedSite>();
			for(Iterator<ResolvedSite> iter = getCommandContext().getSiteResolver().iterator(); iter.hasNext(); )
				resolvedSiteSet.add(iter.next());
			
	        return resolvedSiteSet;
        } 
		catch (ConnectionException cX)
        {
			throw new MethodConnectionException(cX);
        }
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		return null;
	}

	/**
	 * Even though all instances of this command will return the same result,
	 * return false for equals because the Iterator returned is not re-usable. 
	 * 
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

}
