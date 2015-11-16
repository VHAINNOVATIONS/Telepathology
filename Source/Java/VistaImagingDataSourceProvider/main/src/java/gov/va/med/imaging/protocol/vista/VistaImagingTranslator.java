package gov.va.med.imaging.protocol.vista;

import gov.va.med.HealthSummaryURN;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.exchange.enums.ImageAccessReasonType;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.protocol.vista.exceptions.VistaParsingException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.EncryptionUtils;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.url.vista.image.NetworkLocation;
import gov.va.med.imaging.url.vista.image.SiteParameterCredentials;
import gov.va.med.imaging.vistaimagingdatasource.VistaGroup;
import gov.va.med.imaging.vistaimagingdatasource.VistaImage;
import gov.va.med.imaging.vistaimagingdatasource.VistaPatient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * 
 * @author vhaiswwerfej
 *
 */
public class VistaImagingTranslator 
{
	private static Logger logger = Logger.getLogger(VistaImagingTranslator.class);

	/**
	 * Convert the returned String value from an RPC call to a PatientSensitiveValue.
	 * 
	 * @param rtn
	 * @param patientDfn
	 * @return
	 * @throws VistaMethodException
	 */
	public static PatientSensitiveValue convertStringToPatientSensitiveValue(String rtn, String patientDfn)
	throws VistaMethodException
	{
		if( (rtn == null) || (rtn.equals("-1")) ) 
		{
			String msg = "Error response while checking patient sensitivity for patient; DFN=" + patientDfn;				
			logger.error(msg);
			throw new VistaMethodException(msg);
		} 
		String [] lines = rtn.split(StringUtils.NEW_LINE);
		if(lines.length <= 0)
		{
			String msg = "Error parsing response from checking patient sensitive; DFN=" + patientDfn;
			logger.error(msg);
			logger.error("VistA Response [" + rtn + "]");
			throw new VistaMethodException(msg);
		}
		int code = Integer.parseInt(lines[0].trim());
		logger.info("Patient Sensitive level for patient (DFN): '" + patientDfn + "' is '" + code + "'");
		PatientSensitivityLevel sensitiveLevel = PatientSensitivityLevel.getPatientSensitivityLevel(code);
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i < lines.length; i++)
		{
			sb.append(lines[i]);
			if(i != (lines.length - 1))
				sb.append("\n");
		}
		
