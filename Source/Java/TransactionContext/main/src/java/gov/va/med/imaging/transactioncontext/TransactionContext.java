/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

import gov.va.med.RoutingToken;
import java.security.Principal;


/**
 * This interface defines the security and transaction context properties available
 * to the ViX application code.
 * New String properties may be added by simply adding the accessor methods in this
 * interface, the dynamic proxy will take care of the rest.  New properties of other
 * types are very problematic.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface TransactionContext
extends Principal
{
	// The return value of the get accessor method of the properties listed here will be included 
	// when the getContextDebugState() method is called, in order.
	public final static String[] debugProperties = 
	{
		"realm", "transactionId", "siteNumber", "commandClassName", "servicedSource"
	};
	
	/**
	 * properties that are defined to be additional debug information and included when requesting additional information
	 */
	public final static String [] additionalDebugInformationProperties = 
	{
		"vistaSessionIndex"
	};
	
	/**
	 * delimiter used to separate entries in debug information when adding debug information entries
	 */
	public final static String debugInformationDelimiter = "\n";
	
	/**
	 * The realm, access code (user ID), credentials type and
	 * the authenticatedByDelegate flags are read-only.  Its questionalble whether
	 * the application needs access to any of these but they are here for now.
	 * The access and verify codes are always set by the authenticating realm.
	 * The realm identifies the name of the realm that the user was authenticated through.
	 * The application should make no assupmtions or decisions on the values returned
	 * from this method, the value is usefule strictly for logging. 
	 * @return
	 */
	public String getRealm();

	/**
	 * The access code is equivalent to the user id.
	 * NOTE: calling setAccessCode on a TransactionContext where isClientPrincipal() returns false
	 * will be ignored.
	 * 
	 * @return
	 */
	public void setAccessCode(String accessCode);
	public String getAccessCode();

	/**
	 * The verify code is the password.
	 * NOTE: this value may be blank or null if the credentials type is 
	 * not PASSWORD.
	 * NOTE: calling setVerifyCode on a TransactionContext where isClientPrincipal() returns false
	 * will be ignored.
	 * 
	 * @return
	 */
	public void setVerifyCode(String verifyCode);
	public String getVerifyCode();

	/**
	 * If the user authentication was done by a delegated realm (i.e. NOT by VistA)
	 * thene this flag will be "true", else "false".
	 * @return
	 */
	public Boolean isAuthenticatedByDelegate();

	/**
	 * If the user is authenticated by VistA, then this will be true. 
	 * Any other authentication method (delegate, certificate, etc) this will be false
	 * @return
	 */
	public Boolean isAuthenticatedByVista();

	/**
	 * If the backing principal is a ClientPrincipal then return true.
	 * The application may choose to prohibit some operations when this is true.
	 * @return
	 */
	public Boolean isClientPrincipal();

	/**
	 * Identifies the type of credentials provided by the user during authentication.
	 * @return
	 */
	public String getCredentialsType();

	/**
	 * The site name may be set by the authenticating realm or by application
	 * code.
	 * The getLoggerXXX version returns the application set value if available
	 * else it returns the VistA set value.
	 * @return
	 */
	public String getSiteName();
	public String getLoggerSiteName();
	public void setSiteName(String siteName);

	/**
	 * The site number may be set by the authenticating realm or by application
	 * code.
	 * The getLoggerXXX version returns the application set value if available
	 * else it returns the VistA set value.
	 * @return
	 */
	public String getSiteNumber();
	public String getLoggerSiteNumber();
	public void setSiteNumber(String siteNumber);

	/**
	 * The SSN may be set by the authenticating realm or by application
	 * code.
	 * The getLoggerXXX version returns the application set value if available
	 * else it returns the VistA set value.
	 * @return
	 */
	public String getSsn();
	public String getLoggerSsn();
	public void setSsn(String ssn);

	/**
	 * The DUZ may be set by the authenticating realm or by application
	 * code.
	 * The getLoggerXXX version returns the application set value if available
	 * else it returns the VistA set value.
	 * @return
	 */
	public String getDuz();
	public String getLoggerDuz();
	public void setDuz(String duz);

	/**
	 * The site name may be set by the authenticating realm or by application
	 * code.
	 * The getLoggerXXX version returns the application set value if available
	 * else it returns the VistA set value.
	 * @return
	 */
	public String getFullName();
	public String getLoggerFullName();
	public void setFullName(String fullName);

	public String getTransactionId();
	public void setTransactionId(String transactionID);

	public String getPurposeOfUse();
	public void setPurposeOfUse(String purposeOfUse);

	/**
	 * Return a String that includes the user and transaction identity.
	 * This is intended for display/logging and should not be used as any kind of
	 * identifier.
	 * 
	 * @return
	 */
	public String getDisplayIdentity();

	/**
	 * Clear the transaction context, including security context.
	 * Additional calls to this TransactionContext realization or any other
	 * TransactionContext realization on this thread will fail.
	 */
	public void clear();

	/**
	 * Return an opaque object encapsulating the transaction context state.
	 * 
	 * @return
	 */
	public TransactionContextMemento getMemento();
	public void setMemento(TransactionContextMemento memento);

	/**
	 * Returns the hash code of the underlying storage instance, the VistaRealmPrincipal
	 * 
	 * @return
	 */
	public Integer getIdentity();
	
	// A method that returns a String that shows the transaction context state
	// in a form useful for debugging.  This method MUST never throw an Exception
	// and SHOULD never throw a Throwable (i.e. no NPEs!) regardless of state.
	public String getContextDebugState();
	
	/* transaction log specific properties */

	// the time that a transaction started on the Vix (milliseconds since 01Jan1970)
	public Long getStartTime();
	public void setStartTime(Long startTime);

	// the duration of the transaction on the Vix (in milliseconds)
	public Long getDuration();
	public void setDuration(Long startTime);

	public String getPatientID();
	public void setPatientID(String patientID);

	public String getRequestType();
	public void setRequestType(String requestType);

	/**
	 * Asynchronous commands may generate transaction log entries.
	 * The transaction ID must be the same as the parent (client calls)
	 * transaction ID.  The individual step of the command processing
	 * that generates a transaction log entry MUST set a childRequestType.
	 * @return
	 */
	public String getChildRequestType();
	public void setChildRequestType(String childRequestType);

	public String getCommandId();
	public void setCommandId(String commandId);

	public String getParentCommandId();
	public void setParentCommandId(String parentCommandId);

	public String getQueryFilter();
	public void setQueryFilter(String queryFilter);

	// the number of distinct image entities returned (usually 1 or the number of studies in a graph)
	/**
	 * The number of entries returned by the facade
	 */
	public Integer getEntriesReturned();
	public void setEntriesReturned(Integer entriesReturned);

	/**
	 * The number of entries returned by the data source
	 * @return
	 */
	public Integer getDataSourceEntriesReturned();
	public void setDataSourceEntriesReturned(Integer entriesReturned);

	public String getUrn();
	public void setUrn(String urn);

	public String getQuality();
	public void setQuality(String quality);

	public String getOriginatingAddress();
	public void setOriginatingAddress(String originatingAddress);

	public Boolean isItemCached();
	public void setItemCached(Boolean returneditemCached);

	public String getErrorMessage();
	public void setErrorMessage(String errorMessage);

	/**
	 * Returns the source that requested the data (VA or DOD)
	 * @deprecated This field is no longer used in the transaction log
	 * @return
	 * 
	 */
	@Deprecated
	public String getRequestingSource();
	@Deprecated 
	public void setRequestingSource(String requestingSource);

	/**
	 * Returns the site number source of the data (200, 660, etc). 
	 * @return
	 */
	public String getServicedSource();
	public void setServicedSource(String servicedSource);

	// return a hash of the principal realm, identifier and credentials
	public String getSecurityHashCode();

	/**
	 * Returns the protocol of the data source that serviced the request
	 * @return The protocol of the url that was used to handle the request
	 */
	public String getDatasourceProtocol();
	public void setDatasourceProtocol(String protocol);

	// the total number of facade bytes sent
	public Long getFacadeBytesSent();
	public void setFacadeBytesSent(Long facadeBytesSent);

	// the total number of facade bytes received
	public Long getFacadeBytesReceived();
	public void setFacadeBytesReceived(Long facadeBytesReceived);

	// the total number of datasource bytes sent
	public Long getDataSourceBytesSent();
	public void setDataSourceBytesSent(Long dataSourceBytesSent);

	// the total number of datasource bytes received
	public Long getDataSourceBytesReceived();
	public void setDataSourceBytesReceived(Long dataSourceBytesReceived);

	/**
	 * Returns the modality for the image accessed in the transaction
	 * @return The image modality
	 */
	public String getModality();
	public void setModality(String modality);

	/**
	 * The ProtocolOverride property, if not null, will override the
	 * preferred protocols, if the site resolution data source supports
	 * it.  This is useful in testing to force a Vix to call another Vix
	 * over a specific protocol.
	 * It is required that the protocol override NOT be passed
	 * to remote Vix as that would:
	 * 1.) potentially open a security hole, or at the least break because
	 * the security context would not be applicable
	 * 2.) confuse the bejeebers out of anyone testing 'cause it would be
	 * hard to know who is actually serving the request
	 * 
	 * It is also highly recommended that this property not be externally accessible,
	 * that is a VIX facade does not allow it to be set, by normal users. 
	 * 
	 * NOTE: its entirely possible to configure an infinite loop of Vix1
	 * talking to Vix2 talking to the Vix1, talking to Vix2, ....
	 * 
	 * This value must be a comma-separated list of valid protocols.
	 * 
	 * @return
	 */
	public String getOverrideProtocol();
	public void setOverrideProtocol(String preferredProtocols);

	/**
	 * The OverrideRoutingToken property, if not null, forces the VIX to act as a client
	 * and proxy the request to the targeted site.  This is used for testing and not
	 * for production and the warnings for protocol override apply to this property
	 * as well.
	 * @return
	 */
	public RoutingToken getOverrideRoutingToken();
	public void setOverrideRoutingToken(RoutingToken routingToken);


	/**
	 * The ResponseCode property is the code that was sent back to the requesting client
	 * from the facade. For an HTTP request this code might be 404 (not found), 200 (ok), 
	 * 409 (invalid credentials), 500 (internal server error), etc. This response code is 
	 * NOT HTTP specific and because this value might not always be an integer this is held
	 * as a string.
	 * @return
	 */
	public String getResponseCode();
	public void setResponseCode(String responseCode);

	/**
	 * The ExceptionClassName is the name of the class (not including package) that caused the
	 * error. This is used to easily determine the root cause of the problem. If the transaction
	 * did not have an error or any problem, this value will be null.
	 * @return
	 */
	public String getExceptionClassName();
	public void setExceptionClassName(String className);

	/**
	 * Get the command class name executed.
	 * @return the command class name executed.
	 */
	public String getCommandClassName ();

	/**
	 * Set the command class name executed.
	 * @param commandClassName The command class name executed.
	 */
	public void setCommandClassName (String commandClassName);

	/**
	 * Get the machine name of this computer.
	 * @return the machine name - localhost hostname or ip address of this computer, or null if not determinable.
	 */
	public String getMachineName ();

	/**
	 * Set the machine name of this computer.
	 * @param machineName - localhost hostname or ip address of this computer.
	 */
	public void setMachineName (String machineName);

	/*
	 *
	 * Get the amount of time, in milliseconds, that it took to get the first byte back from a socket read request.
	 * @return the Long amount of time, in milliseconds, that it took to get the first byte back from a socket read request, or null if unknown.
	 */
	public Long getTimeToFirstByte ();

	/*
	 *
	 * Set the amount of time, in milliseconds, that it took to get the first byte back from a socket read request.
	 * @param timeToFirstByte The Long amount of time, in milliseconds, that it took to get the first byte back from a socket read request.
	 */
	public void setTimeToFirstByte (Long timeToFirstByte);

	/**
	 * @return the VIX software version as defined by the VIX installer 
	 */
	public String getVixSoftwareVersion();

	/**
	 * Set the VIX software version as defined by the VIX installer
	 * @param vixSoftwareVersion - the VIX software version
	 */
	public void setVixSoftwareVersion(String vixSoftwareVersion);


	/**
	 * @return true if the command is executing asynchronously
	 */
	public Boolean isAsynchronousCommand();

	/**
	 * Set the command execution strategy
	 * @param asynchronousCommand - true if the command is executed asynchronously, false otherwise
	 */
	public void setAsynchronousCommand(Boolean asynchronousCommand);

	/**
	 * Holds the security context type, should be set by the Facade and used by the data source
	 * @return
	 */
	public String getImagingSecurityContextType();
	public void setImagingSecurityContextType(String imagingSecurityContextType);
	
	public String getBrokerSecurityToken();
	public String getLoggerBrokerSecurityToken();
	public void setBrokerSecurityToken(String brokerSecurityToken);
	
	/**
	 * Determines if the token has been generated by the local site.
	 * @return
	 */
	public Boolean isTokenLocallyGenerated();
	public void setTokenLocallyGenerated(Boolean value);
	
	/**
	 * Get/set the name of the application used when generating a broker security token
	 * @return
	 */
	public String getBrokerSecurityApplicationName();
	public void setBrokerSecurityApplicationName(String brokerSecurityApplicationName);
	
	public String getCacheLocationId();
	public String getLoggerCacheLocationId();
	public void setCacheLocationId(String cacheLocationId);
	
	public String getUserDivision();
	public String getLoggerUserDivision();
	public void setUserDivision(String userDivision);
	
	/**
	 * Get the method used for remotely logging into a site (CAPRI or BSE)
	 * @return
	 */
	public String getLoginMethod();
	/**
	 * Set the method used for remotely logging into a site (CAPRI or BSE)
	 * @param remoteLoginMethod
	 */
	public void setLoginMethod(String remoteLoginMethod);
	
	/**
	 * The image format the facade responds with for image requests
	 * @return
	 */
	public String getFacadeImageFormatSent();
	
	/**
	 * The image format the facade responds with for image requests
	 * @param facadeImageFormatSent
	 */
	public void setFacadeImageFormatSent(String facadeImageFormatSent);
	
	/**
	 * The image quality the facade responds with for image requests
	 * @return
	 */
	public String getFacadeImageQualitySent();
	/**
	 * The image quality the facade responds with for image requests
	 * @param facadeImageQualitySent
	 */
	public void setFacadeImageQualitySent(String facadeImageQualitySent);
	
	/**
	 * The image format received by the data source before any image conversion is done
	 * @return
	 */
	public String getDataSourceImageFormatReceived();
	/**
	 * The image format received by the data source before any image conversion is done
	 * @param dataSourceImageFormatRecieved
	 */
	public void setDataSourceImageFormatReceived(String dataSourceImageFormatRecieved);
	
	/**
	 * The image quality received by the data source before any image conversion is done
	 * @return
	 */
	public String getDataSourceImageQualityReceived();
	
	/**
	 * The image quality received by the data source before any image conversion is done
	 * @param dataSourceImageQualityReceived
	 */
	public void setDataSourceImageQualityReceived(String dataSourceImageQualityReceived);
	
	/**
	 * Specifies whether the transaction result should be formatted for a HAIMS-1 client
	 * @return
	 */
	public Boolean isHaims1Client();
	public void setHaims1Client(Boolean value);
	
	/**
	 * Get the version of the client that initiated the request
	 * @return
	 */
	public String getClientVersion();
	
	/**
	 * Set the version of the client that initiated the request
	 * @param version
	 */
	public void setClientVersion(String clientVersion);
	
	/**
	 * Return the name of the method called by the data source, this should be set for each proxy call
	 * @return
	 */
	public String getDataSourceMethod();	
	public void setDataSourceMethod(String dataSourceMethod);
	
	/**
	 * Return the version of the data source called
	 * @return
	 */
	public String getDataSourceVersion();
	public void setDataSourceVersion(String dataSourceVersion);
	
	public String getDebugInformation();
	public void addDebugInformation(String debugInformation);
	public void setDebugInformation(String debugInformation);
	
	/**
	 * Returns the hostname of the server that responded to the request (if it is a VISA implementation)
	 * @return
	 */
	public String getDataSourceResponseServer();
	public void setDataSourceResponseServer(String dataSourceResponseServer);
	
	public String getThreadId();
	public void setThreadId(String threadId);
	
	/**
	 * The site number for the VIX from the VixConfig.xml file
	 * @return
	 */
	public String getVixSiteNumber();
	public void setVixSiteNumber(String vixSiteNumber);
	
	/**
	 * The site number for the VIX that requested data from this VIX, this value comes from that VIX server VixConfig.xml file
	 * @return
	 */
	public String getRequestingVixSiteNumber();
	public void setRequestingVixSiteNumber(String requestingVixSiteNumber);
	
	/**
	 * The identifier of the VistA Session that was used for this transaction
	 * @return
	 */
	public Long getVistaSessionIndex();
	public void setVistaSessionIndex(Long vistaSessionIndex);

	/**
	 * Any fields that are marked as additional debug information properties will be returned by this method. This method MAY return
	 * null if no properties are marked as additional fields or if none of the properties have a value set
	 * @return
	 */
	public String getAdditionalDebugInformation();
	
	/**
	 * 
	 * @return
	 */
	public Boolean isAllowAddFederationCompression();
	public void setAllowAddFederationCompression(Boolean allowAddFederationCompression);
}
