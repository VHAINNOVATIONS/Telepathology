/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 29, 2011
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
package gov.va.med.imaging.vistaimagingdatasource.vix.translator;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.ImageAnnotationURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotation;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationDetails;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationSource;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationUser;
import gov.va.med.imaging.url.vista.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingVixTranslator
{
	
	private final static Logger logger = 
		Logger.getLogger(VistaImagingVixTranslator.class);

	/**
	 * Translate the raw result from VistA into ImageAnnotation business objects
	 * 
		1^3^242
		1^IMAGPROVIDERONETWOSIX,ONETWOSIX^Jul 26, 2011@07:50:04^16.2^CLINICAL_CAPTURE^0^^CARDIOLOGY^SALT LAKE CITY^126
		2^IMAGPROVIDERONETWOSIX,ONETWOSIX^Jul 26, 2011@07:50:59^16.2^CLINICAL_CAPTURE^0^^CARDIOLOGY^SALT LAKE CITY^126
		3^IMAGPROVIDERONETWOSIX,ONETWOSIX^Jul 26, 2011@07:57:10^16.2^CLINICAL_CAPTURE^0^^CARDIOLOGY^SALT LAKE CITY^126
	 * 
	 * 
	 * @param vistaResult
	 * @return
	 */	
	public static List<ImageAnnotation> translateImageAnnotations(AbstractImagingURN imagingUrn, String vistaResult)
	throws MethodException
	{
		List<ImageAnnotation> imageAnnotations = new ArrayList<ImageAnnotation>();
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		String statusLine = lines[0].trim();
		if(!statusLine.startsWith("1"))
		{
			logger.error("Error retrieving annotations for image [" + imagingUrn.toString() + "], " + statusLine);
			throw new MethodException("Exception retrieving image annotations for image '" + imagingUrn.toString() + "', " + statusLine);
		}
		String imageIen = getImageIenFromStatusLine(statusLine, imagingUrn);		
		for(int i = 1; i < lines.length; i++)
		{
			imageAnnotations.add(translateImageAnnotation(imagingUrn, imageIen, lines[i].trim()));
		}
		return imageAnnotations;		
	}
	
	/**
	 * Translate an image annotation 
	 * 
		3^IMAGPROVIDERONETWOSIX,ONETWOSIX^Jul 26, 2011@07:57:10^16.2^CLINICAL_CAPTURE^0^^CARDIOLOGY^SALT LAKE CITY^126
	 * 
	 * @param imagingUrn The identifier of the object containing the annotation
	 * @param imageIen The ien of the image (since the AbstractImagingURN may or may not be an image)
	 * @param imageAnnotationLine
	 * @return
	 */
	private static ImageAnnotation translateImageAnnotation(AbstractImagingURN imagingUrn,
			String imageIen,
			String imageAnnotationLine)
	throws MethodException
	{
		String [] pieces = StringUtils.Split(imageAnnotationLine, StringUtils.CARET);
		String layerId = pieces[0];
		String providerName = pieces[1];
		String annotationDate = pieces[2];
		String version = pieces[3];
		String source = pieces[4];
		String deleted = pieces[5];
		String savedAfterResulted = pieces[6];
		String service = pieces[7];
		// piece 8 is the site name for some reason
		String userDuz = pieces[9];
		
		try
		{
			ImageAnnotationURN annotationUrn = 
				ImageAnnotationURN.create(imagingUrn.getOriginatingSiteId(), layerId, 
						imageIen,
						imagingUrn.getPatientId());
			ImageURN imageUrn = null;
			if(imagingUrn instanceof ImageURN)
			{
				imageUrn = (ImageURN)imagingUrn;
			}
			else
			{
				imageUrn = ImageURN.create(imagingUrn.getOriginatingSiteId(), 
						imageIen, imagingUrn.getImagingIdentifier(), imagingUrn.getPatientId());
			}
			
			Date date = translateAnnotationDate(annotationDate);
			ImageAnnotationSource imageAnnotationSource = translateAnnotationSource(source);
			
			ImageAnnotationUser annotationUser = new ImageAnnotationUser(userDuz, providerName, service);
			
			return new ImageAnnotation(imageUrn, annotationUrn, annotationUser, date, 
					imageAnnotationSource, ("1".equals(savedAfterResulted) ? true : false), 
					version, ("1".equals(deleted)));
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException(urnfX);
		}
		catch(ParseException pX)
		{
			throw new MethodException(pX);
		}
	}
	
	/**
	 * Translate the result of a store annotation RPC call
	 * 
	 * 
	 	1^3^242
		3^^^^^^^^^126
	 * 
	 * 
	 * @param imagingUrn
	 * @param vistaResult
	 * @return
	 * @throws MethodException
	 */
	public static ImageAnnotation translateStoreImageAnnotationResult(AbstractImagingURN imagingUrn, String vistaResult)
	throws MethodException
	{
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		String statusLine = lines[0].trim();
		if(!statusLine.startsWith("1"))
		{
			logger.error("Error storing annotation details for image [" + imagingUrn.toString() + "], " + statusLine);
			throw new MethodException("Exception storing image annotation details (" + imagingUrn.toString() + "), " + statusLine);
		}		
		String imageIen = getImageIenFromStatusLine(statusLine, imagingUrn);
		return translateImageAnnotation(imagingUrn, imageIen, lines[1].trim());		
	}
	
	/**
	 * Translate an image annotation source from the string
	 * @param source
	 * @return
	 */
	private static ImageAnnotationSource translateAnnotationSource(String source)
	{
		return ImageAnnotationSource.getFromEncodedValue(source);
	}
	
	/**
	 * translate a date from string to Date object
	 * @param annotationDate
	 * @return
	 * @throws ParseException
	 */
	public static Date translateAnnotationDate(String annotationDate)
	throws ParseException
	{
		if(annotationDate.length() <= 0)
			return null;
						
		SimpleDateFormat sdf = getExpectedDateFormat(annotationDate);// new SimpleDateFormat("MMM dd, yyyy@kk:mm:ss");
		return sdf.parse(annotationDate);
	}
	
	private static SimpleDateFormat getExpectedDateFormat(String annotationDate)
	{
		// Jun 30, 2011@10:26:58 = 21 characters
		// Jun 30, 2011@10:26:5 = 20 characters
		// Jun 30, 2011@10:26 = 18 characters
		SimpleDateFormat sdf = null;
		switch(annotationDate.length())
		{
			case 18:
				sdf = new SimpleDateFormat("MMM dd, yyyy@kk:mm");
				break;
			case 20:
				sdf = new SimpleDateFormat("MMM dd, yyyy@kk:mm:s");
				break;
			default:
				sdf = new SimpleDateFormat("MMM dd, yyyy@kk:mm:ss");
				break;
		}
		return sdf;
	}
	
	/**
	 * Translate the details of an image annotation from raw RPC result into a business object
	 *
	 	1^4^242
		1^IMAGPROVIDERONETWOSIX,ONETWOSIX^Jul 26, 2011@07:50:04^16.2^CLINICAL_CAPTURE^0^^CARDIOLOGY^SALT LAKE CITY^126
		<?xml version="1.0"?>
		<History imageIEN="836" annotator="John Public" userDUZ="111" primesitenumber="222" service="Electrician" resulted="0" totalmarks="1" version="IG16.2"><Page number="0" marks="1">,,, 
	 * 
	 * @param imagingUrn
	 * @param imageAnnotationUrn
	 * @param vistaResult
	 * @return
	 * @throws MethodException
	 */
	public static ImageAnnotationDetails translateImageAnnotationDetails(AbstractImagingURN imagingUrn, 
			ImageAnnotationURN imageAnnotationUrn, String vistaResult)
	throws MethodException
	{
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		String statusLine = lines[0].trim();
		if(!statusLine.startsWith("1"))
		{
			logger.error("Annotation [" + imageAnnotationUrn.toString() + "] has error, " + statusLine);
			throw new MethodException("Exception retrieving image annotation details (" + imageAnnotationUrn.toString() + "), " + statusLine);
		}
		
		// JMW 9/27/2011 - The M code will return a status of 1 even if the annotation details are not found
		// check for a 0 in the count to indicate there are no lines
		String lineCount = StringUtils.MagPiece(statusLine, StringUtils.CARET, 2);
		if("0".equals(lineCount))
		{
			logger.error("Annotation [" + imageAnnotationUrn.toString() + "] has error, " + statusLine);
			throw new MethodException("No annotation details for [" + imageAnnotationUrn.toString() + "], " + statusLine);
		}
		
		String imageIen = getImageIenFromStatusLine(statusLine, imagingUrn);
		String headerLine = lines[1].trim();
		ImageAnnotation imageAnnotation = translateImageAnnotation(imagingUrn, imageIen, headerLine);
		StringBuilder xml = new StringBuilder(); 
		for(int i = 2; i < lines.length; i++)
		{
			//xml.append(lines[i].trim()); 
			// JMW 9/21/2011 - no longer doing a trim on the line, it was causing issues in the client because the XML would not be formatted properly if an attribute ended on a new line (no space between attributes)
			xml.append(lines[i]);
		}
		return new ImageAnnotationDetails(imageAnnotation, xml.toString());
	}
	
	private static String getImageIenFromStatusLine(String statusLine, AbstractImagingURN imagingUrn)
	{
		String [] statusLinePieces = StringUtils.Split(statusLine, StringUtils.CARET);
		String imageIen = statusLinePieces[2].trim();
		if(imageIen.length() <= 0)
			imageIen = imagingUrn.getImagingIdentifier();
		return imageIen;
	}
}
