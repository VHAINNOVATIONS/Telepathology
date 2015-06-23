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
package gov.va.med.imaging.vistaimagingdatasource.pathology.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.PathologyReadingSite;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus;
import gov.va.med.imaging.pathology.enums.PathologyField;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingPathologyQueryFactory
{
	private final static String RPC_MAGTP_GET_ACTIVE = "MAGTP GET ACTIVE";
	private final static String RPC_MAGTP_GET_SLIDES = "MAGTP GET SLIDES";
	private final static String RPC_MAGTP_GET_CONSULT = "MAGTP GET CONSULT";
	private final static String RPC_MAGTP_GET_SITE_CONFIG = "MAGTP GET SITE CONFIG";
	private final static String RPC_MAGTP_SECOND_LOCK = "MAGTP SECOND LOCK";
	private final static String RPC_MAGTP_PUT_CONSULT = "MAGTP PUT CONSULT";
	private final static String RPC_MAGTP_GET_PATIENT = "MAGTP GET PATIENT";
	private final static String RPC_MAGTP_GET_TEMPLATE_XML = "MAGTP GET TEMPLATE XML";
	private final static String RPC_MAGTP_PUT_TEMPLATE_XML = "MAGTP PUT TEMPLATE XML";
	private final static String RPC_MAGTP_PUT_SITE_CONFIG = "MAGTP PUT SITE CONFIG";
	private final static String RPC_MAGTP_GET_CPRS_REPORT = "MAGTP GET CPRS REPORT";
	private final static String RPC_MAGTP_GET_SUP_REPORTS = "MAGTP GET SUP REPORTS";
	private final static String RPC_MAGTP_GET_TEMPLATE_DATA = "MAGTP GET TEMPLATE DATA";
	private final static String RPC_MAGTP_RESERVE_CASE = "MAGTP RESERVE CASE";
	private final static String RPC_MAGTP_GET_ESIGN = "MAGTP GET ESIGN";
	private final static String RPC_MAGTP_GET_LIST = "MAGTP GET LIST";
	private final static String RPC_MAGTP_CHANGE_CONS_STATUS = "MAGTP CHANGE CONS STATUS";
	private final static String RPC_MAGTP_PUT_REPORT_FIELD = "MAGTP PUT REPORT FIELD";
	private final static String RPC_MAGTP_PUT_SUP_REPORT = "MAGTP PUT SUP REPORT";
	private final static String RPC_MAGTP_GET_SITES = "MAGTP GET SITES";
	private final static String RPC_MAGTP_GET_LOCK_MINS = "MAGTP GET LOCK MINS";
	private final static String RPC_MAGTP_PUT_LOCK_MINS = "MAGTP PUT LOCK MINS";
	private final static String RPC_MAGTP_GET_PREFERENCES = "MAGTP GET PREFERENCES";
	private final static String RPC_MAGTP_PUT_PREFERENCES = "MAGTP PUT PREFERENCES";
	private final static String RPC_MAGTP_GET_SNOMED_CODES = "MAGTP GET SNOMED CODES";
	private final static String RPC_MAGTP_PUT_SNOMED_CODES = "MAGTP PUT SNOMED CODES";
	private final static String RPC_MAGTP_PUT_CPT_CODE = "MAGTP PUT CPT CODE";
	private final static String RPC_MAGTP_GET_CPT_CODE = "MAGTP GET CPT CODE";
	private final static String RPC_MAGTP_GET_USER = "MAGTP GET USER";
	private final static String RPC_MAGTP_GET_CASES = "MAGTP GET CASES";
	private final static String RPC_MAGTP_CHECK_CONS = "MAGTP CHECK CONS";
	private final static String RPC_MAGTP_COPY_CASE = "MAGTP COPY CASE";
	private final static String RPC_MAGTP_DEL_SNOMED_CODES = "MAGTP DEL SNOMED CODES";
	private final static String RPC_MAGTP_GET_NOTE = "MAGTP GET NOTE";
	private final static String RPC_MAGTP_PUT_NOTE = "MAGTP PUT NOTE";
	private final static String RPC_MAGTP_GET_SLIDES_INFO = "MAGTP GET SLIDES INFO";
	
	public static VistaQuery createSaveCaseNoteQuery(PathologyCaseURN pathologyCaseUrn, String note)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_NOTE);
		Map<String, String> noteMap = new HashMap<String, String>();
		String [] lines = StringUtils.Split(note, StringUtils.NEW_LINE);
		for(String line : lines)
		{
			// not splitting the lines based on length, the database can handle any length line, just splitting on the new line
			noteMap.put("\"" +  Integer.toString(noteMap.size()) + "\"", line.trim());
			/*
			List<String> shortLines = splitLineIntoShorterLines(line);
			
			for(String shortLine : shortLines)
			{
				noteMap.put("\"" +  Integer.toString(noteMap.size()) + "\"", shortLine);
			}*/
		}
		query.addParameter(VistaQuery.LIST, noteMap);		
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.toStringAccessionNumber());
		return query;
	}
	
	public static VistaQuery createGetCaseNoteQuery(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_NOTE);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.toStringAccessionNumber());
		return query;
	}
	
	public static VistaQuery createDeleteSnomedCode(PathologyCaseURN pathologyCaseUrn, 
			String tissueId, String snomedId, PathologyField snomedField, String etiologyId)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_DEL_SNOMED_CODES);
		
		StringBuilder sb = new StringBuilder();
		String type = null;
		if(etiologyId != null)
		{
			sb.append(etiologyId);
			sb.append(",");
			type = "ETIOLOGY";
		}
		if(snomedId != null)
		{
			sb.append(snomedId);
			sb.append(",");
			// if type was null because the etiologyId is null
			if(type == null)
				type = getPathologyFieldName(snomedField);
		}
		else
		{
			// morphology is null, deleting the entire tissue
			type = "ORGAN/TISSUE";
		}
		sb.append(tissueId);
		sb.append(StringUtils.CARET);
		sb.append(type);
		query.addParameter(VistaQuery.LITERAL, sb.toString());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createCopyCaseQuery(String patientDfn, PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_COPY_CASE);
		StringBuilder parameter = new StringBuilder();
		parameter.append(patientDfn);
		parameter.append(StringUtils.CARET);
		parameter.append(pathologyCaseUrn.toStringAccessionNumber());
		parameter.append(StringUtils.CARET);
		parameter.append(pathologyCaseUrn.getOriginatingSiteId());
		/*
		for(String specimen : specimens)
		{
			parameter.append(StringUtils.CARET);
			parameter.append(specimen);
		}*/
		query.addParameter(VistaQuery.LITERAL, parameter.toString());
		return query;
	}
	
	public static VistaQuery createCheckPendingConsultations(String stationNumber)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_CHECK_CONS);
		query.addParameter(VistaQuery.LITERAL, stationNumber);
		return query;
	}
	
	public static VistaQuery createGetCasesQuery(List<PathologyCaseURN> cases)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_CASES);
		Map<String, String> snowmedCodeMap = new HashMap<String, String>();		
		for(PathologyCaseURN caseUrn : cases)
		{
			snowmedCodeMap.put("\"" +  Integer.toString(snowmedCodeMap.size()) + "\"", caseUrn.toStringAccessionNumber());
		}
		query.addParameter(VistaQuery.LIST, snowmedCodeMap);
		return query;
	}
	
	public static VistaQuery createGetUserQuery()
	{
		return new VistaQuery(RPC_MAGTP_GET_USER);
	}
	
	public static VistaQuery createGetCptCodesQuery(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_CPT_CODE);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createSaveCptCodesQuery(PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN locationFieldUrn, List<String> cptCodes)
	throws MethodException
	{
		if(!locationFieldUrn.getPathologyField().equals(PathologyField.location))
		{
			throw new MethodException("Location must be of location type");
		}
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_CPT_CODE);
		query.addParameter(VistaQuery.LITERAL, locationFieldUrn.getFieldId());
		
		StringBuilder codes = new StringBuilder();
		String prefix = "";
		for(String cptCode : cptCodes)
		{
			codes.append(prefix);
			codes.append(cptCode);
			prefix = StringUtils.COMMA;
		}		
		
		query.addParameter(VistaQuery.LITERAL, codes.toString());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createSaveCaseTissues(PathologyCaseURN pathologyCaseUrn, 
			PathologyFieldURN tissueFieldUrn)
	throws MethodException
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_SNOMED_CODES);
		Map<String, String> snowmedCodeMap = new HashMap<String, String>();		
		snowmedCodeMap.put("\"" +  Integer.toString(snowmedCodeMap.size()) + "\"", 
				StringUtils.CARET + getPathologyFieldName(PathologyField.topography) + StringUtils.CARET + "1");
		if(!tissueFieldUrn.getPathologyField().equals(PathologyField.topography))
		{
			throw new MethodException("Only fields of type topography can be added to cases.");			
		}
		snowmedCodeMap.put("\"" +  Integer.toString(snowmedCodeMap.size()) + "\"", 
				StringUtils.CARET + tissueFieldUrn.getFieldId());
		
		query.addParameter(VistaQuery.LIST, snowmedCodeMap);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createSaveSnomedCodes(PathologyCaseURN pathologyCaseUrn,
			String tissueId, String morphologyId,
			PathologyFieldURN etiologyFieldUrn)
	throws MethodException
	{
		// you can only add etiology fields to morphology fields
		if(!etiologyFieldUrn.getPathologyField().equals(PathologyField.etiology))
		{
			throw new MethodException("Only fields of type etiology can be added to morphology fields.");			
		}
		
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_SNOMED_CODES);
		Map<String, String> snowmedCodeMap = new HashMap<String, String>();		
		snowmedCodeMap.put("\"" +  Integer.toString(snowmedCodeMap.size()) + "\"", 
				morphologyId + StringUtils.COMMA + tissueId + StringUtils.CARET + getPathologyFieldName(etiologyFieldUrn.getPathologyField()) + StringUtils.CARET + "1");
		snowmedCodeMap.put("\"" +  Integer.toString(snowmedCodeMap.size()) + "\"", 
				morphologyId + StringUtils.COMMA + tissueId + StringUtils.CARET + etiologyFieldUrn.getFieldId());
		query.addParameter(VistaQuery.LIST, snowmedCodeMap);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createSaveSnomedCodes(PathologyCaseURN pathologyCaseUrn, 
			String tissueId, PathologyFieldURN pathologyFieldUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_SNOMED_CODES);
		Map<String, String> snowmedCodeMap = new HashMap<String, String>();		
		snowmedCodeMap.put("\"" +  Integer.toString(snowmedCodeMap.size()) + "\"", 
				tissueId + StringUtils.CARET + getPathologyFieldName(pathologyFieldUrn.getPathologyField()) + StringUtils.CARET + "1");
		snowmedCodeMap.put("\"" +  Integer.toString(snowmedCodeMap.size()) + "\"", 
				tissueId + StringUtils.CARET + pathologyFieldUrn.getFieldId());
		query.addParameter(VistaQuery.LIST, snowmedCodeMap);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createGetSnomedCodesQuery(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_SNOMED_CODES);
		
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	private static String getPathologyFieldName(PathologyField field)
	{
		if(field.equals(PathologyField.topography))
		{
			return "ORGAN/TISSUE";
		}
		else
		{
			return field.name().toUpperCase();
		}
	}
	
	public static VistaQuery createSaveUserPreferencesQuery(String userId, String label, String xml)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_PREFERENCES);
		Map<String, String> preferencesMap = new HashMap<String, String>();
		
		preferencesMap.put("\"" +  Integer.toString(preferencesMap.size()) + "\"", (userId == null ? "" : userId) + StringUtils.CARET + label);
		String [] lines = StringUtils.Split(xml, StringUtils.NEW_LINE);
		for(String line : lines)
		{
			List<String> shortLines = splitLineIntoShorterLines(line);
			
			for(String shortLine : shortLines)
			{
				preferencesMap.put("\"" +  Integer.toString(preferencesMap.size()) + "\"", shortLine);
			}
		}
		query.addParameter(VistaQuery.LIST, preferencesMap);
		return query;
	}
	
	public static VistaQuery createGetUserPreferencesQuery(String userId, String label)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_PREFERENCES);
		query.addParameter(VistaQuery.LITERAL, (userId == null ? "" : userId) + StringUtils.CARET + label);
		return query;
	}
	
	public static VistaQuery createSetLockMinutesQuery(int minutes)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_LOCK_MINS);
		query.addParameter(VistaQuery.LITERAL, minutes + "");
		return query;
	}
	
	public static VistaQuery createGetLockMinutesQuery()
	{
		return new VistaQuery(RPC_MAGTP_GET_LOCK_MINS);
	}
	
	public static VistaQuery createGetSitesQuery()
	{
		return new VistaQuery(RPC_MAGTP_GET_SITES);
	}
	
	public static VistaQuery createSaveCaseSupplementalReportQuery(PathologyCaseURN pathologyCaseUrn, 
			String reportContents, Date date, boolean verified)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_SUP_REPORT);
		Map<String, String> changesMap = new HashMap<String, String>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy kk:mm");
				
		changesMap.put("\"" +  Integer.toString(changesMap.size()) + "\"", sdf.format(date) + StringUtils.CARET + (verified == true ? "1" : "0"));
		String [] lines = StringUtils.Split(reportContents, StringUtils.NEW_LINE);
		for(String line : lines)
		{
			changesMap.put("\"" +  Integer.toString(changesMap.size()) + "\"", line.trim());
		}
		query.addParameter(VistaQuery.LIST, changesMap);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		
		return query;
	}
	
	public static VistaQuery createSaveCaseReportQuery(PathologyCaseURN pathologyCaseUrn,
			List<PathologyCaseReportField> fields)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_REPORT_FIELD);
		Map<String, String> fieldsMap = new HashMap<String, String>();
		if(fields != null)
		{
			for(int i = 0; i < fields.size(); i++)
			{
				PathologyCaseReportField field = fields.get(i);
				int valuesCount = field.getValues().size();
				fieldsMap.put("\"" +  Integer.toString(fieldsMap.size()) + "\"", field.getFieldNumber() + "^" + valuesCount);
				for(int j = 0; j < valuesCount; j++)
				{
					String value = field.getValues().get(j);
					fieldsMap.put("\"" +  Integer.toString(fieldsMap.size()) + "\"", field.getFieldNumber() + "^" + value);
				}
			}
		}
		query.addParameter(VistaQuery.LIST, fieldsMap);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createChangeConsultationStatusQuery(PathologyCaseConsultationURN pathologyCaseConsulationUrn,
			PathologyCaseConsultationUpdateStatus consultationUpdateStatus)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_CHANGE_CONS_STATUS);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseConsulationUrn.getConsultationId());
		switch(consultationUpdateStatus)
		{
			case pending:
				query.addParameter(VistaQuery.LITERAL, "0");
				break;
			case completed:
				query.addParameter(VistaQuery.LITERAL, "1");
				break;
			case refused:
				query.addParameter(VistaQuery.LITERAL, "2");
				break;
			case recalled:
				query.addParameter(VistaQuery.LITERAL, "3");
				break;
		}
		
		return query;
	}
	
	
	
	public static VistaQuery createGetListQuery(PathologyField pathologyField, String searchParameter)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_LIST);
		query.addParameter(VistaQuery.LITERAL, getPathologyFieldFile(pathologyField));
		query.addParameter(VistaQuery.LITERAL, searchParameter);
		return query;
	}
	
	private static String getPathologyFieldFile(PathologyField pathologyField)
	{
		switch (pathologyField)
		{
			case topography:
				return "61";
			case morphology:
				return "61.1";
			case etiology:
				return "61.2";
			case function:
				return "61.3";
			case disease:
				return "61.4"; 
			case procedure:
				return "61.5";
			case users:
				return "200";
			case location:
				return "44";
			case workload:
				return "64";
			case cpt:
				return "81";
		}
		return null;
	}
	
	public static VistaQuery createGetElectronicSignatureAvailable(String apSection)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_ESIGN);
		query.addParameter(VistaQuery.LITERAL, apSection);
		return query;
	}
	
	public static VistaQuery createGetTemplateDataQuery(PathologyCaseURN pathologyCaseUrn, List<String> fields)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_TEMPLATE_DATA);
		Map<String, String> fieldsMap = new HashMap<String, String>();
		if(fields != null)
		{
			for(int i = 0; i < fields.size(); i++)
			{
				fieldsMap.put("\"" +  Integer.toString(fieldsMap.size()) + "\"", fields.get(i));
			}
		}
		query.addParameter(VistaQuery.LIST, fieldsMap);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		
		return query;
	}
	
	public static VistaQuery createGetSupplementalReports(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_SUP_REPORTS);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createGetCprsCaseReport(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_CPRS_REPORT);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	private final static int maxLineLength = 250;
	
	/**
	 * This method splits a line into a list of shorter lines not above the max length for a line. This method is public only for testability
	 * @param line
	 * @return
	 */
	public static List<String> splitLineIntoShorterLines(String line)
	{
		List<String> shortLines = new ArrayList<String>();
		while(line.length() > maxLineLength)
		{
			String subLine = line.substring(0, maxLineLength);
			shortLines.add(subLine);
			line = line.substring(maxLineLength);
		}
		shortLines.add(line);
		return shortLines;
	}
	
	public static VistaQuery createSaveTemplateVistaQuery(String apSection, String xmlTemplate)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_TEMPLATE_XML);
		Map<String, String> xmlTemplateMap = new HashMap<String, String>();
		String [] lines = StringUtils.Split(xmlTemplate, StringUtils.NEW_LINE);
		if(lines != null)
		{
			for(int i = 0; i < lines.length; i++)
			{
				// each line of the input cannot be more than 250 characters in length, this should split things up
				String line = lines[i];
				List<String> shortLines = splitLineIntoShorterLines(line);
				
				for(String shortLine : shortLines)
				{
					xmlTemplateMap.put("\"" +  Integer.toString(xmlTemplateMap.size()) + "\"", shortLine);
				}
				
				// JMW 9/27/2011 - removed trim function to keep extra characters (including new lines) in the database
				//xmlTemplateMap.put("\"" +  Integer.toString(xmlTemplateMap.size()) + "\"", lines[i]);
			}			
		}		
		query.addParameter(VistaQuery.LIST, xmlTemplateMap);
		query.addParameter(VistaQuery.LITERAL, apSection);
		
		return query;
	}
	
	public static VistaQuery createSaveAcquisitionSiteVistaQuery(PathologyAcquisitionSite acquisitionSite, boolean delete)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_SITE_CONFIG);
		StringBuilder sb = new StringBuilder();
		sb.append("0"); // for acquisition site
		sb.append(StringUtils.CARET);
		sb.append(delete == true ? "0" : "1");
		sb.append(StringUtils.CARET);
		sb.append(acquisitionSite.getSiteId());
		if(!delete)
		{
			sb.append(StringUtils.CARET);
			sb.append(acquisitionSite.getPrimarySiteStationNumber());
			sb.append(StringUtils.CARET);
			sb.append(acquisitionSite.isActive() == true ? "1" : "0");
		}		
		query.addParameter(VistaQuery.LITERAL, sb.toString());
		return query;
	}
	
	public static VistaQuery createSaveReadingSiteVistaQuery(PathologyReadingSite readingSite, boolean delete)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_SITE_CONFIG);
		StringBuilder sb = new StringBuilder();
		sb.append("1"); // for reading site
		sb.append(StringUtils.CARET);
		sb.append(delete == true ? "0" : "1");
		sb.append(StringUtils.CARET);
		sb.append(readingSite.getSiteId());
		if(!delete)
		{
			sb.append(StringUtils.CARET);
			sb.append(readingSite.getReadingSiteType().getValue());
			sb.append(StringUtils.CARET);
			sb.append(readingSite.isActive() == true ? "1" : "0");
		}		
		query.addParameter(VistaQuery.LITERAL, sb.toString());
		return query;
	}
	
	public static VistaQuery createGetTemplateVistaQuery(String apSection)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_TEMPLATE_XML);
		query.addParameter(VistaQuery.LITERAL, apSection);
		return query;
	}
	
	public static VistaQuery createPutConsultVistaQuery(PathologyCaseURN pathologyCaseUrn, 
			PathologyCaseAssistance assistanceType, String stationNumber)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_PUT_CONSULT);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.toStringAccessionNumber());
		query.addParameter(VistaQuery.LITERAL, assistanceType.getValue());
		query.addParameter(VistaQuery.LITERAL, stationNumber);
		return query;
	}
	
	public static VistaQuery createLockCaseVistaQuery(PathologyCaseURN pathologyCaseUrn, boolean lock)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_SECOND_LOCK);
		query.addParameter(VistaQuery.LITERAL, (lock == true ? "1" : "0"));
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createReserveCaseVistaQuery(PathologyCaseURN pathologyCaseUrn, boolean lock)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_RESERVE_CASE);
		query.addParameter(VistaQuery.LITERAL, (lock == true ? "1" : "0"));
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createGetPatientCasesVistaQuery(String patientDfn, String requestingSiteId)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_PATIENT);
		query.addParameter(VistaQuery.LITERAL, patientDfn);
		if(requestingSiteId != null)
			query.addParameter(VistaQuery.LITERAL, requestingSiteId);
		return query;
	}
	
	public static VistaQuery createGetSitesVistaQuery(boolean reading)
	{
		// 0 = acquisition, 1 = reading
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_SITE_CONFIG);
		query.addParameter(VistaQuery.LITERAL, reading == true ? "1" : "0");
		return query;
	}

	public static VistaQuery createGetCasesVistaQuery(boolean released, int days, String requestingSiteId)
	{
		//FLAG = 0: Unreleased / 1: Released
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_ACTIVE);
		query.addParameter(VistaQuery.LITERAL, released == true ? "1" : "0");
		//if(days > 0)
		query.addParameter(VistaQuery.LITERAL, days + "");
		if(requestingSiteId != null)
			query.addParameter(VistaQuery.LITERAL, requestingSiteId);	
		return query;
	}
	
	public static VistaQuery createGetSlidesVistaQuery(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_SLIDES);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getPathologyType());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getYear());
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.getNumber());
		return query;
	}
	
	public static VistaQuery createGetConsultStatusQuery(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_CONSULT);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.toStringAccessionNumber());
		return query;
	}
	
	public static VistaQuery createGetCaseSlideInformationQuery(PathologyCaseURN pathologyCaseUrn)
	{
		VistaQuery query = new VistaQuery(RPC_MAGTP_GET_SLIDES_INFO);
		query.addParameter(VistaQuery.LITERAL, pathologyCaseUrn.toStringAccessionNumber());
		return query;
	}
	
}