		return new PatientSensitiveValue(sensitiveLevel, sb.toString());
	}
	
	/**
	 * Extract the name of a server share from a complete UNC path.
	 * 
	 * @param uncPath
	 * @return
	 */
	public static String extractServerShare(String uncPath) 
	{
		String imgPath = uncPath;
		if(imgPath.startsWith("\\\\"))
			imgPath = imgPath.substring(2);
		
		String server = StringUtils.Piece(imgPath, "\\", 1);
		String share = StringUtils.Piece(imgPath, "\\", 2);
		String serverShare = "\\\\" + server + "\\" + share;	
		
		return serverShare;
	}
	
	/**
	 * Get the server share from an Image instance.
	 * The server share is the server share of one of (in preferred order)
	 * 1.) the image Full filename
	 * 2.) the image Abs filename
	 * 3.) the image Big filename 
	 * 4.) a zero length string
	 * 
	 * @param image
	 * @return
	 */
	public static String extractServerShare(Image image) 
	{
		if(image == null) 
		{
			logger.info("Image is null, returning empty server share");
			return "";
		}
		
		if( image.getFullFilename() != null && image.getFullFilename().length() > 0 )
		{
			logger.info("Using FULL file path [" + image.getFullFilename() + "] for image [" + image.getIen() + "] for server share.");
			return extractServerShare( image.getFullFilename().toLowerCase() );
		}
		
		if( image.getAbsFilename() != null && image.getAbsFilename().length() > 0 )
		{
			logger.info("Using ABS file path [" + image.getAbsFilename() + "] for image [" + image.getIen() + "] for server share.");
			return extractServerShare( image.getAbsFilename().toLowerCase() );
		}
		
		if( image.getBigFilename() != null && image.getBigFilename().length() > 0 ) 
		{
			logger.info("Using BIG file path [" + image.getBigFilename() + "] for image [" + image.getIen() + "] for server share.");
			return extractServerShare( image.getBigFilename().toLowerCase() );
		}
	
		logger.info("No file paths specified in image [" + image.getIen() + "].");
		return "";
	}
	
	/**
	 * 
	 * @param rtn
	 * @return
	 * @throws Exception
	 */
	public static String parseOptionNumber(String rtn) 
	throws Exception 
	{
		String[] lines = StringUtils.Split(rtn, StringUtils.CRLF);
		if (!lines[0].equals("[Data]")) {
			throw new Exception("Invalid return format (" + rtn + ")");
		}
		if (lines[1].startsWith("[BEGIN_diERRORS]")) {
			throw new Exception(rtn.substring(8));
		}
		if (lines.length == 1)
			throw new Exception("No option number data");
		
		int p = lines[1].indexOf(",^");
		String optNum = lines[1].substring(p + 2);
		if( !StringUtils.isNumeric(optNum) )
			throw new Exception("Non-numeric option number");
		
		return optNum;
	}
	
	/**
	 * Takes a Vista response like:
	 * 1^Class: CLIN - 
	 * Item~S2^Site^Note Title~~W0^Proc DT~S1^Procedure^# Img~S2^Short Desc^Pkg^Class^Type^Specialty^Event^Origin^Cap Dt~S1~W0^Cap by~~W0^Image ID~S2~W0
	 * 1^WAS^NURSING NOTE^09/28/2001 00:01^NOTE^2^CONSULT NURSE MEDICAL WOUND SPEC INPT^NOTE^CLIN^CONSULT^NURSING^WOUND ASSESSMENT^VA^09/28/2001 01:35^IMAGPROVIDERONETWOSIX,ONETWOSIX^1752|1752^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.JPG^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.ABS^CONSULT NURSE MEDICAL WOUND SPEC INPT^3010928^11^NOTE^09/28/2001^36^M^A^^^2^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^
	 * 2^WAS^OPHTHALMOLOGY^08/20/2001 00:01^OPH^10^Ophthalmology^NOTE^CLIN^IMAGE^EYE CARE^^VA^08/20/2001 22:32^IMAGPROVIDERONETWOSIX,ONETWOSIX^1783|1783^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001784.DCM^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001784.ABS^Ophthalmology^3010820^11^OPH^08/20/2001^41^M^A^^^10^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^^
	 * 
	 * and create a sorted set of VistaGroup instances. 
	 * 
	 * @param groupList
	 * @return
	 */
    public static SortedSet<VistaGroup> createGroupsFromGroupLines(
    	Site site, 
    	String groupList, 
    	PatientIdentifier patientIdentifier,
    	StudyDeletedImageState studyDeletedImageState)
    throws VistaParsingException
    {
    	SortedSet<VistaGroup> groups = new TreeSet<VistaGroup>();    	
    	String headerLine = "";
    	if(groupList.charAt(0) == '1') 
    	{			
			String[] lines = StringUtils.Split(groupList, StringUtils.NEW_LINE);
			// the first two lines of the response contain the response status and the metadata, respectively
			if(lines.length <= 0)
				throw new VistaParsingException("Study list contains no status, meta-data or data.");
			if(lines.length == 1)
				throw new VistaParsingException("Study list contains no meta-data or data.");
			if(lines.length == 2)
			{
				logger.info("Study list contains no data.");
				return groups;
			}
			logger.info("Found and parsing [" + lines.length + "] lines of group data for patient '" + patientIdentifier + "'");
			// parse the response status line and retain the ???
			String rpcResponseLine = lines[0].trim();
			String response = StringUtils.MagPiece(rpcResponseLine, StringUtils.CARET, 2);
			
			// the headerLine is the metadata, describes the format of the study results
			// save it and pass to the method that actually parses the study lines
			headerLine = lines[1];
			
			// for each remaining line in the response, parse a Study instance and add it
			// to our list
			for(int j = 2; j < lines.length; j++) 
			{
				VistaGroup group;
				try
				{
					group = createGroupFromGroupLine(site, headerLine, lines[j], patientIdentifier, 
							studyDeletedImageState);
					if(group != null)
					{
						group.setRpcResponseMsg(response);
						if( ! groups.add(group) )
							logger.warn("Duplicate group, IEN='" + group.getIen() + "' is not being added to result set.");
					}
				}
				catch (URNFormatException x)
				{
					throw new VistaParsingException(x);
				}
			}
		}
    	
		// put the patient ICN field into the groups
		for(VistaGroup group : groups)
			group.setPatientIdentifier(patientIdentifier);
		
    	
    	return groups;
    }
    
    /**
     * THIS SHOULD ONLY BE USED IF NOT USING GET PATIENT STUDY GRAPH RPC - This is for old (non patch 83) sites
     * 
	 * Takes a Vista response like:
	 * 1^Class: CLIN - 
	 * Item~S2^Site^Note Title~~W0^Proc DT~S1^Procedure^# Img~S2^Short Desc^Pkg^Class^Type^Specialty^Event^Origin^Cap Dt~S1~W0^Cap by~~W0^Image ID~S2~W0
	 * 1^WAS^NURSING NOTE^09/28/2001 00:01^NOTE^2^CONSULT NURSE MEDICAL WOUND SPEC INPT^NOTE^CLIN^CONSULT^NURSING^WOUND ASSESSMENT^VA^09/28/2001 01:35^IMAGPROVIDERONETWOSIX,ONETWOSIX^1752|1752^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.JPG^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.ABS^CONSULT NURSE MEDICAL WOUND SPEC INPT^3010928^11^NOTE^09/28/2001^36^M^A^^^2^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^
	 * 2^WAS^OPHTHALMOLOGY^08/20/2001 00:01^OPH^10^Ophthalmology^NOTE^CLIN^IMAGE^EYE CARE^^VA^08/20/2001 22:32^IMAGPROVIDERONETWOSIX,ONETWOSIX^1783|1783^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001784.DCM^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001784.ABS^Ophthalmology^3010820^11^OPH^08/20/2001^41^M^A^^^10^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^^
	 * 
	 * 
	 * @param studyList
	 * @return
	 */
    public static SortedSet<VistaGroup> createGroupsFromGroupLinesHandleSingleImageGroup(
    	Site site, 
    	String studyList, 
    	PatientIdentifier patientIdentifier, 
    	StudyLoadLevel studyLoadLevel,
    	StudyDeletedImageState studyDeletedImageState)
    throws VistaParsingException
    {
    	SortedSet<VistaGroup> studies = new TreeSet<VistaGroup>();    	
    	String headerLine = "";
    	if(studyList.charAt(0) == '1') 
    	{			
			String[] lines = StringUtils.Split(studyList, StringUtils.NEW_LINE);
			// the first two lines of the response contain the response status and the metadata, respectively
			if(lines.length <= 0)
				throw new VistaParsingException("Study list contains no status, meta-data or data.");
			if(lines.length == 1)
				throw new VistaParsingException("Study list contains no meta-data or data.");
			if(lines.length == 2)
			{
				logger.info("Study list contains no data.");
				return studies;
			}
			
			// parse the response status line and retain the ???
			String rpcResponseLine = lines[0].trim();
			String response = StringUtils.MagPiece(rpcResponseLine, StringUtils.CARET, 2);
			
			// the headerLine is the metadata, describes the format of the study results
			// save it and pass to the method that actually parses the study lines
			headerLine = lines[1];
			
			// for each remaining line in the response, parse a Study instance and add it
			// to our list
			for(int j = 2; j < lines.length; j++) 
			{
				VistaGroup study = createGroupFromGroupLineHandleSingleImageGroup(
					site, headerLine, lines[j], patientIdentifier, studyLoadLevel, 
					studyDeletedImageState);
				if(study != null)
				{
					study.setRpcResponseMsg(response);
					studies.add(study);
				}
			}
		}
    	/*
		// put the site number field into the object
		for(Study group : studies)
			group.setPatientIcn(patientIcn);
		*/
    	
    	return studies;
    }
	
    /**
     * take one line from a VistA response and make a Study instance from it.
     * The study header describes the content of the Study string and looks something like:
     * Item~S2^Site^Note Title~~W0^Proc DT~S1^Procedure^# Img~S2^Short Desc^Pkg^Class^Type^Specialty^Event^Origin^Cap Dt~S1~W0^Cap by~~W0^Image ID~S2~W0
     * 
     * A study line looks something like this:
	 * 1^WAS^NURSING NOTE^09/28/2001 00:01^NOTE^2^CONSULT NURSE MEDICAL WOUND SPEC INPT^NOTE^CLIN^CONSULT^NURSING^WOUND ASSESSMENT^VA^09/28/2001 01:35^IMAGPROVIDERONETWOSIX,ONETWOSIX^1752|1752^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.JPG^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.ABS^CONSULT NURSE MEDICAL WOUND SPEC INPT^3010928^11^NOTE^09/28/2001^36^M^A^^^2^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^
     * 
     * NOTE: the study line includes the data for the first image in the study, which contains much of what
     * we would consider Study data
     * 
     * @param studyHeader
     * @param studyString
     * @return
     * @throws URNFormatException 
     * @throws VistaParsingException 
     */
    private static VistaGroup createGroupFromGroupLine(
    	Site site, 
    	String studyHeader, 
    	String studyString, 
    	PatientIdentifier patientIdentifier,
    	StudyDeletedImageState studyDeletedImageState) 
    throws URNFormatException, VistaParsingException
    {
    	if((studyHeader == null) || (studyHeader.equals("")))
    		return null;

    	if((studyString == null) || (studyString.equals("")))
    		return null;

    	String[] parts = StringUtils.Split(studyString, StringUtils.STICK);
    	
    	// do study part
    	String [] pieces = StringUtils.Split(parts[0], StringUtils.CARET);
    	String [] keys = StringUtils.Split(studyHeader, StringUtils.CARET);
    	
    	// clean up the keys (remove any ~ values)
    	for(int i = 0; i < keys.length; i++)
    		keys[i] = StringUtils.MagPiece(keys[i], StringUtils.TILDE, 1);
    	
    	int maxLength = pieces.length;
    	if(maxLength > keys.length)
    		maxLength = keys.length;
    	
    	// create a VistaImage instance to hold the data temporarily
    	VistaImage vistaImage = VistaImage.create("^" + parts[1]);
    	
    	VistaGroup group = new VistaGroup(StudyLoadLevel.STUDY_ONLY, studyDeletedImageState);
    	
		//String key = StringUtils.MagPiece(keys[i], StringUtils.TILDE, 1);
    	for(int i = 0; i < maxLength; i++) 
    		group.setValue( keys[i], pieces[i] );
    	
    	if((group.getProcedureDateString() != null) && (group.getProcedureDateString().length() > 0))
    		group.setProcedureDate(VistaTranslatorUtility.convertVistaDatetoDate(group.getProcedureDateString()));
    	
    	// set this here in case the setValue() calls overwrite it
    	group.setPatientIdentifier(patientIdentifier);
    	group.setPatientName(vistaImage.getPatientName());
    	group.setIen(vistaImage.getIen());
    	// JMW 4/26/2011 P104T4 - set the group site abbreviation from the image value
    	group.setSiteAbbr(vistaImage.getSiteAbbr());
    	
    	// setting the first image, if populating for shallow this will be needed, if full it will
    	// be trashed
    	group.setFirstVistaImage(vistaImage);
    	group.setFirstImageIen(vistaImage.getIen());
    	
    	// new fields available from Patch 93    	
    	group.setDocumentDate(vistaImage.getDocumentDate());
    	group.setSensitive(vistaImage.isSensitive());
    	group.setStudyStatus(vistaImage.getImageStatus());
    	group.setStudyViewStatus(vistaImage.getImageViewStatus());
    	group.setStudyImagesHaveAnnotations(vistaImage.isImageHasAnnotations());
    	
    	return group;
    }
    
    /**
     * Transform a VistaImage instance into an Image instance, adding the study properties.
     * 
     * @param vistaImage
     * @return
     * @throws URNFormatException 
     */
    public static Image transform(String originatingSiteId, String studyId, PatientIdentifier patientIdentifier, 
    		VistaImage vistaImage) 
    throws URNFormatException
    {
    	Image image = Image.create(originatingSiteId, vistaImage.getIen(), studyId, 
    			patientIdentifier, vistaImage.getImageModality());
    	
        image.setFullFilename(vistaImage.getFullFilename());
        image.setAbsFilename(vistaImage.getAbsFilename());
        image.setDescription(vistaImage.getDescription());
        image.setImgType(vistaImage.getImgType());
        image.setProcedure(vistaImage.getProcedure());
        image.setProcedureDate(vistaImage.getProcedureDate());
        image.setAbsLocation(vistaImage.getAbsLocation());
        image.setFullLocation(vistaImage.getFullLocation());
        image.setDicomSequenceNumberForDisplay(vistaImage.getDicomSequenceNumberForDisplay());
        image.setDicomImageNumberForDisplay(vistaImage.getDicomImageNumberForDisplay());
        image.setSiteAbbr(vistaImage.getSiteAbbr());
        image.setQaMessage(vistaImage.getQaMessage());
        image.setBigFilename(vistaImage.getBigFilename());
        image.setPatientDFN(vistaImage.getPatientDFN());
        image.setPatientName(vistaImage.getPatientName());
        image.setImageClass(vistaImage.getImageClass());
        image.setDocumentDate(vistaImage.getDocumentDate());
		image.setCaptureDate(vistaImage.getCaptureDate());
		image.setSensitive(vistaImage.isSensitive());
		image.setImageStatus(vistaImage.getImageStatus());
		image.setImageViewStatus(vistaImage.getImageViewStatus());
		image.setAssociatedNoteResulted(vistaImage.getAssociatedNoteResulted());
		image.setImagePackage(vistaImage.getImagePackage());
		image.setImageHasAnnotations(vistaImage.isImageHasAnnotations());
		image.setImageAnnotationStatus(vistaImage.getImageAnnotationStatus());
    	image.setImageAnnotationStatusDescription(vistaImage.getImageAnnotationStatusDescription());
		
    	return image;
    }
    
    /**
     * Transform a Collection of VistaImage instances into a SortedSet of Image instances,
     * adding the study and patient key data.
     * 
     * @param originatingSiteId
     * @param studyId
     * @param patientIcn
     * @param vistaImages
     * @return
     * @throws URNFormatException
     */
    public static SortedSet<Image> transform(
    	String originatingSiteId, 
    	String studyId, 
    	PatientIdentifier patientIdentifier, 
    	Collection<VistaImage> vistaImages) 
    throws URNFormatException
    {
    	SortedSet<Image> result = new TreeSet<Image>();
    	
    	for(VistaImage vistaImage : vistaImages)
    		if( !result.add(transform(originatingSiteId, studyId, patientIdentifier, vistaImage)) )
    			logger.warn("Duplicate image, IEN='" + vistaImage.getIen() + "' is not being added during transform.");
    	
    	return result;
    }
    /**
     * THIS SHOULD ONLY BE USED IF NOT USING GET PATIENT STUDY GRAPH RPC - This is for old (non patch 83) sites
     * 
     * take one line from a VistA response and make a Study instance from it.
     * The study header describes the content of the Study string and looks something like:
     * Item~S2^Site^Note Title~~W0^Proc DT~S1^Procedure^# Img~S2^Short Desc^Pkg^Class^Type^Specialty^Event^Origin^Cap Dt~S1~W0^Cap by~~W0^Image ID~S2~W0
     * 
     * A study line looks something like this:
	 * 1^WAS^NURSING NOTE^09/28/2001 00:01^NOTE^2^CONSULT NURSE MEDICAL WOUND SPEC INPT^NOTE^CLIN^CONSULT^NURSING^WOUND ASSESSMENT^VA^09/28/2001 01:35^IMAGPROVIDERONETWOSIX,ONETWOSIX^1752|1752^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.JPG^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.ABS^CONSULT NURSE MEDICAL WOUND SPEC INPT^3010928^11^NOTE^09/28/2001^36^M^A^^^2^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^
     * 
     * NOTE: the study line includes the data for the first image in the study, which contains much of what
     * we would consider Study data
     * 
     * @param studyHeader
     * @param studyString
     * @return
     * @throws VistaParsingException 
     */
    public static VistaGroup createGroupFromGroupLineHandleSingleImageGroup(
    	Site site, 
    	String studyHeader, 
    	String studyString, 
    	PatientIdentifier patientIdentifier, 
    	StudyLoadLevel studyLoadLevel,
    	StudyDeletedImageState studyDeletedImageState) 
    throws VistaParsingException
    {
    	if((studyHeader == null) || (studyHeader.equals("")))
    		return null;

    	if((studyString == null) || (studyString.equals("")))
    		return null;

    	String[] parts = StringUtils.Split(studyString, StringUtils.STICK);
    	
    	// do study part
    	String [] pieces = StringUtils.Split(parts[0], StringUtils.CARET);
    	String [] keys = StringUtils.Split(studyHeader, StringUtils.CARET);
    	
    	// clean up the keys (remove any ~ values)
    	for(int i = 0; i < keys.length; i++)
    		keys[i] = StringUtils.MagPiece(keys[i], StringUtils.TILDE, 1);
    	
    	int maxLength = pieces.length;
    	if(maxLength > keys.length)
    		maxLength = keys.length;
    	
    	String imageString = "^" + parts[1];
    	VistaGroup study = new VistaGroup( studyLoadLevel, studyDeletedImageState );
    	
    	for(int i = 0; i < maxLength; i++) 
    	{
    		//String key = StringUtils.MagPiece(keys[i], StringUtils.TILDE, 1);
    		study.setValue( keys[i], pieces[i] );
    	}
    	
    	if((study.getProcedureDateString() != null) && (study.getProcedureDateString().length() > 0))
    	{
    		study.setProcedureDate(VistaTranslatorUtility.convertVistaDatetoDate(study.getProcedureDateString()));
    	}
    	
    	// set patient Icn since not getting from these VistA RPC calls
    	study.setPatientIdentifier(patientIdentifier);
    	
		VistaImage vistaImage = VistaImage.create(imageString);
		study.setIen(vistaImage.getIen());
		
    	// if the study is a single image study, then set the first image
    	if((vistaImage.getImgType() !=  11) || (!studyLoadLevel.isIncludeImages()))
    	{
    		study.setFirstVistaImage(vistaImage);
    		study.setFirstImageIen(vistaImage.getIen());
    		study.setPatientName(vistaImage.getPatientName());
    	}    	
    	study.setStudyImagesHaveAnnotations(vistaImage.isImageHasAnnotations());
    	
    	return study;
    }
    
    /**
     * Transform a collection of VistaGroup instances into a sorted set of Study instances.
     * 
     * @param objectOrigin
     * @param site
     * @param groups
     * @return
     * @throws URNFormatException
     */
    public static SortedSet<Study> transform(ObjectOrigin objectOrigin, Site site, Collection<VistaGroup> groups) 
    throws URNFormatException
    {
    	SortedSet<Study> result = new TreeSet<Study>();
    	
    	for(VistaGroup group : groups)
    		result.add(transform(objectOrigin, site, group));
    	
    	return result;
    }
    
    /**
     * Transform a single VistaGroup instance into a Study instance.
     * 
     * @param group
     * @return
     * @throws URNFormatException 
     */
    public static Study transform(ObjectOrigin objectOrigin, Site site, VistaGroup group) 
    throws URNFormatException
    {
    	Study result = Study.create(objectOrigin, site.getSiteNumber(), group.getIen(), 
    			group.getPatientIdentifier(), group.getStudyLoadLevel(), group.getStudyDeletedImageState());
    	
    	// copy the "dynamic" properties first and then copy the named properties
    	// some of the named properties may overwrite the "dynamic" properties
    	for( Enumeration<String> propertyKeyEnumerator = group.getKeys(); propertyKeyEnumerator.hasMoreElements(); )
    	{
    		String propertyKey = propertyKeyEnumerator.nextElement();
    		result.setValue(propertyKey, group.getValue(propertyKey));
    	}
    	
    	result.setAlienSiteNumber(group.getAlienSiteNumber());
    	result.setCaptureBy(group.getCaptureBy());
    	result.setCaptureDate(group.getCaptureDate());
    	result.setDescription(group.getDescription());
    	result.setErrorMessage(group.getErrorMessage());
    	result.setEvent(group.getEvent());
    	if(group.getFirstVistaImage() != null)
    	{
    		Image firstImage = transform(site.getSiteNumber(), group.getIen(), group.getPatientIdentifier(), group.getFirstVistaImage());
    		result.setFirstImage(firstImage);
    	}
    	result.setFirstImageIen(group.getFirstImageIen());
    	result.setImageCount(group.getImageCount());
    	result.setImagePackage(group.getImagePackage());
    	result.setImageType(group.getImageType());
    	result.setNoteTitle(group.getNoteTitle());
    	result.setOrigin(group.getOrigin());
    	//result.setPatientIcn(group.getPatientIcn());
    	result.setPatientName(group.getPatientName());
    	result.setProcedure(group.getProcedure());
    	result.setProcedureDate(group.getProcedureDate());
    	result.setProcedureDateString(group.getProcedureDateString());
    	result.setRadiologyReport(group.getRadiologyReport());
    	result.setRpcResponseMsg(group.getRpcResponseMsg());
    	result.setSiteAbbr(site.getSiteAbbr());
    	result.setSiteName(site.getSiteName());
    	result.setSpecialty(group.getSpecialty());
    	result.setStudyClass(group.getStudyClass());
    	result.setStudyUid(group.getStudyUid());
    	
    	return result;
    }
    
	private final static String STUDY_UID_KEY = "STUDY_UID";
	private final static String STUDY_PAT_KEY = "STUDY_PAT";
	private final static String STUDY_IEN_KEY = "STUDY_IEN";
	private final static String SERIES_UID_KEY = "SERIES_UID";
	private final static String SERIES_IEN_KEY = "SERIES_IEN";
	private final static String SERIES_NUMBER_KEY = "SERIES_NUMBER";
	private final static String IMAGE_UID_KEY = "IMAGE_UID";
	private final static String IMAGE_IEN_KEY = "IMAGE_IEN";
	private final static String IMAGE_NUMBER_KEY = "IMAGE_NUMBER";
	private final static String IMAGE_ABSTRACT_KEY = "IMAGE_ABSTRACT";
	private final static String IMAGE_FULL_KEY = "IMAGE_FULL";
	private final static String IMAGE_TEXT_KEY = "IMAGE_TEXT";
	private final static String IMAGE_INFO_KEY = "IMAGE_INFO";
	private final static String STUDY_NEXT = "NEXT_STUDY";
	private final static String SERIES_NEXT = "NEXT_SERIES";
	private final static String IMAGE_NEXT = "NEXT_IMAGE";
	private final static String GROUP_IEN_KEY = "GROUP_IEN";
	private final static String STUDY_MODALITY = "STUDY_MODALITY";
	private final static String SERIES_MODALITY = "SERIES_MODALITY";
	private final static String STUDY_ERROR = "STUDY_ERR";
	private final static String IMAGE_ERROR = "IMAGE_ERR";
	
	/**
	 * Convert a String, as returned from VistA Imaging, into a sorted set of Study instances.
	 * 
	 * @param site
	 * @param vistaResponse
	 * @param studyLoadLevel
	 * @return
	 */
	public static SortedSet<Study> createStudiesFromGraph(Site site, String vistaResponse, 
			StudyLoadLevel studyLoadLevel, StudyDeletedImageState studyDeletedImageState) 
	{
		String details[] = StringUtils.Split(vistaResponse, StringUtils.NEW_LINE);
		SortedSet<Study> studies = createStudiesFromGraph(site, details, studyLoadLevel, 
				studyDeletedImageState);
		return studies;
	}
	

	// the definition of the levels in teh hierarchy of data returned from the Vista RPC call
	private static VistaImagingParser.OntologyDelimiterKey[] studyOntologyDelimiterKeys = 
	new VistaImagingParser.OntologyDelimiterKey[] 
	{
		new VistaImagingParser.OntologyDelimiterKey(STUDY_NEXT, new String[] {STUDY_MODALITY}),
		new VistaImagingParser.OntologyDelimiterKey(SERIES_NEXT),
		new VistaImagingParser.OntologyDelimiterKey(IMAGE_NEXT)
	};
	
	/**
	 * 
	 * @param site
	 * @param studyLines - a String array originally from Vista in a form that defies simple description.
	 *        "lines" are CR delimited lines of text
	 *        "parts" are delimited by the vertical bar '|' character
	 *        "pieces" are delimited by the caret '^' character
	 *        Each line consists of 1..n parts.  The first part (index=0) is a key.
	 *        Each part consists of 1..n pieces.
	 *        Prior to calling this method, the parts have been parsed into a String array.
	 *  Example of a single image study:
	 *        11
	 *        NEXT_STUDY||712
	 *        STUDY_IEN|712
	 *        STUDY_PAT|1011|9217103663V710366|IMAGPATIENT1011,1011
	 *        NEXT_SERIES
	 *        SERIES_IEN|712
	 *        NEXT_IMAGE
	 *        IMAGE_IEN|713
	 *        IMAGE_INFO|B2^713^\\isw-werfelj-lt\image1$\DM\00\07\DM000713.TGA^\\isw-werfelj-lt\image1$\DM\00\07\DM000713.ABS^040600-28  CHEST SINGLE VIEW^3000406.1349^3^CR^04/06/2000^^M^A^^^1^1^SLC^^^1011^IMAGPATIENT1011,1011^CLIN^^^^
	 *        IMAGE_ABSTRACT|\\isw-werfelj-lt\image1$\DM\00\07\DM000713.ABS
	 *        IMAGE_FULL|\\isw-werfelj-lt\image1$\DM\00\07\DM000713.TGA
	 *        IMAGE_TEXT|\\isw-werfelj-lt\image1$\DM\00\07\DM000713.TXT
	 *  The first line is the number of lines in the response.
	 *  
	 *  The keys "NEXT_STUDY", "NEXT_SERIES", and "NEXT_IMAGE" make up the "study ontology" definition, really
	 *  just the demarcation of the levels of the hierarchy.  The VistaImagingParser will use those keys as delimiters
	 *  when parsing the String returned from Vista into a hierarchy of lines.
	 *       
	 * @return
	 */
	public static SortedSet<Study> createStudiesFromGraph(Site site, String[] studyLines, 
			StudyLoadLevel studyLoadLevel, StudyDeletedImageState studyDeletedImageState) 
	{		
		SortedSet<Study> studyList = new TreeSet<Study>();
		if( studyLines == null || studyLines.length <= 1 )
			return studyList;		// i.e. return an empty Set
		
		String studyCountLine = studyLines[0].trim();
		try
        {
	        int expectedLineCount = Integer.parseInt( studyCountLine );
	        if( expectedLineCount != studyLines.length-1 )
				logger.warn("The expected number of lines (" + expectedLineCount + 
						") does not match the actual number (" + (studyLines.length-1) + 
						"', continuing.");
        } 
		catch (NumberFormatException e)
        {
			logger.warn("Unable to parse the first line (containing number of lines) in the VistA response.  Line was '" + 
					studyCountLine + "', continuing.");
        }
		
		// drop the first line
		String[] realLines = new String[studyLines.length-1];
		System.arraycopy(studyLines, 1, realLines, 0, realLines.length);
		
		// create a Vista Parser using the hierarchy levels defined by the ontology delimiter keys 
		VistaImagingParser parser = new VistaImagingParser(studyOntologyDelimiterKeys);
		
		List<VistaImagingParser.ParsedVistaLine> parsedStudyLines = parser.parse(realLines, true);

		// if there are any parsed lines that have the root key
		if(parsedStudyLines != null && parsedStudyLines.size() > 0)
			for(VistaImagingParser.ParsedVistaLine studyLine : parsedStudyLines)
			{
				Study study;
				try
				{
					study = createStudy(site, studyLine, studyLoadLevel, studyDeletedImageState);
					if(study != null)
						studyList.add(study);// createStudy(site, studyLine) );
				}
				catch (URNFormatException x)
				{
					logger.error("URNFormatException creating a Study from the VistAImaging study line '" + studyLine + "'.", x);
				}
			}
			// add the complete Study with Series and Image instances attached
			// to the list of Study
		
		return studyList;
	}

	/**
	 * 
	 * @param site
	 * @param studyLine
	 * @return
	 * @throws URNFormatException 
	 */
	private static Study createStudy(Site site, VistaImagingParser.ParsedVistaLine studyLine, 
			StudyLoadLevel studyLoadLevel, StudyDeletedImageState studyDeletedImageState) 
	throws URNFormatException
    {
		VistaImagingParser.ParsedVistaLine ienProperty = studyLine.getProperty(STUDY_IEN_KEY);
	    String ien = null;
	    
	    // we must have the IEN to create a Study
	    if(ienProperty != null)
	    	ien = ienProperty.getValueAtIndex(0);	// either the first value of the STUDY_IEN line
	    else
	    	ien = studyLine.getValueAtIndex(1);		// or the second value from the NEXT_STUDY line	    	   
	    
	    int imageCount = 0;
	    String firstImageIen = "";
	    String cptCode = "";
	    String consolidatedSiteNumber = "";
	    if(ienProperty.isValueAtIndexExists(1))
	    {
	    	String imageCountString = ienProperty.getValueAtIndex(1);
	    	if((imageCountString != null) && (imageCountString.length() > 0))
	    	{
	    		imageCount = Integer.parseInt(imageCountString);
	    	}
	    }
	    if(ienProperty.isValueAtIndexExists(2))
	    {
	    	// CTB 27Nov2009
	    	//firstImageIen = Base32ConversionUtility.base32Encode(ienProperty.getValueAtIndex(2));
	    	firstImageIen = ienProperty.getValueAtIndex(2);
	    }
	    // JMW 10/6/2010 P104 - the CPT code is present in the 3rd piece of the STUDY_IEN field
	    if(ienProperty.isValueAtIndexExists(3))
	    {
	    	cptCode = ienProperty.getValueAtIndex(3);
	    }
	    // JMW 10/29/2010 P104 - if site the image is physically stored at is in the 4th piece of the STUDY_IEN field 
	    if(ienProperty.isValueAtIndexExists(4))
	    {
	    	consolidatedSiteNumber = ienProperty.getValueAtIndex(4);
	    }
	    
	    // CTB 29Nov2009
	    //ien = Base32ConversionUtility.base32Encode(ien);

	    if(!studyLoadLevel.isIncludeImages())
	    {
		    // this is a special case, if the load level was not full
	    	// if the study is a single image study, then older studies may not have an image node,
	    	// it might just be the parent node and it represents the image node. in this case
	    	// the imageCount and firstImageIen will not have been provided in the RPC call,
	    	// the firstImageIen is the same as the group ien and the imageCount is 1
	    	if(imageCount == 0)
	    	{
	    		logger.debug("StudyLoadLevel was not full and image count was 0, setting image count to 1 indicating single image group with no child node");
	    		imageCount = 1;
	    	}
	    	if(firstImageIen.length() == 0)
	    	{
	    		logger.debug("StudyLoadLevel was not full and first Image Ien is mising, setting value to '" + ien + "', indicating single image group with no child node");
	    		firstImageIen = ien;
	    	}
	    }
	    
	    //studyLine.getValueAtIndex(index)

	    String studyUid = null;
	    VistaImagingParser.ParsedVistaLine uidProperty = studyLine.getProperty(STUDY_UID_KEY);
	    if(uidProperty != null)
	    	studyUid = uidProperty.getValueAtIndex(0);
	    
	    String patientIcn = null;
	    String patientName = null;
	    String patientDfn = null;
	    VistaImagingParser.ParsedVistaLine patientProperty = studyLine.getProperty(STUDY_PAT_KEY);
	    if(patientProperty != null)
	    {
	    	patientDfn = patientProperty.getValueAtIndex(0);
	    	patientIcn = patientProperty.getValueAtIndex(1);
	    	patientName = patientProperty.getValueAtIndex(2);
	    }
	    
	    PatientIdentifier patientIdentifier = null;
	    if(patientIcn != null && patientIcn.length() > 0 && !patientIcn.startsWith("-1"))
	    	patientIdentifier = PatientIdentifier.icnPatientIdentifier(patientIcn);
	    else
	    	patientIdentifier = PatientIdentifier.dfnPatientIdentifier(patientDfn);
	    
	    Study study = Study.create(ObjectOrigin.VA, site.getSiteNumber(), ien, 
	    		patientIdentifier, studyLoadLevel, studyDeletedImageState);
	    study.setSiteName(site.getSiteName());
	    study.setSiteAbbr(site.getSiteAbbr());
	    
	    study.setStudyUid(studyUid);

    	study.setPatientName(patientName);
    	study.setCptCode(cptCode);
    	study.setConsolidatedSiteNumber(consolidatedSiteNumber);

	    if(!studyLoadLevel.isIncludeImages())
	    {
			// CTB 29Nov2009
	    	//logger.info("Study is not loaded with images, setting image count to '" + imageCount + 
	    	//		"' and first image IEN to '" + firstImageIen + "' Base32{"  + Base32ConversionUtility.base32Decode(firstImageIen) + "}.");
	    	logger.info("Study is not loaded with images, setting image count to '" + imageCount + 
    			"' and first image IEN to '" + firstImageIen + "'.");
	    	study.setImageCount(imageCount);
	    	study.setFirstImageIen(firstImageIen);
	    }
	    
	    VistaImagingParser.ParsedVistaLine modalityProperty = studyLine.getProperty(STUDY_MODALITY);
	    if(modalityProperty != null)
	    {
	    	String[] modalities = modalityProperty.getValueAtIndex(0).split(",", -1);
	    	for(String modality : modalities)
	    		study.addModality(modality);
	    }
	    VistaImagingParser.ParsedVistaLine errorProperty = studyLine.getProperty(STUDY_ERROR);
	    if(errorProperty != null)
	    {
	    	studyUid = errorProperty.getValueAtIndex(0);
	    	String errorMsg = errorProperty.getValueAtIndex(1);
	    	logger.warn("STUDY Error for study Uid [" + studyUid + "], study Ien [" + ien + "], '" + errorMsg + "'." );
	    	study.setErrorMessage(errorMsg);
	    	// JMW 7/17/08 - we now return the study but keep the error message to use later
	    	//return null;
	    }

	    for( Iterator<VistaImagingParser.ParsedVistaLine> seriesIter = studyLine.childIterator(); seriesIter.hasNext(); )
	    {
	    	VistaImagingParser.ParsedVistaLine seriesLine = seriesIter.next();
	    	Series series = createSeries(site, study, seriesLine);
	    	
	    	study.addSeries(series);
	    }
	    
	    return study;
    }
	
	/**
	 * 
	 * @param site
	 * @param parentStudy
	 * @param seriesLine
	 * @return
	 * @throws URNFormatException 
	 */
	private static Series createSeries(Site site, Study parentStudy, VistaImagingParser.ParsedVistaLine seriesLine) 
	throws URNFormatException
    {
		Series series = new Series();
		
		VistaImagingParser.ParsedVistaLine uidProperty = seriesLine.getProperty(SERIES_UID_KEY);
	    if(uidProperty != null)
	    {
	    	String uid = uidProperty.getValueAtIndex(0);
	    	series.setSeriesUid(uid);
	    }
	    
	    VistaImagingParser.ParsedVistaLine ienProperty = seriesLine.getProperty(SERIES_IEN_KEY);
	    if(ienProperty != null)
	    {
	    	String ien = ienProperty.getValueAtIndex(0);
	    	series.setSeriesIen(ien);
	    }
	    
	    VistaImagingParser.ParsedVistaLine numberProperty = seriesLine.getProperty(SERIES_NUMBER_KEY);
	    if(numberProperty != null)
	    {
	    	String number = numberProperty.getValueAtIndex(0);
	    	series.setSeriesNumber(number);
	    }
	    
	    VistaImagingParser.ParsedVistaLine modalityProperty = seriesLine.getProperty(SERIES_MODALITY);
	    if(modalityProperty != null)
	    {
	    	String modality = modalityProperty.getValueAtIndex(0);
	    	series.setModality(modality);
	    }
	    
	    for( Iterator<VistaImagingParser.ParsedVistaLine> imageIter = seriesLine.childIterator(); imageIter.hasNext(); )
	    {
	    	VistaImagingParser.ParsedVistaLine imageLine = imageIter.next();
	    	try
	    	{
	    		Image image = createImage(site, parentStudy, series, imageLine);	    	
	    		series.addImage(image);
	    	}
	    	catch(VistaParsingException vpX)
	    	{
	    		logger.error("VistaParsingException creating image, " + vpX.getMessage(), vpX);
	    	}
	    }
	    
		return series;
    }
	
	/**
	 * 
	 * @param site
	 * @param parentStudy
	 * @param parentSeries
	 * @param seriesLine
	 * @return
	 * @throws URNFormatException 
	 */
	private static Image createImage(
		Site site, 
		Study parentStudy, 
		Series parentSeries, 
		VistaImagingParser.ParsedVistaLine imageLine) 
	throws URNFormatException, VistaParsingException
    {
		String imageIen = null;
		String consolidatedSiteNumber = "";
		VistaImagingParser.ParsedVistaLine ienProperty = imageLine.getProperty(IMAGE_IEN_KEY);
	    if(ienProperty != null)
	    {
	    	//imageIen = Base32ConversionUtility.base32Encode(ienProperty.getValueAtIndex(0));
	    	imageIen = ienProperty.getValueAtIndex(0);	    	
	    }
	    
	    
	    String imageUid = "";
    	VistaImagingParser.ParsedVistaLine uidProperty = imageLine.getProperty(IMAGE_UID_KEY);
	    if(uidProperty != null)
	    {
	    	String uid = uidProperty.getValueAtIndex(0);
	    	imageUid = uid;
	    }
	    
	    String imageNumber = "";	    
	    VistaImagingParser.ParsedVistaLine numberProperty = imageLine.getProperty(IMAGE_NUMBER_KEY);
	    if(numberProperty != null)
	    {
	    	String number = numberProperty.getValueAtIndex(0);
	    	imageNumber = number;
	    }	    
	    
	    String imageInfoLine = null;
	    VistaImagingParser.ParsedVistaLine infoProperty = imageLine.getProperty(IMAGE_INFO_KEY);
	    if(infoProperty != null)
	    {
	    	String infoLine = infoProperty.getValueAtIndex(0);
	    	imageInfoLine = infoLine;	    	
	    	if(infoProperty.isValueAtIndexExists(1))
	    	{
	    		consolidatedSiteNumber = infoProperty.getValueAtIndex(1);
	    	}
	    }
	    
	    String groupIen = null;
	    VistaImagingParser.ParsedVistaLine groupIenProperty = imageLine.getProperty(GROUP_IEN_KEY);
	    if(groupIenProperty != null)
	    {
	    	// CTB 27Nov2009
	    	//String ien = Base32ConversionUtility.base32Encode(groupIenProperty.getValueAtIndex(0));
	    	//image.setGroupIen(ien);
	    	groupIen = groupIenProperty.getValueAtIndex(0);
	    }
	    else
	    {
	    	// if the GROUP_IEN key is missing that means this is a single image
	    	// study with no child node, in this case the group IEN, image IEN, and STUDY IEN are all the same
	    	//image.setGroupIen(parentStudy.getStudyIen());
	    }
	    VistaImagingParser.ParsedVistaLine imageErrorProperty = imageLine.getProperty(IMAGE_ERROR);
	    String errorMessage = null;
	    if(imageErrorProperty != null)
	    {
	    	String errorMsg = imageErrorProperty.getValueAtIndex(0);
	    	errorMessage = errorMsg;
	    }
	    // JMW 2/6/08 - No longer getting these values from the graph, getting them from the info key	 
	    // JMW 3/7/08 - setting these values at the end so that they overwrite the values we got 
	    // from VistA - not entirely sure about this but sometimes VistA doesn't have the right 
	    // abbreviation (if the site has not set it properly)
	    // JMW 1/21/10 - want to use site abbreviation from VistA for consolidated sites so they have the actual value - this could
	    // cause problems if site doesn't set value properly, but should not have functional impact - only visual impact.
	    //image.setSiteAbbr(site.getSiteAbbr());
	    // JMW 4/9/10 - not sure why setSiteNumber is commented out...
		//image.setSiteNumber(site.getSiteNumber());
	    
	    Image image = Image.create(site.getSiteNumber(), imageIen, 
	    		groupIen == null ? parentStudy.getStudyIen() : groupIen, 
	    		parentStudy.getPatientIdentifier(), parentSeries.getModality());
	    
	    image.setImageUid(imageUid);
	    image.setImageNumber(imageNumber);
	    updateImageWithImageLine(image, imageInfoLine);
	    image.setConsolidatedSiteNumber(consolidatedSiteNumber);
	    if(errorMessage != null)
	    {
	    	image.setErrorMessage(errorMessage);
	    }
	    if(parentStudy.getFirstImage() == null)
    	{
    		parentStudy.setFirstImage(image);
    		parentStudy.setFirstImageIen(image.getIen());
    	}	   
	    
		return image;
    }
	
	private static void updateImageWithImageLine(Image image, String imageLine)
	throws VistaParsingException
	{
		if((image != null) && (imageLine != null))
		{
			VistaImage vistaImage = VistaImage.create(imageLine);
			image.setFullFilename(vistaImage.getFullFilename());
			image.setAbsFilename(vistaImage.getAbsFilename());
			image.setDescription(vistaImage.getDescription());
			image.setImgType(vistaImage.getImgType());
			image.setProcedure(vistaImage.getProcedure());
			image.setProcedureDate(vistaImage.getProcedureDate());
			image.setAbsLocation(vistaImage.getAbsLocation());
			image.setFullLocation(vistaImage.getFullLocation());
			image.setDicomSequenceNumberForDisplay(vistaImage.getDicomSequenceNumberForDisplay());
			image.setDicomImageNumberForDisplay(vistaImage.getDicomImageNumberForDisplay());
			image.setSiteAbbr(vistaImage.getSiteAbbr());
			image.setBigFilename(vistaImage.getBigFilename());
			image.setPatientDFN(vistaImage.getPatientDFN());
			image.setPatientName(vistaImage.getPatientName());
			image.setImageClass(vistaImage.getImageClass());
			image.setDocumentDate(vistaImage.getDocumentDate());
			image.setCaptureDate(vistaImage.getCaptureDate());
			image.setSensitive(vistaImage.isSensitive());
			image.setImageStatus(vistaImage.getImageStatus());
			image.setImageViewStatus(vistaImage.getImageViewStatus());
			image.setAssociatedNoteResulted(vistaImage.getAssociatedNoteResulted());
			image.setImagePackage(vistaImage.getImagePackage());
			image.setImageHasAnnotations(vistaImage.isImageHasAnnotations());
			image.setImageAnnotationStatus(vistaImage.getImageAnnotationStatus());
			image.setImageAnnotationStatusDescription(vistaImage.getImageAnnotationStatusDescription());
			
			
			//image.setIen(Base32ConversionUtility.base32Encode(pieces[1]));
			/*
			String [] pieces = StringUtils.Split(imageLine, StringUtils.CARET);
			image.setFullFilename(pieces[2]);
			image.setAbsFilename(pieces[3]);
			image.setDescription(pieces[4]);
			image.setImgType(Integer.parseInt(pieces[6]));
			image.setProcedure(pieces[7]);
			image.setProcedureDate(VistaTranslatorUtility.convertVistaDatetoDate(pieces[8]));
			image.setAbsLocation(pieces[10]);
	        image.setFullLocation(pieces[11]);
	        image.setDicomSequenceNumberForDisplay(pieces[12]);
	        image.setDicomImageNumberForDisplay(pieces[13]);
	        image.setSiteAbbr(pieces[16]);
	        image.setBigFilename(pieces[18]);
	        image.setPatientDFN(pieces[19]);
	        image.setPatientName(pieces[20]);
	        image.setImageClass(pieces[21]);*/
		}			
	}
	
	/**
     * Converts rpc data from VistA into Image object
     * 
     * @param imageString
     * @return Image object representing VistA string data or null if the String cannot be used to
     * 			build a valid Image instance
	 * @throws URNFormatException 
	 * @throws VistaParsingException 
     */
    private static Image vistaImageStringToImage(String imageString, String originatingSiteId, 
    		String studyId, PatientIdentifier patientIdentifier) 
    throws URNFormatException, VistaParsingException 
    {
        if(imageString == null || imageString.length() == 0)
        	return null;
        
        VistaImage vistaImage = VistaImage.create(imageString);
        return transform(originatingSiteId, studyId, patientIdentifier, vistaImage);
    }
    
    /**
     * @param imageList
     * @return List of image objects based on imageList information
     * @throws URNFormatException 
     * @throws VistaParsingException 
     */
    public static List<Image> VistaImageStringListToImageList(String imageList, String originatingSiteId, 
    		String studyId, PatientIdentifier patientIdentifier) 
    throws URNFormatException
    {
    	List<Image> images = new LinkedList<Image>();
    	if((imageList == null) || (imageList.equals(""))) {
    		return images;
    	}
    	String []lines = StringUtils.Split(imageList, StringUtils.NEW_LINE);
    	for(int i = 1; i < lines.length; i++) 
    	{
    		try
    		{
	    		Image image = vistaImageStringToImage(lines[i], originatingSiteId, studyId, patientIdentifier);
	    		images.add(image);
    		}
    		catch(VistaParsingException vpX)
    		{
    			// if there is a parsing exception, just throw away this image, not the entire list
    			logger.error("VistParsingException parsing image line '" + lines[i] + "'.", vpX);
    		}
    	}
    	return images;
    }
    
    /**
     * 
     * @param networkLocationString
     * @param site
     * @return
     */
    public static List<NetworkLocation> VistaNetworkLocationsToNetworkLocationsList(String networkLocationString, 
    		Site site, SiteParameterCredentials siteParameterCredentials)
    {
    	List<NetworkLocation> networkLocations = new ArrayList<NetworkLocation>();
    	
    	String[] shares = StringUtils.Split(networkLocationString, StringUtils.NEW_LINE);
		
		// skip first element (response message)
		for(int i = 1; i < shares.length; i++) {
			NetworkLocation netLoc = VistaNetworkLocationStringToNetworkLocation(shares[i], site, 
					siteParameterCredentials);
			networkLocations.add(netLoc);
		}
		return networkLocations;
    }
    
    /**
     * 
     * @param networkLocationString
     * @param site
     * @return
     */
    public static NetworkLocation VistaNetworkLocationStringToNetworkLocation(String networkLocationString, 
    		Site site, SiteParameterCredentials siteParameterCredentials)
    {
    	String path =  StringUtils.Piece(networkLocationString, StringUtils.CARET, 2);
		String user = StringUtils.Piece(networkLocationString, StringUtils.CARET, 5);
		if(user == null) 
			user = "";
		String pass = StringUtils.Piece(networkLocationString, StringUtils.CARET, 6);
		if(pass == null)
		{
			pass = "";
		}
		else if(pass.length() > 0)
		{
			try
			{
				pass = EncryptionUtils.decrypt(pass);
			}
			catch(Exception ex)
			{
				System.out.println("Exception decrypting password for share [" + path + "], password is [" + pass + "]");
				ex.printStackTrace();			
			}			
		}
		if(siteParameterCredentials != null) 
		{ 
			if(pass.equals("")) {
				pass = siteParameterCredentials.getPassword();
			}
			if(user.equals("")) {
				user = siteParameterCredentials.getUsername();
			}
		}
    	return new NetworkLocation(path, user, pass, site.getSiteNumber());
    }
    
    public static SiteParameterCredentials VistaImagingSiteParametersStringToSiteCredentials(String imagingSiteParametersString)
    {    	
		String[] parameters = StringUtils.Split(imagingSiteParametersString, StringUtils.NEW_LINE);			
		String username = StringUtils.Piece(parameters[2], StringUtils.CARET, 1);
		String pass = parameters[2];			
		pass = pass.substring(username.length() + 1);
		pass = pass.substring(0, pass.length() - 1); // remove trailing \n character
		if((pass != null) && (pass.length() > 0))
		{
			pass = EncryptionUtils.decrypt(pass);
		}
		return new SiteParameterCredentials(username, pass);
    }
    
    public static List<String> convertVistaVersionsToVersionNumbers(String magVersions)
    {
    	String [] versions = StringUtils.Split(magVersions, StringUtils.NEW_LINE);
    	List<String> magVersionList = new ArrayList<String>(versions.length);
		for(int i = 0; i < versions.length; i++) {		
			String [] versionDetails = StringUtils.Split(versions[i], StringUtils.CARET);
			if(versionDetails[0] != null)
			{
				String []versionPieces = StringUtils.Split(versionDetails[0], StringUtils.SPACE);
				if(versionPieces[0] != null)
					magVersionList.add(versionPieces[0]);
			}
		}
		return magVersionList;
    }
    
    /**
     * Converts data received from a HIS update VistA query into a key-value hashmap.
     * Input data looks like:
     *  
     *  21 data fields returned.
0008,0018^1.2.840.113754.660.20080219103530278.1
0008,0020^20030509
0008,0050^050903-170
0008,0090^IMAGPROVIDERONETWOEIGHT,ONETWOEIGHT\1A
0008,1030^NM
0008,1050^IMAGPROVIDERONETWOEIGHT,ONETWOEIGHT
0010,0010^IMAGPATIENT720,720
0010,0020^000000720
0010,0030^19320000
0010,0032^000000
0010,0040^M
0010,1000^1006170580V294705
0010,1040^430 GRISWOLD DR^^^SALT LAKE CITY^UTAH^33461
0020,000D^1.3.46.670589.8.2021400214009.2001.1.170.8
0020,000E^1.2.840.114234.1.21.1.2155594979.20030206.1545.2
0032,1020^660
0032,1032^IMAGPROVIDERONETWOSIX,ONETWOSIX
0032,1060^RADIOGRAPHIC PROCEDURE
0032,1064 0008,0100^76499
0032,1064 0008,0102^C4
0032,1064 0008,0104^RADIOGRAPHIC PROCEDURE
     * 
     * @param vistaHisUpdate
     * @return
     */
    public static HashMap<String, String> convertVistaHisUpdateToHashmap(String vistaHisUpdate)
    {
    	HashMap<String, String> hisUpdate = new HashMap<String, String>();
    	String [] fields = StringUtils.Split(vistaHisUpdate, StringUtils.NEW_LINE);		
		for(int i = 1; i < fields.length; i++) {
			String fullField = fields[i].trim();
			String tagkey = StringUtils.MagPiece(fullField, StringUtils.CARET, 1);
			String tagval = StringUtils.MagPieceCount(fullField, StringUtils.CARET, 1, 0);
			hisUpdate.put(tagkey, tagval);
		}    	
    	return hisUpdate;
    }
    
    /**
     * 
     * @param vistaResponse
     * @param study
     * @return
     * @throws VistaParsingException 
     */
    public static SortedSet<VistaImage> createImageGroupFromImageLines(String vistaResponse, Study study) 
    throws VistaParsingException
    {
    	SortedSet<VistaImage> images = new TreeSet<VistaImage>();
    	String[] lines = StringUtils.Split(vistaResponse, StringUtils.NEW_LINE);
    	for(int i = 1; i < lines.length; i++)
    	{
    		String imageLine = lines[i];
    		VistaImage image = VistaImage.create(imageLine);
    		images.add(image);    		
    	}
    	return images;    	
    }
    
    
    
    /**
     * 
     * @param studyList
     * @param patientIcn
     * @param siteNumber
     * @return
     * @throws VistaParsingException
     */
    public static List<Image> createImagesForFirstImagesFromVistaGroupList(
    	String studyList, 
    	PatientIdentifier patientIdentifier, 
    	String siteNumber)
    throws VistaParsingException
    {
    	List<Image> images = new ArrayList<Image>();
    	
    	String headerLine = "";
    	if(studyList.charAt(0) == '1') 
    	{			
			String[] lines = StringUtils.Split(studyList, StringUtils.NEW_LINE);
			// the first two lines of the response contain the response status and the metadata, respectively
			if(lines.length <= 0)
				throw new VistaParsingException("Study list contains no status, meta-data or data.");
			if(lines.length == 1)
				throw new VistaParsingException("Study list contains no meta-data or data.");
			if(lines.length == 2)
			{
				logger.info("Study list contains no data.");
				return images;
			}
			
			// parse the response status line and retain the ???
			String rpcResponseLine = lines[0].trim();
			String response = StringUtils.MagPiece(rpcResponseLine, StringUtils.CARET, 2);
			
			// the headerLine is the metadata, describes the format of the study results
			// save it and pass to the method that actually parses the study lines
			headerLine = lines[1];
			
			// for each remaining line in the response, parse a Study instance and add it
			// to our list
			String studyIen = null;
			for(int j = 2; j < lines.length; j++) 
			{
				String imagePiece = StringUtils.MagPiece(lines[j], StringUtils.STICK, 2);
				// this next test will deal with trailing blank lines
				if(imagePiece != null && imagePiece.length() > 0)
				{
					String imageLine = "B1^" + imagePiece;
					VistaImage vistaImage = VistaImage.create(imageLine);
					//String imageIen = extractImageIenFromVistaImageString(imageLine);
					studyIen = studyIen == null ? vistaImage.getIen() : studyIen;
					
					Image image;
					try
					{
						image = transform(siteNumber, studyIen, patientIdentifier, vistaImage);
						//image = Image.create(siteNumber, imageIen, studyIen, patientIcn, null);
						//updateImageWithImageLine(image, imageLine);
						images.add(image);
					}
					catch (URNFormatException x)
					{
						logger.error("URNFormatException parsing line '" + imageLine + "'.", x);
					}
				}
			}
		}
    	return images;
    }
    
    /**
     * Extract the image IEN from a VistA Imaging result image string.
     * The image IEN is needed to build an ImageURN, which is required to create
     * an Image instance.  This method provides a way to get the image IEN before parsing the
     * entire String.
     * 
     * @param imageString
     * @return
     */
    private static String extractImageIenFromVistaImageString(String imageString)
    {
        if( imageString == null || imageString.equals("") )
        	return null;
        
        String[] pieces = StringUtils.Split(imageString, StringUtils.CARET);
    	// CTB 27Nov2009
        //String imageIen = Base32ConversionUtility.base32Encode(pieces[1]);
        String imageIen = pieces[1];

        return imageIen;
    }
    
    /**
     * Converts the RPC response into a list of patient objects
     * @param findPatientResults
     * @return
     */
    public static List<VistaPatient> convertFindPatientResultsToVistaPatient(String findPatientResults)
    {
    	List<VistaPatient> vistaPatients = new ArrayList<VistaPatient>();
    	String[] lines = StringUtils.Split(findPatientResults, StringUtils.NEW_LINE);
    	if(lines.length == 1)
    	{
    		logger.error("Error finding patients, '" + findPatientResults + "'");
    	}
    	else 
    	{
    		for(int i = 1; i < lines.length; i++)    		
    		{
    			String[] patientPieces = StringUtils.Split(lines[i], StringUtils.CARET);
    			String rawPatientInfo = patientPieces[0];
    			boolean sensitive = false;
    			if(rawPatientInfo != null && rawPatientInfo.contains(" *SENSITIVE* "))
    			{
    				sensitive = true;
    			}
    			String dfn = patientPieces[1];
    			if(dfn != null)
    				dfn = dfn.trim();
    			vistaPatients.add(new VistaPatient(dfn, sensitive));
    		}
    	}
    	return vistaPatients;
    }
    
    /**
     * Converts a date string from a patient info query into a Date object
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date convertPatientDetailsDateStringToDate(String dateString)
    throws ParseException
    {
    	if (dateString.length()==10)
    	{
    		// We have a 4-digit year. 
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        	return sdf.parse(dateString);
    	}
    	else
    	{
    		// Must be a 2-digit year
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        	return sdf.parse(dateString);
    	}
    }
    
    /**
     * Converts a patient Info response from VistA into a Patient object
     * @param patientInfoResults
     * @return
     * @throws ParseException
     */
    public static Patient convertPatientInfoResultsToPatient(String patientInfoResults, boolean sensitive)
    throws ParseException
    {
    	String [] pieces = StringUtils.Split(patientInfoResults, StringUtils.CARET);
    	String dfn = pieces[1];
    	String patientName = pieces[2];
    	String patientSex = pieces[3];
    	String patientDob = pieces[4];
    	String ssn = formatSsn(pieces[5]);
    	String veteranStatus = pieces[7];
    	String patientIcn = pieces[10]; 
    	
    	return new Patient(patientName,patientIcn, veteranStatus, 
    			PatientSex.valueOfPatientSex(patientSex), 
    			convertPatientDetailsDateStringToDate(patientDob),
    			ssn, dfn, sensitive);
    }
    
    private static String formatSsn(String ssn) 
    {
    	ssn += "";
    	if (ssn.length() >= 9)
    	{
    		String part1 = ssn.substring(0, 3);
    		String part2 = ssn.substring(3,5);
    		String part3 = ssn.substring(5);
    		
    		ssn = part1 + "-" + part2 + "-" + part3;
    	}
    	
    	return ssn;
	}

	/**
     * Extracts the first photo Id filename from the RPC response from VistA
     * @param vistaResult
     * @return
     */
    public static String extractPatientPhotoIdFilenameFromVistaResult(String vistaResult)
    {
    	if(vistaResult == null)
    		return null;
    	String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
    	if(lines.length < 2)
    		return null;
    	
    	String photoLine = lines[1];
    	String [] linePieces = StringUtils.Split(photoLine, StringUtils.CARET);
    	return linePieces[2];
    }
    
    /**
     * Creates a list of images from an RPC response from VistA for a list of images associated with a TIU note
     * @param vistaResult
     * @param patientIcn
     * @param site
     * @return
     * @throws VistaParsingException 
     */
    public static List<Image> createImageListFromTiuNoteResponse(
    	String vistaResult,
    	String studyId,
    	PatientIdentifier patientIdentifier, 
    	Site site) 
    throws VistaParsingException
    {
    	return extractImageListFromVistaResult(vistaResult, studyId, patientIdentifier, site);
    }
    
    /**
     * Creates a list of images from an RPC response from VistA for a list of images associated with a Radiology consult
     * @param vistaResult
     * @param patientIcn
     * @param site
     * @return
     * @throws VistaParsingException 
     */
    public static List<Image> createImageListFromRadExamResponse(
    	String vistaResult, 
    	String studyId,
    	PatientIdentifier patientIdentifier, 
    	Site site) 
    throws VistaParsingException
    {
    	return extractImageListFromVistaResult(vistaResult, studyId, patientIdentifier, site);
    }

    /**
     * 
     * @param vistaResult
     * @param patientIcn
     * @param site
     * @return
     * @throws VistaParsingException 
     */
	private static List<Image> extractImageListFromVistaResult(
		String vistaResult, 
		String studyId, 
		PatientIdentifier patientIdentifier, 
		Site site) 
	throws VistaParsingException
	{
		if(vistaResult == null)
    		return null;
		
		List<Image> images = new ArrayList<Image>();
    	
		List<VistaImage> vistaImages = extractVistaImageListFromVistaResult(vistaResult);
		for(VistaImage vistaImage : vistaImages)
		{
			Image image;
			try
			{
				image = transform(site.getSiteNumber(), studyId, patientIdentifier, vistaImage);
	    		images.add(image);
			}
			catch (URNFormatException x)
			{
				logger.error("Exception parsing VistaImage '" + vistaImage.toString() + "'.", x);
			} 
		}
		
    	return images;
	}
    

    /**
     * 
     * @param vistaResult
     * @param patientIcn
     * @param site
     * @return
     * @throws VistaParsingException 
     */
	public static List<VistaImage> extractVistaImageListFromVistaResult(String vistaResult) 
	throws VistaParsingException
	{
		if(vistaResult == null)
    		return null;
    	List<VistaImage> images = new ArrayList<VistaImage>();
    	
    	String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
    	for(int i = 1; i < lines.length; i++)
    		images.add( VistaImage.create(lines[i]) );
    	
    	return images;
	}
	
    /**
     * Extract the group parent IEN from an RPC response for the 0 node of an image
     * @param vistaResult
     * @return The IEN of the parent group image or null if this image represents a group image
     */
    public static String extractGroupIenFromNode0Response(String vistaResult)
    {
    	String [] pieces = StringUtils.Split(vistaResult, StringUtils.CARET);
    	// if there is no 10th piece then this is a group image and does not have a parent
    	if(pieces.length < 10)
    		return null;
    	return pieces[9]; // 10th piece of result is group IEN if passed image data
    	//return StringUtils.MagPiece(vistaResult, StringUtils.CARET, 10);
    }
    
    public static List<String> translateUserKeys(String vistaResult)
    {
    	List<String> result = new ArrayList<String>();
    	String[] keys = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		
		// trim whitespace, including CR and/or LF characters
		if(keys != null)
		{
			for(int index=0; index<keys.length; ++index)
			{
				result.add(keys[index].trim());
			}
		}

		return result;
    }

	public static List<Division> translateDivisions(String vistaResult) 
	{
    	List<Division> result = new ArrayList<Division>();
    	String[] divisions = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		
		// trim whitespace, including CR and/or LF characters
		if(divisions != null)
		{
			for(int i=1; i<divisions.length; i++)
			{
				String[] fields = StringUtils.Split(divisions[i], StringUtils.CARET);
				result.add(new Division(fields[0].trim(), fields[1].trim(), fields[2].trim()));
			}
		}

		return result;
	}
	
	public static User translateUser(String vistaResult)
	{
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		String duz = lines[0].trim();
		String name = lines[1].trim();
		//String standardName = lines[2].trim();
		//String division = lines[3].trim();
		String title = lines[4].trim();
		String service = lines[5].trim();		
		/*
		String language = lines[6].trim();
		String dtime = lines[7].trim();
		String vpid = "";
		if(lines.length > 8)
			vpid = lines[8].trim();
		*/
		return new VistaUser(duz, name, title, service);
		
	}
	
	public static List<String> convertTreatingSiteListToSiteNumbers(String vistaResult, 
			boolean includeTrailingCharactersForSite200)
	{
		List<String> result = new ArrayList<String>();
		StringBuilder initialSiteList = new StringBuilder();
		StringBuilder convertedSiteList = new StringBuilder();
		String prefix = "";
		if(vistaResult != null)
		{
			String [] lines = StringUtils.Split(vistaResult.trim(), StringUtils.NEW_LINE);
			if(lines.length <= 0)
			{
				logger.warn("Got empty string results from VistA for treating sites, this shouldn't happen!");
			}
			else if(lines.length > 0)
			{
				String headerLine = lines[0].trim();
				if(headerLine.startsWith("0"))
				{
					logger.info("Patient has no treating sites, " + vistaResult);
				}
				else
				{
					logger.debug("Treating sites header line, " + headerLine);
					for(int i = 1; i < lines.length; i++)
					{
						String [] pieces = StringUtils.Split(lines[i], StringUtils.CARET);
						
						String initialSiteNumber = pieces[0].trim();
						String convertedSiteNumber = extractUnnecessarySiteNumberCharacters(initialSiteNumber, 
								includeTrailingCharactersForSite200);
						initialSiteList.append(prefix);
						initialSiteList.append(initialSiteNumber);
						convertedSiteList.append(prefix);
						convertedSiteList.append(convertedSiteNumber);
						result.add(convertedSiteNumber);
						prefix = ", ";
					}					
				}
			}
		}		
		logger.info("Converted site list '" + initialSiteList.toString() + "' to '" + convertedSiteList.toString() + "'.");
		// put into a hashset to exclude duplicate entries
		return new ArrayList<String>(new HashSet<String>(result));
	}
	
	/**
	 * This method looks at the site number and extracts unnecessary characters. If the site
	 * number starts with letters, they are excluded.  Any letters after numbers are excluded and
	 * any numbers following that are also excluded.
	 * 
	 * ex: ABC200T1 translates to 200
	 * @param rawSiteNumber
	 * @return
	 */
	private static String extractUnnecessarySiteNumberCharacters(String rawSiteNumber, 
			boolean includeTrailingCharactersForSite200)
	{
		StringBuilder result = null;
		for(int i = 0; i < rawSiteNumber.length(); i++)
		{
			char ch = rawSiteNumber.charAt(i);
			int c = (int)ch;
			// check if the character is a letter
			if((c < 48) || (c > 57))
			{
				if(result != null)
				{
					// we have already added some numbers to the result so we are looking at trailing characters
					String sNumber = result.toString();
					// if the current site number is 200
					if(sNumber.startsWith(ExchangeUtil.getDodSiteNumber()))
					{
						// if we want to include trailing characters for 200
						if(includeTrailingCharactersForSite200)
							result.append(ch);	// add the character
						else
							return result.toString(); // return the value
					}
					else
					{
						// not site 200 so just return the value
						return result.toString();
					}
					
				}
			}
			else
			{
				if(result == null)
				{
					result = new StringBuilder();
				}
				result.append(ch);
			}
		}
		if(result == null)
			return "";
		return result.toString();
		
		/*
		String newSiteNumber = rawSiteNumber.replaceAll("[A-Za-z]", "");
		newSiteNumber = newSiteNumber.replaceAll("&", "");
		return newSiteNumber;
		*/			
	}	
	
	/**
	 * 
	 		1^Ok
			13^All images were removed from the group^D^^13
			6^Authorized release of medical records or health information (ROI)^CP^^6
			2^Clinical care for other VA patients^CP^^2
			1^Clinical care for the patient whose images are being downloaded^CP^^1
			7^Corrupt image^D^^7
			4^For approved teaching purposes by VA staff^CP^^4
			5^For use in approved VA publications^CP^^5
			3^For use in approved research by VA staff^CP^^3
			14^HIMS document correction^DS^^14
			12^Image is incorrectly included in an image group^S^^12
			8^Low quality image^DS^^8
			9^Wrong case/exam/accession number^DS^^9
			10^Wrong note title^D^^10
			11^Wrong patient^D^^11
	 * 
	 * @param site
	 * @param rtn
	 * @return
	 * @throws MethodException
	 */
	public static List<ImageAccessReason> translateImageAccessReasons(Site site, String rtn)
	throws MethodException
	{
		if(rtn.startsWith("0"))
		{
			throw new MethodException("Exception retrieving the image access reason list, " + rtn);
		}
		
		RoutingToken routingToken = site.createRoutingToken();
		
		List<ImageAccessReason> reasons = new ArrayList<ImageAccessReason>();
		String [] lines = StringUtils.Split(rtn, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			String [] pieces = StringUtils.Split(lines[i].trim(), StringUtils.CARET);			
			int reasonCode = Integer.parseInt(pieces[0]);
			String description = pieces[1];
			String types = pieces[2];
			//String inactivationDate = pieces[3]; // not going to be used
			String globalCode = pieces[4];
			
			reasons.add(new ImageAccessReason(routingToken, reasonCode, description, 
					translateReasons(types), globalCode));
			
		}
		return reasons;
	}
	
	private static List<ImageAccessReasonType> translateReasons(String types)
	{
		List<ImageAccessReasonType> result = new ArrayList<ImageAccessReasonType>();
		if(types != null)
		{
			char [] charArray = types.toCharArray();
			
			for(int i = 0; i < charArray.length; i++)
			{				
				String reasonCode = String.valueOf(charArray[i]);
				ImageAccessReasonType reasonType =
					ImageAccessReasonType.getFromCode(reasonCode);
				if(reasonType == null)
				{
					logger.warn("Could not find an ImageAccessReasonType for code '" + reasonCode + "'.");
				}
				else
				{
					result.add(reasonType);
				}
			}
		}
		
		
		return result;
	}
	
	public static ElectronicSignatureResult translateElectronicSignature(String rtn)
	{
		String [] pieces = StringUtils.Split(rtn.trim(), StringUtils.CARET);
		if("0".equals(pieces[0]))
		{
			TransactionContextFactory.get().addDebugInformation("verifyElectronicSignature() failed, " + rtn);
			logger.error("Error verifying e-signature, " + rtn);
			return new ElectronicSignatureResult(false, pieces[1]);
		}
		else
		{
			return new ElectronicSignatureResult(true, pieces[1]);
		}
	}
	
	public static List<HealthSummaryType> translateHealthSummaries(String vistaResult, Site site)
	throws MethodException
	{
		List<HealthSummaryType> result = new ArrayList<HealthSummaryType>();
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			String [] pieces = StringUtils.Split(lines[i].trim(), StringUtils.CARET);
			String name = pieces[0];
			String ien = pieces[1];
			try
			{
				HealthSummaryURN healthSummaryUrn = HealthSummaryURN.create(site.getSiteNumber(), ien);
				result.add(new HealthSummaryType(healthSummaryUrn, name));
			}
			catch(URNFormatException urnfX)
			{
				throw new MethodException(urnfX);
			}
		}
		return result;
	}
	
	public static String translateHealthSummary(String vistaResult)
	throws MethodException
	{
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException(vistaResult);
		}
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		StringBuilder result = new StringBuilder();
		for(int i = 1; i < lines.length; i++)
		{
			result.append(lines[i].trim() + StringUtils.NEW_LINE);
		}
		return result.toString();
	}

	public static ApplicationTimeoutParameters translateApplicationTimeoutParameters(String vistaResult)
	throws MethodException
	{
		// Initialize the timeout to 0
		int timeoutInSeconds = 0;

		// try to parse the returned value into an integer. If successful,
		// update the timeoutInSeconds variable
		try 
		{
			int timeoutInMinutes = Integer.parseInt(vistaResult);
			timeoutInSeconds = timeoutInMinutes * 60;
		} catch (Exception e) {
			// Log the exception
			logger.info("Unable to parse importer application timeout value from RPC result: " + vistaResult, e);
		}

		// Return the instance. The timeout value will either be what was
		// returned from VistA, or 0
		return new ApplicationTimeoutParameters(timeoutInSeconds);
	}
}
