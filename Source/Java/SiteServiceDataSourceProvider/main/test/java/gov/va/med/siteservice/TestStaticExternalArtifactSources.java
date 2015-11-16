/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSource;
import gov.va.med.imaging.artifactsource.ArtifactSourceImpl;
import gov.va.med.imaging.datasource.ProviderConfiguration;
import gov.va.med.imaging.exceptions.OIDFormatException;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;

/**
 * @author vhaiswbeckec
 *
 */
public class TestStaticExternalArtifactSources
extends TestCase
{
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	public void testSerialization() 
	throws RoutingTokenFormatException, MalformedURLException, OIDFormatException
	{
		StaticExternalArtifactSources src = new StaticExternalArtifactSources();
		
		RoutingToken rt = RoutingTokenImpl.create("1.2.3.4.56789.10", "R42");
		
		ArtifactSourceImpl as = new ArtifactSourceImpl(
			OID.create("1.2.3.4.56789.10"),
			"R42", 
			new URL[]{new URL("http://localhost:8080/Vix")},
			new URL[]{new URL("http://localhost:8080/Vix")}
		);
		src.add(rt, as);
		
		StaticExternalArtifactSources result = ProviderConfiguration.serializeAndDeserializeByXStreamTest(src);

		assertEquals(src, result);
	}
}
