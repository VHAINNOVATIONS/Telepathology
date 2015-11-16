package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.business.dicom.Study;

public class StudyCacheItem extends BaseTimedCacheValueItem
{

	Study study;
	public StudyCacheItem(Study study)
	{
		this.study = study;
	}

	@Override
	public Object getKey()
	{
		return getCacheKey(study);
	}

	public static Object getCacheKey(Study study)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(study.getPatientRefIEN() + "_");
		buffer.append(study.getProcedureRefIEN() + "_");
		buffer.append(study.getStudyIUID());
		return buffer.toString();
	}

	public Study getStudy()
	{
		return study;
	}
}
