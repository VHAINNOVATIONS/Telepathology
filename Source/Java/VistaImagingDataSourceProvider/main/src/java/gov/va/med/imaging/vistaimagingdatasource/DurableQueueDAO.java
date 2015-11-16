/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

public class DurableQueueDAO extends EntityDAO<DurableQueue> {

	public class GetMessagesResultContainer {
		List<DurableQueueMessage> messages;

		public List<DurableQueueMessage> getMessages() {
			return messages;
		}

		public void setMessages(List<DurableQueueMessage> messages) {
			this.messages = messages;
		}
	}

	private final static String DELIMITER = StringUtils.CARET;
	private final static String LINEDELIMITER = StringUtils.CRLF;
	private final static String LINEBREAK_STUB = "|/|"; // if you alter this, you need to process messages stored prior installing this change with the original string!
	private final static String SUCCESS = "0";
	private final static String FAILURE = "1";
	private final static String RPC_ENQUEUE_Q_MSG = "MAGVA ENQUEUE Q MSG";
	private final static String RPC_DEQUEUE_Q_MSG = "MAGVA DEQUEUE Q MSG";
	private final static String RPC_PEEK_Q_MSG = "MAGVA PEEK Q MSG";
	private final static String RPC_GET_ALL_Q = "MAGVA GET ALL QUEUES";
	private final static String RPC_GET_MESSAGE_COUNT = "MAGV ENS GET QUEUE MESSAGE CNT";
	private final static String RPC_GET_MESSAGES = "MAGV ENS GET QUEUE MESSAGES";
	private final static String RPC_MOVE_MESSAGE = "MAGVA SET QUEUE MESSAGE";
	private final static String RPC_UPDATE_QUEUE = "MAGVA SET QUEUE";
	private final static String Q_MSG_QUEUEFK = "QUEUE"; // FK removed
	private final static String Q_MSG_PRIORITY = "PRIORITY";
	private final static String Q_MSG_EARLIEST_DELIVERY_DATE = "EARLIEST DELIVERY DATE/TIME"; // / inserted
	private final static String Q_MSG_GROUP_ID = "MESSAGE GROUP ID";
	private final static String Q_MSG_PK = "PK";
	private final static String Q_MSG_START_INDEX = "START INDEX";
	private final static String Q_MSG_RECORD_COUNT = "RECORD COUNT";
	private final static String Q_PK = "PK";
	private final static String Q_NUM_RETRIES = "NUM RETRIES";
	private final static String Q_RETRY_DELAY = "RETRY DELAY IN SECONDS";
	private final static String Q_TRIGGER_DELAY = "TRIGGER DELAY IN SECONDS";
//	protected final static int MAX_QUEUE_MESSAGE_LENGTH = 5 * 1024 * 1024; // 5 MB; used as a limit in e-mail handling only

