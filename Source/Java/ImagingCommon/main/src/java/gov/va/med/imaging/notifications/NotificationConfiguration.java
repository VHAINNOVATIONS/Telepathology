/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.va.med.imaging.notifications;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 *
 * @author Jon Louthian
 */
public class NotificationConfiguration extends AbstractBaseFacadeConfiguration
{
    private static NotificationConfiguration config = null;

    private static final String DEFAULT_VIX_STARTUP_SUBJECT = "VIX restarted";
    private static final String DEFAULT_VIX_STARTUP_MESSAGE = "The VIX server has restarted";

    private boolean notificationEnabled;
    private String localSiteNumber;
    private String vixStartupSubject;
    private String vixStartupMessage;


        /**
     * @return the vixStartupSubject
     */
    public String getVixStartupSubject() {
        return isNullOrEmpty(vixStartupSubject) ? DEFAULT_VIX_STARTUP_SUBJECT : vixStartupSubject; 
    }

    /**
     * @param vixStartupSubject the vixStartupSubject to set
     */
    public void setVixStartupSubject(String vixStartupSubject) {
        this.vixStartupSubject = vixStartupSubject;
    }

    /**
     * @return the vixStartupMessage
     */
    public String getVixStartupMessage() {
        return isNullOrEmpty(vixStartupMessage) ? DEFAULT_VIX_STARTUP_MESSAGE : vixStartupMessage; 
    }

    /**
     * @param vixStartupMessage the vixStartupMessage to set
     */
    public void setVixStartupMessage(String vixStartupMessage) {
        this.vixStartupMessage = vixStartupMessage;
    }

    /**
     * @return the localSiteNumber
     */
    public String getLocalSiteNumber() {
        return localSiteNumber;
    }

    /**
     * @param localSiteNumber the localSiteNumber to set
     */
    public void setLocalSiteNumber(String localSiteNumber) {
        this.localSiteNumber = localSiteNumber;
    }

    /**
     * @return the notificationsEnabled
     */
    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    /**
     * @param notificationsEnabled the notificationsEnabled to set
     */
    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    @Override
    public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
    {
        this.setVixStartupSubject(DEFAULT_VIX_STARTUP_SUBJECT);
        this.setVixStartupMessage(DEFAULT_VIX_STARTUP_MESSAGE);
        this.setNotificationEnabled(true);
        
        return this;
    }

    public synchronized static NotificationConfiguration getConfiguration() 
    {
    	try
    	{
    		return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
    				NotificationConfiguration.class);
    	}
    	catch(CannotLoadConfigurationException clcX)
    	{
    		return null;
    	}
    }

    private boolean isNullOrEmpty(String s) {
        if (s == null)
            return true;
        if (s.equals(""))
            return true;
        return false;
    }

    public static void main(String[] args) {
        if (args.length != 2)
        {
            printUsage();
            return;
        }
        NotificationConfiguration defaultConfig = getConfiguration();
        defaultConfig.setLocalSiteNumber(args[0]);
		boolean notificationEnabled = Boolean.parseBoolean(args[1]);
		defaultConfig.setNotificationEnabled(notificationEnabled);
        defaultConfig.storeConfiguration();
    }

    private static void printUsage() {
        System.out.println("This program requires two arguments: local_site_number enable_notifications.");
        System.out.println("enable_notifications can be true or false.");
    }



}
