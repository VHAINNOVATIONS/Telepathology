package gov.va.med.imaging.vistaimagingdatasource.storage;


import gov.va.med.imaging.exchange.business.storage.Place;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlaceDAOTests extends TestCase {

	PlaceDAO dao = new PlaceDAO();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPlaceDAO1(){
		
		List<Place> places = new Vector<Place>();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("0^^1");
		builder.append("\r\n");
		builder.append("IEN^Site IEN^Site Number^Site Name^Net UserName^Net Password");
		builder.append("\r\n");
		builder.append('1');
		builder.append('^');
		builder.append("7247");
		builder.append('^');
		builder.append("7247");
		builder.append('^');
		builder.append("2010 DEMO HOSPITAL");
		builder.append('^');
		builder.append("vhamaster");
		builder.append('\\');
		builder.append("vhaiswia");
		builder.append('^');
		builder.append('"');
		builder.append('r');
		builder.append('k');
		builder.append('k');
		builder.append('N');
		builder.append('`');
		builder.append('`');
		builder.append('X');
		builder.append('%');
		builder.append('*');

		try {
			places = dao.translateFindAll(builder.toString());
			Place place= places.get(0);
			assertEquals("Access1.", place.getPassword());
		} 
		catch (RetrievalException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testPlaceDAO2(){
		
		List<Place> places = new Vector<Place>();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("0^^2");
		builder.append("\r\n");
		builder.append("IEN^Site IEN^Site Number^Site Name^Net UserName^Net Password");
		builder.append("\r\n");
		builder.append("1^660^660^SALT LAKE CITY^vhamaster\\vhaiswIU^(WSSq..%\\ ");
		builder.append("\r\n");
		builder.append("2^589^589^KANSAS CITY, MO^vhamaster\\vhaiswIU^\'bAAj&&0+&");

		try {
			places = dao.translateFindAll(builder.toString());
			Place place= places.get(0);
			assertEquals("Access1.", place.getPassword());
		} 
		catch (RetrievalException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testPlaceDAO3(){
		
		List<Place> places = new Vector<Place>();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("0^^4\r\n");
		builder.append("IEN^Site IEN^Site Number^Site Name^Net UserName^Net Password");
		builder.append("\r\n");
		builder.append("1^657^657^ST. LOUIS MO VAMC-JC DIVISION^vha15\\vhastlia^/W/$\'Pz&");
		builder.append("\r\n");
		builder.append("2^445^657A4^POPLAR BLUFF MO VAMC^vha15\\vhapopiu^,xv&~-gs>s&");
		builder.append("\r\n");
		builder.append("3^657.1^657A0^ST. LOUIS MO VAMC-JB DIVISION^vha15\\vhastliu^(,abPS&yr{/");
		builder.append("\r\n");
		builder.append("36^456^657A5^MARION IL VAMC^vha15\\vhamrniu^ =gh99`HK1");

		try {
			places = dao.translateFindAll(builder.toString());
			Place place= places.get(2);
			assertEquals("657.1", place.getSiteId());
		} 
		catch (RetrievalException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testPlaceDAO4(){
		
		List<Place> places = new Vector<Place>();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("0^^2\r\n");
		builder.append("IEN^Site IEN^Site Number^Site Name^Net UserName^Net Password");
		builder.append("\r\n");
		builder.append("1^657^657^ST. LOUIS MO VAMC-JC DIVISION^vha15\\vhastlia^");
		builder.append("\r\n");
		builder.append("2^445^657A4^POPLAR BLUFF MO VAMC^vha15\\vhapopiu^,xv&~-gs>s&");

		try {
			places = dao.translateFindAll(builder.toString());
			Place place= places.get(0);
			assertEquals("", place.getPassword());
		} 
		catch (RetrievalException e) {
			e.printStackTrace();
			fail();
		}
	}

	/*
	public void testPlaceDAO4(){
		
		List<Place> places = new Vector<Place>();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("0^^3\r\n");
		builder.append("IEN^Site IEN^Site Number^Site Name^Net UserName^Net Password");
		builder.append("\r\n");
		builder.append("1^660^660^SALT LAKE CITY^vhaiswia^#;ss:PPV,3\r\n");
		builder.append("2^589^589^KANSAS CITY, MO^vhamaster\\vhaiswIU^\'bAAj&&0+&\r\n");
		builder.append("3^660.1^^Test decimal IEN^^");

		try {
			places = dao.translateFindAll(builder.toString());
			Place place= places.get(0);
			assertEquals("image", place.getPassword());
		} 
		catch (RetrievalException e) {
			e.printStackTrace();
			fail();
		}
	}
	*/

/*
	@Test
	public void testPlaceDAO4(){
		
		List<Place> places = new Vector<Place>();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("0^^1\r\n");
		builder.append("IEN^Site IEN^Site Number^Site Name^Net UserName^Net Password");
		builder.append("\r\n");
		builder.append("41^421^657GA^BELLEVILLE IL CBOC^vha15\\vhastliu^&cj)?U ");

		try {
			places = dao.translateFindAll(builder.toString());
			Place place= places.get(0);
			assertEquals("image", place.getPassword());
		} 
		catch (RetrievalException e) {
			e.printStackTrace();
			fail();
		}
	}
*/
}
