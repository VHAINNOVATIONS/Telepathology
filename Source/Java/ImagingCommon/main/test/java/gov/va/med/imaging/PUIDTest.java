package gov.va.med.imaging;

import junit.framework.TestCase;

public class PUIDTest extends TestCase
{
	// all we really test is that the value is always the same in a single process
	public void testCreate()
	{
		PUID puid = new PUID();
		PUID puid2 = new PUID();
		
		System.out.println("PUID is [" + puid.toString() + "]");
		assertEquals(puid.toString(), puid2.toString());
	}
}
