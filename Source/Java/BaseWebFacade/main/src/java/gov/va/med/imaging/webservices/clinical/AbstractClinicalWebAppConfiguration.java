/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 16, 2010
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
package gov.va.med.imaging.webservices.clinical;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import gov.va.med.MediaType;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.VistaImageType;
import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;

/**
 * Configuration values for clinical interfaces (AWIV and Clinical Display). This configuration contains 
 * the content types that should be used when requesting images of specific VistaImageTypes
 * 
 * @author vhaiswwerfej
 *
 */
public class AbstractClinicalWebAppConfiguration 
extends AbstractBaseFacadeConfiguration 
{
	
	private final static String contentTypeDelimiter = ",";
	private Hashtable<String, ClinicalContentTypeConfig> contentTypeConfigurations = null;
	private Map<MediaType, VistaImageType> mediaTypeMapping = 
		new Hashtable<MediaType, VistaImageType>();
	
	public AbstractClinicalWebAppConfiguration()
	{
		contentTypeConfigurations = new Hashtable<String, ClinicalContentTypeConfig>();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration() 
	{
		contentTypeConfigurations.clear();		
		addJpegFormats();		
		addBwMedFormats();		
		addColorScanFormats();
		addPatientPhoto();
		addXray();
		addXrayJpeg();
		addTiff();
		addMotionVideo();
		addHtml();
		addWordDoc();
		addTextPlain();
		addPdf();
		addRtf();
		addAudio();
		addDicom();	
		addXmlFormats();
		loadDefaultMediaTypeMapping();
		return this;
	}
	
	private void loadDefaultMediaTypeMapping()
	{
		mediaTypeMapping.put(MediaType.APPLICATION_DICOM, VistaImageType.DICOM);
		mediaTypeMapping.put(MediaType.IMAGE_J2K, VistaImageType.DICOM);
		mediaTypeMapping.put(MediaType.IMAGE_TGA, VistaImageType.XRAY);
		//mediaTypeMapping.put(MediaType.APPLICATION_OCTETSTREAM, null);
		mediaTypeMapping.put(MediaType.AUDIO_MP4, VistaImageType.AUDIO);
		mediaTypeMapping.put(MediaType.AUDIO_WAV, VistaImageType.AUDIO);
		mediaTypeMapping.put(MediaType.AUDIO_MPEG, VistaImageType.AUDIO);
		mediaTypeMapping.put(MediaType.IMAGE_BMP, VistaImageType.JPEG);
		mediaTypeMapping.put(MediaType.IMAGE_TIFF, VistaImageType.TIFF);
		mediaTypeMapping.put(MediaType.TEXT_HTML, VistaImageType.HTML);
		mediaTypeMapping.put(MediaType.VIDEO_AVI, VistaImageType.MOTION_VIDEO);
		mediaTypeMapping.put(MediaType.VIDEO_MPEG, VistaImageType.MOTION_VIDEO);	
		mediaTypeMapping.put(MediaType.IMAGE_JPEG, VistaImageType.DOD_JPG); 		
		mediaTypeMapping.put(MediaType.APPLICATION_DOC, VistaImageType.DOD_WORD_DOCUMENT); 		
		mediaTypeMapping.put(MediaType.APPLICATION_PDF, VistaImageType.DOD_PDF); 				
		mediaTypeMapping.put(MediaType.TEXT_PLAIN, VistaImageType.DOD_ASCII_TEXT); 		
		mediaTypeMapping.put(MediaType.TEXT_RTF, VistaImageType.DOD_RTF); 		
		mediaTypeMapping.put(MediaType.APPLICATION_RTF, VistaImageType.DOD_RTF);
		//mediaTypeMapping.put(MediaType.APPLICATION_DOCX, VistaImageType.DOD_DOCX_DOCUMENT);
		mediaTypeMapping.put(MediaType.APPLICATION_DOCX, VistaImageType.DOD_WORD_DOCUMENT);
		mediaTypeMapping.put(MediaType.TEXT_XML, VistaImageType.XML);
	}	
	
	public VistaImageType getVistaImageType(MediaType mediaType)
	{
		for(Entry<MediaType, VistaImageType> entry : mediaTypeMapping.entrySet())
		{
			if(entry.getKey() == mediaType)
			{
				return entry.getValue();
			}	
		}		
		return null;
	}
	
	/**
	 * Get the configuration for the clinical Display web app content type for the given interface
	 * version, VistA image type and image quality. Will return null if no configuration is found.
	 * 
	 * @param interfaceVersion
	 * @param imageType
	 * @param imageQuality
	 * @return
	 */
	public ClinicalContentTypeConfig getContentTypeConfiguration(VistaImageType imageType, 
			ImageQuality imageQuality)
	{
		String key = getConfigurationKey(imageType, imageQuality);
		ClinicalContentTypeConfig config = contentTypeConfigurations.get(key);
		return config;
	}
	
	/**
	 * @return the contentTypeConfigurations
	 */
	public Hashtable<String, ClinicalContentTypeConfig> getContentTypeConfigurations() {
		return contentTypeConfigurations;
	}

	/**
	 * @param contentTypeConfigurations the contentTypeConfigurations to set
	 */
	public void setContentTypeConfigurations(
			Hashtable<String, ClinicalContentTypeConfig> contentTypeConfigurations) {
		this.contentTypeConfigurations = contentTypeConfigurations;
	}
	
	private void addJpegFormats()
	{		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.JPEG, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
	}
	
	private void addPatientPhoto()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PATIENT_PHOTO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
	}
	
	private void addXrayJpeg()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY_JPEG, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
	}
	
	private void addTiff()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() +
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.TIFF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
	}
	
	private void addMotionVideo()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.REFERENCE, 
				ImageFormat.AVI.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.AVI.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.AVI.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.REFERENCE, 
				ImageFormat.AVI.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.AVI.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.AVI.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.REFERENCE, 
				ImageFormat.AVI.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.AVI.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.MOTION_VIDEO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.AVI.getMime()));
	}
	
	private void addHtml()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.REFERENCE, 
				ImageFormat.HTML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.HTML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.HTML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.REFERENCE, 
				ImageFormat.HTML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.HTML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.HTML.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.REFERENCE, 
				ImageFormat.HTML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.HTML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.HTML, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.HTML.getMime()));
	}
	
	private void addWordDoc()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.REFERENCE, 
				ImageFormat.DOC.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DOC.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DOC.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.REFERENCE, 
				ImageFormat.DOC.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DOC.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DOC.getMime()));
		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.REFERENCE, 
				ImageFormat.DOC.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DOC.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.WORD_DOCUMENT, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DOC.getMime()));
	}
	
	private void addTextPlain()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.REFERENCE, 
				ImageFormat.TEXT_PLAIN.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.TEXT_PLAIN.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.TEXT_PLAIN.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.REFERENCE, 
				ImageFormat.TEXT_PLAIN.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.TEXT_PLAIN.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.TEXT_PLAIN.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.REFERENCE, 
				ImageFormat.TEXT_PLAIN.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.TEXT_PLAIN.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.ASCII_TEXT, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.TEXT_PLAIN.getMime()));
	}
	
	private void addPdf()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.THUMBNAIL,
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.REFERENCE, 
				ImageFormat.PDF.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.PDF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.PDF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.REFERENCE, 
				ImageFormat.PDF.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.PDF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.PDF.getMime()));
		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.REFERENCE, 
				ImageFormat.PDF.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.PDF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.PDF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.PDF.getMime()));
	}
	
	private void addRtf()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.THUMBNAIL,  
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.REFERENCE, 
				ImageFormat.RTF.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.RTF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.RTF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.REFERENCE, 
				ImageFormat.RTF.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.RTF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.RTF.getMime()));
		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.REFERENCE, 
				ImageFormat.RTF.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.RTF.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.RTF, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.RTF.getMime()));
	}
	
	private void addAudio()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.REFERENCE, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.REFERENCE, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));	
		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.REFERENCE, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.AUDIO, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.WAV.getMime() + contentTypeDelimiter + ImageFormat.MP3.getMime()));	
	}
	
	private void addXray()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.REFERENCE, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.REFERENCE, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.REFERENCE, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XRAY, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime()));	
	}
	
	private void addDicom()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.REFERENCE, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.REFERENCE, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.REFERENCE, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime()));	
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.DICOM, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.J2K.getMime() + 
				contentTypeDelimiter + ImageFormat.DICOM.getMime() + contentTypeDelimiter + ImageFormat.TGA.getMime()));	
	}
	
	private void addBwMedFormats()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.BWMED, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
	}
	
	private void addColorScanFormats()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));		
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.REFERENCE, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.COLOR_SCAN, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.TIFF.getMime() + 
				contentTypeDelimiter + ImageFormat.BMP.getMime()));
	}
	
	private void addXmlFormats()
	{
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XML, 
				ImageQuality.THUMBNAIL, 
				ImageFormat.JPEG.getMime() + contentTypeDelimiter + ImageFormat.BMP.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XML, 
				ImageQuality.REFERENCE, 
				ImageFormat.XML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XML, 
				ImageQuality.DIAGNOSTIC, 
				ImageFormat.XML.getMime()));
		addConfiguration(new ClinicalContentTypeConfig(VistaImageType.XML, 
				ImageQuality.DIAGNOSTICUNCOMPRESSED, 
				ImageFormat.XML.getMime()));
	}
	
	private void addConfiguration(ClinicalContentTypeConfig config)
	{
		String key = getConfigurationKey(config);
		contentTypeConfigurations.put(key, config);
	}
	
	private String getConfigurationKey(ClinicalContentTypeConfig config)
	{
		return getConfigurationKey(config.getImageType(), config.getImageQuality());
	}
	
	private String getConfigurationKey(VistaImageType imageType, ImageQuality imageQuality)
	{
		return imageType.name() + "_" + imageQuality.getCanonical();
	}
	
	public Map<MediaType, VistaImageType> getMediaTypeMapping()
	{
		return mediaTypeMapping;
	}

	public void setMediaTypeMapping(
			Map<MediaType, VistaImageType> mediaTypeMapping)
	{
		this.mediaTypeMapping = mediaTypeMapping;
	}

}
