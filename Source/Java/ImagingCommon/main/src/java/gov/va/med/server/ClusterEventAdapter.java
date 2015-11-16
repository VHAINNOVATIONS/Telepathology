/**
 * 
 */
package gov.va.med.server;

/**
 * @author vhaiswbeckec
 *
 */
public interface ClusterEventAdapter
{
	/**
	 * 
	 * @param listener
	 */
	public abstract void addClusterEventListener(ClusterEventListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	public abstract void removeClusterEventListener(ClusterEventListener listener);
	
	/**
	 * 
	 * @param event
	 */
	public abstract void notifyClusterEventListeners(ClusterEvent event);

}
