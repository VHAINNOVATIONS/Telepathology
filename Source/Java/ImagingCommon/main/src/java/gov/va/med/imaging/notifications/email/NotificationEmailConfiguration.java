/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.va.med.imaging.notifications.email;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;
import gov.va.med.imaging.notifications.NotificationTypes;
import gov.va.med.imaging.url.vista.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

/**
 *
 * @author Jon Louthian
 */
public class NotificationEmailConfiguration extends AbstractBaseFacadeConfiguration
{
	private static final Logger logger = Logger.getLogger(NotificationEmailConfiguration.class);
    public static final String DEFAULT_RECIPIENT_ADDRESS = "VhaViVixDev@va.gov";
    private static final int MAXMSGCOUNTPEREMAIL = 100; // set here for not configured input only
    private static final int MAXMSGBYTESIZEPEREMAIL = 5*1024*1024;	// sets upper limit (5 MB), size is configurable by user,
    																// works like upper limit guard; used for not configured input also
    private URI smtpServerUri;
    private InternetAddress senderAddress;
    private int maximumMessageCountPerEmail = MAXMSGCOUNTPEREMAIL;
    private int maximumByteSizePerEmail = MAXMSGBYTESIZEPEREMAIL;

	private HashMap<NotificationTypes, List<InternetAddress>> notificationTypeToRecipientsMap = new HashMap<NotificationTypes, List<InternetAddress>>();

    private static NotificationEmailConfiguration config = null;

    /**
     * @return the smtpHost
     */
    public URI getSmtpServerUri() {
        return smtpServerUri;
    }

    /**
     * @param smtpHost the smtpHost to set
     */
    public void setSmtpServerUri(URI smtpServerUri) {
        this.smtpServerUri = smtpServerUri;
    }

    /**
     * @return the senderAddress
     */
    public InternetAddress getSenderAddress() {
        return senderAddress;
    }

    /**
     * @param senderAddress the senderAddress to set
     */
    public void setSenderAddress(InternetAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    /**
     * @return the notificationTypeToRecipientsMap
     */
    public HashMap<NotificationTypes, List<InternetAddress>> getNotificationTypeToRecipientsMap() 
    {
        return notificationTypeToRecipientsMap;
    }

    /**
     * @param notificationTypeToRecipientsMap the notificationTypeToRecipientsMap to set
     */
    public void setNotificationTypeToRecipientsMap(HashMap<NotificationTypes, List<InternetAddress>> notificationTypeToRecipientsMap) {
        this.notificationTypeToRecipientsMap = notificationTypeToRecipientsMap;
    }


    public List<InternetAddress> getRecipientsForNotificationType(NotificationTypes type)
    {
        // Start with an empty arraylist of recipients, and only overwrite it
        // if there is a key for the type, and the key has mapped recipients,
        // This way we never return null
        List<InternetAddress> recipients = new ArrayList<InternetAddress>();

        if (getNotificationTypeToRecipientsMap().containsKey(type) && (getNotificationTypeToRecipientsMap().get(type) != null))
        {
            recipients = getNotificationTypeToRecipientsMap().get(type);
        }

        // If there are no recipients configured, add the default recipient
        // to the list.
        if (recipients==null || recipients.size() == 0)
        {
            recipients = new ArrayList<InternetAddress>();
            try
            {
                recipients.add(new InternetAddress(DEFAULT_RECIPIENT_ADDRESS));
            }
            catch (AddressException e)
            {
            	logger.error("Error creating the InternetAddress for email notification's default recipient.", e);
            }
        }

        return recipients;
    }


    public void setRecipientsForNotificationType(NotificationTypes notificationType, String commaSeparatedRecipientAddresses)
    {
    	// Create a new list of recipients
    	List<InternetAddress> recipientAddressList = new ArrayList<InternetAddress>();
    	
    	// Split the comma separated string into an array
    	String[] recipientAddresses = StringUtils.Split(commaSeparatedRecipientAddresses, StringUtils.COMMA);
    	
    	// Add each address to the list of internet addresses
    	for (String recipientAddress : recipientAddresses)
    	{
    		try
    		{
    			recipientAddressList.add(new InternetAddress(recipientAddress));
    		}
    		catch(AddressException e)
            {
            	logger.error("Error creating an InternetAddress.", e);
            }
    	}
    	
    	// Set the new list for the given notification type
    	notificationTypeToRecipientsMap.put(notificationType, recipientAddressList);
    }


    @Override
    public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
    {
        // create default recipient mappings for notification types
        try
        {
            // Set the sender and smtp host
            this.setSenderAddress(new InternetAddress("vix@va.gov"));
            this.setSmtpServerUri(new URI("smtp.va.gov"));
            this.maximumMessageCountPerEmail = MAXMSGCOUNTPEREMAIL;
            this.maximumByteSizePerEmail = MAXMSGBYTESIZEPEREMAIL;

            ArrayList<InternetAddress> defaultRecipients = new ArrayList<InternetAddress>();
            defaultRecipients.add(new InternetAddress(DEFAULT_RECIPIENT_ADDRESS));

            getNotificationTypeToRecipientsMap().put(NotificationTypes.GenericNotification, defaultRecipients);
            getNotificationTypeToRecipientsMap().put(NotificationTypes.VixStarted, defaultRecipients);
        }
        catch (URISyntaxException e)
        {
            logger.error("Error creating URI - bad address for the smtp server.", e);
        }
        catch(AddressException e)
        {
        	logger.error("Error creating an InternetAddress.", e);
        }

        return this;
    }

    public synchronized static NotificationEmailConfiguration getConfiguration() 
    {
        try
        {
        	return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
        			NotificationEmailConfiguration.class);
        }
        catch(CannotLoadConfigurationException clcX)
        {
        	return null;
        }
    }

