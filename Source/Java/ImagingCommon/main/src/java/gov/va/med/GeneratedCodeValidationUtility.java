/**
 * 
 */
package gov.va.med;

import gov.va.med.exceptions.ValidationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class implements some simple testing of the router facades created
 * by the code generation in the VIX.  It may be used for similar purposes
 * in other places.
 * This class simply validates that there is one and only one implementation
 * in a class of each method declared in an interface.
 * 
 * @author vhaiswbeckec
 *
 */
public class GeneratedCodeValidationUtility
{
	private final static Logger logger = Logger.getLogger(GeneratedCodeValidationUtility.class);
	
	
	public static void validateCommandsExist(Class<?> routerTesterImplementation)
	throws ValidationException
	{
		// there is a no-arg constructor
		Constructor<?> constructor = null;
		Object routerTesterInstance = null;
		try
		{
			constructor = routerTesterImplementation.getConstructor(new Class<?> [0]);
			routerTesterInstance = constructor.newInstance(new Object[0]);
		}
		catch (SecurityException e1)
		{
			throw new ValidationException("SecurityException creating router test implementation, " + e1.getMessage(), e1);
		}
		catch (NoSuchMethodException e1)
		{
			throw new ValidationException("NoSuchMethodException creating router test implementation, " + e1.getMessage(), e1);
		}
		catch (IllegalArgumentException e)
		{
			throw new ValidationException("IllegalArgumentException creating router test implementation, " + e.getMessage(), e);
		}
		catch (InstantiationException e)
		{
			throw new ValidationException("InstantiationException creating router test implementation, " + e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new ValidationException("IllegalAccessException creating router test implementation, " + e.getMessage(), e);
		}
		catch (InvocationTargetException e)
		{
			throw new ValidationException("InvocationTargetException creating router test implementation, " + e.getMessage(), e);
		}
		
	 	Method [] methods = routerTesterImplementation.getMethods();
	 	int methodTestedCount = 0;
	 	for(Method method : methods)
	 	{
	 		// only test methods from this interface (not methods derived from)
	 		if(method.getDeclaringClass().equals(routerTesterImplementation))
	 		{
		 		//System.out.println("Executing method '" + method.getName() + "'.");
		 		Class<?>[] parameterTypes = method.getParameterTypes();
		 		Object[] parameters = new Object[parameterTypes.length];
		 		for(int i = 0; i < parameterTypes.length; i++)
		 		{
		 			// if the data type is a primitive type, then need to do something
		 			parameters[i] = createDefaultValue(parameterTypes[i]);
		 		}
		 		try
				{
					method.invoke(routerTesterInstance, parameters);
					methodTestedCount++;
				}		 		
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					
					throw new ValidationException("IllegalArgumentException invoking method (" + method.getName() + "), " + e.getMessage(), e);
				}
				catch (IllegalAccessException e)
				{
					throw new ValidationException("IllegalAccessException invoking method (" + method.getName() + "), " + e.getMessage(), e);
				}
				catch (InvocationTargetException e)
				{
					if(e.getCause() instanceof ValidationException)
						throw (ValidationException)e.getCause();
					e.printStackTrace();
					throw new ValidationException("InvocationTargetException invoking method (" + method.getName() + "), " + e.getMessage(), e);
					
				}
	 		}	 	
	 	}
	 	logger.info("Tested '" + methodTestedCount + "' methods for router facade tester '" + routerTesterImplementation.getName() + "'.");
	}
	
	private static Object createDefaultValue(Class<?> parameterType)
	{
		if(parameterType.isPrimitive())
		{
			if(parameterType == boolean.class)
			{
				return false;
			}
			else if((parameterType == int.class) ||
					(parameterType == long.class) ||
					(parameterType == short.class))					
			{
				return 0;
			}
			else if((parameterType == float.class) ||
					(parameterType == double.class))
			{
				return 0.0f;
			}
			else if(parameterType == char.class)
			{
				return (char)0;
			}
			else if(parameterType == byte.class)
			{
				return (byte)0;
			}
		}
		return null;
	}
	
