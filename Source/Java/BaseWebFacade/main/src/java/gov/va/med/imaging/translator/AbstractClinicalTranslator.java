/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 11, 2012
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
package gov.va.med.imaging.translator;

import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.VistaImageType;
import gov.va.med.imaging.webservices.clinical.AbstractClinicalWebAppConfiguration;
import gov.va.med.imaging.webservices.clinical.ClinicalContentTypeConfig;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractClinicalTranslator
{
	public final static String cannedDoDAbstract = ".\\BMP\\DOD_Doc.bmp";
	public final static String annotationDateFormat = "MMM dd, yyyy@kk:mm:ss";
	
	protected final static String ncatRepositoryId = "2.16.840.1.113883.3.198.1";
	
	protected static VistaImageType getImageType(AbstractClinicalWebAppConfiguration configuration, Document document)
	{
		if(ncatRepositoryId.equals(document.getRepositoryId()))
		{
			return VistaImageType.NCAT;
		}
		
		VistaImageType vistaImageType = 
			configuration.getVistaImageType(document.getMediaType());	
		return vistaImageType;
		
	}
	
	protected static String getAbsUri(Image image, String imageUrn,
			AbstractClinicalWebAppConfiguration configuration)
	{
		if((image.getAbsFilename() != null) && (image.getAbsFilename().startsWith("-1")))
		{
			return image.getAbsFilename();
		}
		else
		{
			return "imageURN=" + imageUrn + "&imageQuality=20&contentType=" + getContentType(image, ImageQuality.THUMBNAIL, configuration);
		}
	}
	
	protected static String getFullUri(Image image, String imageUrn,
			AbstractClinicalWebAppConfiguration configuration)
	{
		boolean isRadImage = isRadImage(image);
		if((image.getFullFilename() != null) && (image.getFullFilename().startsWith("-1")))
		{
			return image.getFullFilename(); // put in error state
		}
		else
		{
			// if the image is not radiology, then this is a ref image request, if not rad image
			// then ref location is for the diagnostic image.
			int imageQuality = (isRadImage ? ImageQuality.REFERENCE.getCanonical() : ImageQuality.DIAGNOSTICUNCOMPRESSED.getCanonical());			
			return "imageURN=" + imageUrn + "&imageQuality=" + imageQuality + "&contentType=" + getContentType(image, 
					ImageQuality.REFERENCE, configuration);
		}
	}
	
	protected static String getDiagUri(Image image, String imageUrn,
			AbstractClinicalWebAppConfiguration configuration)
	{
		boolean isRadImage = isRadImage(image);
		if(isRadImage)
		{
			if((image.getBigFilename() != null) && (image.getBigFilename().startsWith("-1")))
			{
				return image.getBigFilename();
			}
			else
			{
				
				return "imageURN=" + imageUrn + "&imageQuality=90&contentType=" + 
					getContentType(image, ImageQuality.DIAGNOSTIC, configuration);
			}
		}
		else
		{
			return "";
		}
	}
	
	protected static String getContentType(Image image, ImageQuality imageQuality,
			AbstractClinicalWebAppConfiguration configuration)
	{
		String contentType = "";
		
		ClinicalContentTypeConfig config = getContentTypeConfig(image.getImgType(), 
				imageQuality, configuration);
		if(config != null)
			contentType = config.getContentType();
		
		if(contentType.length() > 0)
		{
			contentType += ",*/*";
		}
		else
		{
			contentType = "*/*";
		}
		return contentType;
	}
	
	private static ClinicalContentTypeConfig getContentTypeConfig(int imageType, 
			ImageQuality imageQuality, AbstractClinicalWebAppConfiguration configuration)
	{
		VistaImageType vistaImageType = getVistaImageType(imageType);
		if(vistaImageType == null)
		{
			return null;			
		}
		return configuration.getContentTypeConfiguration(vistaImageType, 
				imageQuality);
	}
	
	private static VistaImageType getVistaImageType(int imageType)
	{
		return VistaImageType.valueOfImageType(imageType);
	}
	
	protected static boolean isRadImage(Image image)
	{
		if(image == null)
			return false;
		int imgType = image.getImgType();
		if((imgType == VistaImageType.DICOM.getImageType()) || 
				(imgType == VistaImageType.XRAY.getImageType()))
		{
			return true;
		}
		return false;
	}
}
