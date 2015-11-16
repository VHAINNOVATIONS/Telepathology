package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.HealthSummaryURN;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageAccessReasonType;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.protocol.vista.VistaTranslatorUtility;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class VistaImagingQueryFactory
{
	private static final String RPC_XUS_GET_DIVISIONS = "XUS DIVISION GET";
	public final static String MENU_SUBSCRIPT = "200.03";
	public final static String DELEGATE_SUBSCRIPT = "200.19";

	// rpc calls made to other packages
	private final static String RPC_CONVERT_ICN_TO_DFN = "VAFCTFU CONVERT ICN TO DFN";
	private final static String RPC_CONVERT_DFN_TO_ICN = "VAFCTFU CONVERT DFN TO ICN";
	private final static String RPC_GET_VARIABLE_VALUE = "XWB GET VARIABLE VALUE";
	
	// MAG rpc calls	
	private final static String RPC_MAG_GET_GROUPS = "MAG4 PAT GET IMAGES";
	private final static String RPC_MAG_GET_STUDY_IMAGES = "MAGG GROUP IMAGES";
	private final static String RPC_MAG_REPORT = "MAGGRPT";
	private final static String RPC_MAG_GET_NETLOC = "MAG GET NETLOC";
	private final static String RPC_MAG_MAGGUSER2 = "MAGGUSER2";
	private final static String RPC_MAG_DOD_GET_STUDIES_BY_IEN = "MAG DOD GET STUDIES IEN";
	private final static String RPC_MAG_IMAGE_CURRENT_INFO = "MAG IMAGE CURRENT INFO";
	private final static String RPC_MAG_NEW_SOP_INSTANCE_UID = "MAG NEW SOP INSTANCE UID";
	private final static String RPC_MAG_ACTION_LOG = "MAGGACTION LOG";
	private final static String RPC_MAG_WRKS_UPDATES = "MAGG WRKS UPDATES";
	private final static String RPC_MAG_OFFLINE_IMAGE_ACCESSED = "MAGG OFFLINE IMAGE ACCESSED";	
	private final static String RPC_MAG_INSTALL = "MAGG INSTALL";
	private final static String RPC_MAG_GET_IMAGE_INFO = "MAG4 GET IMAGE INFO";
	private final static String RPC_MAG_SYS_GLOBAL_NODE = "MAGG SYS GLOBAL NODE";
	private final static String RPC_MAG_DEV_FIELD_VALUES = "MAGG DEV FIELD VALUES";
	private final static String RPC_MAG_PAT_FIND = "MAGG PAT FIND";
	private final static String RPC_MAG_PAT_INFO = "MAGG PAT INFO";
	private final static String RPC_MAG_PAT_PHOTOS = "MAGG PAT PHOTOS";
	private final static String RPC_MAG_CPRS_RAD_EXAM = "MAGG CPRS RAD EXAM";
	private final static String RPC_MAG_CPRS_TIU_NOTE = "MAG3 CPRS TIU NOTE";
	private final static String RPC_MAG_BROKER_SECURITY = "MAG BROKER SECURITY";
	private final static String RPC_MAG_IMAGE_LIST = "MAG4 IMAGE LIST";
	private final static String RPC_MAG_USER_KEYS = "MAGGUSERKEYS";
	
	private final static String RPC_MAG_IMAGE_ALLOW_ANNOTATE = "MAG ANNOT IMAGE ALLOW";
	private final static String RPC_MAGJ_GET_TREATING_LIST = "MAGJ GET TREATING LIST";
	
	private final static String RPC_MAGG_REASON_LIST = "MAGG REASON LIST";
	private final static String RPC_MAGG_VERIFY_ESIG = "MAGG VERIFY ESIG";
	private final static String RPC_MAGGHSLIST = "MAGGHSLIST";
	private final static String RPC_MAGGHS = "MAGGHS";
	
	private final static String RPC_GET_TIMEOUT_PARAMETERS = "MAGG GET TIMEOUT";


	// we can leave QA Check off because this will allow "bad" images to go to the VA but we still
	// will get a QA error message which will prevent those images from going to the DOD
	private final static String MAG_QA_CHECK = "1"; // 1 indicates no QA check
	private final static int MAG_MAX_PATIENT_RESULT_COUNT = 100;
	
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // for DICOM UID generation only
	
	private static Logger getLogger()
	{
		return Logger.getLogger(VistaImagingQueryFactory.class);
	}		
	
	/**
	 * 
	 * @param patientICN
	 * @return
	 * @throws MethodException
	 */
	public static VistaQuery createGetPatientDFNVistaQuery(String patientICN) 
	{
		VistaQuery vm = new VistaQuery(RPC_CONVERT_ICN_TO_DFN);
		vm.addParameter(VistaQuery.LITERAL, patientICN);
		return vm;
	}	
	
	public static VistaQuery createGetPatientICNVistaQuery(String patientDFN) 
	{
		VistaQuery vm = new VistaQuery(RPC_CONVERT_DFN_TO_ICN);
		vm.addParameter(VistaQuery.LITERAL, patientDFN);
		return vm;
	}	
	
	/**
	 * Create a VistQuery instance that can be used to execute a
	 * RPC_MAG_GET_STUDIES RPC call on Vista.
	 * The return from the RPC call is a list of studies matching the
	 * class, dates, package, types, specialty and origin filter fields.
	 * The response is a list of caret-delimited Strings, each of which
	 * is a study description including the study IEN.
	 * ex:
	 * 1^Class: CLIN - 
	 * Item~S2^Site^Note Title~~W0^Proc DT~S1^Procedure^# Img~S2^Short Desc^Pkg^Class^Type^Specialty^Event^Origin^Cap Dt~S1~W0^Cap by~~W0^Image ID~S2~W0
	 * 1^WAS^NURSING NOTE^09/28/2001 00:01^NOTE^2^CONSULT NURSE MEDICAL WOUND SPEC INPT^NOTE^CLIN^CONSULT^NURSING^WOUND ASSESSMENT^VA^09/28/2001 01:35^IMAGPROVIDERONETWOSIX,ONETWOSIX^1752|1752^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.JPG^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.ABS^CONSULT NURSE MEDICAL WOUND SPEC INPT^3010928^11^NOTE^09/28/2001^36^M^A^^^2^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^
	 * 2^WAS^OPHTHALMOLOGY^08/20/2001 00:01^OPH^10^Ophthalmology^NOTE^CLIN^IMAGE^EYE CARE^^VA^08/20/2001 22:32^IMAGPROVIDERONETWOSIX,ONETWOSIX^1783|1783^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001784.DCM^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001784.ABS^Ophthalmology^3010820^11^OPH^08/20/2001^41^M^A^^^10^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^^
	 * 
	 * @param patientDfn
	 * @param filter
	 * @return
	 */
	public static VistaQuery createGetGroupsVistaQuery(String patientDfn, StudyFilter filter)
    {
	    String toDate = "";
		String fromDate = "";
		String studyPackage = "";
		String studyClass = "";
		String studyTypes = "";
		String studyEvent = "";
		String studySpecialtiy = "";
		String studyOrigin = "";
		if(filter != null) 
		{
			fromDate = VistaTranslatorUtility.convertDateToRpcFormat(filter.getFromDate());
			toDate = VistaTranslatorUtility.convertDateToRpcFormat(filter.getToDate());
			studyPackage = filter.getStudy_package();
			studyClass = filter.getStudy_class();
			studyTypes = filter.getStudy_type();
			studyEvent = filter.getStudy_event();
			studySpecialtiy = filter.getStudy_specialty();
			studyOrigin = filter.getOrigin();
		}
		
		VistaQuery vm = new VistaQuery(RPC_MAG_GET_GROUPS); // really returns groups
		//							Note: a Study has 1.. Groups and a Group can have 1.. Series!
		vm.addParameter(VistaQuery.LITERAL, patientDfn);
		vm.addParameter(VistaQuery.LITERAL, studyPackage); // PACKAGE
		vm.addParameter(VistaQuery.LITERAL, studyClass); // CLASS
		vm.addParameter(VistaQuery.LITERAL, studyTypes); // TYPES
		vm.addParameter(VistaQuery.LITERAL, studyEvent); // EVENT - tried WOUND ASSESSMENT
		vm.addParameter(VistaQuery.LITERAL, studySpecialtiy); // SPEC - tried RADIOLOGY
		vm.addParameter(VistaQuery.LITERAL, fromDate); // FROM DATE
		vm.addParameter(VistaQuery.LITERAL, toDate); // TO DATE
		vm.addParameter(VistaQuery.LITERAL, studyOrigin); // ORIGIN
	    return vm;
    }
	
	/**
	 * This version should be used at sites without P119 that do not have the new parameter (FLAGS) for deleted images
	 * 
	 * @param studyMap
	 * @param patientDfn
	 * @return
	 */
	public static VistaQuery createGetStudiesByIenVistaQuery(Map<?,?> studyMap, String patientDfn, StudyLoadLevel studyLoadLevel)
	{
		VistaQuery query = new VistaQuery(RPC_MAG_DOD_GET_STUDIES_BY_IEN); // IN: Groups; Out: Studies
		query.addParameter(VistaQuery.LIST, studyMap);
		query.addParameter(VistaQuery.LITERAL, patientDfn);
		if(studyLoadLevel.isIncludeImages())
		{
			query.addParameter(VistaQuery.LITERAL, "0");
		}
		else
		{
			// only include study details
			query.addParameter(VistaQuery.LITERAL, "1");
		}
		return query;
	}
	
	/**
	 * This version makes use of the studyDeletedImageState and should be used by Patch 119 (and later)
	 * @param studyMap
	 * @param patientDfn
	 * @param studyLoadLevel
	 * @param studyDeletedImageState
	 * @return
	 */
	public static VistaQuery createGetStudiesByIenVistaQuery(Map<?,?> studyMap, String patientDfn, 
			StudyLoadLevel studyLoadLevel, StudyDeletedImageState studyDeletedImageState)
	{
		VistaQuery query = new VistaQuery(RPC_MAG_DOD_GET_STUDIES_BY_IEN); // IN: Groups; Out: Studies
		query.addParameter(VistaQuery.LIST, studyMap);
		query.addParameter(VistaQuery.LITERAL, patientDfn);
		if(studyLoadLevel.isIncludeImages())
		{
			query.addParameter(VistaQuery.LITERAL, "0");
		}
		else
		{
			// only include study details
			query.addParameter(VistaQuery.LITERAL, "1");
		}
		if(studyDeletedImageState == StudyDeletedImageState.includesDeletedImages)
		{
			query.addParameter(VistaQuery.LITERAL, "D");	
		}
		else
		{
			query.addParameter(VistaQuery.LITERAL, "");
		}
		return query;
	}
	
	/**
	 * Create a VistaQuery to get a single Study report given the IEN.
	 * 
	 * @param ien
	 * @return
	 */
	public static VistaQuery createGetReportVistaQuery(String ien)
	{
		VistaQuery vm = new VistaQuery(RPC_MAG_REPORT);
		vm.addParameter(VistaQuery.LITERAL, ien);
		vm.addParameter(VistaQuery.LITERAL, MAG_QA_CHECK); // QA Check
		
		return vm;
	}
	
	public static VistaQuery createGetStudyImagesVistaQuery(String studyIen)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_GET_STUDY_IMAGES);
		msg.addParameter(VistaQuery.LITERAL, studyIen);
		msg.addParameter(VistaQuery.LITERAL, MAG_QA_CHECK); // NO QA CHECK
		return msg;
	}
	
	public static VistaQuery createGetNetworkLocationsVistaQuery()
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_GET_NETLOC);
		msg.addParameter(VistaQuery.LITERAL, "ALL");
		return msg;
	}
	
	public static VistaQuery createGetImagingSiteParametersQuery(String workstationId)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_MAGGUSER2);
		msg.addParameter(VistaQuery.LITERAL, workstationId);
		return msg;
	}
	
	public static VistaQuery createNotifyArchiveOperatorQuery(String filename, String imageIen)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_OFFLINE_IMAGE_ACCESSED);
		msg.addParameter(VistaQuery.LITERAL, filename);
		msg.addParameter(VistaQuery.LITERAL, imageIen);
		return msg;
	}
	
	public static VistaQuery createGetMagInstalledVersionsQuery()
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_INSTALL);		
		return msg;
	}
	
	public static VistaQuery createSessionQuery(String workstationId)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_WRKS_UPDATES);
		msg.addParameter(VistaQuery.LITERAL, workstationId + "^^^^^^^^^");
		return msg;
	}
	
	public static VistaQuery createLogImageAccessQuery(boolean isDodImage, String userDuz, 
			String imageIen, String patientDFN, String userSiteNumber)
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		VistaQuery msg = new VistaQuery(RPC_MAG_ACTION_LOG);
		StringBuilder logParamBuilder = new StringBuilder();
		if(isDodImage)
		{
			getLogger().info("Creating query to log VA access to DOD image");
			// if the image is from the DOD, then the person looking at it is always from the VA, no reason
			// to look at the user site number field
			
			// need to put the DOD Image id into the proper place
			// also need to put in RDODVA to indicate user is from the VA accessing a DOD image
			// we don't put the image Id into the IEN field but the additional field since this is a DOD identifier
			logParamBuilder.append("RVDODVA^" + userDuz + "^");
			logParamBuilder.append("^Wrks^" + patientDFN + "^1^");
			logParamBuilder.append(imageIen);
			logParamBuilder.append("|");
			logParamBuilder.append(transactionContext.getTransactionId());
			logParamBuilder.append("|");
			logParamBuilder.append(transactionContext.getSiteNumber());
			logParamBuilder.append("|");
		}
		else
		{
			if(ExchangeUtil.isSiteDOD(userSiteNumber))
			{
				// image is from the VA but the user is from the DOD, so no workstation to use
				getLogger().info("Creating query to log DOD access to VA image");
				logParamBuilder.append("RVVADOD^" + userDuz + "^");
				logParamBuilder.append(imageIen + "^DOD^");
				logParamBuilder.append(patientDFN + "^1^");				
				logParamBuilder.append(""); // no image identifier since VA image
				logParamBuilder.append("|");
				logParamBuilder.append(transactionContext.getTransactionId());
				logParamBuilder.append("|");
				logParamBuilder.append(transactionContext.getLoggerSiteNumber());
				logParamBuilder.append("|");
				logParamBuilder.append(transactionContext.getLoggerFullName());				
			}
			else
			{
				// image is from the VA and the user is from the VA (V2V)
				getLogger().info("Creating query to log VA access to VA image");
				logParamBuilder.append("RVVAVA^" + userDuz + "^");
				logParamBuilder.append(imageIen + "^Wrks^");
				logParamBuilder.append(patientDFN + "^1^");
				logParamBuilder.append(""); // no image identifier since VA image
				logParamBuilder.append("|");
				logParamBuilder.append(transactionContext.getTransactionId());
				logParamBuilder.append("|");
				logParamBuilder.append(transactionContext.getSiteNumber());
				logParamBuilder.append("|");
				logParamBuilder.append(""); // don't need user full name since using real person DUZ
			}			
		}
		getLogger().debug("Image Access Logging Parameter '" + logParamBuilder.toString() + "'");
		msg.addParameter(VistaQuery.LITERAL, logParamBuilder.toString());
		
		return msg;
	}
	
	public static VistaQuery createLogPatientIdMismatchQuery(String imageIen, String patientDFN)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_ACTION_LOG);
		StringBuilder copyParamBuilder = new StringBuilder();
		copyParamBuilder.append("IMGMM");
		copyParamBuilder.append("^^");
		copyParamBuilder.append(imageIen);
		copyParamBuilder.append("^ICN/SSN mismatch^");
		copyParamBuilder.append(patientDFN);
		copyParamBuilder.append("^1");
		msg.addParameter(VistaQuery.LITERAL, copyParamBuilder.toString());
		return msg;
	}
	
	public static VistaQuery createLogImageCopyQuery(
			ImageAccessLogEvent event)
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		VistaQuery msg = new VistaQuery(RPC_MAG_ACTION_LOG);
		StringBuilder copyParamBuilder = new StringBuilder();
		copyParamBuilder.append(event.getReasonCode());
		copyParamBuilder.append("^^");
		copyParamBuilder.append(event.getDecodedImageIen());
		copyParamBuilder.append("^Copy Image^");
		copyParamBuilder.append(event.getPatientDfn());
		copyParamBuilder.append("^1");
		copyParamBuilder.append("^");
		if((event.getReasonDescription() != null) && (event.getReasonDescription().length() > 0))
		{			
			copyParamBuilder.append(event.getReasonDescription());		
		}
		copyParamBuilder.append("|");
		copyParamBuilder.append(transactionContext.getTransactionId());
		msg.addParameter(VistaQuery.LITERAL, copyParamBuilder.toString());
		return msg;
	}
	
	public static VistaQuery createLogImagePrintQuery(ImageAccessLogEvent event)
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		VistaQuery msg = new VistaQuery(RPC_MAG_ACTION_LOG);
		StringBuilder copyParamBuilder = new StringBuilder();
		copyParamBuilder.append(event.getReasonCode());
		copyParamBuilder.append("^^");
		copyParamBuilder.append(event.getDecodedImageIen());
		copyParamBuilder.append("^Print Image^");
		copyParamBuilder.append(event.getPatientDfn());
		copyParamBuilder.append("^1");
		copyParamBuilder.append("^");
		if((event.getReasonDescription() != null) && (event.getReasonDescription().length() > 0))
		{			
			copyParamBuilder.append(event.getReasonDescription());
		}
		copyParamBuilder.append("|");
		copyParamBuilder.append(transactionContext.getTransactionId());
		msg.addParameter(VistaQuery.LITERAL, copyParamBuilder.toString());
		return msg;
	}
	
	/**
	 * Create query to logoff from VistA (close Imaging sessions on server)
	 * @return
	 */
	/*
	public static VistaQuery createMagLogoffQuery()
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_LOGOFF);
		return msg;
	}*/
	
	/**
	 * Create Query to get the HIS update for an image
	 * @param imageIen
	 * @return
	 */
	public static VistaQuery createGetHisUpdateQuery(String imageIen)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_IMAGE_CURRENT_INFO);
		msg.addParameter(VistaQuery.LITERAL, imageIen);
		return msg;
	}
	
	public static VistaQuery createNewSOPInstanceUidQuery(String siteNumber, String imageIen)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_NEW_SOP_INSTANCE_UID);
		msg.addParameter(VistaQuery.LIST, "");
		// TODO if there is a concept of product ID at VI, it should preceed the site#
		msg.addParameter(VistaQuery.LITERAL, "1.2.840.113754." + siteNumber + "." + dateFormat.format(new Date()) + ".1");
		msg.addParameter(VistaQuery.LITERAL, imageIen);
		return msg;
	}
	
	public static VistaQuery createGetImageInformationQuery(String imageId)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_GET_IMAGE_INFO);
		msg.addParameter(VistaQuery.LITERAL, imageId);
		return msg;
	}
	
	public static VistaQuery createGetImageInformationQuery(String imageId, boolean includeDeletedImages)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_GET_IMAGE_INFO);
		msg.addParameter(VistaQuery.LITERAL, imageId);
		if(includeDeletedImages)
		{
			msg.addParameter(VistaQuery.LITERAL, "D");	
		}
		else
		{
			msg.addParameter(VistaQuery.LITERAL, "");
		}
		return msg;
	}
	
	public static VistaQuery createGetSysGlobalNodesQuery(String imageId)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_SYS_GLOBAL_NODE);
		msg.addParameter(VistaQuery.LITERAL, imageId);
		return msg;
	}
	
	public static VistaQuery createGetDevFieldValues(String imageId, String flags)
	{		
		VistaQuery msg = new VistaQuery(RPC_MAG_DEV_FIELD_VALUES);
		msg.addParameter(VistaQuery.LITERAL, imageId);
		if(flags == null)
			flags = "";
		msg.addParameter(VistaQuery.LITERAL, flags.toUpperCase());
		return msg;
	}
	
	/**
	 * Create query to search for patients by name
	 * @param searchName
	 * @return
	 */
	public static VistaQuery createFindPatientQuery(String searchName)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_PAT_FIND);
		msg.addParameter(VistaQuery.LITERAL, MAG_MAX_PATIENT_RESULT_COUNT + "^" + searchName.toUpperCase());
		return msg;
	}
	
	/**
	 * Create query to look for patient info details
	 * @param patientDfn
	 * @return
	 */
	public static VistaQuery createGetPatientInfoQuery(String patientDfn)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_PAT_INFO);
		msg.addParameter(VistaQuery.LITERAL, patientDfn + "^^0^^1");
		return msg;
	}
	
	/**
	 * Creates a patient photo Id query
	 * @param patientDfn
	 * @return
	 */
	public static VistaQuery createGetPatientPhotosQuery(String patientDfn)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_PAT_PHOTOS);
		msg.addParameter(VistaQuery.LITERAL, patientDfn);
		return msg;
	}
	
	public static VistaQuery createGetImagesForCprsRadExam(CprsIdentifier cprsIdentifier)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_CPRS_RAD_EXAM);
		msg.addParameter(VistaQuery.LITERAL, cprsIdentifier.getCprsIdentifier());
		return msg;
	}
	
	public static VistaQuery createGetImagesForCprsTiuNote(CprsIdentifier cprsIdentifier)
	{
		String tiuId = StringUtils.MagPiece(cprsIdentifier.getCprsIdentifier(), StringUtils.CARET, 5);
		VistaQuery msg = new VistaQuery(RPC_MAG_CPRS_TIU_NOTE);
		msg.addParameter(VistaQuery.LITERAL, tiuId);
		return msg;
	}
	
	public static VistaQuery createGetImageGroupIENVistaQuery(String imageIen) 
	{
		//getLogger().info("getContextIEN TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = new VistaQuery(RPC_GET_VARIABLE_VALUE);
		String arg = "^MAG(2005," + imageIen + ",0)";
		msg.addParameter(VistaQuery.REFERENCE, arg);
		return msg;
	}
	
	public static VistaQuery createMagBrokerSecurityQuery()
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_BROKER_SECURITY);
		
		return msg;
	}
	
	/**
	 * 
	 * @param patientDfn
	 * @param studyFilter
	 * @param allowDeletedImages
	 * @return
	 */
	public static VistaQuery createMagImageListQuery(String patientDfn, StudyFilter studyFilter, 
			boolean allowDeletedImages)
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_IMAGE_LIST);
		String controlParameter = "E"; // existing images
		String fromDate = "";
		String toDate = "";
		
		String studyPackage = "";
		String studyClass = "";
		String studyTypes = "";
		String studyEvent = "";
		String studySpecialty = "";
		String studyOrigin = "";
		int maximumResults = Integer.MAX_VALUE;
		if(studyFilter != null) 
		{
			fromDate = VistaTranslatorUtility.convertDateToRpcFormat(studyFilter.getFromDate());
			toDate = VistaTranslatorUtility.convertDateToRpcFormat(studyFilter.getToDate());
			studyPackage = studyFilter.getStudy_package();
			studyClass = studyFilter.getStudy_class();
			studyTypes = studyFilter.getStudy_type();
			studyEvent = studyFilter.getStudy_event();
			studySpecialty = studyFilter.getStudy_specialty();
			studyOrigin = studyFilter.getOrigin();
			maximumResults = studyFilter.getMaximumResults();
		}
		
		
		if((allowDeletedImages) && (studyFilter != null) && (studyFilter.isIncludeDeleted()))		
			controlParameter += "D"; // deleted images
		msg.addParameter(VistaQuery.LITERAL, controlParameter);
		msg.addParameter(VistaQuery.LITERAL, fromDate);
		msg.addParameter(VistaQuery.LITERAL, toDate);
		if(maximumResults < Integer.MAX_VALUE)
		{
			msg.addParameter(VistaQuery.LITERAL, maximumResults + "");
		}
		else
		{
			msg.addParameter(VistaQuery.LITERAL, "");
		}
		Map<String, String> filterParameters = new HashMap<String, String>();
		
		filterParameters.put(filterParameters.size() + "", "IDFN^^" + patientDfn);
		if(!"".equals(studyClass))
		{
			studyClass = studyClass.replace(',', '^'); // need to convert , to ^
			filterParameters.put(filterParameters.size() + "", "IXCLASS^^" + studyClass);
		}
		if(!"".equals(studyPackage))
		{
			studyPackage = studyPackage.replace(',', '^'); // need to convert , to ^			
			filterParameters.put(filterParameters.size() + "", "IXPKG^^" + studyPackage);
		}
		if(!"".equals(studyTypes))
		{
			studyTypes = studyTypes.replace(',', '^'); // need to convert , to ^
			filterParameters.put(filterParameters.size() + "", "IXTYPE^^" + studyTypes);
		}
		//TODO: make sure Event == Procedure - i think so?
		if(!"".equals(studyEvent))
		{
			studyEvent = studyEvent.replace(',', '^'); // need to convert , to ^
			filterParameters.put(filterParameters.size() + "", "IXPROC^^" + studyEvent);
		}
		if(!"".equals(studySpecialty))
		{
			studySpecialty = studySpecialty.replace(',', '^'); // need to convert , to ^	
			filterParameters.put(filterParameters.size() + "", "IXSPEC^^" + studySpecialty);
		}
		if(!"".equals(studyOrigin))
		{
			studyOrigin = studyOrigin.replace(',', '^'); // need to convert , to ^
			filterParameters.put(filterParameters.size() + "", "IXORIGIN^^" + studyOrigin);
		}
		msg.addParameter(VistaQuery.LIST, filterParameters);			
		return msg;
	}
	
	public static VistaQuery createGetUserKeysQuery()
	{
		VistaQuery query = new VistaQuery(RPC_MAG_USER_KEYS);
		return query;
	}

	public static VistaQuery createGetDivisionsQuery(String accessCode) 
	{
		VistaQuery query = new VistaQuery(RPC_XUS_GET_DIVISIONS);
		query.addParameter(VistaQuery.LITERAL, accessCode);
		return query;
	}
	
	public static VistaQuery createAllowAnnotateQuery()
	{
		VistaQuery query = new VistaQuery(RPC_MAG_IMAGE_ALLOW_ANNOTATE);
		return query;
	}
	
	/**
	 * This is a MAGJ rpc that gets the treating sites for a patient and formats the result as a list rather than a string. This RPC is available to 
	 * MAG WINDOWS as part of Patch 122
	 * @param patientDfn
	 * @return
	 */
	public static VistaQuery createMagJGetTreatingSitesQuery(String patientDfn)
	{
		getLogger().info("MagJGetTreatingSites(" + patientDfn + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = new VistaQuery(RPC_MAGJ_GET_TREATING_LIST);
		vm.addParameter(VistaQuery.LITERAL, patientDfn);
		
		return vm;
	}
	
	public static VistaQuery createLogImagingQuery(ImagingLogEvent logEvent, String patientDfn)
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		VistaQuery msg = new VistaQuery(RPC_MAG_ACTION_LOG);
		StringBuilder sb = new StringBuilder();
		sb.append(logEvent.getAccessType());
		sb.append("^");
		sb.append(transactionContext.getDuz());
		sb.append("^");
		
		AbstractImagingURN imagingUrn = logEvent.getImagingUrn();
		if(imagingUrn != null)
		{
			boolean isDodImage = ExchangeUtil.isSiteDOD(imagingUrn.getOriginatingSiteId());
			// if this is a VA image then put the IEN in place here
			if(!isDodImage)
				sb.append(imagingUrn.getImagingIdentifier());
		}
		sb.append("^");
		sb.append(logEvent.getUserInterface());
		sb.append("^");
		sb.append(patientDfn);
		sb.append("^");
		if(logEvent.getImageCount() >= 0)
			sb.append(logEvent.getImageCount());
		sb.append("^");
		sb.append(logEvent.getAdditionalData());
		getLogger().debug("Imaging log event Parameter '" + sb.toString() + "'");
		msg.addParameter(VistaQuery.LITERAL, sb.toString());
		return msg;
	}
	
	public static VistaQuery createGetReasonsListQuery(List<ImageAccessReasonType> reasonTypes)
	{
		VistaQuery query = new VistaQuery(RPC_MAGG_REASON_LIST);
		StringBuilder reasons = new StringBuilder();
		if(reasonTypes != null)
		{
			for(ImageAccessReasonType reasonType : reasonTypes)
			{
				reasons.append(reasonType.getCode());
			}
		}
		else
		{
			// put all options in
			for(ImageAccessReasonType reasonType : ImageAccessReasonType.values())
			{
				reasons.append(reasonType);
			}
		}
		query.addParameter(VistaQuery.LITERAL, reasons.toString());
		
		return query;
	}
	
	public static VistaQuery createVerifyElectronicSignatureQuery(String electronicSignature)
	{		
		VistaQuery vm = new VistaQuery(RPC_MAGG_VERIFY_ESIG);
		vm.addEncryptedParameter(VistaQuery.LITERAL, electronicSignature);
		return vm;
	}
	
	public static VistaQuery createGetHealthSummariesQuery()
	{
		VistaQuery vm = new VistaQuery(RPC_MAGGHSLIST);
		vm.addParameter(VistaQuery.LITERAL, ""); // parameter does nothing
		return vm;
	}
	
	public static VistaQuery createGetHealthSummary(HealthSummaryURN healthSummaryUrn, String patientDfn)
	{
		VistaQuery vm = new VistaQuery(RPC_MAGGHS);
		vm.addParameter(VistaQuery.LITERAL, patientDfn + StringUtils.CARET + healthSummaryUrn.getSummaryId());
		return vm;
	}
	
	public static VistaQuery createGetApplicationTimeoutParameters(String siteId, String applicationName) 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_TIMEOUT_PARAMETERS);
		vm.addParameter(VistaQuery.LITERAL, applicationName);
		return vm;
	}	
	

}
