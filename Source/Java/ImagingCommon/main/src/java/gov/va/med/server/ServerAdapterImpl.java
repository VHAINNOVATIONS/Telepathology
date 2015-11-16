/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 14, 2008
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
package gov.va.med.server;

import gov.va.med.imaging.StackTraceAnalyzer;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * The central gateway of the server agnostic mechanism.  Components of Visa
 * register interest in events here.  Server-specific lifecycle classes send messages 
 * to this singleton.
 * This class also provides access to authentication/authorization services.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ServerAdapterImpl 
implements ClusterEventAdapter, ServerAdapter
{
	private static ServerAdapterImpl singleton = null;
	public static synchronized ServerAdapter getSingleton()
	{
		if(singleton == null)
			singleton = new ServerAdapterImpl();
		
		return singleton;
	}
	
	private Set<ServerLifecycleListener> serverEventListeners = new HashSet<ServerLifecycleListener>();
	private Set<ClusterEventListener> clusterEventListeners = new HashSet<ClusterEventListener>();
	private ClusterMessageSender clusterMessageSender = null;
	private GlobalNamingServer globalNamingServer = null;
	private ServerAuthentication serverAuthentication = null;
	
	private final Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 
	 */
	private ServerAdapterImpl()
	{
		StackTraceAnalyzer stackAnalyzer = new StackTraceAnalyzer( (new Throwable()).getStackTrace() );
		logger.info(
			"Instance '" + this.hashCode() + "' of ServerAdapter has been created, probably by '" + 
			stackAnalyzer.getFirstElementNotInPackage("gov.va.med.server") + 
			"' by class loader '" + this.getClass().hashCode() + "'." +
			"' of type '" + this.getClass().getClassLoader().getClass().getName() + "'.");
	}
	
	
	/**
	 * @return the logger
	 */
	public Logger getLogger()
	{
		return this.logger;
	}


	/**
	 * Add a new listener (on the application side) that gets notified of cluster status changes.
	 * 
	 * @see gov.va.med.server.ClusterEventAdapter#addClusterEventListener(gov.va.med.server.ClusterEventListener)
	 */
	@Override
	public void addClusterEventListener(ClusterEventListener listener)
	{
		clusterEventListeners.add(listener);
	}

	/**
	 * Remove a listener (on the application side) that gets notified of cluster status changes.
	 * 
	 * @see gov.va.med.server.ClusterEventAdapter#removeClusterEventListener(gov.va.med.server.ClusterEventListener)
	 */
	@Override
	public void removeClusterEventListener(ClusterEventListener listener)
	{
		clusterEventListeners.remove(listener);
	}

	public void setClusterMessageSender(ClusterMessageSender sender)
	{
		if(sender == this.clusterMessageSender)
			return;		// silently ignore
		
		if(sender != null && clusterMessageSender != null)
		{
			getLogger().error("Attempt to change cluster message sender is being ignored.");
			return;
		}
		
		if(sender == null && clusterMessageSender != null)
		{
			getLogger().warn("Setting the cluster message sender to null, allowed but intentional?.");
			return;
		}
		
		clusterMessageSender = sender;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.server.ServerAdapter#getClusterMessageSender()
	 */
	public ClusterMessageSender getClusterMessageSender()
	{
		return clusterMessageSender;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.server.ServerAdapter#getGlobalNamingServer()
	 */
	public GlobalNamingServer getGlobalNamingServer()
	{
		return this.globalNamingServer;
	}

	/**
	 * @param globalNamingServer the globalNamingServer to set
	 */
	public void setGlobalNamingServer(GlobalNamingServer globalNamingServer)
	{
		// disregard repetitive sets, avoid logging any changes
		if(globalNamingServer == this.globalNamingServer)
			return;
		
		// disallow resetting the global naming server
		if(globalNamingServer == null && this.globalNamingServer != null)
		{
			getLogger().error("Attempt to reset the global naming server to null is being disregarded");
		}
		else if(globalNamingServer != null && this.globalNamingServer != null)
		{
			getLogger().warn("Changing the global naming server after it has been set.");
			this.globalNamingServer = globalNamingServer;
		}
		else
		{
			getLogger().info("Setting global naming server for the first time.");
			this.globalNamingServer = globalNamingServer;
		}
	}

	/**
	 * @return the serverAuthentication
	 */
	@Override
	public ServerAuthentication getServerAuthentication()
	{
		return this.serverAuthentication;
	}

	/**
	 * @param serverAuthentication the serverAuthentication to set
	 */
	@Override
	public void setServerAuthentication(ServerAuthentication serverAuthentication)
	{
		// disregard repetitive sets, avoid logging any changes
		if(serverAuthentication == this.serverAuthentication)
			return;
		
		// disallow resetting the global naming server
		if(serverAuthentication == null && this.serverAuthentication != null)
		{
			getLogger().error("Attempt to reset the serverAuthentication to null is being disregarded");
		}
		else if(serverAuthentication != null && this.serverAuthentication != null)
		{
			getLogger().warn("Changing the serverAuthentication after it has been set.");
			this.serverAuthentication = serverAuthentication;
		}
		else
		{
			getLogger().info("Setting serverAuthentication for the first time.");
			this.serverAuthentication = serverAuthentication;
		}
	}


	/* (non-Javadoc)
	 * @see gov.va.med.server.ServerAdapter#addServerLifecycleListener(gov.va.med.server.ServerLifecycleListener)
	 */
	public void addServerLifecycleListener(ServerLifecycleListener listener)
	{
		getLogger().info("Adding ServerLifeCycleListener '" + listener.toString() + "'.");
		serverEventListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.server.ServerAdapter#removeServerLifecycleListener(gov.va.med.server.ServerLifecycleListener)
	 */
	public void removeServerLifecycleListener(ServerLifecycleListener listener)
	{
		getLogger().info("Removing ServerLifeCycleListener '" + listener.toString() + "'.");
		serverEventListeners.remove(listener);
	}

	/**
	 * 
	 * @param event
	 */
	public void notifyClusterEventListeners(ClusterEvent event)
	{
		for(ClusterEventListener listener : clusterEventListeners)
			listener.clusterEvent(event);
	}
	
	private boolean serverStarted = false;
	/**
	 * Called from the server-specific adapters to notify us of a server event.
	 * @param applicationEvent
	 */
	public synchronized void serverLifecycleEvent(ServerLifecycleEvent applicationEvent)
	{
		if( applicationEvent.getEventType() == ServerLifecycleEvent.EventType.AFTER_START && !serverStarted)
		{
			serverStarted = true;
		}
		
		if( applicationEvent.getEventType() == ServerLifecycleEvent.EventType.AFTER_STOP && serverStarted)
		{
			serverStarted = false;
		}
		
		notifyServerLifecycleListeners(applicationEvent);
	}

	/**
	 * 
	 * @param event
	 */
	private void notifyServerLifecycleListeners(ServerLifecycleEvent event)
	{
		getLogger().info("Notifying server lifecycle listeners, event is '" + event.toString() + "'.");
		for(ServerLifecycleListener listener : serverEventListeners)
			listener.serverLifecycleEvent(event);
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public boolean sendMessageToCluster(Serializable msg)
	{
		getLogger().info("Sending message '" + msg.toString() + "' to cluster.");
		return clusterMessageSender.sendMessageToCluster(msg);
	}


}
