package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.business.dicom.PatientStudyInfo;
import gov.va.med.imaging.exchange.business.dicom.PatientStudyLookupResults;

public 	class PatientStudyLookupResultsCacheItem extends BaseTimedCacheValueItem
{

	PatientStudyInfo patientStudyInfo;
	PatientStudyLookupResults results;
	public PatientStudyLookupResultsCacheItem(PatientStudyInfo patientStudyInfo, PatientStudyLookupResults results)
	{
		this.patientStudyInfo = patientStudyInfo;
		this.results = results;
	}

	@Override
	public Object getKey()
	{
		return getCacheKey(patientStudyInfo);
	}
	
	public static Object getCacheKey(PatientStudyInfo patientStudyInfo)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(patientStudyInfo.getStudyAccessionNumber() + "_");
		buffer.append(patientStudyInfo.getStudyImagingService() + "_");
		buffer.append(patientStudyInfo.getPatientName() + "_");
		buffer.append(patientStudyInfo.getPatientID() + "_");
		buffer.append(patientStudyInfo.getPatientBirthDate() + "_");
		buffer.append(patientStudyInfo.getPatientSex() + "_");
		buffer.append(patientStudyInfo.getPatientICN());
		return buffer.toString();
	}
	
	public PatientStudyLookupResults getPatientStudyLookupResults()
	{
		return results;
	}
}