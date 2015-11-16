/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Sep 30, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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

package gov.va.med.siteservice;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;

/**
 * A value object that contains the results of artifact source resolution.
 * The result may be either "resolved" or "indirected".
 * If it is resolved then the resolvedArtifactSource field will be non-null.
 * If it is indirected then the routingToken field is not null.
 * An indirected result must be submitted to the site resolver recursively until
 * it is resolved.
 * 
 * @author vhaiswbeckec
 *
 */
public class ResolutionResult
{
	private final ResolvedArtifactSource resolvedArtifactSource;
	private final RoutingToken routingToken;
	
	/**
	 * @param resolvedArtifactSource
	 * @param routingToken
	 */
	public ResolutionResult(ResolvedArtifactSource resolvedArtifactSource)
	{
		super();
		this.resolvedArtifactSource = resolvedArtifactSource;
		this.routingToken = null;
	}
	
	public ResolutionResult(RoutingToken routingToken)
	{
		super();
		this.resolvedArtifactSource = null;
		this.routingToken = routingToken;
	}
	
	public boolean isResolved(){return getResolvedArtifactSource() != null;}
	public boolean isIndirected(){return getRoutingToken() != null;}
	
	/**
	 * @return the resolvedArtifactSource
	 */
	public ResolvedArtifactSource getResolvedArtifactSource()
	{
		return this.resolvedArtifactSource;
	}
	/**
	 * @return the routingToken
	 */
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}
}
