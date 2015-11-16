/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 10, 2008
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

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.datasource.annotations.SPI;


/**
 * @author VHAISWWERFEJ
 *
 */
@SPI(description="This SPI defines operations providing access to image (binaries).")
public interface ImageDataSourceSpi 
extends VersionableDataSourceSpi
{
	
	/**
	 * Retrieves an image from the data source
	 * @param imageUrn The unique identifier for the image
	 * @param requestFormatQualityList A list of the acceptable response types and quality values (in order).
	 * @return An ImageStreamResponse to the image that matches one of the requested accept types or
	 *         null if the Image does not exist.
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ImageStreamResponse getImage(GlobalArtifactIdentifier gai, ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException;
	
	/**
	 * Retrieves an image from the data source
	 * @param Image the meta data image object that represents the image to retrieve
	 * @param requestFormatQualityList A list of the acceptable response types and quality values (in order).
	 * @return An ImageStreamResponse to the image that matches one of the requested accept types
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract ImageStreamResponse getImage(Image image, ImageFormatQualityList requestFormatQualityList)
	throws MethodException, ConnectionException;
	
	/**
	 * Retrieves a stream to the TXT file or null if the datasource does not support retrieving only a TXT file
	 * @param image The image that the TEXT file represents
	 * @return An DataSourceInputStream containing the TXT file or null
	 * @throws UnsupportedOperationException
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws ImageNotFoundException
	 * @throws ImageNearLineException
	 */
	public abstract DataSourceInputStream getImageTXTFile(Image image)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException;
	
	/**
	 * Retrieves the text file associated with the image. This contains the DICOM header and other VistA meta fields
	 * @param imageURN The URN of the image that the TXT file is associated with
	 * @return A DataSourceInputStream that is connected to the TXT file
	 * @throws UnsupportedOperationException
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws ImageNotFoundException If the txt file does not exist
	 * @throws ImageNearLineException If the txt file is on storage that is not currently accessible
	 */
	public abstract DataSourceInputStream getImageTXTFile(ImageURN imageURN)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException;
	
	/**
	 * Retrieves the image information from the data source based on an image identifier
	 * @param imageURN The image identifier
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws ImageNotFoundException
	 */
	public abstract String getImageInformation(AbstractImagingURN imagingUrn, boolean includeDeletedImages)
	throws MethodException, ConnectionException, ImageNotFoundException;
	
	/**
	 * Retrieves the image global nodes from the data source
	 * @param imageURN The image identifier
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws ImageNotFoundException
	 */
	public abstract String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException, ImageNotFoundException;
	
	/**
	 * Retrieves the image dev fields from the data source
	 * @param imageURN The image identifier
	 * @param flags
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws MethodException
	 * @throws ConnectionException
	 * @throws ImageNotFoundException
	 */
	public abstract String getImageDevFields(AbstractImagingURN imagingUrn, String flags)
	throws MethodException, ConnectionException, ImageNotFoundException;
}
