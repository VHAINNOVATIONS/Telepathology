/**
 * 
 */
package gov.va.med;

import gov.va.med.imaging.exceptions.URNFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 * 
 * ========================================================================================================================
 * create() methods can be divided along three dimensions:
 * 1.) Whether the stringified URN is in URNComponents form or String form
 * 2.) Whether the create should handle casting
 * 3.) Whether the serialization format is assumed or explicitly stated
 * 
 * The organization of the create() methods works like this:
 * 1.) A create() taking stringified URNS, parses the URN and then delegates to a create() method
 *     with the same argument list, except URNComponents is substituted for the String
 * 2.) A create() method that specifies an expected type will delegate to a create() method with 
 *     the same argument list, except the expected type, then will dynamically cast the result and return
 * 3.) A create() method that does not take a serialization format will delegate to a create() method
 *     with the same arguments, except with the SERIALIZATION_FORMAT parameter added.
 *     
 * All registered URN classes (the ones that this class creates instances of) must:
 * 1.) implement a public static create(URNComponents, SERIALIZATION_FORMAT) method
 * 2.) implement a public toString(SERIALIZATION_FORMAT) method
 * 3.) implement a toString() method equivalent to toString(SERIALIZATION_FORMAT.RFC2141)
 * 4.) the create(URNComponents, SERIALIZATION_FORMAT) and the toString(SERIALIZATION_FORMAT) must be implemented such that:
 *     URNFactory.create( urn.toString(s), s ).equals(urn)
 *     where urn is of any registered URN type
 *     and s is any value of type SERIALIZATION_FORMAT
 */
public class URNFactory
{
	private static transient Logger logger = Logger.getLogger(URNFactory.class);

	// a URN class must implement: 
	// public static <T extends URN> T create(URNComponents, SERIALIZATION_FORMAT)
	// public static String getManagedNamespace();
	protected static final String FACTORY_METHOD_NAME = "create";
	private static final String TOSTRING_METHOD_NAME = "toString";
	
	private static final Class<?>[] FACTORY_METHOD_PARAMETERS = new Class[]{URNComponents.class, SERIALIZATION_FORMAT.class};
	private static final Class<?>[] TOSTRING_METHOD_PARAMETERS = new Class[]{SERIALIZATION_FORMAT.class};

	protected static Map<NamespaceIdentifier, Class<? extends URN>> urnClasses = 
		Collections.synchronizedMap(new HashMap<NamespaceIdentifier, Class<? extends URN>>());

	// Register all of the urn derived classes that we know of.
	// Registered classes can be created by this class based on 
	// their namespace identifiers.
	static
	{
		// JMW 6/30/2011 P122 - now load the URN classes using a provider pattern.
		// This allows URNs to be included/excluded from VISA implementations but they can be
		// registered with this single URNFactory
		ServiceLoader<URNProvider> serviceLoader = ServiceLoader.load(URNProvider.class);
		
		for(URNProvider urnProvider : serviceLoader)
		{
			logger.info("Registering URNs from URN Provider '" + urnProvider.getClass().getSimpleName() + "'.");
			Class<? extends URN> [] urnClasses = urnProvider.getUrnClasses();
			for(Class<? extends URN> urnClass : urnClasses)
			{
				registerUrnClass(urnClass);
			}
		}
		
		/*
		registerUrnClass(StudyURN.class);
		registerUrnClass(DocumentSetURN.class);
		registerUrnClass(ImageURN.class);
		registerUrnClass(DocumentURN.class);
		registerUrnClass(BhieImageURN.class);
		registerUrnClass(BhieStudyURN.class);
		registerUrnClass(PatientArtifactIdentifierImpl.class);
		registerUrnClass(GlobalArtifactIdentifierImpl.class);
		*/
		//registerUrnClass(ImageAnnotationURN.class);
	}

