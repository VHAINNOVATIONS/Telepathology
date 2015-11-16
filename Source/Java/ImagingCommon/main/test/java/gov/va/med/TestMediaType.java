package gov.va.med;


import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestMediaType
extends TestCase
{
	public void testLookupPositive()
	{
		assertEquals(MediaType.APPLICATION_DICOM, MediaType.lookup("application/dicom") );
		assertEquals(MediaType.APPLICATION_DOC, MediaType.lookup("application/msword") );
		assertEquals(MediaType.APPLICATION_PDF, MediaType.lookup("application/pdf") );
		
		assertEquals(MediaType.AUDIO_MP4, MediaType.lookup("audio/mp4") );
		assertEquals(MediaType.AUDIO_MPEG, MediaType.lookup("audio/mpeg") );
		assertEquals(MediaType.AUDIO_WAV, MediaType.lookup("audio/x-wav") );
		
		assertEquals(MediaType.IMAGE_XBMP, MediaType.lookup("image/x-bmp") );
		assertEquals(MediaType.IMAGE_BMP, MediaType.lookup("image/bmp") );
		assertEquals(MediaType.IMAGE_JP2, MediaType.lookup("image/jp2") );
		assertEquals(MediaType.IMAGE_J2K, MediaType.lookup("image/j2k") );
		assertEquals(MediaType.IMAGE_JPEG, MediaType.lookup("image/jpeg") );
		assertEquals(MediaType.IMAGE_PNG, MediaType.lookup("image/png") );
		assertEquals(MediaType.IMAGE_TGA, MediaType.lookup("image/x-targa") );
		assertEquals(MediaType.IMAGE_TIFF, MediaType.lookup("image/tiff") );
		
		assertEquals(MediaType.MULTIPART_FORM_DATA, MediaType.lookup("multipart/form-data") );
		assertEquals(MediaType.MULTIPART_MIXED, MediaType.lookup("multipart/mixed") );
		
		assertEquals(MediaType.TEXT_CSS, MediaType.lookup("text/css") );
		assertEquals(MediaType.TEXT_CSV, MediaType.lookup("text/csv") );
		assertEquals(MediaType.TEXT_ENRICHED, MediaType.lookup("text/enriched") );
		assertEquals(MediaType.TEXT_HTML, MediaType.lookup("text/html") );
		assertEquals(MediaType.TEXT_PLAIN, MediaType.lookup("text/plain") );
		assertEquals(MediaType.TEXT_RTF, MediaType.lookup("text/rtf") );
		assertEquals(MediaType.TEXT_TSV, MediaType.lookup("text/tab-separated-values") );
		assertEquals(MediaType.TEXT_URI_LIST, MediaType.lookup("text/uri-list") );
		assertEquals(MediaType.TEXT_XML, MediaType.lookup("text/xml") );
		assertEquals(MediaType.TEXT_XML_EXTERNAL_PARSED_ENTITY, MediaType.lookup("text/xml-external-parsed-entity") );
		
		assertEquals(MediaType.VIDEO_AVI, MediaType.lookup("video/x-msvideo") );
		assertEquals(MediaType.VIDEO_BMPEG, MediaType.lookup("video/bmpeg") );
		assertEquals(MediaType.VIDEO_JPEG, MediaType.lookup("video/jpeg") );
		assertEquals(MediaType.VIDEO_JPEG2000, MediaType.lookup("video/jpeg2000") );
		assertEquals(MediaType.VIDEO_MP4, MediaType.lookup("video/mp4") );
		assertEquals(MediaType.VIDEO_MPEG, MediaType.lookup("video/mpeg") );
		assertEquals(MediaType.VIDEO_MPEG4_GENERIC, MediaType.lookup("video/mpeg4-generic") );
		assertEquals(MediaType.VIDEO_OGG, MediaType.lookup("video/ogg") );
		assertEquals(MediaType.VIDEO_QUICKTIME, MediaType.lookup("video/quicktime") );
		
		assertEquals(MediaType.APPLICATION_DOC, MediaType.lookup("APPLICATION/MSWORD") );
		assertEquals(MediaType.APPLICATION_DOC, MediaType.lookup("aPpLiCaTiOn/MsWoRd") );
	}
	
	public void testLookupNegative()
	{
		assertTrue(null == MediaType.lookup("application/junk") );
		assertTrue(null == MediaType.lookup("video/wav") );
		assertTrue(null == MediaType.lookup("text/pdf") );
	}
}
