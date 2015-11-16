/**
 * 
 */
package gov.va.med;

import java.io.Serializable;
import org.apache.log4j.Logger;
import gov.va.med.imaging.exceptions.OIDFormatException;

/**
 * @author vhaiswbeckec
 *
 */
public enum WellKnownOID
implements Serializable
{
	HAIMS_DOCUMENT(new String[]{"2.16.840.1.113883.3.42.10012.100001.206"}, "HAIMS Documents", true),
	BHIE_RADIOLOGY(new String[]{"2.16.840.1.113883.3.42.10012.100001.207"}, "BHIE Radiology", true),
	VA_DOCUMENT(new String[]{"2.16.840.1.113883.3.166", "2.16.840.1.113883.6.233"}, "VA Documents", true),
	VA_RADIOLOGY_IMAGE(new String[]{"1.3.6.1.4.1.3768"}, "VA Radiology", true),
	SNOMED(new String[]{"2.16.840.1.113883.6.96"}, "SNOMED Classification Scheme", false),
	LOINC(new String[]{"2.16.840.1.113883.6.1"}, "LOINC Classification Scheme", false),
	HL7(new String[]{"2.16.840.1.113883.11.19465"}, "HL7 Classification Scheme", false),
	MHS(new String[]{"2.16.840.1.113883.3.42.10012.100001.205"}, "MHS Classification Scheme", false);

	static
	{
		// assure that there are no overlapping OIDs
		for(WellKnownOID oid1 : WellKnownOID.values())
			for(WellKnownOID oid2 : WellKnownOID.values())
			{
				if(oid1 == oid2)
					continue;
				for(OID oid1Value : oid1.getAllValues() )
					for(OID oid2Value : oid2.getAllValues() )
						if(oid1Value.equals(oid2Value))
						{
							Logger.getLogger(WellKnownOID.class).error("Overlap in OID value '" + oid1Value.toString() + "'.");
							throw new java.lang.ExceptionInInitializerError("WellKnownOID values have an overlapping OID value.");
						}
			}
	}
	
	/**
	 * Find a WellKnownOID from a string representation of its OID.
	 * 
	 * @param oidAsString
	 * @return
	 */
	public static WellKnownOID get(String oidAsString)
	{
		try
		{
			return get( OID.create(oidAsString) );
		}
		catch (OIDFormatException x)
		{
			Logger.getLogger(WellKnownOID.class).error("Unable to create OID from '" + oidAsString + "'.");
			return null;
		}
	}
	
	public static WellKnownOID getOrValueOf(String value)
	{
		try
		{
			return get( OID.create(value) );
		}
		catch (OIDFormatException x)
		{
			try
			{
				return WellKnownOID.valueOf(value);
			}
			catch (IllegalArgumentException x1)
			{
				return null;
			}
		}
		
	}
	
	/**
	 * Find a WellKnownOID from its OID.
	 * 
	 * @param value
	 * @return
	 */
	public static WellKnownOID get(OID value)
	{
		if(value == null)
			return null;
		
		for(WellKnownOID wellKnownOid : values())
			for(OID oidValue : wellKnownOid.getAllValues())
				if(value.equals(oidValue))
					return wellKnownOid;
		
		return null;
	}

	/**
	 * @return
	 */
	public static WellKnownOID[] getHomeCommunityIds()
	{
		WellKnownOID[] homeCommunityIds = new WellKnownOID[WellKnownOID.values().length];
		int index = 0;
		for(WellKnownOID wellKnownOid : WellKnownOID.values())
			if(wellKnownOid.isHomeCommunityId())
				homeCommunityIds[index++] = wellKnownOid;
		
		System.arraycopy(homeCommunityIds, 0, homeCommunityIds, 0, index);
		return homeCommunityIds;
	}
	
	// ==================================================================
	private OID[] values;
	private final boolean homeCommunityId;
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * 
	 * @param oidAsString - the array of OIDs that represent an entity
	 * @param description - a description of the entity identified by the OIDs
	 * @param homeCommunityId - whether the OIDs may be used as a home community ID
	 */
	WellKnownOID(String[] oidAsString, String description, boolean homeCommunityId) 
	{
		if(oidAsString == null || oidAsString.length == 0)
			throw new ExceptionInInitializerError("A WellKnownUID must have at least one OID specified in its constructor.");
		try
		{
			values = new OID[oidAsString.length];
			for(int index=0; index < oidAsString.length; ++index)
				values[index] = OID.create(oidAsString[index], description);
		}
		catch (OIDFormatException x)
		{
			logger.error("Unable to initialize WellKnownOID '" + oidAsString + "'.");
		}
		this.homeCommunityId = homeCommunityId;
	}

	public final OID getCanonicalValue()
	{
		return this.values[0];
	}

	public final OID[] getAllValues()
	{
		return values;
	}
	
	protected boolean isHomeCommunityId()
	{
		return this.homeCommunityId;
	}

	/**
	 * Is the given OID one of the OIDs associated to this enumeration value.
	 * 
	 * @param oidAsString
	 * @return
	 */
	public boolean isApplicable(String oidAsString)
	{
		try
		{
			return isApplicable( OID.create(oidAsString) );
		}
		catch (OIDFormatException x)
		{
			logger.warn("OIDFormatException converting '" + oidAsString + "'.");
			return false;
		}
	}
	
	/**
	 * Is the given OID one of the OIDs associated to this enumeration value.
	 * 
	 * @param oid
	 * @return
	 */
	public boolean isApplicable(OID oid)
	{
		for(int oidIndex=0; oidIndex < getAllValues().length; ++oidIndex)
			if(oid.equals(getAllValues()[oidIndex]))
				return true;
		
		return false;
	}
	
	public String getDescription()
	{
		return getCanonicalValue().getDescription();
	}
}
