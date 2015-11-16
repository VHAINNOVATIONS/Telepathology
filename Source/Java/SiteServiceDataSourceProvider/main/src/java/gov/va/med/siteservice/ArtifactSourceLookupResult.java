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

import java.io.Serializable;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceLookupResult
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final ArtifactSourceImpl artifactSource;
	private final RoutingTokenImpl routingToken;

	/**
	 * @param artifactSource
	 * @param routingToken
	 */
	public ArtifactSourceLookupResult(ArtifactSourceImpl artifactSource)
	{
		super();
		this.artifactSource = artifactSource;
		this.routingToken = null;
	}

	public ArtifactSourceLookupResult(RoutingTokenImpl routingToken)
	{
		super();
		this.artifactSource = null;
		this.routingToken = routingToken;
	}
	
	public boolean isIndirected(){return getRoutingToken() != null;}
	public boolean isArtifactSource(){return getArtifactSource() != null;}
	
	/**
	 * @return the artifactSource
	 */
	public ArtifactSourceImpl getArtifactSource()
	{
		return this.artifactSource;
	}
	/**
	 * @return the routingToken
	 */
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return isIndirected() ?
			("Indirection=>" + getRoutingToken().toString()) :
			("TargetSource:" + getArtifactSource().toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.artifactSource == null) ? 0 : this.artifactSource.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArtifactSourceLookupResult other = (ArtifactSourceLookupResult) obj;
		if (this.artifactSource == null)
		{
			if (other.artifactSource != null)
				return false;
		}
		else if (!this.artifactSource.equals(other.artifactSource))
			return false;
		if (this.routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		}
		else if (!this.routingToken.equals(other.routingToken))
			return false;
		return true;
	}
}