	/**
	 * Assure that for each interface method declaration there is one exact match in the implementation. 
	 * 
	 * @param routerInterface
	 * @param routerImplementation
	 */
	public static void validateImplementation(Class<?> routerInterface, Class<?> routerImplementation)
	throws ValidationException
	{
		StringBuilder errorMessages = new StringBuilder();
		boolean unitTestSuccess = true;
		List<Method> matchedImplementationMethodList = new ArrayList<Method>();
		List<Method> suspiciousList = new ArrayList<Method>();
		
		// for every  interface method there must be one exact matching implementation method
		for( Method interfaceMethod : routerInterface.getMethods() )
		{
			Method matchingImplementationMethod = null;
			String interfaceMethodName = interfaceMethod.getName();
			
			for( Method implementationMethod : routerImplementation.getMethods() )
			{
				String implementationMethodName = implementationMethod.getName();
				if( interfaceMethodName.equals(implementationMethodName) )
				{
					Class<?>[] interfaceMethodParameters = interfaceMethod.getParameterTypes();
					Class<?>[] implementationMethodParameters = implementationMethod.getParameterTypes();
					
					Class<?> interfaceMethodReturn = interfaceMethod.getReturnType();
					Class<?> implementationMethodReturn = implementationMethod.getReturnType();
					
					// if there are no parameters in either the interface or implementation
					// then the method parameters match
					if( (interfaceMethodParameters == null || interfaceMethodParameters.length == 0) && 
						(implementationMethodParameters == null || implementationMethodParameters.length == 0) )
					{
						if(interfaceMethodReturn.equals(implementationMethodReturn))
							matchingImplementationMethod = implementationMethod;
						else
							errorMessages.append("Method '" + implementationMethodName +
								"' defined in '" + routerImplementation.getSimpleName() + 
								"' has the same signature as the method declared in '" + routerInterface.getSimpleName() +
								"' but the return types differ.");
					}
					// if the parameter lists match then the methods match
					else if(interfaceMethodParameters != null && interfaceMethodParameters.length == implementationMethodParameters.length)
					{
						for(int parameterIndex=0; parameterIndex < interfaceMethodParameters.length; ++parameterIndex)
							if( !interfaceMethodParameters[parameterIndex].equals(implementationMethodParameters[parameterIndex]) )
							{
								// this could be okay (as in an overloaded method name) or it
								// could be an error in generation.
								if( !matchedImplementationMethodList.contains(implementationMethod) )
									suspiciousList.add(implementationMethod);
								break;
							}
							else if(parameterIndex == interfaceMethodParameters.length-1)
								matchingImplementationMethod = implementationMethod;
					}
					// else the name matches but nothing else matches
					// this could be okay (as in an overloaded method name) or it
					// could be an error in generation.
					else if( !matchedImplementationMethodList.contains(implementationMethod) )
						suspiciousList.add(implementationMethod);
				}
			}
			
			if(matchingImplementationMethod != null)
			{
				suspiciousList.remove(matchingImplementationMethod);		// remove it from the suspicious list
				matchedImplementationMethodList.add(matchingImplementationMethod);	// add it to the matched list
				System.out.println(
					routerInterface.getName() + "." + interfaceMethodName + "->" +
					routerImplementation.getName() + "." + matchingImplementationMethod.getName()
				);
			}
			else
			{
				unitTestSuccess = false;
				errorMessages.append("Method '" + interfaceMethodName +
					"' declared in '" + routerInterface.getSimpleName() + 
					"' does not exist in the implementation '" + routerImplementation.getSimpleName() +
					"'.");
			}
		}
		
		for(Method implementationMethod : suspiciousList)
		{
			unitTestSuccess = false;
			errorMessages.append("Method '" + implementationMethod.getName() +
				"' defined in '" + routerImplementation.getSimpleName() + 
				"' is not declared in '" + routerInterface.getSimpleName() +
				"'.");
		}
		
		if(!unitTestSuccess)
			throw new ValidationException(errorMessages.toString());
	}
}
