/**
 * 
 */
package gov.va.med.imaging.core.interfaces.router;

import java.lang.reflect.Proxy;
import gov.va.med.IdentityProxyInvocationHandler;
import gov.va.med.JndiUtility;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.server.ServerAdapterImpl;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractFacadeRouterImpl
implements FacadeRouter
{
	/**
	 * The JNDI name of the core router
	 */
	private static final String CORE_ROUTER = "java:comp/env/CoreRouter";
	private static final String CORE_ROUTER_GLOBAL_CONTEXT = "CoreRouter";
	/**
	 * The JNDI name of the command factory
	 */
	private static final String COMMAND_FACTORY = "java:comp/env/CommandFactory";
	private static final String COMMAND_FACTORY_GLOBAL_CONTEXT = "CommandFactory";
	private Router router;
	private CommandFactory commandFactory;
	
	/**
	 * 
	 * @return
	 */
	private Logger getMyLogger()
	{
		return Logger.getLogger(AbstractFacadeRouterImpl.class);
	}
	
	/**
	 * 
	 * @return
	 */
	protected synchronized Router getRouter()
	{
		if(router == null)
		{
			javax.naming.Context ctx;
			try
			{
				ctx = new javax.naming.InitialContext();
				getMyLogger().info("Getting reference from context'" + ctx.getNameInNamespace() + "' to router using name '" + CORE_ROUTER + "'.");
				Object obj = ctx.lookup(CORE_ROUTER);
				try
				{
					router = (Router)obj;
				}
				catch (ClassCastException x)
				{
					getMyLogger().warn("Error casting object of type '" + obj.getClass().getName() + 
							"', loaded by '" + obj.getClass().getClassLoader().getClass().getName() + 
							"' to type '" + Router.class.getName() + 
							"', loaded by '" + Router.class.getClassLoader().getClass().getName() + "'. \n" +
							"Creating proxied reference to implementation of Router.");
					
					IdentityProxyInvocationHandler<Router> classLoaderEndRun = 
						new IdentityProxyInvocationHandler<Router>(obj, Router.class);
					router = 
						(Router)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Router.class}, classLoaderEndRun);
				}
			} 
			catch (NamingException x)
			{
				try
				{
					ctx = ServerAdapterImpl.getSingleton().getGlobalNamingServer().getGlobalContext();
					getMyLogger().info("Getting reference from the global context to router using name '" + CORE_ROUTER + "'.");
					Object obj = ctx.lookup(CORE_ROUTER_GLOBAL_CONTEXT);
					router = (Router)obj;
				} 
				catch (NamingException nX1)
				{
					nX1.printStackTrace();
				}
			}			
		}
		
		return router;
	}

	private static String commandFactoryReferenceError = 
		"Web applications must declare a resource to '" + COMMAND_FACTORY + "' in web.xml \n" + 
		"and declare a resource reference in context.xml.";
	
	/**
	 * 
	 * @return
	 */
	protected synchronized CommandFactory getCommandFactory()
	{
		if(commandFactory == null)
		{
			javax.naming.Context ctx;
			try
			{
				ctx = new javax.naming.InitialContext();
				getMyLogger().info("Getting reference from context '" + ctx.getNameInNamespace() + "' to command factory using name '" + COMMAND_FACTORY + "'.");
				getMyLogger().info(JndiUtility.contextDump(ctx, "java:comp", true));
				Object obj = ctx.lookup(COMMAND_FACTORY);
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				try
				{
					// switch the context class loader to the system class load, rather than
					// a web class loader, so that the casting will work.  otherwise the 
					// CommandFactory class may be loaded by the current web app class
					// and the cast will fail
					Thread.currentThread().setContextClassLoader(obj.getClass().getClassLoader());
					commandFactory = (CommandFactory)obj;
				}
				catch(ClassCastException ccX)
				{

					getMyLogger().warn("Error casting object of type '" + obj.getClass().getName() + 
							"', loaded by '" + obj.getClass().getClassLoader().getClass().getName() + 
							"' to type '" + CommandFactory.class.getName() + 
							"', loaded by '" + CommandFactory.class.getClassLoader().getClass().getName() + "'. \n" +
							"Creating proxied reference to implementation of CommandFactory.");
					
					IdentityProxyInvocationHandler<CommandFactory> classLoaderEndRun = 
						new IdentityProxyInvocationHandler<CommandFactory>(obj, obj.getClass());
					commandFactory = 
						(CommandFactory)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{CommandFactory.class}, classLoaderEndRun);
				}
				finally
				{
					Thread.currentThread().setContextClassLoader(loader);
				}
			} 
			catch (NamingException x)
			{
				try
				{
					getMyLogger().info(
							"Failed to get reference to command factory using web naming context." + 
							"Getting reference from the global context to command factory using name '" + 
							COMMAND_FACTORY_GLOBAL_CONTEXT + "'.");
					ctx = ServerAdapterImpl.getSingleton().getGlobalNamingServer().getGlobalContext();
					Object obj = ctx.lookup(COMMAND_FACTORY_GLOBAL_CONTEXT);
					try
					{
						commandFactory = (CommandFactory)obj;
					}
					catch(ClassCastException ccX)
					{
						getMyLogger().error("Error casting object of type '" + obj.getClass().getName() + 
								"', loaded by '" + obj.getClass().getClassLoader().getClass().getName() + 
								"' to type '" + CommandFactory.class.getName() + 
								"', loaded by '" + CommandFactory.class.getClassLoader().getClass().getName() + "'.");
					}
					
				} 
				catch (NamingException nX1)
				{
					getMyLogger().error("Completely failed to get reference from the global context to command factory using name '" + 
							COMMAND_FACTORY + "'.\n" + 
							commandFactoryReferenceError );
				}
				catch (Throwable t)
				{
					getMyLogger().error("Completely failed to get reference to command factory.\n" + commandFactoryReferenceError, t);
				}
			}
		}
		
		return commandFactory;
	}
}
