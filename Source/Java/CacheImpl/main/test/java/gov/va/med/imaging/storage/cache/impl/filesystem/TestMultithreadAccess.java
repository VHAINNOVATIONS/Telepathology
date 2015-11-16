/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import java.net.URI;
import java.net.URISyntaxException;

import gov.va.med.imaging.storage.cache.AbstractTestMultithreadAccess;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestMultithreadAccess 
extends AbstractTestMultithreadAccess
{
	protected URI getCacheUri() 
	throws URISyntaxException
	{
		return new URI("file:///vix/cache/" + this.getName());
	}
	protected String getPrototypeName()
	{
		return "TestWithEvictionPrototype";
	}

}
