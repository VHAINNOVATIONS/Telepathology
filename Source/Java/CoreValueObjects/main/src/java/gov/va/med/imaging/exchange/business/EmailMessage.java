/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 9, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.business;

import java.util.ArrayList;

/**
 * Email class that contains the recipients (as an Array), Subject Line, and Message Body.
 * 
 * @author VHAISWPETERB
 *
 */
public class EmailMessage {

	private ArrayList<String> recipients = null;
	private String subjectLine = null;
	private String messageBody = null;
	private boolean urgent = false;
	
	/**
	 * 	Create object using empty constructor
	 */
	public EmailMessage() {
		
	}
	
	/**
	 * Constructor
	 * @param recipients represents group of email addresses to receive this email.
	 * @param subjectLine represents subject line for the email.
	 * @param messageBody represents the message body for the email.
	 */
	public EmailMessage(String[] recipients, String subjectLine, String messageBody){
		this.recipients = collectRecipientsInArrayList(recipients);
		this.subjectLine = subjectLine;
		this.messageBody = messageBody;
		this.urgent = false;
	}

	/**
	 * Constructor
	 * @param recipients represents group of email addresses to receive this email.
	 * @param subjectLine represents subject line for the email.
	 * @param messageBody represents message body for the email.
	 * @param urgent Is this an urgent message?  Set to True if urgent.
	 */
	public EmailMessage(String[] recipients, String subjectLine, String messageBody, boolean urgent){
		this.recipients = collectRecipientsInArrayList(recipients);
		this.subjectLine = subjectLine;
		this.messageBody = messageBody;
		this.urgent = urgent;
	}

	/**
	 * Constructor
	 * 
	 * @param recipients represents group of email addresses to receive this email.
	 * @param subjectLine represents subject line for the email.
	 * @param messageBody represents message body for the email.
	 * @param urgent Is this an urgent message?  Set to True if urgent.
	 */
	public EmailMessage(ArrayList<String> recipients, String subjectLine, String messageBody, boolean urgent){
		this.recipients = recipients;
		this.subjectLine = subjectLine;
		this.messageBody = messageBody;
		this.urgent = urgent;
	}

	/**
	 * @return the recipients which is a group of email addresses to receive this email.
	 */
	public ArrayList<String> getRecipients() {
		return this.recipients;
	}

	/**
	 * @return the recipients which is a group of email addresses to receive this email.
	 */
	public String[] getRecipientsAsArray() {
		return (String[])this.recipients.toArray();
	}

	/**
	 * @param recipients the recipients to set using a String Array.
	 */
	public void setRecipients(String[] recipients) {
		this.recipients = collectRecipientsInArrayList(recipients);
	}

	/**
	 * @param recipients the recipients to set using an ArrayList.
	 */
	public void setRecipients(ArrayList<String> recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the subjectLine subject line for the email.
	 */
	public String getSubjectLine() {
		return subjectLine;
	}

	/**
	 * @param subjectLine the subject line to set
	 */
	public void setSubjectLine(String subjectLine) {
		this.subjectLine = subjectLine;
	}

	/**
	 * @return the messageBody the message body for the email.
	 */
	public String getMessageBody() {
		return messageBody;
	}

	/**
	 * @param messageBody the message body to set
	 */
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	/**
	 * @return the urgent
	 */
	public boolean isUrgent() {
		return urgent;
	}

	/**
	 * @param urgent the urgent to set
	 */
	public void setUrgent(boolean urgent) {
		this.urgent = urgent;
	}
	

	/**
	 * Converts the String Array into a ArrayList object.
	 * @param recipients
	 * @return
	 */
	private ArrayList<String> collectRecipientsInArrayList(String[] recipients){
		ArrayList<String> list = new ArrayList<String>(recipients.length);
		for(String s : recipients){
			list.add(s);
		}
		return list;
	}
}
