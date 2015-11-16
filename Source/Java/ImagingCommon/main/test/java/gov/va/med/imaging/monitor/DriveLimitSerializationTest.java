/**
 * 
 */
package gov.va.med.imaging.monitor;

import gov.va.med.imaging.SerializationDeserialization;

import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class DriveLimitSerializationTest 
extends TestCase
{

	public void testSerialization() 
	throws DriveLimitInitializationException
	{
		DriveLimit original;
		DriveLimit clone;
		SerializationDeserialization<DriveLimitMemento> serdes = new SerializationDeserialization<DriveLimitMemento>();
		
		original = DriveLimit.create("c", DriveLimit.LimitType.MONITOR, DriveLimit.LimitStatistic.FREE_SPACE, 0L);
		DriveLimitMemento originalMemento = original.getMemento();
		DriveLimitMemento cloneMemento = serdes.serializeAndDeserialize(originalMemento); 
		clone = DriveLimit.create( cloneMemento );
		assertEquals(original, clone);
	}

	
}
