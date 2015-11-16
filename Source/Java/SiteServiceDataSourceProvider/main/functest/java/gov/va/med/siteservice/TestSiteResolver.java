/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Oct 4, 2010
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

import gov.va.med.OID;
import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.siteservice.siteprotocol.ProtocolPreference;
import java.net.URI;
import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestSiteResolver
extends TestCase
{
	private SiteResolutionProviderConfiguration config;
	private SiteResolver siteResolver;
	
	/**
	 * Set up a SiteResolver with a known configuration to test against.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		
		ProtocolHandlerUtility.initialize(false);
		
		config = new SiteResolutionProviderConfiguration();
		
		config.getSiteServiceConfiguration().setRefreshHour(23);
		config.getSiteServiceConfiguration().setRefreshMinimumDelay(1);
		config.getSiteServiceConfiguration().setRefreshPeriod(24l*60l*60l*1000l);		
		config.getSiteServiceConfiguration().setSiteServiceUri(new URI("http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx"));
		
		config.addProtocolConfiguration( "vftp", new ProtocolServerConfiguration(false, "Federation", "/metadata", "/image") );
		config.addProtocolConfiguration( "vista", new ProtocolServerConfiguration(true, null, null, null) );
		config.addProtocolConfiguration( "vistaimaging", new ProtocolServerConfiguration(true, null, null, null) );
		config.addProtocolConfiguration( "xca", new ProtocolServerConfiguration(false, "RespondingGateway", "/query", "/retrieve") );
		config.addProtocolConfiguration( "exchange", new ProtocolServerConfiguration(false, "Exchange", "/query", "/retrieve") );

		config.getExternalArtifactSources().add(
			RoutingTokenImpl.create( "1", "2" ), 
			new ArtifactSourceImpl( OID.create("1"), "2", 
				new URL[]{new URL("exchange://11.11.11.11")},
				new URL[]{new URL("exchange://22.22.22.22")}
			)
		);
		
		config.getExternalArtifactSources().add(
			RoutingTokenImpl.create( "3", "4" ), 
			new ArtifactSourceImpl( OID.create("3"), "4", 
				new URL[]{new URL("vftp://33.33.33.33"), new URL("vistaimaging://44.44.44.44")},
				new URL[]{new URL("vftp://55.55.55.55"), new URL("vistaimaging://66.66.66.66")}
			)
		);
		
		config.getExternalArtifactSources().add(
			RoutingTokenImpl.create( "5", "*" ), 
			new ArtifactSourceImpl( OID.create("5"), "6", 
				new URL[]{new URL("xca://77.77.77.77")},
				new URL[]{new URL("xca://88.88.88.88")}
			)
		);
		
		config.getSiteProtocolPreferenceFactory().add(
			ProtocolPreference.createForVADocumentSite( "660", true, false, true, new String[]{"vistaimaging"} )
		);
		config.getSiteProtocolPreferenceFactory().add(
			ProtocolPreference.createForVARadiologySite("660", true, false, true, new String[]{"vistaimaging"} )
		);

		config.getSiteProtocolPreferenceFactory().add(
			ProtocolPreference.createForVADocumentSite( "200", true, false, true, new String[]{"vista"} )
		);
		config.getSiteProtocolPreferenceFactory().add(
			ProtocolPreference.createForVARadiologySite("200", true, false, true, new String[]{"vista"} )
		);
		
		config.getSiteProtocolPreferenceFactory().add(
			ProtocolPreference.createForVADocumentDefault(new String[]{"vftp", "vistaimaging"} )
		);
		config.getSiteProtocolPreferenceFactory().add(
			ProtocolPreference.createForVARadiologyDefault(new String[]{"vftp", "vistaimaging"} )
		);
		
		// indirection to a repository
		config.getExternalArtifactSources().addIndirection(
			(RoutingTokenImpl)( RoutingTokenImpl.create( "10", "1" ) ), 
			(RoutingTokenImpl)( RoutingTokenImpl.create( "3", "4" ) ) 
		);
		
		// indirection to a gateway
		config.getExternalArtifactSources().addIndirection(
			(RoutingTokenImpl)( RoutingTokenImpl.create( "11", "*" ) ), 
			(RoutingTokenImpl)( RoutingTokenImpl.create( "5", "1" ) ) 
		);
		
		siteResolver = new SiteResolver(config);
	}

	/**
	 * Test method for {@link gov.va.med.siteservice.SiteResolver#resolveArtifactSource(gov.va.med.RoutingToken)}.
	 * @throws RoutingTokenFormatException 
	 */
	@Test
	public void testResolveArtifactSource() 
	throws Exception
	{
		RoutingToken routingToken = null;
		ResolvedArtifactSource resolvedArtifactSource = null;
		URL url = null;
		
		routingToken = RoutingTokenImpl.create("1", "2");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 1, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("exchange");
		assertNotNull(url);
		assertEquals("11.11.11.11", url.getHost());
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("vftp");
		assertNull(url);
		
		routingToken = RoutingTokenImpl.create("3", "4");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 2, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 2, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("vftp");
		assertNotNull(url);
		assertEquals("33.33.33.33", url.getHost());
		url = resolvedArtifactSource.getMetadataUrl("vistaimaging");
		assertNotNull(url);
		url = resolvedArtifactSource.getArtifactUrl("vftp");
		assertNotNull(url);
		assertEquals("55.55.55.55", url.getHost());
		url = resolvedArtifactSource.getArtifactUrl("vistaimaging");
		assertNotNull(url);
		assertEquals("66.66.66.66", url.getHost());

		// unknown artifact source 
		routingToken = RoutingTokenImpl.create("3", "5");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNull(resolvedArtifactSource);
		
		routingToken = RoutingTokenImpl.create("5", "6");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 1, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("xca");
		assertNotNull(url);
		assertEquals("77.77.77.77", url.getHost());
		url = resolvedArtifactSource.getMetadataUrl("vistaimaging");
		assertNull(url);
		url = resolvedArtifactSource.getArtifactUrl("xca");
		assertNotNull(url);
		assertEquals("88.88.88.88", url.getHost());
		url = resolvedArtifactSource.getArtifactUrl("vistaimaging");
		assertNull(url);

		// test wildcard routing
		routingToken = RoutingTokenImpl.create("5", "7");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 1, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("xca");
		assertNotNull(url);
		assertEquals("77.77.77.77", url.getHost());
		url = resolvedArtifactSource.getMetadataUrl("vistaimaging");
		assertNull(url);
		url = resolvedArtifactSource.getArtifactUrl("xca");
		assertNotNull(url);
		assertEquals("88.88.88.88", url.getHost());
		url = resolvedArtifactSource.getArtifactUrl("vistaimaging");
		assertNull(url);
		
	}

	/**
	 * Test method for {@link gov.va.med.siteservice.SiteResolver#resolveSite(java.lang.String)}.
	 * @throws FormatException 
	 */
	@Ignore
	@Test
	public void testResolveLocalSite() 
	throws Exception
	{
		RoutingToken routingToken = null;
		ResolvedArtifactSource resolvedArtifactSource = null;
		URL url = null;
		
		// 660 is the local site, should have 1 preferred protocol
		routingToken = RoutingTokenImpl.createVADocumentSite("660");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 1, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("vistaimaging");
		assertNotNull(url);
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getArtifactUrl("vistaimaging");
		assertNotNull(url);
		assertFalse( resolvedArtifactSource.isAlien() );
		assertTrue( resolvedArtifactSource.isLocal() );
	}
	
	@Ignore
	@Test
	public void testResolveDoDSite() 
	throws Exception
	{
		RoutingToken routingToken = null;
		ResolvedArtifactSource resolvedArtifactSource = null;
		URL url = null;
		
	
		// 200 is the DoD site, should have 1 preferred protocol
		routingToken = RoutingTokenImpl.createVADocumentSite("200");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 1, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("vista");
		assertNotNull(url);
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getArtifactUrl("vista");
		assertNotNull(url);
		//assertTrue( resolvedArtifactSource.isAlien() );
		//assertFalse( resolvedArtifactSource.isLocal() );
	}
	
	@Ignore
	@Test
	public void testResolveRemoteSite() 
	throws Exception
	{
		RoutingToken routingToken = null;
		ResolvedArtifactSource resolvedArtifactSource = null;
		URL url = null;
		
		// 661 is a remote site, should have 2 preferred protocol
		routingToken = RoutingTokenImpl.createVADocumentSite("661");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 2, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 2, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("vftp");
		assertNotNull(url);
		url = resolvedArtifactSource.getMetadataUrl("vistaimaging");
		assertNotNull(url);
		url = resolvedArtifactSource.getArtifactUrl("vftp");
		assertNotNull(url);
		url = resolvedArtifactSource.getArtifactUrl("vistaimaging");
		assertNotNull(url);
		assertFalse( resolvedArtifactSource.isAlien() );
		assertFalse( resolvedArtifactSource.isLocal() );
	}

	@Test
	public void testIndirection() 
	throws Exception
	{
		RoutingToken routingToken = null;
		ResolvedArtifactSource resolvedArtifactSource = null;
		URL url = null;

		// 10:1 should redirect to 3:4
		routingToken = RoutingTokenImpl.create("10", "1");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 2, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 2, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("vftp");
		assertNotNull(url);
		assertEquals("33.33.33.33", url.getHost());
		url = resolvedArtifactSource.getMetadataUrl("vistaimaging");
		assertNotNull(url);
		url = resolvedArtifactSource.getArtifactUrl("vftp");
		assertNotNull(url);
		assertEquals("55.55.55.55", url.getHost());
		url = resolvedArtifactSource.getArtifactUrl("vistaimaging");
		assertNotNull(url);
		assertEquals("66.66.66.66", url.getHost());

		// 11:x should redirect to 5:x
		routingToken = RoutingTokenImpl.create("11", "6");
		resolvedArtifactSource = siteResolver.resolveArtifactSource(routingToken);
		assertNotNull(resolvedArtifactSource);
		assertNotNull(resolvedArtifactSource.getMetadataUrls());
		assertNotNull(resolvedArtifactSource.getArtifactUrls());
		assertEquals( 1, resolvedArtifactSource.getMetadataUrls().size() );
		assertEquals( 1, resolvedArtifactSource.getArtifactUrls().size() );
		url = resolvedArtifactSource.getMetadataUrl("xca");
		assertNotNull(url);
		assertEquals("77.77.77.77", url.getHost());
		url = resolvedArtifactSource.getMetadataUrl("vistaimaging");
		assertNull(url);
		url = resolvedArtifactSource.getArtifactUrl("xca");
		assertNotNull(url);
		assertEquals("88.88.88.88", url.getHost());
		url = resolvedArtifactSource.getArtifactUrl("vistaimaging");
		assertNull(url);
		
	}
}
