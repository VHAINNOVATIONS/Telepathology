/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 28, 2009
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
package gov.va.med.imaging.vistaimagingdatasource.common;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.protocol.vista.VistaImagingTranslator;
import gov.va.med.imaging.core.interfaces.exceptions.PatientNotFoundException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistaimagingdatasource.VistaGroup;
import gov.va.med.imaging.vistaimagingdatasource.VistaImage;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingQueryFactory;
import gov.va.med.imaging.vistaimagingdatasource.configuration.VistaImagingConfiguration;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * Implementations of some common functions that are used in multiple data sources.  Put common functions here to 
 * reduce copying of code.
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingCommonUtilities 
{
	private static Logger logger = Logger.getLogger(VistaImagingCommonUtilities.class);
	
	public final static String DEFAULT_SERIES_NUMBER = "1";
	
	private final static ReplacementCharacters [] reportReplacementCharacters = 
	new ReplacementCharacters [] 
	{ 
		// replace FF (Form Feed) character with new line
		new VistaImagingCommonUtilities.ReplacementCharacters((char)12, (char)32)
	};
	
	public final static String DICOM_PATIENT_ID_KEY = "0010,0020";
	public final static String DICOM_PATIENT_NAME_KEY = "0010,0010";
	public final static String DICOM_ALT_PATIENT_ID_KEY = "0010,1000";
	
	/**
	 * Checks to see if VistA has the requred version of Imaging installed. Requires an open VistaSession and for
	 * the calling function to manage (close) the connection
	 * @param requiredVistaImagingVersion The version of Imaging that is necessary
	 * @param vistaSession An open Vista Session
	 * @return
	 */
	public static boolean isVersionCompatible(String requiredVistaImagingVersion, VistaSession vistaSession) 
	{		
		try
		{		
			VistaQuery magVersionsQuery = VistaImagingQueryFactory.createGetMagInstalledVersionsQuery();
			String magVersions = vistaSession.call(magVersionsQuery);
			
			List<String> magVersionList = VistaImagingTranslator.convertVistaVersionsToVersionNumbers(magVersions);
			for(int i = 0; i < magVersionList.size(); i++)
			{
				if(requiredVistaImagingVersion.equalsIgnoreCase(magVersionList.get(i)))
				{
					logger.info("Vista database has required imaging version [" + requiredVistaImagingVersion + "] installed, ok to continue");
					return true;
				}
			}
		}		
		catch(VistaMethodException vmX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", vmX);	
		}
		catch(InvalidVistaCredentialsException icX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", icX);
		}
		catch(IOException ioX)
		{
			logger.error("There was an error finding the installed Imaging version from VistA", ioX);
		}
		logger.info("Vista database does NOT have required imaging version [" + requiredVistaImagingVersion + "], connection will not continue");
		return false;
	}	
	
	/**
	 * Converts the patient ICN to a DFN. This function requires an open Vista connection and requires the calling
	 * function to manage (Close) the connection. The site to which the query is made is based on the vista session
	 * @param vistaSession
	 * @param patientICN
	 * @return
	 * @throws MethodException
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws PatientNotFoundException
	 */
	public static String getPatientDFN(VistaSession vistaSession, String patientICN) 
	throws MethodException, IOException, ConnectionException, PatientNotFoundException
	{
		logger.info("getPatientDFN(" + patientICN + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = VistaImagingQueryFactory.createGetPatientDFNVistaQuery(patientICN);
		
        String rtn = "";
    	try
        {
	        rtn = vistaSession.call(vm);
	    	if( rtn.startsWith("-1") ) 
	    	{
	    		logger.warn("ICN [" + patientICN + "] not found in database, response [" + rtn + "]");
	    		throw new PatientNotFoundException("Patient ICN [" + patientICN + "] not found in the database");
	    	}
        } 
    	catch (VistaMethodException e)
        {
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		throw new InvalidCredentialsException(e.getMessage());
        }
    	return rtn;
	}
	
	private static String workstationId = null;
	/**
	 * Get the local host name if available, else get a default name
	 * 
	 * @return
	 */
	public static synchronized String getWorkstationId() 
	{
		if(workstationId == null) {
			try {
				InetAddress addr = InetAddress.getLocalHost();
				workstationId = addr.getHostName();
			}
			catch(UnknownHostException uhX) {
				logger.warn("Unable to get local hostname, using default value");
				workstationId = "ViX-DataSource";
			}
			logger.info("Local host name set to [" + workstationId + "]");
			return workstationId;
		}
		return workstationId;
	}
	
	/**
	 * Maybe re-think this a bit, want to merge data but not do unnecessary steps, 
	 * remove items from one list if already used that items data
	 * 
	 * @param vistaSession
	 * @param groups
	 * @param studyGraph
	 * @param studyLoadLevel Needed to determine if the report should be loaded
	 * @return
	 */
	public static SortedSet<Study> mergeStudyLists(
		VistaSession vistaSession, 
		SortedSet<Study> studyGraph, 
		SortedSet<VistaGroup> groups, 
		StudyLoadLevel studyLoadLevel) 
	{
		logger.info("Merging study lists, " + groups.size() + " groups and " + studyGraph.size() + " studies.");
		SortedSet<Study> mergedStudies = new TreeSet<Study>();
		
		// iterate over the study graph and add details that were originally obtained
		// from the groups.
		for(Study study : studyGraph)
		{
			VistaGroup group = getMatchingGroup(groups, study.getStudyIen(), study.getFirstImageIen());
			if(group == null) 
				logger.error("Unable to find matching study details for study '" + study.getStudyIen() + "'.");
			else 
			{
				mergedStudies.add( mergeStudyWithGroup(vistaSession, study, group, studyLoadLevel) );
				logger.info("Merged study lists, matching study IEN " + study.getStudyIen() + 
					", firstImageIEN-" + study.getFirstImageIen() + ". " + 
					" Group " + group.getIen() + " " + (groups.remove(group) ? "was" : "was not") + " removed from further matching."); 
				// remove the group, so we can keep track of whether they were all matched or not
			}
		}
		
		// if not all groups were matched then log an information message
		if(groups.size() > 0)
		{
			logger.info(groups.size() + " remaining group(s) not directly matched to a study");
			for(VistaGroup group : groups)
				// CTB 29Nov2009
				//logger.info("\tStudy [" + group.getIen() + "]=[" + Base32ConversionUtility.base32Decode(group.getIen()) + "]");
				logger.info("\tStudy [" + group.getIen() + "]=[" + group.getIen() + "]");
			
		}
		
		return mergedStudies;
	}
	
	public static void setStudyRadiologyReport(VistaSession vistaSession, Study study)
	{
		try 
		{
			logger.info("Getting radiology report for study '" + study.getStudyIen() + "'");
			study.setRadiologyReport(getReport(vistaSession, study.getStudyIen()));
		}
		catch(VistaMethodException rpcX) 
		{
			logger.warn("Exception retrieving radiology report, " + rpcX.toString());
			study.setRadiologyReport("");
		} 
		catch (ConnectionException cX)
        {
			logger.warn("Exception retrieving radiology report, " + cX.toString());
			study.setRadiologyReport("");
        } 
		catch (MethodException mX)
        {
			logger.warn("Exception retrieving radiology report, " + mX.toString());
			study.setRadiologyReport("");
        }
	}
	
	/**
	 * 
	 * @param ien Base32 encoded IEN of the image to get the report for
	 * @return
	 * @throws MethodException 
	 * @throws ConnectionException 
	 * @throws RpcException
	 */
	public static String getReport(VistaSession vistaSession, String ien) 
	throws VistaMethodException, ConnectionException, MethodException 
	{
		logger.info("getReport(" + ien + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		// CTB 29Nov2009
		//ien = Base32ConversionUtility.base32Decode(ien);
		ien += "^";
		VistaQuery vm = VistaImagingQueryFactory.createGetReportVistaQuery(ien);
		String rtn = null;
		try
		{
			rtn = vistaSession.call(vm);
			return VistaImagingCommonUtilities.extractInvalidCharactersFromReport(rtn);		
		}
		catch (Exception ex)
		{
			logger.error(ex);
			throw new VistaMethodException(ex);
		}	
	}	
	
	/**
	 * Add the VistaImage instances from a sorted set into an existing Study instance
	 * as a single Series.
	 * 
	 * @param study
	 * @param images
	 * @throws URNFormatException 
	 */
	public static void addVistaImagesToStudyAsSeries(Study study, SortedSet<VistaImage> vistaImages) 
	throws URNFormatException
	{
		Series series = Series.create(study.getObjectOrigin(), study.getStudyIen(), DEFAULT_SERIES_NUMBER);
		
		SortedSet<Image> images = 
			VistaImagingTranslator.transform(study.getSiteNumber(), study.getStudyIen(), study.getPatientIdentifier(), 
					vistaImages);
		series.addImages(images);
		study.addSeries(series);
	}	
	
	/**
	 * Add the Image instances from a sorted set into an existing Study instance
	 * as a single Series.
	 * 
	 * @param study
	 * @param images
	 * @throws URNFormatException 
	 */
	public static void addImagesToStudyAsSeries(Study study, SortedSet<Image> images) 
	{
		Series series = Series.create(study.getObjectOrigin(), study.getStudyIen(), DEFAULT_SERIES_NUMBER);
		
		series.addImages(images);
		study.addSeries(series);
	}	
	
	/**
	 * 
	 * @param groups
	 * @param graphStudyIen
	 * @param graphFirstImageIen
	 * @return
	 */
	private static VistaGroup getMatchingGroup(SortedSet<VistaGroup> groups, String graphStudyIen, String graphFirstImageIen) 
	{
		logger.debug("getMatchingGroup(" + groups.size() + " groups," + graphStudyIen + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");

		for(VistaGroup group : groups) 
		{
			String groupIen = group.getIen();
			logger.debug("getMatchingGroup, evaluating group IEN '" + groupIen + 
					"' to study IEN '" + graphStudyIen + 
					"' and graph study first image IEN '" + graphFirstImageIen + "'.");
			if( groupIen.equalsIgnoreCase(graphStudyIen) || groupIen.equalsIgnoreCase(graphFirstImageIen) )
				return group;
		}
		logger.warn("Unable to find group for study graph [" + graphStudyIen + "]");
		return null;
	}	
	
	/**
	 * This method is public only for unit testing (grrr)
	 * @param report
	 * @return
	 */
	public static String extractInvalidCharactersFromReport(String report)
	{
		if(report == null)
			return null;		
		for(ReplacementCharacters replacementChar : reportReplacementCharacters)
		{
			report = report.replace(replacementChar.oldChar, replacementChar.newChar);
		}
		return report;
	}
	
	/**
	*
	*/
	static class ReplacementCharacters
	{
		char oldChar;
		char newChar;
		
		ReplacementCharacters(char oldChar, char newChar)
		{
			this.oldChar = oldChar;
			this.newChar = newChar;
		}
	}
	/**
	 * 
	 * @param vistaSession
	 * @throws MethodException
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public static void createSession(VistaSession vistaSession) 
	throws MethodException, IOException, ConnectionException
	{
		logger.info("createSession() TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = VistaImagingQueryFactory.createSessionQuery(getWorkstationId());
		String rtn = null;
		try
		{
			rtn = vistaSession.call(vm);
			if(rtn.startsWith("0"))
				throw new MethodException(rtn);
			
			logger.info("CreateSession result [" + rtn + "]");
		}
		catch (VistaMethodException e)
        {
			logger.error(e);
    		throw new MethodException(e.getMessage());
        } 
    	catch (InvalidVistaCredentialsException e)
        {
    		logger.error(e);
    		throw new InvalidCredentialsException(e.getMessage());
        }
	}	
	
	/**
	 * 
	 * @param vistaSession
	 * @param study
	 * @param groups
	 * @param studyLoadLevel
	 * @return
	 */
	public static Study mergeStudyWithMatchingGroup(
		VistaSession vistaSession, 
		Study study, 
		SortedSet<VistaGroup> groups, 
		StudyLoadLevel studyLoadLevel)
	{
		logger.info("Merging study '" + study.getStudyIen() + "' with '" + groups.size() + "' possible group(s)");
		VistaGroup group = getMatchingGroup(groups, study.getStudyIen(), study.getFirstImageIen());
		return group == null ? null : mergeStudyWithGroup(vistaSession, study, group, studyLoadLevel);
	}
	
	/**
	 * Merge study details into another Study instance.
	 * The primary keys (site number and study IEN are not changed in the receiving instance).
	 * 
	 * @param vistaSession
	 * @param study
	 * @param groups
	 * @param studyLoadLevel
	 * @return
	 */
	public static Study mergeStudyWithGroup(
		VistaSession vistaSession, 
		Study study, 
		VistaGroup group, 
		StudyLoadLevel studyLoadLevel)
	{
		if( group != null )
		{
			study.setCaptureBy(group.getCaptureBy());
			study.setCaptureDate(group.getCaptureDate());
			study.setDescription(group.getDescription());
			study.setEvent(group.getEvent());
			study.setImagePackage(group.getImagePackage());
			study.setImageType(group.getImageType());
			study.setNoteTitle(group.getNoteTitle());
			study.setStudyClass(group.getStudyClass());
			study.setOrigin(group.getOrigin());
			study.setProcedure(group.getProcedure());
			study.setProcedureDate(group.getProcedureDate());
			study.setSpecialty(group.getSpecialty());
			// JMW 9/17/2010 if there is an actual value here we don't want to override it
			// the value from the group might only contain the group count which is not correct if
			// the study contains information from multiple groups
			if(study.getImageCount() <= 0)
			{
				study.setImageCount(group.getImageCount());
			}
			study.setProcedureDateString(group.getProcedureDateString());		
			study.setAlienSiteNumber(group.getAlienSiteNumber());
			study.setRpcResponseMsg(group.getRpcResponseMsg());
			// JMW 4/26/2011 P104T4 - did not have this in here, copied below code (more or less) from deprecated merge method in Study object
			study.setSiteAbbr(group.getSiteAbbr()); // JMW - site abbr should come from RPC response, so overwrite value from site object here
			
			// new fields from patch 93
			study.setDocumentDate(group.getDocumentDate());
			study.setSensitive(group.isSensitive());
			study.setStudyViewStatus(group.getStudyViewStatus());
			study.setStudyStatus(group.getStudyStatus());
			study.setStudyImagesHaveAnnotations(group.isStudyImagesHaveAnnotations());
			
			if(! study.getStudyLoadLevel().isIncludeImages())
			{
				// if the study was not fully loaded, then its getting the first image from the group RPC call
				// in the case of a single image study, then the IEN from the group RPC for the study is 
				// actually the IEN of the image, not the group, so need to set the first image study IEN 
				// to be the actual study IEN, not the image IEN. This is only important for shallow 
				// requests that are single image groups
				try
				{
					VistaImage vistaImage = group.getFirstVistaImage();		
					// JMW 7/21/2010 P104
					// the IEN of the image from group.getFirstVistaImage() is the IEN of the study/group because this 
					// came from the MAG4 PAT GET IMAGES rpc call
					// this is not the actual IEN of the first image, but the MAG DOD GET STUDIES IEN rpc includes the IEN
					// of the first image, so set the vistaImage IEN to that value, then assign the first image to
					// the study					
					vistaImage.setIen(study.getFirstImageIen()); // this value comes from VistA so it is correct, not the group IEN but always the image IEN												
					Image firstImage = VistaImagingTranslator.transform(study.getSiteNumber(), 
							study.getStudyIen(), study.getPatientIdentifier(), group.getFirstVistaImage());
					// JMW 4/21/2011 P104 T4
					// if the study has a consolidated site number, then it came from the first image in the group (this image)
					// so be sure to assign that value to the firstImage so it can potentially update the image later
					firstImage.setConsolidatedSiteNumber(study.getConsolidatedSiteNumber());
					
					//firstImage.setIen(studyGraph.getFirstImageIen());
					//firstImage.setGroupIen(study.getStudyIen());
					study.setFirstImage(firstImage);				
					study.setFirstImageIen(firstImage.getIen());
					
					//studyDetails.getFirstImage().setStudyIen(studyGraph.getStudyIen());
					//studyDetails.getFirstImage().setIen(studyGraph.getFirstImageIen());
					
					//studyGraph.setFirstImage(studyDetails.getFirstImage());				
					//studyGraph.setFirstImageIen(studyDetails.getFirstImage().getIen());
				}
				catch (URNFormatException x)
				{
					logger.error("Error transforming VistaImage instance into Image instance", x);
				}
			}
		}
		
		if(studyLoadLevel.isIncludeReport())
			setStudyRadiologyReport(vistaSession, study);
		
		return study;
	}
	
	/**
	 * Merge study details into another Study instance.
	 * The primary keys (site number and study IEN are not changed in the receiving instance).
	 * 
	 * @param vistaSession
	 * @param baseStudy
	 * @param groups
	 * @param studyLoadLevel
	 * @return
	 */
	public static Study mergeStudyWithStudy(
		VistaSession vistaSession, 
		Study baseStudy, 
		Study sourceStudy, 
		StudyLoadLevel studyLoadLevel)
	{
		if( sourceStudy != null )
		{
			baseStudy.setCaptureBy(sourceStudy.getCaptureBy());
			baseStudy.setCaptureDate(sourceStudy.getCaptureDate());
			baseStudy.setDescription(sourceStudy.getDescription());
			baseStudy.setEvent(sourceStudy.getEvent());
			baseStudy.setImagePackage(sourceStudy.getImagePackage());
			baseStudy.setImageType(sourceStudy.getImageType());
			baseStudy.setNoteTitle(sourceStudy.getNoteTitle());
			baseStudy.setStudyClass(sourceStudy.getStudyClass());
			baseStudy.setOrigin(sourceStudy.getOrigin());
			baseStudy.setProcedure(sourceStudy.getProcedure());
			baseStudy.setProcedureDate(sourceStudy.getProcedureDate());
			baseStudy.setSpecialty(sourceStudy.getSpecialty());
			// JMW 9/17/2010 if there is an actual value here we don't want to override it
			// the value from the group might only contain the group count which is not correct if
			// the study contains information from multiple groups
			if(baseStudy.getImageCount() <= 0)
			{				
				baseStudy.setImageCount(sourceStudy.getImageCount());
			}
			baseStudy.setProcedureDateString(sourceStudy.getProcedureDateString());		
			baseStudy.setAlienSiteNumber(sourceStudy.getAlienSiteNumber());
			baseStudy.setRpcResponseMsg(sourceStudy.getRpcResponseMsg());
			baseStudy.setSensitive(sourceStudy.isSensitive());
			baseStudy.setDocumentDate(sourceStudy.getDocumentDate());
			baseStudy.setStudyViewStatus(sourceStudy.getStudyViewStatus());
			baseStudy.setStudyStatus(sourceStudy.getStudyStatus());
			baseStudy.setStudyImagesHaveAnnotations(sourceStudy.isStudyImagesHaveAnnotations());
			
			if(! baseStudy.getStudyLoadLevel().isIncludeImages())
			{
				// if the study was not fully loaded, then its getting the first image from the group RPC call
				// in the case of a single image study, then the IEN from the group RPC for the study is 
				// actually the IEN of the image, not the group, so need to set the first image study IEN 
				// to be the actual study IEN, not the image IEN. This is only important for shallow 
				// requests that are single image groups
				
				Image firstImage = baseStudy.getFirstImage();
				
				//System.out.println("Setting first image study Ien '" + Base32ConversionUtility.base32Decode(studyGraph.getStudyIen()) + "'");
				
				//firstImage.setIen(studyGraph.getFirstImageIen());
				//firstImage.setGroupIen(baseStudy.getStudyIen());
				baseStudy.setFirstImage(firstImage);				
				baseStudy.setFirstImageIen(firstImage.getIen());
				
				//studyDetails.getFirstImage().setStudyIen(studyGraph.getStudyIen());
				//studyDetails.getFirstImage().setIen(studyGraph.getFirstImageIen());
				
				//studyGraph.setFirstImage(studyDetails.getFirstImage());				
				//studyGraph.setFirstImageIen(studyDetails.getFirstImage().getIen());
			}
		}
		
		if(studyLoadLevel.isIncludeReport())
			setStudyRadiologyReport(vistaSession, baseStudy);
		
		return baseStudy;
	}
	
	/**
	 * Find the version required to run the data source from the configuration. If no version found then
	 * defaultVersion is returned
	 * 
	 * @param vistaConfiguration
	 * @param dataSourceClass
	 * @param defaultVersion
	 * @return
	 */
	public static String getVistaDataSourceImagingVersion(VistaImagingConfiguration vistaConfiguration,
			Class<?> dataSourceClass, String defaultVersion)
	{
		String version = vistaConfiguration.getDataSourceImagingVersion(dataSourceClass);
		if(version == null)
			return defaultVersion;
		return version;
	}
}
