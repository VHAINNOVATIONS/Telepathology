package gov.va.med.imaging;

import junit.framework.TestCase;

public class IPv4StringTest extends TestCase
{

	public void testCreate()
	{
		assertNotNull( IPv4String.create("0.0.0.0") );
		assertNotNull( IPv4String.create("1.1.1.1") );
		assertNotNull( IPv4String.create("255.255.255.255") );
	}

	public void testCompareTo()
	{
		assertTrue( IPv4String.create("0.0.0.0").compareTo(IPv4String.create("0.0.0.1")) < 0 );
		assertTrue( IPv4String.create("0.0.0.1").compareTo(IPv4String.create("0.0.0.1")) == 0 );
		assertTrue( IPv4String.create("0.0.0.1").compareTo(IPv4String.create("0.0.0.0")) > 0 );
		
		assertTrue( IPv4String.create("1.0.0.0").compareTo(IPv4String.create("0.0.0.1")) > 0 );
		assertTrue( IPv4String.create("1.0.0.0").compareTo(IPv4String.create("0.255.255.255")) > 0 );
		
		assertTrue( IPv4String.create("239.0.0.0").compareTo(IPv4String.administrativelyScopedMinimum) == 0 );
		assertTrue( IPv4String.create("239.0.0.1").compareTo(IPv4String.administrativelyScopedMinimum) > 0 );
		assertTrue( IPv4String.create("239.255.255.255").compareTo(IPv4String.administrativelyScopedMinimum) > 0 );
		assertTrue( IPv4String.create("239.0.0.0").compareTo(IPv4String.administrativelyScopedMaximum) < 0 );
		assertTrue( IPv4String.create("239.0.0.1").compareTo(IPv4String.administrativelyScopedMaximum) < 0 );
		assertTrue( IPv4String.create("239.255.255.255").compareTo(IPv4String.administrativelyScopedMaximum) == 0 );
	}

}
