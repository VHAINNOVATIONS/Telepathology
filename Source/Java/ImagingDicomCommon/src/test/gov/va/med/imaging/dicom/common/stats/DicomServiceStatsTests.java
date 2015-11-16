package gov.va.med.imaging.dicom.common.stats;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

public class DicomServiceStatsTests extends TestCase{

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public void testPortStatus(){
		DicomServiceStats.getInstance().setCurrentPortStatus(10, DicomServiceStats.UP);
		DicomServiceStats.getInstance().setCurrentPortStatus(11, DicomServiceStats.UP);
		DicomServiceStats.getInstance().setCurrentPortStatus(13, DicomServiceStats.DOWN);
		
		ListeningPortStatistics port1 = DicomServiceStats.getInstance().getFromPortList(10);
		assertEquals(DicomServiceStats.UP, port1.getCurrentStatus());

		ListeningPortStatistics port2 = DicomServiceStats.getInstance().getFromPortList(11);
		assertEquals(DicomServiceStats.UP, port2.getCurrentStatus());

		ListeningPortStatistics port3 = DicomServiceStats.getInstance().getFromPortList(13);
		assertEquals(DicomServiceStats.DOWN, port3.getCurrentStatus());
	}
	
	public void testInboundAssociations(){
		DicomServiceStats.getInstance().incrementInboundAssociationAcceptCount("Strawberry", "10.10.10.2");
		DicomServiceStats.getInstance().incrementInboundAssociationAcceptCount("Chocolate", "10.10.10.1");
		DicomServiceStats.getInstance().incrementInboundAssociationAcceptCount("vanilla", "10.10.10.3");
		
		DicomServiceStats.getInstance().incrementInboundAssociationRejectCount("Vanilla", "10.10.10.3");
		DicomServiceStats.getInstance().incrementInboundAssociationRejectCount("VANILLA", "10.10.10.3");
		DicomServiceStats.getInstance().incrementInboundAssociationRejectCount("STRAWBERRY", "10.10.10.2");
	
		
		InboundDicomAssociationStatistics strawberry = DicomServiceStats.getInstance().getFromInboundAssociationList("STRAWBERRY", "10.10.10.2");
		assertEquals(1, strawberry.getTotalAcceptedAssociations());
		assertEquals(1, strawberry.getTotalRejectedAssociations());

		InboundDicomAssociationStatistics chocolate = DicomServiceStats.getInstance().getFromInboundAssociationList("CHOCOLATE", "10.10.10.1");
		assertEquals(1, chocolate.getTotalAcceptedAssociations());
		assertEquals(0, chocolate.getTotalRejectedAssociations());

		InboundDicomAssociationStatistics vanilla = DicomServiceStats.getInstance().getFromInboundAssociationList("VANILLA", "10.10.10.3");
		assertEquals(1, vanilla.getTotalAcceptedAssociations());
		assertEquals(2, vanilla.getTotalRejectedAssociations());
	}
	
	public void testInboundDimseMessages(){
		DicomServiceStats.getInstance().incrementInboundDimseMessageAcceptCount("Strawberry", DicomServiceStats.CSTORE);
		DicomServiceStats.getInstance().incrementInboundDimseMessageAcceptCount("strawberry", DicomServiceStats.CSTORE);
		DicomServiceStats.getInstance().incrementInboundDimseMessageRejectCount("Vanilla", DicomServiceStats.CFIND);
		
		InboundDicomDimseStatistics strawberry = DicomServiceStats.getInstance().getFromDimseList("STRAWBERRY", DicomServiceStats.CSTORE);
		assertEquals(2, strawberry.getTotalProcessedDimseMessages());
		assertEquals(0, strawberry.getTotalRejectedDimseMessages());

		InboundDicomDimseStatistics vanilla = DicomServiceStats.getInstance().getFromDimseList("VANILLA", DicomServiceStats.CFIND);
		assertEquals(1, vanilla.getTotalRejectedDimseMessages());
		assertEquals(0, vanilla.getTotalProcessedDimseMessages());
	}
	
