package gov.va.med.siteservice;

import gov.va.med.ProtocolHandlerUtility;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

public class TestSiteService 
extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ProtocolHandlerUtility.initialize(true);
	}

	public void testInitialization() 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, MalformedURLException, URISyntaxException
	{
		URL siteServiceUrl = new URL("http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx");
		File configurationDirectory = new File("C:\\vixconfig");
		SiteServiceConfiguration config = SiteServiceConfiguration.createDefault(configurationDirectory);
		config.setSiteServiceUri(siteServiceUrl.toURI());
		
		SiteService siteService = new SiteService(config);
		
		assertTrue( siteService.isInitialLoadComplete() );
	}
}