	//
	// Constructor
	//
	public DurableQueueDAO(VistaSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	public DurableQueueMessage dequeue(int queueId, String messageGroupId)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_DEQUEUE_Q_MSG);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Q_MSG_QUEUEFK, Integer.toString(queueId));
		if (messageGroupId != null)
			hm.put(Q_MSG_GROUP_ID, messageGroupId);
		vm.addParameter(VistaQuery.ARRAY, hm);
		return translateDurableQueueMessage(executeRPC(vm));
	}

	public DurableQueueMessage enqueue(DurableQueueMessage message)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_ENQUEUE_Q_MSG);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Q_MSG_QUEUEFK, Integer.toString(message.getQueueId()));
		hm.put(Q_MSG_PRIORITY, "50");
		if (message.getMinDeliveryDateTime() != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.HHmmss");
			String rpcDate = df.format(message.getMinDeliveryDateTime());
			hm.put(Q_MSG_EARLIEST_DELIVERY_DATE, rpcDate);
		}
		if ((message.getMessageGroupId() != null)
				&& !message.getMessageGroupId().equals("")) {
			hm.put(Q_MSG_GROUP_ID, message.getMessageGroupId());
		}
		String lineId;
		String storedMsg=message.getMessage();
		//Substitute line breaks with stub pattern, Note: at read out time stubs become line-breaks
		//Note: This object is put thru xstream.  when xstream is done, the line breaks are represented with "\n".
        String originalSequence = StringUtils.NEW_LINE;
		storedMsg = storedMsg.replace(originalSequence, LINEBREAK_STUB);

		// chop up message to MAX_M_STRING_LENGTH string array of name-value pairs
		String[] messageParts = StringUtils.breakString(storedMsg,
				MAX_M_STRING_LENGTH);
		for (int i = 0; i < messageParts.length; i++) {
			lineId = String.format("MAGMSG%05d", i + 1);
			hm.put(lineId, messageParts[i]);
		}
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		//String[] lineParts = result.split(DELIMITER);
		String[] lineParts = StringUtils.Split(result, DELIMITER);
		if (lineParts[0].equals(FAILURE))
			throw new MethodException(lineParts[1]);
		int id = Integer.parseInt(lineParts[2]);
		message.setId(id);
		return message;
	}

	@Override
	public VistaQuery generateFindAllQuery() {
		VistaQuery vm = new VistaQuery(RPC_GET_ALL_Q);
		return vm;
	}

	protected XStream getConfiguredXStream() {
		XStream xstream = new XStream();

		xstream.alias("QUEUEMESSAGES", GetMessagesResultContainer.class);
		xstream.addImplicitCollection(GetMessagesResultContainer.class,
				"messages");

		xstream.alias("QUEUEMESSAGE", DurableQueueMessage.class);
		xstream.aliasAttribute(DurableQueueMessage.class, "id", "PK");
		xstream.useAttributeFor(DurableQueueMessage.class, "id");

		xstream.aliasAttribute(DurableQueueMessage.class, "queueId", "QUEUE");
		xstream.useAttributeFor(DurableQueueMessage.class, "queueId");

		xstream.aliasAttribute(DurableQueueMessage.class, "priority",
				"PRIORITY");
		xstream.useAttributeFor(DurableQueueMessage.class, "priority");

		xstream.aliasAttribute(DurableQueueMessage.class, "messageGroupId",
				"MESSAGEGROUPID");
		xstream.useAttributeFor(DurableQueueMessage.class, "messageGroupId");

		xstream.aliasAttribute(DurableQueueMessage.class, "message", "MESSAGE");
		xstream.useAttributeFor(DurableQueueMessage.class, "message");

		xstream.aliasAttribute(DurableQueueMessage.class,
				"enqueuedVistaTimestamp", "ENQUEUEDDATETIME");
		xstream.useAttributeFor(DurableQueueMessage.class,
				"enqueuedVistaTimestamp");

		xstream.aliasAttribute(DurableQueueMessage.class,
				"minDeliveryVistaDateTime", "EARLIESTDELIVERYDATETIME");
		xstream.useAttributeFor(DurableQueueMessage.class,
				"minDeliveryVistaDateTime");

		xstream.aliasAttribute(DurableQueueMessage.class,
				"expirationVistaDateTime", "EXPIRATIONDATETIME");
		xstream.useAttributeFor(DurableQueueMessage.class,
				"expirationVistaDateTime");
		return xstream;
	}

	public int getMessageCount(int queueId) throws MethodException,
			ConnectionException {
		return getMessageCount(queueId, null);
	}

	public int getMessageCount(int queueId, String messageGroupId)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_GET_MESSAGE_COUNT);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Q_MSG_QUEUEFK, Integer.toString(queueId));
		if (messageGroupId != null)
			hm.put(Q_MSG_GROUP_ID, messageGroupId);
		else
			hm.put(Q_MSG_GROUP_ID, "*");
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		//String[] lineParts = result.split(DELIMITER);
		String[] lineParts = StringUtils.Split(result, DELIMITER);
		if (lineParts[0].equals(FAILURE)) {
			throw new MethodException(lineParts[1]);
		}
		return Integer.parseInt(lineParts[2]);
	}

	public List<DurableQueueMessage> getMessages(int queueId,
			String messageGroupId, int startIndex, int numRecords)
			throws MethodException, ConnectionException {
		startIndex++; // The M code uses a 1 based index
		VistaQuery vm = new VistaQuery(RPC_GET_MESSAGES);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Q_MSG_QUEUEFK, Integer.toString(queueId));
		if (messageGroupId != null)
			hm.put(Q_MSG_GROUP_ID, messageGroupId);
		else {
			hm.put(Q_MSG_GROUP_ID, "*");
		}
		hm.put(Q_MSG_START_INDEX, Integer.toString(startIndex));
		hm.put(Q_MSG_RECORD_COUNT, Integer.toString(numRecords));
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		return translateDurableQueueMessages(result);
	}

	public void moveMessage(int messageId, int targetQueueId)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_MOVE_MESSAGE);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Q_MSG_PK, Integer.toString(messageId));
		hm.put(Q_MSG_QUEUEFK, Integer.toString(targetQueueId));
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		//String[] lineParts = result.split(DELIMITER);
		String[] lineParts = StringUtils.Split(result, DELIMITER);
		if (lineParts[0].equals(FAILURE))
			throw new MethodException(lineParts[1]);
	}

	public DurableQueueMessage peek(int queueId, String messageGroupId)
			throws MethodException, ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_PEEK_Q_MSG);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Q_MSG_QUEUEFK, Integer.toString(queueId));
		if (messageGroupId != null)
			hm.put(Q_MSG_GROUP_ID, messageGroupId);
		vm.addParameter(VistaQuery.ARRAY, hm);
		return translateDurableQueueMessage(executeRPC(vm));
	}

	public DurableQueueMessage translateDurableQueueMessage(String message)
			throws MethodException {
		int firstLineBreakIndex = message.indexOf("\r\n");
		String firstLine;
		if (firstLineBreakIndex > 0)
			firstLine = message.substring(0, firstLineBreakIndex);
			//firstLine = StringUtils.MagPiece(message, LINEDELIMITER, 1);
		else
			firstLine = message;
		//String[] lineParts = firstLine.split(DELIMITER);
		String[] lineParts = StringUtils.Split(firstLine, DELIMITER);
		if (Integer.parseInt(lineParts[0]) == -1)
			throw new MethodException(message);
		DurableQueueMessage queueMessage = null;
		if (Integer.parseInt(lineParts[2]) > 0) {
			//first restore original line breaks in content
			message = message.replace(LINEBREAK_STUB, StringUtils.NEW_LINE);

			String messageContent = message.substring(firstLineBreakIndex + 2);
			messageContent = this.removeCRLFsFromMessage(messageContent);
			//String messageContent = StringUtils.MagPiece(message, LINEDELIMITER, 2);
			try {
				XStream deserializer = getConfiguredXStream();
				queueMessage = (DurableQueueMessage) deserializer.fromXML(messageContent);
			} catch (XStreamException xsX) {
				throw new MethodException(xsX.getMessage(), xsX);
			}
			queueMessage.convertMessageDates();
		}
		return queueMessage;
	}

	public List<DurableQueueMessage> translateDurableQueueMessages(
			String messageXml) throws MethodException {
		int firstLineBreakIndex = messageXml.indexOf("\r\n");
		String firstLine;
		if (firstLineBreakIndex > 0)
			firstLine = messageXml.substring(0, firstLineBreakIndex);
		else
			firstLine = messageXml;
		//String[] lineParts = firstLine.split(DELIMITER);
		String[] lineParts = StringUtils.Split(firstLine, DELIMITER);
		if (Integer.parseInt(lineParts[0]) == -1)
			throw new MethodException(messageXml);
		//first restore original line breaks in content
		messageXml = messageXml.replace(LINEBREAK_STUB, StringUtils.NEW_LINE);
		GetMessagesResultContainer messageContainer = null;
		String messageContent = messageXml.substring(firstLineBreakIndex + 2);
		messageContent = this.removeCRLFsFromMessage(messageContent);
		try {
			XStream deserializer = getConfiguredXStream();
			messageContainer = (GetMessagesResultContainer) deserializer.fromXML(messageContent);
		} catch (XStreamException xsX) {
			throw new MethodException(xsX.getMessage(), xsX);
		}
		if (messageContainer.getMessages() == null)
			messageContainer.setMessages(new ArrayList<DurableQueueMessage>());
		for (DurableQueueMessage message : messageContainer.getMessages())
			message.convertMessageDates();
		return messageContainer.getMessages();
	}

	@Override
	public List<DurableQueue> translateFindAll(String returnValue) {
		ArrayList<DurableQueue> queues = null;
		String[] results = DicomTranslatorUtility
				.createResultsArray(returnValue);
		//String[] lineParts = results[0].split(DELIMITER);
		// if (lineParts[0].equals(FAILURE))
		// throw new MethodException(lineParts[1]);

		//int numResults = Integer.parseInt(lineParts[2]);
		queues = new ArrayList<DurableQueue>();
		for (int i = 2; i < results.length; i++) {
			if (results[i] != null && results[i].trim().length() > 0) {
				queues.add(translateQueue(results[i]));
			}
		}
		return queues;
	}

	private DurableQueue translateQueue(String message) {
		DurableQueue queue;
		//String[] lineParts = message.split(DELIMITER, -1);
		String[] lineParts = StringUtils.Split(message, DELIMITER);
		queue = new DurableQueue();
		queue.setId(Integer.parseInt(lineParts[0]));
		queue.setName(lineParts[1]);
		queue.setType(lineParts[2]);
		queue.setActive(lineParts[3].equals("1") ? true : false);
		if (lineParts[4] != null && !lineParts[4].isEmpty()) {
			queue.setNumRetries(Integer.parseInt(lineParts[4]));
		} else {
			queue.setNumRetries(5);
		}
		if (lineParts[5] != null && !lineParts[5].isEmpty()) {
			queue.setRetryDelayInSeconds(Integer.parseInt(lineParts[5]));
		} else {
			queue.setRetryDelayInSeconds(1200);
		}
		if (lineParts[6] != null && !lineParts[6].isEmpty()) {
			queue.setTriggerDelayInSeconds(Integer.parseInt(lineParts[6]));
		} else {
			queue.setTriggerDelayInSeconds(1200);
		}
		return queue;
	}

	@Override
	public DurableQueue update(DurableQueue queue) throws MethodException,
			ConnectionException {
		VistaQuery vm = new VistaQuery(RPC_UPDATE_QUEUE);
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Q_PK, Integer.toString(queue.getId()));
		hm.put(Q_NUM_RETRIES, Integer.toString(queue.getNumRetries()));
		hm.put(Q_RETRY_DELAY, Integer.toString(queue.getRetryDelayInSeconds()));
		hm.put(Q_TRIGGER_DELAY, Integer.toString(queue
				.getTriggerDelayInSeconds()));
		vm.addParameter(VistaQuery.ARRAY, hm);
		String result = executeRPC(vm);
		//String[] lineParts = result.split(DELIMITER);
		String[] lineParts = StringUtils.Split(result, DELIMITER);
		if (lineParts[0].equals(FAILURE)) {
			throw new MethodException(lineParts[1]);
		}
		return queue;
	}
	
	private String removeCRLFsFromMessage(String message){
		StringBuilder result = null;
		
		//Split MESSAGE= key from rest of message. 
		String delimiter = "MESSAGE=";
		String[] parts = StringUtils.Split(message, delimiter);
		
		//Pull out the enclosing attribute tag from message.
		String subDelimiter = "</QUEUEMESSAGE >";
		String[] messageSubParts = StringUtils.Split(parts[1], subDelimiter);
		
		//Remove all \r\n from message.  This is because the M side returns \r\n separators 
		//	when merging portions of the original encapsulated message.
		messageSubParts[0] = messageSubParts[0].replace(StringUtils.CRLF, "");
		
		//Now, rebuilt the modified message so rest of the code can successfully process it.
		result = new StringBuilder();
		result.append(parts[0]);
		result.append(delimiter);
		result.append(StringUtils.CRLF);
		result.append(messageSubParts[0]);
		result.append(StringUtils.CRLF);
		result.append(subDelimiter);
		
		return result.toString();
	}
	

}