/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 20, 2011
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
package gov.va.med.imaging.federation.rest.types;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class FederationImageAnnotationType
{
	private String imagingUrn;
	private String imageAnnotationUrn;
	private FederationImageAnnotationUserType annotationSavedByUser;
	private Date annotationSavedDate;
	private FederationImageAnnotationSourceType annotationSource;
	private boolean savedAfterResult;
	private String annotationVersion;
	private boolean annotationDeleted;
	
	public FederationImageAnnotationType()
	{
		super();
	}

	public String getImagingUrn()
	{
		return imagingUrn;
	}

	public void setImagingUrn(String imagingUrn)
	{
		this.imagingUrn = imagingUrn;
	}

	public String getImageAnnotationUrn()
	{
		return imageAnnotationUrn;
	}

	public void setImageAnnotationUrn(String imageAnnotationUrn)
	{
		this.imageAnnotationUrn = imageAnnotationUrn;
	}

	public FederationImageAnnotationUserType getAnnotationSavedByUser()
	{
		return annotationSavedByUser;
	}

	public void setAnnotationSavedByUser(
			FederationImageAnnotationUserType annotationSavedByUser)
	{
		this.annotationSavedByUser = annotationSavedByUser;
	}

	public Date getAnnotationSavedDate()
	{
		return annotationSavedDate;
	}

	public void setAnnotationSavedDate(Date annotationSavedDate)
	{
		this.annotationSavedDate = annotationSavedDate;
	}

	public FederationImageAnnotationSourceType getAnnotationSource()
	{
		return annotationSource;
	}

	public void setAnnotationSource(
			FederationImageAnnotationSourceType annotationSource)
	{
		this.annotationSource = annotationSource;
	}

	public boolean isSavedAfterResult()
	{
		return savedAfterResult;
	}

	public void setSavedAfterResult(boolean savedAfterResult)
	{
		this.savedAfterResult = savedAfterResult;
	}

	public String getAnnotationVersion()
	{
		return annotationVersion;
	}

	public void setAnnotationVersion(String annotationVersion)
	{
		this.annotationVersion = annotationVersion;
	}

	public boolean isAnnotationDeleted()
	{
		return annotationDeleted;
	}

	public void setAnnotationDeleted(boolean annotationDeleted)
	{
		this.annotationDeleted = annotationDeleted;
	}
}
