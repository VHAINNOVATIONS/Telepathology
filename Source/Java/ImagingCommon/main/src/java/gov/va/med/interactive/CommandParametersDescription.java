package gov.va.med.interactive;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Value object describing each parameter of a command.
 * @author VHAISWBECKEC
 *
 */
public class CommandParametersDescription<T>
{
	private final String parameterName;
	private final Class<T> parameterClass;
	private final boolean required;
	private final boolean variant;			// true only for the last parameter, allows repeating
	private String description;
	
	public CommandParametersDescription(Class<T> parameterClass, boolean required)
	{
		this(null, parameterClass, required, false, null);
	}
	
	public CommandParametersDescription(String name, Class<T> parameterClass, boolean required)
	{
		this(name, parameterClass, required, false, null);
	}
	
	public CommandParametersDescription(String name, Class<T> parameterClass, boolean required, boolean variant)
	{
		this(name, parameterClass, required, variant, null);
	}
	
	public CommandParametersDescription(String name, Class<T> parameterClass, boolean required, String description)
	{
		this(name, parameterClass, required, false, description);
	}

	/**
	 * The only real constructor, all else delegate here.
	 * @param name
	 * @param parameterClass
	 * @param required
	 * @param variant
	 * @param description
	 */
	public CommandParametersDescription(
		String name, 
		Class<T> parameterClass, 
		boolean required, 
		boolean variant, 
		String description)
	{
		this.parameterName = name;
		this.parameterClass = parameterClass;
		this.required = required;
		this.variant = variant;
		this.description = description;
	}
	
	/**
	 * @return the parameterName
	 */
	public String getParameterName()
	{
		return this.parameterName;
	}

	/**
	 * @return true if the parameter is required
	 */
	public boolean isRequired()
	{
		return this.required;
	}

	/**
	 * @return the variant
	 */
	public boolean isVariant()
	{
		return this.variant;
	}

	/**
	 * @return the class of the parameter
	 */
	public Class<T> getParameterClass()
	{
		return this.parameterClass;
	}

	/**
	 * If getParameterClass() returns an array type, this returns
	 * the member type 
	 * else this returns the same as getParameterClass().
	 * 
	 * @return
	 */
	public Class<?> getParameterArrayMemberClass()
	{
		if(getParameterClass().isArray())
			return getParameterClass().getComponentType();
		else
			return getParameterClass();
	}
	
	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Convert a raw value (a String) into the type specified by the parameter class.
	 * This method tries two ways to do the type conversion, the first is to look for
	 * a constructor that takes a single String arg, the second is to look for a static
	 * "parse" method that takes a single String arg.  If neither exists or an exception
	 * is thrown from both methods then a CommandTypeValidationException is thrown.
	 * 
	 * @param rawValue
	 * @return
	 * @throws CommandTypeValidationException 
	 */
	public T getValue(String rawValue) 
	throws CommandTypeValidationException
	{
		if(getParameterClass().isArray() && this.getParameterClass().isArray())
			return getArrayValue(rawValue);
		else
			return getSingleValue(rawValue);
	}

	@SuppressWarnings("unchecked")
	private T getArrayValue(String rawValue) 
	throws CommandTypeValidationException
	{
		String[] rawValues = rawValue.split(",");
		T values = (T)Array.newInstance(this.getParameterClass().getComponentType(), rawValues.length);
		
		int index = 0;
		for(String rawValuesMember : rawValues)
		{
			Array.set(values, index, getSingleValue(rawValuesMember) );
			index++;
		}
		
		return values;
	}

	@SuppressWarnings("unchecked")
	private T getSingleValue(String rawValue) 
	throws CommandTypeValidationException
	{
		return (T)getSingleValueInternal(rawValue);
	}
	
	private Object getSingleValueInternal(String rawValue) 
	throws CommandTypeValidationException
	{
		// note that getParameterArrayMemberClass() returns the same as
		// getParameterClass() if the type is not an array
		Class<?> resultClass = getParameterArrayMemberClass();
		
		// special case for Boolean values, where the presence indicates TRUE and lack thereof
		// indicates FALSE 
		if(rawValue == null && Boolean.class == resultClass)
			return Boolean.FALSE;
		
		if(String.class == resultClass)
			return rawValue;
		
		try
		{
			Constructor<?> parameterConstructor = resultClass.getConstructor(String.class);
			return parameterConstructor.newInstance(rawValue);
		} 
		catch (Exception x)
		{
			try
			{
				Method parseMethod = resultClass.getMethod("parse", String.class);
				if( resultClass.equals(parseMethod.getReturnType()) && Modifier.isStatic(parseMethod.getModifiers()) )
					return parseMethod.invoke(null, rawValue);
			}
			catch (Exception x2)
			{
				throw new CommandTypeValidationException(getParameterName(), resultClass, rawValue);
			}
		} 

		return null;
	}
}