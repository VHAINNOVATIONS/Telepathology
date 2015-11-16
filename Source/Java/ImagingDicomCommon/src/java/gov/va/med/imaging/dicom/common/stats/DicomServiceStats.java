/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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

package gov.va.med.imaging.dicom.common.stats;

import gov.va.med.imaging.ImagingMBean;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class DicomServiceStats implements DicomServiceStatsMBean {

	public static final String UP = "LISTENING";
	public static final String DOWN = "NOT LISTENING";
	public static final String FAILED = "FAILED TO LISTEN";
	public static final String CSTORE = "C-Store";
	public static final String CFIND = "C-Find";
	public static final String CMOVE = "C-Move";
	public static final String CECHO = "C-Echo";
	
	
	private static DicomServiceStats instance_ = null;
	private static ObjectName dicomServiceStatsMBeanName = null;
    private static Logger logger = Logger.getLogger (DicomServiceStats.class);
	
	private int totalRADObjectsToCorrectForHDIG;
	private int totalCONObjectsToCorrectForHDIG;
	
	private int dicomCorrectWorkItemCount;
	private int networkImportWorkItemCount;
	private int stagedMediaWorkItemCount;
	private int failedWorkItemCount;
	
	private boolean serviceAccountValid;

	private Set<ListeningPortStatistics> currentPortsStatus;
	private Set<InboundDicomAssociationStatistics> inboundAssociationStats;
	private Set<InboundDicomDimseStatistics> dimseServiceStats;
	private Set<InboundModalityDeviceStatistics> deviceStats;
	private Set<InboundSOPClassStatistics> sopClassStats;
	private Set<InboundDicomObjectStatistics> storageSCPStats;
	private Set<OutboundDicomObjectStatistics> storageSCUStats;
	private Set<OutboundDicomAssociationStatistics> outboundAssociationStats;

	private DicomServiceStats(){
		
		currentPortsStatus = new HashSet<ListeningPortStatistics>();
		inboundAssociationStats = new HashSet<InboundDicomAssociationStatistics>();
		dimseServiceStats = new HashSet<InboundDicomDimseStatistics>();
		deviceStats = new HashSet<InboundModalityDeviceStatistics>();
		sopClassStats = new HashSet<InboundSOPClassStatistics>();
		storageSCPStats = new HashSet<InboundDicomObjectStatistics>();
		storageSCUStats = new HashSet<OutboundDicomObjectStatistics>();
		outboundAssociationStats = new HashSet<OutboundDicomAssociationStatistics>();
		
		totalRADObjectsToCorrectForHDIG = 0;
		totalCONObjectsToCorrectForHDIG = 0;
		
		dicomCorrectWorkItemCount = 0;
		networkImportWorkItemCount = 0;
		stagedMediaWorkItemCount = 0;
		failedWorkItemCount = 0;
		serviceAccountValid = true;

	}

	public synchronized static DicomServiceStats getInstance(){
		if(instance_ == null){
			instance_ = new DicomServiceStats();
			registerResourceMBeans();
		}
		return instance_;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomServiceStatsMBean#getTotalRADObjectsToCorrectForHDIG()
	 */
	@Override
	public int getTotalRADObjectsToCorrectForHDIG() {
		
		return this.totalRADObjectsToCorrectForHDIG;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomServiceStatsMBean#getTotalCONObjectsToCorrectForHDIG()
	 */
	@Override
	public int getTotalCONObjectsToCorrectForHDIG() {
		
		return this.totalCONObjectsToCorrectForHDIG;
	}
	
	public void incrementInboundAssociationAcceptCount(String aet, String ipAddr){
		try {
			InboundDicomAssociationStatistics assoc = getFromInboundAssociationList(aet.toUpperCase(), ipAddr);
			assoc.incrementAcceptedAssociationsCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void incrementInboundAssociationRejectCount(String aet, String ipAddr){
		try {
			InboundDicomAssociationStatistics assoc = getFromInboundAssociationList(aet.toUpperCase(), ipAddr);
			assoc.incrementRejectedAssociationsCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}
	
	public void incrementInboundDimseMessageAcceptCount(String aet, String dimseService){
		try {
			InboundDicomDimseStatistics dimse = getFromDimseList(aet.toUpperCase(), dimseService);
			dimse.incrementProcessedDimseMessageCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void incrementInboundDimseMessageRejectCount(String aet, String dimseService){
		try {
			InboundDicomDimseStatistics dimse = getFromDimseList(aet.toUpperCase(), dimseService);
			dimse.incrementRejectedDimseMessageCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void incrementInboundObjectProcessedCount(String aet, String manufacturer, String model, String sopClassUID){
		try {
			InboundDicomObjectStatistics storage  = getFromStorageSCPList(aet.toUpperCase());
			storage.incrementObjectsProcessedCount();

			InboundModalityDeviceStatistics device  = getFromModalityDeviceList(manufacturer, model);
			device.incrementDicomObjectsProcessedCount();

			InboundSOPClassStatistics sopClass = getFromSOPClassList(sopClassUID);
			sopClass.incrementDicomObjectsProcessedCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void incrementInboundObjectRejectedCount(String aet, String manufacturer, String model, String sopClassUID){
		try {
			InboundDicomObjectStatistics storage  = getFromStorageSCPList(aet.toUpperCase());
			storage.incrementObjectsRejectedCount();

			InboundModalityDeviceStatistics device  = getFromModalityDeviceList(manufacturer, model);
			device.incrementDicomObjectsRejectedCount();

			InboundSOPClassStatistics sopClass = getFromSOPClassList(sopClassUID);
			sopClass.incrementDicomObjectsRejectedCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}
		
	public void incrementInboundObjectsPassedToLegacyGWCount(String aet){
		try {
			InboundDicomObjectStatistics storage  = getFromStorageSCPList(aet.toUpperCase());
			storage.incrementObjectsPassedToLegacyGWCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}		
	}

	public void incrementInboundObjectsPassedToHDIGDataStructureCount(String aet){
		try {
			InboundDicomObjectStatistics storage  = getFromStorageSCPList(aet.toUpperCase());
			storage.incrementObjectsPassedToHDIGDataStructureCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}		
	}

	public void incrementInboundDuplicateObjectCount(String aet, String manufacturer, String model){
		try {
			InboundDicomObjectStatistics storage  = getFromStorageSCPList(aet.toUpperCase());
			storage.incrementDuplicateObjectsCount();

			InboundModalityDeviceStatistics device  = getFromModalityDeviceList(manufacturer, model);
			device.incrementTotalDuplicateObjectsCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void incrementDuplicateInstanceUIDsCount(String manufacturer, String model){
		try {
			InboundModalityDeviceStatistics device  = getFromModalityDeviceList(manufacturer, model);
			device.incrementDuplicateInstanceUIDsCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}
	
	public void incrementIODViolationCount(String manufacturer, String model){
		try {
			InboundModalityDeviceStatistics device  = getFromModalityDeviceList(manufacturer, model);
			device.incrementDicomObjectsWithIODViolationsCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}
	
	public void incrementOutboundObjectTransmittedCount(String aet){
		try {
			OutboundDicomObjectStatistics transmit  = getFromStorageSCUList(aet);
			transmit.incrementObjectsTransmittedToSCPCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void incrementOutboundObjectRejectedCount(String aet){
		try {
			OutboundDicomObjectStatistics transmit  = getFromStorageSCUList(aet);
			transmit.incrementObjectsRejectedBySCPCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void incrementOutboundAssociationAcceptCount(String aet){
		try {
			OutboundDicomAssociationStatistics transmit  = getFromOutboundAssociationList(aet);
			transmit.incrementAcceptedAssociationsCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}
	
	public void incrementOutboundAssociationRejectCount(String aet){
		try {
			OutboundDicomAssociationStatistics transmit  = getFromOutboundAssociationList(aet);
			transmit.incrementRejectedAssociationsCount();
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	public void setCurrentPortStatus(int port, String condition){
		try {
			ListeningPortStatistics listenPort = getFromPortList(port);
			listenPort.setCurrentStatus(condition);
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	/**
	 * @param totalRADObjectsToCorrectForHDIG the totalRADObjectsToCorrectForHDIG to set
	 */
	public void setTotalRADObjectsToCorrectForHDIG(int totalRADObjectsToCorrectForHDIG){
		try {
			this.totalRADObjectsToCorrectForHDIG = totalRADObjectsToCorrectForHDIG;
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}

	/**
	 * @param totalCONObjectsToCorrectForHDIG the totalCONObjectsToCorrectForHDIG to set
	 */
	public void setTotalCONObjectsToCorrectForHDIG(int totalCONObjectsToCorrectForHDIG){
		try {
			this.totalCONObjectsToCorrectForHDIG = totalCONObjectsToCorrectForHDIG;
		} catch (Exception X) {
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
		}
	}
	
	/**
	 * @param aet
	 * @param manufacturer
	 * @param model
	 * @param sopClassName
	 */
	public void incrementInboundAcceptCount(String aet, String manufacturer, String model, String sopClassName) {
		try{
			incrementInboundObjectProcessedCount(aet.toUpperCase(), manufacturer, model, sopClassName);
			incrementInboundDimseMessageAcceptCount(aet.toUpperCase(), DicomServiceStats.CSTORE);
		}
		catch(Exception X){
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
			}
		}

	
	/**
	 * @param aet
	 * @param manufacturer
	 * @param model
	 * @param sopClassName
	 */
	public void incrementInboundRejectCount(String aet, String manufacturer, String model, String sopClassName) {
		try{
			incrementInboundObjectRejectedCount(aet.toUpperCase(), manufacturer, model, sopClassName);
			incrementInboundDimseMessageRejectCount(aet.toUpperCase(), DicomServiceStats.CSTORE);
		}
		catch(Exception X){
			logger.warn(this.getClass().getName()+" failed to update Statistics.");
			}
		}


	public void loadFalseStatistics(){
		
		DicomServiceStats.getInstance().setCurrentPortStatus(10, DicomServiceStats.UP);
		DicomServiceStats.getInstance().setCurrentPortStatus(11, DicomServiceStats.UP);
		DicomServiceStats.getInstance().setCurrentPortStatus(12, DicomServiceStats.UP);
		DicomServiceStats.getInstance().setCurrentPortStatus(13, DicomServiceStats.DOWN);
		
		DicomServiceStats.getInstance().incrementInboundAssociationAcceptCount("Strawberry", "10.10.10.2");
		DicomServiceStats.getInstance().incrementInboundAssociationAcceptCount("ChocoLATE", "10.10.10.1");
		DicomServiceStats.getInstance().incrementInboundAssociationAcceptCount("Vanilla", "10.10.10.3");
		
		DicomServiceStats.getInstance().incrementInboundAssociationRejectCount("Vanilla", "10.10.10.3");
		DicomServiceStats.getInstance().incrementInboundAssociationRejectCount("VANILLA", "10.10.10.3");
		DicomServiceStats.getInstance().incrementInboundAssociationRejectCount("Strawberry", "10.10.10.2");

		DicomServiceStats.getInstance().incrementInboundDimseMessageAcceptCount("Strawberry", CSTORE);
		DicomServiceStats.getInstance().incrementInboundDimseMessageAcceptCount("Strawberry", CSTORE);
		DicomServiceStats.getInstance().incrementInboundDimseMessageRejectCount("Vanilla", CFIND);
		
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("Chocolate", "Veterans Affairs", "Party Cake", "Computed Radiography");
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("CHOCOLATE", "Veterans Affairs", "Party Cake", "Computed Radiography");
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("Vanilla", "Veterans Affairs", "Rocky Road", "Computed Tomography");
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("Strawberry", "Veterans Affairs", "Fudge Ripple", "Magnetic Resonance");
		
		DicomServiceStats.getInstance().incrementInboundObjectRejectedCount("STRAWBERRY", "Veterans Affairs", "Fudge Ripple", "Magnetic Resonance");
		
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("Vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("VANILLA");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("Strawberry");

		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Chocolate");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Chocolate");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Chocolate");
		
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("Vanilla", "Veterans Affairs", "Rocky Road");
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("Vanilla", "Veterans Affairs", "Rocky Road");
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("vanilla", "Veterans Affairs", "Rocky Road");
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("vanilla", "Veterans Affairs", "Rocky Road");
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("vanilla", "Veterans Affairs", "Fudge Ripple");
		
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs", "Cookie Dough");
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs", "Cookie Dough");
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs", "Butter Pecan");
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs", "Fudge Ripple");
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs", "Fudge Ripple");
		
		DicomServiceStats.getInstance().incrementIODViolationCount("Veterans Affairs", "Neopolitan");
		DicomServiceStats.getInstance().incrementIODViolationCount("Veterans Affairs", "Neopolitan");
		
		DicomServiceStats.getInstance().incrementOutboundAssociationAcceptCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundAssociationAcceptCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundAssociationRejectCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundAssociationAcceptCount("Chocolate");
		
		DicomServiceStats.getInstance().incrementOutboundObjectTransmittedCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundObjectTransmittedCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundObjectTransmittedCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundObjectRejectedCount("Chocolate");
				
		DicomServiceStats.getInstance().setTotalRADObjectsToCorrectForHDIG(25);
		DicomServiceStats.getInstance().setTotalCONObjectsToCorrectForHDIG(50);
	}

	
	protected synchronized ListeningPortStatistics getFromPortList(int port){
		ListeningPortStatistics statEntry;

		if(currentPortsStatus.isEmpty()){
			statEntry = new ListeningPortStatistics(port);
			String objName = "Port";
			String objNumber = Integer.toString(currentPortsStatus.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			currentPortsStatus.add(statEntry);
			return statEntry;
		}
		Iterator<ListeningPortStatistics> iter = currentPortsStatus.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) && (statEntry.getPort() == port)){
				return statEntry;
			}
		}
		statEntry = new ListeningPortStatistics(port);
		String objName = "Port";
		String objNumber = Integer.toString(currentPortsStatus.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		currentPortsStatus.add(statEntry);
		return statEntry;
	}	

	protected synchronized InboundDicomAssociationStatistics getFromInboundAssociationList(String aet, String ipAddr){
		InboundDicomAssociationStatistics statEntry;
		
		if(aet != null && aet.length()>0){
			aet = aet.trim();
		}
		if(ipAddr != null && ipAddr.length()>0){
			ipAddr = ipAddr.trim();
		}
		
		if(inboundAssociationStats.isEmpty()){
			statEntry = new InboundDicomAssociationStatistics(aet, ipAddr);
			String objName = "InboundAssociations";
			String objNumber = Integer.toString(inboundAssociationStats.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			inboundAssociationStats.add(statEntry);
			return statEntry;
		}
		Iterator<InboundDicomAssociationStatistics> iter = inboundAssociationStats.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) 
					&& (statEntry.getAeTitle().equals(aet))
					&& (statEntry.getIpAddress().equals(ipAddr))){
				return statEntry;
			}
		}
		statEntry = new InboundDicomAssociationStatistics(aet, ipAddr);
		String objName = "InboundAssociations";
		String objNumber = Integer.toString(inboundAssociationStats.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		inboundAssociationStats.add(statEntry);
		return statEntry;
	}
	
	protected synchronized InboundDicomDimseStatistics getFromDimseList(String aet, String dimseService){
		InboundDicomDimseStatistics statEntry;
		
		if(aet != null && aet.length()>0){
			aet = aet.trim();
		}
		if(dimseService != null && dimseService.length()>0){
			dimseService = dimseService.trim();
		}
		
		if(dimseServiceStats.isEmpty()){
			statEntry = new InboundDicomDimseStatistics(aet, dimseService);
			String objName = "InboundDimseMessages";
			String objNumber = Integer.toString(dimseServiceStats.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			dimseServiceStats.add(statEntry);
			return statEntry;
		}
		Iterator<InboundDicomDimseStatistics> iter = dimseServiceStats.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) 
					&& (statEntry.getAeTitle().equals(aet))
					&& (statEntry.getDimseServiceName().equals(dimseService))){
				return statEntry;
			}
		}
		statEntry = new InboundDicomDimseStatistics(aet, dimseService);
		String objName = "InboundDimseMessages";
		String objNumber = Integer.toString(dimseServiceStats.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		dimseServiceStats.add(statEntry);
		return statEntry;
	}

	protected synchronized InboundDicomObjectStatistics getFromStorageSCPList(String aet){
		InboundDicomObjectStatistics statEntry;
		
		if(aet != null && aet.length()>0){
			aet = aet.trim();
		}
		
		if(storageSCPStats.isEmpty()){
			statEntry = new InboundDicomObjectStatistics(aet);
			String objName = "InboundObjects";
			String objNumber = Integer.toString(storageSCPStats.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			storageSCPStats.add(statEntry);
			return statEntry;
		}
		Iterator<InboundDicomObjectStatistics> iter = storageSCPStats.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) 
					&& (statEntry.getAeTitle().equals(aet))){
				return statEntry;
			}
		}
		statEntry = new InboundDicomObjectStatistics(aet);
		String objName = "InboundObjects";
		String objNumber = Integer.toString(storageSCPStats.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		storageSCPStats.add(statEntry);
		return statEntry;
	}

	protected synchronized InboundModalityDeviceStatistics getFromModalityDeviceList(String manufacturer, String model){
		InboundModalityDeviceStatistics statEntry;

		if(manufacturer != null && manufacturer.length()>0){
			manufacturer = manufacturer.trim();
		}
		if(model != null && model.length()>0){
			model = model.trim();
		}

		if(deviceStats.isEmpty()){
			statEntry = new InboundModalityDeviceStatistics(manufacturer, model);
			String objName = "InboundModalityDevices";
			String objNumber = Integer.toString(deviceStats.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			deviceStats.add(statEntry);
			return statEntry;
		}
		Iterator<InboundModalityDeviceStatistics> iter = deviceStats.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) 
					&& (statEntry.getManufacturer().equals(manufacturer)) 
					&& (statEntry.getModel().equals(model))){
				return statEntry;
			}
		}
		statEntry = new InboundModalityDeviceStatistics(manufacturer, model);
		String objName = "InboundModalityDevices";
		String objNumber = Integer.toString(deviceStats.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		deviceStats.add(statEntry);
		return statEntry;
	}

	protected synchronized InboundSOPClassStatistics getFromSOPClassList(String sopClassUID){
		InboundSOPClassStatistics statEntry;

		if(sopClassUID != null && sopClassUID.length()>0){
			sopClassUID = sopClassUID.trim();
		}

		if(sopClassStats.isEmpty()){
			statEntry = new InboundSOPClassStatistics(sopClassUID);
			String objName = "InboundSOPClasses";
			String objNumber = Integer.toString(sopClassStats.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			sopClassStats.add(statEntry);
			return statEntry;
		}
		Iterator<InboundSOPClassStatistics> iter = sopClassStats.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) 
					&& (statEntry.getModalityCode().equals(sopClassUID))){
				return statEntry;
			}
		}
		statEntry = new InboundSOPClassStatistics(sopClassUID);
		String objName = "InboundSOPClasses";
		String objNumber = Integer.toString(sopClassStats.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		sopClassStats.add(statEntry);
		return statEntry;
	}

	protected synchronized OutboundDicomObjectStatistics getFromStorageSCUList(String aet){
		
		OutboundDicomObjectStatistics statEntry;
		
		if(aet != null && aet.length()>0){
			aet = aet.trim();
		}
		
		if(storageSCUStats.isEmpty()){
			statEntry = new OutboundDicomObjectStatistics(aet);
			String objName = "OutboundObjects";
			String objNumber = Integer.toString(storageSCUStats.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			storageSCUStats.add(statEntry);
			return statEntry;
		}
		Iterator<OutboundDicomObjectStatistics> iter = storageSCUStats.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) 
					&& (statEntry.getAeTitle().equals(aet))){
				return statEntry;
			}
		}
		statEntry = new OutboundDicomObjectStatistics(aet);
		String objName = "OutboundObjects";
		String objNumber = Integer.toString(storageSCUStats.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		storageSCUStats.add(statEntry);
		return statEntry;
	}

	protected synchronized OutboundDicomAssociationStatistics getFromOutboundAssociationList(String aet){
		
		OutboundDicomAssociationStatistics statEntry;
		
		if(aet != null && aet.length()>0){
			aet = aet.trim();
		}
		
		if(outboundAssociationStats.isEmpty()){
			statEntry = new OutboundDicomAssociationStatistics(aet);
			String objName = "OutboundAssociations";
			String objNumber = Integer.toString(outboundAssociationStats.size());
			registerAddedResourceMBeans(objName, objNumber, statEntry);
			outboundAssociationStats.add(statEntry);
			return statEntry;
		}
		Iterator<OutboundDicomAssociationStatistics> iter = outboundAssociationStats.iterator();
		while(iter.hasNext()){
			statEntry = iter.next();
			if((statEntry!=null) 
					&& (statEntry.getAeTitle().equals(aet))){
				return statEntry;
			}
		}
		statEntry = new OutboundDicomAssociationStatistics(aet);
		String objName = "OutboundAssociations";
		String objNumber = Integer.toString(outboundAssociationStats.size());
		registerAddedResourceMBeans(objName, objNumber, statEntry);
		outboundAssociationStats.add(statEntry);
		return statEntry;
	}

	
	/**
	 * This method should only be called once, else MBean exceptions will occur.
	 */
	private synchronized static void registerResourceMBeans()
    {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		
		if(dicomServiceStatsMBeanName == null)
		{
			try
            {
				Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
				mBeanProperties.put( "type", "DicomServiceStats" );
				mBeanProperties.put( "name", "DICOMActivity");
				dicomServiceStatsMBeanName = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
	            mBeanServer.registerMBean(DicomServiceStats.getInstance(), dicomServiceStatsMBeanName);
            } 
			catch (Exception e){ 
				e.printStackTrace();
			}
		}
	}
	
	private synchronized static void registerAddedResourceMBeans(String objName, String number, Object obj){

		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try
        {
			Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
			mBeanProperties.put( "type", "DicomServiceStats" );
			mBeanProperties.put( "name", objName);
			mBeanProperties.put("number", number);
			ObjectName name = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
            mBeanServer.registerMBean(obj, name);
        } 
		catch (Exception e){ 
			//Do nothing
		}
	}

	public void setDicomCorrectWorkItemCount(int dicomCorrectWorkItemCount) {
		this.dicomCorrectWorkItemCount = dicomCorrectWorkItemCount;
	}

	public int getDicomCorrectWorkItemCount() {
		return this.dicomCorrectWorkItemCount;
	}

	public void setNetworkImportWorkItemCount(int networkImportWorkItemCount) {
		this.networkImportWorkItemCount = networkImportWorkItemCount;
	}

	public int getNetworkImportWorkItemCount() {
		return this.networkImportWorkItemCount;
	}

	public void setStagedMediaWorkItemCount(int stagedMediaWorkItemCount) {
		this.stagedMediaWorkItemCount = stagedMediaWorkItemCount;
	}

	public int getStagedMediaWorkItemCount() {
		return this.stagedMediaWorkItemCount;
	}

	public void setFailedWorkItemCount(int failedWorkItemCount) {
		this.failedWorkItemCount = failedWorkItemCount;
	}

	public int getFailedWorkItemCount() {
		return failedWorkItemCount;
	}

	public void setServiceAccountValid(boolean serviceAccountValid) {
		this.serviceAccountValid = serviceAccountValid;
	}

	public boolean isServiceAccountValid() {
		return serviceAccountValid;
	}
}
