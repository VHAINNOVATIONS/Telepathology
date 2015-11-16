package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.LocalizedSite;
import gov.va.med.imaging.exchange.InterfaceURLs;

import java.util.SortedSet;

/**
 * A ResolvedSite is a Site that has been resolved to a set of URLs, in the
 * preferred order of contact.
 * The Router will use the URLs protocol to determine the service implementation
 * to use to contact the site.  Where multiple URLs are specified (i.e. multiple
 * protocols are available) the Router will try each in order until it succeeds or
 * runs out of options.
 * 
 * Resolving a site number to a set of URLs is the responsibility of a
 * SiteResolutionDataSource, each of which may implement their own
 * ResolvedSite class.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface ResolvedSite 
extends LocalizedSite, ResolvedArtifactSource
{	
	public abstract Site getSite();
}