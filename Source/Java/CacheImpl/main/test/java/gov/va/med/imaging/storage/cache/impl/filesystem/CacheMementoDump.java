package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategyMemento;
import gov.va.med.imaging.storage.cache.impl.filesystem.memento.FileSystemCacheMemento;
import gov.va.med.imaging.storage.cache.impl.memento.PersistentRegionMemento;
import gov.va.med.imaging.storage.cache.memento.ByteChannelFactoryMemento;
import gov.va.med.imaging.storage.cache.memento.EvictionTimerImplMemento;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class CacheMementoDump extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * A test driver that will simply print a memento to system.out.
	 * This can be useful for manually editing existing files when new fields are added.
	 * 
	 * @param argv
	 */
	public void testSampleFileSystemCacheMemento()
	{
		FileSystemCacheMemento memento = new FileSystemCacheMemento();
		memento.setEnabled(true);
		memento.setInitialized(true);
		memento.setName("cache-name");
		
		ByteChannelFactoryMemento ibcfiMemento = new ByteChannelFactoryMemento();
		ibcfiMemento.setMaxChannelOpenDuration(new Long(1000));
		ibcfiMemento.setSweepTime(new Long(10000));
		memento.setByteChannelFactoryMemento(ibcfiMemento);
		
		EvictionTimerImplMemento eviMemento = new EvictionTimerImplMemento();
		Map<Long, String> sweepMap = new HashMap<Long, String>();
		sweepMap.put(new Long(1), "0000:00:01:00:00:00@0000:00:00:00:00:00");
		eviMemento.setSweepIntervalMap(sweepMap);
		memento.setEvictionTimerMemento(eviMemento);
		
		LastAccessedEvictionStrategyMemento laesMemento = new LastAccessedEvictionStrategyMemento(); 
		laesMemento.setMaximumTimeSinceLastAccess(10L);
		laesMemento.setName("LastAccessedEvictionStrategy");
		List<LastAccessedEvictionStrategyMemento> evmList= new ArrayList<LastAccessedEvictionStrategyMemento>();
		evmList.add(laesMemento);
		memento.setEvictionStrategyMementos(evmList);
		
		PersistentRegionMemento fscrMemento = new PersistentRegionMemento();
		fscrMemento.setEvictionStrategyNames( new String[]{"LastAccessEvictionStrategy"} );
		fscrMemento.setName("region-name");
		fscrMemento.setSecondsReadWaitsForWriteCompletion(60);
		fscrMemento.setSetModificationTimeOnRead(true);
		List<PersistentRegionMemento> regionMementos = new ArrayList<PersistentRegionMemento>();
		regionMementos.add(fscrMemento);
		memento.setRegionMementos(regionMementos);
		
		XMLEncoder encoder = null;
		
		try
		{
			File tempFile = File.createTempFile("fscache", ".xml", new File("/"));
			encoder = new XMLEncoder(new FileOutputStream(tempFile));
			
			encoder.writeObject(memento);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{encoder.close();}catch(Throwable t){}
		}
	}
}
