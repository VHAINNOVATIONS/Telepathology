/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 30, 2011
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
package gov.va.med.imaging.router.commands;

import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;

/**
 * This command determines if an image is in the cache
 * 
 * @author VHAISWWERFEJ
 *
 */
public class GetImageCachedStatusCommandImpl
extends AbstractImageCommandImpl<Boolean>
{
	private static final long serialVersionUID = -5088788421301323415L;
	
	private final ImageURN imageUrn;
	private final ImageFormatQualityList imageFormatQualityList;
	
	public GetImageCachedStatusCommandImpl(ImageURN imageUrn,
			ImageFormatQualityList imageFormatQualityList)
	{
		this.imageUrn = imageUrn;
		this.imageFormatQualityList = imageFormatQualityList;
	}	

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		getLogger().info("Determining if image '" + getImageUrn().toString(SERIALIZATION_FORMAT.RAW) + "' is cached");
		return isInstanceInCache(getImageUrn(), getImageFormatQualityList());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((imageFormatQualityList == null) ? 0
						: imageFormatQualityList.hashCode());
		result = prime * result
				+ ((imageUrn == null) ? 0 : imageUrn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		GetImageCachedStatusCommandImpl other = (GetImageCachedStatusCommandImpl) obj;
		if (imageFormatQualityList == null)
		{
			if (other.imageFormatQualityList != null)
				return false;
		}
		else if (!imageFormatQualityList.equals(other.imageFormatQualityList))
			return false;
		if (imageUrn == null)
		{
			if (other.imageUrn != null)
				return false;
		}
		else if (!imageUrn.equals(other.imageUrn))
			return false;
		return true;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getImageUrn());
		sb.append(',');
		sb.append(getImageFormatQualityList() == null ? "<null image format>" : getImageFormatQualityList().toString());
		
		return sb.toString();
	}

	public ImageURN getImageUrn()
	{
		return imageUrn;
	}

	public ImageFormatQualityList getImageFormatQualityList()
	{
		return imageFormatQualityList;
	}

}
