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
package gov.va.med.imaging.datasource;

import java.util.List;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.ImageAnnotationURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotation;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationDetails;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotationSource;

/**
 * This SPI retrieves and stores image annotation information.
 * 
 * @author VHAISWWERFEJ
 *
 */
@SPI(description="Defines the interface for image annotations.")
public interface ImageAnnotationDataSourceSpi
extends VersionableDataSourceSpi
{

	/**
	 * Return the list of annotation layers for an image
	 * @param imagingUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<ImageAnnotation> getImageAnnotations(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Get the annotation details for a specific layer
	 * @param imagingUrn
	 * @param imageAnnotationUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ImageAnnotationDetails getAnnotationDetails(AbstractImagingURN imagingUrn, 
			ImageAnnotationURN imageAnnotationUrn)
	throws MethodException, ConnectionException;
	
	/**
	 * Store a new image annotation layer
	 * @param imagingUrn
	 * @param annotationDetails
	 * @param annotationVersion
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ImageAnnotation storeImageAnnotationDetails(AbstractImagingURN imagingUrn, 
			String annotationDetails, String annotationVersion, ImageAnnotationSource annotationSource)
	throws MethodException, ConnectionException;
	
	/**
	 * Returns the most recent layer of annotations for the specified image
	 * @param imagingUrn
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ImageAnnotationDetails getMostRecentAnnotationDetails(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException;
}
