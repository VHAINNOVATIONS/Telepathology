/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 27, 2009
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

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.protocol.vista.exceptions.VistaParsingException;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * This version uses RPC calls before Patch 83 to create a study with a fake series containing the images
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingExternalPackageDataSourceServiceV0 
extends AbstractBaseVistaImagingExternalPackageDataSourceService 
{
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P45";

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingExternalPackageDataSourceServiceV0(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaExternalPackageDataSourceService#fullyPopulateGroupIntoStudy(gov.va.med.imaging.vistadatasource.session.VistaSession, gov.va.med.imaging.exchange.business.Study, java.lang.String)
	 */
	@Override
	protected Study fullyPopulateGroupIntoStudy(
		VistaSession vistaSession,
		Study study, 
		String patientDfn)
	throws InvalidVistaCredentialsException, VistaMethodException, IOException 
	{
		SortedSet<Image> images = null;
		
		// if the study is a group study
		if(study.getFirstImage() == null)
		{
			// CTB 29Nov2009
			//String groupStudyId = Base32ConversionUtility.base32Decode(study.getStudyIen());
			String groupStudyId = study.getStudyIen();
			// create Vista query - MAGG GROUP IMAGES 
			VistaQuery groupImagesQuery = VistaImagingQueryFactory.createGetStudyImagesVistaQuery(groupStudyId);
			
			// get the group of images from VistA
			String vistaResponse = vistaSession.call(groupImagesQuery);
			// convert them into Image business objects
			try
			{
				SortedSet<VistaImage> vistaImages = 
					VistaImagingTranslator.createImageGroupFromImageLines(vistaResponse, study);
				images = VistaImagingTranslator.transform(study.getSiteNumber(), study.getStudyIen(), study.getPatientIdentifier(), vistaImages);
				VistaImagingCommonUtilities.addVistaImagesToStudyAsSeries(study, vistaImages);
			}
			catch (URNFormatException urnfX)
			{
				throw new VistaMethodException(urnfX);
			}
			catch (VistaParsingException vpX)
			{
				throw new VistaMethodException(vpX);
			}
			
    		if(study.getFirstImage() == null && images.size() > 0)
    		{
    			Image firstImage = images.first();
    			study.setFirstImage(firstImage);
    			study.setFirstImageIen(firstImage.getIen());
    			study.setPatientName(firstImage.getPatientName());
    		}
		}
		else
		{
			images = new TreeSet<Image>();						
			images.add(study.getFirstImage());
			VistaImagingCommonUtilities.addImagesToStudyAsSeries(study, images);
		}
		
		// get the report for the study
		try
		{
			study.setRadiologyReport(VistaImagingCommonUtilities.getReport(vistaSession, study.getStudyIen()));
		}
		catch (MethodException mX)
        {
			getLogger().warn("Exception retrieving radiology report, " + mX.toString());
			study.setRadiologyReport("");
        }
		catch(VistaMethodException rpcX) 
		{
			getLogger().warn("Exception retrieving radiology report, " + rpcX.toString());
			study.setRadiologyReport("");
		} 
		catch (ConnectionException cX)
        {
			getLogger().warn("Exception retrieving radiology report, " + cX.toString());
			study.setRadiologyReport("");
        }
		
		return study;
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "0";
	}
}
