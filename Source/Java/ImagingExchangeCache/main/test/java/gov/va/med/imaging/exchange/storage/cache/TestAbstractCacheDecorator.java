/**
 * 
 */
package gov.va.med.imaging.exchange.storage.cache;

import java.io.*;
import java.nio.CharBuffer;
import gov.va.med.OctetSequenceEscaping;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestAbstractCacheDecorator
	extends TestCase
{
	private static OctetSequenceEscaping fileEscaping = 
		OctetSequenceEscaping.createFilenameLegalEscapeEngine();

	/**
	 * Test method for {@link gov.va.med.imaging.exchange.storage.cache.AbstractCacheDecorator#createFilesystemSafeKey(java.lang.String)}.
	 */
	public void testCreateFilesystemSafeKey()
	{
		File tempDirectory = new File( 
			System.getenv("TEMP") == null ?
				System.getenv("TMP") == null ? 
					"/TEMP" : 
					System.getenv("TMP") : 
				System.getenv("TEMP")
		);

		for(String tmpFileName : new String[]{
			"abc.123", 
			"ABC.123", 
			"abc--123", 
			"abc-33-123", 
			"abc-33-123:34", 
			"abc-33-123.345.*&!.txt", 
			"abc-33-123.TXT"
			})
		{
			File tmpFile = new File(tempDirectory, fileEscaping.escapeIllegalCharacters(tmpFileName));
			assertNotNull(tmpFile);
			
			try{ tmpFile.createNewFile();}
			catch (IOException x){ fail(x.getMessage()); }
			
			try
			{
				OutputStreamWriter outWriter = new OutputStreamWriter(new FileOutputStream(tmpFile));
				outWriter.append("HelloWorld");
				outWriter.close();
			}
			catch (IOException x){ fail(x.getMessage()); }

			File tmpFile2 = new File(tempDirectory, fileEscaping.escapeIllegalCharacters(tmpFileName));
			assertNotNull(tmpFile);
			
			try
			{
				InputStreamReader inReader = new InputStreamReader(new FileInputStream(tmpFile2));
				
				CharBuffer cbuf = CharBuffer.allocate(12);
				inReader.read(cbuf );
				inReader.close();
			}
			catch (IOException x){ fail(x.getMessage()); }
			
			tmpFile.deleteOnExit();
		}
	}

}
