package gov.va.med.imaging.storage.cache.impl.timer;

import gov.va.med.imaging.storage.cache.exceptions.InitializationException;
import gov.va.med.imaging.storage.cache.exceptions.InvalidSweepSpecification;
import gov.va.med.imaging.storage.cache.memento.EvictionTimerImplMemento;
import gov.va.med.imaging.storage.cache.timer.EvictionTimerImpl;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class TestEvictionTimerImpl extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	public void testMemento() 
	throws InitializationException, InvalidSweepSpecification
	{
		Map<Long, String> sweepIntervalMap = new HashMap<Long, String>();
		
		sweepIntervalMap.put(new Long(60000L), "0000:00:00:00:00:10");
		sweepIntervalMap.put(new Long(3600000L), "0000:00:00:00:01:00");
		sweepIntervalMap.put(new Long(86400000L), "0000:00:00:01:00:00");
		sweepIntervalMap.put(EvictionTimerImpl.defaultAgeSpecification, "0000:00:01:00:00:00@0000:00:00:03:00:00");
		
		EvictionTimerImpl timer = EvictionTimerImpl.create(sweepIntervalMap);
		EvictionTimerImplMemento memento = timer.createMemento();
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(outStream);
		encoder.writeObject(memento);
		encoder.close();

		System.out.println(outStream.toString());
		
		XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(outStream.toByteArray()));
		EvictionTimerImplMemento restoredMemento = (EvictionTimerImplMemento)decoder.readObject();
		
		EvictionTimerImpl restoredTimer = EvictionTimerImpl.create(restoredMemento);
		
		assertTrue( timer.equals(restoredTimer) );
	}
}
