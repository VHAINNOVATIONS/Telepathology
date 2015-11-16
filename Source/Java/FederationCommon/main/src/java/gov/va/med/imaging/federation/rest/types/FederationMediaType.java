/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 25, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.federation.rest.types;

/**
 * @author vhaiswwerfej
 *
 */
public enum FederationMediaType
{
	APPLICATION_DICOM,
	APPLICATION_PDF,
	APPLICATION_DOC,
	// application/octet-stream is used as a catch-all for unknown mime types
	APPLICATION_OCTETSTREAM,
	APPLICATION_EXCEL,
	APPLICATION_PPT,
	APPLICATION_RTF,
	APPLICATION_DOCX,
	
	AUDIO_WAV,
	AUDIO_MPEG,
	AUDIO_MP4,
	
	IMAGE_BMP, 
	IMAGE_XBMP,
	IMAGE_JPEG, 
	IMAGE_J2K,
	IMAGE_JP2, 
	IMAGE_PNG,
	IMAGE_TIFF, 
	IMAGE_TGA, 
	
	MULTIPART_FORM_DATA,
	MULTIPART_MIXED,
	
	TEXT_CSS,
	TEXT_CSV,
	TEXT_ENRICHED,
	TEXT_HTML,
	TEXT_PLAIN,
	TEXT_RTF,
	TEXT_TSV,
	TEXT_URI_LIST,
	TEXT_XML,
	TEXT_XML_EXTERNAL_PARSED_ENTITY,
	
	VIDEO_BMPEG,
	VIDEO_JPEG,
	VIDEO_JPEG2000,
	VIDEO_MP4,
	VIDEO_MPEG4_GENERIC,
	VIDEO_MPEG,
	VIDEO_OGG,
	VIDEO_QUICKTIME,
	VIDEO_AVI,;
}