	/**
	 * Register a class as a URN class, this makes the class available to
	 * the create() method of this class so that it can make instances of the 
	 * registered class.
	 * 
	 * @param urnClass
	 */
	protected static void registerUrnClass(Class<? extends URN> urnClass)
	{
		URNType urnType = urnClass.getAnnotation(URNType.class);
		NamespaceIdentifier namespace = null;
		boolean requiredFactoryExists = false;
		boolean requiredToStringExists = false;
		
		if(urnType == null)
			Logger.getLogger(URN.class).error(
				"Unable to register URN class '" + urnClass.getName() + "' because: \n" +
				"the required annotation 'URNType' did not exist."
			);
		else
			namespace = new NamespaceIdentifier(urnType.namespace());
		
		try
		{
			// if the static create does not exist then the class is not a valid URN
			Method factory = urnClass.getMethod("create", FACTORY_METHOD_PARAMETERS);
			if( Modifier.isStatic(factory.getModifiers()) & Modifier.isPublic(factory.getModifiers()) )
				requiredFactoryExists = true;
			else
				Logger.getLogger(URN.class).error("The factory method static public create(URNComponents) does not exist for class '" + urnClass.getName() + "'.");
		}
		catch (Exception x)
		{
			Logger.getLogger(URN.class).error(
				"Unable to register URN class '" + urnClass.getName() + "' because: \n" +
				"1.) the factory method 'public " + urnClass.getSimpleName() + " create (URNComponents, SERIALIZATION_FORMAT)' does not exist. \n" +
				"[" + x.getMessage() + "]"
			);
		}

		try
		{
			// if the toString(SERIALIZATION_FORMAT) does not exist then the class is not a valid URN
			Method toString = urnClass.getMethod(TOSTRING_METHOD_NAME, TOSTRING_METHOD_PARAMETERS);
			if( !Modifier.isStatic(toString.getModifiers()) && Modifier.isPublic(toString.getModifiers()) )
				requiredToStringExists = true;
			else
				Logger.getLogger(URN.class).error("The method public toString(SERIALIZATION_FORMAT) does not exist for class '" + urnClass.getName() + "'.");
		}
		catch (Exception x)
		{
			Logger.getLogger(URN.class).error(
				"Unable to register URN class '" + urnClass.getName() + "' because: \n" +
				"1.) the factory method 'public " + urnClass.getSimpleName() + " create (URNComponents, SERIALIZATION_FORMAT)' does not exist. \n" +
				"[" + x.getMessage() + "]"
			);
		}
		
		if(namespace != null && requiredFactoryExists && requiredToStringExists)
		{
			urnClasses.put(namespace, urnClass);
			Logger.getLogger(URN.class).info("URN class '" + urnClass.getName() + "' is now registered with URNFactory with the namespace '" + namespace + "'." );
		}
	}
	
	// ========================================================================================================================
	// create() methods
	// These can be divided along three dimensions:
	// 1.) Whether the stringified URN is in URNComponents form or String form
	// 2.) Whether the create should handle casting
	// 3.) Whether the serialization format is assumed or explicitly stated
	// 
	// ========================================================================================================================
	
	/**
	 * Create an instance of a URN, validating that it is of the expected type.
	 * This version of create will not throw a ClassCastException if the type
	 * of URN created is not that expected, instead it will throw a URNFormatException
	 * wrapped around the ClassCastException.
	 * 
	 * @param <T>
	 * @param urnAsString
	 * @param expectedClass
	 * @return
	 * @throws URNFormatException
	 */
	public static <T extends URN> T create(String urnAsString, Class<T> expectedClass) 
	throws URNFormatException
	{
		URN urn = create(urnAsString);
		
		try
		{
			return expectedClass.cast(urn);
		}
		catch (ClassCastException ccX)
		{
			throw new URNFormatException(
				"ClassCastException caught, unable to cast " + urnAsString + "' to '" + expectedClass.getSimpleName() + "'.", 
				ccX);
		}
	}

	/**
	 * ALL of the variations on the create() method taking the URN as a String MUST eventually 
	 * call this create() method to construct the URNs.
	 * 
	 * @param <T>
	 * @param urnAsString
	 * @param base32Decode
	 * @return
	 * @throws URNFormatException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends URN> T create(String urnAsString) 
	throws URNFormatException
	{
		logger.trace("Creating URN from string '" + urnAsString + "'.");
		URNComponents urnComponents = URNComponents.parse(urnAsString);
		//urnComponents = URN.escapeIllegalCharacters(urnComponents);
		logger.trace("Creating URN from string '" + urnAsString + "' - parsed into URNComponents '" + urnComponents.toString() + "'.");
		return (T)create(urnComponents);
	}


	/**
	 * A factory method to create any kind of registered URN given the
	 * URN component parts.  This method should be used to replace calls to 
	 * class-specific create methods taking the same parameters.
	 * 
	 * @param namespaceIdentifier
	 * @param namespaceSpecificString
	 * @param additionalIdentifiers
	 * @return
	 * @throws URNFormatException
	 */
	@SuppressWarnings("unchecked")
	public <T extends URN> T create(
		NamespaceIdentifier namespaceIdentifier, 
		String namespaceSpecificString,
		Class<T> expectedClass)
	throws URNFormatException
	{
		URNComponents urnComponents = URNComponents.create(namespaceIdentifier, namespaceSpecificString);
		T urn = (T)create(urnComponents);
		
		try
		{
			return expectedClass.cast(urn);
		}
		catch (ClassCastException ccX)
		{
			throw new URNFormatException(
				"ClassCastException caught, unable to cast " + namespaceIdentifier + 
				":" + namespaceSpecificString + 
				"' to '" + expectedClass.getSimpleName() + "'.", 
				ccX);
		}
	}

