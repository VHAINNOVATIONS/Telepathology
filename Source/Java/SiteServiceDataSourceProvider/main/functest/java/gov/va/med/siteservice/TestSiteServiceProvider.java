package gov.va.med.siteservice;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;

import junit.framework.TestCase;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class TestSiteServiceProvider 
extends TestCase
{
	private DataSourceProvider provider;
	
	public URL getUrl() 
	throws MalformedURLException
	{
		return new URL("http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx");
	}
	
	private SiteResolutionDataSourceSpi siteResolutionDataSource;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		Layout layout = new SimpleLayout();
		Appender appender = new WriterAppender(layout, System.out);
		Logger.getRootLogger().addAppender(appender);
		ProtocolHandlerUtility.initialize(true);
		
		provider = new Provider();
		
		siteResolutionDataSource = provider.createSiteResolutionDataSource();
	}

	protected SiteResolutionDataSourceSpi getSiteResolutionDataSource()
	{
		return siteResolutionDataSource;
	}

	/**
	 * Test method for {@link gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveSite(java.lang.String)}.
	 */
	public void testResolveSite()
	{
		ResolvedSite resolvedSite = resolveSite("200", true, false);
		assertNotNull( resolvedSite.getArtifactUrl("exchange") );
		assertNotNull( resolvedSite.getArtifactUrl("xca") );
		
		resolvedSite = resolveSite("660", true, true);
		assertNotNull( resolvedSite.getArtifactUrl("vistaimaging") );
	}

	/**
     * @param resolvedSite
     */
    private ResolvedSite resolveSite(String siteNumber, boolean expected, boolean requireAccelerator)
    {
		ResolvedSite resolvedSite = null;
    	
	    try
        {
			resolvedSite = getSiteResolutionDataSource().resolveSite(siteNumber);
			
			if(expected)
			{
				assertNotNull(resolvedSite);
				assertNotNull(resolvedSite.getSite());
				
				if(requireAccelerator)
					assertNotNull(resolvedSite.getSite().getAcceleratorServer());
			}
			else
				assertNull(resolvedSite);
        } 
        catch (UnsupportedOperationException e)
        {
	        e.printStackTrace();
	        fail(e.getMessage());
        } 
        catch (MethodException e)
        {
	        e.printStackTrace();
	        fail(e.getMessage());
        } 
        catch (ConnectionException e)
        {
	        e.printStackTrace();
	        fail(e.getMessage());
        }
        
        return resolvedSite;
    }
	
    /**
     * 
     * @throws MethodException
     * @throws ConnectionException
     * @throws RoutingTokenFormatException
     */
    public void testArtifactSources() 
    throws MethodException, ConnectionException, RoutingTokenFormatException
    {
    	List<ArtifactSource> artifactSources = getSiteResolutionDataSource().getAllArtifactSources();
    	assertNotNull(artifactSources);
    	
    	List<ResolvedArtifactSource> resolvedArtifactSources = getSiteResolutionDataSource().getAllResolvedArtifactSources();
    	assertNotNull(resolvedArtifactSources);
    	
    	RoutingToken routingToken = RoutingTokenImpl.createVARadiologySite("660");
    	ResolvedArtifactSource ras = getSiteResolutionDataSource().resolveArtifactSource(routingToken);
    	assertNotNull(ras);
    	assertNotNull( ras.getArtifactUrl("vistaimaging") );
    	
    	routingToken = RoutingTokenImpl.createVARadiologySite("200");
    	ras = getSiteResolutionDataSource().resolveArtifactSource(routingToken);
    	assertNotNull(ras);
    	assertNotNull( ras.getArtifactUrl("exchange") );
    	
    	routingToken = RoutingTokenImpl.createDoDDocumentSite("200");
    	ras = getSiteResolutionDataSource().resolveArtifactSource(routingToken);
    	assertNotNull(ras);
    	assertNotNull( ras.getArtifactUrl("xca") );
    }
}
