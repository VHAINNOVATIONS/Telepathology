/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 7, 2012
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
package gov.va.med.imaging.vistaimagingdatasource.pathology.translator;

import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseConsultation;
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.PathologyCaseSlide;
import gov.va.med.imaging.pathology.PathologyCaseSupplementalReport;
import gov.va.med.imaging.pathology.PathologyCaseTemplate;
import gov.va.med.imaging.pathology.PathologyCaseTemplateField;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCptCode;
import gov.va.med.imaging.pathology.PathologyCptCodeResult;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.PathologyFieldValue;
import gov.va.med.imaging.pathology.PathologyReadingSite;
import gov.va.med.imaging.pathology.PathologySaveCaseReportResult;
import gov.va.med.imaging.pathology.PathologySite;
import gov.va.med.imaging.pathology.PathologySnomedCode;
import gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeed;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus;
import gov.va.med.imaging.pathology.enums.PathologyField;
import gov.va.med.imaging.pathology.enums.PathologyReadingSiteType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingPathologyTranslator
{
	private final static Logger logger = Logger.getLogger(VistaImagingPathologyTranslator.class);
	
	public static String translateCaseNote(String vistaResult)
	throws MethodException
	{
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException("Error retrieving case note: " + vistaResult);
		}
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i < lines.length; i++)
		{
			sb.append(lines[i].trim() + "\n");
			/*
			//can't do a trim on the result from VistA because the last character of the line might be a space which is needed 
			// for the XML (might separate a field from an attribute)
			//sb.append(lines[i].trim());
			String line = lines[i];
			// VistA trails each line with a carriage return, want to get rid of that
			if(line.endsWith("\r"))
			{
				sb.append(line.substring(0, line.length() - 1));
			}
			else
			{			
				sb.append(line);
			}*/
		}
		return sb.toString();
	}
	
	
	/**
	 	0^3
		0^88001^Not found in #81
		1^88037^LIMITED AUTOPSY
		1^Visit #: 592
	 * @param vistaResult
	 * @throws MethodException
	 */
	public static List<PathologyCptCodeResult> translateSavingCptCode(String vistaResult)
	throws MethodException
	{
		// JMW 9/4/2012 TP - Duc requested that the RPC return the results of each CPT code added.  
		// Even if one of them failed, other CPT codes might have been set successfully 
		/*
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException("Error saving CPT codes: " + vistaResult);
		}*/
		List<PathologyCptCodeResult> result = new ArrayList<PathologyCptCodeResult>();
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			String [] pieces = StringUtils.Split(lines[i].trim(), StringUtils.CARET);
			boolean success = pieces[0].equals("1");
			if(!pieces[1].startsWith("Visit #"))
			{
				String cptCode = pieces[1];
				String description = pieces[2];
				result.add(new PathologyCptCodeResult(cptCode, success, description));
			}
		}
		return result;
	}
	
	public static String translateSavingSnomedCode(String vistaResult)
	throws MethodException
	{
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException("Error saving snomed codes: " + vistaResult);
		}
		
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		if(lines.length != 2)
			throw new MethodException("Result did not contain 2 lines, " + vistaResult);
		String secondLine = lines[1].trim();
		String [] pieces = StringUtils.Split(secondLine, StringUtils.CARET);
		// piece 1 is the ID of the element added
		return pieces[1];
	}
	
	public static void translateSavingCaseNote(String vistaResult)
	throws MethodException
	{
		checkSuccessfulResult(vistaResult, "Error saving case note");	
	}
	
	public static void translateDeletingSnomedCode(String vistaResult)
	throws MethodException
	{
		checkSuccessfulResult(vistaResult, "Error deleting snomed code");	
	}
	
	public static void translateSavingUserPreferences(String vistaResult)
	throws MethodException
	{
		checkSuccessfulResult(vistaResult, "Error saving pathology user preferences");	
	}
	
	public static void translateSavingCaseSupplementalReport(String vistaResult)
	throws MethodException
	{
		checkSuccessfulResult(vistaResult, "Error saving pathology case supplemental report");	
	}
	
	public static void translateSavingLockMinutes(String vistaResult)
	throws MethodException
	{
		checkSuccessfulResult(vistaResult, "Error saving lock minutes");	
	}
	
	private static void checkSuccessfulResult(String vistaResult, String errorMessage)
	throws MethodException
	{
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException(errorMessage + ": " + vistaResult);
		}	
	}
	
	/**
	 1^0^Case generated^SP 12 3
	 * @param vistaResult
	 * @param originalCaseUrn
	 * @return
	 */
	public static PathologyCaseURN translateCopyCase(String vistaResult, Site site, PatientIdentifier patientIdentifier)
	throws MethodException
	{
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException("Error copying case: " + vistaResult);
		}
		String [] pieces = StringUtils.Split(vistaResult, StringUtils.CARET);
		String newAccessionNumber = pieces[3];
		String [] accessionNumberPieces = StringUtils.Split(newAccessionNumber, StringUtils.SPACE);
		try
		{
			return PathologyCaseURN.create(site.getSiteNumber(), 
					accessionNumberPieces[0], accessionNumberPieces[1], accessionNumberPieces[2], patientIdentifier);
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException(urnfX);
		}
	}
	
	public static boolean translateCheckPendingConsultations(String vistaResult)
	throws MethodException
	{
		// 1^0^0 (No pending consultations) or 1^0^1 (Pending consultations) (Success) or 0^0^Error description (Failure)
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException("Error checking pending consultations: " + vistaResult);
		}
		String [] pieces = StringUtils.Split(vistaResult, StringUtils.CARET);
		if(pieces[2].trim().equals("0"))
			return false;
		return true;
			
	}
	
	public static List<String> translateUserKeys(String vistaResult)
	{
		List<String> result = new ArrayList<String>();
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		// skip first line, contains user info
		for(int i = 1; i < lines.length; i++)
		{
			result.add(lines[i].trim());
		}
		return result;
	}

	/**
	 * 
	1^4^CPT Code^CPT Description^Multiply Factor^Date/Time Entered^User^Visit List: 594;
	3789^Autopsy Single Organ^1^07/19/2012 13:57:52^IMAGPROVIDERONETWOSIX,ONETWOSIX
	284^+SP Specimen^1^07/19/2012 13:56^IMAGPROVIDERONETWOSIX,ONETWOSIX
	1451^Surgical Path Init Handling^1^07/19/2012 13:56^IMAGPROVIDERONETWOSIX,ONETWOSIX
	1452^Transcription File Search Retrieve^1^07/19/2012 13:56^IMAGPROVIDERONETWOSIX,ONETWOSIX
	 * 
	 * 
	 * @param vistaResult
	 * @return
	 * @throws MethodException
	 */
	public static List<PathologyCptCode> translateCptCodesResults(String vistaResult)
	throws MethodException
	{		
		if(vistaResult.startsWith("0"))
		{			
			throw new MethodException("Error getting CPT codes: " + vistaResult);			
		}
		List<PathologyCptCode> result = new ArrayList<PathologyCptCode>();
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			result.add(translateCptCodeLine(lines[i].trim()));
		}
		
		return result;		
	}

	private static PathologyCptCode translateCptCodeLine(String line)
	throws MethodException
	{
		String [] pieces = StringUtils.Split(line, StringUtils.CARET);
		String cptCode = pieces[0];
		String description = pieces[1];
		String multiplyFactorString = pieces[2];
		String dateString = pieces[3];
		String user = pieces[4];
		
		Integer multiplyFactor = null;
		
		if(multiplyFactorString.length() > 0)
		{
			try
			{
				multiplyFactor = Integer.parseInt(multiplyFactorString);
			}
			catch(NumberFormatException nfX)
			{
				throw new MethodException(nfX);
			}
		}
		
		Date dateEntered = parseDateString(dateString);
		
		return new PathologyCptCode(cptCode, description, multiplyFactor, dateEntered, user);			
	}
	
	public static List<PathologySnomedCode> translateSnomedResults(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("0"))
		{			
			throw new MethodException("Error getting CPT codes: " + vistaResult);			
		}
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		List<PathologySnomedCode> tissues = new ArrayList<PathologySnomedCode>();
		for(int i = 1; i < lines.length; i++)
		{
			tissues.add(translateSnomedLine(lines[i].trim()));
		}
		
		return tissues;
	}
	
	private static PathologySnomedCode translateSnomedLine(String snomedLine)
	throws MethodException
	{
		
		// old (no longer used)
		// 1|LIVER^1|MORPHOLOGY^HEPATOCELLULAR CARCINOMA		
		// 3|PROSTATE^1|MORPHOLOGY^XANTHOMA, VERRUCIFORM^ETIOLOGY: IODINE RADIOISOTOPE
		
		// new (7/25/2012)
		// 1|56^LIVER^|PROCEDURE^395^ENDOSCOPIC BRUSH BIOPSY
		// 1|56^LIVER^1|MORPHOLOGY^77^HEPATOCELLULAR CARCINOMA^ETIOLOGY^1176^RIO BRAVO VIRUS
		// 1|56^LIVER^2|MORPHOLOGY^2238^PLASMA MEMBRANE ALTERATION
		
		// newer (8/21/2012) (EM 00 1)
		// 1|56000^LIVER^1|PROCEDURE^1341^ENDOSCOPIC BRUSH BIOPSY
		// 1|56000^LIVER^1|MORPHOLOGY^81703^HEPATOCELLULAR CARCINOMA^1|ETIOLOGY^3878^RIO BRAVO VIRUS
		// 1|56000^LIVER^2|MORPHOLOGY^67000^PLASMA MEMBRANE ALTERATION
		
		String [] stickPieces = StringUtils.Split(snomedLine, StringUtils.STICK);
		String id = stickPieces[0];
		String [] tissuePieces = StringUtils.Split(stickPieces[1].trim(), StringUtils.CARET);
		String tissueCode = tissuePieces[0];
		String tissue = tissuePieces[1];
		String snomedId = null;
		if(tissuePieces.length >= 3)
			snomedId = tissuePieces[2];
		PathologyField field = null;
		String snomedValue = null;
		String snomedCode = null;
		String etiologySnomedValue = null;
		String etiologySnomedCode = null;
		String etiologyId = null;
		if(stickPieces.length >= 3)
		{
			String [] snomedPieces = StringUtils.Split(stickPieces[2].trim(), StringUtils.CARET);
			String fieldType = snomedPieces[0];
			field = PathologyField.valueOf(fieldType.toLowerCase());
			snomedCode = snomedPieces[1];
			snomedValue = snomedPieces[2];
			if(snomedPieces.length >= 4)
			{
				etiologyId = snomedPieces[3];
			}
		}
		if(stickPieces.length >= 4)
		{
			// there is an etiology value
			if(field != PathologyField.morphology)
				throw new MethodException("Tissue contains etiology but is not morphology, " + snomedLine);
			String [] etiologyPieces = StringUtils.Split(stickPieces[3].trim(), StringUtils.CARET);
			etiologySnomedCode = etiologyPieces[1];
			etiologySnomedValue = etiologyPieces[2]; 
		}
		
		if(snomedValue == null)
			return PathologySnomedCode.createTissue(id, tissueCode, tissue);
		if(field == PathologyField.morphology)
		{
			if(etiologySnomedValue != null)
			{
				return PathologySnomedCode.createMorphologySnomedCode(id, tissueCode, tissue, 
						snomedId, snomedCode, snomedValue, etiologyId, etiologySnomedCode, etiologySnomedValue);
			}
			else
			{
				return PathologySnomedCode.createMorphologySnomedCode(id, tissueCode, tissue, 
						snomedId, snomedCode, snomedValue);
			}
		}
		else
		{
			return PathologySnomedCode.createSnomedCode(id, tissueCode, tissue, snomedId, field, snomedCode, snomedValue);
		}
	}
	
	public static String translateUserPreferences(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("0"))
		{
			throw new MethodException("Error getting user preferences: " + vistaResult);
		}
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		StringBuilder sb = new StringBuilder();
		for(int i = 1; i < lines.length; i++)
		{
			//can't do a trim on the result from VistA because the last character of the line might be a space which is needed 
			// for the XML (might separate a field from an attribute)
			//sb.append(lines[i].trim());
			String line = lines[i];
			// VistA trails each line with a carriage return, want to get rid of that
			if(line.endsWith("\r"))
			{
				sb.append(line.substring(0, line.length() - 1));
			}
			else
			{			
				sb.append(line);
			}
		}
		return sb.toString();
	}
	
	public static int translateLockMinutes(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("0"))
		{
			throw new MethodException("Error getting lock minutes: " + vistaResult);
		}
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		if(lines.length <= 1)
			throw new MethodException("Error getting lock minutes: " + vistaResult);
		try
		{
			return Integer.parseInt(lines[1].trim());
		}
		catch(NumberFormatException nfX)
		{
			throw new MethodException(nfX);
		}
	}
	
	public static List<PathologySite> translateSites(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("0"))
		{
			throw new MethodException("Error getting pathology sites: " + vistaResult);
		}
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		List<PathologySite> sites = new ArrayList<PathologySite>();
		for(int i = 1; i < lines.length; i++)
		{
			sites.add(translateSite(lines[i].trim()));
		}
		return sites;		
	}
	
	private static PathologySite translateSite(String line)
	{
		String [] pieces = StringUtils.Split(line, StringUtils.CARET);
		
		return new PathologySite(pieces[0], pieces[1], pieces[2], pieces[3]);
	}
	
	public static List<PathologyFieldValue> translateFieldValues(String vistaResult, Site site, PathologyField field)
	throws MethodException
	{
		if(vistaResult.startsWith("1"))
		{
			List<PathologyFieldValue> result = new ArrayList<PathologyFieldValue>();
			String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
			for(int i = 1; i < lines.length; i++)
			{
				result.add(translateFieldValue(lines[i].trim(), site, field));
			}
			return result;
		}
		else
			throw new MethodException("Error retrieving field values, " + vistaResult);
	}
	
	private static PathologyFieldValue translateFieldValue(String line,
			Site site, PathologyField field)
	throws MethodException
	{
		String [] pieces = StringUtils.Split(line, StringUtils.CARET);
		String ien = pieces[0];
		String name = pieces[1];
		if(pieces.length >= 3)
		{
			String code = pieces[2];
			if(code != null && code.length() > 0)
				name = name + StringUtils.CARET + code;
		}
		
		try
		{
			PathologyFieldURN fieldUrn = PathologyFieldURN.create(site.getSiteNumber(), field, ien);		
			return new PathologyFieldValue(fieldUrn, name);
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException(urnfX);
		}
	}
	
	/*
	 	0^^Electronic Signature authorized
		-1^^ERROR: [MUMPS error]
		-2^^ERROR: Missing AP Section
		1^^Electronic Signature not enabled
		2^^Electronic signature not authorized: missing PROVIDER key
		3^^Electronic Signature not authorized: PROVIDER CLASS must include PHYSICIAN, or CYTOTECHNOLOGIST for CY sections only, or DENTIST for oral and maxillofacial pathology
		4^^Electronic Signature not authorized: PERSON CLASS is inactive or undefined
		5^^Electronic Signature not authorized: PERSON CLASS has expired
		6^^Electronic Signature not authorized: invalid PERSON CLASS

	 */
	public static PathologyElectronicSignatureNeed translateElectronicSignatureNeedResult(String vistaResult)
	throws MethodException
	{
		String [] pieces = StringUtils.Split(vistaResult, StringUtils.CARET);
		
		String statusCode = pieces[0].trim();
		String msg = pieces[2].trim();
		
		if(statusCode.startsWith("-"))
		{
			throw new MethodException(msg);
		}
		
		if(statusCode.startsWith("0"))
			return new PathologyElectronicSignatureNeed(PathologyElectronicSignatureNeedStatus.authorized_needs_signature, msg);
		if(statusCode.startsWith("1"))
			return new PathologyElectronicSignatureNeed(PathologyElectronicSignatureNeedStatus.not_enabled, msg);
		else 
			return new PathologyElectronicSignatureNeed(PathologyElectronicSignatureNeedStatus.not_authorized, msg);
		
	}
	
	public static PathologyCaseTemplate translateTemplate(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("1"))
		{
			List<PathologyCaseTemplateField> fields = new ArrayList<PathologyCaseTemplateField>();
			
			String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
			for(int i = 1; i < lines.length; i++)
			{
				fields.add(translateFieldLine(lines[i].trim()));
			}
			
			return new PathologyCaseTemplate(fields);
		}
		else
		{
			throw new MethodException("Error retrieving supplemental reports, " + vistaResult);
		}
	}
	
	/**
	 .012^SPECIMEN^Skin|
	 * @param line
	 * @return
	 */
	private static PathologyCaseTemplateField translateFieldLine(String line)
	{
		String [] pieces = StringUtils.Split(line, StringUtils.CARET);
		String fieldNumber = pieces[0];
		String label = pieces[1];
		String [] valuePieces = StringUtils.Split(pieces[2].trim(), StringUtils.STICK);
		List<String> values = new ArrayList<String>();
		for(String valuePiece : valuePieces)
		{
			// if the values end with a stick then the Split above will include the piece after the trailing stick as a value, check for an empty string to exlcude 
			if(valuePiece != null && valuePiece.length() > 0)
				values.add(valuePiece);
		}
		return new PathologyCaseTemplateField(fieldNumber, label, values);
	}
	
	public static List<PathologyCaseSupplementalReport> translateSupplementalReports(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("1"))
		{
			List<PathologyCaseSupplementalReport> supplementalReports = 
				new ArrayList<PathologyCaseSupplementalReport>();
			
			String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
			for(int i = 1; i < lines.length; i++)
			{
				supplementalReports.add(translateSupplementalReport(lines[i].trim()));
			}
			
			return supplementalReports;
		}
		else
		{
			throw new MethodException("Error retrieving supplemental reports, " + vistaResult);
		}
	}
	
	private static PathologyCaseSupplementalReport translateSupplementalReport(String line)
	{
		String [] pieces = StringUtils.Split(line, StringUtils.CARET);
		String supplementalReportDateString = pieces[0];
		String verifiedString = pieces[1];
		String verifiedBy = pieces[2];
		String values = pieces[3];
		String [] valuePieces = StringUtils.Split(values, StringUtils.STICK);
		
		Date supplementalReportDate = parseDateString(supplementalReportDateString);
		
		return new PathologyCaseSupplementalReport(supplementalReportDate, verifiedString.equalsIgnoreCase("YES"), 
				(verifiedBy.length() > 0 ? verifiedBy : null), valuePieces);
		
	}
	
	public static String translatePathologyReport(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("1"))
		{
			String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
			StringBuilder result = new StringBuilder();
			String prefix = "";
			for(int i = 1; i < lines.length; i++)
			{
				result.append(prefix);
				result.append(lines[i].trim());
				prefix = "\n";
			}
			return result.toString();
		}
		else
		{
			throw new MethodException("Error retrieving report, " + vistaResult);
		}
	}
	
	public static String translateTemplate(String vistaResult, String apSection)
	throws MethodException	
	{
		if(vistaResult == null)
			throw new MethodException("Null response from VistA");
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		if(lines[0].startsWith("0"))
		{
			String []pieces = StringUtils.Split(lines[0], StringUtils.CARET);
			if(pieces.length >= 3)
			{
				throw new MethodException("Error retrieving template for '" + apSection + "', " + pieces[2]);
			}
			else
			{
				throw new MethodException("Error retrieving template for '" + apSection + "'.");
			}
		}
		StringBuilder result = new StringBuilder();
		for(int i = 1; i < lines.length; i++)
		{
			result.append(lines[i].trim() + "\n");
		}		
		return result.toString();
	}
	
	public static PathologyCaseUpdateAttributeResult translatePutCaseResult(String vistaResult)
	{
		if(vistaResult.startsWith("1"))
		{
			// success
			return PathologyCaseUpdateAttributeResult.createSuccessfulLockResult();
		}
		else
		{
			String [] pieces = StringUtils.Split(vistaResult, StringUtils.CARET);
			return PathologyCaseUpdateAttributeResult.createFailedLockResult(pieces[2]);
		}
	}
		
	/**
	 *
	 	-5^^Update error [Fileman error text]
		-4^^One or more input parameters are missing
		-3^^No accession code for this case
		-2^^File locked by [user] since [date-time]           <- Duc, agreed that this error code will remain
		-1^^ERROR [MUMPS error]
		0^0^File unlocked
		0^1^File locked

	 * 
	 * 
	 * @param vistaResult
	 * @return
	 * @throws MethodException
	 */
	public static PathologyCaseUpdateAttributeResult translateCaseLockResult(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("-2"))
		{
			String errorMsg = StringUtils.MagPiece(vistaResult, StringUtils.CARET, 3);
			return PathologyCaseUpdateAttributeResult.createFailedLockResult(errorMsg);
		}
		else if(vistaResult.startsWith("-"))
		{
			String errorMsg = StringUtils.MagPiece(vistaResult, StringUtils.CARET, 3);
			throw new MethodException("Error locking/unlocking case: " + errorMsg);
		}
		else
		{
			String lockStatusCode = StringUtils.MagPiece(vistaResult, StringUtils.CARET, 2);
			logger.info("Lock caseresult has status [" + lockStatusCode + "]");
			TransactionContextFactory.get().addDebugInformation("Lock case result has status [" + lockStatusCode + "]");
			return PathologyCaseUpdateAttributeResult.createSuccessfulLockResult();
		}	
	}
	
	/**
	 * 
	 * 
	 	0^0^Reservation ended
		0^1^Case reserved
		-1^^ERROR: [MUMPS error]
		-2^^ERROR: AP subsection not specified
		-3^^ERROR: Year not specified
		-4^^ERROR: Accession index not specified
		-5^^ERROR: Invalid context - ...
		-6^^ERROR: No accession code for this case
		-7^^ERROR: Update error - ...
	 * 
	 * 
	 * @param vistaResult
	 * @return
	 */
	public static PathologyCaseReserveResult translateCaseReserveResult(String vistaResult)
	throws MethodException
	{
		String [] pieces = StringUtils.Split(vistaResult, StringUtils.CARET);
		String code = pieces[0].trim();
		String successCode = pieces[1].trim();
		String msg = pieces[2].trim();
		if(code.startsWith("-"))
		{
			throw new MethodException(msg);
		}

		if("0".equals(successCode))
			return PathologyCaseReserveResult.reservation_ended;
		if("1".equals(successCode))
			return PathologyCaseReserveResult.case_reserved;
		throw new MethodException("Unknown code returned from VistA, " + vistaResult);
	}
	
	public static List<AbstractPathologySite> translatePathologySites(String vistaResult, boolean reading)
	throws MethodException
	{
		List<AbstractPathologySite> result = new ArrayList<AbstractPathologySite>();
		if(vistaResult == null)
			return result;
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		if(lines.length == 0)
			return result;
		if(lines[0].startsWith("1"))
		{
			for(int i = 1; i < lines.length; i++)
			{
				String [] pieces = StringUtils.Split(lines[i].trim(), StringUtils.CARET);
				String active = pieces[0];
				//String siteIen = pieces[1];
				String siteStationNumber = pieces[2];
				String siteAbbr = pieces[3];
				String siteName = pieces[4];
				if(reading)
				{
					String readingSiteType = pieces[5];
					result.add(new PathologyReadingSite(siteStationNumber, siteName, siteAbbr, 
							("0".equals(active) ? false : true),
							PathologyReadingSiteType.getFromValue(readingSiteType)));
				}
				else
				{
					//String primarySiteIen = pieces[5];
					String primarySiteStationNumber = pieces[6];
					String primarySiteAbbr = pieces[7];
					String primarySiteName = pieces[8];
					result.add(new PathologyAcquisitionSite(siteStationNumber, siteName, siteAbbr,
							("0".equals(active) ? false : true),
							primarySiteStationNumber, primarySiteName, primarySiteAbbr));
				}
			}
		}
		else
		{
			throw new MethodException("Exception retrieving pathology sites, " + lines[0]);
		}
		
		return result;
	}
	/**
	 Line 0: 1^Number of Lines^IEN^Type^Reservation Date^Interpreting Station^Parent Interpretation Index^Status (Success) 0^0^Error message (Failure)
 	 Line n: IEN^Type^Reservation Date^Interpreting Station^Parent Interpretation Index^Status
 	 
 	 1^2^Consult IEN^Type^Reservation Date^Interpreting Station^Site Abbreviation^Status
18^INTERPRETATION^05/17/2012 10:40:20^660^SLC^PENDING
19^CONSULTATION^05/17/2012 10:40:20^688^WAS^PENDING
	 
	 * @param vistaResult
	 * @return
	 */
	public static List<PathologyCaseConsultation> translateConsultations(PathologyCaseURN pathologyCaseUrn,
			String vistaResult)
	throws MethodException
	{
		List<PathologyCaseConsultation> result = new ArrayList<PathologyCaseConsultation>();
		
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			String [] pieces = StringUtils.Split(lines[i].trim(), StringUtils.CARET);
			String ien = pieces[0];
			String consultationType = pieces[1];
			String reservationDateString = pieces[2];
			String interpretingStation = pieces[3];
			String siteAbbr = pieces[4];
			String status = pieces[5];
			
			try
			{
				PathologyCaseConsultationURN pathologyCaseConsultationUrn = 
					PathologyCaseConsultationURN.create(ien, pathologyCaseUrn);
				Date reservationDate = parseDateString(reservationDateString);
				
				result.add(new PathologyCaseConsultation(pathologyCaseConsultationUrn, consultationType, 
						reservationDate, interpretingStation, siteAbbr, status));
			} 
			catch (URNFormatException e)
			{
				throw new MethodException(e);
			}	
		}
		
		return result;
	}
	
	public static List<PathologyCaseSpecimen> translateSpecimens(String vistaResult)
	{
		List<PathologyCaseSpecimen> result = new ArrayList<PathologyCaseSpecimen>();
		if(vistaResult == null)
			return result;
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			result.add(translatePathologyCaseSpecimen(lines[i]));
		}
		return result;
	}
	
	private static PathologyCaseSpecimen translatePathologyCaseSpecimen(String line)
	{
		String [] pieces = StringUtils.Split(line, StringUtils.CARET);
		String specimen = pieces[0];
		String smear = null;
		String stainProcedure = null;
		int numSlides = 0;
		String lastStainDateString = null;
		if(pieces.length > 4)
		{
			smear = pieces[1];
			stainProcedure = pieces[2];
			if(pieces[3] != null && pieces[3].length() > 0)
				numSlides = Integer.parseInt(pieces[3]);
		}
		if (pieces.length >= 5)
		{
			lastStainDateString = pieces[4];
		}
		
		PathologyCaseSpecimen result = new PathologyCaseSpecimen(specimen);
		result.setStain(stainProcedure);
		result.setNumSlides(numSlides);
		result.setSmearPrep(smear);
		if(lastStainDateString != null && lastStainDateString.length() > 0)
		{
			//03/02/2012 12:13
			Date lastStainDate = parseDateString(lastStainDateString);
			if(lastStainDate != null)
				result.setLastStainDate(lastStainDate);
		}
		
		return result;
	}
	
	/*
	public static List<PathologySlide> translateSlidesResult(String vistaResult)
	{		
		List<PathologySlide> result = new ArrayList<PathologySlide>();
		if(vistaResult == null)
			return result;
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			result.add(translatePathologySlide(lines[i]));
		}
		return result;
	}*/
	
	/**
	 * 
	 * 
	 * @param line
	 * @return
	 */
	/*
	private static PathologySlide translatePathologySlide(String line)
	{
		String [] pieces = StringUtils.Split(line, StringUtils.CARET);
		String specimen = pieces[0];
		String stainType = null;
		String tissueSampleSite = null;
		String slideNumber = null;
		String studyDate = null;
		if(pieces.length > 4)
		{
			stainType = pieces[1];
			tissueSampleSite = pieces[2];
			slideNumber = pieces[3];
		}
		if (pieces.length >= 5)
		{
			studyDate = pieces[4];
		}
		
		PathologySlide result = new PathologySlide();
		result.setSpecimen(specimen);
		result.setStainType(stainType);
		result.setTissueSampleSite(tissueSampleSite);
		result.setSlideNumber(slideNumber);
		result.setStudyDate(studyDate);
		return result;
	}*/
	
	/**
	 * 
	 * @param vistaResult
	 * @return
	 */
	
	public static List<PathologyCase> translateLabCasesResult(Site site, String vistaResult)
	throws MethodException
	{
		List<PathologyCase> cases = new ArrayList<PathologyCase>();
		if(vistaResult == null)
			return cases;
		
		if(vistaResult.startsWith("-"))
		{
			String errorMsg = StringUtils.MagPiece(vistaResult, StringUtils.CARET, 3);
			throw new MethodException(errorMsg);
		}		
		
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		for(int i = 1; i < lines.length; i++)
		{
			PathologyCase pathologyCase = translateLabCase(site, lines[i]);
			if(pathologyCase != null)
				cases.add(pathologyCase);
		}
		return cases;		
	}
	
	/**
	 
	 Line 0: 1^Number of lines^Released Reports/Unreleased Reports (Success) or 0^0^Error message (Failure)
	 Line 1: "Case #~~1^Reserved Flag~~2^Reserved By~~3^Patient Name~~4^Patient ID #~~5^Priority~~6^Slide Available~~7^Specimen Taken Date/Time~~8^Status~~9^Site~~10^AP Section~~11^Year~~12^Accession Number~~13^ICN~~14"
	 
	 CY 11 4^0^^PATIENT,ONEZEROSEVENTWO^1072^ROUTINE^NO^05/10/2011 14:27^In Progress^WAS^CY^11^4^9112345678V505029
	 
	 CY 12 3^0^^PATIENT,ONEZEROZEROSIX^1006^ROUTINE^NO^08/09/2012 12:00:22^Released^WAS^CY^12^3^9312457789V212714^1^^P1006

	 * @param labCaseLine
	 * @return
	 */
	private static PathologyCase translateLabCase(Site site, String labCaseLine)
	throws MethodException
	{
		String [] barPieces = StringUtils.Split(labCaseLine.trim(), StringUtils.STICK);
		
		String [] pieces = StringUtils.Split(barPieces[0].trim(), StringUtils.CARET);
		String accessionNumber = pieces[0];
		int reserved = Integer.parseInt(pieces[1]);
		String reservedBy = pieces[2];
		String patientName = pieces[3];
		String patientDfn = pieces[4];
		String priority = pieces[5];
		String slidesAvailableString = pieces[6];
		String speciminTakenDateString = pieces[7];
		String status = pieces[8];
		String siteAbbr = pieces[9];
		String apSection = pieces[10];
		String year = pieces[11];
		String number = pieces[12];
		String patientIcn = pieces[13];
		int speciminCount = Integer.parseInt(pieces[14]);
		String method = pieces[15];
		String patientSsn = pieces[16];
		String noteAttachedString = pieces[17];
		String isSensitive = "0";
		int numberOfImages = 0;
		String numberOfImagesString = null;
		if(pieces.length > 19)
			isSensitive = pieces[18];
		else // length==19
			numberOfImagesString = pieces[18];
		if(pieces.length >= 20)
			numberOfImagesString = pieces[19];
		if(numberOfImagesString != null && numberOfImagesString.length() > 0)
		{
			try
			{
				numberOfImages = Integer.parseInt(numberOfImagesString);
			}
			catch(Exception ex)
			{
				logger.warn("Error parsing number of images string [" + numberOfImagesString + "], " + ex.getMessage());
			}
		}
		
		PatientIdentifier patientIdentifier = null;
		
		if(patientIcn == null || patientIcn.startsWith("-1"))
		{
			//logger.warn("Excluding PathologyCase '" + accessionNumber + "' because patient does not have an ICN.");
			//TransactionContextFactory.get().addDebugInformation("Excluding PathologyCase '" + accessionNumber + "' because patient does not have an ICN.");			
			//return null;
			patientIdentifier = new PatientIdentifier(patientDfn, PatientIdentifierType.dfn);
		}
		else
		{
			patientIdentifier = new PatientIdentifier(patientIcn, PatientIdentifierType.icn);
		}
		
		Date speciminTakenDate = parseDateString(speciminTakenDateString);
		boolean slidesAvailable = "YES".equals(slidesAvailableString);
		boolean noteAttached = "YES".equals(noteAttachedString);
						
		try
		{
			
			boolean patientSensitive = "1".equals(isSensitive); // sensitive value 1
			PathologyCaseURN pathologyCaseUrn = PathologyCaseURN.create(site.getSiteNumber(), apSection, year, number, patientIdentifier);
			
			return new PathologyCase(pathologyCaseUrn, accessionNumber, reserved, reservedBy, patientName, 
					patientIdentifier, priority, slidesAvailable, speciminTakenDate, 
					status, siteAbbr, speciminCount, patientSsn, method, noteAttached,
					patientSensitive, numberOfImages);
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException(urnfX);
		}
		
		
	}
	
	/**
	 * Parse a date, public for unit testing purposes
	 * @param dateString
	 * @return
	 */
	private static Date parseDateString(String dateString)
	{
		try
		{
			if(dateString.length() == 10)
			{
				// 03/02/2012
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				return sdf.parse(dateString);
			}
			else if(dateString.length() == 16)
			{
				//05/10/2012 14:18
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy kk:mm");
				return sdf.parse(dateString);
			}
			else if(dateString.length() == 19)
			{
				//08/09/2012 12:00:22
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
				return sdf.parse(dateString);
			}
			else
			{
				logger.warn("Cannot parse date '" + dateString + "'.");
			}
		}
		catch(ParseException pX)
		{
			logger.error("Error parsing date '" + dateString + "', " + pX.getMessage());
		}
		return null;
	}

	/**
	 
	 	-6^^Study context not found
		-5^^No Input
		-4^^One or more input parameters are missing
		-3^^[fldnum]: Invalid Field Number
		-2^^[fldnam] – Updating Error: [Fileman error description]
		-1^^ERROR [MUMPS error]
		0
		1^^[alert message text iff report was released]	 
	 
	 * @param vistaResult
	 * @return
	 * @throws MethodException
	 */
	public static PathologySaveCaseReportResult translateSavingCaseReportFields(String vistaResult)
	throws MethodException
	{
		if(vistaResult.startsWith("-"))
		{
			String errorMsg = StringUtils.MagPiece(vistaResult, StringUtils.CARET, 3);
			throw new MethodException("Error saving case report fields: " + errorMsg);
		}
		else if(vistaResult.startsWith("1"))
		{
			String warningMsg = StringUtils.MagPiece(vistaResult, StringUtils.CARET, 3);
			return PathologySaveCaseReportResult.createReleasedResult(warningMsg);
		}
		return PathologySaveCaseReportResult.createUnreleasedResult();
	}
	
	/**
	 * 
	 	1^4^Slide Number^Date/Time Scanned^URL^Zoom Factor^Scan Application^Slide Status^View Application^Description^
		31^^//I873VSTWIN-T:82/@31^30x^SCANSCOPE^ADDED^IMAGESCOPE^^
		32^^//I873VSTWIN-T:82/@32^30x^SCANSCOPE^ADDED^IMAGESCOPE^^
		33^^//I873VSTWIN-T:82/@33^30x^SCANSCOPE^ADDED^IMAGESCOPE^^
		34^^//I873VSTWIN-T:82/@34^30x^SCANSCOPE^ADDED^IMAGESCOPE^^
	 * 
	 * 
	 * @param vistaResult
	 * @return
	 * @throws MethodException
	 */
	public static List<PathologyCaseSlide> translateCaseSlideInformation(String vistaResult)
	throws MethodException
	{
		if(!vistaResult.startsWith("1"))
		{
			throw new MethodException("Error retrieving case slide information: " + vistaResult);
		}
		String [] lines = StringUtils.Split(vistaResult, StringUtils.NEW_LINE);
		List<PathologyCaseSlide> result = new ArrayList<PathologyCaseSlide>();
		for(int i = 1; i < lines.length; i++)
		{
			String [] pieces = StringUtils.Split(lines[i], StringUtils.CARET);

			String slideNumber = pieces[0];
			String dateTimeScanned = pieces[1];
			String url = pieces[2];
			String zoomFactor = pieces[3];
			String scanApplication = pieces[4];
			String slideStatus = pieces[5];
			String viewApplication = pieces[6];
			String description = pieces[7];
			result.add(new PathologyCaseSlide(slideNumber, dateTimeScanned, url, zoomFactor, 
					scanApplication, slideStatus, viewApplication, description));
		}
		return result;
	}
	
}
