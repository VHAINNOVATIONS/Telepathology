/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.va.med.imaging.notifications;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

/**
 *
 * @author Jon Louthian
 */
public class Notification
{
    private static NotificationConfiguration config = NotificationConfiguration.getConfiguration();

    private NotificationTypes notificationType;
    private String subject;
    private String message;

    // Private Constructor
    private Notification(NotificationTypes notificationType, String subject, String message)
    {
        this.notificationType = notificationType;
        this.subject = generateDecoratedSubject(subject);
        this.message = generateDecoratedMessage(message);
    }

    public static Notification getGenericNotification(String subject, String message)
    {
        return new Notification(NotificationTypes.GenericNotification, subject, message);
    }

    public static Notification createNotification(NotificationTypes notificationType, String subject, String message)
    {
        return new Notification(notificationType, subject, message);
    }

    public static Notification getVixStartupNotification()
    {
        return new Notification(NotificationTypes.VixStarted, 
                config.getVixStartupSubject(),
                config.getVixStartupMessage());
    }

    private static String generateDecoratedSubject(String subject)
    {
        StringBuilder builder = new StringBuilder(subject);

        // Append the footer
        builder.append(" - Server '" + getHostName() + "' at Site " + config.getLocalSiteNumber());

        return builder.toString();
    }

    private static String generateDecoratedMessage(String message)
    {
        StringBuilder builder = new StringBuilder(message);

        // Append the footer
        builder.append("\n\n");
        builder.append("Hostname: " + getHostName() + "\n");
        builder.append("SiteId: " + config.getLocalSiteNumber() + "\n");
        builder.append("Timestamp: " + DateFormat.getDateTimeInstance().format(new Date()));


        return builder.toString();
    }

    private static String getHostName()
    {
        String hostName = "";
        try
        {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            //Log host name problem...
        }

        return hostName;
    }

    /**
     * @return the notificationType
     */
    public NotificationTypes getNotificationType() {
        return notificationType;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
