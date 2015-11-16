/**
 * 
 */
package gov.va.med;

/**
 * @author vhaiswbeckec
 * 
 * The top-level Internet media types, of which their are seven
 * currently defined.
 * 
 * @see http://tools.ietf.org/html/rfc2046
 *
 */
public enum TopLevelMediaType
{
	TEXT("text"),
	IMAGE("image"),
	AUDIO("audio"),
	VIDEO("video"),
	APPLICATION("application"),
	MULTIPART("multipart"),
	MESSAGE("message");
	
	private final String value;
	TopLevelMediaType(String value)
	{
		this.value = value;
	}
	
	public static TopLevelMediaType lookup(String value)
	{
		if(value == null)
			return null;
		
		for(TopLevelMediaType topLevelMediaType : TopLevelMediaType.values())
			if(value.equalsIgnoreCase(topLevelMediaType.value))
				return topLevelMediaType;
		
		return null;
	}
}
