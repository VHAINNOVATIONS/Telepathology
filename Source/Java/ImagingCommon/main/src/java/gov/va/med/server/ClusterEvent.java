/**
 * 
 */
package gov.va.med.server;

/**
 * 
 * 
 * @author vhaiswbeckec
 *
 */
public class ClusterEvent
{
	public static ClusterEvent createJoinEvent(String nodeName)
	{
		return new ClusterEvent(ClusterEventType.JOIN, System.currentTimeMillis(), nodeName);
	}
	
	public static ClusterEvent createLeaveEvent(String nodeName)
	{
		return new ClusterEvent(ClusterEventType.LEAVE, System.currentTimeMillis(), nodeName);
	}
	
	public enum ClusterEventType
	{
		JOIN, LEAVE
	}
	
	private final ClusterEventType type;
	private final long time;
	private final String nodeName;
	
	/**
	 * @param type
	 * @param time
	 * @param nodeName
	 */
	private ClusterEvent(ClusterEventType type, long time, String nodeName)
	{
		super();
		this.type = type;
		this.time = time;
		this.nodeName = nodeName;
	}

	/**
	 * @return the type
	 */
	public ClusterEventType getType()
	{
		return this.type;
	}

	/**
	 * @return the time
	 */
	public long getTime()
	{
		return this.time;
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName()
	{
		return this.nodeName;
	}
}
