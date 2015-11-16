/**
 * 
 */
package gov.va.med.imaging.storage.cache;

/**
 * @author VHAISWBECKEC
 * 
 * NOTE: ordering of these enumerations is critical !
 */
public enum CACHE_POPULATION_DEPTH
{
	CACHE_MANAGER, CACHE, REGION, GROUP0, GROUP1, GROUP2, GROUP3, GROUP4, GROUP5, GROUP6, GROUP7, GROUP8, GROUP9, GROUPX, INSTANCE;
	
	public static CACHE_POPULATION_DEPTH next(CACHE_POPULATION_DEPTH current)
	{
		try
		{
			return values()[current == null ? 0 : current.ordinal()+1];
		}
		catch (IndexOutOfBoundsException iobX)
		{
			return null;
		}
	}

	public static CACHE_POPULATION_DEPTH getGroupFromIndex(int groupIndex)
	{
		if(groupIndex == 0)
			return CACHE_POPULATION_DEPTH.GROUP0;
		if(groupIndex == 1)
			return CACHE_POPULATION_DEPTH.GROUP1;
		if(groupIndex == 2)
			return CACHE_POPULATION_DEPTH.GROUP2;
		if(groupIndex == 3)
			return CACHE_POPULATION_DEPTH.GROUP3;
		if(groupIndex == 4)
			return CACHE_POPULATION_DEPTH.GROUP4;
		if(groupIndex == 5)
			return CACHE_POPULATION_DEPTH.GROUP5;
		if(groupIndex == 6)
			return CACHE_POPULATION_DEPTH.GROUP6;
		if(groupIndex == 7)
			return CACHE_POPULATION_DEPTH.GROUP7;
		if(groupIndex == 8)
			return CACHE_POPULATION_DEPTH.GROUP8;
		if(groupIndex == 9)
			return CACHE_POPULATION_DEPTH.GROUP9;
		
		return null;
	}
	
	/**
	 * Returns the index offset from GROUP0
	 * @return
	 */
	public int getGroupIndex()
	{
		return this.ordinal() - GROUP0.ordinal();
	}
}