	public void testInboundObjects(){
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("Chocolate", "Veterans Affairs", "Party Cake", "Computed Radiography");
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("CHOCOLATE", "Veterans Affairs", "Party Cake", "Computed Radiography");
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("Vanilla", "Veterans Affairs", "Fudge Ripple", "Computed Tomography");
		DicomServiceStats.getInstance().incrementInboundObjectProcessedCount("Strawberry", "Veterans Affairs", "Fudge Ripple", "Magnetic Resonance");
		
		DicomServiceStats.getInstance().incrementInboundObjectRejectedCount("Strawberry", "Veterans Affairs", "Fudge Ripple", "Magnetic Resonance");
		
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("Vanilla", "Veterans Affairs", "Fudge Ripple");
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("vanilla", "Veterans Affairs", "Fudge Ripple");
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("Vanilla", "Veterans Affairs", "Fudge Ripple");
		DicomServiceStats.getInstance().incrementInboundDuplicateObjectCount("Strawberry", "Veterans Affairs", "Cookie Dough");
		
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("Vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("VANILLA");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("Vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToLegacyGWCount("Strawberry");
		
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("vanilla");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Strawberry");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Chocolate");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("ChocoLATE");
		DicomServiceStats.getInstance().incrementInboundObjectsPassedToHDIGDataStructureCount("Chocolate");
			
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs", "Cookie Dough");
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs", "Cookie Dough");
		DicomServiceStats.getInstance().incrementDuplicateInstanceUIDsCount("Veterans Affairs ", "Fudge Ripple ");
				
		DicomServiceStats.getInstance().incrementIODViolationCount("Veterans Affairs", "Neopolitan");
		DicomServiceStats.getInstance().incrementIODViolationCount("Veterans Affairs", "Neopolitan");
		
		InboundDicomObjectStatistics chocolate = DicomServiceStats.getInstance().getFromStorageSCPList("CHOCOLATE");
		assertEquals(2, chocolate.getTotalObjectsProcessed());
		assertEquals(0, chocolate.getTotalObjectsRejected());
		assertEquals(0, chocolate.getTotalObjectsPassedToLegacyGW());
		assertEquals(3, chocolate.getTotalObjectsPassedToHDIGDataStructure());
		assertEquals(0, chocolate.getTotalDuplicateObjects());

		InboundDicomObjectStatistics strawberry = DicomServiceStats.getInstance().getFromStorageSCPList("STRAWBERRY");
		assertEquals(1, strawberry.getTotalObjectsProcessed());
		assertEquals(1, strawberry.getTotalObjectsRejected());
		assertEquals(1, strawberry.getTotalObjectsPassedToLegacyGW());
		assertEquals(1, strawberry.getTotalObjectsPassedToHDIGDataStructure());
		assertEquals(1, strawberry.getTotalDuplicateObjects());

		InboundDicomObjectStatistics vanilla = DicomServiceStats.getInstance().getFromStorageSCPList("VANILLA");
		assertEquals(1, vanilla.getTotalObjectsProcessed());
		assertEquals(0, vanilla.getTotalObjectsRejected());
		assertEquals(3, vanilla.getTotalObjectsPassedToLegacyGW());
		assertEquals(2, vanilla.getTotalObjectsPassedToHDIGDataStructure());
		assertEquals(3, vanilla.getTotalDuplicateObjects());

		InboundModalityDeviceStatistics cookie = DicomServiceStats.getInstance().getFromModalityDeviceList("Veterans Affairs", "Cookie Dough");
		assertEquals(2, cookie.getTotalDuplicateInstanceUIDs());
		assertEquals(0, cookie.getTotalDicomObjectsProcessed());
		assertEquals(0, cookie.getTotalDicomObjectsRejected());
		assertEquals(0, cookie.getTotalDicomObjectsWithIODViolations());
		assertEquals(1, cookie.getTotalDuplicateObjects());

		InboundModalityDeviceStatistics cake = DicomServiceStats.getInstance().getFromModalityDeviceList("Veterans Affairs", "Party Cake");
		assertEquals(0, cake.getTotalDuplicateInstanceUIDs());
		assertEquals(2, cake.getTotalDicomObjectsProcessed());
		assertEquals(0, cake.getTotalDicomObjectsRejected());
		assertEquals(0, cake.getTotalDicomObjectsWithIODViolations());
		assertEquals(0, cake.getTotalDuplicateObjects());

		InboundModalityDeviceStatistics ripple = DicomServiceStats.getInstance().getFromModalityDeviceList("Veterans Affairs", "Fudge Ripple");
		assertEquals(1, ripple.getTotalDuplicateInstanceUIDs());
		assertEquals(2, ripple.getTotalDicomObjectsProcessed());
		assertEquals(1, ripple.getTotalDicomObjectsRejected());
		assertEquals(0, ripple.getTotalDicomObjectsWithIODViolations());
		assertEquals(3, ripple.getTotalDuplicateObjects());

		InboundModalityDeviceStatistics neo = DicomServiceStats.getInstance().getFromModalityDeviceList("Veterans Affairs", "Neopolitan");
		assertEquals(0, neo.getTotalDuplicateInstanceUIDs());
		assertEquals(0, neo.getTotalDicomObjectsProcessed());
		assertEquals(0, neo.getTotalDicomObjectsRejected());
		assertEquals(2, neo.getTotalDicomObjectsWithIODViolations());
		assertEquals(0, neo.getTotalDuplicateObjects());
		
		InboundSOPClassStatistics cr = DicomServiceStats.getInstance().getFromSOPClassList("Computed Radiography");
		assertEquals(2,cr.getTotalDicomObjectsProcessed());
		assertEquals(0,cr.getTotalDicomObjectsRejected());
		
		InboundSOPClassStatistics ct = DicomServiceStats.getInstance().getFromSOPClassList("Computed Tomography");
		assertEquals(1,ct.getTotalDicomObjectsProcessed());
		assertEquals(0,ct.getTotalDicomObjectsRejected());

		InboundSOPClassStatistics mr = DicomServiceStats.getInstance().getFromSOPClassList("Magnetic Resonance");
		assertEquals(1,mr.getTotalDicomObjectsProcessed());
		assertEquals(1,mr.getTotalDicomObjectsRejected());

	}
	
