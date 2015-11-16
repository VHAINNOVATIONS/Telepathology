package gov.va.med.imaging.protocol.vista;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.url.vista.EncryptionUtils;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.image.VistaRadSiteCredentials;

import org.apache.log4j.Logger;

/**
 * 
 * @author vhaiswlouthj
 * 
 */

public class VistaImagingVistaRadTranslator
{
	private static Logger logger = Logger.getLogger(VistaImagingVistaRadTranslator.class);
	
	/**
	 * Convert output from VistA from the MAGJ RADCASEIMAGES rpc into a map of ExamImage objects 
	 * 
47^0~Images for Case #020602-98|1011^6979793.8675^1^86|IMAGPATIENT1011,1011^020602-98^CT ABDOMEN W&W/O CONT^FEB  6,2002^13:24^CT^000-00-1011^3^0^^I^|1^-1^660^20021127
B2^3528^\\isw-werfelj-lt\image1$\DM00\00\00\00\35\DM000000003528.TGA^^
B2^3529^\\isw-werfelj-lt\image1$\DM00\00\00\00\35\DM000000003529.TGA^^
	 * 
	 * @param response
	 * @param studyUrn
	 * @param site
	 * @return
	 * @throws URNFormatException
	 */
	public static ExamImages translateExamImagesIntoExamsMap(String response, 
		String examId, String patientIcn, Site site)
	throws URNFormatException
	{
		
		String [] lines = StringUtils.Split(response, StringUtils.NEW_LINE);		
		if((lines == null) && (lines.length == 0))
		{
			return new ExamImages("", false);
		}
		ExamImages images = new ExamImages(lines[0], false);
		if(lines[0].startsWith("0"))
		{
			logger.info("No images available for examId '" + examId + "', response [" + lines[0] + "]");			
			return images;
		}		
		String [] groupPieces = StringUtils.Split(lines[0], StringUtils.STICK);
		String patientName = StringUtils.MagPiece(groupPieces[2], StringUtils.CARET, 1);				
		if(lines.length > 1)
		{
			for(int i = 1; i < lines.length; i++)
			{
				ExamImage image = translateExamImageLineIntoExamImage(lines[i], examId, 
					patientIcn, site, patientName);
				images.add(image);
			}
		}		
		return images;
	}
	
	/**
	 * 
B2^3529^\\isw-werfelj-lt\image1$\DM00\00\00\00\35\DM000000003529.TGA^^
	 * 
	 * 
	 * @param line
	 * @param studyUrn
	 * @param site
	 * @return
	 */
	private static ExamImage translateExamImageLineIntoExamImage(String line, String examId, String patientIcn, 
			Site site, String patientName)
	throws URNFormatException
	{
    	// CTB 27Nov2009
		//String imageId = Base32ConversionUtility.base32Encode(StringUtils.MagPiece(line, StringUtils.CARET, 2));
		String imageId = StringUtils.MagPiece(line, StringUtils.CARET, 2);
		String imagePath = StringUtils.MagPiece(line, StringUtils.CARET, 3);
		ExamImage image = ExamImage.create(site.getSiteNumber(),
				imageId, examId, patientIcn);		
		image.setDiagnosticFilePath(imagePath);
		image.setPatientName(patientName);
		return image;
	}
	
	/**
	 * Convert output from VistA from the MAGJ PT ALL EXAMS rpc into a map of ExamImage objects
	 * 
26^1~Radiology Exams for: IMAGPATIENT1011,1011 (000-00-1011) -- ALL exams are listed.|0
^Day/Case~S3~1^Lock~~2^Procedure~~6^Modifier~~25^Image Date/Time~S1~7^Status~~8^# Img~S2~9^Onl~~10^RC~~12^Site~~23^Mod~~15^Interp By~~20^Imaging Loc~~11^CPT~~27
^040105-174^^CT ORBIT P FOS OR TEMP BON^^10/13/2004@13:31:27^WAITING FOR EXAM^25^Y^^^CT^^TD-RAD^70482^|1011^6949598.9048^1^191||W^^^CT^70482^0^191^0^^
^040105-268^^CT CERVICAL SPINE W/O CONT^^02/11/2004@15:12:28^WAITING FOR EXAM^888^Y^^^CT^^TD-RAD^72125^|1011^6949598.9048^2^202||W^^^CT^72125^0^202^0^^
^040105-231^^MAGNETIC IMAGE,LUMBAR SPIN^^08/11/2006@14:15:12^EXAMINED^172^Y^^^MR^^TD-RAD^72148^|1011^6949598.9048^3^206||E^^^MR^72148^1^206^0^^

	 * 
	 * @param response
	 * @return
	 */
	public static List<Exam> translateExamsResponse(String response, Site site, String patientIcn)
	throws MethodException
	{
		List<Exam> exams = new ArrayList<Exam>();
		String [] lines = StringUtils.Split(response, StringUtils.NEW_LINE);
		// if only 1 line then didn't get any exams for patient
		if(lines.length > 1)
		{
			String headerLine1 = lines[0];
			String headerLine2 = lines[1];			
			for(int i = 2; i < lines.length; i++)
			{
				Exam exam = translateExamLineIntoExam(lines[i], site, patientIcn);
				exam.setRawHeaderLine1(headerLine1);
				exam.setRawHeaderLine2(headerLine2);
				exams.add(exam);
			}
		}
		return exams;
	}
	
