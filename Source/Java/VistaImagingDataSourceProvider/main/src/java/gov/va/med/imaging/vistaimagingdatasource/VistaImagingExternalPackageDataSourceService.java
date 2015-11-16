/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 26, 2009
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
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * This version uses patch 83 RPC calls to built a proper study/series/instance graph
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingExternalPackageDataSourceService 
extends AbstractBaseVistaImagingExternalPackageDataSourceService 
{	
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P83";
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingExternalPackageDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaExternalPackageDataSourceService#getRequiredVistaImagingVersion()
	 */
	@Override
	protected String getRequiredVistaImagingVersion() 
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaExternalPackageDataSourceService#fullyPopulateGroupIntoStudy(gov.va.med.imaging.exchange.business.Study)
	 */
	@Override
	protected Study fullyPopulateGroupIntoStudy(
		VistaSession vistaSession, 
		Study study, 
		String patientDfn)
	throws InvalidVistaCredentialsException, VistaMethodException, IOException
	{
		// CTB 29Nov2009
		//String studyIen = Base32ConversionUtility.base32Decode(study.getStudyIen());
		String studyIen = study.getStudyIen();
		Map<String, String> studyMap = new HashMap<String, String>();
		studyMap.put("" + studyMap.size(), studyIen);
		
		VistaQuery query = 
			VistaImagingQueryFactory.createGetStudiesByIenVistaQuery(studyMap, patientDfn, StudyLoadLevel.FULL);
		
		getLogger().info("Making RPC call to get study graph for single group [" + studyIen + "]");
		String vistaResponse = vistaSession.call(query);
		getLogger().info("RPC call complete, received '" + vistaResponse.length() + "' bytes");
		StudyDeletedImageState studyDeletedImageState = 
			(canRetrieveDeletedImages() ? StudyDeletedImageState.doesNotIncludeDeletedImages : StudyDeletedImageState.cannotIncludeDeletedImages);
		SortedSet<Study> studies = 
			VistaImagingTranslator.createStudiesFromGraph(getSite(), vistaResponse, StudyLoadLevel.FULL, 
					studyDeletedImageState);
		
		return VistaImagingCommonUtilities.mergeStudyWithStudy(vistaSession, studies.first(), study, StudyLoadLevel.FULL);
	}
	
	/**
	 * Determines if this data source can handle retrieving deleted images if the user requests them
	 * @return
	 */
	protected boolean canRetrieveDeletedImages()
	{
		return false;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "1";
	}
}
