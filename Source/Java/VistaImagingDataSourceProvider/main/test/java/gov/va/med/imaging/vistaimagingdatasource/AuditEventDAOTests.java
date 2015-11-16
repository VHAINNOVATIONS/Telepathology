package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.AuditEvent;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.VistaQuery.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuditEventDAOTests extends TestCase{

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateCreateQueryAuditEvent() {
		HashMap<String, String> eventElements = new HashMap<String, String>();
		eventElements.put("AETitle", "FUDGE");
		eventElements.put("DUZ", "126");
		String message = "FUDGE, CFind Request.";
		AuditEvent event = new AuditEvent(AuditEvent.DICOM_QUERY, 
				"vhaiswimgvms20",
				"HDIG",
				message,
				eventElements);
		AuditEventDAO dao = new AuditEventDAO(null);
		VistaQuery vm = null;
		try{
			vm = dao.generateCreateQuery(event);
		}
		catch(Exception X){
			X.printStackTrace();
			fail();
		}
		assertEquals("MAG EVENT AUDIT", vm.getRpcName());
		assertTrue(vm.getParamCount() == 5);
		ArrayList<Parameter> list = (ArrayList<Parameter>) vm.getParams();
		assertTrue(list.get(0).getValue().equals("DICOM QUERY"));
		assertTrue(list.get(1).getValue().equals("vhaiswimgvms20"));
		assertTrue(list.get(2).getValue().equals("HDIG"));
		Map<String, String> map = (Map<String, String>) list.get(3).getList();
		String duz = map.get("1");
		String aet = map.get("2");
		assertTrue(duz.equals("DUZ`126"));
		assertTrue(aet.equals("AETitle`FUDGE"));
		assertTrue(list.get(4).getValue() == "FUDGE, CFind Request.");
	}

	
	@Test
	public void testTranslateAuditEventResult1() {
		String returnValue = "0";
		String message = "FUDGE, CFind Request.";
		AuditEvent event = new AuditEvent(AuditEvent.DICOM_QUERY, 
				"vhaiswimgvms20",
				"HDIG",
				message);

		AuditEventDAO dao = new AuditEventDAO(null);
		AuditEvent result = null;
		try {
			result = dao.translateCreate(event, returnValue);
		} catch (CreationException C) {
			C.printStackTrace();
			fail();
		} catch (MethodException M) {
			M.printStackTrace();
			fail();
		}
		assertTrue(result.isSuccessful() == true);
	}

	@Test
	public void testTranslateAuditEventResult2() {
		String returnValue = "-205,File# 2006.93 and IEN string +1, represent different subfile levels.";
		String message = "FUDGE, CFind Request.";
		AuditEvent event = new AuditEvent(AuditEvent.DICOM_QUERY, 
				"vhaiswimgvms20",
				"HDIG",
				message);

		AuditEventDAO dao = new AuditEventDAO(null);
		AuditEvent result = null;
		try {
			result = dao.translateCreate(event, returnValue);
		} catch (CreationException C) {
			C.printStackTrace();
			fail();
		} catch (MethodException M) {
			M.printStackTrace();
			fail();
		}
		assertTrue(result.isSuccessful() == false);
	}
}
