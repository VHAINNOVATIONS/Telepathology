package gov.va.med.imaging.veins;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorTypeNotificationConfiguration implements PersistentEntity {
	private int id;
	private int placeId;
	private String errorType;
	private Date lastEmailSentDateTime;
	private int executionDelayInSeconds;
	private int evaluationPeriodInSeconds;
	private int maxErrors;
	private Date lastExecutionDateTime;
	private Date dailyExecutionTime;
	private List<String> emailAddresses;

	public ErrorTypeNotificationConfiguration() {
		emailAddresses = new ArrayList<String>();
	}

	public Date getDailyExecutionTime() {
		return dailyExecutionTime;
	}

	public List<String> getEmailAddresses() {
		return emailAddresses;
	}

	public String getErrorType() {
		return errorType;
	}

	public int getEvaluationPeriodInSeconds() {
		return evaluationPeriodInSeconds;
	}

	public int getExecutionDelayInSeconds() {
		return executionDelayInSeconds;
	}

	public int getId() {
		return id;
	}

	public Date getLastEmailSentDateTime() {
		return lastEmailSentDateTime;
	}

	public Date getLastExecutionDateTime() {
		return lastExecutionDateTime;
	}

	public int getMaxErrors() {
		return maxErrors;
	}

	public int getPlaceId() {
		return placeId;
	}

	public void setDailyExecutionTime(Date dailyExecutionTime) {
		this.dailyExecutionTime = dailyExecutionTime;
	}

	public void setEmailAddresses(List<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public void setEvaluationPeriodInSeconds(int evaluationPeriodInSeconds) {
		this.evaluationPeriodInSeconds = evaluationPeriodInSeconds;
	}

	public void setExecutionDelayInSeconds(int executionDelayInSeconds) {
		this.executionDelayInSeconds = executionDelayInSeconds;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastEmailSentDateTime(Date lastEmailSentDateTime) {
		this.lastEmailSentDateTime = lastEmailSentDateTime;
	}

	public void setLastExecutionDateTime(Date lastExecutionDateTime) {
		this.lastExecutionDateTime = lastExecutionDateTime;
	}

	public void setMaxErrors(int maxErrors) {
		this.maxErrors = maxErrors;
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}
}
