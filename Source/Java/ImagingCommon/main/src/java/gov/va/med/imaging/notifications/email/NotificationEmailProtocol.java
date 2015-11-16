/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.med.imaging.notifications.email;

import java.net.URI;
import javax.mail.internet.AddressException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import gov.va.med.imaging.notifications.*;

/**
 *
 * @author Jon Louthian
 */
public class NotificationEmailProtocol implements NotificationProtocol {

    NotificationEmailConfiguration emailConfig = NotificationEmailConfiguration.getConfiguration();
    NotificationConfiguration config = NotificationConfiguration.getConfiguration();
	private static final Logger logger = Logger.getLogger(NotificationEmailProtocol.class);


    public void send(Notification notification) {
    	sendTo(null, notification);
    	
    }
    public void sendTo(List<InternetAddress> tos, Notification notification) {

        Properties props = new Properties();
        props.put("mail.smtp.host", emailConfig.getSmtpServerUri().toString());
        Session session = Session.getInstance(props, null);

        List<InternetAddress> recipients=null;
        if (tos !=null)
        	recipients=tos;
        else
        	recipients = emailConfig.getRecipientsForNotificationType(notification.getNotificationType());

        for (InternetAddress recipient : recipients) {
            try
            {
                Message msg = createMessage(session, recipient, notification);
                Transport.send(msg);
                logger.info(this.getClass().getName()+": HDIG Email sent.");
            } 
            catch (MessagingException e)
            {
            	logger.warn(this.getClass().getName()+": HDIG failed to send email.");
                logger.warn("Email failure: "+e);
            	//e.printStackTrace();
            }
        }
    }

    private Message createMessage(Session session, InternetAddress recipient, Notification notification) throws AddressException, MessagingException {
        // create a message
        Message msg = new MimeMessage(session);
        msg.setFrom(emailConfig.getSenderAddress());
        InternetAddress[] address = {recipient};
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(notification.getSubject());
        msg.setText(notification.getMessage());
        msg.setSentDate(new Date());
        return msg;
    }
}
