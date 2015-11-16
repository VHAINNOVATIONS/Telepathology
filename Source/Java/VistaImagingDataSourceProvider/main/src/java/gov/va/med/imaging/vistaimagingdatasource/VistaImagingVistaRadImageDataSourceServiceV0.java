/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 15, 2009
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
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author vhaiswwerfej
 * @deprecated This version is no longer supported - Patch 101 is too old
 *
 */
@Deprecated
public class VistaImagingVistaRadImageDataSourceServiceV0 
extends AbstractBaseVistaRadImageDataSourceService 
{	
	public final static String MAG_REQUIRED_VERSION = "3.0.101|VIX";

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingVistaRadImageDataSourceServiceV0(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaRadImageDataSourceService#getRequiredVistaImagingVersion()
	 */
	@Override
	protected String getRequiredVistaImagingVersion() 
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaRadImageDataSourceService#getImageHISUpdates(gov.va.med.imaging.vistadatasource.session.VistaSession, gov.va.med.imaging.exchange.business.Image)
	 */
	@Override
	protected HashMap<String, String> getImageHISUpdates(VistaSession vistaSession, ExamImage image) 
	throws MethodException, ConnectionException, IOException 
	{
		HashMap<String, String> result = new HashMap<String, String>();
		// put in some known fields
		result.put(VistaImagingCommonUtilities.DICOM_PATIENT_NAME_KEY, image.getPatientName()); // patient name
		result.put(VistaImagingCommonUtilities.DICOM_PATIENT_ID_KEY, image.getPatientIcn()); // patient ICN
		return result;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "0";
	}
}