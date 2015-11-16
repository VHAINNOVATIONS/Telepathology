/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Dec 9, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.imaging.datasource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vhaiswbeckec
 *
 */
final class ProviderDataSourceFactories
{
	private Provider provider;
	
	ProviderDataSourceFactories(Provider provider)
	{
		if(provider == null)
			throw new IllegalArgumentException("The provider may not be null when creating a ProviderServiceFactories instance.");
		
		this.provider = provider;
	}
	
	// a map from the versionable SPI type to the factory that creates that class
	private Map<Class<? extends VersionableDataSourceSpi>, VersionableServiceProviderFactory<? extends VersionableDataSourceSpi>>
		versionableFactoryMap = new HashMap<Class<? extends VersionableDataSourceSpi>, VersionableServiceProviderFactory<? extends VersionableDataSourceSpi>>();
	
	// a map from the local SPI type to the factory that creates that class
	private Map<Class<? extends LocalDataSourceSpi>, LocalServiceProviderFactory<? extends LocalDataSourceSpi>>
		localFactoryMap = new HashMap<Class<? extends LocalDataSourceSpi>, LocalServiceProviderFactory<? extends LocalDataSourceSpi>>();

	/**
	 * 
	 * @param <S>
	 * @param dataSourceType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <S extends VersionableDataSourceSpi> VersionableServiceProviderFactory<S> getVersionableDataSourceFactory(Class<S> dataSourceType)
	{
		return (VersionableServiceProviderFactory<S>)versionableFactoryMap.get(dataSourceType);
	}
	
	@SuppressWarnings("unchecked")
	public <S extends LocalDataSourceSpi> LocalServiceProviderFactory<S> getLocalDataSourceFactory(Class<S> dataSourceType)
	{
		return (LocalServiceProviderFactory<S>)localFactoryMap.get(dataSourceType);
	}

	/**
	 * 
	 * @param <S>
	 * @param spiType
	 * @return
	 */
	<S extends VersionableDataSourceSpi> 
	VersionableServiceProviderFactory<S> getOrCreateVersionableServiceProviderFactory(Class<S> spiType)
	{
		VersionableServiceProviderFactory<S> spiFactory = getVersionableDataSourceFactory(spiType);
		if(spiFactory == null)
			synchronized(versionableFactoryMap)
			{
				// re-call the get method to assure that the instance was not created on another thread
				// since the previous call
				spiFactory = getVersionableDataSourceFactory(spiType);
				if(spiFactory == null)
				{
					spiFactory = new VersionableServiceProviderFactory<S>(provider, spiType);
					// JMW 6/24/2011 - add the spiFactory to the map
					versionableFactoryMap.put(spiType, spiFactory);
				}
			}

		return spiFactory;
	}

	/**
	 * 
	 * @param <S>
	 * @param spiType
	 * @return
	 */
	<S extends LocalDataSourceSpi> 
	LocalServiceProviderFactory<S> getOrCreateLocalServiceProviderFactory(Class<S> spiType)
	{
		LocalServiceProviderFactory<S> spiFactory = getLocalDataSourceFactory(spiType);
		if(spiFactory == null)
			synchronized(localFactoryMap)
			{
				// re-call the get method to assure that the instance was not created on another thread
				// since the previous call
				spiFactory = getLocalDataSourceFactory(spiType);
				if(spiFactory == null)
				{
					spiFactory = new LocalServiceProviderFactory<S>(provider, spiType);
					// JMW 6/24/2011 - add the spiFactory to the map
					localFactoryMap.put(spiType, spiFactory);
				}
			}

		return spiFactory;
	}
}
