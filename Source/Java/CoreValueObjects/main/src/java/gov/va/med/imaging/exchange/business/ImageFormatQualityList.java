/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 16, 2008
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

import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.url.vista.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ImageFormatQualityList is a list in order of ImageFormatQualities.  
 * This order determines the priority of image format/qualities the requester would like.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageFormatQualityList 
extends ArrayList<ImageFormatQuality> 
{
	private static final long serialVersionUID = 8062000826342269730L;
	
	public ImageFormatQualityList()
	{
		super();
	}
	
	/**
	 * Add multiple ImageFormats all with the same image quality to the list
	 * @param formats List of image formats to add
	 * @param quality The image quality to add for each image format
	 */
	public void addAll(List<ImageFormat> formats, ImageQuality quality)
	{
		for(int i = 0; i < formats.size(); i++)
		{
			this.add(new ImageFormatQuality(formats.get(i), quality));
		}
	}
	
	/**
	 * Checks to see if the list contains a particular ImageFormatQuality
	 * @param formatQuality
	 * @return True if the list has this item, false otherwise
	 */
	public boolean contains(ImageFormatQuality formatQuality)
	{
		for(ImageFormatQuality quality : this)
		{
			if(quality.equals(formatQuality))
				return true;
		}		
		return false;
	}
	
	/**
	 * Converts the entire contents of the list into a proper accept string
	 * @param includeQuality Determines if the accept string should contain the integer quality q-values
	 * @param includeEnclosedMime Determines if the enclosed mime type should be included in the result string
	 * @return An accept string representing the list of image format qualities.
	 */
	public String getAcceptString(boolean includeQuality, boolean includeEnclosedMime)
	{
		if(includeQuality)
		{
			String accept = "";
			for(ImageFormatQuality quality : this)
			{
				String mime = "";
				if(includeEnclosedMime)
					mime = quality.getImageFormat().getMimeWithEnclosedMime();
				else
					mime = quality.getImageFormat().getMime();
				if(accept.length() > 0)
				{
					accept += "," + mime + ";q=" + getQValue(quality.getImageQuality());
				}
				else
				{
					accept += mime + ";q=" + getQValue(quality.getImageQuality());
				}
			}	
			return accept;
		}
		else
		{
			String accept = "";
			for(ImageFormatQuality quality : this)
			{
				String mime = "";
				if(includeEnclosedMime)
					mime = quality.getImageFormat().getMimeWithEnclosedMime();
				else
					mime = quality.getImageFormat().getMime();
				if(accept.length() > 0)
				{
					accept += "," + mime;
				}
				else
				{
					accept += mime;
				}
			}	
			return accept;
		}
	}
	
	/**
	 * Converts the entire contents of the list into a proper accept string
	 * @param includeQuality Determines if the accept string should contain the integer quality q-values
	 * @return An accept string representing the list of image format qualities.
	 */
	public String getAcceptString(boolean includeQuality)
	{
		return getAcceptString(includeQuality, false);
	}
	
	private String getQValue(ImageQuality imageQuality)
	{
		int val = imageQuality.getCanonical();
		double v = (double)val / 100.0;
		DecimalFormat format = new DecimalFormat("#.00");
		return format.format(v);
	}
	
	/**
	 * Add a new image format quality to the list uniquely based on the quality level and the mime type. If this quality and mime
	 * type already exist in the list, it won't be added.  This function does not do any sorting of the items in the list
	 * @param quality The quality to add only if it doesn't already exist.
	 */
	public void addUniqueMime(ImageFormatQuality quality)
	{
		boolean found = false;
		for(ImageFormatQuality q : this)
		{
			if(q.getImageQuality() == quality.getImageQuality())
			{
				if(q.getImageFormat().getMime().equalsIgnoreCase(quality.getImageFormat().getMime()))
				{
					found = true;
					break;
				}
			}
		}
		if(!found)
			this.add(quality);
	}
	
	/**
	 * To be considered equals() both lists must have the same
	 * elements in the same order.
	 * 
	 * @see java.util.AbstractList#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ImageFormatQualityList)
		{
			ImageFormatQualityList that = (ImageFormatQualityList)obj;
			
			if(this.size() == that.size())
			{
				Iterator<ImageFormatQuality> thisIter = this.iterator();
				Iterator<ImageFormatQuality> thatIter = that.iterator();

				try
				{
					while(thisIter.hasNext())
						if( ! thisIter.next().equals(thatIter.next()) )
							return false;
				}
				catch(ConcurrentModificationException cmX)
				{
					return false;
				}
				
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a ImageFormatQualityList instance that has one ImageFormatQuality instance
	 * @param imageFormat
	 * @param imageQuality
	 * @return
	 */
	public static ImageFormatQualityList createListFromFormatQuality(ImageFormat imageFormat, ImageQuality imageQuality)
	{
		ImageFormatQualityList qualityList = new ImageFormatQualityList();
		qualityList.add(new ImageFormatQuality(imageFormat, imageQuality));
		return qualityList;
	}
	
	/**
	 * 
	 * Must include the image quality and optionally should include the sub image format (not required but recommended)
	 * <br />
	 * <b>ex:</b> image/jpeg;q=.70,application/dicom;q=.70,application/dicom/image/j2k;q=.70,image/x-targa;q=.70,image/bmp;q=.70
	 * 
	 * @param imageFormatAccessString
	 * 
	 * @return
	 */
	public static ImageFormatQualityList createListFromAcceptString(String imageFormatAcceptString)
	{
		ImageFormatQualityList imageFormatQualityList = new ImageFormatQualityList();
		
		for(StringTokenizer commaTokenizer = new StringTokenizer(imageFormatAcceptString, ","); commaTokenizer.hasMoreTokens();)
		{
			String imageFormatQualityString = commaTokenizer.nextToken().trim();
			ImageFormat imageFormat = ImageFormat.valueOfMimeType(StringUtils.MagPiece(imageFormatQualityString, StringUtils.SEMICOLON, 1));
			if(imageFormat != null)
			{
				if(imageFormatQualityString.contains(StringUtils.SEMICOLON))
				{
					String q = StringUtils.MagPiece(imageFormatQualityString, StringUtils.SEMICOLON, 2);
					// must have a q value to be able to determine the quality, if no q value then the 
					// format is ignored
					if((q != null) && (q.length() > 0))
					{
						double qv = Double.parseDouble(q.substring(2));
						
						
						ImageQuality imageQuality = ImageQuality.getImageQuality(qv);
						imageFormatQualityList.add(new ImageFormatQuality(imageFormat, imageQuality));
					}
				}
			}			
		}
		return imageFormatQualityList;
	}
	
	public ImageQuality getFirstImageQuality()
	{
		if(this.size() == 0)
			return null;
		ImageFormatQuality quality = this.get(0);
		return quality.getImageQuality();
	}
	
	/**
	 * This method modified the list of ImageFormats in the ImageFormatQualityList to only include those formats
	 * included in the allowedFormats list.  This is necessary to prune a request when communicating with an older VIX
	 * that does not support newer formats.
	 * 
	 * After this method is called the underlying list of ImageFormatQuality objects only includes the image formats
	 * allowed in the allowedFormats list
	 * 
	 * @param allowedFormats List of allowed formats (in no particular order)
	 */
	public void pruneToAllowedFormats(List<ImageFormat> allowedFormats)
	{
		if(allowedFormats != null && allowedFormats.size() > 0)
		{
			Iterator<ImageFormatQuality> iter = this.iterator();
			while(iter.hasNext())
			{
				ImageFormatQuality imageFormatQuality = iter.next();
				if(!isFormatAllowed(imageFormatQuality.getImageFormat(), allowedFormats))
				{
					iter.remove();
				}
			}
		}
	}
	
	/**
	 * Determines if the specified ImageFormat is included in the list of allowed ImageFormats
	 * @param imageFormat
	 * @param allowedFormats
	 * @return True if imageFormat is in the allowedFormats list, false otherwise
	 */
	private boolean isFormatAllowed(ImageFormat imageFormat, List<ImageFormat> allowedFormats)
	{
		for(ImageFormat allowedFormat : allowedFormats)
		{
			if(imageFormat == allowedFormat)
				return true;
		}
		return false;
	}
}
