/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 22, 2008
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

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.StudyGraphDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingStudyGraphDataSourceServiceV0 
extends AbstractBaseVistaImagingStudyGraphService
implements StudyGraphDataSourceSpi
{
	/* =====================================================================
	 * Instance fields and methods
	 * ===================================================================== */
	private Logger logger = Logger.getLogger(this.getClass());
	
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P46";
	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
    
    /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingStudyGraphDataSourceServiceV0(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaStudyGraphService#getRequiredVistaImagingVersion()
	 */
	@Override
	protected String getRequiredVistaImagingVersion() 
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.StudyGraphDataSourceSpi#getPatientStudies(java.lang.String, gov.va.med.imaging.exchange.business.StudyFilter)
	 */
	@Override
	public StudySetResult getPatientStudies(RoutingToken globalRoutingToken, 
		PatientIdentifier patientIdentifier,
		StudyFilter filter, 
		StudyLoadLevel studyLoadLevel) 
	throws UnsupportedOperationException, MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getPatientStudies", getDataSourceVersion());
		logger.info("getPatientStudies(" + patientIdentifier + 
				", " + (filter == null ? "<null>" : filter.toString()) + 
				", Study load level " + studyLoadLevel.toString() + 
				") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaSession localVistaSession = null;

		try
		{
			localVistaSession = getVistaSession();
			
			String patientDfn = null;
			try
			{
				patientDfn = getPatientDfn(localVistaSession, patientIdentifier);
			}
			catch(PatientNotFoundException pnfX)
			{
				// JMW 3/12/08 The patient was not found in the database, return an empty set of studies instead of
	        	logger.warn("Patient [" + patientIdentifier + "] was not found in the VistA database, returning empty study set", pnfX);
	        	return StudySetResult.createFullResult(new TreeSet<Study>());
			}
			
			// If we are getting many studies (i.e. not a single study IEN specified)
			// then check the patient sensitivity level
			// If a single IEN is specified then it is assumed that the patient sensitivity
			// has already been checked.
			if((filter == null) || (!filter.isStudyIenSpecified()))
			{
				// we are getting the full study graph
				checkPatientSensitive(localVistaSession, patientDfn, patientIdentifier, filter);
			}			
			
			SortedSet<Study> groups = null;
			logger.info("Getting groups for patient '" + patientIdentifier + "'.");
	        try
	        {
		        groups = getPatientGroups(localVistaSession, getSite(), 
		        		patientIdentifier, patientDfn, filter, studyLoadLevel);
	        } 
	        catch (IOException e)
	        {
	        	logger.error(e.getMessage());
	        } 
	        logger.info("Found '" + groups.size() + "' groups for patient '" + patientIdentifier + "'");
			// no groups is not an exception scenario but it does mean we don't need to bother getting more 
			// information about each study
			if(groups.size() == 0)
				return StudySetResult.createFullResult(groups);
			
			if(filter != null && filter.isStudyIenSpecified()) 
			{
				GlobalArtifactIdentifier studyUrn = filter.getStudyId();
				
				for(Iterator<Study> groupIter = groups.iterator(); groupIter.hasNext(); )
				{
					Study group = groupIter.next();
					if(! filter.isAllowableStudyId(group.getGlobalArtifactIdentifier()))
					//if(! filter.isAllowableStudyId(studyUrn.toString()))
						groupIter.remove();
				}
				if(groups.size() == 0)
					throw new MethodException("Study [" + (studyUrn == null ? "null" : studyUrn.toString()) + "] not found");
			}
			else 
			{				
				// JMW 12/17/2008, call the filter to remove studies from the groups, this way
				// the datasource doesn't need to have knowledge of the filter details
				if(filter != null)
				{
					filter.preFilter(groups);
				}
			}
			// if we filtered everything out return now, and don't bother makingthe call
			// to populate the entire Studies tree
			if(groups.size() == 0)
				return StudySetResult.createFullResult(groups);
	
			if(!studyLoadLevel.isIncludeImages())
			{
				// don't want the images, just study level data
				if(filter != null)
				{
					filter.postFilter(groups);
				}
				
				if(studyLoadLevel.isIncludeReport())
				{
					logger.info("Loading reports for filtered groups");
					for(Study study : groups)
					{
						VistaImagingCommonUtilities.setStudyRadiologyReport(localVistaSession, study);
					}
					logger.info("Completed getPatientStudies(), returning '" + groups.size() + "' studies.");
					return StudySetResult.createFullResult(groups);
				}
				else
				{
					logger.info("Completed getPatientStudies(), no report or images. Returning '" + groups.size() + "' studies.");
					return StudySetResult.createFullResult(groups);
				}
			}			
			logger.info("Loading study graph data for filtered groups");
			
			// diverge from study graph way of doing things			
			try 
			{
				for(Study study : groups)
				{
					// if the study is a group study
					if(study.getFirstImage() == null)
					{
						// CTB 29Nov2009
						//String groupStudyId = Base32ConversionUtility.base32Decode(study.getStudyIen());
						String groupStudyId = study.getStudyIen();
						VistaQuery groupImagesQuery = VistaImagingQueryFactory.createGetStudyImagesVistaQuery(groupStudyId);
						
						// get the group of images from VistA
						String vistaResponse = localVistaSession.call(groupImagesQuery);
						// convert them into Image business objects
						SortedSet<VistaImage> vistaImages = VistaImagingTranslator.createImageGroupFromImageLines(vistaResponse, study);
						
						// create the series for the images and add them to the study
						VistaImagingCommonUtilities.addVistaImagesToStudyAsSeries(study, vistaImages);
						
						SortedSet<Image> images = VistaImagingTranslator.transform(study.getSiteNumber(), 
								study.getStudyIen(), patientIdentifier, vistaImages);
						
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
						SortedSet<Image>images = new TreeSet<Image>();						
						images.add(study.getFirstImage());
						
						// create the series for the images and add them to the study
						VistaImagingCommonUtilities.addImagesToStudyAsSeries(study, images);
					}
					
					// get the report for the study
					if(studyLoadLevel.isIncludeReport())
					{
						VistaImagingCommonUtilities.setStudyRadiologyReport(localVistaSession, study);
					}
				}
				logger.info("Completed getPatientStudies(), returning '" + groups.size() + "' studies.");
				return StudySetResult.createFullResult(groups);
			}
			catch(VistaMethodException vmX)
			{
				logger.error("Error getting group images for studies", vmX);
				throw new MethodException(vmX);
			}
			catch(InvalidVistaCredentialsException ivcX)
			{
				logger.error("Error getting group images for studies", ivcX);
				throw new InvalidCredentialsException(ivcX);
			}
			catch (URNFormatException urnfX)
			{
				logger.error("Error getting group images for studies, unable to transform VistaImage to Image", urnfX);
				throw new MethodException(urnfX);
			}
		} 
		catch (IOException e)
        {
			throw new ConnectionException(e);
        }
		finally
		{
			try{localVistaSession.close();}
			catch(Throwable t){}
		}
	}		
	
	/**
	 * @throws IOException 
	 * @throws MethodException 
	 * @throws ConnectionException 
	 * @throws InvalidVistaCredentialsException 
	 * 
	 */
	private SortedSet<Study> getPatientGroups(VistaSession session, Site site, PatientIdentifier patientIdentifier, String patientDfn, 
			StudyFilter filter, StudyLoadLevel studyLoadLevel)
	throws MethodException, IOException, ConnectionException
	{		
		logger.info("getPatientGroups(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");

		VistaQuery vm = VistaImagingQueryFactory.createGetGroupsVistaQuery(patientDfn, filter);
		
		String rtn = null;
		try
		{
			logger.info("Making call to get groups for patient '" + patientIdentifier + "'");
			rtn = session.call(vm);
			// check to be sure first character is a 1 (means result is ok)
			
			// if no images for patient, response is [0^No images for filter: All Images]
			
			if(rtn.charAt(0) == '1') 
			{			
				SortedSet<VistaGroup> groups = VistaImagingTranslator.createGroupsFromGroupLinesHandleSingleImageGroup(
					site, rtn, patientIdentifier, studyLoadLevel, StudyDeletedImageState.cannotIncludeDeletedImages);
				return VistaImagingTranslator.transform(ObjectOrigin.VA, site, groups);
			}
			else if(rtn.startsWith("0^No images for filter")) 
			{
				logger.info("0 response from MAG4 PAT GET IMAGES rpc, no images found, [" + rtn + "]");
				return new TreeSet<Study>();
			}
			else if(rtn.startsWith("0^No Such Patient:")) 
			{
				logger.info("0 response from MAG4 PAT GET IMAGES rpc, [" + rtn + "]");
				throw new VistaMethodException("No patient [ "+ patientIdentifier + "] found in database");
			}
			else 
			{
				logger.info("0 response from MAG4 PAT GET IMAGES rpc, [" + rtn + "]");
				throw new VistaMethodException(rtn);
			}
		}
		catch (Exception ex)
		{
			logger.error(ex);
			throw new MethodException(ex);
		}
	}
	
	@Override
	protected String getDataSourceVersion()
	{
		return "0";
	}
}
