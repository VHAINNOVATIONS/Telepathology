/**
 * 
 */
package gov.va.med.server;

/**
 * The interface that defines interaction with the host server, providing
 * access to lifecycle, cluster and authentication/authorization services 
 * provided by the server.
 * 
 * @author vhaiswbeckec
 *
 */
public interface ServerAdapter
extends ClusterEventAdapter
{

	public abstract ClusterMessageSender getClusterMessageSender();

	/**
	 * @return the globalNamingServer
	 */
	public abstract GlobalNamingServer getGlobalNamingServer();
	public abstract void setGlobalNamingServer(GlobalNamingServer globalNamingServer);

	public ServerAuthentication getServerAuthentication();
	public void setServerAuthentication(ServerAuthentication serverAuthentication);
	
	/**
	 * @see gov.va.med.server.tomcat.ServerLifecycleListener#addLifecycleListener(gov.va.med.server.ServerLifecycleListener)
	 */
	public abstract void addServerLifecycleListener(ServerLifecycleListener listener);

	/**
	 * @see gov.va.med.server.tomcat.ServerLifecycleListener#removeLifecycleListener(gov.va.med.server.ServerLifecycleListener)
	 */
	public abstract void removeServerLifecycleListener(ServerLifecycleListener listener);

	/**
	 * 
	 * @param applicationEvent
	 */
	public abstract void serverLifecycleEvent(ServerLifecycleEvent applicationEvent);
	
}