package gov.va.med;


import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestTopLevelMediaType
	extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.imaging.exchange.enums.TopLevelMediaType#lookup(java.lang.String)}.
	 */
	public void testLookup()
	{
		assertTrue( TopLevelMediaType.APPLICATION == TopLevelMediaType.lookup("application") );
		assertTrue( TopLevelMediaType.AUDIO == TopLevelMediaType.lookup("audio") );
		assertTrue( TopLevelMediaType.IMAGE == TopLevelMediaType.lookup("image") );
		assertTrue( TopLevelMediaType.MESSAGE == TopLevelMediaType.lookup("message") );
		assertTrue( TopLevelMediaType.MULTIPART == TopLevelMediaType.lookup("multipart") );
		assertTrue( TopLevelMediaType.TEXT == TopLevelMediaType.lookup("text") );
		assertTrue( TopLevelMediaType.VIDEO == TopLevelMediaType.lookup("video") );
		
		assertTrue( TopLevelMediaType.APPLICATION == TopLevelMediaType.lookup("Application") );
		assertTrue( TopLevelMediaType.AUDIO == TopLevelMediaType.lookup("AUDIO") );
		assertTrue( TopLevelMediaType.IMAGE == TopLevelMediaType.lookup("iMaGe") );
		assertTrue( TopLevelMediaType.MESSAGE == TopLevelMediaType.lookup("MeSsAgE") );
		assertTrue( TopLevelMediaType.MULTIPART == TopLevelMediaType.lookup("MULTIPART") );
		assertTrue( TopLevelMediaType.TEXT == TopLevelMediaType.lookup("texT") );
		assertTrue( TopLevelMediaType.VIDEO == TopLevelMediaType.lookup("viDeo") );
		
		assertTrue( null == TopLevelMediaType.lookup("tele") );
		assertTrue( null == TopLevelMediaType.lookup("APP") );
		assertTrue( null == TopLevelMediaType.lookup("SOUND") );
		assertTrue( null == TopLevelMediaType.lookup("ascii") );
	}

}
