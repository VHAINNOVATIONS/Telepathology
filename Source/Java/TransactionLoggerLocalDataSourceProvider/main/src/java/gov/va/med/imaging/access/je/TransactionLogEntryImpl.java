/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 27, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.access.je;

import gov.va.med.imaging.access.TransactionLogEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.sleepycat.persist.evolve.Conversion;
import com.sleepycat.persist.evolve.Converter;
import com.sleepycat.persist.evolve.Deleter;
//import com.sleepycat.persist.evolve.Mutations;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.EntityModel;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.raw.RawType;

import static com.sleepycat.persist.model.Relationship.*;

/**
 * A simple value object representing a transaction log entry.
 * 
 * @see http://www.oracle.com/technology/documentation/berkeley-db/je/PersistenceAPI/beginningApp.html
 *  Entity classes are classes that have a primary index, and optionally one or more secondary indices. 
 *  That is, these are the classes that you will save and retrieve directly using the DPL. 
 *  You identify an entity class using the @Entity java annotation.
 *  
 *  Persistent classes are classes used by entity classes. They do not have primary or secondary indices 
 *  used for object retrieval. Rather, they are stored or retrieved when an entity class makes direct use of them. 
 *  You identify an persistent class using the @Persistent java annotation. 
 * 
 * To declare a secondary index, we use the @SecondaryKey annotation. 
 * Note that when we do this, we must declare what sort of an index it is; that is, what is its relationship to 
 * other data in the data store.
 *  ONE_TO_ONE - This relationship indicates that the secondary key is unique to the object. 
 *  If an object is stored with a secondary key that already exists in the data store, a run time error is raised.
 *  MANY_TO_ONE - Indicates that the secondary key may be used for multiple objects in the data store. 
 *  That is, the key appears more than once, but for each stored object it can be used only once.   
 *  ONE_TO_MANY - Indicates that the secondary key might be used more than once for a given object. 
 *  Index keys themselves are assumed to be unique, but multiple instances of the index can be used per object.
 *  MANY_TO_MANY - There can be multiple keys for any given object, and for any given key there can be many related objects.  
 * 
 * @author VHAISWBECKEC
 *
 */
