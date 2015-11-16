/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 2, 2010
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
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImagingStudyGraphDataSourceServiceV1
extends VistaImagingStudyGraphDataSourceService
{
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	// Patch 104 is necessary because of the changes to MAG DOD GET STUDIES IEN that handles deleted images
	public final static String MAG_REQUIRED_VERSION = "3.0P104";
	
	public VistaImagingStudyGraphDataSourceServiceV1(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	@Override
	protected String getRequiredVistaImagingVersion()
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), 
				this.getClass(), 
				MAG_REQUIRED_VERSION);
	}

	@Override
	protected VistaQuery getPatientGroupsVistaQuery(String patientDfn,
			StudyFilter studyFilter)
	{
		// JMW 9/30/10 P104
		// patch 104 will NOT support deleted images since the MAG DOD GET STUDIES IEN rpc will not be updated for p104
		return VistaImagingQueryFactory.createMagImageListQuery(patientDfn, studyFilter, false);
	}

	@Override
	protected boolean canRetrieveDeletedImages()
	{
		// if patch 104 is included at the site, then can retrieve deleted images
		// JMW 9/30/10 P104
		// patch 104 will NOT support deleted images since the MAG DOD GET STUDIES IEN rpc will not be updated for p104
		return false;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "2";
	}
}
