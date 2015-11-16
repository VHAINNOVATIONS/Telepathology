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
package gov.va.med.siteservice;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.WellKnownOID;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.ImagingMBean;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConfigurationError;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ProtocolConfigurationError;
import gov.va.med.imaging.datasource.AbstractLocalDataSource;
import gov.va.med.imaging.datasource.ProviderConfiguration;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.ResolvedSiteImpl;
import gov.va.med.imaging.exchange.business.Site;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * The SiteResolutionDataSource service implementation.
 * This class is predominantly a wrapper around the SiteService
 * proxy with additional functionality to build URLs based on
 * the Site information. 
 *
 */
public class SiteResolver 
extends AbstractLocalDataSource
implements SiteResolutionDataSourceSpi
{
	private static Logger logger = Logger.getLogger(SiteResolver.class);
	
	/**
	 * 
	 * @param siteServiceUrl
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static SiteResolver create(SiteResolutionProviderConfiguration configuration) 
	throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return new SiteResolver(configuration);
	}
	
	/**
	 * From a Site instance, create set of URLS to contact that site
	 * in the preferred order of contact.
	 * 
	 * @param site
	 * @return
	 * @throws ProtocolConfigurationError
	 */
	private static SourceURLs buildSiteUrlSet(
		SiteProtocolPreferenceFactory protocolPreferenceFactory, 
		Map<String, ProtocolServerConfiguration> protocolConfiguration, 
		Site site) 
	{
		assert protocolPreferenceFactory != null : "protocolPreferenceFactory is null";
		assert protocolConfiguration != null : "protocolConfiguration is null";
		assert site != null : "site is null";
		
		List<URL> metadataUrls = new ArrayList<URL>();
		List<URL> artifactUrls = new ArrayList<URL>();
		
		if(site == null)
			return new SourceURLs(metadataUrls, artifactUrls);
		
		// if we get errors building the URLs then save them up and, perhaps, throw an error at the end
		Map<String, MalformedURLException> urlExceptions = new HashMap<String, MalformedURLException>();
		String[] protocolPreferences = protocolPreferenceFactory.getPreferredProtocols(site.getSiteNumber());
		
		// for each of the protocols for the site, in preferred order, attempt to create a URL
		for(String protocol : protocolPreferences)
		{
			// Find the configured protocol from the site protocol then 
			// get the site's constituent server configuration for the protocol
			ProtocolServerConfiguration psc = protocolConfiguration.get(protocol);
			if(psc != null)
			{
				// find the server and port available at the site for the specified protocol
				// this was previously a simple differentiation between vista and any
				// other protocol
				URL metadataUrl = site.getAvailableMetadataServer(protocol);
				URL artifactUrl = site.getAvailableArtifactServer(protocol);
				logger.debug("Site '" + site.getSiteNumber() + "', protocol '" + protocol + "' => " + 
					(artifactUrl == null ? "null" : artifactUrl.toString())
				);
				if(artifactUrl != null)
				{
					String artifactServer = artifactUrl.getHost();
					int artifactPort = artifactUrl.getPort();
					String metadataServer = metadataUrl.getHost();
					int metadataPort = metadataUrl.getPort();
					String artifactFile = psc.getAbsoluteImagePath();
					String metadataFile = psc.getAbsoluteMetadataPath();
					
					try
					{
						metadataUrls.add( new URL( protocol, metadataServer, metadataPort, metadataFile ) );
						artifactUrls.add( new URL( protocol, artifactServer, artifactPort, artifactFile ) );
		            } 
					catch (MalformedURLException e)
		            {
						String msg = "An error occurred creating a URL for site '" + site.getSiteName() + 
							"'.  A URLStreamHandler for the protocol '" + protocol + 
							"' may not be available.  Please check that the required stream handlers are deployed correctly.";
						logger.error(msg);
						// create a new MalformedURLException with our message
						urlExceptions.put(protocol, new MalformedURLException(msg));
		            }
				}
			}
			else
			{
				// the protocol is not configured,
				// either the site is configured incorrectly or the protocol 
				// is not installed
				String msg = 
					"The protocol '" + protocol + 
					"', specified by artifact source '" + site.getIdentifier() +
					"' is not a configured protocol in the site configuration.";
				
				logger.error(msg);
				urlExceptions.put( protocol, new MalformedURLException(msg) );
			}
		}

		// if any MalformedURLExceptions occurred throw an Error
		if(urlExceptions != null && urlExceptions.size() > 0)
			throw new ProtocolConfigurationError(urlExceptions);
		
		return new SourceURLs(metadataUrls, artifactUrls);
	}
	
	// =======================================================================================
	//
	// =======================================================================================
	private SiteResolutionProviderConfiguration configuration;
	private SiteService siteService;
	private ExternalArtifactSourceResolver externalArtifactSourceResolver;
	
	// lists the protocols by preference order
	private SiteProtocolPreferenceFactory protocolPreferenceFactory;
	
	// maps the application to the metadata and image paths
	private Map<String, ProtocolServerConfiguration> protocolConfiguration;
	
	public SiteResolver() 
	{
		this((SiteResolutionProviderConfiguration)null);
	}
	
	/**
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 * 
	 */
	public SiteResolver(SiteResolutionProviderConfiguration memento) 
	{
		super();
		
		if(memento != null)
		{
			logger.info("Using provided configuration of type '" + memento.getClass().getSimpleName() + "'." );
			assert memento.getExternalArtifactSources() != null : "Memento external artifact source is null and must not be.";
			assert memento.getProtocolConfiguration() != null : "Memento protocol configuration is null and must not be.";
			assert memento.getSiteProtocolPreferenceFactory() != null : "Memento site protocol preference factory is null and must not be.";
			assert memento.getSiteServiceConfiguration() != null : "Memento site service configuration is null and must not be.";
			this.configuration = memento;
		}
		else
		{
			ProviderConfiguration<SiteResolutionProviderConfiguration> providerConfiguration = 
				SiteResolutionProvider.getProviderConfiguration();
			assert providerConfiguration != null;
			try
			{
				logger.info("Loading configuration from '" + providerConfiguration.getConfigurationFileName() + "'." );
				this.configuration = providerConfiguration.loadConfiguration();
				assert this.configuration != null : "Configuration is null after loading and no exception thrown.";
				logger.info("Configuration of type '" + this.configuration.getClass().getSimpleName() + "'." );
			}
			catch (IOException x)
			{
		        throw new ConfigurationError(
	        		this.getClass().getName(), 
	        		"Exception occurred loading configuration memento.", 
	        		x);
			}
		}

		createSubSystems();
	}

	/**
	 * Create the child components of the SiteResolver from the configuration.
	 * 
	 * @throws ConfigurationError
	 */
	private void createSubSystems() 
	throws ConfigurationError
	{
		assert this.configuration != null : "Configuration should logically be available here regardless of how it was loaded.";
		
		assert this.configuration.getSiteServiceConfiguration() != null;
		try
        {
	        siteService = new SiteService(this.configuration.getSiteServiceConfiguration());
        } 
		catch (Exception e)
        {
			String siteServiceLocation = "<unknown>";
			try
			{
				siteServiceLocation = this.configuration.getSiteServiceConfiguration().getSiteServiceUri().toString();
			}
			catch (NullPointerException x){}
	        e.printStackTrace();
	        throw new ConfigurationError(
        		this.getClass().getName(), 
        		"Exception occurred instantiating SiteService, please check:\n" +
        		"1.) site service URL '" + siteServiceLocation + "' is correct.", 
        		e);
        }
		
		assert this.configuration.getProtocolConfiguration() != null;
		protocolConfiguration = this.configuration.getProtocolConfiguration();

		assert this.configuration.getSiteProtocolPreferenceFactory() != null;
		protocolPreferenceFactory = this.configuration.getSiteProtocolPreferenceFactory();
		
		assert this.configuration.getExternalArtifactSources() != null;
		ExternalArtifactSources externalArtifactSources = this.configuration.getExternalArtifactSources();
		externalArtifactSourceResolver = new ExternalArtifactSourceResolverImpl(
			externalArtifactSources,
			protocolPreferenceFactory );
		registerMBeanServer(siteService);
	}
	
	private static ObjectName siteServiceManagerMBeanName = null;
	
	private static synchronized void registerMBeanServer(SiteService siteService)
	{
		if(siteServiceManagerMBeanName == null)
		{
			logger.info("Registering Site Service With JMX");
			try
			{				
	            // add statistics
				SiteServiceStatistics siteServiceMbean = new SiteServiceStatistics(siteService);
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
				mBeanProperties.put( "type", "SiteService" );
				//mBeanProperties.put( "name", "Manager-" + Integer.toHexString(bufferManager.hashCode()) );
				mBeanProperties.put( "name", "Configuration");
				siteServiceManagerMBeanName = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
				mBeanServer.registerMBean(siteServiceMbean, siteServiceManagerMBeanName);	            
			}
			catch(Exception ex)
			{
				logger.error("Error registering Site Service with JMX", ex);
			}
		}
	}

	/**
	 * 
	 * @param externalRoutingMemento
	 * @return
	 */
	private ExternalArtifactSources createExternalArtifactSourceMapping(RoutingToArtifactSourceMapMemento externalRoutingMemento)
	{
		assert externalRoutingMemento != null;
		
		ExternalArtifactSources eas = null;
		String externalArtifactSourceMappingClassName = externalRoutingMemento.getRoutingToArtifactSourceMapClassName();
		logger.info("External artifact source mapping class type '" + externalArtifactSourceMappingClassName + "'.");
		
		if(externalArtifactSourceMappingClassName == null)
		{
			externalArtifactSourceMappingClassName = StaticExternalArtifactSources.class.getName();
			logger.info("External artifact source mapping class type not specified, defaulting to '" + externalArtifactSourceMappingClassName + "'.");
		}
		
		try
		{
			Class<?> externalArtifactSourceMappingClass = Class.forName(externalArtifactSourceMappingClassName);
			Constructor<?> ctor = externalArtifactSourceMappingClass.getConstructor(RoutingToArtifactSourceMapMemento.class);
			
			eas = (ExternalArtifactSources)ctor.newInstance(externalRoutingMemento);
			
			externalArtifactSourceResolver = new ExternalArtifactSourceResolverImpl(eas, protocolPreferenceFactory);
		}
		catch (Exception x)
		{
			logger.error("Unable to instantiate external artifact source mapping class '" + externalArtifactSourceMappingClassName + "'.", x);
		}
		
		return eas;
	}

	/**
	 * If a wildcard routing exists for the given home community ID then that is a gateway,
	 * return true.  otherwise return false.
	 * 
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSource#isRepositoryGatewayExtant(gov.va.med.OID)
	 */
	@Override
	public boolean isRepositoryGatewayExtant(OID homeCommunityId) 
	throws MethodException, ConnectionException
	{
		try
		{
			RoutingToken wildcardRoutingToken = RoutingTokenImpl.create(homeCommunityId, RoutingToken.ROUTING_WILDCARD);
			try
			{
				return resolveArtifactSource(wildcardRoutingToken) != null;
			}
			catch (Exception x)
			{
				return false;
			}
		}
		catch (RoutingTokenFormatException x)
		{
			throw new MethodException(x);
		}
	}

	/**
	 * 
	 * @param artifactSource
	 * @return
	 */
	@Override
	public ResolvedArtifactSource resolveArtifactSource(RoutingToken routingToken)
	throws UnsupportedOperationException, MethodException, ConnectionException
	{
		// look in the external artifact sources first, if the artifact source can
		// be found there then return that.
		ResolutionResult externalResolutionResult = externalArtifactSourceResolver.resolve(routingToken);

		// if the external artifact source is an indirection through a routing token
		// then recursively call ourselves to resolve the new routing token.
		if(externalResolutionResult != null && externalResolutionResult.isIndirected())
		{
			logger.info(routingToken.toRoutingTokenString() + "=> indirected to =>" + externalResolutionResult.getRoutingToken().toRoutingTokenString());
			return resolveArtifactSource(externalResolutionResult.getRoutingToken());
		}
		else
		{
			ResolvedArtifactSource resolvedArtifactSource = 
				externalResolutionResult == null ? null : externalResolutionResult.getResolvedArtifactSource();

			if(resolvedArtifactSource != null)
				logger.info(routingToken.toString() + "=> resolved as external artifact source to =>" + resolvedArtifactSource.toString());
			
			// if the external artifact source did not list the artifact source and it is a
			// VA artifact source, use the repository ID as a site number and attempt to
			// resolve it using site service.
			if( resolvedArtifactSource == null &&
				WellKnownOID.VA_DOCUMENT.isApplicable(routingToken.getHomeCommunityId()) ||
				WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(routingToken.getHomeCommunityId()) )
			{
				resolvedArtifactSource = resolveSite(routingToken.getRepositoryUniqueId());
				logger.debug(routingToken.toString() + "=> resolved as site to =>" + (resolvedArtifactSource == null ? "null" : resolvedArtifactSource.toString()) );
			}
			
			return resolvedArtifactSource;
		}
	}
	
	/**
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveSite(java.lang.String)
	 */
	@Override
	public ResolvedSite resolveSite(String siteNumber) 
	throws UnsupportedOperationException
	{
		Site site = siteService.getSiteByNumber(siteNumber);

		return site == null ? null : resolveSite(site);
	}
	
	@Override
	public ResolvedSite resolveSite(Site site) 
	throws UnsupportedOperationException
	{
		if(site == null)
			return null;
		
		if(protocolPreferenceFactory.isSiteEnabled(site.getSiteNumber()))
		{		
			SourceURLs siteUrls = SiteResolver.buildSiteUrlSet(
				protocolPreferenceFactory, 
				protocolConfiguration, 
				site); 
	
		    return ResolvedSiteImpl.create( 
		    	site, 
		    	protocolPreferenceFactory.isSiteLocal(site.getSiteNumber()), 
		    	protocolPreferenceFactory.isSiteAlien(site.getSiteNumber()), 
		    	protocolPreferenceFactory.isSiteEnabled(site.getSiteNumber()),
		    	siteUrls.getMetadataUrls(), siteUrls.getArtifactUrls() 
		    );
		}
		else
		{
			logger.info("Site '" + site.getSiteNumber() + "' is disabled, cannot resolve site");
			return ResolvedSiteImpl.create(
					site, 
					protocolPreferenceFactory.isSiteLocal(site.getSiteNumber()), 
			    	protocolPreferenceFactory.isSiteAlien(site.getSiteNumber()),
			    	protocolPreferenceFactory.isSiteEnabled(site.getSiteNumber()),
			    	new ArrayList<URL>(), new ArrayList<URL>());
		}
	}

	/**
	 * This version of resolveSite() is really only for testing because it
	 * lets direct requests explicitly, which is something we should not
	 * do in production.
	 * 
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi#resolveSite(java.lang.String)
	 */
	@Override
	public ResolvedSite resolveSite(String siteNumber, String[] protocolOverride) 
	throws UnsupportedOperationException
	{
		return resolveSite(siteService.getSiteByNumber(siteNumber), protocolOverride);
	}

	/**
	 * 
	 */
	@Override
	public ResolvedSite resolveSite(Site site, String[] protocolOverride) 
	throws UnsupportedOperationException
	{
		if(site == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		for(String protocol : protocolOverride)
			sb.append((sb.length() > 0 ? "," : "") + protocol);
		logger.warn("Overriding the preferred protocols with :" + sb.toString());
		
		SiteProtocolPreferenceFactory ppf = 
			new ConstantProtocolPreferenceFactory(protocolOverride);
		
		SourceURLs sourceUrls = SiteResolver.buildSiteUrlSet(ppf, protocolConfiguration, site);
		// ResolvedSite will try to build the URLs of the host based on the Site
		// information.
	    return ResolvedSiteImpl.create(
	    	site,
	    	ppf.isSiteLocal(site.getSiteNumber()),
	    	ppf.isSiteAlien(site.getSiteNumber()),
	    	ppf.isSiteEnabled(site.getSiteNumber()),
	    	sourceUrls.getMetadataUrls(),
	    	sourceUrls.getArtifactUrls()
	    );
	}
	
	/**
	 * 
	 * @return
	 */
    public Iterator<ResolvedSite> iterator()
    {
	    return new Iterator<ResolvedSite>()
	    {
	    	private Iterator<Site> siteIterator = siteService.iterator();

			@Override
            public boolean hasNext()
            {
	            return siteIterator.hasNext();
            }

			@Override
            public ResolvedSite next()
            {
	            return resolveSite(siteIterator.next().getSiteNumber());
            }

			@Override
            public void remove()
            {
				throw new UnsupportedOperationException("remove() is not supported by the SiteService iterator.");
            }
	    };
    }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSource#getAllRegions()
	 */
	@Override
	public List<Region> getAllRegions() 
	throws MethodException, ConnectionException 
	{
		return siteService.getAllRegions();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.SiteResolutionDataSource#resolveRegion(java.lang.String)
	 */
	@Override
	public Region resolveRegion(String regionId) 
	throws MethodException, ConnectionException 
	{
		return siteService.getRegionByNumber(regionId);
	}
	
	/**
	 * This gets all of the artifact sources,
	 * it does not include the indirected routing tokens.
	 */
	@Override
	public List<ArtifactSource> getAllArtifactSources() 
	throws MethodException, ConnectionException
	{
		List<ArtifactSource> artifactSources = new ArrayList<ArtifactSource>();
		
		for(Site site : this.siteService)
			artifactSources.add(site);

		for(Iterator<ResolutionResult> iter = this.externalArtifactSourceResolver.iterator(); iter.hasNext(); )
		{
			ResolutionResult resolutionResult = iter.next();
			if(resolutionResult.isResolved())
				artifactSources.add(resolutionResult.getResolvedArtifactSource().getArtifactSource());
		}
		return artifactSources;
	}

	@Override
	public List<ResolvedArtifactSource> getAllResolvedArtifactSources() 
	throws MethodException, ConnectionException
	{
		List<ResolvedArtifactSource> resolvedArtifactSources = new ArrayList<ResolvedArtifactSource>();
		
		for(Site site : this.siteService)
			resolvedArtifactSources.add( resolveSite(site) );

		for(Iterator<ResolutionResult> iter = this.externalArtifactSourceResolver.iterator(); iter.hasNext(); )
		{
			ResolutionResult resolutionResult = iter.next();
			if(resolutionResult.isResolved())
				resolvedArtifactSources.add(resolutionResult.getResolvedArtifactSource());
		}		
		return resolvedArtifactSources;
	}
	
	@Override
	public void refreshSiteResolutionData() 
	throws MethodException, ConnectionException
	{
		siteService.refreshCache();
	}

	@Override
	public Site getSite(String siteId) 
	throws MethodException, ConnectionException
	{
		return siteService.getSiteByNumber(siteId);
	}

	/**
	 * A simple little value object to pass the collections of metadata
	 * and artifact URLs together.
	 */
	static class SourceURLs
	{
		private final List<URL> metadataUrls;
		private final List<URL> artifactUrls;
		
		SourceURLs(List<URL> metadataUrls, List<URL> artifactUrls)
		{
			super();
			this.metadataUrls = metadataUrls;
			this.artifactUrls = artifactUrls;
		}

		public List<URL> getMetadataUrls(){return this.metadataUrls;}
		public List<URL> getArtifactUrls(){return this.artifactUrls;}
	}
}
