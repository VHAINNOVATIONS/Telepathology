/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 4, 2011
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
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * This is an implementation that uses Patch 104 for version checking, it also provides the 
 * parameter to the RPC when getting the list of images in an exam to determine if images should
 * be moved from JB to HD
 * 
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingVistaRadDataSourceServiceV3
extends VistaImagingVistaRadDataSourceServiceV2
{

	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation
	public final static String MAG_REQUIRED_VERSION = "3.0.104|VIX";
	
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingVistaRadDataSourceServiceV3(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

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
		return "3";
	}

	@Override
	protected VistaQuery getExamImagesQuery(String examId,
			boolean useTgaImages, boolean forceImagesFromJb)
	{
		return VistaImagingVistaRadQueryFactory.createMagJGetExamImages(examId, 
				useTgaImages, forceImagesFromJb);
	}
}
