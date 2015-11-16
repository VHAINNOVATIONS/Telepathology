/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 5, 2009
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
package gov.va.med.imaging.wado;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.http.AcceptElement;
import gov.va.med.imaging.http.AcceptElementList;
import gov.va.med.imaging.wado.query.WadoQuery;
import gov.va.med.imaging.wado.query.WadoRequest;
import gov.va.med.imaging.wado.query.exceptions.WadoQueryComplianceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * The abstract root of Image facade servlets in the VIX architecture. This is not used by the Vix Gui web app.
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractBaseFacadeImageServlet 
extends AbstractBaseImageServlet 
{
	private static final long serialVersionUID = -8384468428645822396L;

	/**
	 * Returns the list of acceptable formats for thumbnail quality images
	 * @return
	 */
	protected abstract List<ImageFormat> getAcceptableThumbnailResponseTypes();
	
	/**
	 * Returns the list of acceptable formats for reference quality images
	 * @return
	 */
	protected abstract List<ImageFormat> getAcceptableReferenceResponseTypes(boolean includeSubTypes);
	
	/**
	 * Returns the list of acceptable formats for diagnostic quality images
	 * @return
	 */
	protected abstract List<ImageFormat> getAcceptableDiagnosticResponseTypes(boolean includeSubTypes);

	
	/**
	 * chooseXchangeContentType does HTTP request contentType check for non-WADO (XCHANGE) 
	 * compliant image requests. Returns accepted/selected contentType.
	 * For THUMBNAILs, only "image/jpeg" is accepted. For REFERENCE and DIAGNOSTIC quality images,
	 * "application/dicom" (default), and if alternate logic applies "image/jp2" and "image/jpeg"
	 * are accepted, if one or more are present the first is set.
	 * 
	 * @param acceptElementList list of requested contentType entries (AcceptElementList type)
	 * @return ImageContentType accepted/selected contentType
	 * @throws WadoQueryComplianceException
	 */
	protected List<ImageFormat> validateContentType(
		ImageQuality imageQuality, 
		AcceptElementList acceptElementList,
		List<ImageFormat> contentTypeWithSubTypeList)
	throws WadoQueryComplianceException
	{
		
		// if they are not the exact same size, throw away the content type list with sub types, its invalid
		
		if(contentTypeWithSubTypeList != null)
		{
			if(contentTypeWithSubTypeList.size() != acceptElementList.size())
				contentTypeWithSubTypeList = null;
		}
		
		boolean includeSubTypes = (contentTypeWithSubTypeList != null);
		
		if(imageQuality == ImageQuality.THUMBNAIL)
			return validateContentType(acceptElementList, getAcceptableThumbnailResponseTypes(), 
					contentTypeWithSubTypeList);
		else if(imageQuality == ImageQuality.REFERENCE)
			return validateContentType(acceptElementList, getAcceptableReferenceResponseTypes(includeSubTypes), 
					contentTypeWithSubTypeList);
		else if(imageQuality == ImageQuality.DIAGNOSTIC)
			return validateContentType(acceptElementList, getAcceptableDiagnosticResponseTypes(includeSubTypes), 
					contentTypeWithSubTypeList);
		else if(imageQuality == ImageQuality.DIAGNOSTICUNCOMPRESSED)
			return validateContentType(acceptElementList, getAcceptableDiagnosticResponseTypes(includeSubTypes), 
					contentTypeWithSubTypeList);

		throw new 
		WadoQueryComplianceException("Unknown image quality value for "
			+ (imageQuality == null ? "null" : imageQuality.name()) + 
			" image: '" + acceptElementList.toString() + "'" );
	}
	
	/**
	 * Validate that elements in the acceptElementList are in the acceptable response types.
	 * Any elements on the accept list that are not on the acceptable reponse types are ignored.
	 * If there are no common elements then it is an error because the client asked for something
	 * outside the specification definition. 
	 * The resulting list of ImageFormat will be in the order of the acceptable response types list.
	 */
	private List<ImageFormat> validateContentType(
		AcceptElementList acceptElementList, 
		List<ImageFormat> acceptableResponseTypes,
		List<ImageFormat> contentTypeWithSubTypeList)
	throws WadoQueryComplianceException
	{		
		List<ImageFormat> selectedContentType = new ArrayList<ImageFormat>();
		// JMW 8/18/08 - change the order of searching, search starting with the requested list so the order the request
		// was made in is preserved
		// the only problem with this is that it doesn't remove duplicate entries (done in seperate list below)
		//for(AcceptElement acceptElement : acceptElementList)
		for(int i = 0; i < acceptElementList.size(); i++)
		{
			AcceptElement acceptElement = acceptElementList.get(i);			
			String acceptElementType = acceptElement.getMediaType();
			String acceptElementSubType = acceptElement.getMediaSubType();
			String mime = acceptElementType + "/" + acceptElementSubType;
			
			ImageFormat contentTypeWithSubType = null;
			if((contentTypeWithSubTypeList != null) && (contentTypeWithSubTypeList.size() >= i))
			{
				contentTypeWithSubType = contentTypeWithSubTypeList.get(i);
				// JMW 3/16/2011 P104
				// Check for null contentTypeWithSubType which might occur if an image format requested could not be
				// mapped to an ImageFormat enumeration. It's OK to have null items in here (so the sizes of the two lists
				// match) but need to check for null to prevent NPE
				if((contentTypeWithSubType != null) && 
						(!contentTypeWithSubType.getMime().equals(mime)))
				{
					// the content type from the list does not match the accept type element, use the accept type element
					contentTypeWithSubType = null;
				}
			}			
			for(ImageFormat imageFormat : acceptableResponseTypes)
			{
				// if the current requested format is anything, then want to add all of the formats this interface supports
				if(ImageFormat.ANYTHING.getMime().equals(mime))
				{
					addUniqueFormatToList(imageFormat, selectedContentType);
					// don't break here, keep looping through the list
				}
				else
				{
					if(contentTypeWithSubType != null)
					{
						if(contentTypeWithSubType == imageFormat)
						{
							addUniqueFormatToList(imageFormat, selectedContentType);
							break; // break out of the for loop (already found the format that matches)
						}
					}
					else if(imageFormat.getMime().equals(mime))
					{
						addUniqueFormatToList(imageFormat, selectedContentType);
						break; // break out of the for loop (already found the format that matches)
					}
				}	
			}
		}		
		
		if (selectedContentType.size() == 0) 
		{
			String msg = "Illegal Exchange accept type[s], values (";
			
			String acceptElementsString = null;
			for( AcceptElement acceptElement : acceptElementList )
				acceptElementsString += (acceptElementsString==null ? "" : ",") + acceptElement.toString();
			msg += acceptElementsString;

			msg += ") are not of the acceptable types (";
			
			String acceptableElementsString = null;
			for( ImageFormat acceptableImageFormat : acceptableResponseTypes )
				acceptableElementsString += (acceptableElementsString==null ? "" : ",") + acceptableImageFormat.toString();
			msg += acceptableElementsString;
			
			msg += ").";
			
			throw new 
				WadoQueryComplianceException( msg );
		}

		return selectedContentType;
	}
	
	protected long doExchangeCompliantGet(WadoRequest wadoRequest, HttpServletResponse resp, 
			boolean logImageAccess) 
		throws WadoQueryComplianceException, IOException, ImageServletException, SecurityCredentialsExpiredException
		{
			getLogger().debug("Doing Exchange compliant GET:  " + wadoRequest.toString());
			WadoQuery wadoQuery = wadoRequest.getWadoQuery();
			
			ImageURN imageUrn = wadoQuery.getInstanceUrn();
			GlobalArtifactIdentifier gai = wadoQuery.getGlobalArtifactIdentifier();
			ImageQuality imageQuality = ImageQuality.getImageQuality( wadoRequest.getWadoQuery().getImageQualityValue() );
			AcceptElementList contentTypeList = wadoQuery.getContentTypeList();
			List<ImageFormat> contentTypeWtihSubTypeList = wadoQuery.getContentTypeWithSubTypeList();
			List<ImageFormat> acceptableResponseContent = 
				validateContentType(imageQuality, contentTypeList, contentTypeWtihSubTypeList);
			
			// Do sanity check for non-Wado requests
			getLogger().debug("   GET params:  imageUrn=[" + (imageUrn == null ? "NULL" : imageUrn)
					+ "]  ImageQuality=[" + imageQuality.name()
					+ "]");
					
			// if the object (instance) GUID is supplied then just stream the instance
			// back, ignoring any other parameters
			
			MetadataNotification metadataNotification = new MetadataNotification(resp, true);
			if(wadoQuery.isGetTxtFile()) 
			{
				return streamTxtFileInstanceByUrn(imageUrn, resp.getOutputStream(), metadataNotification);
			}
			else 
			{
				long bytes = 0L;
				if(imageUrn == null)
				{
					bytes = streamDocument(gai, resp.getOutputStream(), metadataNotification);
				}
				else
				{
					bytes = streamImageInstanceByUrn(imageUrn, imageQuality, acceptableResponseContent, 			
							resp.getOutputStream(), metadataNotification, logImageAccess);
				}
				return bytes;
			}
		}
	
	/**
	 * Add a unique image format to the list
	 * @param imageFormat
	 * @param selectedContentType
	 */
	private void addUniqueFormatToList(ImageFormat imageFormat, List<ImageFormat> selectedContentType)
	{
		boolean alreadyInList = false;
		for(ImageFormat selectedFormat : selectedContentType)
		{
			if(selectedFormat == imageFormat)
			{
				alreadyInList = true;
				break;
			}
		}
		if(!alreadyInList)					
			selectedContentType.add(imageFormat);
	}
	

}
