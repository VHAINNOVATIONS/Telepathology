package gov.va.med.imaging.exchange.enums;

/**
 * The list of mime types that the VIX understands.
 * 
 * @author VHAISWBECKEC
 */
public enum ImageFormat 
{
	// The order of the declared enum instances is significant.
	// More specific types should precede less specific types.
	TGA("image/x-targa", "TGA format image", null, false, "TGA"), 
	DOWNSAMPLEDTGA("image/x-targa", "Reduced resolution TGA format image", null, false, "TGA"), 
	TIFF("image/tiff", "TIFF format image", null, false, "TIF"), 
	BMP("image/bmp", "A bitmap image", null, false, "BMP"), 
	JPEG("image/jpeg", "JPEG formatted image", null, true, "JPG"), 
	J2K("image/j2k", "JPEG 2000 formatted image", null, true, "J2K"), 
	DICOM("application/dicom", "A DICOM image, the content is unknown", null, false, "DCM"), 
	DICOMJPEG("application/dicom", "DICOM wrapped JPEG images", "image/jpeg", true, "DCM"), 
	DICOMJPEG2000("application/dicom", "DICOM wrapped JPEG 2000-images", "image/j2k", true, "DCM"),
	DICOMPDF("application/dicom","DICOM wrapped PDF documents", "application/pdf", false, "DCM"),
	PDF("application/pdf", "A PDF document", null, false, "PDF"),
	TEXT_DICOM("text/dicom","Plain text file containing DICOM header information for an image", null, false, "TXT"),
	TEXT_PLAIN("text/plain","Plain text file containing patient text data (not DICOM header information)", null, false, "TXT"),
	IMAGE("image/*","Any image type", null, false, "IMG"),
	APPLICATION("application/*","Any application type", null, false, "APP"),
	ANYTHING("*/*", "Any filetype available", null, false, ""),
	DOC("application/msword", "Microsoft Word documents", null, false, "DOC"),
	AVI("video/x-msvideo", "AVI Video", null, false, "AVI"),
	RTF("text/rtf", "Rich text application document", null, false, "RTF"),
	WAV("audio/x-wav", "WAV audio", null, false, "WAV"),
	HTML("text/html","HTML document", null, false, "HTM"),
	MP3("audio/mpeg3", "Audio MPEG Layer 3", null, false, "MP3"),
	MPG("video/mpeg", "Video MPEG", null, false, "MPG"),
	//UNKNOWN("?/?", "Unknown file format", null),
	ORIGINAL("x/x", "Represents the original format of the image as it is stored by the owner", null, false, ""),
	GIF("image/gif", "GIF formatted image", null, true, "GIF"),
	PNG("image/png", "PNG image", null, true, "PNG"),
	XLS("application/vnd.ms-excel", "Microsoft Excel (pre Office 2007) format", null, false, "XLS"),
	DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word 2007+ format", null, false, "DOCX"),
	XML("text/xml", "XML Files", null, false, "XML");
	//ANNOTATIONS_BURNED("image/annotated", "Image with annotations burned in", null, false);
			
	
	private final String mime;
	private final String type;
	private final String subType;
	private final String enclosedMime;
	private final String enclosedType;
	private final String enclosedSubType;
	private final String description;
	private final boolean compressed;
	private final String defaultFileExtension;
	
	private final static String mimeDelimiter = "/";
	
