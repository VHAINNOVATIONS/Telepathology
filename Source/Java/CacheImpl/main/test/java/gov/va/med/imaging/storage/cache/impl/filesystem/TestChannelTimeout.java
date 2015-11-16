/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import java.net.URI;
import java.net.URISyntaxException;

import gov.va.med.imaging.storage.cache.AbstractTestChannelTimeout;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestChannelTimeout 
extends AbstractTestChannelTimeout
{
	protected URI getCacheUri() 
	throws URISyntaxException
	{
		//return new URI("file://e:/vix-cache/" + this.getName());
		return new URI("file:///vix/cache/" + this.getName());
	}
	protected String getPrototypeName()
	{
		return "TestWithNoEvictionPrototype";
	}
	
}
