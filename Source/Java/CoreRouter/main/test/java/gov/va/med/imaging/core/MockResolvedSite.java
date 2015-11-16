package gov.va.med.imaging.core;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSourceImpl;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.SiteImpl;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class MockResolvedSite
extends ResolvedArtifactSourceImpl
implements ResolvedSite
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param artifactSource
	 * @param artifactUrls
	 * @param metadataUrls
	 * @throws MalformedURLException 
	 */
	public MockResolvedSite() 
	throws MalformedURLException
	{
		super(
			new SiteImpl("42", "660", "Washington DC", "WDC",
			new URL("vftp://127.0.0.1"),
			new URL("vista://127.0.0.1:9300")
		), 
		null, null);
		
		addMetadataUrl(new URL("vftp://127.0.0.1/Vix/metadata"));
		addMetadataUrl(new URL("vista://127.0.0.1:9300"));
		addArtifactUrl(new URL("vftp://127.0.0.1/Vix/artifact"));
		addArtifactUrl(new URL("vista://127.0.0.1:9300"));
	}

	@Override
	public Site getSite()
	{
		return (Site)getArtifactSource();
	}

	@Override
	public boolean isAlienSite(){return false;}

	@Override
	public boolean isLocalSite(){return false;}

	@Override
	public boolean isAlien(){return isAlienSite();}

	@Override
	public boolean isLocal(){return isLocalSite();}
}