/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 7, 2010
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
package gov.va.med.imaging.exchange.business.vistarad;

import gov.va.med.MockDataGenerationType;
import gov.va.med.URN;
import gov.va.med.URNFactory;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.url.vista.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a list of exam images in an exam.  This object contains the raw RPC result 
 * header line for a set of exam images.
 * 
 * @author vhaiswwerfej
 *
 */
@MockDataGenerationType(componentValueType="gov.va.med.imaging.exchange.business.vistarad.ExamImage")
public class ExamImages 
extends ArrayList<ExamImage>
{
	private static final long serialVersionUID = 4999632459599290026L;
	
	private final String rawHeader;
	private boolean cacheImageMetadata;
	private String consolidatedSiteNumber = null;
	
	/**
	 * 
	 * @param rawHeader first line of the response from the data source
	 * @param alwaysFromCache if true, always return this metadata from the cache (if its in the cache), if false the determination of whether or not to use the metadata from the cache is based on the header
	 */
	public ExamImages(String rawHeader, boolean alwaysFromCache)
	{
		this.rawHeader = rawHeader;
		determineExamImagesOnJukebox(alwaysFromCache);
		extractConsolidatedSite();
	}
	
	public boolean containsConsolidatedSite()
	{
		return consolidatedSiteNumber != null;		
	}
	
	/**
	 * Look at the rawHeader and try to extract the consolidated site number (if it exists)
	 */
	private void extractConsolidatedSite()
	{
		if((rawHeader != null) && (rawHeader.length() > 0))
		{
			String fourthPiece = StringUtils.MagPiece(rawHeader, StringUtils.STICK, 4);
			consolidatedSiteNumber = StringUtils.MagPiece(fourthPiece, StringUtils.CARET, 7);
			if(consolidatedSiteNumber != null)
				consolidatedSiteNumber = consolidatedSiteNumber.trim();
		}
	}
	
	private void determineExamImagesOnJukebox(boolean alwaysFromCache)
	{
		if(rawHeader == null)
		{
			cacheImageMetadata = false;
		}
		else
		{
			// if alwaysFromCache - always use this metadata if it is found in the cache 
			if(alwaysFromCache)
			{
				cacheImageMetadata = true;
			}
			else
			{
				// determine based on the header string if the metadata can be used from cache
				if(rawHeader.startsWith("0^2~"))
				{
					cacheImageMetadata = false;			
				}
				else
				{
					cacheImageMetadata = true;
				}
			}
		}
	}

	public ExamImage get(String urnAsString) 
	throws URNFormatException
	{
		return get(URNFactory.create(urnAsString));
	}
	
	public ExamImage get(URN imageUrn) 
	{
		if(imageUrn == null)
			return null;
		
		for(ExamImage examImage : this)
			if( imageUrn.equals(examImage.getImageUrn()) )
				return examImage;
		
		return null;
	}
	
	/**
	 * @return the rawHeader
	 */
	public String getRawHeader() {
		return rawHeader;
	}

	/**
	 * Indicates if the exam image metadata should be cached
	 * 
	 * @return the cacheImageMetadata
	 */
	public boolean isCacheImageMetadata() {
		return cacheImageMetadata;
	}

	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (this.cacheImageMetadata ? 1231 : 1237);
		result = prime * result + ((this.rawHeader == null) ? 0 : this.rawHeader.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ExamImages other = (ExamImages) obj;
		if (this.cacheImageMetadata != other.cacheImageMetadata)
			return false;
		if (this.rawHeader == null)
		{
			if (other.rawHeader != null)
				return false;
		}
		else if (!this.rawHeader.equals(other.rawHeader))
			return false;
		
		return compositionEquals(other);
	}

	private boolean compositionEquals(ExamImages other)
	{
		Iterator<ExamImage> thisIter = iterator();
		Iterator<ExamImage> otherIter = other.iterator();
		
		while(thisIter.hasNext() && otherIter.hasNext())
		{
			ExamImage image = thisIter.next();
			ExamImage otherImage = otherIter.next();
			
			if(image == null && otherImage != null || image != null && otherImage == null)
				return false;
			if(image != null && otherImage != null && ! image.equals(otherImage))
				return false;
		}
		
		return !(thisIter.hasNext() || otherIter.hasNext());
	}
}
