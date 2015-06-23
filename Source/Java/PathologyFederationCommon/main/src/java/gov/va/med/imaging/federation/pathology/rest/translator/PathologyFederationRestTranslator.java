/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 20, 2012
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
package gov.va.med.imaging.federation.pathology.rest.translator;

import gov.va.med.URNFactory;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseConsultationUpdateStatusType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseReportFieldType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseReserveResultType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSaveSupplementalReportType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSlideType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSupplementalReportType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseTemplateFieldType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseTemplateType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationAcquisitionSiteType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseAssistanceType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseSpecimenType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCaseUpdateAttributeResultType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationConsultationType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCptCodeResultType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationCptCodeType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationElectronicSignatureNeedStatusType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationElectronicSignatureNeedType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationFieldType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationFieldValueType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationReadingSiteType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationReadingSiteTypeType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationSaveCaseReportResultType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationSiteType;
import gov.va.med.imaging.federation.pathology.rest.types.PathologyFederationSnomedCodeType;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseConsultation;
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseSlide;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseSupplementalReport;
import gov.va.med.imaging.pathology.PathologyCaseTemplate;
import gov.va.med.imaging.pathology.PathologyCaseTemplateField;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.PathologyCptCode;
import gov.va.med.imaging.pathology.PathologyCptCodeResult;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.PathologyFieldValue;
import gov.va.med.imaging.pathology.PathologyReadingSite;
import gov.va.med.imaging.pathology.PathologySaveCaseReportResult;
import gov.va.med.imaging.pathology.PathologySite;
import gov.va.med.imaging.pathology.PathologySnomedCode;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus;
import gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeed;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus;
import gov.va.med.imaging.pathology.enums.PathologyField;
import gov.va.med.imaging.rest.types.RestCoreTranslator;
import gov.va.med.imaging.rest.types.RestStringArrayType;
import gov.va.med.imaging.rest.types.RestStringType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Translator for Pathology Federation objects
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PathologyFederationRestTranslator
{
	private final static Logger logger = Logger.getLogger(PathologyFederationRestTranslator.class);
	
	private static Map<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyFederationReadingSiteTypeType> readingSiteTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyCaseAssistance, PathologyFederationCaseAssistanceType> caseAssistanceTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyField, PathologyFederationFieldType> fieldTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus, PathologyFederationElectronicSignatureNeedStatusType> electronicSignatureNeededTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus, PathologyFederationCaseConsultationUpdateStatusType> caseConsultationStatusTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult, PathologyFederationCaseReserveResultType> caseReserveResultTypeMap;
	
	static
	{
		readingSiteTypeMap = new HashMap<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyFederationReadingSiteTypeType>();
		readingSiteTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType.interpretation, PathologyFederationReadingSiteTypeType.interpretation);
		readingSiteTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType.consultation, PathologyFederationReadingSiteTypeType.consultation);
		readingSiteTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType.both, PathologyFederationReadingSiteTypeType.both);
		
		caseAssistanceTypeMap = new HashMap<PathologyCaseAssistance, PathologyFederationCaseAssistanceType>();
		caseAssistanceTypeMap.put(PathologyCaseAssistance.consultation, PathologyFederationCaseAssistanceType.consultation);
		caseAssistanceTypeMap.put(PathologyCaseAssistance.interpretation, PathologyFederationCaseAssistanceType.interpretation);
		
		fieldTypeMap = new HashMap<PathologyField, PathologyFederationFieldType>();
		fieldTypeMap.put(PathologyField.disease, PathologyFederationFieldType.disease);
		fieldTypeMap.put(PathologyField.etiology, PathologyFederationFieldType.etiology);
		fieldTypeMap.put(PathologyField.function, PathologyFederationFieldType.function);
		fieldTypeMap.put(PathologyField.location, PathologyFederationFieldType.location);
		fieldTypeMap.put(PathologyField.morphology, PathologyFederationFieldType.morphology);
		fieldTypeMap.put(PathologyField.procedure, PathologyFederationFieldType.procedure);
		fieldTypeMap.put(PathologyField.topography, PathologyFederationFieldType.topography);
		fieldTypeMap.put(PathologyField.users, PathologyFederationFieldType.users);
		fieldTypeMap.put(PathologyField.workload, PathologyFederationFieldType.workload);
		fieldTypeMap.put(PathologyField.cpt, PathologyFederationFieldType.cpt);
		
		electronicSignatureNeededTypeMap = new HashMap<PathologyElectronicSignatureNeedStatus, PathologyFederationElectronicSignatureNeedStatusType>();
		electronicSignatureNeededTypeMap.put(PathologyElectronicSignatureNeedStatus.authorized_needs_signature, 
				PathologyFederationElectronicSignatureNeedStatusType.authorized_needs_signature);
		electronicSignatureNeededTypeMap.put(PathologyElectronicSignatureNeedStatus.not_enabled, 
				PathologyFederationElectronicSignatureNeedStatusType.not_enabled);
		electronicSignatureNeededTypeMap.put(PathologyElectronicSignatureNeedStatus.not_authorized,
				PathologyFederationElectronicSignatureNeedStatusType.not_authorized);
		
		caseConsultationStatusTypeMap = new HashMap<PathologyCaseConsultationUpdateStatus, PathologyFederationCaseConsultationUpdateStatusType>();
		caseConsultationStatusTypeMap.put(PathologyCaseConsultationUpdateStatus.completed, PathologyFederationCaseConsultationUpdateStatusType.completed);
		caseConsultationStatusTypeMap.put(PathologyCaseConsultationUpdateStatus.recalled, PathologyFederationCaseConsultationUpdateStatusType.recalled);
		caseConsultationStatusTypeMap.put(PathologyCaseConsultationUpdateStatus.pending, PathologyFederationCaseConsultationUpdateStatusType.pending);
		caseConsultationStatusTypeMap.put(PathologyCaseConsultationUpdateStatus.refused, PathologyFederationCaseConsultationUpdateStatusType.refused);		
		
		caseReserveResultTypeMap = new HashMap<PathologyCaseReserveResult, PathologyFederationCaseReserveResultType>();
		caseReserveResultTypeMap.put(PathologyCaseReserveResult.case_reserved, PathologyFederationCaseReserveResultType.case_reserved);
		caseReserveResultTypeMap.put(PathologyCaseReserveResult.reservation_ended, PathologyFederationCaseReserveResultType.reservation_ended);
	}
	
	public static PathologyFederationElectronicSignatureNeedType translate(PathologyElectronicSignatureNeed electronicSignatureNeed)
	{
		if(electronicSignatureNeed == null)
			return null;
		return new PathologyFederationElectronicSignatureNeedType(translate(electronicSignatureNeed.getStatus()), 
				electronicSignatureNeed.getMessage());
	}
	
	public static PathologyElectronicSignatureNeed translate(PathologyFederationElectronicSignatureNeedType electronicSignatureNeed)
	{
		if(electronicSignatureNeed == null)
			return null;
		return new PathologyElectronicSignatureNeed(translate(electronicSignatureNeed.getStatus()), 
				electronicSignatureNeed.getMessage());
	}
	
	public static PathologyFederationCptCodeResultType[] translateCptCodeResults(List<PathologyCptCodeResult> cptCodes)
	{
		if(cptCodes == null)
			return null;
		
		PathologyFederationCptCodeResultType[] result = new PathologyFederationCptCodeResultType[cptCodes.size()];
		for(int i = 0; i < cptCodes.size(); i++)
		{
			PathologyCptCodeResult cptCodeResult = cptCodes.get(i);
			result[i] = new PathologyFederationCptCodeResultType(cptCodeResult.getCptCode(), cptCodeResult.isSuccessfullyAdded(),
					cptCodeResult.getDescription());
		}
		
		return result;
	}
	
	public static List<PathologyCptCodeResult> translate(PathologyFederationCptCodeResultType [] cptCodes)
	{
		if(cptCodes == null)
			return null;
		List<PathologyCptCodeResult> result = new ArrayList<PathologyCptCodeResult>();
		for(PathologyFederationCptCodeResultType cptCode : cptCodes)
		{
			result.add(new PathologyCptCodeResult(cptCode.getCptCode(), cptCode.isSuccessfullyAdded(), cptCode.getDescription()));
		}
		
		return result;
	}
	
	public static PathologyCaseURN translateCaseId(RestStringType caseId)
	throws MethodException
	{
		if(caseId == null)
			throw new MethodException("Null case ID returned");
		try
		{
			return URNFactory.create(caseId.getValue(), PathologyCaseURN.class);
		}
		catch(URNFormatException urnfX)
		{
			throw new MethodException(urnfX);
		}
	}
	
	public static List<PathologyFieldURN> translateFieldIds(RestStringArrayType fieldIds)
	throws MethodException
	{
		if(fieldIds == null)
			return null;
		List<PathologyFieldURN> result = new ArrayList<PathologyFieldURN>();
		for(String fieldId : fieldIds.getValue())
		{
			try
			{
				result.add(URNFactory.create(fieldId, PathologyFieldURN.class));
			}
			catch(URNFormatException urnfX)
			{
				throw new MethodException(urnfX);
			}
		}
		return result;
	}
	
	public static RestStringArrayType translateFieldIds(List<PathologyFieldURN> fieldUrns)
	{
		if(fieldUrns == null)
			return null;
		String [] result = new String[fieldUrns.size()];
		for(int i = 0; i < fieldUrns.size(); i++)
		{
			result[i] = fieldUrns.get(i).toString();
		}
		
		return new RestStringArrayType(result);
	}
	
	public static List<PathologyCaseURN> translateCaseIds(RestStringArrayType caseUrns)
	throws MethodException
	{
		if(caseUrns == null)
			return null;
		List<PathologyCaseURN> result = new ArrayList<PathologyCaseURN>();
		for(String urn : caseUrns.getValue())
		{
			try
			{
				result.add(URNFactory.create(urn, PathologyCaseURN.class));
			}
			catch(URNFormatException urnfX)
			{
				throw new MethodException(urnfX);
			}
		}
		return result;
	}
	
	public static PathologyFederationCptCodeType [] translateCptCodes(List<PathologyCptCode> cptCodes)
	{
		if(cptCodes == null)
			return null;
		PathologyFederationCptCodeType [] result = new PathologyFederationCptCodeType[cptCodes.size()];
		for(int i = 0; i < cptCodes.size(); i++)
		{
			result[i] = translate(cptCodes.get(i));
		}
		return result;
	}
	
	private static PathologyFederationCptCodeType translate(PathologyCptCode cptCode)
	{
		return new PathologyFederationCptCodeType(cptCode.getCptCode(), 
				cptCode.getDescription(), cptCode.getMultiplyFactor(), 
				cptCode.getDateEntered(), cptCode.getUser());
	}
	
	public static List<PathologyCptCode> translate(PathologyFederationCptCodeType [] cptCodes)
	{
		if(cptCodes == null)
			return null;
		List<PathologyCptCode> result = new ArrayList<PathologyCptCode>();
		for(PathologyFederationCptCodeType cptCode : cptCodes)
		{
			result.add(translate(cptCode));
		}
		
		return result;
	}
	
	private static PathologyCptCode translate(PathologyFederationCptCodeType cptCode)
	{
		return new PathologyCptCode(cptCode.getCptCode(), cptCode.getDescription(), cptCode.getMultiplyFactor(),
				cptCode.getDateEntered(), cptCode.getUser());
	}
	
	public static RestStringArrayType translateCaseUrns(List<PathologyCaseURN> caseUrns)
	{
		if(caseUrns == null)
			return null;
		String [] result = new String[caseUrns.size()];
		for(int i = 0; i < caseUrns.size(); i++)
		{
			result[i] = caseUrns.get(i).toString();
		}
		
		return new RestStringArrayType(result);
	}
	
	public static PathologyFederationSiteType [] translateAllPathologySites(List<PathologySite> sites)
	{
		if(sites == null)
			return null;
		PathologyFederationSiteType [] result = new PathologyFederationSiteType[sites.size()];
		for(int i = 0; i < sites.size(); i++)
		{
			result[i] = translate(sites.get(i));
		}
		
		return result;
	}
	
	private static PathologyFederationSiteType translate(PathologySite site)
	{
		return new PathologyFederationSiteType(site.getSiteId(), 
				site.getSiteName(), site.getStationNumber(), site.getSiteAbbr());
	}
	
	public static List<PathologySite> translate(PathologyFederationSiteType [] sites)
	{
		if(sites == null)
			return null;
		List<PathologySite> result = new ArrayList<PathologySite>();
		for(PathologyFederationSiteType site : sites)
		{
			result.add(translate(site));
		}
		
		return result;
	}
	
	private static PathologySite translate(PathologyFederationSiteType site)
	{
		return new PathologySite(site.getSiteId(), site.getSiteName(), site.getStationNumber(), site.getSiteAbbr());
	}
	
	public static PathologyFederationCaseSaveSupplementalReportType translate(String reportContents, Date date)
	{
		return new PathologyFederationCaseSaveSupplementalReportType(reportContents, date);
	}
	
	public static List<PathologyCaseReportField> translateCaseReportFields(PathologyFederationCaseReportFieldType [] fields)
	{
		if(fields == null)
			return null;
		List<PathologyCaseReportField> result = new ArrayList<PathologyCaseReportField>();
		for(PathologyFederationCaseReportFieldType field : fields)
		{
			result.add(translate(field));
		}
		
		return result;
	}
	
	private static PathologyCaseReportField translate(PathologyFederationCaseReportFieldType field)
	{
		List<String> values = new ArrayList<String>();
		for(String value : field.getValues())
		{
			values.add(value);
		}
		return new PathologyCaseReportField(field.getFieldNumber(), values);
	}
	
	public static PathologyFederationCaseReportFieldType [] translateCaseReportFields(List<PathologyCaseReportField> fields)
	{
		if(fields == null)
			return null;
		
		PathologyFederationCaseReportFieldType [] result = new PathologyFederationCaseReportFieldType[fields.size()];
		
		for(int i = 0; i < fields.size(); i++)
		{
			result[i] = translate(fields.get(i));
		}
		
		return result;
	}
	
	private static PathologyFederationCaseReportFieldType translate(PathologyCaseReportField field)
	{
		return new PathologyFederationCaseReportFieldType(field.getFieldNumber(), field.getValues().toArray(new String[field.getValues().size()]));
	}
	
	public static PathologyFederationCaseConsultationUpdateStatusType translate(PathologyCaseConsultationUpdateStatus fieldType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus, PathologyFederationCaseConsultationUpdateStatusType> entry : PathologyFederationRestTranslator.caseConsultationStatusTypeMap.entrySet() )
			if( entry.getKey() == fieldType )
				return entry.getValue();
		
		return null;
	}
	
	public static PathologyCaseConsultationUpdateStatus translate(PathologyFederationCaseConsultationUpdateStatusType fieldType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus, PathologyFederationCaseConsultationUpdateStatusType> entry : PathologyFederationRestTranslator.caseConsultationStatusTypeMap.entrySet() )
			if( entry.getValue() == fieldType )
				return entry.getKey();
		
		return null;
	}
	
	public static PathologyFederationFieldValueType [] translateFieldValues(List<PathologyFieldValue> fieldValues)
	{
		if(fieldValues == null)
			return null;
		
		PathologyFederationFieldValueType [] result = new PathologyFederationFieldValueType[fieldValues.size()];
		for(int i = 0; i < fieldValues.size(); i++)
		{
			result[i] = translate(fieldValues.get(i));
		}
		
		return result;
	}
	
	private static PathologyFederationFieldValueType translate(PathologyFieldValue fieldValue)
	{
		return new PathologyFederationFieldValueType(fieldValue.getFieldUrn().toString(), fieldValue.getName());
	}
	
	public static List<PathologyFieldValue> translate(PathologyFederationFieldValueType [] fieldValues)
	throws MethodException
	{
		if(fieldValues == null)
			return null;
		List<PathologyFieldValue> result = new ArrayList<PathologyFieldValue>();
		for(PathologyFederationFieldValueType fieldValue : fieldValues)
		{
			result.add(translate(fieldValue));
		}
		return result;
	}
	
	private static PathologyFieldValue translate(PathologyFederationFieldValueType fieldValue)
	throws MethodException
	{
		try
		{
			PathologyFieldURN fieldUrn = URNFactory.create(fieldValue.getFieldId(), PathologyFieldURN.class);
			return new PathologyFieldValue(fieldUrn, fieldValue.getName());
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating pathology field value", urnfX);
			throw new MethodException(urnfX);
		}
	}
	
	public static PathologyFederationCaseTemplateType translate(PathologyCaseTemplate template)
	{
		if(template == null)
			return null;
		
		List<PathologyCaseTemplateField> fields = template.getFields();
		
		PathologyFederationCaseTemplateFieldType [] result = new PathologyFederationCaseTemplateFieldType[fields.size()];
		for(int i = 0; i < fields.size(); i++)
		{
			result[i] = translate(fields.get(i));
		}
		return new PathologyFederationCaseTemplateType(result);
	}
	
	private static PathologyFederationCaseTemplateFieldType translate(PathologyCaseTemplateField field)
	{
		RestStringArrayType values = RestCoreTranslator.translateStrings(field.getValues());
		return new PathologyFederationCaseTemplateFieldType(field.getFieldNumber(), field.getLabel(), 
				values);
	}
	
	public static PathologyCaseTemplate translate(PathologyFederationCaseTemplateType template)
	{
		if(template == null)
			return null;
		
		List<PathologyCaseTemplateField> fields = new ArrayList<PathologyCaseTemplateField>();
		for(PathologyFederationCaseTemplateFieldType field : template.getFields())
		{
			fields.add(translate(field));
		}
		
		return new PathologyCaseTemplate(fields);
	}
	
	private static PathologyCaseTemplateField translate(PathologyFederationCaseTemplateFieldType field)
	{
		List<String> values = new ArrayList<String>();
		// Jersey seems to convert empty string arrays into null responses
		if(field.getValues().getValue() != null)
		{			
			for(String value : field.getValues().getValue())
				values.add(value);
		}
		return new PathologyCaseTemplateField(field.getFieldNumber(), field.getLabel(), values); 
	}
	
	public static PathologyFederationCaseSupplementalReportType [] translateSupplementalReports(List<PathologyCaseSupplementalReport> supplementalReports)
	{
		if(supplementalReports == null)
			return null;
	
		PathologyFederationCaseSupplementalReportType [] result = 
			new PathologyFederationCaseSupplementalReportType[supplementalReports.size()];
		
		for(int i = 0; i < supplementalReports.size(); i++)
		{
			result[i] = translate(supplementalReports.get(i));
		}
		
		return result;
	}
	
	private static PathologyFederationCaseSupplementalReportType translate(PathologyCaseSupplementalReport supplementalReport)
	{
		return new PathologyFederationCaseSupplementalReportType(supplementalReport.getSupplementalReportDate(),
				supplementalReport.isVerified(), supplementalReport.getVerifiedProvider(), supplementalReport.getValues());
	}
	
	public static List<PathologyCaseSupplementalReport> translate(PathologyFederationCaseSupplementalReportType [] supplementalReports)
	{
		if(supplementalReports == null)
			return null;
		List<PathologyCaseSupplementalReport> result = new ArrayList<PathologyCaseSupplementalReport>();
		for(PathologyFederationCaseSupplementalReportType supplementalReport : supplementalReports)
		{
			result.add(translate(supplementalReport));
		}
		return result;		
	}

	private static PathologyCaseSupplementalReport translate(PathologyFederationCaseSupplementalReportType supplementalReport)
	{
		return new PathologyCaseSupplementalReport(supplementalReport.getSupplementalReportDate(),
				supplementalReport.isVerified(), supplementalReport.getVerifiedProvider(), 
				supplementalReport.getValues());
	}
	
	public static PathologyFederationSnomedCodeType [] translateSnomedCodes(List<PathologySnomedCode> snomedCodes)
	{
		if(snomedCodes == null)
			return null;
		PathologyFederationSnomedCodeType [] result = new PathologyFederationSnomedCodeType[snomedCodes.size()];
		for(int i = 0; i < snomedCodes.size(); i++)
		{
			result[i] = translate(snomedCodes.get(i));
		}
		
		return result;
	}
	
	private static PathologyFederationSnomedCodeType translate(PathologySnomedCode snomedCode)
	{
		return new PathologyFederationSnomedCodeType(snomedCode.getTissueId(), snomedCode.getTissueCode(), snomedCode.getTissue(),
				translate(snomedCode.getField()), snomedCode.getSnomedCode(), snomedCode.getSnomedValue(), snomedCode.getSnomedId(),
				snomedCode.getEtiologyId(), snomedCode.getEtiologySnomedCode(), snomedCode.getEtiologySnomedValue());
	}
	
	public static List<PathologySnomedCode> translate(PathologyFederationSnomedCodeType [] snomedCodes)
	{
		if(snomedCodes == null)
			return null;
		List<PathologySnomedCode> result = new ArrayList<PathologySnomedCode>();
		for(PathologyFederationSnomedCodeType snomedCode : snomedCodes)
		{
			result.add(translate(snomedCode));
		}
		return result;
	}
	
	private static PathologySnomedCode translate(PathologyFederationSnomedCodeType snomedCode)
	{
		if(snomedCode.getEtiologySnomedValue() != null && snomedCode.getEtiologySnomedValue().length() > 0)
		{
			return PathologySnomedCode.createMorphologySnomedCode(snomedCode.getTissueId(), snomedCode.getTissueCode(),
					snomedCode.getTissue(),snomedCode.getSnomedId(), snomedCode.getSnomedCode(), snomedCode.getSnomedValue(),
					snomedCode.getEtiologyId(), snomedCode.getEtiologySnomedCode(), snomedCode.getEtiologySnomedValue());
		}
		else if(snomedCode.getField() == PathologyFederationFieldType.morphology)
		{
			return PathologySnomedCode.createMorphologySnomedCode(snomedCode.getTissueId(), snomedCode.getTissueCode(), snomedCode.getTissue(),
					snomedCode.getSnomedId(), snomedCode.getSnomedCode(), snomedCode.getSnomedValue());
		}
		else if(snomedCode.getSnomedValue() != null && snomedCode.getSnomedValue().length() > 0)
		{
			return PathologySnomedCode.createSnomedCode(snomedCode.getTissueId(), snomedCode.getTissueCode(), snomedCode.getTissue(),
					snomedCode.getSnomedId(), translate(snomedCode.getField()), snomedCode.getSnomedCode(), snomedCode.getSnomedValue());
		}
		else
		{
			return PathologySnomedCode.createTissue(snomedCode.getTissueId(), snomedCode.getTissueCode(), snomedCode.getTissue());
		}
				
	}
	
	public static PathologyElectronicSignatureNeedStatus translate(PathologyFederationElectronicSignatureNeedStatusType electronicSignatureNeededType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus, PathologyFederationElectronicSignatureNeedStatusType> entry : PathologyFederationRestTranslator.electronicSignatureNeededTypeMap.entrySet() )
			if( entry.getValue() == electronicSignatureNeededType )
				return entry.getKey();
		
		return null;
	}
	
	public static PathologyFederationElectronicSignatureNeedStatusType translate(PathologyElectronicSignatureNeedStatus electronicSignatureNeededType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus, PathologyFederationElectronicSignatureNeedStatusType> entry : PathologyFederationRestTranslator.electronicSignatureNeededTypeMap.entrySet() )
			if( entry.getKey() == electronicSignatureNeededType )
				return entry.getValue();
		
		return null;
	}
	
	public static PathologyField translate(PathologyFederationFieldType fieldType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyField, PathologyFederationFieldType> entry : PathologyFederationRestTranslator.fieldTypeMap.entrySet() )
			if( entry.getValue() == fieldType )
				return entry.getKey();
		
		return null;
	}
	
	public static PathologyFederationFieldType translate(PathologyField field)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyField, PathologyFederationFieldType> entry : PathologyFederationRestTranslator.fieldTypeMap.entrySet() )
			if( entry.getKey() == field )
				return entry.getValue();
		
		return null;
	}
	
	public static PathologyFederationCaseSpecimenType [] translateCaseSpecimens(List<PathologyCaseSpecimen> specimens)
	{
		if(specimens == null)
			return null;
		PathologyFederationCaseSpecimenType [] result = new PathologyFederationCaseSpecimenType[specimens.size()];
		for(int i = 0; i < specimens.size(); i++)
		{
			result[i] = translate(specimens.get(i));
		}
		return result;
	}
	
	private static PathologyFederationCaseSpecimenType translate(PathologyCaseSpecimen specimen)
	{
		return new PathologyFederationCaseSpecimenType(specimen.getSpecimen(), 
				specimen.getSmearPrep(), specimen.getStain(), specimen.getNumSlides(), 
				specimen.getLastStainDate());
	}
	
	public static PathologyFederationCaseType[] translateCases(List<PathologyCase> cases)
	{
		if(cases == null)
			return null;
		PathologyFederationCaseType[] result = new PathologyFederationCaseType[cases.size()];
		for(int i = 0; i < cases.size(); i++)
		{
			result[i] = translate(cases.get(i));
		}
		return result;
	}
	
	private static PathologyFederationCaseType translate(PathologyCase pathologyCase)
	{
		PathologyFederationCaseType result = new PathologyFederationCaseType(pathologyCase.getPathologyCaseUrn().toString(),
				pathologyCase.getAccessionNumber(), pathologyCase.getReserved(), pathologyCase.getReservedBy(), pathologyCase.getPatientName(),
				RestCoreTranslator.translate(pathologyCase.getPatientIdentifier()),
				pathologyCase.getPriority(),pathologyCase.isSlidesAvailable(), pathologyCase.getSpecimenTakenDate(), 
				pathologyCase.getStatus(), pathologyCase.getSiteAbbr(), pathologyCase.getSpecimenCount(), pathologyCase.getPatientSsn(), 
				pathologyCase.getMethod(), pathologyCase.isNoteAttached(), pathologyCase.isPatientSensitive(), 
				pathologyCase.getNumberOfImages());
		
		if(pathologyCase.getConsultations() != null)
		{
			List<PathologyCaseConsultation> consultations = pathologyCase.getConsultations();
			result.setConsultations(translateConsultations(consultations));
		}
		
		return result;
	}
	
	private static PathologyFederationConsultationType [] translateConsultations(List<PathologyCaseConsultation> consultations)
	{
		PathologyFederationConsultationType [] result = new PathologyFederationConsultationType [consultations.size()];
		for(int i = 0; i < consultations.size(); i++)
		{
			result[i] = translate(consultations.get(i));
		}
		return result;
	}
	
	private static PathologyFederationConsultationType translate(PathologyCaseConsultation consultation)
	{
		return new PathologyFederationConsultationType(consultation.getPathologyCaseConsultationUrn().toString(), 
				consultation.getType(), consultation.getReservationDate(), consultation.getInterpretingStation(), 
				consultation.getSiteAbbr(), consultation.getStatus());
	}
	
	private static List<PathologyCaseConsultation> translate(PathologyFederationConsultationType [] consultations)
	throws URNFormatException
	{
		if(consultations == null)
			return null;
		List<PathologyCaseConsultation> result = new ArrayList<PathologyCaseConsultation>();
		for(PathologyFederationConsultationType consultation : consultations)
			result.add(translate(consultation));
		return result;
	}
	
	private static PathologyCaseConsultation translate(PathologyFederationConsultationType consultation)
	throws URNFormatException
	{
		PathologyCaseConsultationURN urn = 
				URNFactory.create(consultation.getConsultationId(), PathologyCaseConsultationURN.class);
		return new PathologyCaseConsultation(urn, consultation.getType(), consultation.getReservationDate(),
				consultation.getInterpretingStation(), consultation.getSiteAbbr(), consultation.getStatus());
	}
	
	public static PathologyFederationCaseAssistanceType translate(PathologyCaseAssistance caseAssistance)
	{
		for( Entry<PathologyCaseAssistance, PathologyFederationCaseAssistanceType> entry : PathologyFederationRestTranslator.caseAssistanceTypeMap.entrySet() )
			if( entry.getKey() == caseAssistance)
				return entry.getValue();
		
		return null;
	}
	
	public static PathologyCaseAssistance translate(PathologyFederationCaseAssistanceType caseAssistance)
	{
		for( Entry<PathologyCaseAssistance, PathologyFederationCaseAssistanceType> entry : PathologyFederationRestTranslator.caseAssistanceTypeMap.entrySet() )
			if( entry.getValue() == caseAssistance)
				return entry.getKey();
		
		return null;
	}
	
	public static List<String> translateStringArrayToList(String [] input)
	{
		if(input == null)
			return null;
		List<String> result = new ArrayList<String>();
		for(String in : input)
			result.add(in);
		return result;
	}
	
	public static String[] translateStringListToArray(List<String> input)
	{
		if(input == null)
			return null;
		String [] result = new String[input.size()];
		for(int i = 0; i < input.size(); i++)
		{
			result[i] = input.get(i);
		}
		return result;
	}
	
	public static String translateStringListToDelimitedString(List<String> input, String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for(String in : input)
		{
			sb.append(prefix);
			sb.append(in);
			prefix = delimiter;
		}
		return sb.toString();
	}
	
	public static PathologyFederationCaseUpdateAttributeResultType translate(PathologyCaseUpdateAttributeResult attribute)
	{
		if(attribute == null)
			return null;
		if(attribute.isSuccess())
			return new PathologyFederationCaseUpdateAttributeResultType(true, null);
		return new PathologyFederationCaseUpdateAttributeResultType(false, attribute.getErrorMessage());
	}
	
	public static PathologyCaseUpdateAttributeResult translate(PathologyFederationCaseUpdateAttributeResultType attribute)
	{
		if(attribute == null)
			return null;
		if(attribute.isSuccess())
			return PathologyCaseUpdateAttributeResult.createSuccessfulLockResult();
		return PathologyCaseUpdateAttributeResult.createFailedLockResult(attribute.getErrorMessage());
	}
	
	private static PathologyFederationReadingSiteTypeType translate(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType readingSiteType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyFederationReadingSiteTypeType> entry : PathologyFederationRestTranslator.readingSiteTypeMap.entrySet() )
			if( entry.getKey() == readingSiteType )
				return entry.getValue();
		
		return null;
	}
	
	private static gov.va.med.imaging.pathology.enums.PathologyReadingSiteType translate(PathologyFederationReadingSiteTypeType readingSiteType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyFederationReadingSiteTypeType> entry : PathologyFederationRestTranslator.readingSiteTypeMap.entrySet() )
			if(entry.getValue() == readingSiteType)
				return entry.getKey();
		
		return null;
	}
	
	
	/*
	public static PathologyFederationAbstractSiteType [] translatePathologySites(List<AbstractPathologySite> sites)
	throws MethodException
	{
		if(sites == null)
			return null;
		PathologyFederationAbstractSiteType [] result = new PathologyFederationAbstractSiteType[sites.size()];
		for(int i = 0; i < sites.size(); i++)
		{
			result[i] = translate(sites.get(i));
		}
		return result;
	}
	
	
	private static PathologyFederationAbstractSiteType translate(AbstractPathologySite site)
	throws MethodException
	{
		if(site instanceof PathologyAcquisitionSite)
		{
			PathologyAcquisitionSite acqSite = (PathologyAcquisitionSite)site;
			return new PathologyFederationAcquisitionSiteType(acqSite.getSiteId(), acqSite.getSiteName(), 
					acqSite.getSiteAbbr(), acqSite.isActive(), acqSite.getPrimarySiteStationNumber(), 
					acqSite.getPrimarySiteName(), acqSite.getPrimarySiteAbbr());
		}
		else if(site instanceof PathologyReadingSite)
		{
			PathologyReadingSite readSite = (PathologyReadingSite)site;
			return new PathologyFederationReadingSiteType(readSite.getSiteId(), readSite.getSiteName(), 
					readSite.getSiteAbbr(), readSite.isActive(), translate(readSite.getReadingSiteType()));
		}
		throw new MethodException("Site not an acquisition or reading site!");
	}	*/
	/*
	public static List<AbstractPathologySite> translate(PathologyFederationReadingSiteType [] readingSites)
	throws MethodException
	{
		if(readingSites == null)
			return null;
		List<AbstractPathologySite> result = new ArrayList<AbstractPathologySite>();
		for(PathologyFederationAbstractSiteType site : sites)
		{
			result.add(translate(site));
		}
		
		return result;
	}
	
	private static AbstractPathologySite translate(PathologyFederationAbstractSiteType site)
	throws MethodException
	{
		if(site instanceof PathologyFederationAcquisitionSiteType)
		{
			PathologyFederationAcquisitionSiteType acqSite = (PathologyFederationAcquisitionSiteType)site;
			return translate(acqSite);
		}
		else if(site instanceof PathologyFederationReadingSiteType)
		{
			PathologyFederationReadingSiteType readingSite = (PathologyFederationReadingSiteType)site;
			return translate(readingSite);			
		}
		throw new MethodException("Site not an acquisition or reading site!");
	}*/
	
	public static PathologyFederationAcquisitionSiteType [] translateAbstractSitesToAcquisitionSites(List<AbstractPathologySite> acquisitionSites)
	{
		if(acquisitionSites == null)
			return null;
		PathologyFederationAcquisitionSiteType [] result = new PathologyFederationAcquisitionSiteType [acquisitionSites.size()];
		for(int i = 0; i < acquisitionSites.size(); i++)
		{
			PathologyAcquisitionSite acquisitionSite = (PathologyAcquisitionSite)acquisitionSites.get(i);
			result[i] = translate(acquisitionSite);
		}
		return result;
	}
	
	/*
	public static PathologyFederationAcquisitionSiteType [] translateAcquisitionSiteList(List<PathologyAcquisitionSite> acquisitionSites)
	{
		if(acquisitionSites == null)
			return null;
		PathologyFederationAcquisitionSiteType [] result = new PathologyFederationAcquisitionSiteType [acquisitionSites.size()];
		for(int i = 0; i < acquisitionSites.size(); i++)
		{
			result[i] = translate(acquisitionSites.get(i));
		}
		return result;
	}*/
	
	public static PathologyFederationAcquisitionSiteType translate(PathologyAcquisitionSite acquisitionSite)
	{
		return new PathologyFederationAcquisitionSiteType(acquisitionSite.getSiteId(), acquisitionSite.getSiteName(), 
				acquisitionSite.getSiteAbbr(), acquisitionSite.isActive(), acquisitionSite.getPrimarySiteStationNumber(),
				acquisitionSite.getPrimarySiteName(), acquisitionSite.getPrimarySiteAbbr());
	}
	
	public static List<PathologyAcquisitionSite> translate(PathologyFederationAcquisitionSiteType [] acquisitionSites)
	{
		if(acquisitionSites == null)
			return null;
		List<PathologyAcquisitionSite> result = new ArrayList<PathologyAcquisitionSite>();
		for(PathologyFederationAcquisitionSiteType acquisitionSite : acquisitionSites)
		{
			result.add(translate(acquisitionSite));
		}
		return result;
	}
	
	public static PathologyAcquisitionSite translate(PathologyFederationAcquisitionSiteType acquisitionSite)
	{
		return new PathologyAcquisitionSite(acquisitionSite.getSiteId(), acquisitionSite.getSiteName(), 
				acquisitionSite.getSiteAbbr(), acquisitionSite.isActive(), acquisitionSite.getPrimarySiteId(), 
				acquisitionSite.getPrimarySiteName(), acquisitionSite.getPrimarySiteAbbr());
	}

	public static List<PathologyReadingSite> translate(PathologyFederationReadingSiteType [] readingSites)
	{
		if(readingSites == null)
			return null;
		List<PathologyReadingSite> result = new ArrayList<PathologyReadingSite>();
		for(PathologyFederationReadingSiteType readingSite : readingSites)
		{
			result.add(translate(readingSite));
		}
		return result;
	}
	
	public static PathologyReadingSite translate(PathologyFederationReadingSiteType readingSite)
	{
		return new PathologyReadingSite(readingSite.getSiteId(), readingSite.getSiteName(), 
				readingSite.getSiteAbbr(), readingSite.isActive(), translate(readingSite.getReadingSiteType()));
	}
	
	public static PathologyFederationReadingSiteType [] translateAbstractSitesToReadingSites(List<AbstractPathologySite> readingSites)
	{
		if(readingSites == null)
			return null;
		PathologyFederationReadingSiteType [] result = new PathologyFederationReadingSiteType [readingSites.size()];
		for(int i = 0; i < readingSites.size(); i++)
		{
			PathologyReadingSite readingSite = (PathologyReadingSite)readingSites.get(i);
			result[i] = translate(readingSite); 
		}
		return result;
	}
	
	/*
	public static PathologyFederationReadingSiteType [] translateReadingSiteList(List<PathologyReadingSite> readingSites)
	{
		if(readingSites == null)
			return null;
		PathologyFederationReadingSiteType [] result = new PathologyFederationReadingSiteType [readingSites.size()];
		for(int i = 0; i < readingSites.size(); i++)
		{
			result[i] = translate(readingSites.get(i)); 
		}
		return result;
	}*/
	
	public static PathologyFederationReadingSiteType translate(PathologyReadingSite readingSite)
	{
		return new PathologyFederationReadingSiteType(readingSite.getSiteId(), 
				readingSite.getSiteName(), readingSite.getSiteAbbr(), readingSite.isActive(), 
				translate(readingSite.getReadingSiteType()));
	}
	
	public static List<PathologyCaseSpecimen> translate(PathologyFederationCaseSpecimenType [] specimens)
	{
		if(specimens == null)
			return null;
		List<PathologyCaseSpecimen> result = new ArrayList<PathologyCaseSpecimen>();
		for(PathologyFederationCaseSpecimenType specimen : specimens)
		{
			result.add(translate(specimen));
		}
		
		return result;
	}
	
	private static PathologyCaseSpecimen translate(PathologyFederationCaseSpecimenType specimen)
	{
		PathologyCaseSpecimen result = new PathologyCaseSpecimen(specimen.getSpecimen());
		result.setLastStainDate(specimen.getLastStainDate());
		result.setNumSlides(specimen.getNumSlides());
		result.setSmearPrep(specimen.getSmearPrep());
		result.setStain(specimen.getStain());
		return result;
	}

	public static List<PathologyCase> translate(PathologyFederationCaseType [] cases) 
	throws MethodException
	{
		if(cases == null)
			return null;
		List<PathologyCase> result = new ArrayList<PathologyCase>();
		for(PathologyFederationCaseType c : cases)
		{
			result.add(translate(c));
		}
		
		return result;
	}
	
	private static PathologyCase translate(PathologyFederationCaseType caseType) 
	throws MethodException
	{
		try
		{
			PathologyCaseURN pathologyCaseUrn = URNFactory.create(caseType.getPathologyCaseId(), PathologyCaseURN.class);			
			List<PathologyCaseConsultation> consultations = translate(caseType.getConsultations());			
			PathologyCase result = new PathologyCase(pathologyCaseUrn, caseType.getAccessionNumber(), caseType.getReserved(), caseType.getReservedBy(),
					caseType.getPatientName(),
					RestCoreTranslator.translate(caseType.getPatientIdentifier()),
					caseType.getPriority(), caseType.isSlidesAvailable(), 
					caseType.getSpecimenTakenDate(), caseType.getStatus(), caseType.getSiteAbbr(), caseType.getSpecimenCount(), caseType.getPatientSsn(), 
					caseType.getMethod(), caseType.isNoteAttached(), caseType.isPatientSensitive(), caseType.getNumberOfImages());
			result.setConsultations(consultations);
			return result;
		}
		catch(URNFormatException urnfX)
		{
			logger.error("URNFormatException translating pathology cases", urnfX);
			throw new MethodException(urnfX);
		}
	}

	public static PathologyCaseReserveResult translate(PathologyFederationCaseReserveResultType caseReserveResult)
	{
		for( Entry<PathologyCaseReserveResult, PathologyFederationCaseReserveResultType> entry : PathologyFederationRestTranslator.caseReserveResultTypeMap.entrySet() )
			if( entry.getValue() == caseReserveResult)
				return entry.getKey();
		
		return null;
	}
	
	public static PathologyFederationCaseReserveResultType translate(PathologyCaseReserveResult caseReserveResult)
	{
		for( Entry<PathologyCaseReserveResult, PathologyFederationCaseReserveResultType> entry : PathologyFederationRestTranslator.caseReserveResultTypeMap.entrySet() )
			if( entry.getKey() == caseReserveResult)
				return entry.getValue();
		
		return null;
	}
	
	public static PathologyFederationSaveCaseReportResultType translate(PathologySaveCaseReportResult saveCaseReportResult)
	{
		return new PathologyFederationSaveCaseReportResultType(saveCaseReportResult.isReleased(), saveCaseReportResult.getMessage());
	}
	
	public static PathologySaveCaseReportResult translate(PathologyFederationSaveCaseReportResultType saveCaseReportResult)
	{
		if(saveCaseReportResult.isReleased())
			return PathologySaveCaseReportResult.createReleasedResult(saveCaseReportResult.getMessage());
		return PathologySaveCaseReportResult.createUnreleasedResult();
	}
	
	public static PathologyFederationCaseSlideType [] translateCaseSlides(List<PathologyCaseSlide> slides)
	{
		if(slides == null)
			return null;
		PathologyFederationCaseSlideType [] result = new PathologyFederationCaseSlideType[slides.size()];
		for(int i = 0; i < slides.size(); i++)
		{
			result[i] = translate(slides.get(i));
		}
		return result;
	}
	
	private static PathologyFederationCaseSlideType translate(PathologyCaseSlide slide)
	{
		return new PathologyFederationCaseSlideType(slide.getSlideNumber(), slide.getDateTimeScanned(), slide.getUrl(), 
				slide.getZoomFactor(), slide.getScanApplication(), slide.getSlideStatus(), slide.getViewApplication(),
				slide.getDescription());
	}
	
	public static List<PathologyCaseSlide> translate(PathologyFederationCaseSlideType [] slides)
	{
		if(slides == null)
			return null;
		List<PathologyCaseSlide> result = new ArrayList<PathologyCaseSlide>();
		for(PathologyFederationCaseSlideType slide : slides)
		{
			result.add(translate(slide));
		}
		
		return result;
	}
	
	private static PathologyCaseSlide translate(PathologyFederationCaseSlideType slide )
	{
		return new PathologyCaseSlide(slide.getSlideNumber(), slide.getDateTimeScanned(), slide.getUrl(), 
				slide.getZoomFactor(), slide.getScanApplication(), slide.getSlideStatus(), slide.getViewApplication(),
				slide.getDescription());
	}
}
