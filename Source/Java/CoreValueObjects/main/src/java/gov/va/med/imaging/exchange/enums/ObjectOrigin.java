package gov.va.med.imaging.exchange.enums;

import gov.va.med.WellKnownOID;

/**
 * This enum should be deleted and the home community ID be used in its place
 * 
 * @author vhaiswbeckec
 *
 */
@Deprecated
public enum ObjectOrigin 
{
	VA, DOD, UNKNOWN;
	
	public static ObjectOrigin inferFromHomeCommunityId(String homeCommunityId)
	{
		WellKnownOID oid = WellKnownOID.get(homeCommunityId);
		if(oid == null)
			return ObjectOrigin.UNKNOWN;
		
		if( WellKnownOID.HAIMS_DOCUMENT == oid)
			return ObjectOrigin.DOD;
		if( WellKnownOID.HL7 == oid)
			return ObjectOrigin.UNKNOWN;
		if( WellKnownOID.LOINC == oid)
			return ObjectOrigin.UNKNOWN;
		if( WellKnownOID.MHS == oid)
			return ObjectOrigin.UNKNOWN;
		if( WellKnownOID.SNOMED == oid)
			return ObjectOrigin.UNKNOWN;
		if( WellKnownOID.VA_DOCUMENT == oid)
			return ObjectOrigin.VA;
		if( WellKnownOID.VA_RADIOLOGY_IMAGE == oid)
			return ObjectOrigin.VA;
		
		return ObjectOrigin.UNKNOWN;
	}
}
