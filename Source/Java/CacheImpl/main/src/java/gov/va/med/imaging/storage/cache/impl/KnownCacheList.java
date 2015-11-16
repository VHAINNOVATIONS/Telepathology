package gov.va.med.imaging.storage.cache.impl;

import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.exceptions.CacheInitializationException;
import gov.va.med.imaging.storage.cache.memento.CacheMemento;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 * 
 * A Cache specific list that:
 * is appropriately synchronized
 * prevents duplicate entries (same name)
 * maintains synchronization with the persistent storage
 * 
 * NOTE: in general public methods should be synchronized to assure that
 * this class can keep the config files and the internal known file
 * list in synch.
 * 
 * 
 */
public class KnownCacheList
implements Iterable<Cache>
{
	public static final String cacheConfigFileNameSuffix = "-cache.xml";
	
	private Logger logger = Logger.getLogger(this.getClass());
	private File configurationDirectory;
	private List<Cache> wrappedList = new ArrayList<Cache>();
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param configurationDirectory
	 * @param cacheFactory
	 */
	KnownCacheList(File configurationDirectory)
	{
		this.configurationDirectory = configurationDirectory;
		loadAll();
	}
	
	File getConfigurationDirectory()
	{
		return this.configurationDirectory;
	}
	
	/**
	 * 
	 */
	public Iterator<Cache> iterator()
	{
		return wrappedList.iterator();
	}

	public boolean isKnownCache(Cache cache)
	{
		return wrappedList.contains(cache);
	}

	/**
	 * Add an existing cache to this list.
	 * 
	 * @param cache
	 * @return
	 * @throws IOException
	 */
	private synchronized boolean add(Cache cache) 
	throws IOException
	{
		if(cache == null)
			return false;
		
		// prevent duplicate names
		if( get(cache.getName()) != null)
			return false;
		
		CacheMemento memento = cache.createMemento();
		File mementoFile = getCacheMementoFile(cache.getName());
		
		// if we fail to save the file then the cache will not get
		// saved in the known list
		storeCacheMemento(memento, mementoFile);
		
		return wrappedList.add(cache);
	}

	/**
	 * Create a cache instance that supports the protocol specified by the URI scheme
	 * and then saves the cache instance in this list.  Configure the new cache with no
	 * regions and no eviction strategies.
	 * 
	 * @param cacheName
	 * @param locationUri
	 * @return
	 * @throws CacheException
	 * @throws IOException
	 */
	public synchronized Cache create(String cacheName, URI locationUri) 
	throws CacheException, IOException
	{
		return create(cacheName, locationUri, (String)null);
	}
	
	/**
	 * Create a cache instance that supports the protocol specified by the URI scheme
	 * and then saves the cache instance in this list.  Configure the new cache as specified
	 * by the createMode.
	 * 
	 * @param name
	 * @param locationUri
	 * @return
	 * @throws CacheException
	 * @throws IOException
	 */
	public synchronized Cache create(String cacheName, URI locationUri, String configurationStrategyName) 
	throws CacheException, IOException
	{
		if(cacheName == null || locationUri == null)
			return null;
		
		Cache cache = CacheFactory.getSingleton().createCache(cacheName, locationUri, configurationStrategyName);
		
		add(cache);
		
		return cache;
	}
	
	/**
	 * This method requires that all parameters be non-null
	 * 
	 * @param name
	 * @param locationUri
	 * @param prototype
	 * @return
	 */
	public Cache create(String cacheName, URI locationUri, InputStream prototype)
	throws CacheException, IOException
	{
		if(cacheName == null || locationUri == null || prototype == null)
			return null;
		
		Cache cache = CacheFactory.getSingleton().createCache(cacheName, locationUri, prototype);
		
		add(cache);
		
		return cache;
	}
	
	/**
	 * 
	 * @param cacheName
	 * @return
	 */
	public synchronized Cache get(String cacheName)
	{
		if(cacheName == null)
			return null;
		
		for(Cache cache: wrappedList)
			if(cacheName.equals(cache.getName()))
				return cache;
		
		return null;
	}
	
	synchronized boolean remove(String cacheName)
	{
		Cache deadManWalking = null;
		
		if(cacheName == null)
			return false;
		
		for(Cache cache: wrappedList)
			if(cacheName.equals(cache.getName()))
			{
				deadManWalking = cache;
				break;
			}
		
		if(deadManWalking != null && wrappedList.remove(deadManWalking) )
		{
			deleteConfiguration(deadManWalking.getName());
			return true;
		}
		
		return false;
	}
	
	public synchronized void storeAll() throws IOException
	{
		for(Cache cache : wrappedList )
			store(cache);
	}
	
	public synchronized void store(Cache cache) 
	throws IOException
	{
		String cacheName = cache.getName();
		CacheMemento cacheMemento = cache.createMemento();
		
		File mementoFile = getCacheMementoFile(cacheName);
		storeCacheMemento(cacheMemento, mementoFile);
	}
	
	/**
	 * Load all of the known Cache from the configuration files.
	 * @throws CacheInitializationException 
	 *
	 */
	private void loadAll()
	{
		CacheConfigurationFiles files = new CacheConfigurationFiles(configurationDirectory);
		for(File mementoFile : files)
		{
			try
			{
				CacheMemento memento = loadCacheMemento(mementoFile);
				Cache cache = CacheFactory.getSingleton().createCache(memento);
				
				wrappedList.add(cache);
			} 
			catch(CacheInitializationException x)
			{
				logger.error(x);
			}
			catch (FileNotFoundException x)
			{
				logger.error(x);
			} 
			catch (IOException x)
			{
				logger.error(x);
			}
		}
	}
	
	/**
	 * 
	 * @param memento
	 * @param mementoFile
	 * @throws IOException
	 */
	private void storeCacheMemento(CacheMemento memento, File mementoFile) 
	throws IOException 
	{
		// The XMLEncoder uses the current threads context classloader to
		// load classes it needs to serialize.  You would kinda' think that
		// since it is handed a fully populated object to serialize ir wouldn't
		// need to load anything but it does.
		// Unfortunately, in a Tomcat server at least, the thread is an RMI handling thread
		// whose context class loader does not have access to the memento classes.
		// Hence this garbage about setting the thread's context class loader, and then
		// setting it back when we're done.  The class loader that loaded the cache must
		// have access to the memento classes, since it created the instances, so that is
		// why it is used.
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		
		XMLEncoder encoder = null;
		
		try
		{
			Thread.currentThread().setContextClassLoader( memento.getClass().getClassLoader() );
			logger.info("Storing configuration to '" + mementoFile.getCanonicalPath() + "'.");
			
			encoder = new XMLEncoder(new FileOutputStream(mementoFile));
			logger.info("Configuration encoder '" + encoder.getClass().getName() + "' created, writing memento object.");

			encoder.writeObject(memento);
		} 
		finally
		{
			try{encoder.close();}catch(Throwable x){}	// eat any exceptions
			Thread.currentThread().setContextClassLoader( contextClassLoader );
		}
	}
	
	/**
	 * 
	 * @param mementoFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private CacheMemento loadCacheMemento(File mementoFile) 
	throws FileNotFoundException, IOException
	{
		XMLDecoder encoder = null;
		
		// see comment in storeMemento() on class loading
		//ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		
		try
		{
			//if(getCache() != null )
			//	Thread.currentThread().setContextClassLoader( getCache().getClass().getClassLoader() );
			logger.info("Loading configuration from '" + mementoFile.getCanonicalPath() + "'.");
			
			encoder = new XMLDecoder(new FileInputStream(mementoFile));

			return (CacheMemento)encoder.readObject();
		} 
		catch (FileNotFoundException fnfX)
		{
			logger.error("Configuration file not accessible", fnfX);
			throw fnfX;
		} 
		catch (IOException ioX)
		{
			logger.error("Configuration file not accessible", ioX);
			throw ioX;
		}
		catch (ClassCastException ccX)
		{
			logger.error("Configuration file does not contain FileSystemCacheMemento", ccX);
			throw ccX;
		}
		finally
		{
			try{encoder.close();}catch(Throwable x){}	// eat any exceptions
			//if(getCache() != null )
			//	Thread.currentThread().setContextClassLoader( contextClassLoader );
		}
	}
	
	/**
	 * Maps a cache name to the configuration file name.
	 * 
	 * @return
	 */
	private String getCacheMementoFilename(String cacheName)
	{
		return cacheName + cacheConfigFileNameSuffix;
	}

	/**
	 * Get the cache configuration file given the cache name.
	 * 
	 * @param cacheName
	 * @return
	 */
	private File getCacheMementoFile(String cacheName)
	{
		return new File( configurationDirectory, getCacheMementoFilename(cacheName) );
	}
	
	/**
	 * This is really only here for testing because this is the only class that knows
	 * where the memento files are kept.
	 * This deletes the persistent cache state file (i.e. the configuration).  Don't
	 * call it if you don't mean it 'cause it will delete the file. 
	 */
	private void deleteConfiguration(String cacheName)
	{
		File mementoFile = getCacheMementoFile(cacheName);
		
		logger.warn("Deleting memento file '" + mementoFile.getAbsolutePath() + "'.");
		mementoFile.delete();
		logger.warn("Deleted memento file '" + mementoFile.getAbsolutePath() + "'.");
	}

	
	/**
	 * The CacheConfigurationFiles class is an Iterable class over the 
	 * configuration files that existed when the instance was initialized. 
	 * 
	 * @author VHAISWBECKEC
	 *
	 */
	private class CacheConfigurationFiles
	implements Iterable<File>
	{
		private File[] configFiles; 
		CacheConfigurationFiles(File configurationDirectory)
		{
			configFiles = configurationDirectory.listFiles(
				new FilenameFilter()
				{
					public boolean accept(File dir, String name)
					{
						return name.endsWith(cacheConfigFileNameSuffix);
					}
				}
			);
		}

		/**
		 * @see java.lang.Iterable#iterator()
		 */
		public Iterator<File> iterator()
		{
			return new Iterator<File>()
			{
				private int index = 0;
				public boolean hasNext()
				{
					return index < configFiles.length;
				}

				public File next()
				{
					return configFiles[index++];
				}

				/**
				 * @throws OperationNotSupportedException
				 */
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
				
			};
		}
	}
}