	/**
	 * 
	 * @param mimeType
	 * @param description
	 * @param enclosedMimeType
	 */
	ImageFormat(String mimeType, String description, String enclosedMimeType, 
			boolean compressed, String defaultFileExtension)
	{
		this.mime = mimeType;
		this.compressed = compressed;
		this.defaultFileExtension = defaultFileExtension;
		int slashIndex = mimeType.indexOf(mimeDelimiter); 
		if(slashIndex >= 0)
		{
			this.type = mimeType.substring(0, slashIndex);
			this.subType = slashIndex < mimeType.length() ? mimeType.substring(slashIndex+1) : null;
		}
		else
		{
			this.type = mimeType;
			this.subType = null;
		}
		
		this.description = description;
		this.enclosedMime = enclosedMimeType;
		if(enclosedMime != null)
		{
			slashIndex = enclosedMime.indexOf(mimeDelimiter); 
			if(slashIndex >= 0)
			{
				this.enclosedType = enclosedMime.substring(0, slashIndex);
				this.enclosedSubType = slashIndex < enclosedMime.length() ? enclosedMime.substring(slashIndex+1) : null;
			}
			else
			{
				this.enclosedType = enclosedMime;
				this.enclosedSubType = null;
			}
		}
		else
		{
			this.enclosedType = null;
			this.enclosedSubType = null;
		}
	}

	public String getMime()
	{
		return this.mime;
	}

	public String getType()
    {
    	return type;
    }

	public String getSubType()
    {
    	return subType;
    }

	public String getDescription()
	{
		return this.description;
	}

	public String getEnclosedMime()
    {
    	return enclosedMime;
    }

	public String getEnclosedType()
    {
    	return enclosedType;
    }

	public String getEnclosedSubType()
    {
    	return enclosedSubType;
    }
	
	public String getMimeWithEnclosedMime()
	{
		if(enclosedMime != null)
		{
			return mime + mimeDelimiter + enclosedMime;
		}
		else
		{
			return mime;
		}
	}

	/**
	 * Determines if the image format is a compressed format
	 * 
	 * @return the compressed
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * 
	 * @param imageFormat
	 * @return
	 */
	public static String getContentType(ImageFormat imageFormat) 
	{
		return imageFormat.getMime();
	}
	
	public String getDefaultFileExtension()
	{
		return defaultFileExtension;
	}

	/**
	 * Do a reverse lookup, find the ImageFormat enum from the mime type.
	 * Note that the mime type is specific to type and subtype and is 
	 * case sensitive.
	 * Note that this will return the first ImageFormat enum mapped to the
	 * mime type, there may be many.
	 * If the enclosed type is specified then it must match
	 * that in the ImageFormat enclosedMimeType.
	 * @param mimeType
	 * @return
	 */
	public static ImageFormat valueOfMimeType(String mimeType)
	{
		String pieces[] = mimeType.split(mimeDelimiter);
		if(pieces.length == 4)
		{
			String mime = pieces[0] + mimeDelimiter + pieces[1];
			String enclosedMime = pieces[2] + mimeDelimiter + pieces[3];
			return valueOfMimeType(mime, enclosedMime);
			
		}
		else
		{
			return valueOfMimeType(mimeType, null);
		}
	}
	
	public static ImageFormat valueOfMimeType(String mimeType, String enclosedMimeType)
	{
		for(ImageFormat imageFormat : ImageFormat.values())
			if(imageFormat.getMime().equals(mimeType))
				if(enclosedMimeType == null)
					return imageFormat;
				else if( imageFormat.getEnclosedMime() != null && 
						imageFormat.getEnclosedMime().equals(enclosedMimeType) )
					return imageFormat;
		
		return null;
	}
	
	public static boolean isDICOMFormat(ImageFormat imageFormat)
	{
		if((imageFormat == ImageFormat.DICOM) ||
			(imageFormat == ImageFormat.DICOMJPEG) ||
			(imageFormat == ImageFormat.DICOMJPEG2000) ||
			(imageFormat == ImageFormat.DICOMPDF))
			return true;
		return false;
			
	}
	
	public static boolean isDICOMFormat(String mimeType)
	{
		ImageFormat format = ImageFormat.valueOfMimeType(mimeType);
		if(format == null)
			return false;
		return isDICOMFormat(format);
	}
	
	/**
	 * 
	 * @param ordinal
	 * @return
	 */
	public static ImageFormat valueOf(int ordinal)
	{
		for(ImageFormat imageFormat : ImageFormat.values())
			if(imageFormat.ordinal() == ordinal)
				return imageFormat;
		
		return null;
	}
	
}
