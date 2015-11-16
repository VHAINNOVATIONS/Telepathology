/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 22, 2010
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
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * This version of the interface gets image information and can include details about deleted images.
 * 
 * This version will require Patch 119 because that is where the VIX will support deleted images
 * 
 * <b>THIS IS TO BE ENABLED IN P119, NOT P104</b>
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImageDataSourceServiceV2
extends VistaImageDataSourceService
{
	/**
	 * This functionality is actually available in Patch 117 however the VIX does not officially support
	 * deleted images until Patch 119
	 */
	public final static String MAG_REQUIRED_VERSION = "3.0P119";
	
	public VistaImageDataSourceServiceV2(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
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

	@Override
	protected VistaQuery getImageInformationQuery(String identifier,
			boolean includeDeletedImages)
	{
		VistaQuery msg = VistaImagingQueryFactory.createGetImageInformationQuery(identifier, 
				includeDeletedImages);
		return msg;
	}	
	
	@Override
	protected String getDataSourceVersion()
	{
		return "2";
	}
}
