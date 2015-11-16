/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.timer;

import gov.va.med.imaging.storage.cache.memento.EvictionTimerImplMemento;

import java.beans.XMLEncoder;

/**
 * @author VHAISWBECKEC
 *
 */
public class EvictionTimerImplUtil
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		EvictionTimerImplMemento memento = new EvictionTimerImplMemento();
		
		memento.addSweepInterval(3600000L, "0000:00:00:00:01:00");	// if less than an hour then next minute
		memento.addSweepInterval(86400000L, "0000:00:00:01:00:00");	// if less than a day then next hour
		memento.addSweepInterval(Long.MAX_VALUE, "0000:00:01:00:00:00@0000:00:00:03:00:00");	//else, run it at 3AM 
		
		XMLEncoder encoder = new XMLEncoder(System.out);
		
		encoder.writeObject(memento);
		
		encoder.close();
	}

}
