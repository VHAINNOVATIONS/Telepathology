package gov.va.med.imaging.core;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.IAppConfiguration;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.RoutingOverrideSpi;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.NoValidServiceConstructorError;
import gov.va.med.imaging.exchange.configuration.AppConfiguration;
import gov.va.med.server.ServerAdapterImpl;
import gov.va.med.server.ServerLifecycleEvent;
import gov.va.med.server.ServerLifecycleListener;
import java.net.MalformedURLException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * The Router is the core object in the VIX system.
 * It takes requests from facades and selects a service 
 * to satisfy the request.
 * It also deals with reading/writing from the cache.
 * 
 * The Router is the entire external interface to the facade components. 
 * 
 * @author VHAISWBECKEC
 *
 */
public class RouterImpl
implements Router, ServerLifecycleListener
{
	private static final Logger logger = Logger.getLogger(Router.class);
	static 
	{
		ProtocolHandlerUtility.initialize(true);
	}
	
	private final AppConfiguration appConfiguration;
	private final SiteResolutionDataSourceSpi siteResolver;
	private final List<RoutingOverrideSpi> routingOverrideServices;
	//	private final IImageConversion imageConversion;
	private final DataSourceProvider provider;

	// asynchronous router commands are delegated to a helper class
	private final AsynchronousRouterImpl asynchRouter;
	//private final CommandFactoryImpl asynchCommandFactory;
	
	private boolean failoverOnMethodException = true;
	

	
	/**
	 * 
	 * @param appConfiguration
	 * @param dodCache
	 * @param vaCache
	 * @param imgConversion
	 * @param lifecycleAdapter
	 * @throws MalformedURLException
	 * @throws NoValidServiceConstructorError
	 * @throws ConnectionException
	 */
	public RouterImpl(
		AppConfiguration appConfiguration
    ) 
	throws MalformedURLException, ConnectionException
	{
		logger.info("RouterImpl() - " + this.hashCode());

		this.appConfiguration = appConfiguration;
//		this.imageConversion = imgConversion;
		provider = new Provider();
		siteResolver = provider.createSiteResolutionDataSource();
		routingOverrideServices = provider.createRoutingOverrideServices();

		appConfiguration.getLocalSiteNumber();

		ServerAdapterImpl.getSingleton().addServerLifecycleListener(this);
		
		logger.info("<ctor> site resolver was" + (siteResolver == null ? " NOT " : " ") + "created successfully.");
		if(routingOverrideServices != null && routingOverrideServices.size() > 0)
		{
			String msg = "The following routing overrides are in place: ";
			for(RoutingOverrideSpi overrideService : routingOverrideServices)
				msg += overrideService.toString() + ",";
			logger.warn(msg);
		}
		
		this.asynchRouter = new AsynchronousRouterImpl(this);
		//this.asynchCommandFactory = new CommandFactoryImpl(this, dodCache, vaCache);
	}
	
	//public void setAppConfiguration(AppConfiguration appConfiguration) 
	//{
	//	this.appConfiguration = appConfiguration;
	//}

	// -------------------------------------------------------------------
	// Application Configuration properties made available for commands
	// -------------------------------------------------------------------
	@Override
	public boolean isCachingEnabled()
	{
		return getAppConfiguration().isCachingEnabled();		
	}
	
	@Override
	public IAppConfiguration getAppConfiguration()
	{
		return appConfiguration;
	}

	public SiteResolutionDataSourceSpi getSiteResolver()
	{
		return siteResolver;
	}

	/**
	 * @return the DataSourceProvider
	 */
	public DataSourceProvider getProvider()
	{
		return this.provider;
	}

	/**
	 * The boolean property FailoverOnMethodException effects how the RouterImpl 
	 * behaves when a MethodException occurs within a Service implementation.
	 * If FailoverOnMethodException is true then the RouterImpl will attempt to 
	 * use an alternate protocol to contact the remote site.  If all available
	 * protocols result in failure then the MethodException will be included in a
	 * CompositeMethodException, along with the Exception instances generated from 
	 * each protocol attempted.
	 * If FailoverOnMethodException is false then a MethodException in a
	 * Service implementation is passed up to the client.
	 * In either case all MethodException occurrences are logged.
	 * @return
	 */
	public boolean isFailoverOnMethodException()
    {
    	return failoverOnMethodException;
    }

	protected void setFailoverOnMethodException(boolean failoverOnMethodException)
    {
    	this.failoverOnMethodException = failoverOnMethodException;
    }

	// ============================================================
	// ServerLifecycleListener Implementation
	// ============================================================
	@Override
    public void serverLifecycleEvent(ServerLifecycleEvent event)
    {
		logger.info("CoreRouter received a server " + event.getEventType().toString() + " event.");
    }


	// =====================================================================================================
	// Asynchronous Processing Related Methods
	// =====================================================================================================
	//@Override
    //public CommandFactory getAsynchronousCommandFactory()
    //{
	//   return asynchCommandFactory;
    //}

	/**
	 * Submit a command for asynchronous execution, optionally providing a listener where the results
	 * may be communicated back to the client.
	 * 
	 * @param command - an AsynchronousCommand instance, created by this Router's AsynchronousCommandFactory
	 * as returned by the getAsynchronousCommandFactory().
	 * @param resultQueue - an optional Queue reference where the client can obtain the result of the asynchronous
	 * command
     * @see gov.va.med.imaging.core.interfaces.Router#doAsynchronously(gov.va.med.imaging.core.interfaces.AsynchronousRouterCommandTypes, java.util.Queue)
     */
    @Override
    public <T extends Object> void doAsynchronously(Command<T> command)
    {
    	if(command instanceof AbstractCommandImpl)
    	{
    		logger.info("Asynchronous execution of command of type '" + command.getClass().getSimpleName() +"' requested.");
    		if(isCommandAsynchronouslyExecutable(command))
    		{
        		logger.info("Submitting command of type '" + command.getClass().getSimpleName() +"' for asynchronous execution.");
    			asynchRouter.doAsynchronously((AbstractCommandImpl<?>)command);
    		}
			else
			{
        		logger.warn("Command of type '" + command.getClass().getSimpleName() +"' is not eligible for asynchronous execution and is being executed synchronously.");
				try
				{
					T result = doSynchronously(command);
					AsynchronousCommandResult<T> asynchResult = new AsynchronousCommandResult<T>(command, result);
				} 
    			catch (MethodException x)
				{
					x.printStackTrace();
				} 
    			catch (ConnectionException x)
				{
					x.printStackTrace();
				}
			}
    	}
    	else
    		logger.error("Request for asynchronous execution of an Command that is not compatible with this Router implementation.");
    }

    private boolean isCommandAsynchronouslyExecutable(Command<?> command)
    {
    	RouterCommandExecution commandAnnotation = command.getClass().getAnnotation(RouterCommandExecution.class);
    	return commandAnnotation != null && commandAnnotation.asynchronous();
    }
    
    private boolean isCommandDistributable(Command<?> command)
    {
    	RouterCommandExecution commandAnnotation = command.getClass().getAnnotation(RouterCommandExecution.class);
    	return commandAnnotation != null && commandAnnotation.distributable();
    }
    
    /**
     * 
     * @see gov.va.med.imaging.core.interfaces.Router#doSynchronously(gov.va.med.imaging.core.interfaces.router.Command)
     * public Object doSynchronously(Command<?> command)
     */
    @Override
    public <T extends Object> T doSynchronously(Command<T> command)
	throws MethodException, ConnectionException
    {
    	if(command instanceof AbstractCommandImpl<?>)
    	{
			return ((AbstractCommandImpl<T>)command).callSynchronously();
    	}
    	else
    	{
    		logger.error("Request for synchronous execution of command '" + 
    			(command == null ? "<null>" : command.toString()) + 
    			"' that is not compatible with this Router implementation.");
    		return null;
    	}
    }
    
}
