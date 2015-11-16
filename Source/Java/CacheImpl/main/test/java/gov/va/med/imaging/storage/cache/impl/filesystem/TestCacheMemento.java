package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.AbstractTestCacheMemento;
import gov.va.med.imaging.storage.cache.Cache;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public class TestCacheMemento 
extends AbstractTestCacheMemento
{

	/**
	 * @return
	 * @throws URISyntaxException
	 */
	protected URI getCacheUri() throws URISyntaxException
	{
		return new URI( "file:///vix/unit-test/" + getCacheName() );
	}
	
	protected void validateCacheRealizationClass(Cache cache)
	{
		assertTrue( cache instanceof FileSystemCache );
	}
	
	protected String getCacheName()
	{
		return this.getClass().getSimpleName();
	}
	
}
