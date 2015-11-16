package gov.va.med.imaging;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.access.TransactionLogEntry;
import gov.va.med.imaging.access.TransactionLogWriter;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.Region;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.SiteNumberArtifactSourceTranslator;
import gov.va.med.imaging.exchange.business.WelcomeMessage;
import gov.va.med.imaging.exchange.enums.DatasourceProtocol;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.javalogs.JavaLogFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Iterator;
//import java.util.List;

/**
 * 
 * @author vhaiswbeckec
 *
 */
@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface BaseWebFacadeRouter
extends FacadeRouter
{
	
	
	/**
	 * 
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract Set<ResolvedSite> getResolvedSiteSet()
	throws MethodException, ConnectionException;

	/**
	 * 
	 * @param siteNumber
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract ResolvedSite getResolvedSite(String siteNumber)
	throws MethodException, ConnectionException;

	/**
	 * 
	 * @param patientIcn
	 * @param siteNumber
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract List<ResolvedArtifactSource> getTreatingSites(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			boolean includeTrailingCharactersForSite200, SiteNumberArtifactSourceTranslator siteNumberArtifactSourceTranslator)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false)
	public abstract List<ResolvedArtifactSource> getTreatingSites(RoutingToken routingToken, PatientIdentifier patientIdentifier)
	throws MethodException, ConnectionException;
   
   

	/**
	 * Asynchronously store a TransactionLog record using a TransactionLogEntry object.
	 * @param entry The TransactionLogEntry object to store a TransactionLog record.
	 */
	@FacadeRouterMethod(asynchronous = true, commandClassName = "PostTransactionLogEntryCommand")
	public abstract void postTransactionLogEntry(TransactionLogEntry entry);

	/**
	 * Synchronously store a TransactionLog record using a TransactionLogEntry object.
	 * @param entry The TransactionLogEntry object to store a TransactionLog record.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous = false, commandClassName = "PostTransactionLogEntryCommand")
	public abstract void postTransactionLogEntryImmediate(TransactionLogEntry entry) 
	throws MethodException, ConnectionException;

	/**
	 * Get a List of Transaction Log records.
	 * This is a composite command-
	 * 
	 * getTransactionLogEntryList (null, null, null, null, null, null, null, null, null, null, null, null) -
	 * maps to SPI getAllLogEntries ().
	 * 
	 * getTransactionLogEntryList (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, null) -
	 * maps to SPI getLogEntries (startDate, endDate, imageQuality, use, modality, datasourceProtocol, errorMessage, imageUrn, transactionId, forward).
	 * 
	 * getTransactionLogEntryList (null, null, null, null, null, null, null, null, null, null, ?, ?) -
	 * maps to SPI getLogEntries (fieldName, fieldValue).
	 * 
	 * @return the List of TransactionLog records meeting the query criteria, if any.
	 * 
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	/*
	@FacadeRouterMethod(asynchronous = false, commandClassName = "GetTransactionLogEntryListCommand")
	public abstract List<TransactionLogEntry> getTransactionLogEntryList(
		Date startDate, Date endDate, ImageQuality imageQuality,
		String user, String modality,
		DatasourceProtocol datasourceProtocol, String errorMessage,
		String imageUrn, String transactionId, Boolean forward,
		String fieldName, String fieldValue, Integer startIndex,
			Integer endIndex) 
	throws MethodException, ConnectionException;
	*/
	
	@FacadeRouterMethod(asynchronous = false, commandClassName = "GetTransactionLogEntriesCommand")
	public abstract void getTransactionLogEntries(
			TransactionLogWriter transactionLogWriter,
			Date               startDate,
			Date               endDate, 
			ImageQuality       imageQuality, 
			String             user, 
			String             modality, 
			DatasourceProtocol datasourceProtocol,
			String             errorMessage,
			String             imageUrn,
			String             transactionId, 
			Boolean            forward,
			Integer            startIndex,
			Integer            endIndex) 
	throws MethodException, ConnectionException;

	/**
	 * Asynchronously delete TransactionLog records older than the number of days passed in.
	 * @param maxDaysAllowed The number of days of storage allowed before TransactionLog records purged. 
	 */
	@FacadeRouterMethod(asynchronous = true, commandClassName = "DeleteTransactionLogEntryCommand")
	public abstract void deleteTransactionLogEntry(Integer maxDaysAllowed);

	/**
	 * Synchronously delete TransactionLog records older than the number of days passed in.
	 * @param maxDaysAllowed The number of days of storage allowed before TransactionLog records purged.
	 * @return the number of TransactionLog records purged.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous = false, commandClassName = "DeleteTransactionLogEntryCommand")
	public abstract void deleteTransactionLogEntryImmediate(Integer maxDaysAllowed) 
	throws MethodException, ConnectionException;
	
	
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetRegionListCommand")
	public abstract List<Region> getRegionList()
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetJavaLogListCommand")
	public abstract List<JavaLogFile> getJavaLogFiles()
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetJavaLogFileCommand")
	public abstract InputStream getJavaLogFile(String filename)
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetArtifactSourceListCommand")
	public abstract List<ResolvedArtifactSource> getArtifactSourceList()
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetResolvedArtifactSourceListCommand")
	public abstract List<ResolvedArtifactSource> getResolvedArtifactSourceList()
	throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetResolvedArtifactSourceCommand")
	public abstract List<ResolvedArtifactSource> getResolvedArtifactSource(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	
	
	@FacadeRouterMethod(asynchronous=false, commandClassName="GetCachedWelcomeMessageCommand")
	public abstract WelcomeMessage getCachedWelcomeMessage(RoutingToken routingToken)
	throws MethodException, ConnectionException;
	
	/**
	 * 
	 * @param patientName
	 * @param siteNumber
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	@FacadeRouterMethod(asynchronous=false)
	public abstract List<Patient> getPatientList(
		String patientName,
		RoutingToken routingToken)
	throws MethodException, ConnectionException;
}
