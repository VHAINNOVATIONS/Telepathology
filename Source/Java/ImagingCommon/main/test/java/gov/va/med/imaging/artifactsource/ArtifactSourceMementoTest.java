/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import gov.va.med.OID;
import gov.va.med.imaging.exceptions.OIDFormatException;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceMementoTest
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

	public void testSerializationOfEmpty()
	{
		ArtifactSourceMemento src = new ArtifactSourceMemento();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(4096);
		XMLEncoder encoder = new XMLEncoder(outStream);
		encoder.writeObject(src);
		encoder.close();
		
		ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		XMLDecoder decoder = new XMLDecoder(inStream);
		ArtifactSourceMemento copy = (ArtifactSourceMemento)decoder.readObject();
		
		assertEquals(src, copy);
		
	}
	
	public void testSerialization()
	{
		ArtifactSourceMemento src = new ArtifactSourceMemento();
		src.setArtifactSourceClassName("classname");
		src.setArtifactUrls(new String[]{"http://localhost:8080/Vix"});
		src.setMetadataUrls(new String[]{"http://localhost:8080/Vix"});
		src.setHomeCommunityId("1.2.3.4.56789.10");
		src.setRepositoryId("R1");
		src.setName("Fred");
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(4096);
		XMLEncoder encoder = new XMLEncoder(outStream);
		encoder.setExceptionListener(new ExceptionListener() 
			{
			    public void exceptionThrown(Exception exception) 
			    {
			    	logger.error(exception.getMessage());
			        exception.printStackTrace();
			    }
			}
		);
		encoder.writeObject(src);
		encoder.close();
		
		ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		XMLDecoder decoder = new XMLDecoder(inStream);
		ArtifactSourceMemento copy = (ArtifactSourceMemento)decoder.readObject();
		
		assertEquals(src, copy);
	}
	
	public void testCreation() 
	throws MalformedURLException, OIDFormatException
	{
		ArtifactSourceImpl impl = new ArtifactSourceImpl(
			OID.create("1.2.3.4.5.6.7.8.9.10"),
			"R42",
			new URL[]{new URL("http://localhost:8080/Vix")},
			new URL[]{new URL("http://localhost:8080/Vix")}
		);
		ArtifactSourceMemento src = impl.getMemento();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(4096);
		XMLEncoder encoder = new XMLEncoder(outStream);
		encoder.writeObject(src);
		encoder.close();
		
		ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		XMLDecoder decoder = new XMLDecoder(inStream);
		ArtifactSourceMemento copy = (ArtifactSourceMemento)decoder.readObject();
		
		assertEquals(src, copy);
		
		ArtifactSource copyImpl = ArtifactSourceFactory.create(copy);
		
		assertEquals(impl.getHomeCommunityId(), copyImpl.getHomeCommunityId());
		assertEquals(impl.getRepositoryId(), copyImpl.getRepositoryId());
		assertEquals(impl.getIdentifier(), copyImpl.getIdentifier());
		assertEquals(impl.getName(), copyImpl.getName());
		assertEquals(impl.getArtifactServerCount(), copyImpl.getArtifactServerCount());
		assertEquals(impl.getMetadataServerCount(), copyImpl.getMetadataServerCount());
		assertEquals(impl, copyImpl);
	}
}
