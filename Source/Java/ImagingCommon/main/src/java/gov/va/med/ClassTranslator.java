/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jul 28, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A little helper that wraps up the ugly casting and typing into a templated
 * class. To use this class create an instance with the target type specified
 * for the template, e.g. ApplicationPropertyAccessor<Short>
 * shortPropertyAccessor = new ApplicationPropertyAccessor<Short>("shortValue",
 * "parseShort"); The method names should be the method to convert from other
 * base types to the target type and from a String to the target type.
 * 
 * For an example of usage, see
 * 
 * @see gov.va.med.imaging.transactioncontext.TransactionContextProxyInvocationHandler
 * 
 * @author VHAISWBECKEC
 * 
 * @param <SRC> - the type of Object that we are converting from.
 * @param <DEST> - the type of Object that we are converting to.
 */
public class ClassTranslator<SRC, DEST>
{
	private static Logger logger = Logger.getLogger(ClassTranslator.class);
	
	private static final Map<ConversionMapKey, Method> conversionMethodMap;
	
	static
	{
		conversionMethodMap = new HashMap<ConversionMapKey, Method>();
		
		try
		{
		}
		catch (Exception x)
		{
			logger.error(x);
		}		
	}
	
	public static <SRC extends Object, DEST extends Object> void addConversionMethod(Class<SRC> sourceClass, Class<DEST> destinationClass, Method conversionMethod)
	{
		conversionMethodMap.put(new ConversionMapKey(sourceClass, destinationClass), conversionMethod);
	}
	
	public static <SRC extends Object, DEST extends Object> ClassTranslator<SRC, DEST> create(
		ConversionMapKey conversionMapKey)
	{
		try
		{
			Method registeredMethod = conversionMethodMap.get(conversionMapKey);
			if(registeredMethod != null)
				return new ClassTranslator<SRC, DEST>( registeredMethod );
			if(conversionMapKey.getSourceClass() == String.class)
				return new ClassTranslator<SRC, DEST>( conversionMapKey.getDestinationClass().getMethod("valueOf", conversionMapKey.getSourceClass()) );
			if(conversionMapKey.getDestinationClass() == String.class)
				return new ClassTranslator<SRC, DEST>( conversionMapKey.getSourceClass().getMethod("toString", conversionMapKey.getDestinationClass()) );
		}
		catch (Exception x)
		{
			logger.warn("Don't know how to convert '" + conversionMapKey.getSourceClass().getName() + "' to '" + conversionMapKey.getDestinationClass().getName() + "'.");
			// no conversion known, just return null
		}
		
		return null;
	}
	
	public static <SRC extends Object, DEST extends Object> ClassTranslator<SRC, DEST> create(
		Class<SRC> sourceClass, 
		Class<DEST> destinationClass)
	{
		return create(new ConversionMapKey(sourceClass, destinationClass));
	}

	// ====================================================================================================
	// 
	// ====================================================================================================
	private final Method conversionMethod;
	private final boolean staticConversion;

	/**
	 * 
	 */
	public ClassTranslator(Method conversionMethod)
	{
		this.conversionMethod = conversionMethod;			// the Method to convert
		if( !Modifier.isPublic(this.conversionMethod.getModifiers()) )
			throw new IllegalArgumentException("The method '" + conversionMethod.getName() + "' must be a public method and it is not.");
		
		if( Modifier.isStatic(this.conversionMethod.getModifiers()) && conversionMethod.getParameterTypes().length == 1 )
			staticConversion = true;
		else if( !Modifier.isStatic(this.conversionMethod.getModifiers()) && conversionMethod.getParameterTypes().length == 0 )
			staticConversion = false;
		else
			throw new IllegalArgumentException("The method '" + conversionMethod.getName() + "' must be either a static method taking one parameter or an instance method taking none.");
	}

	@SuppressWarnings("unchecked")
	public Object convert(Object value)
	{
		if (value == null)
			return (DEST)null;

		Class<? extends SRC>[] conversionParameterTypes = (Class<? extends SRC>[])conversionMethod.getParameterTypes();

		try
		{
			// converting from S to T usually means calling
			// a parse method
			if(conversionMethod != null)
			{
				DEST convertedValue = (DEST)this.conversionMethod.invoke(
					this.staticConversion ? null : value,
					this.staticConversion ? conversionParameterTypes[0].cast(value) : (Object[])null);
				return convertedValue;
			}
		}
		catch (ClassCastException ccX)
		{
			logger.error(ccX);
		}
		catch (Exception e)
		{
			logger.warn(e);
		}
		
		return (DEST)null;
	}
	
	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	static class ConversionMapKey
	{
		private final Class<?> sourceClass;
		private final Class<?> destinationClass;
		
		public ConversionMapKey(Class<?> sourceClass, Class<?> destinationClass)
		{
			super();
			this.sourceClass = sourceClass;
			this.destinationClass = destinationClass;
		}
		public Class<?> getSourceClass()
		{
			return this.sourceClass;
		}
		public Class<?> getDestinationClass()
		{
			return this.destinationClass;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.destinationClass == null) ? 0 : this.destinationClass.hashCode());
			result = prime * result + ((this.sourceClass == null) ? 0 : this.sourceClass.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConversionMapKey other = (ConversionMapKey) obj;
			if (this.destinationClass == null)
			{
				if (other.destinationClass != null)
					return false;
			}
			else if (!this.destinationClass.equals(other.destinationClass))
				return false;
			if (this.sourceClass == null)
			{
				if (other.sourceClass != null)
					return false;
			}
			else if (!this.sourceClass.equals(other.sourceClass))
				return false;
			return true;
		}
	}
}
