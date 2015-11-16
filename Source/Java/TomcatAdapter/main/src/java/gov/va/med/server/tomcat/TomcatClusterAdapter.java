/**
 * 
 */
package gov.va.med.server.tomcat;

import gov.va.med.server.*;

import java.io.Serializable;

import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.Member;
import org.apache.log4j.Logger;

/**
 * A class that adapts Tomcat clustering messages to a server-neutral form.
 * 
 * @author vhaiswbeckec
 *
 */
public class TomcatClusterAdapter 
implements org.apache.catalina.tribes.MembershipListener, 
	org.apache.catalina.tribes.ChannelListener,
	org.apache.catalina.LifecycleListener,
	ClusterMessageSender
{
	private final ServerAdapter clusterAdapter;
	
	public TomcatClusterAdapter()
	{
		clusterAdapter = ServerAdapterImpl.getSingleton();
	}

	/**
	 * @return the clusterAdapter
	 */
	public ServerAdapter getClusterAdapter()
	{
		return this.clusterAdapter;
	}
	
	private Logger logger = Logger.getLogger(this.getClass());
	private Logger getLogger()
	{
		return logger;
	}

	// ===============================================================================
	// org.apache.catalina.LifecycleListener implementation
	// ===============================================================================
	public void lifecycleEvent(org.apache.catalina.LifecycleEvent event)
	{
		if( org.apache.catalina.Lifecycle.START_EVENT.equals(event.getType()) )
			start();
		else if( org.apache.catalina.Lifecycle.STOP_EVENT.equals(event.getType()) )
			stop();
	}
	
	private org.apache.catalina.tribes.Channel channel = null;
	private synchronized org.apache.catalina.tribes.Channel getChannel()
	{
		if(channel == null)
		{
			channel = new org.apache.catalina.tribes.group.GroupChannel();
			
	        //attach the listeners to the channel
			getChannel().addMembershipListener(this);
			getChannel().addChannelListener(this);
		}
		return channel;
	}

	private void start()
	{
		getLogger().info("Starting channel in TomcatClusterAdapter.");
		try
		{
			getChannel().start(org.apache.catalina.tribes.Channel.DEFAULT);
			getLogger().info("Channel STARTED in TomcatClusterAdapter.");
		} 
		catch (ChannelException x)
		{
			getLogger().error(x);
		}
	}
	
	private void stop()
	{
		getLogger().info("Stoppong channel in TomcatClusterAdapter.");
		try
		{
			channel.stop(org.apache.catalina.tribes.Channel.DEFAULT);
			getLogger().info("Channel STOPPED in TomcatClusterAdapter.");
		} 
		catch (ChannelException x)
		{
			getLogger().error(x);
		}
	}
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean sendMessageToCluster(Serializable msg)
	{
        //retrieve my current members
        Member[] group = channel.getMembers();

        //send the message
        try
		{
			channel.send(group, msg, org.apache.catalina.tribes.Channel.SEND_OPTIONS_DEFAULT);
			return true;
		} 
        catch (ChannelException x)
		{
			getLogger().error(x);
			return false;
		}
	}
	
	// ================================================================================================
	// Implementation of ClusterEventAdapter is in GenericClusterEventAdapter
	// ================================================================================================
	
	// ================================================================================================
	// Implementation of org.apache.catalina.tribes.MembershipListener
	// 
	// cluster event notification from Tomcat
	// ================================================================================================
	
	/* (non-Javadoc)
	 * @see org.apache.catalina.tribes.MembershipListener#memberAdded(org.apache.catalina.tribes.Member)
	 */
	@Override
	public void memberAdded(Member newMember)
	{
		String newMemberName = new String( newMember.getHost() );
		
		getClusterAdapter().notifyClusterEventListeners(ClusterEvent.createJoinEvent(newMemberName));
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.tribes.MembershipListener#memberDisappeared(org.apache.catalina.tribes.Member)
	 */
	@Override
	public void memberDisappeared(Member lostMember)
	{
		String newMemberName = new String( lostMember.getHost() );
		
		getClusterAdapter().notifyClusterEventListeners(ClusterEvent.createLeaveEvent(newMemberName));
	}

	// ================================================================================================
	// Implementation of org.apache.catalina.tribes.ChannelListener
	// 
	// cluster message notification from Tomcat
	// ================================================================================================
	
	/* (non-Javadoc)
	 * @see org.apache.catalina.tribes.ChannelListener#accept(java.io.Serializable, org.apache.catalina.tribes.Member)
	 */
	@Override
	public boolean accept(Serializable msg, Member node)
	{
		// return false to indicate that this message was not processed by us
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.tribes.ChannelListener#messageReceived(java.io.Serializable, org.apache.catalina.tribes.Member)
	 */
	@Override
	public void messageReceived(Serializable msg, Member node)
	{
		// TODO Auto-generated method stub
		
	}

}
