package gov.va.med.imaging.access;

public interface TransactionLogEntry
{

	public abstract Long getStartTime();

	public abstract Long getElapsedTime();

	public abstract String getPatientIcn();

	public abstract String getQueryType();

	public abstract String getQueryFilter();

	public abstract String getCommandClassName();

	public abstract Integer getItemCount();

	public abstract Long getFacadeBytesSent();
	
	public abstract Long getFacadeBytesReceived();
	
	public abstract Long getDataSourceBytesSent();
	
	public abstract Long getDataSourceBytesReceived();
	
	public abstract String getQuality();

	public abstract String getMachineName();

	public abstract String getRequestingSite();

	public abstract String getOriginatingHost();

	public abstract String getUser();

	public abstract String getTransactionId();

	public abstract String getUrn();

	public abstract String getErrorMessage();

	public abstract String getModality();

	public abstract String getPurposeOfUse();

	public abstract String getDatasourceProtocol();
	
	public abstract Boolean isCacheHit();
	
	public abstract String getResponseCode();
	
	public abstract String getExceptionClassName();
	
	public abstract String getRealmSiteNumber();

	public abstract Long getTimeToFirstByte();

	public abstract String getVixSoftwareVersion();
	
	public abstract String getRespondingSite();
	
	public abstract Integer getDataSourceItemsReceived();
	
	public abstract Boolean isAsynchronousCommand();
   
	public abstract String getCommandId();
	
	public abstract String getParentCommandId();
	
	public abstract String getRemoteLoginMethod();
	
	public abstract String getFacadeImageFormatSent();
	
	public abstract String getFacadeImageQualitySent();
	
	public abstract String getDataSourceImageFormatReceived();
	
	public abstract String getDataSourceImageQualityReceived();
	
	public abstract String getClientVersion();
	
	public abstract String getDataSourceMethod();
	
	public abstract String getDataSourceVersion();
	
	public abstract String getDebugInformation();
	
	public abstract String getDataSourceResponseServer();
	
	public abstract String getThreadId();
	
	public abstract String getVixSiteNumber();
	
	public abstract String getRequestingVixSiteNumber();
   
}