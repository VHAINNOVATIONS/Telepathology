/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import java.net.URI;
import java.net.URISyntaxException;

import gov.va.med.imaging.storage.cache.AbstractTestDirectCacheAccess;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestDirectCacheAccess 
extends AbstractTestDirectCacheAccess
{

	protected URI getCacheUri() 
	throws URISyntaxException
	{
		return new URI("file:///vix/cache/" + this.getName());
	}
	protected String getPrototypeName()
	{
		return "TestDirectAccess";
	}
}
