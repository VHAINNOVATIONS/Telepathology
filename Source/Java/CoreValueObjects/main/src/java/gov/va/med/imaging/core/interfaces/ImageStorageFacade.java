/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 7, 2008
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
package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.enums.StorageProximity;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedImageStreamResponse;
import gov.va.med.imaging.exchange.storage.ByteBufferBackedInputStream;

/**
 * Facade interface for image storage types
 * 
 * @author VHAISWWERFEJ
 *
 */
public interface ImageStorageFacade 
{
	
	/**
	 * Retrieves an open sized stream to an image. THIS SHOULD NOT BE USED TO RETRIEVE A TXT FILE!
	 * @param imageIdentifier The identifier for a file (Filename, URN, etc)
	 * @param imageCredentials The credentials necessary to open the file
	 * @param imageProximity The proximity of the file
	 * @param requestFormatQualityList
	 * @return The open sized stream
	 * @throws ImageNearLineException Occurs if the there is a nearline exception
	 * @throws ImageNotFoundException Occurs if the image does not exist or cannot be read
	 */
	public ByteBufferBackedImageStreamResponse openImageStream(String imageIdentifier, 
			StorageCredentials imageCredentials, StorageProximity imageProximity,
			ImageFormatQualityList requestFormatQualityList)
	throws ImageNearLineException, ImageNotFoundException, ConnectionException, ImageConversionException, MethodException;
	
	/**
	 * Retrieves an open sized stream to a TXT file.  This should be used to retrieve a TXT file.
	 * Notice it takes the same parameters as the openImageStream - this is intentional. Its up
	 * to the implementing function to determine how to take the image details and make the necessary 
	 * changes to retrieve a TXT file. 
	 * @param imageIdentifier the identifier for the image (not the txt file)
	 * @param imageCredentials The credentials to access the TXT file
	 * @param imageProximity The proximity of the TXT file
	 * @return An opened sized input stream to the TXT file
	 * @throws ImageNearLineException Occurs if the there is a nearline exception
	 * @throws ImageNotFoundException Occurs if the TXT file does not exist or cannot be read
	 */
	public ByteBufferBackedInputStream openTXTStream(String imageIdentifier, 
			StorageCredentials imageCredentials, StorageProximity imageProximity)
	throws ImageNearLineException, ImageNotFoundException, ConnectionException, MethodException;
	
	public ByteBufferBackedInputStream openPhotoId(String imageIdentifier, StorageCredentials imageCredentials)
	throws ImageNotFoundException, ConnectionException, MethodException;
	
	/**
	 * Clear any internal buffers to be sure not wasting memory. Return a buffer to its memory pool
	 * @deprecated
	 */
	public void clearBuffers();
}
