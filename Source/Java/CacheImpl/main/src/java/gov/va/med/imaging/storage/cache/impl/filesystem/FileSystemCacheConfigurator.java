package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.EvictionTimer;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.*;
import gov.va.med.imaging.storage.cache.impl.CacheConfigurator;
import gov.va.med.imaging.storage.cache.impl.eviction.LastAccessedEvictionStrategy;
import gov.va.med.imaging.storage.cache.timer.EvictionTimerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used basically once in the life of a VIX when it is first
 * run.
 * This class will initialize a FileSystemCache instance with the expected
 * regions, eviction strategies and byte channel factories.
 * 
 * The cache properties will be:
 * 
 * initialized = false
 * enabled = false
 * default EvictionTimerImpl
 *  ten-second interval
 *  minute interval
 *  hour interval
 *  day interval
 * default InstanceByteChannelFactoryImpl
 * default EvictionStrategies (LastAccessedEvictionStrategy instances)
 *  ten-second-lifespan
 * 	one-minute-lifespan
 *  one-hour-lifespan
 *  one-day-lifespan
 *  seven-day-lifespan
 *  thirty-day-lifespan
 * default Regions
 *  test-metadata-region
 *  test-image-region
 *  va-metadata-region
 *  va-image-region
 *  dod-metadata-region
 *  dod-image-region
 *  
 * @author VHAISWBECKEC
 *
 */
public class FileSystemCacheConfigurator 
implements CacheConfigurator
{
	public FileSystemCacheConfigurator()
	{
		
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.impl.filesystem.CacheConfigurator#getEvictionTimerSweepIntervalMap()
	 */
	public Map<Long, String> getEvictionTimerSweepIntervalMap()
	{
		Map<Long, String> sweepIntervalMap = new HashMap<Long, String>();
		
		sweepIntervalMap.put(new Long(60000L), "0000:00:00:00:00:10"); 		// if less than a minute then next 10 seconds, this is mostly here for testing
		sweepIntervalMap.put(new Long(3600000L), "0000:00:00:00:01:00"); 	// if less than an hour then next minute
		sweepIntervalMap.put(new Long(86400000), "0000:00:00:01:00:00"); 	// if less than a day then next hour
		sweepIntervalMap.put(EvictionTimerImpl.defaultAgeSpecification, "0000:00:01:00:00:00@0000:00:00:03:00:00"); // else, run it once a day at 3AM
		
		return sweepIntervalMap;
	}

}
