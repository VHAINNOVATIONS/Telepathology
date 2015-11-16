package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.BeanUtils;
import gov.va.med.imaging.BusinessKey;

public class DurableQueue implements PersistentEntity {
	private int id;
	private String name;
	private String type;
	private boolean isActive;
	private int numRetries;
	private int retryDelayInSeconds;
	private int triggerDelayInSeconds; 

	public DurableQueue(){
		
	}
	
	public DurableQueue(int id, String name, String type, boolean isActive) {
		super();
		this.setId(id);
		this.setName(name);
		this.setType(type);
		this.setActive(isActive);
	}

	@Override
	public boolean equals(Object obj) {
		return BeanUtils.equals(this, obj);
	}

	@BusinessKey
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getNumRetries() {
		return numRetries;
	}

	public int getRetryDelayInSeconds() {
		return retryDelayInSeconds;
	}

	public int getTriggerDelayInSeconds() {
		return triggerDelayInSeconds;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return BeanUtils.hashCode(this);
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumRetries(int numRetries) {
		this.numRetries = numRetries;
	}

	public void setRetryDelayInSeconds(int retryDelayInSeconds) {
		this.retryDelayInSeconds = retryDelayInSeconds;
	}

	public void setTriggerDelayInSeconds(int triggerDelayInSeconds) {
		this.triggerDelayInSeconds = triggerDelayInSeconds;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
