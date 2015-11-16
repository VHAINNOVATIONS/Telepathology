package gov.va.med.siteservice.siteprotocol;

import gov.va.med.RoutingToken;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;

public class TestProtocolPreferenceSet 
extends TestCase
{

	/**
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 * 
	 */
	public void testSerialization() 
	throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		SiteProtocolPreferenceFactoryImpl original = new SiteProtocolPreferenceFactoryImpl();
		original.add(ProtocolPreference.createForVARadiologySite("100", false, true, true, new String[]{"exchange"}));
		original.add(ProtocolPreference.createForVARadiologySite("200", true, false, true, new String[]{"xyz"}));
		original.add(ProtocolPreference.createForVARadiologySite("300", false, true, true, new String[]{"abc"}));
		original.add(ProtocolPreference.createForVARadiologySite(RoutingToken.ROUTING_WILDCARD, false, false, true, new String[]{"vftp", "vistaimaging"}));

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XStream outXstream = new XStream();
		outXstream.toXML(original, outStream);
		outStream.close();
		
		System.out.println(outStream.toString());

		XStream inXStream = new XStream();
		SiteProtocolPreferenceFactoryImpl copy = 
			(SiteProtocolPreferenceFactoryImpl)inXStream.fromXML( new ByteArrayInputStream(outStream.toByteArray()) );
		
		assertEquals(original, copy);
		
		assertNotNull(copy.getPreferredProtocols("999"));
		assertEquals(2, copy.getPreferredProtocols("999").length);
		assertEquals("vftp", copy.getPreferredProtocols("999")[0]);
		assertEquals("vistaimaging", copy.getPreferredProtocols("999")[1]);
		
		assertNotNull(copy.getPreferredProtocols("000"));
		assertEquals(2, copy.getPreferredProtocols("000").length);
		assertEquals("vftp", copy.getPreferredProtocols("000")[0]);
		assertEquals("vistaimaging", copy.getPreferredProtocols("000")[1]);
		
		assertNotNull(copy.getPreferredProtocols("100"));
		assertEquals(1, copy.getPreferredProtocols("100").length);
		assertEquals("exchange", copy.getPreferredProtocols("100")[0]);
		
		assertNotNull(copy.getPreferredProtocols("200"));
		assertEquals(1, copy.getPreferredProtocols("200").length);
		assertEquals("xyz", copy.getPreferredProtocols("200")[0]);
		
		assertNotNull(copy.getPreferredProtocols("300"));
		assertEquals(1, copy.getPreferredProtocols("300").length);
		assertEquals("abc", copy.getPreferredProtocols("300")[0]);
		
		assertEquals(false, copy.isSiteLocal("999"));
		assertEquals(false, copy.isSiteLocal("000"));
		assertEquals(false, copy.isSiteLocal("100"));
		assertEquals(true, copy.isSiteLocal("200"));
		assertEquals(false, copy.isSiteLocal("300"));
		
		assertEquals(false, copy.isSiteAlien("999"));
		assertEquals(false, copy.isSiteAlien("000"));
		assertEquals(true, copy.isSiteAlien("100"));
		assertEquals(false, copy.isSiteAlien("200"));
		assertEquals(true, copy.isSiteAlien("300"));
	}
}
