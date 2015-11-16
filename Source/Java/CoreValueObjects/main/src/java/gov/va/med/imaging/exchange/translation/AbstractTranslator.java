/**
 * 
 */
package gov.va.med.imaging.exchange.translation;

import gov.va.med.imaging.core.ObjectVocabulary;
import gov.va.med.imaging.exchange.translation.exceptions.MultipleTranslatorFoundException;
import gov.va.med.imaging.exchange.translation.exceptions.NoTranslatorFoundException;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public class AbstractTranslator
{
	private static final String TRANSLATE_METHOD_REGEX = "translate[A-Za-z0-9]*";
	private static Pattern TRANSLATE_METHOD_PATTERN;
	
	static
	{
		TRANSLATE_METHOD_PATTERN = Pattern.compile(TRANSLATE_METHOD_REGEX);
	}
	
	/**
	 * The list of available translator classes
	 */
	private static List<Class<? extends AbstractTranslator>> translatorClasses = 
		new ArrayList<Class<? extends AbstractTranslator>>();
	public static void registerTranslatorClass( Class<? extends AbstractTranslator> translator)
	{
		synchronized (translatorClasses)
		{
			if( !validateTranslator(translator) )
				return;
			
			translatorClasses.add(translator);
		}
	}
	
	public static void deRegisterTranslatorClass( Class<? extends AbstractTranslator> translator)
	{
		translatorClasses.remove(translator);
	}

	/**
	 * 
	 * @return
	 */
	public static Iterator<Class<? extends AbstractTranslator>> registeredTranslatorsIterator()
	{
		return Collections.unmodifiableList(translatorClasses).iterator();
	}
	
	/**
	 * validate that the translator does not contain any member methods that may be confused
	 * with methods in the currently known translators
	 * 
	 * @param translator
	 * @return
	 */
	private static boolean validateTranslator(Class<? extends AbstractTranslator> translator)
	{
		for(Method method : translator.getMethods())
		{
			Class<?> returnType = method.getReturnType();
			Class<?>[] parameterTypes = method.getParameterTypes();
			if(returnType != Void.class && returnType != void.class && parameterTypes != null && parameterTypes.length == 1)
			{
				try
				{
					findTranslationMethod(returnType, parameterTypes);
					return false;
				}
				catch (NoTranslatorFoundException ntX)
				{
					// this is what should happen
				}
				catch (MultipleTranslatorFoundException mtX)
				{
					getLogger().error(mtX);
					return false;
				}
				catch (TranslationException x)
				{
					getLogger().error(x);
					return false;
				}
			}
		}
		
		return true;
	}

	protected static Logger getLogger()
	{
		return Logger.getLogger(AbstractTranslator.class);		
	}
	
	/**
	 * By default, any method name that begins with "translate" will be considered
	 * when searching for a translation method. 
	 * @return
	 */
	protected static Pattern getMethodNamePattern()
	{
		return TRANSLATE_METHOD_PATTERN;
	}
	
	/**
	 * Look through the list of available translation classes for a method that will
	 * translate the source type to the destination type.
	 * The method MUST be a static method, taking one argument of type sourceClass and returning
	 * an instance of destinationClass.
	 * 
	 * @param sourceClass
	 * @param destinationClass
	 * @return
	 */
	public static Method findTranslationMethod(Class<?> destinationClass, Class<?>[] sourceClasses) 
	throws TranslationException
	{
		if(sourceClasses == null || sourceClasses.length == 0 || destinationClass == null)
			throw new NoTranslatorFoundException("Cannot translate to/from a null type.");
		
		List<Method> candidateMethods = new ArrayList<Method>();
		
		for(Class<?> translatorClass : translatorClasses)
		{
			int classModifiers = translatorClass.getModifiers();
			if( Modifier.isPublic(classModifiers) && !Modifier.isInterface(classModifiers) )
			{
				try
				{
					for(Method method : translatorClass.getDeclaredMethods())
					{
						Class<?>[] methodParameterTypes = method.getParameterTypes();
						if( isTranslatorMethod(method) && 
							isParameterListCompatible(methodParameterTypes, sourceClasses) )
						{
							Class<?> methodReturnType = method.getReturnType();
							if( methodReturnType.isAssignableFrom(destinationClass) )
								candidateMethods.add(method);
						}
					}
					//Method translationMethod = translatorClass.getDeclaredMethod(TRANSLATE_METHOD_REGEX, sourceClasses);
					//Class<?> methodReturnType = translationMethod.getReturnType();
					//int modifiers = translationMethod.getModifiers();
					//if( Modifier.isPublic(modifiers) && 
					//	Modifier.isStatic(modifiers) && 
					//	methodReturnType.isAssignableFrom(destinationClass) )
					//		return translationMethod;
				}
				catch (SecurityException x)
				{
					Logger.getLogger(AbstractTranslator.class).warn(
						"While getting the " + TRANSLATE_METHOD_REGEX + " method reference from " + 
						translatorClass.getName() + " a SecurityException was thrown. " +
						"This may inocuous but the method is not available for translation and that intent should be validated" 
					);
				}
			}
		}
		
		if(candidateMethods.size() == 0)
			throw new NoTranslatorFoundException(sourceClasses, destinationClass);
		
		if(candidateMethods.size() > 1)
			throw new MultipleTranslatorFoundException(sourceClasses, destinationClass, candidateMethods);
		
		return candidateMethods.get(0);
	}

	/**
	 * Determines if a method meets the following requirements:
	 * 1.) is declared public
	 * 2.) is declared static
	 * 3.) has a return type other than void or Void
	 * 4.) has at least one parameter
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isTranslatorMethod(Method method)
	{
		String methodName = method.getName();
		Class<?>[] methodParameterTypes = method.getParameterTypes();
		if( getMethodNamePattern().matcher(methodName).matches() )
		{
			int modifiers = method.getModifiers();
			Class<?> returnType = method.getReturnType();
			Class<?>[] parameters = method.getParameterTypes();
			
			if( Modifier.isPublic(modifiers) && 
				Modifier.isStatic(modifiers) &&
				Void.class != returnType && void.class != returnType &&
				parameters.length > 0)
				return true;
		}
		
		return false;
	}
	
	/**
	 * For a Method to be a Business to Interface translator method it
	 * must:
	 * 1.) be a translator method (i.e. isTranslatorMethod(method) returns true)
	 * 2.) take one argument whose class is within a business package according to
	 *     the VIX semantics
	 * 3.) the result is a type that is not within the business packages according
	 *     to the VIX semantics
	 *     
	 * @param method
	 * @return
	 */
	public static boolean isBusinessToInterfaceMethod(Method method)
	{
		if(! isTranslatorMethod(method))
			return false;
		
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?> resultType = method.getReturnType();

		int businessObjectsInParameterTypes = 0;
		for(Class<?> parameterType : parameterTypes)
			if(ObjectVocabulary.isObjectClass(parameterType))
				businessObjectsInParameterTypes++;
		
		return businessObjectsInParameterTypes > 0 && 
			!ObjectVocabulary.isObjectClass(resultType);
	}
	
	/**
	 * For a Method to be a Interface to Business translator method it
	 * must:
	 * 1.) be a translator method (i.e. isTranslatorMethod(method) returns true)
	 * 2.) take one argument whose class is not within a business package according to
	 *     the VIX semantics
	 * 3.) the result is a type that is within the business packages according
	 *     to the VIX semantics
	 *     
	 * @param method
	 * @return
	 */
	public static boolean isInterfaceToBusinessMethod(Method method)
	{
		if(! isTranslatorMethod(method))
			return false;
		
		Class<?>[] parameters = method.getParameterTypes();
		if( parameters.length != 1 )
			return false;
		
		Class<?> resultClass = method.getReturnType();
		
		return !ObjectVocabulary.isObjectClass(parameters[0]) && 
			ObjectVocabulary.isObjectClass(resultClass);
	}
	
	/**
	 * Return true iff the actual parameter types are assignment compatible
	 * with the expected parameter types.
	 * 
	 * @param methodParameterTypes
	 * @param sourceClasses
	 * @return
	 */
	private static boolean isParameterListCompatible(Class<?>[] actual, Class<?>[] expected)
	{
		if( (actual == null || actual.length == 0) && (expected == null || expected.length == 0) )
			return false;
		if( (actual == null && expected != null) || (actual != null && expected == null) )
			return false;
		if(actual.length != expected.length)
			return false;
		
		for(int index=0; index < actual.length; ++index)
			if( ! actual[index].isAssignableFrom(expected[index]) )
				return false;
		
		return true;
	}

	/**
	 * @param filter
	 * @return
	 * @throws TranslationException 
	 */
	@SuppressWarnings("unchecked")
	public static <D extends Object> D translate(Class<D> destinationClass, Object... source) 
	throws TranslationException
	{
		if(source == null || source.length == 0)
		{
			System.out.println( "Requested to convert null to '" + destinationClass.getSimpleName().toString() + "'." );
			return null;
		}
		Class<?>[] sourceClasses = new Class<?>[source.length];
		for(int index=0; index < source.length; ++index)
		{
			if(source[index] == null)
			{
				System.out.println( "Null source[" + index + "] results in unknown typing, conversion to '" + destinationClass.getSimpleName().toString() + "' aborted." );
				return null;
			}
			sourceClasses[index] = source[index].getClass();
		}
		
		Method translationMethod = AbstractTranslator.findTranslationMethod(destinationClass, sourceClasses);
		
		try
		{
			//Class<?> translatorClass = translationMethod.getDeclaringClass();
			return (D)( translationMethod.invoke(null, source) );
		}
		catch(Exception x)
		{
			x.printStackTrace();
			throw new TranslationException(x);
		}
	}

	public static TranslationMethods getAllTranslationMethods()
	{
		TranslationMethods translatorMethods = new TranslationMethods();
		for( Iterator<Class<? extends AbstractTranslator>> translatorIter = AbstractTranslator.registeredTranslatorsIterator();
			translatorIter.hasNext(); )
		{
			Class<? extends AbstractTranslator> translatorClass = translatorIter.next();
			
			for(Method method : translatorClass.getDeclaredMethods())
				if( AbstractTranslator.isTranslatorMethod(method) )
					translatorMethods.add(method);
		}
		
		return translatorMethods;
	}
	
	public static class TranslationMethods 
	extends HashSet<Method>
	{
		private static final long serialVersionUID = -8971053036829027744L;

		/**
		 * Return an unmodifiable Set of the Method instances within this
		 * Set that meet the criteria for a Business to Interface Translator
		 * method.
		 * 
		 * @return
		 */
		public Set<Method> businessToInterfaceMethods()
		{
			Set<Method> b2iMethods = new HashSet<Method>();
			
			for(Method method : this)
				if(isBusinessToInterfaceMethod(method))
					b2iMethods.add(method);
			
			return Collections.unmodifiableSet( b2iMethods );
		}

		/**
		 * Return an unmodifiable Set of the Method instances within this
		 * Set that meet the criteria for a Interface to Business Translator
		 * method.
		 * 
		 * @return
		 */
		public Set<Method> interfaceToBusinessMethods()
		{
			Set<Method> i2bMethods = new HashSet<Method>();
			
			for(Method method : this)
				if(isInterfaceToBusinessMethod(method))
					i2bMethods.add(method);
			
			return Collections.unmodifiableSet( i2bMethods );
		}
	}
}
