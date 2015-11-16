/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.HashMap;
import java.util.Map;

/**
 * A real utility class.  
 * A collection of static methods used only by other members of this package.
 * 
 * @author vhaiswbeckec
 *
 */
public class RouterSemanticsUtility
{
	private static Map<String, Class<?>> primitiveToClassMap;
	
	static
	{
		primitiveToClassMap = new HashMap<String, Class<?>>();
		
		primitiveToClassMap.put("byte", java.lang.Byte.class);
		primitiveToClassMap.put("short", java.lang.Short.class);
		primitiveToClassMap.put("int", java.lang.Integer.class);
		primitiveToClassMap.put("long", java.lang.Long.class);
		primitiveToClassMap.put("float", java.lang.Float.class);
		primitiveToClassMap.put("double", java.lang.Double.class);
		primitiveToClassMap.put("char", java.lang.Character.class);
		primitiveToClassMap.put("void", java.lang.Void.class);
	}
	
	/**
	 * Upper-case the first element of the string.
	 * 
	 * @param element
	 * @return
	 */
	public static String standardizeToClassName(String element)
	{
		return setFirstCharCase(element, true);
	}
	
	/**
	 * Lower-case the first element of the string.
	 * 
	 * @param element
	 * @return
	 */
	public static String standardizeToMethodName(String element)
	{
		return setFirstCharCase(element, false);
	}
	
	/**
	 * 
	 * @param element
	 * @param upperCase
	 * @return
	 */
	static String setFirstCharCase(String element, boolean upperCase)
	{
		if(element == null || element.length() == 0)
			return element;
		
		char firstChar = upperCase ?
			Character.toUpperCase( element.charAt(0) ) :
			Character.toLowerCase( element.charAt(0) );
		if(element.length() == 1)
			return new String( new char[]{firstChar} );
		return new String( new char[]{firstChar} ) + element.substring(1);
		
	}

	/**
	 * 
	 * @param packageName
	 * @return
	 */
	static String formatPackageName(String packageName)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(packageName);
		sb.append(".*");

		return sb.toString();
	}
	
	/**
	 * If the type is an atomic type then return the name of the Class
	 * wrapper for that particular type, else return the type as given. 
	 * 
	 * @param type
	 * @return
	 */
	public static String mapPrimitiveToClassType(String type)
	{
		Class<?> mappedType = primitiveToClassMap.get(type);
		if(mappedType != null)
			return mappedType.getName();
		
		return type;
	}
}
