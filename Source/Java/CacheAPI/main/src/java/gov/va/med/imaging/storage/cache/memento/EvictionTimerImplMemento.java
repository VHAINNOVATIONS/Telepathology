package gov.va.med.imaging.storage.cache.memento;

import java.beans.XMLDecoder;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class EvictionTimerImplMemento
extends EvictionTimerMemento
implements Serializable
{
	private static final long serialVersionUID = -1226753608913657581L;
	
	public static EvictionTimerImplMemento createDefault()
	{
		EvictionTimerImplMemento memento = null;
		XMLDecoder decoder = null;
		
		InputStream inStream = EvictionTimerImplMemento.class.getClassLoader().getResourceAsStream("defaultEvictionTimerMemento.xml");
		if(inStream != null)
		{
			decoder = new XMLDecoder(inStream);
			
			memento = (EvictionTimerImplMemento)decoder.readObject();
			
			decoder.close();
		}
		
		return memento;
	}

	// map from the max age to the scheduled sweep time (as an interval specification)
	// the order of the Map is critical so assure that the order is preserved by
	// using a sorted Map instance
	private Map<Long, String> sweepIntervalMap = new TreeMap<Long, String>();
	
	public EvictionTimerImplMemento()
	{
		
	}
	
	public void addSweepInterval(Long max, String intervalSpecification)
	{
		sweepIntervalMap.put(max, intervalSpecification);
	}
	
	public Map<Long, String> getSweepIntervalMap()
	{
		return sweepIntervalMap;
	}
	
	public void setSweepIntervalMap(Map<Long, String> map)
	{
		sweepIntervalMap = map;
	}
}
