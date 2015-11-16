/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class creates an Audit Event object to store in the Audit Event Log in the 
 * Data Source.  The Audit Event Log is a VA requirement to show basic operations 
 * and/or interactions.  Examples would be System Shutdown and Startup, 
 * and DICOM Service requests.
 * 
 * Though this class was originally created for DICOM Activity, it may be used by any 
 * application within VistA Imaging.
 * 
 * The minimum information needed for the Audit Log is the event, 
 * hostname, and application name.  Must use the static fields in this class to 
 * populate the event field.  Anything else may cause the object to be rejected 
 * from the Data Source.
 * 
 * An example:
 *	HashMap<String, String> eventElements = new HashMap<String, String>();
 *  eventElements.put("AETitle", AETitle);
 *  eventElements.put("DUZ", 126);
 *  String message = AETitle+", CFind Request.";
 *	AuditEvent event = new AuditEvent(AuditEvent.DICOM_QUERY, 
 *							"vhaiswimgvms20",
 *							"HDIG",
 *							message,
 *							eventElements));
 *
 * 
 * @author vhaiswpeterb
 *
 */
public class AuditEvent 
implements PersistentEntity, Serializable{

	public static final long serialVersionUID = -4671052335797613795L;
	public static final String STARTUP = "STARTUP"; 
	public static final String SHUTDOWN = "SHUTDOWN"; 
	public static final String DICOM_QUERY = "DICOM QUERY"; 
	public static final String DICOM_RETRIEVE = "DICOM RETRIEVE"; 
	public static final String DICOM_STORAGE = "DICOM STORAGE";  
	public static final String DISABLE_AUDIT = "DISABLE AUDIT"; 
	public static final String ENABLE_AUDIT = "ENABLE AUDIT"; 
	public static final String AUTHENTICATION_FAILURE = "AUTHENTICATION FAILURE";
	public static final String CLIENT_LOGIN = "CLIENT LOGIN";
	
	private String event = null;
	private String hostname = null;
	private String applicationName = null;
	private HashMap<String, String> eventElements = null;
	private String message = null;
	private int id = 0;
	private boolean successful = false;
	
	
	/**
	 * Constructor 
	 */
	public AuditEvent() {
		this.eventElements = new HashMap<String, String>();
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param event represents an event or action taken by a system.
	 * @param hostname represents the machine that had the event.
	 * @param appName represents the Application that had the event. 
	 */
	public AuditEvent(String event, String hostname, String appName){
		this.event = event;
		this.hostname = hostname;
		this.applicationName = appName;
		this.eventElements = new HashMap<String, String>();
	}

	/**
	 * Constructor
	 * 
	 * @param event represents an event or action taken by a system.
	 * @param hostname represents the machine that had the event.
	 * @param appName represents the Application that had the event. 
	 * @param message represents a human readable text message.
	 */
	public AuditEvent(String event, String hostname, String appName, String message){
		this.event = event;
		this.hostname = hostname;
		this.applicationName = appName;
		this.message = message;
		this.eventElements = new HashMap<String, String>();
	}

	/**
	 * Constructor
	 * 
	 * @param event represents an event or action taken by a system.
	 * @param hostname represents the machine that had the event.
	 * @param appName represents the Application that had the event. 
	 * @param message represents a human readable free text message.
	 * @param eventElements represents ancillary name/value pairs that relate to the event.
	 */
	public AuditEvent(String event, String hostname, String appName, String message, 
						HashMap<String, String> eventElements){
		this.event = event;
		this.hostname = hostname;
		this.applicationName = appName;
		this.message = message;
		this.eventElements = eventElements;
	}

	
	@Override
	/**
	 * Get the Data ID.  Not used.
	 */
	public int getId() {
		return this.id;
	}

	@Override
	/**
	 * Set the Data ID.  Not used.
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}


	/**
	 * @param event the event to set.  Use the static fields in this
	 * 		class to populate this field.
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the successful.  A TRUE means the Audit Event object was
	 * 			successfully stored in the Data Source.
	 */
	public boolean isSuccessful() {
		return successful;
	}


	/**
	 * @param successful the successful to set
	 */
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}


	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}


	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}


	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}


	/**
	 * @param applicationName the applicationName refers to the name of the
	 * 			application generating the event.  An example is the HDIG.
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}


	/**
	 * @return the eventElements
	 */
	public HashMap<String, String> getEventElements() {
		return eventElements;
	}


	/**
	 * @param eventElements the eventElements are basically name-value pairs 
	 * 			saved in a HashMap.
	 */
	public void setEventElements(HashMap<String, String> eventElements) {
		this.eventElements = eventElements;
	}


	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * @param message the message is a String to give a brief 
	 * 			description of the event. 
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
