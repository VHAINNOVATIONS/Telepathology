package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DurableQueueMessage implements PersistentEntity {
	private int id;
	private DurableQueue queue;
	private int queueId;
	private String message;
	private Date enqueuedTimestamp;
	private Date minDeliveryDateTime;
	private Date expirationDateTime;
	private String messageGroupId;
	private int priority;
	// these are a hack for XStream to deserialize the xml. The should be
	// removed,
	// and XStream should be given a custom converter to convert the Vista Date
	// Format to a java Date object
	private String enqueuedVistaTimestamp;
	private String minDeliveryVistaDateTime;
	private String expirationVistaDateTime;

	public DurableQueueMessage() {

	}

	public DurableQueueMessage(DurableQueue queue, String messageGroupId,
			String message) {
		super();
		this.setQueue(queue);
		this.setMessageGroupId(messageGroupId);
		this.message = message;
	}

	public void convertMessageDates() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.hhmmss");
		if (enqueuedVistaTimestamp != null
				&& !enqueuedVistaTimestamp.equals("")) {
			try {
				this.setEnqueuedTimestamp(df.parse(enqueuedVistaTimestamp));
			} catch (ParseException pe) {
			}
		}
		if (minDeliveryVistaDateTime != null
				&& !minDeliveryVistaDateTime.equals("")) {
			try {
				this.setMinDeliveryDateTime(df.parse(minDeliveryVistaDateTime));
			} catch (ParseException pe) {
			}
		}
		if (expirationVistaDateTime != null
				&& !expirationVistaDateTime.equals("")) {
			try {
				this.setExpirationDateTime(df.parse(expirationVistaDateTime));
			} catch (ParseException pe) {
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		return BeanUtils.equals(this, obj);
	}

	public Date getEnqueuedTimestamp() {
		return enqueuedTimestamp;
	}

	public Date getExpirationDateTime() {
		return expirationDateTime;
	}

	@BusinessKey
	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public Date getMinDeliveryDateTime() {
		return minDeliveryDateTime;
	}

	public String getMessageGroupId() {
		return messageGroupId;
	}

	public int getPriority() {
		return priority;
	}

	public DurableQueue getQueue() {
		return queue;
	}

	public int getQueueId() {
		return queueId;
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	public void setEnqueuedTimestamp(Date enqueuedTimestamp) {
		this.enqueuedTimestamp = enqueuedTimestamp;
	}

	public void setExpirationDateTime(Date expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMinDeliveryDateTime(Date minDeliveryDateTime) {
		this.minDeliveryDateTime = minDeliveryDateTime;
	}

	public void setMessageGroupId(String messageGroupId) {
		this.messageGroupId = messageGroupId;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setQueue(DurableQueue queue) {
		this.queue = queue;
		if (queue != null)
			this.queueId = queue.getId();
	}

	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}
}
