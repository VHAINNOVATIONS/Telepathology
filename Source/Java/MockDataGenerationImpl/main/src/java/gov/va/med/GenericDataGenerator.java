/**
 * 
 */
package gov.va.med;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 1, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
 * 
 * @author vhaiswbeckec
 *
 */
public class GenericDataGenerator
implements Observer
{
	public static String DEFAULT_STRING_VALUE = Messages.getString("GenericDataGenerator.DefaultStringValue"); //$NON-NLS-1$
	public static String DEFAULT_BOOLEAN_VALUE = Messages.getString("GenericDataGenerator.DefaultBooleanValue"); //$NON-NLS-1$
	public static String DEFAULT_CHAR_VALUE = Messages.getString("GenericDataGenerator.DefaultCharValue"); //$NON-NLS-1$
	public static String DEFAULT_BYTE_VALUE = Messages.getString("GenericDataGenerator.DefaultByteValue"); //$NON-NLS-1$
	public static String DEFAULT_SHORT_VALUE = Messages.getString("GenericDataGenerator.DefaultShortValue"); //$NON-NLS-1$
	public static String DEFAULT_INT_VALUE = Messages.getString("GenericDataGenerator.DefaultIntValue"); //$NON-NLS-1$
	public static String DEFAULT_LONG_VALUE = Messages.getString("GenericDataGenerator.DefaultLOngValue"); //$NON-NLS-1$
	public static String DEFAULT_FLOAT_VALUE = Messages.getString("GenericDataGenerator.DefaultFloatValue"); //$NON-NLS-1$
	public static String DEFAULT_DOUBLE_VALUE = Messages.getString("GenericDataGenerator.DefaultDoubleValue"); //$NON-NLS-1$
	public static Date DEFAULT_DATE_VALUE = new Date();

	// a regex used to generate String values where no pattern is specified
	public static String DEFAULT_STRING_PATTERN = "[A-Z][a-z]{2,8}"; //$NON-NLS-1$
	
	public DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$
	public DateFormat DEFAULT_DATETIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss"); //$NON-NLS-1$
	
	private final static String RANDOM_STRING_CHARACTER_CLASS = "[A-Za-z0-9]"; //$NON-NLS-1$
	private final static String STRING_MAXIMUM_REPEATER = "{32}"; //$NON-NLS-1$
	private final static String STRING_MINIMUM_REPEATER = "{3}"; //$NON-NLS-1$
	private final static String STRING_RANDOM_REPEATER = "{3,32}"; //$NON-NLS-1$
	
	private final static String RANDOM_CHAR_PATTERN = "[a-zA-Z0-9_!@#$%^&*()+]"; //$NON-NLS-1$
	private final static String RANDOM_BYTE_PATTERN = "[+-][0-9]{1,2}"; //$NON-NLS-1$
	private final static String RANDOM_SHORT_PATTERN = "[+-][0-9]{1,4}"; //$NON-NLS-1$
	private final static String RANDOM_INT_PATTERN = "[+-][0-9]{1,8}"; //$NON-NLS-1$
	private final static String RANDOM_LONG_PATTERN = "[+-][0-9]{1,16}"; //$NON-NLS-1$
	private final static String RANDOM_FLOAT_PATTERN = "[+-][0-9]{1,8}.[0-9]{1,8}"; //$NON-NLS-1$
	private final static String RANDOM_DOUBLE_PATTERN = "[+-][0-9]{1,16}.[0-9]{1,16}"; //$NON-NLS-1$
	
	private final static String RANDOM_PHONE_PATTERN = "[1-9][0-9]{2}-[0-9]{2}-[0-9]{4}"; //$NON-NLS-1$
	private final static String RANDOM_NAME_PATTERN = "[A-Z][a-z]{3,9} [A-Z] [A-Z][a-z]{3,12}"; //$NON-NLS-1$
	private final static String RANDOM_SURNAME_PATTERN = "[[A-Z][a-z]{3,12}"; //$NON-NLS-1$
	private final static String RANDOM_GIVENNAME_PATTERN = "[A-Z][a-z]{3,9}"; //$NON-NLS-1$
	private final static String RANDOM_SSN_PATTERN = "[1-9][0-9]{2}-[0-9]{2}-[0-9]{4}"; //$NON-NLS-1$

	private final static String RANDOM_STREETADDRESS_PATTERN = "[1-9][0-9]{1,4} [A-Z][a-z]{3,14} [A-Z][a-z]"; //$NON-NLS-1$
	private final static String RANDOM_APARTMENT_PATTERN = "[A-Z][a-z]{1-3} [1-9][0-9]{1,4}"; //$NON-NLS-1$
	private final static String RANDOM_CITY_PATTERN = "[A-Z][a-z]{1-14}"; //$NON-NLS-1$
	private final static String RANDOM_STATE_PATTERN = "[A-Z][a-z]"; //$NON-NLS-1$
	private final static String RANDOM_ZIP_PATTERN = "[1-9][0-9]{4}"; //$NON-NLS-1$
	private final static String RANDOM_ZIP4_PATTERN = "[1-9][0-9]{3}"; //$NON-NLS-1$

	/**
	 * 
	 */
	public enum Mode
	{
		DEFAULT, 		// use default values
		RANDOMIZE, 		// use randomly generated values
		MINIMUM, 		// use the minimum permissible value
		MAXIMUM			// use the maximum permissible value
	}
	
	// The discriminators for what constitutes a create method in this class
	public static final String CREATE_METHOD_REGEX = "(create)([A-Z][A-Za-z0-9_]*)?"; //$NON-NLS-1$
	public static final Pattern CREATE_METHOD_PATTERN = Pattern.compile(CREATE_METHOD_REGEX);
	public static final Class<?>[] COMPLEX_CREATE_METHOD_PARAMETERS = new Class<?>[]
	{
		InstancePopulation.class, AggregationPopulation.class, CompositionPopulation.class, ReferenceMap.class
	};
	public static final Class<?>[] REFERENCE_CREATE_METHOD_PARAMETERS = new Class<?>[]
	{
		ReferenceMap.class
	};
	public static final Class<?>[] SIMPLE_CREATE_METHOD_PARAMETERS = new Class<?>[]
 	{
 	};
	public static final String POSTPOPULATE_METHOD_REGEX = "(postPopulate)([A-Z][A-Za-z0-9_]*)?"; //$NON-NLS-1$
	public static final Pattern POSTPOPULATE_METHOD_PATTERN = Pattern.compile(POSTPOPULATE_METHOD_REGEX);

	// ====================================================================================
	//
	// ====================================================================================
	private List<GenericDataGenerator> registeredDataGenerators = 
		Collections.synchronizedList( new ArrayList<GenericDataGenerator>() );
	
	public void registerDataGenerator(GenericDataGenerator dataGenerator)
	{
		registeredDataGenerators.add(dataGenerator);
	}
	public void deRegisterDataGenerator(GenericDataGenerator dataGenerator)
	{
		registeredDataGenerators.remove(dataGenerator);
	}
	
	public List<GenericDataGenerator> getRegisteredDataGenerator()
	{
		return Collections.unmodifiableList(registeredDataGenerators);
	}
	
	// ====================================================================================
	//
	// ====================================================================================
	private final DataGenerationConfiguration configuration;
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public GenericDataGenerator()
	{
		this( new DataGenerationConfiguration(Mode.RANDOMIZE, NetworkSimulatingInputStream.EXCEPTION_MODE.RELIABLE, NetworkSimulatingInputStream.DELAY_MODE.NONE) );
	}
	
	public GenericDataGenerator(Mode mode)
	{
		this( new DataGenerationConfiguration(mode, NetworkSimulatingInputStream.EXCEPTION_MODE.RELIABLE, NetworkSimulatingInputStream.DELAY_MODE.NONE) );
	}
	
	public GenericDataGenerator(DataGenerationConfiguration configuration)
	{
		this.configuration = configuration;
		configuration.addObserver(this);	// add ourselves as an observer of our configuration
		registerDataGenerator(this);		// always register ourselves
	}
	
	// This method gets called to notify us that the configuration has changed.
	// As of now (4May2010) this class does not keep any local copies of configuration
	// and does not need to be sensitive to changes.  If that changes though 
	@Override
	public void update(Observable o, Object arg)
	{
		if(o == this.configuration)
		{
			ObservableMap.Event event = (ObservableMap.Event)arg;
			//event.getType();
			//event.getOldValue();
			//event.getNewValue();
		}
	}
	
	protected DataGenerationConfiguration getConfiguration()
	{
		return this.configuration;
	}
	
	protected boolean isRandomize()
	{
		return Mode.RANDOMIZE == getConfiguration().getMode();
	}
	
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * Create some list of bytes we can use for data
	 * the data should be re-used if tests must be consistent.
	 * 
	 * @param testDataLength
	 * @return
	 */
	public byte[] createRandomByteArray(int testDataLength)
	{
		byte[] sampleData = new byte[testDataLength];
		for(int index=0; index<testDataLength; ++index)
			sampleData[index] = (byte)(index % 256);
		
		return sampleData;
	}

	/**
	 * Create an instance of an object of the class given by resultClass, 
	 * populating it according to the instance and child population enums.
	 * 
	 * @param <T>
	 * @param resultClass
	 * @param instancePopulation
	 * @param children
	 * @param optionalParameters
	 * @return an instance of class resultClass or null if unable to create an instance
	 */
	public <T extends Object> T createInstance(
		Class<T> resultClass, 
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates,
		CompositionPopulation components,
		Object ... optionalParameters )
	{
		return createInstance(
			resultClass, 
			instancePopulation, aggregates, components,
			ReferenceMap.createRoot(),
			optionalParameters);
	}
	
	/**
	 * Create an instance of the requested type with no context (referential
	 * integrity) and no population of children or composition elements.
	 * This method requires that a simple createXXX method exists for the 
	 * requested type.
	 * 
	 * @param <T>
	 * @param resultClass
	 * @return
	 */
	public <T extends Object> T createInstance( Class<T> resultClass )
	{
		return createInstance(
			resultClass, 
			InstancePopulation.FULL, 
			AggregationPopulation.MANY, 
			CompositionPopulation.MANY, 
			ReferenceMap.createRoot(), 
			(Object [])null);
	}
	
	/**
	 * Create an instance of the requested collection type with no context (referential
	 * integrity) and no population of children or composition elements.
	 * This method requires that a simple createXXX method exists for the 
	 * requested type.
	 * 
	 * @param <T>
	 * @param <E>
	 * @param collectionType
	 * @param elementType
	 * @return
	 */
	public <T extends Collection<E>, E extends Object> T createCollectionInstance(
		Class<T> collectionType, 
		Class<E> elementType)
	{
		return createCollectionInstance(
			collectionType, 
			elementType, 
			InstancePopulation.FULL, 
			AggregationPopulation.MANY, 
			CompositionPopulation.MANY, 
			ReferenceMap.createRoot(), 
			(Object [])null
		);
	}
	
	public <T extends Map<K, V>, K extends Object, V extends Object> T createMapInstance(
		Class<T> mapType,
		Class<K> keyType, 
		Class<V> valueType)
	{
		return createMapInstance(
			mapType, 
			keyType, 
			valueType, 
			InstancePopulation.FULL, 
			AggregationPopulation.MANY, 
			CompositionPopulation.MANY, 
			ReferenceMap.createRoot(), 
			(Object [])null
		);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param resultClass
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @param references
	 * @param optionalParameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T createInstance(
		Class<T> resultClass, 
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates,
		CompositionPopulation components,
		ReferenceMap references,
		Object ... optionalParameters )
	{
		getLogger().info("createInstance(" + resultClass.getName() +  //$NON-NLS-1$
			", " + (instancePopulation == null ? "null instances" : instancePopulation.toString()) +  //$NON-NLS-1$ //$NON-NLS-2$
			", " + (aggregates == null ? "null aggregates" : aggregates.toString()) + //$NON-NLS-1$ //$NON-NLS-2$
			", " + (components == null ? "null components" : components.toString()) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		if( isSimpleType(resultClass) )
			return (T)createSimpleDataType(resultClass);
		
		if( resultClass.isEnum() )
			return (T)selectEnum( (Class<Enum>)resultClass );
		
		Method complexCreateMethod = getComplexObjectCreateMethod(resultClass, optionalParameters);
		Method referenceCreateMethod = getReferenceObjectCreateMethod(resultClass, optionalParameters);
		Method simpleCreateMethod = getSimpleObjectCreateMethod(resultClass, optionalParameters);
		Method postPopulateMethod = getPostPopulateMethod(resultClass);
		if(complexCreateMethod != null && 
			instancePopulation != null &&
			aggregates != null &&
			components != null &&
			references != null)
		{
			getLogger().info("found method '" + complexCreateMethod.toGenericString() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
			try
			{
				ReferenceMap childReferences = references.createChild();
				Object[] parameters = combineParameters(instancePopulation, aggregates, components, childReferences, optionalParameters);
				GenericDataGenerator registeredInstance = getRegisteredInstance(complexCreateMethod.getDeclaringClass());
				T instance = (T)complexCreateMethod.invoke(registeredInstance, parameters);
				getLogger().info("created instance using '" + complexCreateMethod.toGenericString() + "'."); //$NON-NLS-1$ //$NON-NLS-2$

				if(components != CompositionPopulation.NONE)
					populateComposition(instance, instancePopulation, aggregates, components, references);
				if(instancePopulation == InstancePopulation.FULL)
					populateChildren(instance, instancePopulation, aggregates, components, references);
				//populateAnnotatedFields(instance, instancePopulation, aggregates, components, references);
				
				if(postPopulateMethod != null)
					postPopulateMethod.invoke(registeredInstance, instance);
				
				return instance;
			}
			catch (Exception x)
			{
				getLogger().error(x);
				x.printStackTrace();
			}
		}
		else if(referenceCreateMethod != null && references != null)
		{
			getLogger().info("found method '" + referenceCreateMethod.toGenericString() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
			try
			{
				ReferenceMap childReferences = references.createChild();
				Object[] parameters = combineParameters(childReferences, optionalParameters);
				GenericDataGenerator registeredInstance = getRegisteredInstance(complexCreateMethod.getDeclaringClass());
				T instance = (T)referenceCreateMethod.invoke(registeredInstance, parameters);
				getLogger().info("created instance using '" + referenceCreateMethod.toGenericString() + "'."); //$NON-NLS-1$ //$NON-NLS-2$

				return instance;
			}
			catch (Exception x)
			{
				getLogger().error(x);
			}
		}
		else if(simpleCreateMethod != null)
		{
			getLogger().info("found method '" + simpleCreateMethod.toGenericString() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
			try
			{
				GenericDataGenerator registeredInstance = getRegisteredInstance(simpleCreateMethod.getDeclaringClass());
				T instance = (T)simpleCreateMethod.invoke(registeredInstance, optionalParameters);
				getLogger().info("created instance using '" + simpleCreateMethod.toGenericString() + "'."); //$NON-NLS-1$ //$NON-NLS-2$

				return instance;
			}
			catch (Exception x)
			{
				getLogger().error(x);
				x.printStackTrace();
			}
		}
		else
		{
			getLogger().warn("No create method found, creating instance of '" + resultClass.getSimpleName() + "' using default constructor." );
			try
			{
				T instance = resultClass.newInstance();
				getLogger().warn("Created instance of '" + resultClass.getSimpleName() + "' using default constructor." );
				populateChildren(instance, instancePopulation, aggregates, components, references);
				return instance;
			}
			catch (Throwable x)
			{
				getLogger().error("Unable to create instance of '" + resultClass.getSimpleName() + "' using default constructor.", x);
				while((x = x.getCause()) != null)
					getLogger().error("ROOT CAUSE of inability to create instance of '" + resultClass.getSimpleName() + "' using default constructor follows.", x);
				x.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param <T>
	 * @param <E>
	 * @param collectionType
	 * @param elementType
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @param references
	 * @param optionalParameters
	 * @return
	 */
	public <T extends Collection<E>, E extends Object> T createCollectionInstance(
		Class<T> collectionType, 
		Class<E> elementType,
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates,
		CompositionPopulation components,
		ReferenceMap references,
		Object ... optionalParameters )
	{
		Collection<E> collection = null;
		try
		{
			if(collectionType.isInterface())
			{
				if(Set.class.isAssignableFrom(collectionType))
				{
					if( SortedSet.class.isAssignableFrom(collectionType) && collectionType.isAssignableFrom(TreeSet.class) )
						collection = new TreeSet<E>();
					else if( HashSet.class.isAssignableFrom(collectionType) )
						collection = new HashSet<E>();
				}
				else if( List.class.isAssignableFrom(collectionType) && collectionType.isAssignableFrom(ArrayList.class) )
					collection = new ArrayList<E>();
				else if( Queue.class.isAssignableFrom(collectionType) && collectionType.isAssignableFrom(LinkedList.class) )
					collection = new LinkedList<E>();
			}
			else
				collection = collectionType.newInstance();
			
			if(collection == null)
			{
				getLogger().error("Unable to create collection of type " + collectionType.getName() + "."); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			}
			
			for(int elementCount=0; elementCount<components.getCount(); ++elementCount)
				collection.add( createInstance(elementType, instancePopulation, aggregates, components, references, optionalParameters) );
			
			return (T)collection;
		}
		catch (Exception x)
		{
			getLogger().error(x);
			x.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param <T>	A Map realization
	 * @param <K>	The key type of the Map
	 * @param <V>	The value type of the Map
	 * @param keyType
	 * @param valueType
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @param references
	 * @param optionalParameters
	 * @return
	 */
	public <T extends Map<K,V>, K extends Object,V extends Object> T createMapInstance(
		Class<T> mapType,
		Class<K> keyType, 
		Class<V> valueType,
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates,
		CompositionPopulation components,
		ReferenceMap references,
		Object ... optionalParameters )
	{
		Map<K,V> map = null;
		try
		{
			if( mapType.isInterface() && mapType.isAssignableFrom(TreeMap.class) )
				map = new TreeMap<K,V>();		// use a TreeMap because it implements Map, SortedMap and NavigableMap
			else
				map = mapType.newInstance();
			
			if(map == null)
			{
				getLogger().error("Unable to create map of type " + mapType.getName() + "."); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			}
			
			for(int elementCount=0; elementCount<components.getCount(); ++elementCount)
			{
				K key = (K)createInstance(keyType, instancePopulation, aggregates, components, references, optionalParameters);
				V value = (V)createInstance(valueType, instancePopulation, aggregates, components, references, optionalParameters);

				if(key == null)
					logger.warn("Failed to create key, instance of " + keyType.toString() + ".");
				if(value == null)
					logger.warn("Failed to create value, instance of " + valueType.toString() + ".");
				
				map.put( key, value );
			}
			
			return (T)map;
		}
		catch (Exception x)
		{
			getLogger().error(x);
			x.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param declaringClass
	 * @return
	 */
	private GenericDataGenerator getRegisteredInstance(Class<?> declaringClass)
	{
		if(declaringClass == null)
			return null;
		
		for(GenericDataGenerator dataGenerator : getRegisteredDataGenerator())
			if(dataGenerator.getClass() == declaringClass)
				return dataGenerator;
		
		return null;
	}

	/**
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @param optionalParameters
	 * @return
	 */
	private static final int COMPLEX_CREATE_KNOWN_PARAMETER_COUNT = 4;
	
	private Object[] combineParameters(
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates,
		CompositionPopulation components, 
		ReferenceMap references,
		Object[] optionalParameters)
	{
		Object[] result = new Object[
			  COMPLEX_CREATE_KNOWN_PARAMETER_COUNT + 
			  (optionalParameters == null ? 0 : optionalParameters.length)
			];
		
		result[0] = instancePopulation;
		result[1] = aggregates;
		result[2] = components;
		result[3] = references;
		
		for(
		int index=COMPLEX_CREATE_KNOWN_PARAMETER_COUNT; 
		optionalParameters != null && optionalParameters.length > 0 && index <= COMPLEX_CREATE_KNOWN_PARAMETER_COUNT + optionalParameters.length; 
		++index)
			result[index] = optionalParameters[index - COMPLEX_CREATE_KNOWN_PARAMETER_COUNT];
		
		return result;
	}
	
	private static final int REFERENCE_CREATE_KNOWN_PARAMETER_COUNT = 1;
	private Object[] combineParameters(
		ReferenceMap references,
		Object[] optionalParameters)
	{
		Object[] result = new Object[REFERENCE_CREATE_KNOWN_PARAMETER_COUNT + (optionalParameters == null ? 0 : optionalParameters.length)];
		
		result[0] = references;
		
		for(
		int index=REFERENCE_CREATE_KNOWN_PARAMETER_COUNT; 
		optionalParameters != null && index <= optionalParameters.length + REFERENCE_CREATE_KNOWN_PARAMETER_COUNT; 
		++index)
			result[index] = optionalParameters[index - REFERENCE_CREATE_KNOWN_PARAMETER_COUNT];
		
		return result;
	}

	/**
	 * Find a method in this class that is:
	 * 1.) static
	 * 2.) named according to the regex "create[A-Z][A=Za-z0-9_]"
	 * 3.) public
	 * 4.) returns an instance of type resultClass
	 * 5.) has a parameter list equal to 
	 * 
	 * @param <T>
	 * @param resultClass
	 * @return
	 */
	protected <T extends Object> Method getComplexObjectCreateMethod(
		Class<T> resultClass, 
		Object ... optionalParameters)
	{
		if(resultClass == null)
			return null;
		
		Class<?>[] parameterList = combineParameterLists(COMPLEX_CREATE_METHOD_PARAMETERS, optionalParameters); 
		
		for(GenericDataGenerator dataGenerator : this.getRegisteredDataGenerator())
		{
			Class<? extends GenericDataGenerator> dataGeneratorClass = dataGenerator.getClass();
			
			for(Method method : dataGeneratorClass.getDeclaredMethods())
			{
				int methodModifiers = method.getModifiers();
				if( Modifier.isPublic(methodModifiers) && 
					method.getReturnType() == resultClass && 
					Pattern.matches(CREATE_METHOD_REGEX, method.getName()) &&
					parameterListEqual(parameterList, method.getParameterTypes()))
						return method;
			}
		}		
		return null;
	}

	/**
	 * Get a createXXX method that creates an object and takes a
	 * ReferenceMap parameter.
	 * 
	 * @param <T>
	 * @param resultClass
	 * @param optionalParameters
	 * @return
	 */
	protected <T extends Object> Method getReferenceObjectCreateMethod(
		Class<T> resultClass, 
		Object ... optionalParameters)
	{
		if(resultClass == null)
			return null;
		
		Class<?>[] parameterList = combineParameterLists(REFERENCE_CREATE_METHOD_PARAMETERS, optionalParameters); 
		
		for(GenericDataGenerator dataGenerator : this.getRegisteredDataGenerator())
		{
			Class<? extends GenericDataGenerator> dataGeneratorClass = dataGenerator.getClass();
			
			for(Method method : dataGeneratorClass.getDeclaredMethods())
			{
				int methodModifiers = method.getModifiers();
				if( Modifier.isPublic(methodModifiers) && 
					method.getReturnType() == resultClass && 
					Pattern.matches(CREATE_METHOD_REGEX, method.getName()) &&
					parameterListEqual(parameterList, method.getParameterTypes()))
						return method;
			}
		}
		
		return null;
	}
	
	/**
	 * Find a createXXX method that creates an object and takes no
	 * environment parameters.
	 * 
	 * @param <T>
	 * @param resultClass
	 * @param optionalParameters
	 * @return
	 */
	protected <T extends Object> Method getSimpleObjectCreateMethod(
		Class<T> resultClass, 
		Object ... optionalParameters)
	{
		if(resultClass == null)
			return null;
		
		Class<?>[] parameterList = combineParameterLists(SIMPLE_CREATE_METHOD_PARAMETERS, optionalParameters); 
		
		for(GenericDataGenerator dataGenerator : this.getRegisteredDataGenerator())
		{
			Class<? extends GenericDataGenerator> dataGeneratorClass = dataGenerator.getClass();
			
			for(Method method : dataGeneratorClass.getDeclaredMethods())
			{
				int methodModifiers = method.getModifiers();
				if( Modifier.isPublic(methodModifiers) && 
					method.getReturnType() == resultClass && 
					Pattern.matches(CREATE_METHOD_REGEX, method.getName()) &&
					parameterListEqual(parameterList, method.getParameterTypes()))
						return method;
			}
		}
		
		return null;
	}
	
	/**
	 * Find a method that will be called after an object is created
	 * but before the result is returned to the calling client. 
	 * 
	 * @param <T>
	 * @param resultClass
	 * @return
	 */
	private final <T extends Object> Method getPostPopulateMethod(Class<T> resultClass)
	{
		if(resultClass == null)
			return null;
		
		Class<?>[] parameterList = new Class<?>[]{resultClass};
		for(GenericDataGenerator dataGenerator : this.getRegisteredDataGenerator())
		{
			Class<? extends GenericDataGenerator> dataGeneratorClass = dataGenerator.getClass();
			
			for(Method method : dataGeneratorClass.getDeclaredMethods())
			{
				int methodModifiers = method.getModifiers();
				if( Modifier.isPublic(methodModifiers) && 
					(Void.class == method.getReturnType() || void.class == method.getReturnType()) && 
					Pattern.matches(POSTPOPULATE_METHOD_REGEX, method.getName()) &&
					parameterListEqual(parameterList, method.getParameterTypes()))
						return method;
			}
		}
		
		return null;
	}
	
	/**
	 * @param simpleCreateMethodParameters
	 * @param optionalParameters
	 * @return
	 */
	private Class<?>[] combineParameterLists(Class<?>[] createMethodParameters, Object[] optionalParameters)
	{
		Class<?>[] result = new Class[
		   (createMethodParameters == null ? 0 : createMethodParameters.length) +
		   (optionalParameters == null ? 0 : optionalParameters.length)
		];
		
		int resultIndex = 0;
		for(int index=0; createMethodParameters != null && index < createMethodParameters.length; ++index)
			result[resultIndex++] = createMethodParameters[index];
		
		for(int index=0; optionalParameters != null && index < optionalParameters.length; ++index)
			result[resultIndex++] = optionalParameters[index].getClass();
		
		return result;
	}

	/**
	 * @param createMethodParameters
	 * @param parameterTypes
	 * @return
	 */
	protected boolean parameterListEqual(Class<?>[] expectedParameterTypes, Class<?>[] actualParameterTypes)
	{
		if( expectedParameterTypes == null && actualParameterTypes == null )
			return true;
		
		if( (expectedParameterTypes == null && actualParameterTypes != null) || 
			(expectedParameterTypes != null && actualParameterTypes == null))
				return false;
		
		if(expectedParameterTypes.length != actualParameterTypes.length)
			return false;
		
		for(int index = 0; index < expectedParameterTypes.length; ++index)
			if(expectedParameterTypes[index] != actualParameterTypes[index])
				return false;
		
		return true;
	}

	/**
	 * 
	 * @param instance
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @param references
	 */
	protected void populateChildren(
		Object instance, 
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates,
		CompositionPopulation components,
		ReferenceMap references)
	{
		if( InstancePopulation.FULL == instancePopulation || 
			InstancePopulation.REQUIRED == instancePopulation ||
			InstancePopulation.DEFAULT == instancePopulation)
				populateAnnotatedFields(instance, instancePopulation, aggregates, components, references);
	}
	
	/**
	 * 
	 * @param instance
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 */
	protected void populateComponentInstances(
		Class<?> resultClass, 
		Object instance, 
		InstancePopulation instancePopulation,
		AggregationPopulation aggregates,
		CompositionPopulation components,
		ReferenceMap references)
	{
		if(Map.class.isAssignableFrom(resultClass))
			populateMapComponentInstances(resultClass, instance, instancePopulation, aggregates, components, references);
		else if(Collection.class.isAssignableFrom(resultClass))
			populateCollectionComponentInstances(resultClass, instance, instancePopulation, aggregates, components, references);
		else
			return;
	}
	
	/**
	 * @param resultClass
	 * @param instance
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 */
	@SuppressWarnings("unchecked")
	private void populateCollectionComponentInstances(
		Class resultClass, 
		Object instance,
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates, 
		CompositionPopulation components,
		ReferenceMap references
		)
	{
		MockDataGenerationType dataGenerationAnnotation = 
			(MockDataGenerationType)resultClass.getAnnotation(MockDataGenerationType.class);
		
		if(dataGenerationAnnotation != null)
		{
			String valueType = dataGenerationAnnotation.componentValueType();
			if(valueType == null)
			{
				getLogger().error("Collection derived class '" + resultClass.getName() + "' must specify a componentValueType in the annotation."); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
				
			Collection instanceAsMap = (Collection)instance;
			try
			{
				Class valueClass = Class.forName(valueType);
				
				for(int componentIndex=0; componentIndex < components.getCount(); ++componentIndex)
				{
					Object value = createInstance(valueClass, instancePopulation, aggregates, components, references);
					
					instanceAsMap.add(value);
				}
			}
			catch (ClassNotFoundException x)
			{
				x.printStackTrace();
			} 
		}
	}

	/**
	 * @param resultClass
	 * @param instance
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 */
	@SuppressWarnings("unchecked")
	private void populateMapComponentInstances(
		Class resultClass, 
		Object instance,
		InstancePopulation instancePopulation, 
		AggregationPopulation aggregates, 
		CompositionPopulation components,
		ReferenceMap references)
	{
		MockDataGenerationType dataGenerationAnnotation = 
			(MockDataGenerationType)resultClass.getAnnotation(MockDataGenerationType.class);
		
		String keyType = dataGenerationAnnotation.componentKeyType();
		if(keyType == null)
		{
			getLogger().error("Map derived field '" + resultClass.getName() + "' must specify a componentKeyType in the annotation."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		String valueType = dataGenerationAnnotation.componentValueType();
		if(valueType == null)
		{
			getLogger().error("Map derived class '" + resultClass.getName() + "' must specify a componentValueType in the annotation."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
			
		Map instanceAsMap = (Map)instance;
		try
		{
			Class keyClass = Class.forName(keyType);
			Class valueClass = Class.forName(valueType);
			
			for(int componentIndex=0; componentIndex < components.getCount(); ++componentIndex)
			{
				Object key = createInstance(keyClass, instancePopulation, aggregates, components, references);
				Object value = createInstance(valueClass, instancePopulation, aggregates, components, references);
				
				instanceAsMap.put(key, value);
			}
		}
		catch (ClassNotFoundException x)
		{
			x.printStackTrace();
		} 
	}

	/**
	 * Populate an instance of a type that is annotated with a MockDataGenerationType
	 * annotation.
	 * 
	 * @param instance
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @throws IllegalAccessException 
	 */
	protected void populateComposition(
		Object instance, 
		InstancePopulation instancePopulation,
		AggregationPopulation aggregates,
		CompositionPopulation components,
		ReferenceMap references) 
	throws IllegalAccessException
	{
		Class<?> instanceClass = instance.getClass();
		MockDataGenerationType typeAnnotation = instanceClass.getAnnotation(MockDataGenerationType.class);
		if(typeAnnotation != null)
		{
			if( Map.class.isInstance(instance) )
			{
				populateMapField(
					(Map)instance,
					typeAnnotation.componentKeyType(), typeAnnotation.componentValueType(),
					instance.getClass().getSimpleName(), components.getCount(), 
					instancePopulation, aggregates, components, references);
			}
			else if( Collection.class.isInstance(instance) )
			{
				populateCollectionField(
					(Collection)instance,
					typeAnnotation.componentValueType(),
					instance.getClass().getSimpleName(), components.getCount(), 
					instancePopulation, aggregates, components, references);
			}
		}
	}
	
	/**
	 * Populate the 'simple' fields that are annotated with the
	 * MockDataGenerationField annotation.
	 * 
	 * @param instance
	 * @param instancePopulation
	 * @param children
	 */
	protected void populateAnnotatedFields(
		Object instance, 
		InstancePopulation instancePopulation,
		AggregationPopulation aggregates,
		CompositionPopulation components,
		ReferenceMap references)
	{
		Field[] fields = instance.getClass().getDeclaredFields();
		
		for(Field field : fields)
		{
			MockDataGenerationField dataGenerationAnnotation = field.getAnnotation(MockDataGenerationField.class);
			
			// if the field is annotated and the annotation specifies required or
			// this generator is set to ALL mode then populate the field
			if( dataGenerationAnnotation != null )
				populateAnnotatedField(
					instance, 
					instancePopulation, 
					aggregates, 
					components, 
					references, 
					field,
					dataGenerationAnnotation);
		}
	}
	/**
	 * @param instance
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @param references
	 * @param field
	 * @param dataGenerationAnnotation
	 */
	private void populateAnnotatedField(
		Object instance, 
		InstancePopulation instancePopulation,
		AggregationPopulation aggregates, 
		CompositionPopulation components, 
		ReferenceMap references, 
		Field field,
		MockDataGenerationField dataGenerationAnnotation)
	{
		// if the field is not required or the data generation mode is not FULL the field does not need
		// to be populated
		if( !(dataGenerationAnnotation.required() || InstancePopulation.FULL == instancePopulation) )
			return;
		
		Class<?> fieldType = field.getType();
		
		try
		{
			if( isSimpleType(fieldType) )
			{
				String mockData = 
					String.class == field.getType() ?
						createStringData(			// creates the data as a string according to the annotation
							dataGenerationAnnotation.pattern(),
							dataGenerationAnnotation.defaultValue(),
							dataGenerationAnnotation.minimumStringLength(),
							dataGenerationAnnotation.maximumStringLength() ) :
					Date.class == field.getType() ?
						createDateData(			// creates the data as a string according to the annotation
							dataGenerationAnnotation.defaultValue(),
							dataGenerationAnnotation.minimumDate(),
							dataGenerationAnnotation.maximumDate() ) :
						createNumericData(			// creates the data as a string according to the annotation
							field.getType(),
							dataGenerationAnnotation.pattern(),
							dataGenerationAnnotation.defaultValue(),
							dataGenerationAnnotation.minimum(),
							dataGenerationAnnotation.maximum() );
				
				try
				{
					if( ! setSimpleField(instance, field, mockData) )
						getLogger().error( "Unable to set " + instance.getClass().getSimpleName() +  //$NON-NLS-1$
							"." + field.getName() + ", type must be a simple type." ); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch (Exception x)
				{
					getLogger().error( "Unable to set " + instance.getClass().getSimpleName() +  //$NON-NLS-1$
						"." + field.getName() +  //$NON-NLS-1$
						", exception of type " + x.getClass().getSimpleName() + "(" + x.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			else if( fieldType.isArray() && isSimpleType(fieldType.getComponentType()) )
			{
				Class<?> componentType = fieldType.getComponentType();
				// creates an array of the component type
				Object valueArray = Array.newInstance(componentType, 3);
				for(int index=0; index < aggregates.getCount(); ++index)
				{
					String mockData = createNumericData(			// creates the data as a string according to the annotation
						componentType,
						dataGenerationAnnotation.pattern(),
						dataGenerationAnnotation.defaultValue(),
						dataGenerationAnnotation.minimum(),
						dataGenerationAnnotation.maximum() );
					
					setArrayValue(valueArray, index, mockData);
				}
			}
			else if( fieldType.isArray() && !isSimpleType(fieldType.getComponentType()) )
			{
				Class<?> componentType = fieldType.getComponentType();
				// creates an array of the component type
				Object valueArray = Array.newInstance(componentType, 3);
				for(int index=0; index < aggregates.getCount(); ++index)
				{
					Object obj = createInstance(fieldType, instancePopulation, aggregates, components, references);
					
					Array.set(valueArray, index, obj);
				}
			}
			else if( InputStream.class == fieldType )
			{
				boolean accessibleField = field.isAccessible();
				field.setAccessible(true);

				try
				{
					MockSizedInputStream inStream = createInputStream(
						instancePopulation,
						dataGenerationAnnotation.mediaTypes(),
						dataGenerationAnnotation.defaultMediaType());
					
					field.set(instance, inStream);
				}
				finally
				{
					field.setAccessible(accessibleField);
				}
			}
			else
			{
				boolean accessibleField = field.isAccessible();
				field.setAccessible(true);

				try
				{
					if( Map.class.isAssignableFrom(fieldType) )
					{
						Map map = (Map)field.get(instance);
						String name = field.getName();
						if(map == null)
							getLogger().error("Map derived field '" + name + "' must be initialized to an empty instance."); //$NON-NLS-1$ //$NON-NLS-2$
						else if(map.size() > 0)
							getLogger().error("Map derived field '" + name + "' must be empty."); //$NON-NLS-1$ //$NON-NLS-2$
						
						populateMapField(
							map, 
							dataGenerationAnnotation.componentKeyType(), dataGenerationAnnotation.componentValueType(), 
							name, components.getCount(),
							instancePopulation, aggregates, components, references);
					}
					else if( Collection.class.isAssignableFrom(fieldType) )
					{
						Collection<?> collection = (Collection<?>)field.get(instance);
						String name = field.getName();
						if(collection == null)
							getLogger().error("Collection derived field '" + name + "' must be initialized to an empty instance."); //$NON-NLS-1$ //$NON-NLS-2$
						else if(collection.size() > 0)
							getLogger().error("Collection derived field '" + name + "' must be empty."); //$NON-NLS-1$ //$NON-NLS-2$
						
						populateCollectionField(
							collection, dataGenerationAnnotation.componentValueType(), 
							name, components.getCount(),
							instancePopulation, aggregates, components, references);
					}
					else
					{
						// the createInstance() will return true if it can
						// create the instance.  This gives application code
						// an opportunity to create instances.
						Object obj = createInstance(fieldType, instancePopulation, aggregates, components, references);
						if(obj != null)
							setReferenceField(instance, field, obj);
					}
				}
				finally
				{
					field.setAccessible(accessibleField);
				}
			}
		}
		catch (IllegalArgumentException x)
		{
			x.printStackTrace();
		}
		catch (IllegalAccessException x)
		{
			x.printStackTrace();
		}
		finally
		{
			
		}
	}

	/**
	 * @param instancePopulation
	 * @param mediaTypes
	 * @param defaultMediaType
	 * @return
	 */
	private MockSizedInputStream createInputStream(
		InstancePopulation instancePopulation, 
		String mediaTypes,
		String defaultMediaType)
	{
		String[] splitMediaTypes = ( mediaTypes == null || mediaTypes.length() == 0 ? null : mediaTypes.split(",") );
		
		String mediaType = null;
		
		if(Mode.DEFAULT == getConfiguration().getMode())
			mediaType = defaultMediaType;
		if(mediaType == null && Mode.MINIMUM == getConfiguration().getMode() && splitMediaTypes != null)
			mediaType = splitMediaTypes[0];
		if(mediaType == null && Mode.MAXIMUM == getConfiguration().getMode() && splitMediaTypes != null)
			mediaType = splitMediaTypes[splitMediaTypes.length-1];
		if(mediaType == null && splitMediaTypes != null)
			mediaType = splitMediaTypes[createRandomInt(0, splitMediaTypes.length-1)];
		
		return mediaType == null ? null : getInputStream(mediaType);
	}
	
	/**
	 * 
	 * @param map
	 * @param keyType
	 * @param valueType
	 * @param name
	 * @param desiredInstanceCount
	 * @param instancePopulation
	 * @param aggregates
	 * @param components
	 * @param references
	 * @throws IllegalAccessException
	 */
	private void populateMapField(
		Map map, 
		String keyType,
		String valueType,
		String name,
		int desiredInstanceCount,
		InstancePopulation instancePopulation,
		AggregationPopulation aggregates, 
		CompositionPopulation components,
		ReferenceMap references) 
	throws IllegalAccessException
	{
		if(keyType == null)
			getLogger().error("Map derived field '" + name + "' must specify a componentKeyType in the annotation."); //$NON-NLS-1$ //$NON-NLS-2$
		if(valueType == null)
			getLogger().error("Map derived field '" + name + "' must specify a componentValueType in the annotation."); //$NON-NLS-1$ //$NON-NLS-2$
		
		if(map == null)
			getLogger().error("Collection derived field '" + name + "' must be initialized to an empty instance."); //$NON-NLS-1$ //$NON-NLS-2$
		else if(map.size() > 0)
			getLogger().error("Collection derived field '" + name + "' has " + map.size() + " contained instances and must be empty."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		//Map currentValue = (Map)field.get(instance);
		try
		{
			Class<?> keyTypeClass = Class.forName(keyType);
			Class<?> valueTypeClass = Class.forName(valueType);
		
			for(int index = 0; index < desiredInstanceCount; ++index)
			{
				Object key = createInstance(keyTypeClass, instancePopulation, aggregates, components, references);
				Object value = createInstance(valueTypeClass, instancePopulation, aggregates, components, references);
				
				map.put(key, value);
			}
		}
		catch (ClassNotFoundException x)
		{
			// TODO Auto-generated catch block
			x.printStackTrace();
		}
	}

	private void populateCollectionField(
		Collection collection, 
		String valueType,  
		String name, 
		int desiredInstanceCount,
		InstancePopulation instancePopulation,
		AggregationPopulation aggregates, 
		CompositionPopulation components,
		ReferenceMap references) 
	throws IllegalAccessException
	{
		if(valueType == null || valueType.length() == 0)
			getLogger().error("Collection derived field '" + name + "' must specify a componentValueType in the annotation."); //$NON-NLS-1$ //$NON-NLS-2$
		
		if(collection == null)
			getLogger().error("Collection derived field '" + name + "' must be initialized to an empty instance."); //$NON-NLS-1$ //$NON-NLS-2$
		else if(collection.size() > 0)
			getLogger().error("Collection derived field '" + name + "' has " + collection.size() + " contained instances and must be empty."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		else
		{
			for(int index = 0; index < desiredInstanceCount; ++index)
			{
				Class<?> valueTypeClass;
				try
				{
					valueTypeClass = Class.forName(valueType);
					Object value = createInstance(valueTypeClass, instancePopulation, aggregates, components, references);
					if(value != null)
						collection.add(value);
					else
						getLogger().error("Unable to create collection instance of type '" + valueType + "'."); //$NON-NLS-1$ //$NON-NLS-2$
					
				}
				catch (ClassNotFoundException x)
				{
					getLogger().error("No class named '" + valueType + "' found."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
	
	/**
	 * 
	 * @param valueArray
	 * @param index
	 * @param stringValue
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private boolean setArrayValue(Object valueArray, int index, String stringValue) 
	throws IllegalArgumentException, IllegalAccessException
	{
		Class<?> fieldType = valueArray.getClass().getComponentType();
		Object value = null;
		
		if(String.class == fieldType)
			value = stringValue;
		
		else if(Boolean.class == fieldType)
			value = new Boolean(stringValue);
		else if(Character.class == fieldType)
			value = new Character(stringValue.charAt(0));
		else if(Byte.class == fieldType)
			value = new Byte(stringValue);
		else if(Short.class == fieldType)
			value = new Short(stringValue);
		else if(Integer.class == fieldType)
			value = new Integer(stringValue);
		else if(Long.class == fieldType)
			value = new Long(stringValue);
		else if(Float.class == fieldType)
			value = new Float(stringValue);
		else if(Double.class == fieldType)
			value = new Double(stringValue);
		
		if(value != null)
		{
			Array.set(valueArray, index, value);
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param field
	 * @param mockData
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ParseException 
	 */
	private boolean setSimpleField(Object instance, Field field, String value) 
	throws IllegalArgumentException, IllegalAccessException, ParseException
	{
		Class<?> fieldType = field.getType();
		
		if(String.class == fieldType)
			setReferenceField(instance, field, value);
		
		else if(Date.class == fieldType)
			setReferenceField(instance, field, parseDateValue(value));
		
		else if(Boolean.class == fieldType)
			setReferenceField(instance, field, new Boolean(value) );
		else if(Character.class == fieldType)
			setReferenceField(instance, field, new Character(value.charAt(0)) );
		else if(Byte.class == fieldType)
			setReferenceField(instance, field, new Byte(value) );
		else if(Short.class == fieldType)
			setReferenceField(instance, field, new Short(value) );
		else if(Integer.class == fieldType)
			setReferenceField(instance, field, new Integer(value) );
		else if(Long.class == fieldType)
			setReferenceField(instance, field, new Long(value) );
		else if(Float.class == fieldType)
			setReferenceField(instance, field, new Float(value) );
		else if(Double.class == fieldType)
			setReferenceField(instance, field, new Double(value) );
		
		else if(boolean.class == fieldType)
			field.set( instance, Boolean.parseBoolean(value) );
		else if(char.class == fieldType)
			field.set( instance, value.charAt(0) );
		else if(byte.class == fieldType)
			field.set( instance, Byte.parseByte(value) );
		else if(short.class == fieldType)
			field.set( instance, Short.parseShort(value) );
		else if(int.class == fieldType)
			field.set( instance, Integer.parseInt(value) );
		else if(long.class == fieldType)
			field.set( instance, Long.parseLong(value) );
		else if(float.class == fieldType)
			field.set( instance, Float.parseFloat(value) );
		else if(double.class == fieldType)
			field.set( instance, Double.parseDouble(value) );
		else
			return false;
		
		return true;
	}

	/**
	 * 
	 * @param instance
	 * @param field
	 * @param value
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	protected void setReferenceField(Object instance, Field field, Object value) 
	throws IllegalArgumentException, IllegalAccessException
	{
		boolean accessible = field.isAccessible();
		try
		{
			field.setAccessible(true);
			field.set( instance, value );
		}
		finally
		{
			field.setAccessible(accessible);
		}
	}
	
	/**
	 * @param type
	 * @param pattern
	 * @param defaultValue
	 * @param minimum
	 * @param maximum
	 * @return
	 */
	private <T extends Object> String createNumericData(
		Class<T> type, 
		String pattern, 
		String defaultValue, 
		double minimum, 
		double maximum)
	{
		if(Mode.DEFAULT == getConfiguration().getMode())
			return defaultValue != null ? defaultValue : getDefaultFieldValue(type);
		
		// if the pattern is specified then minimum, maximum are ignored
		// Dates do not use the pattern, they must specify minimum and maximum 
		if(pattern != null && Date.class != type)
			return createRandomString(pattern);
		
		return createRandomString(type, minimum, maximum);
	}

	/**
	 * @param pattern
	 * @param defaultValue
	 * @param minimumStringLength
	 * @param maximumStringLength
	 * @return
	 */
	private String createStringData(
		String pattern, 
		String defaultValue, 
		int minimumStringLength,
		int maximumStringLength)
	{
		if(Mode.DEFAULT == getConfiguration().getMode())
			return defaultValue != null && defaultValue.length() > 0 ? defaultValue : DEFAULT_STRING_VALUE;
		
		// if the pattern is specified then minimum, maximum are ignored
		// Dates do not use the pattern, they must specify minimum and maximum 
		if(pattern != null && pattern.length() > 0)
			return createRandomString(pattern);
		
		return createRandomString(String.class, minimumStringLength, maximumStringLength);
	}

	/**
	 * @param defaultValue
	 * @param minimumStringLength
	 * @param maximumStringLength
	 * @return
	 */
	private String createDateData(
		String defaultValue, 
		String minimumValue, 
		String maximumValue)
	{
		try
		{
			Date minimumDate = parseDateValue(minimumValue);
			Date maximumDate = parseDateValue(maximumValue);
			
			Date randomDate = new Date( 
				(long)(Math.random() * (maximumDate.getTime() - minimumDate.getTime()) ) + minimumDate.getTime()
			);
			
			return DEFAULT_DATE_FORMAT.format(randomDate);
		}
		catch (ParseException x)
		{
			x.printStackTrace();
			return null;
		}
	}

	private Date parseDateValue(String minimumValue) 
	throws ParseException
	{
		Date date;
		
		try{date = DEFAULT_DATETIME_FORMAT.parse(minimumValue);}
		catch (ParseException x)
		{date = DEFAULT_DATE_FORMAT.parse(minimumValue);}
		return date;
	}

	
	/**
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T extends Object> Object createSimpleDataType(Class<T> type)
	{
		if( ! isSimpleType(type) )
			return null;
		if(Mode.DEFAULT == getConfiguration().getMode())
			return getDefaultFieldValue(type);

		if(String.class == type)
			return createString();
		if(Date.class == type)
			return createRandomDate();
		if(Boolean.class == type || boolean.class == type)
			return createRandomBoolean();
		if(Character.class == type || char.class == type)
			return createRandomChar(RANDOM_CHAR_PATTERN);
		if(Byte.class == type || byte.class == type)
			return createRandomByte(RANDOM_BYTE_PATTERN);
		if(Short.class == type || short.class == type)
			return createRandomShort(RANDOM_SHORT_PATTERN);
		if(Integer.class == type || int.class == type)
			return createRandomInt(RANDOM_INT_PATTERN);
		if(Long.class == type || long.class == type)
			return createRandomLong(RANDOM_LONG_PATTERN);
		if(Float.class == type || float.class == type)
			return createRandomFloat(RANDOM_FLOAT_PATTERN);
		if(Double.class == type || double.class == type)
			return createRandomDouble(RANDOM_DOUBLE_PATTERN);

		return null;
	}
	
	/**
	 * 
	 * @param mediaType - formatted as type/subtype
	 * @return
	 */
	public MockSizedInputStream getInputStream(String mediaType)
	{
		String[] mediaTypeComponents = mediaType.split("/");
		if(mediaTypeComponents.length != 2)
		{
			logger.error("Media type '" + mediaType + "' is not valid when creating an input stream.");
			return null;
		}
		String resourceName = "gov/va/med/streamdata/" + mediaTypeComponents[0] + "." + mediaTypeComponents[1];

		URL resourceUrl = this.getClass().getClassLoader().getResource(resourceName);
		InputStream wrappedStream = null;
		long size = 0L;
		
		try
		{
			URLConnection conn = resourceUrl.openConnection();
			size = conn.getContentLength();
			wrappedStream = conn.getInputStream();
		}
		catch (IOException x)
		{
			x.printStackTrace();
		}
		
		return wrappedStream == null ? 
			null : 
			new MockSizedInputStream(
				new NetworkSimulatingInputStream(wrappedStream, getConfiguration().getDelayMode(), getConfiguration().getExceptionMode()),
				size,
				mediaType );
	}
	
	/**
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	private <T extends Object> String getDefaultFieldValue(Class<T> type)
	{
		if(String.class == type)
			return DEFAULT_STRING_VALUE;
		if(Date.class == type)
			return DEFAULT_DATE_FORMAT.format( DEFAULT_DATE_VALUE );
		if(Boolean.class == type || boolean.class == type)
			return DEFAULT_BOOLEAN_VALUE;
		if(Character.class == type || char.class == type)
			return DEFAULT_CHAR_VALUE;
		if(Byte.class == type || byte.class == type)
			return DEFAULT_BYTE_VALUE;
		if(Short.class == type || short.class == type)
			return DEFAULT_SHORT_VALUE;
		if(Integer.class == type || int.class == type)
			return DEFAULT_INT_VALUE;
		if(Long.class == type || long.class == type)
			return DEFAULT_LONG_VALUE;
		if(Float.class == type || float.class == type)
			return DEFAULT_FLOAT_VALUE;
		if(Double.class == type || double.class == type)
			return DEFAULT_DOUBLE_VALUE;

		return null;
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	protected boolean isSimpleType(Class<?> clazz)
	{
		return 
			String.class == clazz ||
			Date.class == clazz ||
			Boolean.class == clazz || boolean.class == clazz ||
			Character.class == clazz || char.class == clazz ||
			Byte.class == clazz || byte.class == clazz ||
			Integer.class == clazz || int.class == clazz ||
			Long.class == clazz || long.class == clazz ||
			Float.class == clazz || float.class == clazz ||
			Double.class == clazz || double.class == clazz ||
			Void.class == clazz || void.class == clazz;
	}
	
	protected boolean setSimplePropertyValue(Object instance, Method setter, Class<?> type)
	{
		String defaultValue = getDefaultFieldValue(type);

		try
		{
			if(String.class == type)
				setter.invoke(instance, defaultValue );
			else if(byte.class == type)
				setter.invoke(instance, Byte.parseByte(defaultValue) );
			else if(char.class == type)
				setter.invoke(instance, defaultValue.charAt(0) );
			else if(short.class == type)
				setter.invoke(instance, Short.parseShort(defaultValue) );
			else if(int.class == type)
				setter.invoke(instance, Integer.parseInt(defaultValue) );
			else if(long.class == type)
				setter.invoke(instance, Long.parseLong(defaultValue) );
			else if(float.class == type)
				setter.invoke(instance, Float.parseFloat(defaultValue) );
			else if(double.class == type)
				setter.invoke(instance, Double.parseDouble(defaultValue) );
			return true;
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		return false;
	}
	
	// 0x5b is left square bracket '['
	// 0x5d is right square bracket ']'
	// 0x7b is left curly bracket '{'
	// 0x7d is right curly bracket '}'
	// reads as:
	// (\\x5b[^\\x5d]*\\x5d) - left square bracket, followed by any number of anything but 
	//                         right square bracket followed by right square bracket
	// (?:\\x7b([0-9]+)(?:,([0-9]+))?\\x7d)? - optionally followed by a left curly bracket
	//                                         at least one digit, optionally followed by a comma
	//                                         and at least one digit, followed by a right curly
	//                                         bracket
	// -or-
	// [^\\x5b]* - anything but a left square bracket
	// 
	public static final String SIMPLE_REGEX = 
		"(\\x5b[^\\x5d]*\\x5d)(?:\\x7b([0-9]+)(?:,([0-9]+))?\\x7d)?|[^\\x5b]*"; //$NON-NLS-1$
	public static final String DEFAULT_MIN = "1"; //$NON-NLS-1$
	public static final String DEFAULT_MAX = "1"; //$NON-NLS-1$
	public final static Pattern simpleRegexPattern = Pattern.compile(SIMPLE_REGEX);

	
	public static final int MINIMUM_MANY_CHILD_COUNT = 2;
	public static final int DEFAULT_MANY_CHILD_COUNT = 3;
	public static final int MAXIMUM_MANY_CHILD_COUNT = 16;
	protected int createManyChildCount()
	{
		switch( getConfiguration().getMode() )
		{
		case MAXIMUM:
			return MAXIMUM_MANY_CHILD_COUNT;
		case MINIMUM:
			return MINIMUM_MANY_CHILD_COUNT;
		case RANDOMIZE:
			return (int)( (Math.random() * (MAXIMUM_MANY_CHILD_COUNT - MINIMUM_MANY_CHILD_COUNT)) + MINIMUM_MANY_CHILD_COUNT );
		case DEFAULT:
		default:
			return DEFAULT_MANY_CHILD_COUNT;
		}
	}
	
	/**
	 * Used to create a test string when the content and size are not important.
	 * if randomize is true then the returned String will be an alphanumeric string
	 * between 3 and 32 characters long.
	 * else the returned value will be "DefaultValue"
	 * 
	 * @return
	 */
	public String createString()
	{
		switch( getConfiguration().getMode() )
		{
		case MAXIMUM:
			return createRandomString(RANDOM_STRING_CHARACTER_CLASS + STRING_MAXIMUM_REPEATER);
		case MINIMUM:
			return createRandomString(RANDOM_STRING_CHARACTER_CLASS + STRING_MINIMUM_REPEATER);
		case RANDOMIZE:
			return createRandomString(RANDOM_STRING_CHARACTER_CLASS + STRING_RANDOM_REPEATER);
		case DEFAULT:
		default:
			return DEFAULT_STRING_VALUE;
		}
	}
	
	public Date createRandomDate()
	{
		return new Date(createRandomLong(RANDOM_LONG_PATTERN));
	}
	
	public Date createRandomDate(Date minimum, Date maximum)
	{
		long timestamp = createRandomLong(minimum.getTime(), maximum.getTime());
		return new Date(timestamp);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean createRandomBoolean()
	{
		return Math.random() >= .5;
	}
	
	/**
	 * Create a random byte value using the given pattern.
	 * The pattern may include a [+-] at the beginning to specify
	 * that both negative and positive numbers may be generated.
	 * This is nonstandard regex but makes the internal parsing much
	 * easier.
	 */
	public byte createRandomByte(String regex)
	throws NumberFormatException
	{
		String s = createRandomString(regex);
		if(s.startsWith("+")) //$NON-NLS-1$
			s = s.substring(1);
		return Byte.parseByte(s);
	}
	
	/**
	 * Create a random short value using the given pattern.
	 * The pattern may include a [+-] at the beginning to specify
	 * that both negative and positive numbers may be generated.
	 * This is nonstandard regex but makes the internal parsing much
	 * easier.
	 * 
	 * @param regex
	 * @return
	 * @throws NumberFormatException
	 */
	public short createRandomShort(String regex)
	throws NumberFormatException
	{
		String s = createRandomString(regex);
		if(s.startsWith("+")) //$NON-NLS-1$
			s = s.substring(1);
		return Short.parseShort(s);
	}
	
	/**
	 * Create a random int value using the given pattern.
	 * The pattern may include a [+-] at the beginning to specify
	 * that both negative and positive numbers may be generated.
	 * This is nonstandard regex but makes the internal parsing much
	 * easier.
	 * 
	 * @param regex
	 * @return
	 * @throws NumberFormatException
	 */
	public int createRandomInt(String regex)
	throws NumberFormatException
	{
		String s = createRandomString(regex);
		if(s.startsWith("+")) //$NON-NLS-1$
			s = s.substring(1);
		return Integer.parseInt(s);
	}
	
	/**
	 * Create a random long value using the given pattern.
	 * The pattern may include a [+-] at the beginning to specify
	 * that both negative and positive numbers may be generated.
	 * This is nonstandard regex but makes the internal parsing much
	 * easier.
	 * 
	 * @param regex
	 * @return
	 * @throws NumberFormatException
	 */
	public long createRandomLong(String regex)
	throws NumberFormatException
	{
		String s = createRandomString(regex);
		if(s.startsWith("+")) //$NON-NLS-1$
			s = s.substring(1);
		return Long.parseLong(s);
	}

	/**
	 * Create a random float value using the given pattern.
	 * The pattern may include a [+-] at the beginning to specify
	 * that both negative and positive numbers may be generated.
	 * This is nonstandard regex but makes the internal parsing much
	 * easier.
	 * 
	 * @param regex
	 * @return
	 * @throws NumberFormatException
	 */
	public float createRandomFloat(String regex)
	throws NumberFormatException
	{
		String s = createRandomString(regex);
		if(s.startsWith("+")) //$NON-NLS-1$
			s = s.substring(1);
		return Float.parseFloat(s);
	}
	
	/**
	 * Create a random double value using the given pattern.
	 * The pattern may include a [+-] at the beginning to specify
	 * that both negative and positive numbers may be generated.
	 * This is nonstandard regex but makes the internal parsing much
	 * easier.
	 * 
	 * @param regex
	 * @return
	 * @throws NumberFormatException
	 */
	public double createRandomDouble(String regex)
	throws NumberFormatException
	{
		String s = createRandomString(regex);
		if(s.startsWith("+")) //$NON-NLS-1$
			s = s.substring(1);
		return Double.parseDouble(s);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T createRandomNumber(Class<T> type, double minimum, double maximum)
	{
		if(Byte.class == type)
		{
			byte value = createRandomByte((byte)minimum, (byte)maximum);
			return (T)new Byte(value);
		}
		else if(Character.class == type)
		{
			char value = createRandomChar((char)minimum, (char)maximum);
			return (T)new Character(value);
		}
		else if(Short.class == type)
		{
			short value = createRandomShort((short)minimum, (short)maximum);
			return (T)new Short(value);
		}
		else if(Integer.class == type)
		{
			int value = createRandomInt((int)minimum, (int)maximum);
			return (T)new Integer(value);
		}
		else if(long.class == type || Long.class == type)
		{
			long value = createRandomLong((long)minimum, (long)maximum);
			return (T)new Long(value);
		}
		else if(float.class == type || Float.class == type)
		{
			float value = createRandomFloat((float)minimum, (float)maximum);
			return (T)new Float(value);
		}
		else if(double.class == type || Double.class == type)
		{
			double value = createRandomDouble((double)minimum, (double)maximum);
			return (T)new Double(value);
		}
		
		return null;
	}
	
	public char createRandomChar(char minimum, char maximum)
	throws NumberFormatException
	{
		return (char)(((double)(maximum-minimum)) * Math.random() + (double)minimum);
	}
	
	public byte createRandomByte(byte minimum, byte maximum)
	throws NumberFormatException
	{
		return (byte)(((double)(maximum-minimum)) * Math.random() + (double)minimum);
	}
	
	public short createRandomShort(short minimum, short maximum)
	throws NumberFormatException
	{
		return (short)(((double)(maximum-minimum)) * Math.random() + (double)minimum);
	}
	
	public int createRandomInt(int minimum, int maximum)
	throws NumberFormatException
	{
		return (int)(((double)(maximum-minimum)) * Math.random() + (double)minimum);
	}
	
	public long createRandomLong(long minimum, long maximum)
	throws NumberFormatException
	{
		return (long)(((double)(maximum-minimum)) * Math.random() + (double)minimum);
	}
	
	public float createRandomFloat(float minimum, float maximum)
	throws NumberFormatException
	{
		return (float)(((double)(maximum-minimum)) * Math.random() + (double)minimum);
	}
	
	public double createRandomDouble(double minimum, double maximum)
	throws NumberFormatException
	{
		return ((double)(maximum-minimum)) * Math.random() + (double)minimum;
	}
	
	public String createRandomPhoneNumber()
	{
		return createRandomString(RANDOM_PHONE_PATTERN);
	}
	public String createRandomName()
	{
		return createRandomString(RANDOM_NAME_PATTERN);
	}
	public String createRandomSurname()
	{
		return createRandomString(RANDOM_SURNAME_PATTERN);
	}
	public String createRandomGivenName()
	{
		return createRandomString(RANDOM_GIVENNAME_PATTERN);
	}
	public String createRandomSSN()
	{
		return createRandomString(RANDOM_SSN_PATTERN);
	}
	
	public String createRandomStreetAddress()
	{
		return createRandomString(RANDOM_STREETADDRESS_PATTERN);
	}
	public String createRandomApartment()
	{
		return createRandomString(RANDOM_APARTMENT_PATTERN);
	}
	public String createRandomCity()
	{
		return createRandomString(RANDOM_CITY_PATTERN);
	}
	public String createRandomState()
	{
		return createRandomString(RANDOM_STATE_PATTERN);
	}
	public String createRandomZip()
	{
		return createRandomString(RANDOM_ZIP_PATTERN);
	}
	public String createRandomZip4()
	{
		return createRandomString(RANDOM_ZIP4_PATTERN);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param enumerationClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Enum> T selectEnum(Class<T> enumerationClass)
	{
		try
		{
			Method valuesMethod = enumerationClass.getMethod("values", (Class<?>[])null); //$NON-NLS-1$
			Enum[] values = (Enum[])( valuesMethod.invoke(null, (Object[])null) );
			int index = (Mode.DEFAULT == getConfiguration().getMode()) ? 0 :
				(Mode.MINIMUM == getConfiguration().getMode()) ? 0 :
				(Mode.MAXIMUM == getConfiguration().getMode()) ? values.length-1 :
				(int)(Math.random() * values.length);
			String name = values[index].name();
			return (T)Enum.valueOf(enumerationClass, name);
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Given a simple regular expression, build a string in
	 * the given pattern.
	 * The allowable characters are:
	 * 0-9 a-z A-Z
	 * []{}
	 * 
	 * eg: regex="Hello[A-Z][a-z]{3}" may generate
	 * "HelloAbgt" or "HelloNjhs"
	 * 
	 * @param regex
	 * @return
	 */
	public String createRandomString(String regex)
	{
		StringBuilder sb = new StringBuilder();
		
		Matcher matcher = simpleRegexPattern.matcher(regex);
		
		while( matcher.find() )
		{
			String group = matcher.group();
			// if group count is 1 then this is a literal string
			if( matcher.group(1) == null)
				sb.append(group);
			else			// not a literal group, need to generate a string by pattern
			{
				String pattern = matcher.group(1);
				pattern = pattern.substring(1, pattern.length()-1);
				int minCharCount = Integer.parseInt( 
					matcher.group(2) != null ? 
						matcher.group(2) : 
						DEFAULT_MIN );
				int maxCharCount = Integer.parseInt( 
					matcher.group(3) != null ? 
						matcher.group(3) :
						matcher.group(2) != null ? 
							matcher.group(2) : 
							DEFAULT_MAX );
				
				int actualCharCount = (int)( (Math.random() * (maxCharCount - minCharCount)) + minCharCount ); 
				for(int index=0; index < actualCharCount; ++index)
					sb.append( createRandomChar(pattern) );
			}
		}
		
		return sb.toString();
	}

	/**
	 * 
	 * @param type
	 * @param minimum
	 * @param maximum
	 * @return
	 */
	public <T extends Object> String createRandomString(Class<T> type, double minimum, double maximum)
	{
		if(String.class == type)
			return createRandomString("[a-zA-Z0-9]{" + (int)minimum + "," + (int)maximum + "}" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		else if(Date.class == type)
		{
			return null;
		}
		else
			return "" + createRandomNumber(type, minimum, maximum); //$NON-NLS-1$
	}
	
	public final static String CHARACTER_CLASS_REGEX = "([^\\x2d\\x26])([\\x2d\\x26])([^\\x2d\\x26])|(.)"; //$NON-NLS-1$
	public final static Pattern characterClassPattern = Pattern.compile(CHARACTER_CLASS_REGEX);
	
	public final static int LEFT_OPERAND_GROUP = 1;
	public final static int OPERATOR_GROUP = 2;
	public final static int RIGHT_OPERAND_GROUP = 3;
	public final static int LITERAL_GROUP = 4;
	
	/**
	 * This will take a String meeting the regular expression defined above
	 * and parse it to determine the acceptable character ranges and then
	 * randomly pick a character in that range.
	 * The regex results in something like:
	 * "a" => group(0)="a", group(1)=null, group(2)=null, group(3)="a"
	 * "a-b" => group(0)="a-b", group(1)="a", group(2)="b", group(3)=null
	 * "cd-vf" (requires multiple "find()" calls) =>
	 * 1.) group(0)="c",   group(1)=null, group(2)=null, group(3)=null, group(4)="c"
	 * 2.) group(0)="d-v", group(1)="d",  group(2)="-",  group(3)="v", group(4)=null
	 * 3.) group(0)="f",   group(1)=null, group(2)=null, group(3)=null, group(4)="f"
	 * 2.) group(0)="d|v", group(1)="d",  group(2)="|",  group(3)="v", group(4)=null
	 * 
	 * @param pattern - should be in the form '[' + some characters + ']'
	 * @return
	 */
	protected char createRandomChar(String pattern)
	{
		StringBuilder sb = new StringBuilder();
		Matcher characterClassMatcher = characterClassPattern.matcher(pattern);
		
		while(characterClassMatcher.find())
		{
			if( characterClassMatcher.group(LITERAL_GROUP) != null )
				sb.append(characterClassMatcher.group(LITERAL_GROUP));
			else
			{
				char leftOperand=characterClassMatcher.group(LEFT_OPERAND_GROUP).charAt(0);
				char rightOperand=characterClassMatcher.group(RIGHT_OPERAND_GROUP).charAt(0);
				String operator=characterClassMatcher.group(OPERATOR_GROUP);
				
				if( "-".equals(operator) ) //$NON-NLS-1$
					for(char ch=leftOperand; ch <= rightOperand; ++ch)
						sb.append(ch);
				else if( "&".equals(operator) ) //$NON-NLS-1$
					if(leftOperand == rightOperand)
						sb.append(leftOperand);
			}
		}
		
		int random = (int)(Math.random() * sb.length());
		return sb.charAt(random);
	}
	
	/**
	 * Write from minSize to maxSize random bytes to the given channel
	 * 
	 * @param writableChannel
	 * @param minSize
	 * @param maxSize
	 * @throws IOException 
	 */
	public void writeRandomBytes(WritableByteChannel writableChannel, int minSize, int maxSize) 
	throws IOException
	{
		int size = Math.abs( createRandomInt(minSize, maxSize) );
		ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		for(int index=0; index < size; ++index)
			buffer.put(createRandomByte((byte)0, (byte)255));
		buffer.flip();
		writableChannel.write(buffer);
	}
	
	/**
	 * 
	 * @param instance
	 * @param instancePopulation
	 * @param children
	 */
	protected void populateChildrenThroughSetters(
		Object instance, 
		InstancePopulation instancePopulation, 
		AggregationPopulation children,
		CompositionPopulation components,
		ReferenceMap references)
	{
		if(AggregationPopulation.NONE == children)
			return;
		
		for(Iterator<Method> setterIterator=declaredSetterMethods(instance); setterIterator.hasNext(); )
		{
			Method setter = setterIterator.next();
			Class<?> resultType = setter.getParameterTypes()[0];
			if( isSimpleType(resultType) )
				setSimplePropertyValue(instance, setter, resultType);
			
			else if( resultType.isArray() )
			{
				Class<?> componentType = resultType.getComponentType();
				Object[] value = new Object[AggregationPopulation.ONE == children ? 1 : 3];
				for( int index=0; index < children.getCount(); ++index )
					value[index] = createInstance(componentType, instancePopulation, children, components, references);

				try
				{
					setter.invoke(instance, resultType.cast(value));
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}
			// all reference types, including collections
			else
			{
				try
				{
					Object value = createInstance(resultType, instancePopulation, children, components, references);
					if(value != null)
						setter.invoke(instance, resultType.cast(value));
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}
		}
	}
	
	private static final String GETTER_REGEX = "(is|get)([A-Z][A-Za-z0-9_]*)"; //$NON-NLS-1$
	protected static Iterator<Method> declaredGetterMethods(Object instance)
	{
		List<Method> declaredGetterList = new ArrayList<Method>();
		Class<?> instanceClass = instance.getClass();
		Method[] declaredMethods = instanceClass.getDeclaredMethods();
		for(Method method : declaredMethods)
			if( Pattern.matches(GETTER_REGEX, method.getName()) && 
				method.getReturnType() != Void.class &&
				(method.getParameterTypes() == null || method.getParameterTypes().length == 0) )
					declaredGetterList.add(method);
		
		return declaredGetterList.iterator();
	}
	
	private static final String SETTER_REGEX = "(set)([A-Z][A-Za-z0-9_]*)"; //$NON-NLS-1$
	protected static Iterator<Method> declaredSetterMethods(Object instance)
	{
		List<Method> declaredSetterList = new ArrayList<Method>();
		Class<?> instanceClass = instance.getClass();
		Method[] declaredMethods = instanceClass.getDeclaredMethods();
		for(Method method : declaredMethods)
			if( Pattern.matches(SETTER_REGEX, method.getName()) && 
				method.getReturnType() == Void.class &&
				method.getParameterTypes().length == 1)
					declaredSetterList.add(method);
		
		return declaredSetterList.iterator();
	}
	
	/**
	 * a test driver of the random string generator
	 * @param argv
	 */
	public static void main(String[] argv)
	{
		GenericDataGenerator generator = new GenericDataGenerator(Mode.RANDOMIZE);
		//System.out.println( generator.createExamInstance(InstancePopulation.FULL, ChildInstancesPopulation.MANY).toString() );
		//System.out.println( generator.createExamSiteInstance(InstancePopulation.FULL, ChildInstancesPopulation.MANY).toString() );
		
		System.out.println("Random byte " + generator.createRandomByte( (byte)-32, (byte)32) ); //$NON-NLS-1$
		System.out.println("Random byte " + generator.createRandomByte( (byte)-32, (byte)32) ); //$NON-NLS-1$
		
		System.out.println("Random char " + generator.createRandomChar("[a-z]") ); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random char " + generator.createRandomChar( (char)0x60, (char)0x72) ); //$NON-NLS-1$
		
		System.out.println("Random short " + generator.createRandomShort("[+-][1-9][0-9]{0,4}") ); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random short " + generator.createRandomShort( (short)-128, (short)128) ); //$NON-NLS-1$
		
		System.out.println("Random int " + generator.createRandomInt("[+-][1-9][0-9]{0,6}") ); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random int " + generator.createRandomInt( (int)-1024, (int)1024) ); //$NON-NLS-1$
		
		System.out.println("Random long " + generator.createRandomLong("[+-][1-9][0-9]{0,8}") ); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random long " + generator.createRandomLong( (long)-1024, (long)1024) ); //$NON-NLS-1$
		
		System.out.println("Random float " + generator.createRandomFloat("[+-][1-9][0-9]{0,8}.[0-9]{3,4}") ); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random float " + generator.createRandomFloat( (float)-1024.0001, (float)1024.9999) ); //$NON-NLS-1$
		
		System.out.println("Random double " + generator.createRandomDouble("[+-][1-9][0-9]{0,8}.[0-9]{3,4}") ); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random double " + generator.createRandomDouble( (double)-2048.0001, (float)2048.9999) ); //$NON-NLS-1$
		
		for(int index=0; index<10; ++index)
			System.out.println("Random String (looks like a float)" + generator.createRandomString("[+-][1-9].[0-9]{2}")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String " + generator.createRandomString("HelloWorld")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String " + generator.createRandomString("HelloWorld[a-z]")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String " + generator.createRandomString("HelloWorld[a-z]{3,4}")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String " + generator.createRandomString("HelloWorld[abcdef]{3,3}")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String " + generator.createRandomString("HelloWorld[abc123]{3,3}")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String " + generator.createRandomString("HelloWorld[A-Z][a-z0-9]{3,3}")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String (looks like a name)" + generator.createRandomString("[A-Z][a-z]{3,9} [A-Z] [A-Z][a-z]{3,12}")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String (3 digits)" + generator.createRandomString("[1-9][0-9]{2}")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("Random String " + generator.createRandomString("[1-9][0-9]{9}V[1-9][0-9]{5}")); //$NON-NLS-1$ //$NON-NLS-2$
		for(int index=0; index<10; ++index)
			System.out.println( "Random String  (looks like a SSN)" + generator.createRandomString("[1-9][0-9]{2}-[0-9]{2}-[0-9]{4}") ); //$NON-NLS-1$ //$NON-NLS-2$
		for(int index=0; index<10; ++index)
			System.out.println( "Random String  (looks like a phone number)" + generator.createRandomString("([1-9][0-9]{2})[1-9][0-9]{2}-[0-9]{4}") ); //$NON-NLS-1$ //$NON-NLS-2$
		
		for(int index=0; index<10; ++index)
			System.out.println( "Randomly selected enum " + generator.selectEnum(Mode.class) + "." ); //$NON-NLS-1$ //$NON-NLS-2$
		
		for(int index=0; index<10; ++index)
			System.out.println( "Randomly created String{32,1024} " + generator.createStringData(null, null, 32, 1024) + "." ); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
