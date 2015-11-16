package gov.va.med.imaging.exchange.business;

import java.util.HashMap;
import java.util.List;

public class WorkItemCounts 
{
	private HashMap<String, HashMap<String, Integer>> workItemCounts = new HashMap<String, HashMap<String, Integer>>();
	
	public void addCountForSubtypeAndStatus(String subtype, String status, int count)
	{
		// Find or create the map of statuses for the specified subtype
		HashMap<String, Integer> countsForSubtype;
		if (workItemCounts.containsKey(subtype))
		{
			countsForSubtype = workItemCounts.get(subtype);
		}
		else
		{
			countsForSubtype = new HashMap<String, Integer>();
			workItemCounts.put(subtype, countsForSubtype);
		}
		
		// Add the counts for this status under the appropriate subytpe
		countsForSubtype.put(status, count);
		
	}
	
	/**
	 * Returns the count of work items of the specified subtype with the specified status
	 * @param subtype
	 * @param status
	 * @return
	 */
	public int getCountForSubtypeAndStatus(String subtype, String status)
	{
		int count = 0;
		
		if(workItemCounts.containsKey(subtype))
		{
			HashMap<String, Integer> countsForSubtype = workItemCounts.get(subtype);
			
			if (countsForSubtype.containsKey(status))
			{
				count = countsForSubtype.get(status);
			}
		}
		
		return count;
	}
	
	/**
	 * Returns the count of workitems with the provided status across all specified subtypes
	 * @param subtypes
	 * @param status
	 * @return
	 */
	public int getCountForSubtypesAndStatus(List<String> subtypes, String status)
	{
		// If the list of subtypes is null, return 0
		if (subtypes == null)
			return 0;

		// Subtype list is present, so continue
		int count = 0;
		for (String subtype : subtypes)
		{
			count += getCountForSubtypeAndStatus(subtype, status);
		}
		
		return count;
	}

	/**
	 * Returns the count of workitems having the given subtype and any of the provided statuses
	 * 
	 * @param subtype
	 * @param statuses
	 * @return
	 */
	public int getCountForSubtypeAndStatuses(String subtype, List<String> statuses)
	{
		// If the list of statuses is null, return 0
		if (statuses == null)
			return 0;

		// Status list is present, so continue
		int count = 0;
		for (String status : statuses)
		{
			count += getCountForSubtypeAndStatus(subtype, status);
		}
		
		return count;
	}

	/**
	 * Given a list of subtypes, and a list of statuses, this returns the count of work items
	 * in the cartesian join of statuses and subtypes.
	 * @param subtypes
	 * @param statuses
	 * @return
	 */
	public int getCountForSubtypesAndStatuses(List<String> subtypes, List<String> statuses)
	{
		// If either of the collections are null, return 0
		if (subtypes == null || statuses == null)
			return 0;


		// Neither collection is null, so continue
		int count = 0;
		for (String status : statuses)
		{
			for(String subtype : subtypes)
			{
				count += getCountForSubtypeAndStatus(subtype, status);
			}
		}
		
		return count;
	}

}
