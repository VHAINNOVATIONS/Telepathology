/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import java.net.URI;
import java.net.URISyntaxException;

import gov.va.med.imaging.storage.cache.AbstractTestSimpleDirectAccess;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestSimpleDirectAccess 
extends AbstractTestSimpleDirectAccess
{
	protected URI getCacheUri() 
	throws URISyntaxException
	{
		return new URI("file:///vix/cache/" + this.getName());
		
		// smb://[[[domain;]username[:password]@]server[:port]/[[share/[dir/]file]]][?[param=value[param2=value2[...]]]
		//return new URI("smb://jcifs:Raptor999@Isw-beckeyc/jcifs-cache/");
		//return new URI("smb://jcifs:Raptor999@192.168.0.152/jcifs-cache/");
	}
	protected String getPrototypeName()
	{
		return "TestWithNoEvictionPrototype";
	}
	
}
