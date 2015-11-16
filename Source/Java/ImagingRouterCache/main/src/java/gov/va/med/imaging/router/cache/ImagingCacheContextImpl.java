/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 14, 2011
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
package gov.va.med.imaging.router.cache;

import gov.va.med.IdentityProxyInvocationHandler;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.TransactionLoggerDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.storage.cache.DODSourcedCache;
import gov.va.med.imaging.exchange.storage.cache.VASourcedCache;
import gov.va.med.server.ServerAdapterImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class ImagingCacheContextImpl 
implements ImagingCacheContext
{
	private final CommandContext commandContext;
	private final DODSourcedCache extraEnterpriseCache;
	private final VASourcedCache intraEnterpriseCacheCache;
	
	private final static Logger logger = Logger.getLogger(ImagingCacheContextImpl.class);
	private static ImagingCache imagingCache;
	
	/**
	 * The JNDI name of the core router
	 */
	private static final String IMAGING_CACHE = "java:comp/env/ImagingCache";
	private static final String IMAGING_CACHE_GLOBAL_CONTEXT = "ImagingCache";
	
	public ImagingCacheContextImpl(CommandContext commandContext)
	{
		this.commandContext = commandContext;
		
		ImagingCache imagingCache = getImagingCache();
		
		//ImagingCacheHolder cacheHolder = ImagingCacheHolder.getSingleton();
		this.extraEnterpriseCache = imagingCache.getDODSourcedCache();
		this.intraEnterpriseCacheCache = imagingCache.getVASourcedCache();
	}
	
	protected CommandContext getCommandContext() {
		return commandContext;
	}

	@Override
	public DODSourcedCache getExtraEnterpriseCache()
	{
		return this.extraEnterpriseCache;
	}

	@Override
	public VASourcedCache getIntraEnterpriseCacheCache()
	{
		return this.intraEnterpriseCacheCache;
	}

	@Override
	public Router getRouter() 
	{
		return commandContext.getRouter();
	}

	@Override
	public DataSourceProvider getProvider() 
	{
		return commandContext.getProvider();
	}

	@Override
	public SiteResolutionDataSourceSpi getSiteResolver() 
	{
		return commandContext.getSiteResolver();
	}

	@Override
	public CommandFactory getCommandFactory() 
	{
		return commandContext.getCommandFactory();
	}

	@Override
	public TransactionLoggerDataSourceSpi getTransactionLoggerService() 
	{
		return commandContext.getTransactionLoggerService();
	}

	@Override
	public boolean isCachingEnabled() 
	{
		return commandContext.isCachingEnabled();
	}

	@Override
	public ResolvedSite getLocalSite() 
	{
		return commandContext.getLocalSite();
	}

	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(
			RoutingToken routingToken) 
	throws MethodException 
	{
		return commandContext.getResolvedArtifactSource(routingToken);
	}

	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(
			RoutingToken routingToken,
			Class<? extends VersionableDataSourceSpi> spi, Method method,
			Object[] parameters) 
	throws MethodException 
	{
		return commandContext.getResolvedArtifactSource(routingToken, spi, method, parameters);
	}
	
	protected synchronized static ImagingCache getImagingCache()
	{
		if(imagingCache == null)
		{
			javax.naming.Context ctx;
			try
			{
				ctx = new javax.naming.InitialContext();
				logger.info("Getting reference from context'" + ctx.getNameInNamespace() + "' to imaging cache using name '" + IMAGING_CACHE + "'.");
				Object obj = ctx.lookup(IMAGING_CACHE);
				try
				{
					imagingCache = (ImagingCache)obj;
				}
				catch (ClassCastException x)
				{
					logger.warn("Error casting object of type '" + obj.getClass().getName() + 
							"', loaded by '" + obj.getClass().getClassLoader().getClass().getName() + 
							"' to type '" + Router.class.getName() + 
							"', loaded by '" + Router.class.getClassLoader().getClass().getName() + "'. \n" +
							"Creating proxied reference to implementation of ImagingCache.");
					
					IdentityProxyInvocationHandler<ImagingCache> classLoaderEndRun = 
						new IdentityProxyInvocationHandler<ImagingCache>(obj, ImagingCache.class);
					imagingCache = 
						(ImagingCache)Proxy.newProxyInstance(ImagingCacheContextImpl.class.getClassLoader(), new Class[]{ImagingCache.class}, classLoaderEndRun);
				}
			} 
			catch (NamingException x)
			{
				try
				{
					ctx = ServerAdapterImpl.getSingleton().getGlobalNamingServer().getGlobalContext();
					logger.info("Getting reference from the global context to router using name '" + IMAGING_CACHE + "'.");
					Object obj = ctx.lookup(IMAGING_CACHE_GLOBAL_CONTEXT);
					imagingCache = (ImagingCache)obj;
				} 
				catch (NamingException nX1)
				{
					nX1.printStackTrace();
				}
			}			
		}
		
		return imagingCache;
	}

}
