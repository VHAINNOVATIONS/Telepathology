/**
 * 
 */
package gov.va.med;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vhaiswbeckec
 *
 */
public class JavaBeanUtility
{
	public static String SETTER_PREFIX = "set";
	public static String GETTER_PREFIX = "get";

	/**
	 * 
	 * @param obj
	 * @param propertyName - should be explicitly cast to a type if null
	 * @param propertyValue
	 */
	public static void setProperty(Object obj, String propertyName, Object propertyValue)
	throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		setProperty(obj, propertyName, propertyValue, false);
	}

	/**
	 * 
	 * @param obj
	 * @param propertyName
	 * @param propertyValue - should be explicitly cast to a type if null
	 * @param overrideScoping
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static <T extends Object> void setProperty(Object obj, String propertyName, T propertyValue, boolean overrideScoping) 
	throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		String propertyBaseName = normalizePropertyBaseName(propertyName);
		String propertySetter = SETTER_PREFIX + propertyBaseName;
		
		Class<?> objClass = obj.getClass();
		
		Method setter = objClass.getMethod(propertySetter, new Class[]{propertyValue.getClass()});
		if( Void.class != setter.getReturnType() )
			throw new NoSuchMethodException("The class '" + obj.getClass().getName() + "' does not have a proper setter method named '" + propertySetter + "' returning a void type.");
		
		if(overrideScoping)
			setter.setAccessible(true);
		
		setter.invoke(obj, new Object[]{propertyValue});
		
		return;
	}
	
	/**
	 * @param propertyName
	 * @return
	 */
	private static String normalizePropertyBaseName(String propertyName)
	{
		if(propertyName == null) return null;
		propertyName = propertyName.trim();
		if(propertyName.length() < 1) return "";
		char firstChar = propertyName.charAt(0);
		return Character.toUpperCase(firstChar) + propertyName.substring(1);
	}

	public static Object getProperty(Object obj, String propertyName)
	throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassCastException
	{
		return getProperty(obj, propertyName, false);
	}
	
	/**
	 * 
	 * @param obj
	 * @param propertyName
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object getProperty(Object obj, String propertyName, boolean overrideScoping)
	throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassCastException
	{
		String propertyBaseName = normalizePropertyBaseName(propertyName);
		String propertyGetter = GETTER_PREFIX + propertyBaseName;
		
		Class<?> objClass = obj.getClass();
		
		Method setter = objClass.getMethod(propertyGetter, new Class[]{});
		
		if(overrideScoping)
			setter.setAccessible(true);
		
		return setter.invoke(obj, new Object[]{});
	}
	
	/**
	 * 
	 * @param type
	 * @param propertyName
	 * @param propertyType
	 * @return
	 */
	public static Method findSetter(Class<?> type, String propertyName, Class<?> propertyType)
	{
		String propertyBaseName = normalizePropertyBaseName(propertyName);
		String propertySetter = SETTER_PREFIX + propertyBaseName;
		
		return findMethod(type, propertySetter, new Class<?>[]{propertyType}, Void.class );
	}
	
	/**
	 * 
	 * @param type
	 * @param propertyName
	 * @param propertyType
	 * @return
	 */
	public static Method findGetter(Class<?> type, String propertyName, Class<?> propertyType)
	{
		String propertyBaseName = normalizePropertyBaseName(propertyName);
		String propertyGetter = GETTER_PREFIX + propertyBaseName;
		
		return findMethod(type, propertyGetter, new Class<?>[]{}, propertyType );
	}
	
	public static String normalizedGetterName(String propertyName)
	{
		String propertyBaseName = normalizePropertyBaseName(propertyName);
		return GETTER_PREFIX + propertyBaseName;
	}
	
	public static String normalizedSetterName(String propertyName)
	{
		String propertyBaseName = normalizePropertyBaseName(propertyName);
		return SETTER_PREFIX + propertyBaseName;
	}
	
	/**
	 * All args must be supplied, if any args are null then this method returns null.
	 * 
	 * @param type
	 * @param methodName
	 * @param parameterTypes
	 * @param resultType
	 * @return
	 */
	public static Method findMethod(Class<?> type, String methodName, Class<?>[] parameterTypes, Class<?> resultType)
	{
		if(type == null || methodName == null || parameterTypes == null || resultType == null)
			return null;
		
		List<Method> compatibleMethods = new ArrayList<Method>();
		
		for(Method method : type.getMethods())
		{
			if(! methodName.equals(method.getName()))
				continue;
			if(! method.getReturnType().isAssignableFrom(resultType) )
				continue;
			
			Class<?>[] methodParameterTypes = method.getParameterTypes();
			if(methodParameterTypes.length != parameterTypes.length)
				continue;
			
			boolean compatibleParameters = true; 
			for(int parameterIndex=0; parameterIndex < parameterTypes.length && compatibleParameters; ++parameterIndex)
				compatibleParameters |= methodParameterTypes[parameterIndex].isAssignableFrom(parameterTypes[parameterIndex]);
			if(! compatibleParameters)
				continue;
			
			compatibleMethods.add(method);
		}
		
		return compatibleMethods.size() > 0 ? compatibleMethods.get(0) : null;
	}
	
	/**
	 * Returns a List of all of the methods in the given class that have the correct
	 * name, based on the propertyName, of a setter for that property.
	 * 
	 * @param type
	 * @param propertyName
	 * @return
	 */
	public static List<Method> findSetterMethods(Class<?> type, String propertyName)
	{
		if(type == null || propertyName == null)
			return null;
		String setterName = normalizedSetterName(propertyName);
		
		List<Method> compatibleMethods = new ArrayList<Method>();
		
		for(Method method : type.getMethods())
			if( setterName.equals(method.getName()) && method.getParameterTypes().length == 1 )
				compatibleMethods.add(method);
		
		return compatibleMethods;
	}
	
	/**
	 * Returns a List of all of the methods in the given class that have the correct
	 * name, based on the propertyName, of a getter for that property.
	 * 
	 * @param type
	 * @param propertyName
	 * @return
	 */
	public List<Method> findGetterMethods(Class<?> type, String propertyName)
	{
		if(type == null || propertyName == null)
			return null;
		String getterName = normalizedGetterName(propertyName);
		
		List<Method> compatibleMethods = new ArrayList<Method>();
		
		for(Method method : type.getMethods())
			if( getterName.equals(method.getName()) && method.getParameterTypes().length == 0 )
				compatibleMethods.add(method);
		
		return compatibleMethods;
	}
}
