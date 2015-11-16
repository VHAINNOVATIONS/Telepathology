/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import java.util.List;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.util.GroupVisitor;

/**
 * @author VHAISWBECKEC
 * 
 * This class will traverse each Group in a region and build a list
 * of the oldest Group instances in the region such that the total size of all the 
 * Group instances will be less than but close to the target size.
 *
 */
public class GroupEvictionCandidateVisitor 
extends GroupVisitor
{
	/**
	 * 
	 * @param region
	 * @param includeBranchNodes
	 * @param includeLeafNodes
	 * @param writer
	 */
	public static TargetSizeGroupPathSet createAndRun(
		Region region, boolean includeBranchNodes, boolean includeLeafNodes, long targetSize)
	{
		GroupEvictionCandidateVisitor reporter = 
			new GroupEvictionCandidateVisitor(region, includeBranchNodes, includeLeafNodes, targetSize);
		reporter.run();
		
		return reporter.getEvictionCandidates();
	}
	
	private final TargetSizeGroupPathSet evictionCandidates;
	
	private GroupEvictionCandidateVisitor(
		Region region, boolean includeBranchNodes, boolean includeLeafNodes, long targetSize)
	{
		super(region, includeBranchNodes, includeLeafNodes, true);
		this.evictionCandidates = new TargetSizeGroupPathSet(targetSize);
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.util.GroupVisitor#groupVisit(gov.va.med.imaging.storage.cache.Group)
	 */
	@Override
	public void groupVisit(Region region, List<Group> path, Group group)
	throws Exception
	{
		evictionCandidates.add( new GroupPath(region, path, group) );
	}

	public TargetSizeGroupPathSet getEvictionCandidates()
	{
		return this.evictionCandidates;
	}
	
	public long getTotalVisitedSize()
	{
		return this.evictionCandidates.getTotalVisitedSize();
	}
}
