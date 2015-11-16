/**
 * 
 */
package gov.va.med.imaging.storage.cache.impl;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import gov.va.med.imaging.storage.cache.Group;
import gov.va.med.imaging.storage.cache.Region;
import gov.va.med.imaging.storage.cache.util.GroupVisitor;

/**
 * @author VHAISWBECKEC
 *
 */
public class GroupStatisticsReporter 
extends GroupVisitor
{
	/**
	 * 
	 * @param region
	 * @param includeBranchNodes
	 * @param includeLeafNodes
	 * @param writer
	 */
	public static void createAndRun(Region region, boolean includeBranchNodes, boolean includeLeafNodes, Writer writer)
	{
		GroupStatisticsReporter reporter = new GroupStatisticsReporter(region, includeBranchNodes, includeLeafNodes, writer);
		reporter.run();
	}
	
	private Writer writer;
	private DateFormat df = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
	
	private GroupStatisticsReporter(Region region, boolean includeBranchNodes, boolean includeLeafNodes, Writer writer)
	{
		super(region, includeBranchNodes, includeLeafNodes);
		this.writer = writer;
		
		StringBuilder sb = new StringBuilder();
		sb.append("Group Name");
		sb.append("\t");
		sb.append("Last Accessed");
		sb.append("\t");
		sb.append("Size");
		sb.append("\n");
		try
		{
			writer.write( sb.toString() );
		} 
		catch (IOException x)
		{
			x.printStackTrace();
		}
	}

	/**
	 * @see gov.va.med.imaging.storage.cache.util.GroupVisitor#groupVisit(gov.va.med.imaging.storage.cache.Group)
	 */
	@Override
	public void groupVisit(Region region, List<Group> path, Group group)
	throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append(group.getName());
		sb.append("\t");
		sb.append( df.format(group.getLastAccessed()) );
		sb.append("\t");
		sb.append(group.getSize());
		sb.append("\n");
		writer.write( sb.toString() );
	}

}
