/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.GUID;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * A mock implementation of TransactionContext to be used in test cases
 * that need a valid transaction context.
 * 
 * @author vhaiswbeckec
 *
 */
public class MockTransactionContext
implements TransactionContext
{
	private String accessCode = "access";
	private Long facadeBytesSent = new Long(1);
	private Long facadeBytesReceived = new Long(1);
	private Long dataSourceBytesSent = new Long(1);
	private Long dataSourceBytesReceived = new Long(1);
	private String childRequestType = "childRequestType";
	private String credentialsType = "credentialType";
	private String datasourceProtocol = "datasourceProtocol";
	private String displayIdentity = "displayIdentity";
	private Long duration = new Long(1);
	private String duz = "duz";
	private Integer entriesReturned = new Integer(1);
	private String errorMessage = "errorMessage";
	private String exceptionClassName = "exceptionClassName";
	private String fullName = "fullName";
	private String loggerDuz = "loggerDuz";
	private String loggerFullName = "loggerFullName";
	private String loggerSiteName = "loggerSiteName";
	private String loggerSiteNumber = "loggerSiteNumber";
	private String loggerSsn = "loggerSsn";
	private String commandClassName = "commandClassName";
	private String machineName = "machineName";
	private Long timeToFirstByte = new Long(1);
	private String modality = "modality";
	private String originatingAddress = "originatingAddress";
	private String patientID = "patientId";
	private String overrideProtocol = "overrideProtocol";
	private String purposeOfUse = "purposeOfUse";
	private String quality = "quality";
	private String queryFilter = "queryFilter";
	private String realm = "realm";
	private String requestType = "requestType";
	private String requestingSource = "requestingSource";
	private String responseCode = "responseCode";
	private String securityHashCode = "securityHashCode";
	private String servicedSource = "servicedSource";
	private String siteName = "siteName";
	private String siteNumber = "siteNumber";
	private String ssn = "ssn";
	private Long startTime = new Long(System.currentTimeMillis());
	private String transactionId = (new GUID()).toString();
	private String urn = "urn";
	private String verifyCode = "verifyCode";
	private Boolean authenticatedByDelegate = Boolean.FALSE;
	private Boolean authenticatedByVista = Boolean.FALSE;
	private Boolean clientPrincipal = Boolean.FALSE;
	private Boolean itemCached = Boolean.FALSE;
    private String name = "name";
    private String vixSoftwareVersion = "v1.0";
    private String imagingSecurityContextType = "imagingSecurityContextType";
    private Integer dataSourceEntriesReturned = new Integer(0);
    private Boolean asynchronousCommand = Boolean.FALSE;
	private String parentCommandId = null;
	private String commandId = (new GUID()).toString();
	private String brokerSecurityToken = "brokerSecurityToken";
	private Boolean tokenLocallyGenerated = Boolean.FALSE;
	private String brokerSecurityApplicationName = "brokerSecurityApplicationName";	
	private String cacheLocationId = "cacheLocationId";
	private String userDivision = "userDivision";
	private String remoteLoginMethod = "remoteLoginMethod";
	private String facadeImageFormatSent = "facadeImageFormatSent";
	private String facadeImageQualitySent = "facadeImageQualitySent";
	private String dataSourceImageFormatReceived = "dataSourceImageFormatReceived";
	private String dataSourceImageQualityReceived = "dataSourceImageQualityReceived";
	private Boolean haims1Client = Boolean.TRUE;
	private String clientVersion = "clientVersion";
	private String dataSourceVersion = "dataSourceVersion";
	private String dataSourceMethod= "dataSourceMethod";
	private String debugInformation = "debugInformation";
	private RoutingToken overrideRoutingToken;
	private String dataSourceResponseServer = "dataSourceResponseServer";
	private String threadId = "threadId";
	private String vixSiteNumber = "vixSiteNumber";
	private String requestingVixSiteNumber = "requestingVixSiteNumber";
	private Long sessionIndex = -1l;
	private Boolean allowAddFederationCompression = true;
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#clear()
	 */
	@Override
	public void clear()
	{
		
	}
	
	@Override
	public Integer getIdentity()
	{
		return new Integer(this.hashCode());
	}

	public String getContextDebugState()
	{
		return "MockTransactionContext state";
	}
	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getAccessCode()
	 */
	@Override
	public String getAccessCode()
	{
		return accessCode;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getFacadeBytesSent()
	 */
	@Override
	public Long getFacadeBytesSent()
	{
		return facadeBytesSent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getFacadeBytesReceived()
	 */
	@Override
	public Long getFacadeBytesReceived()
	{
		return facadeBytesReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDataSourceBytesSent()
	 */
	@Override
	public Long getDataSourceBytesSent()
	{
		return dataSourceBytesSent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDataSourceBytesReceived()
	 */
	@Override
	public Long getDataSourceBytesReceived()
	{
		return dataSourceBytesReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getChildRequestType()
	 */
	@Override
	public String getChildRequestType()
	{
		return childRequestType ;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getCredentialsType()
	 */
	@Override
	public String getCredentialsType()
	{
		return credentialsType;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDatasourceProtocol()
	 */
	@Override
	public String getDatasourceProtocol()
	{
		return datasourceProtocol;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDisplayIdentity()
	 */
	@Override
	public String getDisplayIdentity()
	{
		return displayIdentity;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDuration()
	 */
	@Override
	public Long getDuration()
	{
		return duration;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDuz()
	 */
	@Override
	public String getDuz()
	{
		return duz;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getEntriesReturned()
	 */
	@Override
	public Integer getEntriesReturned()
	{
		return entriesReturned;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getErrorMessage()
	 */
	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getExceptionClassName()
	 */
	@Override
	public String getExceptionClassName()
	{
		return exceptionClassName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getFullName()
	 */
	@Override
	public String getFullName()
	{
		return fullName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerDuz()
	 */
	@Override
	public String getLoggerDuz()
	{
		return loggerDuz;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerFullName()
	 */
	@Override
	public String getLoggerFullName()
	{
		return loggerFullName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerSiteName()
	 */
	@Override
	public String getLoggerSiteName()
	{
		return loggerSiteName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerSiteNumber()
	 */
	@Override
	public String getLoggerSiteNumber()
	{
		return loggerSiteNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerSsn()
	 */
	@Override
	public String getLoggerSsn()
	{
		return loggerSsn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getCommandClassName()
	 */
	@Override
	public String getCommandClassName()
	{
		return commandClassName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getMachineName()
	 */
	@Override
	public String getMachineName()
	{
		return machineName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getTimeToFirstByte()
	 */
	@Override
	public Long getTimeToFirstByte ()
	{
		
		return timeToFirstByte;
		
	} // getTimeToFirstByte
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getModality()
	 */
	@Override
	public String getModality()
	{
		return modality;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getOriginatingAddress()
	 */
	@Override
	public String getOriginatingAddress()
	{
		return originatingAddress;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getPatientID()
	 */
	@Override
	public String getPatientID()
	{
		return patientID;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getProtocolOverride()
	 */
	@Override
	public String getOverrideProtocol()
	{
		return overrideProtocol;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getPurposeOfUse()
	 */
	@Override
	public String getPurposeOfUse()
	{
		return purposeOfUse;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getQuality()
	 */
	@Override
	public String getQuality()
	{
		return quality;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getQueryFilter()
	 */
	@Override
	public String getQueryFilter()
	{
		return queryFilter;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getRealm()
	 */
	@Override
	public String getRealm()
	{
		return realm;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getRequestType()
	 */
	@Override
	public String getRequestType()
	{
		return requestType;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getRequestingSource()
	 */
	@Override
	public String getRequestingSource()
	{
		return requestingSource;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getResponseCode()
	 */
	@Override
	public String getResponseCode()
	{
		return responseCode;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getSecurityHashCode()
	 */
	@Override
	public String getSecurityHashCode()
	{
		return securityHashCode;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getServicedSource()
	 */
	@Override
	public String getServicedSource()
	{
		return servicedSource;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getSiteName()
	 */
	@Override
	public String getSiteName()
	{
		return siteName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getSiteNumber()
	 */
	@Override
	public String getSiteNumber()
	{
		return siteNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getSsn()
	 */
	@Override
	public String getSsn()
	{
		return ssn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getStartTime()
	 */
	@Override
	public Long getStartTime()
	{
		return startTime;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getOverrideRoutingToken()
	 */
	@Override
	public RoutingToken getOverrideRoutingToken()
	{
		return this.overrideRoutingToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setOverrideProtocol(java.lang.String)
	 */
	@Override
	public void setOverrideProtocol(String preferredProtocols)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getTransactionId()
	 */
	@Override
	public String getTransactionId()
	{
		return transactionId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getUrn()
	 */
	@Override
	public String getUrn()
	{
		return urn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getVerifyCode()
	 */
	@Override
	public String getVerifyCode()
	{
		return verifyCode;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isAuthenticatedByDelegate()
	 */
	@Override
	public Boolean isAuthenticatedByDelegate()
	{
		return authenticatedByDelegate;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isAuthenticatedByVista()
	 */
	@Override
	public Boolean isAuthenticatedByVista() 
	{
		return authenticatedByVista;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isClientPrincipal()
	 */
	@Override
	public Boolean isClientPrincipal()
	{
		return clientPrincipal;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isItemCached()
	 */
	@Override
	public Boolean isItemCached()
	{
		return itemCached;
	}

	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName()
	{
		return name ;
	}

   /* (non-Javadoc)
    * @see gov.va.med.imaging.transactioncontext.TransactionContext#getVixSoftwareVersion()
    */
   @Override
   public String getVixSoftwareVersion()
   {
      return vixSoftwareVersion;
   }

	/**
	 * @param accessCode the accessCode to set
	 */
	public void setAccessCode(String accessCode)
	{
		this.accessCode = accessCode;
	}

	/**
	 * @param facadeBytesSent the facadeBytesSent to set
	 */
	public void setFacadeBytesSent(Long facadeBytesSent)
	{
		this.facadeBytesSent = facadeBytesSent;
	}

	/**
	 * @param facadeBytesReceived the facadeBytesReceived to set
	 */
	public void setFacadeBytesReceived(Long facadeBytesReceived)
	{
		this.facadeBytesReceived = facadeBytesReceived;
	}

	/**
	 * @param dataSourceBytesSent the dataSourceBytesSent to set
	 */
	public void setDataSourceBytesSent(Long dataSource)
	{
		this.dataSourceBytesSent = dataSource;
	}

	/**
	 * @param dataSourceBytesReceived the dataSourceBytesReceived to set
	 */
	public void setDataSourceBytesReceived(Long dataSourceBytesReceived)
	{
		this.dataSourceBytesReceived = dataSourceBytesReceived;
	}

	/**
	 * @param childRequestType the childRequestType to set
	 */
	public void setChildRequestType(String childRequestType)
	{
		this.childRequestType = childRequestType;
	}

	/**
	 * @param credentialsType the credentialsType to set
	 */
	public void setCredentialsType(String credentialsType)
	{
		this.credentialsType = credentialsType;
	}

	/**
	 * @param datasourceProtocol the datasourceProtocol to set
	 */
	public void setDatasourceProtocol(String datasourceProtocol)
	{
		this.datasourceProtocol = datasourceProtocol;
	}

	/**
	 * @param displayIdentity the displayIdentity to set
	 */
	public void setDisplayIdentity(String displayIdentity)
	{
		this.displayIdentity = displayIdentity;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Long duration)
	{
		this.duration = duration;
	}

	/**
	 * @param duz the duz to set
	 */
	public void setDuz(String duz)
	{
		this.duz = duz;
	}

	/**
	 * @param entriesReturned the entriesReturned to set
	 */
	public void setEntriesReturned(Integer entriesReturned)
	{
		this.entriesReturned = entriesReturned;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	/**
	 * @param exceptionClassName the exceptionClassName to set
	 */
	public void setExceptionClassName(String exceptionClassName)
	{
		this.exceptionClassName = exceptionClassName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	/**
	 * @param loggerDuz the loggerDuz to set
	 */
	public void setLoggerDuz(String loggerDuz)
	{
		this.loggerDuz = loggerDuz;
	}

	/**
	 * @param loggerFullName the loggerFullName to set
	 */
	public void setLoggerFullName(String loggerFullName)
	{
		this.loggerFullName = loggerFullName;
	}

	/**
	 * @param loggerSiteName the loggerSiteName to set
	 */
	public void setLoggerSiteName(String loggerSiteName)
	{
		this.loggerSiteName = loggerSiteName;
	}

	/**
	 * @param loggerSiteNumber the loggerSiteNumber to set
	 */
	public void setLoggerSiteNumber(String loggerSiteNumber)
	{
		this.loggerSiteNumber = loggerSiteNumber;
	}

	/**
	 * @param loggerSsn the loggerSsn to set
	 */
	public void setLoggerSsn(String loggerSsn)
	{
		this.loggerSsn = loggerSsn;
	}

	/**
	 * @param commandClassName the commandClassName to set
	 */
	public void setCommandClassName(String commandClassName)
	{
		this.commandClassName = commandClassName;
	}

	/**
	 * @param machineName the machineName to set
	 */
	public void setMachineName(String machineName)
	{
		this.machineName = machineName;
	}

	/**
	 * @param timeToFirstByte the timeToFirstByte to set
	 */
	public void setTimeToFirstByte (Long timeToFirstByte)
	{
		
		this.timeToFirstByte = timeToFirstByte;
		
	} // setTimeToFirstByte
	
	/**
	 * @param modality the modality to set
	 */
	public void setModality(String modality)
	{
		this.modality = modality;
	}

	/**
	 * @param originatingAddress the originatingAddress to set
	 */
	public void setOriginatingAddress(String originatingAddress)
	{
		this.originatingAddress = originatingAddress;
	}

	/**
	 * @param patientID the patientID to set
	 */
	public void setPatientID(String patientID)
	{
		this.patientID = patientID;
	}

	/**
	 * @param protocolOverride the protocolOverride to set
	 */
	public void setProtocolOverride(String overrideProtocol)
	{
		this.overrideProtocol = overrideProtocol;
	}

	/**
	 * @param purposeOfUse the purposeOfUse to set
	 */
	public void setPurposeOfUse(String purposeOfUse)
	{
		this.purposeOfUse = purposeOfUse;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(String quality)
	{
		this.quality = quality;
	}

	/**
	 * @param queryFilter the queryFilter to set
	 */
	public void setQueryFilter(String queryFilter)
	{
		this.queryFilter = queryFilter;
	}

	/**
	 * @param realm the realm to set
	 */
	public void setRealm(String realm)
	{
		this.realm = realm;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(String requestType)
	{
		this.requestType = requestType;
	}

	/**
	 * @param requestingSource the requestingSource to set
	 */
	public void setRequestingSource(String requestingSource)
	{
		this.requestingSource = requestingSource;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode)
	{
		this.responseCode = responseCode;
	}

	/**
	 * @param securityHashCode the securityHashCode to set
	 */
	public void setSecurityHashCode(String securityHashCode)
	{
		this.securityHashCode = securityHashCode;
	}

	/**
	 * @param servicedSource the servicedSource to set
	 */
	public void setServicedSource(String servicedSource)
	{
		this.servicedSource = servicedSource;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	/**
	 * @param siteNumber the siteNumber to set
	 */
	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	/**
	 * @param ssn the ssn to set
	 */
	public void setSsn(String ssn)
	{
		this.ssn = ssn;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Long startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}

	/**
	 * @param urn the urn to set
	 */
	public void setUrn(String urn)
	{
		this.urn = urn;
	}

	/**
	 * @param verifyCode the verifyCode to set
	 */
	public void setVerifyCode(String verifyCode)
	{
		this.verifyCode = verifyCode;
	}

	/**
	 * @param authenticatedByDelegate the authenticatedByDelegate to set
	 */
	public void setAuthenticatedByDelegate(Boolean authenticatedByDelegate)
	{
		this.authenticatedByDelegate = authenticatedByDelegate;
	}

	/**
	 * @param clientPrincipal the clientPrincipal to set
	 */
	public void setClientPrincipal(Boolean clientPrincipal)
	{
		this.clientPrincipal = clientPrincipal;
	}

	/**
	 * @param itemCached the itemCached to set
	 */
	public void setItemCached(Boolean itemCached)
	{
		this.itemCached = itemCached;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

   /**
    * @param vixSoftwareVersion the Vix Software Version to set
    */
   public void setVixSoftwareVersion(String vixSoftwareVersion)
   {
      this.vixSoftwareVersion = vixSoftwareVersion;
   }

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getMemento()
	 */
	@Override
	public TransactionContextMemento getMemento()
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(outStream);
			out.writeObject(this);
			out.flush();
			out.close();
			
			return new TransactionContextMemento(outStream.toByteArray());
		} 
		catch (IOException x)
		{
			x.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setMemento(gov.va.med.imaging.transactioncontext.TransactionContextMemento)
	 */
	@Override
	public void setMemento(TransactionContextMemento memento)
	{
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getImagingSecurityContextType()
	 */
	@Override
	public String getImagingSecurityContextType() {
		return imagingSecurityContextType;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setImagingSecurityContextType(java.lang.String)
	 */
	@Override
	public void setImagingSecurityContextType(String imagingSecurityContextType) 
	{
		this.imagingSecurityContextType = imagingSecurityContextType;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDataSourceEntriesReturned()
	 */
	@Override
	public Integer getDataSourceEntriesReturned() {
		return dataSourceEntriesReturned;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setDataSourceEntriesReturned(java.lang.Integer)
	 */
	@Override
	public void setDataSourceEntriesReturned(Integer entriesReturned) {
		dataSourceEntriesReturned = entriesReturned;
	}

	@Override
	public String getCommandId()
	{
		return commandId;
	}

	@Override
	public String getParentCommandId()
	{
		return this.parentCommandId;
	}

	@Override
	public void setCommandId(String commandId)
	{
		this.commandId  = commandId;
	}

	@Override
	public void setParentCommandId(String parentCommandId)
	{
		this.parentCommandId = parentCommandId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isAsynchronousCommand()
	 */
	@Override
	public Boolean isAsynchronousCommand() 
	{
		return asynchronousCommand;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setAsynchronousCommand(java.lang.Boolean)
	 */
	@Override
	public void setAsynchronousCommand(Boolean asynchronousCommand) 
	{
		this.asynchronousCommand = asynchronousCommand;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getBrokerSecurityToken()
	 */
	@Override
	public String getBrokerSecurityToken() 
	{
		return brokerSecurityToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setBrokerSecurityToken(java.lang.String)
	 */
	@Override
	public void setBrokerSecurityToken(String brokerSecurityToken) 
	{
		this.brokerSecurityToken = brokerSecurityToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerBrokerSecurityToken()
	 */
	@Override
	public String getLoggerBrokerSecurityToken() 
	{
		return brokerSecurityToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isTokenLocallyGenerated()
	 */
	@Override
	public Boolean isTokenLocallyGenerated() {
		return tokenLocallyGenerated;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setTokenLocallyGenerated(java.lang.Boolean)
	 */
	@Override
	public void setTokenLocallyGenerated(Boolean value) {
		this.tokenLocallyGenerated = value;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getBrokerSecurityApplicationName()
	 */
	@Override
	public String getBrokerSecurityApplicationName() 
	{
		return this.brokerSecurityApplicationName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setBrokerSecurityApplicationName(java.lang.String)
	 */
	@Override
	public void setBrokerSecurityApplicationName(
			String brokerSecurityApplicationName) 
	{
		this.brokerSecurityApplicationName = brokerSecurityApplicationName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getCacheLocationId()
	 */
	@Override
	public String getCacheLocationId() {
		return cacheLocationId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerCacheLocationId()
	 */
	@Override
	public String getLoggerCacheLocationId() {
		return cacheLocationId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setCacheLocationId(java.lang.String)
	 */
	@Override
	public void setCacheLocationId(String cacheLocationId) 
	{
		this.cacheLocationId = cacheLocationId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getLoggerUserDivision()
	 */
	@Override
	public String getLoggerUserDivision() 
	{
		return userDivision;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getUserDivision()
	 */
	@Override
	public String getUserDivision() 
	{
		return userDivision;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setUserDivision(java.lang.String)
	 */
	@Override
	public void setUserDivision(String userDivision) {
		this.userDivision = userDivision;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getRemoteLoginMethod()
	 */
	@Override
	public String getLoginMethod() {
		return remoteLoginMethod;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setRemoteLoginMethod(java.lang.String)
	 */
	@Override
	public void setLoginMethod(String remoteLoginMethod) 
	{
		this.remoteLoginMethod = remoteLoginMethod;
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getFacadeImageFormatSent()
	 */
	@Override
	public String getFacadeImageFormatSent()
	{
		return facadeImageFormatSent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setFacadeImageFormatSent(java.lang.String)
	 */
	@Override
	public void setFacadeImageFormatSent(String facadeImageFormatSent)
	{
		this.facadeImageFormatSent = facadeImageFormatSent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getFacadeImageQualitySent()
	 */
	@Override
	public String getFacadeImageQualitySent()
	{
		return facadeImageQualitySent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setFacadeImageQualitySent(java.lang.String)
	 */
	@Override
	public void setFacadeImageQualitySent(String facadeImageQualitySent)
	{
		this.facadeImageQualitySent = facadeImageQualitySent;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDataSourceImageFormatReceived()
	 */
	@Override
	public String getDataSourceImageFormatReceived()
	{
		return dataSourceImageFormatReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getDataSourceImageQualityReceived()
	 */
	@Override
	public String getDataSourceImageQualityReceived()
	{
		return dataSourceImageQualityReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setDataSourceImageFormatReceived(java.lang.String)
	 */
	@Override
	public void setDataSourceImageFormatReceived(
		String dataSourceImageFormatRecieved)
	{
		this.dataSourceImageFormatReceived = dataSourceImageFormatRecieved;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setDataSourceImageQualityReceived(java.lang.String)
	 */
	@Override
	public void setDataSourceImageQualityReceived(
		String dataSourceImageQualityReceived)
	{
		this.dataSourceImageQualityReceived = dataSourceImageQualityReceived;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#isHaims1Client()
	 */
	@Override
	public Boolean isHaims1Client()
	{
		return haims1Client;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setHaims1Client(java.lang.Boolean)
	 */
	@Override
	public void setHaims1Client(Boolean value)
	{
		this.haims1Client  = value;
	}

	@Override
	public String getClientVersion()
	{
		return this.clientVersion;
	}

	@Override
	public void setClientVersion(String clientVersion)
	{
		this.clientVersion = clientVersion;
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
	public void setDataSourceMethod(String dataSourceMethod)
	{
		this.dataSourceMethod = dataSourceMethod;
		
	}

	@Override
	public void setDataSourceVersion(String dataSourceVersion)
	{
		this.dataSourceVersion = dataSourceVersion;
	}

	@Override
	public String getDebugInformation()
	{
		return debugInformation;
	}

	@Override
	public void setDebugInformation(String debugInformation)
	{
		this.debugInformation = debugInformation;
	}

	@Override
	public void addDebugInformation(String debugInformation)
	{
		if(this.debugInformation == null)
			this.debugInformation = debugInformation;
		else
			this.debugInformation += "\n" + debugInformation;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setOverrideRoutingToken(java.lang.String)
	 */
	@Override
	public void setOverrideRoutingToken(RoutingToken overrideRoutingToken)
	{
		this.overrideRoutingToken = overrideRoutingToken;
	}

	@Override
	public String getDataSourceResponseServer()
	{
		return dataSourceResponseServer;
	}

	@Override
	public void setDataSourceResponseServer(String dataSourceResponseServer)
	{
		this.dataSourceResponseServer = dataSourceResponseServer;
	}

	@Override
	public String getThreadId()
	{
		return threadId;
	}

	@Override
	public void setThreadId(String threadId)
	{
		this.threadId = threadId;
	}

	@Override
	public String getVixSiteNumber()
	{
		return vixSiteNumber;
	}

	@Override
	public void setVixSiteNumber(String vixSiteNumber)
	{
		this.vixSiteNumber = vixSiteNumber;
	}

	@Override
	public String getRequestingVixSiteNumber()
	{
		return requestingVixSiteNumber;
	}

	@Override
	public void setRequestingVixSiteNumber(String requestingVixSiteNumber)
	{
		this.requestingVixSiteNumber = requestingVixSiteNumber;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getVistaSessionIndex()
	 */
	@Override
	public Long getVistaSessionIndex()
	{
		return sessionIndex;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#setVistaSessionIndex(java.lang.Long)
	 */
	@Override
	public void setVistaSessionIndex(Long vistaSessionIndex)
	{
		this.sessionIndex = vistaSessionIndex;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.transactioncontext.TransactionContext#getAdditionalDebugInformation()
	 */
	@Override
	public String getAdditionalDebugInformation()
	{
		return null;
	}

	@Override
	public Boolean isAllowAddFederationCompression()
	{
		return allowAddFederationCompression;
	}

	@Override
	public void setAllowAddFederationCompression(Boolean allowAddFederationCompression)
	{
		this.allowAddFederationCompression = allowAddFederationCompression;
	}
}
