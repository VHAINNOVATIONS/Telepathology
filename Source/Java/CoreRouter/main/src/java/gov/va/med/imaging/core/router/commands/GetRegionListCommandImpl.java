/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.Region;
import java.util.List;

/**
 * @author vhaiswbeckec
 *
 */
public class GetRegionListCommandImpl 
extends AbstractCommandImpl<List<Region>> 
{
	private static final long serialVersionUID = -1641953235756357554L;

	/**
	 * @param commandContext
	 */
	public GetRegionListCommandImpl()
	{
		super();
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public List<Region> callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException
	{
		List<Region> regions = null;
        try
        {
        	regions = getCommandContext().getSiteResolver().getAllRegions();
        } 
        catch (MethodException e)
        {
        	getLogger().error("Configured site resolution service failed to resolve all regions.", e);
        	throw new MethodException("Exception in all region resolution", e);
        } 
        catch (ConnectionException e)
        {
        	getLogger().error("Configured site resolution service is unable to contact data source.", e);
        	throw new MethodConnectionException(e);
        }
		return regions;
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		return null;
	}

}
