package gov.va.med.server.tomcat;

import org.apache.catalina.LifecycleEvent;

import gov.va.med.server.ServerAdapter;
import gov.va.med.server.ServerAdapterImpl;
import gov.va.med.server.ServerLifecycleEvent;

/**
 * A class that listens for server lifecycle messages from Tomcat 
 * and then forwards ServerLifecycleEvent instances to the registered components.  
 * This class is Tomcat specific and should not be referenced outside of the
 * tomcat package (or better yet, all Tomcat stuff should be moved to its own
 * package).
 * 
 * Note that this delegates all operations to an internal singleton.  This is because
 * the server creates it own instance of this class but we need to assure that lifecycle
 * events (from the server) get to all of the components that register as listeners.
 * 
 * Note that this class is Tomcat-specific.
 * 
 * @author VHAISWBECKEC
 *
 */
public class TomcatLifecycleAdapter 
implements org.apache.catalina.LifecycleListener
{
	private final ServerAdapter serverAdapter;
	
	public TomcatLifecycleAdapter()
	{
		this.serverAdapter = ServerAdapterImpl.getSingleton();
		this.serverAdapter.setGlobalNamingServer( new TomcatNamingServer() );
	}
	
	/**
	 * @return the serverAdapter
	 */
	public ServerAdapter getServerAdapter()
	{
		return this.serverAdapter;
	}

	// ===============================================================================
	// org.apache.catalina.LifecycleListener implementation
	// ===============================================================================
	public void lifecycleEvent(org.apache.catalina.LifecycleEvent event)
	{
		ServerLifecycleEvent applicationEvent = translateLifecleEvent(event);
		
		if(applicationEvent != null)
			getServerAdapter().serverLifecycleEvent(applicationEvent);
	}

	/**
	 * This method maps a Tomcat LifecycleEvent into a VIX ServerLifeCycleEvent
	 * @param event
	 * @return
	 */
	private ServerLifecycleEvent translateLifecleEvent(LifecycleEvent event)
	{
		ServerLifecycleEvent applicationEvent = translate(event);
		
		return applicationEvent;
	}

	/**
	 * Translate the Tomcat specific events into VIX, server-agnostic, events
	 * 
	 * @param event
	 * @return
	 */
	static ServerLifecycleEvent translate(LifecycleEvent event)
	{
		if ("INIT".equalsIgnoreCase( event.getType() ))
			return new ServerLifecycleEvent(ServerLifecycleEvent.EventType.INIT);			
		else if ("BEFORE_START".equalsIgnoreCase( event.getType() ))
			return new ServerLifecycleEvent(ServerLifecycleEvent.EventType.BEFORE_START);			
		else if ("START".equalsIgnoreCase( event.getType() ))
			return new ServerLifecycleEvent(ServerLifecycleEvent.EventType.START);			
		else if ("AFTER_START".equalsIgnoreCase( event.getType() ))
			return new ServerLifecycleEvent(ServerLifecycleEvent.EventType.AFTER_START);			
		else if ("BEFORE_STOP".equalsIgnoreCase( event.getType() ))
			return new ServerLifecycleEvent(ServerLifecycleEvent.EventType.BEFORE_STOP);			
		else if ("STOP".equalsIgnoreCase( event.getType() ))
			return new ServerLifecycleEvent(ServerLifecycleEvent.EventType.STOP);			
		else if ("AFTER_STOP".equalsIgnoreCase( event.getType() ))
			return new ServerLifecycleEvent(ServerLifecycleEvent.EventType.AFTER_STOP);			
		
		return null;
	}
	
}

