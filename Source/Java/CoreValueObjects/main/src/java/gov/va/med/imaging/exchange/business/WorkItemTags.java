package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkItemTags
{
	private List<WorkItemTag> tags = new ArrayList<WorkItemTag>();
	private HashMap<String, List<String>> tagMap = new HashMap<String, List<String>>();
	
	public void addTag(String key, String value)
	{
		// First, add it to the list of workItems
		tags.add(new WorkItemTag(key, value));
		
		// Next add it to the HashMap representation
		if (tagMap.containsKey(key))
		{
			tagMap.get(key).add(value);
		}
		else
		{
			// We don't have a value for this key yet.
			// Create the list of values, add this value, 
			// then add the key and valueList to the map
			List<String> values = new ArrayList<String>();
			values.add(value);
			tagMap.put(key, values);
		}
		
	}
	
	public List<String> getValueList(String key)
	{
		List<String> valueList = null;
		if (tagMap.containsKey(key))
		{
			valueList = tagMap.get(key);
		}
		
		return valueList;
	}
	
	public String getValue(String key)
	{
		String value = "";
		if (tagMap.containsKey(key) && tagMap.get(key) != null)
		{
			List<String> values = tagMap.get(key);
			
			if (values.size() > 0)
			{
				value = values.get(0);
			}
		}
		
		return value;
	}

	public List<WorkItemTag> getTags() {
		return tags;
	}
	
	
}
