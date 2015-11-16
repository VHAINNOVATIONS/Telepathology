/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
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
public class GetResolvedArtifactSourceListCommandImpl
extends AbstractCommandImpl<List<ResolvedArtifactSource>>
{
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public List<ResolvedArtifactSource> callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		List<ResolvedArtifactSource> resolvedArtifactSources = null;
        try
        {
        	resolvedArtifactSources = getCommandContext().getSiteResolver().getAllResolvedArtifactSources();
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
		return resolvedArtifactSources;
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

}
