/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 9, 2009
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.conversion.ImageConversionUtility;
import gov.va.med.imaging.conversion.enums.ImageConversionSatisfaction;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vista.storage.SmbStorageUtility;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImageDataSourceServiceV0 
extends AbstractBaseVistaImageService
{
	public final static boolean USE_ASYNCHRONOUS_DISCONNECT = false;	// set to true to use asynchronous VistaConnection

	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
		
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P59"; // patch 66 is required for the HIS update RPC	
	
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImageDataSourceServiceV0(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
        this.imageConversionUtility = new ImageConversionUtility(new SmbStorageUtility(), 
    		ImageConversionSatisfaction.SATISFY_ALLOWED_COMPRESSION, true);
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaImageService#getRequiredVistaImagingVersion()
	 */
	@Override
	protected String getRequiredVistaImagingVersion() 
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaImageService#getImageHISUpdates(gov.va.med.imaging.vistadatasource.session.VistaSession, gov.va.med.imaging.exchange.business.Image)
	 */
	@Override
	protected HashMap<String, String> getImageHISUpdates(
		VistaSession vistaSession, Image image) 
	throws MethodException, ConnectionException, IOException
	{
		HashMap<String, String> result = new HashMap<String, String>();
		// put in some known fields
		result.put(VistaImagingCommonUtilities.DICOM_PATIENT_NAME_KEY, image.getPatientName()); // patient name
		//result.put(VistaCommonUtilities.DICOM_PATIENT_ID_KEY, image.getPatientICN()); // patient ICN - should be SSN here - need to get it!
		result.put(VistaImagingCommonUtilities.DICOM_ALT_PATIENT_ID_KEY, image.getPatientId()); // patient ICN

		return result;
	}


	@Override
	protected VistaQuery getImageInformationQuery(String identifier,
			boolean includeDeletedImages)
	{
		return VistaImagingQueryFactory.createGetImageInformationQuery(identifier);
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "0";
	}
}
