/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive.commands;

import gov.va.med.JavaBeanUtility;
import gov.va.med.imaging.storage.cache.Cache;
import gov.va.med.imaging.storage.cache.EvictionStrategy;
import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author VHAISWBECKEC
 *
 */
public class ModifyEvictionCommand 
extends Command<CacheManagerImpl>
{
	private static CommandParametersDescription[] commandParameters = new CommandParametersDescription[]
    {
		new CommandParametersDescription(String.class, true), 	// eviction strategy name
		new CommandParametersDescription(String.class, true),	// property name
		new CommandParametersDescription(String.class, true)	// property value
   	};
   	
  	public static CommandParametersDescription[] getCommandParametersDescription()
  	{
  		return commandParameters;
  	}

	/**
	 * @param commandParameterValues
	 */
	public ModifyEvictionCommand(String[] commandParameterValues)
	{
		super(commandParameterValues);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.storage.cache.interactive.ValidCommandProcessor#processCommand(gov.va.med.imaging.storage.cache.interactive.CommandProcessor, gov.va.med.imaging.storage.cache.impl.CacheManagerImpl, gov.va.med.imaging.storage.cache.interactive.Command)
	 */
	@Override
	public void processCommand(CommandProcessor processor, CacheManagerImpl manager) 
	throws Exception
	{
		String evictionStrategyName = getCommandParameterValues()[0];
		String propertyKey = getCommandParameterValues()[1];
		String propertyValue = getCommandParameterValues()[2];
		
		Cache cache = manager.getActiveCache();

		EvictionStrategy evictionStrategy = cache.getEvictionStrategy(evictionStrategyName);
		if(evictionStrategy != null)
			modifyProperty(evictionStrategy, propertyKey, propertyValue);
		else
			System.err.println("Eviction strategy '" + evictionStrategyName + "' not found in active cache.");
	}

	/**
	 * @param evictionStrategy
	 * @param propertyKey
	 * @param propertyValue
	 */
	private void modifyProperty(EvictionStrategy evictionStrategy, String propertyKey, String propertyValue)
	{
		try
		{
			// get a list of any methods named like a setter, that have one parameter
			List<Method> candidateMethods = JavaBeanUtility.findSetterMethods(evictionStrategy.getClass(), propertyKey);
			
			if(candidateMethods == null || candidateMethods.size() == 0)
			{
				System.err.println("The property '" + propertyKey + "' has no accessible setter method.");
				return;
			}
			
			// order them by relevance to the property type that we have
			Collections.sort(candidateMethods, new MethodRelavanceComparator(propertyValue.getClass()));
			for(Method method : candidateMethods)
			{
				Class<?> parameterType = method.getParameterTypes()[0];
				// preferably we simply invoke the setter with the given value
				if( parameterType.isAssignableFrom(propertyValue.getClass()) )
				{
					method.invoke( evictionStrategy, propertyValue );
					System.out.println("The property '" + propertyKey + "' has been set to '" + propertyValue + "' using an assignable accessor.");
					break;
				}
				else
				{
					try
					{
						if(parameterType.isPrimitive())
						{
							Object typedParameter = createPrimitiveTypedParameter(parameterType, propertyValue);
							if(typedParameter != null)
							{
								method.invoke(evictionStrategy, typedParameter);
								System.out.println("The property '" + propertyKey + "' has been set to '" + propertyValue + "' using an primitive accessor.");
								break;
							}
						}
						else
						{
							Constructor<?> constructor = parameterType.getConstructor(propertyValue.getClass());
							Object typedParameter = constructor.newInstance(propertyValue);
							method.invoke(evictionStrategy, typedParameter);
							System.out.println("The property '" + propertyKey + "' has been set to '" + propertyValue + "' using a typed reference accessor.");
							break;
						}
					}
					catch(Exception x){}
				}
			}
		} 
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	/**
	 * @param parameterType
	 * @param propertyValue
	 * @return
	 */
	private Object createPrimitiveTypedParameter(Class<?> parameterType,
			String propertyValue)
	{
		try{ if( Long.TYPE == parameterType) return new Long(propertyValue); }
		catch(NumberFormatException nfX){return null;}
		try{ if( Integer.TYPE == parameterType) return new Integer(propertyValue); }
		catch(NumberFormatException nfX){return null;}
		try{ if( Short.TYPE == parameterType) return new Short(propertyValue); }
		catch(NumberFormatException nfX){return null;}
		try{ if( Byte.TYPE == parameterType) return new Byte(propertyValue); }
		catch(NumberFormatException nfX){return null;}
		try{ if( Double.TYPE == parameterType) return new Double(propertyValue); }
		catch(NumberFormatException nfX){return null;}
		try{ if( Float.TYPE == parameterType) return new Float(propertyValue); }
		catch(NumberFormatException nfX){return null;}
		
		return null;
	}

	/**
	 * A comparator that will sort by the closeness of its one
	 * parameter to a target type.
	 */
	private class MethodRelavanceComparator
	implements Comparator<Method>
	{
		private final Class<?> targetType;
		
		MethodRelavanceComparator(Class<?> targetType)
		{
			this.targetType = targetType;
		}
		
		@Override
		public int compare(Method method1, Method method2)
		{
			if(method1.getParameterTypes().length != 1 && method2.getParameterTypes().length != 1)
				return 0;
			if(method1.getParameterTypes().length != 1)
				return 1;
			if(method2.getParameterTypes().length != 1)
				return -1;
			
			Class<?> method1Type = method1.getParameterTypes()[0];
			Class<?> method2Type = method2.getParameterTypes()[0];
			
			// first choice is a method that takes the exact type
			if( method1Type.equals(targetType) )
				return -1;
			if( method2Type.equals(targetType) )
				return 1;
			
			// second choice is a method that takes a supertype
			if( method1Type.isAssignableFrom(targetType) )
				return -1;
			if( method2Type.isAssignableFrom(targetType) )
				return 1;

			// last choice is a method that takes something we can create with a constructor
			try
			{
				method1Type.getConstructor(targetType);
				return -1;
			} 
			catch (Exception x){}
			
			try
			{
				method2Type.getConstructor(targetType);
				return 1;
			} 
			catch (Exception x){}
			
			return 0;
		}
	}
}
