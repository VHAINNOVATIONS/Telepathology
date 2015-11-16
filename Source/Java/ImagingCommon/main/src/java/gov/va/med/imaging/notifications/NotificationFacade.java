/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.va.med.imaging.notifications;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.imaging.notifications.email.NotificationEmailProtocol;
import gov.va.med.server.ServerAdapterImpl;
import gov.va.med.server.ServerLifecycleEvent;
import gov.va.med.server.ServerLifecycleListener;

/**
 *
 * @author Jon Louthian
 */
public class NotificationFacade
implements ServerLifecycleListener
{
	private static final Logger logger = Logger.getLogger(NotificationFacade.class);
    private static NotificationConfiguration config = NotificationConfiguration.getConfiguration();
    static List<NotificationProtocol> protocols;

    static
    {
        protocols = new ArrayList<NotificationProtocol>();
        protocols.add(new NotificationEmailProtocol());
    }
    
	public NotificationFacade()
	{
		ServerAdapterImpl.getSingleton().addServerLifecycleListener(this);
	}

    public static void sendGenericNotification(String subject, String message)
    {
        send(Notification.getGenericNotification(subject, message));
    }

    public static void sendVixStartupNotification()
    {
        send(Notification.getVixStartupNotification());
    }

    public static void sendNotification(NotificationTypes notificationType, String subject, String message)
    {
    	Notification notification = Notification.createNotification(notificationType, subject, message);
    	send(notification);
    }

    private static void send(Notification notification)
    {
        if (config.isNotificationEnabled())
        {
        	logNotification(notification);
            for (NotificationProtocol protocol : protocols)
            {
                protocol.send(notification);
            }
        }
        else
        {
        	logger.info("A notification of type " + notification.getNotificationType().name() 
        			+ " was requested, but the notification subsystem is currently disabled.");
        }
    }

	@Override
	public void serverLifecycleEvent(ServerLifecycleEvent event)
	{
		if (event.getEventType().equals(ServerLifecycleEvent.EventType.AFTER_START))
		{
			// Send startup notification
			sendVixStartupNotification();
		}
		
	}
	
	private static void logNotification(Notification notification)
	{
		StringBuilder message = new StringBuilder("Notification of type " + notification.getNotificationType().name() + " received. ");
		message.append("Subject: " + notification.getSubject() + ".");
		logger.info(message);
	}

}