	/**
	 * 
	 * @param <T>
	 * @param urnComponents
	 * @param base32Decode
	 * @return
	 * @throws URNFormatException
	 */
	@SuppressWarnings( "unchecked" )
	private static <T extends URN> T create(URNComponents urnComponents)
	throws URNFormatException
	{
		Class<? extends URN> registeredClass = getRegisteredUrnClass(urnComponents.getNamespaceIdentifier());
		logger.trace("Creating URN from string '" + urnComponents.toString() + "' - registered class is '" + 
			(registeredClass == null ? "null" : registeredClass.getSimpleName()) + "'.");
		
		if(registeredClass == null)
			return (T)( new URN(urnComponents, SERIALIZATION_FORMAT.NATIVE) );
		else
			return (T)create(registeredClass, FACTORY_METHOD_NAME, FACTORY_METHOD_PARAMETERS, new Object[]{urnComponents, SERIALIZATION_FORMAT.NATIVE});
	}
	
	@SuppressWarnings( "unchecked" )
	public static <T extends URN> T create(
		String urnAsString, 
		SERIALIZATION_FORMAT serializationFormat, 
		Class<T> expectedClass)
	throws URNFormatException
	{
		T urn = (T)create(urnAsString, serializationFormat);

		try
		{
			return expectedClass.cast(urn);
		}
		catch (ClassCastException ccX)
		{
			throw new URNFormatException(
				"ClassCastException caught, unable to cast URN created from " + urnAsString + 
				"' to '" + expectedClass.getSimpleName() + "'.", 
				ccX);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends URN> T create(
		String urnAsString, 
		SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		logger.trace("Creating URN from string '" + urnAsString + "'.");
		// if the serialization format is VFTP then have the URNComponents unescape before parsing the NSS
		URNComponents urnComponents = URNComponents.parse( urnAsString, serializationFormat );
		//urnComponents = URN.escapeIllegalCharacters(urnComponents);
		logger.trace("Creating URN from string '" + urnAsString + "' - parsed into URNComponents '" + urnComponents.toString() + "'.");
		T urn = (T)create(urnComponents, serializationFormat);

		return urn;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends URN> T create(
		URNComponents urnComponents, 
		SERIALIZATION_FORMAT serializationFormat, 
		Class<T> expectedClass)
	throws URNFormatException
	{
		T urn = (T)create(urnComponents, serializationFormat);

		try
		{
			return expectedClass.cast(urn);
		}
		catch (ClassCastException ccX)
		{
			throw new URNFormatException(
				"ClassCastException caught, unable to cast URN created from " + urnComponents + 
				"' to '" + expectedClass.getSimpleName() + "'.", 
				ccX);
		}
	}
	
	/**
	 * @param <T>
	 * @param urnComponents
	 * @param serializationFormat
	 * @return
	 * @throws URNFormatException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends URN> T create(
		URNComponents urnComponents, 
		SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		Class<? extends URN> registeredClass = getRegisteredUrnClass(urnComponents.getNamespaceIdentifier());
		logger.trace("Creating URN from string '" + urnComponents.toString() + "' - registered class is '" + 
			(registeredClass == null ? "null" : registeredClass.getSimpleName()) + "'.");
		
		if(registeredClass == null)
			return (T)( new URN(urnComponents, serializationFormat) );
		else
			return (T)create(registeredClass, FACTORY_METHOD_NAME, FACTORY_METHOD_PARAMETERS, new Object[]{urnComponents, serializationFormat});
	}
	
	/**
	 * Generic "create" that all other creates call.
	 * Tries to create an instance of a URN by first finding and calling a create()
	 * factory method and then a constructor if the factory method does not exist.
	 * The parameter types and parameters are the same for both the factory method
	 * and for the constructor.
	 * 
	 * @param <T>
	 * @return
	 * @throws URNFormatException 
	 */
	@SuppressWarnings( "unchecked" )
	protected static <T extends URN> T create(
		Class<? extends URN> registeredClass,
		String createMethodName, 
		Class<?>[] parameterTypes,
		Object[] parameters) 
	throws URNFormatException
	{
		assert(parameterTypes.length == parameters.length);
		
		// build a string used to identify the URN we are creating for logging
		String msgIdentifier = buildMessageIdentifier(parameterTypes, parameters);
		
		try
		{
			// Try to find and use a create method in the registered class,
			// if none is available then use a constructor directly.
			// This construct allows registered classes to create instances
			// of derived classes rather than creating the registered class.
			try
			{
				Method factoryMethod = registeredClass.getDeclaredMethod(createMethodName, parameterTypes);
				T urn = (T)( factoryMethod.invoke(null, parameters) );
				logger.trace("Creating URN from parameters " + msgIdentifier + ", created '" + urn.toString() + "' using factory method.");
				return urn;
			}
			catch (NoSuchMethodException x)
			{
				// no create method, call the constructor directly
				Constructor<? extends URN> urnConstructor = registeredClass.getConstructor(parameterTypes);
				T urn = (T)( urnConstructor.newInstance(parameters) );
				logger.trace("Creating URN from parameters " + msgIdentifier + ", created '" + urn.toString() + "' using constructor.");
				return urn;
			}
		}
		catch (ClassCastException x)
		{
			String msg = "Unable to create URN of class '" + registeredClass.getName() + 
				"' casting to that type failed.  URN namespace mapping may be incorrect.  See URN class code.";
			logger.trace("Creating URN from parameters " + msgIdentifier + ", error '" + msg + "'.");
			throw new URNFormatException(msg);
		}
		catch (NoSuchMethodException x)
		{
			String msg = "Registered URN class '" + registeredClass.getName() + 
				"' does not implement a constructor in the form '" + registeredClass.getSimpleName() + 
				"(" + createClassnameString(parameterTypes) + ")', which is required.";
			logger.trace("Creating URN from parameters " + msgIdentifier + ", error '" + msg + "'.");
			throw new URNFormatException(msg);
		}
		catch (SecurityException sX)
		{
			String msg = 
				"Registered URN class '" + registeredClass.getName() + 
				"' implements a constructor in the form '" + registeredClass.getSimpleName() + 
				"(" + createClassnameString(parameterTypes) + ")', but it is inaccessible.";
			logger.trace("Creating URN from string '" + parameters.toString() + "', error '" + msg + "'.");
			throw new URNFormatException(msg, sX);
		}
		catch (InvocationTargetException itX)
		{
			String msg = 
				"Registered URN class '" + registeredClass.getName() + 
				"' implements a constructor in the form '" + registeredClass.getSimpleName() + 
				"(" + createClassnameString(parameterTypes) + "[" + 
				parameters.toString() + "])', but invoking it failed.";
			logger.trace("Creating URN from parameters " + msgIdentifier + ", error '" + msg + "'.");
			if(itX.getCause() instanceof URNFormatException)
				throw (URNFormatException)itX.getCause();
			else
				throw new URNFormatException(msg, itX );
		}
		catch (Exception x)
		{
			String msg = 
				"Registered URN class '" + registeredClass.getName() + 
				"' implements a constructor in the correct form '" + registeredClass.getSimpleName() + 
				"(" + URNComponents.class.getSimpleName() + "[" + 
				parameters.toString() + "])', but invoking it failed.";
			logger.trace("Creating URN from parameters " + msgIdentifier + ", error '" + msg + "'.");
			throw new URNFormatException(msg, x);
		}
		
	}

	/**
	 * @param parameterTypes
	 * @return
	 */
	private static String createClassnameString(Class<?>[] parameterTypes)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; parameterTypes != null && i<parameterTypes.length; ++i)
			sb.append( i>0 ? "," + parameterTypes[i].getSimpleName() : parameterTypes[i].getSimpleName() );
		
		return sb.toString();
	}

	/**
	 * Build a string to use in error reporting
	 * 
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 */
	private static String buildMessageIdentifier(Class<?>[] parameterTypes, Object[] parameters)
	{
		// build a string to use in error reporting
		StringBuilder sbMsgIdentifier = new StringBuilder();
		for(int index=0; index < parameterTypes.length; ++index)
		{
			if(index != 0)
				sbMsgIdentifier.append(",");
			sbMsgIdentifier.append("[");
			sbMsgIdentifier.append(parameterTypes[index] == null ? "<null>" : parameterTypes[index].getSimpleName());
			sbMsgIdentifier.append("]='");
			sbMsgIdentifier.append(parameterTypes[index] == null ? "<null>" : parameterTypes[index].toString());
			sbMsgIdentifier.append("]");
		}
		return sbMsgIdentifier.toString();
	}
	
//	public static <T extends URN> T create(URI urnAsURI, Class<T> expectedClass) 
//	throws URNFormatException
//	{
//		return create(urnAsURI.toString(), expectedClass);
//	}

	/**
	 * Returns true if the class is registered as a URN class with the URNFactory.
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isRegisteredUrnClass(Class<? extends URN> clazz)
	{
		return urnClasses.containsValue(clazz);
	}
	
	/**
	 * 
	 * @param namespaceIdentifier
	 * @return
	 */
	private static Class<? extends URN> getRegisteredUrnClass(NamespaceIdentifier namespaceIdentifier)
	{
		if(namespaceIdentifier == null)
			return null;
		
		return urnClasses.get(namespaceIdentifier);
	}
}
