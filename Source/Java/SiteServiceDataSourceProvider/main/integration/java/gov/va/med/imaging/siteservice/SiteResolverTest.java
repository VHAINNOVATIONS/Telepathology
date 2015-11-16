package gov.va.med.imaging.siteservice;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.siteservice.SiteResolver;
import java.net.URL;
import java.util.Iterator;
import junit.framework.TestCase;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class SiteResolverTest extends TestCase
{
	//private String siteServiceLocation = "http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx";
	private SiteResolutionDataSourceSpi resolver;
	private DataSourceProvider provider;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ProtocolHandlerUtility.initialize(true);
		
		provider = new Provider();
		
		resolver = provider.createSiteResolutionDataSource();
		
		assertNotNull(resolver);						// we must get an instance to do the tests
		assertTrue( resolver instanceof SiteResolver);	// make sure we're testing the class we think we're testing
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * @throws ConnectionException 
	 * @throws MethodException 
	 * 
	 */
	public void testResolveSites() 
	throws MethodException, ConnectionException
	{
		ResolvedSite resolvedSite;
		
		resolvedSite = resolver.resolveSite("660");
		assertNotNull(resolvedSite);
		assertEquals(false, resolvedSite.isAlienSite());
		assertEquals(true, resolvedSite.isLocalSite());
		
		resolvedSite = resolver.resolveSite("200");
		assertNotNull(resolvedSite);
		assertEquals(true, resolvedSite.isAlienSite());
		assertEquals(false, resolvedSite.isLocalSite());
	}
	
	/**
	 * 
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public void testResolveSitesProtocols() 
	throws MethodException, ConnectionException
	{
		ResolvedSite resolvedSite;
		
		resolvedSite = resolver.resolveSite("660", new String[]{"vftp", "vista", "exchange"});
		assertNotNull(resolvedSite);
		assertEquals(false, resolvedSite.isAlienSite());
		assertEquals(false, resolvedSite.isLocalSite());

		assertEquals(3, resolvedSite.getArtifactUrls().size() );
		
		URL metadataUrl = null;
		URL artifactUrl = null;
		Iterator<URL> metadataIter = resolvedSite.getMetadataUrls().iterator();
		Iterator<URL> artifactIter = resolvedSite.getArtifactUrls().iterator();
		
		metadataUrl = metadataIter.next();
		artifactUrl = artifactIter.next();
		assertNotNull(metadataUrl);
		assertEquals("vftp", metadataUrl.getProtocol() );
		assertNotNull(artifactUrl);
		assertEquals("vftp", artifactUrl );

		metadataUrl = metadataIter.next();
		artifactUrl = artifactIter.next();
		assertNotNull(metadataUrl);
		assertEquals("vista", metadataUrl.getProtocol() );
		assertNotNull(artifactUrl);
		assertEquals("vista", artifactUrl );
		
		metadataUrl = metadataIter.next();
		artifactUrl = artifactIter.next();
		assertNotNull(metadataUrl);
		assertEquals("exchange", metadataUrl.getProtocol() );
		assertNotNull(artifactUrl);
		assertEquals("exchange", artifactUrl );
	}
}
