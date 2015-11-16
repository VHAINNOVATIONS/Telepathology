/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 5, 2008
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.conversion.ImageConversionUtility;
import gov.va.med.imaging.conversion.enums.ImageConversionSatisfaction;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vista.storage.SmbStorageUtility;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Implementation of the ImageDataSourceSpi for retrieving images from a VistA system
 * 
 * @author VHAISWWERFEJ
 *
 */
public class VistaImageDataSourceService 
extends AbstractBaseVistaImageService 
{
	
	public final static boolean USE_ASYNCHRONOUS_DISCONNECT = false;	// set to true to use asynchronous VistaConnection

	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
	private final static Logger logger = Logger.getLogger(VistaImageDataSourceService.class);
	
//	private final static NetworkLocationCacheManager networkLocationCache = new NetworkLocationCacheManager();
	
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	// patch 66 has the HIS update RPC call but its not registered to MAG WINDOWS - this doesn't happen until
	// Patch 83, so P83 is required for image retrieval.
	public final static String MAG_REQUIRED_VERSION = "3.0P83"; 
	
	private final static String DICOM_SOP_INSTANCE_UID_FIELD = "0008,0018";	
	
	 /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImageDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
		// CTB 29Nov2009
		//String decodedImageIen = Base32ConversionUtility.base32Decode(image.getIen());
		String decodedImageIen = image.getIen();
		logger.info("getImageHISUpdates(" + decodedImageIen + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		HashMap<String, String> hisUpdate = null;
		VistaQuery hisUpdateQuery = VistaImagingQueryFactory.createGetHisUpdateQuery(decodedImageIen);
		
		String rtn = null;
		try
		{
			rtn = vistaSession.call(hisUpdateQuery);		
			logger.debug("Result from HisUpdate RPC [" + rtn + "]");
			hisUpdate = VistaImagingTranslator.convertVistaHisUpdateToHashmap(rtn);			
			String value = hisUpdate.get(DICOM_SOP_INSTANCE_UID_FIELD);
			if(value != null)
			{
				logger.debug("His Update contains DICOM SOP INSTANCE UID field");
				// check for error in tag value
				String eMessage = StringUtils.MagPiece(value, StringUtils.COMMA, 1);
				if("-1".equals(eMessage)) {					
					value = getNewSopInstanceUID(vistaSession, decodedImageIen);
					logger.info("DICOM SOP INSTANCE UID field is missing, putting in new value[ " + value + "]");
					hisUpdate.put(DICOM_SOP_INSTANCE_UID_FIELD, value);
				}
			}
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error retrieving HIS update for image [" + decodedImageIen + "]", vmX);
			// this error occurs if there was something wrong calling the RPC - this should not prevent
			// the image from being displayed.
			// not really sure if should return empty map or null, trying null for now which will make
			// image conversion not work but should not stop image from being retrieved and converted to non-DICOM
			//throw new MethodException(vmX);
			return null;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error retrieving HIS update for image [" + decodedImageIen + "]", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		return hisUpdate;
	}

	private String getNewSopInstanceUID(VistaSession vistaSession, String decodedImageIen) 
	throws MethodException, ConnectionException, IOException
	{
		logger.info("getNewSopInstanceUID(" + decodedImageIen + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery newSopInstanceUidQuery = VistaImagingQueryFactory.createNewSOPInstanceUidQuery(getSite().getSiteNumber(), decodedImageIen);
		String rtn = null;
		try
		{
			rtn = vistaSession.call(newSopInstanceUidQuery);
			return rtn.trim();
		}
		catch(VistaMethodException vmX)
		{
			logger.error("Error create new SOP Instance UID for image [" + decodedImageIen + "]", vmX);
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			logger.error("Error create new SOP Instance UID for image [" + decodedImageIen + "]", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
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
		return "1";
	}
}
