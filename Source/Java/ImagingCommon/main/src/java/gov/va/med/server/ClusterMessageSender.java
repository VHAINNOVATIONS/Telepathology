/**
 * 
 */
package gov.va.med.server;

import java.io.Serializable;

/**
 * This interface must be implemented by the server specific class that sends messages
 * to the cluster nodes.
 * 
 * @author vhaiswbeckec
 *
 */
public interface ClusterMessageSender
{
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public boolean sendMessageToCluster(Serializable msg);

}