	private static Exam translateExamLineIntoExam(String examLine, Site site, String patientIcn)
	throws MethodException
	{
		String [] examPieces = StringUtils.Split(examLine, StringUtils.STICK);
    	// CTB 27Nov2009
		//String examId = Base32ConversionUtility.base32Encode(examPieces[1]);
		String examId = examPieces[1];
		Exam exam = null;
		try
		{
			exam = Exam.create(site.getSiteNumber(), examId, patientIcn);
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException("URNFormatException creating URN for exam from (" + site.getSiteNumber() + ", " + examId + ", " + patientIcn + "), " + urnfX.getMessage(), urnfX);
		}
		//exam.setRawOutput(examPieces[0]);
		// JMW 8/26/2009 - Sundar said they want the full line of information to parse in VRad
		exam.setRawOutput(examLine);
		//W^^^CT^72125^0^202^0^^
		String fieldPiece = examPieces[3];
		String modality = StringUtils.MagPiece(fieldPiece, StringUtils.CARET, 4);
		String cptCode = StringUtils.MagPiece(fieldPiece, StringUtils.CARET, 5);
		exam.setCptCode(cptCode);
		exam.setModality(modality);
		exam.setSiteAbbr(site.getSiteAbbr());
		exam.setSiteName(site.getSiteName());
		
		//TODO: don't really have the patient name... is it actually needed?
		exam.setPatientName("");
		//TODO: set exam status, what does W mean?
		//exam.setExamStatus(ExamStatus.)
		return exam;
	}
	
	/**
	 * Convert output from VistA from the MAGJ RADACTIVEEXAMS rpc into a map of ExamImage objects
	 * @param response
	 * @return
	 */
	/*
	public static ActiveExams translateActiveExamsResponse(String response, String siteNumber)
	{
		String [] lines = StringUtils.Split(response, StringUtils.NEW_LINE);
		
		String headerLine1 = "";
		String headerLine2 = "";
		if(lines.length > 1)
		{
			headerLine1 = lines[0];
		}
		if(lines.length > 2)
		{
			headerLine1 = lines[1];
		}
		ActiveExams result = new ActiveExams(siteNumber, headerLine1, headerLine2);
		for(int i = 2; i < lines.length; i++)
		{
			ActiveExam activeExam
		}
		return result;
	}*/
	/*
	public static String[] translateActiveExamListResponse(String response)
	{
		String [] lines = StringUtils.Split(response, StringUtils.NEW_LINE);
		return lines;
	}
	*/
	
	
	/**
	 * Convert output from VistA from the MAGJ CPTMATCH rpc into a map of ExamImage objects
	 * @param response
	 * @return
	 */
	public static String[] translateRelevantCptCodeResponse(String response)
	{
		String [] lines = StringUtils.Split(response, StringUtils.NEW_LINE);
		String stringCount = StringUtils.MagPiece(lines[0], StringUtils.CARET, 1);
		int resultCount = Integer.parseInt(stringCount);
		String [] cptCodes = new String[resultCount];
		if(resultCount == 0)
		{
			logger.info("0 CPT Codes found that match, response from server: " + lines[0]);		
		}
		else
		{
			logger.info("Found [" + resultCount + "] CPT Codes that match");
			for(int i = 1; i < lines.length; i++)
			{
				String cptCode = StringUtils.MagPiece(lines[i], StringUtils.CARET, 1);
				cptCodes[i - 1] = cptCode;
			}
		}
		return cptCodes;		
	}
	
	/**
	 * 
	 * 
1^1~Version Check OK. Server: 3.0.76.14 Client: 3.0.76.14|126^IMAGPROVIDERONETWOSIX,ONETWOSIX^SAF^1^3.0.76.14
vhamaster\vhaiswIU^'bAAj&&0+&^3^0
*KEYS
*END
	 * 
	 * 
	 * @param lines
	 * @return
	 */
	public static VistaRadSiteCredentials createSiteCredentialsFromResponse(String result, String siteNumber)
	{
		String [] lines = StringUtils.Split(result, StringUtils.NEW_LINE);
		if(lines.length >= 2)
		{
			String username = StringUtils.MagPiece(lines[1], StringUtils.CARET, 1);
			String password = StringUtils.MagPiece(lines[1], StringUtils.CARET, 2);
			
			if((password != null) && (password.length() > 0))
			{
				try
				{
					password = EncryptionUtils.decrypt(password);
				}
				catch(Exception ex)
				{
					password = "";
					System.out.println("Exception decrypting password for site [" + siteNumber + "].");
					ex.printStackTrace();					
				}
			}
			else
			{
				password = "";
			}			
			return new VistaRadSiteCredentials(siteNumber, username, password);
		}
		else
		{
			logger.warn("MAGJ USER2 rpc does not contain enough lines for site credentials");			
			return null;
		}
	}

}
