/**
 * 
 */
package gov.va.med.imaging.storage.cache.util;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class implements a depth-first traversal over all of the groups in a Region.
 * For each Group visited, the groupVisit() method is called, derived classes
 * should do something useful in that method.
 * 
 * Note that this class implements Runnable and may therefore be run on
 * a seperate thread.  It may also be run on the calling thread by calling
 * the run() method directly.  This class is thread-safe in that it may run in
 * either mode.  Multiple calls to run() from different threads are discouraged
 * , subsequent calls will wait for the first call to complete.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class GroupVisitor
implements Runnable
{
	private final Logger logger = Logger.getLogger(this.getClass());
	private final Region region;
	private final boolean includeLeafNodes;
	private final boolean includeBranchNodes;
	private final boolean lockRegions;

	/**
	 * Create a GroupVisitor over the given region, specifying whether to include
	 * branch nodes or leaf nodes.  If includeBranchNodes is true then Group instances
	 * that have at least one child Group will be included.  If includeLeafNodes is true
	 * then Group instances that do not have any child Groups will be included.
	 * 
	 * @param region
	 * @param includeBranchNodes
	 * @param includeLeafNodes
	 * @param lockRegions
	 */
	public GroupVisitor(Region region, boolean includeBranchNodes, boolean includeLeafNodes, boolean lockRegions)
	{
		this.region = region;
		this.includeBranchNodes = includeBranchNodes;
		this.includeLeafNodes = includeLeafNodes;
		this.lockRegions = lockRegions;
	}
	
	public GroupVisitor(Region region, boolean includeBranchNodes, boolean includeLeafNodes)
	{
		this(region, includeBranchNodes, includeLeafNodes, false);
	}
	
	public Region getRegion()
	{
		return this.region;
	}
	
	public boolean isIncludeBranchNodes()
	{
		return this.includeBranchNodes;
	}
	
	public boolean isIncludeLeafNodes()
	{
		return this.includeLeafNodes;
	}

	public synchronized void run()
	{
		// sanity check
		if(!isIncludeBranchNodes() && ! isIncludeLeafNodes())
			return;
		
		try
		{
			if(lockRegions)
			{
				synchronized (region)
				{
					Iterator<? extends Group>  groupIter = region.getGroups();
					traverseGroups(new ArrayList<Group>(), groupIter);
				}
			}
			else
			{
				try
				{
					Iterator<? extends Group>  groupIter = region.getGroups();
					traverseGroups(new ArrayList<Group>(), groupIter);
				}
				catch (ConcurrentModificationException cmX)
				{
					cmX.printStackTrace();
				}
			}
		} 
		catch (CacheException x)
		{
			x.printStackTrace();
		}
	}

	/**
	 * @param groupIter
	 * @throws CacheException
	 */
	private void traverseGroups(List<Group> path, Iterator<? extends Group> groupIter) 
	throws CacheException
	{
		if(groupIter == null)
			return;
		
		while( groupIter.hasNext() )
		{
			Group group = null;
			try
			{
				group = groupIter.next();
			} 
			catch (ConcurrentModificationException cmX)
			{
				logger.warn("Unable to traverse groups, probably during an eviction pass.");
				break;
			}
			
			// if both branch and leaf nodes are requested then
			// don't bother checking the existence of child groups,
			// which can be an expensive operation
			if( isIncludeBranchNodes() && isIncludeLeafNodes() )
			{
				try{groupVisit(this.getRegion(), path, group);}
				catch(Exception x){x.printStackTrace();}
			}
			else
			{
				// do this once for efficiency (getGroups can be expensive)
				boolean groupHasChildGroups = group.getGroups().hasNext();
				
				// if include branch nodes and the group has child groups
				if(isIncludeBranchNodes() && groupHasChildGroups)
				{
					try{groupVisit(this.getRegion(), path, group);}
					catch(Exception x){x.printStackTrace();}
				}
				// if include leaf nodes and the group does not have child groups
				else if(isIncludeLeafNodes() && !groupHasChildGroups)
				{
					try{groupVisit(this.getRegion(), path, group);}
					catch(Exception x){x.printStackTrace();}
				}
			}
			
			List<Group> currentPath = new ArrayList<Group>();
			currentPath.addAll(path);
			currentPath.add(group);
			
			// recursively call ouselves to traverse our ancestors
			traverseGroups( currentPath, group.getGroups() );
		}
	}


	public abstract void groupVisit(Region region, List<Group> path, Group group)
	throws Exception;
}
