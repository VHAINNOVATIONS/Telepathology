/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import java.util.List;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.util.GroupVisitor;

/**
 * A class that simply visits every leaf node in a cache region and
 * sums the sizes.
 * For normal, synchronous use, call the static createAndRun() method.
 * To run asynch, creat an instance of the class manually and run it on
 * it own thread.  Get the collectiveSize when it is complete.
 * 
 * @author vhaiswbeckec
 *
 */
public class RegionSizeCalculator 
extends GroupVisitor
{
	public static long createAndRun(
			Region region)
	{
		RegionSizeCalculator calculator = new RegionSizeCalculator(region);
		calculator.run();
		
		return calculator.getCollectiveSize();
	}
	
	private long collectiveSize = 0L;
	
	/**
	 * @param region
	 * @param includeBranchNodes
	 * @param includeLeafNodes
	 */
	public RegionSizeCalculator(Region region)
	{
		super(region, false, true);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.util.GroupVisitor#groupVisit(gov.va.med.imaging.storage.cache.Region, java.util.List, gov.va.med.imaging.storage.cache.Group)
	 */
	@Override
	public void groupVisit(Region region, List<Group> path, Group group)
	throws Exception
	{
		collectiveSize += group.getSize();
	}

	/**
	 * 
	 * @return
	 */
	public long getCollectiveSize()
	{
		return this.collectiveSize;
	}
}
