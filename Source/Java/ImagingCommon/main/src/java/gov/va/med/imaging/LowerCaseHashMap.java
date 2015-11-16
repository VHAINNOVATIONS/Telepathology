/**
 * 
 */
package gov.va.med.imaging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author VHAISWBECKEC
 *
 */
public class LowerCaseHashMap<K extends String, V extends Object> 
extends HashMap
{

	/**
	 * 
	 */
	public LowerCaseHashMap (Map<K, V> sourceMap)
	{
		Set<K> sourceKeySet = sourceMap.keySet();
		
		for(Iterator<K> sourceKeyIter = sourceKeySet.iterator(); sourceKeyIter.hasNext(); )
		{
			String key = (String)sourceKeyIter.next();
			put(key.toLowerCase(), sourceMap.get(key));
		}
	}

}