    public static void main(String[] args) 
    {

    	// If there are 0 or 1 arguments, we're fine. Otherwise, print usage.
   		if (args.length < 2)
   		{
   	    	// Get the admin email address for invalid service account credentials
   	    	String adminEmailAddress = getAdminEmailAddress(args);

   	    	// Set it in the config file
   	    	NotificationEmailConfiguration config = NotificationEmailConfiguration.getConfiguration();
        	config.setRecipientsForNotificationType(NotificationTypes.InvalidServiceAccountCredentials, adminEmailAddress);
            getConfiguration().storeConfiguration();
   		}
    	else
    	{
            System.out.println("This program requires zero or one argument. If one argument is provided, it must be:");
            System.out.println("  * An email address (or addresses, separated by commas) for the individual(s) to");
            System.out.println("    notify if the service has invalid service account credentials");
    	}
    }

    /**
     * Gets the admin email address for invalid service account credentials. If no args
     * are passed in, the default email is used. Otherwise, the passed-in argument is used
     * @param args
     * @return
     */
	private static String getAdminEmailAddress(String[] args) 
	{
		if (args.length == 0)
		{
			return DEFAULT_RECIPIENT_ADDRESS;
		}
		else
		{
			return args[0];
		}
	}

	public String getRecipientsForNotificationTypeAsDelimitedString(NotificationTypes notificationType) 
	{
		List<InternetAddress> addresses = getRecipientsForNotificationType(notificationType);
		
		int count = 1;
		
		String delimitedAddressString = "";
		
		for(InternetAddress address : addresses)
		{
			if (count > 1)
			{
				delimitedAddressString += ",";
			}
			
			delimitedAddressString += address.getAddress();
			count++;
		}
		
		return delimitedAddressString;
	}

	/**
	 * @return the maximumMessageCountPerEmail
	 */
	public int getMaximumMessageCountPerEmail() {
		return maximumMessageCountPerEmail;
	}

	/**
	 * @param maximumMessageCountPerEmail the maximumMessageCountPerEmail to set
	 */
	public void setMaximumMessageCountPerEmail(int maximumMessageCountPerEmail) {
		this.maximumMessageCountPerEmail = maximumMessageCountPerEmail;
	}
	
	/**
	 * @return the maximumByteSizePerEmail
	 */
    public int getMaximumByteSizePerEmail() {
		return maximumByteSizePerEmail;
	}

    /**
	 * @param maximumMessageSizePerEmail the maximumByteSizeCountPerEmail to set
	 */
	public void setMaximumByteSizePerEmail(int maximumByteSizePerEmail) {
		if ((maximumByteSizePerEmail < MAXMSGBYTESIZEPEREMAIL) && (maximumByteSizePerEmail > 0))
			this.maximumByteSizePerEmail = maximumByteSizePerEmail;
		else {
			this.maximumByteSizePerEmail = MAXMSGBYTESIZEPEREMAIL;
			// TODO log: invalid e-mail byte size in configuration file	should be between 0 and MAXMSGBYTESIZEPEREMAIL
		}
	}
}
