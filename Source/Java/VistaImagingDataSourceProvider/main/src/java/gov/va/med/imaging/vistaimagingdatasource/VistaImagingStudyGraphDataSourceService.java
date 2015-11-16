/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 7, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  
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
import gov.va.med.URNFactory;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * An implementation of a StudyGraphDataSourceSpi that talks to VistA.
 * 
 * NOTE:
 * 1.) public methods that do Vista access (particularly anything defined
 * in the StudyGraphDataSourceSpi interface) must acquire a VistaSession instance using
 * getVistaSession().
 * 2.) private methods which are only called from public methods that do Vista access
 * must include a VistaSession parameter, they should not acquire their own VistaSession
 * 3.) Where a method is both public and called from within this class, there should be a
 * public version following rule 1, calling a private version following rule 2.
 * 
 * @author VHAISWBECKEC
 *
 */
public class VistaImagingStudyGraphDataSourceService 
extends AbstractBaseVistaImagingStudyGraphService
{	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
	/* =====================================================================
	 * Instance fields and methods
	 * ===================================================================== */
	private Logger logger = Logger.getLogger(this.getClass());
	//private int maximumPatientSensitivityLevel = DEFAULT_PATIENT_SENSITIVITY_LEVEL;
	
	// The required version of VistA Imaging needed to execute the RPC calls for this operation
	public final static String MAG_REQUIRED_VERSION = "3.0P83";
	
    /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingStudyGraphDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
			VistaImagingDataSourceProvider.getVistaConfiguration(), 
			this.getClass(), 
			MAG_REQUIRED_VERSION);
	}

	/**
	 * Return the maximum permissible patient sensitivity level
	 * that will be included in results.  Sensitivity levels
	 * range from:
	 * 0 - least sensitive
	 *     - to -
	 * 3 - most sensitive
	 * 
	 * @return
	 */
	public int getMaximumPatientSensitivityLevel(StudyFilter filter)
	{
		return filter.getMaximumAllowedLevel().getCode();
	}
    
    /**
     * Getting Studies from Vista is a two step-process because:
     * a.) the Vista data structure is not organized around a study-series-instance hierarchy but
     * around a study-group-instance, where group and study are not synonymous.  A Study has 
     * 1..n Groups and a Group can have 1..n Series.
     * b.) the filtering provided by Vista does not implement all of the filtering that our
     * Filter object requires
     * 
     * @param patientIcn
     * @param filter
     * @return
     * @throws  
     * @see gov.va.med.imaging.datasource.StudyGraphDataSourceSpi#getStudies(String, Filter)
     */
    @Override
    public StudySetResult getPatientStudies(RoutingToken globalRoutingToken, PatientIdentifier patientIdentifier, 
    		StudyFilter filter, StudyLoadLevel studyLoadLevel)
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
			
			// if we are getting a single study
			if((filter != null) && (filter.isStudyIenSpecified()))
			{
				// we are getting a single study				
				logger.info("Getting study '" + filter.getStudyId() + "' for patient '" + patientIdentifier + "'");
				Study singleStudy = getSingleStudyMatchingFilter(localVistaSession, patientIdentifier, 
						patientDfn, filter, studyLoadLevel);
				SortedSet<Study> studies = new TreeSet<Study>();
				studies.add(singleStudy);
				return StudySetResult.createFullResult(studies);
			}
			// we are getting multiple studies
			else
			{
				// we are getting all the studies for a patient (that match the filter)				
				logger.info("Getting all studies that match filter for patient '" + patientIdentifier + "'");
				
		    	// check the patient sensitivity first, if it fails an exception will be thrown
		    	checkPatientSensitive(localVistaSession, patientDfn, patientIdentifier, filter);
		    	
				return StudySetResult.createFullResult(getAllStudiesMatchingFilter(localVistaSession, patientIdentifier, patientDfn, filter, studyLoadLevel));
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
     * This method is implemented here for completeness.
     * As of this date it is not used in production deployment. 
     */
    @Override
	public Study getStudy(PatientIdentifier patientIdentifier, GlobalArtifactIdentifier studyId) 
    throws MethodException, ConnectionException
	{
    	VistaCommonUtilities.setDataSourceMethodAndVersion("getStudy", getDataSourceVersion());
    	if( !(studyId instanceof StudyURN) )
    		throw new ConnectionException(this.getClass().getSimpleName() + " does not recognize GAI of type " + studyId.getClass().getName() + ".");
    	
    	StudyFilter filter = new StudyFilter(studyId); 
		StudyLoadLevel studyLoadLevel = StudyLoadLevel.FULL;
		StudySetResult result = getPatientStudies(studyId, patientIdentifier, filter, studyLoadLevel);
		if(result == null)
		{
			logger.error("Got null StudySetResult - this should NEVER happen!");
			return null;
		}
		SortedSet<Study> studies = result.getArtifacts();
		if(studies != null && studies.size() > 0)
			return studies.first();
		
		return null;
	}

    /**
     * This method is implemented here for completeness.
     * As of this date it is not used in production deployment. 
     */
	@Override
	public String getStudyReport(PatientIdentifier patientIdentifier, GlobalArtifactIdentifier studyId) 
	throws MethodException, ConnectionException
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("getStudyReport", getDataSourceVersion());
		Study study = getStudy(patientIdentifier, studyId);
		return study.getRadiologyReport();
	}

	/**
     * 
     * @param localVistaSession
     * @param patientIcn
     * @param patientDfn
     * @param filter
     * @param studyLoadLevel
     * @return
     * @throws ConnectionException
     * @throws MethodException
     * @throws IOException
     */
    private SortedSet<Study> getAllStudiesMatchingFilter(
    	VistaSession localVistaSession, 
    	PatientIdentifier patientIdentifier, 
    	String patientDfn, 
    	StudyFilter filter, 
    	StudyLoadLevel studyLoadLevel)
	throws ConnectionException, MethodException, IOException
    {
		// Note: a Study has 1.. Groups and a Group can have 1.. Series!
		SortedSet<VistaGroup> groups = null;
		logger.info("Getting groups for patient '" + patientIdentifier + "'.");
		groups = getPatientGroups(localVistaSession, getSite(), patientDfn, patientIdentifier, filter);		// 23Nov CTB = groups study ien is null
		logger.info("Found '" + groups.size() + "' groups for patient '" + patientIdentifier + "'");
		// no groups is not an exception scenario but it does mean we don't need to bother getting more 
		// information about each study
		if(groups.size() == 0)
			return new TreeSet<Study>();		// return a zero-length set		
		
		// Warning: if this filter (and studyIen) is not null, reducedGroup might truncate multiple group studies!!!
		Map<String, String> studyMap = new HashMap<String, String>();
			
		// JMW 12/17/2008, call the filter to remove studies from the groups, this way
		// the datasource doesn't need to have knowledge of the filter details
		if(filter != null)
			filter.preFilter(groups);
		
		// if we filtered everything out return now, and don't bother making the call
		// to populate the entire Studies tree
		if(groups.size() == 0)
			return new TreeSet<Study>();		// return a zero-length set		
		
		logger.info("Loading study graph data for filtered groups");

		// Build a list of Study IEN
		// The use of a Map is an artifact of VistA, which needs an index of the list elements.
		// The Map ends up as something like {"0", "662576753"},{"1", "761576512"} ...
		StringBuilder groupMessage = new StringBuilder();
		groupMessage.append('{');
		for(VistaGroup group : groups)
		{
			// CTB 29Nov2009
			//studyMap.put("" + studyMap.size(), Base32ConversionUtility.base32Decode(group.getIen()));
			studyMap.put( Integer.toString(studyMap.size()), group.getIen() );
			if(groupMessage.length() > 1)
				groupMessage.append(',');
			groupMessage.append(group.getIen());
		}
		groupMessage.append('}');
		
		//
		boolean includesDeletedImages = (filter == null ? false : filter.isIncludeDeleted());
		boolean canIncludeDeletedImages = canRetrieveDeletedImages(); // if this data source cannot support getting deleted images, then it is not an option
		StudyDeletedImageState studyDeletedImageState = StudyDeletedImageState.cannotIncludeDeletedImages;
		if(canIncludeDeletedImages) // if the DS supports getting deleted images, set appropriately based on user request
			studyDeletedImageState = (includesDeletedImages ? StudyDeletedImageState.includesDeletedImages : StudyDeletedImageState.doesNotIncludeDeletedImages);
		SortedSet<Study> studies = getPatientStudyGraph(localVistaSession, studyMap, patientDfn, 
				studyLoadLevel, studyDeletedImageState);
		logger.info("getPatientStudyGraph for studies " + groupMessage.toString() + " returned " + studies.size() + " studies.");
		SortedSet<Study> result = 
			VistaImagingCommonUtilities.mergeStudyLists(localVistaSession, studies, groups, studyLoadLevel);
		logger.info("Merging studies and groups results in " + result.size() + " studies.");
		
		// JMW 12/17/2008, call the filter to remove studies from the groups, this way
		// the datasource doesn't need to have knowledge of the filter details
		if(filter != null)
		{
			filter.postFilter(result);
		}
		logger.info("Completed getPatientStudies(), returning '" + result.size() + "' studies.");
		return result;
    }

	/**
     * 
     * @param localVistaSession
     * @param patientIcn
     * @param patientDfn
     * @param filter
     * @param studyLoadLevel
     * @return
     * @throws ConnectionException
     * @throws MethodException
     * @throws IOException
     */
    private Study getSingleStudyMatchingFilter(VistaSession localVistaSession, 
    		PatientIdentifier patientIdentifier, String patientDfn, StudyFilter filter, StudyLoadLevel studyLoadLevel)
	throws ConnectionException, MethodException, IOException
    {
    	StudyURN studyUrn = null;
    	if(filter.getStudyId() instanceof StudyURN)
    	{
    		try
    		{
    			studyUrn = URNFactory.create(filter.getStudyId().toString());
    		}
    		catch(URNFormatException urnfX)
    		{
    			throw new MethodException("Error converting filter study Id to StudyURN", urnfX);
    		}
    	}
    	else
    	{
    		throw new MethodException("Cannot cast filter study Id '" + filter.getStudyId() + "' to StudyURN, must be study URN to get study type.");
    	}    	
    	
    	String studyIen = studyUrn.getStudyId();
    	Map<String, String> studyMap = new HashMap<String, String>();
		// CTB 29Nov2009
    	//studyMap.put("" + studyMap.size(), Base32ConversionUtility.base32Decode(studyIen));
    	studyMap.put("" + studyMap.size(), studyIen);
    	
    	boolean includesDeletedImages = (filter == null ? false : filter.isIncludeDeleted());
    	boolean canIncludeDeletedImages = canRetrieveDeletedImages(); // if this data source cannot support getting deleted images, then it is not an option
		StudyDeletedImageState studyDeletedImageState = StudyDeletedImageState.cannotIncludeDeletedImages;
		if(canIncludeDeletedImages) // if the DS supports getting deleted images, set appropriately based on user request
			studyDeletedImageState = (includesDeletedImages ? StudyDeletedImageState.includesDeletedImages : StudyDeletedImageState.doesNotIncludeDeletedImages);
    	
    	SortedSet<Study> studies = getPatientStudyGraph(localVistaSession, studyMap, patientDfn, 
    			studyLoadLevel, studyDeletedImageState);
    	if((studies == null) || (studies.size() <= 0))
    	{
    		throw new MethodException("Study [" + studyIen + "] not found");
    	}    	    	
    	if(studies.size() > 1)
    	{
    		throw new MethodException("Found '" + studies.size() + "' matching studies, this should never happen!");
    	}
    	Study study = studies.first();
		logger.info("Getting groups for patient '" + patientIdentifier + "'.");
		SortedSet<VistaGroup> groups = getPatientGroups(localVistaSession, getSite(), patientDfn, patientIdentifier, filter);
		if((groups == null) || (groups.size() <= 0))
    	{
    		throw new MethodException("Study [" + studyIen + "] not found");
    	}   
		logger.info("Found '" + groups.size() + "' groups for patient '" + patientIdentifier + "'");
		Study result = VistaImagingCommonUtilities.mergeStudyWithMatchingGroup(localVistaSession, study, groups, studyLoadLevel);
		if(result == null)
		{
			throw new MethodException("Cannot find study '" + studyIen + "', could not match group to study.");
		}
		return result;
    }
    
    /**
     * 
     * @param localVistaSession
     * @param studyMap
     * @param patientDfn
     * @param studyLoadLevel
     * @return
     * @throws MethodException
     * @throws ConnectionException
     */
    private SortedSet<Study> getPatientStudyGraph(
    	VistaSession localVistaSession, 
    	Map<String, String> studyMap, 
    	String patientDfn, 
    	StudyLoadLevel studyLoadLevel,
    	StudyDeletedImageState studyDeletedImageState)
	throws MethodException, ConnectionException
    {    	
    	try
    	{
	    	VistaQuery query = getPatientStudyGraphVistaQuery(studyMap, patientDfn, studyLoadLevel, 
	    			studyDeletedImageState);
			logger.info("Retrieving study graph for patient containing '" + studyMap.size() + "' groups");
			String vistaResponse = localVistaSession.call(query);
			logger.info("Completed study graph RPC call, parsing response...");				
			
			SortedSet<Study> studies = VistaImagingTranslator.createStudiesFromGraph(getSite(), 
					vistaResponse, studyLoadLevel, studyDeletedImageState);
			logger.info("Converted response into '" + ((studies == null) ? 0 : studies.size()) + "' studies");
			return studies;
    	}
		catch (Exception ex)
		{
			logger.error(ex);
			ex.printStackTrace();
			throw new MethodException(ex);
		}
    }
    
    protected VistaQuery getPatientGroupsVistaQuery(String patientDfn, StudyFilter studyFilter)
    {
    	return VistaImagingQueryFactory.createGetGroupsVistaQuery(patientDfn, studyFilter);
    }
    
    /**
     * Method to get the VistaQuery to call to get the study graph
     * @param studyMap
     * @param patientDfn
     * @param studyLoadLevel
     * @param studyDeletedImageState
     * @return
     */
    protected VistaQuery getPatientStudyGraphVistaQuery(Map<String, String> studyMap, 
        	String patientDfn, 
        	StudyLoadLevel studyLoadLevel,
        	StudyDeletedImageState studyDeletedImageState)
    {
    	// this version does not use the studyDeletedImageState
    	return VistaImagingQueryFactory.createGetStudiesByIenVistaQuery(studyMap, 
    			patientDfn, studyLoadLevel);
    }

	/**
	 * @throws IOException 
	 * @throws MethodException 
	 * @throws ConnectionException 
	 * @throws InvalidVistaCredentialsException 
	 * 
	 */
	private SortedSet<VistaGroup> getPatientGroups(
		VistaSession session, 
		Site site, 
		String patientDfn, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	throws MethodException, IOException, ConnectionException
	{		
		logger.info("getPatientGroups(" + patientIdentifier + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");

		// creates call to RPC MAG4_PAT_GET_IMAGES
		VistaQuery vm = getPatientGroupsVistaQuery(patientDfn, filter);
		logger.info("getPatientGroups with RPC '" + vm.getRpcName() + "'.");
		
		String rtn = null;
		try
		{
			logger.info("Making call to get groups for patient '" + patientIdentifier + "'");
			rtn = session.call(vm);
			// check to be sure first character is a 1 (means result is ok)
			
			// if no images for patient, response is [0^No images for filter: All Images]
			
			if(rtn.charAt(0) == '1') 
			{			
				boolean includesDeletedImages = (filter == null ? false : filter.isIncludeDeleted());
				boolean canIncludeDeletedImages = canRetrieveDeletedImages(); // if this data source cannot support getting deleted images, then it is not an option
				StudyDeletedImageState studyDeletedImageState = StudyDeletedImageState.cannotIncludeDeletedImages;
				if(canIncludeDeletedImages) // if the DS supports getting deleted images, set appropriately based on user request
					studyDeletedImageState = (includesDeletedImages ? StudyDeletedImageState.includesDeletedImages : StudyDeletedImageState.doesNotIncludeDeletedImages);
				return VistaImagingTranslator.createGroupsFromGroupLines(site, rtn, patientIdentifier, 
						studyDeletedImageState);
			}
			else if(rtn.startsWith("0^No images for filter")) 
			{
				logger.info("0 response from getPatientGroupsVistaQuery() rpc, no images found, [" + rtn + "]");
				return new TreeSet<VistaGroup>();
			}
			else if(rtn.startsWith("0^No Such Patient:")) 
			{
				logger.info("0 response from getPatientGroupsVistaQuery() rpc, [" + rtn + "]");
				throw new VistaMethodException("No patient [ "+ patientIdentifier + "] found in database");
			}
			else 
			{
				logger.info("0 response from getPatientGroupsVistaQuery() rpc, [" + rtn + "]");
				throw new VistaMethodException(rtn);
			}
		}
		catch (Exception ex)
		{
			logger.error(ex);
			throw new MethodException(ex);
		}
	}    	
	
	/**
	 * 
	 * @param procedure
	 * @param filter
	 * @return
	 */
	/*
	private boolean procedureUsed(String procedure, StudyFilter filter) 
	{
		if(procedure == null)
			return false;
		
		if( filter.getAllowableStudyTypes() == null || filter.getAllowableStudyTypes().size() == 0 )
			return true;
		
		for( int i = 0; i < filter.getAllowableStudyTypes().size(); i++ )
			if( procedure.equalsIgnoreCase(filter.getAllowableStudyTypes().get(i)) )
				return true;
		
		return false;
	}
	*/
    
    /**
     * 
     * @return
     * @throws IOException 
     * @throws VistaException 
     */
	public boolean disconnect() 
	throws IOException 
	{
		// don;t do anything, let the connection timeout
		return true;
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
