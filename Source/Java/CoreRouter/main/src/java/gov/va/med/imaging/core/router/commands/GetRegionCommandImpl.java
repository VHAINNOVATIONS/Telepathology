/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.Region;

/**
 * @author vhaiswbeckec
 *
 */
public class GetRegionCommandImpl 
extends AbstractCommandImpl<Region> 
{
	private static final long serialVersionUID = 5279615772387888601L;
	private final String regionId;

	/**
	 * @param commandContext
	 */
	public GetRegionCommandImpl(String regionId)
	{
		super();
		
		this.regionId = regionId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Region callSynchronouslyInTransactionContext()
			throws MethodException, ConnectionException
	{
		Region region = null;
        try
        {
        	region = getCommandContext().getSiteResolver().resolveRegion(getRegionId());
        } 
        catch (MethodException e)
        {
        	getLogger().error("Configured site resolution service failed to resolve region.", e);
        	throw new MethodException("Exception in region resolution", e);
        } 
        catch (ConnectionException e)
        {
        	getLogger().error("Configured site resolution service is unable to contact data source.", e);
        	throw new MethodConnectionException(e);
        }
		return region;
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
		return getRegionId();
	}

	public String getRegionId()
	{
		return regionId;
	}

}
