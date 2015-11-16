/**
 * 
 */
package gov.va.med.imaging.exchange.enums;

/**
 * @author VHAISWBECKEC
 *
 */
public enum StudyClass
{
	CLIN("CLIN", "Clinical Only"), 
	CLIN_ADMIN("CLIN/ADMIN", "Clinical/Administrative"), 
	ADMIN_CLIN("ADMIN/CLIN", "Administrative/Clinical");	

	/**
	 * Return the enumeration element with the given key value.
	 * 
	 * @param shortDescription
	 * @return
	 */
	public static StudyClass valueOfKey(String shortDescription)
	{
		for(StudyClass studyClass : StudyClass.values())
			if(studyClass.getKey().equals(shortDescription))
				return studyClass;
		
		return null;
	}

	/**
	 * Returns the ordinal of the StudyClass with the given key value.
	 * 
	 * @param shortDescription
	 * @return
	 */
	public static int indexOfKey(String shortDescription)
	{
		for(StudyClass studyClass : StudyClass.values())
			if(studyClass.getKey().equals(shortDescription))
				return studyClass.ordinal();
		
		return -1;
	}

	private String key; 
	private String description;
	
	StudyClass(String shortDescription, String description)
	{
		this.key = shortDescription;
		this.description = description;
	}

	public String getDescription()
	{
		return this.description;
	}

	public String getKey()
	{
		return this.key;
	}

}
