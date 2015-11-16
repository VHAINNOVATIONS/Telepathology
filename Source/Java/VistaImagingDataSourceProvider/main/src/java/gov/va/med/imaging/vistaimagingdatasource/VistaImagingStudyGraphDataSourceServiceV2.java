/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 20, 2012
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

import java.util.Map;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.datasource.exceptions.UnsupportedProtocolException;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingStudyGraphDataSourceServiceV2 
extends VistaImagingStudyGraphDataSourceService
{
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	// Patch 119 is necessary because of the changes to MAG DOD GET STUDIES IEN that handles deleted images
	public final static String MAG_REQUIRED_VERSION = "3.0P119";
	
	public VistaImagingStudyGraphDataSourceServiceV2(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}
	
	public static VistaImagingStudyGraphDataSourceServiceV2 create(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws ConnectionException, UnsupportedProtocolException
	{
		return new VistaImagingStudyGraphDataSourceServiceV2(resolvedArtifactSource, protocol);
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
		// JMW 4/20/2012 P119
		// include the option for deleted images
		return VistaImagingQueryFactory.createMagImageListQuery(patientDfn, studyFilter, true);
	}
	
	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.VistaImagingStudyGraphDataSourceService#getPatientStudyGraphVistaQuery(java.util.Map, java.lang.String, gov.va.med.imaging.exchange.enums.StudyLoadLevel, gov.va.med.imaging.exchange.enums.StudyDeletedImageState)
	 */
	@Override
	protected VistaQuery getPatientStudyGraphVistaQuery(
			Map<String, String> studyMap, String patientDfn,
			StudyLoadLevel studyLoadLevel,
			StudyDeletedImageState studyDeletedImageState) 
	{
		// override to include the study deleted image state to use to determine if deleted images should be included in the result
		// this version does uses the studyDeletedImageState
    	return VistaImagingQueryFactory.createGetStudiesByIenVistaQuery(studyMap, 
    			patientDfn, studyLoadLevel, studyDeletedImageState);
    
	}

	@Override
	protected boolean canRetrieveDeletedImages()
	{
		// JMW 4/20/2012 Patch 119 will support deleted images
		return true;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "3";
	}
}
