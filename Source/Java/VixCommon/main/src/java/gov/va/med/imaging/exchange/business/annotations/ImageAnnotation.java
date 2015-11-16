/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 16, 2011
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
package gov.va.med.imaging.exchange.business.annotations;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.ImageAnnotationURN;

import java.util.Date;

/**
 * The description of an annotation layer
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageAnnotation
{
	private final AbstractImagingURN imagingUrn;
	private final ImageAnnotationURN annotationUrn;
	private final ImageAnnotationUser annotationSavedByUser;
	private final Date annotationSavedDate;
	private final ImageAnnotationSource annotationSource;
	private boolean savedAfterResult;
	private final String annotationVersion;
	private final boolean annotationDeleted;
	
	/**
	 * 
	 * @param imagingUrn
	 * @param annotationUrn
	 * @param annotationSavedByUser
	 * @param annotationSavedDate
	 * @param annotationSource
	 * @param savedAfterResult
	 * @param annotationVersion
	 * @param annotationDeleted
	 */
	public ImageAnnotation(AbstractImagingURN imagingUrn, ImageAnnotationURN annotationUrn,
			ImageAnnotationUser annotationSavedByUser,
			Date annotationSavedDate, ImageAnnotationSource annotationSource,
			boolean savedAfterResult, String annotationVersion,
			boolean annotationDeleted)
	{
		super();
		this.imagingUrn = imagingUrn;
		this.annotationUrn = annotationUrn;
		this.annotationSavedByUser = annotationSavedByUser;
		this.annotationSavedDate = annotationSavedDate;
		this.annotationSource = annotationSource;
		this.savedAfterResult = savedAfterResult;
		this.annotationVersion = annotationVersion;
		this.annotationDeleted = annotationDeleted;
	}

	/**
	 * Determines if the annotation layer was created after the associated consult was resulted
	 * @return
	 */
	public boolean isSavedAfterResult()
	{
		return savedAfterResult;
	}

	public void setSavedAfterResult(boolean savedAfterResult)
	{
		this.savedAfterResult = savedAfterResult;
	}

	/**
	 * Return the identifier for the annotation layer.  
	 * This identifier is not globally unique and is not even unique at a single site.  
	 * It is only unique within a single image entry
	 * @return
	 */
	public ImageAnnotationURN getAnnotationUrn()
	{
		return annotationUrn;
	}

	/**
	 * Return the globally unique image identifier the annotation layer is associated with
	 * @return
	 */
	public AbstractImagingURN getImagingUrn()
	{
		return imagingUrn;
	}

	/**
	 * Get the user who created the annotation layer
	 * @return
	 */
	public ImageAnnotationUser getAnnotationSavedByUser()
	{
		return annotationSavedByUser;
	}

	/**
	 * Return the date the annotation layer was saved
	 * @return
	 */
	public Date getAnnotationSavedDate()
	{
		return annotationSavedDate;
	}

	/**
	 * Return the source application which created the annotation layer
	 * @return
	 */
	public ImageAnnotationSource getAnnotationSource()
	{
		return annotationSource;
	}

	/**
	 * Return the version of the annotation layer
	 * @return
	 */
	public String getAnnotationVersion()
	{
		return annotationVersion;
	}

	/**
	 * Returns true of the annotation is deleted, false if it exists
	 * @return
	 */
	public boolean isAnnotationDeleted()
	{
		return annotationDeleted;
	}

	@Override
	public String toString()
	{
		return "ImageAnnotation [imagingUrn=" + imagingUrn + ", annotationUrn="
				+ annotationUrn + ", annotationSavedByUser="
				+ annotationSavedByUser + ", annotationSavedDate="
				+ annotationSavedDate + ", annotationSource="
				+ annotationSource + ", savedAfterResult=" + savedAfterResult
				+ ", annotationVersion=" + annotationVersion + "]";
	}
}
