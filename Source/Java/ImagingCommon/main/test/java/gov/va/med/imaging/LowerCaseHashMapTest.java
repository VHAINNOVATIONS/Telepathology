package gov.va.med.imaging;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class LowerCaseHashMapTest extends TestCase
{

	public void testLowerCaseHashMap()
	{
		Map<String, String> source = null;
		LowerCaseHashMap<String, String> result = null;
		
		source = new HashMap<String, String>();
		source.put("Hello", "world");
		result = new LowerCaseHashMap<String, String>(source);
		assertEquals("world", result.get("hello") );
	}

}
