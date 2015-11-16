package gov.va.med.imaging.exchange.business.dicom;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstrumentConfigTest extends TestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInstrumentConfigSiteID(){
		InstrumentConfig instrument = new InstrumentConfig();
		instrument.setDescription("Test");
		instrument.setHostName("vhaiswimgvms111");
		instrument.setMachineId("vhaiswimgvms777");
		instrument.setService("RAD");
		instrument.setSiteId("222");
		
		assertEquals("vhaiswimgvms777", instrument.getMachineId());
		assertEquals("RAD", instrument.getService());
		assertEquals("222", instrument.getSiteId());
	}
	
	@Test
	public void testInstrumentConfigSiteIDEmpty(){
		InstrumentConfig instrument = new InstrumentConfig();
		instrument.setDescription("Test");
		instrument.setHostName("vhaiswimgvms111");
		instrument.setMachineId("vhaiswimgvms777");
		instrument.setService("RAD");
		instrument.setSiteId("");
		
		assertEquals("vhaiswimgvms777", instrument.getMachineId());
		assertEquals("RAD", instrument.getService());
		assertEquals("", instrument.getSiteId());
	}

	@Test
	public void testInstrumentConfigSiteIDNull(){
		InstrumentConfig instrument = new InstrumentConfig();
		instrument.setDescription("Test");
		instrument.setHostName("vhaiswimgvms111");
		instrument.setMachineId("vhaiswimgvms777");
		instrument.setService("RAD");
		
		assertEquals("vhaiswimgvms777", instrument.getMachineId());
		assertEquals("RAD", instrument.getService());
		assertEquals(null, instrument.getSiteId());
	}

}
