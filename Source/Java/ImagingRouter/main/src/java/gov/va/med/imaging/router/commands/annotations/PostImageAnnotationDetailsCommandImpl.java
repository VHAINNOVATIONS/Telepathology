/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 17, 2011
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
package gov.va.med.imaging.router.commands.annotations;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotation;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationSource;
import gov.va.med.imaging.router.commands.AbstractStudyCommandImpl;
import gov.va.med.imaging.router.commands.CommonStudyCacheFunctions;
import gov.va.med.imaging.router.facade.ImagingContext;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PostImageAnnotationDetailsCommandImpl
extends AbstractStudyCommandImpl<ImageAnnotation>
{
	private static final long serialVersionUID = -3542386871762299844L;
	
	private final AbstractImagingURN imagingUrn; 
	private final String annotationDetails;
	private final String annotationVersion;
	private final ImageAnnotationSource annotationSource;
	
	public PostImageAnnotationDetailsCommandImpl(
			AbstractImagingURN imagingUrn, String annotationDetails,
			String annotationVersion, ImageAnnotationSource annotationSource)
	{
		super();
		this.imagingUrn = imagingUrn;
		this.annotationDetails = annotationDetails;
		this.annotationVersion = annotationVersion;
		this.annotationSource = annotationSource;
	}

	public AbstractImagingURN getImagingUrn()
	{
		return imagingUrn;
	}

	public String getAnnotationDetails()
	{
		return annotationDetails;
	}

	public String getAnnotationVersion()
	{
		return annotationVersion;
	}

	public ImageAnnotationSource getAnnotationSource()
	{
		return annotationSource;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return getImagingUrn();
	}

	@Override
	public ImageAnnotation callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		
		ImageAnnotation result = 
			ImagingContext.getRouter().postImageAnnotationDetails(getImagingUrn(), 
					getAnnotationDetails(), getAnnotationVersion(), getAnnotationSource());
		// after storing the annotations for an image we need to see if there is cached metadata for the image
		// if so then the image object must be updated to indicate there are actually annotations for the image
		AbstractImagingURN imagingUrn = result.getImagingUrn();
		if(imagingUrn instanceof ImageURN)
		{
			ImageURN imageUrn = (ImageURN)imagingUrn;
			Image image = findImageInCachedStudyGraph(imageUrn);
			if(image != null)
			{
				if(!image.isImageHasAnnotations())
				{
					getLogger().info("Image '" + imageUrn.toString() + "' did not previously have annotations, updating cached object to indicate annotations now stored with image");
					image.setImageHasAnnotations(true);
					try
					{
						CommonStudyCacheFunctions.updateImageInCache(getCommandContext(), image);
					}
					catch (URNFormatException urnfX)
					{
						getLogger().error("URNFormatException getting the study for the image, " + urnfX.getMessage());
					}
				}				
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
