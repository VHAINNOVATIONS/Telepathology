/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 25, 2011
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
package gov.va.med.imaging.business;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ArtifactSourceMemento;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteConnection;

/**
 * @author vhaiswwerfej
 *
 */
public class TestSite 
implements Site
{

	private static final long serialVersionUID = 3947810517393134868L;
	
	private final String siteName;
	private final String siteNumber;
	private final String siteAbbr;	

	public TestSite(String siteName, String siteNumber, String siteAbbr)
	{
		super();
		this.siteName = siteName;
		this.siteNumber = siteNumber;
		this.siteAbbr = siteAbbr;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getAcceleratorPort()
	 */
	@Override
	public int getAcceleratorPort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getAcceleratorServer()
	 */
	@Override
	public String getAcceleratorServer()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getRegionId()
	 */
	@Override
	public String getRegionId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getSiteAbbr()
	 */
	@Override
	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getSiteName()
	 */
	@Override
	public String getSiteName()
	{
		return siteName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getSiteNumber()
	 */
	@Override
	public String getSiteNumber()
	{
		return siteNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getVistaPort()
	 */
	@Override
	public int getVistaPort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#getVistaServer()
	 */
	@Override
	public String getVistaServer()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#hasAcceleratorServer()
	 */
	@Override
	public boolean hasAcceleratorServer()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#artifactIterator()
	 */
	@Override
	public Iterator<URL> artifactIterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#createRoutingToken()
	 */
	@Override
	public RoutingToken createRoutingToken()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getArtifactServerCount()
	 */
	@Override
	public int getArtifactServerCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getArtifactUrls()
	 */
	@Override
	public List<URL> getArtifactUrls()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getAvailableArtifactServer(java.lang.String)
	 */
	@Override
	public URL getAvailableArtifactServer(String protocol)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getAvailableMetadataServer(java.lang.String)
	 */
	@Override
	public URL getAvailableMetadataServer(String protocol)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getHomeCommunityId()
	 */
	@Override
	public OID getHomeCommunityId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getIdentifier()
	 */
	@Override
	public String getIdentifier()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getMemento()
	 */
	@Override
	public ArtifactSourceMemento getMemento()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getMetadataServerCount()
	 */
	@Override
	public int getMetadataServerCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getMetadataUrls()
	 */
	@Override
	public List<URL> getMetadataUrls()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getName()
	 */
	@Override
	public String getName()
	{
		return getSiteName();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#getRepositoryId()
	 */
	@Override
	public String getRepositoryId()
	{
		return getSiteNumber();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#isDodDocument()
	 */
	@Override
	public boolean isDodDocument()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#isDodRadiology()
	 */
	@Override
	public boolean isDodRadiology()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#isRepresents(gov.va.med.OID, java.lang.String)
	 */
	@Override
	public boolean isRepresents(OID homeCommunityId, String repositoryId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#isVaDocument()
	 */
	@Override
	public boolean isVaDocument()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#isVaRadiology()
	 */
	@Override
	public boolean isVaRadiology()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.artifactsource.ArtifactSource#metadataIterator()
	 */
	@Override
	public Iterator<URL> metadataIterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#isSiteUserAuthenticatable()
	 */
	@Override
	public boolean isSiteUserAuthenticatable()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.Site#isSitePatientLookupable()
	 */
	@Override
	public boolean isSitePatientLookupable()
	{
		return true;
	}

	@Override
	public Map<String, SiteConnection> getSiteConnections() {
		// TODO Auto-generated method stub
		return null;
	}

}