	public void testOutboundAssociations(){
		DicomServiceStats.getInstance().incrementOutboundAssociationAcceptCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundAssociationAcceptCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundAssociationRejectCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundAssociationAcceptCount("Chocolate");
		
		OutboundDicomAssociationStatistics chocolate = DicomServiceStats.getInstance().getFromOutboundAssociationList("Chocolate");
		assertEquals(3, chocolate.getTotalAcceptedAssociations());
		assertEquals(1, chocolate.getTotalRejectedAssociations());		
	}
	
	public void testOutboundObjects(){
		DicomServiceStats.getInstance().incrementOutboundObjectTransmittedCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundObjectTransmittedCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundObjectTransmittedCount("Chocolate");
		DicomServiceStats.getInstance().incrementOutboundObjectRejectedCount("Chocolate");
		
		OutboundDicomObjectStatistics chocolate = DicomServiceStats.getInstance().getFromStorageSCUList("Chocolate");
		assertEquals(3,chocolate.getTotalObjectsTransmittedToSCP());
		assertEquals(1, chocolate.getTotalObjectsRejectedBySCP());		
	}
	
	public void testDicomServiceStats(){
		DicomServiceStats.getInstance().setTotalRADObjectsToCorrectForHDIG(25);
		DicomServiceStats.getInstance().setTotalCONObjectsToCorrectForHDIG(50);
		assertEquals(25, DicomServiceStats.getInstance().getTotalRADObjectsToCorrectForHDIG());
		assertEquals(50, DicomServiceStats.getInstance().getTotalCONObjectsToCorrectForHDIG());
		
		DicomServiceStats.getInstance().setTotalRADObjectsToCorrectForHDIG(0);
		DicomServiceStats.getInstance().setTotalCONObjectsToCorrectForHDIG(0);
		assertEquals(0, DicomServiceStats.getInstance().getTotalRADObjectsToCorrectForHDIG());
		assertEquals(0, DicomServiceStats.getInstance().getTotalCONObjectsToCorrectForHDIG());
	}
}
