/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import java.util.ArrayList;
import java.util.List;

/**
 * A command that finds the ArtifactSource(s) that a RoutingToken
 * will be directed to.
 * 
 * @author vhaiswbeckec
 *
 */
public class GetResolvedArtifactSourceCommandImpl
extends AbstractCommandImpl<List<ResolvedArtifactSource>>
{
	private static final long serialVersionUID = 1L;

	private final RoutingToken routingToken;
	
	public GetResolvedArtifactSourceCommandImpl(RoutingToken routingToken)
	{
		this.routingToken = routingToken;
	}
	
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public List<ResolvedArtifactSource> callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		List<ResolvedArtifactSource> artifactSources = new ArrayList<ResolvedArtifactSource>(1);
        try
        {
        	ResolvedArtifactSource resolvedArtifactSource = 
        		getCommandContext().getSiteResolver().resolveArtifactSource(getRoutingToken());
        	if(resolvedArtifactSource != null)
        		artifactSources.add(resolvedArtifactSource);
        } 
        catch (MethodException e)
        {
        	getLogger().error("Configured site resolution service failed to resolve routing token '" + routingToken + "'.", e);
        	throw new MethodException("Exception in all region resolution", e);
        } 
        catch (ConnectionException e)
        {
        	getLogger().error("Configured site resolution service is unable to contact data source.", e);
        	throw new MethodConnectionException(e);
        }
        
		return artifactSources;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(this.getClass().isInstance(obj))
			return getRoutingToken().equals( ((GetResolvedArtifactSourceCommandImpl)obj).routingToken );
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

}
