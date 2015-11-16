/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.exchange.business.SiteImpl;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public class TestArtifactSourceMemento
	extends TestCase
{
	@Override
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		ProtocolHandlerUtility.initialize(true);
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	public void testArtifactRepositoryImplSerialization() 
	throws MalformedURLException
	{
		ArtifactSource as = new ArtifactSourceImpl(
			WellKnownOID.VA_DOCUMENT.getCanonicalValue(),
			"42", 
			new URL[]{new URL("http://localhost/app/file")}, 
			new URL[]{new URL("http://localhost/app/file")}
		);
		ArtifactSourceMemento asm = as.getMemento();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder( out );
		encoder.writeObject(asm);
		encoder.close();
		
		System.out.println( new String(out.toByteArray()) );
		
		XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(out.toByteArray()));
		ArtifactSourceMemento asm2 = (ArtifactSourceMemento)decoder.readObject();
		
		assertEquals(asm, asm2);
	}


	public void testSiteImplSerialization() 
	throws MalformedURLException
	{
		ArtifactSource as = new SiteImpl(
			"660", "Name", "Nm", 
			"localhost", 9300,
			"localhist", 8080,
			"42"
		);
		ArtifactSourceMemento asm = as.getMemento();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder( out );
		encoder.writeObject(asm);
		encoder.close();
		
		System.out.println( new String(out.toByteArray()) );
		
		XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(out.toByteArray()));
		ArtifactSourceMemento asm2 = (ArtifactSourceMemento)decoder.readObject();
		
		assertEquals(asm, asm2);
	}
}
