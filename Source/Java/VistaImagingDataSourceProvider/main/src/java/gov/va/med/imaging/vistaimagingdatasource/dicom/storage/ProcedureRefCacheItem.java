package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.BaseTimedCacheValueItem;
import gov.va.med.imaging.exchange.business.dicom.ProcedureRef;

public class ProcedureRefCacheItem extends BaseTimedCacheValueItem
{

	ProcedureRef procedureRef;
	public ProcedureRefCacheItem(ProcedureRef procedureRef)
	{
		this.procedureRef = procedureRef;
	}

	@Override
	public Object getKey()
	{
		return getCacheKey(procedureRef);
	}

	public static Object getCacheKey(ProcedureRef procedureRef)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(procedureRef.getPatientRefIEN() + "_");
		buffer.append(procedureRef.getProcedureID() + "_");
		buffer.append(procedureRef.getProcedureIDType() + "_");
		buffer.append(procedureRef.getCreatingEntity() + "_");
		buffer.append(procedureRef.getAssigningAuthority());
		return buffer.toString();
	}

	public ProcedureRef getProcedureRef()
	{
		return procedureRef;
	}
}