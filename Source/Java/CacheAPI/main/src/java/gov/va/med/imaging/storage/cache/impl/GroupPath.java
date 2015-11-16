/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import java.util.List;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWBECKEC
 * A simple value object that contains references to a Group
 * and all of its ancestors.
 * The path field DOES NOT include the Group to which the GroupPath applies.
 * The last entry in the path is the immediate ancestor (the parent) of the target Group.
 */
public class GroupPath
{
	private Region region;
	private List<Group> path;
	private Group group;
	
	public GroupPath(Region region, List<Group> path, Group group)
	{
		super();
		this.region = region;
		this.path = path;
		this.group = group;
	}

	/**
	 * @return the group
	 */
	public Group getGroup()
	{
		return this.group;
	}

	/**
	 * @return the path
	 */
	public List<Group> getPath()
	{
		return this.path;
	}
	
	/**
	 * Return the names of the Group instances in the path as a String array, which
	 * is the way that the Cache usually deals with paths.
	 * @return
	 */
	public String[] getPathName()
	{
		String[] pathName = new String[path.size()];
		
		int index=0;
		for(Group group : path)
			pathName[index++] = group.getName();
		
		return pathName;
	}

	/**
	 * @return the region
	 */
	public Region getRegion()
	{
		return this.region;
	}
	
	/**
	 * Determines if the path from Region, through the path to the Group is valid.
	 * this method uses the Group.getName() to determine existence of the Group instances.
	 * 
	 * @return
	 */
	public boolean validate()
	{
		try
		{
			Group parent = getRegion().getGroup(getPathName());
			parent.getChildGroup(group.getName());
		} 
		catch (CacheException x)
		{
			return false;
		}
		
		return true;
	}
}
