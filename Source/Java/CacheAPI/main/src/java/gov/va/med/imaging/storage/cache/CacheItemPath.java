/**
 * 
 */
package gov.va.med.imaging.storage.cache;

import java.io.Serializable;

/**
 * @author VHAISWBECKEC
 *
 */
public class CacheItemPath
implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private String cacheName;
	private String regionName;
	private String[] groupNames;
	private String instanceName;
	
	/**
	 * Default no-arg constructor required for GWT serialization
	 */
	public CacheItemPath()
	{
		this(null, null, (String[])null, null);
	}

	public CacheItemPath(String cacheName)
	{
		this(cacheName, null, (String[])null, null);
	}
	
	public CacheItemPath(String cacheName, String regionName)
	{
		this(cacheName, regionName, (String[])null, null);
	}
	
	public CacheItemPath(String cacheName, String regionName, String groupName)
	{
		this(cacheName, regionName, new String[]{groupName}, null);
	}
	
	public CacheItemPath(String cacheName, String regionName, String parentGroupName, String childGroupName)
	{
		this(cacheName, regionName, new String[]{parentGroupName, childGroupName}, null);
	}
	
	public CacheItemPath(String cacheName, String regionName, String[] groupsName) 
	{
		this.cacheName = cacheName;
		this.regionName = regionName;
		this.groupNames = groupsName;
		this.instanceName = null;
	}

	public CacheItemPath(String cacheName, String regionName, String[] groupNames, String instanceName)
	{
		this.cacheName = cacheName;
		this.regionName = regionName;
		this.groupNames = groupNames;
		this.instanceName = instanceName;
	}
	
	// ========================================================================================================
	public static final int CACHE_INDEX = 0;
	public static final int REGION_INDEX = 1;
	public static final char INSTANCE_INDICATOR = '.';		// preceeds the instance name, to indicate it as instance, not a group
	// ========================================================================================================
	

	public String getCacheName()
	{
		return cacheName;
	}

	public String getRegionName()
	{
		return regionName;
	}

	public String[] getGroupsName()
	{
		return groupNames;
	}

	public String getLastGroupName()
	{
		return groupNames != null && groupNames.length > 0 ?
			groupNames[groupNames.length-1] : null;
	}
	
	public CACHE_POPULATION_DEPTH getLastGroupDepth()
	{
		if( groupNames != null && groupNames.length > 0)
		{
			return 
				groupNames.length == 1 ? CACHE_POPULATION_DEPTH.GROUP0 :
				groupNames.length == 2 ? CACHE_POPULATION_DEPTH.GROUP1 :
				groupNames.length == 3 ? CACHE_POPULATION_DEPTH.GROUP2 :
				groupNames.length == 4 ? CACHE_POPULATION_DEPTH.GROUP3 :
				groupNames.length == 5 ? CACHE_POPULATION_DEPTH.GROUP4 :
				groupNames.length == 6 ? CACHE_POPULATION_DEPTH.GROUP5 :
				groupNames.length == 7 ? CACHE_POPULATION_DEPTH.GROUP6 :
				groupNames.length == 8 ? CACHE_POPULATION_DEPTH.GROUP7 :
				groupNames.length == 9 ? CACHE_POPULATION_DEPTH.GROUP8 :
				CACHE_POPULATION_DEPTH.GROUPX;
		}
		else
			return CACHE_POPULATION_DEPTH.REGION;
	}
	
	public String getInstanceName()
	{
		return instanceName;
	}

	/**
	 * 
	 * @return - the path to this instances parent node
	 */
	public CacheItemPath createParentPath()
	{
		if(getInstanceName() != null)
			return new CacheItemPath(getCacheName(), getRegionName(), getGroupsName(), null);
		
		if(getGroupsName() != null)
		{
			CACHE_POPULATION_DEPTH groupDepth = getLastGroupDepth();
			int parentGroupCount = (groupDepth.ordinal() - CACHE_POPULATION_DEPTH.GROUP0.ordinal());
			if(parentGroupCount == 0)
				return new CacheItemPath(getCacheName(), getRegionName(), (String[])null, null);
			
			String[] childGroups = new String[parentGroupCount];
			System.arraycopy(getGroupsName(), 0, childGroups, 0, childGroups.length);
			
			return new CacheItemPath(getCacheName(), getRegionName(), childGroups, null);
		}
		
		if(getRegionName() == null)
			return new CacheItemPath(getCacheName());
		
		return new CacheItemPath();
	}
	
	public CacheItemPath createChildPath(String childName, boolean childIsInstance)
	{
		if(regionName == null)
			return new CacheItemPath(this.getCacheName(), childName);
		if(groupNames == null)
			return new CacheItemPath(this.getCacheName(), this.getRegionName(), childName);
		if(groupNames != null && instanceName == null && !childIsInstance)
		{
			String[] childGroupNames = new String[getGroupsName().length + 1];
			System.arraycopy(getGroupsName(), 0, childGroupNames, 0, getGroupsName().length);
			childGroupNames[childGroupNames.length - 1] = childName;
			return new CacheItemPath(this.getCacheName(), this.getRegionName(), childGroupNames, null);
		}
		else if(groupNames != null && instanceName == null && childIsInstance)
		{
			return new CacheItemPath(this.getCacheName(), this.getRegionName(), this.getGroupsName(), childName);
		}
		else
			return null;
	}
	
	public CacheItemPath createChildInstancePath(String childName)
	{
		if(groupNames != null && instanceName == null)
			return new CacheItemPath(getCacheName(), getRegionName(), getGroupsName(), childName);
		
		return null;
	}
	
	@Override
	public String toString()
	{
		String result = cacheName;
		
		if(regionName != null)
		{
			result += "." + regionName;
			if(groupNames != null)
			{
				for(String groupName : groupNames)
					result += "." + groupName;
				
				if(instanceName != null)
					result += "[" + instanceName + "]";
			}
		}
		
		return result;
	}

	/**
	 * Return the name of the cache item at the specified depth.
	 * 
	 * @param currentDepth
	 * @return
	 */
	public String getNameAt(CACHE_POPULATION_DEPTH currentDepth)
	{
		if(currentDepth == null)
			throw new AssertionError("getNameAt, currentDepth parameter is null and must not be...");
		
		switch( currentDepth )
		{
			case CACHE: return getCacheName();
			case REGION: return getRegionName();
			case GROUP0: return getGroupsName() == null || this.getGroupsName().length < 1 ? null : this.getGroupsName()[0]; 
			case GROUP1: return getGroupsName() == null || this.getGroupsName().length < 2 ? null : this.getGroupsName()[1]; 
			case GROUP2: return getGroupsName() == null || this.getGroupsName().length < 3 ? null : this.getGroupsName()[2]; 
			case GROUP3: return getGroupsName() == null || this.getGroupsName().length < 4 ? null : this.getGroupsName()[3]; 
			case GROUP4: return getGroupsName() == null || this.getGroupsName().length < 5 ? null : this.getGroupsName()[4]; 
			case GROUP5: return getGroupsName() == null || this.getGroupsName().length < 6 ? null : this.getGroupsName()[5]; 
			case GROUP6: return getGroupsName() == null || this.getGroupsName().length < 7 ? null : this.getGroupsName()[6]; 
			case GROUP7: return getGroupsName() == null || this.getGroupsName().length < 8 ? null : this.getGroupsName()[7]; 
			case GROUP8: return getGroupsName() == null || this.getGroupsName().length < 9 ? null : this.getGroupsName()[8]; 
			case INSTANCE: return this.getInstanceName();
		}
		return null;
	}

	/**
	 * Gets the deepest populated element of the path.
	 * @return
	 */
	public CACHE_POPULATION_DEPTH getEndpointDepth()
	{
		CACHE_POPULATION_DEPTH previousDepth = null;
		for( CACHE_POPULATION_DEPTH currentDepth = CACHE_POPULATION_DEPTH.CACHE_MANAGER;
			currentDepth != null;
			currentDepth = nextDepth(currentDepth) )
				previousDepth = currentDepth;
		
		return previousDepth;
	}
	

	/**
	 * Gets the next populated element of the path or null if the endpoint has been reached.
	 * 
	 * @param currentDepth
	 * @return
	 */
	public CACHE_POPULATION_DEPTH nextDepth(CACHE_POPULATION_DEPTH currentDepth)
	{
		switch( currentDepth )
		{
			case CACHE_MANAGER:
				if(getCacheName() != null)
					return CACHE_POPULATION_DEPTH.CACHE;
				else 
					return null;
				
			case CACHE:
				if(getRegionName() != null)
					return CACHE_POPULATION_DEPTH.REGION;
				else
					return null;
				
			case REGION: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP0.getGroupIndex());
				
			case GROUP0: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP1.getGroupIndex());
				
			case GROUP1: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP2.getGroupIndex());
				
			case GROUP2: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP3.getGroupIndex());
				
			case GROUP3: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP4.getGroupIndex());
				
			case GROUP4: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP5.getGroupIndex());
				
			case GROUP5: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP6.getGroupIndex());
				
			case GROUP6: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP7.getGroupIndex());
				
			case GROUP7: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP8.getGroupIndex());
				
			case GROUP8: 
				return getDepthAtGroupLevel(CACHE_POPULATION_DEPTH.GROUP9.getGroupIndex());
				
			case GROUP9: 
				return CACHE_POPULATION_DEPTH.INSTANCE;
				
			case INSTANCE: 
				return null;
		}
		
		return null;
	}

	/**
	 * 
	 * @param groupIndex
	 * @return
	 */
	private CACHE_POPULATION_DEPTH getDepthAtGroupLevel(int groupIndex) 
	{
		if( getGroupsName() != null && this.getGroupsName().length >= (groupIndex+1) ) 
		{
			if(getGroupsName()[groupIndex] != null)
				return CACHE_POPULATION_DEPTH.getGroupFromIndex(groupIndex); 
			else
				return null;
		}
		else
		{
			if(getInstanceName() != null)
				return CACHE_POPULATION_DEPTH.INSTANCE; 
			else
				return null;
		}
	}
	
}
