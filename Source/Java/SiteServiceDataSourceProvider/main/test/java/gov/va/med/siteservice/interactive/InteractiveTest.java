/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 22, 2010
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

package gov.va.med.siteservice.interactive;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;
import gov.va.med.interactive.CommandController;
import gov.va.med.interactive.CommandFactory;
import gov.va.med.interactive.CommandLineCommandSource;
import gov.va.med.interactive.CommandSource;
import gov.va.med.siteservice.ArtifactSourceLookupResult;
import gov.va.med.siteservice.SiteResolutionProvider;
import gov.va.med.siteservice.SiteResolutionProviderConfiguration;
import gov.va.med.siteservice.SiteServiceConfiguration;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * @author vhaiswbeckec
 *
 */
public class InteractiveTest
extends TestCase
{
	private OutputStream commandSink;
	private CommandController<SiteResolutionProviderConfiguration> commandController;
	private SiteResolutionProvider provider;
	private SiteResolutionProviderConfiguration config;
	
	/**
	 * 
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		Logger.getRootLogger().setLevel(Level.ALL);
		ProtocolHandlerUtility.initialize(true);
		
		//saveExistingConfigurationFile();
		
		provider = new SiteResolutionProvider();
		//provider.getConfiguration();
		config = provider.getInstanceConfiguration();
		
		try
		{
			PipedInputStream inStream = new java.io.PipedInputStream();
			commandSink = new PipedOutputStream(inStream);

			CommandFactory<SiteResolutionProviderConfiguration> factory = 
				new SiteServiceConfigurationCommandFactory(provider);
			CommandSource<SiteResolutionProviderConfiguration> commandSource = 
				new CommandLineCommandSource<SiteResolutionProviderConfiguration>(null, inStream);
			commandController = 
				new CommandController<SiteResolutionProviderConfiguration>(
					config, 
					factory,
					commandSource,
					true,
					true,
					false
				);
			commandController.run();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	public void testCommands() 
	throws IOException, InterruptedException, RoutingTokenFormatException, URISyntaxException
	{
		assertNull( config.getProtocolConfiguration("xyz") );
		
		commandSink.write("list\n".getBytes());
		commandSink.write("protocol <xyz> <yadayada> </appPath> </imagePath>\n".getBytes());		
		commandSink.write("repository <1.2.3.4.5.6.7.8.9.10> <655321> <ftp://127.0.0.1/metadata,http://127.0.0.1/moremetadata> <ftp://127.0.0.1/image,http://127.0.0.1/moreimage>\n".getBytes());
		commandSink.write("siteService <http://localhost/siteservice> <sitecache> <regioncache> <23> <42> <9999>\n".getBytes());
		
		commandSink.write("exit\n".getBytes());
		commandSink.close();
		commandController.waitForProcessorComplete();
		
		assertNotNull( config.getProtocolConfiguration("xyz") );
		assertEquals( "yadayada", config.getProtocolConfiguration("xyz").getApplication() );
		assertEquals( "/appPath", config.getProtocolConfiguration("xyz").getMetadataPath() );
		assertEquals( "/imagePath", config.getProtocolConfiguration("xyz").getImagePath() );
		
		RoutingTokenImpl artifactSourceKey = (RoutingTokenImpl) RoutingTokenImpl.create("1.2.3.4.5.6.7.8.9.10", "655321");
		ArtifactSourceLookupResult externalArtifactSourceLookup = config.getExternalArtifactSources().getIncluding(artifactSourceKey);
		assertNotNull(externalArtifactSourceLookup);
		assertTrue( externalArtifactSourceLookup.isArtifactSource() );
		ArtifactSourceImpl externalArtifactSource = externalArtifactSourceLookup.getArtifactSource();
		
		boolean metadataSourceFound = false;
		for(URL metadataUrl : externalArtifactSource.getMetadataUrls() )
			if("ftp://127.0.0.1/metadata".equals(metadataUrl.toString()))
				metadataSourceFound = true;
		assertTrue(metadataSourceFound);
		boolean artifactSourceFound = false;
		for(URL artifactUrl : externalArtifactSource.getArtifactUrls() )
			if("ftp://127.0.0.1/image".equals(artifactUrl.toString()))
				artifactSourceFound = true;
		assertTrue(artifactSourceFound);

		
		SiteServiceConfiguration siteServiceConfig = config.getSiteServiceConfiguration();
		assertEquals( new URI("http://localhost/siteservice"), siteServiceConfig.getSiteServiceUri() );
		assertEquals( "sitecache", siteServiceConfig.getSiteServiceCacheFileName() );
		assertEquals( "regioncache", siteServiceConfig.getRegionListCacheFileName() );
		assertEquals( 42, siteServiceConfig.getRefreshMinimumDelay() );
		assertEquals( 9999L, siteServiceConfig.getRefreshPeriod() );
		assertEquals( 23, siteServiceConfig.getRefreshHour() );
	}
	
	@Override
	protected void tearDown() 
	throws Exception
	{
		//restoreExistingConfigurationFile();
		super.tearDown();
	}

	private File tempFile; 
	private void saveExistingConfigurationFile() 
	throws IOException
	{
		File originalFile = SiteResolutionProvider.getProviderConfiguration().getConfigurationFile();
		File tempFile = new File( SiteResolutionProvider.getProviderConfiguration().getConfigurationDirectory() + "configTest.xml" );
		
		originalFile.renameTo(tempFile);
	}
	
	private void restoreExistingConfigurationFile() 
	throws IOException
	{
		File testFile = SiteResolutionProvider.getProviderConfiguration().getConfigurationFile();
		testFile.delete();
		tempFile.renameTo(testFile);
	}
}
