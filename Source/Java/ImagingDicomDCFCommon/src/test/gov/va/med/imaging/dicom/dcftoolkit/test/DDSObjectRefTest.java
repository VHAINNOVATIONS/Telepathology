/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 15, 2012
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
package gov.va.med.imaging.dicom.dcftoolkit.test;

import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.dicom.InstrumentConfig;
import gov.va.med.imaging.exchange.business.dicom.Series;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomSQElement;
import com.lbs.DCS.UID;

/**
 * @author vhaiswpeterb
 *
 */
public class DDSObjectRefTest extends DicomDCFCommonTestBase {

	/**
	 * @param arg0
	 */
	public DDSObjectRefTest(String arg0) {
		super(arg0);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl#getSeries(gov.va.med.imaging.exchange.business.dicom.DicomAE, gov.va.med.imaging.exchange.business.dicom.InstrumentConfig)}.
	 */
	@Test
	public void testGetSeriesNullInstrument() {
		
		// If there's a null instrument, we should fall back to the site id in DicomServerConfiguration.
		// Since config is a singleton, "prime" it with an expected siteId before running the test.
		DicomServerConfiguration config = DicomServerConfiguration.getConfiguration();
		config.setSiteId("12345");
		
		DicomAE ae = createDicomAE();
		DicomDataSetImpl dds = createDefaultDataSetForSeriesRef();
		if(dds == null){
			logger.error("Failed to build DicomDataSet");
			fail();
		}
		Series series = dds.getSeries(ae, null);
		assertEquals("12345", series.getAcqSite());
		assertEquals("REMOTEAET", series.getSourceAETitle());
		assertEquals("LOCALAET", series.getRetrieveAETitle());
		assertEquals("ACME, INC.", series.getSeriesCreator());
		assertEquals("LEAD BALLOON", series.getSeriesCreatorDeviceModel());
		assertEquals("CR", series.getModality());
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl#getSeries(gov.va.med.imaging.exchange.business.dicom.DicomAE, gov.va.med.imaging.exchange.business.dicom.InstrumentConfig)}.
	 */
	@Test
	public void testGetSeriesDivisionSiteID() {
		DicomAE ae = createDicomAE();
		InstrumentConfig instrument = createInstrument();
		instrument.setSiteId("777");		
		DicomDataSetImpl dds = createDefaultDataSetForSeriesRef();
		if(dds == null){
			logger.error("Failed to build DicomDataSet");
			fail();
		}
		Series series = dds.getSeries(ae, instrument);
		assertEquals("777", series.getAcqSite());
		assertEquals("REMOTEAET", series.getSourceAETitle());
		assertEquals("LOCALAET", series.getRetrieveAETitle());
		assertEquals("ACME, INC.", series.getSeriesCreator());
		assertEquals("LEAD BALLOON", series.getSeriesCreatorDeviceModel());
		assertEquals("CR", series.getModality());
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl#getSeries(gov.va.med.imaging.exchange.business.dicom.DicomAE, gov.va.med.imaging.exchange.business.dicom.InstrumentConfig)}.
	 */
	@Test
	public void testGetSeriesHDIGSiteID() {
		DicomAE ae = createDicomAE();
		InstrumentConfig instrument = createInstrument();	
		DicomDataSetImpl dds = createDefaultDataSetForSeriesRef();
		if(dds == null){
			logger.error("Failed to build DicomDataSet");
			fail();
		}
		Series series = dds.getSeries(ae, instrument);
		assertEquals("660", series.getAcqSite());
		assertEquals("REMOTEAET", series.getSourceAETitle());
		assertEquals("LOCALAET", series.getRetrieveAETitle());
		assertEquals("ACME, INC.", series.getSeriesCreator());
		assertEquals("LEAD BALLOON", series.getSeriesCreatorDeviceModel());
		assertEquals("CR", series.getModality());
	}

	
    private DicomDataSetImpl createDefaultDataSetForSeriesRef(){
        DicomDataSet dds = new DicomDataSet();
        DicomDataSetImpl initialDDS;
        
        try{
            dds.insert(DCM.E_SOPCLASS_UID, UID.SOPCLASSCOMPUTEDRADIOGRAPHY);
            dds.insert(DCM.E_SOPINSTANCE_UID, "1.2.840.1.1.2.4");
            dds.insert(DCM.E_STUDY_DATE, "01012007");
            dds.insert(DCM.E_MODALITY, "CR");
            dds.insert(DCM.E_ACCESSION_NUMBER, "010101-101");
            dds.insert(DCM.E_REFERRING_PHYSICIANS_NAME, "MELLMAN^LARRY^BUD");
            dds.insert(DCM.E_SERIES_DATE, "20120101");
            dds.insert(DCM.E_SERIES_TIME, "011500");
            dds.insert(DCM.E_BODY_PART_EXAMINED, "HEAD");
            dds.insert(DCM.E_PATIENTS_NAME, "BABALOU^BEAR");
            dds.insert(DCM.E_PATIENTS_BIRTH_DATE, "01011945");
            dds.insert(new AttributeTag("0008, 1030"), "Chest");
            dds.insert(DCM.E_PATIENT_ID, "000-00-1212");
            dds.insert(DCM.E_STUDY_INSTANCE_UID, "1.2.336866.22.3.4554.1");
            dds.insert(DCM.E_SERIES_INSTANCE_UID, "1.2.336866.22.3.4554.1.3");
            dds.insert(DCM.E_SERIES_NUMBER, "565");
            dds.insert(DCM.E_SERIES_DESCRIPTION, "No Series Description");
            dds.insert(DCM.E_MANUFACTURER, "ACME, INC.");
            dds.insert(DCM.E_MANUFACTURERS_MODEL_NAME, "LEAD BALLOON");
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
    
	private InstrumentConfig createInstrument() {
		InstrumentConfig instrument = new InstrumentConfig();
		instrument.setHostName("vhaiswimgvms1212");
		instrument.setMachineId("vhaiswimgvmsdig1");
		instrument.setNickName("Fudge");
		instrument.setPort(3333);
		instrument.setService("RAD");
		instrument.setSiteId("660");
		instrument.setSite("Salt Lake City");
		return instrument;
	}

	private DicomAE createDicomAE() {
		DicomAE ae = new DicomAE();
		ae.setHostName("vhaiswimgvms1001");
		ae.setImagingService("RAD");
		ae.setLocalAETitle("LOCALAET");
		ae.setPort("2222");
		ae.setRemoteAETitle("REMOTEAET");
		ae.setSiteNumber("888");
		return ae;
	}


}
