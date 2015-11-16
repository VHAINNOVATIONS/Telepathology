/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 23, 2010
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
package gov.va.med.imaging.exchange.enums;

/**
 * Enumeration which contains the state of the study's ability to hold deleted images. Since some studies
 * don't include deleted images, and never can - we need to keep those separate from studies which do not
 * contain deleted images but potentially could. This enumeration defines the current state of the study
 * including its potential to hold deleted studies
 * 
 * @author vhaiswwerfej
 *
 */
public enum StudyDeletedImageState
{
	includesDeletedImages(true, true, 
			"Study includes deleted images"), 
	cannotIncludeDeletedImages (false, false, 
			"Study is not from a source that can include deleted images"), 
	doesNotIncludeDeletedImages(false, true, 
			"Study is from source that can include deleted images but does not");
	
	final String description;
	final boolean deletedImagesLoaded;
	final boolean canIncludeDeletedImages;

	StudyDeletedImageState(boolean deletedImagesLoaded, boolean canIncludeDeletedImages, 
			String description)
	{
		this.deletedImagesLoaded = deletedImagesLoaded;
		this.canIncludeDeletedImages = canIncludeDeletedImages;
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public boolean isDeletedImagesLoaded()
	{
		return deletedImagesLoaded;
	}

	public boolean isCanIncludeDeletedImages()
	{
		return canIncludeDeletedImages;
	}
}
