/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 10, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.enums;

/**
 * This is a VistA specific enumeration that describes the Image Type field stored in VistA.
 * This is similar but NOT the same as ImageFormat which describes the format of an image.
 * This does not define a MIME type. This simply makes it easier to understand what the 
 * numeric image type value corresponds to as defined by VistA.
 * 
 *  The following definitions come from VistA and should not be modified unless they are changed
 *  in VistA
 * 
 * @author VHAISWWERFEJ
 *
 */
public enum VistaImageType 
{
	JPEG(1, "JPEG Image", ImageFormat.JPEG.getMime(), "JPG"),
	BWMED(9, "Black and White med images", ImageFormat.TGA.getMime(), "TGA"),
	COLOR_SCAN(17, "Color Scan", ImageFormat.JPEG.getMime(), "JPG"),
	PATIENT_PHOTO(18, "Patient Photo Id", ImageFormat.JPEG.getMime(), "JPG"),
	XRAY_JPEG(19, "JPEG XRay image", ImageFormat.JPEG.getMime(), "JPG"),
	TIFF(15, "TIFF image", ImageFormat.TIFF.getMime(), "TIF"),
	MOTION_VIDEO(21, "Motion Video (AVI, MPG)", ImageFormat.AVI.getMime(), "AVI"),
	HTML(101, "HTML Document", ImageFormat.HTML.getMime(), "HTM"),
	WORD_DOCUMENT(102, "Word Document", ImageFormat.DOC.getMime(), "DOC"),
	ASCII_TEXT(103, "ASCII Text", ImageFormat.TEXT_PLAIN.getMime(), "TXT"),
	PDF(104, "PDF Document", ImageFormat.PDF.getMime(), "PDF"),
	RTF(105, "RTF Document", ImageFormat.RTF.getMime(), "RTF"),
	AUDIO(106, "Audio File (WAV, MP3)", ImageFormat.WAV.getMime(), "WAV"),
	XRAY(3, "TGA Image", ImageFormat.TGA.getMime(), "TGA"), //TGA
	DICOM(100, "DICOM Image", ImageFormat.DICOM.getMime(), "DCM"),
	NCAT(501, "DoD NCAT Reports", ImageFormat.PDF.getMime(), "PDF"),
	UNKNOWN_IMAGING_TYPE(502, "Type not known to VistA Imaging", ImageFormat.ANYTHING.getMime(), ""),
	DOD_JPG(503, "JPG from the DoD", ImageFormat.JPEG.getMime(), "JPG"),
	DOD_WORD_DOCUMENT(504, "Word Document from the DoD", ImageFormat.DOC.getMime(), "DOC"),
	DOD_ASCII_TEXT(505, "ASCII Test from the DoD", ImageFormat.TEXT_PLAIN.getMime(), "TXT"),
	DOD_PDF(506, "PDF Document from the DoD", ImageFormat.PDF.getMime(), "PDF"),
	DOD_RTF(507, "RTF Document from the DoD", ImageFormat.RTF.getMime(), "RTF"),//,
	//DOD_DOCX_DOCUMENT(508, "Word Document in DOCX format", ImageFormat.DOCX.getMime());
	XML(107, "XML Document", ImageFormat.XML.getMime(), "XML");
	
	private final int imageType;
	private final String description;
	private final String defaultMimeType;
	private final String defaultFileExtension;
	
	VistaImageType(int imageType, String description, String defaultMimeType, String defaultFileExtension)
	{
		this.imageType = imageType;
		this.description = description;
		this.defaultMimeType = defaultMimeType;
		this.defaultFileExtension = defaultFileExtension;
	}

	/**
	 * Convert a numeric image type into the VistaImageType that represents that value.
	 * @param imageType
	 * @return
	 */
	public static VistaImageType valueOfImageType(int imageType)
	{
		for(VistaImageType vistaImageType : VistaImageType.values())
		{
			if(vistaImageType.imageType == imageType)
			{
				return vistaImageType;
			}
		}
		return null;
	}

	/**
	 * @return the imageType
	 */
	public int getImageType() {
		return imageType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the defaultMimeType
	 */
	public String getDefaultMimeType() {
		return defaultMimeType;
	}

	public String getDefaultFileExtension()
	{
		return defaultFileExtension;
	}	
}
