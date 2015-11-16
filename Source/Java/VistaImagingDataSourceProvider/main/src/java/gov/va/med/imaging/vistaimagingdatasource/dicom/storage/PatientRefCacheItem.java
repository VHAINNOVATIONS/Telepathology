package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.business.dicom.PatientRef;

public 	class PatientRefCacheItem extends BaseTimedCacheValueItem
{

	PatientRef patientRef;
	public PatientRefCacheItem(PatientRef patientRef)
	{
		this.patientRef = patientRef;
	}

	@Override
	public Object getKey()
	{
		return getCacheKey(patientRef);
	}
	
	public static Object getCacheKey(PatientRef patientRef)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(patientRef.getEnterprisePatientId() + "_");
		buffer.append(patientRef.getIdType() + "_");
		buffer.append(patientRef.getCreatingEntity() + "-");
		buffer.append(patientRef.getAssigningAuthority());
		return buffer.toString();
	}

	public PatientRef getPatientRef()
	{
		return patientRef;
	}
}