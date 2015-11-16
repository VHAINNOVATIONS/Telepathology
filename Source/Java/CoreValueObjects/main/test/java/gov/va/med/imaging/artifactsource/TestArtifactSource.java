/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import java.net.MalformedURLException;
import java.net.URL;
import gov.va.med.RoutingToken;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.exchange.business.SiteImpl;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestArtifactSource
	extends TestCase
{

	protected void setUp() 
	throws Exception
	{
		super.setUp();
	}

	public void testIdentity() 
	throws MalformedURLException
	{
		ArtifactSource a1 = null;
		
		a1 = new SiteImpl("42", "200", "SiteName", "SN", true, true, new URL("http://localhost:8080/app/file"));
		assertNotNull(a1.getIdentifier());
		System.out.println(a1.getIdentifier());
		
		a1 = new ArtifactSourceImpl(
			WellKnownOID.HAIMS_DOCUMENT.getCanonicalValue(),
			RoutingToken.ROUTING_WILDCARD,
			new URL[]{new URL("http://localhost:8080/app/file")}, 
			new URL[]{new URL("http://localhost:8080/app/file")}
		);
		assertNotNull(a1.getIdentifier());
		System.out.println(a1.getIdentifier());
		
		a1 = new ArtifactSourceImpl(
			WellKnownOID.VA_DOCUMENT.getCanonicalValue(), 
			"660", 
			new URL[]{new URL("http://localhost:8080/app/file")}, 
			new URL[]{new URL("http://localhost:8080/app/file")}
		);
		assertNotNull(a1.getIdentifier());
		System.out.println(a1.getIdentifier());
		
	}
	
	public void testInclusion()
	{
		
	}

	protected void tearDown() 
	throws Exception
	{
		super.tearDown();
	}

}
