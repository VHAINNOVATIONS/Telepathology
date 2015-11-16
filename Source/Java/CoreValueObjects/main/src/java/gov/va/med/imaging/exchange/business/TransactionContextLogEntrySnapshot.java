/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 19, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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

import gov.va.med.imaging.access.TransactionLogEntry;
import gov.va.med.imaging.transactioncontext.TransactionContext;

/**
 * Business object record which represents a snapshot of the transaction context 
 * which will be used for logging.
 * 
 * @author vhaiswwerfej
 *
 */
public class TransactionContextLogEntrySnapshot 
implements TransactionLogEntry 
{
	private final String transactionId;
	private final String commandClassName;
	private final Long startTime;
	private final Long elapsedTime;
	private final String patientIcn;
	private final String queryType;
	private final String queryFilter;
	private final Integer itemCount;
	private final Long facadeBytesSent;
	private final Long facadeBytesReceived;
	private final Long dataSourceBytesSent;
	private final Long dataSourceBytesReceived;
	private final String quality;
	private final String requestingSite;
	private final String originatingHost;
	private final String user;
	private final String urn;
	private final Boolean cacheHit;
	private final String errorMessage;
	private final String modality;
	private final String purposeOfUse;
	private final String datasourceProtocol;
	private final String responseCode;
	private final String exceptionClassName;
	private final String realmSiteNumber;
	private final Long timeToFirstByte;
	private final String vixSoftwareVersion;
	private final String respondingSite;
	private final String machineName;
	private final Integer dataSourceItemsReceived;
	private final Boolean asynchronousCommand;	
	private final String commandId;
	private final String parentCommandId;
	private final String remoteLoginMethod;
	private final String facadeImageFormatSent;
	private final String facadeImageQualitySent;
	private final String dataSourceImageFormatReceived;
	private final String dataSourceImageQualityReceived;
	private final String clientVersion;
	private final String dataSourceVersion;
	private final String dataSourceMethod;
	private final String debugInformation;
	private final String dataSourceResponseServer;
	private final String threadId;
	private final String vixSiteNumber;
	private final String requestingVixSiteNumber;
	
	public TransactionContextLogEntrySnapshot(TransactionContext context)
	{
		super();
		
		Long transactionStartTime = context.getStartTime ();
		if(transactionStartTime == null)
			transactionStartTime = new Long(System.currentTimeMillis());
		Long transactionExecutionTime = transactionStartTime == 0L ? 
				0L : System.currentTimeMillis () - transactionStartTime;

		String siteNumber = context.getLoggerSiteNumber ();
		String requestType = context.getRequestType () + ": " + siteNumber + " <- " + context.getServicedSource ();
		this.startTime = transactionStartTime;
		this.elapsedTime = transactionExecutionTime;
		this.patientIcn = context.getPatientID();
		this.queryType = requestType;
		this.queryFilter = context.getQueryFilter();
		this.commandClassName = context.getCommandClassName();
		this.itemCount = context.getEntriesReturned();
		this.facadeBytesSent = context.getFacadeBytesSent();
		this.facadeBytesReceived = context.getFacadeBytesReceived();
		this.dataSourceBytesSent = context.getDataSourceBytesSent();
		this.dataSourceBytesReceived = context.getDataSourceBytesReceived();
		this.quality = context.getQuality();
		this.machineName = context.getMachineName();
		this.requestingSite = siteNumber;
		this.originatingHost = context.getOriginatingAddress();
		this.user = context.getLoggerFullName();
		this.transactionId = context.getTransactionId();
		this.urn = context.getUrn ();
		this.cacheHit = context.isItemCached();
		this.errorMessage = context.getErrorMessage();
		this.modality = context.getModality();
		this.purposeOfUse = context.getPurposeOfUse();
		this.datasourceProtocol = context.getDatasourceProtocol();
		this.responseCode = context.getResponseCode();
		this.exceptionClassName = context.getExceptionClassName();
		this.realmSiteNumber = context.getRealm();
		this.timeToFirstByte = context.getTimeToFirstByte();
		this.vixSoftwareVersion = context.getVixSoftwareVersion();
		this.respondingSite = context.getServicedSource();
		this.dataSourceItemsReceived = context.getDataSourceEntriesReturned();
		this.asynchronousCommand = context.isAsynchronousCommand();
		this.commandId = context.getCommandId();
		this.parentCommandId = context.getParentCommandId();
		this.remoteLoginMethod = context.getLoginMethod();
		this.facadeImageFormatSent = context.getFacadeImageFormatSent();
		this.facadeImageQualitySent = context.getFacadeImageQualitySent();
		this.dataSourceImageFormatReceived = context.getDataSourceImageFormatReceived();
		this.dataSourceImageQualityReceived = context.getDataSourceImageQualityReceived();
		this.clientVersion = context.getClientVersion();
		this.dataSourceVersion = context.getDataSourceVersion();
		this.dataSourceMethod = context.getDataSourceMethod();
		
		// JMW 3/4/2013 P118 debug information and additional debug information should be merged together
		String additionalDebugInformation = context.getAdditionalDebugInformation();
		String debugInformation = context.getDebugInformation();
		// if the debug information is null, just set to additional debug info (which might also be null)
		if(debugInformation == null)
			debugInformation = additionalDebugInformation;
		else
		{
			// debug information has value, check for additional debug info and merge together
			if(additionalDebugInformation != null)
				debugInformation = debugInformation + TransactionContext.debugInformationDelimiter + additionalDebugInformation;
		}
		
		this.debugInformation = debugInformation; 
		this.dataSourceResponseServer = context.getDataSourceResponseServer();
		this.threadId = context.getThreadId();
		this.vixSiteNumber = context.getVixSiteNumber();
		this.requestingVixSiteNumber = context.getRequestingVixSiteNumber();
	}
	
	public TransactionContextLogEntrySnapshot(
			Long startTime, Long elapsedTime, 
			String patientIcn, 
			String queryType, String queryFilter, 
			String commandClassName,
			Integer itemCount,
			Long facadeBytesSent,
			Long facadeBytesReceived,
			Long dataSourceBytesSent,
			Long dataSourceBytesReceived,
			String quality, 
			String machineName,
			String requestingSite, String originatingHost, String user, 
			String transactionId, 
			String urn, 
			Boolean cacheHit,
			String errorMessage, 
			String modality, 
			String purposeOfUse, 
			String datasourceProtocol, 
			String responseCode, 
			String exceptionClassName,
			String realmSiteNumber,
			Long timeToFirstByte,
			String vixSoftwareVersion,
			String respondingSite,
			Integer dataSourceItemsReceived,
            Boolean asynchronousCommand,
            String commandId,
            String parentCommandId,
            String remoteLoginMethod,
            String facadeImageFormatSent,
            String facadeImageQualitySent,
            String dataSourceImageFormatReceived,
            String dataSourceImageQualityReceived,
            String clientVersion,
            String dataSourceVersion,
            String dataSourceMethod,
            String debugInformation,
            String dataSourceResponseServer,
            String threadId,
            String vixSiteNumber,
            String requestingVixSiteNumber)
	{
		super();
		this.startTime = startTime;
		this.elapsedTime = elapsedTime;
		this.patientIcn = patientIcn;
		this.queryType = queryType;
		this.queryFilter = queryFilter;
		this.commandClassName = commandClassName;
		this.itemCount = itemCount;
		this.facadeBytesSent = facadeBytesSent;
		this.facadeBytesReceived = facadeBytesReceived;
		this.dataSourceBytesSent = dataSourceBytesSent;
		this.dataSourceBytesReceived = dataSourceBytesReceived;
		this.quality = quality;
		this.machineName = machineName;
		this.requestingSite = requestingSite;
		this.originatingHost = originatingHost;
		this.user = user;
		this.transactionId = transactionId;
		this.urn = urn;
		this.cacheHit = cacheHit;
		this.errorMessage = errorMessage;
		this.modality = modality;
		this.purposeOfUse = purposeOfUse;
		this.datasourceProtocol = datasourceProtocol;
		this.responseCode = responseCode;
		this.exceptionClassName = exceptionClassName;
		this.realmSiteNumber = realmSiteNumber;
		this.timeToFirstByte = timeToFirstByte;
		this.vixSoftwareVersion = vixSoftwareVersion;
		this.respondingSite = respondingSite;
		this.dataSourceItemsReceived = dataSourceItemsReceived;
		this.asynchronousCommand = asynchronousCommand;
		this.commandId = commandId;
		this.parentCommandId = parentCommandId;
		this.remoteLoginMethod = remoteLoginMethod;
		this.facadeImageQualitySent = facadeImageQualitySent;
		this.facadeImageFormatSent = facadeImageFormatSent;
		this.dataSourceImageFormatReceived = dataSourceImageFormatReceived;
		this.dataSourceImageQualityReceived = dataSourceImageQualityReceived;
		this.clientVersion = clientVersion;
		this.dataSourceVersion = dataSourceVersion;
		this.dataSourceMethod = dataSourceMethod;
		this.debugInformation = debugInformation;
		this.dataSourceResponseServer = dataSourceResponseServer;
		this.threadId = threadId;
		this.vixSiteNumber = vixSiteNumber;
		this.requestingVixSiteNumber = requestingVixSiteNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getCommandClassName()
	 */
	@Override
	public String getCommandClassName() {
		return commandClassName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesReceived()
	 */
	@Override
	public Long getDataSourceBytesReceived() {
		return dataSourceBytesReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesSent()
	 */
	@Override
	public Long getDataSourceBytesSent() {
		return dataSourceBytesSent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceItemsReceived()
	 */
	@Override
	public Integer getDataSourceItemsReceived() {
		return dataSourceItemsReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDatasourceProtocol()
	 */
	@Override
	public String getDatasourceProtocol() {
		return datasourceProtocol;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getElapsedTime()
	 */
	@Override
	public Long getElapsedTime() {
		return elapsedTime;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getExceptionClassName()
	 */
	@Override
	public String getExceptionClassName() {
		return exceptionClassName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesReceived()
	 */
	@Override
	public Long getFacadeBytesReceived() {
		return facadeBytesReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesSent()
	 */
	@Override
	public Long getFacadeBytesSent() {
		return facadeBytesSent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getItemCount()
	 */
	@Override
	public Integer getItemCount() {
		return itemCount;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getMachineName()
	 */
	@Override
	public String getMachineName() {
		return machineName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getModality()
	 */
	@Override
	public String getModality() {
		return modality;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getOriginatingHost()
	 */
	@Override
	public String getOriginatingHost() {
		return originatingHost;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getPatientIcn()
	 */
	@Override
	public String getPatientIcn() {
		return patientIcn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getPurposeOfUse()
	 */
	@Override
	public String getPurposeOfUse() {
		return purposeOfUse;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQuality()
	 */
	@Override
	public String getQuality() {
		return quality;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQueryFilter()
	 */
	@Override
	public String getQueryFilter() {
		return queryFilter;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQueryType()
	 */
	@Override
	public String getQueryType() {
		return queryType;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRealmSiteNumber()
	 */
	@Override
	public String getRealmSiteNumber() {
		return realmSiteNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRequestingSite()
	 */
	@Override
	public String getRequestingSite() {
		return requestingSite;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRespondingSite()
	 */
	@Override
	public String getRespondingSite() {
		return respondingSite;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getResponseCode()
	 */
	@Override
	public String getResponseCode() {
		return responseCode;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getStartTime()
	 */
	@Override
	public Long getStartTime() {
		return startTime;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getTimeToFirstByte()
	 */
	@Override
	public Long getTimeToFirstByte() {
		return timeToFirstByte;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getTransactionId()
	 */
	@Override
	public String getTransactionId() {
		return transactionId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getUrn()
	 */
	@Override
	public String getUrn() {
		return urn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getUser()
	 */
	@Override
	public String getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getVixSoftwareVersion()
	 */
	@Override
	public String getVixSoftwareVersion() {
		return vixSoftwareVersion;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#isCacheHit()
	 */
	@Override
	public Boolean isCacheHit() {
		return cacheHit;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#isAsynchronousCommand()
	 */
	@Override
	public Boolean isAsynchronousCommand() 
	{
		return asynchronousCommand;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getCommandId()
	 */
	@Override
	public String getCommandId() {
		return commandId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getParentCommandId()
	 */
	@Override
	public String getParentCommandId() {
		return parentCommandId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRemoteLoginMethod()
	 */
	@Override
	public String getRemoteLoginMethod() {
		return remoteLoginMethod;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeImageFormatSent()
	 */
	@Override
	public String getFacadeImageFormatSent()
	{
		return facadeImageFormatSent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeImageQualitySent()
	 */
	@Override
	public String getFacadeImageQualitySent()
	{
		return facadeImageQualitySent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceImageFormatReceived()
	 */
	@Override
	public String getDataSourceImageFormatReceived()
	{
		return dataSourceImageFormatReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceImageQualityReceived()
	 */
	@Override
	public String getDataSourceImageQualityReceived()
	{
		return dataSourceImageQualityReceived;
	}

	@Override
	public String getClientVersion()
	{
		return clientVersion;
	}

	@Override
	public String getDataSourceMethod()
	{
		return dataSourceMethod;
	}

	@Override
	public String getDataSourceVersion()
	{
		return dataSourceVersion;
	}

	@Override
	public String getDebugInformation()
	{
		return debugInformation;
	}

	@Override
	public String getDataSourceResponseServer()
	{
		return dataSourceResponseServer;
	}

	@Override
	public String getThreadId()
	{
		return threadId;
	}

	@Override
	public String getVixSiteNumber()
	{
		return vixSiteNumber;
	}

	@Override
	public String getRequestingVixSiteNumber()
	{
		return requestingVixSiteNumber;
	}

}
