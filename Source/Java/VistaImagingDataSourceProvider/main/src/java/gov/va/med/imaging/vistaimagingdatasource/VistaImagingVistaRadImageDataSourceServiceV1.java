/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 13, 2009
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
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
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

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImagingVistaRadImageDataSourceServiceV1 
extends AbstractBaseVistaRadImageDataSourceService 
{
	
	public final static String MAG_REQUIRED_VERSION = "3.0.90|VIX";
	private final static String DICOM_SOP_INSTANCE_UID_FIELD = "0008,0018";	
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingVistaRadImageDataSourceServiceV1(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
        this.imageConversionUtility = new ImageConversionUtility(new SmbStorageUtility(), 
    		ImageConversionSatisfaction.SATISFY_ALLOWED_COMPRESSION, true);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.AbstractBaseVistaRadImageDataSourceService#getImageHISUpdates(gov.va.med.imaging.vistadatasource.session.VistaSession, gov.va.med.imaging.exchange.business.vistarad.ExamImage)
	 */
	@Override
	protected HashMap<String, String> getImageHISUpdates(VistaSession vistaSession, ExamImage image) 
	throws MethodException, ConnectionException, IOException 
	{
		// JMW 4/6/2011 P104 - for some reason P83 did not enable this even though the RPCs are
		// available for VistARad.  Enabling this now as Part of Patch 104, still only requires Patch 90
		String imageIen = image.getImageId();
		getLogger().info("getImageHISUpdates(" + imageIen + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		HashMap<String, String> hisUpdate = null;
		VistaQuery hisUpdateQuery = VistaImagingQueryFactory.createGetHisUpdateQuery(imageIen);
		
		String rtn = null;
		try
		{
			rtn = vistaSession.call(hisUpdateQuery);		
			getLogger().debug("Result from HisUpdate RPC [" + rtn + "]");
			hisUpdate = VistaImagingTranslator.convertVistaHisUpdateToHashmap(rtn);			
			String value = hisUpdate.get(DICOM_SOP_INSTANCE_UID_FIELD);
			if(value != null)
			{
				getLogger().debug("His Update contains DICOM SOP INSTANCE UID field");
				// check for error in tag value
				String eMessage = StringUtils.MagPiece(value, StringUtils.COMMA, 1);
				if("-1".equals(eMessage)) {					
					value = getNewSopInstanceUID(vistaSession, imageIen);
					getLogger().info("DICOM SOP INSTANCE UID field is missing, putting in new value[ " + value + "]");
					hisUpdate.put(DICOM_SOP_INSTANCE_UID_FIELD, value);
				}
			}
		}
		catch(VistaMethodException vmX)
		{
			getLogger().error("Error retrieving HIS update for image [" + imageIen + "]", vmX);
			// this error occurs if there was something wrong calling the RPC - this should not prevent
			// the image from being displayed.
			// not really sure if should return empty map or null, trying null for now which will make
			// image conversion not work but should not stop image from being retrieved and converted to non-DICOM
			//throw new MethodException(vmX);
			return null;
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			getLogger().error("Error retrieving HIS update for image [" + imageIen + "]", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
		return hisUpdate;
	}
	
	private String getNewSopInstanceUID(VistaSession vistaSession, String decodedImageIen) 
	throws MethodException, ConnectionException, IOException
	{
		getLogger().info("getNewSopInstanceUID(" + decodedImageIen + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery newSopInstanceUidQuery = VistaImagingQueryFactory.createNewSOPInstanceUidQuery(getSite().getSiteNumber(), 
				decodedImageIen);
		String rtn = null;
		try
		{
			rtn = vistaSession.call(newSopInstanceUidQuery);
			return rtn.trim();
		}
		catch(VistaMethodException vmX)
		{
			getLogger().error("Error create new SOP Instance UID for image [" + decodedImageIen + "]", vmX);
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			getLogger().error("Error create new SOP Instance UID for image [" + decodedImageIen + "]", ivcX);
			throw new InvalidCredentialsException(ivcX);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.AbstractBaseVistaRadImageDataSourceService#getRequiredVistaImagingVersion()
	 */
	@Override
	protected String getRequiredVistaImagingVersion() 
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
			VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
			MAG_REQUIRED_VERSION);
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "1";
	}
}