@Entity(version=17)
public class TransactionLogEntryImpl 
implements TransactionLogEntry,
java.io.Serializable
{
	private static final long serialVersionUID = 6L;

	@PrimaryKey(sequence="TransactionLogSequence")
	private Long sequenceNumber;

	@SecondaryKey(relate=MANY_TO_ONE)
	private String transactionId;

	@SecondaryKey(relate=MANY_TO_ONE)
	private Long startTime;
	private Long elapsedTime;
	private String patientIcn;
	private String queryType;
	private String queryFilter;
	private String commandClassName;
	private Integer itemCount;
	private Long facadeBytesSent;
	private Long facadeBytesReceived;
	private Long dataSourceBytesSent;
	private Long dataSourceBytesReceived;
	private String quality;
	private String requestingSite;
	private String originatingHost;
	private String user;
	private String urn;
	private Boolean cacheHit;
	private String errorMessage;
	private String modality;
	private String purposeOfUse;
	private String imageThroughput;
	private String datasourceProtocol;
	private String responseCode;
	private String exceptionClassName;
	private String realmSiteNumber;
	private Long timeToFirstByte;
	private String vixSoftwareVersion;
	private String respondingSite;
	private Integer dataSourceItemsReceived;
	private Boolean asynchronousCommand;
	private String commandId;
	private String parentCommandId;
	private String remoteLoginMethod;
	private String facadeImageFormatSent;
	private String facadeImageQualitySent;
	private String dataSourceImageFormatReceived;
	private String dataSourceImageQualityReceived;
	private String clientVersion;
	private String dataSourceVersion;
	private String dataSourceMethod;
	private String debugInformation;
	private String dataSourceResponseServer;
	private String threadId;
	private String vixSiteNumber;
	private String requestingVixSiteNumber;

	@SecondaryKey(relate=MANY_TO_ONE)
	private String machineName;

	public TransactionLogEntryImpl()
	{
		commandClassName = "";
		machineName = null;
		timeToFirstByte = null;
	}

	public TransactionLogEntryImpl(
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
		this.facadeImageFormatSent = facadeImageFormatSent;
		this.facadeImageQualitySent = facadeImageQualitySent;
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
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRespondingSite()
	 */
	@Override
	public String getRespondingSite() 
	{
		return respondingSite;
	}

	public Long getSequenceNumber()
	{
		return sequenceNumber;
	}

	// Need to reset to null after local write for fresh insert on remote box.
	public void setSequenceNumber(Long sequenceNumber)
	{
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getStartTime()
	 */
	public Long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Long startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getElapsedTime()
	 */
	public Long getElapsedTime()
	{
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime)
	{
		this.elapsedTime = elapsedTime;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getPatientIcn()
	 */
	public String getPatientIcn()
	{
		return patientIcn;
	}

	public void setPatientIcn(String patientIcn)
	{
		this.patientIcn = patientIcn;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQueryType()
	 */
	public String getQueryType()
	{
		return queryType;
	}

	public void setQueryType(String queryType)
	{
		this.queryType = queryType;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQueryFilter()
	 */
	public String getQueryFilter()
	{
		return queryFilter;
	}

	public void setQueryFilter(String queryFilter)
	{
		this.queryFilter = queryFilter;
	}


	/**
	 * Get the Command Class name executed.
	 * @return the Command Class name executed.
	 */
	@Override
	public String getCommandClassName ()
	{

		return commandClassName;

	} // getCommandClassName


	/**
	 * Set the Command Class name executed.
	 * @param commandClassName The Command Class name executed.
	 */
	public void setCommandClassName (String commandClassName)
	{

		this.commandClassName = commandClassName;

	} // setCommandClassName


	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getItemCount()
	 */
	public Integer getItemCount()
	{
		return itemCount;
	}

	public void setItemCount(Integer itemCount)
	{
		this.itemCount = itemCount;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesSent()
	 */
	public Long getFacadeBytesSent()
	{
		return facadeBytesSent;
	}

	public void setFacadeBytesSent(Long facadeBytesSent)
	{
		this.facadeBytesSent = facadeBytesSent;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getFacadeBytesReceived()
	 */
	public Long getFacadeBytesReceived()
	{
		return facadeBytesReceived;
	}

	public void setFacadeBytesReceived(Long facadeBytesReceived)
	{
		this.facadeBytesReceived = facadeBytesReceived;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesSent()
	 */
	public Long getDataSourceBytesSent()
	{
		return dataSourceBytesSent;
	}

	public void setDataSourceBytesSent(Long dataSourceBytesSent)
	{
		this.dataSourceBytesSent = dataSourceBytesSent;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceBytesReceived()
	 */
	public Long getDataSourceBytesReceived()
	{
		return dataSourceBytesReceived;
	}

	public void setDataSourceBytesReceived(Long dataSourceBytesReceived)
	{
		this.dataSourceBytesReceived = dataSourceBytesReceived;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getQuality()
	 */
	public String getQuality()
	{
		return quality;
	}

	public void setQuality(String quality)
	{
		this.quality = quality;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRequestingSite()
	 */
	public String getRequestingSite()
	{
		return requestingSite;
	}

	public void setRequestingSite(String requestingSite)
	{
		this.requestingSite = requestingSite;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getOriginatingHost()
	 */
	public String getOriginatingHost()
	{
		return originatingHost;
	}

	public void setOriginatingHost(String originatingHost)
	{
		this.originatingHost = originatingHost;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getUser()
	 */
	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getTransactionId()
	 */
	public String getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getUrn()
	 */
	public String getUrn()
	{
		return urn;
	}

	public void setUrn(String urn)
	{
		this.urn = urn;
	}

	public Boolean isCacheHit()
	{
		return cacheHit;
	}

	public void setCacheHit(Boolean cacheHit)
	{
		this.cacheHit = cacheHit;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getErrorMessage()
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getModality()
	 */
	public String getModality()
	{
		return modality;
	}

	public void setModality(String modality)
	{
		this.modality = modality;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getPurposeOfUse()
	 */
	public String getPurposeOfUse()
	{
		return purposeOfUse;
	}

	public void setPurposeOfUse(String purposeOfUse)
	{
		this.purposeOfUse = purposeOfUse;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getImageThroughput()
	 */
	public String getImageThroughput()
	{
		return imageThroughput;
	}

	public void setImageThroughput(String imageThroughput)
	{
		this.imageThroughput = imageThroughput;
	}

	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDatasourceProtocol()
	 */
	public String getDatasourceProtocol()
	{
		return datasourceProtocol;
	}

	public void setDatasourceProtocol(String datasourceProtocol)
	{
		this.datasourceProtocol = datasourceProtocol;
	}



	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getExceptionClassName()
	 */
	@Override
	public String getExceptionClassName() {
		return exceptionClassName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getResponseCode()
	 */
	@Override
	public String getResponseCode() {
		return responseCode;
	}	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getRealmSiteNumber()
	 */
	@Override
	public String getRealmSiteNumber() {
		return realmSiteNumber;
	}


	/**
	 * Get the machine name of this computer.
	 * @return the machine name - localhost hostname or ip address of this computer, or null if not determinable.
	 */
	@Override
	public String getMachineName ()
	{

		return machineName;

	} // getMachineName


	/**
	 * Set the machine name of this computer.
	 * @param machineName - localhost hostname or ip address of this computer.
	 */
	public void setMachineName (String machineName)
	{

		this.machineName = machineName;

	} // setMachineName


	/**
	 * Get the amount of time, in milliseconds, that it took to get the first byte back from a socket read request.
	 * @return the Long amount of time, in milliseconds, that it took to get the first byte back from a socket read request, or null if unknown.
	 */
	@Override
	public Long getTimeToFirstByte ()
	{

		return timeToFirstByte;

	} // getTimeToFirstByte


	/**
	 * Set the amount of time, in milliseconds, that it took to get the first byte back from a socket read request.
	 * @param timeToFirstByte The Long amount of time, in milliseconds, that it took to get the first byte back from a socket read request.
	 */
	public void setTimeToFirstByte (Long timeToFirstByte)
	{

		this.timeToFirstByte = timeToFirstByte;

	} // setTimeToFirstByte


	/**
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getVixSoftwareVersion()
	 */
	public String getVixSoftwareVersion()
	{
		return vixSoftwareVersion;
	}

	public void setVixSoftwareVersion(String vixSoftwareVersion)
	{
		this.vixSoftwareVersion = vixSoftwareVersion;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#getDataSourceItemsReceived()
	 */
	@Override
	public Integer getDataSourceItemsReceived() 
	{
		return dataSourceItemsReceived;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.access.TransactionLogEntry#isAsynchronousCommand()
	 */
	@Override
	public Boolean isAsynchronousCommand() {
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

	public void setDataSourceVersion(String dataSourceVersion)
	{
		this.dataSourceVersion = dataSourceVersion;
	}

	public void setDataSourceMethod(String dataSourceMethod)
	{
		this.dataSourceMethod = dataSourceMethod;
	}

	@Override
	public String getDebugInformation()
	{
		return this.debugInformation;
	}

	@Override
	public String getDataSourceResponseServer()
	{
		return this.dataSourceResponseServer;
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

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		DateFormat df = new SimpleDateFormat("ddMMMyyyy");

		sb.append(sequenceNumber);
		sb.append("[");
		sb.append(transactionId);
		sb.append("]");

		sb.append(" Start=");
		sb.append(startTime == null ? "null" : df.format(startTime));

		sb.append(" Duration=");
		sb.append(elapsedTime);

		sb.append(" PatientICN=");
		sb.append(patientIcn);

		sb.append(" QueryType=");
		sb.append(queryType);

		sb.append(" QueryFilter=");
		sb.append(queryFilter);

		sb.append(" CommandClassName=");
		sb.append(commandClassName);

		sb.append(" ItemCount=");
		sb.append(itemCount);

		sb.append(" FacadeBytesSent=");
		sb.append(getFacadeBytesSent() == null ? "0" : getFacadeBytesSent());

		sb.append(" FacadeBytesReceived=");
		sb.append(getFacadeBytesReceived() == null ? "0" : getFacadeBytesReceived());

		sb.append(" DataSourceBytesSent=");
		sb.append(getDataSourceBytesSent() == null ? "0" : getDataSourceBytesSent());

		sb.append(" DataSourceBytesReceived=");
		sb.append(getDataSourceBytesReceived() == null ? "0" : getDataSourceBytesReceived());

		sb.append(" Quality=");
		sb.append(quality);

		sb.append(" MachineName=");
		sb.append(getMachineName() == null ? "null" : getMachineName());

		sb.append(" RequestingSite=");
		sb.append(requestingSite);

		sb.append(" OriginatingHost=");
		sb.append(originatingHost);

		sb.append(" User=");
		sb.append(user);

		sb.append(" URN=");
		sb.append(urn);

		sb.append(" CacheHit=");
		sb.append(cacheHit);

		sb.append(" ErrorMessage=");
		sb.append(errorMessage);

		sb.append(" Modality=");
		sb.append(modality);

		sb.append(" PurposeOfUse=");
		sb.append(purposeOfUse);

		sb.append(" ImageThroughput=");
		sb.append(imageThroughput);

		sb.append(" DatasourceProtocol=");
		sb.append(datasourceProtocol);

		sb.append(" ResponseCode=");
		sb.append(responseCode);

		sb.append(" ExceptionClassName=");
		sb.append(exceptionClassName);

		sb.append(" RealmSiteNumber=");
		sb.append(realmSiteNumber);

		sb.append(" TimeToFirstByte=");
		sb.append(timeToFirstByte == null ? "null" : timeToFirstByte);

		sb.append(" VixSoftwareVersion=");
		sb.append(vixSoftwareVersion);

		sb.append(" RespondingSite=");
		sb.append(respondingSite == null ? "null" : respondingSite);
		
		sb.append(" RemoteLoginMethod=");
		sb.append(remoteLoginMethod == null ? "null" : remoteLoginMethod);
		
		sb.append(" FacadeImageFormatSent=");
		sb.append(facadeImageFormatSent == null ? "null" : facadeImageFormatSent);
		
		sb.append(" FacadeImageQualitySent=");
		sb.append(facadeImageQualitySent == null ? "null" : facadeImageQualitySent);
		
		sb.append(" DataSourceImageQualityReceived=");
		sb.append(dataSourceImageQualityReceived == null ? "null" : dataSourceImageQualityReceived);
		
		sb.append(" DataSourceImageFormatReceived=");
		sb.append(dataSourceImageFormatReceived == null ? "null" : dataSourceImageFormatReceived);
		
		sb.append(" ClientVersion=");
		sb.append(clientVersion == null ? "null" : clientVersion);
		
		sb.append(" DataSourceMethod=");
		sb.append(dataSourceMethod == null ? "null" : dataSourceMethod);
		
		sb.append(" DataSourceVersion=");
		sb.append(dataSourceVersion == null ? "null" : dataSourceVersion);
		
		sb.append(" DebugInformation=");
		sb.append(debugInformation == null ? "null" : debugInformation);
		
		sb.append(" DataSourceResponseServer=");
		sb.append(dataSourceResponseServer == null ? "null" : dataSourceResponseServer);
		
		sb.append(" ThreadId=");
		sb.append(threadId == null ? "null" : threadId);
		
		sb.append(" VixSiteNumber=");
		sb.append(vixSiteNumber == null ? "null" : vixSiteNumber);
		
		sb.append(" RequestingVixSiteNumber=");
		sb.append(requestingVixSiteNumber == null ? "null" : requestingVixSiteNumber);

		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TransactionLogEntryImpl other = (TransactionLogEntryImpl) obj;
		if (transactionId == null)
		{
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		return true;
	}

	public static Converter[] getConverters()
	{
		return null;
		//return new Converter[]
		                       //{
		//	new Converter(TransactionLogEntryImpl.class.getName(), 0, new UpgradeVersion0Conversion())
		//};
	}

	public static Deleter[] getDeleters()
	{
		return new Deleter[]
		                   {
				new Deleter (TransactionLogEntryImpl.class.getName (), 0, "bytesTransferred"),
				new Deleter (TransactionLogEntryImpl.class.getName (), 1, "bytesTransferred"),
				new Deleter (TransactionLogEntryImpl.class.getName (), 2, "bytesTransferred"),
				new Deleter (TransactionLogEntryImpl.class.getName (), 3, "bytesTransferred")
		                   };
	}

	/**
	 * Avoiding any state in a Conversion class and assure all non-transient fields
	 * are Serializable
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	static class UpgradeVersion0Conversion
	implements Conversion
	{
		// NOTE: this must be updated if changes are made to this class
		private static final long serialVersionUID = 1L;
		private transient EntityModel entityModel;
		private transient RawType version0Type;
		private transient RawType currentType;

		/* (non-Javadoc)
		 * @see com.sleepycat.persist.evolve.Conversion#convert(java.lang.Object)
		 */
		@Override
		public Object convert(Object arg0)
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see com.sleepycat.persist.evolve.Conversion#initialize(com.sleepycat.persist.model.EntityModel)
		 */
		@Override
		public void initialize(EntityModel entityModel)
		{
			this.entityModel = entityModel;
			currentType = entityModel.getRawType(TransactionLogEntryImpl.class.getName());
		}

		@Override
		public boolean equals(Object obj) 
		{
			return obj instanceof UpgradeVersion0Conversion;
		}
	}
}
