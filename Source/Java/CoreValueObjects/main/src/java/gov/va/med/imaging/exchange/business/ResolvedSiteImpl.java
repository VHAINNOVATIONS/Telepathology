/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jan 15, 2008
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSourceImpl;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A value object to contain a Site and the associated URLs to contact that Site.
 * The URLS are the URL of the complete path to the needed endpoint.  Proxies using
 * these URLs may not use all of the path information but they should use
 * the host and port information. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class ResolvedSiteImpl 
extends ResolvedArtifactSourceImpl
implements ResolvedSite, Serializable
{
	private static final long serialVersionUID = 1L;

	// ===============================================================================
	// Factory Methods and related helpers 
	// ===============================================================================
	
	/**
	 * A convenience constructor to create an instance with a
	 * Site and any number of associated URL instances in a SortedSet.
	 * 
	 * @param site
	 * @param urls
	 */
	public static ResolvedSiteImpl create(
		Site site, 
		boolean localSite, 
		boolean alienSite, 
		boolean enabled, 
		List<URL> metadataUrls, 
		List<URL> artifactUrls)
    {
		return new ResolvedSiteImpl(site, localSite, alienSite, enabled, metadataUrls, artifactUrls);
    }
	
	private static ResolvedSiteImpl localResolvedTestSite = null;
	public static synchronized ResolvedSiteImpl createLocalResolvedTestSite() 
	throws MalformedURLException
	{
		if(localResolvedTestSite == null)
		{
			SiteImpl localTestSite = SiteImpl.createLocalTestSite();
			List<URL> metadataUrls = new ArrayList<URL>();
			List<URL> artifactUrls = new ArrayList<URL>();
			
			metadataUrls.add(new URL("vista://127.0.0.1:9003"));
			metadataUrls.add(new URL("vftp://127.0.0.1:8080/"));
			
			artifactUrls.add(new URL("vista://127.0.0.1:9003"));
			artifactUrls.add(new URL("vftp://127.0.0.1:8080/"));
			
			localResolvedTestSite = ResolvedSiteImpl.create(localTestSite, true, true, true,
				metadataUrls, 
				artifactUrls
			);
		}
				
		return localResolvedTestSite;
	}
	
	// ===============================================================================
	// Instance Members
	// ===============================================================================
	private boolean localSite;
	private boolean alienSite;
	private boolean enabled;
	
	private static transient Logger logger = Logger.getLogger(ResolvedSiteImpl.class);
	
	/**
	 * Construct a ResolvedSite with a Site and a SortedSet 
	 * of associated URL instances.
	 *  
	 * @param site
	 * @param artifactUrls 
	 * @param metadataUrls 
	 * @param urls
	 */
	private ResolvedSiteImpl(
			Site site, 
			boolean localSite, 
			boolean alienSite,
			boolean enabled, 
			List<URL> metadataUrls, 
			List<URL> artifactUrls)
    {
	    super(site, metadataUrls, artifactUrls);
	    this.localSite = localSite;
	    this.alienSite = alienSite;
	    this.enabled = enabled;
    }

	/**
     * @see gov.va.med.imaging.exchange.business.ResolvedSite#getSite()
     */
	@Override
	public Site getSite()
    {
    	return (Site)getArtifactSource();
    }

	@Override
    public boolean isLocalSite()
    {
	    return localSite;
    }

	@Override
	public boolean isAlienSite()
    {
    	return alienSite;
    }

	@Override
	public boolean isAlien()
	{
		return isAlienSite();
	}

	@Override
	public boolean isLocal()
	{
		return isLocalSite();
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( super.toString() );
		sb.append( ' ' );
		sb.append( isLocal() ? "LOCAL SITE" : "REMOTE SITE");
		sb.append( ' ' );
		sb.append( isAlien() ? "ALIEN SITE" : "NATIVE SITE");
		sb.append( ' ' );
		sb.append( isEnabled() ? "SITE ENABLED" : "SITE DISABLED");
		
		return sb.toString();
	}
}
