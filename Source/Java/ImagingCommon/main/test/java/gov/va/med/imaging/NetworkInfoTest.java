package gov.va.med.imaging;

import junit.framework.TestCase;

public class NetworkInfoTest extends TestCase
{

	public void testMacAddressMatching()
	{
		assertTrue( NetworkInfo.windowsIsMacAddress("00-00-00-00-00-00") );		
		assertTrue( NetworkInfo.windowsIsMacAddress("FF-FF-FF-FF-FF-FF") );		
		assertFalse( NetworkInfo.windowsIsMacAddress("FX-FF-FF-FF-FF-FF") );		
		assertFalse( NetworkInfo.windowsIsMacAddress("FF") );
		assertFalse( NetworkInfo.windowsIsMacAddress("FF-") );
		assertFalse( NetworkInfo.windowsIsMacAddress("") );
		assertFalse( NetworkInfo.windowsIsMacAddress(null) );
	}

}
