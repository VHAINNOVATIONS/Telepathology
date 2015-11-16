/**
 * 
 */
package gov.va.med;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * The type previously known as mime type.
 * The "media type" identifies the type of data, usually during transport,
 * in terms of the specification of the binary data.  
 * A representation of the media type as a string is used in the content-type
 * header field of an HTTP message.
 * The Media Type is divided into a general type and a subtype indicating
 * the specific format.
 * 
 * @see http://tools.ietf.org/html/rfc2046
 * @see http://www.iana.org/assignments/media-types/
 * 
 * @author vhaiswbeckec
 *
 */
public enum MediaType
{
	// If the URL given for the relevant specification is not a valid URL then this class
	// will fail to initialize with a runtime exception.
	
	APPLICATION_DICOM(TopLevelMediaType.APPLICATION, "dicom", "http://www.rfc-editor.org/rfc/rfc3240.txt",
			new String [] {"DCM"}),
	APPLICATION_PDF(TopLevelMediaType.APPLICATION, "pdf", "http://www.rfc-editor.org/rfc/rfc3778.txt",
			new String [] {"PDF"}),
	APPLICATION_DOC(TopLevelMediaType.APPLICATION, "msword", "http://www.iana.org/assignments/contact-people.htm#Lindner",
			new String [] {"DOC"}),
	// application/octet-stream is used as a catch-all for unknown mime types
	APPLICATION_OCTETSTREAM(TopLevelMediaType.APPLICATION, "octet-stream", "http://www.rfc-editor.org/rfc/rfc2045.txt",
			null),
	APPLICATION_EXCEL(TopLevelMediaType.APPLICATION, "vnd.ms-excel", "http://www.w3schools.com/media/media_mimeref.asp",
			new String [] {"XLS"}),
	APPLICATION_PPT(TopLevelMediaType.APPLICATION, "vnd.ms-powerpoint", "http://filext.com/file-extension/PPT",
			new String [] {"PPT"}),			
	APPLICATION_RTF(TopLevelMediaType.APPLICATION, "rtf", "http://www.iana.org/assignments/media-types/application/rtf",
			new String [] {"RTF"}),
	APPLICATION_DOCX(TopLevelMediaType.APPLICATION, "vnd.openxmlformats-officedocument.wordprocessingml.document", null,
			new String [] {"DOCX"}),
	
	AUDIO_WAV(TopLevelMediaType.AUDIO, "x-wav", null,
			new String [] {"WAV"}),
	AUDIO_MPEG(TopLevelMediaType.AUDIO, "mpeg", "http://www.rfc-editor.org/rfc/rfc3003.txt",
			new String [] {"MP3"}),
	AUDIO_MP4(TopLevelMediaType.AUDIO, "mp4", "http://www.rfc-editor.org/rfc/rfc4337.txt",
			new String [] {"MP4"}),
	
	IMAGE_BMP(TopLevelMediaType.IMAGE,"bmp", null,
			new String [] {"BMP"}),
	IMAGE_XBMP(TopLevelMediaType.IMAGE,"x-bmp", null,
			null),	
	IMAGE_JPEG(TopLevelMediaType.IMAGE, "jpeg", "http://www.rfc-editor.org/rfc/rfc2046.txt",
			new String [] {"JPG"}), 
	IMAGE_J2K(TopLevelMediaType.IMAGE, "j2k", null,
			new String [] {"J2K"}), 
	IMAGE_JP2(TopLevelMediaType.IMAGE, "jp2", "http://www.rfc-editor.org/rfc/rfc3745.txt",
			new String [] {"J2K"}), 
	IMAGE_PNG(TopLevelMediaType.IMAGE, "png", "http://www.iana.org/assignments/contact-people.htm#Randers-Pehrson",
			new String [] {"PNG"}),
	IMAGE_TIFF(TopLevelMediaType.IMAGE,"tiff", "http://www.rfc-editor.org/rfc/rfc2302.txt",
			new String [] {"TIF", "TIFF"}), 
	IMAGE_TGA(TopLevelMediaType.IMAGE, "x-targa", null,
			new String [] {"TGA", "BW", "BIG", "756", "PAC"}), 
	
	MULTIPART_FORM_DATA(TopLevelMediaType.MULTIPART, "form-data", "http://www.rfc-editor.org/rfc/rfc2388.txt",
			null),
	MULTIPART_MIXED(TopLevelMediaType.MULTIPART, "mixed", "http://www.rfc-editor.org/rfc/rfc2046.txt",
			null),
	
	TEXT_CSS(TopLevelMediaType.TEXT, "css", "http://www.rfc-editor.org/rfc/rfc2318.txt",
			new String [] {"CSS"}),
	TEXT_CSV(TopLevelMediaType.TEXT, "csv", "http://www.rfc-editor.org/rfc/rfc4180.txt",
			new String [] {"CSV"}),
	TEXT_ENRICHED(TopLevelMediaType.TEXT, "enriched", "http://www.rfc-editor.org/rfc/rfc1896.txt",
			null),
	TEXT_HTML(TopLevelMediaType.TEXT, "html", "http://www.rfc-editor.org/rfc/rfc2854.txt",
			new String [] {"HTM", "HTML"}),
	TEXT_PLAIN(TopLevelMediaType.TEXT, "plain", "http://www.rfc-editor.org/rfc/rfc5147.txt",
			new String [] {"TXT", "ASC"}),
	TEXT_RTF(TopLevelMediaType.TEXT, "rtf", "http://www.iana.org/assignments/contact-people.htm#Lindner",
			new String [] {"RTF"}),
	TEXT_TSV(TopLevelMediaType.TEXT, "tab-separated-values", "http://www.iana.org/assignments/contact-people.htm#Lindner",
			new String [] {"TSV"}),
	TEXT_URI_LIST(TopLevelMediaType.TEXT, "uri-list", "http://www.rfc-editor.org/rfc/rfc2483.txt",
			null),
	TEXT_XML(TopLevelMediaType.TEXT, "xml", "http://www.rfc-editor.org/rfc/rfc3023.txt",
			new String [] {"XML"}),
	TEXT_XML_EXTERNAL_PARSED_ENTITY(TopLevelMediaType.TEXT, "xml-external-parsed-entity", "http://www.rfc-editor.org/rfc/rfc3023.txt",
			null),
	
