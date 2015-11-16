/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl.filesystem;

import gov.va.med.imaging.storage.cache.AbstractAccessDirectCacheTest;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;
import gov.va.med.imaging.storage.cache.impl.GroupStatisticsReporter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author VHAISWBECKEC
 *
 */
public class TestGroupStatistics 
extends AbstractAccessDirectCacheTest
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
		return "TestWithEvictionPrototype";
	}
	
	public void testGroupStatistics() 
	throws CacheException, IOException
	{
		
		createRetrieveAndCompareInstance("test-metadata", new String[]{"fred"}, "barney", getSampleData());
		createRetrieveAndCompareInstance("test-metadata", new String[]{"fred", "wilma"}, "betty", getSampleData());
		
		Writer writer = new OutputStreamWriter(System.out);
		
		GroupStatisticsReporter.createAndRun( getCache().getRegion("test-metadata"), true, true, writer );
		
		writer.close();
	}

}
