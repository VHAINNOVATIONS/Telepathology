/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 13, 2012
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
package gov.va.med.imaging.pathology.rest.translator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gov.va.med.PatientIdentifier;
import gov.va.med.URNFactory;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseConsultation;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseSupplementalReport;
import gov.va.med.imaging.pathology.PathologyCaseTemplate;
import gov.va.med.imaging.pathology.PathologyCaseTemplateField;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCptCode;
import gov.va.med.imaging.pathology.PathologyCptCodeResult;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.PathologyFieldValue;
import gov.va.med.imaging.pathology.PathologyReadingSite;
import gov.va.med.imaging.pathology.PathologySaveCaseReportResult;
import gov.va.med.imaging.pathology.PathologySite;
import gov.va.med.imaging.pathology.PathologySnomedCode;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeed;
import gov.va.med.imaging.pathology.rest.types.PathologyAcquisitionSiteType;
import gov.va.med.imaging.pathology.rest.types.PathologyAcquisitionSitesType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseConsultationUpdateStatusType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseReportFieldType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseReportFieldsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseReserveResultType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseSupplementalReportType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseSupplementalReportsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseTemplateFieldType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseTemplateFieldsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseTemplateInputFieldsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseTemplateType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseUpdateAttributeResultType;
import gov.va.med.imaging.pathology.rest.types.PathologyCaseType;
import gov.va.med.imaging.pathology.rest.types.PathologyCasesType;
import gov.va.med.imaging.pathology.rest.types.PathologyConsultationType;
import gov.va.med.imaging.pathology.rest.types.PathologyConsultationsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCopyCaseResultType;
import gov.va.med.imaging.pathology.rest.types.PathologyCptCodeResultType;
import gov.va.med.imaging.pathology.rest.types.PathologyCptCodeResultsType;
import gov.va.med.imaging.pathology.rest.types.PathologyCptCodeType;
import gov.va.med.imaging.pathology.rest.types.PathologyCptCodesType;
import gov.va.med.imaging.pathology.rest.types.PathologyElectronicSignatureNeedStatusType;
import gov.va.med.imaging.pathology.rest.types.PathologyElectronicSignatureNeedType;
import gov.va.med.imaging.pathology.rest.types.PathologyFieldType;
import gov.va.med.imaging.pathology.rest.types.PathologyFieldValueType;
import gov.va.med.imaging.pathology.rest.types.PathologyFieldValuesType;
import gov.va.med.imaging.pathology.rest.types.PathologyPatientType;
import gov.va.med.imaging.pathology.rest.types.PathologyReadingSiteType;
import gov.va.med.imaging.pathology.rest.types.PathologyReadingSiteTypeType;
import gov.va.med.imaging.pathology.rest.types.PathologyReadingSitesType;
import gov.va.med.imaging.pathology.rest.types.PathologySaveCaseReportResultType;
import gov.va.med.imaging.pathology.rest.types.PathologySiteType;
import gov.va.med.imaging.pathology.rest.types.PathologySitesType;
import gov.va.med.imaging.pathology.rest.types.PathologySnomedCodeType;
import gov.va.med.imaging.pathology.rest.types.PathologySnomedCodesType;
import gov.va.med.imaging.pathology.rest.types.PathologySpecimenType;
import gov.va.med.imaging.pathology.rest.types.PathologySpecimensType;
import gov.va.med.imaging.pathology.rest.types.PathologyTemplatesType;
import gov.va.med.imaging.rest.types.RestStringArrayType;
/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyRestTranslator
{
	private static Map<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyReadingSiteTypeType> readingSiteTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus, PathologyElectronicSignatureNeedStatusType> electronicSignatureNeedTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyField, PathologyFieldType> pathologyFieldsTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus, PathologyCaseConsultationUpdateStatusType> caseConsultationStatusTypeMap;
	private static Map<gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult, PathologyCaseReserveResultType> caseReserveResultMap;
	
	static
	{
		readingSiteTypeMap = new HashMap<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyReadingSiteTypeType>();
		readingSiteTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType.interpretation, PathologyReadingSiteTypeType.interpretation);
		readingSiteTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType.consultation, PathologyReadingSiteTypeType.consultation);
		readingSiteTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType.both, PathologyReadingSiteTypeType.both);
		
		electronicSignatureNeedTypeMap = new HashMap<gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus, PathologyElectronicSignatureNeedStatusType>();
		electronicSignatureNeedTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus.authorized_needs_signature, 
				PathologyElectronicSignatureNeedStatusType.authorized_needs_signature);
		electronicSignatureNeedTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus.not_enabled, 
				PathologyElectronicSignatureNeedStatusType.not_enabled);
		electronicSignatureNeedTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus.not_authorized, 
				PathologyElectronicSignatureNeedStatusType.not_authorized);
		
		pathologyFieldsTypeMap = new HashMap<gov.va.med.imaging.pathology.enums.PathologyField, PathologyFieldType>();
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.disease, 
				PathologyFieldType.disease);
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.etiology, 
				PathologyFieldType.etiology);
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.function, 
				PathologyFieldType.function);		
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.morphology, 
				PathologyFieldType.morphology);
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.procedure, 
				PathologyFieldType.procedure);
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.users, 
				PathologyFieldType.users);
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.topography, 
				PathologyFieldType.topography);
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.location, 
				PathologyFieldType.location);
		pathologyFieldsTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyField.workload, 
				PathologyFieldType.workload);
		
		caseConsultationStatusTypeMap = new HashMap<gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus, 
			PathologyCaseConsultationUpdateStatusType>();
		caseConsultationStatusTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus.completed, 
				PathologyCaseConsultationUpdateStatusType.completed);
		caseConsultationStatusTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus.recalled, 
				PathologyCaseConsultationUpdateStatusType.recalled);
		caseConsultationStatusTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus.pending, 
				PathologyCaseConsultationUpdateStatusType.pending);
		caseConsultationStatusTypeMap.put(gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus.refused, 
				PathologyCaseConsultationUpdateStatusType.refused);
		
		caseReserveResultMap = new HashMap<gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult, 
				PathologyCaseReserveResultType>();
		caseReserveResultMap.put(gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult.case_reserved,
				PathologyCaseReserveResultType.case_reserved);
		caseReserveResultMap.put(gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult.reservation_ended,
				PathologyCaseReserveResultType.reservation_ended);
		
	}
	
	public static PathologyElectronicSignatureNeedType translate(PathologyElectronicSignatureNeed electronicSignatureNeed)
	{
		return new PathologyElectronicSignatureNeedType(translate(electronicSignatureNeed.getStatus()),
				electronicSignatureNeed.getMessage());
	}
	
	public static PathologyCopyCaseResultType translate(PathologyCaseURN pathologyCaseUrn)
	{
		if(pathologyCaseUrn == null)
			return null;
		return new PathologyCopyCaseResultType(pathologyCaseUrn.toString(), pathologyCaseUrn.toStringAccessionNumber());
	}
	
	public static PathologyCptCodeResultsType translateCptCodeResults(List<PathologyCptCodeResult> cptCodes)
	{
		if(cptCodes == null)
			return null;
		PathologyCptCodeResultType [] result = new PathologyCptCodeResultType[cptCodes.size()];
		for(int i = 0; i < cptCodes.size(); i++)
		{
			result[i] = translate(cptCodes.get(i));
		}
		
		return new PathologyCptCodeResultsType(result);
	}
	
	private static PathologyCptCodeResultType translate(PathologyCptCodeResult cptCode)
	{
		return new PathologyCptCodeResultType(cptCode.getCptCode(), cptCode.isSuccessfullyAdded(), cptCode.getDescription());
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
				PathologyFieldURN fieldUrn = URNFactory.create(fieldId, PathologyFieldURN.class);
				result.add(fieldUrn);
			}
			catch(URNFormatException urnfX)
			{
				throw new MethodException(urnfX);
			}
		}
		return result;
	}
	
	public static List<PathologyCaseURN> translateCaseIds(RestStringArrayType caseIds)
	throws MethodException
	{
		if(caseIds == null)
			return null;
		List<PathologyCaseURN> result = new ArrayList<PathologyCaseURN>();
		for(String caseId : caseIds.getValue())
		{
			try
			{
				PathologyCaseURN caseUrn = URNFactory.create(caseId, PathologyCaseURN.class);
				result.add(caseUrn);
			}
			catch(URNFormatException urnfX)
			{
				throw new MethodException(urnfX);
			}
		}
		return result;
	}
	
	public static PathologyCptCodesType translateCptCodes(List<PathologyCptCode> cptCodes)
	{
		if(cptCodes == null)
			return null;
		
		PathologyCptCodeType [] result = new PathologyCptCodeType[cptCodes.size()];
		for(int i = 0; i < cptCodes.size(); i++)
		{
			result[i] = translate(cptCodes.get(i));
		}
		
		return new PathologyCptCodesType(result);
	}
	
	private static PathologyCptCodeType translate(PathologyCptCode cptCode)
	{
		return new PathologyCptCodeType(cptCode.getCptCode(), cptCode.getDescription(), cptCode.getMultiplyFactor(),
				cptCode.getDateEntered(), cptCode.getUser());
	}
	
	public static PathologySnomedCodesType translateSnomedCodes(List<PathologySnomedCode> snomedCodes)
	{
		if(snomedCodes == null)
			return null;
		PathologySnomedCodeType [] result = new PathologySnomedCodeType[snomedCodes.size()];
		for(int i = 0; i < snomedCodes.size(); i++)
		{
			result[i] = translate(snomedCodes.get(i));
		}
		
		return new PathologySnomedCodesType(result);
	}
	
	private static PathologySnomedCodeType translate(PathologySnomedCode snomedCode)
	{
		return new PathologySnomedCodeType(snomedCode.getTissueId(),
				snomedCode.getTissueCode(),
				snomedCode.getTissue(),
				translate(snomedCode.getField()),
				snomedCode.getSnomedCode(),
				snomedCode.getSnomedValue(),
				snomedCode.getSnomedId(),
				snomedCode.getEtiologyId(),
				snomedCode.getEtiologySnomedCode(),
				snomedCode.getEtiologySnomedValue());
	}
	
	public static PathologySitesType translateSites(List<PathologySite> sites)
	{
		if(sites == null)
			return null;
		PathologySiteType [] result = new PathologySiteType[sites.size()];
		for(int i = 0; i < sites.size(); i++)
		{
			result[i] = translate(sites.get(i));
		}
		
		return new PathologySitesType(result);
	}
	
	private static PathologySiteType translate(PathologySite site)
	{
		return new PathologySiteType(site.getStationNumber(), site.getSiteName(), site.getSiteAbbr());
	}
	
	public static Date translateDate(String date)
	throws MethodException
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmm");
			return sdf.parse(date);
		}
		catch(ParseException pX)
		{
			throw new MethodException(pX);
		}
	}
	
	public static List<PathologyCaseReportField> translate(PathologyCaseReportFieldsType caseReportFields)
	{
		if(caseReportFields == null || caseReportFields.getField() == null)
			return null;
		
		List<PathologyCaseReportField> result = new ArrayList<PathologyCaseReportField>();
		for(PathologyCaseReportFieldType caseReportField : caseReportFields.getField())
		{
			result.add(translate(caseReportField));
		}
		
		return result;
	}
	
	private static PathologyCaseReportField translate(PathologyCaseReportFieldType caseReportField)
	{
		String [] values = caseReportField.getValues().getValue();
		List<String> fieldValues = new ArrayList<String>();
		if(values != null)
		{
			for(String value : values)
				fieldValues.add(value);
		}
		
		return new PathologyCaseReportField(caseReportField.getFieldNumber(), fieldValues);
	}
	
	public static PathologyFieldValuesType translateFieldValues(List<PathologyFieldValue> fieldValues)
	{
		if(fieldValues == null)
			return null;
		
		PathologyFieldValueType [] result = new PathologyFieldValueType[fieldValues.size()];
		
		for(int i = 0; i < fieldValues.size(); i++)
		{
			result[i] = translate(fieldValues.get(i));
		}
		
		return new PathologyFieldValuesType(result);
	}
	
	private static PathologyFieldValueType translate(PathologyFieldValue fieldValue)
	{
		return new PathologyFieldValueType(fieldValue.getFieldUrn().toString(), fieldValue.getName());
	}
	
	public static PathologyCaseTemplateType translate(PathologyCaseTemplate caseTemplate)
	{
		if(caseTemplate == null)
			return null;		
		
		PathologyCaseTemplateFieldType [] fields = new PathologyCaseTemplateFieldType[caseTemplate.getFields().size()];
		for(int i = 0; i < caseTemplate.getFields().size(); i++)
		{
			fields[i] = translate(caseTemplate.getFields().get(i));
		}
		
		return new PathologyCaseTemplateType(new PathologyCaseTemplateFieldsType(fields));
	}
	
	private static PathologyCaseTemplateFieldType translate(PathologyCaseTemplateField templateField)
	{
		List<String> templateValues = templateField.getValues();
		String [] values = new String[templateValues.size()];
		for(int i = 0; i < templateValues.size(); i++)
		{
			values[i] = templateValues.get(i);
		}
		return new PathologyCaseTemplateFieldType(templateField.getFieldNumber(), 
				templateField.getLabel(), new RestStringArrayType(values));
	}
	
	public static List<String> translate(PathologyCaseTemplateInputFieldsType fields)
	{
		if(fields == null || fields.getField() == null)
			return null;
		List<String> result = new ArrayList<String>();
		for(String value : fields.getField())
			result.add(value);
		return result;
	}
	
	public static PathologyCaseSupplementalReportsType translateSupplementalReports(List<PathologyCaseSupplementalReport> supplementalReports)
	{
		if(supplementalReports == null)
			return null;
		
		PathologyCaseSupplementalReportType [] result = new PathologyCaseSupplementalReportType[supplementalReports.size()];
		for(int i = 0; i < supplementalReports.size(); i++)
		{
			result[i] = translate(supplementalReports.get(i));
		}
		
		return new PathologyCaseSupplementalReportsType(result);
	}
	
	private static PathologyCaseSupplementalReportType translate(PathologyCaseSupplementalReport supplementalReport)
	{
		return new PathologyCaseSupplementalReportType(supplementalReport.getSupplementalReportDate(),
				supplementalReport.isVerified(), supplementalReport.getVerifiedProvider(),
				new RestStringArrayType(supplementalReport.getValues()));
	}
	
	public static PathologyReadingSite translate(PathologyReadingSiteType readingSite)
	{
		return new PathologyReadingSite(readingSite.getSiteId(), null, null, 
				readingSite.isActive(), translate(readingSite.getReadingSiteType()));
	}
	
	public static PathologyAcquisitionSite translate(PathologyAcquisitionSiteType acquisitionSite)
	{
		return new PathologyAcquisitionSite(acquisitionSite.getSiteId(), null, null, 
				acquisitionSite.isActive(), acquisitionSite.getPrimarySiteStationNumber(), null, null);
	}
	
	public static PathologyTemplatesType translateTemplates(List<String> templates)
	{
		if(templates == null)
			return null;
		//PathologyTemplateType [] result = new PathologyTemplateType[templates.size()];
		
		/*
		for(int i = 0; i < templates.size(); i++)
		{
			result[i] = new PathologyTemplateType(templates.get(i));
		}
		return new PathologyTemplatesType(result);
		*/
		return new PathologyTemplatesType(templates.toArray(new String[templates.size()]));
	}
	
	public static PathologySpecimensType translatePathologySpecimens(List<PathologyCaseSpecimen> specimens)
	{
		if(specimens == null)
			return null;
		List<PathologySpecimenType> result = new ArrayList<PathologySpecimenType>();
		for(PathologyCaseSpecimen specimen : specimens)
		{
			PathologySpecimenType pst = translate(specimen);
			if(pst != null)
				result.add(pst);
		}
		return new PathologySpecimensType(result.toArray(new PathologySpecimenType[result.size()]));
	}
	
	private static PathologySpecimenType translate(PathologyCaseSpecimen specimen)
	{
		if(specimen == null)
			return null;
		return new PathologySpecimenType(specimen.getSpecimen(), specimen.getSmearPrep(), specimen.getStain(),
				specimen.getNumSlides(), specimen.getLastStainDate());
	}
	
	public static PathologyCaseUpdateAttributeResultType translate(PathologyCaseUpdateAttributeResult lockResult)
	{
		if(lockResult == null)
			return null;
		
		return new PathologyCaseUpdateAttributeResultType(lockResult.isSuccess(), lockResult.getErrorMessage());
	}
	
	public static PathologyPatientType translate(Patient patient)
	{
		if(patient == null)
			return null;
		
		return new PathologyPatientType(patient.getPatientIcn(),
				patient.getDfn(), patient.getPatientName(), 
				patient.getPatientSex().name(), patient.getDob());
	}
	
	public static PathologyCasesType translateCases(List<PathologyCase> cases)
	{
		if(cases == null)
			return null;
		
		List<PathologyCaseType> result = new ArrayList<PathologyCaseType>();
		for(PathologyCase c : cases)
		{
			PathologyCaseType pct = translate(c);
			if(pct != null)
				result.add(pct);
		}
		
		return new PathologyCasesType(result.toArray(new PathologyCaseType[result.size()]));
	}
	
	private static PathologyCaseType translate(PathologyCase pathologyCase)
	{
		if(pathologyCase == null)
			return null;
		
		PatientIdentifier patientIdentifier = pathologyCase.getPatientIdentifier();
		
		PathologyCaseType result = new PathologyCaseType(pathologyCase.getPathologyCaseUrn().toString(),
				pathologyCase.getAccessionNumber(), pathologyCase.getReserved(),
				pathologyCase.getReservedBy(), "",
				pathologyCase.getPatientName(), 
				patientIdentifier.toString(),
				pathologyCase.getPriority(), pathologyCase.isSlidesAvailable(), 
				pathologyCase.getSpecimenTakenDate(), pathologyCase.getStatus(),
				pathologyCase.getSiteAbbr(), 
				pathologyCase.getPathologyCaseUrn().getOriginatingSiteId(), 
				pathologyCase.getSpecimenCount(), pathologyCase.getMethod(), 
				pathologyCase.getPatientSsn(), pathologyCase.isNoteAttached(),
				patientIdentifier.getPatientIdentifierType().isLocal(),
				pathologyCase.isPatientSensitive(),
				pathologyCase.getNumberOfImages());
		
		result.setConsultations(translateConsultations(pathologyCase.getConsultations()));
		
		return result;
	}
	
	private static PathologyConsultationsType translateConsultations(List<PathologyCaseConsultation> consultations)
	{
		if(consultations == null)
			return null;
		
		List<PathologyConsultationType> result = new ArrayList<PathologyConsultationType>();
		for(PathologyCaseConsultation consultation : consultations)
		{
			if(consultation != null)
			{
				result.add(new PathologyConsultationType(consultation.getPathologyCaseConsultationUrn().toString(),
						consultation.getInterpretingStation(),
						//consultation.getPathologyCaseConsultationUrn().getOriginatingSiteId(), 
						consultation.getStatus(), consultation.getSiteAbbr(), consultation.getType()));
			}
		}
		
		return new PathologyConsultationsType(result.toArray(new PathologyConsultationType[result.size()]));
	}
	
	public static PathologyReadingSitesType translateReadingSites(List<AbstractPathologySite> sites)
	{
		if(sites == null)
			return null;
		List<PathologyReadingSiteType> result = new ArrayList<PathologyReadingSiteType>();
		
		for(AbstractPathologySite site : sites)
		{
			if(site instanceof PathologyReadingSite)
			{
				PathologyReadingSite readingSite = (PathologyReadingSite)site;
				result.add(translate(readingSite));
			}
		}
		
		return new PathologyReadingSitesType(result.toArray(new PathologyReadingSiteType[result.size()]));
	}
	
	public static PathologyAcquisitionSitesType translateAcquisitionSites(List<AbstractPathologySite> sites)
	{
		if(sites == null)
			return null;
		List<PathologyAcquisitionSiteType> result = new ArrayList<PathologyAcquisitionSiteType>();
		
		for(AbstractPathologySite site : sites)
		{
			if(site instanceof PathologyAcquisitionSite)
			{				
				PathologyAcquisitionSite acquisitionSite = (PathologyAcquisitionSite)site;
				result.add(new PathologyAcquisitionSiteType(acquisitionSite.getSiteId(), acquisitionSite.getSiteName(), acquisitionSite.getSiteAbbr(),
						acquisitionSite.isActive(), acquisitionSite.getPrimarySiteStationNumber(), acquisitionSite.getPrimarySiteAbbr(), 
						acquisitionSite.getPrimarySiteName()));
			}
		}
		
		return new PathologyAcquisitionSitesType(result.toArray(new PathologyAcquisitionSiteType[result.size()]));
	}
	
	/*
	public static PathologySitesType translateSites(List<AbstractPathologySite> sites)
	{
		if(sites == null)
			return null;
		List<PathologySiteType> result = new ArrayList<PathologySiteType>();
		
		for(AbstractPathologySite site : sites)
		{
			PathologySiteType pst = translateSite(site);
			if(pst != null)
				result.add(pst);
		}
		PathologySiteType []resultArray = new PathologySiteType[result.size()];
		for(int i = 0; i < result.size(); i++)
		{
			resultArray[i] = result.get(i);
		}
		
		
		return new PathologySitesType(resultArray);//.toArray(new PathologySiteType[result.size()]));
	}
	
	private static PathologySiteType translateSite(AbstractPathologySite site)
	{
		if(site instanceof PathologyReadingSite)
		{
			PathologyReadingSite readingSite = (PathologyReadingSite)site;
			return translate(readingSite);
			
		}
		else if(site instanceof PathologyAcquisitionSite)
		{
			PathologyAcquisitionSite acquisitionSite = (PathologyAcquisitionSite)site;
			return new PathologyAcquisitionSiteType(acquisitionSite.getSiteId(), acquisitionSite.getSiteName(), acquisitionSite.getSiteAbbr(),
					acquisitionSite.isActive(), acquisitionSite.getPrimarySiteStationNumber(), acquisitionSite.getPrimarySiteAbbr(), 
					acquisitionSite.getPrimarySiteName());
		}
		return null;
	}*/
	
	private static PathologyReadingSiteType translate(PathologyReadingSite site)
	{
		PathologyReadingSiteTypeType readingSiteType = translate(site.getReadingSiteType());
		return new PathologyReadingSiteType(site.getSiteId(), site.getSiteName(), site.getSiteAbbr(), site.isActive(), readingSiteType);
	}
	
	private static PathologyReadingSiteTypeType translate(gov.va.med.imaging.pathology.enums.PathologyReadingSiteType readingSiteType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyReadingSiteTypeType> entry : PathologyRestTranslator.readingSiteTypeMap.entrySet() )
			if( entry.getKey() == readingSiteType )
				return entry.getValue();
		
		return null;
	}
	
	private static gov.va.med.imaging.pathology.enums.PathologyReadingSiteType translate(PathologyReadingSiteTypeType readingSiteType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyReadingSiteType, PathologyReadingSiteTypeType> entry : PathologyRestTranslator.readingSiteTypeMap.entrySet() )
			if(entry.getValue() == readingSiteType)
				return entry.getKey();
		
		return null;
	}
	
	private static PathologyElectronicSignatureNeedStatusType translate(gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus electronicSignatureNeed)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeedStatus, PathologyElectronicSignatureNeedStatusType> entry : PathologyRestTranslator.electronicSignatureNeedTypeMap.entrySet() )
			if( entry.getKey() == electronicSignatureNeed )
				return entry.getValue();
		
		return null;
	}
	
	public static gov.va.med.imaging.pathology.enums.PathologyField translate(PathologyFieldType fieldType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyField, PathologyFieldType> entry : PathologyRestTranslator.pathologyFieldsTypeMap.entrySet() )
			if(entry.getValue() == fieldType)
				return entry.getKey();
		
		return null;
	}
	
	private static PathologyFieldType translate(gov.va.med.imaging.pathology.enums.PathologyField fieldType)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyField, PathologyFieldType> entry : PathologyRestTranslator.pathologyFieldsTypeMap.entrySet() )
			if(entry.getKey() == fieldType)
				return entry.getValue();
		
		return null;
	}
	
	public static gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus translate(PathologyCaseConsultationUpdateStatusType consultationUpdateStatus)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus, PathologyCaseConsultationUpdateStatusType> entry : PathologyRestTranslator.caseConsultationStatusTypeMap.entrySet() )
			if( entry.getValue() == consultationUpdateStatus )
				return entry.getKey();
		
		return null;
	}
	
	/*
	public static gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult translate(PathologyCaseReserveResultType caseReserveResult)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult, PathologyCaseReserveResultType> entry : PathologyRestTranslator.caseReserveResultMap.entrySet() )
			if( entry.getValue() == caseReserveResult )
				return entry.getKey();
		
		return null;
	}*/
	
	public static PathologyCaseReserveResultType translate(gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult caseReserveResult)
	{
		for( Entry<gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult, PathologyCaseReserveResultType> entry : PathologyRestTranslator.caseReserveResultMap.entrySet() )
			if( entry.getKey() == caseReserveResult )
				return entry.getValue();
		
		return null;
	}
	
	public static PathologySaveCaseReportResultType translate(PathologySaveCaseReportResult saveCaseReport)
	{
		return new PathologySaveCaseReportResultType(saveCaseReport.isReleased(), saveCaseReport.getMessage());
	}
}

