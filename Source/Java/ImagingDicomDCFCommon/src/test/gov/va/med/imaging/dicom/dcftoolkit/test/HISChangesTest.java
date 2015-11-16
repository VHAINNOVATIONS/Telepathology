/*
 * Created on Apr 24, 2007
 *
 */
package gov.va.med.imaging.dicom.dcftoolkit.test;

import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomElementImpl;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;

import java.util.HashMap;

import org.junit.Test;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomSQElement;
import com.lbs.DCS.UID;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class HISChangesTest extends DicomDCFCommonTestBase {

    /**
     * Constructor
     *
     * @param arg0
     */
    public HISChangesTest(String arg0) {
        super(arg0);
    }
    
    /*
     * @see DicomDCFSCUTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see DicomDCFSCUTestBase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testNullHISValues(){
        
        TESTLOGGER.debug("Test with null HIS Values");
        DicomDataSetImpl dds = this.createDefaultDataSet();
        
        HashMap<String, String> hisChanges = new HashMap<String, String>();
        hisChanges.put("0008,0060", null);
        hisChanges.put("0032,1064 0008,0104", null);
        try{
            dds.updateHISChangesToDDS(hisChanges);
        	DicomElementImpl element = (DicomElementImpl)dds.getDicomElement("0008,0060");
        	assertEquals("CR", element.getStringValue());

        	assertEquals("Chest 2 Views", dds.getDicomElementValue("0032,1064", "0008,0104"));
        	        	
        }
        catch(DicomException de){
        	de.printStackTrace();
        	fail();
        }
    }
 
    @Test
    public void testEmptyHISValue(){
        
    	TESTLOGGER.debug("Test with Empty HIS values.");
        DicomDataSetImpl dds = this.createDefaultDataSet();

        HashMap<String, String> hisChanges = new HashMap<String, String>();
        hisChanges.put("0008,0060", "");
        hisChanges.put("0032,1064 0008,0104", "");
        try{
            dds.updateHISChangesToDDS(hisChanges);
        	DicomElementImpl element = (DicomElementImpl)dds.getDicomElement("0008,0060");
        	assertEquals("CR", element.getStringValue());

        	assertEquals("", dds.getDicomElementValue("0032,1064", "0008,0104"));

        }
        catch(DicomException de){
            de.printStackTrace();
            fail();
        }
    }
    
	@Test
	public void testModalityCodeOne() {
		TESTLOGGER.debug("Test Modality Code One");
        DicomDataSetImpl dds = this.createDefaultDataSet();
		
		HashMap<String, String> changes = new HashMap<String, String>();
		changes.put("0010,0010", "PATIENT^TEST");
		changes.put("0010,0020", "000000010");
		changes.put("0010,0030", "19540101");
		changes.put("0010,0040", "M");
		
		try {
            dds.updateHISChangesToDDS(changes);
        	DicomElementImpl element = (DicomElementImpl)dds.getDicomElement("0008,0060");
        	assertEquals("CR", element.getStringValue());

		} catch (DicomException e) {
			e.printStackTrace();
			fail();
		}		
	}

	
	@Test
	public void testModalityCodeTwo() {
		TESTLOGGER.debug("Test Modality Code Two");
        DicomDataSetImpl dds = this.createDefaultDataSet();
		
		HashMap<String, String> changes = new HashMap<String, String>();
		changes.put("0010,0010", "PATIENT^TEST");
		changes.put("0010,0020", "000000010");
		changes.put("0010,0030", "19540101");
		changes.put("0010,0040", "M");
		changes.put("0008,0060", "MR");
		
		try {
            dds.updateHISChangesToDDS(changes);
        	DicomElementImpl element = (DicomElementImpl)dds.getDicomElement("0008,0060");
        	assertEquals("CR", element.getStringValue());
		} 
		catch (DicomException e) {
			e.printStackTrace();
			fail();
		}		
	}

	@Test
	public void testModalityCodeThree() {
		TESTLOGGER.debug("Test Modality Code Three");
        DicomDataSetImpl dds = this.createDefaultDataSet();
		
		HashMap<String, String> changes = new HashMap<String, String>();
		changes.put("0010,0010", "PATIENT^TEST");
		changes.put("0010,0020", "000000010");
		changes.put("0010,0030", "19540101");
		changes.put("0010,0040", "M");
		changes.put("0008,0060", "CT,MR");
		
		try {
            dds.updateHISChangesToDDS(changes);
        	DicomElementImpl element = (DicomElementImpl)dds.getDicomElement("0008,0060");
        	assertEquals("CR", element.getStringValue());
		} catch (DicomException e) {
			e.printStackTrace();
			fail();
		}		
	}

	@Test
	public void testModalityCodeFour() {
		TESTLOGGER.debug("Test Modality Code Four");
        DicomDataSetImpl dds = this.createDefaultDataSet();
		
		HashMap<String, String> changes = new HashMap<String, String>();
		changes.put("0010,0010", "PATIENT^TEST");
		changes.put("0010,0020", "000000010");
		changes.put("0010,0030", "19540101");
		changes.put("0010,0040", "M");
		changes.put("0008,0060", "DX\\US");
		
		try {
            dds.updateHISChangesToDDS(changes);
        	DicomElementImpl element = (DicomElementImpl)dds.getDicomElement("0008,0060");
        	assertEquals("CR", element.getStringValue());
		} catch (DicomException e) {
			e.printStackTrace();
			fail();
		}		
	}


    private DicomDataSetImpl createDefaultDataSet(){
        DicomDataSet dds = new DicomDataSet();
        DicomDataSetImpl initialDDS;
        
        try{
            dds.insert(DCM.E_SOPCLASS_UID, UID.SOPCLASSCOMPUTEDRADIOGRAPHY);
            dds.insert(DCM.E_SOPINSTANCE_UID, "1.2.840.1.1.2.4");
            dds.insert(DCM.E_STUDY_DATE, "01012007");
            dds.insert(DCM.E_MODALITY, "CR");
            dds.insert(DCM.E_ACCESSION_NUMBER, "010101-101");
            dds.insert(DCM.E_REFERRING_PHYSICIANS_NAME, "MELLMAN^LARRY^BUD");
            dds.insert(DCM.E_PATIENTS_NAME, "BABALOU^BEAR");
            dds.insert(DCM.E_PATIENTS_BIRTH_DATE, "01011945");
            dds.insert(new AttributeTag("0008, 1030"), "Chest");
            dds.insert(DCM.E_PATIENT_ID, "000-00-1212");
            dds.insert(DCM.E_STUDY_INSTANCE_UID, "1.2.336866.22.3.4554.1");
            dds.insert(DCM.E_CURRENT_PATIENT_LOCATION, "Hospital");
            DicomDataSet seq = new DicomDataSet();
            seq.insert(new AttributeTag("0008,0100"), "01");
            seq.insert(new AttributeTag("0008,0102"), "DEMO");
            seq.insert(new AttributeTag("0008,0104"), "Chest 2 Views");
            DicomSQElement seqElement = new DicomSQElement(new AttributeTag("0032,1064"), seq);
            dds.insert(seqElement);
            
            initialDDS = new DicomDataSetImpl(dds);
        }
        catch(DCSException dcs){
        	TESTLOGGER.debug("Failed building initial DDS.");
            return null;
        }
        return initialDDS;
    }
    
}
