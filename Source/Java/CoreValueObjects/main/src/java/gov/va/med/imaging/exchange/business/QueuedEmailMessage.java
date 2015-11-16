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
import java.util.Calendar;

/**
 * 	A Subclass that is use to support the EmailMessage object within the queue environment of VISA.
 * 
 * @author VHAISWPETERB
 *
 */
public class QueuedEmailMessage extends EmailMessage {

	private int retryCount = 0;
	private int messageCount = 1;
	private int bodyByteSize = 0;
	private Calendar dateTimePosted = null;

	/**
	 * Constructor
	 * 
	 */
	public QueuedEmailMessage() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param recipients represents group of email addresses to receive this email.
	 * @param subjectLine represents subject line for the email.
	 * @param messageBody represents the message body for the email.
	 */
	public QueuedEmailMessage(String[] recipients, String subjectLine,
			String messageBody) {
		super(recipients, subjectLine, messageBody);
	}

	/**
	 * Constructor
	 * 
	 * @param recipients represents group of email addresses to receive this email.
	 * @param subjectLine represents subject line for the email.
	 * @param messageBody represents the message body for the email.
	 * @param urgent Is this an urgent message?  Set to True if urgent.
	 */
	public QueuedEmailMessage(String[] recipients, String subjectLine,
			String messageBody, boolean urgent) {
		super(recipients, subjectLine, messageBody, urgent);
	}
	
	/**
	 * 
	 * @param email subclass object being passed in.
	 */
	public QueuedEmailMessage(EmailMessage email){
		super(email.getRecipients(), email.getSubjectLine(), email.getMessageBody(),
				email.isUrgent());
	}
	/**
	 * @return the retryCount represents number of times attempted to send the email message.
	 */
	public int getRetryCount() {
		return retryCount;
	}

	/**
	 * @param retryCount the retryCount to set
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	
	/**
	 * @return the messageCount represents the number of email messages combined into
	 * 	a single email message.
	 */
	public int getMessageCount() {
		return messageCount;
	}
	
	/**
	 * @return the bodyByteSize
	 */
	public int getBodyByteSize() {
		return bodyByteSize;
	}

	/**
	 * @param bodyByteSize the bodyByteSize to set
	 */
	public void setBodyByteSize(int bodyByteSize) {
		this.bodyByteSize = bodyByteSize;
	}

	/**
	 * increment Retry count.  This is used to track number of retries to send
	 * 	an email message. 
	 * 
	 */
	public void incrementRetryCount(){
		retryCount++;
	}

	/**
	 * @param messageCount the messageCount to set
	 */
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * The Subject Line and the day of the email message being posted are used to 
	 * 	this determination.
	 */
	@Override
	public boolean equals(Object obj) {
		QueuedEmailMessage email = (QueuedEmailMessage)obj;
    	int thisInstanceDay = email.getDateTimePosted().get(Calendar.DAY_OF_YEAR);
    	int emailObjDay = this.getDateTimePosted().get(Calendar.DAY_OF_YEAR);
		return (this.getSubjectLine().equalsIgnoreCase(email.getSubjectLine()) && thisInstanceDay == emailObjDay) ? true : false;
	}

	/**
	 * @return the dateTimePosted represents when the email message was posted to the queue in VISA.
	 */
	public Calendar getDateTimePosted() {
		return dateTimePosted;
	}

	/**
	 * @param dateTimePosted the dateTimePosted to set
	 */
	public void setDateTimePosted(Calendar dateTimePosted) {
		this.dateTimePosted = dateTimePosted;
	}

	
}
