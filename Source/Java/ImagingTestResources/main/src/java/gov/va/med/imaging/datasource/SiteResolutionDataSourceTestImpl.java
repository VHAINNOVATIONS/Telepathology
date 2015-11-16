/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 1, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.datasource;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;

import java.util.Iterator;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public class SiteResolutionDataSourceTestImpl 
implements SiteResolutionDataSourceSpi
{
	private final List<Site> sites;
	
	public SiteResolutionDataSourceTestImpl(List<Site> sites)
	{
		this.sites = sites;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#getAllArtifactSources()
	 */
	@Override
	public List<ArtifactSource> getAllArtifactSources() 
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#getAllRegions()
	 */
	@Override
	public List<Region> getAllRegions() 
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#getAllResolvedArtifactSources()
	 */
	@Override
	public List<ResolvedArtifactSource> getAllResolvedArtifactSources()
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#getSite(java.lang.String)
	 */
	@Override
	public Site getSite(String siteId) 
	throws MethodException, ConnectionException
	{
		for(Site site : sites)
		{
			if(site.getSiteNumber().equals(siteId))
			{
				return site;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#isRepositoryGatewayExtant(gov.va.med.OID)
	 */
	@Override
	public boolean isRepositoryGatewayExtant(OID homeCommunityId)
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#iterator()
	 */
	@Override
	public Iterator<ResolvedSite> iterator() 
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#refreshSiteResolutionData()
	 */
	@Override
	public void refreshSiteResolutionData() 
	throws MethodException, ConnectionException
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveArtifactSource(gov.va.med.RoutingToken)
	 */
	@Override
	public ResolvedArtifactSource resolveArtifactSource(
			RoutingToken routingToken) 
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveRegion(java.lang.String)
	 */
	@Override
	public Region resolveRegion(String regionId) 
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveSite(java.lang.String)
	 */
	@Override
	public ResolvedSite resolveSite(String siteId) 
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveSite(java.lang.String, java.lang.String[])
	 */
	@Override
	public ResolvedSite resolveSite(String siteId, String[] preferredProtocol)
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveSite(gov.va.med.imaging.exchange.business.Site)
	 */
	@Override
	public ResolvedSite resolveSite(Site site) 
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveSite(gov.va.med.imaging.exchange.business.Site, java.lang.String[])
	 */
	@Override
	public ResolvedSite resolveSite(Site site, String[] preferredProtocol)
	throws MethodException, ConnectionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DataSourceSpi#setConfiguration(java.lang.Object)
	 */
	@Override
	public void setConfiguration(Object configuration)
	{
		
	}

}