	VIDEO_BMPEG(TopLevelMediaType.VIDEO, "BMPEG", "http://www.rfc-editor.org/rfc/rfc3555.txt",
			null),
	VIDEO_JPEG(TopLevelMediaType.VIDEO, "JPEG", "http://www.rfc-editor.org/rfc/rfc3555.txt",
			null),
	VIDEO_JPEG2000(TopLevelMediaType.VIDEO, "jpeg2000", "http://www.rfc-editor.org/rfc/rfc5372.txt",
			null),
	VIDEO_MP4(TopLevelMediaType.VIDEO, "mp4", "http://www.rfc-editor.org/rfc/rfc4337.txt",
			new String [] {"MP4"}),
	VIDEO_MPEG4_GENERIC(TopLevelMediaType.VIDEO, "mpeg4-generic", "http://www.rfc-editor.org/rfc/rfc3640.txt",
			null),
	VIDEO_MPEG(TopLevelMediaType.VIDEO, "MPEG", "http://www.rfc-editor.org/rfc/rfc4337.txt",
			new String [] {"MPG", "MPEG"}),
	VIDEO_OGG(TopLevelMediaType.VIDEO, "ogg", "http://www.rfc-editor.org/rfc/rfc5334.txt",
			new String [] {"OGG"}),
	VIDEO_QUICKTIME(TopLevelMediaType.VIDEO, "quicktime", "http://www.iana.org/assignments/contact-people.htm#Lindner",
			new String [] {"MOV"}),
	VIDEO_AVI(TopLevelMediaType.VIDEO, "x-msvideo", null, 
			new String [] {"AVI"});
	
	private final TopLevelMediaType topLevelMediaType;
	private final String subtype;
	private URL releventSpecification;
	private final String [] fileExtensions;
	
	MediaType(TopLevelMediaType topLevelMediaType, String subtype, String [] fileExtensions)
	{
		this.topLevelMediaType = topLevelMediaType;
		this.subtype = subtype;
		this.releventSpecification = null;
		this.fileExtensions = fileExtensions;
	}
	
	MediaType(TopLevelMediaType topLevelMediaType, String subtype, String releventSpecification, 
			String [] fileExtensions) 
	{
		this.topLevelMediaType = topLevelMediaType;
		this.subtype = subtype;
		this.fileExtensions = fileExtensions;
		try
		{
			this.releventSpecification = releventSpecification == null ? null : new URL(releventSpecification);
		}
		catch (MalformedURLException x)
		{
			Logger.getLogger(this.getClass()).warn("MediaType '" + topLevelMediaType + "'/'" + subtype + "' -> " + releventSpecification + ", URL is not valid.");
			x.printStackTrace();
		}
	}
	
	public TopLevelMediaType getTopLevelMediaType()
	{
		return this.topLevelMediaType;
	}

	public String getSubtype()
	{
		return this.subtype;
	}

	public URL getReleventSpecification()
	{
		return this.releventSpecification;
	}

	public String[] getFileExtensions()
	{
		return fileExtensions;
	}

	@Override
	public String toString()
	{
		return topLevelMediaType.toString() + "/" + subtype;
	}
	
	/**
	 * 
	 * @param mediaTypeString
	 * @return
	 */
	public static MediaType lookup(String mediaTypeString)
	{
		int firstSemicolonIndex = mediaTypeString.indexOf(';');
		if( firstSemicolonIndex > 0 )
			mediaTypeString = mediaTypeString.substring(0, firstSemicolonIndex);
		if( firstSemicolonIndex == 0 )
			mediaTypeString = "";

		String[] parts = mediaTypeString.split("/");
		if(parts.length != 2)
			return null;
		TopLevelMediaType topLevelMediaType = TopLevelMediaType.lookup(parts[0]);
		
		if(topLevelMediaType == null)
			return null;
		
		for(MediaType mediaType : MediaType.values())
			if( topLevelMediaType == mediaType.getTopLevelMediaType() && parts[1].equalsIgnoreCase(mediaType.getSubtype()) )
				return mediaType;
		
		return null;
	}
	
	/**
	 * Determines the media type based on the file extension. If no media type matches, null is 
	 * returned.  The comparison is not case sensitive
	 * 
	 * @param fileExtension The file extension of an artifact (not including the period)
	 * @return First media type that matches the extension or null if none match
	 */
	public static MediaType lookupByFileExtension(String fileExtension)
	{
		if(fileExtension == null)
			return null;
		
		for(MediaType mediaType : MediaType.values())
		{
			if(mediaType.fileExtensions != null)
			{
				for(String mediaTypeExtension : mediaType.fileExtensions)
				{
					if(mediaTypeExtension.equalsIgnoreCase(fileExtension))
						return mediaType;
				}
			}
		}
		return null;
	}
}
