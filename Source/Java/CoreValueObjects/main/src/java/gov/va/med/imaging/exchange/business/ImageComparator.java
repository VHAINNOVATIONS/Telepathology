/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 14, 2006
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.exchange.enums.ObjectOrigin;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A Comparator class to assure that the images in a series remain in order of ien
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageComparator implements Comparator<Image>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4742160600868516303L;

	/**
	 * 
	 */
	@Override
	public int compare(Image image1, Image image2)
	{
		if(image1 == null && image2 != null)
			return 1;
		else if(image1 != null && image2 == null)
			return -1;
		else if(image1 == null && image2 == null)
			return 0;
		
		/*
		int dicomImageNumberRelation = image2.getImageNumber().compareTo(image1.getImageNumber());
		int dicomImageUidRelation = image2.getImageUid().compareTo(image1.getImageUid());
		int imageIenRelation = image2.getIen().compareTo(image1.getIen());
		*/
		// not sure if it should be image1 - image2, or image2 - image1, 
		// with image2 - image1, DOD images came in wrong order. Will this change have an effect on VA images?
		
		
		if((image1.getObjectOrigin() == ObjectOrigin.DOD) || 
			(image2.getObjectOrigin() == ObjectOrigin.DOD)) 
		{
			int dicomImageNumberRelation = 0;
			
			try
			{
				Integer image1ImageNumber = Integer.parseInt(image1.getImageNumber());
				Integer image2ImageNumber = Integer.parseInt(image2.getImageNumber());
				dicomImageNumberRelation = image1ImageNumber.compareTo(image2ImageNumber);
			}
			catch(Throwable t) {}
		
			int dicomImageUidRelation = 
				image1.getImageUid() == null ? 
					(image2.getImageUid() == null ? 0 : 1) :  
					image1.getImageUid().compareTo(image2.getImageUid());
					
			int imageIenRelation = 
				image1.getIen() == null ? 
					(image2.getIen() == null ? 0 : 1) :
					image1.getIen().compareTo(image2.getIen());
			
			// if the image is from the DOD then the ien field (Id) does not necessarily contain useful sorting information (since it is Base64+ encoded)
			// if the dicom image id and dicom UID are not 0, then use only these two fields to sort by.
			return dicomImageNumberRelation != 0 ? dicomImageNumberRelation : 
				dicomImageUidRelation != 0 ? dicomImageUidRelation : 
					imageIenRelation;
			
		}
		else 
		{
			// JMW 3/12/08 - reversing the order of the sorts (was causing the images to be in descending order instead of ascending)
			
			int dicomImageNumberRelation = 0;
			
			try
			{
				Integer image1ImageNumber = Integer.parseInt(image1.getImageNumber());
				Integer image2ImageNumber = Integer.parseInt(image2.getImageNumber());
				dicomImageNumberRelation = image1ImageNumber.compareTo(image2ImageNumber);
			}
			catch(Throwable t) 
			{
				if(image1.getImageNumber() != null)
				{
					dicomImageNumberRelation = image1.getImageNumber().compareTo(image2.getImageNumber());
				}
			}
			
			//int dicomImageNumberRelation = image1.getImageNumber().compareTo(image2.getImageNumber());
			// allow null ImageUID for non-dicom images
			int dicomImageUidRelation = image1.getImageUid() != null && image2.getImageUid() != null ?   
				image1.getImageUid().compareTo(image2.getImageUid()) :
				0;
			// JMW 3/12/08 - base32 decoding the IEN so it can be sorted properly
			// CTB 29Nov2009
			//int imageIenRelation = Base32ConversionUtility.base32Decode(image1.getIen()).compareTo(Base32ConversionUtility.base32Decode(image2.getIen()));
			int imageIenRelation = image1.getIen().compareTo(image2.getIen());
			/*
			return 	imageIenRelation != 0 ? imageIenRelation :
				dicomImageUidRelation != 0 ? dicomImageUidRelation : 
					dicomImageNumberRelation;
			*/
			// JMW 9/26/08 - images IENs do NOT always indicate proper order for the images
			return 	dicomImageNumberRelation != 0 ? dicomImageNumberRelation :
				imageIenRelation != 0 ? imageIenRelation : 
					dicomImageUidRelation;
		}
	}
}
