package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.EvictionTimer;
import gov.va.med.imaging.storage.cache.InstanceByteChannelFactory;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.*;
import gov.va.med.imaging.storage.cache.impl.AbstractCacheImpl;
import gov.va.med.imaging.storage.cache.impl.eviction.EvictionStrategyFactory;
import gov.va.med.imaging.storage.cache.impl.filesystem.memento.FileSystemCacheMemento;
import gov.va.med.imaging.storage.cache.impl.memento.PersistentRegionMemento;
import gov.va.med.imaging.storage.cache.memento.CacheMemento;
import gov.va.med.imaging.storage.cache.memento.EvictionStrategyMemento;
import gov.va.med.imaging.storage.cache.memento.RegionMemento;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The cache in the FileSystemCache is completely file system based. Groups and
 * Regions are implemented as directories, Instances are files. Region, Groups
 * and Instance instances in memory are transient reflections of the file system
 * state. In all cases the file system is the final arbiter of the state of the
 * cache.
 * 
 * Last access time is not universally supported by filesystems, and the Java IO
 * package does not make the access time available. This Cache uses the modified
 * date as the last access date. In general, external calls to the cache will
 * update the modified time while internal calls, used by eviction threads, do
 * not.
 */
public class FileSystemCache 
extends AbstractCacheImpl
{
	public final static String protocol = "file";
	private Logger log = Logger.getLogger(this.getClass());
	private File rootDirectory;

	/**
	 * Create a cache instance with just the name defined.
	 * 
	 * @return
	 */
	public static FileSystemCache create(String name, URI locationUri, EvictionTimer evictionTimer)
	throws CacheException
	{
		FileSystemCache cache = new FileSystemCache(name, locationUri, evictionTimer, FileSystemByteChannelFactory.create());
		
		return cache;
	}

	/**
	 * Create a cache instance from a Memento, which contains all of its state information.
	 * 
	 * @param memento
	 * @param timerImpl
	 * @return
	 * @throws CacheException
	 */
	public static FileSystemCache create(CacheMemento memento) 
	throws CacheException
	{
		FileSystemCache cache = new FileSystemCache(memento);
		
		return cache;
	}
	
	/**
	 * 
	 * @param byteChannelFactory
	 * @throws CacheException 
	 * @throws CacheException
	 */
	public FileSystemCache(CacheMemento memento) 
	throws CacheException
	{
		super(memento);
		log.info("constructing cache from memento '" + memento.getName() + "'");
		
		if(memento instanceof FileSystemCacheMemento)
			restoreFromMemento((FileSystemCacheMemento)memento);
	}

	/**
	 * The FileSystemCache will be started in a disabled mode when using this constructor.
	 * The MBean that manages this class may configure an instance of this class after it
	 * has been created.
	 * 
	 * @param name
	 * @param locationUri
	 * @param defaultTimer
	 * @param byteChannelFactory
	 * @throws CacheInitializationException 
	 */
	public FileSystemCache(String name, URI locationUri, EvictionTimer defaultTimer, InstanceByteChannelFactory<?> byteChannelFactory) 
	throws CacheInitializationException
	{
		super(name, locationUri, defaultTimer, byteChannelFactory);
	}

	@Override
	protected String extractPath(URI locationUri)
	{
		// a kludge cause windows uses the colon as the drive letter delimiter
		// which gets interpreted as the authority in URI form.
		String authority = locationUri.getAuthority();
		if(authority != null)
			return authority + "/" + locationUri.getPath();
		else
			return locationUri.getPath();
	}

	/**
	 * Between the time the cache is created and initialized, this contains the root directory name
	 * after initialization, the name should be derived from the rootDirectory.
	 * NOTE: the values may not be .equals() because of the differences in abstract versus
	 * concrete pathnames. 
	 */
	public String getPersistenceRoot()
	{
		if(isInitialized())
			try{ return getRootDirectory().getAbsolutePath(); } 
			catch (CacheStateException x){ log.error(x); return null; }		// should never happen
		else
			return getLocationPath();
	}
	
	/**
	 * 
	 * @return
	 * @throws CacheStateException 
	 */
	public File getRootDirectory() 
	throws CacheStateException
	{
		// Check whether the rootDirectory has been set rather than whether the cache has been initialized
		// because, internally, this method must return the root directory before initialization is complete.
		// Externally, isInitialized() and the existence of a non-null root directory are nearly synonomous.
		if( rootDirectory == null )
			throw new CacheStateException("File system cache must be initialized before the root directory is available.");

		return this.rootDirectory;
	}
	
	private void setRootDirectory(File rootDirectory)
	{
		this.rootDirectory = rootDirectory;
	}

	// ===============================================================================================================
	// Lifecycle and operation management (initialization and enablement) 
	// ===============================================================================================================
	/**
	 * @param properties
	 * @throws CacheException
	 */
	
	/**
	 * @throws InitializationException
	 * @throws CacheStateException
	 */
	@Override
	protected void internalInitialize() 
	throws InitializationException, CacheStateException
	{
		String rootDirName = getLocationPath();
		
		if (rootDirName == null || rootDirName.length() == 0)
			throw new InitializationException(
					"Root directory must be a valid absolute path specification, the root directory will be created if it does not exist");

		log.info("Cache initializing, location is '" + rootDirName + "'.");
		setRootDirectory(new File(rootDirName));
		
		if (!rootDirectory.exists())
		{
			log.info("Root directory '" + getRootDirectory().getAbsolutePath() + "' does not exist, creating ...");
			getRootDirectory().mkdirs();
		}
	}
	
	// ===========================================================================================================
	// Region Management Methods
	// ===========================================================================================================
	protected void validateRegionType(Region region)
	throws IncompatibleRegionException
	{
		if(! (region instanceof FileSystemRegion) )
			throw new IncompatibleRegionException(
					"Region '" + region.getName() + 
					"' is an instance of '" + region.getClass().getName() + 
					"' and is not an instance of FileSystemCacheRegion and is incompatible with FileSystemCache");
	}
	
	/**
	 * Create a Region instance that is compatible with this Cache instance.
	 * Default everything but the region name.
	 * NOTE: this method does not associate the Region instance created to the Cache,
	 * it simply creates the Region
	 * 
	 * @param name
	 */
	@Override
	public FileSystemRegion createRegion(String name, String[] evictionStrategyNames)
	throws RegionInitializationException
	{
		return FileSystemRegion.create(
			this, 
			name, 
			evictionStrategyNames, 
			defaultSecondsReadWaitsForWriteCompletion, 
			defaultSetModificationTimeOnRead
		);
	}

	@Override
	public FileSystemRegion createRegion(RegionMemento regionMemento)
	throws RegionInitializationException
	{
		if(regionMemento instanceof PersistentRegionMemento)
			return FileSystemRegion.create(
				this,
				(PersistentRegionMemento)regionMemento
			);
		
		throw new RegionInitializationException(PersistentRegionMemento.class,  regionMemento.getClass());
	}
	
	// ===========================================================================================
	// Memento (state persistence and loading) Related methods
	// ===========================================================================================
	@Override
	public FileSystemCacheMemento createMemento()
	{
		FileSystemCacheMemento memento = new FileSystemCacheMemento();
		
		memento.setEvictionTimerMemento(defaultEvictionTimer.createMemento());
		
		memento.setName(getName());
		memento.setLocationUri(getLocationUri().toString());
		memento.setEnabled(isEnabled());
		memento.setInitialized(isInitialized());
		
		if(getInstanceByteChannelFactory() instanceof FileSystemByteChannelFactory)
			memento.setByteChannelFactoryMemento(  ((FileSystemByteChannelFactory)getInstanceByteChannelFactory()).createMemento() );
		memento.setEvictionStrategyMementos(createEvictionStrategyMementos());
		memento.setRegionMementos(createRegionMementos());
		
		return memento;
	}
	
	protected List<? extends EvictionStrategyMemento> createEvictionStrategyMementos()
	{
		List<EvictionStrategyMemento> evictionStrategyMementos = new ArrayList<EvictionStrategyMemento>();
		for(EvictionStrategy evictionStrategy:getEvictionStrategies())
			evictionStrategyMementos.add( evictionStrategy.createMemento() );
		
		return evictionStrategyMementos;
	}
	
	protected List<PersistentRegionMemento> createRegionMementos()
	{
		List<PersistentRegionMemento> regionMementos = new ArrayList<PersistentRegionMemento>();
		for(Region region:getRegions())
			regionMementos.add( ((FileSystemRegion)region).createMemento() );
		
		return regionMementos;
	}
	
	/**
	 * The cache must be restored from a memento in the following order:
	 * 0.) the default eviction timer
	 * 1.) root directory name
	 * 2.) instance byte channel
	 * 3.) eviction strategies
	 * 4.) regions (needs instance byte channel and eviction strategies)
	 * 5.) initialized flag
	 * 6.) enabled flag
	 * 7.) the START signal may be acted upon, sending the START is the responsibility of
	 *     the FileSystemCacheManager
	 * 
	 * @param memento
	 * @throws CacheException 
	 */
	private void restoreFromMemento(FileSystemCacheMemento memento) 
	throws CacheException
	{
		// restore the byte channel factory
		setInstanceByteChannelFactory( FileSystemByteChannelFactory.create(memento.getByteChannelFactoryMemento()) );
		
		for( EvictionStrategyMemento evictionStrategyMemento : memento.getEvictionStrategyMementos() )
		{
			EvictionStrategy evictionStrategy = 
				EvictionStrategyFactory.getSingleton().createEvictionStrategy(evictionStrategyMemento, defaultEvictionTimer);
			addEvictionStrategy(evictionStrategy);
		}
		
		for(RegionMemento regionMemento:memento.getRegionMementos())
		{
			if(regionMemento instanceof PersistentRegionMemento)
			{
				FileSystemRegion region = FileSystemRegion.create(
					this, 
					(PersistentRegionMemento)regionMemento
				); 
				addRegion(region);
			}
		}
		
		// note that set initialized to true is much more than a simple bit flip
		if(memento.isInitialized())
			setInitialized(true);
		// setting enabled to true is pretty much a simple bit flip
		if(memento.isEnabled())
			setEnabled(true);
	}
}
